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
package com.seer.datacruncher.alerts;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.Mail;
import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.mail.MailConfig;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class AlertsByMail extends AlertDispatcher {
	
	private String mailFrom;
	private String mailSubject;
	private String mailTemplate;
	private VelocityEngine velocityEngine;
	private Logger log = Logger.getLogger(this.getClass());

	@Override
	public boolean dispatchAlert(AlertsData alertsData) throws Exception {
		String logMsg = "AlertsByMail:dispatchAlert:";
		boolean dispatchStatus = false;
		List<UserEntity> users = alertsData.getUsers();
		
		if (users == null || users.size() == 0) {
			log.error(logMsg+" No Users available to dispatch alert mail.");
			return true;
		}
		try {
			MailConfig mailConfiguration = getMailConfig(users,
					alertsData.getDataStreams(), alertsData.getSchemaEntity(),
					alertsData.getAlertType(),alertsData.getTotStream());
			Mail.getJavaMailService().sendMail(mailConfiguration);
			dispatchStatus = true;
		} catch (Exception e) {
			log.error(logMsg+"Failed to dispatch mail:",e);
		}
		return dispatchStatus;
	}
	
	//-------------------HELPERS-----------------------------
	private MailConfig getMailConfig(List<UserEntity> users,
			List<DatastreamEntity> dataStreams, SchemaEntity schemaEntity,
			String alertType,int totStream) {
	MailConfig mailConfig = new MailConfig();
		mailConfig.setMailTo(getMailToAddress(users));
		mailConfig.setMailFrom(this.mailFrom);
		mailConfig.setSubject(this.mailSubject);
		String mailContent = "";
        String serverUrl = CommonUtils.getDownloadStreamUrl();
		if ("EveryTime".equalsIgnoreCase(alertType)) {
			mailContent = getMailContent(dataStreams.get(0),schemaEntity,serverUrl);
		}else{
			mailContent = getMailContent(dataStreams,schemaEntity,totStream,serverUrl);
		}
		mailConfig.setText(mailContent);
		return mailConfig;
	}
	
	private String getMailContent(List<DatastreamEntity> dataStreams,SchemaEntity schemaEntity,int totStream,String serverUrl ){
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("dataStreams",dataStreams);
        model.put("totStream",totStream);
        model.put("maxStream", GenericType.maxEmailStream);
        model.put("serverUrl", serverUrl);
		String mailContent =  VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, getTemplateForList(totStream), model);
		return mailContent;
	}
	private String getMailContent(DatastreamEntity dataStreamEntity,SchemaEntity schemaEntity,String serverUrl){
		Map<String,String> model = new HashMap<String,String>();
		model.put("streamStatus",StreamStatus.getStatus(String.valueOf(dataStreamEntity.getChecked())).name());
		Date date = dataStreamEntity.getReceivedDate();
		SimpleDateFormat dFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		model.put("receivedTime",dFormat.format(date));
		model.put("message",dataStreamEntity.getMessage());
        serverUrl += String.valueOf(dataStreamEntity.getIdDatastream());
        model.put("schema",serverUrl);
        model.put("schemaName",dataStreamEntity.getSchemaName());

		String mailContent =  VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, getTemplate(), model);
		return mailContent;
	}
	
	private String getTemplateForList(int totStream){
		StringBuilder mTemplate = new StringBuilder();
		mTemplate.append(this.mailTemplate);
		mTemplate.append("_").append("oneaday");
        if(totStream > GenericType.maxEmailStream)
            mTemplate.append("_count");
        mTemplate.append("_");
		mTemplate.append(Locale.getDefault().getLanguage());
		mTemplate.append(".vm");
		return mTemplate.toString();
	}
	
	private String getTemplate(){
		StringBuilder mTemplate = new StringBuilder();
		mTemplate.append(this.mailTemplate);
		mTemplate.append("_");
		mTemplate.append(Locale.getDefault().getLanguage());
		mTemplate.append(".vm");
		return mTemplate.toString();
	}
	private String getMailToAddress(List<UserEntity> users){
		StringBuilder sBuilder = new StringBuilder();
		for (UserEntity userEntity : users) {
			if(sBuilder.toString().length()>0){
				sBuilder.append(",");
			}
			sBuilder.append(userEntity.getEmail());
		}
		return sBuilder.toString();
	}
	
	//-------------------SETTERS & GETTERS -------------------
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
	/**
	 * @return the mailTemplate
	 */
	public String getMailTemplate() {
		return mailTemplate;
	}
	/**
	 * @param mailTemplate the mailTemplate to set
	 */
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
