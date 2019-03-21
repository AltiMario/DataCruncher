/*
 * Copyright (c) 2019  Altimari Mario
 * All rights reserved
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
 */

package com.seer.datacruncher.listeners;

import com.seer.datacruncher.jpa.dao.DaoSet;
import org.apache.activemq.thread.DefaultThreadPools;
import org.apache.log4j.Logger;

import com.seer.datacruncher.connection.ConnectionPoolsSet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Application Context Listener to call when context is stopped
 * @author Hassan Saghir
 *
 */
public class ApplicationListener implements ServletContextListener,DaoSet {

    private final Logger log = Logger.getLogger(this.getClass());
	@Override
    public void contextInitialized(ServletContextEvent sce) {
        // On Application Startup
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	ConnectionPoolsSet.destroyAllPools();    	
    	// On Application Shutdown
    	clearJdbcDrivers();
    	clearReferencesThreadLocals();
    	clearActiveMQThreads();
    }
    
    public void clearJdbcDrivers(){
    	/** Deregister autoloaded database drivers **/
    	try {
    		Enumeration<Driver> drivers = DriverManager.getDrivers();
    		while(drivers.hasMoreElements()){
    			Driver driver = drivers.nextElement();
                log.info("INFO: Deregistering Driver: "+driver.getClass());
    			DriverManager.deregisterDriver(driver);
    		}
    		Thread.sleep(1000);
    	} catch (Exception e) {
            log.error("ERROR: Deregistering Database Drivers Failed: "+e.getMessage());
    	}
    }
    
    // Clear any ThreadLocals loaded by this class loader
    private void clearReferencesThreadLocals() {
        Thread[] threads = getThreads();

        try {
            // Make the fields in the Thread class that store ThreadLocals
            // accessible
            Field threadLocalsField =
                Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Field inheritableThreadLocalsField =
                Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            // Make the underlying array of ThreadLoad.ThreadLocalMap.Entry objects
            // accessible
            Class<?> tlmClass =
                Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = tlmClass.getDeclaredField("table");
            tableField.setAccessible(true);
            
            for (int i = 0; i < threads.length; i++) {
                Object threadLocalMap;
                if (threads[i] != null) {
                    // Clear the first map
                    threadLocalMap = threadLocalsField.get(threads[i]);
                    clearThreadLocalMap(threadLocalMap, tableField);
                    // Clear the second map
                    threadLocalMap =
                        inheritableThreadLocalsField.get(threads[i]);
                    clearThreadLocalMap(threadLocalMap, tableField);
                }
            }
        } catch (Exception e) {
            log.error("ERROR: clearing local thread map Failed: "+e.getMessage());
        }       
    }
    
    /*
     * Clears the given thread local map object. Also pass in the field that
     * points to the internal table to save re-calculating it on every
     * call to this method.
     */
    private void clearThreadLocalMap(Object map, Field internalTableField)
    throws NoSuchMethodException, IllegalAccessException,
    NoSuchFieldException, InvocationTargetException {
    	if (map != null) {
    		Method mapRemove =
    			map.getClass().getDeclaredMethod("remove",
    					ThreadLocal.class);
    		mapRemove.setAccessible(true);
    		Object[] table = (Object[]) internalTableField.get(map);
    		int staleEntriesCount = 0;
    		if (table != null) {
    			for (int j =0; j < table.length; j++) {
    				if (table[j] != null) {
    					boolean remove = false;
    					// Check the key
    					Object key = ((Reference<?>) table[j]).get();
    					if (this.equals(key) || (key != null)) {
    						remove = true;
    					}
    					// Check the value
    					Field valueField =
    						table[j].getClass().getDeclaredField("value");
    					valueField.setAccessible(true);
    					Object value = valueField.get(table[j]);
    					if (this.equals(value) || (value != null)) {
    						remove = true;
    					}
    					if (remove) {
    						Object[] args = new Object[4];
    						if (key != null) {
    							args[0] = key.getClass().getCanonicalName();
    							args[1] = key.toString();
    						}
    						if (value != null) {
    							args[2] = value.getClass().getCanonicalName();
    							args[3] = value.toString();
    						}
    						if (key == null) {
    							staleEntriesCount++;
    						} else {
    							mapRemove.invoke(map, key);
    						}
    					}
    				}
    			}
    		}
    		if (staleEntriesCount > 0) {
    			Method mapRemoveStale =
    				map.getClass().getDeclaredMethod("expungeStaleEntries");
    			mapRemoveStale.setAccessible(true);
    			mapRemoveStale.invoke(map);
    		}
    	}
    }
    
    /*
     * Get the set of current threads as an array.
     */
    private Thread[] getThreads() {
        // Get the current thread group 
        ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
        // Find the root thread group
        while (tg.getParent() != null) {
            tg = tg.getParent();
        }
        
        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);
        // Make sure we don't miss any threads
        while (threadCountActual == threadCountGuess) {
            threadCountGuess *=2;
            threads = new Thread[threadCountGuess];
            // Note tg.enumerate(Thread[]) silently ignores any threads that
            // can't fit into the array 
            threadCountActual = tg.enumerate(threads);
        }
        
        return threads;
    }
    
    private void clearActiveMQThreads(){
    	try {
			DefaultThreadPools.shutdown();
		} catch (Exception ignored) {}
    }

}
