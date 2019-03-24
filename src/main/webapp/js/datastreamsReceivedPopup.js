/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

function popupDatastreamReceived(record) {
	
	var schemaName = record.get('name');
	if (schemaName == null) {
		schemaName = "";
	}
	Ext.define('datastreamsReceived', {
		extend: 'Ext.data.Model',
		fields: [
			'idDatastream',
			'idSchema',
			'datastream',
			'checked',
			'message',
			{name : 'receivedDate' , type: 'date', dateFormat: 'c'},
			//{name : 'receivedTime' , type: 'date' , dateFormat: 'Y-m-d'}
			//'receivedDate'
			//'receivedTime'
		],	
		idProperty:'idDatastream'
	});
	var storeDatastreamsReceived = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'datastreamsReceived',
		idProperty:'idDatastream',
		pageSize:18,
		proxy: {
			type: 'ajax',
			api: {
				read    : './datastreamsReceivedPopupRead.json?idSchema=' + record.get('idSchema'),
				update  : './datastreamsReceivedPopupUpdate.json?_method=put',
				destroy : './datastreamsReceivedPopupDestroy.json'
			},
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty: 'idDatastream',
				totalProperty: 'total'
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
				}               
			}			
	    }	   
	});
	storeDatastreamsReceived.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeDatastreamsReceived.remove();			
		}	
	});
	storeDatastreamsReceived.load({params:{start:0 , limit:18}});
	
	function formatLineWrap(value){
	    var str = '<div style="white-space:pre-wrap !important;display:inline-block;">' + value + '</div>';
	    return str;
	}
	
	function checkedStatus(value){
		if(value == 1) {
			value = "check.png";
		}else if(value == 0) {
			value = "error.png";
		} else if(value == 2) {
			value = "warning.png";
		}
		var str = "<img src='./images/" + value + "' />";
	    return str;
	}
	
	var columnsDatastreamsReceived = [
		{align: 'center' , dataIndex: 'idDatastream' , header: 'Id' , sortable: true , width: 50, searchable: false},
		{dataIndex: 'message' , header: 'Message' , sortable: true , flex: 1, renderer : formatLineWrap},
		{align: 'center', dataIndex: 'checked' , header: _label['checked'] ,  sortable: true ,  flex: 1 , renderer : checkedStatus, searchable: false},
		{align: 'center' , dataIndex: 'receivedDate' ,  format: "Y-m-d H:i:s" , header: _label['date'] ,  sortable: true , flex: 1 , xtype: 'datecolumn'}
		//{align: 'center' , dataIndex: 'receivedDate' , format: 'Y-m-d H:i:s' , header: _label['time'] ,  sortable: true , flex: 1 , xtype: 'datecolumn'}
	];

	var tbarDatastreamsReceived = [
		{iconCls: 'schema_delete' , handler: deleteDatastreamReceived , text: _message["delete"]} , '-' ,
		{iconCls: 'schema_edit' , handler: editDatastreamReceived , text: _message["edit"]}
	];

	var datastreamsReceivedGrid = Ext.create('Ext.grid.Panel', {
		bbar:Ext.create('Ext.PagingToolbar', {
			store: storeDatastreamsReceived,
			displayInfo: true		
		}),
		title: _message["datastreamReceivedOfSchema"]+schemaName,
		features: [searchFeature],
		columnLines: true,
		columns: columnsDatastreamsReceived,
		height: 468,
		id: 'datastreamsReceivedGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeDatastreamsReceived,
		tbar: tbarDatastreamsReceived
	});
	
	function deleteDatastreamReceived() {
	    var record = datastreamsReceivedGrid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert["selectRecord"]);
	        return false;
	    };
	    datastreamsReceivedGrid.store.remove(record);
	    datastreamsReceivedGrid.store.sync();
	};

	function editDatastreamReceived() {
	    var record = datastreamsReceivedGrid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert["selectRecord"]);
	        return false;
	    };
	    popupValidateDatastream(record , "edit");
	};
	
	new Ext.Window({
		height: 500,
		id: 'popupDatastreamReceived',
		items: [datastreamsReceivedGrid] ,
		layout: 'fit',
		modal: true,
		resizable: false,
	    title: _label["dataStreamReceived"],
	    width: 760,
		maximizable: true,
	    listeners : {
            'resize' : function(win,width,height,opt){
               if(width != 760) {
            	   storeDatastreamsReceived.pageSize = 26;
            	   storeDatastreamsReceived.loadPage(1);
               } else {
            	   storeDatastreamsReceived.pageSize = 18;
            	   storeDatastreamsReceived.loadPage(1);
               }
             }
	    }
    }).show();
};