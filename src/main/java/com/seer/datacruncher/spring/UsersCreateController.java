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

import com.seer.datacruncher.constants.Alerts;
import com.seer.datacruncher.constants.Mail;
import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.mail.MailConfig;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class UsersCreateController implements Controller, DaoSet {
	private String mailFrom;
	private String mailSubject;
	private String mailTemplate;
	private VelocityEngine velocityEngine;
	
	private Logger log = Logger.getLogger(this.getClass());

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		String json = request.getReader().readLine();
		boolean isSelfRegister = false;
		if("yes".equals(request.getParameter("isSelfRegister"))){
			isSelfRegister = true;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		UserEntity userEntity = new UserEntity ();
		if(!isSelfRegister){
			HttpSession session = request.getSession();
			UserEntity loggedUser = (UserEntity)session.getAttribute("user");
			userEntity = mapper.readValue(json , UserEntity.class);
			userEntity.setTheme("classic");
			userEntity.setCreatedBy(loggedUser.getIdUser());
		}else{
			try {
				getUserEntity(userEntity,request);
			} catch (ParseException e) {
				log.error("Parse Exception:"+e,e);
			}
		}
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		Create create = null;
		HttpSession session = request.getSession();
		String sCode = (String)session.getAttribute("simple.capcha.session.key");
		String reqCaptch = request.getParameter("captcha");
		if (reqCaptch != null && (!reqCaptch.equalsIgnoreCase(sCode))) {
			create = new Create();
			create.setSuccess(false);
			create.setMessage("invalidCaptcha");
		}else{
			create = usersDao.create(userEntity);
			String language = userEntity.getLanguage();
			if (null == language || language.length() == 0) {
				language = "en";
			}
			if (create != null && create.getSuccess()) {
                if(applicationConfigDao.isAlertDispatcherMailSet()) {
                    if (userEntity.getEmail() != null
                            && userEntity.getEmail().trim().length() > 0 ) {
                        // Added thread call for send the response immediately with out waiting for mail dispatch.
                        MailDispatcher mailDispatcher = new MailDispatcher(mailFrom, mailSubject, userEntity, mailTemplate);
                        Thread thread = new Thread(mailDispatcher);
                        thread.start();
                    }else{
                        log.info("No Email address configured to dispatch welcome mail for user:"+userEntity.getUserName());
                    }
                }else{
                    log.info("No Dispatcher Email configured to dispatch welcome mail for user:"+userEntity.getUserName());
                }
			}
		}
		if(create.getSuccess()) {
			userEntity = (UserEntity)create.getResults();
			String encPassword = userEntity.getPassword();
			try {
				String plainPassword = new CryptoUtil().decrypt(encPassword);
				userEntity.setPassword(plainPassword);
			} catch (Exception e) {
			}
		}
		out.write(mapper.writeValueAsBytes(create));
		out.flush();
		out.close();
 		return null;
	}
	
	//-----------HELPERS--------------
	private class MailDispatcher implements Runnable{
		private String mailFrom;
		private String subject;
		private UserEntity userEntity;
		private String mailTemplate;
	
		/**
		 * @param mailFrom
		 * @param subject
		 * @param userEntity
		 * @param mailTemplate
		 */
		public MailDispatcher(String mailFrom, String subject,
				UserEntity userEntity, String mailTemplate) {
			this.mailFrom = mailFrom;
			this.subject = subject;
			this.userEntity = userEntity;
			this.mailTemplate = mailTemplate;
		}

		@Override
		public void run() {
			MailConfig mailConfig = new MailConfig();
			mailConfig.setMailTo(this.userEntity.getEmail());
			mailConfig.setMailFrom(this.mailFrom);
			mailConfig.setSubject(this.subject);
			Map<String,String> model = new HashMap<String,String>();
			model.put("name",this.userEntity.getName());
			model.put("surname",this.userEntity.getSurname());
			model.put("userName",this.userEntity.getUserName());
			String mailContent = CommonUtils.mergeVelocityTemplateForEmail(velocityEngine, mailTemplate, model);
			mailConfig.setText(mailContent);
			try {
				Mail.getJavaMailService().sendMail(mailConfig);
			} catch (Exception e) {
				log.error("Failed to dispatch mail:",e);
			}
		}
	}
	
	
	private void getUserEntity(UserEntity userEntity, HttpServletRequest request) throws ParseException{
		userEntity.setUserName(request.getParameter("userName"));
		userEntity.setPassword(request.getParameter("password"));
		userEntity.setName(request.getParameter("name"));
		userEntity.setSurname(request.getParameter("surname"));
		userEntity.setEmail(request.getParameter("email"));
		String dob = request.getParameter("dob");
		if (dob != null && dob.length() > 0) {
			userEntity.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dob")));
		}
		userEntity.setIdRole(Roles.USER.getDbCode());
		userEntity.setLanguage(request.getParameter("language"));
		String idAlertStr = request.getParameter("idAlert");
		long idAlert = 0;
		if (null == idAlertStr || "".equalsIgnoreCase(idAlertStr)) {
			idAlert = Alerts.NEVER.getDbCode();
		}else{
			idAlert = Long.parseLong(idAlertStr);
		}
		userEntity.setIdAlert(idAlert);
		userEntity.setTheme(request.getParameter("theme"));
		userEntity.setEnabled(0);
		userEntity.setCreatedBy(-1);
	}
	
	//-----------SETTERS & GETTERS----------
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