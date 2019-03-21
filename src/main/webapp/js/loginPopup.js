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
var role;
var roleActivities;
var userId;
Ext.QuickTips.init();

var App = new Ext.App();
var storeDefaultDatabaseType = new Ext.data.ArrayStore({
	data : [ [ '1', 'MySql' ], [ '2', 'Oracle' ], [ '3', 'SQL Server' ],
			[ '4', 'PostgreSQL' ], [ '5', 'DB2' ], [ '6', 'SQLite' ],
			[ '7', 'Firebird' ], [ '8', 'SAPDB' ], [ '9', 'HSQLDB' ] ],
	fields : [ 'idDatabaseType', 'name' ]
});

function popupLogin() {

	new Ext.Window(
			{
				height : 200,
				id : 'popupLogin',
				items : [
						{
							value : _label['userName'],
							x : 5,
							xtype : 'displayfield',
							y : 5
						},
						{
							id : 'loginUserName',
							x : 5,
							xtype : 'textfield',
							width : 150,
							y : 25
						},
						{
							value : _label['password'],
							x : 165,
							xtype : 'displayfield',
							y : 5
						},
						{
							id : 'loginPassword',
							inputType : 'password',
							x : 165,
							xtype : 'textfield',
							width : 150,
							y : 25
						},

						{
							xtype : 'button',
							x : 10,
							y : 70,
							text : _label['forgetPassword'],
							border : 0,
							tabIndex : -1,
							handler : function() {
								popupForgetPassword();
							}
						},
						{
							xtype : 'button',
							x : 170,
							y : 70,
							text : _label['register'],
							border : 0,
							tabIndex : -1,
							handler : function() {
								popupUserRegistration();
							}
						},

						{
							text : _message['ok'],
							x : 100,
							xtype : 'button',
							width : 100,
							y : 110,
							listeners : {
								click : function() {
									Ext.Ajax
											.request({
												params : {
													userName : Ext.getCmp(
															'loginUserName')
															.getValue(),
													password : Ext.getCmp(
															'loginPassword')
															.getValue()
												},
												success : function(result) {
													if ("invalid" == result.responseText) {
														App
																.setAlert(
																		false,
																		_error['invalidCredentials']);
													} else if ("notEnabled" == result.responseText) {
														App
																.setAlert(
																		false,
																		_error['userNotEnabled']);
													} else {
														var r = result.responseText;
														var responseObj = Ext
																.decode(r);
														role = responseObj.idRole;
														roleActivities = responseObj.roleActivities
																.toString();
														userId = responseObj.idUser;
														window.location = 'main.jsp';
													}
												},
												url : './login.json'
											});
								}
							}
						} ],
				layout : 'absolute',
				modal : false,
				resizable : false,
				closable : false,
				title : _label['login'],
				width : 335
			}).show();
}

function popupDBConfig() {

	var uploadContent = {
		xtype : 'form',
		id : 'defaultDBForm',
		layout : 'absolute',
		fileUpload : true,
		frame : false,
		labelWidth : 50,
		height : 220,
		width : 520,
		items : [ {
			id : 'configType',
			name : 'configType',
			value : 'database',
			xtype : 'hidden'
		}, {
			value : _label['databaseType'],
			x : 10,
			xtype : 'displayfield',
			y : 10
		}, {
			displayField : 'name',
			id : 'idDatabaseType',
			name : 'idDatabaseType',
			mode : 'local',
			store : storeDefaultDatabaseType,
			value : '1',
			triggerAction : 'all',
			valueField : 'idDatabaseType',
			x : 10,
			xtype : 'combo',
			width : 150,
			y : 30,
			disabled : true
		}, {
			value : _label['host'],
			x : 170,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'host',
			name : 'host',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 30,
			value : '127.0.0.1',
			disabled : true
		}, {
			value : _label['port'],
			x : 330,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'port',
			name : 'port',
			x : 330,
			xtype : 'textfield',
			width : 150,
			y : 30,
			value : '3306',
			disabled : true
		}, {
			value : _label['databaseName'],
			x : 10,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'databaseName',
			name : 'databaseName',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 95,
			value : 'datacruncher',
			disabled : true
		}, {
			value : _label['userName'],
			x : 170,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'userName',
			name : 'userName',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 95,
			value : 'root',
			disabled : true
		}, {
			value : _label['password'],
			x : 330,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'password',
			name : 'password',
			inputType : 'password',
			x : 330,
			xtype : 'textfield',
			width : 150,
			y : 95,
			value : 'root',
			disabled : true
		} ]
	};

	new Ext.Window({
		height : 230,
		id : 'popupDefaultDB',
		items : [ uploadContent, {
			value : _message["persistenceMsg"],
			x : 20,
			xtype : 'displayfield',
			y : 130
		}, {
			text : _label["continue"],
			x : 135,
			xtype : 'button',
			width : 100,
			y : 160,
			handler : function() {
				var form = Ext.getCmp('defaultDBForm').getForm();

				if (form.isValid()) {
					Ext.getCmp('popupDefaultDB').close();
					popupUserProfile();
					// To save form data need to uncomment
					/*
					 * form.submit({ url: './appConfigCreate.json', success:
					 * function(form, action) { App.setAlert(true ,
					 * action.result.message); if(action.result.success) {
					 * Ext.getCmp('popupDefaultDB').close(); popupUserProfile(); } },
					 * failure: function(form, action) {
					 * App.setAlert(_message["error"], action.result.message); }
					 * });
					 */
				}
			}
		}, {
			text : _message["close"],
			x : 270,
			xtype : 'button',
			width : 100,
			y : 160,
			handler : function() {
				Ext.getCmp('popupDefaultDB').close();
			}
		} ],
		layout : 'absolute',
		modal : true,
		resizable : false,
		title : _label['defaultDatabase'],
		width : 520
	}).show();
}

function popupFTPConfig(add) {

	var ftpContent = {
		xtype : 'form',
		id : 'ftpForm',
		layout : 'absolute',
		frame : false,
		labelWidth : 50,
		height : 510,
		width : 350,
		items : [ {
			id : 'configType',
			name : 'configType',
			value : 'ftp',
			xtype : 'hidden'
		}, {
			id : 'idApplicationConfig',
			name : 'idApplicationConfig',
			xtype : 'hidden'
		}, {
			value : _label['userName'],
			x : 10,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'userName',
			name : 'userName',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 30
		}, {
			value : _label['password'],
			x : 170,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'password',
			name : 'password',
			inputType : 'password',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 30
		}, {
			value : _label['inputDir'],
			x : 10,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'inputDirectory',
			name : 'inputDirectory',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 95
		}, {
			value : _label['outputDir'],
			x : 170,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'outputDirectory',
			name : 'outputDirectory',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 95
		}, {
			value : _label['serverPort'],
			x : 10,
			xtype : 'displayfield',
			y : 140
		}, {
			id : 'serverPort',
			name : 'serverPort',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 160
		} ]
	};

	new Ext.Window({
		height : 275,
		id : 'popupFTPConfig',
		items : [
				ftpContent,
				{
					text : _message['save'],
					x : 115,
					xtype : 'button',
					width : 100,
					y : 205,
					handler : function() {
						var form = Ext.getCmp('ftpForm').getForm();
						var formSubmitURL = './appConfigCreate.json';

						if (Ext.getCmp('idApplicationConfig').value > 0) {
							formSubmitURL = './appConfigUpdate.json';
						}

						if (form.isValid()) {
							form.submit({
								url : formSubmitURL,
								success : function(form, action) {
									App.setAlert(true, action.result.message);
									Ext.getCmp('popupFTPConfig').close();
									if (add) {
										popupLogin();
									}
								},
								failure : function(form, action) {
									App.setAlert(_message["error"],
											action.result.message);
								}
							});
						}
					}
				} ],
		layout : 'absolute',
		modal : true,
		resizable : false,
		closable : add == true ? false : true,
		title : _label['ftpConfig'],
		width : 350
	});

	if (add) {
		Ext.getCmp('userName').setValue('admin');
		Ext.getCmp('password').setValue('admin');
		Ext.getCmp('inputDirectory').setValue('ftp_storage_dir/inputdir');
		Ext.getCmp('outputDirectory').setValue('ftp_storage_dir/outputdir');
		Ext.getCmp('serverPort').setValue('21');
		Ext.getCmp('popupFTPConfig').show();
	} else {
		Ext.Ajax.request({
			url : './appConfigRead.json',
			params : {
				configType : 'ftp'
			},
			disableCaching : false,
			success : function(result, request) {
				var response = Ext.decode(result.responseText);
				if (response.success == true) {
					Ext.getCmp('idApplicationConfig').setValue(
							response.results[0].idApplicationConfig);
					Ext.getCmp('userName').setValue(
							response.results[0].userName);
					Ext.getCmp('password').setValue(
							response.results[0].password);
					Ext.getCmp('inputDirectory').setValue(
							response.results[0].inputDir);
					Ext.getCmp('outputDirectory').setValue(
							response.results[0].outputDir);
					Ext.getCmp('serverPort').setValue(
							response.results[0].serverPort);
				}
				Ext.getCmp('popupFTPConfig').show();
			},
			failure : function() {
				alert("Failure");
			}
		});
	}
}

function popupUserProfile() {

	var userProfile = {
		xtype : 'form',
		id : 'userProfileForm',
		layout : 'absolute',
		frame : false,
		labelWidth : 50,
		height : 210,
		width : 665,
		items : [ {
			id : 'configType',
			name : 'configType',
			value : 'userProfile',
			xtype : 'hidden'
		}, {
			value : _label['userName'],
			x : 10,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'userName',
			name : 'userName',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 30,
			value : 'admin'
		}, {
			value : _label['password'],
			x : 170,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'password',
			name : 'password',
			inputType : 'password',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 30,
			value : 'admin'
		}, {
			value : _label['email'],
			x : 330,
			xtype : 'displayfield',
			y : 10
		}, {
			id : 'email',
			name : 'email',
			x : 330,
			xtype : 'textfield',
			width : 205,
			y : 30,
			value : 'altimario@gmail.com'
		}, {
			value : _label['language'],
			x : 545,
			xtype : 'displayfield',
			y : 10
		}, {
			displayField : 'name',
			forceSelection : true,
			id : 'language',
			name : 'language',
			queryMode : 'local',
			value : 'en',
			store : storeLanguage,
			triggerAction : 'all',
			valueField : 'idLanguage',
			x : 545,
			xtype : 'combo',
			width : 100,
			y : 30
		}, {
			value : _label['name'],
			x : 10,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'name',
			name : 'name',
			x : 10,
			xtype : 'textfield',
			width : 150,
			y : 95,
			value : 'Administrator'
		}, {
			value : _label['surname'],
			x : 170,
			xtype : 'displayfield',
			y : 75
		}, {
			id : 'surname',
			name : 'surname',
			x : 170,
			xtype : 'textfield',
			width : 150,
			y : 95,
			value : 'Administrator'
		}, {
			value : _label['dob'],
			x : 330,
			xtype : 'displayfield',
			y : 75
		}, {
			format : 'd/m/Y',
			id : 'dob',
			name : 'dob',
			x : 330,
			xtype : 'datefield',
			width : 150,
			y : 95
		}, {
			value : 'Alert',
			x : 490,
			xtype : 'displayfield',
			y : 75
		}, {
			displayField : 'alertName',
			forceSelection : true,
			id : 'idAlert',
			name : 'idAlert',
			queryMode : 'local',
			value : 1,
			store : storeAlert,
			triggerAction : 'all',
			valueField : 'idAlert',
			x : 490,
			xtype : 'combo',
			width : 150,
			y : 95
		} ]
	};
	new Ext.Window({
		height : 215,
		id : 'popupDefaultUserProfile',
		items : [
				userProfile,
				{
					text : _message['save'],
					x : 225,
					xtype : 'button',
					width : 100,
					y : 140,
					handler : function() {

						var form = Ext.getCmp('userProfileForm').getForm();

						if (form.isValid()) {

							form.submit({
								url : './appConfigCreate.json',
								success : function(form, action) {
									App.setAlert(true, action.result.message);
									Ext.getCmp('popupDefaultUserProfile')
											.close();
									popupFTPConfig(true);
								},
								failure : function(form, action) {
									App.setAlert(_message["error"],
											action.result.message);
								}
							});
						}
					}
				}, {
					text : _message['cancel'],
					x : 330,
					xtype : 'button',
					width : 100,
					y : 140,
					handler : function() {
						Ext.getCmp('popupDefaultUserProfile').close();
					}
				} ],
		layout : 'absolute',
		modal : true,
		resizable : false,
		title : _label['modifyProfile'],
		width : 665
	}).show();
}