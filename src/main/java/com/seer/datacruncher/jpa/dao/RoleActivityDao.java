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

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.ReadOnlyTx;
import com.seer.datacruncher.jpa.entity.RoleActivityEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ReadOnlyTx
public class RoleActivityDao {

	Logger log = Logger.getLogger(this.getClass().getName());
	
    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected RoleActivityDao() {
	}
	
	public ReadList read(long idRole) {
		String logMsg = "RoleActivityDao:read():";
		ReadList readList = new ReadList();
		try {
			log.debug(logMsg + "Entry");
			@SuppressWarnings("unchecked")
			List<String> result = em.createNamedQuery("RoleActivityEntity.findScriptIdByRoleIdAndActivityId")
					.setParameter("idRole", idRole).getResultList();
			readList.setResults(result);
		} catch (Exception exception) {
			log.error(logMsg + "Exception : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
		return readList;
	}

    public void init() {
		String logMsg = "RoleActivityDao:init():";
		try {
			log.debug(logMsg + "Entry");
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("RoleActivityEntity.count").getResultList();
			if (count.get(0).longValue() == 0L) {
				// Admin Activities
                persistRecords(getAdminActivities(), Roles.ADMINISTRATOR);
				// Application Manager Activities
				persistRecords(getAppManagerActivities(), Roles.APPLICAITON_MANAGER);
				// Operator Activities
				persistRecords(getOperatorActivities(), Roles.OPERATOR);
				// Dispatcher Activities
				persistRecords(getDispatcherActivities(), Roles.DISPATCHER);
				// User Activities
				persistRecords(getUserActivities(), Roles.USER);
			}
		} catch (Exception exception) {
			log.error("ActivityDao - init : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}
	
	private void persistRecords(Activity[] activites, Roles role){
		RoleActivityEntity  roleActivityEntry = null;
		for (Activity activity : activites) {
			roleActivityEntry = new RoleActivityEntity(role.getDbCode(),activity.getDbCode(),true);
			commonDao.persist(roleActivityEntry);
			roleActivityEntry = null;
		}
		activites = null;
	}
	
	private Activity[] getAdminActivities(){
		Activity[] adminActivities = Activity.values();
		return adminActivities;
	}
	
	private Activity[] getAppManagerActivities(){
		Activity[] appManagerActivities = {
				//Application Menu
				Activity.APPLICATION_ADD,
				Activity.APPLICATION_DELETE,
				Activity.APPLICATION_EDIT,
				Activity.APPLICATION_HELP,
				
				//Database Menu
				Activity.DATABASE_ADD,
				Activity.DATABASE_DELETE,
				Activity.DATABASE_EDIT,
				Activity.DATABASE_REFRESH,
				Activity.DATABASE_HELP,
				
				//Schema Menu
				Activity.SCHEMA_LIST,
				Activity.SCHEMA_ADD,
				Activity.SCHEMA_DELETE,
				Activity.SCHEMA_EDIT,
				Activity.SCHEMA_DUPLICATE,
				Activity.SCHEMA_EDIT_SCHMAFIELDS,
				Activity.SCHEMA_LOAD_SCHMAFIELDS,
				Activity.SCHEMA_VALIDATE_DATASTREAM,
				Activity.SCHEMA_DATA_STREAM_RECEIVED,
				Activity.SCHEMA_MACRO,
				Activity.SCHEMA_CUSTOM_ERRORS,	
				Activity.SCHEMA_DOCS,
				Activity.SCHEMA_EXPORT_TO_XSD,
				Activity.SCHEMA_IMPORT_FROM_XSD,
			    Activity.SCHEMA_EXPORT_XSD_IMAGE,
				Activity.SCHEMA_SHARE_WITH_WORLD,
				Activity.SCHEMA_HELP,
				Activity.SCHEMA_EXTRA_CHECK,
				Activity.SCHEMA_TRIGGERS,
				Activity.SCHEMA_SITE_GENERATION,
                Activity.SCHEMA_FORECASTING,
				
				//Standard Schema Menu
				Activity.STANDARD_SCHEMA_LIST,
				Activity.STANDARD_SCHEMA_ADD,
				Activity.STANDARD_SCHEMA_DELETE,
				Activity.STANDARD_SCHEMA_EDIT,
				Activity.STANDARD_SCHEMA_DUPLICATE,
				Activity.STANDARD_SCHEMA_VALIDATE_DATASTREAM,
				Activity.STANDARD_SCHEMA_DATA_STREAM_RECEIVED,
				Activity.STANDARD_SCHEMA_DOCS,
				Activity.STANDARD_SCHEMA_HELP,
				Activity.STANDARD_SUPPORTED,
				
				//Generation Stream Menu
				Activity.GENERATION_STREAM_LIST,
				Activity.GENERATION_STREAM_ADD,
				Activity.GENERATION_STREAM_DELETE,
				Activity.GENERATION_STREAM_EDIT,
				Activity.GENERATION_STREAM_HELP,
				Activity.GENERATION_STREAM_DUPLICATE,
				Activity.GENERATION_STREAM_SEND,
				Activity.GENERATION_STREAM_DOCS,
				Activity.GENERATION_STREAM_IMPORT_FROM_XSD,
				Activity.GENERATION_STREAM_EDIT_FIELDS,

				// Stream Loading Menu
				Activity.STREAM_LOADING_ADD,
				Activity.STREAM_LOADING_DELETE,
				Activity.STREAM_LOADING_EDIT,
				Activity.STREAM_LOADING_EDIT_FIELDS,
				Activity.STREAM_LOADING_HELP,
				Activity.STREAM_LOADING_LIST,
				
				//Users Menu
				Activity.USER_ADD,
				Activity.USER_DELETE,
				Activity.USER_EDIT,
				Activity.USER_HELP,
				
				
				//Connections Menu
				Activity.CONNECTION_ADD,
				Activity.CONNECTION_DELETE,
				Activity.CONNECTION_EDIT,
				Activity.CONNECTION_REFRESH,
				Activity.CONNECTION_HELP,
				
				//JOBS Menu
				Activity.JOBS_ADD,
				Activity.JOBS_DELETE,
				Activity.JOBS_EDIT,
				Activity.JOBS_HELP,
				
				//Scheduler Menu
				Activity.SCHEDULER_ADD,
				Activity.SCHEDULER_DELETE,
				Activity.SCHEDULER_EDIT,
				Activity.SCHEDULER_HELP,
				
				//Reports Menu
				Activity.REPORT_REALTIME,
				Activity.REPORT_DETAILED_STATE,
				Activity.REPORT_MONTHLY_STATE,
				Activity.REPORT_ANNUAL_STATE,
				Activity.REPORT_HELP,
				
				//Admin Menu
				Activity.ADMIN_HELP,
				Activity.ADMIN_INFO,
				Activity.ADMIN_LOG,
				Activity.USER_LOGOUT,	
				
				//DB Info Menu
				Activity.DBINFO_GENERAL,	
				Activity.DBINFO_SUPPORT,
				Activity.DBINFO_LIMITATION,
				Activity.DBINFO_FUNCITON,
				Activity.DBINFO_STD_SQL_TYPE,
				Activity.DBINFO_USER_DEFINE_TYPE,
				Activity.DBINFO_CATALOG,
				Activity.DBINFO_SCHEMA,
				Activity.DBINFO_PROCEDURE,
				Activity.DBINFO_PARAMETER,
				Activity.DBINFO_INDEX,
				Activity.DBINFO_TABLE_MODEL,
				Activity.DBINFO_DB_METADATA,
				Activity.DBINFO_TABLE_METADATA,
				Activity.DBINFO_DATA,
				Activity.DBINFO_ALL_TABLES,
				Activity.DBINFO_TABLE,
				Activity.DBINFO_COLUMN,
				
				//Data Quality Menu
				Activity.DATA_QUALITY_DUPLICATE,	
				Activity.DATA_QUALITY_SIMILARITY,
				Activity.DATA_QUALITY_STANDARDIZATION,
				Activity.DATA_QUALITY_REPLACE_NULL,
				Activity.DATA_QUALITY_AND,
				Activity.DATA_QUALITY_OR,
				Activity.DATA_QUALITY_MATCHED,
				Activity.DATA_QUALITY_UNMATCHED,
				Activity.DATA_QUALITY_UPPER_CASE,
				Activity.DATA_QUALITY_LOWER_CASE,
				Activity.DATA_QUALITY_TITLE_CASE,
				Activity.DATA_QUALITY_SENTENCE_CASE,
				Activity.DATA_QUALITY_DESCRETE_MATCH,
				Activity.DATA_QUALITY_DESCRETE_NO_MATCH,
				Activity.DATA_QUALITY_CARDINALITY,
				Activity.DATA_QUALITY_CARDINALITY_EDITABLE,
				Activity.DATA_QUALITY_TABLE_COMPARISON
		};
		return appManagerActivities;
	}
	
	private Activity[] getOperatorActivities(){
		Activity[] operatorActivities = {
				//Application Menu
				Activity.APPLICATION_HELP,
				//User Menu
				Activity.USER_EDIT,
				Activity.USER_LOGOUT,
				Activity.USER_HELP,
				//Report Menu
				Activity.REPORT_REALTIME,
				Activity.REPORT_DETAILED_STATE,
				Activity.REPORT_MONTHLY_STATE,
				Activity.REPORT_ANNUAL_STATE,
				Activity.REPORT_HELP,
				//Schema Menu
				Activity.SCHEMA_LIST,
				Activity.SCHEMA_ADD,
				Activity.SCHEMA_DELETE,
				Activity.SCHEMA_EDIT,
				Activity.SCHEMA_DUPLICATE,
				Activity.SCHEMA_EDIT_SCHMAFIELDS,
				Activity.SCHEMA_LOAD_SCHMAFIELDS,
				Activity.SCHEMA_VALIDATE_DATASTREAM,
				Activity.SCHEMA_DATA_STREAM_RECEIVED,
				Activity.SCHEMA_MACRO,
				Activity.SCHEMA_DOCS,
				Activity.SCHEMA_EXPORT_TO_XSD,
                Activity.SCHEMA_IMPORT_FROM_XSD,
				Activity.SCHEMA_EXPORT_XSD_IMAGE,
				Activity.SCHEMA_SHARE_WITH_WORLD,
				Activity.SCHEMA_SITE_GENERATION,
				Activity.SCHEMA_HELP,
				Activity.SCHEMA_EXTRA_CHECK,
				Activity.SCHEMA_TRIGGERS,
                Activity.SCHEMA_FORECASTING,
				
				//Standard Schema Menu
				Activity.STANDARD_SCHEMA_LIST,
				Activity.STANDARD_SCHEMA_ADD,
				Activity.STANDARD_SCHEMA_DELETE,
				Activity.STANDARD_SCHEMA_EDIT,
				Activity.STANDARD_SCHEMA_DUPLICATE,
				Activity.STANDARD_SCHEMA_VALIDATE_DATASTREAM,
				Activity.STANDARD_SCHEMA_DATA_STREAM_RECEIVED,
				Activity.STANDARD_SCHEMA_DOCS,
				Activity.STANDARD_SCHEMA_HELP,
				Activity.STANDARD_SUPPORTED,
				//Generation Stream Menu
				Activity.GENERATION_STREAM_LIST,
				Activity.GENERATION_STREAM_ADD,
				Activity.GENERATION_STREAM_DELETE,
				Activity.GENERATION_STREAM_EDIT,
				Activity.GENERATION_STREAM_DUPLICATE,
				Activity.GENERATION_STREAM_DOCS,
				Activity.GENERATION_STREAM_IMPORT_FROM_XSD,
				Activity.GENERATION_STREAM_HELP,
		};
		return operatorActivities;
	}
	
	private Activity[] getDispatcherActivities(){
		Activity[] dispatcherActivities = {
				//Application Menu
				Activity.APPLICATION_HELP,
				//User Menu
				Activity.USER_EDIT,
				Activity.USER_LOGOUT,
				Activity.USER_HELP,
				//Schema Menu
				Activity.SCHEMA_LIST,
				Activity.SCHEMA_VALIDATE_DATASTREAM,
				Activity.SCHEMA_DATA_STREAM_RECEIVED,
				Activity.SCHEMA_HELP,
		};
		return dispatcherActivities;
	}
	
	private Activity[] getUserActivities(){
		Activity[] userActivities = {
				//User Menu
				Activity.USER_EDIT,
				Activity.USER_LOGOUT,
				Activity.USER_HELP,
		};
		return userActivities;
	}
}
