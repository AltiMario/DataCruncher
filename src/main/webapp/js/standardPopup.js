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

var idStandardSchemaType = 3;
var isInitFields = false;

function popupStandardSchemas(record, add) {
	
	if (add) {
		scheduleDataSource.schedulerStore.load();
	} else {
		isInitFields = true;
		Ext.Ajax.request( {
			params : {
				idSchemaLib : record.get('idSchemaLib')
			},
			success: function (result) {
				recordResult = Ext.JSON.decode(result.responseText);				
				if (recordResult.success == true) {
					record.set("idVersionLibrary", recordResult.results.version);					
				} else {
					App.setAlert(false, recordResult.errMsg);
				}
			},
			url : './schemasLibRead.json'
		});
	}
	var storeStreamType = new Ext.data.ArrayStore({
		data : [[ '7', _streamType['HL7'] ], [ '8', _streamType['SWIFT'] ], [ '9', _streamType['EDI_CICA'] ] ],
		fields: ['idStreamType' , 'name']
	});
	
	var storeSchemasLib = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'schemasLib',
		idProperty:'idSchemaLib',
		proxy: {
			type: 'ajax',
	        api: {
				read    : './schemasLibRead.json?type=version&streamType=' + record.get('idStreamType')			
			},
			extraParams: {
	            idSchemaType: 3
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: "success",
				messageProperty: 'message'
			}
		}
	});
	storeSchemasLib.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.message.indexOf('Error')!=-1){
					Ext.Msg.alert("" , _error['connectionError']);
				}
			}
		}	
	});
	
	var storeLibName = new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		model: 'schemasLib',
		idProperty:'idSchemaLib',
		proxy: {
			type: 'ajax',
	        api: {
				read    : './schemasLibRead.json'			
			},
			extraParams: {
	            idSchemaType: 3
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: "success",
				messageProperty: 'message'
			}
		}
	});
	storeLibName.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.message.indexOf('Error')!=-1){
					Ext.Msg.alert("" , _error['connectionError']);
				}
			}
		}	
	});
	
	new Ext.Window(
			{
				height : 540,
				width : 530,
				id : 'popupStandardSchemas',
				bodyStyle:{"background-color":"#ffffff;padding:10px;"},		
				layout : 'absolute',
				modal : true,
				resizable : false,
				title : _label['standardValidations'],
				items : [ {
					xtype : 'form',
					waitMsgTarget: true,
					layout : 'absolute',
					border : false,
					frame : false,
					url : 'controller.validateForm.json',
					items : [
					         //first row					
					         { 	xtype : 'fieldset',
					        	 height : 83,
					        	 width: 500,
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
					        	        	  width : 100,
					        	        	  value : record.get('version')
					        	          }, {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idActiveCheckBox',
					        	        	  submitValue : false,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['active'],
					        	        	  x : 430,
					        	        	  name : 'isActive',
					        	        	  checked : record.get('isActive'),
					        	        	  disabled: record.get('idSchema') > 0 ? false : true,
					        	        			  listeners : {
					        	        				  change : function(checkbox, newValue) {
					        	        					  if(newValue && record.get('idSchema') > 0){

					        	        						  Ext.Ajax.request({
					        	        							  params : {
					        	        								  idSchema : record.get('idSchema')
					        	        							  },
					        	        							  success : function(result) {
					        	        								  var recordResult = Ext.JSON.decode(result.responseText);
					        	        								  if (recordResult.success == "false") {                                						
					        	        									  App.setAlert(false,	_error['schemaActivateError']);
					        	        									  Ext.getCmp('idActiveCheckBox').setValue(false);
					        	        								  }
					        	        							  },
					        	        							  url : './schemaValidate.json'
					        	        						  });

					        	        					  }
					        	        				  }
					        	        			  }
					        	          }
					        	          ]
					         },

					         //second row
					         { 	xtype : 'fieldset',
					        	 width: 500,
					        	 height : 83,
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
					        	        	  listeners:{
					        	        		  select: function(){
					        	        			  isInitFields = false;
					        	        			  storeSchemasLib.proxy.api.read = './schemasLibRead.json?type=version&streamType=' + Ext.getCmp('idStreamType').getValue();
					        	        			  storeSchemasLib.load();
					        	        		  }
					        	        	  }
					        	          },{
					        	        	  displayField : 'version',
					        	        	  forceSelection : true,
					        	        	  id : 'idVersionLibrary',
					        	        	  name : 'idVersionLibrary',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idVersionLibrary'),
					        	        	  store : storeSchemasLib,
					        	        	  triggerAction : 'all',
					        	        	  valueField : 'version',
					        	        	  fieldLabel : _label['versionLibrary'],
					        	        	  labelAlign : 'top',
					        	        	  x : 160,
					        	        	  xtype : 'combo',
					        	        	  width : 150,
					        	        	  listeners:{
					        	        		  select: function(){							        	
					        	        			  isInitFields = false;
					        	        			  storeLibName.proxy.api.read = './schemasLibRead.json?type=libName&version=' + Ext.getCmp('idVersionLibrary').getValue() + '&streamType=' + Ext.getCmp('idStreamType').getValue();
					        	        			  storeLibName.load();
					        	        		  }
					        	        	  }
					        	          },{
					        	        	  displayField : 'libName',
					        	        	  forceSelection : true,
					        	        	  id : 'idSchema',
					        	        	  name : 'idSchema',					        	        	  
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idSchemaLib'),
					        	        	  store : storeLibName,
					        	        	  triggerAction : 'all',
					        	        	  valueField : 'idSchemaLib',
					        	        	  fieldLabel : _label['schema'],
					        	        	  labelAlign : 'top',
					        	        	  x : 320,
					        	        	  xtype : 'combo',
					        	        	  width : 150								
					        	          }
					        	          ]
					         },

					         //third row - 1st column
					         {	xtype: 'fieldset',
					        	 height : 98,
					        	 width : 370,
					        	 y : 190,
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
					        	        	  width : 100,
					        	        	  y: 5
					        	          },
					        	          {
					        	        	  format : 'd/m/Y',
					        	        	  id : 'endDate',
					        	        	  name : 'endDate',					        	        	  
					        	        	  value : record.get('endDate'),
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['endDate'],
					        	        	  x : 110,
					        	        	  y: 5,
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
					        	        	  x : 220,
					        	        	  y: 5,
					        	        	  name : 'plannedCheckBox',
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
					        	        							  Ext.getCmp('id').setValue(
					        	        									  rec.get('plannedName'));
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
					        	        			  x : 240,
					        	        			  xtype : 'combo',
					        	        			  width : 108,
					        	        			  y : 18
					        	          }
					        	          ]
					         },

					         //third row - 2nd column
					         {	xtype: 'fieldset',
					        	 x : 395, 
					        	 y : 190,
					        	 layout : 'absolute',
					        	 width : 103,
					        	 height : 98,
					        	 title : _label['streamLogging'],
					        	 items : [
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idInValidCheckBox',
					        	        	  submitValue : false,
					        	        	  name : 'isInValid',
					        	        	  boxLabel : _label['streamLoggingKO'],
					        	        	  checked : record.get('isInValid')
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idWarningCheckBox',
					        	        	  submitValue : false,
					        	        	  y : 20,
					        	        	  name : 'isWarning',
					        	        	  boxLabel : _label['streamLoggingWarning'],
					        	        	  checked : record.get('isWarning')
					        	          },
					        	          {
					        	        	  xtype : 'checkboxfield',
					        	        	  id : 'idValidCheckBox',
					        	        	  y : 40,
					        	        	  boxLabel : _label['streamLoggingOK'],
					        	        	  submitValue : false,
					        	        	  name : 'isValid',
					        	        	  checked : record.get('isValid')
					        	          }
					        	          ]							
					         },				

					         //fourth row
					         {
					        	 height : 150,
					        	 id : 'description',
					        	 submitValue : false,
					        	 value : record.get('description'),
					        	 xtype : 'htmleditor',
					        	 labelAlign : 'top',
					        	 fieldLabel : _label['description'],
					        	 y : 295,
					        	 enableFontSize : false,
					        	 width : 488
					         },
					         {
									text : _message['save'],
									x : 150,
									xtype : 'button',
									width : 100,
									y : 455,
									handler : function() {
										var form = this.up('form').getForm();

			                    		form.submit( {
			                    			waitMsg: _message["waitMessage"],
			                    			params : {schemaType : 'validationStandardStream'},
			                    			success : function(form, action) {

			                    				record.set('name', Ext.getCmp('name')
			                    						.getValue());
			                    				record.set('description', Ext.getCmp(
			                    				'description').getValue());
			                    				record.set('idApplication', Ext.getCmp(
			                    				'idApplication').getValue());
			                    				record.set('idStreamType', Ext.getCmp(
			                    				'idStreamType').getValue());
			                    				record.set('version', Ext.getCmp('version')
			                    						.getValue());							
			                    				record.set('startDate', Ext.getCmp('startDate')
			                    						.getValue());
			                    				record.set('endDate', Ext.getCmp('endDate')
			                    						.getValue());
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
			                    				record.set("idSchemaType", idStandardSchemaType);
			                    				record.set("idSchemaLib", Ext.getCmp('idSchema').getValue());
			                    				if (add) {
			                    					standardSchemasGrid.store.insert(0, record);
			                    				} 
			                    				standardSchemasGrid.store.sync();								
			                    				Ext.getCmp('popupStandardSchemas').close();
			                    			},
			                    			failure : function(form, action) {
			                    				//
			                    			}
			                    		});
									}
					         },
					         {
									text : _message['cancel'],
									x : 255,
									xtype : 'button',
									width : 100,
									y : 455,
									handler : function() {
										Ext.getCmp('popupStandardSchemas').close();
									}
					         }]
				}]
			}).show();	
	
	Ext.getCmp('idVersionLibrary').store.on('load',function(store) {
		
		if(isInitFields == true && record.get("idVersionLibrary")) {
			Ext.getCmp('idVersionLibrary').setValue(record.get("idVersionLibrary"));
		} else {
			Ext.getCmp('idVersionLibrary').setValue(store.first().get("version"));
		}
		storeLibName.proxy.api.read = './schemasLibRead.json?type=libName&version=' + Ext.getCmp('idVersionLibrary').getValue() + '&streamType=' + Ext.getCmp('idStreamType').getValue();
    	storeLibName.load();    	
	});
	Ext.getCmp('idSchema').store.on('load',function(store) {
		
		if(isInitFields == true && record.get("idSchemaLib")) {
			Ext.getCmp('idSchema').setValue(record.get("idSchemaLib"));
		} else {
			Ext.getCmp('idSchema').setValue(store.first().get('idSchemaLib'));
		}
	});
	
};

function popupStandardSchemaHelp() {
	if (Ext.getCmp('popupStandardSchemaHelp')) return;
	new Ext.Window({
		height : 250,
		id : 'popupStandardSchemaHelp',
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
				Ext.getCmp('popupStandardSchemaHelp').close();
			}
		} ],
		width : 665
	}).show(this);
};

