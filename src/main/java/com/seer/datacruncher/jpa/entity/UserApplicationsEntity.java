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
package com.seer.datacruncher.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_user_applications")
@NamedQueries({
	@NamedQuery(name="UserApplicationsEntity.findByUserId" , query="SELECT u FROM UserApplicationsEntity u WHERE u.idUser = :idUser"),
	@NamedQuery(name="UserApplicationsEntity.findByApplicationId", query="SELECT e FROM UserApplicationsEntity e WHERE e.idApplication = :appId")
})
public class UserApplicationsEntity {
	
	@Column(name="id_user")
	private long idUser;

	@Column(name="id_application")
	private long idApplication;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_user_application")
	private long idUserApplication;
	
	/**
	 * 
	 */
	public UserApplicationsEntity() {
	}

	/**
	 * @return the idUserApplication
	 */
	public long getIdUserApplication() {
		return idUserApplication;
	}

	/**
	 * @param idUserApplication the idUserApplication to set
	 */
	public void setIdUserApplication(long idUserApplication) {
		this.idUserApplication = idUserApplication;
	}

	/**
	 * @return the idUser
	 */
	public long getIdUser() {
		return idUser;
	}
	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return the idApplication
	 */
	public long getIdApplication() {
		return idApplication;
	}

	/**
	 * @param idApplication the idApplication to set
	 */
	public void setIdApplication(long idApplication) {
		this.idApplication = idApplication;
	}
}