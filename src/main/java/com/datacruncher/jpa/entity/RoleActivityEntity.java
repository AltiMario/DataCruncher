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
import javax.persistence.Transient;

@Entity
@Table(name = "jv_role_activity")

@NamedQueries({
    @NamedQuery(name = "RoleActivityEntity.findAll", query = "SELECT j FROM RoleActivityEntity j"),
    @NamedQuery(name = "RoleActivityEntity.count", query = "SELECT COUNT (j) FROM RoleActivityEntity j"),
    @NamedQuery(name = "RoleActivityEntity.findByRoleId", query = "SELECT j FROM RoleActivityEntity j WHERE j.idRole = :idRole"),
    @NamedQuery(name = "RoleActivityEntity.findScriptIdByRoleIdAndActivityId", query = "SELECT a.idScript FROM RoleActivityEntity r,ActivityEntity a WHERE r.idRole = :idRole and r.idActivity=a.idActivity")    
})

public class RoleActivityEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_role_activity")
	private long idRoleActivity;
	
	@Basic(optional = false)
    @Column(name = "id_role")
	private long idRole;
	
	@Basic(optional = false)
    @Column(name = "id_activity")
	private long idActivity;
	
	@Basic(optional = false)
    @Column(name = "allowed")
	private boolean allowed;

	@Transient
	private String idScript;
	
	/**
	 * 
	 */
	public RoleActivityEntity() {
	}

	/**
	 * @param idRole
	 * @param idActivity
	 * @param allowed
	 */
	public RoleActivityEntity(long idRole, long idActivity, boolean allowed) {
		this.idRole = idRole;
		this.idActivity = idActivity;
		this.allowed = allowed;
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
	 * @return the idActivity
	 */
	public long getIdActivity() {
		return idActivity;
	}

	/**
	 * @param idActivity the idActivity to set
	 */
	public void setIdActivity(long idActivity) {
		this.idActivity = idActivity;
	}

	/**
	 * @return the allowed
	 */
	public boolean isAllowed() {
		return allowed;
	}

	/**
	 * @param allowed the allowed to set
	 */
	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	/**
	 * @return the idRoleActivity
	 */
	public long getIdRoleActivity() {
		return idRoleActivity;
	}

	/**
	 * @param idRoleActivity the idRoleActivity to set
	 */
	public void setIdRoleActivity(long idRoleActivity) {
		this.idRoleActivity = idRoleActivity;
	}

	/**
	 * @return the idScript
	 */
	public String getIdScript() {
		return idScript;
	}

	/**
	 * @param idScript the idScript to set
	 */
	public void setIdScript(String idScript) {
		this.idScript = idScript;
	}
	
}