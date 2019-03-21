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
package com.datacruncher.constants;

public enum Activity {
	//Application Menu
	APPLICATION_ADD(1,"addApp"),
	APPLICATION_DELETE(2,"delApp"),
	APPLICATION_EDIT(3,"editApp"),
	APPLICATION_HELP(4,"appHelp"),
	
	//Database Menu
	DATABASE_ADD(5,"addDb"),
	DATABASE_DELETE(6,"delDb"),
	DATABASE_EDIT(7,"editDb"),
	DATABASE_REFRESH(8,"refershDb"),
	DATABASE_HELP(9,"dbHelp"),
	
	//Schema Menu
	SCHEMA_LIST(10,"schemasList"),
	SCHEMA_ADD(11,"addSchema"),
	SCHEMA_DELETE(12,"deleteSchema"),
	SCHEMA_EDIT(13,"editSchema"),
	SCHEMA_DUPLICATE(14,"duplicateSchema"),
	SCHEMA_EDIT_SCHMAFIELDS(15,"editSchemaFields"),
	SCHEMA_LOAD_SCHMAFIELDS(16,"loadSchemaFields"),
	SCHEMA_VALIDATE_DATASTREAM(17,"validateDatastream"),
	SCHEMA_DATA_STREAM_RECEIVED(18,"datastreamReceived"),
	SCHEMA_MACRO(19,"macro"),
	SCHEMA_CUSTOM_ERRORS(47,"customErrors"),	
	SCHEMA_DOCS(20,"schemaDocuments"),
	SCHEMA_EXPORT_TO_XSD(21,"schemaExportToXSD"),
    SCHEMA_IMPORT_FROM_XSD(71,"schemaImportFromXSD"),
	SCHEMA_EXPORT_XSD_IMAGE(46,"schemaXSDExportToPNG"),
	SCHEMA_SHARE_WITH_WORLD(60,"schemaShareWithWorld"),
	SCHEMA_HELP(22,"schemaHelp"),
	SCHEMA_EXTRA_CHECK(58,"schemaExtraCheck"),
	SCHEMA_TRIGGERS(73,"schemaTriggers"),
    SCHEMA_FORECASTING(120, "forecastForm"),
	
	//Standard Schema Menu
	STANDARD_SCHEMA_LIST(62,"standardSchemasList"),
	STANDARD_SCHEMA_ADD(63,"addStandardSchema"),
	STANDARD_SCHEMA_DELETE(64,"deleteStandardSchema"),
	STANDARD_SCHEMA_EDIT(65,"editStandardSchema"),
	STANDARD_SCHEMA_DUPLICATE(66,"duplicateStandardSchema"),
	STANDARD_SCHEMA_VALIDATE_DATASTREAM(67,"validateStandardDatastream"),
	STANDARD_SCHEMA_DATA_STREAM_RECEIVED(68,"standardDatastreamReceived"),
	STANDARD_SCHEMA_DOCS(69,"standardSchemaDocuments"),
	STANDARD_SCHEMA_HELP(70,"standardSchemaHelp"),
	STANDARD_SUPPORTED(72,"standardSupported"),
	
	//Generation Stream Menu
    GENERATION_STREAM_LIST(50,"generationStreamList"),
	GENERATION_STREAM_ADD(51,"addGenerationStream"),
	GENERATION_STREAM_DELETE(52,"deleteGenerationStream"),
	GENERATION_STREAM_EDIT(53,"editGenerationStream"),
	GENERATION_STREAM_HELP(54,"generationStreamHelp"),
	GENERATION_STREAM_DUPLICATE(55,"duplicateGenerationStream"),
	GENERATION_STREAM_SEND(61,"sendGenerationStream"),
	GENERATION_STREAM_DOCS(56,"generationStreamDocuments"),
	GENERATION_STREAM_IMPORT_FROM_XSD(57,"generationStreamImportFromXSD"),
    GENERATION_STREAM_EDIT_FIELDS(59,"editGenerationStreamFields"),

	// Stream Loading Menu
    STREAM_LOADING_LIST(114, "listStreamLoading"),
	STREAM_LOADING_ADD(115, "addStreamLoading"),
	STREAM_LOADING_DELETE(116, "deleteStreamLoading"),
	STREAM_LOADING_EDIT(117, "editStreamLoading"),
    STREAM_LOADING_EDIT_FIELDS(118, "editFieldsStreamLoading"),
	STREAM_LOADING_HELP(119, "helpStreamLoading"),
    
	//Users Menu
	USER_ADD(23,"addUser"),
	USER_DELETE(24,"delUser"),
	USER_EDIT(25,"editUser"),
	USER_HELP(26,"userHelp"),
	
	
	//Connections Menu
	CONNECTION_ADD(27,"addConnection"),
	CONNECTION_DELETE(28,"delConnection"),
	CONNECTION_EDIT(29,"editConnection"),
	CONNECTION_REFRESH(30,"refreshConnections"),
	CONNECTION_HELP(31,"connectionsHelp"),
	
	//JOBS Menu
	JOBS_ADD(32,"addJob"),
	JOBS_DELETE(33,"delJob"),
	JOBS_EDIT(34,"editJob"),
	JOBS_HELP(35,"jobHelp"),
	
	//Scheduler Menu
	SCHEDULER_ADD(36,"addTask"),
	SCHEDULER_DELETE(37,"delTask"),
	SCHEDULER_EDIT(38,"editTask"),
	SCHEDULER_HELP(39,"taskHelp"),
	
	//Reports Menu
	REPORT_REALTIME(40,"realTimeReport"),
	REPORT_DETAILED_STATE(76,"detailedStateReport"),
	REPORT_MONTHLY_STATE(41,"monthlyStateReport"),
	REPORT_ANNUAL_STATE(48,"annualStateReport"),
	REPORT_HELP(42,"reportHelp"),
	
	//Admin Menu
	ADMIN_SERVER(43,"server"),
	ADMIN_HELP(44,"adminHelp"),
	ADMIN_INFO(49,"adminInfo"),
	ADMIN_EMAIL(74,"adminEmail"),
	ADMIN_LOG(77,"adminLog"),
	ADMIN_FTP(75,"adminFTP"),
	USER_LOGOUT(45,"logoutUser"),	
	;
	
	private int dbCode;
	private String scriptCode;
	
	Activity(int dbCode, String scriptCode) {
        this.dbCode = dbCode;
        this.scriptCode = scriptCode;
    }
	
    public int getDbCode() {
        return dbCode;
    }
    public String getScriptCode() {
        return scriptCode;
    }
    public static Activity getStatus(int dbCode) {
    	Activity[] statusList = Activity.values();
        for (Activity status : statusList) {
			if (dbCode == status.getDbCode()) {
                return status;
            }
        }
        return null;
    }
}
