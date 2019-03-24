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
package com.datacruncher.utils.mail;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailService {
	private Logger log = Logger.getLogger(this.getClass());
	private JavaMailSender mailSender;

    public void sendMail(MailConfig mailConfig) throws Exception {
        String logMsg = "MailService:sendMail():";
        InputStream attachment = null;
        MimeMessage mimeMessage = null;
        MimeMessageHelper helper = null;
        try {
            mimeMessage = mailSender.createMimeMessage();
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setText(mailConfig.getText(),true);
            if (StringUtils.isEmpty(mailConfig.getMailTo())) {
                log.error("Invalid or empty 'toAddress' configured!!");
                throw new Exception("Invalid or empty 'toAddress' configured");
            }
            if (StringUtils.isEmpty(mailConfig.getMailFrom())) {
                log.error("Invalid or empty 'FromAddress' configured!!");
                throw new Exception("Invalid or empty 'FromAddress' configured");
            }
            if(!isEmailValid(mailConfig.getMailFrom())){
                log.error("Invalid 'FromAddress' configured!!");
                throw new Exception("Invalid 'FromAddress' configured");
            }
            helper.setFrom(new InternetAddress(mailConfig.getMailFrom()));
            helper.setSubject(mailConfig.getSubject());
            helper.setTo(getToAddress(mailConfig.getMailTo()));
            attachment = mailConfig.getAttachment();
            if (attachment != null) {
                StreamAttachmentDataSource datasource = new StreamAttachmentDataSource(mailConfig.getAttachment());
                helper.addAttachment(mailConfig.getAttachmentName(), datasource);
            }
            this.mailSender.send(mimeMessage);

        } catch (AuthenticationFailedException afex) {
            log.error(logMsg+"AuthenticationFailedException:",afex);
            throw new Exception("AuthenticationFailedException", afex);
        } catch (MessagingException mex) {
            log.error(logMsg+"Exception:",mex);
            throw new Exception("MessagingException", mex);
        } catch(Exception ex) {
            log.error(logMsg+"Exception:",ex);
            throw ex;
        }
    }
	//----------------------HELPERS------------------
	private InternetAddress[] getToAddress(String mailToAddress){
		try{
			List<InternetAddress> toAddress = new ArrayList<InternetAddress>();
			StringBuilder failedMailAddress = new StringBuilder();
			String[] addressList = mailToAddress.split(",");
			InternetAddress interAddr = null;
			for (String mailTo : addressList) {
				if(isEmailValid(mailTo)){
					interAddr = new InternetAddress(mailTo);
					toAddress.add(interAddr);
				}else{
					if(failedMailAddress.toString().length()>0){
						failedMailAddress.append(",");
					}
					failedMailAddress.append(mailTo);
				}
			}
			InternetAddress[] finalToAddressList = new InternetAddress[toAddress.size()];
			if (toAddress != null && toAddress.size() > 0) {
				return toAddress.toArray(finalToAddressList);
			} else {
				if (StringUtils.isEmpty(failedMailAddress.toString())) {
					log.error("Unable to send the mail to [ " + failedMailAddress.toString()+"]");
				}
				throw new Exception("No valid toAddress configured");
			}
		}catch(Exception e){
			log.error(e);
		}
		return null;
	}
	
	private boolean isEmailValid(String email) {
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)|\\[([0-9])++(.[0-9])++\\])");
        Matcher matcher = pattern.matcher(email);
        return (matcher.matches());
    }
	
	public JavaMailSender getMailSender() {
		return mailSender;
	}
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	
}
