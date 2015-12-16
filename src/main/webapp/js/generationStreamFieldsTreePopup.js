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

var mask;
var isLinkToDbRemoved = false;
var treestore1 = new  Ext.data.TreeStore ( {
    autoSync:true,
    proxy:{
        type:'ajax'
    },
	fields : [ 
		{name : 'id', type : 'String'}, {name : 'text', type : 'String'},
		{name : 'linkToDb', type : 'String'}]	
});

//generationStreamFieldsDataStores
gsfDataStores = {
	dbTree : new Ext.data.TreeStore( {
		autoLoad : false,		
		fields : [ {name : 'sql_text', type : 'String'}, {name : 'text', type : 'String'},
			{name : 'var_name', type : 'String'}, {name : 'can_be_deleted_menu', type : 'boolean'}],	
		proxy : {
			type : 'ajax',
			extraParams:{
				//idSchema : schemasGrid.getSelectionModel().getSelection()[0] ? 
				         //schemasGrid.getSelectionModel().getSelection()[0].get("idSchema"): "-1" ,
                dbType : 1
			},
			url : 'schemaDbTreePopupRead.json'
		},
		listeners: {
				beforeload: {
	               fn: function() {	            	   
	            	   if(Ext.getCmp('dbTreeSchema') && Ext.getCmp('dbTreeSchema').isVisible()) {
	            		   mask = new Ext.LoadMask(Ext.getBody(), {msg: _message['waitMessage']});
	            		   mask.show();
	            	   }
	               }
	           },
	           load: {
	               fn: function() {
	            	   if(mask) {
	            		   mask.hide();
	            		   mask = null;
	            	   }
	               }
	           },
	           scope: this               
	        }
	})
}

var generationStreamFieldsTree = Ext.create('Ext.tree.Panel', {
	autoScroll: true,
	frame: false,
	region: 'center',
	id: 'generationStreamFieldsTree',
	store:treestore1,
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
		itemclick: function(view, record,item,index,event){
			generationStreamFieldsHandler(view,record,event);  
		},
		itemcontextmenu:function(view, record,item,index,event){
			event.stopEvent( );
			generationStreamFieldsHandler(view,record,event);  
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
						generationStreamFieldsTree.store.load();

					} 
				}
			});
		}
	},
	rootVisible: true,
	useArrows:true
});

gsftpGlobals = {
	stage : 0,
	visibilityArr : [
		/*code 1: '>' */  [0, 1, 0, 0],
		/*code 2: '<' */  [0, 1, 0, 0],
		/*code 3: 'ins' */[1, 0, 0, 0],
		/*code 4: '>=' */ [0, 1, 0, 0],
		/*code 5: '<=' */ [0, 1, 0, 0],
		/*code 6: '=' */  [0, 1, 0, 0],
		/*code 7: 'and' */[0, 1, 0, 1],
		/*code 8: 'edi' */[0, 0, 1, 0],
		/*code 9: 'add' */[0, 0, 1, 0],
		/*code 10: 'res'*/[1, 1, 0, 1],
	    /*code 11: '!='*/ [0, 1, 0, 0]
	],
	getButtonVisibility : function(button) {
		var code = button.code;
		button.setDisabled(!gsftpGlobals.visibilityArr[code - 1][gsftpGlobals.stage]);
	},
	clearConditions : function() {
		Ext.getCmp('fieldsTreeTextArea').setValue('');
		gsftpGlobals.stage = 0;
		Ext.getCmp('fieldsTreeButton8').setValue('');
		for (var i = 1; i <= 11; i++) {
			gsftpGlobals.getButtonVisibility(Ext.getCmp('fieldsTreeButton' + i));
		}
		//Ext.getCmp('queryEditorButtonsPanel').setDisabled(false);
	}
}

var editedQuery = null;

var generationStreamPanel = {
	id : 'generationStreamPanel',
	layout : 'border',
	items : [
		generationStreamFieldsTree,
		{	
			region: 'south',
			title: _label['queryGeneration'],
			collapsible: true,	
			frame : false,
			height : 200,
			layout : 'hbox',
			defaults : {
				border : false
			},
			buttonAlign : 'center',
			buttons : [
				{ text : _label['resultSet'], id : 'fieldsTreeButton10', code : 10,
					handler : function() {
						sendGenerationStreamFunction(Ext.getCmp('fieldsTreeTextArea').getValue());
					}
				},
				{ text : _label['clearEditor'], 
					handler : function() {
				    	gsftpGlobals.clearConditions();
					}
				},
				{
					text : _label['editQuery'],
					handler : function() {
					    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
					    if (!record) {
					    	App.setAlert(false, _alert['selectRecord']);
					        return false;
					    };
	                    Ext.Ajax.request({
	                        success: function (response, opts) {
	                    		if (response.responseText == '') {
	                    			App.setAlert(false, _alert['schemaWithoutLinkedToDbFields']);
	                    		} else {
									new Ext.Window({ 
										height: 350,
										width: 500,
										layout: 'fit',
										modal: true,
										resizable: false,
										title: _label['editQuery'],
										buttonAlign: 'center',
										items: [
											{
												xtype : 'textarea',
												id : 'editQueryTA',
												value : response.responseText,
												enableKeyEvents : true,
												listeners : {
													keypress : function() {
														Ext.getCmp('saveQueryButton').setDisabled(true);
													}
												}
											}
		
										],
										buttons: [
								            {text: _label['validateQuery'], xtype: 'button', width: 100,
								                handler: function() {
													Ext.Ajax.request({
		                        						success: function (resp, opts) {
															var recordResult = Ext.JSON.decode(resp.responseText);
                                                            if (eval(recordResult.success)) {
                                                                Ext.getCmp('saveQueryButton').setDisabled(false);
                                                                App.setAlert(App.STATUS_OK, _message['successSqlValidation']);
                                                            } else {
                                                                App.setAlert(App.STATUS_ERROR, recordResult.message);
                                                            }
		                        						},
								                        url: './schemaLinkedFields.json?action=validateSql&idSchema=' + encodeURIComponent(record.get('idSchema')) +
								                            '&query=' + encodeURIComponent(Ext.getCmp('editQueryTA').getValue())
													});
								                }
								            },
											{text: _label['saveQuery'], xtype: 'button', width: 100, 
								            	id : 'saveQueryButton',
								            	disabled : true,
												handler: function() {
													Ext.Ajax.request({
		                        						success: function (resp, opts) {
															App.setAlert(true, _alert['sqlSaved']);
														},
		                        						params : {
		                        							action : 'setSqlByIdSchema',
		                        							idSchema : record.get('idSchema'),
		                        							query : Ext.getCmp('editQueryTA').getValue(),
		                        							meta : Ext.getCmp('fieldsTreeTextArea').getValue(),
		                        							isCustom : false
		                        						},
								                        url: './schemaLinkedFields.json'
													});
								            		//Ext.getCmp('queryEditorButtonsPanel').setDisabled(true);
								            		this.ownerCt.ownerCt.close();
												}
	
											}, 
											{text: _label['deleteQuery'], xtype : 'button', width : 100,
												handler : function() {
													Ext.Ajax.request({
		                        						success: function (resp, opts) {
															if (eval(resp.responseText)) {
																App.setAlert(true, _alert['sqlDeleted']);
															}
														},
		                        						params : {
		                        							action : 'deleteSqlByIdSchema',
		                        							idSchema : record.get('idSchema')
		                        						},
								                        url: './schemaLinkedFields.json'
													});
								            		//Ext.getCmp('queryEditorButtonsPanel').setDisabled(false);
								            		this.ownerCt.ownerCt.close();
												}
											},
											{text: _message['cancel'], xtype: 'button', width: 100, 
												handler: function() {
													this.ownerCt.ownerCt.close();
												}
											}
										]
									}).show();
								}
	                        },
	                        url: './schemaLinkedFields.json?action=getSqlFromMeta&idSchema=' + record.get('idSchema') +
	                            '&query=' + Ext.getCmp('fieldsTreeTextArea').getValue()
	                    });
					}
				}
			],
			items : [
				{
					margin : 5,
					xtype : 'textarea',
					height : 120,
					readOnly : true,
					id : 'fieldsTreeTextArea',
					flex : 3
				},
				{
					flex : 7,
					//id : 'queryEditorButtonsPanel',
				    layout: {
				        type : 'table',
				        columns : 3
				    },
				    defaults: {
		        		margin : 5,
		        		width : 32,
		        		listeners : {
				    		afterrender : function() {
				    			for (var i = 1; i <= 11; i++) {
				    				gsftpGlobals.getButtonVisibility(Ext.getCmp('fieldsTreeButton' + i));
				    			}
				    		},
		        			click : function(button) {
				    			var area = Ext.getCmp('fieldsTreeTextArea');
				    			switch (button.code) {
				    				case 1:
				    					area.setValue(area.getValue() + ' > ');
				    					break;
				    				case 2:
				    					area.setValue(area.getValue() + ' < ');				    					
				    					break;
				    				case 3:
				    					var record = Ext.getCmp('generationStreamFieldsTree').getSelectionModel().getSelection()[0];
				    					if (!record) {
				    						App.setAlert(false, _alert['chooseTree']);
				    						return;
				    					}
				    					var link = record.data.linkToDb; 
		    							if (!link) {
		    								App.setAlert(false, _alert['fieldWithoutDbLink']);
				    						return;
				    					}
		    							area.setValue(area.getValue() + link);
				    					break;
				    				case 4:
				    					area.setValue(area.getValue() + ' >= ');
				    					break;
				    				case 5:
				    					area.setValue(area.getValue() + ' <= ');				    					
				    					break;
				    				case 6:
				    					area.setValue(area.getValue() + ' = ');				    					
				    					break;
				    				case 7: 
				    					gsftpGlobals.stage = -1;
				    					Ext.getCmp('fieldsTreeButton8').setValue('');
				    					area.setValue(area.getValue() + ';\n');
				    					break;
				    				case 9:
				    					if (!Ext.getCmp('fieldsTreeButton8').validate()) {
				    						return;
				    					}
				    					area.setValue(area.getValue() + "'" + Ext.getCmp('fieldsTreeButton8').getValue() + "'");
				    					break;
				    				case 11:
				    					area.setValue(area.getValue() + ' != ');				    					
				    					break;
				    			}
				    			if (++gsftpGlobals.stage >= 4) gsftpGlobals.stage = 0;
				    			for (var i = 1; i <= 11; i++) {
				    				gsftpGlobals.getButtonVisibility(Ext.getCmp('fieldsTreeButton' + i));
				    			}		
		        			}
 		        		}
		    		},
		    		items : [
						//1st row
						{
							xtype : 'button',
							id : 'fieldsTreeButton1',
							text : '>',
							code : 1
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton2',
							text : '<',
							code : 2
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton3',
							text : _button['INSERT'],
							width : 60,
							code : 3
		
						},
						//2nd row
						{
							xtype : 'button',
							id : 'fieldsTreeButton4',
							text : '>=',
							code : 4
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton5',
							text : '<=',
							code : 5
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton11',
							text : '!=',
							code : 11
						},
						//3d row
						{
							xtype : 'button',
							id : 'fieldsTreeButton6',
							text : '=',
							code : 6
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton7',
							text : _button['AND'],
							colspan : 2,
							code : 7,
							width : 60
						},
						//4th row
						{
							xtype : 'textfield',
							id : 'fieldsTreeButton8',
							colspan : 2,
							width : 75,
							submitValue : false,
							allowBlank : false,
							code : 8
						},
						{
							xtype : 'button',
							id : 'fieldsTreeButton9',
							text : _button['ADD'],
							width : 50,
							code : 9
						}
					]
				}
			]
		}
	]
}

function addBranchGenerationStreamField() { //add root
	var idSchema = generationStreamGrid.getSelectionModel().getSelection()[0].get('idSchema');
	var sModel = generationStreamFieldsTree.getSelectionModel();
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
			addRecord.parentId = idParent;
			popupTrackField(addRecord, 'add', 'branch', idSchema);		
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
			//record.expand();
			//generationStreamFieldsTree.getSelectionModel().select(newNode);
		}
		
	});
}

function addLeafGenerationStreamField() {
	var idSchema = generationStreamGrid.getSelectionModel().getSelection()[0].get('idSchema');
	var record = generationStreamFieldsTree.getSelectionModel().getSelection()[0];
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
			popupTrackField(addRecord , 'add' , 'leaf', idSchema);
			var newNode = {id: idSchemaField , text: addRecord.name , leaf: true};
		    if (record.get('leaf')) {
		    	record.parentNode.appendChild(newNode);
		    } else {
		    	record.appendChild(newNode);
		    }
		    //record.expand();
		    //generationStreamFieldsTree.getSelectionModel().select(newNode);
			//schemaFieldsTree.getNodeById(idSchemaField).select(newNode);
		},
		url: 'schemaFieldsTreePopupCreate.json'
	});
}

function deleteGenerationStreamField() {
	var record = generationStreamFieldsTree.getSelectionModel().getSelection()[0];
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
	generationStreamFieldsTree.getStore().getNodeById(recordId).remove();
	//schemaFieldsTree.store.sync();
	//generationStreamFieldsTree.collapseAll();
	//generationStreamFieldsTree.expandAll();
}

function modifyGenerationStreamField() {
	var record = generationStreamFieldsTree.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    var schemId = generationStreamGrid.getSelectionModel().getSelection()[0].get("idSchema");
    if (record.get('leaf')) {
    	popupTrackField(record.data , 'modify' , 'leaf', schemId);
    } else {
    	popupTrackField(record.data, 'modify', 'branch', schemId);
    }
};

function validateGenerationStreamFunc() {
    var record = generationStreamGrid.getSelectionModel().getSelection()[0];
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
				bodyStyle : 'padding:10px;',		
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

var generationStreamFieldsXMLRootMenu = Ext.create('Ext.menu.Menu', {
	items: [
	    {iconCls: 'schema_add' , handler: addBranchGenerationStreamField , text: _message['addRoot']}
	]
});

var generationStreamFieldsJSONRootMenu = Ext.create('Ext.menu.Menu', {
	items: [
	        {iconCls: 'schema_add_branch' , handler: addBranchGenerationStreamField , text: _message['addBranch']},
	        {iconCls: 'schema_add' , handler: addLeafGenerationStreamField , text: _message['addLeaf']}
	]
});
var generationStreamFieldsXMLMenu = Ext.create('Ext.menu.Menu', {
	items: [
	    {iconCls: 'schema_add_branch' , handler: addBranchGenerationStreamField , text: _message['addBranch']},
	    {iconCls: 'schema_add' , handler: addLeafGenerationStreamField , text: _message['addLeaf']},
	    {iconCls: 'schema_delete' , handler: deleteGenerationStreamField , text: _message['delete']},
	    {iconCls: 'schema_edit' , handler: modifyGenerationStreamField , text: _message['edit']},
	   	{iconCls: 'bug' , handler: validateGenerationStreamFunc, text: _message['validateSchema']}
	]
});

var generationStreamFieldsRootMenu = new Ext.menu.Menu({
	items: [
	    {iconCls: 'schema_add' , handler: addLeafGenerationStreamField , text: _message['addItem']}
	]
});

var generationStreamFieldsMenu = new Ext.menu.Menu({
	items: [
	    {iconCls: 'schema_add' , handler: addLeafGenerationStreamField , text: _message['addItem']},
	    {iconCls: 'schema_delete' , handler: deleteGenerationStreamField , text: _message['delete']},
	    {iconCls: 'schema_edit' , handler: modifyGenerationStreamField , text: _message['edit']},
	    {iconCls: 'bug' , handler: validateGenerationStreamFunc, text: _message['validateSchema']}
	]
});

function generationStreamFieldsHandler(node,record,event) {
	node.select(record,true);
	var itemSelected =  generationStreamGrid.getSelectionModel().getSelection()[0];
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
					generationStreamFieldsJSONRootMenu.showAt(event.getPageX(),event.getPageY());
				}else{
					generationStreamFieldsXMLRootMenu.showAt(event.getPageX(),event.getPageY());
				}
			} else if (idStreamType == 5) {
				generationStreamFieldsJSONRootMenu.showAt(event.getPageX(),event.getPageY());
			}
		} else {
			generationStreamFieldsXMLMenu.showAt(event.getPageX(),event.getPageY());
		}
		
	} else {
		if (generationStreamFieldsTree.getSelectionModel().getSelection()[0].get('id') == 0) {
			generationStreamFieldsRootMenu.showAt(event.getPageX(),event.getPageY());
		} else {
			generationStreamFieldsMenu.showAt(event.getPageX(),event.getPageY());
		}
	}
}

function popupTrackField(record, action, fieldType, schemId) {

	//'record' is substituted to another one in lowest ajax request
	
    var popupWidth = 695;
    var popupHeight = 470;
	var idFieldType = 4;
	if (fieldType == 'branch') {
		if (record.parentId == undefined || record.parentId == 0) {
			popupWidth = 280;
			popupHeight = 420;
		}
		idFieldType = 1;
	}
	var winClose = false;
    new Ext.Window( {
		border : true,
		height : popupHeight,
		disabled : true,
		id : 'popupSchemaField',
		items : [
		         {value:_label['name']  , x: 5 , xtype: 'displayfield' , y: 5},
		         {id: 'name' , x: 5 , xtype: 'textfield' , width: 150 , y: 25},       
		 		 { 	
		         	id : 'linkToDbTextField',
		 			labelAlign : 'top',
		 			y : 60,
		 			x : 5,
		 			fieldLabel : _label['linkToDbSelected'],
		 			xtype : 'textfield',
		 			width : 250,
		 			disabled: true
		 		 },
		         {value: _label['description'] , id:'lblDescription', x: 5 , xtype: 'displayfield' , y: 110},
		         {height: 250 , id:'description' , x: 5 , xtype: 'htmleditor' , width: 250 , y: 130},
		         {value: _label['db_fields'] , id:'lblDBFields',  x: 270 , xtype: 'displayfield' , y: 5},
		         {
		             xtype : 'treepanel',
		             id : 'dbTreeSchema',
		             x: 270 ,
		             y: 25 ,
		             width: 400,
		             height: 360,            
		             disabled : false,
		             collapsible : false,
		             store : gsfDataStores.dbTree,
		             rootVisible : false,
		             useArrows : false,
		             scroll : false,
		             viewConfig : {
		                 style : {
		                     overflowY : 'scroll'
		                 }
		             },
		             listeners : {
		             	afterrender : function() {
		             		if (gsfDataStores.dbTree.isLoading()) {
		 						var mask = new Ext.LoadMask(Ext.getBody(), 
		 							{	msg : _message['waitMessage'], 
		 								store : gsfDataStores.dbTree
		 							}
		 						);
		 						mask.show();
		 					}
		             	},
		             	itemclick : function(view, rec, item, index, e) {
		             		isLinkToDbRemoved = false;
		 					Ext.create('Ext.menu.Menu', {
		 						items: [
		 						        {iconCls: 'alertIcon', 
		 						        	handler: function() {
		 										Ext.Ajax.request({
		                         					success: function (resp, opts) {
		 												var tree = generationStreamFieldsTree;													
		 												tree.getStore().load();
		 												gsfDataStores.dbTree.load();
		 					                    		if (gsfDataStores.dbTree.isLoading()) {
		 													var mask = new Ext.LoadMask(Ext.getBody(), 
		 														{	msg : _message['waitMessage'], 
		 															store : gsfDataStores.dbTree
		 														}
		 													);
		 													mask.show();
		 												}
		 					                    		isLinkToDbRemoved = true;
		 					                    		record.linkToDb = '';
		 					                    		Ext.getCmp('linkToDbTextField').setValue() == '';
		 												App.setAlert(true, _label['deleteLinkToDb']);
		 											},
		                         					params : {
		                         						action : 'deleteLinkToDb',
		                         						idField : record.idSchemaField
		                         					},
		 						                    url: './schemaLinkedFields.json'
		 										});
		 						        	}, 
		 						        	disabled: !eval(rec.get('can_be_deleted_menu')), 
		 						        	text: _label['deleteLinkToDb']
		 						      }
		 						]
		 					}).showAt(e.getXY())
		             	}
		             }
		         },
		         {xtype: 'button', id:'btnSave', text: _message["save"] , x: 225 , width: 100 , y: 400 ,
		                handler: function() {
		    
		                    if (Ext.getCmp('name').getValue() == "") {
		                        App.setAlert(false , _alert['nameFieldRequired']);
		                        return false;
		                    }
		                    var re = new RegExp('[^-_A-Za-z0-9]');
		                    if(Ext.getCmp('name').getValue().match(re)){
		                        App.setAlert(false , _message['nameFieldInvalidFormate']);
		                        return false;
		                    }
		                    //checks if first letter of field is (a..z or A..Z or _)
		                    if (!validateField(Ext.getCmp('name').getValue(),/^[a-zA-Z_]/)) {
		                        Ext.MessageBox.show({
		                            title: _label["alert"],
		                            buttons: Ext.MessageBox.OK,
		                            msg: _message["nameFieldInvalidFormate"],
		                            icon: Ext.MessageBox.WARNING
		                        });
		                        return false;
		                    }
		    		                   
		                    //link to the selected field of the Database fields tree.                    
		                    var lCmp = Ext.getCmp('dbTreeSchema');
		                    var linkToDbSelection = (lCmp && lCmp.getSelectionModel() && lCmp.getSelectionModel().getSelection()[0])
		                    	? lCmp.getSelectionModel().getSelection()[0].get("id") : record.linkToDb;
		                    	
		                    if(isLinkToDbRemoved || (fieldType == 'leaf' && (linkToDbSelection == null || linkToDbSelection.indexOf('.') == -1))) {
		                    	App.setAlert(false , _error['invalidLinkFieldError']);
		                    	return false;
		                    }
		                    
		                    //if value in the textfield 'DB field' of 'Field detail' tab is deleted -> delete linkToDb in tree
		                    if (Ext.getCmp('linkToDbTextField').getValue() == '' && record.linkToDb != null && record.linkToDb != '') {
		                    	linkToDbSelection = '';
		                    }
		  
		                    Ext.Ajax.request({
		                        params: {
		                            idSchema: record.idSchema,
		                            idSchemaField: record.idSchemaField,
		                            idFieldType: idFieldType,
		                            name: Ext.getCmp('name').getValue(),
		                            description: Ext.getCmp('description').getValue(),                                      
									linkToDb : linkToDbSelection,
									isBranch : fieldType == 'branch' ? true : false
		                        },
		                        success: function (response) {
		                        	action = 'save';
		                            Ext.getCmp('popupSchemaField').close();
		                            		                            
		                            generationStreamFieldsTree.store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + record.idSchema;
		                            loadingStreamFieldsTree.store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + record.idSchema;
		                            
		            	    	    Ext.Ajax.request({
		            	    			params : {
		            	    				idSchema : record.idSchema
		            	    			},
		            	    			success: function (result) {
		            	    				jsonResp = Ext.JSON.decode(result.responseText);
		            	    				treestore1.setRootNode({
		            	    					id: '0',
		            	    					text: jsonResp.name,
		            	    					leaf: false,
		            	    					expanded: true 
		            	    				});
		            	    				loadingStreamFieldsTree.store.setRootNode({
		            	    					id: '0',
		            	    					text: jsonResp.name,
		            	    					leaf: false,
		            	    					expanded: true 
		            	    				});
		            	    	       },
		            	    		   url : './getSchemaNameById.json'
		            	    		});
		                        },
		                        url: './schemaFieldsPopupUpdate.json'
		                    });
		                }
		            },
		            {xtype: 'button', id:'btnCancel', text: _message["cancel"] , x: 330, width: 100 , y: 400 ,
		                handler: function() {		                	                	
		                    Ext.getCmp('popupSchemaField').close();		                   
		                }
		            }
		     ],
		layout : 'absolute',
		listeners : {
			beforeclose : function(panel, options) {
				winClose = true;
				if (action == 'add') {
					deleteGenerationStreamField();
				}
			},
			beforedestroy : function(panel, options) {
				if (winClose == false) {
					if (action == 'add') {
						deleteGenerationStreamField();
					}
				}
			}
        },
        modal: true,
        resizable: false,
        title: _label['trackField'],
        width: popupWidth
    }).show();

    if(fieldType == 'branch') {
    	if(record.parentId == undefined || record.parentId == 0) {
    		Ext.getCmp('dbTreeSchema').hide();
    		Ext.getCmp('linkToDbTextField').hide();
    		Ext.getCmp('lblDBFields').hide();
    		Ext.getCmp('lblDescription').setPosition(5,60);
    		Ext.getCmp('description').setPosition(5,80);
    		Ext.getCmp('btnSave').setPosition(25, 350);
    		Ext.getCmp('btnCancel').setPosition(135, 350);
    	}
    }
    var recordId;
    if(record.data!='undefined'){
        recordId = record.id;
    }else{
        recordId = record.get('id');
    }

    Ext.Ajax.request({
        params: {
            idSchemaField: recordId
        },
        success: function (response) {
            record = Ext.decode(response.responseText);
            Ext.getCmp('name').setValue(record.name);
            Ext.getCmp('linkToDbTextField').setValue(record.linkToDb);
            Ext.getCmp('description').setValue(record.description);
            Ext.getCmp("popupSchemaField").setDisabled(false);
                        
            Ext.Ajax.request( {
                params : {
                    schemaId : schemId
                },
                url : './schemaFieldIsForecasted.json',
                success : function(result) {
                    recordResult = Ext.JSON.decode(result.responseText);
                    if (Ext.getCmp("forecastingSlidesPanel")) {
                    	Ext.getCmp("forecastingSlidesPanel").setDisabled(
                        	!eval(recordResult.isForecasted));
                    }
                }
            });
           
				gsfDataStores.dbTree.proxy.extraParams.idSchema = schemId;
				gsfDataStores.dbTree.proxy.extraParams.fieldId = recordId;
				gsfDataStores.dbTree.proxy.extraParams.isBranch = (fieldType == 'branch') ? true : false;
				gsfDataStores.dbTree.setRootNode({
					id: '0',
					leaf: false,
					expanded: true 
				});	
				var j = 0;
				var counter = 0;
				var check = function() { 
					var node = gsfDataStores.dbTree.getNodeById(record.linkToDb);
					if (node) {
						node.bubble(function(node) {
							//set green color to connected node only to the leaf node
							if (j++ == 0) { 
								node.set('cls', 'linked_node_to_db_green');
							}
							node.expand();
						});			
					} else {
						if (counter++ < 50) Ext.defer(check, 100);
					}
				};
				check.call(this);
            
        },
        url: './schemaFieldsPopupRead.json'
    });
}