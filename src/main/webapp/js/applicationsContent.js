/*
 * Copyright (c) 2019  Altimari Mario
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

function activateApplicationFunc(applicationId, bIsActive) {
		
	Ext.Ajax.request( {
		params : {
			applicationId : applicationId,
			isActive : (eval(bIsActive) ? 1 : 0)
		},
		success: function (result) {
			storeApplications.load();		
		},
		url : './applicationIsActive.json'
	});	 	
};

Ext.define('applications', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idApplication', type:'int', defaultValue:0}, 
		{name: 'name',   type: 'string'},
        {name: 'description', type: 'string'},
        {name : 'startDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name : 'endDate' , type: 'date' , dateFormat: 'Y-m-d'},
		{name : 'isActive' , type:'int'},
		{name: 'isPlanned', type: 'boolean'},
        {name: 'plannedName', type: 'int'},
        {name: 'isSiteGenerated', type: 'boolean'}
    ],	
	idProperty:'idApplication'
});
var storeApplications = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'applications',
	idProperty:'idApplication',
	proxy: {
		type: 'ajax',
        api: {
			read    : './applicationsRead.json',
			create  : './applicationsCreate.json',
			update  : './applicationsUpdate.json?_method=put',
			destroy : './applicationsDestroy.json?_method=delete'
		},		
		extraParams:{		
			idApplication: ""            
        },
        reader: {
            type: 'json',
            root: 'results',
			successProperty: 'success',
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
				if(operation.action == 'update'){
					storeApplications.load();
				}
			}               
        }    
    }
});
storeApplications.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.message.indexOf('Error')!=-1){
					Ext.Msg.alert("" , _error['connectionError']);
				} else{
					App.setAlert(false , responseObj.message);
					if (operation.action == 'create' || operation.action == 'destroy') {
						storeApplications.load();
					}
				}
			}
		}	
});
var isEditSuccess = false;
var editorApplications = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
        clicksToEdit: 2,
        validationScheme : 'validationApplications',
        validateEditSuccess : function() {
        	isEditSuccess = true;
        	applicationsGrid.store.sync();
        },
		listeners: {
			beforeedit:function(editor,e,eOpts ) {
				applicationsGrid.columns[4].hide();	
				if(roleActivities.indexOf("editApp")==-1){
					return false;
				}						
			},
			canceledit: function(grid, eOpts) {				
				applicationsGrid.columns[4].setVisible(true);
			},
			afteredit: function() {
				if(isEditSuccess == true) {
					isEditSuccess = false;
					applicationsGrid.columns[4].setVisible(true);
				}
			}
		}			
    });

var columnsApplications = [	
	{align: 'center' , dataIndex: 'idApplication' , header: 'Id' , sortable: true , width: 50 },
	{dataIndex: 'name' , editor: {xtype:'textfield'} , header: _label['name'] ,  sortable: true , width: 150},
	{dataIndex: 'startDate' , xtype: 'datecolumn',  header: _label['startDate'], width: 150 , field: {xtype: 'datefield',format: 'd/m/Y'}},
	{dataIndex: 'endDate' , xtype: 'datecolumn',  header: _label['endDate'], width: 150 , field: {xtype: 'datefield',format: 'd/m/Y'}},	
	{dataIndex: 'isActive' , header : _label['active'], width: 50 , sortable: true, renderer : function(value, cell, record, rowIndex,
			colIndex, store) {
		var res = '<input type="checkbox"';
		if (eval(value))
			res += 'checked="checked"';
		res += 'onclick="activateApplicationFunc(\'' + record.data.idApplication + '\', this.checked)"';
		res += ' >';
		return res 
	}}
];


var applicationsGrid = Ext.create('Ext.grid.Panel', {
	columnLines: true,
	columns: columnsApplications,
	frame: false,
	border: false,
	id: 'applicationsGrid',
	plugins:  [editorApplications],
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	store: storeApplications,
	title: _label['applications']
});

function addApplication() {
	var record = new applicationsGrid.store.model();
	editorApplications.cancelEdit();
	popupApplication(record , true);
};

function deleteApplication() {
    var record = applicationsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    
    Ext.MessageBox.confirm('Delete Application?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	Ext.MessageBox.confirm('Delete Application?', _message['delRecursiveMsg']+" '"+ record.get('name')+"'" + _message['sureMsg'],  function(btnConfirm) {
        		if(btnConfirm == 'yes') {        				
        			applicationsGrid.store.proxy.extraParams.idApplication = record.data.idApplication;
               		applicationsGrid.store.remove(record);
               		applicationsGrid.store.sync();
        		}
        	});
        }
    });
};

function editApplication() {
    var record = applicationsGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
	editorApplications.cancelEdit();
	
	var rowIndex = storeApplications.findExact("idApplication", record.get('idApplication'));	
	record = storeApplications.getAt(rowIndex);	
	
	popupApplication(record , false);
};

function helpApplication(){
	popupApplicationHelp();
}