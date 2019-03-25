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
var appId = -1;
var comboApplication = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',
	store: storeApplications,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idApplication'
});

Ext.define('schemaTriggerModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'idEventTrigger', type:'int'},
        {name: 'name', type:'string'},
		{name: 'description', type:'string'},
		{name: 'code', type:'string'}		
    ],	
	idProperty:'idEventTrigger'
});
var storeSchemaTriggers = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'schemaTriggerModel',
		idProperty:'idEventTrigger',
		pageSize: -1,
		fields: ['id', 'name', 'description', 'code'],
		proxy: {
			type: 'ajax',
			api: {
				read    : './eventTriggerRead.json',
				create  : './eventTriggerCreate.json',
				update  : './eventTriggerUpdate.json',
				destroy : './eventTriggerDestroy.json'
			},
			extraParams:{		
				idEventTrigger: ""            
	        },
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty:'idEventTrigger'
			},
			writer: {
				type: 'json',
				writeAllFields:true
			}
		},
		listeners: {
			write: function(store, operation){
				var respText = operation.response.responseText;			
				if (respText) {
						var responseObj = Ext.decode(respText);
						App.setAlert(true,responseObj.message);
						if (responseObj.extraMessage) App.setAlert(true, responseObj.extraMessage);
						if (operation.action == 'update') {
							storeSchemaTriggers.load();
						}
				}
	        }   	
	    }
	});
	storeSchemaTriggers.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeSchemaTriggers.remove();			
		}	
	});
	

var storeEventTriggerStatus = new Ext.data.ArrayStore({
	data : [['0' , _eventStatus['OK']] , ['1' , _eventStatus['KO']] , ['2' , _eventStatus['Warning']]],
	fields: ['idTriggerStatus' , 'name']
});

function activateSchemaFunc(schemaId, bIsActive, schemaTypeId) {	

		var isValid = !bIsActive;
		
		if (bIsActive) {
			Ext.Ajax.request({
				params : {
					idSchema : schemaId
				},
				success : function(result) {
					var recordResult = Ext.JSON.decode(result.responseText);
					if (recordResult.success == "false") {
						isValid = false;
						App.setAlert(false,	_error['schemaActivateError']);
					} else {
						isValid = true;
					}
					
					if (isValid == true) {
						Ext.Ajax.request({
							params : {
								schemaId : schemaId,
								isActive : (eval(bIsActive) ? 1 : 0)
							},
							success : function(result) {
								if(schemaTypeId == 1) {
									storeSchemas.load();
								} else if(schemaTypeId == 2) {
									storeGenerationStream.load();
								} else if(schemaTypeId == 3) {
									storeStandardSchemas.load();
								}
							},
							url : './schemaIsActive.json'
						});
					} else {
						if(schemaTypeId == 1) {
							storeSchemas.load();	
						} else if(schemaTypeId == 2) {
							storeGenerationStream.load();
						} else if(schemaTypeId == 3) {
							storeStandardSchemas.load();
						}						
					}
				},
				url : './schemaValidate.json'
			});
		} else {
			Ext.Ajax.request({
				params : {
					schemaId : schemaId,
					isActive : (eval(bIsActive) ? 1 : 0)
				},
				success : function(result) {
					if(schemaTypeId == 1) {
						storeSchemas.load();	
					} else if(schemaTypeId == 2) {
						storeGenerationStream.load();
					} else if(schemaTypeId == 3) {
						storeStandardSchemas.load();
					}
				},
				url : './schemaIsActive.json'
			});
		}
};

function applicationName(idApplication) {
	if(idApplication <= 0) {
		return "";
	}
	if  (storeApplications.getCount()) {
		var rec = storeApplications.getAt(storeApplications.findExact('idApplication' , idApplication));
		return rec == null ? idApplication : rec.get('name');
	} else {
		return _message["error"];
	}
}

var comboDatabase = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',
	store: storeDatabases,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idDatabase'
});

function databaseName(idDatabase) {
	if  (storeDatabases.getCount()) {
		var rec = storeDatabases.getAt(storeDatabases.findExact('idDatabase' , idDatabase));
		return rec == null ? '&nbsp;' : rec.get('name');
	} else {
		return '&nbsp;';
	}
}

var storeStreamType = new Ext.data.ArrayStore({
	data : [['1' , _streamType['XML']] , ['2' , _streamType['XMLEXI']] , ['3' , _streamType['flatFileFixedPosition']],
            ['4' , _streamType['flatFileDelimited']],['5' , _streamType['JSON']], ['6' , _streamType['EXCEL']] ],
	fields: ['idStreamType' , 'name']
});

function streamTypeName(idStreamType) {
	var rec = storeStreamType.getAt(storeStreamType.find('idStreamType' , idStreamType));
	return rec == null ? idStreamType : rec.get('name');
	
}
Ext.define('schemas', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idSchema', type:'int', defaultValue:0}, 
		{name: 'idApplication', type:'int'},
		{name: 'idDatabase', type:'int'},
		{name: 'idInputDatabase', type:'int'},
		{name: 'idLinkedSchema', type:'int'},
		{name: 'idStreamType', type:'string'},
		{name: 'delimiter', type:'string'},
		{name: 'name', type:'string'},
		{name: 'description', type:'string'},
		{name: 'startDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name: 'endDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name: 'isActive' , type:'int'},
		{name: 'version' , type:'string'},
		{name: 'publishToDb', type: 'boolean'},
		{name: 'isForecasted', type: 'boolean'},
		{name: 'isIndexedIncrement', type: 'boolean'},
		{name: 'noHeader', type: 'boolean'},
		{name: 'isWarnTolerance', type: 'boolean'},
		{name: 'inputToDb', type: 'boolean'},
        {name: 'loadedXSD', type: 'boolean', defaultValue: false},
        {name: 'isPlanned', type: 'boolean'},
        {name: 'isValid' , type:'int', defaultValue: 1},
		{name: 'isInValid' , type:'int', defaultValue: 1},
		{name: 'isWarning' , type:'int', defaultValue: 1},
        {name: 'plannedName', type: 'int'},
        {name: 'idSchemaType', type: 'int', defaultValue: 1},
        {name: 'service', type: 'string', defaultValue: '-1'},
        {name: 'chrDelimiter', type: 'char'},
        {name: 'idTriggerStatus', type: 'string',defaultValue: '-1'},
        {name: 'idEventTrigger', type: 'int', defaultValue: '0'},
        {name: 'idValidationDatabase', type: 'int'},
        {name: 'isEventTrigger', type: 'boolean', defaultValue: false},
        {name: 'isAvailable', type: 'int'},
        {name: 'isMongoDB', type: 'boolean', defaultValue: false}
    ],	
	idProperty:'idSchema'
});

var storeSchemas = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'schemas',
	pageSize: getPageSize(),
	idProperty:'idSchema',
	proxy: {
		type: 'ajax',
        api: {
			read    : './schemasRead.json',
			create  : './schemasCreate.json',
			update  : './schemasUpdate.json',
			destroy : './schemasDestroy.json'
		},
		extraParams: {
            idSchemaType: "1,2",
            appIds: -1
        },
        reader: {
            type: 'json',
            root: 'results',
            totalProperty: 'total',
			successProperty: "success",
			messageProperty: 'message'
		},
		writer: {
			type: 'json',
			writeAllFields:true
		}
	},
	listeners: {
		write: function(store, operation){
			if (operation.response.responseText) {
				var responseObj = Ext.decode(operation.response.responseText);
				App.setAlert(true,responseObj.message);
				if (responseObj.extraMessage) App.setAlert(true, responseObj.extraMessage);
				if(operation.action == 'create' || operation.action == 'destroy'){
					schemasGrid.store.loadPage(1);
				}
				
				if(operation.action == 'create') {
					Ext.getCmp('filterApplication').setValue(schemaStoreApplications.getAt('0').get('idApplication'));
					Ext.Msg.show({
						title:_message['wizard'],
						msg: _message['schemaCreateField'],
						buttons: Ext.Msg.OKCANCEL,
						fn: function ( answer ) {
							if ( answer == 'ok') {
								var record = Ext.getCmp('schemas').getStore().getNodeById('editSchemaFields');
								Ext.getCmp('schemas').getSelectionModel().select(record)
                				schemasGrid.getView().select(0);
								editSchemaFields();
							}
						}
					});
				}
			}               
        }   	
    }
});
storeSchemas.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.message.indexOf('Error')!=-1){
				Ext.Msg.alert("" , _error['connectionError']);
			} else{
				App.setAlert(false , responseObj.message);
				if (operation.action == 'create') {
					storeSchemas.load();
	        	} else {
	        		storeSchemas.remove();
	        	}
			}
		}
	}	
});
var isEditSuccess = false;
var editorSchemas = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
        clicksToEdit: 2,
        validationScheme : 'validationCustomStream',
        excludeParams: 'idDatabase',
        validateEditSuccess : function() {
        	isEditSuccess = true;
        	schemasGrid.store.sync();
        },
        listeners: {
        
			beforeedit: function(editor,e,eOpts ) {
				schemasGrid.columns[7].hide();
			},
			canceledit: function(grid, eOpts) {				
				schemasGrid.columns[7].setVisible(true);
			},	
			afteredit: function() {
				if(isEditSuccess == true) {
					isEditSuccess = false;
					schemasGrid.columns[7].setVisible(true);
				}				
			}
		}			
    });

var columnsSchemas = [
	{dataIndex: 'idSchema' ,  header: 'Id' ,   sortable: true , width: 70},
	{dataIndex: 'name' , editor: {xtype:'textfield'} , header: _label['name'] ,   sortable: true , width: 150},
	{dataIndex: 'idApplication',editor: {xtype:'combobox', displayField: 'name', forceSelection: true, queryMode: 'local', 	
	store: storeApplications, triggerAction: 'all', typeAhead: true, valueField: 'idApplication'} , header: _label['application'] ,  renderer: function(value){return applicationName(value);} , sortable: true , width: 150},
	{dataIndex: 'idDatabase', editor: {xtype:'combobox', displayField: 'name', forceSelection: true, queryMode: 'local',
	store: storeDatabases, triggerAction: 'all', typeAhead: true, valueField: 'idDatabase'} , header: _label['database'] ,   renderer: function(value){return databaseName(value);} , sortable: true , width: 150},
	{dataIndex: 'idStreamType' , header: _label['streamType'] , renderer: function(value){return streamTypeName(value);} , sortable: true , width: 150},
	{dataIndex: 'startDate' , xtype: 'datecolumn',  header: _label['startDate'], width: 130 , field: {xtype: 'datefield',format: 'd/m/Y'}},
	{dataIndex: 'endDate' , xtype: 'datecolumn',  header: _label['endDate'], width: 130 , field: {xtype: 'datefield',format: 'd/m/Y'}},
	{dataIndex: 'isActive' , header : _label['active'], width: 50 , sortable: true, renderer : function(value, cell, record, rowIndex,
			colIndex, store) {
		var res = '<input type="checkbox"';
		if (eval(value))
			res += 'checked="checked"';
		res += 'onclick="activateSchemaFunc(\'' + record.data.idSchema + '\', this.checked, 1)"';
		res += ' >';
		return res;
	}},
	{dataIndex: 'idSchemaType' , header: _label['linked'], width: 70, renderer: function(value, cell, record, rowIndex, colIndex, store) {
		if(record.data.idSchemaType == 2) {
			return '<img src=\'./images/linked.png\' />';
		} else {
			return '<img src=\'./images/unlinked.png\' />';
		}
		return '';
	}
    }
];

var schemaStoreApplications = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'applications',
	idProperty:'idApplication',
	proxy: {
		type: 'ajax',
        api: {
			read    : './applicationsRead.json'
		},		
		extraParams:{		
			idApplication: ""            
        },
        reader: {
            type: 'json',
            root: 'results',
			successProperty: 'success',
			messageProperty: 'message'
		}
	}
});
schemaStoreApplications.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.message.indexOf('Error')!=-1){
					Ext.Msg.alert("" , _error['connectionError']);
				} else{
					App.setAlert(false , responseObj.message);
					if (operation.action == 'create' || operation.action == 'destroy') {
						schemaStoreApplications.load();
					}
				}
			}
		}	
});
schemaStoreApplications.on('load',function(store) {
	schemaStoreApplications.insert(0,{idApplication: '-1', name: 'All'});
	
	if(schemaStoreApplications.getCount() > 0) {
		Ext.getCmp('filterApplication').setValue(schemaStoreApplications.getAt('0').get('idApplication'));
		Ext.getCmp('filterGenerationApplication').setValue(schemaStoreApplications.getAt('0').get('idApplication'));
		Ext.getCmp('filterStandardApplication').setValue(schemaStoreApplications.getAt('0').get('idApplication'));
		Ext.getCmp('filterLoadingStreamApplication').setValue(schemaStoreApplications.getAt('0').get('idApplication'));		
	}
});
function getPageSize() {
	
	if(screen.height <= 768)
		return 17;
	else
		return 37;
}
var schemasGrid = new Ext.grid.Panel({
	columnLines: true,
	columns: columnsSchemas,
	frame: false,
	id: 'schemasGrid',
	plugins: [editorSchemas],
	// Migration Change
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	bbar: {
        xtype: 'pagingtoolbar',
        pageSize: getPageSize(),
        store: storeSchemas,
        displayInfo: true,
        plugins: new Ext.ux.ProgressBarPager()
    },
	store: storeSchemas,
	dockedItems: [{
        dock: 'top',
        xtype: 'toolbar',
        items: [
        {xtype: 'label', text: _label['selectApplications']},
        {displayField:'name' , forceSelection: true , id: 'filterApplication' , name: 'filterApplication', multiSelect:true, store: schemaStoreApplications , triggerAction: 'all', valueField: 'idApplication' , xtype: 'combo', width: 150 , submitValue: false,
        	
			listeners: {
				change: function(combo, records) {
					
					var appIds = Ext.getCmp('filterApplication').getValue();
					
					var strAppId = appIds.toString();
					if(strAppId == '') {
						Ext.getCmp('filterApplication').setValue(-1);
					} else if(strAppId.indexOf("-1") != -1 && appIds.length > 1) {
						strAppId = strAppId.replace("-1,", "");
						var val = new Array();
						
						for(count = 0; count < appIds.length; count++){
							if(appIds[count] == '-1') {
								appIds[count] = 0;
							} 
						}
						Ext.getCmp('filterApplication').setValue(appIds);
						return;
					}
					
					schemasGrid.store.proxy.extraParams.appIds = appIds.toString();
					schemasGrid.store.loadPage(1);
				} 
			} 
		}
        ]
	}],
	title: _label['customValidation']
});

function schemasList() {
	storeSchemas.load();
	Ext.getCmp('content').layout.setActiveItem('schemasGrid');
};

function addSchema() {
	if (storeApplications.getCount() != 0 ) {
		var record = new schemasGrid.store.model(); //recordType
		record.set('idApplication' , storeApplications.getAt(0).get('idApplication')); 
		record.set('idStreamType' , 1);
		if(storeSchemaTriggers.getCount() != 0 )
			record.set('idEventTrigger' , storeSchemaTriggers.getAt(0).get('idEventTrigger'));
		record.set('idTriggerStatus','0');		
		editorSchemas.cancelEdit();
		popupSchemas(record , true);
	} else {
		App.setAlert(false , _alert['createApp']);
	}
};

function deleteSchema() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.Msg.show({
        title:_message['del']+' Schema?',
        msg: _message['delMsg']+" '"+ record.get('name')+"'?",
        buttons: Ext.Msg.YESNO,
        fn: function ( answer ) {
            if ( answer == 'yes') {
                schemasGrid.store.remove(record);
                schemasGrid.store.sync();
                //schemasGrid.store.loadPage(1);
            }
        },
        icon:  Ext.MessageBox.QUESTION});
};

function editSchemaFields() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];

    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
	editorSchemas.cancelEdit();
    schemaFieldsTree.store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + record.get('idSchema');
    //schemaFieldsTree.store.load();
	Ext.Ajax.request({
		params : {
			idSchema : record.get('idSchema')
		},
		success: function (result) {
			jsonResp = Ext.JSON.decode(result.responseText);
			treestore.setRootNode({
				id: '0',
				text: jsonResp.name,
				leaf: false
				//expanded: true // If true, the store load's itself immediately; we want that to happen!
			});
			schemaFieldsTree.expandAll();
       },
	   url : './getSchemaNameById.json'
	});

	Ext.getCmp('content').layout.setActiveItem('schemaFieldsTree');
};
function editSchema() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorSchemas.cancelEdit();
    
    var rowIndex = storeSchemas.findExact("idSchema", record.get('idSchema'));	
	record = storeSchemas.getAt(rowIndex);
    
    popupSchemas(record , false);
};

function duplicateSchema() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
	Ext.Ajax.request( {
		params : {
			primarySchemaId : record.get('idSchema')
		},
		success: function (result) {
			recordResult = Ext.JSON.decode(result.responseText);
			if (recordResult.success == "true") {
				schemasGrid.store.load();
				App.setAlert(true, _message['successDuplicateSchema']);
			} else {
				App.setAlert(false, recordResult.errMsg);
			}
		},
		url : './schemaDuplicate.json'
	});
};

function validateDatastream() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorSchemas.cancelEdit();
    popupValidateDatastream(record , 'add');
};

function datastreamReceived() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorSchemas.cancelEdit();
    popupDatastreamReceived(record);
};
function schemaDocuments() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorSchemas.cancelEdit();
    popupSchemaDocuments(record);
};

function showForm() {
	popupSchemaHelp();
};

function schemaImportFromJv() {
    editorSchemas.cancelEdit();
    var idApplication = storeApplications.getAt(0).get('idApplication');
    var uploadContent = {
        xtype: 'form',
        id: 'importForm',
        fileUpload: true,
        frame: false,
        border: false,
        labelWidth: 50,
        items: [
            {name: 'file' , fieldLabel: 'File' , id: 'form-file' , emptyText: _label['selectImportFile'], xtype: 'fileuploadfield' ,
                listeners:{
                    change: function(obj, filename, options){
                        this.fireEvent('upload',  filename);
                        Ext.getCmp("btnImport").enable();
                    }
                },
                width: 475}
        ]
    };

    new Ext.Window({
        id: 'popupImportSchema',
        items: [
            uploadContent,
            {id: 'btnImport',
                name: 'btnImport',disabled: true,
                text: _label["import"], x: 135 , xtype: 'button' , width: 100 , y: 70 ,
                handler: function(){
                    var form = Ext.getCmp('importForm').getForm();
                    if (form.isValid()) {
                        form.submit({
                            url: './schemaImportFromJv.json?idApplication=' + idApplication +'&idSchemaType=1',
                            waitMsg: _message["loadTheFile"],
                            success: function(form, action) {
                                var warMsg = action.result.message;
                                schemasGrid.store.load();
                                if (warMsg != '') {
                                    Ext.MessageBox.show({
                                        title: _message['fileUploaded'],
                                        buttons: Ext.MessageBox.OK,
                                        msg: warMsg,
                                        icon: Ext.MessageBox.WARNING
                                    });
                                }else{
                                    App.setAlert(App.STATUS_OK ,_message['fileUploaded'] );
                                }
                                Ext.getCmp('popupImportSchema').close();
                                schemasList();
                            },
                            failure: function(form, action) {

                                if(action.result.message == "1") {
                                    App.setAlert(_message["error"], _error["invalidImportFileExtError"]);
                                } else if(action.result.message == "2"){
                                    App.setAlert(_message["error"], _error["invalidXSDFileError"]);
                                } else {
                                    App.setAlert(_message["error"], action.result.message);
                                }

                            }
                        });
                    }
                }
            },
            {text: _message["close"] , x: 250 , xtype: 'button' , width: 100 , y: 70 ,
                handler: function(){
                    Ext.getCmp('popupImportSchema').close();
                }
            }
        ],
        layout: 'absolute',
        modal: true,
        resizable: false,
        title: _label['importFile'],
        width: 500,
        height: 140
    }).show();
};
function helpSchema(){
	popupSchemaHelp();
};
function extraCheckSchema() {
	popupSchemaExtraCheck();
};
function schemaTrigger(){
	popupTrigger();
}
