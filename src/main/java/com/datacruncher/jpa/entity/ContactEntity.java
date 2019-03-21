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

package com.datacruncher.jpa.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "jv_contacts")

public class ContactEntity implements Serializable {
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_contact")
    private long idContact;
    
    @Column(name = "firstname")
    private String firstName;
        
    @Column(name = "lastname")
    private String lastName;
	
    @Column(name = "emailid")
    private String emailID;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "company_website")
    private String companyWebsite;
    
    @Column(name = "position")
    private String position;
    
    @Transient
    private boolean isAuthorized;
    
    @Column(name= "is_shared")
    private boolean isShared;
    
    @Lob
    @Column(name = "msgtext")
    private String msgText;
    
    @Column(name = "is_emailsent")
	private Integer isEmailSent = 0;
	    
    @Basic(optional = true)
    @Column(name = "id_schema")
    private long idSchema;
    
    @Basic(optional = false)
    @Column(name = "id_user")
    private long idUser;
    
	public ContactEntity() {
	}
	
	public ContactEntity(long idContact) {
		this.idContact = idContact;
	}
		
	public ContactEntity(long idContact, String firstName,
			String lastName, String emailID, String msgText) {
		this.idContact = idContact;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailID = emailID;
		this.msgText = msgText;
	}	
		
	public long getIdContact() {
		return idContact;
	}

	public void setIdContact(long idContact) {
		this.idContact = idContact;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	
	public Integer getIsEmailSent() {
		return isEmailSent;
	}
	
	public void setIsEmailSent(Integer isEmailSent) {
		this.isEmailSent = isEmailSent;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public boolean getIsAuthorized() {
		return isAuthorized;
	}

	public void setIsAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}
	
	public boolean getIsShared() {
		return isShared;
	}

	public void setIsShared(boolean isShared) {
		this.isShared = isShared;
	}
	
	public long getIdSchema() {
        return idSchema;
    }
    
    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
    }

	public long getIdUser() {
		return idUser;
	}

	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}
}