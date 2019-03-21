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

/**
 * Grid columns
 */
var lspSchemaStore = new Ext.data.Store({
		autoSave : false,
		autoLoad : true,
		model : 'schemas',
		idProperty : 'idSchema',
		proxy : {
			type : 'ajax',
			api : {
				read : './schemasRead.json'
			},
			extraParams: {
	            idSchemaType: "1,2",
	            appIds: -1
	        },
			reader : {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	});

var columnsLoadingStream = [ {
	dataIndex : 'idSchema',
	header : 'Id',
	sortable : true,
	width : 70
}, {
	dataIndex : 'name',
	editor : {
		xtype : 'textfield'
	},
	header : _label['name'],
	sortable : true,
	width : 150
}, {
	dataIndex : 'idApplication',
	editor : {
		xtype : 'combobox',
		displayField : 'name',
		forceSelection : true,
		queryMode : 'local',
		store : storeApplications,
		triggerAction : 'all',
		typeAhead : true,
		valueField : 'idApplication'
	},
	header : _label['application'],
	renderer : function(value) {
		return applicationName(value);
	},
	sortable : true,
	width : 150
}, {
	dataIndex : 'idLinkedSchema',
	editor : {
		xtype : 'combobox',
		displayField : 'name',
		forceSelection : true,
		//queryMode : 'local',
		store : lspSchemaStore,
		triggerAction : 'all',
		typeAhead : true,
		valueField : 'idSchema'
	},
	header : _label['customValidation'],
	renderer : function(value) {
		return getSchemaName(value);
	},
	sortable : true,
	width : 200
}, {
	dataIndex : 'idDatabase',
	editor : {
		xtype : 'combobox',
		displayField : 'name',
		forceSelection : true,
		queryMode : 'local',
		store : storeDatabases,
		triggerAction : 'all',
		typeAhead : true,
		valueField : 'idDatabase'
	},
	header : _label['outputDatabase'],
	renderer : function(value) {
		return databaseName(value);
	},
	sortable : true,
	width : 150
}, {
	dataIndex : 'idSchemaType',
	header : _label['linked'],
	width : 70,
	renderer : function(value, cell, record, rowIndex, colIndex, store) {
		return '<img src=\'./images/linked.png\' />';
	}
} ];

/**
 * Grid store
 */
var streamLoadingStore = new Ext.data.Store({
	autoSave : false,
	autoLoad : true,
	model : 'schemas',
	pageSize : getPageSize(),
	idProperty : 'idSchema',
	proxy : {
		type : 'ajax',
		api : {
			read : './schemasRead.json',
			create : './schemasCreate.json',
			update : './schemasUpdate.json?_method=put',
			destroy : './schemasDestroy.json?'
		},
		extraParams : {
			idSchemaType : 4,
			appIds : -1
		},
		reader : {
			type : 'json',
			root : 'results',
			successProperty : 'success',
			messageProperty : 'message'
		},
		writer : {
			type : 'json',
			writeAllFields : true
		}
	},
	listeners : {
		write : function(store, operation) {
			if (operation.response.responseText) {
				var responseObj = Ext.decode(operation.response.responseText);
				App.setAlert(true, responseObj.message);
				if (operation.action == 'create' || operation.action == 'destroy') {
					streamLoadingStore.loadPage(1);
				}
				
				if (operation.action == 'create') {
					
    				Ext.Msg.show({
    					title:_message['wizard'],
    					msg: _message['schemaCreateField'],
    					buttons: Ext.Msg.OKCANCEL,
    					fn: function ( answer ) {
    						if ( answer == 'ok') {
    							var record = Ext.getCmp('streamLoadingSchemas').getStore().getNodeById('editFieldsStreamLoading');
    							Ext.getCmp('streamLoadingSchemas').getSelectionModel().select(record);
                    			loadingStreamGrid.getView().select(0);
    							editLoadingStreamFields();
    						}
    					}
    				});    				
				}
			}
		}
	}
});
streamLoadingStore.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.message.indexOf('Error') != -1) {
				Ext.Msg.alert("", _error['connectionError']);
			} else {
				App.setAlert(false, responseObj.message);
				if (operation.action == 'create') {
					streamLoadingStore.load();
				} else {
					streamLoadingStore.remove();
				}
			}
		}
	}
});

/**
 * Grid editor
 */
var editorLoadingStream = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
	clicksToEdit : 2,
	validationScheme : 'validationLoadingStream',
	validateEditSuccess : function() {
		loadingStreamGrid.store.sync();
	},
	listeners : {
		afteredit : function(c) {
			if (c.field == 'idLinkedSchema') {
				var linkedSchemaId = c.newValues.idLinkedSchema;
				var rec = lspSchemaStore.getAt(lspSchemaStore.find('idSchema', linkedSchemaId));
				c.record.set('idStreamType', rec.get('idStreamType'));
				loadingStreamGrid.store.sync();
				loadingFieldsTreeReload(linkedSchemaId);
			}
		}
	}
});

/**
 * Grid
 */
var loadingStreamGrid = new Ext.grid.Panel({
	columnLines : true,
	columns : columnsLoadingStream,
	frame : false,
	plugins : [ editorLoadingStream ],
	selModel : Ext.create('Ext.selection.RowModel', {
		mode : 'SINGLE'
	}),
	bbar : {
		xtype : 'pagingtoolbar',
		pageSize : getPageSize(),
		store : streamLoadingStore,
		displayInfo : true,
		plugins : new Ext.ux.ProgressBarPager()
	},
	store : streamLoadingStore,
	dockedItems : [ {
		dock : 'top',
		xtype : 'toolbar',
		items : [ {
			xtype : 'label',
			text : _label['selectApplications']
		}, {
			displayField : 'name',
			forceSelection : true,
			id : 'filterLoadingStreamApplication',
			name : 'filterLoadingStreamApplication',
			queryMode : 'local',
			multiSelect : true,
			store : schemaStoreApplications,
			triggerAction : 'all',
			valueField : 'idApplication',
			xtype : 'combo',
			width : 150,
			submitValue : false,
			listeners : {
				change : function(combo, records) {

					var appIds = Ext.getCmp('filterLoadingStreamApplication').getValue();

					var strAppId = appIds.toString();
					if (strAppId == '') {
						Ext.getCmp('filterLoadingStreamApplication').setValue(-1);
					} else if (strAppId.indexOf("-1") != -1 && appIds.length > 1) {
						strAppId = strAppId.replace("-1,", "");
						for (var count = 0; count < appIds.length; count++) {
							if (appIds[count] == '-1') {
								appIds[count] = 0;
							}
						}
						Ext.getCmp('filterLoadingStreamApplication').setValue(appIds);
						return;
					}

					loadingStreamGrid.store.proxy.extraParams.appIds = appIds.toString();
					loadingStreamGrid.store.loadPage(1);
				}
			}
		} ]
	} ],
	title : _label['streamLoadingLabel']
});

function getSchemaName(idSchema) {
	if (idSchema <= 0) {
		return "";
	}
	if (lspSchemaStore.getCount()) {
		var rec = lspSchemaStore.getAt(lspSchemaStore.find('idSchema', idSchema));
		return rec == null ? idSchema : rec.get('name');
	} else {
		return _message["error"];
	}
}

function listLoadingStreams() {
	streamLoadingStore.load();
	Ext.getCmp('content').layout.setActiveItem(loadingStreamGrid);
}

function addLoadingStreamFunc() {
	if (storeApplications.getCount() != 0) {
		var record = new loadingStreamGrid.store.model();
		editorLoadingStream.cancelEdit();
		popupLoadingStream(record, true);
	} else {
		App.setAlert(false, _alert['createApp']);
	}
};

function deleteLoadingStream() {
	var record = loadingStreamGrid.getSelectionModel().getSelection()[0];
	if (!record) {
		App.setAlert(false, _alert['selectRecord']);
		return false;
	}
	Ext.MessageBox.confirm('Delete Stream Loading?', _message['delMsg'] + " '" + record.get('name') + "'?", function(btn) {
		if (btn == 'yes') {
			loadingStreamGrid.store.remove(record);
			loadingStreamGrid.store.sync();
		}
	});
};

function editLoadingStreamFunc() {
	var record = loadingStreamGrid.getSelectionModel().getSelection()[0];
	if (!record) {
		App.setAlert(false, _alert['selectRecord']);
		return false;
	}
    var rowIndex = loadingStreamGrid.getStore().findExact("idSchema", record.get('idSchema'));	
	record = loadingStreamGrid.getStore().getAt(rowIndex);
	editorLoadingStream.cancelEdit();
	popupLoadingStream(record, false);
};

function editLoadingStreamFields() {
	var record = loadingStreamGrid.getSelectionModel().getSelection()[0];
	if (!record) {
		App.setAlert(false, _alert['selectRecord']);
		return false;
	}
    var rowIndex = loadingStreamGrid.getStore().findExact("idSchema", record.get('idSchema'));	
	record = loadingStreamGrid.getStore().getAt(rowIndex);
	Ext.Ajax.request({
		url : "./statusRead.json",
		params : {
			idDatabase : record.get('idDatabase')
		},
		disableCaching : false,
		success : function(result, request) {
			if (result.responseText == 1) {
				editorLoadingStream.cancelEdit();
				var schemaId = record.get('idSchema');
				loadingStreamFieldsTree.store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + schemaId;
				loadingFieldsTreeReload(schemaId);
				Ext.getCmp('content').layout.setActiveItem(loadingStreamFieldsTree);
			} else {
				App.setAlert(false, _error['databaseConnectionError']);
			}
		}
	});
};

function loadingFieldsTreeReload(schemaId) {
	Ext.Ajax.request({
		params : {
			idSchema : schemaId
		},
		success : function(result) {
			jsonResp = Ext.JSON.decode(result.responseText);
			loadingStreamFieldsTree.store.setRootNode({
				id : '0',
				text : jsonResp.name,
				leaf : false,
				expanded : true
			});
		},
		url : './getSchemaNameById.json'
	});
}