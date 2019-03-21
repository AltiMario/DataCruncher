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

var treestore = new  Ext.data.TreeStore ( {
    autoSync:true,
    proxy:{
        type:'ajax'
    }
});

var schemaFieldsTree = Ext.create('Ext.tree.Panel', {
	autoScroll: true,
	frame: false,
	id: 'schemaFieldsTree',
	store:treestore,
	//singleExpand: true,
	viewConfig: {
		plugins: {
			ptype: 'treeviewdragdrop'
			//appendOnly: true
		},
		style : {
			overflowY : 'scroll',
			overflowX : 'scroll'
		}
	},
	
	listeners: {
		/*load: function(store, records,successful,operation, eOpts){
			schemaFieldsTree.collapseAll();
			schemaFieldsTree.expandAll();
		},*/
		itemclick: function(view, record,item,index,event){
			schemaFieldsHandler(view,record,event);  
		},
		itemcontextmenu:function(view, record,item,index,event){
			event.stopEvent( );
			schemaFieldsHandler(view,record,event);  
		},    		
		itemmove : function(node, oldParent, newParent, index, options) {
			Ext.Ajax.request({
				params: {
					idSchemaField: node.get('id'),
					idNewParent: newParent.get('id'),
					idOldParent: oldParent.get('id'),					
					elementOrder: index + 1
				},
				url: 'schemaFieldsTreePopupMove.json',
				success : function(result) {
					recordResult = Ext.JSON.decode(result.responseText);
					if (!eval(recordResult.success)) {
						schemaFieldsTree.store.load();

					} 
				}
			});
		}
	},
	rootVisible: true,
	useArrows:true
});

function addBranchSchemaField() { //add root
	var idSchema = schemasGrid.getSelectionModel().getSelection()[0].get('idSchema');
	var sModel = schemaFieldsTree.getSelectionModel();
	var record = sModel.getSelection()[0];
	if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    var idParent;
    if (record.get('leaf')) {
    	idParent = record.parentNode.get('id');
    } else {
    	idParent = record.get('id');
    };
	Ext.Ajax.request({
		params: {
			idSchema: idSchema,
			idParent: idParent,
			leaf: false
		},
		url: 'schemaFieldsTreePopupCreate.json',
		success: function (result , request) {
			var idSchemaField = Ext.decode(result.responseText).idSchemaField;
			var addRecord = new Object;
			addRecord.id = idSchemaField;
			addRecord.idFieldType = 1;
			addRecord.name = 'New branch';
			popupSchemaField(addRecord , 'add' , 'branch', false);
			var newNode = {
					text: addRecord.name,
					expanded: true,
					id: idSchemaField,
					leaf: false,
					children:[]
				};
			if (record.get('leaf')) {
				record.parentNode.appendChild(newNode);
			} else {
				record.appendChild(newNode);
			}
			record.expand();
			schemaFieldsTree.getSelectionModel().select(newNode);
		}
		
	});
}

function addLeafSchemaField() {
	var idSchema = schemasGrid.getSelectionModel().getSelection()[0].get('idSchema');
	var record = schemaFieldsTree.getSelectionModel().getSelection()[0];
	if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    var idParent;
    if (record.get('leaf')) {
    	idSchema: idSchema,
		idParent = record.parentNode.get('id');
    } else {
    	idSchema: idSchema,
		idParent = record.get('id');
    };
	Ext.Ajax.request({
		params: {
			idSchema: idSchema,
			idParent: idParent,
			leaf: true
		},
		success: function (result , request) {
			var idSchemaField = Ext.JSON.decode(result.responseText).idSchemaField;
			var addRecord = new Object;
			addRecord.id = idSchemaField;
			addRecord.idFieldType = 4;
			addRecord.name = 'New leaf';
			popupSchemaField(addRecord , 'add' , 'leaf', false);
			var newNode = {id: idSchemaField , text: addRecord.name , leaf: true};
		    if (record.get('leaf')) {
		    	record.parentNode.appendChild(newNode);
		    } else {
		    	record.appendChild(newNode);
		    }
			record.expand();
			schemaFieldsTree.getSelectionModel().select(newNode);
			//schemaFieldsTree.getNodeById(idSchemaField).select(newNode);
		},
		url: 'schemaFieldsTreePopupCreate.json'
	});
}

/**
 * Deletes schema field.
 * 
 * @return {TypeName} 
 */
function deleteSchemaField() {
	var record = schemaFieldsTree.getSelectionModel().getSelection()[0];
	if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
	var recordId = record.id;
	if(recordId.indexOf('Ext.')!=-1){
		recordId = record.get('id');
	}
	Ext.Ajax.request({
		params: {
			idSchemaField: recordId
		},
		url: 'schemaFieldsTreePopupDestroy.json'
	});
	schemaFieldsTree.getStore().getNodeById(recordId).remove();
	//schemaFieldsTree.getStore().load();
	//schemaFieldsTree.expandAll();
}

function modifySchemaField() {
	var record = schemaFieldsTree.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    if (record.get('leaf')) {
    	popupSchemaField(record.data , 'modify' , 'leaf', false);
    } else {
    	popupSchemaField(record.data , 'modify' , 'branch', false);	
    }
};

function validateSchemaFunc() {
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
	Ext.Ajax.request( {
		params : {
			idSchema : record.get('idSchema')
		},
		success: function (result) {
			var recordResult = Ext.JSON.decode(result.responseText);
			var msg = _message['schemaValidationSuccess'];
			if (recordResult.success == "false") {
				msg = recordResult.responseMsg;
			}
			
			new Ext.Window({
				border: false,
				height: 190,
				width: 220,
				iconCls: 'bug',
				id: 'validateSchemaResultWindowId',
				html: msg,
				layout: 'absolute',
				modal: true,
				resizable: true,
				autoScroll : true,
				bodyStyle:{"background-color":"#ffffff;padding:10px;"},		
				title: _message['validateSchema'],
				buttons : [ {
					text : _message['ok'],
					handler : function() {
						Ext.getCmp('validateSchemaResultWindowId').close();
					}
				} ]
		    }).show();
		},
		url : './schemaValidate.json'
	});
}

var schemaFieldsXMLRootMenu = Ext.create('Ext.menu.Menu', {
	items: [
	    {iconCls: 'root_add' , handler: addBranchSchemaField , text: _message['addRoot']}
	]
});

var schemaFieldsJSONRootMenu = Ext.create('Ext.menu.Menu', {
	items: [
	        {iconCls: 'schema_add_branch' , handler: addBranchSchemaField , text: _message['addBranch']},
	        {iconCls: 'schema_add' , handler: addLeafSchemaField , text: _message['addLeaf']}
	]
});
var schemaFieldsXMLMenu = Ext.create('Ext.menu.Menu', {
	items: [
	    {iconCls: 'schema_add_branch' , handler: addBranchSchemaField , text: _message['addBranch']},
	    {iconCls: 'schema_add' , handler: addLeafSchemaField , text: _message['addLeaf']},
	    {iconCls: 'schema_delete' , handler: deleteSchemaField , text: _message['delete']},
	    {iconCls: 'schema_edit' , handler: modifySchemaField , text: _message['edit']},
	   	{iconCls: 'bug' , handler: validateSchemaFunc, text: _message['validateSchema']}
	]
});

var schemaFieldsRootMenu = new Ext.menu.Menu({
	items: [
	    {iconCls: 'schema_add' , handler: addLeafSchemaField , text: _message['addItem']}
	]
});

var schemaFieldsMenu = new Ext.menu.Menu({
	items: [
	    {iconCls: 'schema_add' , handler: addLeafSchemaField , text: _message['addItem']},
	    {iconCls: 'schema_delete' , handler: deleteSchemaField , text: _message['delete']},
	    {iconCls: 'schema_edit' , handler: modifySchemaField , text: _message['edit']},
	    {iconCls: 'bug' , handler: validateSchemaFunc, text: _message['validateSchema']}
	]
});

function schemaFieldsHandler(node,record,event) {
	node.select(record,true);
	var itemSelected =  schemasGrid.getSelectionModel().getSelection()[0];
	if (!itemSelected) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
	
	var idStreamType = itemSelected.get('idStreamType');
	
	if (idStreamType == 1 || idStreamType == 2 || idStreamType == 5) {
		//1=XML 2=EXI 5=JSON
		if (record.get('id') == 0) {
			if (!record.hasChildNodes()) {
				if (idStreamType == 5) {
					schemaFieldsJSONRootMenu.showAt(event.getPageX(),event.getPageY());
				}else{
					schemaFieldsXMLRootMenu.showAt(event.getPageX(),event.getPageY());
				}
			} else if (idStreamType == 5) {
				schemaFieldsJSONRootMenu.showAt(event.getPageX(),event.getPageY());
			}
		} else {
			schemaFieldsXMLMenu.showAt(event.getPageX(),event.getPageY());
		}
		
	} else {
		
		if (schemaFieldsTree.getSelectionModel().getSelection()[0].get('id') == 0) {
			schemaFieldsRootMenu.showAt(event.getPageX(),event.getPageY());
		} else {
			schemaFieldsMenu.showAt(event.getPageX(),event.getPageY());
		}
	}
}

//schemaFieldsTree.on('itemclick' , schemaFieldsHandler);
//schemaFieldsTree.on('itemcontextmenu' , schemaFieldsHandler);