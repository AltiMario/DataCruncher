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

var storeLanguage = new Ext.data.ArrayStore({
	data : [['en' , _language['english']],['it' , _language['italian']],['de' , _language['german']],['ru' , _language['russian']]],
	fields: ['idLanguage' , 'name']
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
function popupUserRegistration() {
	 new Ext.Window({
		height: 285,
		id: 'popupUserRegistration',
		items: [
			{value: _label['userName'] , x: 10 , xtype: 'displayfield' , y: 10},
			{id: 'userName', x: 10 , xtype: 'textfield' , width: 150 , y: 30, allowBlank:false},
			{value: _label['password'] , x: 170 , xtype: 'displayfield' , y: 10},
			{id: 'password', inputType:'password', x: 170 , xtype: 'textfield' , width: 150 , y: 30, allowBlank:false},
			{value: _label['email'] , x: 330 , xtype: 'displayfield' , y: 10},
			{id: 'email',  x: 330 , xtype: 'textfield' , width: 200 , y: 30,vtype:'email', allowBlank:false},
			{value: _label['language'] , x: 540 , xtype: 'displayfield' , y: 10},
			{displayField:'name' , forceSelection: true , id: 'idLanguage' , queryMode: 'local' , value:'en', store: storeLanguage , triggerAction: 'all', valueField: 'idLanguage' , x: 540 , xtype: 'combo', width: 100 , y: 30},
			{value: _label['name'] , x: 10 , xtype: 'displayfield' , y: 75},
			{id: 'name',  x: 10 , xtype: 'textfield' , width: 150 , y: 95},
			{value: _label['surname'] , x: 170 , xtype: 'displayfield' , y: 75},
			{id: 'surname', x: 170 , xtype: 'textfield' , width: 150 , y: 95},
			{value:_label['dob'] , x: 330 , xtype: 'displayfield' , y: 75},
			{format: 'd/m/Y' , id: 'dob' , x:  330 , xtype: 'datefield' , width: 150 , y: 95},						
			{value: 'Alert' , x: 490 , xtype: 'displayfield' , y: 75},
			{displayField:'alertName' , forceSelection: true , id: 'idAlert' , queryMode: 'local' , value:1, store: storeAlert , triggerAction: 'all', valueField: 'idAlert' , x: 490 , xtype: 'combo', width: 150 , y: 95},
			{value: _label['securityCode'] , x: 10 , xtype: 'displayfield' , y: 150},
			{id: 'securityCode',  x: 10 , xtype: 'textfield' , width: 220 , y: 170},
			{id: 'captchaId', x: 240 ,  xtype: 'component', border:2,autoEl: {tag: 'img',src:'./captcha.json'}, width: 220 , y: 135},
			{text: _message['captchaNotClear'] , x: 470 , xtype: 'label' , y: 150, style:"textDecoration:underline",
				listeners: {
					click: {
						element: 'el', 
						fn: function(){ 
							var curr = Ext.getCmp('captchaId');
							curr.el.dom.src = './captcha.json?t='+new Date().getTime();
									
						}
					}
				}
			},
			{text: _message['save'] , x: 225 , xtype: 'button' , width: 100 , y: 210 ,
				handler: function() {
					Ext.Ajax.request({
	        			params: {
	        				userName: Ext.getCmp('userName').getValue(),
	        				password: Ext.getCmp('password').getValue(),
							name: Ext.getCmp('name').getValue(),
							surname: Ext.getCmp('surname').getValue(),
							email: Ext.getCmp('email').getValue(),
							dob: Ext.getCmp('dob').getValue(),
							language: Ext.getCmp('idLanguage').getValue(),
							captcha: Ext.getCmp('securityCode').getValue(),
							idAlert: Ext.getCmp('idAlert').getValue()
	        			},
	        			success: function (result) {
	        				var responseObj = Ext.decode(result.responseText);
							if("invalidCaptcha"==responseObj.message){
								App.setAlert(false , _error['invalidCaptcha']);
							} else if(!responseObj.success){
								App.setAlert(false , responseObj.message);
							} else{								
								Ext.getCmp('popupUserRegistration').close();								
								App.setAlert(true , responseObj.message);
							}
	        			},
	        			url: './usersCreate.json?isSelfRegister=yes'
	        		});					
				}				
			},
			{text: _message['cancel'] , x: 330 , xtype: 'button' , width: 100 , y: 210 ,
				handler: function() {
					Ext.getCmp('popupUserRegistration').close();
				}
			}
		],
		layout: 'absolute',
		modal: true,
		resizable: true,
	    title: 'Registration',
	    width: 665
    }).show();
};