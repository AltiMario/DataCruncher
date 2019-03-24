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


function popupUser(record , add) {    
	new Ext.Window({
		height: 210,
		id: 'popupUser',
		bodyStyle:{"background-color":"#ffffff"},
		items : [ {
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
				{value: _label['userName'] , x: 10 , xtype: 'displayfield' , y: 10},
				{id: 'userName', name: 'userName', value: record.get('userName'), x: 10 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['password'] , x: 170 , xtype: 'displayfield' , y: 10},
				{id: 'password', name: 'password', inputType:'password',value: record.get('password'), x: 170 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['email'] , x: 330 , xtype: 'displayfield' , y: 10},
				{id: 'email', name: 'email', value: record.get('email'), x: 330 , xtype: 'textfield' , width: 205 , y: 30},
				{value: _label['language'] , x: 545 , xtype: 'displayfield' , y: 10},
				{displayField:'name' , forceSelection: true , id: 'idLanguage', name: 'idLanguage', queryMode: 'local' , value:'en', store: storeLanguage , triggerAction: 'all', valueField: 'idLanguage' , x: 545 , xtype: 'combo', width: 100 , y: 30},			
				{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 75},
				{id: 'name', name: 'name', value: record.get('name'), x: 10 , xtype: 'textfield' , width: 150 , y: 95},
				{value: _label['surname'] , x: 170 , xtype: 'displayfield' , y: 75},
				{id: 'surname', name: 'surname', value: record.get('surname'), x: 170 , xtype: 'textfield' , width: 150 , y: 95},
				{value:_label['dob'] , x: 330 , xtype: 'displayfield' , y: 75},
				{format: 'd/m/Y' , id: 'dob', name: 'dob', value: record.get('dateOfBirth') , x: 330 , xtype: 'datefield' , width: 150 , y: 95},
				{value: 'Alert' , x: 490 , xtype: 'displayfield' , y: 75},
				{displayField:'alertName' , forceSelection: true , id: 'idAlert', name: 'idAlert', queryMode: 'local' , value:1, store: storeAlert , triggerAction: 'all', valueField: 'idAlert' , x: 490 , xtype: 'combo', width: 150 , y: 95},
				{
					text : _message['save'],
					x : 235,
					xtype : 'button',
					width : 100,
					y : 135,
					handler : function() {
						var form = this.up('form').getForm();
				        form.submit( {
				            waitMsg: _message["waitMessage"],
				            params : {schemaType : 'validationUsers'},
							success : function(form, action) {
								record.set('userName' , Ext.getCmp('userName').getValue());
								record.set('password' , Ext.getCmp('password').getValue());
								record.set('name' , Ext.getCmp('name').getValue());
								record.set('surname' , Ext.getCmp('surname').getValue());
								record.set('email' , Ext.getCmp('email').getValue());
								record.set('dateOfBirth' , Ext.getCmp('dob').getValue());
								record.set('idRole' , 5);
								record.set('enabled',1);
								record.set('language',Ext.getCmp('idLanguage').getValue());
								record.set('idAlert',Ext.getCmp('idAlert').getValue());
								record.set('userApplications', new Array());
								record.set('userSchemas', new Array());
								if (add) {
									usersGrid.store.insert(0,record);
								}
								usersGrid.store.sync();
								Ext.getCmp('popupUser').close();
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
					y : 135,
					handler : function() {
						Ext.getCmp('popupUser').close();
					}
		        }
			]
		}],
		layout: 'absolute',
		modal: true,
		resizable: false,
	    title: _label['users'],
	    width: 665
    }).show();
};

function popupUserManage(record , add, role) {
	var storeSchemasForUser = new Ext.data.Store({
		autoSave: false,
		autoLoad: true,
		model: 'schemas',
		idProperty:'idSchema',
		proxy: {
			type: 'ajax',
			api: {
				read    : './schemasRead.json'			
			},
			reader: {
				type: 'json',
				root: 'results',
				successProperty: "success",
				messageProperty: 'message'
			}		
		},
		listeners: {
			load: function(store, records, successful,operation,eOpts){
				if(Ext.getCmp('idSchema').getValue().length>0){
					Ext.getCmp('idSchema').clearValue();
				}
				if(record.raw && record.raw.userSchemas) {
					var len = record.raw.userSchemas.length;
					//var len = record.get('userSchemas').length;
					var val = new Array();
					for(i=0;i<len;i++){
						val[i]=record.raw.userSchemas[i].idSchema;							
						//val[i]=record.get('userSchemas')[i].idSchema;
					}
					Ext.getCmp('idSchema').setValue(val);
				} else if(record.get('userSchemas').length > 0) {
					var schemaData = record.get('userSchemas');
					var schemaDataArray = String(schemaData).split(';');
					var len = schemaDataArray.length;
					if(len > 1) {
						var val = new Array();
						for(i=0;i<len;i++){
							val[i]=parseInt(schemaDataArray[i]);
						}
						Ext.getCmp('idSchema').setValue(val);	
					} else {
						Ext.getCmp('idSchema').setValue(schemaData);
					}
				}
			}
		}
	});
	var storeRoles;
	if ( role == 1) {
		storeRoles =  Ext.create('Ext.data.ArrayStore', {
			data : [[2 , _role['appManager']] , [3 , _role['operator']] , [4 , _role['dispatcher']],[5, _role['user']] ],
			fields: ['idRole' , 'name']
		});
	} else if ( role == 2) {
		storeRoles =  Ext.create('Ext.data.ArrayStore', {
			data : [[3 , 'Operator'] , [4 , 'Dispatcher']],
			fields: ['idRole' , 'name']
		});
	}
    new Ext.Window({
		height: 355,
		id: 'popupUserManage',
		bodyStyle:{"background-color":"#ffffff"},
		items: [{
		    xtype : 'form',
		    waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
				{value: _label['userName'] , x: 10 , xtype: 'displayfield' , y: 10},
				{id: 'userName', name: 'userName', value: record.get('userName'), x: 10 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['email'] , x: 180 , xtype: 'displayfield' , y: 10},
				{id: 'email', name: 'email', value: record.get('email'), x: 180 , xtype: 'textfield' , width: 190 , y: 30},
				{value: _label['language'] , x: 380 , xtype: 'displayfield' , y: 10},
				{displayField:'name' , forceSelection: true , id: 'idLanguage' , name: 'idLanguage' ,queryMode: 'local' , value:record.get('language'), store: storeLanguage , triggerAction: 'all', valueField: 'idLanguage' , x: 380 , xtype: 'combo', width: 100 , y: 30},
				{value: _label['role'] , x: 490 , xtype: 'displayfield' , y: 10},
				{displayField:'name' , forceSelection: true , id: 'idRole' , name: 'idRole' , queryMode: 'local' , value: record.get('idRole') , store: storeRoles , triggerAction: 'all', valueField: 'idRole' , x: 490 , xtype: 'combo', width: 150 , y: 30, submitValue: false,
					listeners: {
						afterrender:function(component, options){
							if (role == 1) { // Admin							
							}
							else if (role == 2) { // Application Manager
								Ext.getCmp("userName").setReadOnly(true);	
								Ext.getCmp("email").setReadOnly(true);	
								Ext.getCmp("name").setReadOnly(true);	
								Ext.getCmp("surname").setReadOnly(true);	
								Ext.getCmp("dob").setReadOnly(true);
								//Ext.getCmp("idLanguage").setReadOnly(true); //Do we need to make this field as readonly?
							}
						}		
					}
				},
				{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 75},	
				{id: 'name', name: 'name', value: record.get('name'), x: 10 , xtype: 'textfield' , width: 150 , y: 95},
				{value: _label['surname'] , x: 170 , xtype: 'displayfield' , y: 75},
				{id: 'surname', name: 'surname', value: record.get('surname'), x: 170 , xtype: 'textfield' , width: 150 , y: 95},
				{value:_label['dob'] , x: 330 , xtype: 'displayfield' , y: 75},
				{format: 'd/m/Y' , id: 'dob' , name: 'dob', value: record.get('dateOfBirth') , x: 330 , xtype: 'datefield' , width: 150 , y: 95},
				{value: 'Alert' , x: 490 , xtype: 'displayfield' , y: 75},
				{displayField:'alertName' , forceSelection: true , id: 'idAlert' , name: 'idAlert', queryMode: 'local' , value:record.get('idAlert'), store: storeAlert , triggerAction: 'all', valueField: 'idAlert' , x: 490 , xtype: 'combo', width: 150 , y: 95},
				{id: 'applicationLabel' ,value: _label['application'] , x: 10 , xtype: 'displayfield' , y: 140},
				{displayField:'name' , forceSelection: true , id: 'idApplication' , name: 'idApplication', queryMode: 'local' ,  multiSelect:true,store: storeApplications , triggerAction: 'all', valueField: 'idApplication' , x: 10 , xtype: 'combo', width: 630 , y: 160, submitValue: false,
					listeners: {
						select: function(combo, records) {
							var appIds = Ext.getCmp('idApplication').getValue();
							Ext.getCmp('idSchema').clearValue();
							storeSchemasForUser.proxy.api.read="./schemasRead.json?appIds="+appIds;
							storeSchemasForUser.load();
						},
						change:function(field, newValue,oldValue,eOpts){
							var appIds = Ext.getCmp('idApplication').getValue();
							if(appIds.length==0){
							Ext.getCmp('idSchema').clearValue();
								storeSchemasForUser.removeAll();
							}
						},
						beforerender: function( component, eOpts ){
							if(record.raw && record.raw.userApplications) {
								var len = record.raw.userApplications.length;
								var val = new Array();
								for(i=0;i<len;i++){
									val[i]=record.raw.userApplications[i].idApplication;
								}						
								Ext.getCmp('idApplication').setValue(val);
	
								var appIds = Ext.getCmp('idApplication').getValue();
								storeSchemasForUser.proxy.api.read="./schemasRead.json?appIds="+appIds;
								storeSchemasForUser.sync();
							} else if(record.get('userApplications').length > 0) {
								var appData = record.get('userApplications');
								var appDataArray = String(appData).split(",");
								var len = appDataArray.length;
								var val = new Array();
								if(len > 1) {									
									for(i=0;i<len;i++){
										val[i]=parseInt(appDataArray[i]);
									}
									Ext.getCmp('idApplication').setValue(val);
									
									var appIds = Ext.getCmp('idApplication').getValue();

									storeSchemasForUser.proxy.api.read="./schemasRead.json?appIds="+appIds;
									storeSchemasForUser.sync();
									
								} else {
									Ext.getCmp('idApplication').setValue(appData);
									storeSchemasForUser.proxy.api.read="./schemasRead.json?appIds="+appData;
									storeSchemasForUser.sync();
								}
							}
						}
					}			
				},
				{id: 'schemaLabel' ,value: _label['schema'] , x: 10 , xtype: 'displayfield' , y: 205},
				{displayField:'name' , forceSelection: true , id: 'idSchema' , name: 'idSchema', queryMode: 'local' , multiSelect:true, store: storeSchemasForUser , triggerAction: 'all', valueField: 'idSchema' , x: 10 , xtype: 'combo', width: 630 , y: 225, submitValue: false,
							
				},
				{text: _message['save'] , x: 225 , xtype: 'button' , width: 100 , y: 280 ,
					handler: function() {
					
						var form = this.up('form').getForm();
					
			    	    form.submit( {
			        	    waitMsg: _message["waitMessage"],
			            	params : {schemaType : 'validationUsers'},
							success : function(form, action) {
								record.set('userName' , Ext.getCmp('userName').getValue());
								record.set('name' , Ext.getCmp('name').getValue());
								record.set('surname' , Ext.getCmp('surname').getValue());
								record.set('email' , Ext.getCmp('email').getValue());
								record.set('idRole' , Ext.getCmp('idRole').getValue());
								record.set('dateOfBirth' , Ext.getCmp('dob').getValue());
								record.set('userApplications', Ext.getCmp('idApplication').getValue());
								record.set('userSchemas', Ext.getCmp('idSchema').getValue());
								record.set('password',record.get('password'));
								record.set('enabled',record.get('enabled'));
								record.set('createdBy',record.get('createdBy'));
								record.set('language',Ext.getCmp('idLanguage').getValue());
								record.set('idAlert',Ext.getCmp('idAlert').getValue());
								if (add) {
									usersGrid.store.insert(0,record);
								}		
								usersGrid.store.sync();
								Ext.getCmp('popupUserManage').close();
							},
							failure : function(form, action) {
								//
							}
						});	
				    }
				}, {text: _message['cancel'] , x: 330 , xtype: 'button' , width: 100 , y: 280 ,
					handler: function() {
						Ext.getCmp('popupUserManage').close();
					}	
				}]
			}],
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: _label['users'],
		    width: 665
	    }).show();
};
var prevTheme = '';
var currentTheme = '';
function popupModifyProfile(record , add){
	prevTheme = record.get('theme');
	new Ext.Window({
		height: 215,
		id: 'popupModifyProfile',
		bodyStyle:{"background-color":"#ffffff"},
		items: [{
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
				{value: _label['userName'] , x: 10 ,xtype: 'displayfield' , y: 10},
				{id: 'userName', name: 'userName', value: record.get('userName'), x: 10 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['password'] , x: 170 , xtype: 'displayfield' , y: 10},
				{id: 'password', name: 'password', inputType:'password',value: record.get('password'), x: 170 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['email'] , x: 330 , xtype: 'displayfield' , y: 10},
				{id: 'email', name: 'email', value: record.get('email'), x: 330 , xtype: 'textfield' , width: 260 , y: 30},
				{value: _label['language'] , x: 600 , xtype: 'displayfield' , y: 10},
				{displayField:'name' , forceSelection: true , id: 'idLanguage' , name: 'idLanguage' ,queryMode: 'local' , value:record.get('language'), store: storeLanguage , triggerAction: 'all', valueField: 'idLanguage' , x: 600 , xtype: 'combo', width: 100 , y: 30},			
				{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 75},
				{id: 'name', name: 'name', value: record.get('name'), x: 10 , xtype: 'textfield' , width: 150 , y: 95},
				{value: _label['surname'] , x: 170 , xtype: 'displayfield' , y: 75},
				{id: 'surname', name: 'surname', value: record.get('surname'), x: 170 , xtype: 'textfield' , width: 150 , y: 95},
				{value:_label['dob'] , x: 330 , xtype: 'displayfield' , y: 75},
				{format: 'd/m/Y' , id: 'dob' , name: 'dob' ,value: record.get('dateOfBirth') , x: 330 , xtype: 'datefield' , width: 100 , y: 95},
				{value: 'Alert' , x: 440 , xtype: 'displayfield' , y: 75},
				{displayField:'alertName' , forceSelection: true , id: 'idAlert' , name: 'idAlert' ,queryMode: 'local' , value:record.get('idAlert'), store: storeAlert , triggerAction: 'all', valueField: 'idAlert' , x: 440 , xtype: 'combo', width: 150 , y: 95},
				{value: 'Theme' , x: 600 , xtype: 'displayfield' , y: 75},
				{displayField:'name' , forceSelection: true , id: 'idTheme' , name: 'idTheme' ,queryMode: 'local' , value:record.get('theme') == null ? 'neptune' : record.get('theme'), store: storeTheme , valueField: 'idTheme', triggerAction: 'all', x: 600 , xtype: 'combo', width: 100 , y: 95, submitValue: false},			
				{text: _message['save'] , x: 250 , xtype: 'button' , width: 100 , y: 140 ,
					handler: function() {					
						var form = this.up('form').getForm();
						
				        form.submit( {
				            waitMsg: _message["waitMessage"],
				            params : {schemaType : 'validationUsers'},
							success : function(form, action) {
								
								record.set('userName' , Ext.getCmp('userName').getValue());
								record.set('password' , Ext.getCmp('password').getValue());
								record.set('name' , Ext.getCmp('name').getValue());
								record.set('surname' , Ext.getCmp('surname').getValue());
								record.set('email' , Ext.getCmp('email').getValue());
								record.set('dateOfBirth' , Ext.getCmp('dob').getValue());
								record.set('idRole' , record.get('idRole'));
								record.set('enabled',record.get('enabled'));
								record.set('createdBy',record.get('createdBy'));
								record.set('language',Ext.getCmp('idLanguage').getValue());
								record.set('idAlert',Ext.getCmp('idAlert').getValue());
								record.set('theme',Ext.getCmp('idTheme').getValue());
								if (add) {
									usersGrid.store.insert(0,record);
								}
								usersGrid.store.sync();
								currentTheme = record.get('theme');
								Ext.getCmp('popupModifyProfile').close();
							},
							failure : function(form, action) {
								//
							}
						});
			    	}
				},
				{text: _message['cancel'], x: 355 , xtype: 'button' , width: 100 , y: 140 ,
					handler: function() {
						Ext.getCmp('popupModifyProfile').close();
					}
				}]
			}],
			layout: 'absolute',
			modal: true,
			resizable: false,
	    	title: 'Modify Profile',
		    width: 730
   	}).show();
}
function popupUserHelp(){
	if (Ext.getCmp('popupUserHelp')) return;	
	new Ext.Window({
		height: 250,
		id: 'popupUserHelp',
		layout: 'absolute',
		modal: true,
		resizable: false,
		bodyStyle:'padding:10px;',
	    title: _message['help'],	 
        html: _message['userHelpMessage'],        
	    items:[
	         {text: _message['ok'] , xtype: 'button' , width: 100, x:280, y:180,
			handler: function() {
				Ext.getCmp('popupUserHelp').close();
			}
	    }],
	    width: 665
    }).show(this);
};