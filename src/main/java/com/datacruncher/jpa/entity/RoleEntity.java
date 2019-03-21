
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_roles")

@NamedQueries({
    @NamedQuery(name = "RoleEntity.findAll", query = "SELECT j FROM RoleEntity j"),
    @NamedQuery(name = "RoleEntity.count", query = "SELECT COUNT (j) FROM RoleEntity j"),
    @NamedQuery(name = "RoleEntity.findByRoleId", query = "SELECT j FROM RoleEntity j WHERE j.idRole = :idRole")})

public class RoleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
    @Basic(optional = false)
    @Column(name = "id_role")
    private long idRole;
	
	@Basic(optional = false)
	@Column(name = "role_name")
	private String roleName;
	
	@Column(name = "description")
    private String description;

	/**
	 * @param roleName
	 * @param description
	 */
	public RoleEntity(String roleName, String description) {
		this.roleName = roleName;
		this.description = description;
	}
	/**
	 * Default constructor otherwise hibernate finder methods will be failed on this entity.
	 */
	public RoleEntity() {
		//
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
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}