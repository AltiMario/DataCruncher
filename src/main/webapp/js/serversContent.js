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

function activateServerFunc(serverId, bIsActive) {
	Ext.Ajax.request( {
		params : {
			serverId : serverId,
			isActive : (eval(bIsActive) ? 1 : 0)
		},
		url : './serverSetIsActive.json'
	});
}

function popupAdminHelp() {
	if (Ext.getCmp('popupAdminHelp')) return;
	new Ext.Window( {
		height : 250,
		id : 'popupAdminHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['adminHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupAdminHelp').close();
			}
		} ],
		width : 665
	}).show(this);
}

var adminInfoGrid = new Ext.grid.Panel({
	columnLines : true,
	columns : [{
				dataIndex : 'name',
				header : '',
				sortable : true,
				width : 150
			},{
				dataIndex : 'value',
				header : '',
				sortable : true,
				width : 150
			} ],
	frame : false,
	id : 'adminInfoGridId',
	store : new Ext.data.JsonStore( {
		autoLoad : false,
		fields : ['name', 'value' ],
		proxy : {
			type : 'ajax',
	        api : {
				read    : './adminInfoRead.json'
			},
			reader : {
				type : 'json',
				root : 'results'
			}
		}
	}),
	title : _label['info'],
	viewConfig:{
	    markDirty:false
	}
}
);

var task = {
	    run: function(){
    		var date = new Date();
	    		
    		var hour = date.getHours();
    		var min = date.getMinutes();
    		var sec = date.getSeconds();
    		var day = date.getDate();
    		var month = date.getMonth() + 1;
    		var year = date.getFullYear();
    		
    		var strTime = (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month) + "/" + year + "  " + (hour < 10 ? "0" + hour : hour)  + ":" + (min < 10 ? "0" + min : min)  + ":" + (sec < 10 ? "0" + sec : sec);
    		adminInfoGrid.store.getAt(1).set('value', strTime);
	    },
	    interval: 1000
}

Ext.TaskManager.start(task);
	
logRenderers = {
		statusRenderer : function(value) {
			if (value == '0') {
				return _status['info'];
			} else if (value == '1') {
				return _status['error'];
			} else if (value == '2') {
				return _status['warning'];
			}
			return value;
		}
}

function getPageSize() {
	
	if(screen.height <= 768)
		return 22;
	else
		return 37;
}
var storeAdminLog = new Ext.data.JsonStore( {
	autoLoad : false,
	pageSize: getPageSize(),
	beforeCount: 0, 
	fields : ['idlog', 'idStatus', 'logDateTime', 'message' ],
	proxy : {
		type : 'ajax',
        api : {
			read    : './adminLogRead.json',
			destroy : './applicationsDestroy.json?_method=delete'
		},
		reader : {
			type : 'json',
			root : 'results',
			totalProperty: 'total'
		},
		writer: {
			type: 'json',
			writeAllFields:true
		},
		filterParam: 'searchstr'
	},
	selections : null,
	remoteFilter: true,
	buffered: false,
    listeners : {
    	totalcountchange : onStoreSizeChange,
		beforeload : function(store) {
			store.selections = adminLogGrid.getSelectionModel().getSelection();
			store.beforeCount = store.getTotalCount();
		},
		load : function(store) {
			if(store.getTotalCount() > 0) {
				Ext.getCmp('lblMatchingCount').setText(_label['matching_recs'] + ': ' + store.getTotalCount());
			} else {
				Ext.getCmp('lblMatchingCount').setText(_label['no_matching_recs']);
			}
			if (store.selections && store.selections.length > 0) {
				Ext.Array.forEach(store.selections, function(item) {
					var s = storeAdminLog;
					//item.index % s.pageSize : stay on current page 
					//s.getTotalCount() - s.beforeCount : move selection down when new rows are added
					var c = (item.index % s.pageSize) + (s.getTotalCount() - s.beforeCount);
					if (c <= s.pageSize) {
						adminLogGrid.getSelectionModel().select(c, true);	
					}
				});
			}
		}
	}
});

var adminLogGrid = new Ext.grid.Panel({
	bbar: {
        xtype: 'pagingtoolbar',
        pageSize: getPageSize(),
        store: storeAdminLog,
        displayInfo: true,
        plugins: new Ext.ux.ProgressBarPager()
    },
	selModel : Ext.create('Ext.selection.CheckboxModel'),
	columnLines : true,
	columns : [{
				dataIndex : 'idlog',
				header : _label['id'],
				sortable : true,
				width : 65
			},{
				dataIndex : 'idStatus',
				header : _label['status'],
				sortable : true,
				width : 100,
				renderer : logRenderers.statusRenderer
			},{
				dataIndex : 'logDateTime',
				header : _label['date'],
				sortable : true,
				width : 150				
			},{
				dataIndex : 'message',
				header : _label['description'],
				sortable : true,
				width : 720
			}
			],
	frame : false,
	id : 'adminLogGridId',
	store : storeAdminLog,
	viewConfig: {
		loadMask: false,
		forceFit: true,
		stripeRows: false,
	    getRowClass: function(record, rowIndex, rowParams, store) {
	    	 if(record.data.idStatus == 1)
	    		 return "red-background";
	    	 else if(record.data.idStatus == 2)
	    		 return "yellow-background";
	    },
	    trackOver: false,
        emptyText: '<h1 style="margin:20px">No matching results</h1>'
	},
	dockedItems: [{
        dock: 'top',
        xtype: 'toolbar',
        items: [
        {iconCls: 'schema_edit' , handler: deleteLogRecord , text: _message["deleteSelected"]},        
        {
            width: 400,
            fieldLabel: _label['search_desc'],
            labelWidth: 150,
            xtype: 'searchfield',
            store: storeAdminLog
        },
        {
        	id: 'lblMatchingCount',
            text: 'Matching records: ',
            xtype: 'label',
            store: storeAdminLog
        }
        /*, '->', {
            xtype: 'component',
            itemId: 'status',
            tpl: _label['matching_recs']+': {count}',
            style: 'margin-right:5px'
        }*/]
    }],
    selModel: {
        pruneRemoved: false
    },
	title : _label['log']
}
);
var adminLogPanel = Ext.create('Ext.Panel', {
	title: _label['log'],
	id: 'adminLogPanel',
	layout : 'vbox',
	bodyStyle:{"background-color":"#D9E7F8"}, 
	items: [{
		xtype: 'panel',
		layout: {
			type: 'absolute'
		},
		items: [{
			height: 510,
			width: 1250,
			items: [adminLogGrid]
			
		}]
	},
	{ xtype: 'container', height: 10 },
	{
		xtype: 'panel',
		bodyStyle:{"background-color":"#D9E7F8"},
		border : false,
		layout: 'absolute',
		items: [{			
	    		layout : 'absolute',
	    		bodyStyle:{"background-color":"#D9E7F8"},
				width : 900,
				border : false,
				items : [
				         {
				        	 xtype: 'button',
				        	 x: 520,
				        	 width : 100,
				 	    	 text : _message['deleteAll'],
				 	    	 handler: function(){
				 	    		 
				        		Ext.Ajax.request({
				        			success : function(result) {
				        				var recordResult = Ext.JSON.decode(result.responseText);
				        				if (recordResult.success == true) {
				        					App.setAlert(true,recordResult.message);
				        					adminLogGrid.store.load(); 
				        				}
				        				
				        			},
				        			url: './adminLogDestroy.json'
				        		});
				 	    	 }
				         }
	 			]		
		}]
	}
	]
});

var serversGrid = new Ext.grid.Panel({
			columnLines : true,
			columns : [{
						dataIndex : 'id',
						header : 'Id',
						sortable : true,
						width : 50
					},{
						dataIndex : 'name',
						header : _label['serverType'],
						sortable : true,
						width : 150
					},{
						dataIndex : 'isActive',
						header : _label['active'],
						sortable : true,
						width : 50,
						renderer : function(value, cell, record, rowIndex,
								colIndex, store) {
							var res = '<input type="checkbox"';
							if (eval(value))
								res += 'checked="checked"';
							res += 'onclick="activateServerFunc(\'' + record.data.id + '\', this.checked)"';
							res += ' >';
							return res
						}
					} ],
			frame : false,
			id : 'serversGridId',
			store : new Ext.data.JsonStore( {
				autoLoad : false,
				fields : [ 'id', 'name', 'isActive' ],
				proxy : {
					type : 'ajax',
			        api : {
						read    : './serversRead.json'
					},
					reader : {
						type : 'json',
						root : 'results'
					}
				}
			}),
			title : _label['server']
		}
);
function onStoreSizeChange() {
    adminLogGrid.down('#status').update({count: storeAdminLog.getTotalCount()});
}
function popupAdminEmail() {
		
	 var adminEmail = {
	        xtype: 'form',
	        id: 'adminEmailForm',
	        layout: 'absolute',
	        frame: false,
	        labelWidth: 50,
	        height: 320,
	        width: 380,
	        items: [
	                {id: 'configType' , name: 'configType' , value:'email', xtype: 'hidden'},
	                {id: 'idApplicationConfig' , name: 'idApplicationConfig' , xtype: 'hidden'},
	                {value: _label['userName'] , x: 10 , xtype: 'displayfield' , y: 10},
				    {id: 'userName' , name: 'userName' ,x: 10 , xtype: 'textfield' , width: 180 , y: 30, allowBlank: false},
				    {value: _label['password'] , x: 200 , xtype: 'displayfield' , y: 10},
				    {id: 'password' , name: 'password' ,inputType: 'password' , x: 200 , xtype: 'textfield' , width: 150 , y: 30, allowBlank: false},				    			
				    {value: _label['host'] , x: 10 , xtype: 'displayfield' , y: 75},
				    {id: 'host' , name: 'host' ,x: 10 , xtype: 'textfield' , width: 255 , y: 95, allowBlank: false},			
				    {value: _label['port'] , x: 275 , xtype: 'displayfield' , y: 75},
				    {id: 'port' , name: 'port' ,x: 275 , xtype: 'textfield' , width: 75 , y: 95, allowBlank: false},
				    {value: _label['protocol'] , x: 10 , xtype: 'displayfield' , y: 140},
				    {id: 'protocol' , name: 'protocol' ,x: 10 , xtype: 'textfield' , width: 70 , y: 160, allowBlank: false},			
				    {value: _label['encoding'] , x: 120 , xtype: 'displayfield' , y: 140},
				    {id: 'encoding' , name: 'encoding' ,x: 120 , xtype: 'textfield' , width: 70 , y: 160, allowBlank: false},
				    {value: _label['smtpsTimeout'] , x: 230 , xtype: 'displayfield' , y: 140},
				    {id: 'smtpstimeout' , name: 'smtpstimeout' ,x: 230 , xtype: 'textfield' , width: 120 , y: 160, allowBlank: false},
				    {value: _label['startttls'] , x: 10 , xtype: 'displayfield' , y: 200},
				    {id: 'starttls' , name: 'starttls' ,x: 30 , xtype: 'checkboxfield' , inputValue : 1, submitValue : true, y: 220},			
				    {value: _label['smtpsAuthenticate'] , x: 140 , xtype: 'displayfield' , y: 200},
				    {id: 'smtpsAuthenticate' , name: 'smtpsAuthenticate' ,x: 190 , xtype: 'checkboxfield', inputValue : 1, submitValue : true, width: 150 , y: 220}
				    ]
	    };
	     
		new Ext.Window({
			height: 330,
			id: 'popupAdminEmail',
			items: [
				adminEmail,
				{text: _message['save'], id: 'save', name: 'save', x: 30 , xtype: 'button' , width: 100 , y: 260 , 
					handler: function(){					    
					    processEmailForm(true);
					}
				},
				{text: _message['delete'], id: 'delete', name: 'delete', x: 135 , xtype: 'button' , width: 100 , y: 260 , disabled: true,
					handler: function(){
						Ext.MessageBox.confirm('Delete e-mail settings?', _message['delMsg'] + " e-mail settings?",  function(btn) {
					        if(btn == 'yes') {
					        	Ext.Ajax.request({
					        		params : {
					        			configType: 'email'
					        		},
					        		url: './appConfigDestroy.json',
					        		success : function(result) {
					        			var recordResult = Ext.JSON.decode(result.responseText);
					        			if (recordResult.success == true) {
					        				App.setAlert(true,recordResult.message);
					        				adminLogGrid.store.load(); 
					        			}
					        			Ext.getCmp('popupAdminEmail').close();
					        		},
					        		failure : function() {
					        			return 'failed';
					        		}
					        	});
					        }							
					    });
					}
				},
				{text: _message['test'], id: 'test', name: 'test', x: 240 , xtype: 'button' , width: 100 , y: 260 ,
					handler: function(){						
						processEmailForm(false);
					}
				}
			],
			layout: 'absolute',
			modal: true,
			resizable: false,
			title: _label['emailSettings'],
		    width: 380
	    });
		
		var req = Ext.Ajax.request({
		    url: './appConfigRead.json',
		    params : {
				configType : 'email'
			},
		    disableCaching: false,
		    success: function ( result, request ) {
		    	var response = Ext.decode(result.responseText);				    	
		    	if(response.success == true){
		    		Ext.getCmp('idApplicationConfig').setValue(response.results[0].idApplicationConfig);
		    		Ext.getCmp('userName').setValue(response.results[0].userName);
		    		Ext.getCmp('password').setValue(response.results[0].password);
		    		Ext.getCmp('host').setValue(response.results[0].host);
		    		Ext.getCmp('port').setValue(response.results[0].port);
		    		Ext.getCmp('protocol').setValue(response.results[0].protocol);
		    		Ext.getCmp('encoding').setValue(response.results[0].encoding);
		    		Ext.getCmp('smtpstimeout').setValue(response.results[0].smtpsTimeout);
		    		Ext.getCmp('starttls').setValue(response.results[0].isStarTtls);
		    		Ext.getCmp('smtpsAuthenticate').setValue(response.results[0].isSmtpsAuthenticate);
		    		Ext.getCmp('delete').setDisabled(false);
		    	}
		    	Ext.getCmp('popupAdminEmail').show();
		    },
		    failure: function () {
		    	alert("Failure");
		    }
		});
	    
}
function processEmailForm(add) {
	var form = Ext.getCmp('adminEmailForm').getForm();
	var formSubmitURL = './appConfigCreate.json';
	var isDeleteDisabled = true;
	
	if(add && Ext.getCmp('idApplicationConfig').value > 0) {
    	formSubmitURL = './appConfigUpdate.json';
    } else if(!add) {
    	formSubmitURL = './appConfigCheck.json';
    }
    
	if(Ext.getCmp('idApplicationConfig').value > 0) {
		isDeleteDisabled = false;
	}
	
    if(Ext.getCmp('userName').getValue().length == 0) {
    	App.setAlert(false, _error['userNameRequired']);
    	return false;
	} else if(Ext.getCmp('password').getValue().length == 0) {
    	App.setAlert(false, _error['passwordRequired']);
    	return false;
	} else if(Ext.getCmp('host').getValue().length == 0) {
    	App.setAlert(false, _error['hostRequired']);
    	return false;
	} else if(Ext.getCmp('port').getValue().length == 0) {
    	App.setAlert(false, _error['portRequired']);
    	return false;
	} else if(Ext.getCmp('protocol').getValue().length == 0) {
    	App.setAlert(false, _error['protocolRequired']);
    	return false;
	} else if(Ext.getCmp('encoding').getValue().length == 0) {
    	App.setAlert(false, _error['encodingRequired']);
    	return false;
	} else if(Ext.getCmp('smtpstimeout').getValue().length == 0) {
    	App.setAlert(false, _error['smtpstimeoutRequired']);
    	return false;
	} 
    
	if (form.isValid()) {
		Ext.getCmp('save').setDisabled(true);
		Ext.getCmp('delete').setDisabled(true);
		Ext.getCmp('test').setDisabled(true);
		
		var mask = new Ext.LoadMask(Ext.getBody(), 
					{	
						msg : _message['waitMessage']
					}
				   );
				   mask.show();
		form.submit({
			url: formSubmitURL,		                        
			success: function(form, action) {
				mask.hide();
				mask = null;
				Ext.getCmp('save').setDisabled(false);
				Ext.getCmp('delete').setDisabled(isDeleteDisabled);
				Ext.getCmp('test').setDisabled(false);
				App.setAlert(true , action.result.message);
				if(add) {
					Ext.getCmp('popupAdminEmail').close();
				}
			},
			failure: function(form, action) {
			  mask.hide();
			  mask = null;
			  Ext.getCmp('save').setDisabled(false);
			  Ext.getCmp('delete').setDisabled(isDeleteDisabled);
			  Ext.getCmp('test').setDisabled(false);
              App.setAlert(_message["error"], action.result.message);																	
			}
		});
	}
}

function deleteLogRecord() {
	var grid = this.up('grid');
	var selections = grid.getSelectionModel().getSelection();
	var total = selections.length;	
	var logIds = '';
	for(var count = 0; count < total; count++){
	    logIds = selections[count].get('idlog') + "," + logIds;
	}	 
	if(logIds == '') {
		App.setAlert(false, _alert['selectRecord'] );
		return;	 
	}
	grid.getSelectionModel().deselectAll(false);
	Ext.Ajax.request({
		params : {
			logIds: logIds,
			isAllDelete: false
		},
		url: './adminLogDestroy.json',
		success : function(result) {
			var recordResult = Ext.JSON.decode(result.responseText);
			if (recordResult.success == true) {
				App.setAlert(true,recordResult.message);
				adminLogGrid.store.load(); 
			}			
		},
		failure : function() {
			return 'failed';
		}
	});
}