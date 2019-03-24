/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.datacruncher.eventtrigger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DynamicClassLoader {
	
	private final static Logger logger = Logger.getLogger(DynamicClassLoader.class);
	
	private String compileClasspath;
	private ClassLoader parentClassLoader;
	private List<SourceDirectory> sourceDirectories = new ArrayList<SourceDirectory>();
	private Map loadedClasses = new HashMap();
	private static DynamicClassLoader dynamicClassLoader;
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public static DynamicClassLoader getInstance(){
		try{
			lock.writeLock().lock();
			if (dynamicClassLoader == null) {
				dynamicClassLoader = new DynamicClassLoader();
			}
		}catch(Exception e){
			
		} finally {
			lock.writeLock().unlock();
		}
		return dynamicClassLoader;
	}
	
	public DynamicClassLoader() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public DynamicClassLoader(ClassLoader parentClassLoader) {
		this(extractClasspath(parentClassLoader), parentClassLoader);
	}

	public DynamicClassLoader(String compileClasspath, ClassLoader parentClassLoader) {
		this.compileClasspath = compileClasspath;
		this.parentClassLoader = parentClassLoader;
	}
	
	
	public Class getLoadedClass(String className){
		Class clazz = null;
		LoadedClass loadedClass =  (LoadedClass) loadedClasses.get(className);
		if (loadedClass != null) {
			clazz = loadedClass.getClazz();	
		}
		return clazz;
	}
	
	public void removeLoadedClass(String className){
		LoadedClass loadedClass = (LoadedClass)loadedClasses.get(className);
		if(loadedClass != null){
			loadedClasses.remove(className);
		}
	}
	
	private static String extractClasspath(ClassLoader classLoader) {
		
		StringBuffer classLoaderBuf = new StringBuffer();
		try {
			while (classLoader != null) {
				if (classLoader instanceof URLClassLoader) {
					URL urls[] = ((URLClassLoader) classLoader).getURLs();
					for (int i = 0; i < urls.length; i++) {
						if (classLoaderBuf.length() > 0) {
							classLoaderBuf.append(File.pathSeparatorChar);
						}
						classLoaderBuf.append(URLDecoder.decode(urls[i].getFile(),"UTF-8").toString());
					}
				}
				classLoader = classLoader.getParent();
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception:"+e.getMessage(),e);
		}
		return classLoaderBuf.toString();
	}
	
	public boolean addSourceDir(File srcDir) {
		try {
			srcDir = srcDir.getCanonicalFile();
		} catch (IOException e) {
			// ignore
		}
		synchronized (sourceDirectories) {
			if(CollectionUtils.isNotEmpty(sourceDirectories)){
				for (Object obj: sourceDirectories) {
					SourceDirectory sDirectory = (SourceDirectory) obj;
					if(sDirectory.srcDir.equals(srcDir)){
						return false;
					}
				}
			}
			SourceDirectory sDirectory = new SourceDirectory(srcDir);
			sourceDirectories.add(sDirectory);
		}
		return true;
	}
	
	public Object newProxyInstance(Class interfaceClass, String implClassName)
			throws RuntimeException {
		JVInvocationHandler handler = new JVInvocationHandler(implClassName);
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class[] { interfaceClass }, handler);
	}
	
	public Class loadClass(String className) throws ClassNotFoundException {

		LoadedClass loadedClass = null;
		synchronized (loadedClasses) {
			loadedClass = (LoadedClass) loadedClasses.get(className);
		}

		// first access of a class
		if (loadedClass == null) {

			String resource = className.replace('.', '/') + ".java";
			SourceDirectory src = locateResource(resource);
			if (src == null) {
				throw new ClassNotFoundException("Dynamic class not found "
						+ className);
			}

			synchronized (this) {
				// compile and load class
				loadedClass = new LoadedClass(className, src);

				synchronized (loadedClasses) {
					loadedClasses.put(className, loadedClass);
				}
			}

			return loadedClass.clazz;
		}

		// subsequent access
		if (loadedClass.isChanged()) {
			// unload and load again
			unload(loadedClass.srcDir);
			return loadClass(className);
		}

		return loadedClass.clazz;
	}
	
	private SourceDirectory locateResource(String resource) {
		for (int i = 0; i < sourceDirectories.size(); i++) {
			SourceDirectory src = (SourceDirectory) sourceDirectories.get(i);
			if (new File(src.srcDir, resource).exists()) {
				return src;
			}
		}
		return null;
	}
	
	private void unload(SourceDirectory src) {
		// clear loaded classes
		synchronized (loadedClasses) {
			for (Iterator iter = loadedClasses.values().iterator(); iter
					.hasNext();) {
				LoadedClass loadedClass = (LoadedClass) iter.next();
				if (loadedClass.srcDir == src) {
					iter.remove();
				}
			}
		}
		// create new class loader
		src.recreateClassLoader();
	}
	
	//---- Support class----------
	private static class LoadedClass {
		String className;

		SourceDirectory srcDir;

		File srcFile;

		File binFile;

		Class clazz;

		public Class getClazz() {
			return clazz;
		}
		
		public File getSrcFile() {
			return srcFile;
		}

		public File getBinFile() {
			return binFile;
		}
		long lastModified;

		LoadedClass(String className, SourceDirectory src) {
			this.className = className;
			this.srcDir = src;

			String path = className.replace('.', '/');
			this.srcFile = new File(src.srcDir, path + ".java");
			this.binFile = new File(src.binDir, path + ".class");

			compileAndLoadClass();
		}

		boolean isChanged() {
			return srcFile.lastModified() != lastModified;
		}

		void compileAndLoadClass() {

			if (clazz != null) {
				return; // class already loaded
			}

			// compile, if required
			String error = null;
			if (binFile.lastModified() < srcFile.lastModified()) {
				error = srcDir.djCompiler.compile(new File[] { srcFile });
			}

			if (error != null) {
				throw new RuntimeException("Failed to compile "
						//+ srcFile.getAbsolutePath() + ". Error: "
						+ error);
			}

			try {
				// load class
				clazz = srcDir.classLoader.loadClass(className);

				// load class success, remember timestamp
				lastModified = srcFile.lastModified();

			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load Dynamic class "
						+ srcFile.getAbsolutePath());
			}
		}
	}
	
	private class SourceDirectory {
		File srcDir;

		File binDir;

		DynamicJavaCompiler djCompiler;

		URLClassLoader classLoader;

		SourceDirectory(File srcDir) {
			this.srcDir = srcDir;

			String subdir = srcDir.getAbsolutePath().replace(':', '_').replace(
					'/', '_').replace('\\', '_');
			this.binDir = new File(System.getProperty("java.io.tmpdir"),
					"DataCruncher/" + subdir);
			this.binDir.mkdirs();

			// prepare compiler
			this.djCompiler = new DynamicJavaCompiler(compileClasspath, binDir.getAbsolutePath());

			// class loader
			recreateClassLoader();
		}

		void recreateClassLoader() {
			try {
				classLoader = new URLClassLoader(new URL[] { binDir.toURI().toURL() },
						parentClassLoader);
			} catch (MalformedURLException e) {
				logger.error("Exception:"+e.getMessage(),e);
			}
		}
	}
	
	private class JVInvocationHandler implements InvocationHandler {

		String backendClassName;

		Object backend;

		JVInvocationHandler(String className) {
			backendClassName = className;

			try {
				Class clazz = loadClass(backendClassName);
				backend = newDynamicCodeInstance(clazz);

			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			// check if class has been updated
			Class clazz = loadClass(backendClassName);
			if (backend.getClass() != clazz) {
				backend = newDynamicCodeInstance(clazz);
			}

			try {
				// invoke on backend
				return method.invoke(backend, args);

			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		private Object newDynamicCodeInstance(Class clazz) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(
						"Failed to new instance of Dynamic class "
								+ clazz.getName(), e);
			}
		}
	}
}
