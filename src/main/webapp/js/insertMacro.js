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

var isalphanumeric = false;
var isnumeric = false;
var isdate = false;

var macroTreesPanel = {
	xtype : 'panel',
	layout : 'border',
	height : 240,
	width: 630,
	defaults: {
	    split: true,
		collapsible : true
	},
	x: 10,
	y: 150,
	items : [
			{	
				id : 'macroTreePanel',
				xtype : 'tabpanel',
	    		region:'center',
	    		collapsible : false,
				items : [ {
					xtype : 'treepanel',
					title: _label["trackFields"],		
					collapsible : false,
					id : 'treeSchema',
					store : macrosDataStores.schemaTree,
					rootVisible : true,
					useArrows : true,
					scroll : false,
					viewConfig : {
						style : {
							overflowY : 'scroll',
							overflowX : 'scroll'
						}
					},
					disabled : true,
					listeners : {
						load : function() {
							//this.collapseAll();
							//this.expandAll();		
						},		
						itemdblclick : function(view, record, item, index, e,
								options) {		
							var fieldType = record.data.fieldType;					
							var uniqueName = record.data.uniqueName;
							var nodePath = record.data.nodePath; 
							//string below prevents from double-click on folders
							if (uniqueName == '' || fieldType == '' || nodePath == '') return;
							addVARIABLE(uniqueName, uniqueName, fieldType);
							var s = uniqueName + ';' + fieldType + ';' + nodePath + '\n';
							var isExists = false;
							for (var i = 0; i < macrosVarsToSave.length; i++) {
								if (macrosVarsToSave[i] == s) {
									isExists = true;
									break;
								}
							}
							if (!isExists) macrosVarsToSave.push(s);
						}
					}
				},
				{
					title: _label["dbTrackFields"],
					xtype : 'treepanel',
					id : 'dbTreeSchema',
					disabled : true,
					collapsible : false,
					store : macrosDataStores.dbTree,
					rootVisible : false,
					useArrows : false,
					scroll : true,
					listeners : {
						itemdblclick : function(view, record, item, index, e, options) {	
							var sql = record.data.sql_text;
							//string below prevents from double-click on folders
							if (sql == '') return;
							var text = Ext.getCmp("jmc").getValue();
							var token = null;
							var i = 0;
							for (i = text.length; i--; i > 0) {
								if (text[i] == ' ') continue;
								if (text[i] == '=' && text[i - 1] == '=') {
									i--;
									//attention: here change '==' -> '=' for sql
									token = '=';
									break;
								} else if (text[i] == '=' && text[i - 1] == '!')  {
									i--;
									token = '!=';
									break;
								} else if (text[i] == '<') {
									//attention: here change '<' -> '>' for sql, cause sides interchange									
									token = '>';
									break;
								} else if (text[i] == '>') {
									//attention: here change '>' -> '<' for sql, cause sides interchange										
									token = '<';
									break;
								}
							} 
							if (token) {
								var word = '';
								for (i; i--; i > 0) {
									if (text[i] == ' ') continue;
									if ((text[i] == '&' && text[i - 1] == '&') 
										|| (text[i] == '|' && text[i - 1] == '|')
										|| (text[i] == '(')) {
										break;			
									}
									word += text[i];
								}
								if (word != '') {
									word = word.jv_trim().jv_reverse();
									var beginStr = text.substr(0, i + 1);
									Ext.getCmp("jmc").setValue(beginStr + ' ');
									addVARIABLE(record.data.var_name, sql.jv_format(token, word), 'Alphanumeric');
									Ext.getCmp("different").disable();
									Ext.getCmp("equal").disable();
								}
							}
						}
					}
				}
				]
			},
			{   title: _label["instructions"],
			    region:'east',
			    cmargins: '5 5 0 0',
				width : 300,			    
			    minSize: 100,
			    maxSize: 250, layout: 'absolute',
				items: [
				    {xtype: 'button',text: _button["IF"], x : 5, y : 5, width: 50,id: 'if',handler: function(){addIF();this.disable();}},
				    {xtype: 'button',text: _button["THEN"],x: 65,y: 5,width: 50,id: 'then',disabled: true,handler: function(){addTHEN();}},
				    {xtype: 'button',text: _button["ENDIF"],x: 125,y: 5,width: 50,id: 'endIf',disabled:true,handler:function(){addEND();}},
					{xtype: 'button',text: '(',x: 185,y: 5,width: 50,disabled: true,count: 0,id: '(',handler:function(){addS1();}},
					{xtype: 'button',text: ')',x: 245,y: 5,width: 50,disabled: true,count: 0,id: ')',handler:function(){addS2();}},
					{xtype: 'button',text: '+',width: 50, x : 5 , y: 35,disabled: true,id: '+',handler:function(){addPLUS();}},
					{xtype: 'button',text: '-',x: 65,disabled: true,y: 35,width: 50,id: '-',handler:function(){addMINUS();}},
					{xtype: 'button',text: '*',x: 125,y: 35,disabled: true,width: 50,id: '*',handler:function(){addMULTIPLICATION();}},
					{xtype: 'button',text: '/',x: 185,y: 35,width: 50,disabled: true,id: '/',handler:function(){addDIVISION();}},
					{xtype: 'button',text: _button["AND"],x: 5,y: 65,width: 50,disabled: true,id: 'AND',handler:function(){addAND();}},
					{xtype: 'button',text: _button["OR"],x: 65,y: 65,width: 50,disabled: true,id: 'OR', handler:function(){addOR();}},
					{xtype: 'button',text: 'NULL',x: 125,y: 65,width: 50,disabled: true,id: 'null',handler:function(){addNULL();}},
					{xtype: 'button',text: _button["EMPTY"],x: 185,y: 65,width: 50,id: 'empty',disabled: true,handler:function(){addEMPTY();}},					
					{xtype: 'button',text: _button["DIFFERENT"],x: 5,y: 95,width: 56,disabled: true,id: 'different',handler:function(){addNOT();}},
					{xtype: 'button',text: _button["EQUAL"],x: 65,y: 95,width: 50,disabled: true,id: 'equal',handler:function(){addCOMPARE();}},
					{xtype: 'button',text: '&lt;',x: 125,y: 95,disabled: true,width: 50,id: '<',handler:function(){addMINOR();}},
					{xtype: 'button',text: '&gt;',x: 185,y: 95,disabled: true,width: 50,id: '>',handler:function(){addMAJOR();}},
					
					{xtype: 'button',text: _button["VALUE"],x: 245, y: 145,width: 50,id: 'value',disabled: true,handler:function(){if(Ext.getCmp("value").value!=''){addVALUE();}}},
					{xtype: 'button',text: _button["STRING"],x: 100,y: 145,width: 50,id: 'string',disabled: true,handler:function(){if(Ext.getCmp("string").value!=''){addSTRING();}}},
					{xtype: 'button',text: _button["ERROR"],disabled: true, width: 50,x: 245,y: 175,id: 'error',handler:function(){if(Ext.getCmp("error").value!=''){addERROR();}}},
					{xtype: 'textfield',x: 155,y: 145,width: 85, id: 'valueT', disabled: true, maskRe: /[0-9\.]/},
					{xtype: 'textfield',x: 5,y: 145,width: 90,id: 'stringT', disabled: true},
					{xtype: 'textfield',x: 5,y: 175,width: 235,id: 'errorT', disabled: true}
				]               
			}
	]	
}

var insertMacro = [
{xtype: 'label',text: _label["name"],x: 10,y: 10},
{xtype: 'label',text: _label["description"],x: 225,y: 10},
{xtype: 'textfield',x: 10,y: 30,width: 195,allowBlank: false,id: 'macroName'},
{xtype: 'textfield',x: 225,y: 30,width: 295,id: 'description',allowBlank: false},
{xtype : 'fieldset', title : _label['relevance'], x : 535, y : 5, width : 105, height : 73, 
	padding : '0 0 0 5' /* need to set all paddings manually, otherwise default values*/,
	items : [
		{ xtype : 'radiogroup', columns : 1, id : 'macroErrorRadioId',
		  vertical : false, 
		  items : [ {
					boxLabel : _message['error'],
					inputValue : 0,
					name : 'rb',
					checked : true
				}, {
					boxLabel : _label['streamLoggingWarning'],
					inputValue : 2,
					name : 'rb'
				} ]
		}
	]
},
{xtype: 'label',text: _label["rule"],x: 10,y: 60, id:'lblrule'},
{xtype: 'textarea',x: 10,y: 80,width: 630,readOnly:true,id: 'ruleText',allowBlank: true},
{xtype: 'label',text: _label["javaMetaCode"],x: 10,y: 60, hidden:true, id:'lbljmc'},
{xtype: 'textarea',x: 10,y: 80,width: 630, height: 80, readOnly:false,id: 'jmc',allowBlank: false,hidden:true},
{xtype: 'button',text: _message["save"],x: 510,y: 400,width: 50,id:'save',disabled: true, handler:
	function save(){
	  
	  if(Ext.getCmp('macroName').getValue() == '' || Ext.getCmp('description').getValue() == ''){
		Ext.Msg.alert(_alert["fillFields"]);
		return;
	} else if(Ext.getCmp('expertMod').pressed == true && Ext.getCmp('jmc').getValue() == '') {
		Ext.Msg.alert(_alert["fillFields"]);
		return;
	} else if(Ext.getCmp('expertMod').pressed == false && Ext.getCmp('ruleText').getValue() == '') {
		Ext.Msg.alert(_alert["fillFields"]);
		return;
	}
	  
	//Use the selected schema record to get schema id
	var schemaRecord = schemasGrid.getSelectionModel().getSelection()[0];
		
	//RecordType
	var record = new macroGrid.store.model();
		
	record.set('idSchema' , schemaRecord.get('idSchema'));
	record.set('name' , Ext.getCmp('macroName').getValue());
	record.set('description' , Ext.getCmp('description').getValue());
	record.set('rule' , Ext.getCmp('jmc').getValue());	
	record.set('ruleSimple' , Ext.getCmp('ruleText').getValue());		
	record.set('vars', macrosUtils.arrayAsString(macrosVarsToSave));
	record.set('isActive', 1);	
	record.set('errorType', Ext.getCmp('macroErrorRadioId').getChecked()[0].getSubmitValue());	
		
	//insert into macro Grid
	macroGrid.store.insert(0 , record);		
	macroGrid.store.sync();
		
	Ext.getCmp('treeSchema').save = Ext.getCmp('treeSchema').disabled;	
	
}
},
{xtype: 'button',text: _message["cancel"],x: 570,y: 400,width: 70,handler: function() {Ext.getCmp('addMacro').close();}},

macroTreesPanel

];


function macroTreeEnable(bEnable) {
	if (bEnable) {
		Ext.getCmp("treeSchema").enable();
	} else {
		Ext.getCmp("treeSchema").disable();
	}
	Ext.getCmp('macroTreePanel').setActiveTab('treeSchema');
}

function macroDbTreeEnable(bEnable) {
	if (bEnable) {
		var isEn = schemasGrid.getSelectionModel().getSelection()[0].get('inputToDb');
		isEn ? Ext.getCmp("dbTreeSchema").enable() : Ext.getCmp("dbTreeSchema").disable();
	} else {
		Ext.getCmp("dbTreeSchema").disable();
		Ext.getCmp("AND").enable();
		Ext.getCmp("OR").enable();
		Ext.getCmp("then").enable();
	}
}

function addIF(){	
	isalphanumeric = false;
	isnumeric = false;
	isdate = false;
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["IF"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'if(');
	macroTreeEnable(true);
	Ext.getCmp("(").enable();
}
function addTHEN(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["THEN"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'){');
	Ext.getCmp("error").enable();
	Ext.getCmp("errorT").enable();
	Ext.getCmp("string").disable();
	Ext.getCmp("value").disable();
	Ext.getCmp("then").disable();
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
	Ext.getCmp("null").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
}
function addEND(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["ENDIF"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'}');
	Ext.getCmp("endIf").disable();
	Ext.getCmp("save").disable();
}
function addAND(){	
	isalphanumeric = false;
	isnumeric = false;
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["AND"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+' && ');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();

}

function addOR(){	
	isalphanumeric = false;
	isnumeric = false;
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["OR"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+' || ');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();
}

function addNOT(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["DIFFERENT"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'!=');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	

	if(isalphanumeric == true || isdate == true) {
				
		Ext.getCmp("null").enable();
		Ext.getCmp("empty").enable();
		Ext.getCmp("string").enable();
		Ext.getCmp("stringT").enable();
		Ext.getCmp("value").disable();
		Ext.getCmp("valueT").disable();
	} else {
		
		Ext.getCmp("null").disable();
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("stringT").disable();
		Ext.getCmp("value").enable();
		Ext.getCmp("valueT").enable();
	}
	
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();	
	macroTreeEnable(true);
	macroDbTreeEnable(true);	
	Ext.getCmp("then").disable();
}

function addNULL(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' NULL ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'null');
	Ext.getCmp("AND").enable();
	Ext.getCmp("OR").enable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").disable();
	Ext.getCmp("string").disable();
	Ext.getCmp("valueT").disable();
	Ext.getCmp("stringT").disable();
	macroTreeEnable(false);
	Ext.getCmp("then").enable();
	Ext.getCmp("string").setText('STRING');
	isdate = false;
}

function addEMPTY(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["EMPTY"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'\"\"');
	Ext.getCmp("AND").enable();
	Ext.getCmp("OR").enable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").disable();
	Ext.getCmp("string").disable();
	Ext.getCmp("valueT").disable();
	Ext.getCmp("stringT").disable();
	macroTreeEnable(false);
	Ext.getCmp("then").enable();
	Ext.getCmp("string").setText('STRING');
	isdate = false;
}

function addMINOR(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_label["min"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'<');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	Ext.getCmp("stringT").disable();
	macroTreeEnable(true);
	macroDbTreeEnable(true);	
	Ext.getCmp("then").disable();
}


function addMAJOR(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_label["max"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'>');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	Ext.getCmp("stringT").disable();
	macroTreeEnable(true);
	macroDbTreeEnable(true);	
	Ext.getCmp("then").disable();
}

function addCOMPARE(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_label["equal"]+' ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'==');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	
	if(isalphanumeric == true || isdate == true) {
				
		Ext.getCmp("null").enable();
		Ext.getCmp("empty").enable();
		Ext.getCmp("string").enable();
		Ext.getCmp("stringT").enable();
		Ext.getCmp("value").disable();
		Ext.getCmp("valueT").disable();
	} else {
		
		Ext.getCmp("null").disable();
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("stringT").disable();
		Ext.getCmp("value").enable();
		Ext.getCmp("valueT").enable();
	}
	
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	macroTreeEnable(true);
	macroDbTreeEnable(true);
	Ext.getCmp("then").disable();
}


function addS1(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+'(');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'(');	
	Ext.getCmp("(").count++;
	Ext.getCmp(")").disable();
	Ext.getCmp("then").disable();

}

function addS2(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+')');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+')');
	Ext.getCmp(")").count++;
		
	if (Ext.getCmp("(").count == Ext.getCmp(")").count){
		Ext.getCmp(")").disable();
	} else {
		Ext.getCmp(")").enable();
	}
}

function addPLUS(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+'+');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'+');	
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	Ext.getCmp("stringT").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();
}

function addMINUS(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+'-');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'-');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();
}

function addDIVISION(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+'/');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'/');
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();
}

function addMULTIPLICATION(){
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+'*');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'*');	
	Ext.getCmp("AND").disable();
	Ext.getCmp("OR").disable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	Ext.getCmp("value").enable();
	Ext.getCmp("valueT").enable();
	Ext.getCmp("string").disable();
	macroTreeEnable(true);
	Ext.getCmp("then").disable();
}


function addVALUE(){
	
	if(Ext.getCmp("valueT").getValue() == '') {
		App.setAlert(false , _error['valueEmptyError']);		
		return;
	} else  if(Ext.getCmp("valueT").getValue().split("\.").length > 2){		
		App.setAlert(false , _error['decimalValueError']);		
		return;
	} 
	
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' ['+Ext.getCmp("valueT").getValue()+'] ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+''+Ext.getCmp("valueT").getValue()+'');
	Ext.getCmp("valueT").setValue('');
	Ext.getCmp("valueT").disable();
	Ext.getCmp("different").enable();
	Ext.getCmp("equal").enable();
	Ext.getCmp("string").disable();
	Ext.getCmp("value").disable();
	Ext.getCmp("AND").enable();
	Ext.getCmp("OR").enable();
	Ext.getCmp("+").enable();
	Ext.getCmp("-").enable();
	Ext.getCmp("*").enable();
	Ext.getCmp("/").enable();
	Ext.getCmp("null").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	macroDbTreeEnable(false);
	Ext.getCmp("dbTreeSchema").disable();		
	Ext.getCmp("then").enable();

	if(Ext.getCmp("(").count != Ext.getCmp(")")) {
		Ext.getCmp(")").enable();
	}
}

function addSTRING(){

	if(Ext.getCmp("stringT").getValue() == '' && isdate == false) {
		App.setAlert(false , _error['stringEmptyError']);		
		return;
	} else if(Ext.getCmp("stringT").getValue() == '' && isdate == true) {
		App.setAlert(false , _error['dateEmptyError']);
		return;
	}
	
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' ['+Ext.getCmp("stringT").getValue()+'] ');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+'"'+Ext.getCmp("stringT").getValue()+'"');
	Ext.getCmp("stringT").setValue('');
	Ext.getCmp("string").disable();
	Ext.getCmp("stringT").disable();
	Ext.getCmp("value").disable();
	Ext.getCmp("AND").enable();
	Ext.getCmp("OR").enable();
	Ext.getCmp("+").disable();
	Ext.getCmp("-").disable();
	Ext.getCmp("*").disable();
	Ext.getCmp("/").disable();
	Ext.getCmp("null").disable();
	Ext.getCmp("different").disable();
	Ext.getCmp("equal").disable();
	Ext.getCmp("<").disable();
	Ext.getCmp(">").disable();
	Ext.getCmp("empty").disable();
	macroTreeEnable(false);
	Ext.getCmp("then").enable();

	if(Ext.getCmp("(").count != Ext.getCmp(")")) {
		Ext.getCmp(")").enable();
	}
	
	macroDbTreeEnable(false);
	Ext.getCmp("string").setText('STRING');
	isdate = false;
}


function addERROR(){
	
	if(Ext.getCmp("errorT").getValue() == '') {
		App.setAlert(false , _error['errorEmptyError']);		
		return;
	}
	
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue()+' '+_button["ERROR"]+'('+Ext.getCmp("errorT").getValue()+')');
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue()+' flowErrors ="'+Ext.getCmp("errorT").getValue()+'";');
	Ext.getCmp("errorT").setValue('');
	Ext.getCmp("endIf").enable();
	Ext.getCmp("error").disable();
	Ext.getCmp("errorT").disable();
}

function addVARIABLE(input, expertModeText, fieldType){
	if(fieldType=='Numeric'){
		isnumeric = true;
		Ext.getCmp("AND").enable();
		Ext.getCmp("OR").enable();
		Ext.getCmp("+").enable();
		Ext.getCmp("-").enable();
		Ext.getCmp("*").enable();
		Ext.getCmp("/").enable();
		Ext.getCmp("null").disable();
		Ext.getCmp("different").enable();
		Ext.getCmp("equal").enable();
		Ext.getCmp("<").enable();
		Ext.getCmp(">").enable();
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("value").disable();
		Ext.getCmp("stringT").disable();
		Ext.getCmp("valueT").disable();
	}

	if(fieldType=='Decimal'){
		
		isnumeric = true;
		Ext.getCmp("AND").enable();
		Ext.getCmp("OR").enable();
		Ext.getCmp("+").enable();
		Ext.getCmp("-").enable();
		Ext.getCmp("*").enable();
		Ext.getCmp("/").enable();
		Ext.getCmp("null").disable();
		Ext.getCmp("different").enable();
		Ext.getCmp("equal").enable();
		Ext.getCmp("<").enable();
		Ext.getCmp(">").enable();
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("value").disable();
		Ext.getCmp("stringT").disable();
		Ext.getCmp("valueT").disable();		
	}

	if(fieldType=='Alphanumeric'){
		isalphanumeric = true;
		Ext.getCmp("AND").enable();
		Ext.getCmp("OR").enable();
		Ext.getCmp("+").disable();
		Ext.getCmp("-").disable();
		Ext.getCmp("*").disable();
		Ext.getCmp("/").disable();
		Ext.getCmp("null").disable();
		Ext.getCmp("different").enable();
		Ext.getCmp("equal").enable();
		Ext.getCmp("<").disable();
		Ext.getCmp(">").disable();
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("value").disable();		
		Ext.getCmp("stringT").disable();
		Ext.getCmp("valueT").disable();
	}

	if(fieldType == 'Date'){
		isdate = true;
		Ext.getCmp("string").setText('DATE');
		Ext.getCmp("AND").enable();
		Ext.getCmp("OR").enable();
		Ext.getCmp("+").disable();
		Ext.getCmp("-").disable();
		Ext.getCmp("*").disable();
		Ext.getCmp("/").disable();
		Ext.getCmp("<").disable();
		Ext.getCmp(">").disable();
		Ext.getCmp("null").disable();
		Ext.getCmp("different").enable();
		Ext.getCmp("equal").enable();		
		Ext.getCmp("empty").disable();
		Ext.getCmp("string").disable();
		Ext.getCmp("value").disable();		
		Ext.getCmp("stringT").disable();
		Ext.getCmp("valueT").disable();
	}
	
	macroTreeEnable(false);		
	if(isalphanumeric == true && isnumeric == true) {
		Ext.getCmp("different").disable();
		Ext.getCmp("equal").disable();
		Ext.getCmp("AND").disable();
		Ext.getCmp("OR").disable();
	}
		
	if(Ext.getCmp("(").count != Ext.getCmp(")")) {			
		Ext.getCmp(")").enable();	
	}
	
	Ext.getCmp("then").disable();
	macroDbTreeEnable(false);	
	Ext.getCmp("ruleText").setValue(Ext.getCmp("ruleText").getValue() + ' [' + input + '] ');	
	Ext.getCmp("jmc").setValue(Ext.getCmp("jmc").getValue() + '' + expertModeText + '');
}