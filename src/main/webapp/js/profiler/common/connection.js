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

Ext.require([ 'Ext.form.*', 'Ext.data.*' ]);

function connectionform(){

	var formPanel = Ext.create('Ext.form.Panel', {
		renderTo : 'form-connection',
		frame : true,
		title : 'Connection Information',
		width : 440,
		bodyPadding : 5,
		waitMsgTarget : true,

		fieldDefaults : {
			labelAlign : 'right',
			labelWidth : 135,
			msgTarget : 'side'
		},

		// configure how to read the XML data
		//reader : Ext.create('Ext.data.reader.Xml', {			
		//	successProperty : '@success'
		//}),

		items : [ {
			xtype : 'fieldset',
			title : 'Connection Information',
			defaultType : 'textfield',
			defaults : {
				width : 380
			},
			items : [ {
				xtype : 'combobox',
				fieldLabel : 'DB Type',
				name : 'db_type',
				store : Ext.create('Ext.data.ArrayStore', {
					fields : [ 'abbr', 'db_type' ],
					data : Analysis.data.db_types
				}),
				valueField : 'abbr',
				displayField : 'db_type',
				typeAhead : true,
				queryMode : 'local',
				listeners : {
					select : {
						fn : function(combo, value) {
							autoFillFields(combo.getValue());
						}
					}
				}

			}, {
				fieldLabel : 'DB Name',
				name : 'db_name',
				id : 'db_name'
			}, {
				fieldLabel : 'User',
				name : 'user'
			}, {
				fieldLabel : 'Password',
				name : 'password'
			}, {
				fieldLabel : 'DB Driver',
				name : 'db_driver',
				id : 'db_driver'
			}, {
				fieldLabel : 'DB Protocol',
				name : 'db_protocol',
				id : 'db_protocol'
			}, {
				fieldLabel : 'JDBC URL',
				name : 'jdbc_url'
			}, {
				fieldLabel : 'Data Catalog',
				name : 'data_catalog'
			}, {
				fieldLabel : 'DB Schema Pattern',
				name : 'db_schema_pattern'
			}, {
				fieldLabel : 'DB Table Pattern',
				name : 'db_table_pattern'
			}, {
				fieldLabel : 'DB Column Pattern',
				name : 'db_column_pattern'
			}, {
				fieldLabel : 'DB Show Type',
				name : 'db_show_type',
				id : 'db_show_type'
			}, {
				xtype : 'hidden',
				name : 'common_db_type',
				id : 'common_db_type'
			} ]
		} ],

		buttons : [ {
			text : 'Test Connection',
			handler : function() {
				this.up('form').getForm().submit({
					url : 'testConnection.json',
					submitEmptyText : false,
					waitMsg : 'Connecting to Database...',
					success : function(response) {						
						Ext.Msg.alert('Status','Connection Succesful.');
						Ext.getCmp('continue').enable();
					},
					failure : function(response) {						
						Ext.Msg.alert('Status','Connection Failed.');
					}
				});
			}
		}, {
			text : 'Continue',
			disabled: true,
			id: 'continue',	
			handler : function() {
				window.location = "Home.do";
			}
		} ]
	});

	return formPanel;
}

function connectionformLoader(table, columns){
	var compTables = null;
	var compColumns = null;
	var formPanel = Ext.create('Ext.form.Panel', {
		//renderTo : 'form-connection',
		frame : true,
		title : 'Connection Information',
		width : 440,
		bodyPadding : 5,
		waitMsgTarget : true,

		fieldDefaults : {
			labelAlign : 'right',
			labelWidth : 135,
			msgTarget : 'side'
		},

		// configure how to read the XML data
		//reader : Ext.create('Ext.data.reader.Xml', {			
		//	successProperty : '@success'
		//}),

		items : [ {
			xtype : 'fieldset',
			title : 'Connection Information',
			defaultType : 'textfield',
			defaults : {
				width : 380
			},
			items : [ {
				xtype : 'combobox',
				fieldLabel : 'DB Type',
				name : 'db_type',
				store : Ext.create('Ext.data.ArrayStore', {
					fields : [ 'abbr', 'db_type' ],
					data : Analysis.data.db_types
				}),
				valueField : 'abbr',
				displayField : 'db_type',
				typeAhead : true,
				queryMode : 'local',
				listeners : {
					select : {
						fn : function(combo, value) {
							autoFillFields(combo.getValue());
						}
					}
				}

			}, {
				fieldLabel : 'DB Name',
				name : 'db_name',
				id : 'db_name'
			}, {
				fieldLabel : 'User',
				name : 'user',
				id:  'user'
			}, {
				fieldLabel : 'Password',
				name : 'password',
				id:  'password'
			}, {
				fieldLabel : 'DB Driver',
				name : 'db_driver',
				id : 'db_driver'
			}, {
				fieldLabel : 'DB Protocol',
				name : 'db_protocol',
				id : 'db_protocol'
			}, {
				fieldLabel : 'JDBC URL',
				name : 'jdbc_url',
				id:  'jdbc_url'
			}, {
				fieldLabel : 'Data Catalog',
				name : 'data_catalog',
				id:  'data_catalog'
			}, {
				fieldLabel : 'DB Schema Pattern',
				name : 'db_schema_pattern',
				id:  'db_schema_pattern'
			}, {
				fieldLabel : 'DB Table Pattern',
				name : 'db_table_pattern',
				id:  'db_table_pattern'
			}, {
				fieldLabel : 'DB Column Pattern',
				name : 'db_column_pattern',
				id:  'db_column_pattern'
			}, {
				fieldLabel : 'DB Show Type',
				name : 'db_show_type',
				id : 'db_show_type'
			}, {
				xtype : 'hidden',
				name : 'common_db_type',
				id : 'common_db_type'
			},{
				xtype : 'hidden',
				name : 'comparison',
				id : 'comparison',
				value:'true'
			} ]
		} ],

		buttons : [ {
			text : 'Test Connection',
			handler : function() {
				Ext.Ajax.request({
					   url: "testConnection.json",   
					   waitMsg : 'Connecting to Database...',
					   success: function(response) {										   
						    var obj = Ext.decode(response.responseText);
						    if(obj.success == true) {
						    	compTables = obj.tables;
						    	compColumns = obj.columns;
						    	Ext.Msg.alert('Status','Connection Succesful.');
						    	Ext.getCmp('continue').enable();
						    } else {
						    	Ext.Msg.alert('Status','Connection Failed.');
						    }
						},  
						failure : function(response) {						
							Ext.Msg.alert('Status','Connection Failed.');
						},
						params: {	        	
							db_name: Ext.getCmp('db_name').getValue(),
							user: Ext.getCmp('user').getValue(),
							password: Ext.getCmp('password').getValue(),
							db_driver: Ext.getCmp('db_driver').getValue(),
							db_protocol: Ext.getCmp('db_protocol').getValue(),
							jdbc_url: Ext.getCmp('jdbc_url').getValue(),
							data_catalog: Ext.getCmp('data_catalog').getValue(),
							db_schema_pattern: Ext.getCmp('db_schema_pattern').getValue(),
							db_table_pattern: Ext.getCmp('db_table_pattern').getValue(),
							db_column_pattern: Ext.getCmp('db_column_pattern').getValue(),
							db_show_type: Ext.getCmp('db_show_type').getValue(),
							common_db_type: Ext.getCmp('common_db_type').getValue(),
							comparison:'true'
				        }
					});
				
			}
		}, {
			text : 'Continue',
			disabled: true,
			id: 'continue',	
			handler : function() {
				formPanel.destroy();
				comparionsForm(table,columns, compTables, compColumns);
			}
		} ]
	});

	return formPanel;
}

function autoFillFields(selectedValue) {
	if (selectedValue == 'mysql_client') {
		Ext.getCmp('db_protocol').setValue("jdbc:mysql");
		Ext.getCmp('db_driver').setValue("com.mysql.jdbc.Driver");
		Ext.getCmp('db_name').setValue("//hostname/db");
		Ext.getCmp('common_db_type').setValue("MYSQL");
		
		Ext.getCmp('db_show_type').setValue("TABLE");
	}
	else if (selectedValue == 'oracle_native') {
		Ext.getCmp('db_protocol').setValue("jdbc:oracle:thin");
		Ext.getCmp('db_driver').setValue("oracle.jdbc.OracleDriver");
		Ext.getCmp('db_name').setValue("//hostname/SID");	
		Ext.getCmp('common_db_type').setValue("ORACLE_NATIVE");

		
	}
	else if (selectedValue == 'oracle_windows') {
		Ext.getCmp('db_protocol').setValue("jdbc:odbc");
		Ext.getCmp('db_driver').setValue("sun.jdbc.odbc.JdbcOdbcDriver");
		Ext.getCmp('common_db_type').setValue("ORACLE_ODBC");	
		
		
	}
	else if (selectedValue == 'mysql_windows') {
		Ext.getCmp('db_protocol').setValue("jdbc:odbc");
		Ext.getCmp('db_driver').setValue("sun.jdbc.odbc.JdbcOdbcDriver");
		Ext.getCmp('common_db_type').setValue("MYSQL");		
	}
	else if (selectedValue == 'sqlserver_bridge') {
		Ext.getCmp('db_protocol').setValue("jdbc:odbc");
		Ext.getCmp('db_driver').setValue("sun.jdbc.odbc.JdbcOdbcDriver");
		Ext.getCmp('common_db_type').setValue("SQL_SERVER");
		
	}
	else if (selectedValue == 'access_bridege') {
		Ext.getCmp('db_protocol').setValue("jdbc:odbc");
		Ext.getCmp('db_driver').setValue("sun.jdbc.odbc.JdbcOdbcDriver");
		Ext.getCmp('common_db_type').setValue("MS_ACCESS");		
	}
	else if (selectedValue == 'other_jdbc') {
		Ext.getCmp('db_protocol').setValue("jdbc:dbname");
		Ext.getCmp('db_driver').setValue("jdbc.DbNameDriver");
		Ext.getCmp('common_db_type').setValue("Others");		
	}
	else if (selectedValue == 'other_bridge') {
		Ext.getCmp('db_protocol').setValue("jdbc:odbc");
		Ext.getCmp('db_driver').setValue("sun.jdbc.odbc.JdbcOdbcDriver");
		Ext.getCmp('common_db_type').setValue("Others");		
	}
	
}
var globCompColumns = null;
var colCount = null;
function comparionsForm(table, columns, compTables, compColumns){	
	globCompColumns = compColumns;
	var columnStr = "";

	var tmpColumns = columns.toString().split(",");
	colCount = tmpColumns.length;
	for(var ind=0;ind<tmpColumns.length;ind++){
		columnStr = columnStr + "<tr class='body-style'> <td>Column</td> <td>&nbsp;&nbsp;"+tmpColumns[ind]+"</td> <td>&nbsp;&nbsp;Maps&nbsp;&nbsp;</td> <td><select id='columnNames"+ind+"'></select></td> <td><input type='checkbox"+ind+"' id='checkKey'/></td> </tr>";
	}
	var finalStr= "<table> <tr class='body-style'> <td>Table</td> <td>&nbsp;&nbsp;Table&nbsp;&nbsp;"+table+"</td> <td>Table</td> <td><select id='tableNames' onchange=\"changeComparisonColumnValues(this.value)\"></select></td> <td>Unique Key</td> </tr> "+columnStr+" </table> <table> <tr class='body-style'> <td><input type='radio' checked name='comMatch' value='true'>Show Matched Record</td> <td><input type='radio' checked name='comMatch' value='false'>Show No-Matched Record</td> </tr> </table>";
	var	comparisonform = Ext
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
								text : 'Compare',
								handler : function() {	
									var rColumns = '';
									var gridColumns = [];
									var columnArr = []; 
									   for(var k=0; k< colCount; k++){
										   columnArr.push(document.getElementById("columnNames" + k).value);
										   gridColumns.push({ 
										        "text" : document.getElementById("columnNames" + k).value,
										        "dataIndex"  : document.getElementById("columnNames" + k).value,
										        "sortable"       : true,
										        "width": (100/(colCount)) + "%",
										        "editor": ''
										    });
										   if(rColumns == ''){
											   rColumns = document.getElementById("columnNames" + k).value;
										   }
										   else{
											   rColumns = rColumns + "," + document.getElementById("columnNames" + k).value;
										   }												
									   }
									   var radios = document.getElementsByName("comMatch");
									   var matchValue = '';
										for(var i=0;i<radios.length;i++){
											if(radios[i].checked){
												matchValue = radios[i].value;
											}
										}
									
									var url = "dataQuality.json?action=comparison&table=" + table + "&columns=" + columns + "&rTable=" + document.getElementById("tableNames").value + "&rColumns=" + rColumns + "&comMatch=" + matchValue;
									Ext.define('Comp',{
								        extend: 'Ext.data.Model',
								        fields: columnArr       
								    });

								    // create the Data Store
									var store = new Ext.data.Store({
								        model: 'Comp',
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
								    // create the grid
								    var grid = Ext.create('Ext.grid.Panel', {
								        store: store,
								        columns: gridColumns,
								        //renderTo:'example-grid',
								        width: 540,
								        height: 200
								    });
								   var  winComGrid = Ext.widget('window', {
										title : 'Table Comparison',
										closeAction : 'destroy',
										width : 740,
										height : 400,
										minHeight : 400,
										layout : 'fit',
										resizable : true,
										modal : true,
										items : [grid]
									});
								    this.up('window').destroy();
								    winComGrid.show();
								}
							},{
								text : 'Cancel',
								handler : function() {
									this.up('window').destroy();
								}
							} ]
				
			});
	
	
	var win = new Ext.Window({
		  title: 'Map Dialog',
		  closeAction : 'destroy',
		  width: 400,
		  height: 200,
		  preventBodyReset: true,
		  //html: finalStr,
		  items : comparisonform
		});
		win.show();
		
		var x=document.getElementById("tableNames");		
		for(var i=0; i< compTables.length;i++){
			var option1=document.createElement("option");			
			option1.text= compTables[i][0];
			
			try
			  {
			  // for IE earlier than version 8
			  x.add(option1,x.options[null]);
			 
			  }
			catch (e)
			  {
			  x.add(option1,null);
			 
			  }
		}
		
		var defColumns = compColumns[tables[0][0]];
		for(var k=0; k< colCount; k++){
		var xc=document.getElementById("columnNames" + k);
		for(var j=0; j< defColumns.length;j++){
			var option3=document.createElement("option");			
			option3.text= defColumns[j][0];			
			try
			  {
			  // for IE earlier than version 8
			  xc.add(option3,x.options[null]);			  
			  }
			catch (e)
			  {
			  xc.add(option3,null);
			 
			  }
		}
		}
}
function changeComparisonColumnValues(value){
	for(var i=0; i< colCount; i++){
	document.getElementById("columnNames" + i).innerHTML = "";
	var sel = document.getElementById("columnNames" + i);
	
	var defColumns = globCompColumns[value];
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
}