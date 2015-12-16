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

    /*Ext.define('example.fielderror', {
        extend: 'Ext.data.Model',
        fields: ['id', 'msg']
    });*/

function popupDatabase(record , add) {
    new Ext.Window({
		height: 405,
		id: 'popupDatabase',
		bodyStyle:{"background-color":"#ffffff"},
		items : [ {
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [
				{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 10},
				{id: 'name', name : 'name', value: record.get('name'), x: 10 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['databaseType'] , x: 170 , xtype: 'displayfield' , y: 10},
				{displayField: 'name' , id: 'idDatabaseType', name : 'idDatabaseType', mode: 'local' , value: record.get('idDatabaseType') , store: storeDatabaseType , triggerAction: 'all', valueField: 'idDatabaseType' , x: 170 , xtype: 'combo', width: 150 , y: 30},
				{value: _label['host'] , x: 330 , xtype: 'displayfield' , y: 10},
				{id: 'host', name : 'host', value: record.get('host') , x: 330 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['port'] , x: 490 , xtype: 'displayfield' , y: 10},
				{id: 'port', name: 'port', value: record.get('port') , x: 490 , xtype: 'textfield' , width: 150 , y: 30},
				{value: _label['databaseName'] , x: 10 , xtype: 'displayfield' , y: 55},
				{id: 'databaseName', name: 'databaseName',  value: record.get('databaseName') , x: 10 , xtype: 'textfield' , width: 150 , y: 75},
				{value: _label['userName'] , x: 170 , xtype: 'displayfield' , y: 55},
				{id: 'userName', name : 'userName', value: record.get('userName') , x: 170 , xtype: 'textfield' , width: 150 , y: 75},
				{value: _label['password'] , x: 330 , xtype: 'displayfield' , y: 55},
				{id: 'password' , inputType: 'password' , submitValue : false, value: record.get('password') , x: 330 , xtype: 'textfield' , width: 150 , y: 75},
				{value: _label['description']  , x: 10 , xtype: 'displayfield' , y: 100},
				{height: 200 , id:'description', submitValue : false,  value: record.get('description') , x: 10 , xtype: 'htmleditor' , width: 630 , y: 120},
				{
					text : _message['save'],
					x : 220,
					y : 330,
					xtype : 'button',
					width : 100,
					handler : function() {
						var form = this.up('form').getForm();
			            form.submit( {
			            	waitMsg: _message["waitMessage"],
			            	params : {schemaType : 'validationDb'},
							success : function(form, action) {
								record.set('name' , Ext.getCmp('name').getValue());
								record.set('description' , Ext.getCmp('description').getValue());
								record.set('idDatabaseType' , Ext.getCmp('idDatabaseType').getValue());
								record.set('host' , Ext.getCmp('host').getValue());
								record.set('port' , Ext.getCmp('port').getValue());
								record.set('databaseName' , Ext.getCmp('databaseName').getValue());
								record.set('userName' , Ext.getCmp('userName').getValue());
								record.set('password' , Ext.getCmp('password').getValue());
								record.set('status',"<img src='./images/ajax-loader.gif' />");
								if (add) {
									databasesGrid.store.insert(0 , record);
								}
								databasesGrid.store.sync({
									success : function() {
										databasesGrid.store.load();
									}
								});
								Ext.getCmp('popupDatabase').close();
							},
							failure : function(form, action) {
								//
							}
						});
					}
				},
				{text: _message['cancel'] , x: 325 , xtype: 'button' , width: 100 , y: 330 ,
					handler: function(){
						Ext.getCmp('popupDatabase').close();
					}
				}
			]}
		],
		layout: 'absolute',
		modal: true,
		resizable: false,
	    title: _label['database'],
	    width: 665
    }).show();
};

function popupDatabaseHelp(){
	if (Ext.getCmp('popupDatabaseHelp')) return;	
	new Ext.Window({
		height: 250,
		id: 'popupDatabaseHelp',
		layout: 'absolute',
		modal: true,
		resizable: false,
		bodyStyle:'padding:10px;',
	    title: _message['help'],	 
        html: _message['databaseHelpMessage'],        
	    items:[
	         {text: _message['ok'] , xtype: 'button' , width: 100, x:280, y:180,
			handler: function() {
				Ext.getCmp('popupDatabaseHelp').close();
			}
	    }],
	    width: 665
    }).show(this);
};