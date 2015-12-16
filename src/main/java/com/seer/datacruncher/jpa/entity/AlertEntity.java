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
package com.seer.datacruncher.jpa.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_alerts_table")
@NamedQueries({
		@NamedQuery(name = "AlertEntity.findAll", query = "SELECT j FROM AlertEntity j"),
		@NamedQuery(name = "AlertEntity.count", query = "SELECT COUNT (j) FROM AlertEntity j") })
public class AlertEntity {

	@Id
	@Basic(optional = false)
	@Column(name = "id_alert")
	private long idAlert;

	@Column(name = "alert_name")
	private String alertName;

	/**
	 * 
	 */
	public AlertEntity() {
	}
	
	/**
	 * @param idAlert
	 * @param alertName
	 */
	public AlertEntity(long idAlert, String alertName) {
		this.idAlert = idAlert;
		this.alertName = alertName;
	}


	/**
	 * @return the idAlert
	 */
	public long getIdAlert() {
		return idAlert;
	}

	/**
	 * @param idAlert
	 *            the idAlert to set
	 */
	public void setIdAlert(long idAlert) {
		this.idAlert = idAlert;
	}

	/**
	 * @return the alertName
	 */
	public String getAlertName() {
		return alertName;
	}

	/**
	 * @param alertName
	 *            the alertName to set
	 */
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
}
