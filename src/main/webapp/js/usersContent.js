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
 function activateUser(userId, bIsActive) {
		
	Ext.Ajax.request( {
		params : {
			userId : userId,
			isActive : (eval(bIsActive) ? 1 : 0)
		},
		success: function (result) {
			storeUsers.load();		
		},
		url : './activateDeactivateUser.json'
	});	 	
};
var storeTheme = new Ext.data.ArrayStore({
	data : [['neptune' , 'Neptune'],['classic' , 'Classic']],
	fields: ['idTheme' , 'name']
});
var storeLanguage = new Ext.data.ArrayStore({
	data : [['en' , _language['english']],['it' , _language['italian']],['de' , _language['german']],['ru' , _language['russian']]],
	fields: ['idLanguage' , 'name']
});

var storeIdRole = new Ext.data.ArrayStore({
	data : [['1' , _role['admin']],[2 , _role['appManager']] , [3 , _role['operator']] , [4 , _role['dispatcher']],[5, _role['user']] ],
	fields: ['idRole' , 'name']
});
Ext.define('alerts', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idAlert', type:'int', defaultValue:0}, 
		{name: 'alertName', type: 'string'}
    ],	
	idProperty:'idAlert'
});
var storeAlert = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'alerts',
	proxy: {
		type: 'ajax',
		api: {
			read    : './alertsRead.json'
		},
		reader: {
			type: 'json',
			root: 'results',
			idProperty: 'idAlert'			
		}
	}
});
storeAlert.load(); 
function roleName(idRole) {	
	if  (storeIdRole.getCount()) {
		var rec = storeIdRole.getAt(storeIdRole.find('idRole' , idRole));
		return rec == null ? idRole : rec.get('name');
	} else {
		return 'errore';
	};	
}

Ext.define('users', {
    extend: 'Ext.data.Model',
    fields: [
		{name: 'idUser', type:'int', defaultValue:0}, 
		{name: 'userName', type: 'string'},
		{name: 'password', type: 'string'},        
		{name: 'name', type: 'string'},
        {name: 'surname', type: 'string' },
		{name: 'email', type: 'string'},
		{name: 'idRole', type:'int'},
		{name: 'idAlert', type:'int'},
		{name: 'createdBy', type:'int'},
		{name: 'enabled', type: 'int'},
		{name: 'language', type:'string'},
		{name: 'dateOfBirth', type: 'date' , dateFormat: 'Y-m-d'},
		{name: 'userApplications', mapping: 'idApplication'},
		{name: 'userSchemas', mapping: 'idSchema'},
		{name: 'theme', type: 'string' }
    ],	
	idProperty:'idUser'
});
var storeUsers = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'users',
	idProperty:'idUser',
	proxy: {
		type: 'ajax',
        api: {
			read    : './usersRead.json',
			create  : './usersCreate.json',
			update  : './usersUpdate.json?_method=put',
			destroy : './usersDestroy.json?_method=delete'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message',
			idProperty: 'idUser'
		},
		extraParams:{		
			userId: ""            
        },
		writer: {
			type: 'json',
			writeAllFields:true
		}
	},
	listeners: {
		load: function(store,operation,options){
			if(role != 1){
				usersGrid.columns[6].setVisible(false);
			}
		},
		
		write: function(store, operation){
			if (operation.response.responseText) {
				var responseObj = Ext.decode(operation.response.responseText);
				App.setAlert(true,responseObj.message);
				if(operation.action == 'update'){
					storeUsers.load();
					if(prevTheme != currentTheme) {
						window.location.reload();
					}
				}
			}               
        }
    }
});
storeUsers.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if(responseObj.message.indexOf('Error')!=-1){
			Ext.Msg.alert("" , _error['connectionError']);
		} else{
			App.setAlert(false , responseObj.message);
			if (operation.action == 'create') {
				storeUsers.load();
        	} else {
        		storeUsers.remove();
        	}
		}
	}	
});
var isEditSuccess = false;
var editorUsers  = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
        clicksToEdit: 2,
        validationScheme : 'validationUsers',
        validateEditSuccess : function() {
        	isEditSuccess = true;
        	usersGrid.store.sync();
        },
        listeners: {
			beforeedit:function(editor,e,eOpts ) {
				usersGrid.columns[6].hide();		
			},
			canceledit: function(grid, eOpts) {				
				usersGrid.columns[6].setVisible(true);
			},
			afteredit: function() {
				if(isEditSuccess == true) {
					isEditSuccess = false;
					usersGrid.columns[6].setVisible(true);
				}
			}
		}
    });

var columnsUsers = [
	{align: 'center' , dataIndex: 'idUser' , header: 'Id' ,  sortable: true , width: 50},
	{dataIndex: 'userName' , editor: {xtype:'textfield'} , header: _label['userName']  , sortable: true , width: 120},
	{dataIndex: 'name' , editor: {xtype:'textfield'} , header: _label['name'] , sortable: true , width: 120},
	{dataIndex: 'surname' , editor: {xtype:'textfield'} , header: _label['surname']  , sortable: true , width: 100},
	{dataIndex: 'email' , editor: {xtype:'textfield'} , header: _label['email'] , sortable: true , width: 150},
	{dataIndex: 'idRole' , header: _label['role'] , sortable: true , width: 120,renderer: function(value){return roleName(value);}},
	{dataIndex: 'enabled' , header : _label['active'], width: 50 , sortable: true, renderer : function(value, cell, record, rowIndex,
			colIndex, store) {
		var res = '<input type="checkbox"';
		if (eval(value))
			res += 'checked="checked"';
		res += 'onclick="activateUser(\'' + record.data.idUser + '\', this.checked)"';
		res += ' >';
		return res 
	}}	
];

var usersGrid = new Ext.grid.Panel({
	columnLines: true,
	columns: columnsUsers,
	frame: false,
	id: 'usersGrid',
	plugins: [editorUsers],
	// Migration Change
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	store: storeUsers,
	title: _label['users']
});

function addUser() {
	var record = new usersGrid.store.model(); //recordType
	editorUsers.cancelEdit();
	popupUser(record , true);
};

function deleteUser() {
    var record = usersGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
	
   	Ext.MessageBox.confirm('Delete User?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
   		if(btn == 'yes') {
   			usersGrid.store.proxy.extraParams.userId = record.data.idUser;
   			usersGrid.store.remove(record);
   			usersGrid.store.sync();
   		}
   	});
};
function editUser() {
    var record = usersGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorUsers.cancelEdit();
   
    var rowIndex = storeUsers.findExact("idUser", record.get('idUser'));	
	record = storeUsers.getAt(rowIndex);
	
	//Roles: 1= admin;2=Application Manager;3=Operator;4=Dispatcher;5=user
    if(role==1 || role==2){ 
		if(userId == record.get('idUser')){
			popupModifyProfile(record , false);
		}else{
			popupUserManage(record, false,role);
		}		
	} else{
		popupModifyProfile(record , false);
	}
    //popupUser(record , false);
};

function helpUser(){
	popupUserHelp();
};