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

package com.seer.datacruncher.jpa.dao;

import javax.persistence.EntityManager;

public interface DaoSet {

	CustomErrorsDao customErrorsDao = DaoServices.getCustomErrorsDao();
	MacrosDao macrosDao = DaoServices.getMacrosDao();
	ActivityDao activityDao = DaoServices.getActivityDao();	
	AlertsDao alertsDao = DaoServices.getAlertsDao();
	AlertsAuditDao alertsAuditDao = DaoServices.getAlertsAuditDao();
	AlphanumericFieldValuesDao alphaFieldDao = DaoServices.getAlphaFieldDao();
	ApplicationsDao appDao = DaoServices.getApplicationsDao();
	ContactsDao contactDao = DaoServices.getContactsDao();
	ChecksTypeDao checksTypeDao = DaoServices.getChecksTypeDao();
    SchemaLibDao schemaLibDao = DaoServices.getSchemaLibDao();
	ConnectionsDao connectionsDao = DaoServices.getConnectionsDao();	
	DatabasesDao dbDao = DaoServices.getDbDao();
	DatastreamsDao datastreamsDao = DaoServices.getDatastreamDao();
	FileDao fileDao = DaoServices.getFileDao();
	JobsDao jobsDao = DaoServices.getJobsDao();	
	NumericFieldValuesDao numericFieldDao = DaoServices.getNumericFieldDao();
	CreditsDao creditsDao = DaoServices.getCreditsDao();
	RoleActivityDao roleActivityDao = DaoServices.getRoleActivityDao();
	RoleDao roleDao = DaoServices.getRoleDao();	
	SchemaFieldsDao schemaFieldsDao = DaoServices.getSchemaFieldsDao();
	SchemasDao schemasDao = DaoServices.getSchemasDao();
	SchemasXSDDao schemasXSDDao = DaoServices.getSchemasXSDDao();
	ServersDao serversDao = DaoServices.getServersDao();
	TasksDao tasksDao = DaoServices.getTasksDao();
	UserApplicationsDao userAppDao = DaoServices.getUserAppDao();
	UserSchemasDao userSchemasDao = DaoServices.getUserSchemasDao();
	UsersDao usersDao = DaoServices.getUsersDao();
	EventTriggerDao eventTriggerDao = DaoServices.getEventTriggerDao();
    SchemaTriggerStatusDao schemaTriggerStatusDao  =  DaoServices.getSchemaTriggerStatusDao();
	EntityManager entityManager = DaoServices.getEntityManager();
	ApplicationConfigDao applicationConfigDao = DaoServices.getApplicationConfigDao();
    SchemaSQLDao schemaSQLDao =  DaoServices.getSchemaSQLDao();
    LogDao logDao =  DaoServices.getLogDao();
    ForecastDao forecastDao =  DaoServices.getForecastDao();
}
