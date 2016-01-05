/*
 * Copyright (c) 2015  www.see-r.com
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

package com.seer.datacruncher.services.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.log4j.Logger;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.jpa.entity.ServersEntity;
import com.seer.datacruncher.spring.AppContext;

/**
 * This is a spring bean class. and used to start and stop embedded FTP server. 
 * @author Naveen
 *
 */
public class FtpServerHandler implements DaoSet {
	
	private static Logger log = Logger.getLogger(FtpServerHandler.class);	
	public static FtpServer server;
		
	public FtpServerHandler() {
		this(null);
	}

	/**
	 * Constructor to initialize the embedded FTP server with default credentials.
	 */
	public FtpServerHandler(Integer port) {
		
		if(server == null || server.isStopped()) {
			/*JVPropertyPlaceholderConfigurer propertyConfigurer = AppContext.getApplicationContext().getBean(JVPropertyPlaceholderConfigurer.class);
			String homedirectory = propertyConfigurer.getProps().getProperty("ftp.homedirectory");
			String maxIdleTimeInSec = propertyConfigurer.getProps().getProperty("ftp.maxIdleTimeInSec");
			*/
			//System.out.println("HOME DIRECTORY:"+homedirectory);
			//System.out.println("maxIdleTimeInSec:"+maxIdleTimeInSec);

			ListenerFactory factory = new ListenerFactory();
			if ( port == null ) {
	        	port = ApplicationConfigEntity.FTP_DEFAULT_SAFE_PORT;
	        	if ( applicationConfigDao != null ) {
			        ApplicationConfigEntity appConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
			        if ( appConfigEntity != null ) {
			        	port = appConfigEntity.getServerPort();
			        }
	        	}
			}
			factory.setPort(port);
			
			FtpServerFactory serverFactory = new FtpServerFactory();
			serverFactory.addListener("default", factory.createListener());
			
			UserAuthenticationManagerFactory userAuthManagerFactory =  AppContext.getApplicationContext().getBean(UserAuthenticationManagerFactory.class);
			
			serverFactory.setUserManager(userAuthManagerFactory.createUserManager());
			NativeFileSystemFactory f = new NativeFileSystemFactory();
			f.setCreateHome(true);
			serverFactory.setFileSystem(f);
			server = serverFactory.createServer();
		}
	}
	/**
	 * Method will invoke after the bean initialization and start the embedded FTP server.
	 * @throws Exception
	 */
	public void init() throws Exception {
		
		ServersEntity serverEntity = serversDao.find(Servers.FTP.getDbCode());
		
		if(serverEntity != null && serverEntity.getIsActive() == 0) {
			log.info("Ftp Server Service is stopped.");
			return;
		} 
		if(server == null || server.isStopped()) {
			server.start();
		}
		log.info("Ftp Server started.");		
	}

	/**
	 * Method will stop the embedded FTP server at the time of spring context destroy.
	 * @throws Exception
	 */
	public void destroy() throws Exception {
		
		if(!server.isStopped()) {			
			server.stop();
			server = null;
			log.info("Ftp Server stopped.");			
		}		
	}
}
