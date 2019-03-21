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
package com.seer.datacruncher.constants;

import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.mail.MailService;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;


public class Mail implements DaoSet {

    static Logger log = Logger.getLogger(Mail.class);
	
    public static JavaMailSenderImpl instanceMailSender;
    public static MailService mailService;
    
    public static MailService getJavaMailService() {
        if(mailService == null) {
        	configureMailService();
        }
    	return mailService;
    }
    
    public static void configureMailService() {    	
    	ApplicationConfigEntity configEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.EMAIL);
        instiateMailService(configEntity);        
    }
    
    public static void instiateMailService(ApplicationConfigEntity configEntity) {
    	
    	try {
    		if(configEntity != null) {
    			instanceMailSender = new JavaMailSenderImpl();
    			instanceMailSender.setHost(configEntity.getHost());
    			instanceMailSender.setPort(Integer.parseInt(configEntity.getPort()));
    			instanceMailSender.setUsername(configEntity.getUserName());
    			instanceMailSender.setPassword(new CryptoUtil().decrypt(configEntity.getPassword()));
    			instanceMailSender.setProtocol(configEntity.getProtocol());
    			instanceMailSender.setDefaultEncoding(configEntity.getEncoding());

    			Properties javaMailProperties = new Properties();
    			javaMailProperties.put("mail.smtps.auth", configEntity.getIsSmtpsAuthenticate() == 1 ? "true" : "false");
    			javaMailProperties.put("mail.smtps.starttls.enable", configEntity.getIsStarTtls() == 1 ? "true" : "false");
    			javaMailProperties.put("mail.smtp.timeout", Integer.parseInt(configEntity.getSmtpsTimeout()));

    			instanceMailSender.setJavaMailProperties(javaMailProperties);
    			mailService = new MailService();
    			mailService.setMailSender(instanceMailSender);

    		} else {
    			instanceMailSender = null;
    			mailService = null;
    		}
    	} catch (Exception exception) {
    		log.error("Mail Configuration Error: " + exception);
    	}
    }
}