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

connectionDataStores = {
    serviceStore : new Ext.data.ArrayStore( {
        data : [ [ '1', _serviceType['FTP'] ], [ '4', _serviceType['SAMBAWIN'] ], [ '5', _serviceType['HTTP'] ] ],
        fields : [ 'service', 'value' ]
    }) ,
    typeStore : new Ext.data.ArrayStore( {
        data : [ [ 1,'DOWNLOAD' ], [ 2, 'UPLOAD']  ],
        fields : [ 'idConnType', 'value' ]
    })
}
connectionsRenderers = {
		iconTpl : new Ext.XTemplate('<div class="{iconCls}" style="min-height:18px;vertical-align: middle;"',
				' id="{divId}" />').compile()
		,
		iconConnStatusRenderer : function(data, cell, record, rowIndex, colIndex, store) {
			var divId = 'connectionsStatusDivId_' + rowIndex + colIndex;
			var str = connectionsRenderers.iconTpl.applyTemplate({ iconCls : 'ajax-loader',
				divId : divId});
			Ext.Ajax.request( {
				params : {
					ftpIp : record.get('host'),
					ftpPort : record.get('port'),
					userName : record.get('userName'),
					password : record.get('password'),
					service : record.get('service'),
                    idConnType: record.get('idConnType'),
					id : record.get('id')
				},
				url : './isSuccessfulConnection.json',
				success: function (result) {
					recordResult = Ext.JSON.decode(result.responseText);
					var check = function(){
						var div = document.getElementById(divId);
						if (div) {
							//if service is unknown (not ftp or other) then set null class
							if (record.get('service') == 0) {
								div.className = null;
							} else {
								div.className = eval(recordResult.success) ? 'checkIcon' : 'alertIcon';		
								eval(recordResult.success) ? record.data.status = '1' : record.data.status = '0';								
							}
						} else {
							check.defer(100);
						}
					};
					check.call(this);
				},
				failure : function() {
					return 'failed';
				}
		 	});	
			return str;
		},
		serviceRenderer : function(value) {
			if (value == '0') {
				return '';
			} else if (value == '1') {
				return _serviceType['FTP'];
			} else if (value == '4') {
				return _serviceType['SAMBAWIN'];
			} else if (value == '5') {
				return _serviceType['HTTP'];
			}
			return value;
		}
}

var editorConnections = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
	clicksToEdit : 2,
	validationScheme : 'validationConnections',
	validateEditSuccess : function() {
		connectionsGrid.store.sync();
	}
});

var connectionsGrid = new Ext.grid.Panel({
			columnLines : true,
			columns : [{dataIndex : 'id', header : 'Id', sortable : true, width : 50},
					{dataIndex : 'name', header : _label['name'], sortable : true, width : 150, editor: {xtype:'textfield'}},
					{dataIndex : 'host', header : _label['host'], sortable : true, width : 150, editor: {xtype:'textfield'}},
					{dataIndex : 'port', header : _label['port'], sortable : true, width : 50, editor: {xtype:'textfield'}},
					{dataIndex : 'userName', header : _label['userName'], sortable : true, width : 150, editor: {xtype:'textfield'}},
					{dataIndex : 'directory', header : _label['directory'], sortable : true, width : 150, editor: {xtype:'textfield'}},
					{dataIndex : 'fileName', header : _label['fileName'], sortable : true, width : 150, editor: {xtype:'textfield'}},
					{dataIndex : 'service', header : _label['service'], sortable : true, width : 150, renderer : connectionsRenderers.serviceRenderer,
						editor: {xtype:'combobox', forceSelection : true, queryMode: 'local', displayField: 'value', store: connectionDataStores.serviceStore, valueField: 'service'}},
					{dataIndex : 'status', header : _label['status'], sortable : true, width : 50, renderer : connectionsRenderers.iconConnStatusRenderer},
					{dataIndex : 'idConnType', header : '', editor: {xtype:'textfield'}, hidden : true},
					{
						dataIndex : 'downloadFile',
						xtype:'templatecolumn',
						tpl: '<img src="images/test_download.png" style="cursor:pointer;">',
						header : _label['downloadConnFile'], sortable : false, width : 50, align:'center',
						listeners: { 
			                click: function(grid, td) {
			                   
			                	var record = grid.getSelectionModel().getSelection()[0];
			                    if(record.data.status > 0) {		                    	
			    			       
			    			        Ext.Ajax.request( { 
			    			            		params : {
			    			            			connId : record.get('id')
			    			            		},
			    			            		success: function (result) {			   
			    			            				recordResult = Ext.JSON.decode(result.responseText);
			    			            				if(eval(recordResult.success)) {
			    			            					var iFrame = document.createElement("iframe"); 
				    			            				iFrame.src = './connectionsFileDownload.json?action=downloadXSD&connId=' + record.get('id'); 
				    			            				iFrame.style.display = "none"; 
				    			            				document.body.appendChild(iFrame);	
			    			            				} else {
			    			            					App.setAlert(true, _error['fileDownloadError']);
			    			            				}
			    			            					            		
			    			            		},
			    			            		url : './connectionsFileDownload.json?action=checkValidity'
			    			      }); 
			    						
			                    } else {
			                    	App.setAlert(false , _error['serviceDisabledError']);
			                    }
			                }						
						}
					}
			],
			frame : false,
			id : 'connectionsGridId',
			plugins: [editorConnections],
			store : new Ext.data.JsonStore( {
				autoLoad : false,
				fields : [{name: 'id', type: 'int', defaultValue: 0},
                    {name: 'name', type: 'string'},
					{name: 'host', type: 'string'},
                    {name: 'port', type: 'string'},
					{name: 'userName', type: 'string'},
                    {name: 'directory', type: 'string'},
					{name: 'password', type: 'string'},
					{name: 'fileName', type: 'string'},
                    {name: 'service', type: 'string', defaultValue: '0'},
                    {name: 'idConnType', type: 'int', defaultValue: 1},
					{name: 'status', type: 'string', defaultValue: '0'}
				],
				proxy : {
					type : 'ajax',
			        api : {
						read : './connectionsRead.json',
						create : './connectionsCreate.json',
						update : './connectionsUpdate.json?_method=put',
						destroy : './connectionsDestroy.json?_method=delete'
					},
					reader : {
						type : 'json',
						root : 'results'
					},
					extraParams:{		
						connId: ""            
			        },
					listeners : {
						exception : function (proxy, response, operation) {
							if (response) {
								var responseObj = Ext.decode(response.responseText);
								if (responseObj.message.indexOf('Error') != -1) {
									Ext.Msg.alert("", _error['connectionError']);
								} else {
									App.setAlert(false, responseObj.message);
									if (operation.action == 'create') {
										connectionsGrid.store.load();
									} else {
										connectionsGrid.store.remove();
									}
								}
							}
						}
					}
				},
				listeners : {
					write : function(store, operation) {
						if (operation.response.responseText) {
							var responseObj = Ext.decode(operation.response.responseText);
							App.setAlert(true, responseObj.message);
							if (responseObj.extraMessage) App.setAlert(true, responseObj.extraMessage);							
							if (operation.action == 'update' || operation.action == 'create') {
								store.load();
							}
						}
					}
				}  
			}),
			title : _label['connections']
		}
);

function addConnection() {
	var record = new connectionsGrid.store.model();
	editorConnections.cancelEdit();
	popupConnection(record, true);
}

function editConnection() {
    var record = connectionsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
    editorConnections.cancelEdit();
    popupConnection(record, false);
};

function deleteConnection() {
    var record = connectionsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.MessageBox.confirm('Delete Connections?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	connectionsGrid.store.proxy.extraParams.connId = record.data.id;
        	connectionsGrid.store.remove(record);
        	connectionsGrid.store.sync();
        }
    });
};

function popupConnection(record, add) {
    new Ext.Window({
		height: 255,
		id : 'connectionsPopupWindow',
		bodyStyle:{"background-color":"#ffffff"},
		items : [ {
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
				{value: _label['name'] , x: 10 , xtype: 'displayfield', y: 10},
				{xtype: 'textfield', id: 'name', name: 'name', value: record.get('name'), x: 10, width: 180 , y: 30}, 
				{value: _label['host'] , x: 200 , xtype: 'displayfield', y: 10},
				{xtype: 'textfield', id: 'host', name: 'host', value: record.get('host'), x: 200, width: 200 , y: 30}, 
				{value: _label['port'] , x: 410 , xtype: 'displayfield', y: 10},
				{xtype: 'textfield', id: 'port', name: 'port', value: record.get('port'), x: 410, width: 50 , y: 30}, 
				{value: _label['userName'] , x: 470 , xtype: 'displayfield' , y: 10},
				{xtype: 'textfield', id: 'userName', name: 'userName', value: record.get('userName'), x: 470, width: 170 , y: 30}, 
				
				{value: _label['password'] , x: 10 , xtype: 'displayfield', y: 70},
				{xtype: 'textfield', id: 'connectionPasswordPopupFieldId', submitValue : false, value: record.get('password'), x: 10, width: 100 , y: 90, inputType : 'password'}, 
				{value: _label['directory'] , x: 120 , xtype: 'displayfield', y: 70},
				{xtype: 'textfield', id: 'directory', name: 'directory', value: record.get('directory'), x: 120, width: 220 , y: 90}, 
				{value: _label['fileName'] , x: 350 , xtype: 'displayfield', y: 70},
				{xtype: 'textfield', id: 'fileName', name: 'fileName', value: record.get('fileName'), x: 350, width: 170 , y: 90}, 
				{value: _label['service'] , x: 530 , xtype: 'displayfield' , y: 70},
				{xtype: 'combo', id: 'service', name: 'service', value: record.get('service'), x: 530, width: 110 , y: 90, forceSelection : true,
					displayField : 'value', queryMode: 'local', valueField : 'service', store : connectionDataStores.serviceStore, editable : false,
					listeners : {
						afterrender : function(me) {
							//select by default the first row
							me.setValue(me.store.getAt('0').get('service'));
						}		
					}
				},
	            {value: _label['connType'] , x: 10 , xtype: 'displayfield' , y: 130},
	            {xtype: 'combo', id: 'idConnType', name: 'idConnType', value: record.get('idConnType'), x: 10, width: 120 , y: 150, forceSelection : true,
	                displayField : 'value', queryMode: 'local', valueField : 'idConnType', store : connectionDataStores.typeStore},
	            {
					text : _message['save'],
					x : 235,
					xtype : 'button',
					width : 100,
					y : 180,
					handler : function() {
						var form = this.up('form').getForm();
				        form.submit( {
				            waitMsg: _message["waitMessage"],
				            params : {schemaType : 'validationConnections'},
							success : function(form, action) {
								record.set('name', Ext.getCmp('name').getValue());
								record.set('host', Ext.getCmp('host').getValue());
								record.set('port', Ext.getCmp('port').getValue());
								record.set('userName', Ext.getCmp('userName').getValue());
								record.set('password', Ext.getCmp('connectionPasswordPopupFieldId').getValue());
								record.set('directory', Ext.getCmp('directory').getValue());
								record.set('fileName', Ext.getCmp('fileName').getValue());
								var val = Ext.getCmp('service').getValue(); 
								if (val) record.set('service', val);
			
			                    val = Ext.getCmp('idConnType').getValue();
			                    if (val) record.set('idConnType', val);
			
			                    if (add) {
									connectionsGrid.store.insert(0, record);
								}
								connectionsGrid.store.sync();
								Ext.getCmp('connectionsPopupWindow').close();
							},
							failure : function(form, action) {
								//
							}
						});
					}
		        },
		        {
					text : _message['cancel'],
					x : 340,
					xtype : 'button',
					width : 100,
					y : 180,
					handler : function() {
						Ext.getCmp('connectionsPopupWindow').close();
					}
		        }
	        ]
		}],
		layout: 'absolute',
		modal: true,
		resizable: false,
	    title: _label['connections'],
	    width: 665
    }).show();
}


function connectionsHelp() {
	if (Ext.getCmp('popupConnectionsHelp')) return;
	new Ext.Window( {
		height : 250,
		id : 'popupConnectionsHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['connectionsHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupConnectionsHelp').close();
			}
		} ],
		width : 665
	}).show(this);
}