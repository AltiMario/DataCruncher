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

var conditionIndex = 1;
var conditionColArr = null;
var currentIndex = 1; 
var conditionQuery = '';
var addConditionTable = '';
function addCondition(table, isReloadGrid){
	conditionQuery = '';
	if(table == ''){
		table = addConditionTable;
	}
	currentIndex = 1;
	conditionIndex = 1;
	var str1 = '<table><tr><td><table><tr><td>';
	
	var str2 = '<select class="body-style" id="conditionTables" size="15" multiple>';
	
	var str3 = '';	
	
	var str6 = '';

	
	var colArr =  columns[table];
	conditionColArr = colArr;
	for(var i=0; i< colArr.length; i++) {  
		var tmpColArr = colArr[i];	
		
		str3 = str3 + '<option>'+ tmpColArr[0]+'</option>';
		 
		var condtionSelect = '<select class="body-style" id="condition_'+ conditionIndex +'"><option value="0">Condition</option><option value="1">--------</option><option value="2">IS NULL</option><option value="3">IS NOT NULL</option><option value="4">LIKE</option><option value="5">NOT LIKE</option><option value="6">=</option><option value="7"><></option></select>';
		var conditionTypeSelect = '<select class="body-style" id="conditionType_'+ conditionIndex +'"><option>OR</option><option>AND</option></select>';

		 str6 = str6 + '<tr><td><input class="body-style" type="text" size="20" id="columnName_'+ conditionIndex +'"><input class="body-style" type="text" size="20" id="columnType_'+ conditionIndex +'">'+ condtionSelect +'<input class="body-style" type="text" size="20" id="conditionValue_'+ conditionIndex +'">' + conditionTypeSelect + '</td></tr>';
		 conditionIndex = conditionIndex + 1;
	}
	
	var str4 = '</select>';
		
	var str5 = '</td></tr></table></td><td><table><tr><td><button onclick="fillCondition();" name="select" value=">>" type="button">>></button></td></tr></table></td><td><table>';

	
	var str7 = '</table></td></tr></table>';
	
	
	var finalStr = str1 + str2 + str3 + str4 + str5 + str6 + str7;
	
	
	var	conditionform = Ext
	.widget(
			'form',
			{
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				border : false,
				bodyPadding : 10,
				
				defaults : {
					margins : '0 0 10 0'
				},

				
				items : [ {
					xtype : 'fieldset',								
					defaultType : 'textfield',
					defaults : {
						width : 380
					},
					html : finalStr
				} ],
				buttons : [							
							{
								text : 'Validate',
								handler : function() {	
									validateData(table, true);
								}
							},{
								text : 'Apply',
								id: 'apply',	
								disabled: true,
								handler : function() {		
									if(isReloadGrid){
										validateData(table, false);
									}
									else{
										validateData(table, true);
									}
									this.up('window').destroy();
								}
							},{
								text : 'Cancel',
								handler : function() {
									this.up('window').destroy();
								}
							} ]
				
			});
	
	
	var win = new Ext.Window({
		  title: 'Table Query Panel',
		  closeAction : 'destroy',
		  width: 850,
		  height: 400,
		  autoScroll : true,
		  preventBodyReset: true,
		  //html: finalStr,
		  items : conditionform
		});
		win.show();


}

function fillCondition(){
	var select = document.getElementById("conditionTables");
	var result = [];
	var options = select && select.options;
	var opt;

	for (var i=0, iLen=options.length; i<iLen; i++) {
		opt = options[i];

		if (opt.selected) {
			result.push(opt.value || opt.text);
		}
	}
	for(var i=0;i<result.length;i++){
		var column = result[i];
		for(var j=0; j< conditionColArr.length; j++) {  
			var tmpColArr = conditionColArr[j];	
			if(column == tmpColArr[0]){
				document.getElementById("columnName_" +(currentIndex)).value = column;
				document.getElementById("columnType_" +(currentIndex)).value = tmpColArr[1];
				currentIndex = currentIndex + 1;
			}
		}
	}
	
}

function validateData(table, isValidate){
	var columnNames = '';
	var columnTypes = '';	
	var condition = '';
	var conditionValue = '';
	var conditionType = '';
	
	
	for(var j=1; j< currentIndex; j++) {  
		if(columnNames == ''){
			columnNames = document.getElementById("columnName_" +(j)).value;
		}
		else{
			columnNames = columnNames + ',' + document.getElementById("columnName_" +(j)).value;
		}
		
		if(columnTypes == ''){
			columnTypes = document.getElementById("columnType_" +(j)).value;
		}
		else{
			columnTypes = columnTypes + ',' + document.getElementById("columnType_" +(j)).value;
		}
		
		if(condition == ''){
			condition = document.getElementById("condition_" +(j)).value;
		}
		else{
			condition = condition + ',' + document.getElementById("condition_" +(j)).value;
		}
		
		if(conditionValue == ''){
			conditionValue = document.getElementById("conditionValue_" +(j)).value;
		}
		else{
			conditionValue = conditionValue + ',' + document.getElementById("conditionValue_" +(j)).value;
		}
		
		if(conditionType == ''){
			conditionType = document.getElementById("conditionType_" +(j)).value;
		}
		else{
			conditionType = conditionType + ',' + document.getElementById("conditionType_" +(j)).value;
		}
	
	}
	if(isValidate){
	Ext.Ajax.request({
		   url: 'profilerMisc.json',    // where you wanna post
		   success: function(response) {				
			    var obj = Ext.decode(response.responseText);
			    conditionQuery = obj.conditionQuery;
				Ext.Msg.alert('Status','Query Success. ' + obj.count + ' rows found.');
				Ext.getCmp('apply').enable();
			},   
		   //failure: failFn,
		   params: { action: 'validateCondition', index: currentIndex, table: table , columnNames: columnNames, columnTypes: columnTypes, condition: condition, conditionValue: conditionValue, conditionType: conditionType}  // your json data
	});
	}
	else{
		reloadTabMisc(table, false, { action: 'applyCondition', index: currentIndex, table: table , columnNames: columnNames, columnTypes: columnTypes, condition: condition, conditionValue: conditionValue, conditionType: conditionType});
		currentIndex = 1;
	}
}

function reloadTabMisc(selectedValue, isLeaf, params) {
	
	var tabs = Ext.createWidget('tabpanel',
			{
				renderTo : 'home-center',
				activeTab : 0,
				width : '100%',
				height : '100%',
				plain : true,
				defaults : {
					autoScroll : true,
					bodyPadding : 10
				},
				items : [
						{
							title : 'Information',
							itemId : 'information',
							loader : {
								url: 'profilerMisc.json',    // where you wanna post
		  						params: params,  // your json data
		  						contentType : 'html',											
								scripts : true,
								loadMask : true
							},
							listeners : {
								activate : function(tab) {
									tab.loader.load();
								}
							}
						},
						{
							title : 'Analysis',
							itemId : 'analysis',
							loader : {
								url : 'profilerInfo.json?selectedValue='
										+ selectedValue,
								contentType : 'html',
								autoLoad : false,
								scripts : true,
								loadMask : true
							},
							listeners : {
								activate : function(tab) {
									tab.loader.load();
								}
							}
						} ]
			});
	if (!isLeaf) {
		tabs.child('#analysis').tab.hide();
	}
}

function showCondition(){
	if(conditionQuery == ''){
		Ext.Msg.alert('Query Information','Condition Not Set');
	}
	else{
		Ext.Msg.alert('Query Information',conditionQuery);
	}
}