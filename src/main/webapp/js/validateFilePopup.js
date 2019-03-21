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

function popupValidateFile(record) {
    var uploadContent = {
        xtype: 'form',
        id: 'uploadForm',
        name: 'uploadForm',
        fileUpload: true,
        frame: false,
        border: false,
        labelWidth: 50,
        items: [
			{name: 'file' , fieldLabel: 'File' , id: 'form-file' , emptyText: _message['selectFileValidate'] , 
				xtype: 'fileuploadfield' , 
				listeners:{
					change: function(obj, filename, options){
							this.fireEvent('upload',  filename);
							Ext.getCmp("btnFileValidate").enable();
					}
				},
			width: 350}
		]
    };
     
	new Ext.Window({
		height: 115,
		id: 'popupValidateFile',
		items: [
			uploadContent,
			{id: 'btnFileValidate',
			 name: 'btnFileValidate',
			 text: _message["validate"],
			 disabled: true, x: 135 , xtype: 'button' , width: 100 , y: 45 ,
			 handler: function(){
				    var form = Ext.getCmp('uploadForm').getForm();
					if (form.isValid()) {
						form.submit({
							url: './validateFilePopup.json?idSchema=' + record.get('idSchema'),
							waitMsg: _message["validTheFile"],
							success: function(form, action) {
								App.setAlert(_message["result"] , action.result.message);
								Ext.getCmp('popupValidateFile').close();
							},
							failure: function(form, action) {
								App.setAlert(_message["error"], action.result.message);
								Ext.getCmp('popupValidateFile').close();
							}
							
						});
					}
				}
			},
			{text: _message["close"] , x: 250 , xtype: 'button' , width: 100 , y: 45 ,
	        	handler: function(){
	        		Ext.getCmp('popupValidateFile').close();
	        	}
	        }
		],
		layout: 'absolute',
		modal: true,
		resizable: false,
	    title: _message['validateFile'],
	    width: 500
    }).show();
};