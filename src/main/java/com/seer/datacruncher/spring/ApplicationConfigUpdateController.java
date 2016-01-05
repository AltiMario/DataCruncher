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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.constants.Mail;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class ApplicationConfigUpdateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		
		Update update = new Update();
				
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();	
		
		String configType = request.getParameter("configType");
		
		if(configType.equals("email")) {
          	ApplicationConfigEntity appConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.EMAIL);
			appConfigEntity.setUserName(request.getParameter("userName"));
			appConfigEntity.setPassword(request.getParameter("password"));			
			appConfigEntity.setHost(request.getParameter("host"));
			appConfigEntity.setPort(request.getParameter("port"));
			appConfigEntity.setProtocol(request.getParameter("protocol"));
			appConfigEntity.setEncoding(request.getParameter("encoding"));
			appConfigEntity.setSmtpsTimeout(request.getParameter("smtpstimeout"));
			appConfigEntity.setIsStarTtls(Integer.parseInt(request.getParameter("starttls") == null ? "0" : request.getParameter("starttls")));
			appConfigEntity.setIsSmtpsAuthenticate(Integer.parseInt(request.getParameter("smtpsAuthenticate") == null ? "0" : request.getParameter("smtpsAuthenticate")));
			
			update = applicationConfigDao.update(appConfigEntity);
			if(update.isSuccess()) {
				Mail.configureMailService();
			}
			update.setMessage(I18n.getMessage("success.emailConfigSaved"));
		} else if(configType.equals("ftp")) {
			ApplicationConfigEntity appConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
			appConfigEntity.setUserName(request.getParameter("userName"));
			appConfigEntity.setPassword(request.getParameter("password"));			
			appConfigEntity.setInputDir(request.getParameter("inputDirectory"));
			appConfigEntity.setOutputDir(request.getParameter("outputDirectory"));
			try {
				appConfigEntity.setServerPort(Integer.parseInt(request.getParameter("serverPort")));
				update = applicationConfigDao.update(appConfigEntity);		
				update.setMessage(I18n.getMessage("success.ftpConfigSaved"));
			}
			catch (Throwable t) {
				update = new Update();
				update.setSuccess(false);
				update.setMessage(I18n.getMessage("error.ftpConfigPortInNotANumber"));
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();				
		
		out.write(mapper.writeValueAsBytes(update));
		out.flush();
		out.close();
 		
 		return null;
	}
}