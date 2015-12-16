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
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.mail.MailConfig;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpiredLicenseEmails implements Job,DaoSet {

	private Logger log = Logger.getLogger(this.getClass());
    private String mailTemplate;
    private String mailFrom;
    private VelocityEngine velocityEngine;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        mailTemplate = context.getJobDetail().getJobDataMap().getString("mailTemplate");
        mailFrom = context.getJobDetail().getJobDataMap().getString("mailFrom");
        velocityEngine = (VelocityEngine)context.getJobDetail().getJobDataMap().get("velocityEngine");
        AppInfoBean appInfo = (AppInfoBean) AppContext.getApplicationContext().getBean("appInfoBean");
		if (!appInfo.isExpired() && Mail.getJavaMailService() != null) {

            String strLicenseDate = appInfo.getValidity();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            int diff = 0;
            try {
				Date licenseDate = sdf.parse(strLicenseDate);
				diff = (int)((licenseDate.getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000)) + 1;
			} catch (ParseException e) {
				log.error("ExpiredLicenseEmails :: Date ParseException", e);
			}
            log.info("diff:"+diff);
            if (diff == 1 || diff == 7 || diff == 30) {

                String mailTo = "";
                for (UserEntity user : usersDao.getAdmins())
                    mailTo += (user.getEmail() + ",");
                log.info("mailTo:"+mailTo);
				if (mailTo.endsWith(",")) {
                    MailConfig mailConfig = new MailConfig();
                    mailConfig.setSubject(MessageFormat.format(I18n.getMessage("label.valid-until-before"), diff));
                    mailConfig.setMailFrom(mailFrom);
                    Map<String, String> model = new HashMap<String, String>();
                    model.put("client", appInfo.getClient() == null ? "" : appInfo.getClient());
                    model.put("dealer", appInfo.getDealer() == null ? "" : appInfo.getDealer());
                    model.put("module", appInfo.getModule() == null ? "" : appInfo.getModule());
                    model.put("validity", appInfo.getValidity() == null ? "" : appInfo.getValidity());
                    mailConfig.setText(CommonUtils.mergeVelocityTemplateForEmail(velocityEngine, mailTemplate, model));
					mailConfig.setMailTo(mailTo.substring(0, mailTo.length() - 1));
					try {
						Mail.getJavaMailService().sendMail(mailConfig);
                        log.info("License expiration in "+diff +" days. Sent alert email to :"+mailTo);
					} catch (Exception e) {
						log.error("Unable to send an email about license expiration to administrator", e);
					}
				}
            }
		}
	}
	
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
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
