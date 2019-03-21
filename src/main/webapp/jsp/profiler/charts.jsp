<%@ page language="java" session="true"
	contentType="text/html; charset=iso-8859-1"%>
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

<style type="text/css">
.body-style {
	padding-top: 0px;
	text-align: center;
	font-style: bold;
	color: gray;
	font-size: 11px;
}
</style>
<table width='100%'>
	<tr width='100%'>
		<td width='33%' align='center' class='body-style'><%=request.getAttribute("parentValue")%></td>
		<td width='33%' align='center' class='body-style'><%=request.getAttribute("selectedValue")%></td>
		<td width='33%' align='center' class='body-style'><!-- <a href=''>Save Image</a> --></td>
	</tr>
</table>
<hr />

<table>
	<tr>
		<td><div id='left-charts-div'></div></td>
		<td class='body-style'><a href='javascript:void(0)' onclick='minMaxValueGrid();'>Min Value</a> : <%=request.getAttribute("minValue")%></div> <br /> <a href='javascript:void(0)' onclick='minMaxValueGrid();'>Max Value</a> : <%=request.getAttribute("maxValue")%> </br><div id='right-charts-div'></div></td>
	</tr>
</table>

<script type="text/javascript">
	//Ext.onReady(function() {

		var data = <%=request.getAttribute("barChartData")%>;
		var store = Ext.create('Ext.data.JsonStore', {
			fields : [ 'name', 'data1' ],
			data : data
		});

		var win = Ext.create('Ext.Panel', {
			renderTo : 'left-charts-div',
			width : 400,
			height : 300,
			hidden : false,
			title : 'Record Count',
			layout : 'fit',
			items : {
				id : 'chartCol',
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
					yField : 'data1'
				} ]
			}
		});

		var pieChartData = <%=request.getAttribute("pieChartData")%>;
		var pieChartStore = Ext.create('Ext.data.JsonStore', {
			fields : [ 'name', 'data1' ],
			data : pieChartData
		});
		var pie = Ext.create('widget.panel', {
			width : 400,
			height : 300,
			title : 'Distribution Chart',
			renderTo : 'left-charts-div',
			layout : 'fit',
			items : {
				xtype : 'chart',
				id : 'chartPie',
				animate : true,
				store : pieChartStore,
				shadow : true,
				// legend: {
				//     position: 'right'
				//},
				insetPadding : 10,
				theme : 'Base:gradients',
				series : [ {
					type : 'pie',
					field : 'data1',
					showInLegend : true,
					tips : {
						trackMouse : true,
						width : 70,
						height : 28,
						renderer : function(storeItem, item) {
							//calculate percentage.
							var total = 0;
							pieChartStore.each(function(rec) {
								total += rec.get('data1');
							});
							this.setTitle(storeItem.get('data1'));
						}
					},
					highlight : {
						segment : {
							margin : 20
						}
					},
					label : {
						field : 'name',
						display : 'rotate',
						contrast : true,
						font : '12px Arial'
					}
				} ]
			}
		});
		
		var patternData = <%=request.getAttribute("patternChartData")%>;
	    
	    window.store1 = Ext.create('Ext.data.JsonStore', {
	        fields: ['name', 'data1'],
	        data: patternData
	    });
	    var pattern = Ext.create('widget.panel', {
	        width: 400,
	        height: 600,
	        hidden: false,
	        maximizable: true,
	        title: 'Pattern Information',
	        renderTo : 'right-charts-div',
	        layout: 'fit',	        
	        items: {
	            xtype: 'chart',
	            animate: true,
	            style: 'background:#fff',
	            shadow: false,
	            store: store1,
	            axes: [{
	                type: 'Numeric',
	                position: 'bottom',
	                fields: ['data1'],
	                label: {
	                   renderer: Ext.util.Format.numberRenderer('0,0')
	                },	               
	                minimum: 0
	            }, {
	                type: 'Category',
	                position: 'left',
	                fields: ['name']
	              
	            }],
	            series: [{
	                type: 'bar',
	                axis: 'bottom',
	                label: {
	                    display: 'insideEnd',
	                    field: 'data1',
	                    renderer: Ext.util.Format.numberRenderer('0'),
	                    orientation: 'horizontal',
	                    color: '#333',
	                    'text-anchor': 'middle',
	                    contrast: true
	                },
	                xField: 'name',
	                yField: ['data1'],
	                //color renderer
	                renderer: function(sprite, record, attr, index, store) {
	                    var fieldValue = Math.random() * 20 + 10;
	                    var value = (record.get('data1') >> 0) % 5;
	                    var color = ['rgb(213, 70, 121)', 
	                                 'rgb(44, 153, 201)', 
	                                 'rgb(146, 6, 157)', 
	                                 'rgb(49, 149, 0)', 
	                                 'rgb(249, 153, 0)'][value];
	                    return Ext.apply(attr, {
	                        fill: color
	                    });
	                }
	            }]
	        }
	    });
	//});
	
	function minMaxValueGrid(){
		Ext.define('TopBottom', {
	        extend: 'Ext.data.Model',
	        fields: ['top', 'bottom']
	    });
		var store = Ext.create('Ext.data.Store', {
	        model: 'TopBottom',
	        autoLoad: true,
	        proxy: {
	            // load using HTTP
	            type: 'ajax',
	            url: 'profilerInfo.json?action=minMaxValues&selectedValue=<%=request.getAttribute("selectedValue")%>&parent=<%=request.getAttribute("parentValue")%>',
	            // the return will be JSON, so lets set up a reader
	            reader: {
	                type: 'json',
	                // records will have an "Item" tag                
	                root: 'items',
	                totalRecords: 'totalCount'
	            }
	        }
	    });
		//var minmaxgrid = Ext.create('Ext.ux.LiveSearchGridPanel', {
		var minmaxgrid = Ext.create('Ext.grid.Panel', {	
            store: store,
            columns: [
                {text: "Top", flex: 1, dataIndex: 'top', sortable: true},
                {text: "Bottom", flex: 1, dataIndex: 'bottom', sortable: true}
              
            ],
            //renderTo:'example-grid',
            
            width: 640,
            height: 500
        });
		var minmaxgridWindow = new Ext.Window({
	    	height: 540,
	        width: 655,
	        title: '<%=request.getAttribute("parentValue")%> : <%=request.getAttribute("selectedValue")%>',
	        items: [
				minmaxgrid
	        ]
	    });
	    
		minmaxgridWindow.show();
	}
</script>