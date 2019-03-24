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

macrosDataStores = {
	schemaTree : new Ext.data.TreeStore( {
		autoSync : true,
		fields : [ {name : 'uniqueName', type : 'String'}, {name : 'nodePath', type : 'String'},
			{name : 'fieldType', type : 'String'},
			{name : 'id', type : 'String'}, {name : 'text', type : 'String'}],	
		autoLoad : false,
		proxy : {
			type : 'ajax'
		}
	}),
	dbTree : new Ext.data.TreeStore( {
		autoLoad : false,		
		fields : [ {name : 'sql_text', type : 'String'}, {name : 'text', type : 'String'},
			{name : 'var_name', type : 'String'}],	
		proxy : {
			type : 'ajax',
			extraParams:{
				idSchema : schemasGrid.getSelectionModel().getSelection()[0] ? 
				         schemasGrid.getSelectionModel().getSelection()[0].get("idSchema"): "-1" ,
                dbType : "2"
			},
			url : 'schemaDbTreePopupRead.json' 
		}
	})
};

macrosUtils = {
	arrayAsString : function(arr) {
		var res = '';
		for (var i = 0; i < arr.length; i++) {
			res += arr[i];
		}
		return res;
	},
	
	activateMacro :	function (macroId, bIsActive) {	
		Ext.Ajax.request( {
			params : {
				macroId : macroId,
				isActive : (eval(bIsActive) ? 1 : 0)
			},
			url : './macrosIsActive.json',
			success : function (result) {
				recordResult = Ext.JSON.decode(result.responseText);
				if (eval(recordResult.success)) {
					App.setAlert(true, recordResult.msg);
				} else {
					callAlert(recordResult.msg);
				}
			}
		});
	}
};

Ext.define('macros', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idMacro', type:'int' }, 
		{name: 'name', type:'String'},
		{name: 'rule', type:'String'},
		{name: 'ruleSimple', type:'String'},
		{name: 'description', type:'String'},		
		{name: 'idSchema', type:'int'},
		{name: 'vars', type : 'String'},
		{name: 'isActive', type:'int'},
		{name: 'errorType', type:'int'}
    ],
    idProperty:'idMacro'
});

var storeMacros = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'macros',
	proxy: {
		type: 'ajax',
        api: {
			read    : './macrosRead.json',
			create  : './macrosCreate.json',
			update  : './macrosUpdate.json?_method=put',
			destroy : './macrosDestroy.json?_method=delete&macId='
		},
		 extraParams:{
			schemaId: schemasGrid.getSelectionModel().getSelection()[0] ? schemasGrid.getSelectionModel().getSelection()[0].get("idSchema"): "0"
            
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
			writeAllFields:true
		}
	},
	
	listeners: {
    	write: function(store, operation){
			var respText = operation.response.responseText;
			if (respText) {
					var responseObj = Ext.decode(respText);
					App.setAlert(true, responseObj.message);
					if (responseObj.message == 'Record inserted successfully') {
						Ext.getCmp('addMacro').close();
					}
			}               
        }		
    }    
});

storeMacros.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.message.indexOf('Error')!=-1){
				Ext.Msg.alert("" , _error['connectionError']);
			} else{
				 App.setAlert(false , _error['nameExistError']);
				 if (operation.action == 'create') {
					
					 macroGrid.store.load({ 
							params:{
								schemaId: schemasGrid.getSelectionModel().getSelection()[0].get("idSchema")
					    	}
						});				 					
				} else {					
					storeMacros.remove();
				} 
			}
		}
	}	
});

//Declaring global variables
var macroGrid;
var macrosVarsToSave = new Array();

function macro() {
    	var record = schemasGrid.getSelectionModel().getSelection()[0];
    	
    	if (!record) {
    		App.setAlert(false , _alert["selectRecord"]);
    		return false;
    	}  
    	
    	editorSchemas.cancelEdit();
    	
    	/*if (record.get("loadedXSD")) {
        	App.setAlert(false , _error_ee["schemaLoadFieldsError"]);
            return false;
        };
        
        editorSchemas.cancelEdit();
        */
        
        Ext.Ajax.request({
    		params : {
    			idSchema : record.get('idSchema')
    		},
    		success: function (result) {    			
    			jsonResp = Ext.JSON.decode(result.responseText);    			
    			    			
    			if ( jsonResp instanceof Array) {
    				
    				if(record.get("idStreamType") == 5) {
    					
    					if(jsonResp.length == 0) {
    						App.setAlert(false , _error["schemaEmptyFieldError"]);
    						return false;
    					} else {
    						popupMacro();
    					}
    				} else if(record.get("idStreamType") == 1 || record.get("idStreamType") == 2) {

    					if(jsonResp[0].hasOwnProperty("children") && jsonResp[0].children.length == 0) {
    						App.setAlert(false , _error["schemaEmptyFieldError"]);
    						return false;
    					} else{    					
    						popupMacro();
    					}
    				} else if(jsonResp.length > 0) {
    						popupMacro();    					
    				} else {
    					App.setAlert(false , _error["schemaEmptyFieldError"]);
    					return false;
    				}
    			} else{ 
    				App.setAlert(false , _error["schemaEmptyFieldError"]);
    				return false;
    			} 		
           },
    	   url : './schemaFieldsTreePopupRead.json'
    	});        
    	
    	var columnsMacro = [
	        {dataIndex: 'idMacro'  , header: "ID" , width: 65},
	        {dataIndex: 'name' , header: _label["name"] , width: 180 },
	        {dataIndex: 'ruleSimple', header: _label["rule"] ,  width: 340 },
			{dataIndex: 'isActive' , header : _label['active'], width: 50 , sortable: true, renderer : function(value, cell, record) {
				var res = '<input type="checkbox"';
				if (eval(value))
					res += 'checked="checked"';
				res += 'onclick="macrosUtils.activateMacro(\'' + record.data.idMacro + '\', this.checked)"';
				res += ' >';
				return res ;
			}}
        ];
    	
    	var tbarMacro = [
        {iconCls: 'schema_macroAdd' , handler: addMacro,  text: _message["add"]} , '-' ,
        {iconCls: 'schema_macroDelete' ,  text: _message["delete"], handler: cancelMacro} , '-' ,
        {iconCls: 'schema_macroEdit' ,  text: _message["edit"], handler: editMacro}
        ];    	
    	
    	macroGrid = new Ext.grid.Panel({
    		columns: columnsMacro,
            frame: false,
            border: false,
    		id: 'macroGrid',
    		selModel: Ext.create('Ext.selection.RowModel', { 
				mode:'SINGLE'
			}),
    		title: false,
    		tbar: tbarMacro,
    		viewConfig: {forceFit: true},
    		height: 357,
    		store: storeMacros
    	});

		//loading to display the macros of schema selected    	
		storeMacros.load({
		    params:{
				schemaId: schemasGrid.getSelectionModel().getSelection()[0].get("idSchema")
		    }
		});
		
    	function cancelMacro () {
    		var index = macroGrid.getSelectionModel().getSelection()[0];
    		if (!index) {
    			Ext.Msg.alert('', _alert["errorSelectRecord"]);
    			return false;
    			
    		}
    		macroGrid.store.remove(index);
    		macroGrid.store.sync();
    	};

    	function popupMacro() {
    		//Main Macro Panel
    		var macroPanel = new Ext.form.Panel({ id: 'macroPanel', items: macroGrid });
    		new Ext.Window({ height: 390 , width: 665 , id: 'macro_win' , title: 'Macro' , modal: true , items: macroPanel}).show();
    	};

//  	Add Macro
    	function addMacro(){
    		new Ext.Window({
    			id: 'addMacro',
    			title: _message_ee["addMacro"],
    			layout: 'absolute',
    			height: 520,
    			width: 670,
    			resizable: false,
    			tbar: {
    				xtype: 'toolbar',
					items: [
							{
								xtype: 'button',
								text:  _label["clearEditor"],
								iconCls: 'clear',
								id: 'clearEditor',
								handler: 
									function clearEditor(){									
									Ext.getCmp("string").setText('STRING');
									Ext.getCmp("ruleText").setValue('');
									Ext.getCmp("jmc").setValue('');
									Ext.getCmp("macroName").setValue('');
									Ext.getCmp("description").setValue('');
									Ext.getCmp("if").enable();
									Ext.getCmp("then").disable();
									Ext.getCmp("endIf").disable();
									Ext.getCmp("string").disable();
									Ext.getCmp("value").disable();
									Ext.getCmp("stringT").disable();
									Ext.getCmp("valueT").disable();
									Ext.getCmp("errorT").disable();
									Ext.getCmp("error").disable();
									Ext.getCmp("AND").disable();
									Ext.getCmp("OR").disable();
									Ext.getCmp("(").disable();
									Ext.getCmp(")").disable();
									Ext.getCmp("+").disable();
									Ext.getCmp("-").disable();
									Ext.getCmp("*").disable();
									Ext.getCmp("/").disable();
									Ext.getCmp("different").disable();
									Ext.getCmp("equal").disable();
									Ext.getCmp("<").disable();
									Ext.getCmp(">").disable();
									Ext.getCmp("save").disable();
									Ext.getCmp("null").disable();
									Ext.getCmp("empty").disable();
									Ext.getCmp("stringT").setValue('');
									macroTreeEnable(false);
									Ext.getCmp("valueT").setValue('');
									Ext.getCmp("errorT").setValue('');
									Ext.getCmp("stringT").setValue('');
									Ext.getCmp("expertMod").enableToggle = true;
									Ext.getCmp("expertMod").toggle(false); 
									macrosVarsToSave = new Array();
								}   	
							}, '-' ,
							{
								xtype: 'button',
								text: _message["validate"],
								iconCls: 'bug',
								id: 'compile',
								handler: function () {								
									Ext.Ajax.request( {
										params : {
											varList : macrosUtils.arrayAsString(macrosVarsToSave),
											rule : Ext.getCmp("jmc").getValue(),
											schemaId : schemasGrid.getSelectionModel().getSelection()[0].get("idSchema")
										},
										url : './macrosValidate.json',
										success: function (result) {
											recordResult = Ext.JSON.decode(result.responseText);
											var resHtml = _message_ee["macroCorrectly"];
											if (eval(recordResult.success)) {
												Ext.getCmp("save").enable();
												Ext.getCmp("if").disable();
												Ext.getCmp("then").disable();
												Ext.getCmp("endIf").disable();
												Ext.getCmp("string").enable();
												Ext.getCmp("value").disable();
												Ext.getCmp("error").disable();
												Ext.getCmp("AND").disable();
												Ext.getCmp("OR").disable();
												Ext.getCmp("(").disable();
												Ext.getCmp(")").disable();
												Ext.getCmp("+").disable();
												Ext.getCmp("-").disable();
												Ext.getCmp("*").disable();
												Ext.getCmp("/").disable();
												Ext.getCmp("different").disable();
												Ext.getCmp("equal").disable();
												Ext.getCmp("<").disable();
												Ext.getCmp(">").disable();
												Ext.getCmp("null").disable();
												Ext.getCmp("empty").disable();
												Ext.getCmp("string").disable();
											} else {
												resHtml = '<div style="width:100%;height:100%;overflow:auto;">' 
													+ _message_ee["macroIncorrectly"]
													+ '<br><br>' + recordResult.errMsg
													+ '</div>';
											}
											var win = new Ext.Window({
												title: _message["validate"],
												iconCls: 'bug',
												width: 550,
												height:300,
												resizable: true,
												layout: 'fit',
												plain:true,
												modal:true,
												bodyStyle:'padding:5px;',
												buttonAlign:'right',
												html: resHtml,
												buttons:[{text:_message["close"] , handler: function(){win.close();}}]
											});
											win.show();
										}
								 	});	
								}
							},'-',
							{
								xtype: 'button',
								text: _label["expertMod"],
								iconCls: 'lock',
								itemId: 'Expert Modality',
								id: 'expertMod',
								pressed: false,
								enableToggle:true,
								toggleHandler: function expertMode(btn, pressed){
									if(pressed){
										//Ext.getCmp("jmc").readOnly=false;
										//Ext.getCmp("jmc").getEl().setStyle('color:green');
										//Ext.getCmp("jmc").getEl( ).applyStyles("{color:green}");
										//Ext.apply(Ext.getCmp("jmc"),'style:{color:green}');
										//Ext.getCmp("jmc").style.color="green";
										/* Ext.getCmp("lbljmc").setVisible(true);
										Ext.getCmp("jmc").setVisible(true);
										Ext.getCmp("lblrule").setVisible(false);
										Ext.getCmp("ruleText").setVisible(false);*/									
											
										Ext.getCmp("lbljmc").show();
										Ext.getCmp("jmc").show();
										Ext.getCmp("lblrule").hide();
										Ext.getCmp("ruleText").hide();
										
										Ext.getCmp("if").disable();
										Ext.getCmp("then").disable();
										Ext.getCmp("endIf").disable();
										Ext.getCmp("string").enable();
										Ext.getCmp("value").disable();
										Ext.getCmp("error").disable();
										Ext.getCmp("AND").disable();
										Ext.getCmp("OR").disable();
										Ext.getCmp("(").disable();
										Ext.getCmp(")").disable();
										Ext.getCmp("+").disable();
										Ext.getCmp("-").disable();
										Ext.getCmp("*").disable();
										Ext.getCmp("/").disable();
										Ext.getCmp("different").disable();
										Ext.getCmp("equal").disable();
										Ext.getCmp("<").disable();
										Ext.getCmp(">").disable();
										Ext.getCmp("save").disable();
										Ext.getCmp("null").disable();
										Ext.getCmp("empty").disable();
										Ext.getCmp("string").disable();
										macroTreeEnable(true);
										
										Ext.getCmp("valueT").disable();
										Ext.getCmp("stringT").disable();
										Ext.getCmp("errorT").disable();
									}else{
										//Ext.getCmp("jmc").getEl().setStyle('color:black');
										//Ext.getCmp("jmc").getEl( ).applyStyles("{color:black}");
										//Ext.getCmp("jmc").style.color="black";
										//Ext.getCmp("jmc").readOnly=true;
										
										/* Ext.getCmp("lbljmc").setVisible(false);
										Ext.getCmp("jmc").setVisible(false);
										Ext.getCmp("lblrule").setVisible(true);
										Ext.getCmp("ruleText").setVisible(true);
										*/										
										//Ext.getCmp("if").enable();
																				
										Ext.getCmp("lbljmc").hide();
										Ext.getCmp("jmc").hide();
										Ext.getCmp("lblrule").show();
										Ext.getCmp("ruleText").show();
										
										Ext.getCmp("save").disable();
										macroTreeEnable(false);
										
										//Ext.getCmp("valueT").enable();
										//Ext.getCmp("stringT").enable();
										//Ext.getCmp("errorT").enable();
									} 
								}
							}
]
    			},
    			items: insertMacro, // add macro content
    			listeners : {
    				afterrender : function() {
						macrosDrawFieldsTree();
    				},
    				close : function() {
    					macrosVarsToSave = new Array();	    					
    				}
    			}
    		}).show();

    	}
    	
    	function macrosDrawFieldsTree() {
    		var schemaId = schemasGrid.getSelectionModel().getSelection()[0].get("idSchema"); 
    		var _store = macrosDataStores.schemaTree;
			_store.proxy.url = 'schemaFieldsTreePopupRead.json?idSchema=' + schemaId;
			//_store.load();
			Ext.Ajax.request({
				params : {
					idSchema : schemaId
				},
				success: function(result) {
					jsonResp = Ext.JSON.decode(result.responseText);
					_store.setRootNode({
							id: '0',
							text: jsonResp.name,
							leaf: false,
							expanded: true 
					});	
				},
				url : './getSchemaNameById.json'
			});
			macrosDataStores.dbTree.proxy.extraParams.idSchema = schemaId;
			//setRootNode is an analog of store.load()
			macrosDataStores.dbTree.setRootNode({
					id: '0',
					leaf: false,
					expanded: true 
			});	
    	}
    	
//Edit Macro
    	
    	function editMacro(){
    		
            var record = macroGrid.getSelectionModel().getSelection()[0];
            if (!record) {
            	Ext.Msg.alert('', _alert["errorSelectRecord"]);
                return false; 
            }
    		new Ext.Window({
    			id: 'editMacro',
    			title: _message_ee["macroEditing"],
    			layout: 'absolute',
    			height: 520,
    			width: 670,
    			resizable: false,
    			tbar: {
    				xtype: 'toolbar',
					items: [
							{
								xtype: 'button',
								text: _label["clearEditor"],
								iconCls: 'clear',
								id: 'clear',
								handler: 
									function clearEditor(){
									
									Ext.getCmp("ruleText").setValue('');
									Ext.getCmp("jmc").setValue('');
									Ext.getCmp("macroName").setValue('');
									Ext.getCmp("description").setValue('');
									Ext.getCmp("if").enable();
									Ext.getCmp("then").disable();
									Ext.getCmp("endIf").disable();
									Ext.getCmp("string").enable();
									Ext.getCmp("value").disable();
									Ext.getCmp("error").disable();
									Ext.getCmp("AND").disable();
									Ext.getCmp("OR").disable();
									Ext.getCmp("(").disable();
									Ext.getCmp(")").disable();
									Ext.getCmp("+").disable();
									Ext.getCmp("-").disable();
									Ext.getCmp("*").disable();
									Ext.getCmp("/").disable();
									Ext.getCmp("different").disable();
									Ext.getCmp("equal").disable();
									Ext.getCmp("<").disable();
									Ext.getCmp(">").disable();
									Ext.getCmp("save").disable();
									Ext.getCmp("null").disable();
									Ext.getCmp("empty").disable();
									Ext.getCmp("string").disable();
								
									Ext.getCmp("expertMod").enableToggle = true;
									Ext.getCmp("expertMod").toggle(false);	
								}   	
							}, '-' ,
							{
								xtype: 'button',
								text: _label["compile"],
								iconCls: 'bug',
								id: 'compile',
								handler: function () {
									Ext.getCmp("save").enable();
									var win = new Ext.Window({
										title: _label_ee["macroCompilation"],
										iconCls: 'bug',
										width: 550,
										height:300,
										resizable: false,
										layout: 'fit',
										plain:true,
										modal:true,
										bodyStyle:'padding:5px;',
										buttonAlign:'right',
										html: _message_ee["macroCorrectly"],
										buttons:[{text:_message["close"], handler: function(){win.close();}}]
									});
									win.show();
									
									Ext.getCmp("if").disable();
									Ext.getCmp("then").disable();
									Ext.getCmp("endIf").disable();
									Ext.getCmp("string").enable();
									Ext.getCmp("value").disable();
									Ext.getCmp("error").disable();
									Ext.getCmp("AND").disable();
									Ext.getCmp("OR").disable();
									Ext.getCmp("(").disable();
									Ext.getCmp(")").disable();
									Ext.getCmp("+").disable();
									Ext.getCmp("-").disable();
									Ext.getCmp("*").disable();
									Ext.getCmp("/").disable();
									Ext.getCmp("different").disable();
									Ext.getCmp("equal").disable();
									Ext.getCmp("<").disable();
									Ext.getCmp(">").disable();
									Ext.getCmp("null").disable();
									Ext.getCmp("empty").disable();
									Ext.getCmp("string").disable();
								}
							}, '-',
							{
								xtype: 'button',
								text: _label["expertMod"],
								iconCls: 'lock',
								itemId: 'Expert Modality',
								id: 'expertMod',
								pressed: false,
								enableToggle:true,
								toggleHandler: function expertMode(btn, pressed){
									if(pressed){
										Ext.getCmp("jmc").readOnly=false;
										Ext.getCmp("jmc").getEl().setStyle('color:green');
										//Ext.apply(Ext.getCmp("jmc"),'style:{color:green}');
										//Ext.getCmp("jmc").style.color="green";										
										Ext.getCmp("if").disable();
										Ext.getCmp("then").disable();
										Ext.getCmp("endIf").disable();
										Ext.getCmp("string").enable();
										Ext.getCmp("value").disable();
										Ext.getCmp("error").disable();
										Ext.getCmp("AND").disable();
										Ext.getCmp("OR").disable();
										Ext.getCmp("(").disable();
										Ext.getCmp(")").disable();
										Ext.getCmp("+").disable();
										Ext.getCmp("-").disable();
										Ext.getCmp("*").disable();
										Ext.getCmp("/").disable();
										Ext.getCmp("different").disable();
										Ext.getCmp("equal").disable();
										Ext.getCmp("<").disable();
										Ext.getCmp(">").disable();
										Ext.getCmp("save").disable();
										Ext.getCmp("null").disable();
										Ext.getCmp("empty").disable();
										Ext.getCmp("string").disable();
										macroTreeEnable(true);
										
										
									}else{										
										Ext.getCmp("jmc").getEl().setStyle('color:black');
										//Ext.getCmp("jmc").getEl( ).applyStyles("{color:black}");
										//Ext.getCmp("jmc").style.color="black";
										Ext.getCmp("jmc").readOnly = true;
										macroTreeEnable(false);									
									} 
								}
							}
]
    			},
    			listeners : {
    				afterrender : function() {
						macrosDrawFieldsTree();
    				},
    				close : function() {
    					macrosVarsToSave = new Array();	    					
    				}
    			},
    			items: 
    				//EDIT
    				[
    				 {xtype: 'label',text: _label["name"],x: 10,y: 10},
    				 {xtype: 'label',text: _label["description"],x: 225,y: 10},
    				 {xtype: 'textfield',x: 10,y: 30,width: 195,allowBlank: false,id: 'name', value: record.get('name')},
					 {xtype: 'textfield',x: 225,y: 30, width: 295,id: 'description',allowBlank: false, value: record.get('description')},
					 {xtype : 'fieldset', title : _label['relevance'], x : 535, y : 5, width : 105, height : 73,
						 padding : '0 0 0 5' /* need to set all paddings manually, otherwise default values*/,
						 items : [
							 {xtype : 'radiogroup', columns : 1, id : 'macroEditErrorRadioId', 
								  vertical : false, items : [ {		 
									  								boxLabel : _message['error'],
																	inputValue : 0,
																	name : 'rb',
																	checked : record.get('errorType') == 0
																}, {
																	boxLabel : _label['streamLoggingWarning'],
																	inputValue : 2,
																	name : 'rb',
																	checked : record.get('errorType') != 0
																}]
							 }]
					 },
					 {xtype: 'label',text: _label["rule"],x: 10,y: 60},
					 {xtype: 'textarea',x: 10,y: 80,width: 610,readOnly:true,id: 'ruleText',allowBlank: true, value: record.get('ruleSimple')},
					 {xtype: 'label',text: _label["javaMetaCode"],x: 10,y: 60,hidden:true},
					 {xtype: 'textarea',height: 80,x: 10,y: 80, width: 610,readOnly:false,id: 'jmc',allowBlank: false, value: record.get('rule'),hidden:true},
					 {xtype: 'button',text: _message["save"],x: 510,y: 400,width: 70,id:'save',
							handler: 
								function(){
									if(Ext.getCmp('name').getValue() == '' || Ext.getCmp('description').getValue() == '' || Ext.getCmp('ruleText').getValue() == '' 
										|| Ext.getCmp('jmc').getValue() == ''){
										Ext.Msg.alert(_alert["fillFields"]);
									} else {
										//Use the selected schema record to get schema id
										var schemaRecord = schemasGrid.getSelectionModel().getSelection()[0];									
										record.set('idSchema' , schemaRecord.get('idSchema'));
										record.set('rule', Ext.getCmp('jmc').getValue());
										record.set('name', Ext.getCmp('name').getValue());
										record.set('description', Ext.getCmp('description').getValue());
										record.set('errorType', Ext.getCmp('macroEditErrorRadioId').getChecked()[0].getSubmitValue());
										macroGrid.store.sync();
										Ext.getCmp('editMacro').close();
									}
								}
								
							
						},
					{xtype: 'button',text: _message["cancel"],x: 570, y: 400,width: 70, handler: function(){Ext.getCmp('editMacro').close();}},
					
					macroTreesPanel
				]		
    		}).show();
    		
    		Ext.getCmp("if").disable();
    		//Ext.getCmp("save").disable();
    		macroTreeEnable(false);
    		Ext.getCmp("jmc").disable();
    		Ext.getCmp("ruleText").disable();
    		Ext.getCmp("clear").disable();
    		Ext.getCmp("compile").disable();
    		Ext.getCmp("expertMod").disable();
    	}
       	
}