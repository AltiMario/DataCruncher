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

DataCruncher.views.LoginForm = Ext.extend(Ext.form.FormPanel, {
    initComponent : function(){
        var config = {
			layout: 'fit',
			fullscreen: true,
			scroll: 'vertical',
			dockedItems: [
				{
					dock: 'top',
					xtype: 'toolbar',
					title: 'DataCruncher',
					type: 'light'
				},
				{
					dock: 'bottom',
					xtype: 'toolbar',
					type: 'light',
					items: [
						{
							xtype: 'spacer'
						},
						{
                            text: 'Reset',
							ui: 'confirm',
                            handler: this.onResetAction,
                        },
						{
							text: 'Submit',
							ui: 'confirm',
							handler: this.onSubmitAction,
							scope: this
						}
					]
				}
			],

			items: [
				{
					xtype: 'fieldset',
					title: 'Login',
					instructions: 'Please enter the information above.',
					defaults: {
						required: true,
						labelAlign: 'left',
						labelWidth: '40%'
					},
					items: [{
                        xtype: 'textfield',
                        name : 'name',
                        label: 'Name',
                        useClearIcon: true,
                        autoCapitalize : false
					}, {
                        xtype: 'passwordfield',
                        name : 'password',
                        label: 'Password',
                        useClearIcon: false
                    }]
				}
			]
		};	
        
        Ext.apply(this, config);
        DataCruncher.views.LoginForm.superclass.initComponent.call(this);
    },
	onSubmitAction: function() {
		Ext.Ajax.request({
			url: DataCruncher.util.actions.loginUrl,//'../login.json',
			params: {
				userName: this.getValues().name,
				password: this.getValues().password
			},
			success: function (result) {
				if("invalid"==result.responseText){
					Ext.Msg.alert('Warning', _error['invalidCredentials']); 							
				}else if("notEnabled"==result.responseText){
					Ext.Msg.alert('Warning',  _error['userNotEnabled']); 						
				}else{
					DataCruncher.views.loginForm.reset();
					localStorage.setItem("loginstatus", true);
					Ext.dispatch({
						controller: 'DataCruncher',
						action    : 'schemaList',
						historyUrl: "DataCruncher/schemaList"		
					});					
				}
			},
			failure: function(resuilt){
			}
		});		
	},
	onResetAction: function() {
		DataCruncher.views.loginForm.reset();
	}
});