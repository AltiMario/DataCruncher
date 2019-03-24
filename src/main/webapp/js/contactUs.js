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

var win;
var form;
var myTask;

Ext.define('Ext.form.action.JsonSubmit', {
    extend:'Ext.form.action.Submit',
    alternateClassName: 'Ext.form.Action.JsonSubmit',
    alias: 'formaction.JsonSubmit',
    type: 'JsonSubmit',

    run : function() {

        var encodedParams = Ext.encode(form.getValues());

        var submitURL = "./contactsCreate.json";
        if(Ext.getCmp("idContact").getValue() != 0) {
            submitURL = "./contactsUpdate.json";
        }
        Ext.Ajax.request(Ext.apply(this.createCallback(form), {

            url:submitURL,
            headers: {'Content-Type': 'application/json'},
            jsonData: form.getValues(),
            success: function (result) {
                var responseObj = Ext.decode(result.responseText);
                var success = responseObj.success;
                App.setAlert(success,responseObj.message);
                myTask.hide();

                if(success) {
                    form.getForm().reset();
                    Ext.getCmp('btnSend').setDisabled(false);
                    Ext.getCmp('window').close();
                } else {
                    isEmailErrorFound = true;
                }
            },
            failure : function(result) {
                var responseObj = Ext.decode(result.responseText);
                Ext.getCmp('btnSend').setDisabled(false);
                App.setAlert(true,responseObj.message);
            }

        }));
    }
});
function initContactFormWithData(recordResult) {

    Ext.getCmp("firstName").setValue(recordResult.results.firstName);
    Ext.getCmp("lastName").setValue(recordResult.results.lastName);
    Ext.getCmp("emailID").setValue(recordResult.results.emailID);
    Ext.getCmp("companyName").setValue(recordResult.results.companyName);
    Ext.getCmp("companyWebsite").setValue(recordResult.results.companyWebsite);
    Ext.getCmp("position").setValue(recordResult.results.position);
    Ext.getCmp("msgText").setValue(recordResult.results.msgText);
    Ext.getCmp("btnSend").setDisabled(false);
    Ext.getCmp("idContact").setValue(recordResult.results.idContact);
    Ext.getCmp("isAuthorized").setValue(true);
}
function showContactForm() {

    Ext.Ajax.request( {
        url : './contactsRead.json',
        success : function(result) {
            recordResult = Ext.JSON.decode(result.responseText);
            if(isEmailErrorFound) {
                initContactFormWithData(recordResult);
            }
        }
    });
    if (!win) {
        form = Ext.widget('form', {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },

            border: false,
            bodyPadding: 10,

            fieldDefaults: {
                labelAlign: 'top',
                labelWidth: 100,
                labelStyle: 'font-weight:bold'
            },
            defaults: {
                margins: '0 0 10 0'
            },

            items: [{
                xtype: 'fieldcontainer',
                fieldLabel: '',
                layout: 'hbox',
                defaultType: 'textfield',

                fieldDefaults: {
                    labelAlign: 'top'
                },

                items: [{
                    flex: 1,
                    name: 'firstName',
                    id: 'firstName',
                    afterLabelTextTpl: required,
                    fieldLabel: _label['firstName'],
                    allowBlank: false,
                    labelStyle: 'font-weight:bold;padding:0'
                }, {
                    flex: 2,
                    name: 'lastName',
                    id: 'lastName',
                    afterLabelTextTpl: required,
                    fieldLabel: _label['lastName'],
                    allowBlank: false,
                    margins: '0 0 0 5',
                    labelStyle: 'font-weight:bold;padding:0'
                }]
            }, {
                xtype: 'fieldcontainer',
                fieldLabel: '',
                layout: 'hbox',
                defaultType: 'textfield',

                fieldDefaults: {
                    labelAlign: 'top'
                },

                items: [{
                    name: 'emailID',
                    id: 'emailID',
                    afterLabelTextTpl: required,
                    fieldLabel: _label['emailAddress'],
                    allowBlank: false,
                    labelStyle: 'font-weight:bold;padding:0',
                    vtype: 'email',
                    width: 220
                }, {
                    name: 'companyName',
                    id: 'companyName',
                    fieldLabel: _label['companyName'],
                    allowBlank: true,
                    margins: '0 0 0 5',
                    labelStyle: 'font-weight:bold;padding:0',
                    width: 140
                }]
            },{
                xtype: 'fieldcontainer',
                fieldLabel: '',
                layout: 'hbox',
                defaultType: 'textfield',

                fieldDefaults: {
                    labelAlign: 'top'
                },

                items: [{
                    name: 'companyWebsite',
                    id: 'companyWebsite',
                    fieldLabel: _label['companyWebsite'],
                    vtype: 'url',
                    allowBlank: true,
                    labelStyle: 'font-weight:bold;padding:0',
                    width: 220
                }, {
                    flex: 2,
                    name: 'position',
                    id: 'position',
                    fieldLabel: _label['position'],
                    allowBlank: true,
                    margins: '0 0 0 5',
                    labelStyle: 'font-weight:bold;padding:0',
                    width: 140
                }]
            }, {
                xtype: 'textareafield',
                afterLabelTextTpl: required,
                fieldLabel: _label['message'],
                labelAlign: 'top',
                margins: '0',
                allowBlank: false,
                name: 'msgText',
                id: 'msgText',
                height: 120
            },
                {
                    xtype: 'checkboxfield',
                    afterLabelTextTpl: required,
                    boxLabel: _label['authorize'],
                    labelAlign: 'right',
                    id: 'isAuthorized',
                    name: 'isAuthorized',
                    inputValue: 'true',
                    uncheckedValue: 'false',
                    listeners : {
                        change : function(checkbox, newValue) {
                            newValue ? Ext.getCmp('btnSend').enable() : Ext.getCmp('btnSend').disable();
                        }
                    }
                },
                {
                    xtype: 'hidden',
                    labelAlign: 'top',
                    value: 0,
                    name: 'idContact',
                    id: 'idContact'
                },
                {id: 'isShared' , name: 'isShared', xtype: 'hidden', value: 'false'},
                {id: 'idSchema' , name: 'idSchema', xtype: 'hidden', value: 0}],

            buttons: [{
                text: _message['cancel'],
                id:'btnCancel',
                handler: function() {
                    this.up('form').getForm().reset();
                    Ext.getCmp('window').close();
                }
            }, {
                text: _label['send'],
                id:'btnSend',
                disabled: true,
                handler: function() {
                    if (this.up('form').getForm().isValid()) {
                        this.up('form').getForm().findField("idSchema").setValue(0);
                        this.up('form').getForm().findField("isShared").setValue('false');
                        Ext.getCmp('btnSend').setDisabled(true);
                        myTask = new Ext.LoadMask(Ext.getBody(), {msg: _message['waitMessage']});
                        myTask.show();
                        this.up('form').getForm().doAction("JsonSubmit");
                    }
                }
            }]
        });

        win = new Ext.Window({
            title: _label['contactUs'],
            id: 'window',
            closeAction: 'hide',
            width: 400,
            height: 430,
            minHeight: 400,
            layout: 'fit',
            resizable: true,
            bodyStyle:{"background-color":"#ffffff"},
            modal: true,
            items: form
        });
    }
    win.show();
}

var btnContactus = ( {
    frame : false,
    id : 'contactus',
    xtype : 'button',
    height: 30,
    width: 100,
    text: _label['contactUs'],
    x: 0,
    y: 30,
    handler: showContactForm
});
