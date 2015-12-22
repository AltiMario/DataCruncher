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
package com.seer.datacruncher.services.ftp.camel;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.services.ftp.FTPPollJobProcessor;
import com.seer.datacruncher.services.ftp.FtpServerHandler;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.JVPropertyPlaceholderConfigurer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Praveen
 *
 */
public class FtpPolling extends RouteBuilder implements DaoSet {

	@Autowired
	private JVPropertyPlaceholderConfigurer propertyConfigurer;
	
	/* (non-Javadoc)
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception {
		System.out.println("FtpPolling: Configuration");
		
		ApplicationConfigEntity configEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
		
		String ftpServer = propertyConfigurer.getProps().getProperty("ftp.server");
		String userName = configEntity.getUserName();
		String password = new CryptoUtil().decrypt(configEntity.getPassword());
		String inputDir = configEntity.getInputDir();
        //String homedirectory = configEntity.getOutputDir();
        //String maxIdleTimeInSec = propertyConfigurer.getProps().getProperty("ftp.maxIdleTimeInSec");
		
        FtpServerHandler ftpServerHandler = new FtpServerHandler();
        ftpServerHandler.init();

		// If input directory is not set, set inputDirectory as rootDirectory
		if(StringUtils.isBlank(inputDir)){
			inputDir = "/";
		}
				
		
		FTPPollJobProcessor ftpPollJobProcessor = new FTPPollJobProcessor();
		ftpPollJobProcessor.setPropertyConfigurer(propertyConfigurer);
		String uri = "ftp://"
				+ ftpServer
				+ "/"
				+ inputDir
				+ "?username="
				+ userName
				+ "&password="
				+ password
				+ "&filter=#ftpFileFilter&consumer.delay="
				+ 60000;
		
		System.out.println("FTP Server Started");
		from(uri).process(ftpPollJobProcessor);
	}
}
