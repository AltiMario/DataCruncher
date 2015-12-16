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

import com.seer.datacruncher.constants.Mail;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ContactEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.mail.MailConfig;

import java.io.IOException;
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
 
public class ContactsUpdateController implements Controller, DaoSet {

	private String mailFrom;
	private String mailTo;
	private String mailSubject;
	private String mailTemplate;
	private VelocityEngine velocityEngine;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();		
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		
		String json = request.getReader().readLine();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ContactEntity contactEntity = mapper.readValue(json , ContactEntity.class);
		contactEntity.setIdUser(user.getIdUser());
		
		MailConfig mailConfig = new MailConfig();
		mailConfig.setMailTo(mailTo);
		mailConfig.setMailFrom(mailFrom);
		mailConfig.setSubject(mailSubject + " " + contactEntity.getEmailID());
		
		Map<String,String> model = new HashMap<String,String>();
		model.put("firstName", contactEntity.getFirstName());
		model.put("lastName", contactEntity.getLastName());		
		model.put("message", contactEntity.getMsgText().replaceAll("\n", "<br/>"));
		
		String mailContent = CommonUtils.mergeVelocityTemplateForEmail(velocityEngine, mailTemplate, model);
		mailConfig.setText(mailContent);
		try {
			Mail.getJavaMailService().sendMail(mailConfig);
			contactEntity.setIsEmailSent(1);
		} catch(Exception ex) {
			log.error("Failed to dispatch mail:", ex);
			contactEntity.setIsEmailSent(0);	
		}
		
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		Update update = contactDao.update(contactEntity);		
		out.write(mapper.writeValueAsBytes(update));
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
		 * @return the mailTo
		 */
		public String getMailTo() {
			return mailTo;
		}

		/**
		 * @param mailTo the mailTo to set
		 */
		public void setMailTo(String mailTo) {
			this.mailTo = mailTo;
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