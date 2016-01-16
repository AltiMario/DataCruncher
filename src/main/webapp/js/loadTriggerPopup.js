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

var storeSchemaAllTriggers = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'schemaTriggerModel',
		idProperty:'idEventTrigger',
		pageSize: 20,
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
				idProperty:'idEventTrigger',
				totalProperty: 'total'
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
							storeSchemaAllTriggers.load();
						}
				}
	        }   	
	    }
	});
	storeSchemaAllTriggers.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeSchemaAllTriggers.remove();			
		}	
	});
	storeSchemaAllTriggers.load({params:{start:0 , limit:20}});
	
	schemaExtraCheckRenderers = {
		iconTpl : new Ext.XTemplate('<div class="{iconCls}" style="min-height:18px;vertical-align: middle;"',
				' id="{divId}" />').compile()
		,
		iconSchemaExtraCheckRenderer : function(data, cell, record, rowIndex, colIndex, store) {
			var divId = 'schemaExtraCheckStatusDivId_' + rowIndex + colIndex;
			var str ='';
			if(record.get('systemRule'))
				str = schemaExtraCheckRenderers.iconTpl.applyTemplate({ iconCls : 'lock', divId : divId});
			else
				str = schemaExtraCheckRenderers.iconTpl.applyTemplate({ iconCls : 'unlock', divId : divId});
			return str;
		}
		
}

function popupTrigger() {
	var columnsSchemaTrigger = [
  		{align: 'center' , dataIndex: 'idEventTrigger' , header: 'Id' , sortable: true , width: 50},
  		{dataIndex: 'name' , header: 'Name' , sortable: true , flex: 1},
  		{dataIndex: 'description' , header: 'Description' , sortable: true , flex: 1},
  		{dataIndex : 'status', header:'Status', sortable : true, width : 50, renderer : schemaExtraCheckRenderers.iconSchemaExtraCheckRenderer}
     ];
	var tbarSchemaTrigger = [
	    {iconCls: 'schema_trigger_add' , handler: addSchemaTriggerCode, text: _message["addCode"]} , '-' ,
	    {iconCls: 'schema_trigger_edit' , handler: editSchemaTrigger, text: _message["edit"]} , '-' ,
   		{iconCls: 'schema_trigger_delete' , handler: deleteSchemaTrigger, text: _message["delete"]}
   	];
	
	var schemaTriggerGrid = Ext.create('Ext.grid.Panel', {
		bbar:Ext.create('Ext.PagingToolbar', {
			store: storeSchemaAllTriggers,
			displayInfo: true			
		}),
		columnLines: true,
		columns: columnsSchemaTrigger,
		height: 468,
		id: 'schemaTriggerGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeSchemaAllTriggers,
		tbar: tbarSchemaTrigger
	});
	
	
	function deleteSchemaTrigger() {
		var record = schemaTriggerGrid.getSelectionModel().getSelection()[0];
		if (!record) {
			App.setAlert(false, _alert["selectRecord"]);
			idFile = "";
			return false;
		} else if (record.get('systemRule')) {
			App.setAlert(false, _error["recordDeleteError"]);
			return false;
		} else {
			schemaTriggerGrid.store.proxy.extraParams.idEventTrigger = record.get('idEventTrigger');
			schemaTriggerGrid.store.remove(record);
			schemaTriggerGrid.store.sync();
			//schemaTriggerGrid.store.load();
		}
	}
	
	function addSchemaTriggerCode() {	    
		var record = new schemaTriggerGrid.store.model(); //recordType	   
	    try {
			popupAddCodeEE(record);
			schemaTriggerGrid.store.sync();
		} catch (e) {
			callAlert((e instanceof ReferenceError) ? _message['optionNotReady']
					: 'loadTriggerPopup.js :: unknown error');
		}
	}

	function editSchemaTrigger() {
		var record = schemaTriggerGrid.getSelectionModel().getSelection()[0];
		
		if (!record) {
			App.setAlert(false , _alert["selectRecord"]);
			return false;
		};
		if(record.get('systemRule')) {
			App.setAlert(false , _error["recordEditError"]);
			return false;
		}
		popupAddCode(record, false);
		schemaTriggerGrid.store.sync();
	}
	new Ext.Window({
		height: 500,
		id: 'popupSchemaExtraCheck',
		items: [schemaTriggerGrid] ,
		layout: 'fit',
		modal: true,
		resizable: false,
	    title: _label["trigger"],
	    width: 715,
	    maximizable: true,
	    listeners : {
            'resize' : function(win,width,height,opt){
               if(width != 715) {
            	   storeSchemaAllTriggers.pageSize = 21;
            	   storeSchemaAllTriggers.load({params:{limit:21}});
               } else {
            	   storeSchemaAllTriggers.pageSize = 20;
            	   storeSchemaAllTriggers.load({params:{limit:20}});
               }
             }
	    }
    }).show();
};

function popupAddCode(record, add) {
	var tbarTriggerCode = [
	    {iconCls: 'schema_trigger_clear_editor' , handler: clearEditor, text: _message['clearEditor']} , '-' ,
	    {iconCls: 'schema_trigger_validate' , handler: validate, text: _message["validate"]}   		
   	];
	
	function clearEditor() {	    
		Ext.getCmp('name').setValue("");
		Ext.getCmp('description').setValue("");
		Ext.getCmp('codeTa').setValue("");		
	};
	
	function validate() {		
		Ext.Ajax.request( {
			params : {
				code : Ext.getCmp('codeTa').getValue(),
				name : Ext.getCmp('name').getValue(),
				addReq:add
			},
			url : './isTriggerCodeValid.json',
			success: function (result) {
				recordResult = Ext.JSON.decode(result.responseText);
				if(recordResult=="Success"){
					invokePopup(recordResult,true);				
				}else{
					invokePopup(recordResult,false);					
				}				
			},
			failure : function() {
				App.setAlert(false , 'Code validation failed. Please check the sample entry for reference.');
				Ext.getCmp("btnSaveCode").setDisabled(true);				
			}
		});	
	};
	
	function invokePopup(content, status) {
	    new Ext.Window({ 
			height: 160,
			width: 420,
			layout: 'absolute',
			modal: true,
			autoScroll: true,
			resizable: false,
		    title: _label['triggerValidation'],
			html : '<div style="padding: 5px;">' + content + '</div>',
			buttons : [
				{text: 'Ok', xtype: 'button' , width: 100, 
					handler: function() {
						this.ownerCt.ownerCt.close();
						if(status){
							Ext.getCmp("btnSaveCode").enable();
						}else{
							Ext.getCmp("btnSaveCode").setDisabled(true);
						}
						
					}
				}
			]
	    }).show(); 
	}	
	new Ext.Window({
			id: 'popupAddCode', 
			tbar: tbarTriggerCode,
			items: [
				{name: 'name' , fieldLabel: _label['name'] , id: 'name' , xtype: 'textfield' , x: 5, y: 5, labelAlign: 'top',
					width: 200, value: record.get('name'),
					listeners: {
				        'change': function(){
							Ext.getCmp("btnSaveCode").setDisabled(true);
				        }
				    }
				},
				{ 
					xtype: 'textfield',
					id: 'description',
					name: 'description',
					fieldLabel: _label['description'],
					width: 360, x: 220, y: 5, labelAlign: 'top', value: record.get('description') 
				},
				{
					xtype: 'textareafield',
					fieldLabel: _label['code'],
					labelAlign: 'top',
					margins: '5',
					allowBlank: false,
					name: 'codeTa',
					id: 'codeTa',
					height: 230,
					width: 575,
					x: 5, y: 60,value: record.get('code'),
					listeners: {
				        'change': function(){
				          Ext.getCmp("btnSaveCode").setDisabled(true);
				        }
				     }
				},				
				{id: 'btnSaveCode',
				  name: 'btnSaveCode', disabled: true,text: _message["save"], x: 375 , xtype: 'button' , width: 100 , y: 300 ,
				  handler: function(){
					record.set('name', Ext.getCmp('name').getValue());
					record.set('description', Ext.getCmp('description').getValue());
					record.set('code', Ext.getCmp('codeTa').getValue());
					if(add) {
						Ext.getCmp('schemaTriggerGrid').store.insert(0, record);
					}						
					Ext.getCmp('schemaTriggerGrid').store.sync();
					Ext.getCmp('popupAddCode').close();
				  }
					
				},
				{text: _message["cancel"] , x: 479 , xtype: 'button' , width: 100 , y: 300 ,
		        	handler: function(){
		        		Ext.getCmp('popupAddCode').close();
		        	}
		        }
			],
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: 'Add Code',
		    width: 600,
		    height: 410
	    }).show();
}

