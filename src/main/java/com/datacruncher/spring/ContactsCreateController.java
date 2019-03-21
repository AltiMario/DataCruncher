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

package com.datacruncher.spring;

import com.datacruncher.constants.FileInfo;
import com.datacruncher.constants.Mail;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.ContactEntity;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.utils.CryptoUtil;
import com.datacruncher.utils.generic.CommonUtils;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.utils.mail.MailConfig;
import com.datacruncher.utils.schema.SchemaValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
 
public class ContactsCreateController implements Controller, DaoSet {

	private String mailFrom;
	private String mailTo;
	private String mailSubject;
	private String mailTemplate;
	private VelocityEngine velocityEngine;
	
	private Logger log = Logger.getLogger(this.getClass());

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		
		ServletOutputStream out;
		response.setContentType("application/json");
		out = response.getOutputStream();	
		
		HttpSession session = request.getSession();		
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		String json = request.getReader().readLine();		
		ObjectMapper mapper = new ObjectMapper();
				
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
		
		String fileName = contactEntity.getIdSchema() + FileInfo.EXIMPORT_FILE_EXTENSION;
		Map<String, String> resMap = null;
		
		if(contactEntity.getIsShared()) {
			try {
				SchemaValidator schemaValidator = new SchemaValidator();
				resMap = schemaValidator
						.validateSchema(contactEntity.getIdSchema());
				String keptXSD = schemaValidator.getSchemaCantaints();
				if (keptXSD != null && keptXSD.trim().length() > 0) {
					keptXSD = new CryptoUtil().encrypt(keptXSD);
					BufferedWriter bos = new BufferedWriter(new FileWriter(
							new File(fileName)));
					bos.write(keptXSD);
					bos.flush();
					bos.close();

					FileInputStream fis = new FileInputStream(
							new File(fileName));
					mailConfig.setAttachment(fis);
					mailConfig.setAttachmentName(fileName);
				}
				
			} catch (Exception ex) {
				log.error("Failed in attaching file to an email:",ex);				
			}
		}
		try {
			
			if(resMap != null && !Boolean.valueOf(resMap.get("success"))) {
				out.write(("{success:false, message: \"" + I18n.getMessage("error.schemaSharedError") + "\"}")
						.getBytes());
				out.flush();
				out.close();
				
				return null;
			}
			Mail.getJavaMailService().sendMail(mailConfig);
			contactEntity.setIsEmailSent(1);
		} catch (Exception e) {
			log.error("Failed to dispatch mail:",e);
			contactEntity.setIsEmailSent(0);			
		} finally {
			if(new File(fileName).exists()) {
				new File(fileName).delete();				
			}
		}
		Create create;
		if(contactEntity.getIsShared() && contactEntity.getIsEmailSent() == 0) {
			create = new Create();
			create.setMessage(I18n.getMessage("error.emailConfigError"));
			create.setSuccess(false);			
		} else {
			create = contactDao.create(contactEntity);
		}
		
		out.write(mapper.writeValueAsBytes(create));
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

