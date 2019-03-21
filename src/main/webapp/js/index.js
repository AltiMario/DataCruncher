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
Ext.QuickTips.init();
delete Ext.tip.Tip.prototype.minWidth;
Ext.Loader.setConfig({enabled: true});

var isEmailErrorFound = false;
var required = '<span style="color:black;font-weight:bold" data-qtip="Required">*</span>';
var App = new Ext.App();
var graphLoadTimer;

//Application menu
var addAppObj = new Object();
addAppObj.iconCls = 'application_add';
addAppObj.text = _message['add'];
addAppObj.leaf = true;
addAppObj.id = 'addApp';

var deleteAppObj = new Object();
deleteAppObj.iconCls = 'application_delete';
deleteAppObj.text = _message['delete'];
deleteAppObj.leaf = true;
deleteAppObj.id = 'delApp';

var editAppObj = new Object();
editAppObj.iconCls = 'application_edit';
editAppObj.text = _message['edit'];
editAppObj.leaf = true;
editAppObj.id = 'editApp';

var helpAppObj = new Object();
helpAppObj.iconCls = 'help';
helpAppObj.text = _message['help'];
helpAppObj.leaf = true;
helpAppObj.id = 'appHelp';

//Database Menu
var addDbObj = new Object();
addDbObj.iconCls = 'database_add';
addDbObj.text = _message['add'];
addDbObj.leaf= true;
addDbObj.id= 'addDb';

var deleteDbObj = new Object();
deleteDbObj.iconCls=  'database_delete';
deleteDbObj.text=  _message['delete'];
deleteDbObj.leaf= true;
deleteDbObj.id= 'delDb';

var editDbObj = new Object();
editDbObj.iconCls=  'database_edit';
editDbObj.text= _message['edit'];
editDbObj.leaf= true;
editDbObj.id= 'editDb';

var refreshDdObj = new Object();
refreshDdObj.iconCls=  'database_refresh';
refreshDdObj.text=  _message['refresh'];
refreshDdObj.leaf= true;
refreshDdObj.id= 'refershDb';

var helpDbObj = new Object();
helpDbObj.iconCls=  'help';
helpDbObj.text=  _message['help'];
helpDbObj.leaf= true;
helpDbObj.id= 'dbHelp';

// Custom Schema Menu
var listSchemaObj = new Object();
listSchemaObj.iconCls =  'schema_list';
listSchemaObj.text =  _message['schemasList'];
listSchemaObj.leaf = true;
listSchemaObj.id = 'schemasList';

var addSchemaObj = new Object();
addSchemaObj.iconCls =  'schema_add';
addSchemaObj.text =  _message['add'];
addSchemaObj.leaf = true;
addSchemaObj.id = 'addSchema';

var deleteSchemaObj = new Object();
deleteSchemaObj.iconCls =  'schema_delete';
deleteSchemaObj.text =  _message['delete'];
deleteSchemaObj.leaf = true;
deleteSchemaObj.id =  'deleteSchema';

var editSchemaObj = new Object();
editSchemaObj.iconCls =  'schema_edit';
editSchemaObj.text =  _message['edit'];
editSchemaObj.leaf = true;
editSchemaObj.id =  'editSchema';

var duplicateSchemaObj = new Object();
duplicateSchemaObj.iconCls =  'schema_duplicate';
duplicateSchemaObj.text =  _message['duplicate'];
duplicateSchemaObj.leaf = true;
duplicateSchemaObj.id =  'duplicateSchema';

var editFieldsSchemaObj = new Object();
editFieldsSchemaObj.iconCls =  'schema_edit_fields';
editFieldsSchemaObj.text =  _message['editFields'];
editFieldsSchemaObj.leaf = true;
editFieldsSchemaObj.id =  'editSchemaFields'

var validTraceSchemaObj = new Object();
validTraceSchemaObj.iconCls =  'schema_valid_trace';
validTraceSchemaObj.text =  _message['validateDatastream'];
validTraceSchemaObj.leaf =  true;
validTraceSchemaObj.id =  'validateDatastream';

var extraCheckSchemaObj = new Object();
extraCheckSchemaObj.iconCls =  'schema_extra_check';
extraCheckSchemaObj.text =  _label['extraCheck'];
extraCheckSchemaObj.leaf =  true;
extraCheckSchemaObj.id =  'schemaExtraCheck';

var triggerSchemaObj = new Object();
triggerSchemaObj.iconCls =  'schema_trigger';
triggerSchemaObj.text =  _label['trigger'];
triggerSchemaObj.leaf =  true;
triggerSchemaObj.id =  'schemaTrigger';

var dataStreamReceivedSchemaObj = new Object();
dataStreamReceivedSchemaObj.iconCls =  'schema_datastream_received';
dataStreamReceivedSchemaObj.text =  _message['datastreamReceived'];
dataStreamReceivedSchemaObj.leaf =  true;
dataStreamReceivedSchemaObj.id =  'datastreamReceived';

var docSchemaObj = new Object();
docSchemaObj.iconCls =  'schema_document';
docSchemaObj.text =  _message['schemaDocuments'];
docSchemaObj.leaf =  true;
docSchemaObj.id =  'schemaDocuments';

var importFromXSDSchemaObj = new Object();
importFromXSDSchemaObj.iconCls =  'schema_import_schema';
importFromXSDSchemaObj.text =  _message['importSchema'];
importFromXSDSchemaObj.leaf =  true;
importFromXSDSchemaObj.id =  'schemaImportFromXSD';

var shareWithWorldSchemaObj = new Object();
shareWithWorldSchemaObj.iconCls =  'schema_share';
shareWithWorldSchemaObj.text =  _message['shareSchema'];
shareWithWorldSchemaObj.leaf =  true;
shareWithWorldSchemaObj.id =  'schemaShareWithWorld';

var forecastFormObj = new Object();
forecastFormObj.iconCls =  'forecasting';
forecastFormObj.text =  _message['forecasting'];
forecastFormObj.leaf =  true;
forecastFormObj.id =  'forecastForm';

var helpSchemaObj = new Object();
helpSchemaObj.iconCls =  'help';
helpSchemaObj.text =  _message['help'];
helpSchemaObj.leaf =  true;
helpSchemaObj.id =  'schemaHelp';

//Standard Schema Menu
var listStandardSchemaObj = new Object();
listStandardSchemaObj.iconCls =  'schema_list';
listStandardSchemaObj.text =  _message['schemasList'];
listStandardSchemaObj.leaf = true;
listStandardSchemaObj.id = 'standardSchemasList';

var addStandardSchemaObj = new Object();
addStandardSchemaObj.iconCls =  'schema_add';
addStandardSchemaObj.text =  _message['add'];
addStandardSchemaObj.leaf = true;
addStandardSchemaObj.id = 'addStandardSchema';

var deleteStandardSchemaObj = new Object();
deleteStandardSchemaObj.iconCls =  'schema_delete';
deleteStandardSchemaObj.text =  _message['delete'];
deleteStandardSchemaObj.leaf = true;
deleteStandardSchemaObj.id =  'deleteStandardSchema';

var editStandardSchemaObj = new Object();
editStandardSchemaObj.iconCls =  'schema_edit';
editStandardSchemaObj.text =  _message['edit'];
editStandardSchemaObj.leaf = true;
editStandardSchemaObj.id =  'editStandardSchema';

var duplicateStandardSchemaObj = new Object();
duplicateStandardSchemaObj.iconCls =  'schema_duplicate';
duplicateStandardSchemaObj.text =  _message['duplicate'];
duplicateStandardSchemaObj.leaf = true;
duplicateStandardSchemaObj.id =  'duplicateStandardSchema';

var validTraceStandardSchemaObj = new Object();
validTraceStandardSchemaObj.iconCls =  'schema_valid_trace';
validTraceStandardSchemaObj.text =  _message['validateDatastream'];
validTraceStandardSchemaObj.leaf =  true;
validTraceStandardSchemaObj.id =  'validateStandardDatastream';

var dataStreamReceivedStandardSchemaObj = new Object();
dataStreamReceivedStandardSchemaObj.iconCls =  'schema_datastream_received';
dataStreamReceivedStandardSchemaObj.text =  _message['datastreamReceived'];
dataStreamReceivedStandardSchemaObj.leaf =  true;
dataStreamReceivedStandardSchemaObj.id =  'standardDatastreamReceived';

var docStandardSchemaObj = new Object();
docStandardSchemaObj.iconCls =  'schema_document';
docStandardSchemaObj.text =  _message['schemaDocuments'];
docStandardSchemaObj.leaf =  true;
docStandardSchemaObj.id =  'standardSchemaDocuments';

var helpStandardSchemaObj = new Object();
helpStandardSchemaObj.iconCls =  'help';
helpStandardSchemaObj.text =  _message['help'];
helpStandardSchemaObj.leaf =  true;
helpStandardSchemaObj.id =  'standardSchemaHelp';

var supportedStandardSchemaObj = new Object();
supportedStandardSchemaObj.iconCls =  'supported_standard';
supportedStandardSchemaObj.text =  _message['supportedStandard'];
supportedStandardSchemaObj.leaf =  true;
supportedStandardSchemaObj.id =  'standardSupported';

//Generation Stream Menu
var listGenerationStreamObj = new Object();
listGenerationStreamObj.iconCls =  'schema_list';
listGenerationStreamObj.text =  _message['schemasList'];
listGenerationStreamObj.leaf = true;
listGenerationStreamObj.id = 'generationStreamList';

var addGenerationStreamObj = new Object();
addGenerationStreamObj.iconCls =  'schema_add';
addGenerationStreamObj.text =  _message['add'];
addGenerationStreamObj.leaf = true;
addGenerationStreamObj.id = 'addGenerationStream';

var deleteGenerationStreamObj = new Object();
deleteGenerationStreamObj.iconCls =  'schema_delete';
deleteGenerationStreamObj.text =  _message['delete'];
deleteGenerationStreamObj.leaf = true;
deleteGenerationStreamObj.id =  'deleteGenerationStream';

var editGenerationStreamObj = new Object();
editGenerationStreamObj.iconCls =  'schema_edit';
editGenerationStreamObj.text =  _message['edit'];
editGenerationStreamObj.leaf = true;
editGenerationStreamObj.id =  'editGenerationStream';

var helpGenerationStreamObj = new Object();
helpGenerationStreamObj.iconCls =  'help';
helpGenerationStreamObj.text =  _message['help'];
helpGenerationStreamObj.leaf =  true;
helpGenerationStreamObj.id =  'generationStreamHelp';

var editFieldsGenerationStreamObj = new Object();
editFieldsGenerationStreamObj.iconCls =  'schema_edit_fields';
editFieldsGenerationStreamObj.text =  _message['editFields'];
editFieldsGenerationStreamObj.leaf = true;
editFieldsGenerationStreamObj.id =  'editGenerationStreamFields';

var duplicateGenerationStreamObj = new Object();
duplicateGenerationStreamObj.iconCls =  'schema_duplicate';
duplicateGenerationStreamObj.text =  _message['duplicate'];
duplicateGenerationStreamObj.leaf = true;
duplicateGenerationStreamObj.id =  'duplicateGenerationStream';

var sendGenerationStreamObj = new Object();
sendGenerationStreamObj.iconCls =  'stream_generation';
sendGenerationStreamObj.text =  _message['sendStream'];
sendGenerationStreamObj.leaf = true;
sendGenerationStreamObj.id =  'sendGenerationStream';

var docGenerationStreamObj = new Object();
docGenerationStreamObj.iconCls =  'schema_document';
docGenerationStreamObj.text =  _message['schemaDocuments'];
docGenerationStreamObj.leaf =  true;
docGenerationStreamObj.id =  'generationStreamDocuments';

var importFromXSDGenerationStreamaObj = new Object();
importFromXSDGenerationStreamaObj.iconCls =  'schema_import_schema';
importFromXSDGenerationStreamaObj.text =  _message['importSchema'];
importFromXSDGenerationStreamaObj.leaf =  true;
importFromXSDGenerationStreamaObj.id =  'generationStreamImportFromXSD';

// Stream Loading Menu
var listStreamLoadingObj = new Object();
listStreamLoadingObj.iconCls = 'schema_list';
listStreamLoadingObj.text = _message['schemasList'];
listStreamLoadingObj.leaf = true;
listStreamLoadingObj.id = 'listStreamLoading';

var addStreamLoadingObj = new Object();
addStreamLoadingObj.iconCls = 'schema_add';
addStreamLoadingObj.text = _message['add'];
addStreamLoadingObj.leaf = true;
addStreamLoadingObj.id = 'addStreamLoading';

var deleteStreamLoadingObj = new Object();
deleteStreamLoadingObj.iconCls = 'schema_delete';
deleteStreamLoadingObj.text = _message['delete'];
deleteStreamLoadingObj.leaf = true;
deleteStreamLoadingObj.id = 'deleteStreamLoading';

var editStreamLoadingObj = new Object();
editStreamLoadingObj.iconCls = 'schema_edit';
editStreamLoadingObj.text = _message['edit'];
editStreamLoadingObj.leaf = true;
editStreamLoadingObj.id = 'editStreamLoading';

var editFieldsStreamLoadingObj = new Object();
editFieldsStreamLoadingObj.iconCls = 'schema_edit_fields';
editFieldsStreamLoadingObj.text = _message['editFields'];
editFieldsStreamLoadingObj.leaf = true;
editFieldsStreamLoadingObj.id = 'editFieldsStreamLoading';

var helpStreamLoadingObj = new Object();
helpStreamLoadingObj.iconCls = 'help';
helpStreamLoadingObj.text = _message['help'];
helpStreamLoadingObj.leaf = true;
helpStreamLoadingObj.id = 'helpStreamLoading';

// Schema Menu (ee-module)
var errorsSchemaObj = new Object();
errorsSchemaObj.iconCls = 'alertIcon';
errorsSchemaObj.text = _message['customErrors'];
errorsSchemaObj.leaf = true;
errorsSchemaObj.id = 'customErrors';

var macroSchemaObj = new Object();
macroSchemaObj.iconCls = 'schema_macro';
macroSchemaObj.text = _message['macro'];
macroSchemaObj.leaf = true;
macroSchemaObj.id = 'macro';

var exportToImageXSDSchemaObj = new Object();
exportToImageXSDSchemaObj.iconCls = 'schema_xsd_diagram';
exportToImageXSDSchemaObj.text = _message['exportPng'];
exportToImageXSDSchemaObj.leaf =  true;
exportToImageXSDSchemaObj.id =  'schemaXSDExportToPNG';

var loadFieldsSchemaObj = new Object();
loadFieldsSchemaObj.iconCls =  'schema_load_fields';
loadFieldsSchemaObj.text =  _message['loadFields'];
loadFieldsSchemaObj.leaf =  true;
loadFieldsSchemaObj.id =  'loadSchemaFields';

var exportToXSDSchemaObj = new Object();
exportToXSDSchemaObj.iconCls =  'schema_export_schema';
exportToXSDSchemaObj.text =  _message['exportSchema'];
exportToXSDSchemaObj.leaf =  true;
exportToXSDSchemaObj.id =  'schemaExportToXSD';

// user Menu
var addUserObj = new Object();
addUserObj.iconCls = 'user_application_add';
addUserObj.text = _message['add'];
addUserObj.leaf = true;
addUserObj.id = 'addUser';

var deleteUserObj = new Object();
deleteUserObj.iconCls = 'user_application_delete';
deleteUserObj.text = _message['delete'];
deleteUserObj.leaf = true;
deleteUserObj.id = 'delUser';

var editUserObj = new Object();
editUserObj.iconCls = 'user_application_edit';
editUserObj.text = _message['edit'];
editUserObj.leaf = true;
editUserObj.id = 'editUser';

var logoutUserObj = new Object();
logoutUserObj.iconCls = 'user_logout';
logoutUserObj.text = _message['logout'];
logoutUserObj.leaf = true;
logoutUserObj.id = 'logoutUser';


var helpUserObj = new Object();
helpUserObj.iconCls = 'help';
helpUserObj.text = _message['help'];
helpUserObj.leaf = true;
helpUserObj.id = 'userHelp';

// connection menu
var addConnObj = new Object();
addConnObj.iconCls = 'planner_add';
addConnObj.text = _message['add'];
addConnObj.leaf = true;
addConnObj.id = 'addConnection';

var deleteConnObj = new Object();
deleteConnObj.iconCls = 'planner_delete';
deleteConnObj.text = _message['delete'];
deleteConnObj.leaf = true;
deleteConnObj.id = 'delConnection';

var editConnObj = new Object();
editConnObj.iconCls = 'planner_edit';
editConnObj.text = _message['edit'];
editConnObj.leaf = true;
editConnObj.id = 'editConnection';


var refreshConnObj = new Object();
refreshConnObj.iconCls = 'database_refresh';
refreshConnObj.text = _message['refresh'];
refreshConnObj.leaf = true;
refreshConnObj.id = 'refreshConnections';

var helpConnObj = new Object();
helpConnObj.iconCls = 'help';
helpConnObj.text = _message['help'];
helpConnObj.leaf = true;
helpConnObj.id = 'connectionsHelp';

// Jobs Menu

var addJobsObj = new Object();
addJobsObj.iconCls = 'planner_add';
addJobsObj.text = _message['add'];
addJobsObj.leaf = true;
addJobsObj.id = 'addJob';

var deleteJobsObj = new Object();
deleteJobsObj.iconCls = 'planner_delete';
deleteJobsObj.text = _message['delete'];
deleteJobsObj.leaf = true;
deleteJobsObj.id = 'delJob';

var editJobsObj = new Object();
editJobsObj.iconCls = 'planner_edit';
editJobsObj.text = _message['edit'];
editJobsObj.leaf = true;
editJobsObj.id = 'editJob';

var helpJobsObj = new Object();
helpJobsObj.iconCls = 'help';
helpJobsObj.text = _message['help'];
helpJobsObj.leaf = true;
helpJobsObj.id = 'jobHelp';

//Schedulers Menu
var addSchedulerObj = new Object();
addSchedulerObj.iconCls = 'planner_add';
addSchedulerObj.text = _message['add'];
addSchedulerObj.leaf = true;
addSchedulerObj.id = 'addTask';

var deleteSchedulerObj = new Object();
deleteSchedulerObj.iconCls = 'planner_delete';
deleteSchedulerObj.text = _message['delete'];
deleteSchedulerObj.leaf = true;
deleteSchedulerObj.id = 'delTask';

var editSchedulerObj = new Object();
editSchedulerObj.iconCls = 'planner_edit';
editSchedulerObj.text = _message['edit'];
editSchedulerObj.leaf = true;
editSchedulerObj.id = 'editTask';

var helpSchedulerObj = new Object();
helpSchedulerObj.iconCls = 'help';
helpSchedulerObj.text = _message['help'];
helpSchedulerObj.leaf = true;
helpSchedulerObj.id = 'taskHelp';

// Reports Menu
var realtimeReportObj = new Object();
realtimeReportObj.iconCls = 'report_realTime';
realtimeReportObj.text = _label['realTime'];
realtimeReportObj.leaf = true;
realtimeReportObj.id = 'realTimeReport';

var detailedReportObj = new Object();
detailedReportObj.iconCls = 'report_state';
detailedReportObj.text = _label['detailedState'];
detailedReportObj.leaf = true;
detailedReportObj.id = 'detailedStateReport';

var monthlyReportObj = new Object();
monthlyReportObj.iconCls = 'report_state_monthly';
monthlyReportObj.text = _label['monthlyState'];
monthlyReportObj.leaf = true;
monthlyReportObj.id = 'monthlyStateReport';

var annualStateReportObj = new Object();
annualStateReportObj.iconCls = 'report_state_annual';
annualStateReportObj.text = _label['annualState'];
annualStateReportObj.leaf = true;
annualStateReportObj.id = 'annualStateReport';

var helpReportObj = new Object();
helpReportObj.iconCls = 'help';
helpReportObj.text = _message['help'];
helpReportObj.leaf = true;
helpReportObj.id = 'reportHelp';

// Admin Menu
var serverAdminObj = new Object();
serverAdminObj.iconCls = 'server';
serverAdminObj.text = _label['server'];
serverAdminObj.leaf = true;
serverAdminObj.id = 'server';

var infoAdminObj = new Object();
infoAdminObj.iconCls = 'info';
infoAdminObj.text = _label['info_service'];
infoAdminObj.leaf = true;
infoAdminObj.id = 'adminInfo';

var helpAdminObj = new Object();
helpAdminObj.iconCls = 'help';
helpAdminObj.text = _message['help'];
helpAdminObj.leaf = true;
helpAdminObj.id = 'adminHelp';

var emailAdminObj = new Object();
emailAdminObj.iconCls = 'email';
emailAdminObj.text = _label['email_config'];
emailAdminObj.leaf = true;
emailAdminObj.id = 'adminEmail';

var ftpAdminObj = new Object();
ftpAdminObj.iconCls = 'ftpConfig';
ftpAdminObj.text = _label['ftpConfig'];
ftpAdminObj.leaf = true;
ftpAdminObj.id = 'adminFTP';

var logAdminObj = new Object();
logAdminObj.iconCls = 'log';
logAdminObj.text = _label['log'];
logAdminObj.leaf = true;
logAdminObj.id = 'adminLog';

var applicationTreeJsIds = [addAppObj,deleteAppObj,editAppObj,helpAppObj];
var dbTreeJsIds = [addDbObj,deleteDbObj,editDbObj,refreshDdObj,helpDbObj];
var generationStreamJsIds = [listGenerationStreamObj,addGenerationStreamObj,deleteGenerationStreamObj,editGenerationStreamObj,duplicateGenerationStreamObj,
    editFieldsGenerationStreamObj,sendGenerationStreamObj, docGenerationStreamObj,importFromXSDGenerationStreamaObj,helpGenerationStreamObj];
var streamLoadingJsIds = [ listStreamLoadingObj, addStreamLoadingObj,
		deleteStreamLoadingObj, editStreamLoadingObj,
		editFieldsStreamLoadingObj, helpStreamLoadingObj ];
var schemasJsIds = [listSchemaObj,addSchemaObj,deleteSchemaObj,editSchemaObj,duplicateSchemaObj,editFieldsSchemaObj,loadFieldsSchemaObj, errorsSchemaObj,
	validTraceSchemaObj,dataStreamReceivedSchemaObj,extraCheckSchemaObj,macroSchemaObj,triggerSchemaObj,shareWithWorldSchemaObj,exportToXSDSchemaObj,exportToImageXSDSchemaObj,importFromXSDSchemaObj,docSchemaObj,forecastFormObj,helpSchemaObj];
var standardSchemasJsIds = [listStandardSchemaObj,addStandardSchemaObj,deleteStandardSchemaObj,editStandardSchemaObj,duplicateStandardSchemaObj,validTraceStandardSchemaObj,dataStreamReceivedStandardSchemaObj,docStandardSchemaObj,supportedStandardSchemaObj,helpStandardSchemaObj];
var userTreeJsIds = [addUserObj,deleteUserObj,editUserObj,logoutUserObj,helpUserObj];
var connectionsJsIds = [addConnObj,deleteConnObj,editConnObj,refreshConnObj,helpConnObj];
var jobsJsIds = [addJobsObj,deleteJobsObj,editJobsObj,helpJobsObj];
var schedulerJsIds = [addSchedulerObj,deleteSchedulerObj,editSchedulerObj,helpSchedulerObj];
var reportsJsIds = [realtimeReportObj,detailedReportObj,monthlyReportObj,annualStateReportObj,helpReportObj];
var adminJsIds = [logAdminObj,emailAdminObj,serverAdminObj,infoAdminObj,ftpAdminObj,helpAdminObj];
var menuChlds = new Array();
var contentChlds = new Array();
var applicationChlds = new Array();
var databaseChlds = new Array();
var generationStreamChlds = new Array();
var streamLoadingChlds = new Array();
var schemaChlds = new Array();
var standardSchemaChlds = new Array();
var userChlds = new Array();
var connectionsChlds = new Array();
var jobsChlds = new Array();
var schedulerChlds = new Array();
var reportChlds = new Array();
var adminChlds = new Array();

var appChCnt = 0;
for(i=0;i<applicationTreeJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(applicationTreeJsIds[i].id)!=-1){
		applicationChlds[appChCnt] = {
			iconCls: applicationTreeJsIds[i].iconCls,
			text: applicationTreeJsIds[i].text,
			leaf:applicationTreeJsIds[i].leaf,
			id:applicationTreeJsIds[i].id			
		};
		appChCnt++;
	}	
}
var dbChCnt = 0;
for(i=0;i<dbTreeJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(dbTreeJsIds[i].id)!=-1){
		databaseChlds[dbChCnt] = {
			iconCls: dbTreeJsIds[i].iconCls,
			text: dbTreeJsIds[i].text,
			leaf:dbTreeJsIds[i].leaf,
			id:dbTreeJsIds[i].id			
		};
		dbChCnt++;
	}	
}
var sChCnt =0;
for(i=0;i<schemasJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(schemasJsIds[i].id)!=-1){
		schemaChlds[sChCnt] = {
			iconCls: schemasJsIds[i].iconCls,
			text: schemasJsIds[i].text,
			leaf:schemasJsIds[i].leaf,
			id:schemasJsIds[i].id
		};
		sChCnt++;
	}	
}

var sStdChCnt =0;
for(i=0;i<standardSchemasJsIds.length;i++){
	
	if(roleActivities!=null && roleActivities.indexOf(standardSchemasJsIds[i].id)!=-1){
		standardSchemaChlds[sStdChCnt] = {
			iconCls: standardSchemasJsIds[i].iconCls,
			text: standardSchemasJsIds[i].text,
			leaf:standardSchemasJsIds[i].leaf,
			id:standardSchemasJsIds[i].id
		};
		sStdChCnt++;
	}	
}

var gsChCnt =0;
for(i=0;i<generationStreamJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(generationStreamJsIds[i].id)!=-1){
		generationStreamChlds[gsChCnt] = {
				iconCls: generationStreamJsIds[i].iconCls,
				text: generationStreamJsIds[i].text,
				leaf:generationStreamJsIds[i].leaf,
				id:generationStreamJsIds[i].id
			};
		gsChCnt++;
	}	
}

var slChCntCounter = 0;
for ( var i = 0; i < streamLoadingJsIds.length; i++) {
	if (roleActivities != null
			&& roleActivities.indexOf(streamLoadingJsIds[i].id) != -1) {
		streamLoadingChlds[slChCntCounter] = {
			iconCls : streamLoadingJsIds[i].iconCls,
			text : streamLoadingJsIds[i].text,
			leaf : streamLoadingJsIds[i].leaf,
			id : streamLoadingJsIds[i].id
		};
		slChCntCounter++;
	}
}

var uChCnt = 0;
for(i=0;i<userTreeJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(userTreeJsIds[i].id)!=-1){
		userChlds[uChCnt] = {
			iconCls: userTreeJsIds[i].iconCls,
			text: userTreeJsIds[i].text,
			leaf:userTreeJsIds[i].leaf,
			id:userTreeJsIds[i].id			
		};
		uChCnt++;
	}	
}
var conChCnt =0;
for(i=0;i<connectionsJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(connectionsJsIds[i].id)!=-1){
		connectionsChlds[conChCnt] = {
			iconCls: connectionsJsIds[i].iconCls,
			text: connectionsJsIds[i].text,
			leaf:connectionsJsIds[i].leaf,
			id:connectionsJsIds[i].id			
		};
		conChCnt++;
	}	
}
var jChCnt = 0;
for(i=0;i<jobsJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(jobsJsIds[i].id)!=-1){
		jobsChlds[jChCnt] = {
			iconCls: jobsJsIds[i].iconCls,
			text: jobsJsIds[i].text,
			leaf:jobsJsIds[i].leaf,
			id:jobsJsIds[i].id			
		};
		jChCnt++;
	}	
}
var sChCnt = 0;
for(i=0;i<schedulerJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(schedulerJsIds[i].id)!=-1){
		schedulerChlds[sChCnt] = {
			iconCls: schedulerJsIds[i].iconCls,
			text: schedulerJsIds[i].text,
			leaf:schedulerJsIds[i].leaf,
			id:schedulerJsIds[i].id			
		};
		sChCnt++;
	}	
}
var rChCnt = 0;
for(i=0;i<reportsJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(reportsJsIds[i].id)!=-1){
		reportChlds[rChCnt] = {
			iconCls: reportsJsIds[i].iconCls,
			text: reportsJsIds[i].text,
			leaf:reportsJsIds[i].leaf,
			id:reportsJsIds[i].id			
		};
		rChCnt++;
	}	
}
var aChCnt = 0;
for(i=0;i<adminJsIds.length;i++){
	if(roleActivities!=null && roleActivities.indexOf(adminJsIds[i].id)!=-1){
		adminChlds[aChCnt] = {
			iconCls: adminJsIds[i].iconCls,
			text: adminJsIds[i].text,
			leaf:adminJsIds[i].leaf,
			id:adminJsIds[i].id			
		};
		aChCnt++;
	}	
}

function logout(){
	Ext.Ajax.request({
		success: function (result) {
			window.location="index.jsp";  
		},
		url: './logout.json'
	});
}
var header = ({	
	collapsible: true,
	html: [ "<table width=\"100%\"><tr><td align=\"left\" valign=\"top\">",
            "&nbsp;<a href='http://altimario.github.io/DataCruncher' target='_blank'><img src=\"./images/logo.png\" alt='DataCruncher'></a></td><td align=\"right\" valign=\"top\">",
            getHtmlMsgForCredits(),
            "&nbsp;&nbsp;</td></tr>",
            "</table>" 
          ],
	region: 'south',
    bodyStyle:{"background-color":"#3892D3"},
    title: _label['info']
});

var applications = ({
	frame: false,
	id: 'applications',
	iconCls : 'applications',
	listeners:{
		expand: function() {
			storeApplications.load();
			Ext.getCmp('content').layout.setActiveItem('applicationsGrid');
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='addApp'){
				// Add tree node event
				addApplication();
			} else if(rec.get('id')=='delApp'){
				// Delete tree node event
				deleteApplication();
			} else if(rec.get('id')=='editApp'){
				// Edit tree node event
				editApplication();
			} else{
				helpApplication();
			}			
		}
    },
	root: {
		children: applicationChlds
	},
	rootVisible: false,
	title: _label['applications'],
	useArrows: true,
	xtype: 'treepanel'
});

var databases = ({
	frame: false,
	id: 'databases',
	iconCls : 'database',
	listeners:{
		expand: function() {
			storeDatabases.load();
			Ext.getCmp('content').layout.setActiveItem('databasesGrid');
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='addDb'){
				// Add tree node event
				addDatabase();
			} else if(rec.get('id')=='delDb'){
				// Delete tree node event
				deleteDatabase();
			} else if(rec.get('id')=='editDb'){
				// Edit tree node event
				editDatabase();
			} else if(rec.get('id')=='refershDb'){
				// Refresh tree node event
				storeDatabases.load();
				Ext.getCmp('content').layout.setActiveItem('databasesGrid');
			}else{
				helpDatabase();
			}			
		}
    },
	root: {
		children: databaseChlds
	},
	rootVisible: false,
	title: _label['database'],
	useArrows: true,
	xtype: 'treepanel'
});

var schemas = ({
	frame: false,
	id: 'schemas',
	iconCls : 'custom_schema_validation',
	listeners:{
		expand: function() {
			storeSchemas.load();
			storeApplications.load();
			Ext.getCmp('content').layout.setActiveItem('schemasGrid');
		},
		itemclick : function(view,rec,item,index,eventObj) {
			
			if(rec.get('id')=='schemasList'){
				schemasList();
			} else if(rec.get('id')=='addSchema'){
				addSchema();
			} else if(rec.get('id')=='deleteSchema'){
				deleteSchema();
			}  else if(rec.get('id')=='editSchema'){
				editSchema();
			}  else if(rec.get('id')=='duplicateSchema'){
				duplicateSchema();
			}  else if(rec.get('id')=='editSchemaFields'){
				editSchemaFields();
			}  else if(rec.get('id')=='validateDatastream'){
				validateDatastream();
			} else if(rec.get('id')=='datastreamReceived'){
				datastreamReceived();
			} else if(rec.get('id')=='schemaDocuments'){	
				schemaDocuments();
            } else if(rec.get('id')=='schemaImportFromXSD'){
                schemaImportFromJv();
			} else if(rec.get('id')=='schemaHelp'){
				helpSchema();
			} else if(rec.get('id')=='schemaExtraCheck'){
				extraCheckSchema();
			} else if(rec.get('id')=='schemaShareWithWorld') {
				schemaShareWithWorld();
			} else if(rec.get('id')=='schemaTrigger'){
				schemaTrigger();
            } else if(rec.get('id')=='forecastForm') {
                showForecastForm();
            } else {
				try {
					if (rec.get('id') == 'macro') {
						macro();
					} else if (rec.get('id') == 'customErrors') {
						funcSchemaCustomErrors();
					} else if (rec.get('id') == 'schemaXSDExportToPNG') {
						schemaXSDExportToPNG();
                    }  else if(rec.get('id')=='loadSchemaFields'){
                        loadSchemaFields();
                    } else if(rec.get('id')=='schemaExportToXSD'){
                        schemaExportToXSD();
                    }
				} catch (e) {
					callAlert((e instanceof ReferenceError) ? _message['optionNotReady']
						: 'index.js :: unknown error');	
				}
			}		
		}
    },
	root: {
		children: schemaChlds
	},
	rootVisible: false,
	title: _label['customValidation'],
	useArrows: true,
	xtype: 'treepanel'
});

var generationStream = ({
	frame: false,
	id: 'generationStream',
	iconCls : 'stream_generation',
	listeners:{
		expand: function() {
			storeApplications.load();
			storeGenerationStream.load();
			Ext.getCmp('content').layout.setActiveItem('generationStreamGrid');
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='generationStreamList'){
				generationStreamList();
			} else if(rec.get('id')=='addGenerationStream'){
				addGenerationStream();
			} else if(rec.get('id')=='deleteGenerationStream'){
				deleteGenerationStream();
			}  else if(rec.get('id')=='editGenerationStream'){
				editGenerationStream();
			}  else if(rec.get('id')=='duplicateGenerationStream'){
				duplicateGenerationStream();
			}  else if(rec.get('id')=='editGenerationStreamFields'){
				editGenerationStreamFields();
			}  else if(rec.get('id')=='generationStreamHelp'){
				helpGenerationStream();
			} else if(rec.get('id')=='generationStreamDocuments'){
				generationStreamDocuments();
			} else if(rec.get('id')=='generationStreamImportFromXSD'){
				generationStreamImportFromXSD();
			} else if(rec.get('id')=='sendGenerationStream'){
				sendGenerationStreamFunction();
			}
		}
    },
	root: {
		children: generationStreamChlds
	},
	rootVisible: false,
	title: _label['generationStream'],
	useArrows: true,
	xtype: 'treepanel'
});

var streamLoadingPane = ({
	frame : false,
	id: 'streamLoadingSchemas',
	iconCls : 'stream_loading',
	listeners : {
		expand : function() {
			streamLoadingStore.load();
			Ext.getCmp('content').layout.setActiveItem(loadingStreamGrid);
		},
		itemclick : function(view, rec, item, index, eventObj) {
			if (rec.get('id') == 'listStreamLoading') {
				listLoadingStreams();
			} else if (rec.get('id') == 'addStreamLoading') {
				addLoadingStreamFunc();
			} else if (rec.get('id') == 'deleteStreamLoading') {
				deleteLoadingStream();
			} else if (rec.get('id') == 'editStreamLoading') {
				editLoadingStreamFunc();
			} else if (rec.get('id') == 'editFieldsStreamLoading') {
				editLoadingStreamFields();
			} else if (rec.get('id') == 'helpStreamLoading') {
				popupSchemaHelp();
			}
		}
	},
	root : {
		children : streamLoadingChlds
	},
	rootVisible : false,
	title : _label['streamLoadingLabel'],
	useArrows : true,
	xtype : 'treepanel'
});

var standardSchemas = ({
	frame: false,
	id: 'standardSchemas',
	iconCls : 'standard_schema_validation',
	listeners:{
		expand: function() {
			storeApplications.load();
			storeStandardSchemas.load();
			Ext.getCmp('content').layout.setActiveItem(Ext.getCmp('standardSchemasGrid'));
		},
		itemclick : function(view,rec,item,index,eventObj) {
			
			if(rec.get('id')=='standardSchemasList'){
				standardSchemasList();
			} else if(rec.get('id')=='addStandardSchema'){
				addStandardSchema();
			} else if(rec.get('id')=='deleteStandardSchema'){
				deleteStandardSchema();
			}  else if(rec.get('id')=='editStandardSchema'){
				editStandardSchema();
			}  else if(rec.get('id')=='duplicateStandardSchema'){
				duplicateStandardSchema();
			} else if(rec.get('id')=='validateStandardDatastream'){
				validateStandardDatastream();
			} else if(rec.get('id')=='standardDatastreamReceived'){
				standardDatastreamReceived();
			} else if(rec.get('id')=='standardSchemaDocuments'){	
				standardSchemaDocuments();
			} else if(rec.get('id')=='standardSchemaHelp'){
				helpStandardSchema();
			} else if(rec.get('id')=='standardSupported'){
				popupStandardSupported();
			}
		}
    },
	root: {
		children: standardSchemaChlds
	},
	rootVisible: false,
	title: _label['standardValidations'],
	useArrows: true,
	xtype: 'treepanel'
});

var users = ({
	frame: false,
	id: 'users',
	iconCls : 'users',
	listeners:{
		expand: function() {
			storeUsers.load();
			storeApplications.load();
			Ext.getCmp('content').layout.setActiveItem('usersGrid');
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='addUser'){
				// Add tree node event
				addUser();
			} else if(rec.get('id')=='delUser'){
				// Delete tree node event
				deleteUser();
			}  else if(rec.get('id')=='editUser'){
				// Edit tree node event
				editUser();
			} else if(rec.get('id')=='logoutUser'){
				// signout user
				logout();
			}else{
				helpUser();
			}			
		}
    },
	root: {
		children: userChlds
	},
	rootVisible: false,
	title: _label['users'],
	useArrows: true,
	xtype: 'treepanel'
});
var lblCredits = ( {
	frame : false,
	id : 'lblcontactus',	
	xtype : 'label',	
    width: 200,
    listeners : {
		afterrender : function() {
			this.update(getHtmlMsgForCredits(true));
		}
    }
});

var connections = ( {
	frame : false,
	id : 'connectionsTreePanel',
	listeners : {
		expand : function() {
			Ext.getCmp('connectionsGridId').store.load();
			Ext.getCmp('content').layout.setActiveItem('connectionsGridId');
		},
		itemclick : function(view, rec, item, index, eventObj) {
			if (rec.get('id') == 'addConnection') {
				addConnection();
			} else if (rec.get('id') == 'delConnection') {
				deleteConnection();
			} else if (rec.get('id') == 'editConnection') {
				editConnection();
			} else if (rec.get('id') == 'refreshConnections') {
				Ext.getCmp('connectionsGridId').store.load();
			} else {
				connectionsHelp();
			}
		}
	},
	root : {
		children : connectionsChlds
	},
	rootVisible : false,
	title : _label['connections'],
	useArrows : true,
	xtype : 'treepanel'
});

var jobs = ( {
	frame : false,
	id : 'jobsTreePanel',
	listeners : {
		expand : function() {
			jobsDataStores.appsStore.load();
			jobsDataStores.schemasStore.load();
			jobsDataStores.generatedSchemasStore.load();
			jobsDataStores.allSchemasStore.load();			
			jobsDataStores.connectionsStore.load();
			jobsDataStores.schedulerStore.load();			
			jobsDataStores.allSchemaTriggersStore.load();
			Ext.getCmp('jobsGridId').store.load();			
			Ext.getCmp('content').layout.setActiveItem('jobsGridId');
		},
		itemclick : function(view, rec, item, index, eventObj) {
			if (rec.get('id') == 'addJob') {
				addJob();
			} else if (rec.get('id') == 'delJob') {
				deleteJob();
			} else if (rec.get('id') == 'editJob') {
				editJob();
			} else {
				helpJobs();
			}
		}
	},
	root : {
		children : jobsChlds
	},
	rootVisible : false,
	title : _label['jobs'],
	useArrows : true,
	xtype : 'treepanel'
});


var scheduler = ( {
	frame : false,
	id : 'schedulerTreePanel',
	listeners : {
		expand : function() {
			Ext.getCmp('tasksGridId').store.load();
			Ext.getCmp('content').layout.setActiveItem('tasksGridId');
		},
		itemclick : function(view, rec, item, index, eventObj) {
			if (rec.get('id') == 'addTask') {
				addTask();
			} else if (rec.get('id') == 'delTask') {
				deleteTask();
			} else if (rec.get('id') == 'editTask') {
				editTask();
			} else {
				helpScheduler();
			}
		}
	},
	root : {
		children : schedulerChlds
	},
	rootVisible : false,
	title : _label['planner'],
	useArrows : true,
	xtype : 'treepanel'
});

var schedulers = ({
	collapsible: true,
	frame: false,
	id: 'schedulersMainPanel',
	iconCls : 'schedulers',
	activeItem : 0,	
	items : [scheduler,  connections, jobs],
    layout: 'accordion',
    padding: '0 0 0 5',
    style : 'background: #fff',
	region: 'center',
	title: _label['schedulers'],
	listeners : {
		expand : function() {
			Ext.getCmp('tasksGridId').store.load();
			Ext.getCmp('content').layout.setActiveItem('tasksGridId');
			Ext.getCmp('schedulerTreePanel').expand();
		}
	}
});

var streamManagement = ({
	collapsible: true,
	frame: false,
	id: 'streamManagement',
	iconCls : 'stream_management',
	activeItem : 0,	
	items : [generationStream, streamLoadingPane, schemas, standardSchemas],
    layout: 'accordion',
    padding: '0 0 0 5',
    style : 'background: #fff',
	region: 'center',
	title: _label['streamManagement'],
	listeners : {
		expand : function() {
			Ext.getCmp('generationStreamGrid').store.load();
			Ext.getCmp('content').layout.setActiveItem('generationStreamGrid');
			Ext.getCmp('generationStream').expand();
		}
	}
});

clearTimeout(graphLoadTimer);

var reports = ({
	frame: false,
	id: 'reports',
	iconCls : 'report_state',
	listeners:{
		expand: function() {
			setDefaultAppForReports(storeApplications);
			realTimeGraphStore.load();
			yearStore.load();
			Ext.getCmp('content').getLayout().setActiveItem(realTimeGraph);
            doGetRealTimeLoadData();
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='realTimeReport'){
				realTimeGraphStore.load();
				Ext.getCmp('content').getLayout().setActiveItem(realTimeGraph);
			} else if(rec.get('id')=='detailedStateReport'){
				//detailedStateGraphStore.load();
				initComboDetailedDate();
				barChartStateDetailedStore.load(); 
				Ext.getCmp('content').getLayout().setActiveItem(detailedStateGraph);
			} else if(rec.get('id')=='monthlyStateReport'){
				stateGraphStore.load();
				barChartStateStore.load();				
				storeSchema.load();
				Ext.getCmp('content').getLayout().setActiveItem(stateGraph);
			} else if(rec.get('id')=='annualStateReport'){
				//_report annualStateGraphStore.load();
				annualBarChartStateStore.load();
				storeAnnualSchema.load();
				Ext.getCmp('content').getLayout().setActiveItem(annualStateGraph);
			} else if(rec.get('id')=='reportHelp'){
				helpReports();
			}
		},
		collapse : function() {
			clearTimeout(graphLoadTimer);
		}
    },
	root: {
		children: reportChlds
	},
	rootVisible: false,
	title: 'Report',
	useArrows: true,
	xtype: 'treepanel'
});

//Administration
var admin = ({
	frame: false,
	id: 'serversPanelId',
	title: _label['admin'],
	iconCls : 'admin',
	isTimerEnabled : false,
	listeners:{
		expand: function() {
			var store = Ext.getCmp('adminLogGridId').store;
			store.load();
			Ext.getCmp('content').layout.setActiveItem(adminLogGrid);
			var f = function() {
				if (this.isTimerEnabled) {
					store.loadPage(store.currentPage);
					Ext.defer(f, 3000, this);
				}
			}
			this.isTimerEnabled = true;
			Ext.defer(f, 3000, this);
			//doGetRealTimeLogData();
		},
		itemclick : function(view,rec,item,index,eventObj) {
			if(rec.get('id')=='server'){
				Ext.getCmp('serversGridId').store.load();
				Ext.getCmp('content').layout.setActiveItem('serversGridId');
			} else if(rec.get('id')=='adminInfo'){		
				Ext.getCmp('adminInfoGridId').store.load();
				Ext.getCmp('content').layout.setActiveItem(adminInfoGrid);
			} else if(rec.get('id')=='adminHelp'){
				popupAdminHelp();
			} else if(rec.get('id')=='adminEmail'){
				popupAdminEmail();
			} else if(rec.get('id')=='adminFTP'){
				popupFTPConfig(false);
			} else if(rec.get('id')=='adminLog'){
				Ext.getCmp('adminLogGridId').store.load();
				Ext.getCmp('content').layout.setActiveItem(adminLogGrid);
			}
		},
		collapse : function() {
			this.isTimerEnabled = false;
        }
	},
	root: {
		children: adminChlds
	},
	rootVisible: false,
	useArrows: true,
	xtype: 'treepanel'
});

//applications
var menuChldCnt=0;
var contentChldCnt=0;
if(applicationChlds.length>0){
	menuChlds[menuChldCnt] = applications;
	contentChlds[contentChldCnt]=applicationsGrid;
	menuChldCnt++;
	contentChldCnt++;	
}
//database
if(databaseChlds.length>0){
	menuChlds[menuChldCnt] = databases;
	contentChlds[contentChldCnt]=databasesGrid;
	menuChldCnt++;
	contentChldCnt++;	
}
//stream management
if(generationStreamChlds.length > 0 || schemaChlds.length > 0 || standardSchemaChlds.length > 0){

	menuChlds[menuChldCnt] = streamManagement;

	contentChlds[contentChldCnt]=generationStreamGrid;
	contentChldCnt++;
    contentChlds[contentChldCnt]=generationStreamPanel;
    contentChldCnt++;
    
	contentChlds[contentChldCnt]=schemasGrid;
	contentChldCnt++;
	contentChlds[contentChldCnt]=schemaFieldsTree;
	contentChldCnt++;

	contentChlds[contentChldCnt]=schemasGrid;
	contentChldCnt++;
	contentChlds[contentChldCnt]=schemaFieldsTree;
	contentChldCnt++;
	menuChldCnt++;
}

//schedulers
if(schedulerChlds.length>0 || connectionsChlds.length>0 || jobsChlds.length>0){
	menuChlds[menuChldCnt] = schedulers;
	contentChlds[contentChldCnt]=tasksGrid;
	contentChldCnt++;
	contentChlds[contentChldCnt]=connectionsGrid;
	contentChldCnt++;
	contentChlds[contentChldCnt]=jobsGrid;
	contentChldCnt++;
	menuChldCnt++;
}
//users
if(userChlds.length>0){
	menuChlds[menuChldCnt] = users;
	contentChlds[contentChldCnt]=usersGrid;
	menuChldCnt++;
	contentChldCnt++;
}
//Reports
if(reportChlds.length>0){
	menuChlds[menuChldCnt] = reports;
	contentChlds[contentChldCnt]=stateGraph;
	contentChldCnt++;
	menuChldCnt++
}
//Admin
if(adminChlds.length>0){
	menuChlds[menuChldCnt] = admin;
	contentChlds[contentChldCnt]=serversGrid;
	contentChldCnt++;
	menuChldCnt++;
}

var menu = ({
	collapsible: true,
	frame: false,
	id: 'menu',
	items : menuChlds,
    layout: 'accordion',
    margins: '5 5 5 5',
    padding: '2 2 2 2',
	region: 'west',
	title: _label['mainMenu'],
    width: 280
});

var content = ({
	activeItem : 0,
	frame: false,
	id: 'content',
	items : contentChlds,
	layout: 'card',
	margins: '5 5 5 0',
	region: 'center'
});

globalVars = {
	defaultTimeout : 60000,
	sessionId : null,
	userNotActiveTime : 0,
	maxTime : 45 * 60 * 1000
};
	
Ext.onReady(function() {
	Ext.Ajax.timeout = globalVars.defaultTimeout;	
	
	Ext.create('Ext.container.Viewport', {
		items: [header , menu , content],
		layout: 'border'
	});
	Ext.Ajax.request( {
        url : './emailError.json',
        success : function(result) {
            recordResult = Ext.JSON.decode(result.responseText);
            
            if(recordResult.success) {
            	isEmailErrorFound = true;
            	App.setAlert(false , recordResult.message);
            }
        }
    });

	/**
	 * On session timeout - redirect to login page.
	 */
	var interval = 1000;
	var isSessionTimeout = function() {
		globalVars.userNotActiveTime += interval;
		if (globalVars.userNotActiveTime > globalVars.maxTime) {
			globalVars.userNotActiveTime = 0;
			window.location = "index.jsp"; 
		} else {
			Ext.defer(isSessionTimeout, interval);		
		}
	};
	isSessionTimeout.call(this);
	/**
	 * On server restart - redirect to login page. 
	 * @param {Object} conn
	 * @param {Object} response
	 * @param {Object} options
	 */
	Ext.Ajax.on('requestcomplete', function (conn, response, options) {
		globalVars.userNotActiveTime = 0;
		if (globalVars.sessionId) {
			if (globalVars.sessionId != Ext.util.Cookies.get("JSESSIONID")) {
				window.location = "index.jsp"; 
			}
		} else {
			globalVars.sessionId = Ext.util.Cookies.get("JSESSIONID");
		}
	});
	/**
	 * On server stop - alert.
	 * @param {Object} conn
	 * @param {Object} response
	 * @param {Object} options
	 */
	Ext.Ajax.on('requestexception', function (conn, response, options) {
		callAlert(_alert['serverDown']);
	});
});
