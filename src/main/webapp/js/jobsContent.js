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

jobsDataStores = {
	appsStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'idApplication', type : 'int'}, {name : 'name', type : 'string'} ],
		idProperty : 'idApplication',
		proxy: {
			type: 'ajax',
			url : './applicationsRead.json',
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	schemasStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'idSchema', type : 'int'}, {name : 'name', type : 'string'},
			{name : 'idApplication', type : 'int'} ],
		idProperty : 'idSchema',
		proxy: {
			type: 'ajax',
			url : './schemasRead.json',
			extraParams: {
	            idSchemaType: 1
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	generatedSchemasStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'idSchema', type : 'int'}, {name : 'name', type : 'string'},
			{name : 'idApplication', type : 'int'} ],
		idProperty : 'idSchema',
		proxy: {
			type: 'ajax',
			url : './schemasRead.json',
			extraParams: {
	            idSchemaType: 2
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	allSchemasStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'idSchema', type : 'int'}, {name : 'name', type : 'string'},
			{name : 'idApplication', type : 'int'} ],
		idProperty : 'idSchema',
		proxy: {
			type: 'ajax',
			url : './schemasRead.json',
			extraParams: {
	            idSchemaType: 1
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	connectionsStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'id', type : 'int'}, {name : 'name', type : 'string'}, {name : 'idConnType', type : 'int'} ],
		idProperty : 'id',
		proxy: {
			type: 'ajax',
			url : './connectionsRead.json',
			reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	schedulerStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'id', type : 'int'}, {name : 'name', type : 'string'} ],
		idProperty : 'id',
		proxy: {
			type: 'ajax',
			url : './tasksRead.json',
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	}),
	allSchemaTriggersStore : new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields : [ {name : 'idEventTrigger', type : 'int'}, {name : 'name', type : 'string'},
			{name : 'systemType', type : 'boolean'} ],
		idProperty : 'idEventTrigger',
		proxy: {
			type: 'ajax',
			url : './eventTriggerRead.json',
			extraParams:{		
				idEventTrigger: "",
				isAllTrigger: true
	        },
	        reader: {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	})
}

jobsRenderers = {
	appRenderer : function(value) {
		var st = jobsDataStores.appsStore;
		var rec = st.getAt(st.findExact('idApplication', value));
		return rec == null ? '' : rec.get('name');
	},
	schemasRenderer : function(value) {
		var st = jobsDataStores.schemasStore;
		var rec = st.getAt(st.findExact('idSchema', value));
		if(rec)
			return rec == null ? '' : rec.get('name');
		else
			return '';
	},
	generatedSchemasRenderer : function(value) {
		var st = jobsDataStores.generatedSchemasStore;
		var rec = st.getAt(st.findExact('idSchema', value));
		if(rec)
			return rec == null ? '' : rec.get('name');
		else
			return '';
	},
	connectionsRenderer : function(value) {
		var st = jobsDataStores.connectionsStore;
		var rec = st.getAt(st.findExact('id', value));
		return rec == null ? '' : rec.get('name');
	},
	schedulerRenderer : function(value) {
		var st = jobsDataStores.schedulerStore;
		var rec = st.getAt(st.findExact('id', value));
		return rec == null ? '' : rec.get('name');
	},
	triggerRenderer: function(value) {
		var st = jobsDataStores.allSchemaTriggersStore;
		var rec = st.getAt(st.findExact('idEventTrigger', value));
		return rec == null ? '' : rec.get('name');
	}
}

var editorJobs = Ext.create('Ext.grid.plugin.RowEditing', {
	clicksToEdit : 2,
	listeners : {
		afteredit : function() {
			jobsGrid.store.sync();
		},
		beforeedit : function(roweditor, e) {
			updateSchemasByApp(jobsGrid.getSelectionModel().getSelection()[0]);
			updateGeneratedSchemasByApp(jobsGrid.getSelectionModel().getSelection()[0]);
		}
	}
});

function updateSchemasByApp(record) {
	if (record) {
		var appId = record.get('idApplication');
		jobsDataStores.schemasStore.filterBy(function(localRec) {
			return (localRec.get('idApplication') == appId);			
		})
	}
}
function updateGeneratedSchemasByApp(record) {
	if (record) {
		var appId = record.get('idApplication');
		jobsDataStores.generatedSchemasStore.filterBy(function(localRec) {
			return (localRec.get('idApplication') == appId);			
		})
	}
}
function updateSchemasByAppAndResetValue(record) {
	if (record) {
		var appId = record.get('idApplication');
		Ext.getCmp('jobsSchemasPopupFieldId').setValue("");
		jobsDataStores.schemasStore.filterBy(function(localRec) {
			return (localRec.get('idApplication') == appId);			
		})
	}
}
function updateConnectionsBySchema(idConnType) {
	Ext.getCmp('jobsConnectionsPopupFieldId').setValue("");
		jobsDataStores.connectionsStore.filterBy(function(localRec) {
		return (localRec.get('idConnType') == idConnType);
	})
}
var jobsGrid = new Ext.grid.Panel({
			columnLines : true,
			columns : [{dataIndex : 'id', header : 'Id', sortable : true, width : 50},
					{dataIndex : 'name', header : _label['name'], sortable : true, width : 140, editor: {xtype:'textfield'}},
					{dataIndex : 'description', header : _label['description'], sortable : true, width : 140, editor: {xtype:'textfield'}},
					{dataIndex : 'idApplication', header : _label['application'], sortable : true, width : 150, renderer : jobsRenderers.appRenderer,
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', store: jobsDataStores.appsStore, valueField: 'idApplication',
							listeners : {
								select : function(combo, records) {
									updateSchemasByApp(records[0]);
									updateGeneratedSchemasByApp(records[0]);
								}
							}
						}
					},
					{dataIndex : 'idSchema', header : _label['customValidation'], sortable : true, width : 140, renderer : jobsRenderers.schemasRenderer,
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', valueField: 'idSchema', store: jobsDataStores.schemasStore,
							listeners : {
								expand : function(combo, opts) {
									var f = function() {
										updateSchemasByApp(jobsGrid.getSelectionModel().getSelection()[0]);
									}
									Ext.Function.defer(f, 10);
								}
							}
					}},
					{dataIndex : 'idSchema', header : _label['generationStream'], sortable : true, width : 140, renderer : jobsRenderers.generatedSchemasRenderer, 
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', valueField: 'idSchema', store: jobsDataStores.generatedSchemasStore,
							listeners : {
								expand : function(combo, opts) {
									var f = function() {
										updateGeneratedSchemasByApp(jobsGrid.getSelectionModel().getSelection()[0]);
									}
									Ext.Function.defer(f, 10);
								}
							}
					}},
					{dataIndex : 'idConnection', header : _label['connections'], sortable : true, width : 140, renderer : jobsRenderers.connectionsRenderer,
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', store: jobsDataStores.connectionsStore, valueField: 'id'}},
					{dataIndex : 'idScheduler', header : _label['planner'], sortable : true, width : 140, renderer : jobsRenderers.schedulerRenderer,
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', store: jobsDataStores.schedulerStore, valueField: 'id'}},
					{dataIndex : 'idEventTrigger', header : _label['trigger'], sortable : true, width : 140, renderer : jobsRenderers.triggerRenderer,
							editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'name', store: storeSchemaTriggers, valueField: 'idEventTrigger'}},							
					{dataIndex : 'isActive',
						header : _label['active'],
						sortable : true,
						width : 50,
						renderer : function(value, cell, record, rowIndex,
								colIndex, store) {
							var res = '<input type="checkbox"';
							if (eval(value))
								res += 'checked="checked"';
							res += 'onclick="activateJobFunc(\'' + record.data.id + '\', \''+ record.data.idConnection + '\', \'' + record.data.idScheduler + '\', this)"';
							res += '>';
							return res
						}
					}
			],
			frame : false,
			id : 'jobsGridId',
			plugins: [editorJobs],
			store : new Ext.data.JsonStore( {
				autoLoad : false,
				fields : [{name: 'id', type: 'int', defaultValue: 0}, {name: 'name', type: 'string'}, {name: 'description', type: 'string'},
					{name: 'idApplication', type: 'int'}, {name: 'idSchema', type: 'int'},
					{name: 'idConnection', type: 'int'}, {name: 'idScheduler', type: 'int'},
					{name: 'isActive', type: 'int'}, {name: 'isStreamGenerated', type: 'int', defaultValue: 0},
					{name: 'idEventTrigger', type: 'int'}
				],
				proxy : {
					type : 'ajax',
			        api : {
						read : './jobsRead.json',
						create : './jobsCreate.json',
						update : './jobsUpdate.json?_method=put',
						destroy : './jobsDestroy.json?_method=delete'
					},
					reader : {
						type : 'json',
						root : 'results'
					},
					extraParams:{		
						jobId: ""            
			        },
					listeners : {
						exception : function(proxy, response, operation) {
							if (response) {
								var responseObj = Ext.decode(response.responseText);
								if (responseObj.message.indexOf('Error') != -1) {
									Ext.Msg.alert("", _error['connectionError']);
								} else {
									App.setAlert(false, responseObj.message);
									if (operation.action == 'create' || operation.action == 'update') {
										jobsGrid.store.load();
									} else {
										jobsGrid.store.remove();
									}
								}
							}
						}
					}
				},
				listeners : {
					write : function(store, operation) {
						if (operation.response.responseText) {
							var responseObj = Ext.decode(operation.response.responseText);
							App.setAlert(true, responseObj.message);
							if (operation.action == 'update') {
								store.load();
							}
						}
					}
				}  
			}),
			title : _label['jobs']
		}
);

function activateJobFunc(jobId, idConnection, idScheduler, checkbox) {
	Ext.Ajax.request( {
		params : {
			jobId : jobId,
			isActive : (eval(checkbox.checked) ? 1 : 0)
		},
		url : './jobsSetIsActive.json',
		success: function (result) {
			recordResult = Ext.JSON.decode(result.responseText);
			App.setAlert(eval(recordResult.success), recordResult.msg);
		}
	});
};

function addJob() {
	var record = new jobsGrid.store.model();
	editorJobs.cancelEdit();
	popupJob(record, true);
}

function editJob() {
    var record = jobsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
    editorJobs.cancelEdit();
    popupJob(record, false);
};

function deleteJob() {
    var record = jobsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.MessageBox.confirm('Delete Jobs?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	jobsGrid.store.proxy.extraParams.jobId = record.data.id;
        	jobsGrid.store.remove(record);
        	jobsGrid.store.sync();
        }
    });
};

function popupJob(record, add) {
    new Ext.Window({
		height: 320,
		id : 'jobsPopupWindow',
		bodyStyle:{"background-color":"#ffffff"},
		items : [ {
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
			        {value: _label['name'] , x: 10 , xtype: 'displayfield', y: 10},
			        {xtype: 'textfield', id: 'jobsNamePopupFieldId', name : 'jobsNamePopupFieldId', value: record.get('name'), x: 10, width: 150 , y: 30}, 
			        {value: _label['application'] , x: 180 , xtype: 'displayfield' , y: 10},			
			        {xtype: 'combo', id: 'jobsAppsPopupFieldId', name : 'jobsAppsPopupFieldId', value: record.get('idApplication'), x: 180, width: 200 , y: 30, forceSelection : true,
			        	displayField : 'name', queryMode: 'local', valueField : 'idApplication', store : jobsDataStores.appsStore, disabled: record.get('idEventTrigger') == 0 ? false : true,
			        			listeners : {
			        				select : function(combo, records) {
			        					updateSchemasByAppAndResetValue(records[0]);
			        					if(Ext.getCmp('rbCustomStream').getValue())
			        						updateConnectionsBySchema(1);
			        					else
			        						updateConnectionsBySchema(2);
			        					Ext.getCmp('jobsConnectionsPopupFieldId').setDisabled(false);
			        				}
			        			}
			        },
			        {value: _label['planner'] , x: 400 , xtype: 'displayfield' , y: 10},
			        {xtype: 'combo', id: 'jobsSchedulerPopupFieldId', name : 'jobsSchedulerPopupFieldId', value: record.get('idScheduler'), x: 400, width: 200 , y: 30, forceSelection : true,
			        	displayField : 'name', queryMode: 'local', valueField : 'id', store : jobsDataStores.schedulerStore},
			        	{ xtype: "radio", x : 10, y : 90, fieldLabel: "", id: "rbCustomStream", name: 'rbCustomStream', submitValue : false,  disabled: record.get('idEventTrigger') == 0 ? false : true, checked: record.get('isStreamGenerated') == 0 ? true : false,
			        			listeners: {
			        				change : function(el,val) {	
			        					if(val) {
			        						updateConnectionsBySchema(1);
			        						Ext.getCmp('jobsGeneratedSchemasPopupFieldId').setDisabled(true);
			        						Ext.getCmp('jobsSchemasPopupFieldId').setDisabled(false);
			        						Ext.getCmp('jobsGeneratedSchemasPopupFieldId').setValue("");
			        					} else {
			        						updateConnectionsBySchema(2);
			        						Ext.getCmp('jobsGeneratedSchemasPopupFieldId').setDisabled(false);
			        						Ext.getCmp('jobsSchemasPopupFieldId').setDisabled(true);
			        						Ext.getCmp('jobsSchemasPopupFieldId').setValue("");
			        					}
			        				}
			        			}
			        	},
			        	{value : _label['customValidation'], x : 40, xtype : 'displayfield', y : 70 },
			        	{xtype: 'combo', id: 'jobsSchemasPopupFieldId', name : 'jobsSchemasPopupFieldId', value: record.get('isStreamGenerated') == 0 ? record.get('idSchema') : '', x: 40, width: 200 , y: 90, forceSelection : true,
			        			disabled: record.get('idEventTrigger') == 0 ? (record.get('isStreamGenerated') == 0 ? false : true) : true, displayField : 'name', queryMode: 'local', valueField : 'idSchema', store : jobsDataStores.schemasStore,  
			        					listeners : {
			        						expand : function(combo, opts) {
			        							var f = function() {
			        								var appId = Ext.getCmp('jobsAppsPopupFieldId').getValue();
			        								jobsDataStores.schemasStore.filterBy(function(localRec) {
			        									return (localRec.get('idApplication') == appId);			
			        								})
			        							}
			        							Ext.Function.defer(f, 10);
			        						}
			        					}
			        	},
			        	{ xtype: "radio", y : 90, x : 260, fieldLabel: "", id: "rbGeneratedSchema", submitValue : false, disabled: record.get('idEventTrigger') == 0 ? false : true, name: 'rbCustomStream',  checked:record.get('isStreamGenerated') == 1 ? true : false},
			        	{value : _label['generationStream'], x : 290, xtype : 'displayfield', y : 70 },						
			        	{xtype: 'combo', id: 'jobsGeneratedSchemasPopupFieldId', name: 'jobsGeneratedSchemasPopupFieldId', value: record.get('isStreamGenerated') == 1 ? record.get('idSchema') : '', x: 290, width: 200 , y: 90, forceSelection : true,
			        			disabled: record.get('idEventTrigger') == 0 ? (record.get('isStreamGenerated') == 1 ? false : true) : true, displayField : 'name', queryMode: 'local', valueField : 'idSchema', store : jobsDataStores.generatedSchemasStore, 
			        					listeners : {
			        						expand : function(combo, opts) {
			        							var f = function() {
			        								var appId = Ext.getCmp('jobsAppsPopupFieldId').getValue();
			        								jobsDataStores.generatedSchemasStore.filterBy(function(localRec) {
			        									return (localRec.get('idApplication') == appId);			
			        								})
			        							}
			        							Ext.Function.defer(f, 10);
			        						}
			        					}
			        	},
			        	{value: _label['connections'] , x: 10 , xtype: 'displayfield' , y: 130},			
			        	{xtype: 'combo', id: 'jobsConnectionsPopupFieldId', name : 'jobsConnectionsPopupFieldId', value: record.get('idConnection'), x: 10, width: 200 , y: 150, forceSelection : true,
			        		disabled: record.get('idEventTrigger') == 0 ? add : true, displayField : 'name', queryMode: 'local', valueField : 'id', store : jobsDataStores.connectionsStore },
			        		{value: _label['trigger'] , x: 250 , xtype: 'displayfield' , y: 130},
			        		{xtype: 'combo', id: 'jobsEventTrigger', name: 'jobsEventTrigger', value: record.get('idEventTrigger') == 0 ? "" : record.get('idEventTrigger'), x: 250, width: 180 , y: 150, 
			        				displayField : 'name', queryMode: 'local', valueField : 'idEventTrigger', store : storeSchemaTriggers,
			        				listeners : {
			        					select : function(combo, opts) {
			        						Ext.getCmp('jobsAppsPopupFieldId').setDisabled(true);
			        						Ext.getCmp('jobsConnectionsPopupFieldId').setDisabled(true);
			        						Ext.getCmp('jobsSchemasPopupFieldId').setDisabled(true);
			        						Ext.getCmp('jobsGeneratedSchemasPopupFieldId').setDisabled(true);
			        						Ext.getCmp('rbGeneratedSchema').setDisabled(true);
			        						Ext.getCmp('rbCustomStream').setDisabled(true);
			        					}
			        				}
			        		},
			        		{value: _label['description'] , x: 10 , xtype: 'displayfield', y: 180},
			        		{xtype: 'textfield', id: 'jobsDescPopupFieldId', name : 'jobsDescPopupFieldId', value: record.get('description'), x: 10, width: 590 , y: 200},
			        		{
								text : _message['save'],
								x : 205,
								xtype : 'button',
								width : 100,
								y : 240,
								handler : function() {
									var form = this.up('form').getForm();

	        		        		   form.submit( {
	        		        			   waitMsg: _message["waitMessage"],
	        		        			   params : {schemaType : 'validationJobs'},
	        		        			   success : function(form, action) {				

	        		        				   if(Ext.getCmp('jobsNamePopupFieldId').getValue() == '') {
	        		        					   App.setAlert(false, _alert['nameFieldRequired']);
	        		        					   return;
	        		        				   } 

	        		        				   if(!Ext.getCmp('jobsEventTrigger').getValue()) {						

	        		        					   if(!Ext.getCmp('jobsAppsPopupFieldId').getValue()) {
	        		        						   App.setAlert(false, _error['applicationRequired']);
	        		        						   return;
	        		        					   } else if(!Ext.getCmp('jobsSchedulerPopupFieldId').getValue()) {
	        		        						   App.setAlert(false, _error['plannerRequired']);
	        		        						   return;
	        		        					   } else if(Ext.getCmp('rbCustomStream').getValue()) {							
	        		        						   if(!Ext.getCmp('jobsSchemasPopupFieldId').getValue()) {
	        		        							   App.setAlert(false, _error['streamValidationRequired']);
	        		        							   return;	
	        		        						   } else if(!Ext.getCmp('jobsConnectionsPopupFieldId').getValue()) {
	        		        							   App.setAlert(false, _error['connectionRequired']);
	        		        							   return;
	        		        						   }
	        		        					   } else if(!Ext.getCmp('jobsGeneratedSchemasPopupFieldId').getValue()) {
	        		        						   App.setAlert(false, _error['streamGenerationRequired']);
	        		        						   return;
	        		        					   }
	        		        				   } else if(!Ext.getCmp('jobsSchedulerPopupFieldId').getValue()) {
	        		        					   App.setAlert(false, _error['plannerRequired']);
	        		        					   return;
	        		        				   }

	        		        				   record.set('name', Ext.getCmp('jobsNamePopupFieldId').getValue());
	        		        				   record.set('description', Ext.getCmp('jobsDescPopupFieldId').getValue());

	        		        				   if(!Ext.getCmp('jobsEventTrigger').getValue()) {
	        		        					   var val = Ext.getCmp('jobsAppsPopupFieldId').getValue(); 
	        		        					   if (val) record.set('idApplication', val);  
	        		        					   val = Ext.getCmp('jobsSchemasPopupFieldId').getValue(); 
	        		        					   if (val) record.set('idSchema', val);  
	        		        					   val = Ext.getCmp('jobsConnectionsPopupFieldId').getValue(); 
	        		        					   if (val) {
	        		        						   record.set('idConnection', val);
	        		        					   } else {
	        		        						   record.set('idConnection', 0);
	        		        					   }
	        		        					   val = Ext.getCmp('jobsSchedulerPopupFieldId').getValue(); 
	        		        					   if (val) record.set('idScheduler', val);
	        		        					   if(Ext.getCmp('rbCustomStream').getValue()) {
	        		        						   record.set('isStreamGenerated', 0);
	        		        						   val = Ext.getCmp('jobsSchemasPopupFieldId').getValue(); 
	        		        						   if (val) record.set('idSchema', val);
	        		        					   } else {
	        		        						   record.set('isStreamGenerated', 1);
	        		        						   val = Ext.getCmp('jobsGeneratedSchemasPopupFieldId').getValue(); 
	        		        						   if (val) record.set('idSchema', val);
	        		        					   }
	        		        				   } else {
	        		        					   record.set('idApplication', 0);  
	        		        					   record.set('idConnection', 0);
	        		        					   record.set('isStreamGenerated', 0);
	        		        					   record.set('idSchema', 0);
	        		        					   val = Ext.getCmp('jobsSchedulerPopupFieldId').getValue(); 
	        		        					   if (val) record.set('idScheduler', val);						
	        		        					   val = Ext.getCmp('jobsEventTrigger').getValue();					
	        		        					   if (val) {
	        		        						   record.set('idEventTrigger', val);
	        		        					   } else {
	        		        						   record.set('idEventTrigger', 0);
	        		        					   }
	        		        				   }
	        		        				   if (add) {
	        		        					   jobsGrid.store.insert(0, record);
	        		        				   }
	        		        				   jobsGrid.store.sync();
	        		        				   Ext.getCmp('jobsPopupWindow').close();
	        		        			   },
	        		        			   failure : function(form, action) {
	        		        				   //
	        		        			   }
	        		        		   });
								}
					        },
					        {
								text : _message['cancel'],
								x : 340,
								xtype : 'button',
								width : 100,
								y : 240,
								handler : function() {
									Ext.getCmp('jobsPopupWindow').close();		
								}
					        }],
			        		
					}],
			        layout: 'absolute',
			        modal: true,
			        resizable: false,
			        title: _label['jobs'],
			        width: 620
		}).show();

    	if(!add) {
    		if(Ext.getCmp('rbCustomStream').getValue())
    			updateConnectionsBySchema(1);
    		else
    			updateConnectionsBySchema(2);
    		var connId = record.get('idConnection');
    		Ext.getCmp('jobsConnectionsPopupFieldId').setValue(connId);
    	}
    }


function helpJobs() {
	if (Ext.getCmp('popupJobsHelp')) return;	
	new Ext.Window( {
		height : 250,
		id : 'popupJobsHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['jobsHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupJobsHelp').close();
			}
		} ],
		width : 665
	}).show(this);
}