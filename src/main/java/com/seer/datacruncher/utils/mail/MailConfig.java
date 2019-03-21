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
package com.seer.datacruncher.utils.mail;

import java.io.InputStream;

public class MailConfig {
	private String mailTo;
	private String mailFrom;
	private String subject;
	private String text;
	private InputStream attachment;

	private String smptHost;
	private String smptPort;
	private String smptUserName;
	private String smptPassword;
	private boolean smptAuthEnable;
	private boolean smptTlsEnable;
	private String attachmentName;
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
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the attachment
	 */
	public InputStream getAttachment() {
		return attachment;
	}
	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(InputStream attachment) {
		this.attachment = attachment;
	}
	/**
	 * @return the smptHost
	 */
	public String getSmptHost() {
		return smptHost;
	}
	/**
	 * @param smptHost the smptHost to set
	 */
	public void setSmptHost(String smptHost) {
		this.smptHost = smptHost;
	}
	/**
	 * @return the smptPort
	 */
	public String getSmptPort() {
		return smptPort;
	}
	/**
	 * @param smptPort the smptPort to set
	 */
	public void setSmptPort(String smptPort) {
		this.smptPort = smptPort;
	}
	/**
	 * @return the smptUserName
	 */
	public String getSmptUserName() {
		return smptUserName;
	}
	/**
	 * @param smptUserName the smptUserName to set
	 */
	public void setSmptUserName(String smptUserName) {
		this.smptUserName = smptUserName;
	}
	/**
	 * @return the smptPassword
	 */
	public String getSmptPassword() {
		return smptPassword;
	}
	/**
	 * @param smptPassword the smptPassword to set
	 */
	public void setSmptPassword(String smptPassword) {
		this.smptPassword = smptPassword;
	}
	/**
	 * @return the smptAuthEnable
	 */
	public boolean isSmptAuthEnable() {
		return smptAuthEnable;
	}
	/**
	 * @param smptAuthEnable the smptAuthEnable to set
	 */
	public void setSmptAuthEnable(boolean smptAuthEnable) {
		this.smptAuthEnable = smptAuthEnable;
	}
	/**
	 * @return the smptTlsEnable
	 */
	public boolean isSmptTlsEnable() {
		return smptTlsEnable;
	}
	/**
	 * @param smptTlsEnable the smptTlsEnable to set
	 */
	public void setSmptTlsEnable(boolean smptTlsEnable) {
		this.smptTlsEnable = smptTlsEnable;
	}
	/**
	 * @return the attachmentName
	 */
	public String getAttachmentName() {
		return attachmentName;
	}
	/**
	 * @param attachmentName the attachmentName to set
	 */
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}
