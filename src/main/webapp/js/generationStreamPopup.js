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

var generationStreamType = 2;
function popupGenerationStream(record, add) {
		
	var storeStreamType = new Ext.data.ArrayStore({
		data : [ [ '1', _streamType['XML'] ], [ '2', _streamType['XMLEXI'] ],
				[ '3', _streamType['flatFileFixedPosition'] ],
				[ '4', _streamType['flatFileDelimited'] ],
				[ '5', _streamType['JSON'] ], [ '6', _streamType['EXCEL'] ] ],
		fields : [ 'idStreamType', 'name' ]
	});
	var x1, x2, x3, y1, y2, y3;
	new Ext.Window(
			{
				height : 520,
				width : 540,
				id : 'popupGenerationStream',
				bodyStyle:{"background-color":"#ffffff;padding:10px;"},
				layout : 'absolute',
				modal : true,
				resizable : false,
				title : _label['generationStream'],
				items : [ {
					xtype : 'form',
					waitMsgTarget: true,
					layout : 'absolute',
					frame : false,
					border: false,
					url : 'controller.validateForm.json',
					items : [
					         //first row					
					         { 	xtype : 'fieldset',
					        	 height : 80,
					        	 width : 510,
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
					        	        	  inputValue : true,
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
					        	 height : 80,
					        	 width : 510,
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
					        	        			  Ext.getCmp('textFieldDelimiter').setValue(
					        	        			  "");
					        	        			  if (Ext.getCmp("idStreamType").getValue() == 4) {
					        	        				  Ext.getCmp("delimiter").setDisabled(
					        	        						  false);

					        	        			  } else {
					        	        				  Ext.getCmp("delimiter").setDisabled(
					        	        						  true);
					        	        				  Ext.getCmp('textFieldDelimiter')
					        	        				  .setDisabled(true);
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
					        	        	  name : 'delimiter',					        	        	  
					        	        	  x : 160,
					        	        	  xtype : 'radiogroup',
					        	        	  width : 295,
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['delimiter'],
					        	        	  name : 'delimiter',
					        	        	  items : [ {
					        	        		  boxLabel : ';',
					        	        		  inputValue : 1,
					        	        		  name : 'delimiterOption',
					        	        		  checked : true
					        	        	  }, {
					        	        		  boxLabel : '|',
					        	        		  inputValue : 2,
					        	        		  name : 'delimiterOption'
					        	        	  }, {
					        	        		  boxLabel : 'Tab',
					        	        		  inputValue : 3,
					        	        		  name : 'delimiterOption'
					        	        	  }, {
					        	        		  boxLabel : 'Other',
					        	        		  inputValue : 4,
					        	        		  name : 'delimiterOption'
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
					        	        	  x : 435,
					        	        	  y : 22,
					        	        	  xtype : 'textfield',
					        	        	  width : 35,
					        	        	  minLength: 1,
					        	        	  maxLength: 1,
					        	        	  enforceMaxLength: true
					        	          }
					        	          ]
					         },

					         //third row - 1st column
					         { 	xtype : 'fieldset',
					        	 height : 80,
					        	 width : 510,
					        	 width : 190,
					        	 y : 190,
					        	 title : _label['database'],
					        	 layout : 'absolute',
					        	 items : [							
					        	          {  
					        	        	  displayField : 'name',
					        	        	  id : 'idInputDatabase',
					        	        	  name : 'idInputDatabase',					        	        	  
					        	        	  labelAlign : 'top',
					        	        	  fieldLabel : _label['inputDatabase'],
					        	        	  queryMode : 'local',
					        	        	  value : record.get('idInputDatabase') == 0 ? '' : record.get('idInputDatabase'),
					        	        			  store : storeDatabases,
					        	        			  triggerAction : 'all',
					        	        			  valueField : 'idDatabase',
					        	        			  x : 5,
					        	        			  xtype : 'combo',
					        	        			  width : 160,
					        	        			  y : 0
					        	          }
					        	          ]
					         },

					         //fourth row
					         {
					        	 height : 150,
					        	 id : 'description',
					        	 name : 'description',
					        	 submitValue : false,
					        	 value : record.get('description'),
					        	 xtype : 'htmleditor',
					        	 labelAlign : 'top',
					        	 fieldLabel : _label['description'],
					        	 y : 280,
					        	 enableFontSize : false,
					        	 width : 510
					         },
					         {
									text : _message['save'],
									x : 150,
									y : 440,
									xtype : 'button',
									width : 100,					
									handler : function() {
										var form = this.up('form').getForm();
			                    		enableOrDisableFields(false);
			                    		form.submit( {
			                    			waitMsg: _message["waitMessage"],
			                    			params : {schemaType : 'validationStreamGeneration'},
			                    			success : function(form, action) {
			                    				
			                    				record.set('name', Ext.getCmp('name')
			                    						.getValue());
			                    				record.set('description', Ext.getCmp(
			                    				'description').getValue());
			                    				record.set('idApplication', Ext.getCmp(
			                    				'idApplication').getValue());
			                    				record.set('idInputDatabase', Ext.getCmp(
			                    				'idInputDatabase').getValue());
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

			                    				if (Ext.getCmp('idActiveCheckBox').getValue()) {
			                    					record.set('isActive', 1);
			                    				} else {
			                    					record.set('isActive', 0);
			                    				}

			                    				record.set("idSchemaType", generationStreamType);
			                    				if(add) {
			                    					record.set('publishToDb', false);
			                    					record.set('inputToDb', false);
			                    					generationStreamGrid.store.insert(0, record);
			                    				}								
			                    				generationStreamGrid.store.sync();
			                    				Ext.getCmp('popupGenerationStream').close();
			                    			},
			                    			failure : function(form, action) {
			                    				enableOrDisableFields(true);
			                    			}
			                    		});
									}
					         },
					         {
					        	 text : _message['cancel'],
					        	 x : 255,
					        	 y : 440,
					        	 xtype : 'button',
					        	 width : 100,					
					        	 handler: function(){
								 Ext.getCmp('popupGenerationStream').close();
							}
					   }]

				}]
			}).show();

	if (record.get("idStreamType") == 4) {

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