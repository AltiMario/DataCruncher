/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

new Ext.data.JsonStore ( {
    storeId: 'customErrorsStore',
	autoSave: false,
	autoLoad: false,
	fields : [
		{name: 'id', type:'int' }, 
		{name: 'name', type:'String'},
		{name: 'description', type:'String'},		
		{name: 'idSchema', type:'int'}
    ],
	proxy: {
		type: 'ajax',
        api: {
			read    : './customErrorsRead.json',
			create  : './customErrorsCreate.json',
			update  : './customErrorsUpdate.json?_method=put',
			destroy : './customErrorsDestroy.json?_method=delete'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: 'success',
			messageProperty: 'message',
			idProperty: 'idMacro'
		},
		writer: {
			type: 'json',
			writeAllFields: true
		},
		listeners : {
			exception : function (proxy, response, operation) {		
				if (response) {
					var responseObj = Ext.decode(response.responseText);
					if(responseObj){
						if(responseObj.message.indexOf('Error')!=-1){
							Ext.Msg.alert("" , _error['connectionError']);
						} else{
							App.setAlert(false , responseObj.message);
							var _store = Ext.data.StoreManager.lookup('customErrorsStore');							
							if (operation.action == 'create') {
								_store.load();
							} else {
								_store.remove();
							}
						}
					}
				}	
			}
		}
	},
	listeners : {
		write : function(store, operation) {
			var respText = operation.response.responseText;
			if (respText) {
				if (respText.withoutQuots().startsWith(constants.licenseError)) {
					//reload the store, because record is modified in 'save' handler
					//here rejectChanges() is not working since sync() was called.
					this.load();
					callAlert(respText.withoutQuots().split('%%')[1]);
				} else {
					var responseObj = Ext.decode(respText);
					App.setAlert(true, responseObj.message);
					if (operation.action == 'update') {
						this.load();
					}
				}
			}
		}
	}
});

var editorCustomErrors = Ext.create('Ext.ux.grid.plugin.RowEditing', {
        clicksToEdit: 2,
		listeners: { 
			afteredit: function() {
				var _store = Ext.data.StoreManager.lookup('customErrorsStore');
				_store.sync();
			}	
	}
});

function funcSchemaCustomErrors() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
	editorSchemas.cancelEdit();
	var _store = Ext.data.StoreManager.lookup('customErrorsStore');
	var schemaId = schemasGrid.getSelectionModel().getSelection()[0].get("idSchema");
	_store.proxy.extraParams.schemaId = schemaId;
	_store.proxy.extraParams.isCombo = 'false';	
	_store.load();
	new Ext.Window({
				height : 390,
				width : 665,
				title : _message['customErrors'],
				modal : true,
				items : { xtype : 'grid',
					 store: _store,
					 columns: [
					        { header: 'Id',  dataIndex: 'id' },
					        { header: _label["name"], dataIndex: 'name', flex: 1, editor: {xtype:'textfield'} },
					        { header: _label["description"], dataIndex: 'description', editor: {xtype:'textfield'} }
					 ],
                    frame: false,
                    border: false,
					plugins:  [editorCustomErrors],
					selModel : Ext.create('Ext.selection.RowModel', {
						mode : 'SINGLE'
					}),
					title : false,
					tbar : [{
							iconCls : 'alertIcon',
							handler :  function() {
								customErrorsHandlers.addCustomError(_store, schemaId);
							},							
							text : _message["add"]
						},'-', {
							iconCls : 'alertIcon',
							text : _message["delete"],
							handler : function() {
								customErrorsHandlers.deleteCustomError(this);								
							}
						}
					],
					viewConfig : {
						forceFit : true
					},
					height : 357
				}
			}).show();
}

customErrorsHandlers = {
	addCustomError : function(store, schemaId) {
		var record = new store.model();
	    new Ext.Window({ 
			height: 150,
			items: [
				{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 10},
				{id: 'customErrorName', x: 10 , xtype: 'textfield' , width: 280 , y: 30},
				{value: _label['description'] , x: 300 , xtype: 'displayfield' , y: 10},
				{id:'customErrorDescription' ,  x: 300 , xtype: 'textfield' , width: 305 , y: 30}],
			buttons : [
				{text: _message['save'] , xtype: 'button' , width: 100 ,
					handler: function() {					
						record.set('name' , Ext.getCmp('customErrorName').getValue());
						record.set('description' , Ext.getCmp('customErrorDescription').getValue());
						record.set('idSchema' , schemaId);	
						store.add(record);		
						store.sync();
						//sort: always 'Add new...' to be in the end of list
						store.sort('id', 'DESC')
						this.ownerCt.ownerCt.close();
					}
				},
				{text: _message['cancel'] , xtype: 'button' , width: 100, 
					handler: function() {
						this.ownerCt.ownerCt.close();
					}
				}
			],
			layout: 'absolute',
			modal: true,
			resizable: true,
		    title: _message['customErrors'],
		    width: 630
	    }).show(); 
	},
	deleteCustomError : function(button) {
		var grid = button.ownerCt.ownerCt;
	    var record = grid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert['selectRecord']);
	        return false;
	    };
	    grid.store.proxy.extraParams.errorId = record.data.id;
	    grid.store.remove(record);
		grid.store.sync();
	}
}