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

function reloadTab(selectedValue, isLeaf, parentValue, isCondition) {
	Ext.getCmp('home-center').removeAll();
	//document.getElementById('home-center').innerHTML = '';
	var tabs = Ext.createWidget('tabpanel',
			{
				//renderTo : 'home-center',
				activeTab : 0,
				border: false,
				plain : true,
				defaults : {
					autoScroll : true,
					bodyPadding : 10
				},
				items : [
						{
							collapsible: true,
							layout: 'fit',
							title : 'Information',
							border: false,
							itemId : 'information',
							loader : {
								url : 'profilerInfo.json?selectedValue='
										+ selectedValue
										+ '&isLeaf=' + isLeaf
										+ '&parent=' + parentValue,
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
							border: false,
							loader : {
								url : 'profilerInfo.json?selectedValue='
										+ selectedValue + '&isLeaf=' + isLeaf
										+ '&parent=' + parentValue+ '&tab=analysis' + '&queryString=' + conditionQuery,
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
	conditionQuery = '';
	Ext.getCmp('home-center').add(tabs);
}
function slowInputDialog(title, type){	
	
	var record = Ext.getCmp("databasesGrid").getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectDBRecord']);
        return false;
    }
    
	Ext.Msg.prompt('Table Input Dialog', 'Enter Table Pattern:', function(btn, text){
	    if (btn == 'ok'){
	    	loadDBInfo(title, type, text)
	    }
	});
}

function slowInputDialogForReplaceNull(columns, table, action){	
	
	Ext.Msg.prompt('Input', 'Replace Null With: <br> For date object format is dd-MM-yyyy', function(btn, text){
	    if (btn == 'ok'){
			loadPopUpGrid(columns, table, action, text);

	    }
	});
}


function loadDBInfo(title, type, text){

	var record = Ext.getCmp("databasesGrid").getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectDBRecord']);
        return false;
    }
    
	if(text == undefined){
		text = '';
	}
	Ext.QuickTips.init();
	
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
    if(type == 'datainfo' || type == 'tabledatainfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['table', 'column' ,'record', 'unique' , 'pattern','nall', 'zero' ,'empty']	       
	    });
    }
    else if(type == 'indexinfo' || type == 'tableindexinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['table', 'column' ,'index', 'type' , 'qualifier','isUnique', 'ascdsc' ,'cardinality', 'pages' , 'filter']	       
	    });
    }
    else if(type == 'tableprivilegeinfo' || type == 'columnprivilegeinfo'  || type == 'alltableprivinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['table', 'grantor' ,'grantee', 'privileges' , 'grantable']	       
	    });
    }
    else if(type == 'tablemetadatainfo' || type == 'dbmetadatainfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['table', 'column' ,'type', 'size' , 'precision','radix', 'remark' ,'default', 'byte' , 'originalpos', 'nullable']	       
	    });
    }
    else if(type == 'procedureinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['procedure', 'remark' ,'type', 'schema' , 'category']	       
	    });
    }
    else if(type == 'parameterinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['paramter', 'type' ,'nullable', 'procedure' , 'schema', 'category']	       
	    });
    }
    else if(type == 'usersqlinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['name', 'class' ,'datatype', 'basetype' , 'remark', 'category','schema']	       
	    });
    }
    else if(type == 'standardsqlinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['name', 'datatype' ,'precision', 'prefix' , 'suffix', 'param','nullable', 'casesensitive' ,'searchable', 'unsigned' , 'autoincremental']	       
	    });
    }
    else if(type == 'funcinfo'){
    	Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['stringfx', 'numericfx' ,'datefx', 'systemfx' , 'sqlkeyword']	       
	    });
    }
//    else if(type == 'cataloginfo' || type =='schemainfo'){
//    	Ext.define('DBInfo', {
//	        extend: 'Ext.data.Model',
//	        fields: ['property', 'value']	       
//	    });
//    }
    else{
	    Ext.define('DBInfo', {
	        extend: 'Ext.data.Model',
	        fields: ['property', 'value']
	    });
    }


    // create the Data Store
    var store = Ext.create('Ext.data.Store', {
        model: 'DBInfo',
        autoLoad: true,
        proxy: {
            // load using HTTP
            type: 'ajax',
            url: 'dbInfo.json?type=' + type + '&text=' + text,
            // the return will be JSON, so lets set up a reader
            reader: {
                type: 'json',
                // records will have an "Item" tag                
                root: 'items',
                totalRecords: 'totalCount'
            }
        }
    });
    
    var grid = null;
    if(type == 'datainfo' || type =='tabledatainfo'){
    		grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Table", flex: 1, dataIndex: 'table', sortable: true},
                {text: "Column", flex: 1, dataIndex: 'column', sortable: true},
                {text: "Record", flex: 1, dataIndex: 'record', sortable: true},
                {text: "Unique", flex: 1, dataIndex: 'unique', sortable: true},
                {text: "Pattern", flex: 1, dataIndex: 'pattern', sortable: true},
                {text: "Null", flex: 1, dataIndex: 'nall', sortable: true},
                {text: "Zero", flex: 1, dataIndex: 'zero', sortable: true},
                {text: "Empty", flex: 1, dataIndex: 'empty', sortable: true}                            
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'indexinfo' || type =='tableindexinfo'){
    	grid = Ext.create('Ext.grid.Panel', {	
            store: store,
            columns: [
                {text: "Table", flex: 1, dataIndex: 'table', sortable: true},
                {text: "Column", flex: 1, dataIndex: 'column', sortable: true},
                {text: "Index", flex: 1, dataIndex: 'index', sortable: true},
                {text: "Type", flex: 1, dataIndex: 'type', sortable: true},
                {text: "Qulifier", flex: 1, dataIndex: 'qualifier', sortable: true},
                {text: "Is Unique", flex: 1, dataIndex: 'isUnique', sortable: true},
                {text: "Asc/ Dsc", flex: 1, dataIndex: 'ascdsc', sortable: true},
                {text: "Cardinality", flex: 1, dataIndex: 'cardinality', sortable: true},
                {text: "Pages", flex: 1, dataIndex: 'pages', sortable: true},              
                {text: "Filter", flex: 1, dataIndex: 'filter', sortable: true}                
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'tableprivilegeinfo' || type == 'columnprivilegeinfo' || type == 'alltableprivinfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Table", flex: 1, dataIndex: 'table', sortable: true},
                {text: "Grantor", flex: 1, dataIndex: 'grantor', sortable: true},
                {text: "Grantee", flex: 1, dataIndex: 'grantee', sortable: true},
                {text: "Privileges", flex: 1, dataIndex: 'privileges', sortable: true},
                {text: "Grantable", flex: 1, dataIndex: 'grantable', sortable: true}
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'tablemetadatainfo' || type == 'dbmetadatainfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Table", flex: 1, dataIndex: 'table', sortable: true},
                {text: "Column", flex: 1, dataIndex: 'column', sortable: true},
                {text: "Type", flex: 1, dataIndex: 'type', sortable: true},
                {text: "Size", flex: 1, dataIndex: 'size', sortable: true},
                {text: "Precision", flex: 1, dataIndex: 'precision', sortable: true},
                {text: "Radix", flex: 1, dataIndex: 'radix', sortable: true},
                {text: "Remark", flex: 1, dataIndex: 'remark', sortable: true},
                {text: "Default", flex: 1, dataIndex: 'default', sortable: true},
                {text: "Byte", flex: 1, dataIndex: 'byte', sortable: true},
                {text: "Original Pos", flex: 1, dataIndex: 'originalpos', sortable: true},
                {text: "Nullable", flex: 1, dataIndex: 'nullable', sortable: true}                
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'procedureinfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Procedure", flex: 1, dataIndex: 'procedure', sortable: true},
                {text: "Remark", flex: 1, dataIndex: 'remark', sortable: true},
                {text: "Type", flex: 1, dataIndex: 'type', sortable: true},
                {text: "Schema", flex: 1, dataIndex: 'schema', sortable: true},
                {text: "Category", flex: 1, dataIndex: 'category', sortable: true}            
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'parameterinfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Paramter", flex: 1, dataIndex: 'paramter', sortable: true},
                {text: "Type", flex: 1, dataIndex: 'type', sortable: true},
                {text: "Nullable", flex: 1, dataIndex: 'nullable', sortable: true},
                {text: "Procedure", flex: 1, dataIndex:'procedure', sortable: true},
                {text: "Schema", flex: 1, dataIndex: 'schema', sortable: true},
                {text: "Category", flex: 1, dataIndex: 'category', sortable: true}
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'schemainfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Index", flex: 1, dataIndex: 'property', sortable: true},
                {text: "Schema", flex: 1, dataIndex: 'value', sortable: true}
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'usersqlinfo'){
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Name", flex: 1, dataIndex: 'name', sortable: true},
                {text: "Class", flex: 1, dataIndex: 'datatype', sortable: true},
                {text: "Datatype", flex: 1, dataIndex: 'precision', sortable: true},
                {text: "Basetype", flex: 1, dataIndex: 'prefix', sortable: true},
                {text: "Remark", flex: 1, dataIndex: 'suffix', sortable: true},
                {text: "Category", flex: 1, dataIndex: 'param', sortable: true},
                {text: "Schema", flex: 1, dataIndex: 'nullable', sortable: true}               
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
   }
   else if(type == 'standardsqlinfo'){
	   grid = Ext.create('Ext.grid.Panel', {
             store: store,
             columns: [
                 {text: "Name", flex: 1, dataIndex: 'name', sortable: true},
                 {text: "Data_type", flex: 1, dataIndex: 'datatype', sortable: true},
                 {text: "Precision", flex: 1, dataIndex: 'precision', sortable: true},
                 {text: "Prefix", flex: 1, dataIndex: 'prefix', sortable: true},
                 {text: "Suffix", flex: 1, dataIndex: 'suffix', sortable: true},
                 {text: "Param", flex: 1, dataIndex: 'param', sortable: true},
                 {text: "Nullable", flex: 1, dataIndex: 'nullable', sortable: true},
                 {text: "Case Sensitive", flex: 1, dataIndex: 'casesensitive', sortable: true},
                 {text: "Searchable", flex: 1, dataIndex: 'searchable', sortable: true},
                 {text: "Unsigned", flex: 1, dataIndex: 'unsigned', sortable: true},
                 {text: "Auto Increamental", flex: 1, dataIndex: 'autoincremental', sortable: true}
             ],
             //renderTo:'example-grid',
             width: 640,
             height: 500
         });
    }
    else if(type == 'funcinfo'){
    	// create the grid
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "String Fx", flex: 1, dataIndex: 'stringfx', sortable: true},
                {text: "Numeric Fx", flex: 1, dataIndex: 'numericfx', sortable: true},
                {text: "Date Fx", flex: 1, dataIndex: 'datefx', sortable: true},
                {text: "System Fx", flex: 1, dataIndex: 'systemfx', sortable: true},
                {text: "SQL Keywords", flex: 1, dataIndex: 'sqlkeyword', sortable: true}
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else if(type == 'cataloginfo'){
    	// create the grid
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Index", flex: 1, dataIndex: 'property', sortable: true},
                {text: "Catalog", flex: 1, dataIndex: 'value', sortable: true}
              
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    else{
    	// create the grid
    	grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {text: "Property", flex: 1, dataIndex: 'property', sortable: true},
                {text: "Value", width: 180, dataIndex: 'value', sortable: true}          
            ],
            //renderTo:'example-grid',
            width: 640,
            height: 500
        });
    }
    
    
    var gridWindow = new Ext.Window({
    	height: 540,
        width: 655,
        title: title,
        items: [
            grid
        ]
    });
    
    gridWindow.show();


}
var form = null;
var win;
var columns;
var tables;
	
function loadPopUpForm(action) {

	var record = Ext.getCmp('databasesGrid').getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectDBRecord']);
        return false;
    }
        
	conditionQuery = '';
	var ds = Ext.create('Ext.data.ArrayStore', {
        data: columns[tables[0][0]],
        fields: ['value','text'],
        sortInfo: {
            field: 'value',
            direction: 'ASC'
        }
    });
	
	addConditionTable = tables[0][0];
	
	//if (!win) {
		form = Ext
				.widget(
						'form',
						{
							layout : {
								type : 'vbox',
								align : 'stretch'
							},
							border : false,
							bodyPadding : 10,
							autoScroll:true,
							fieldDefaults : {
								labelAlign : 'top',
								labelWidth : 100,
								labelStyle : 'font-weight:bold'
							},
							defaults : {
								margins : '0 0 10 0'
							},

							
							items : [ {
								xtype : 'fieldcontainer',
								fieldLabel : 'Select Table',
								labelStyle : 'font-weight:bold;padding:0',
								layout : 'hbox',								

								fieldDefaults : {
									labelAlign : 'top'
								},

								items : [ {
									flex : 1,
									name : 'tables',	
									id: 'tables',	
									xtype : 'combobox',		
									store: Ext.create('Ext.data.ArrayStore', {
				                        fields: ['abbr', 'table'],
				                        data : tables 
				                    }),
				                    valueField: 'abbr',
				                    typeAhead: true,
				                    queryMode: 'local',
				                    emptyText: 'Select a table...',
				                    listeners: {
				                        select: function(combo,value, index) {
				                        	changeStore(value[0].data.table);
				                        	addConditionTable = value[0].data.table;
				                        }
				                    },
				                    value:tables[0][0],
				                    displayField: 'table'
									
								},{
						            xtype: 'displayfield',
						            name: 'addCondition',		
						            fieldLabel : '',
						            value: '<a href="javascript:void(0)" onclick="addCondition(\'\', true);">Add Condition </a>|'
						        },
								{
						            xtype: 'displayfield',
						            name: 'showCondition',	
						            fieldLabel : '',
						            value: '<a href="javascript:void(0)" onclick="showCondition();">Show Condition </a>'
						        }
								 ]
							}, 
							{
					            xtype: 'itemselector',
					            name: 'itemselector',
					            id:'itemselector',
					            itemId: 'itemselector',
					            anchor: '100%',
					            fieldLabel: 'Select Column(s) for Quality Rule',
					            imagePath: '../ux/images/',

					            store: ds,
					            displayField: 'value',
					            valueField: 'value',					           

					            allowBlank: false,
					            // minSelections: 2,
					            // maxSelections: 3,
					            msgTarget: 'side'
					        } ],

							buttons : [
									{
										text : 'Cancel',
										handler : function() {
											this.up('form').getForm().reset();
											this.up('window').destroy();
										}
									},
									{
										text : 'Next',
										handler : function() {
											if(action == 'inclusive' || action == 'exclusive'){
												 var columns = Ext.getCmp('itemselector').getValue();
												 var table = Ext.getCmp('tables').getValue();
												 var globAction = action;
							                	 Ext.MessageBox.confirm('Confirm', 'Records will be updated. Do you want to continue ?', function(btn){
							                		 if(btn == "yes") {
														loadPopUpGrid(columns, table, globAction, '');
							                		 }

							                	 });
											}
											else if(action == 'descrete' || action == 'descretenomatch'){
												loadPopUpDescrete(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(), action);
											}
											else if(action == 'standard'){
												loadPopUpStandard(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(), action);
											}
											else if(action == 'similarity'){
												loadPopUpSimilarity(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(), action);
											}
											else if(action == 'format' || action == 'formatnomatch'){
												loadformatter(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(),action)

											}
											else if(action == 'replacenull'){
												slowInputDialogForReplaceNull(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(),action)
											}
											else if(action == 'comparison'){
												var tables = Ext.getCmp('tables').getValue();
												var columns = Ext.getCmp('itemselector').getValue();
							                	 Ext.MessageBox.confirm('Table Comparison Dialog', 'Choose table to compare from other Data Source ?', function(btn){
							                		 if(btn == "yes") {
							                			 loadComparisonWindow(action, tables, columns);
							                		 }

							                	 });
												
											}
											else{
												loadPopUpGrid(Ext.getCmp('itemselector').getValue(), Ext.getCmp('tables').getValue(), action, '');
											}
											this.up('window').destroy();
										}
									} ]
						});

		win = Ext.widget('window', {
			title : 'Select Table and Columns',
			closeAction : 'destroy',
			width : 500,
			height : 400,
			minHeight : 400,
			layout : 'fit',
			resizable : true,
			modal : true,
			autoScroll:true,
			items : form
		});
	//}
	win.show();

}
function loadComparisonWindow(action, table, columns){
	var companel = connectionformLoader(table, columns);
	var compwin = Ext.create('Ext.Window', {
		title : 'Connection Dialog',
		closeAction : 'destroy',
		width : 500,
		height : 500,
		minHeight : 400,
		layout : 'fit',
		resizable : true,
		modal : true,
		items:[
		       companel
		   ]
	});
//}
	compwin.show();
}
var cardinalityForm= null;
function loadCardinalityForm(action, edit) {

	var record = Ext.getCmp("databasesGrid").getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectDBRecord']);
        return false;
    };
    
	conditionQuery = '';
	var ds = Ext.create('Ext.data.ArrayStore', {
        data: columns[tables[0][0]],
        fields: ['value','text'],
        sortInfo: {
            field: 'value',
            direction: 'ASC'
        }
    });
	
	addConditionTable = tables[0][0];
	var compareTableStr ="<table> <tr class='body-style'> <td>Select Table A</td><td></td><td></td><td></td><td></td><td>Select Table B</td> </tr> <tr class='body-style'> <td><select id='aTable' onchange=\"changeColumnValues('aTableColumn', this.value)\"></select></td><td></td><td></td><td></td><td></td><td><select id='bTable' onchange=\"changeColumnValues('bTableColumn',this.value)\"></select></td> </tr> <tr class='body-style'> <td><select id='aTableColumn'></select></td><td></td><td></td><td></td><td></td><td><select id='bTableColumn'></select></td> </tr> <tr class='body-style'> <td><div id='aTableType'></div></td><td><input type='radio' checked name='relation' value='0'>1:1</td><td><input type='radio' name='relation' value='1'>1:M(Including One)</td><td><input type='radio' name='relation' value='2'>1:M(Excluding One)</td><td><input type='radio' name='relation' value='3'>1:<input type='text' id='1mt' size='4' value='5'></td><td><div id='bTableType'></div></td> </tr><tr class='body-style'><td></td><td></td><td></td><td><Button onclick='clickAnalyse();'> Analyze </Button></td><td></td><td></td></tr></table><table> <tr class='body-style'><td><a href='javascript:void(0)' onclick=\"clickAnalyseLink('TANM', " +edit + ");\">Table A No Match</a></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td><a href='javascript:void(0)' onclick=\"clickAnalyseLink('TAM', " +edit+ ");\">Table A Match</a></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td><a href='javascript:void(0)' onclick=\"clickAnalyseLink('TBM', " +edit+ ");\">Table B Match</a></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td><a href='javascript:void(0)' onclick=\"clickAnalyseLink('TBNM'," + edit + ");\">Table B No Match</a></td></tr></table><table><tr><td><div id='tcChart'></div></td><td><div id='tcGrid'></div></td></tr></table>";

	//if (!win) {
	cardinalityForm = Ext
				.widget(
						'form',
						{
							layout : {
								type : 'vbox',
								align : 'stretch'
							},
							border : false,
							bodyPadding : 10,

							fieldDefaults : {
								labelAlign : 'top',
								labelWidth : 100,
								labelStyle : 'font-weight:bold'
							},
							defaults : {
								margins : '0 0 10 0'
							},

							
							items : [ {														

								fieldDefaults : {
									labelAlign : 'top'
								},
								html : compareTableStr,
							
							}],

							buttons : [
									]
						});

		win = Ext.widget('window', {
			title : 'Compare Table Frame',
			closeAction : 'destroy',
			width : 900,
			height : 550,
			minHeight : 400,
			layout : 'fit',
			resizable : true,
			modal : true,
			items : cardinalityForm
		});
	//}
	win.show();
	var x=document.getElementById("aTable");
	var y=document.getElementById("bTable");
	
	var xc=document.getElementById("aTableColumn");
	var yc=document.getElementById("bTableColumn");
	for(var i=0; i< tables.length;i++){
		var option1=document.createElement("option");
		var option2=document.createElement("option");
		option1.text= tables[i][0];
		option2.text= tables[i][0];
		try
		  {
		  // for IE earlier than version 8
		  x.add(option1,x.options[null]);
		  y.add(option2,y.options[null]);
		  }
		catch (e)
		  {
		  x.add(option1,null);
		  y.add(option2,null);
		  }
	}
	var defColumns = columns[tables[0][0]];
	for(var j=0; j< defColumns.length;j++){
		var option3=document.createElement("option");
		var option4=document.createElement("option");
		option3.text= defColumns[j][0];
		option4.text= defColumns[j][0];
		try
		  {
		  // for IE earlier than version 8
		  xc.add(option3,x.options[null]);
		  yc.add(option4,y.options[null]);
		  }
		catch (e)
		  {
		  xc.add(option3,null);
		  yc.add(option4,null);
		  }
	}
}
function clickAnalyse(){
	var relationValue = '';
	var radios = document.getElementsByName("relation");
	for(var i=0;i<radios.length;i++){
		if(radios[i].checked){
			relationValue = radios[i].value;
		}
	}
	Ext.Ajax.request({
		   url: 'compareTable.json',        
		   success: function(response) {	
			   var obj = Ext.decode(response.responseText);
			   
			   document.getElementById("tcChart").innerHTML='';
			   var myData = obj.data;
			   //var data = [['Table A',17,2],['Table B',17,2]];
				var store = Ext.create('Ext.data.JsonStore', {
					fields : [ 'name', 'data1', 'data2', 'data3' ],
					data : myData
				});

				var cardinalitywin = Ext.create('Ext.Panel', {
					renderTo : 'tcChart',
					width : 200,
					height : 300,
					hidden : false,
					title : '',
					layout : 'fit',
					items : {
						id : 'cardinalitychartCol',
						xtype : 'chart',
						style : 'background:#fff',
						animate : true,
						shadow : true,
						store : store,
						axes : [{
			                type: 'Numeric',
			                position: 'left',
			                fields: ['data1','data2', 'data3'],			                
			                grid: true,
			                minimum: 0
			            }, {
							type : 'Category',
							position : 'bottom',
							fields : [ 'name' ]
						} ],
						series : [ {
							type : 'column',							
							xField : 'name',
							yField : ['data1','data2','data3']
						} ]
					}
				});
				//store.load();
				cardinalityForm.doLayout();
			},   
		   //failure: failFn,
			params: {	        	
	        	table1: document.getElementById("aTable").value,
	        	table2: document.getElementById("bTable").value,
	        	col1: document.getElementById("aTableColumn").value,
	        	col2: document.getElementById("bTableColumn").value,
	        	relation: relationValue,	 
	        	ft: document.getElementById("1mt").value,	 
	        	action: "button",
	        	editable:false
	            
	        }
	});
}

function clickAnalyseLink(link, edit){
	var relationValue = '';	
	var radios = document.getElementsByName("relation");
	for(var i=0;i<radios.length;i++){
		if(radios[i].checked){
			relationValue = radios[i].value;			
		}
	}
	Ext.Ajax.request({
		   url: 'compareTable.json',        
		   success: function(response) {	
			   var obj = Ext.decode(response.responseText);
			   
			   document.getElementById("tcGrid").innerHTML='';
			   var myData = obj.data;

			             // create the data store
			             var store = Ext.create('Ext.data.ArrayStore', {
			                 fields: obj.fields,
			                 data: myData
			             });

			             // create the Grid
			             var grid = Ext.create('Ext.grid.Panel', {
			                 store: store,
			                 stateful: true,
			                 stateId: 'stateGrid',
			                 columns: obj.columns,
			                 height: 250,
			                 width: 500,
			                 title: '',
			                 renderTo: 'tcGrid',
			                 viewConfig: {
			                     stripeRows: true
			                 },
			                 plugins: [
			     	                  Ext.create('Ext.grid.plugin.RowEditing', {
			     	                      clicksToEdit: 2
			     	                  })
			     	        ]
			             });
			             store.load();
			             cardinalityForm.doLayout();
			},   
		   //failure: failFn,
			params: {	        	
	        	table1: document.getElementById("aTable").value,
	        	table2: document.getElementById("bTable").value,
	        	col1: document.getElementById("aTableColumn").value,
	        	col2: document.getElementById("bTableColumn").value,
	        	relation: relationValue,	 
	        	ft: document.getElementById("1mt").value,	 
	        	action: "link",
	        	link:link,
	        	editable:edit
	            
	        }
	});
}
function changeColumnValues(selval,value){
	document.getElementById(selval).innerHTML = "";
	var sel=document.getElementById(selval);
	
	var defColumns = columns[value];
	for(var j=0; j< defColumns.length;j++){
		var option=document.createElement("option");
		
		option.text= defColumns[j][0];
		
		try
		  {
		  // for IE earlier than version 8
		  sel.add(option,x.options[null]);		
		  }
		catch (e)
		  {
		  sel.add(option,null);		
		  }
	}
}
function changeStore(key){	
	var combobox = form.down('#itemselector');
	var ds = Ext.create('Ext.data.ArrayStore', {
    data: columns[key],
    fields: ['value','text'],
    sortInfo: {
        field: 'value',
        direction: 'ASC'
    }
	});
	combobox.bindStore(ds);
}


//pop up grid
function loadPopUpGrid(oriColumnValues, table, action, input, formatValues, formatType, filePath, text, delimeter) {
	var columnValues = '';
	var title = '';
	var editableCount = 0;
	var url = 'dataQuality.json?queryString=' + conditionQuery;
	
	if(action == 'similarity'){
		columnValues = 'Delete Editable,' + oriColumnValues;  
		title = 'Similar Records';
		editableCount = 1;
		url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&searchCriteria=' + input + '&importance=' + formatValues + '&skipWords=' + formatType;

	}
	else if(action == 'duplicate'){
		columnValues = 'count,' + oriColumnValues;  
		title = 'Duplicate Row Show Window';
		url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&replace=' + input;

	}
	else if(action == 'uppercase' || action == 'lowercase' || action == 'titlecase' || action == 'sentencecase' || action == 'replacenull' || action == 'format' || action == 'formatnomatch' || action == 'standard' || action == 'descrete' || action == 'descretenomatch' || action == 'inclusive' || action == 'exclusive'){
		url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&replace=' + input;
		var addPrefix = true;
		if(action == 'inclusive' || action == 'exclusive'){
			title = 'Completeness';
			addPrefix = false;

		}
		else if(action == 'descrete' || action == 'descretenomatch'){
			url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&text=' + text + '&delimeter=' + delimeter;
		}
		else if(action == 'standard'){
			url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&filePath=' + filePath;
		}
		else if(action == 'replacenull'){
			title = 'Replace Null';
		}
		else if(action == 'format' || action == 'formatnomatch'){
			url = url + '&action='+ action + '&table=' + table + '&columns='+oriColumnValues + '&formatType=' + formatType + '&formatValues=' + formatValues;
		}		
		else{
			title = 'Case Format Window';
		}
		var tmpColumnArr = oriColumnValues.toString().split(","); 

		var tmpColumnValues = '';
		var colArr =  columns[table];
		for(var i=0; i< colArr.length; i++) {  
			var tmpColArr = colArr[i];		
			if(!contains(tmpColumnArr,tmpColArr[0]) || addPrefix){
				if(tmpColumnValues == ''){
					tmpColumnValues = tmpColArr[0];			
				}
				else{
					tmpColumnValues = tmpColumnValues + "," + tmpColArr[0];			
				}
				if(action == 'inclusive' || action == 'exclusive'){
					editableCount = editableCount + 1;
				}
			}
			
		}
		for(var i=0; i< tmpColumnArr.length; i++) {   
			if(addPrefix){
				columnValues = tmpColumnArr[i] + ' Editable,';  
			}
			else{
				columnValues = tmpColumnArr[i] + ',';  

			}
			editableCount = editableCount + 1;
		}
		columnValues = columnValues + tmpColumnValues
	}
	
	var gridColumns = [];
	var columnArr = columnValues.toString().split(","); 
	
	for(var i=0; i< columnArr.length; i++) {   

		if(i < editableCount){
			gridColumns.push({ 
		        "text" : columnArr[i],
		        "dataIndex"  : columnArr[i],
		        "sortable"       : true,
		        "width": (100/(columnArr.length)) + "%",
		        "editor": 'textfield'
		    });
		}
		else{
			gridColumns.push({ 
		        "text" : columnArr[i],
		        "dataIndex"  : columnArr[i],
		        "sortable"       : true,
		        "width": (100/(columnArr.length)) + "%",		        
		    });
		}
	}
	
	Ext.define('Col', {
        extend: 'Ext.data.Model',
        fields: columnArr
    });

    var store = new Ext.data.Store({
        model: 'Col',
        proxy: {
            type: 'ajax',
            url: url,
            reader: {
                type: 'json',
                root: 'items',
                totalProperty: 'totalCount'
            }
        }
    });
    store.load();
	
    var winGrid;
    
	if (!winGrid) {
		// create the grid
		var grid = Ext.create('Ext.grid.Panel', {
	        store: store,
	        columns: gridColumns,	       
	        width: 740,
	        height: 200,
	     // inline buttons
	        dockedItems: [{
	            xtype: 'toolbar',
	            dock: 'bottom',
	            ui: 'footer',
	            layout: {
	                pack: 'center'
	            },
	            items: [{
	                minWidth: 80,
	                text: 'Commit All',
	                handler : function() {
	                	 var modified = store.getModifiedRecords();	                	 
	                	 //Ext.MessageBox.confirm('Confirm', 'Database will be updated. Do you want to continue ??', commitAll);
	                	 Ext.MessageBox.confirm('Confirm', 'Database will be updated. Do you want to continue ?', function(btn){
								//commitAll(modified, url);
	                		 if(btn == "yes") {
								var fieldName = grid.columns[0].dataIndex;
								var commitValue = '';
								store.each(function(rec){	
									if(commitValue == ''){
										commitValue = rec.get(fieldName);
									}
									else{
										commitValue = commitValue + "," + rec.get(fieldName);
									}
								    var tmp = rec.get(fieldName);
								});
								Ext.Ajax.request({
								   url: "dataQuality.json",        
								   success: function(response) {										   
									   winGrid.destroy();
									},   									  
									params: {	        	
										commitValue: commitValue,
										action: action,
										subAction:'commit',
										table: table,
										columns: oriColumnValues,
										replace: input,
										queryString: conditionQuery,
										text: text,
										delimeter: delimeter,
										filePath:filePath,
										formatType: formatType,
										formatValues: formatValues
							        }
								});
	                		 }
	                	 });
					}
	            },{
	                minWidth: 80,
	                text: 'Cancel',
	                handler : function() {	
	                	this.up('window').destroy();
	                }
	            }]
	        }],
	        plugins: [
	                  Ext.create('Ext.grid.plugin.RowEditing', {
	                      clicksToEdit: 2
	                  })
	        ]
	    });
	    
	   

	    winGrid = Ext.widget('window', {
			title : title,
			closeAction : 'hide',
			width : 740,
			height : 400,
			minHeight : 400,
			layout : 'fit',
			resizable : true,
			modal : true,
			items : [grid]
		});
	}
	winGrid.show();

}
function contains(a, obj) {
    var i = a.length;
    while (i--) {
       if (a[i] === obj) {
           return true;
       }
    }
    return false;
}
function loadformatter(columns, table, action){
	
	var formatTypes = [['Number','Number'],['Date','Date'],['Phone','Phone'],['Formatted String','Formatted String']];
	var formatValues = {'Number':[['##.####%','##.####%'],['##,###.##','##,###.##'],['##,###;-##,###','##,###;-##,###'],['0.000','0.000'],['###,###.##','###,###.##'],['###.00','###.00'],['¤####','¤####'],['#,###.##','#,###.##'],['##.E0','##.E0'],['%###','%###'],['######','######']],'Date':[['yyyy-MM-dd','yyyy-MM-dd'],['dd/MM/yyyy','dd/MM/yyyy'],['MM/dd/yyyy','MM/dd/yyyy'],['EEE, d MMM yyyy HH:mm:ss Z','EEE, d MMM yyyy HH:mm:ss Z'],['EEE MMM dd HH:mm:ss z yyyy','EEE MMM dd HH:mm:ss z yyyy'],['MM-dd-yyyy1','MM-dd-yyyy1']],'Phone':[['(###) ###-####','(###) ###-####'],['########','########'],['#### ####','#### ####'],['###.###.####','###.###.####'],['###-###-####','###-###-####'],['(0##)########','(0##)########'],['##### #####','##### #####'],['(###)###-####','(###)###-####']],'Formatted String':[['UUUUU','UUUUU'],['######','######']]};

	var ds = Ext.create('Ext.data.ArrayStore', {
        data: formatValues['Number'],
        fields: ['value','text'],
        sortInfo: {
            field: 'value',
            direction: 'ASC'
        }
    });
	
		//if (!win) {
			var formMatch = Ext
					.widget(
							'form',
							{
								layout : {
									type : 'vbox',
									align : 'stretch'
								},
								border : false,
								bodyPadding : 10,

								fieldDefaults : {
									labelAlign : 'top',
									labelWidth : 100,
									labelStyle : 'font-weight:bold'
								},
								defaults : {
									margins : '0 0 10 0'
								},

								
								items : [ {
									xtype : 'fieldcontainer',
									fieldLabel : 'Select Table',
									labelStyle : 'font-weight:bold;padding:0',
									layout : 'hbox',								

									fieldDefaults : {
										labelAlign : 'top'
									},

									items : [ {
										flex : 1,
										name : 'formatTypes',	
										id: 'formatTypes',	
										xtype : 'combobox',		
										store: Ext.create('Ext.data.ArrayStore', {
					                        fields: ['abbr', 'table'],
					                        data : formatTypes 
					                    }),
					                    valueField: 'abbr',
					                    typeAhead: true,
					                    queryMode: 'local',
					                    emptyText: 'Select a table...',
					                    listeners: {
					                        select: function(combo,value, index) {					                        	
					                        	
					                        	var combobox = formMatch.down('#matchItemselector');
					                        	var ds = Ext.create('Ext.data.ArrayStore', {
					                            data: formatValues[value[0].data.table],
					                            fields: ['value','text'],
					                            sortInfo: {
					                                field: 'value',
					                                direction: 'ASC'
					                            }
					                        	});
					                        	combobox.bindStore(ds);
					                        }
					                    },
					                    displayField: 'table'
										
									}]
								}, 
								{
						            xtype: 'itemselector',
						            name: 'matchItemselector',
						            id:'matchItemselector',
						            itemId: 'matchItemselector',
						            anchor: '100%',
						            fieldLabel: 'Select Relavant Formats',
						            imagePath: '../ux/images/',

						            store: ds,
						            displayField: 'text',
						            valueField: 'value',					           

						            allowBlank: false,
						            // minSelections: 2,
						            // maxSelections: 3,
						            msgTarget: 'side'
						        } ],

								buttons : [
										{
											text : 'Cancel',
											handler : function() {
												this.up('form').getForm().reset();
												this.up('window').destroy();
											}
										},
										{
											text : 'Ok',
											handler : function() {												
												loadPopUpGrid(columns, table, action, '' ,Ext.getCmp('matchItemselector').getValue(), Ext.getCmp('formatTypes').getValue());
											}
										} ]
							});

			var winMatch = Ext.widget('window', {
				title : 'Format Dialog',
				closeAction : 'destroy',
				width : 500,
				height : 400,
				minHeight : 400,
				layout : 'fit',
				resizable : true,
				modal : true,
				items : formMatch
			});
		//}
			winMatch.show();
}

function loadPopUpSimilarity(columns, table, action) {	

	// if (!win) {
	var	similarityform = Ext
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
								items : [ 
									{
									    xtype: 'displayfield',
									    name: 'field',		
									    fieldLabel : 'Field',
									    value: columns
									},	
									{
										xtype : 'combobox',
										fieldLabel : 'Search Criteria',
										name : 'sc',
										id:'sc',
										store:          Ext.create('Ext.data.Store', {
		                                    fields : ['name', 'value'],
		                                    data   : [
		                                        {name: 'Don\'t Use',value : '0' },
		                                        {name: 'Exact', value : '1'},
		                                        {name: 'Similar-Any',value : '2'},
		                                        {name: 'Similar-All',value : '3'},
		                                        {name: 'Left Imp.',value : '4'},
		                                        {name: 'Right Imp.',value : '5'}
		                                    ]
		                                }),
										valueField : 'value',
										displayField : 'name',
										value:'0',
										typeAhead : true									

									},
									{
									xtype : 'combobox',
									fieldLabel : 'Importance',
									name : 'imp',
									id : 'imp',
									store:          Ext.create('Ext.data.Store', {
	                                    fields : ['name', 'value'],
	                                    data   : [
	                                        {name : 'Low',   value: '0'},
	                                        {name : 'Medium',  value: '1'},
	                                        {name : 'High', value: '2'}
	                                    ]
	                                }),
									valueField : 'value',
									displayField : 'name',
									value:'0',
									typeAhead : true									

								},{
									fieldLabel : 'Skip Words',
									name : 'skipWords',
									id:'skipWords',
									value: 'And,Or,Not'
								} ]
							} ],
							buttons : [
									{
										text : 'Cancel',
										handler : function() {
											this.up('form').getForm().reset();
											this.up('window').destroy();
										}
									},
									{
										text : 'Search',
										handler : function() {											
											var sc = Ext.getCmp('sc').getValue();
											var imp = Ext.getCmp('imp').getValue();
											var sw = Ext.getCmp('skipWords').getValue();
											loadPopUpGrid(columns, table, action, sc, imp, sw, '','');		
				                        	this.up('window').destroy();
										}
									} ]
						});

		var similaritywin = Ext.widget('window', {
			title : 'Similarity Map',
			closeAction : 'destroy',
			width : 500,
			height : 200,
			minHeight : 200,
			layout : 'fit',
			resizable : true,
			modal : true,
			items : similarityform
		});
	// }
		similaritywin.show();

}

function loadPopUpStandard(columns, table, action) {	

	// if (!win) {
	var	similarityform = Ext
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
								items : [ 									
									{
							            xtype: 'filefield',
							            name: 'file1',
							            fieldLabel: 'File upload'
									}
									]
							} ],
							buttons : [
										{
											text : 'Cancel',
											handler : function() {
												this.up('form').getForm().reset();
												this.up('window').destroy();
											}
										},
										{
											text : 'Open',
											handler : function() {											
												var form = this.up('form').getForm();
								                if(form.isValid()){
								                    form.submit({
								                        url: 'profilerFileUpload.json',
								                        waitMsg: 'Uploading your file...',
								                        success: function(fp, o) {
								                        	loadPopUpGrid(columns, table, action, '', '', '', o.result.file);		
								                        	this.up('window').destroy();
								                        }
								                    });
								                }
											}
										} ]
							
						});

		var similaritywin = Ext.widget('window', {
			title : 'Select Standardization File',
			closeAction : 'destroy',
			width : 500,
			height : 200,
			minHeight : 200,
			layout : 'fit',
			resizable : true,
			modal : true,
			items : similarityform
		});
	// }
		similaritywin.show();

}

function loadPopUpDescrete(columns, table, action) {	

	// if (!win) {
	var	similarityform = Ext
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
								items : [ 
										{
											xtype : 'combobox',
											fieldLabel : 'Delimeter',
											name : 'delimeter',
											id: 'delimeter',
											store:          Ext.create('Ext.data.Store', {
										        fields : ['name', 'value'],
										        data   : [
										            {name : 'Comma',   value: 'Comma'},
										            {name : 'New Line',  value: 'New Line'},
										            {name : 'White Space', value: 'White Space'},
										            {name : 'Others', value: 'Others'}
										        ]
										    }),
											valueField : 'value',
											displayField : 'name',
											typeAhead : true									
										
										},
										{
										    xtype: 'textfield',
										    name: 'others',
										    id: 'others',
										    fieldLabel: 'Others'
										},
										{
										    xtype: 'displayfield',
										    name: 'dateFormat',
										    fieldLabel: 'Date Format',
										    value:'dd-MM-yyyy'
										},
										{
										    xtype: 'textareafield',
										    name: 'text',
										    id: 'text'
										    
										}
									]
							} ],
							buttons : [
										{
											text : 'Cancel',
											handler : function() {
												this.up('form').getForm().reset();
												this.up('window').destroy();
											}
										},
										{
											text : 'Ok',
											handler : function() {		
												var delim = Ext.getCmp('delimeter').getValue();
												if(delim =='Others'){
													delim = Ext.getCmp('others').getValue();
												}
												loadPopUpGrid(columns, table, action, '', '', '', '',Ext.getCmp('text').getValue(),delim);		
					                        	this.up('window').destroy();
											}
										} ]
							
						});

		var similaritywin = Ext.widget('window', {
			title : 'Select Standardization File',
			closeAction : 'destroy',
			width : 500,
			height : 300,
			minHeight : 300,
			layout : 'fit',
			resizable : true,
			modal : true,
			items : similarityform
		});
	// }
		similaritywin.show();

}
function commitAll(modified) {	
	
	alert(modified);
}
function tableModelInfoTab(type) {

	Ext.getCmp('home-center').removeAll();
	
	var record = Ext.getCmp("databasesGrid").getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectDBRecord']);
        return false;
    }
    
	var tabs = Ext.createWidget('tabpanel',
			{
				//renderTo : 'home-center',
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
							title : 'Tables with No PK',
							itemId : 'tablesNoPK',
							loader : {
								url : 'dbInfo.json?type=tablemodelinfo&subType=noPK',
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
							title : 'Tables with NO FK',
							itemId : 'tablesNoFK',
							loader : {
								url : 'dbInfo.json?type=tablemodelinfo&subType=noFK',
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
						} ,
						{
							title : 'Table Model',
							itemId : 'tableModel',
							loader : {
								url : 'dbInfo.json?type=tablemodelinfo',
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
	
	Ext.getCmp('home-center').add(tabs);
	var tableModelwin = Ext.widget('window', {
		title : 'Relationship Pane',
		closeAction : 'destroy',
		width : 800,
		height : 500,
		minHeight : 300,
		layout : 'fit',
		resizable : true,
		modal : true,
		items : tabs
	});
// }
	tableModelwin.show();
}
