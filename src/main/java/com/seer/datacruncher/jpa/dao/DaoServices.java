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

package com.seer.datacruncher.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * The Class Dao Services.
 */
public class DaoServices {

    private static ActivityDao activityDao;	
    private static AlertsDao alertsDao;
    private static AlertsAuditDao  alertsAuditDao;
    private static AlphanumericFieldValuesDao alphaFieldDao;
    private static ApplicationsDao applicationDao;  
    private static ChecksTypeDao checksTypeDao;
    private static SchemaLibDao schemaLibDao;
    private static ConnectionsDao connectionsDao;
    private static ContactsDao contactsDao;
    private static DatabasesDao dbDao;  
    private static DatastreamsDao datastreamDao;
    private static FileDao fileDao;
    private static JobsDao jobsDao;
    private static NumericFieldValuesDao numericFieldDao;
    private static RoleActivityDao roleActivityDao;
    private static RoleDao roleDao;    
    private static SchemaFieldsDao schemaFieldsDao;
    private static SchemasDao schemasDao;
    private static SchemasXSDDao schemasXSDDao;
    private static ServersDao serversDao;
    private static TasksDao tasksDao;
    private static UserApplicationsDao userAppDao;
    private static UserSchemasDao userSchemasDao;
    private static UsersDao usersDao;
    private static EventTriggerDao eventTriggerDao;
    private static SchemaTriggerStatusDao schemaTriggerStatusDao;
    private static EntityManager entityManager;
    private static ApplicationConfigDao applicationConfigDao;
    private static SchemaSQLDao schemaSQLDao;
    private static LogDao logDao;
    private static ForecastDao forecastDao;
	private static CustomErrorsDao customErrorsDao;
	private static MacrosDao macrosDao;

	public static CustomErrorsDao getCustomErrorsDao() {
		return customErrorsDao;
	}

	public void setCustomErrorsDao(CustomErrorsDao customErrorsDao) {
		DaoServices.customErrorsDao = customErrorsDao;
	}

	public static MacrosDao getMacrosDao() {
		return macrosDao;
	}

	public void setMacrosDao(MacrosDao macrosDao) {
		DaoServices.macrosDao = macrosDao;
	}
	public static ActivityDao getActivityDao() {
        return activityDao;
    }

    public void setActivityDao(ActivityDao activityDao) {
    	DaoServices.activityDao = activityDao;
    }    
    
	public static AlertsDao getAlertsDao() {
		return alertsDao;
	}

	public void setAlertsDao(AlertsDao alertsDao) {
		DaoServices.alertsDao = alertsDao;
	}    
	
	
	public static AlertsAuditDao getAlertsAuditDao() {
		return alertsAuditDao;
	}

	public void setAlertsAuditDao(AlertsAuditDao alertsAuditDao) {
		DaoServices.alertsAuditDao = alertsAuditDao;
	}   
	
	
	public static AlphanumericFieldValuesDao getAlphaFieldDao() {
		return alphaFieldDao;
	}

	public void setAlphaFieldDao(AlphanumericFieldValuesDao alphaFieldDao) {
		DaoServices.alphaFieldDao = alphaFieldDao;
	}	

    public static ApplicationsDao getApplicationsDao() {
        return applicationDao;
    }

    public void setApplicationsDao(ApplicationsDao applicationDao) {
    	DaoServices.applicationDao = applicationDao;
    }
    
	public static ChecksTypeDao getChecksTypeDao() {
		return checksTypeDao;
	}

	public void setChecksTypeDao(ChecksTypeDao checksTypeDao) {
		DaoServices.checksTypeDao = checksTypeDao;
	}

    public static SchemaLibDao getSchemaLibDao() {
        return schemaLibDao;
    }

    public void setSchemaLibDao(SchemaLibDao schemaLibDao) {
        DaoServices.schemaLibDao = schemaLibDao;
    }

    public static ConnectionsDao getConnectionsDao() {
		return connectionsDao;
	}

	public void setConnectionsDao(ConnectionsDao connectionsDao) {
		DaoServices.connectionsDao = connectionsDao;
	}

	public static ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		DaoServices.contactsDao = contactsDao;
	}
	
	public static DatabasesDao getDbDao() {
		return dbDao;
	}

	public void setDbDao(DatabasesDao dbDao) {
		DaoServices.dbDao = dbDao;
	}

	public static DatastreamsDao getDatastreamDao() {
		return datastreamDao;
	}

	public void setDatastreamDao(DatastreamsDao datastreamDao) {
		DaoServices.datastreamDao = datastreamDao;
	}

	public static FileDao getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		DaoServices.fileDao = fileDao;
	}

	public static JobsDao getJobsDao() {
		return jobsDao;
	}

	public void setJobsDao(JobsDao jobsDao) {
		DaoServices.jobsDao = jobsDao;
	}

	public static NumericFieldValuesDao getNumericFieldDao() {
		return numericFieldDao;
	}

	public void setNumericFieldDao(NumericFieldValuesDao numericFieldDao) {
		DaoServices.numericFieldDao = numericFieldDao;
	}

	public static RoleActivityDao getRoleActivityDao() {
		return roleActivityDao;
	}

	public void setRoleActivityDao(RoleActivityDao roleActivityDao) {
		DaoServices.roleActivityDao = roleActivityDao;
	}

	public static RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		DaoServices.roleDao = roleDao;
	}

	public static SchemaFieldsDao getSchemaFieldsDao() {
		return schemaFieldsDao;
	}

	public void setSchemaFieldsDao(SchemaFieldsDao schemaFieldsDao) {
		DaoServices.schemaFieldsDao = schemaFieldsDao;
	}

	public static SchemasDao getSchemasDao() {
		return schemasDao;
	}

	public void setSchemasDao(SchemasDao schemasDao) {
		DaoServices.schemasDao = schemasDao;
	}

	public static SchemasXSDDao getSchemasXSDDao() {
		return schemasXSDDao;
	}

	public void setSchemasXSDDao(SchemasXSDDao schemasXSDDao) {
		DaoServices.schemasXSDDao = schemasXSDDao;
	}

	public static ServersDao getServersDao() {
		return serversDao;
	}

	public void setServersDao(ServersDao serversDao) {
		DaoServices.serversDao = serversDao;
	}

	public static TasksDao getTasksDao() {
		return tasksDao;
	}

	public void setTasksDao(TasksDao tasksDao) {
		DaoServices.tasksDao = tasksDao;
	}

	public static UserApplicationsDao getUserAppDao() {
		return userAppDao;
	}

	public void setUserAppDao(UserApplicationsDao userAppDao) {
		DaoServices.userAppDao = userAppDao;
	}

	public static UserSchemasDao getUserSchemasDao() {
		return userSchemasDao;
	}

	public void setUserSchemasDao(UserSchemasDao userSchemasDao) {
		DaoServices.userSchemasDao = userSchemasDao;
	}

	public static UsersDao getUsersDao() {
		return usersDao;
	}

    public static SchemaTriggerStatusDao getSchemaTriggerStatusDao() {
        return schemaTriggerStatusDao;
    }

	public void setUsersDao(UsersDao usersDao) {
		DaoServices.usersDao = usersDao;
	}
    public void setSchemaTriggerStatusDao(SchemaTriggerStatusDao schemaTriggerStatusDao) {
        DaoServices.schemaTriggerStatusDao = schemaTriggerStatusDao;
    }

	public static EventTriggerDao getEventTriggerDao() {
		return eventTriggerDao;
	}

	public  void setEventTriggerDao(EventTriggerDao eventTriggerDao) {
		DaoServices.eventTriggerDao = eventTriggerDao;
	}

	public static EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManagerFactory entityManagerFactory) {
		DaoServices.entityManager = entityManagerFactory.createEntityManager();
	}
	
	public static ApplicationConfigDao getApplicationConfigDao() {
		return applicationConfigDao;
	}
	
	public void setApplicationConfigDao(ApplicationConfigDao applicationConfigDao) {
		DaoServices.applicationConfigDao = applicationConfigDao;
	}
	
    public static SchemaSQLDao getSchemaSQLDao() {
        return schemaSQLDao;
    }

    public void setSchemaSQLDao(SchemaSQLDao schemaSQLDao) {
        DaoServices.schemaSQLDao = schemaSQLDao;
    }

    public static LogDao getLogDao() {
        return logDao;
    }

    public void setLogDao(LogDao logDao) {
    	DaoServices.logDao = logDao;
    }

    public static ForecastDao getForecastDao() {
        return forecastDao;
    }

    public void setForecastDao(ForecastDao forecastDao) {
        DaoServices.forecastDao = forecastDao;
    }

}
