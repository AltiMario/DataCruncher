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

function popupUploadDocSchema(record) {
	var uploadContent = {
	        xtype: 'form',
	        id: 'uploadForm',
	        fileUpload: true,
	        frame: false,
	        border: false,
	        labelWidth: 50,
	        items: [
				{name: 'file' , fieldLabel: 'File' , id: 'form-file' , emptyText: _label['selectImportFile'], xtype: 'fileuploadfield' ,
					listeners:{
						change: function(obj, filename, options){
								this.fireEvent('upload',  filename);
								Ext.getCmp("btnValidate").enable();
						}
					},
					width: 475},
				{ xtype: 'textfield',
				     id: 'description',
				     name: 'description',
				     fieldLabel: _label['description'],
				     width: 475
				   }
			
				
				]
	    };
	     
		new Ext.Window({
			id: 'popupUploadDocSchema',
			items: [
				uploadContent,
				{id: 'btnValidate',
				  name: 'btnValidate',disabled: true,
				  text: _message["upload"], x: 135 , xtype: 'button' , width: 100 , y: 70 ,
					handler: function(){
					    var form = Ext.getCmp('uploadForm').getForm();
						if (form.isValid()) {
							form.submit({
								url: './upload.json?action=uploadFile&idSchema=' + record.get('idSchema'),
		                        waitMsg: _message["loadTheFile"],
								success: function(form, action) {
									Ext.getCmp('docSchemaGrid').getStore().load({params:{start:0 , limit:18}});
                                    App.setAlert(true , _message['fileUploaded']);
									Ext.getCmp('popupUploadDocSchema').close();
								},
								failure: function(form, action) {
									App.setAlert(_message["fileNotUploaded"],action.result.message);
									Ext.getCmp('popupUploadDocSchema').close();
								}
							});
						}
					}
				},
				{text: _message["close"] , x: 250 , xtype: 'button' , width: 100 , y: 70 ,
		        	handler: function(){
		        		Ext.getCmp('popupUploadDocSchema').close();
		        	}
		        }
			],
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: _message['uploadFile'],
		    width: 500,
		    height: 140
	    }).show();
	};