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

function popupLoadingStream(record, add) {
	
	var lspSchemaStore = new Ext.data.Store({
		autoSave : false,
		autoLoad : true,
		model : 'schemas',
		idProperty : 'idSchema',
		proxy : {
			type : 'ajax',
			api : {
				read : './schemasRead.json'
			},
			extraParams: {
	            idSchemaType: "1,2",
	            appIds: -1
	        },
			reader : {
	            type: 'json',
	            root: 'results',
				successProperty: 'success',
				messageProperty: 'message'
			}
		}
	});
	
	new Ext.Window({
		height : 520,
		width : 540,
		bodyStyle : {
			"background-color" : "#ffffff;padding:10px;"
		},
		layout : 'absolute',
		modal : true,
		resizable : false,
		title : _label['streamLoadingLabel'],
		items : [ {
			xtype : 'form',
			waitMsgTarget : true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items : [
			//first row					
			{
				xtype : 'fieldset',
				height : 80,
				width : 510,
				title : _label['base'],
				layout : 'absolute',
				items : [ {
					id : 'name',
					name : 'name',
					value : record.get('name'),
					labelAlign : 'top',
					fieldLabel : _label['name'],
					xtype : 'textfield',
					width : 150
				}, {
					displayField : 'name',
					forceSelection : true,
					id : 'idApplication',
					name : 'idApplication',
					queryMode : 'local',
					value : record.get('idApplication'),
					store : storeApplications,
					triggerAction : 'all',
					valueField : 'idApplication',
					fieldLabel : _label['application'],
					labelAlign : 'top',
					x : 160,
					xtype : 'combo',
					width : 170,
					listeners : {
						/*
						 * SM: is needed?
						 *  
						 * select : function(combo, records) {
							var appIds = Ext.getCmp('idApplication').getValue();
							var rec = storeApplications.getAt(storeApplications.find('idApplication', appIds));
							Ext.getCmp('id').clearValue();
						},*/
						change : function(field, newValue, oldValue, eOpts) {
							var appComp = Ext.getCmp('idApplication');
							if (appComp && appComp.getValue().length == 0) {
								Ext.getCmp('idSchema').clearValue();
								storeSchemasForUser.removeAll();
							}
						}
					}
				}]
			},

			//second row
			{
				xtype : 'fieldset',
				height : 80,
				width : 510,
				y : 100,
				title : _label['dataStream'],
				layout : 'absolute',
				items : [ {
					submitValue : false,
					displayField : 'name',
					forceSelection : true,
					queryMode : 'local',
					store : storeApplications,
					triggerAction : 'all',
					valueField : 'idApplication',
					fieldLabel : _label['application'],
					labelAlign : 'top',
					xtype : 'combo',
					width : 150,
					listeners : {
						select : function(combo, records) {
							var appId = combo.getValue();
							var schemasCombo = Ext.getCmp('idLinkedSchema');
							schemasCombo.clearValue();
							schemasCombo.getStore().proxy.extraParams.appIds = appId;
							//schemasCombo.getStore().proxy.api.read = "./schemasRead.json;
							schemasCombo.getStore().load();
						}
					}
				}, {
					triggerAction : 'all',
					fieldLabel : _label['customValidation'],
					value : record.get('idLinkedSchema'),
					labelAlign : 'top',
					xtype : 'combo',
					width : 170,
					x : 160,
					displayField : 'name',
					valueField : 'idSchema',
					forceSelection : true,
					id : 'idLinkedSchema',
					name : 'idLinkedSchema',
					store : lspSchemaStore
				} ]
			},

			// third row
			{
				xtype : 'fieldset',
				height : 80,
				width : 180,
				y : 190,
				title : _label['database'],
				layout : 'absolute',
				items : [ {
					displayField : 'name',
					id : 'idDatabase',
					name : 'idDatabase',
					labelAlign : 'top',
					fieldLabel : _label['outputDatabase'],
					queryMode : 'local',
					value : record.get('idDatabase') == 0 ? '' : record.get('idDatabase'),
					store : storeDatabases,
					triggerAction : 'all',
					valueField : 'idDatabase',
					xtype : 'combo',
					width : 150
				} ]
			},
			
			//fourth row
			{
				height : 150,
				id : 'description',
				name : 'description',
				submitValue : false,
				value : record.get('description'),
				xtype : 'htmleditor',
				labelAlign : 'top',
				fieldLabel : _label['description'],
				y : 280,
				enableFontSize : false,
				width : 510
			} ],
			buttons : [ {
				text : _message['save'],
				x : 150,
				y : 440,
				xtype : 'button',
				width : 100,
				handler : function() {
					var form = this.up('form').getForm();
					var win = this.ownerCt.ownerCt.ownerCt;
					if (win) {}
					form.submit({
						waitMsg : _message["waitMessage"],
						params : {
							schemaType : 'validationLoadingStream'
						},
						success : function(form, action) {
							record.set('name', Ext.getCmp('name').getValue());
							record.set('description', Ext.getCmp('description').getValue());
							record.set('idApplication', Ext.getCmp('idApplication').getValue());
							var linkedSchemaId = Ext.getCmp('idLinkedSchema').getValue();
							record.set('idLinkedSchema', linkedSchemaId);
							record.set('idDatabase', Ext.getCmp('idDatabase').getValue());
							var rec = lspSchemaStore.getAt(lspSchemaStore.find('idSchema', linkedSchemaId));
							var needToReloadFieldsTree = false;
							if (record.get('idStreamType') != rec.get('idStreamType')) {
								needToReloadFieldsTree = true;
							}
							record.set('idStreamType', rec.get('idStreamType'));
							record.set('idSchemaType', 4);
							if (add) {
								loadingStreamGrid.store.insert(0, record);
							}
							loadingStreamGrid.store.sync();
							
							if (needToReloadFieldsTree) {
								loadingFieldsTreeReload(linkedSchemaId);
							}
							win.close();
						}
					});
				}
			}, {
				text : _message['cancel'],
				x : 255,
				y : 440,
				xtype : 'button',
				width : 100,
				handler : function() {
					this.ownerCt.ownerCt.ownerCt.close();
				}
			} ]

		} ]
	}).show();
};