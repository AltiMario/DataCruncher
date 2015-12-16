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
function popupSchemaDocuments(record) {
	
	var storeDocSchema = new Ext.data.Store({
		autoSave: false,
		autoLoad: false,
		fields: ['idFile', 'idSchema', 'name', 'description', 'contentType'],
		idProperty:'idFile',
		pageSize:18,
		proxy: {
			type: 'ajax',
			api: {
				read    : './documentsPopupRead.json?idSchema=' + record.get('idSchema')
					},
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message',
				idProperty: 'idFile',
				totalProperty: 'total'
			}
		}
	});
	storeDocSchema.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			App.setAlert(false , responseObj.message);
			storeDocSchema.remove();			
		}	
	});
	storeDocSchema.load({params:{start:0 , limit:18}});
	
	var columnsDocSchema = [
  		{align: 'center' , dataIndex: 'idFile' , header: 'Id' , sortable: true , width: 50},
  		{dataIndex: 'name' , header: 'Name' , sortable: true , flex: 1},
  		{dataIndex: 'description' , header: 'Description' , sortable: true , flex: 1},
  		{dataIndex: 'contentType' , header: 'Type' , sortable: true , flex: 1}
     ];
	var tbarDocSchema = [
   		{iconCls: 'schema_document_delete' , handler: deleteDocSchema, text: _message["delete"]} , '-' ,
   		{iconCls: 'schema_document_upload' , handler: uploadDocSchema , text: _message["upload"]}, '-' ,
   		{iconCls: 'schema_document_download' , handler: downloadDocSchema , text: _message["download"]}
   	];
	
	var docSchemaGrid = Ext.create('Ext.grid.Panel', {
		bbar:Ext.create('Ext.PagingToolbar', {
			store: storeDocSchema,
			displayInfo: true			
		}),
		columnLines: true,
		columns: columnsDocSchema,
		height: 468,
		id: 'docSchemaGrid',
		selModel: Ext.create('Ext.selection.RowModel', { 
			mode:'SINGLE'
		}),
		store: storeDocSchema,
		tbar: tbarDocSchema
	});
	
	
	function deleteDocSchema() {
	    var idFile;
	    var record = docSchemaGrid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert["selectRecord"]);
	    	idFile = "";
	        return false;
	    }else{
	    	idFile = record.get('idFile');
		    Ext.Ajax.request({ 
				params: {
					fileid: idFile
				},
				success: function (result) {
					    var recordResult = Ext.JSON.decode(result.responseText);
					 
					    if (eval(recordResult.success)) {
					    	App.setAlert(true, recordResult.message);
					    	docSchemaGrid.store.remove(record);
						    docSchemaGrid.store.sync();
					    }else{
					    	App.setAlert(false, recordResult.message);
					    }
					},
					url: './documentsPopupDestroy.json',
				failure : function(result) {
					var recordResult = Ext.JSON.decode(result.responseText);
					App.setAlert(false, recordResult.message);	
				}
			});
	    };
	    
	};
	function downloadDocSchema() {
	    var record = docSchemaGrid.getSelectionModel().getSelection()[0];
	    if (!record) {
	    	App.setAlert(false , _alert["selectRecord"]);
	        return false;
	    };
	    
	    ActionFile(record.get('idFile')); 
	};
	function uploadDocSchema() {
		popupUploadDocSchema(record);
		docSchemaGrid.store.sync();
	};
	function ActionFile(fileid) {
        var body = Ext.getBody();

        var frame = body.createChild({
            tag: 'iframe',
            cls: 'x-hidden',
            id: 'iframe',
            name: 'iframe'
        });
        var form = body.createChild({
            tag: 'form',
            cls: 'x-hidden',
            id: 'form',
            action: './documentsPopupDownload.json',
            target: 'iframe'
        });

        var inputpara = form.createChild({
            tag: 'input',
            type: 'hidden',
            name: 'fileid', // parameter name
            value: fileid  // parameter value
        });
        
        form.dom.submit();
    }
	new Ext.Window({
		height: 500,
		id: 'popupSchemaDocuments',
		items: [docSchemaGrid] ,
		layout: 'fit',
		modal: true,
		resizable: false,
	    title: _label["loadDocuments"],
	    width: 760,
	    maximizable: true,
	    listeners : {
            'resize' : function(win,width,height,opt){
               if(width != 760) {
            	   storeDocSchema.pageSize = 26;
            	   storeDocSchema.load({params:{limit:26}});
               } else {
            	   storeDocSchema.pageSize = 18;
            	   storeDocSchema.load({params:{limit:18}});
               }
             }
	    }
    }).show();
};
