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

function popupForgetPassword() {
	new Ext.Window({
		height: 190,
		fileUpload: true,
        id: 'popupForgetPassword',
		items: [
			{value: _label['userName'] , x: 5 , xtype: 'displayfield' , y: 5},
			{id: 'userName' , x: 5 , xtype: 'textfield' , width: 150 , y: 25, allowBlank:false},
			{value: _label['email'] , x: 165 , xtype: 'displayfield' , y: 5},
			{id: 'email' , x: 165 , xtype: 'textfield' , width: 150 , y: 25,allowBlank:false,vtype:'email'},
	        {value: _message['forgetPasswordMsg'] , x: 35 , xtype: 'displayfield' , y: 70},
			{text: _message['ok'] , x: 65 , xtype: 'button' , width: 100 , y: 110 ,
				handler: function() {
					Ext.Ajax.request({
	        			params: {
	        				userName: Ext.getCmp('userName').getValue(),
	        				email: Ext.getCmp('email').getValue()
	        			},
	        			success: function (result) {
	        				if("userNameRequired"==result.responseText){
								App.setAlert(false , _error['userNameRequired']);
							}else if("emailRequired"==result.responseText){
								App.setAlert(false , _error['emailRequired']);
							}else if("noRecordFound"==result.responseText){
								App.setAlert(false , _error['noRecordFound']);
							}else{
								var responseObj = Ext.decode(result.responseText);
																
								App.setAlert(responseObj.success , responseObj.message);
								if(responseObj.success) {
									Ext.getCmp('popupForgetPassword').close();
								}
							}
	        			},
	        			url: './forgetPassword.json'
	        		});					
				}
			},
			{text: _message['cancel'] , x: 175 , xtype: 'button' , width: 100 , y: 110 ,
				handler: function() {
					Ext.getCmp('popupForgetPassword').close();
				}
			}
	    ],
		layout: 'absolute',
		modal: true,
		resizable: false,
		title: _label['forgetPassword'],
		closable: false,
		width: 335
	}).show();
};