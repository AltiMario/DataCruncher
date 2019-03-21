<%@ page language="java" session="true" contentType="text/html; charset=iso-8859-1" %>
<%--
  ~ Copyright (c) 2019  Altimari Mario
  ~ All rights reserved
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<table width='100%'>
<tr width='100%'>
<td width='33%' align='center' class='body-style'><%=request.getAttribute("serverName")%></td>
<td width='33%' align='center' class='body-style'><%=request.getAttribute("parentValue")%></td>
<td width='33%' align='center' class='body-style'><%=request.getAttribute("selectedValue")%></td>
</tr>
</table>
<hr />
<table width='65%' align='center'>
<tr>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="addCondition('<%=request.getAttribute("parentValue")%>', false)">Add Condition</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="showCondition();">Show Condition</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="showNumberAnalytics();">Number Analytics</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="showStringAnalytics();">String Analytics</a></td>
<td width='10%' class='body-style'><input type="checkbox" name="edit" id="edit" value="Edit">Edit Mode</td>
<td width='20%' class='body-style'><button value='Show' onclick="showGrid();">Show Record</button></td>
</tr>
</table>
<hr />

<div id='center-grid' style="display:none;"></div>
<div id='number-analytics' style="display:none;">
<table width='65%' align='center'>
<tr>
<td width='10%' class='body-style'>Profile Time: <%=request.getAttribute("systemDate")%></td>
<td width='10%' class='body-style'></td>
<td width='10%' class='body-style'><button value='Comment' onclick="showGrid();">Comment</button></td>
<td width='10%' class='body-style'><button value='Show' onclick="showGrid();">Save Report</button></td>
<td width='10%' class='body-style'><button value='Statistics' onclick="numberAnalyticsTab('<%=request.getAttribute("parentValue")%>','<%=request.getAttribute("selectedValue")%>');">Statistics</button></td>
</tr>
</table>
</div>

<div id="bin-analysis" style="display:none;"></div>
<div id="cluster-analysis" style="display:none;"></div>
<div id="number-profiler" style="display:none;"></div>
<div id='number-profiler-grid' style="display:none;"></div>
<div id='string-analytics' style="display:none;"></div>
<div id="string-analytics-grid"></div>
<script type="text/javascript">
var form1 = null;
var form2 = null;
var form3 = null;
var nptable = '<%=request.getAttribute("parentValue")%>';
var npcolumn = '<%=request.getAttribute("selectedValue")%>';
var normalColumns = <%=request.getAttribute("gridColumns")%>;
var editColumns = <%=request.getAttribute("gridColumnsEdit")%>;
var anaeditgrid = null
//sample static data for the store
var myData = <%=request.getAttribute("gridData")%>;



// create the data store
var store = Ext.create('Ext.data.ArrayStore', {
    fields: <%=request.getAttribute("gridFields")%>,
    data: myData
});
//Ext.onReady(function() {
	
	//GRID
	
Ext.QuickTips.init();
    
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));    
	
   
   function getSelectedRecordCount(){	  
 	   Ext.Msg.alert('Record Count','Record Count = ' + anaeditgrid.getStore().getCount() + '<br />Column Count = ' + anaeditgrid.columns.length);
   }
   function getRecordCount(){	  
	   Ext.Msg.alert('Record Count','Record Count = ' + anaeditgrid.getStore().getCount() + '<br />Column Count = ' + anaeditgrid.columns.length);
   }
    function selectAllFromGrid(){
    	anaeditgrid.getSelectionModel().selectAll();
    }
    
    function deSelectAllFromGrid(){    	
    	anaeditgrid.getSelectionModel().clearSelections();
    }
    function copyAll(s){
    	var s = Ext.getCmp('anaeditgrid').store.serializeData(format);
		if (window.clipboardData)
			window.clipboardData.setData('text', s);
		else
			return (s);
    }
    
 // ExtJs Store method to allow exporting store data to desired formats.
    Ext.data.Store.prototype.serializeData = function(mode) {
        var store = this;
        var a = [];
        
        if(mode == 'text'){
        	
        }
        else if(mode == 'json'){
                Ext.each(store.data.items, function(item) {
                    a.push(item.data);
                });
                return (Ext.encode(a));
        }
        else{           
                var separator = '\t';
                if (mode == SerializationMode.Csv) {
                    separator = ',';
                }
                Ext.each(store.data.items, function(item) {
                    var s = '';
                    item = item.data;
                    for (key in item) {
                        s = s + item[key] + separator;
                    }
                    s = s.substr(0, s.length - 1);

                    a.push(s);
                });

                return (a.join('\n'));
        }
        
    }
 
 //Other field sets
   var binAnalysisStr ="<table width='65%' align='center'> <tr> <td width='10%' class='body-style'> <table> <tr><td class='body-style'>Lower Value</td><td class='body-style'>Bin Names</td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue1'></td><td class='body-style'><input type='text' value='Bin 1' id='binNames1'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue2'></td><td class='body-style'><input type='text' value='Bin 2' id='binNames2'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue3'></td><td class='body-style'><input type='text' value='Bin 3' id='binNames3'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue4'></td><td class='body-style'><input type='text' value='Bin 4' id='binNames4'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue5'></td><td class='body-style'><input type='text' value='Bin 5' id='binNames5'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue6'></td><td class='body-style'><input type='text' value='Bin 6' id='binNames6'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue7'></td><td class='body-style'><input type='text' value='Bin 7' id='binNames7'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue8'></td><td class='body-style'><input type='text' value='Bin 8' id='binNames8'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue9'></td><td class='body-style'><input type='text' value='Bin 9' id='binNames9'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue10'></td><td class='body-style'><input type='text' value='Bin 10' id='binNames10'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue11'></td><td class='body-style'><select id='bin_color'><option value='red'>Red</option><option value='green'>Green</option><option value='yellow'>Yellow</option><option value='blue'>Blue</option></select></td></tr></table> </td> <td width='10%' class='body-style'><div id='bar-chart'></div></td> </tr> </table>";
	   form1 = Ext.create('Ext.form.Panel', {			
			frame : true,
			title : 'Bin Analysis',
			width : '100%',
			hight : '200',
			bodyPadding : 5,
			waitMsgTarget : true,

			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 135,
				msgTarget : 'side'
			},
			renderTo: 'bin-analysis',
				
				items : [ {
					xtype : 'fieldset',								
					defaultType : 'textfield',
					defaults : {
						width : 380
					},
					html : binAnalysisStr
				} ],
				buttons : [							
							{
								text : 'Bar Chart',
								handler : function() {	
									submitBinAnalysis();
								}
							},{
								text : 'Zoom In',								
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
								text : 'Zoom Out',
								handler : function() {
									this.up('window').destroy();
								}
							},{
								text : 'Reset',
								handler : function() {
									this.up('window').destroy();
								}
							}  ]
				
			});
   
   
   var clusterAnalysisStr ="<table> <tr> <td class='body-style'><select id='cluster_color'><option>Red</option><option>Green</option><option>Yellow</option><option>Blue</option></select>Group data in:<input type='text' id='cluster_data_in' /><button value='Chart' onclick='showGrid();'>Chart</button><button value='Zoom In' onclick='showGrid();'>Zoom In</button><button value='Zoom Out' onclick='showGrid();'>Zoom Out</button><button value='Reset' onclick='showGrid();'>Reset</button></td> </tr> </table> <table width='65%' align='center'> <tr> <td width='10%' class='body-style'> <h4>Lower</h4><div id='slider1'></div></td><td width='10%' class='body-style'><h4>Upper</h4><div id='slider2'></div> </td> <td width='10%' class='body-style'><div id='bar-chart'></div></td> </tr> </table>";
	   form2 = Ext.create('Ext.form.Panel', {			
			frame : true,
			title : 'Cluster Analysis',
			width : '100%',
			height : '400',
			bodyPadding : 5,
			waitMsgTarget : true,

			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 135,
				msgTarget : 'side'
			},
			renderTo: 'cluster-analysis',
				
				items : [ {
					xtype : 'fieldset',								
					defaultType : 'textfield',
					defaults : {
						width : 380
					},
					html : clusterAnalysisStr
				} ],
				buttons : [	]
				
			});
	   
	   var numberProfilerStr ="<table width='65%' align='center'> <tr> <td width='10%' class='body-style'> <table> <tr> <td class='body-style'> <fieldset> <legend>On Complete Column</legend> <input type='checkbox' id='occsum' value='Sum'>Sum <input type='checkbox' id='occcount' value='Count'>Count <input type='checkbox' id='occmin' value='Min'>Min <input type='checkbox' id='occmax' value='Max'>Max <input type='checkbox' id='occavg' value='Avg'>Avg </fieldset> </td> </tr> <tr> <td class='body-style'> <fieldset> <legend>Value less than</legend><input type='text' id='valueLessThan'><br/> <input type='checkbox' id='vltsum' value='Sum'>Sum <input type='checkbox' id='vltcount' value='Count'>Count <input type='checkbox' id='vltmin' value='Min'>Min <input type='checkbox' id='vltmax' value='Max'>Max <input type='checkbox' id='vltavg' value='Avg'>Avg </fieldset> </td> </tr> <tr> <td class='body-style'> <fieldset> <legend>Value more than</legend> <input type='text' id='valueMoreThan'> <br/><input type='checkbox' id='vmtsum' value='Sum'>Sum <input type='checkbox' id='vmtcount' value='Count'>Count <input type='checkbox' id='vmtmin' value='Min'>Min <input type='checkbox' id='vmtmax' value='Max'>Max <input type='checkbox' id='vmtavg' value='Avg'>Avg </fieldset> </td> </tr> <tr> <td class='body-style'> <fieldset> <legend>Value in between</legend> <input type='text' id='valueInBetweenFrom1'> <input type='text' id='valueInBetweenTo1'><input type='checkbox' id='vib1sum' value='Sum'>Sum <input type='checkbox' id='vib1count' value='Count'>Count <input type='checkbox' id='vib1min' value='Min'>Min <input type='checkbox' id='vib1max' value='Max'>Max <input type='checkbox' id='vib1avg' value='Avg'>Avg </fieldset> </td> </tr> <tr> <td class='body-style'> <fieldset> <legend>Value in between</legend> <input type='text' id='valueInBetweenFrom2'><input type='text' id='valueInBetweenTo2'><input type='checkbox' id='vib2sum' value='Sum'>Sum <input type='checkbox' id='vib2count' value='Count'>Count <input type='checkbox' id='vib2min' value='Min'>Min <input type='checkbox' id='vib2max' value='Max'>Max <input type='checkbox' id='vib2avg' value='Avg'>Avg </fieldset> </td> </tr> </table> </td> <td width='10%' class='body-style'><button value='Number Profiler' onclick='submitNumberProfiler();'>Number Profiler</button></td> </tr> </table>  ";
	   form3 = Ext.create('Ext.form.Panel', {			
			frame : true,
			title : 'Number Profiler',
			width : '100%',
			bodyPadding : 5,
			waitMsgTarget : true,

			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 135,
				msgTarget : 'side'
			},
			renderTo: 'number-profiler',
				
				items : [ {
					xtype : 'fieldset',								
					defaultType : 'textfield',
					defaults : {
						width : 380
					},
					html : numberProfilerStr
				} ],
				buttons : [	]
				
			});
	   
	   var stringAnalStr ="<table> <tr> <td> <table> <tr> <td class='body-style'> <input type='checkbox' id= 'distinct' /> Disctinct</td> </tr> <tr> <td class='body-style'> <select id= 'strLike'> <option value='0'>String NOT LIKE</option><option value='1'>String LIKE</option><option value='2'>Regex</option></select></td> </tr> <tr> <td class='body-style'>Pattern_1 Count: <div id= 'pattern1'> </div></td> </tr> <tr> <td class='body-style'>Pattern_2 Count: <div id= 'pattern2'> </div></td> </tr> <tr> <td class='body-style'>Pattern_3 Count: <div id= 'pattern3'> </div></td> </tr> </table> </td> <td> <table> <tr> <td class='body-style'> Pattern_1: <input type= 'text' id= 'selpattern1' value='%'><input type= 'checkbox' id= 'checkpattern1'>Ignore case</td> </tr> <tr> <td class='body-style'> Pattern_2: <input type= 'text' id= 'selpattern2' value='%'><input type= 'checkbox' id= 'checkpattern2'>Ignore case</td> </tr> <tr> <td class='body-style'> Pattern_3: <input type= 'text' id= 'selpattern3' value='%'><input type= 'checkbox' id= 'checkpattern3'>Ignore case</td> </tr> </table> </td> <td><button onclick='submitStringProfiler()'>Search</button></td> </tr> </table>";
	   form4 = Ext.create('Ext.form.Panel', {			
			frame : true,
			title : '',
			width : '100%',
			hight : '200',
			bodyPadding : 5,
			waitMsgTarget : true,

			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 135,
				msgTarget : 'side'
			},
			renderTo: 'string-analytics',
				
				items : [ {
					xtype : 'fieldset',								
					defaultType : 'textfield',
					defaults : {
						width : 380
					},
					html : stringAnalStr
				} ],
				buttons : [							
							 ]
				
			});



//});

function slider(){
	Ext.create('Ext.slider.Single', {
        renderTo: 'slider1',
        hideLabel: true,
        useTips: false,
        height: 214,
        vertical: true,
        minValue: 0,
        maxValue: 100
    });
	Ext.create('Ext.slider.Single', {
        renderTo: 'slider2',
        hideLabel: true,
        useTips: false,
        height: 214,
        vertical: true,
        minValue: 0,
        maxValue: 100
    });
}
function updateGrid(){

	var parms = []; 
	var updatedRecords = anaeditgrid.getStore().getUpdatedRecords();

	Ext.each(updatedRecords,function(record){
 		parms.push(record.data);
	});
	
	var strData = Ext.encode(parms);

	Ext.Ajax.request({
		params : {
			selectedValue : '<%=request.getAttribute("selectedValue")%>',
			parent: '<%=request.getAttribute("parentValue")%>',
			action: 'update',
			data : strData
		},
		success : function(result) {
			var recordResult = Ext.JSON.decode(result.responseText);
				if(recordResult.success == true) {
					myData = recordResult.results;
					store.sync();
				}
		},
		url : './profilerInfoUpdate.json'
	}) 
}
function drawGrid(edit){
	   var colValue = normalColumns;
	   if(edit){
		   colValue = editColumns;
	   }
	   anaeditgrid = Ext.create('Ext.grid.Panel', {
	        store: store,
	        id: 'tableGrid',	
	        stateful: true,
	        stateId: 'stateGrid',
	        columns: colValue,
	        width: '100%',
	        title: '<%=request.getAttribute("parentValue")%>',
	        renderTo: 'center-grid',
	        selModel: {
	            mode: 'MULTI'
	        },
	        viewConfig: {
	            stripeRows: true
	        },
	        plugins: [
 	                  Ext.create('Ext.grid.plugin.CellEditing', {
 	                      clicksToEdit: 2
 	                       	                      
 	                  })
 	        ],
 	       
	    });
	   anaeditgrid.getView().refresh();
	   
}

function showGrid(){
	if(document.getElementById("edit").checked){
		Ext.MessageBox.confirm('Confirm', 'Edit mode will update underlying database. Do you want Edit mode?', showGridYes);
	}
	else{
		showGridYes();
	}
    
}
function showGridYes(){
	if(document.getElementById("edit").checked) {
		document.getElementById('center-grid').innerHTML = '<button value=\'Update\' onclick="updateGrid();">Update</button>';
	} else {
		document.getElementById('center-grid').innerHTML = '';
	}

	document.getElementById('string-analytics').style.display="none";
   	document.getElementById('number-analytics').style.display="none";    	 
    document.getElementById('bin-analysis').style.display="none"; 
    document.getElementById('cluster-analysis').style.display="none"; 
    document.getElementById('number-profiler').style.display="none"; 
    document.getElementById('number-profiler-grid').style.display="none"; 
    document.getElementById('center-grid').style.display="inline";
    drawGrid(document.getElementById("edit").checked);
    //Ext.getCmp('tableGrid').getView().refresh(); 
}
function showNumberAnalytics(){
	  
    if (document.getElementById('number-analytics').style.display=="none"){
        document.getElementById('number-analytics').style.display="inline";  
        document.getElementById('bin-analysis').style.display="inline"; 
        document.getElementById('cluster-analysis').style.display="inline"; 
        document.getElementById('number-profiler').style.display="inline"; 
        document.getElementById('number-profiler-grid').style.display="inline"; 
        document.getElementById('center-grid').style.display="none";
        document.getElementById('string-analytics').style.display="none";
        form1.doLayout();
        form2.doLayout();
        form3.doLayout();
        slider();
    } 
}
function showStringAnalytics(){
	  
    if (document.getElementById('string-analytics').style.display=="none"){
        document.getElementById('string-analytics').style.display="inline";   
        document.getElementById('center-grid').style.display="none";
        document.getElementById('number-analytics').style.display="none";
        document.getElementById('bin-analysis').style.display="none"; 
        document.getElementById('cluster-analysis').style.display="none"; 
        document.getElementById('number-profiler').style.display="none"; 
        document.getElementById('number-profiler-grid').style.display="none"; 
        form4.doLayout();
    } 
}

function numberAnalyticsTab(table, column) {	
	
	var numberanalyticstabs = Ext.createWidget('tabpanel',
			{				
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
							title : 'Frequency Analysis',
							itemId : 'frequencey',
							loader : {
								url : 'numberAnalytics.json?table=' + table + '&column='+ column + '&condition=' + conditionQuery + '&tab=1',
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
							title : 'Variance Analysis',
							itemId : 'variance',
							loader : {
								url : 'numberAnalytics.json?table=' + table + '&column='+ column + '&condition=' + conditionQuery + '&tab=2',
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
						},{
							title : 'Percentile Analysis',
							itemId : 'percentile',
							loader : {
								url : 'numberAnalytics.json?table=' + table + '&column='+ column + '&condition=' + conditionQuery + '&tab=3',
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
	var numberanalitics = Ext.widget('window', {
		title : 'Advance Number Analysis',
		closeAction : 'destroy',
		width : 700,
		height : 500,
		minHeight : 400,
		layout : 'fit',
		resizable : true,
		modal : true,
		items : numberanalyticstabs
	});

	numberanalitics.show();
	
}

function submitBinAnalysis(){
	
	var color = document.getElementById('bin_color').value;
	Ext.Ajax.request({
		   url: 'numberAnalytics.json',        
		   success: function(response) {	
			   document.getElementById('bar-chart').innerHTML = '';
			    var obj = Ext.decode(response.responseText);			  
				
			    var data = Ext.decode(obj.barChartData);
			  
				var store = Ext.create('Ext.data.JsonStore', {
					fields : [ 'name', 'data1' ],
					data : data
				});

				var binChart = Ext.create('Ext.Panel', {
					renderTo : 'bar-chart',
					width : 450,
					height : 250,
					hidden : false,
					title : 'Bin Chart',
					layout : 'fit',
					items : {
						id : 'binChartCol',
						xtype : 'chart',
						style : 'background:#fff',
						animate : true,
						shadow : true,
						store : store,
						axes : [ {
							type : 'Category',
							position : 'bottom',
							fields : [ 'name' ]
						} ],
						series : [ {
							type : 'column',
							axis : 'left',
							highlight : true,
							label : {
								display : 'insideEnd',
								'text-anchor' : 'middle',
								field : 'data1',
								renderer : Ext.util.Format.numberRenderer('0'),
								orientation : 'vertical',
								color : '#333'
							},
							xField : 'name',
							yField : 'data1',
							//color renderer
			                renderer: function(sprite, record, attr, index, store) {			                    
			                    return Ext.apply(attr, {
			                        fill: color
			                    });
			                }
						} ]
					}
				});
			},   
		   //failure: failFn,
			params: {
	        	table: nptable,
	        	column: npcolumn,
	        	condition: conditionQuery,
	        	text1: document.getElementById("lowerValue1").value,
	        	text2: document.getElementById("lowerValue2").value,
	        	text3: document.getElementById("lowerValue3").value,
	        	text4: document.getElementById("lowerValue4").value,
	        	text5: document.getElementById("lowerValue5").value,
	        	text6: document.getElementById("lowerValue6").value,
	        	text7: document.getElementById("lowerValue7").value,
	        	text8: document.getElementById("lowerValue8").value,
	        	text9: document.getElementById("lowerValue9").value,
	        	text10: document.getElementById("lowerValue10").value,
	        	text11: document.getElementById("lowerValue11").value,
	        	binNames1: document.getElementById("binNames1").value,
	        	binNames2: document.getElementById("binNames2").value,
	        	binNames3: document.getElementById("binNames3").value,
	        	binNames4: document.getElementById("binNames4").value,
	        	binNames5: document.getElementById("binNames5").value,
	        	binNames6: document.getElementById("binNames6").value,
	        	binNames7: document.getElementById("binNames7").value,
	        	binNames8: document.getElementById("binNames8").value,
	        	binNames9: document.getElementById("binNames9").value,
	        	binNames10: document.getElementById("binNames10").value,
	        	action: "binAnalysis"
	            
	        }
	});
}
function submitNumberProfiler(){
	document.getElementById('number-profiler-grid').innerHTML = '';
	var aggr = getCheckBoxValues('occcount','occavg','occmax','occmin','occsum');
	var less = getCheckBoxValues('vltcount','vltavg','vltmax','vltmin','vltsum');
	var more = getCheckBoxValues('vmtcount','vmtavg','vmtmax','vmtmin','vmtsum');
	var between1 = getCheckBoxValues('vib1count','vib1avg','vib1max','vib1min','vib1sum');
	var between2 = getCheckBoxValues('vib2count','vib2avg','vib2max','vib2min','vib2sum');
	

	var text2 = document.getElementById('valueLessThan').value;
	var text3 = document.getElementById('valueMoreThan').value;
	var text4 = document.getElementById('valueInBetweenFrom1').value;
	var text5 = document.getElementById('valueInBetweenTo1').value;
	var text6 = document.getElementById('valueInBetweenFrom2').value;
	var text7 = document.getElementById('valueInBetweenTo2').value;	
	
	var columnArr = ['value','aggr','less','more','between1','between1'];
	Ext.define('Col', {
        extend: 'Ext.data.Model',
        fields: columnArr
    });
	var url='';
    var store = new Ext.data.Store({
        model: 'Col',
        proxy: {
            type: 'ajax',
            url: 'numberAnalytics.json?action=numberProfiler&aggr=' + aggr + '&less=' + less + '&more=' + more + '&between1=' + between1 + '&between2=' + between2 + '&text2=' + text2 + '&text3=' + text3+ '&text4=' + text4+ '&text5=' + text5+ '&text6=' + text6+ '&text7=' + text7 + '&table=' + nptable + '&column='+ npcolumn,
            reader: {
                type: 'json',
                root: 'items',
                totalProperty: 'totalCount'
            }
        }
    });
    store.load();
	
    var gridColumns = [];
	
    // create the grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        columns: [
                  {text: "Values", flex: 1, dataIndex: 'value', sortable: true},
                  {text: "Aggregate", flex: 1, dataIndex: 'aggr', sortable: true},
                  {text: "<", flex: 1, dataIndex: 'less', sortable: true},
                  {text: ">", flex: 1, dataIndex: 'more', sortable: true},
                  {text: "<>", flex: 1, dataIndex: 'between1', sortable: true},
                  {text: "<>", flex: 1, dataIndex: 'between2', sortable: true}                                     
                
              ],
        renderTo:'number-profiler-grid',
        width: 540,
        height: 200
    });
}

function getCheckBoxValues(id1,id2,id3,id4,id5){
	var counter = 0;
	var val='';
	if(document.getElementById(id1).checked){
		counter++;
		val = val + 'Y';
	}
	else{
		val = val + 'N';
	}
	if(document.getElementById(id2).checked){
		counter++;
		val = val + 'Y';
	}
	else{
		val = val + 'N';
	}
	if(document.getElementById(id3).checked){
		counter++;
		val = val + 'Y';
	}
	else{
		val = val + 'N';
	}
	if(document.getElementById(id4).checked){
		counter++;
		val = val + 'Y';
	}
	else{
		val = val + 'N';
	}
	if(document.getElementById(id5).checked){
		counter++;
		val = val + 'Y';
	}
	else{
		val = val + 'N';
	}
	return counter+val;
}

function submitStringProfiler(){
	document.getElementById('string-analytics-grid').innerHTML = '';
	Ext.Ajax.request({
		   url: 'stringAnalytics.json',        
		   success: function(response) {				
			    var obj = Ext.decode(response.responseText);			  
				document.getElementById("pattern1").innerHTML = obj.q1;
				document.getElementById("pattern2").innerHTML = obj.q2;
				document.getElementById("pattern3").innerHTML = obj.q3;
			},   
		   //failure: failFn,
			params: {
	        	table: nptable,
	        	column: npcolumn,
	        	qp_1: document.getElementById("selpattern1").value,
	        	qp_2: document.getElementById("selpattern2").value,
	        	qp_3: document.getElementById("selpattern3").value,
	        	qc_1: document.getElementById("checkpattern1").checked,
	        	qc_2: document.getElementById("checkpattern2").checked,
	        	qc_3: document.getElementById("checkpattern3").checked,
	        	q_s: document.getElementById("strLike").value,
	        	_distinct: document.getElementById("distinct").checked,
	        	action: "noGrid"
	            
	        }
	});
	
	var columnArr = ['q1','q2','q3'];
	Ext.define('Col', {
        extend: 'Ext.data.Model',
        fields: columnArr
    });
	var url='';
    var store = new Ext.data.Store({
        model: 'Col',
        proxy: {
            type: 'ajax',
            url: 'stringAnalytics.json?' + 'qp_1=' + encodeURI(document.getElementById("selpattern1").value) + '&qp_2=' + encodeURI(document.getElementById("selpattern2").value) + '&qp_3=' + encodeURI(document.getElementById("selpattern3").value) + '&q_s=' + document.getElementById("strLike").value + '&_distinct=' + document.getElementById("distinct").checked + '&table=' + nptable + '&column='+ npcolumn + 'qc_1=' + document.getElementById("checkpattern1").checked + '&qc_2=' + document.getElementById("checkpattern2").value + '&qc_3=' + document.getElementById("checkpattern3").value,
            reader: {
                type: 'json',
                root: 'items',
                totalProperty: 'totalCount'
            }
        }
    });
    store.load();
	
    var gridColumns = [];
	
    // create the grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        columns: [
                  {text: "Pattern_1", flex: 1, dataIndex: 'q1', sortable: true},
                  {text: "Pattern_2", flex: 1, dataIndex: 'q2', sortable: true},
                  {text: "Pattern_3", flex: 1, dataIndex: 'q3', sortable: true}           
                
              ],
        renderTo:'string-analytics-grid',
        width: '100%',
        height: 500
    });
	
}
</script>