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
<td width='10%' class='body-style'><input type="checkbox" name="edit" value="Edit">Edit Mode</td>
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
</tr>
</table>
<table>
<tr>
<td>
<table>
<tr>
<td class='body-style'> <input type="checkbox" id="distinct" /> Disctinct</td>
</tr>
<tr>
<td class='body-style'> <select id="strLike"> <option>String NOT LIKE</option><option>String LIKE</option><option>Regex</option></select></td>
</tr>
<tr>
<td class='body-style'>Pattern_1 Count: <div id="pattern1"> </div></td>
</tr>
<tr>
<td class='body-style'>Pattern_2 Count: <div id="pattern2"> </div></td>
</tr>
<tr>
<td class='body-style'>Pattern_3 Count: <div id="pattern3"> </div></td>
</tr>
</table>
</td>
<td>
<table>
<tr>
<td class='body-style'> Pattern_1: <input type="text" id="selpattern1"><input type="checkbox" id="checkpattern1">Ignore case</td>
</tr>
<tr>
<td class='body-style'> Pattern_2: <input type="text" id="selpattern2"><input type="checkbox" id="checkpattern2">Ignore case</td>
</tr>
<tr>
<td class='body-style'> Pattern_3: <input type="text" id="selpattern3"><input type="checkbox" id="checkpattern3">Ignore case</td>
</tr>
</table>
</td>
<td><button>Search</button></td>
</tr>
</table>
</div>
<div id='string-anal-grid' style="display:none;"></div>
<script type="text/javascript">
var form1 = null;
var form2 = null;
var form3 = null;
var nptable = '<%=request.getAttribute("parentValue")%>';
var npcolumn = '<%=request.getAttribute("selectedValue")%>';
//Ext.onReady(function() {
	
	//GRID
	
Ext.QuickTips.init();
    
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

    // sample static data for the store
    var myData = <%=request.getAttribute("gridData")%>;

   

    // create the data store
    var store = Ext.create('Ext.data.ArrayStore', {
        fields: <%=request.getAttribute("gridFields")%>,
        data: myData
    });

    // create the Grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        id: 'tableGrid',	
        stateful: true,
        stateId: 'stateGrid',
        columns: <%=request.getAttribute("gridColumns")%>,
        height: '100%',
        width: '100%',
        title: '<%=request.getAttribute("parentValue")%>',
        renderTo: 'center-grid',
        selModel: {
            mode: 'MULTI'
        },
        viewConfig: {
            stripeRows: true
        }
    });
    
   function getSelectedRecordCount(){	  
 	   Ext.Msg.alert('Record Count','Record Count = ' + grid.getStore().getCount() + '<br />Column Count = ' + grid.columns.length);
   }
   function getRecordCount(){	  
	   Ext.Msg.alert('Record Count','Record Count = ' + grid.getStore().getCount() + '<br />Column Count = ' + grid.columns.length);
   }
    function selectAllFromGrid(){
    	 grid.getSelectionModel().selectAll();
    }
    
    function deSelectAllFromGrid(){    	
    	 grid.getSelectionModel().clearSelections();
    }
    function copyAll(s){
    	var s = Ext.getCmp('grid').store.serializeData(format);
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
   var binAnalysisStr ="<table width='65%' align='center'> <tr> <td width='10%' class='body-style'> <table> <tr><td class='body-style'>Lower Value</td><td class='body-style'>Bin Names</td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue1'></td><td class='body-style'><input type='text' value='Bin 1' id='binNames1'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue2'></td><td class='body-style'><input type='text' value='Bin 2' id='binNames2'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue3'></td><td class='body-style'><input type='text' value='Bin 3' id='binNames3'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue4'></td><td class='body-style'><input type='text' value='Bin 4' id='binNames4'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue5'></td><td class='body-style'><input type='text' value='Bin 5' id='binNames5'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue6'></td><td class='body-style'><input type='text' value='Bin 6' id='binNames6'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue7'></td><td class='body-style'><input type='text' value='Bin 7' id='binNames7'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue8'></td><td class='body-style'><input type='text' value='Bin 8' id='binNames8'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue9'></td><td class='body-style'><input type='text' value='Bin 9' id='binNames9'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue10'></td><td class='body-style'><input type='text' value='Bin 10' id='binNames10'></td></tr> <tr><td class='body-style'><input type='text' value='0' id='lowerValue11'></td><td class='body-style'><select id='bin_color'><option>Red</option><option>Green</option><option>Yellow</option><option>Blue</option></select></td></tr></table> </td> <td width='10%' class='body-style'><div id='bar-chart'></div></td> </tr> </table>";
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
									validateData(table, true);
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
function showGrid(){
  
    if (document.getElementById('center-grid').style.display=="none"){
    	 document.getElementById('string-analytics').style.display="none";
    	 document.getElementById('number-analytics').style.display="none";    	 
         document.getElementById('bin-analysis').style.display="none"; 
         document.getElementById('cluster-analysis').style.display="none"; 
         document.getElementById('number-profiler').style.display="none"; 
         document.getElementById('number-profiler-grid').style.display="none"; 
         document.getElementById('center-grid').style.display="inline";
         Ext.getCmp('tableGrid').getView().refresh();
    } 
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
        document.getElementById('center-grid').style.display=="none";
        document.getElementById('number-analytics').style.display="none";
        document.getElementById('bin-analysis').style.display="none"; 
        document.getElementById('cluster-analysis').style.display="none"; 
        document.getElementById('number-profiler').style.display="none"; 
        document.getElementById('number-profiler-grid').style.display="none"; 
    } 
}





</script>