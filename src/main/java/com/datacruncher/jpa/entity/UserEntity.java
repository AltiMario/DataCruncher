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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "jv_users")
@NamedQueries({
	@NamedQuery(name = "UserEntity.findAll" ,query="SELECT u FROM UserEntity u ORDER BY u.idUser DESC"),
	@NamedQuery(name = "UserEntity.findByCreatedBy" ,query="SELECT u FROM UserEntity u WHERE u.createdBy=:createdBy ORDER BY u.idUser DESC"),
	@NamedQuery(name = "UserEntity.findByUserNameAndPassword" ,query="SELECT u FROM UserEntity u WHERE u.userName = :userName and u.password = :password ORDER BY u.idUser DESC"),
	@NamedQuery(name = "UserEntity.findByUserName" ,query="SELECT u FROM UserEntity u WHERE u.userName = :userName ORDER BY u.idUser DESC"),
	@NamedQuery(name = "UserEntity.findUserByNameNMailId", query = "SELECT j FROM UserEntity j WHERE j.userName= :userName and j.email= :email"),
	@NamedQuery(name = "UserEntity.count", query = "SELECT COUNT (j) FROM UserEntity j WHERE j.idRole=1"),
	@NamedQuery(name = "UserEntity.countDuplicateByName", query = "SELECT COUNT (u) FROM UserEntity u WHERE u.idUser != :idUser AND u.userName = :userName")
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_user")
    private long idUser;
    
    @Column(name = "username" , unique = true)
    private String userName;
    
    @Column(name = "password")
    private String password;
    
	@Column(name = "name")
    private String name;
    
	@Column(name = "surname")
    private String surname;
	
	@Column(name = "email")
    private String email;
	
	@Column(name = "enabled")
	private Integer enabled = 1;

	@Column(name = "id_role")
	private long idRole = 5;
	
	@Column(name="language")
	private String language = "en";
	
	@Column(name="id_alert")
	private long idAlert = 1;
	
	@Column(name="created_by")
	private long createdBy;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date_of_birth")
	private Date dateOfBirth;
	
	@Column(name="theme")
	private String theme;
	
	@Transient
	private List<UserApplicationsEntity> userApplications  = new ArrayList<UserApplicationsEntity>();
	
	@Transient
	private List<UserSchemasEntity> userSchemas  = new ArrayList<UserSchemasEntity>();
	
	@Transient
	private List<String> roleActivities;
	
	
	public UserEntity(){
	}

	public UserEntity(String userName, String password, String name,
			String surname, String email, Integer enabled, long idRole,
			String language, long idAlert, long createdBy, Date date, String theme) {
		this.userName = userName;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.enabled = enabled;
		this.idRole = idRole;
		this.language = language;
		this.idAlert = idAlert;
		this.createdBy = createdBy;
		this.dateOfBirth = date;
		this.theme = theme;
	}
	
	/**
	 * @return the userSchemas
	 */
	@OneToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.ALL}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<UserSchemasEntity> getUserSchemas() {
		return userSchemas;
	}
	/**
	 * @param userSchemas the userSchemas to set
	 */
	public void setUserSchemas(List<UserSchemasEntity> userSchemas) {
		this.userSchemas = userSchemas;
	}
	/**
	 * @return the userApplications
	 */
	@OneToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.ALL}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<UserApplicationsEntity> getUserApplications() {
		return userApplications;
	}
	/**
	 * @param userApplications the userApplications to set
	 */
	public void setUserApplications(List<UserApplicationsEntity> userApplications) {
		this.userApplications = userApplications;
	}
	public long getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 * @param userName
	 * @param password
	 * @param name
	 * @param surname
	 * @param email
	 * @param enabled
	 * @param idRole
	 * @param dateOfBirth
	 */
	public UserEntity(long idUser, String userName, String password,
			String name, String surname, String email, Integer enabled,
			long idRole, Date dateOfBirth) {
		this.idUser = idUser;
		this.userName = userName;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.enabled = enabled;
		this.idRole = idRole;
		this.dateOfBirth = dateOfBirth;
	}
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the idRole
	 */
	public long getIdRole() {
		return idRole;
	}

	/**
	 * @param idRole the idRole to set
	 */
	public void setIdRole(long idRole) {
		this.idRole = idRole;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	/**
	 * @return the roleActivities
	 */
	public List<String> getRoleActivities() {
		return roleActivities;
	}
	/**
	 * @param roleActivities the roleActivities to set
	 */
	public void setRoleActivities(List<String> roleActivities) {
		this.roleActivities = roleActivities;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the idAlert
	 */
	public long getIdAlert() {
		return idAlert;
	}
	/**
	 * @param idAlert the idAlert to set
	 */
	public void setIdAlert(long idAlert) {
		this.idAlert = idAlert;
	}

	/**
	 * @return the createdBy
	 */
	public long getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}