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
package com.datacruncher.alerts;

import com.datacruncher.jpa.entity.DatastreamEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.UserEntity;

import java.util.List;

public class AlertsData {
	private List<DatastreamEntity> dataStreams;
	private SchemaEntity schemaEntity;
	private List<UserEntity> users;
	private String alertType;
    private int totStream =0;
	
	public AlertsData() {
		
	}
	
	/**
	 * @param dataStreams
	 * @param schemaEntity
	 * @param users
	 * @param alertType
	 */
	public AlertsData(List<DatastreamEntity> dataStreams,
			SchemaEntity schemaEntity, List<UserEntity> users, String alertType) {
		this.dataStreams = dataStreams;
		this.schemaEntity = schemaEntity;
		this.users = users;
		this.alertType = alertType;
	}
	//----------SETTERS & GETTERS--------------
	
	/**
	 * @return the schemaEntity
	 */
	public SchemaEntity getSchemaEntity() {
		return schemaEntity;
	}
	/**
	 * @return the dataStreams
	 */
	public List<DatastreamEntity> getDataStreams() {
		return dataStreams;
	}
	/**
	 * @param dataStreams the dataStreams to set
	 */
	public void setDataStreams(List<DatastreamEntity> dataStreams) {
		this.dataStreams = dataStreams;
	}
	/**
	 * @param schemaEntity the schemaEntity to set
	 */
	public void setSchemaEntity(SchemaEntity schemaEntity) {
		this.schemaEntity = schemaEntity;
	}
	/**
	 * @return the users
	 */
	public List<UserEntity> getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
	 */
	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}

	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

    public int getTotStream() {
        return totStream;
    }

    public void setTotStream(int totStream) {
        this.totStream = totStream;
    }
}
