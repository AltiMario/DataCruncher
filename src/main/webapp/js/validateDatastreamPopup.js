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

function popupValidateDatastream(record , actionPopup) {
	var schemaName = record.get('name');
	if (schemaName == null) {
		schemaName = "";
	}
	new Ext.Window({
		height: 470,
		fileUpload: true,
        id: 'popupValidateDatastream',
		items: [
	        {id: 'idSchema',  value: record.get('idSchema') , x: 10 , xtype: 'hidden' , width: 150 , y: 10},
	        {height: 375 , id:'datastream' , value: record.get('datastream') , x: 10 , xtype: 'textarea' , width: 725 , y: 10},
	        {text: _message["generate"] , x: 170 , xtype: 'button' , id: 'btnGenerate', width: 100 , y: 400 ,
	        	handler: function(){
	        		Ext.Ajax.request({
	        			params: {
	        				idSchema: Ext.getCmp('idSchema').getValue()
	        			},
	        			success : function(result) {
	        				var recordResult = Ext.JSON.decode(result.responseText);
	        				if (recordResult.success == true) {
	        					Ext.getCmp('datastream').setValue(recordResult.message);	
	        				}
	        				
	        			},
	        			url: './generateDataStream.json'
	        		});	        		
	        	}
	        },
	        {text: _message["validate"] , id: 'btnValidate', x: 275 , xtype: 'button' , width: 100 , y: 400 ,
	        	handler: function(){
	        		var ds = Ext.getCmp('datastream').getValue();
	        		
	        		if (ds == null || ds == '' ){
	        			App.setAlert(false , _alert['insertTrace']);
	        		}else{
	        			
		        		Ext.Ajax.request({
		        			params: {
		        				idSchema: Ext.getCmp('idSchema').getValue(),
		        				datastream: Ext.getCmp('datastream').getValue()
		        			},
		        			success: function (response, opts) {
		        				App.setAlert(_message["response"] , response.responseText);
		        			},
		        			url: './validateDatastreamPopup.json'
		        		});
	        		};
	        	}
	        },
	        {text: _message["validateFile"] , id: 'btnValidateFile', x: 380, xtype: 'button' , width: 100 , y: 400 ,
	        	handler: function(){
	        		popupValidateFile(record);
	        	}
	        },
	        {text: _message["close"] , x: 485, id: 'btnClose', xtype: 'button' , width: 100 , y: 400 ,
	        	handler: function(){
	        		Ext.getCmp('popupValidateDatastream').close();
	        	}
	        }
		],
		layout: 'absolute',
		modal: true,
		resizable: false,
		title: _message["validateDatastreamOfSchema"]+schemaName,
		width: 760
	}).show();
	
	if(!(record.get('idStreamType') == 1 || record.get('idStreamType') == 5)) {
		Ext.getCmp('btnGenerate').setVisible(false);
		Ext.getCmp('btnValidate').setPosition(210,400);
		Ext.getCmp('btnValidateFile').setPosition(315,400);
		Ext.getCmp('btnClose').setPosition(420,400);
	}
};