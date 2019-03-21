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

Ext.define('sendStreamDynamicModel', {
    extend: 'Ext.data.Model',
	fields: [ ]	// are defined in the JSON response metaData
});

function sendGenerationStreamPopup(record, query) {
	var store = Ext.create('Ext.data.JsonStore', {
        autoLoad: false,
		autoSync: false,
        model: 'sendStreamDynamicModel',
        proxy: {
            type: 'ajax',
            url: './schemaLinkedFields.json?action=dynamicGrid&idSchema=' + record.get('idSchema') +
            	(query == undefined ? '' : '&query=' + query)
        }
	});	
    var grid = Ext.create('Ext.grid.Panel', {
		autoRender: true,
        store: store,
        //fake columns, they will be removed after server response
        columns: [
                  { text: 'Name',  dataIndex: 'name' },
                  { text: 'Email', dataIndex: 'email', flex: 1 },
                  { text: 'Phone', dataIndex: 'phone' }
              ],
		//forceFit: true,
        //autoScroll: true,
		hidden: true,
        /*columns: {
			defaults: {
				field: {
					xtype: 'textfield'
				}
			}
		}*/
    });
    var noValuesLabel = Ext.create('Ext.form.Label', {
		text: _message['noValuesForDisplay'],
		hidden: true,
		margin: '5'
    }); 	
	new Ext.Window({ 
		height: 450,
		width: 700,
		layout: 'fit',
		modal: true,
		resizable: false,
		title: _message['sendStream'],
		buttonAlign: 'center',
		items: [
			grid,
			noValuesLabel
		],
		buttons: [
            {text: _label['sendValidation'], xtype: 'button', width: 100,
                handler: function() {
                    if (store.data.length == 0) {
                        callAlert(_alert['noStreamData']);
                        return;
                    }
                    Ext.MessageBox.show({
                        msg: 'Validating your stream, please wait...',
                        progressText: 'Validating...',
                        width:300,
                        wait:true,
                        waitConfig: {interval:200}
                    });
					Ext.Ajax.timeout = 300000; //5 minutes
                    Ext.Ajax.request({
                        success: function (response, opts) {
                            Ext.MessageBox.hide();
                            App.setAlert(_message["response"] , response.responseText);
                        },
                        url: './schemaLinkedFields.json?action=validateStream&idSchema=' + record.get('idSchema') +
                            (query == undefined ? '' : '&query=' + query)
                    });
					Ext.Ajax.timeout = globalVars.defaultTimeout; //back to standart request timeout 60 sec
                }
            },
            {text: _message['save'], xtype: 'button', width: 100,
                handler: function() {
                    if (store.data.length == 0) {
                        callAlert(_alert['noStreamData']);
                        return;
                    }
                    Ext.MessageBox.show({
                        msg: 'Creating your file, please wait...',
                        progressText: 'Creating...',
                        width:300,
                        wait:true,
                        waitConfig: {interval:200}
                    });
                    Ext.Ajax.request( {
                        params : {
                            idSchema : record.get('idSchema'),
                            query : query == undefined ? '' : query
                        },
                        success: function (result) {
                            Ext.MessageBox.hide();
                            var recordResult = Ext.JSON.decode(result.responseText);
                            if (eval(recordResult.success)) {
                                var iFrame = document.createElement("iframe");
                                iFrame.src = './schemaLinkedFields.json?action=downloadStream&idSchema=' + record.get('idSchema');
                                iFrame.style.display = "none";
                                document.body.appendChild(iFrame);
                            } else {
                                App.setAlert(App.STATUS_ERROR, recordResult.responseMsg);
                            }
                        },
                        url : './schemaLinkedFields.json?action=checkStream'
                    });
                }
            },
			{text: _message['cancel'], xtype: 'button', width: 100, 
				handler: function() {
					this.ownerCt.ownerCt.close();
				}
			}
		]
	}).show(); 
	
	store.load({
		callback : function(r, options, success){
			if (eval(success)) {
				grid.show();
				grid.reconfigure(store, r[0].stores[0].proxy.reader.metaData.fields);
			} else {
				noValuesLabel.show();
			}
		}
	}); 
	
	var mask = new Ext.LoadMask(Ext.getBody(), 
		{	msg : _message['waitMessage'], 
			store : store
		}
	);
	mask.show();
};