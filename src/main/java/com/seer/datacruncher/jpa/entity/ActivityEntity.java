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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_activity")

@NamedQueries({
    @NamedQuery(name = "ActivityEntity.findAll", query = "SELECT j FROM ActivityEntity j"),
    @NamedQuery(name = "ActivityEntity.count", query = "SELECT COUNT (j) FROM ActivityEntity j")})
public class ActivityEntity {

	@Id
    @Basic(optional = false)
    @Column(name = "id_activity")
	private long idActivity;
	
	@Basic(optional = false)
	@Column(name = "id_script")
	private String idScript;
	
	@Column(name = "activity")
	private String activity;

	/**
	 * Default constructor otherwise hibernate finder methods will be failed on this entity.
	 */
	public ActivityEntity() {
		//
	}
	
	public ActivityEntity(long id, String idScript, String activity) {
		this.idActivity = id;
		this.idScript = idScript;
		this.activity = activity;
		
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

	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}
}