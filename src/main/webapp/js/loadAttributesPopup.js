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
var idSchema;
var schemaTypeId;
var isAttributeChanged = false;
Ext.define('schemaAttributes', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'idSchemaField', type:'int'},
		{name: 'name', type:'string'},
		{name: 'description', type:'string'}
    ],	
	idProperty:'idSchemaField'
});
function popupAttributesList(schemaFieldID, idSchemaType) {	
	schemaTypeId = idSchemaType;
	var record;
	if(idSchemaType ==  1) {
		record = schemasGrid.getSelectionModel().getSelection()[0];		
	} else if(idSchemaType == 2) {
		record = generationStreamGrid.getSelectionModel().getSelection()[0];
	}
	idSchema = record.get('idSchema');
	var idSchemaField = schemaFieldID;
	
	var storeSchemaAttributes = new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		model: 'schemaAttributes',
		idProperty:'idSchemaField',
		pageSize: 18,
		fields: ['idSchemaField', 'name', 'description'],
		proxy: {
			type: 'ajax',
			api: {
				read    : './schemaAttributesPopupRead.json',
				create  : './schemaExtraCheckCreate.json',
				update  : './schemaExtraCheckUpdate.json',
				destroy : './schemaFieldsTreePopupDestroy.json'
			},
			extraParams:{		
				idSchema: idSchema,
				idSchemaField: idSchemaField
	        },
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty:'idSchemaField',
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
					if(operation.action == 'update'){
						storeSchemaAttributes.load();
					}
				}
	        }   	
	    }
	});
	storeSchemaAttributes.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeSchemaAttributes.remove();			
		}	
	});
	storeSchemaAttributes.load({params:{start:0 , limit:10}});
	
	var columnsSchemaAttributes = [
  		{align: 'center' , dataIndex: 'idSchemaField' , header: 'Id' , sortable: true , width: 50},
  		{dataIndex: 'name' , header: 'Name' , sortable: true , width: 250},
  		{dataIndex: 'description' , header: 'Description' , sortable: true , width: 350}
     ];
	var tbarSchemaAttributes = [
	    {iconCls: 'field_attribute_add' , handler: addSchemaAttributes, text: _message["add"]} , '-' ,
	    {iconCls: 'field_attribute_edit' , handler: editSchemaAttributes, text: _message["edit"]} , '-' ,
   		{iconCls: 'field_attribute_delete' , handler: deleteSchemaAttributes, text: _message["delete"]}
   	];
	
	var schemaAttributesGrid = Ext.create('Ext.grid.Panel', {
		bbar:Ext.create('Ext.PagingToolbar', {
			store: storeSchemaAttributes,
			displayInfo: true			
		}),
		columnLines: true,
		columns: columnsSchemaAttributes,
		height: 468,
		id: 'schemaAttributesGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeSchemaAttributes,
		tbar: tbarSchemaAttributes
	});
	
	
	function deleteSchemaAttributes() {
	      
		var record = schemaAttributesGrid.getSelectionModel().getSelection()[0];
		if (!record) {
	    	App.setAlert(false , _alert['selectRecord']);
	        return false;
	    };
	    
		schemaAttributesGrid.store.proxy.extraParams.idSchemaField = record.data.idSchemaField;
    	schemaAttributesGrid.store.remove(record);
    	schemaAttributesGrid.store.sync();
    	isAttributeChanged = true;
	};
	function addSchemaAttributes() {		
		var idParent = idSchemaField;
	
		Ext.Ajax.request({
			params : {
				idSchema : idSchema,
				idParent : idParent,
				isAttribute : 1,
				leaf : true
			},
			url : 'schemaFieldsTreePopupCreate.json',
			success : function(result, request) {
				var idSchemaField = Ext.decode(result.responseText).idSchemaField;
				var addRecord = new Object;
				addRecord.id = idSchemaField;
				addRecord.idFieldType = 4;
				addRecord.name = 'New leaf';
				popupSchemaAttribute(addRecord, 'add', schemaTypeId);
			}
		});
	};
	
	function editSchemaAttributes() {
		var record = schemaAttributesGrid.getSelectionModel().getSelection()[0];
		
		if (!record) {
			App.setAlert(false , _alert["selectRecord"]);
			return false;
		};
		if(record.get('systemRule')) {
			App.setAlert(false , _error["recordEditError"]);
			return false;
		}
		popupSchemaAttribute(record , 'modify', schemaTypeId);
		schemaAttributesGrid.store.sync();
	};
	new Ext.Window({
		height: 500,
		id: 'popupSchemaAttributes',
		items: [schemaAttributesGrid] ,
		layout: 'absolute',
		modal: true,
		resizable: false,
	    title: _label["attributes"],
	    width: 840
    }).show();
};