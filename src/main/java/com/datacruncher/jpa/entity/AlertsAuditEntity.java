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
package com.datacruncher.jpa.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "jv_alerts_audit")
@NamedQueries({
		@NamedQuery(name = "AlertsAuditEntity.findAll", query = "SELECT j FROM AlertsAuditEntity j"),
		@NamedQuery(name = "AlertsAuditEntity.findMaxEndDate", query = "SELECT max(j.jobEndDate) FROM AlertsAuditEntity j"),
		@NamedQuery(name = "AlertsAuditEntity.count", query = "SELECT COUNT (j) FROM AlertsAuditEntity j") })
		
public class AlertsAuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_audit")
	private long idAudit;

	@Temporal(TemporalType.DATE)
	@Column(name = "job_start_date")
	private Date jobStartDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "job_end_date")
	private Date jobEndDate;
	
	@Column(name = "status")
	private String status;

	@Column(name = "streams_found")
	private long streamsFound;
	/**
	 * @return the idAudit
	 */
	public long getIdAudit() {
		return idAudit;
	}

	/**
	 * 
	 */
	public AlertsAuditEntity() {
	}

	/**
	 * @param idAudit
	 * @param jobStartDate
	 * @param jobEndDate
	 * @param status
	 * @param streamsFound
	 */
	public AlertsAuditEntity(long idAudit, Date jobStartDate, Date jobEndDate,
			String status, long streamsFound) {
		this.idAudit = idAudit;
		this.jobStartDate = jobStartDate;
		this.jobEndDate = jobEndDate;
		this.status = status;
		this.streamsFound = streamsFound;
	}

	/**
	 * @param idAudit the idAudit to set
	 */
	public void setIdAudit(long idAudit) {
		this.idAudit = idAudit;
	}

	/**
	 * @return the jobStartDate
	 */
	public Date getJobStartDate() {
		return jobStartDate;
	}

	/**
	 * @param jobStartDate the jobStartDate to set
	 */
	public void setJobStartDate(Date jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	/**
	 * @return the jobEndDate
	 */
	public Date getJobEndDate() {
		return jobEndDate;
	}

	/**
	 * @param jobEndDate the jobEndDate to set
	 */
	public void setJobEndDate(Date jobEndDate) {
		this.jobEndDate = jobEndDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the streamsFound
	 */
	public long getStreamsFound() {
		return streamsFound;
	}

	/**
	 * @param streamsFound the streamsFound to set
	 */
	public void setStreamsFound(long streamsFound) {
		this.streamsFound = streamsFound;
	}
}
