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

Ext.define('schemaExtraCheck', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'idCheckType', type:'int'},
        {name: 'name', type:'string'},
		{name: 'description', type:'string'},
		{name: 'type', type:'string'},
		{name: 'value', type:'string'},
		{name: 'systemRule', type:'boolean'},
		{name: 'regExp', type:'boolean'},
		{name: 'extraCheckType', type:'string'}
    ],	
	idProperty:'idCheckType'
});
function popupSchemaExtraCheck() {
	
	var storeSchemaExtraCheck = new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		model: 'schemaExtraCheck',
		idProperty:'idCheckType',
		pageSize: 20,
		fields: ['id', 'name', 'description', 'type', 'systemRule', 'value','tokenRule'],
		proxy: {
			type: 'ajax',
			api: {
				read    : './schemaExtraCheckRead.json',
				create  : './schemaExtraCheckCreate.json',
				update  : './schemaExtraCheckUpdate.json',
				destroy : './schemaExtraCheckDestroy.json'
			},
			extraParams:{		
				idCheckType: ""            
	        },
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty:'idCheckType',
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
						storeSchemaExtraCheck.load();
					}
				}
	        }   	
	    }
	});
	storeSchemaExtraCheck.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeSchemaExtraCheck.remove();			
		}	
	});
	storeSchemaExtraCheck.load(/*{params:{start:0 , limit:10}}*/);
	
	var columnsSchemaExtraCheck = [
  		{align: 'center' , dataIndex: 'idCheckType' , header: 'Id' , sortable: true , width: 50},
  		{dataIndex: 'name' , header: 'Name' , sortable: true , flex: 1},
  		{dataIndex: 'description' , header: 'Description' , sortable: true , flex: 1},
  		{dataIndex: 'extraCheckType' , header: 'Type' , sortable: true , flex: 1, renderer: function(value, cell, record, rowIndex,
  				colIndex, store) {
  			if (record.data.value == 'MACRO'){
  				return "Macro";
            }else{
                return record.data.extraCheckType;				
            }
  		}},
  		{dataIndex : 'status', header:'Status', sortable : true, width : 50, renderer : schemaExtraCheckRenderers.iconSchemaExtraCheckRenderer}
     ];
	var tbarSchemaExtraCheck = [
	{iconCls: 'schema_extra_check_add' , handler: addCheckCode, text: 'Add Code'} , '-' ,
	    {iconCls: 'schema_extra_check_add' , handler: addSchemaExtraCheck, text: 'Add Regular Expression'} , '-' ,
	    {iconCls: 'schema_extra_check_edit' , handler: editSchemaExtraCheck, text: _message["edit"]} , '-' ,
   		{iconCls: 'schema_extra_check_delete' , handler: deleteSchemaExtraCheck, text: _message["delete"]}
   	];
	
	var schemaExtraCheckGrid = Ext.create('Ext.grid.Panel', {
		bbar:Ext.create('Ext.PagingToolbar', {
			store: storeSchemaExtraCheck,
			displayInfo: true			
		}),
		columnLines: true,
		columns: columnsSchemaExtraCheck,
		height: 468,
		id: 'schemaExtraCheckGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeSchemaExtraCheck,
		tbar: tbarSchemaExtraCheck
	})

	function addCheckCode() {	    
		var record = new schemaExtraCheckGrid.store.model(); //recordType	   
	    popupAddCheckCode(record, true);
		schemaExtraCheckGrid.store.sync(); 
	}
	
	function deleteSchemaExtraCheck() {
	    var record = schemaExtraCheckGrid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert["selectRecord"]);
	    	idFile = "";
	        return false;
	    } else if(record.get('systemRule')) {
	    	App.setAlert(false , _error["recordDeleteError"]);
			return false;
		}else{
			schemaExtraCheckGrid.store.proxy.extraParams.idCheckType = record.get('idCheckType');
	    	schemaExtraCheckGrid.store.remove(record);
	    	schemaExtraCheckGrid.store.sync();
	    	schemaExtraCheckGrid.store.load();	    	
	    }
	    
	}

	function addSchemaExtraCheck() {	    
		var record = new schemaExtraCheckGrid.store.model(); //recordType	   
	    popupAddRegExp(record, true);
		schemaExtraCheckGrid.store.sync(); 
	}

	function editSchemaExtraCheck() {
		var record = schemaExtraCheckGrid.getSelectionModel().getSelection()[0];
		
		if (!record) {
			App.setAlert(false , _alert["selectRecord"]);
			return false;
		}
		if(record.get('systemRule')) {
			App.setAlert(false , _error["recordEditError"]);
			return false;
		}
		if(record.data.extraCheckType=='Custom Code'){
			popupAddCheckCode(record, false);
		}else{
			popupAddRegExp(record, false);
		}	
		schemaExtraCheckGrid.store.sync();
	}
	new Ext.Window({
		height: 500,
		id: 'popupSchemaExtraCheck',
		items: [schemaExtraCheckGrid] ,
		layout: 'fit',
		modal: true,
		resizable: false,
	    title: _label["extraCheck"],
	    width: 880,
	    maximizable: true,
	    listeners : {
            'resize' : function(win,width,height,opt){
               if(width != 880) {
            	   storeSchemaExtraCheck.pageSize = 28;
            	   storeSchemaExtraCheck.load({params:{limit:28}});
               } else {
            	   storeSchemaExtraCheck.pageSize = 20;
            	   storeSchemaExtraCheck.load({params:{limit:20}});
               }
             }
	    }
    }).show();
}

function popupAddRegExp(record, add) {
	
	new Ext.Window({
			id: 'popupRegExp',
			items: [
				{name: 'name' , fieldLabel: _label['name'] , id: 'name' , xtype: 'textfield' , x: 5, y: 5, labelAlign: 'top',
					width: 200, value: record.get('name') },
				{ xtype: 'textfield',
					     id: 'description',
					     name: 'description',
					     fieldLabel: _label['description'],
					     width: 350, x: 220, y: 5, labelAlign: 'top', value: record.get('description') 
				},
				{ xtype: 'textfield',
				     id: 'regularExpression',
				     name: 'regularExpression',
				     fieldLabel: _label['regularExpression'],
				     x: 5, y: 60, labelAlign: 'top', width: 425, value: record.get('value') ,
				     listeners: {
				        'change': function(){
				          Ext.getCmp("btnSave").setDisabled(true);
				        }
				     }
				},
				{text:_message['validate'] , x: 440 , xtype: 'button' , width: 65 , y: 80 ,
					handler: function() {
						
						if (Ext.getCmp('regularExpression').getValue().length == 0) {
							App.setAlert(false, _error["regExpFieldRequired"]);
							return;
						}
						
						Ext.Ajax.request({ 
							params: {
								regularExpression: Ext.getCmp('regularExpression').getValue()
							},
							success: function (result) {
								var responseObj = Ext.decode(result.responseText);
								if(responseObj.success) {
									Ext.getCmp("btnSave").enable();
								}
		        				App.setAlert(true, responseObj.message);
		        				
		        			},
							url: './regularExpressionCheck.json'
						});
					}
				},
				{text:_message['loadmsg'] , x: 510, xtype: 'button', width: 55, y: 80,
					handler: function() {
						var win = new Ext.Window( {
								border : false,
								layout: 'fit',
								id : 'windowWithComboStoredRegExps',
								items : { 
									border : false, 
									width : 230,
									height : 80,
									items : {
										xtype : 'combo',
										y : 25,
										x : 15,
										displayField : 'nlsId',
										id : 'comboStoredRegExps',
										mode : 'remote',
										store : new Ext.data.JsonStore( {
											autoSave : false,
											autoLoad : false,
											fields : [ 'idRegExps', 'name', 'value', 'nlsId' ],
											proxy : {
												type : 'ajax',
												api : {
													read : "./schemaExtraCheckRead.json?type=regExps"
												},
												reader : {
													type : 'json',
													root : 'results',
													idProperty : 'idRegExps'			
												}
											}
										}),
										triggerAction : 'all',
										valueField : 'value',
										width : 200,
										listeners : {
											'select' : function(combo, record, index) {
												Ext.getCmp('regularExpression').setValue(record[0].get('value'));
												Ext.getCmp("btnSave").setDisabled(false);
												win.close();
											}
										}
									}
								},
								modal : true,
								resizable : false,
								title : _label['regExpStored']
							});
						win.show();
					}
				},
				{id: 'btnSave',
				  name: 'btnSave', disabled: record.get('idCheckType') > 0 ? false : true,
				  text: _message["save"], x: 180 , xtype: 'button' , width: 100 , y: 130 ,
					handler: function(){
						
						if (Ext.getCmp('name').getValue().length == 0) {
							App.setAlert(false,
									_alert['nameFieldRequired']);
							return;
						}

						if (Ext.getCmp('description').getValue().length == 0) {
							App.setAlert(false, _error['descFieldRequired']);
							return;
						}
						
						if (Ext.getCmp('regularExpression').getValue().length == 0) {
							App.setAlert(false, _error['regExpFieldRequired']);
							return;
						}
						
						record.set('name', Ext.getCmp('name')
								.getValue());
						record.set('description', Ext.getCmp(
								'description').getValue());
						record.set('value', Ext.getCmp('regularExpression')
								.getValue());
						record.set('type', "Regular Expression");
						record.set('systemRule', false);
						record.set('regExp', true);
						record.set('extraCheckType', "Regular Expression");
						
						if(add) {
							Ext.getCmp('schemaExtraCheckGrid').store.insert(0, record);
						}
						
						Ext.getCmp('schemaExtraCheckGrid').store.sync();
						Ext.getCmp('popupRegExp').close();
					}
				},
				{text: _message["cancel"] , x: 290 , xtype: 'button' , width: 100 , y: 130 ,
		        	handler: function(){
		        		Ext.getCmp('popupRegExp').close();
		        	}
		        }
			],
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: _label['regularExpression'],
		    width: 600,
		    height: 200
	    }).show();
	}
//Add Code	
function popupAddCheckCode(record, add) {
	var tbarTriggerCode = [
	    {iconCls: 'schema_trigger_clear_editor' , handler: clearEditor, text: _message['clearEditor']} , '-' ,
	    {iconCls: 'schema_trigger_validate' , handler: validate, text: _message["validate"]}   		
   	];
	
	function clearEditor() {	    
		Ext.getCmp('name').setValue("");
		Ext.getCmp('description').setValue("");
		Ext.getCmp('codeTa').setValue("");		
	}
	
	function validate() {		
		Ext.Ajax.request( {
			params : {
				value : Ext.getCmp('codeTa').getValue(),
				name : Ext.getCmp('name').getValue(),
				addReq:add
			},
			url : './isExtraCheckCodeValid.json',
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
				Ext.getCmp("btnSaveCode").disabled();
			}
		});	
	}
	function invokePopup(content, status) {
	    new Ext.Window({ 
			height: 160,
			width: 420,
			layout: 'absolute',
			modal: true,
			autoScroll: true,
			resizable: false,
		    title: _label["customCodeValidation"],
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
			id: 'popupAddCheckCode', 
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
					x: 5, y: 60,value: record.get('value'),
					listeners: {
				        'change': function(){
							Ext.getCmp("btnSaveCode").setDisabled(true);
				        }
				    }
				},				
				{id: 'btnSaveCode',
				  name: 'btnSaveCode',  disabled: true,text: _message["save"], x: 375 , xtype: 'button' , width: 100 , y: 300 ,
				  handler: function(){
					
					
					if (Ext.getCmp('name').getValue().length == 0) {
						App.setAlert(false,
								_alert['nameFieldRequired']);
						return;
					}

					if (Ext.getCmp('description').getValue().length == 0) {
						App.setAlert(false, _error['descFieldRequired']);
						return;
					}
					record.set('name', Ext.getCmp('name').getValue());
					record.set('description', Ext.getCmp('description').getValue());
					record.set('value', Ext.getCmp('codeTa').getValue());
					record.set('type', "Custom Code");
					record.set('systemRule', false);
					record.set('regExp', false);
					record.set('extraCheckType', "Custom Code");					
					
					if(add) {
						Ext.getCmp('schemaExtraCheckGrid').store.insert(0, record);
					}						
					Ext.getCmp('schemaExtraCheckGrid').store.sync();
					Ext.getCmp('popupAddCheckCode').close();
				  }
					
				},
				{text: _message["cancel"] , x: 479 , xtype: 'button' , width: 100 , y: 300 ,
		        	handler: function(){
		        		Ext.getCmp('popupAddCheckCode').close();
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