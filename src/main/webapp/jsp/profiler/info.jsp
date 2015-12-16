<%@ page language="java" session="true" contentType="text/html; charset=iso-8859-1" %>
<%--
  ~ Copyright (c) 2015  www.see-r.com
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
<td width='33%' align='center' class='body-style'><%=request.getAttribute("selectedValue")%></td>
<td width='33%' align='center' class='body-style'><%=request.getAttribute("rowCount")%></td>
</tr>
</table>
<hr />
<table width='65%' align='center'>
<tr>
<td width='5%' class='body-style'><a href='javascript:void(0)' onclick="addCondition('<%=request.getAttribute("selectedValue")%>', true)">Add Condition</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="showCondition();">Show Condition</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="loadDBInfo('Table Metadata Information','tablemetadatainfo','<%=request.getAttribute("selectedValue")%>');">MetaData</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="loadDBInfo('Data Information','tabledatainfo', '<%=request.getAttribute("selectedValue")%>');">Summary Data</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="loadDBInfo('Table Infomation','tableprivilegeinfo', '<%=request.getAttribute("selectedValue")%>');">Table Privilege</a></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="loadDBInfo('Index Information','tableindexinfo', '<%=request.getAttribute("selectedValue")%>');">Indexes</a></td>
</tr>
</table>
<hr />
<table width='60%' align='center'>
<tr>
<td width='7%'><div id="container"><div id='menubar'></div></div></td>
<td width='10%' class='body-style'><!-- <a href=''>Horizontal Scroll</a> --></td>
<td width='10%'><button value='Print' onclick='printGrid();'>Print</button></td>
<td width='10%' class='body-style'><!-- <a href=''>No Grid</a> --></td>
<td width='10%' class='body-style'><a href='javascript:void(0)' onclick="submitGrid('excel');">Excel</a>|<a href='javascript:void(0)' onclick="submitGrid('csv');">Csv</a>|<a href='javascript:void(0)' onclick="submitGrid('xml');">Xml</a></td>
</tr>
</table>
<div id='center-grid'></div>
<style type='text/css'>
.x-livesearch-match {
    font-weight: bold;
    background-color: yellow;
}

.x-toolbar .x-form-cb-wrap {
    line-height: 22px;
}

.x-toolbar .x-form-checkbox {
    vertical-align: 0;
}
</style>
<script type="text/javascript">
var mainTablegrid = null;
var mainTableData = null;
//Ext.onReady(function() {
	Ext.QuickTips.init();	

	var toolsMenu = Ext.create('Ext.menu.Menu', {		
		id : 'toolsMenu',
		style : {
			overflow : 'visible'
		},
		items : [
		         
		{
			text : 'Select All',
			handler: selectAllFromGrid,
			menu : '' // <-- submenu by reference
		},{
			text : 'Deselect All',
			handler: deSelectAllFromGrid,
			menu : '' // <-- submenu by reference
		}
		, '-',{
			text : 'Record Count',
			handler: getRecordCount,
			menu : '' // <-- submenu by reference
		},{
			text : 'Selected Count',
			handler: getSelectedRecordCount,
			menu : '' // <-- submenu by reference
		}
		, '-',{
			text : 'Analyse Selected',
			handler: analyticsGrids,
			menu : '' // <-- submenu by reference
		} ]
	});

	
	document.getElementById('menubar').innerHTML = '';
	document.getElementById('center-grid').innerHTML = '';
	var totalColumns = <%=request.getAttribute("colCount")%>;
	document.getElementById('center-grid').style.width = (totalColumns * 80) + 'px';
	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.suspendLayout = true;
	tb.render('menubar');
	toolsMenu.add(' ');	
	tb.minWidth = 70;

	tb.add({
		//icon : 'preview.png',
		cls : 'x-btn-text-icon',
		text : 'Menu',
		menu : toolsMenu
	});
	
	

	tb.suspendLayout = false;
	tb.doLayout();

	function onItemClick(item) {
		// TODO
	}
	
	//GRID
	
Ext.QuickTips.init();
    
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

    // sample static data for the store
    mainTableData = <%=request.getAttribute("gridData")%>;

   

    // create the data store
    var store = Ext.create('Ext.data.ArrayStore', {
        fields: <%=request.getAttribute("gridFields")%>,
        data: mainTableData
    });

    // create the Grid    
    mainTablegrid = Ext.create('Ext.grid.Panel', {
        store: store,
        stateful: true,
        stateId: 'stateGrid',
        layout:'fit',
        columns: <%=request.getAttribute("gridColumns")%>,
        monitorResize:true,
        title: '<%=request.getAttribute("selectedValue")%>',    	
        renderTo: 'center-grid',
        selModel: Ext.create('Ext.selection.RowModel', { 
    		mode:'SIMPLE'
    	}),
        viewConfig: {
            stripeRows: true
        }
    });
   
    function numberAnalyticsInfoTab(table, column) {	
    	
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
   function analyticsGrids(){
	   var tmpColumn = ':';	 
	   var colArr = columns['<%=request.getAttribute("selectedValue")%>'];
	   for(var i=0;i< colArr.length;i++){
		   if(colArr[i][0] == mainTablegrid.columns[mainTablegrid.getSelectionModel().getCurrentPosition().column].text){
			   tmpColumn = colArr[i][0] + ":" + colArr[i][1]
		   }
	   }
	   numberAnalyticsInfoTab('<%=request.getAttribute("selectedValue")%>', tmpColumn);
   }
   function getSelectedRecordCount(){	  
 	   Ext.Msg.alert('Record Count','Record Count = ' + mainTablegrid.getSelectionModel().getCount() + '<br />Column Count = ' + mainTablegrid.columns.length);
   }
   function getRecordCount(){	  
	   Ext.Msg.alert('Record Count','Record Count = ' + mainTablegrid.getStore().getCount() + '<br />Column Count = ' + mainTablegrid.columns.length);
   }
    function selectAllFromGrid(){
    	mainTablegrid.getSelectionModel().selectAll();
    }
    
    function deSelectAllFromGrid(){    	
    	mainTablegrid.getSelectionModel().deselectAll();
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

//});

function printGrid(){	
	Ext.ux.grid.Printer.printAutomatically = false;
	Ext.ux.grid.Printer.print(mainTablegrid);
}
function submitGrid(action){
	var columns = '';
	var data = '';
	var cols = mainTablegrid.columns;
	var storedata = mainTableData;
	for(var i=0;i<storedata.length;i++){
		var arr = storedata[i];
		var tmp = '';
		for(var j=0;j<arr.length;j++){
			if(tmp ==''){
				tmp = storedata[i][j];
			}
			else{
				tmp = tmp + "&&&&&" + storedata[i][j];
			}
			
		}
		if(data == ''){
			data = tmp;
		}
		else{
			data = data + "@@@@@" + tmp;
		}
	}
	for(var i=0;i< cols.length;i++){
		if(columns == ''){
			columns = cols[i].text;
		}
		else{
			columns = columns + "&&&&&" + cols[i].text;
		}
	}
	
	document.getElementById("exportcolumns").value=columns;
	document.getElementById("exportdata").value=data;
	document.getElementById("exportaction").value=action;
    document.forms["exportForm"].submit();
}
</script>
<form id='exportForm' action="exporter.json" method="POST" accept-charset=utf-8>
<input type="hidden" id="exportcolumns" name="exportcolumns" />
<input type="hidden" id="exportdata" name="exportdata"/>
<input type="hidden" id="exportaction" name="exportaction"/>
</form>