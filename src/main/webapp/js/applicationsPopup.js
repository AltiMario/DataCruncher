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

function popupApplication(record, add) {
	if (add) {
		scheduleDataSource.schedulerStore.load();
	}
	new Ext.Window(
			{
				height : 365,
				id : 'popupApplication',
				bodyStyle:{"background-color":"#ffffff"},
				items : [ {
					xtype : 'form',
					waitMsgTarget: true,
					layout : 'absolute',
					frame : false,
					border : false,
					url : 'controller.validateForm.json',
					items: [
						{
							value : _label['name'],
							x : 10,
							xtype : 'displayfield',
							y : 10
						},
						{
							id : 'name',
							name : 'name',
							value : record.get('name'),
							x : 10,
							xtype : 'textfield',
							width : 150,
							y : 30
						},
						{
							value : _label['startDate'],
							x : 170,
							xtype : 'displayfield',
							y : 10
						},
						{
							format : 'd/m/Y',
							id : 'startDate',
							name : 'startDate',
							value : record.get('startDate'),
							x : 170,
							xtype : 'datefield',
							width : 150,
							y : 30
						},
						{
							value : _label['endDate'],
							x : 330,
							xtype : 'displayfield',
							y : 10
						},
						{
							format : 'd/m/Y',
							id : 'endDate',
							name : 'endDate',
							value : record.get('endDate'),
							x : 330,
							xtype : 'datefield',
							width : 150,
							y : 30
						},
						{
							value : _label['planned'],
							x : 490,
							xtype : 'displayfield',
							y : 10
						},
						{
							xtype : 'checkboxfield',
							id : 'idPlannedCheckBox',
							inputValue : true,
							submitValue : false,
							uncheckedValue : false,
							x : 490,
							y : 30,
							name : 'plannedCheckBox',
							checked : record.get('isPlanned'),
							disabled : scheduleDataSource.schedulerStore
									.getCount() > 0 ? false : true,
							listeners : {
								change : function(checkbox, newValue) {
									newValue ? Ext.getCmp('plannedName').enable() : Ext
											.getCmp('plannedName').disable();
								},
								afterrender : function() {
									scheduleDataSource.schedulerStore.load();
									this.checked ? Ext.getCmp('plannedName').enable()
											: Ext.getCmp('plannedName').disable();
								}
							}
						},
						{
							displayField : 'name',
							id : 'plannedName',
							name: 'plannedName',
							queryMode : 'local',
							value : record.get('plannedName'),
							value : record.get('plannedName') <= 0 ? ''
									: record.get('plannedName'),
							store : scheduleDataSource.schedulerStore,
							triggerAction : 'all',
							valueField : 'id',
							x : 510,
							xtype : 'combo',
							width : 100,
							y : 30

						},

						{
							value : _label['active'],
							x : 620,
							xtype : 'displayfield',
							y : 10
						},

						{

							xtype : 'checkboxfield',
							id : 'idActiveCheckBox',
							inputValue : true,
							submitValue : false,
							uncheckedValue : false,
							x : 620,
							y : 30,
							name : 'activeCheckBox',
							checked : record.get('isActive')

						},

						{
							value : _label['description'],
							x : 10,
							xtype : 'displayfield',
							y : 55
						},
						{
							height : 200,
							id : 'description',
							name : 'description',
							submitValue : false,
							value : record.get('description'),
							x : 10,
							xtype : 'htmleditor',
							width : 660,
							y : 75
						},
						{
							text : _message['save'],
							x : 225,
							xtype : 'button',
							width : 100,
							y : 290,
							handler : function() {
								
								var form = this.up('form').getForm();
								
						        form.submit( {
						            waitMsg: _message["waitMessage"],
						            params : {schemaType : 'validationApplications'},
									success : function(form, action) {										
									
										record.set('name', Ext.getCmp('name').getValue());
										record.set('description', Ext.getCmp('description').getValue());
										record.set('startDate', Ext.getCmp('startDate').getValue());
										record.set('endDate', Ext.getCmp('endDate').getValue());
										if (Ext.getCmp('idPlannedCheckBox').getValue()) {
											record.set('isPlanned', true);
											record.set('plannedName', Ext.getCmp('plannedName').getValue());
										} else {
											record.set('isPlanned', false);
											record.set('plannedName', -1);
										}

										if (Ext.getCmp('idActiveCheckBox').getValue()) {
											record.set('isActive', 1);
										} else {
											record.set('isActive', 0);
										}
										record.set('isSiteGenerated', false);
										if (add) {
											applicationsGrid.store.insert(0, record);
										}
										applicationsGrid.store.sync();
										Ext.getCmp('popupApplication').close();
									},
									failure : function(form, action) {
										//
									}
								});
						    }
						}, {
							text : _message['cancel'],
							x : 330,
							xtype : 'button',
							width : 100,
							y : 290,
							handler : function() {
								Ext.getCmp('popupApplication').close();
							}
						}]
				}],
				layout : 'absolute',
				modal : true,
				resizable : false,
				title : _label['application'],
				width : 690
			}).show();
};

function popupApplicationHelp() {
	if (Ext.getCmp('popupApplicationHelp'))
		return;
	new Ext.Window( {
		height : 250,
		id : 'popupApplicationHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['applicationHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupApplicationHelp').close();
			}
		} ],
		width : 665
	}).show(this);

};