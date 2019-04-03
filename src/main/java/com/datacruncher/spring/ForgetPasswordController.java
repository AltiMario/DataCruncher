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

package com.datacruncher.spring;

import com.datacruncher.constants.Mail;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.utils.generic.CommonUtils;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.utils.mail.MailConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ForgetPasswordController implements Controller, DaoSet {
	
	private String mailFrom;
	private String mailSubject;
	private String mailTemplate;
	private VelocityEngine velocityEngine;
	
	private Logger log = Logger.getLogger(this.getClass());

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		boolean isEmailSent = false;
		String userName = request.getParameter("userName");
		String email = request.getParameter("email");
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		if (userName == null || "".equalsIgnoreCase(userName.trim())) {
			out.write("userNameRequired".getBytes());
			out.flush();
			out.close();
	 		return null;
		}
		if (email == null|| "".equalsIgnoreCase(email.trim())) {
			out.write("emailRequired".getBytes());
			out.flush();
			out.close();
	 		return null;
		}
		
		UserEntity userEntity = new UserEntity ();
		userEntity = usersDao.findUserByNameNMailId(userName , email);
		
		ObjectMapper mapper = new ObjectMapper();
 		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        Update updateResult = new Update();
		if (userEntity != null) {
			String tempPassword = RandomStringUtils.randomAlphanumeric(4);
			userEntity.setPassword(tempPassword);
			updateResult = usersDao.update(userEntity);
			if(updateResult.isSuccess()){
				MailConfig mailConfig = new MailConfig();
				mailConfig.setMailTo(userEntity.getEmail());
				mailConfig.setMailFrom(mailFrom);
				mailConfig.setSubject(mailSubject);
				Map<String,Object> model = new HashMap<>();
				model.put("name",userEntity.getUserName());
				model.put("tempPassword",tempPassword);
				String mailContent = CommonUtils.mergeVelocityTemplateForEmail(velocityEngine, mailTemplate, model);
				mailConfig.setText(mailContent);
				try {
					Mail.getJavaMailService().sendMail(mailConfig);
					isEmailSent = true;
				} catch (Exception e) {
					isEmailSent = false;
					log.error("Failed to dispatch mail:",e);
					
				}
			}
			if(!isEmailSent) {
				updateResult.setMessage(I18n.getMessage("error.emailConfigError"));
				updateResult.setSuccess(false);
			}else{
                updateResult.setMessage(I18n.getMessage("success.emailConfigSuccess"));
            }

		} else {
            updateResult.setMessage(I18n.getMessage("error.emailError"));
            updateResult.setSuccess(false);

		}
        out.write(mapper.writeValueAsBytes(updateResult));
		out.flush();
		out.close();
 		return null;
	}

	//----------------SETTERS & GETTERS-------------
	
	/**
	 * @return the mailFrom
	 */
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * @param mailSubject the mailSubject to set
	 */
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	/**
	 * @return the velocityEngine
	 */
	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	/**
	 * @param velocityEngine the velocityEngine to set
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
}