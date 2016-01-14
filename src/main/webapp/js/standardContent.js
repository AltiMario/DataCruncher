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

var comboApplication = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',
	store: storeApplications,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idApplication'
});

function standardStreamTypeName(idStreamType) {
	var rec = storeStandardStreamType.getAt(storeStandardStreamType.find('idStreamType' , idStreamType));
	return rec == null ? idStreamType : rec.get('name');
	
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

var storeStandardStreamType = new Ext.data.ArrayStore({
	data : [[ '7', _streamType['HL7'] ], [ '8', _streamType['SWIFT'] ], [ '9', _streamType['EDI_CICA'] ] ],
	fields: ['idStreamType' , 'name']
});

Ext.define('standardSchemas', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idSchema', type:'int', defaultValue:0}, 
		{name: 'idApplication', type:'int'},
		{name: 'idStreamType', type:'string'},
		{name: 'name', type:'string'},
		{name: 'description', type:'string'},
		{name: 'startDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name: 'endDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name: 'isActive' , type:'int'},
		{name: 'version' , type:'string'},
		{name: 'isPlanned', type: 'boolean'},
        {name: 'isValid' , type:'int', defaultValue: 1},
		{name: 'isInValid' , type:'int', defaultValue: 1},
		{name: 'isWarning' , type:'int', defaultValue: 1},
        {name: 'plannedName', type: 'int'},
        {name: 'idSchemaType', type: 'int', defaultValue: 1},
        {name: 'idSchemaLib', type: 'int'},
        {name: 'idVersionLibrary', type: 'string'}
    ],	
	idProperty:'idSchema'
});
Ext.define('schemasLib', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idSchemaLib', type:'int', defaultValue:0}, 
		{name: 'libType', type:'int'},
		{name: 'version', type:'string'},
		{name: 'defaultNsLib', type:'string'},
		{name: 'libPath', type:'string'},
		{name: 'libName', type:'string'},
		{name: 'libFile' , type: 'string'}
    ],	
	idProperty:'idSchemaLib'
});
var storeStandardSchemas = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'standardSchemas',
	pageSize: getStandardPageSize(),
	idProperty:'idSchema',
	proxy: {
		type: 'ajax',
        api: {
			read    : './schemasRead.json',
			create  : './schemasCreate.json',
			update  : './schemasUpdate.json?_method=put',
			destroy : './schemasDestroy.json?'
		},
		extraParams: {
            idSchemaType: 3,
            appIds: -1
        },
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message',
			totalProperty: 'total'	
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
					standardSchemasGrid.store.loadPage(1);
				}
			}               
        }   	
    }
});
storeStandardSchemas.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.message.indexOf('Error')!=-1){
				Ext.Msg.alert("" , _error['connectionError']);
			} else{
				App.setAlert(false , responseObj.message);
				if (operation.action == 'create') {
					storeStandardSchemas.load();
	        	} else {
	        		storeStandardSchemas.remove();
	        	}
			}
		}
	}	
});
var isEditSuccess = false;
var editorStandardSchemas =  Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
        clicksToEdit: 2,
        validationScheme : 'validationStandardStream',
        validateEditSuccess : function() {
        	isEditSuccess = true;
        	standardSchemasGrid.store.sync();
        },
		listeners: {
	
			beforeedit: function(editor,e,eOpts ) {
				standardSchemasGrid.columns[6].hide();
			},
			canceledit: function(grid, eOpts) {				
				standardSchemasGrid.columns[6].setVisible(true);
			},	
			afteredit: function() {
				if(isEditSuccess == true) {
					isEditSuccess = false;
					standardSchemasGrid.columns[6].setVisible(true);
				}
			}
		}			
    });

var columnsStandardSchemas = [
	{dataIndex: 'idSchema' ,  header: 'Id' ,   sortable: true , width: 150},
	{dataIndex: 'name' , editor: {xtype:'textfield'} , header: _label['name'] ,   sortable: true , width: 150},
	{dataIndex: 'idApplication',editor: {xtype:'combobox', displayField: 'name', forceSelection: true, queryMode: 'local', 	
	store: storeApplications, triggerAction: 'all', typeAhead: true, valueField: 'idApplication'} , header: _label['application'] ,  renderer: function(value){return applicationName(value);} , sortable: true , width: 150},
	/*{dataIndex: 'idDatabase', editor: {xtype:'combobox', displayField: 'name', forceSelection: true, queryMode: 'local',
	store: storeDatabases, triggerAction: 'all', typeAhead: true, valueField: 'idDatabase'} , header: _label['database'] ,   renderer: function(value){return databaseName(value);} , sortable: true , width: 150},*/
	{dataIndex: 'idStreamType' , header: _label['streamType'] , renderer: function(value){return standardStreamTypeName(value);} , sortable: true , width: 150},
	{dataIndex: 'startDate' , xtype: 'datecolumn',  header: _label['startDate'], width: 150 , field: {xtype: 'datefield',format: 'd/m/Y'}},
	{dataIndex: 'endDate' , xtype: 'datecolumn',  header: _label['endDate'], width: 150 , field: {xtype: 'datefield',format: 'd/m/Y'}},
	{dataIndex: 'isActive' , header : _label['active'], width: 50 , sortable: true, renderer : function(value, cell, record, rowIndex,
			colIndex, store) {
		var res = '<input type="checkbox"';
		if (eval(value))
			res += 'checked="checked"';
		res += 'onclick="activateSchemaFunc(\'' + record.data.idSchema + '\', this.checked, 3)"';
		res += ' >';
		return res 
	}}
];

var standardSchemasGrid = new Ext.grid.Panel({
	columnLines: true,
	columns: columnsStandardSchemas,
	frame: false,
	id: 'standardSchemasGrid',
	plugins: [editorStandardSchemas],
	// Migration Change
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	bbar: {
        xtype: 'pagingtoolbar',
        pageSize: getStandardPageSize(),
        store: storeStandardSchemas,
        displayInfo: true,
        plugins: new Ext.ux.ProgressBarPager()
    },
	store: storeStandardSchemas,
	dockedItems: [{
        dock: 'top',
        xtype: 'toolbar',
        items: [
        {xtype: 'label', text: _label['selectApplications']},
        {displayField:'name' , forceSelection: true , id: 'filterStandardApplication' , name: 'filterStandardApplication', queryMode: 'local' ,  multiSelect:true,store: schemaStoreApplications , triggerAction: 'all', valueField: 'idApplication' , xtype: 'combo', width: 150 , submitValue: false,
			listeners: {
				change: function(combo, records) {
					
					var appIds = Ext.getCmp('filterStandardApplication').getValue();
					
					var strAppId = appIds.toString();
					if(strAppId == '') {
						Ext.getCmp('filterStandardApplication').setValue(-1);
					} else if(strAppId.indexOf("-1") != -1 && appIds.length > 1) {
						strAppId = strAppId.replace("-1,", "");
						var val = new Array();
						
						for(count = 0; count < appIds.length; count++){
							if(appIds[count] == '-1') {
								appIds[count] = 0;
							} 
						}
						Ext.getCmp('filterStandardApplication').setValue(appIds);
						return;
					}
										
					standardSchemasGrid.store.proxy.extraParams.appIds = appIds.toString();
					standardSchemasGrid.store.loadPage(1);
				}
			}			
		}
        ]
	}],
	title: _label['standardValidations']
});

function getStandardPageSize() {
	if(screen.height <= 768)
		return 17;
	else
		return 37;
}
function standardSchemasList() {
	storeStandardSchemas.sync();
	Ext.getCmp('content').layout.setActiveItem(Ext.getCmp('standardSchemasGrid'));
};

function addStandardSchema() {
	if (storeApplications.getCount() != 0 ) {
		var record = new standardSchemasGrid.store.model(); //recordType
		record.set('idApplication' , storeApplications.getAt(0).get('idApplication'));		
		record.set('idStreamType' , 7);
		editorStandardSchemas.cancelEdit();
		popupStandardSchemas(record , true);
	} else {
		App.setAlert(false , _alert['createApp']);
	}
};

function deleteStandardSchema() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.MessageBox.confirm('Delete Standard stream?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	standardSchemasGrid.store.remove(record);
        	standardSchemasGrid.store.sync();
        }
    });
};

function editStandardSchema() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorStandardSchemas.cancelEdit();
    
    var rowIndex = storeStandardSchemas.findExact("idSchema", record.get('idSchema'));	
	record = storeStandardSchemas.getAt(rowIndex);
	
    popupStandardSchemas(record , false);
};

function duplicateStandardSchema() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
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
				standardSchemasGrid.store.load();
				App.setAlert(true, _message['successDuplicateSchema']);
			} else {
				App.setAlert(false, recordResult.errMsg);
			}
		},
		url : './schemaDuplicate.json'
	});
};

function validateStandardDatastream() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorStandardSchemas.cancelEdit();
    popupValidateDatastream(record , 'add');
};

function standardDatastreamReceived() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorStandardSchemas.cancelEdit();
    popupDatastreamReceived(record);
};
function standardSchemaDocuments() {
    var record = standardSchemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorStandardSchemas.cancelEdit();
    popupSchemaDocuments(record);
};

function helpStandardSchema(){
	popupSchemaHelp();
};
function popupStandardSupported() {
		
	Ext.define('supportedStandard', {
		extend: 'Ext.data.Model',
		fields: [
			'idSchemaLib',
			'libType',
			'version',
			'libName',
			'availability'
		],	
		idProperty:'idSchemaLib'
	});
	var storeSupportedStandard = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'supportedStandard',
		idProperty:'idSchemaLib',
		pageSize:19,
		proxy: {
			type: 'ajax',
			api: {
				read    : './schemasLibRead.json?list=all'
			},
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty: 'idSchemaLib',
				totalProperty: 'total'
			}
		}	   
	});
	storeSupportedStandard.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeDatastreamsReceived.remove();			
		}	
	});
	storeSupportedStandard.load({params:{start:0 , limit:19}});

	function getAvailabilityName(value){
		var str = '';
		/*if(value == 1) {
			value = 'Open Edition';
		} else if(value == 2) {
			value = 'Enterprise Edition';
		}
	    str = '<div style="white-space:pre-wrap !important;display:inline-block;">' + value + '</div>';*/
		str = '<div style="white-space:pre-wrap !important;display:inline-block;">Full Edition</div>';
	    return str;
	}
	
	function getTypeName(value){
		if(value == 7) {
			value = 'HL7';
		} else if(value == 8) {
			value = 'SWIFT';
		} else if(value == 9) {
			value = 'EDI CICA';
		}
	    var str = '<div style="white-space:pre-wrap !important;display:inline-block;">' + value + '</div>';
	    return str;
	}
	
	var columnsSupportedStandard = [
		{align: 'center' , dataIndex: 'idSchemaLib' , header: 'Id' , sortable: true , width: 50},
		{dataIndex: 'libType' , header: 'Type' , sortable: true , flex: 1, renderer : getTypeName},
		{dataIndex: 'version' , header: 'Version' ,  sortable: true ,  flex: 1},
		{dataIndex: 'libName' , header: 'Standard' ,  sortable: true , flex: 1},
		{dataIndex: 'availability' , header: 'Availability' ,  sortable: true , flex: 1, renderer : getAvailabilityName}
	];
	
	var supportedStandardGrid = Ext.create('Ext.grid.Panel', {
		bbar: {
	        xtype: 'pagingtoolbar',
	        pageSize: storeSupportedStandard.pageSize,
	        store: storeSupportedStandard,
	        displayInfo: true,
	        plugins: new Ext.ux.ProgressBarPager()
	    },
		columnLines: true,
		columns: columnsSupportedStandard,
		height: 468,
		id: 'supportedStandardGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeSupportedStandard
	});
	new Ext.Window({
		height: 500,
		id: 'popupSupportedStandard',
		items: [supportedStandardGrid] ,
		layout: 'fit',
		modal: true,
		resizable: false,
	    title: _message["supportedStandard"],
	    width: 765,
	    maximizable: true,
	    listeners : {
            'resize' : function(win,width,height,opt){
               if(width != 765) {
            	   storeSupportedStandard.pageSize = 27;
            	   storeSupportedStandard.load({params:{limit:27}});
               } else {
            	   storeSupportedStandard.pageSize = 19;
            	   storeSupportedStandard.load({params:{limit:19}});
               }
             }
	    }
    }).show();

};