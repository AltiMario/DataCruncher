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

var storeGenerationStream = new Ext.data.Store({
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
			update  : './schemasUpdate.json?_method=put',
			destroy : './schemasDestroy.json?'
		},
		extraParams: {
            idSchemaType: 2,
            appIds: -1
        },
        reader: {
            type: 'json',
            root: 'results',
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
				if(operation.action == 'create' || operation.action == 'destroy'){
					storeGenerationStream.loadPage(1);
				}
				
				if(operation.action == 'create') {

    				Ext.getCmp('filterGenerationApplication').setValue(schemaStoreApplications.getAt(schemaStoreApplications.getCount() - 1).get('idApplication'));
    				Ext.Msg.show({
    					title:_message['wizard'],
    					msg: _message['schemaCreateField'],
    					buttons: Ext.Msg.OKCANCEL,
    					fn: function ( answer ) {
    						if ( answer == 'ok') {
    							var record = Ext.getCmp('generationStream').getStore().getNodeById('editGenerationStreamFields');
    							Ext.getCmp('generationStream').getSelectionModel().select(record)
                    			generationStreamGrid.getView().select(0);
    							editGenerationStreamFields();
    						}
    					}
    				});    				
				}
			}               
        }   	
    }
});
storeGenerationStream.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.message.indexOf('Error')!=-1){
				Ext.Msg.alert("" , _error['connectionError']);
			} else{
				App.setAlert(false , responseObj.message);
				if (operation.action == 'create') {
					storeGenerationStream.load();
	        	} else {
	        		storeGenerationStream.remove();
	        	}
			}
		}
	}	
});
var editorGenerationStream =  Ext.create('Ext.ux.grid.plugin.RowEditing', {
    clicksToEdit: 2,
	listeners: {

		beforeedit: function(editor,e,eOpts ) {
			generationStreamGrid.columns[7].hide();
		},
		canceledit: function(grid, eOpts) {				
			generationStreamGrid.columns[7].setVisible(true);
		},	
		afteredit: function() {
			generationStreamGrid.store.sync();
			generationStreamGrid.columns[7].setVisible(true);
		}
	}			
});
var columnsGeneratedStream = [
		{
			dataIndex : 'idSchema',
			header : 'Id',
			sortable : true,
			width : 70
		},
		{
			dataIndex : 'name',
			editor : {
				xtype : 'textfield'
			},
			header : _label['name'],
			sortable : true,
			width : 150
		},
		{
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
		},
		{
			dataIndex : 'idInputDatabase',
			editor : {
				xtype : 'combobox',
				displayField : 'name',
				forceSelection : true,
				queryMode : 'local',
				store : storeDatabases,
				triggerAction : 'all',
				typeAhead : true,
				valueField : 'idInputDatabase'
			},
			header : _label['database'],
			renderer : function(value) {
				return databaseName(value);
			},
			sortable : true,
			width : 150
		},
		{
			dataIndex : 'idStreamType',
			header : _label['streamType'],
			renderer : function(value) {
				return streamTypeName(value);
			},
			sortable : true,
			width : 150
		},
		{
			dataIndex : 'isActive',
			header : _label['active'],
			width : 50,
			sortable : true,
			renderer : function(value, cell, record, rowIndex, colIndex, store) {
				var res = '<input type="checkbox"';
				if (eval(value))
					res += 'checked="checked"';
				res += 'onclick="activateSchemaFunc(\'' + record.data.idSchema
						+ '\', this.checked, 2)"';
				res += ' >';
				return res
			}
		},
		{
			dataIndex : 'idSchemaType',
			header : _label['linked'],
			width : 70,
			renderer : function(value, cell, record, rowIndex, colIndex, store) {
				if (record.data.idSchemaType == 2) {
					return '<img src=\'./images/linked.png\' />';
				} else {
					return '<img src=\'./images/unlinked.png\' />';
				}
				return '';
			}
		} ];
var generationStreamGrid = new Ext.grid.Panel({
	columnLines: true,
	columns: columnsGeneratedStream,
	frame: false,
	id: 'generationStreamGrid',
	plugins: [editorGenerationStream],
	// Migration Change
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	bbar: {
        xtype: 'pagingtoolbar',
        pageSize: getPageSize(),
        store: storeGenerationStream,
        displayInfo: true,
        plugins: new Ext.ux.ProgressBarPager()
    },
	store: storeGenerationStream,
	dockedItems: [{
        dock: 'top',
        xtype: 'toolbar',
        items: [
        {xtype: 'label', text: _label['selectApplications']},
        {displayField:'name' , forceSelection: true , id: 'filterGenerationApplication' , name: 'filterGenerationApplication', queryMode: 'local' ,  multiSelect:true,store: schemaStoreApplications , triggerAction: 'all', valueField: 'idApplication' , xtype: 'combo', width: 150 , submitValue: false,
			listeners: {
				change: function(combo, records) {
					
					var appIds = Ext.getCmp('filterGenerationApplication').getValue();
					
					var strAppId = appIds.toString();
					if(strAppId == '') {
						Ext.getCmp('filterGenerationApplication').setValue(-1);
					} else if(strAppId.indexOf("-1") != -1 && appIds.length > 1) {
						strAppId = strAppId.replace("-1,", "");
						var val = new Array();
						
						for(count = 0; count < appIds.length; count++){
							if(appIds[count] == '-1') {
								appIds[count] = 0;
							} 
						}
						Ext.getCmp('filterGenerationApplication').setValue(appIds);
						return;
					}
					
					generationStreamGrid.store.proxy.extraParams.appIds = appIds.toString();
					generationStreamGrid.store.loadPage(1);
				}
			}			
		}
        ]
	}],
	title: _label['generationStream']
});

function generationStreamList() {
	storeGenerationStream.load();
	Ext.getCmp('content').layout.setActiveItem('generationStreamGrid');
};

function addGenerationStream() {
	if (storeApplications.getCount() != 0 ) {
		var record = new generationStreamGrid.store.model(); //recordType
		record.set('idApplication' , storeApplications.getAt(0).get('idApplication')); 
		if(storeDatabases.getCount() != 0 )
			record.set('idDatabase' , storeDatabases.getAt(0).get('idDatabase'));
		record.set('idStreamType' , 1);
		editorGenerationStream.cancelEdit();
		popupGenerationStream(record , true);
	} else {
		App.setAlert(false , _alert['createApp']);
	}
};


function deleteGenerationStream() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.MessageBox.confirm('Delete Stream generation?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	generationStreamGrid.store.remove(record);
        	generationStreamGrid.store.sync();
        }
    });
};

function editGenerationStreamFields() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
           
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };

    var rowIndex = storeGenerationStream.findExact("idSchema", record.get('idSchema'));	
	record = storeGenerationStream.getAt(rowIndex);

    Ext.Ajax.request({
	    url: "./statusRead.json",
	    params: {
	    	idDatabase: record.get('idInputDatabase')
	    },
	    disableCaching: false,
	    success: function ( result, request ) {

	    	if(result.responseText == 1) {
	    		editorGenerationStream.cancelEdit();
	    	    
	    	    generationStreamFieldsTree.store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + record.get('idSchema');

	    	    Ext.Ajax.request({
	    			params : {
	    				idSchema : record.get('idSchema')
	    			},
	    			success: function (result) {
	    				jsonResp = Ext.JSON.decode(result.responseText);
	    				treestore1.setRootNode({
	    					id: '0',
	    					text: jsonResp.name,
	    					leaf: false,
	    					expanded: true // If true, the store load's itself immediately; we want that to happen!
	    				});
	    	       },
	    		   url : './getSchemaNameById.json'
	    		});
	    		Ext.Ajax.request( {
	    			success : function(resp, opts) {
	    				var result = resp.responseText;
	    				if (result == '') {
	    					gsftpGlobals.stage = 0;
	    					Ext.getCmp('fieldsTreeButton8').setValue('');
	    					for (var i = 1; i <= 11; i++) {
	    						gsftpGlobals.getButtonVisibility(Ext.getCmp('fieldsTreeButton' + i));
	    					}
	    				}
	    				Ext.getCmp('fieldsTreeTextArea').setValue(result);
	    			},
	    			params : {
	    				action : 'getMetaCondition',
	    				idSchema : record.get('idSchema')
	    			},
	    			url : './schemaLinkedFields.json'
	    		});

	    		Ext.getCmp('content').layout.setActiveItem('generationStreamPanel');
	    	} else {
	    		App.setAlert(false, _error['databaseConnectionError']);
	    	}
	    }
    });
    
};

function editGenerationStream() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorGenerationStream.cancelEdit();
    
    var rowIndex = storeGenerationStream.findExact("idSchema", record.get('idSchema'));	
	record = storeGenerationStream.getAt(rowIndex);
	
    popupGenerationStream(record , false);
};
function duplicateGenerationStream() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
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
				generationStreamGrid.store.load();
				App.setAlert(true, _message['successDuplicateSchema']);
			} else {
				App.setAlert(false, recordResult.errMsg);
			}
		},
		url : './schemaDuplicate.json'
	});
};
function helpGenerationStream(){
	popupSchemaHelp();
};
function generationStreamDocuments() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorGenerationStream.cancelEdit();
    popupSchemaDocuments(record);
};
function generationStreamImportFromXSD() {
   popupImportSchemaFile();
};
function popupImportSchemaFile() {
    var idApplication = storeApplications.getAt(0).get('idApplication');
	var uploadContent = {
	        xtype: 'form',
	        id: 'importForm',
	        fileUpload: true,
	        frame: false,
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
			id: 'popupUploadDocSchema',
			items: [
				uploadContent,
				{id: 'btnImport',
				  name: 'btnImport',disabled: true,
				  text: _label["import"], x: 135 , xtype: 'button' , width: 100 , y: 70 ,
					handler: function(){
					    var form = Ext.getCmp('importForm').getForm();
						if (form.isValid()) {
							form.submit({
								url: './schemaImportFromJv.json?idApplication=' + idApplication +'&idSchemaType=2',
		                        waitMsg: _message["loadTheFile"],
								success: function(form, action) {
									generationStreamGrid.store.load();
                                    var warMsg = action.result.message;
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
									Ext.getCmp('popupUploadDocSchema').close();
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
		        		Ext.getCmp('popupUploadDocSchema').close();
		        	}
		        }
			],
			layout: 'absolute',
			modal: true,
			border: false,
			resizable: false,
		    title: _label['importFile'],
		    width: 500,
		    height: 140
	    }).show();
	};
	
function sendGenerationStreamFunction(query) {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
    sendGenerationStreamPopup(record, query);
}