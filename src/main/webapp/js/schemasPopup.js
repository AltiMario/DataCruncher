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

var idSchemaType = 1;
function popupSchemas(record, add) {
	if (add) {
		idSchemaType = 1;
		scheduleDataSource.schedulerStore.load();
	}else{
        idSchemaType = record.get('idSchemaType');
    }
	var storeStreamType = new Ext.data.ArrayStore({
		data : [ [ '1', _streamType['XML'] ], [ '2', _streamType['XMLEXI'] ],
				[ '3', _streamType['flatFileFixedPosition'] ],
				[ '4', _streamType['flatFileDelimited'] ],
				[ '5', _streamType['JSON'] ], [ '6', _streamType['EXCEL'] ] ],
		fields : [ 'idStreamType', 'name' ]
	});
	
	var setAvailCheckBox = function() {
		var flag = (idSchemaType == 1 || idSchemaType == 2) ? false : true;
		if (!flag) {
			var params = {
				url : 'controller.isAdmin.json',
				success : function(resp) {
					flag = eval(resp.responseText);
					Ext.getCmp('idAvailableCheckBox').setDisabled(flag);
					//Ext.getCmp('idAvailableCheckBoxLabel').setVisible(flag);
				}
			}
			Ext.Ajax.request(params);
		}
	}
	
	var customErrorsStore = Ext.data.StoreManager.lookup('customErrorsStore');
	var isEEVersion = new Boolean();
	isEEVersion = customErrorsStore;

	new Ext.Window(
			{
				height : 515,
				width : 935,
				bodyStyle:{"background-color":"#ffffff;padding:10px;"},
				listeners : {
					beforerender : function() {
						setAvailCheckBox.call();
					}
 				}, 
				id : 'popupSchemas',
				layout : 'absolute',
				modal : true,
				resizable : false,
				title : _label['customValidation'],
				items : [ {
					xtype : 'form',
					waitMsgTarget: true,
					layout : 'absolute',
					frame : false,
					border : false,
					url : 'controller.validateForm.json',
					items : [
					         //first row - 1st column					
					         {	 xtype : 'fieldset',
					        	 height : 83,
					        	 width: 502,
					        	 title : _label['base'],
					        	 layout : 'absolute',
					        	 items : [
					        	          {
					        	        	  id : 'name',
					        	        	  name : 'name',					        	        	  
					        	        	  value : record.get('name'),
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['name'],
					        	        	  xtype : 'textfield',
					        	        	  width : 150
					        	          }, {
					        	        	  displayField : 'name',
					        	        	  forceSelection : true,
					        	        	  id : 'idApplication',
					        	        	  name : 'idApplication',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idApplication'),
					        	        	  store : storeApplications,
					        	        	  triggerAction : 'all',
					        	        	  valueField : 'idApplication',
					        	        	  fieldLabel : _label['application'],
					        	        	  labelAlign : 'top',
					        	        	  x : 160,
					        	        	  xtype : 'combo',
					        	        	  width : 150,
					        	        	  listeners : {
					        	        		  select : function(combo, records) {
					        	        			  var appIds = Ext.getCmp('idApplication')
					        	        			  .getValue();
					        	        			  var rec = storeApplications
					        	        			  .getAt(storeApplications.find(
					        	        					  'idApplication', appIds));
					        	        			  Ext.getCmp('id').clearValue();
					        	        			  if (Ext.getCmp('idPlannedCheckBox')
					        	        					  .getValue()) {
					        	        				  Ext.getCmp('id').setValue(
					        	        						  rec.get('plannedName'));
					        	        			  }
					        	        		  },
					        	        		  change : function(field, newValue, oldValue,
					        	        				  eOpts) {
					        	        			  var appIds = Ext.getCmp('idApplication')
					        	        			  .getValue();
					        	        			  if (appIds.length == 0) {
					        	        				  Ext.getCmp('idSchema').clearValue();
					        	        				  storeSchemasForUser.removeAll();
					        	        			  }
					        	        		  }
					        	        	  }
					        	          }, {
					        	        	  id : 'version',
					        	        	  name : 'version',					        	        	  
					        	        	  x : 320,
					        	        	  xtype : 'textfield',
					        	        	  disabled: true,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['version'],
					        	        	  width : 50,
					        	        	  value : record.get('version')
					        	          }, {
					        	        	  xtype : 'label',
					        	        	  text : _label['active'] + ':',
					        	        	  x : 380,
					        	        	  y : 0
					        	          }, {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idActiveCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue : false,
					        	        	  x : 390,
					        	        	  y : 18,
					        	        	  name : 'isActive',
					        	        	  checked : record.get('isActive'),
					        	        	  disabled: record.get('idSchema') > 0 ? false : true,
					        	        			  listeners : {
					        	        				  change : function(checkbox, newValue) {
					        	        					  if(newValue && record.get('idSchema') > 0){
					        	        						  Ext.getCmp('idAvailableCheckBox').setDisabled(false);
					        	        						  Ext.Ajax.request({
					        	        							  params : {
					        	        								  idSchema : record.get('idSchema')
					        	        							  },
					        	        							  success : function(result) {
					        	        								  var recordResult = Ext.JSON.decode(result.responseText);
					        	        								  if (recordResult.success == "false") {                                						
					        	        									  App.setAlert(false,	_error['schemaActivateError']);
					        	        									  Ext.getCmp('idActiveCheckBox').setValue(false);
					        	        									  Ext.getCmp('idAvailableCheckBox').setDisabled(true);
					        	        									  Ext.getCmp('idAvailableCheckBox').setValue(false);
					        	        								  }
					        	        							  },
					        	        							  url : './schemaValidate.json'
					        	        						  });

					        	        					  } else {
					        	        						  Ext.getCmp('idAvailableCheckBox').setDisabled(true);
					        	        						  Ext.getCmp('idAvailableCheckBox').setValue(false);
					        	        					  }
					        	        				  }
					        	        			  }
					        	          }, {
					        	        	  xtype : 'label',
					        	        	  x: 430, 
					        	        	  y: 0,
					        	        	  id : 'idAvailableCheckBoxLabel',
					        	        	  text : _label['available'] + ':',
					        	        	  hidden : false 
					        	          }, {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idAvailableCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue : false,
					        	        	  x : 440,
					        	        	  y: 18,
					        	        	  name : 'isAvailable',
					        	        	  checked : record.get('isAvailable'),
					        	        	  disabled : record.get('idSchema') > 0 ? (record.get('isActive') == true ? false : true ) : true,
					        	        			  hidden : false		
					        	          }
					        	          ]
					         },

					         //first row - 2nd column
					         {   xtype : 'fieldset',
					        	 height : 83,
					        	 width : 385,
					        	 x: 520,
					        	 y : 0,
					        	 title : _label['database'],
					        	 layout : 'absolute',
					        	 items : [							
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idValidateToDbCheckBox',
					        	        	  submitValue : false,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['validationDatabase'],
					        	        	  uncheckedValue : false,
					        	        	  checked : record.get('idValidationDatabase'),
					        	        	  disabled: storeDatabases.getCount() > 0 ? false : true,
					        	        			  listeners : {
					        	        				  change : function(checkbox, newValue) {
					        	        					  if(newValue){
					        	        						  Ext.getCmp('idValidationDatabase').enable();
					        	        					  }else{
					        	        						  Ext.getCmp( 'idValidationDatabase').setValue('')
					        	        						  Ext.getCmp( 'idValidationDatabase').disable();
					        	        					  }
					        	        				  },
					        	        				  afterrender : function() {
					        	        					  this.checked ? Ext
					        	        							  .getCmp('idValidationDatabase').enable()
					        	        							  : Ext.getCmp('idValidationDatabase')
					        	        							  .disable();
					        	        				  }
					        	        			  }
					        	          },					
					        	          {  
					        	        	  displayField : 'name',
					        	        	  id : 'idValidationDatabase',
					        	        	  name : 'idValidationDatabase',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idValidationDatabase') == 0 ? '' : record.get('idValidationDatabase'),
					        	        			  store : storeDatabases,
					        	        			  triggerAction : 'all',
					        	        			  valueField : 'idDatabase',
					        	        			  x : 25,
					        	        			  xtype : 'combo',
					        	        			  width : 130,
					        	        			  y : 18
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idPublishToDbCheckBox',
					        	        	  submitValue : false,
					        	        	  uncheckedValue : false,
					        	        	  x : 190,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['storeDatabase'],
					        	        	  checked : record.get('idDatabase') == 0 ? false : true,
					        	        	  disabled: storeDatabases.getCount() > 0 ? false : true,
					        	        			  listeners : {
					        	        				  change : function(checkbox, newValue) {
					        	        					  if(newValue){
					        	        						  Ext.getCmp('idDatabase').enable()
					        	        					  }else{
					        	        						  Ext.getCmp('idDatabase').disable();
					        	        						  Ext.getCmp('idDatabase').setValue('');
					        	        					  }

					        	        					  /*newValue && record.get('idDatabase') != 0 ? Ext.getCmp('idForecastingCheckBox').enable() : 
 											Ext.getCmp('idForecastingCheckBox').disable();
 										/*if (newValue == false && Ext.getCmp('idForecastingCheckBox').checked == true) 
 											Ext.getCmp('idForecastingCheckBox').setValue(false);*/
					        	        					  var forecastChkbx = Ext.getCmp('idForecastingCheckBox'); 
					        	        					  var indexChkbx = Ext.getCmp('idIndexIncrementCheckBox'); 
					        	        					  var wtChkbx = Ext.getCmp('idWarnToleranceCheckBox');
					        	        					  //if publishToDb select event
					        	        					  if (newValue && record.get('idDatabase') != 0) {
					        	        						  forecastChkbx.enable();
					        	        						  indexChkbx.enable();
					        	        						  wtChkbx.enable();
					        	        					  } else {
					        	        						  forecastChkbx.disable();
					        	        						  indexChkbx.disable();
					        	        						  wtChkbx.disable();
					        	        					  }
					        	        					  //if publishToDb deselect event
					        	        					  if (newValue == false) {
					        	        						  if (forecastChkbx.checked == true) forecastChkbx.setValue(false);
					        	        						  if (indexChkbx.checked == true) indexChkbx.setValue(false);
					        	        						  if (wtChkbx.checked == true) wtChkbx.setValue(false);
					        	        					  }
					        	        				  },
					        	        				  afterrender : function() {
					        	        					  this.checked ? Ext.getCmp('idDatabase')
					        	        							  .enable() : Ext
					        	        							  .getCmp('idDatabase').disable();
					        	        				  }
					        	        			  }
					        	          },
					        	          {
					        	        	  displayField : 'name',
					        	        	  id : 'idDatabase',
					        	        	  name : 'idDatabase',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idDatabase') == 0 ? '' : record.get('idDatabase'),
					        	        			  store : storeDatabases,
					        	        			  triggerAction : 'all',
					        	        			  valueField : 'idDatabase',
					        	        			  x : 215,
					        	        			  xtype : 'combo',
					        	        			  width : 140,
					        	        			  y : 18,
					        	        			  listeners : {
					        	        				  change : function(field, newValue, oldValue) {
					        	        					  if (newValue != 0) {
					        	        						  Ext.getCmp('idForecastingCheckBox').enable();
					        	        						  Ext.getCmp('idIndexIncrementCheckBox').enable();
					        	        						  Ext.getCmp('idWarnToleranceCheckBox').enable();
					        	        					  }
					        	        				  }
					        	        			  }
					        	          }
					        	          ]
					         },

					         //second row - 1st column
					         { 	xtype : 'fieldset',
					        	 width: 582,
					        	 height : 113,
					        	 y : 100,
					        	 title : _label['dataStream'],
					        	 layout : 'absolute',
					        	 items : [
					        	          {
					        	        	  displayField : 'name',
					        	        	  id : 'idStreamType',
					        	        	  name : 'idStreamType',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idStreamType'),
					        	        	  store : storeStreamType,
					        	        	  triggerAction : 'all',
					        	        	  forceSelection : true,
					        	        	  valueField : 'idStreamType',
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['streamType'],
					        	        	  xtype : 'combo',
					        	        	  width : 150,
					        	        	  listeners : {
					        	        		  collapse : function() {
					        	        			  Ext.getCmp('textFieldDelimiter').setValue("");
					        	        			  if (Ext.getCmp("idStreamType").getValue() == 4) {
					        	        				  Ext.getCmp("delimiter").setDisabled(false);
					        	        				  Ext.getCmp("trimchar").setDisabled(false);
					        	        				  Ext.getCmp("idNoHeaderCheckBox").setDisabled(false);
					        	        			  } else {
					        	        				  Ext.getCmp("trimchar").setDisabled(true);
					        	        				  Ext.getCmp("idNoHeaderCheckBox").setDisabled(true);
					        	        				  Ext.getCmp("delimiter").setDisabled(true);
					        	        				  Ext.getCmp("delimiterLabel").setDisabled(true);
					        	        				  Ext.getCmp('textFieldDelimiter').setDisabled(true);											
					        	        			  }
					        	        		  },
					        	        		  afterrender : function() {
					        	        			  if (record.get('idSchema') != 0) {
					        	        				  Ext.getCmp('idStreamType').disable();
					        	        			  }
					        	        		  }
					        	        	  }
					        	          },							
					        	          {
					        	        	  id : 'delimiter',
					        	        	  x : 160,
					        	        	  xtype : 'radiogroup',
					        	        	  width : 165,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['delimiter'],
					        	        	  name : 'delimiter',
					        	        	  items : [ {
					        	        		  boxLabel : ';',
					        	        		  inputValue : 1,
					        	        		  name : 'delimiterOption',
					        	        		  checked : true,
					        	        		  width: 30
					        	        	  }, {
					        	        		  boxLabel : '|',
					        	        		  inputValue : 2,
					        	        		  name : 'delimiterOption',
					        	        		  width: 30
					        	        	  }, {
					        	        		  boxLabel : 'Tab',
					        	        		  inputValue : 3,
					        	        		  name : 'delimiterOption',
					        	        		  width: 50
					        	        	  }, {
					        	        		  boxLabel : 'Other',
					        	        		  inputValue : 4,
					        	        		  name : 'delimiterOption',
					        	        		  width: 55	
					        	        	  } ],
					        	        	  listeners : {
					        	        		  change : function(el, val) {
					        	        			  if (Ext.getCmp('delimiter').getValue().delimiterOption == 4) {
					        	        				  Ext.getCmp('textFieldDelimiter')
					        	        				  .setDisabled(false);
					        	        			  } else {
					        	        				  Ext.getCmp('textFieldDelimiter')
					        	        				  .setDisabled(true);
					        	        				  Ext.getCmp('textFieldDelimiter')
					        	        				  .setValue("");
					        	        			  }
					        	        		  }
					        	        	  }
					        	          },
					        	          {
					        	        	  id : 'textFieldDelimiter',
					        	        	  name : 'textFieldDelimiter',					        	        	  
					        	        	  x : 340,
					        	        	  y : 18,
					        	        	  xtype : 'textfield',
					        	        	  width : 35,
					        	        	  minLength: 1,
					        	        	  maxLength: 1,
					        	        	  enforceMaxLength: true
					        	          },
					        	          {value: _label['trimChar'] , x : 410,  y : 0, xtype: 'displayfield'},
					        	          {								
					        	        	  id : 'trimchar',
					        	        	  name : 'trimchar',					        	        	  
					        	        	  value : record.get('chrDelimiter') == '\0' ? '' : record.get('chrDelimiter'),
					        	        			  x : 410,
					        	        			  y: 20,
					        	        			  xtype : 'textfield',
					        	        			  width : 30,
					        	        			  minLength: 1,
					        	        			  maxLength: 1,
					        	        			  enforceMaxLength: true,
					        	        			  disabled: true
					        	          },
					        	          {value: 'No Header' /*_label['noHeader']*/ , x : 480,  y : 0, xtype: 'displayfield'},
					        	          {								
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idNoHeaderCheckBox',
					        	        	  name : 'idNoHeaderCheckBox',					        	        	  
			        	        			  x : 480,
			        	        			  y: 20,
			        	        			  inputValue : true,
			        	        			  submitValue: false,
			        	        			  disabled : false, // Ext.getCmp("idStreamType").getValue() != 4,
			        	        			  checked : record.get('noHeader')
						        	      }
					        	    ]
					         },

					         //second row - 2nd column
					         {	xtype: 'fieldset',
					        	 height : 113,
					        	 width : 305,
					        	 x: 600,
					        	 y : 100,						
					        	 layout : 'absolute',
					        	 title : _label['timeWindow'],
					        	 items : [
					        	          {
					        	        	  format : 'd/m/Y',
					        	        	  id : 'startDate',
					        	        	  name : 'startDate',					        	        	  
					        	        	  value : record.get('startDate'),
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['startDate'],
					        	        	  xtype : 'datefield',
					        	        	  width : 100
					        	          },
					        	          {
					        	        	  format : 'd/m/Y',
					        	        	  id : 'endDate',
					        	        	  name : 'endDate',					        	        	  
					        	        	  value : record.get('endDate'),
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['endDate'],
					        	        	  x : 0,
					        	        	  y : 40,
					        	        	  xtype : 'datefield',
					        	        	  width : 100
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idPlannedCheckBox',
					        	        	  submitValue : false,
					        	        	  uncheckedValue : false,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['planned'],
					        	        	  x : 120,
					        	        	  checked : record.get('isPlanned'),
					        	        	  disabled: scheduleDataSource.schedulerStore.getCount() > 0 ? false : true,
					        	        			  listeners : {
					        	        				  change : function(checkbox, newValue) {
					        	        					  if (newValue) {
					        	        						  Ext.getCmp('id').enable();
					        	        						  var appIds = Ext
					        	        						  .getCmp('idApplication')
					        	        						  .getValue();
					        	        						  var rec = storeApplications
					        	        						  .getAt(storeApplications
					        	        								  .find('idApplication',
					        	        										  appIds));
					        	        						  if (rec.get('plannedName')) {
					        	        							  Ext.getCmp('id').clearValue();
					        	        							  Ext.getCmp('id').setValue(rec.get('plannedName') == "-1" ? "" : rec.get('plannedName'));
					        	        						  }
					        	        					  } else {
					        	        						  Ext.getCmp('id').disable();
					        	        					  }
					        	        				  },
					        	        				  afterrender : function() {
					        	        					  scheduleDataSource.schedulerStore.load();
					        	        					  this.checked ? Ext.getCmp('id').enable()
					        	        							  : Ext.getCmp('id').disable();
					        	        				  }
					        	        			  }
					        	          },
					        	          {
					        	        	  displayField : 'name',
					        	        	  id : 'id',
					        	        	  name : 'id',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('plannedName') <= 0 ? '' : record.get('plannedName'),
			        	        			  store : scheduleDataSource.schedulerStore,
			        	        			  triggerAction : 'all',
			        	        			  valueField : 'id',
			        	        			  x : 140,
			        	        			  xtype : 'combo',
			        	        			  width : 118,
			        	        			  y : 18
					        	          }
					        	          ]
					         },

					         //third row - 1st Column
					         { 	xtype : 'fieldset',
					        	 height : 83,
					        	 width : 370,
					        	 y : 210,
					        	 title : 'Event Trigger',
					        	 layout : 'absolute',
					        	 items : [							
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idEventTriggerCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue : false,
					        	        	  labelAlign : 'top',
					        	        	  x : 10,
					        	        	  y : 20,
					        	        	  name : 'idActiveEventCheckBox',
					        	        	  checked : record.get('isEventTrigger'),
					        	        	  listeners : {
					        	        		  change : function(checkbox, newValue) {		
					        	        			  Ext.getCmp('idTriggerStatus').setDisabled(!newValue);
					        	        			  Ext.getCmp('idEventTrigger').setDisabled(!newValue);
					        	        		  }
					        	        	  }
					        	          },
					        	          {
					        	        	  displayField : 'name',
					        	        	  id : 'idTriggerStatus',
					        	        	  name : 'idTriggerStatus',
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idTriggerStatus'),
					        	        	  store : storeEventTriggerStatus,
					        	        	  triggerAction : 'all',
					        	        	  forceSelection : true,
					        	        	  valueField : 'idTriggerStatus',
					        	        	  labelAlign : 'top',
					        	        	  x : 40,
					        	        	  fieldLabel : 'Status',
					        	        	  xtype : 'combo',
					        	        	  width : 85,
					        	        	  disabled: record.get('isEventTrigger') == true ? false : true

					        	          },
					        	          {
					        	        	  displayField : 'name',
					        	        	  forceSelection : true,
					        	        	  id : 'idEventTrigger',
					        	        	  name : 'idEventTrigger',
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idEventTrigger'),
					        	        	  store : storeSchemaTriggers,
					        	        	  triggerAction : 'all',
					        	        	  valueField : 'idEventTrigger',
					        	        	  fieldLabel : 'Trigger',
					        	        	  labelAlign : 'top',
					        	        	  x : 145,
					        	        	  xtype : 'combo',
					        	        	  width : 200,
					        	        	  disabled: record.get('isEventTrigger') == true ? false : true
					        	          }
					        	          ]
					         },

					         //third row - 2nd column
					         {	xtype: 'fieldset',
					        	 x : 375, 
					        	 y : 220,
					        	 layout : 'absolute',
					        	 width : 95,
					        	 height : 83,
					        	 layout : 'absolute',
					        	 title : _label['global'],
					        	 items : [
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idForecastingCheckBox',
					        	        	  y: 10,
					        	        	  name : 'idForecastingCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue: false,
					        	        	  disabled : !record.get('publishToDb'),
					        	        	  boxLabel : _label['forecasting'],
					        	        	  checked : record.get('isForecasted')
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idIndexIncrementCheckBox',
					        	        	  y: 30,
					        	        	  name : 'idIndexIncrementCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue: false,
					        	        	  disabled : !record.get('publishToDb'),
					        	        	  boxLabel : _label['indexing'],
					        	        	  checked : record.get('isIndexedIncrement')
					        	          }				        	          
					        	          ]
					         },

					         //third row - 3rd column
					         {	xtype: 'fieldset',
					        	 x : 475, 
					        	 y : 220,
					        	 layout : 'absolute',
					        	 width : 190,
					        	 height : 83,
					        	 title : _label['streamLogging'],
					        	 items : [
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idInValidCheckBox',
					        	        	  y: 10,
					        	        	  submitValue : false,
					        	        	  boxLabel : _label['streamLoggingKO'],
					        	        	  checked : record.get('isInValid')
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idWarningCheckBox',
					        	        	  y: 10,
					        	        	  submitValue : false,
					        	        	  x : 50,
					        	        	  boxLabel : _label['streamLoggingWarning'],
					        	        	  checked : record.get('isWarning')
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idValidCheckBox',
					        	        	  x : 130,
					        	        	  y: 10,
					        	        	  boxLabel : _label['streamLoggingOK'],
					        	        	  submitValue : false,
					        	        	  checked : record.get('isValid')
					        	          }
					        	          ]							
					         },

					        //third row - 4th column
					         {	xtype: 'fieldset',
					        	 x : 670, 
					        	 y : 220,
					        	 layout : 'absolute',
					        	 width : 100,
					        	 height : 83,
					        	 title : _label['noSqlLog'],
					        	 items : [
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idMongoDBCheckBox',
					        	        	  name : 'idMongoDBCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue: false,
					        	        	  y : 10,
					        	        	  x : 0,
					        	        	 // disabled : !isEEVersion,
					        	        	  boxLabel : _label['mongoDB'],
                                              checked : record.get('isMongoDB')
					        	          }
					        	          ]
					         },
					         
					         //third row - 5th column
					         {	xtype: 'fieldset',
					        	 x : 775, 
					        	 y : 220,
					        	 layout : 'absolute',
					        	 width : 130,
					        	 height : 83,
					        	 title : _label['warnTolerance'],
					        	 items : [
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idWarnToleranceCheckBox',
					        	        	  name : 'idWarnToleranceCheckBox',
					        	        	  inputValue : true,
					        	        	  submitValue: false,
					        	        	  y : 10,
					        	        	  x : 20,
					        	        	  disabled : !record.get('publishToDb'),
					        	        	  boxLabel : _label['available'],
					        	        	  checked : record.get('isWarnTolerance')
					        	          }
					        	          ]
					         },

					         //fourth row
					         {
					        	 height : 138,
					        	 id : 'description',
					        	 name : 'description',
					        	 submitValue : false,
					        	 value : record.get('description'),
					        	 xtype : 'htmleditor',
					        	 labelAlign : 'top',
					        	 fieldLabel : _label['description'],
					        	 y : 285,
					        	 enableFontSize : false,
					        	 width : 900
					         },
					         {
									text : _message['save'],
									x : 350,
									xtype : 'button',
									width : 100,
									y : 435,
									handler : function() {
										var form = this.up('form').getForm();
			                    		enableOrDisableFields(false);
			                    		form.submit( {
			                    			waitMsg: _message["waitMessage"],
			                    			params : {schemaType : 'validationCustomStream'},
			                    			success : function(form, action) {               				

			                    				record.set('name', Ext.getCmp('name')
			                    						.getValue());
			                    				record.set('description', Ext.getCmp(
			                    				'description').getValue());
			                    				record.set('idApplication', Ext.getCmp(
			                    				'idApplication').getValue());
			                    				record.set('idDatabase', Ext.getCmp(
			                    				'idDatabase').getValue());

			                    				if(Ext.getCmp('idEventTriggerCheckBox').getValue()) {									
			                    					record.set('isEventTrigger', true);
			                    					record.set('idEventTrigger', Ext.getCmp('idEventTrigger').getValue());
			                    					record.set('idTriggerStatus', Ext.getCmp('idTriggerStatus').getValue());								
			                    				} else {
			                    					record.set('isEventTrigger', false);
			                    					record.set('idEventTrigger', 0);
			                    					record.set('idTriggerStatus', -1);
			                    				}

			                    				record.set('idStreamType', Ext.getCmp(
			                    				'idStreamType').getValue());
			                    				record.set('version', Ext.getCmp('version')
			                    						.getValue());

			                    				if (Ext.getCmp('idStreamType').getValue() == 4) {
			                    					record.set('delimiter', Ext.getCmp(
			                    					'idStreamType').getValue());
			                    					if (Ext.getCmp('delimiter').getValue().delimiterOption == 1) {
			                    						record.set('delimiter', ';');
			                    					}
			                    					if (Ext.getCmp('delimiter').getValue().delimiterOption == 2) {
			                    						record.set('delimiter', '|');
			                    					}
			                    					if (Ext.getCmp('delimiter').getValue().delimiterOption == 3) {
			                    						record.set('delimiter', '\t');
			                    					}
			                    					if (Ext.getCmp('delimiter').getValue().delimiterOption == 4) {
			                    						record.set('delimiter', Ext.getCmp(
			                    						'textFieldDelimiter')
			                    						.getValue());
			                    					}
			                    				} else {
			                    					record.set('delimiter', null);
			                    				}
			                    				record.set('startDate', Ext.getCmp('startDate')
			                    						.getValue());
			                    				record.set('endDate', Ext.getCmp('endDate')
			                    						.getValue());
			                    				record.set('publishToDb', Ext.getCmp(
			                    				'idPublishToDbCheckBox').getValue());
			                    				record.set('inputToDb', Ext.getCmp(
			                    				'idValidateToDbCheckBox').getValue());

			                    				if (Ext.getCmp('idPlannedCheckBox').getValue()) {
			                    					record.set('isPlanned', true);
			                    					record.set('plannedName', Ext.getCmp('id')
			                    							.getValue());
			                    				} else {
			                    					record.set('isPlanned', false);
			                    					record.set('plannedName', -1);
			                    				}

			                    				if (Ext.getCmp('idActiveCheckBox').getValue()) {
			                    					record.set('isActive', 1);
			                    				} else {
			                    					record.set('isActive', 0);
			                    				}
			                    				if (Ext.getCmp('idAvailableCheckBox').getValue()) {
			                    					record.set('isAvailable', 1);
			                    				} else {
			                    					record.set('isAvailable', 0);
			                    				}
			                    				if (Ext.getCmp('idValidCheckBox').getValue()) {
			                    					record.set('isValid', 1);
			                    				} else {
			                    					record.set('isValid', 0);
			                    				}	
			                    				if (Ext.getCmp('idInValidCheckBox').getValue()) {
			                    					record.set('isInValid', 1);
			                    				} else {
			                    					record.set('isInValid', 0);
			                    				}	
			                    				if (Ext.getCmp('idWarningCheckBox').getValue()) {
			                    					record.set('isWarning', 1);
			                    				} else {
			                    					record.set('isWarning', 0);
			                    				}
                                                if (Ext.getCmp('idMongoDBCheckBox').getValue()) {
                                                    record.set('isMongoDB', true);
                                                } else {
                                                    record.set('isMongoDB', false);
                                                }
                                                
			                    				record.set('isForecasted', Ext.getCmp('idForecastingCheckBox').getValue());
			                    				record.set('isIndexedIncrement', Ext.getCmp('idIndexIncrementCheckBox').getValue());
			                    				record.set('noHeader', Ext.getCmp('idNoHeaderCheckBox').getValue());
			                    				
			                    				record.set('isWarnTolerance', Ext.getCmp('idWarnToleranceCheckBox').getValue());
			                    				record.set('chrDelimiter', Ext.getCmp('trimchar').getValue());
			                    				record.set("idSchemaType", idSchemaType);
			                    				record.set("idValidationDatabase", Ext.getCmp('idValidationDatabase').getValue());
			                    				
			                    				if (add) {
			                    					schemasGrid.store.insert(0, record);
			                    				} 
			                    				schemasGrid.store.sync();
			                    				
			                    				Ext.getCmp('popupSchemas').close();
			                    			},
			                    			failure : function(form, action) {
			                    				enableOrDisableFields(true);	
			                    			}
			                    		});
									}
							 },
							 {
									text : _message['cancel'],
									x : 455,
									xtype : 'button',
									width : 100,
									y : 435,
									handler : function() {
										Ext.getCmp('popupSchemas').close();
									}
							}],				
				}]
			}).show();

	if (Ext.getCmp("idStreamType").getValue() == 4) {
		Ext.getCmp('trimchar').setDisabled(false);
		Ext.getCmp('idNoHeaderCheckBox').setDisabled(false);
		if (record.get('delimiter') == ';') {
			Ext.getCmp('delimiter').items.items[0].setValue(true);
			Ext.getCmp('textFieldDelimiter').setDisabled(true);
			Ext.getCmp('textFieldDelimiter').setValue("");
		} else if (record.get('delimiter') == '|') {
			Ext.getCmp('delimiter').items.items[1].setValue(true);
			Ext.getCmp('textFieldDelimiter').setDisabled(true);
			Ext.getCmp('textFieldDelimiter').setValue("");
		} else if (record.get('delimiter') == '\t') {
			Ext.getCmp('delimiter').items.items[2].setValue(true);
			Ext.getCmp('textFieldDelimiter').setDisabled(true);
			Ext.getCmp('textFieldDelimiter').setValue("");
		} else {
			Ext.getCmp('delimiter').items.items[3].setValue(true);
			Ext.getCmp('textFieldDelimiter').setDisabled(false);
			Ext.getCmp('textFieldDelimiter').setValue(record.get('delimiter'));
		}
	} else {
		Ext.getCmp('delimiter').setDisabled(true);
		Ext.getCmp('idNoHeaderCheckBox').setDisabled(true);
		Ext.getCmp('trimchar').setDisabled(true);
		Ext.getCmp('textFieldDelimiter').setDisabled(true);
		Ext.getCmp('textFieldDelimiter').setValue("");
	}
};
function enableOrDisableFields(state) {
	Ext.getCmp('idStreamType').setDisabled(state);
}
function popupSchemaHelp() {
	if (Ext.getCmp('popupSchemaHelp')) return;
	new Ext.Window({
		height : 250,
		id : 'popupSchemaHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['schemaHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupSchemaHelp').close();
			}
		} ],
		width : 665
	}).show(this);
};
scheduleDataSource = {
	schedulerStore : new Ext.data.JsonStore({
		autoSave : false,
		autoLoad : true,
		fields : [ {
			name : 'id',
			type : 'int',
			defaultValue : 0
		}, {
			name : 'name',
			type : 'string'
		} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './tasksRead.json'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		},
		listeners: {
			load: function(store,records,successful,operation,options ) {
	            if(store.getCount() > 0 && Ext.getCmp('idPlannedCheckBox') != undefined) {
	            	Ext.getCmp('idPlannedCheckBox').setDisabled(false);
	            } else if(Ext.getCmp('idPlannedCheckBox') != undefined){
	            	Ext.getCmp('idPlannedCheckBox').setDisabled(true);
	            }
	        }
		}
	})
}
