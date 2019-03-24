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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;

public class DynamicJavaCompiler {
	
	private String classpath;

	private String outputdir;

	private String sourcepath;

	private String bootclasspath;

	private String extdirs;

	private String encoding;

	private String target;
	
	public DynamicJavaCompiler(String classpath, String outputdir) {
		this.classpath = classpath;
		this.outputdir = outputdir;
	}
	
	public String compile(File srcFiles[]) {
		String paths[] = new String[srcFiles.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = srcFiles[i].getAbsolutePath();
		}
		return compile(paths);
	}
	
	public String compile(String srcFiles[]) {
		StringWriter err = new StringWriter();
		PrintWriter errPrinter = new PrintWriter(err);

		String args[] = buildJavacArgs(srcFiles);
				
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);				
		int resultCode = compiler.run(null, System.out, ps, args);
		errPrinter.close();
		String errorMsg = baos.toString();
		if(StringUtils.isNotEmpty(errorMsg)){
			String[] eMsg = errorMsg.split("error:");
			errorMsg = eMsg[1];
		}
		return (resultCode == 0) ? null : errorMsg;
	}
	
	private String[] buildJavacArgs(String srcFiles[]) {
		ArrayList<String> args = new ArrayList<String>();

		if (classpath != null) {
			args.add("-classpath");
			args.add(classpath);
		}
		if (outputdir != null) {
			args.add("-d");
			args.add(outputdir);
		}
		if (sourcepath != null) {
			args.add("-sourcepath");
			args.add(sourcepath);
		}
		if (bootclasspath != null) {
			args.add("-bootclasspath");
			args.add(bootclasspath);
		}
		if (extdirs != null) {
			args.add("-extdirs");
			args.add(extdirs);
		}
		if (encoding != null) {
			args.add("-encoding");
			args.add(encoding);
		}
		if (target != null) {
			args.add("-target");
			args.add(target);
		}

		for (int i = 0; i < srcFiles.length; i++) {
			args.add(srcFiles[i]);
		}
		return (String[]) args.toArray(new String[args.size()]);
	}

	//--------SETTERS & GETTERS -------------
	/**
	 * @return the classpath
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * @param classpath the classpath to set
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * @return the outputdir
	 */
	public String getOutputdir() {
		return outputdir;
	}

	/**
	 * @param outputdir the outputdir to set
	 */
	public void setOutputdir(String outputdir) {
		this.outputdir = outputdir;
	}

	/**
	 * @return the sourcepath
	 */
	public String getSourcepath() {
		return sourcepath;
	}

	/**
	 * @param sourcepath the sourcepath to set
	 */
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	/**
	 * @return the bootclasspath
	 */
	public String getBootclasspath() {
		return bootclasspath;
	}

	/**
	 * @param bootclasspath the bootclasspath to set
	 */
	public void setBootclasspath(String bootclasspath) {
		this.bootclasspath = bootclasspath;
	}

	/**
	 * @return the extdirs
	 */
	public String getExtdirs() {
		return extdirs;
	}

	/**
	 * @param extdirs the extdirs to set
	 */
	public void setExtdirs(String extdirs) {
		this.extdirs = extdirs;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
}
