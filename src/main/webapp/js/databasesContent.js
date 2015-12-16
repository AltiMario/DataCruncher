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

var columns = '';
var tables = '';

var storeDatabaseType = new Ext.data.ArrayStore({
	data : [['1' , 'MySql'],['2' , 'Oracle'],['3' , 'SQL Server'],['4' , 'PostgreSQL'],['5' , 'DB2'],['6' , 'SQLite'],['7' , 'Firebird'],['8' , 'SAPDB'],['9' , 'HSQLDB'] ],
	fields: ['idDatabaseType' , 'name']
});

var comboDatabaseType = Ext.create('Ext.form.ComboBox', {
	displayField: 'name',
	querymode: 'local',
	store: storeDatabaseType,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idDatabaseType'
});

function databaseTypeName(idDatabaseType) {	
	if  (storeDatabaseType.getCount()) {
		var rec = storeDatabaseType.getAt(storeDatabaseType.find('idDatabaseType' , idDatabaseType));
		return rec == null ? idDatabaseType : rec.get('name');
	} else {
		return 'errore';
	};	
}

Ext.define('databases', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idDatabase', type:'int', defaultValue:0}, 
		{name: 'idDatabaseType', type:'String'},
		{name: 'name', type:'String'},
		{name: 'host', type:'String'},
		{name: 'port', type:'String'},
		{name: 'databaseName', type:'String'},
		{name: 'userName', type:'String'},
		{name: 'password', type:'String'},
		{name: 'description', type:'String'}		
    ],
    idProperty:'idDatabase'
});

var storeDatabases = new Ext.data.Store({
	autoLoad: true,
	autoSync : false,
	model: 'databases',
	idProperty:'idDatabase',
	proxy: {
		type: 'ajax',
        api: {
			read    : './databasesRead.json',
			create  : './databasesCreate.json',
			update  : './databasesUpdate.json?_method=put',
			destroy : './databasesDestroy.json?_method=delete&dbId='
		},		
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'			
		},
		writer: {
			type: 'json',
			writeAllFields:true
		}
	},
		
	listeners: {
		write: function(store, operation){
			if (operation.response.responseText) {
				var responseObj = Ext.decode(operation.response.responseText);
				App.setAlert(true,responseObj.message);
				/*if(operation.action == 'update'){
					operation.records[0].set('status',"<img src='./images/ajax-loader.gif' />");
					updateDatabasesStatus();
				}else if(operation.action == 'create'){
					storeDatabases.getAt(0).set('status',"<img src='./images/ajax-loader.gif' />");
					updateDatabasesStatus();
				}*/
			}               
        },
        //load :initializing the connection with value
    	//Connecting...
    	load: function(store,records,successful,operation,options ) {
            store.each(function(records){
            		records.set('status',"<img src='./images/ajax-loader.gif' />");
            });
            updateDatabasesStatus();           
        }
    }
  });
storeDatabases.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.message.indexOf('Error')!=-1){
				Ext.Msg.alert("" , _error['connectionError']);
			} else{
				App.setAlert(false , responseObj.message);
				if (operation.action == 'create') {
	    			storeDatabases.load();
	        	}else{
	    			storeDatabases.remove();
	        	}
			}
		}
	}	
});

var editorDatabases =  Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
        clicksToEdit: 2,
        validationScheme : 'validationDb',
		validateEditSuccess : function() {
			databasesGrid.columns[7].setVisible(true);
			var record = databasesGrid.getSelectionModel().getSelection()[0];
	    	record.set('status',"<img src='./images/ajax-loader.gif' />");
			databasesGrid.store.sync({
				success : function() {
			    	updateDatabasesStatus();
				}
			});
		},
		listeners: {
			beforeedit: function(editor,e,eOpts ) {
				databasesGrid.columns[7].hide();
			},
			canceledit: function(grid, eOpts) {
				databasesGrid.columns[7].setVisible(true);
			}
		}			
    });

function password() {
	return '****';
}

var columnsDatabases = [
	{dataIndex: 'idDatabase' , header: 'Id' ,   sortable: true , width: 50},
	{dataIndex: 'name' , editor: {xtype:'textfield'}  , header: _label['name'] , sortable: true , width: 150},
	{dataIndex: 'idDatabaseType', editor: {xtype:'combobox', displayField: 'name', querymode: 'local', store: storeDatabaseType, triggerAction:  'all', typeAhead: true, valueField: 'idDatabaseType'}, header: _label['databaseType'], sortable: true , width: 100, renderer: function(value){return databaseTypeName(value);} },
	{dataIndex: 'host' , editor: {xtype:'textfield'}  , header: _label['host'] ,  sortable: true , width: 150},
	{dataIndex: 'port' , editor: {xtype:'textfield'}  , header: _label['port'] ,  sortable: true , width: 50},
	{dataIndex: 'databaseName' , editor: {xtype:'textfield'}  , header: _label['databaseName'] ,  sortable: true , width: 150},
	{dataIndex: 'userName' , editor: {xtype:'textfield'}  , header: _label['userName'] , sortable: true , width: 150},
	{dataIndex: 'status', header: _label['status'], id:'status',align: 'CENTER'}
];

Ext.define('NoMarkDirtyFeature', {
	extend: 'Ext.grid.feature.Feature',
	mutateMetaRowTpl: function(metaRowTpl) {
		metaRowTpl[2] = metaRowTpl[2].replace(/\{\{id\}\-modified\}/, '');
	}
});

var databasesGrid =  Ext.create('Ext.grid.Panel', {
	columnLines: true,
	columns: columnsDatabases,
	frame: false,
	border: false,
	id: 'databasesGrid',
	plugins: [editorDatabases],
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	store: storeDatabases,
	title: _label['database'],
	listeners: {
		itemclick: function(grid, record, item, index, e) {
   			var id = record.data.idDatabase;

   			Ext.Ajax.request({
   				url: "./profilerLoad.json?action=loadProfile&_dbId=" + id,
   				disableCaching: false,
   				success: function ( result, request ) {
   					var response = Ext.decode(result.responseText);			
   					if(response.success == true){
   						
					var sampleTreeStore = Ext.create('Ext.data.TreeStore', {
							autoSync:true,
   							proxy : {
   								type : 'ajax',
   								url : 'profilerTree.json'
   							},
   							sorters : [ {
   								property : 'leaf',
   								direction : 'ASC'
   							}, {
   								property : 'text',
   								direction : 'ASC'
   							} ]
   						}); 
						sampleTreeStore.setRootNode({
							id: '0',
							text: 'Data Base',
							leaf: false,
							expanded: true // If true, the store load's itself immediately; we want that to happen!
						});
   						var sampleTree = Ext.create('Ext.tree.Panel', {
   							autoScroll: true,
   							id: 'dbTableTree',
   							store:sampleTreeStore,
   							singleExpand: true,
   							rootVisible: true,
   							useArrows:true,
   							listeners : {
   								itemclick : function(view, record, item, index, e) {
   									reloadTab(record.get('text'), record.isLeaf(),
   											record.parentNode.get('text'));
   								}
   							}
   						});
   						
   						if(Ext.getCmp('centerPanel').items.length > 0) {
   							Ext.getCmp('centerPanel').removeAll();
   						}
   						
   						Ext.getCmp('centerPanel').add(sampleTree);
   						
   						Ext.Ajax.request( {
   							url : './profilerLoad.json?action=getColumnNames',
   							success: function (result) {				
   								var temp = result.responseText;
   								temp = temp.substring(1, (temp.length - 1));
   								columns = Ext.JSON.decode(temp);
   							},
   							failure : function() {
   							
   							}
   						});

   						Ext.Ajax.request( {
   							url : './profilerLoad.json?action=getTableNames',
   							success: function (result) {
   								var temp = result.responseText;
   								temp = temp.substring(1, (temp.length - 1));
   								tables = Ext.JSON.decode(temp);
   							},
   							failure : function() {
   							
   							}
   						});
   					} else {	    		
   						App.setAlert(false ,response.message);
   					}
   				}
   			}); 		    
   		   }
	},
	features: [
			Ext.create('NoMarkDirtyFeature')
	]
});

var databasesGridReadOnly = Ext.create('Ext.Panel',{
    frame:false,
    border:false,
    collapsible:false,
    id: 'databasesGridReadOnlyPanel',
    title: '',
    layout:'border',
    items:[{
      region: 'center', 
      id:'westPanel',
      title:'',
      xtype:'panel',
      layout:'fit',
      monitorResize:true,
      border: true,
      items:[{
   	   xtype:'panel',
   	   layout:'fit',
   	   border : false,
   	   autoScroll: false,
   	   monitorResize:true,
   	   frame : false,
   	   id: 'home-center'
      }]
    },         
    {
       region: 'east',     
       id:'centerPanel',
       width:'20%',
       collapseMode: 'mini',
       xtype:'panel',
       layout:'fit',
       collapsible:true,
       autoScroll: false,
       border: false,
       baseCls:'x-plain',
       split: true ,
       title:''
    }]
  });

function connectionToDB(id) {
	Ext.Ajax.request({
		url: "./profilerLoad.json?_dbId=" + id,
		disableCaching: false,
		success: function ( result, request ) {
			var response = Ext.decode(result.responseText);			
			if(response.success == true){
				//window.location = "profilerHome.json";	
				window.open("profilerHome.json");
			} else {	    		
				App.setAlert(false ,response.message);
			}
		}
	});
}
function addDatabase() {
	var record = new databasesGrid.store.model();
	record.set('idDatabaseType' , 1);
	editorDatabases.cancelEdit();
	popupDatabase(record , true);
};

function deleteDatabase() {
    var record = databasesGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    }; 
    
    Ext.MessageBox.confirm('Delete Database?', _message['delMsg']+" '"+ record.get('name')+"'??",  function(btn) {
        if(btn == 'yes') {
			var store = databasesGrid.store;
        	store.remove(record);    
        	store.sync();
        }
    });
        
};
function editDatabase() {
    var record = databasesGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorDatabases.cancelEdit();
    popupDatabase(record , false);
};

function helpDatabase(){
	popupDatabaseHelp();
};

function renderIcon(status) {
	//read the images associated with connections
	var imgConnected = './images/check.png';
	var imgNotConnected = './images/alert.png';
	var imgConnecting = './images/ajax-loader.gif';
	var imgID;
	if(status == undefined) 
		imgID = imgConnecting;
	else if(status == false)
		imgID = imgNotConnected;
	else
		imgID = imgConnected;

	//return the image associated with the status of the connection
	return '<img src="' + imgID + '" />';
}

var updateDatabasesStatus = function() {
	var myrequest = new Object();
	myrequest['count'] = storeDatabases.getCount();
	for(var i = 0; i < storeDatabases.getCount(); i++) {

		//create the body of the post message to the status controller
		var record = storeDatabases.getAt(i);
		myrequest['idDatabaseType' + i] = databaseTypeName(record.get('idDatabaseType'));
		myrequest['host' + i] = record.get('host');
		myrequest['port' + i] = record.get('port');
		myrequest['databaseName' + i] = record.get('databaseName');
		myrequest['userName' + i] = record.get('userName');
		myrequest['password' + i] = record.get('password');
	}
	//send the request to the status controller
	Ext.Ajax.request({
	    url: "./statusRead.json",
	    params: Ext.urlEncode(myrequest),
	    disableCaching: false,
	    success: function ( result, request ) {
	    	//load the response in array of status
	    	//For every element of the array
	    	// 0 = not connected to the database
	    	// 1 = connected to the database
	    	var arraystatus = result.responseText;
	    	for(var i = 0; i < storeDatabases.getCount(); i++) {
	    		var record = storeDatabases.getAt(i);
	    		if(arraystatus.charAt(i) == '0')
	    			record.set('status', renderIcon(false));
	    		else
	    			record.set('status', renderIcon(true));
	    	}
	    }
	});
};

