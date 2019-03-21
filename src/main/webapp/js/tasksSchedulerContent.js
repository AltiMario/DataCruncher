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

schedulerDataSources = {
	minutesStore : new Ext.data.JsonStore( {
		autoSave : false,
		autoLoad : true,
		fields : [ {name : 'id', type : 'String'}, {name : 'value', type : 'String'} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './timeForTaskRead.json?unit=minute'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		}
	}),
	
	hoursStore : new Ext.data.JsonStore( {
		autoSave : false,
		autoLoad : true,
		fields : [ {name : 'id', type : 'String'}, {name : 'value', type : 'String'} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './timeForTaskRead.json?unit=hour'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		}
	}),
	
	daysStore : new Ext.data.JsonStore( {
		autoSave : false,
		autoLoad : true,
		fields : [ {name : 'id', type : 'String'}, {name : 'value', type : 'String'} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './timeForTaskRead.json?unit=day'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		}
	}),
	
	monthsStore : new Ext.data.JsonStore( {
		autoSave : false,
		autoLoad : true,
		fields : [ {name : 'id', type : 'String'}, {name : 'value', type : 'String'} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './timeForTaskRead.json?unit=month'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		}
	}),
	
	weeksStore : new Ext.data.JsonStore( {
		autoSave : false,
		autoLoad : true,
		fields : [ {name : 'id', type : 'String'}, {name : 'value', type : 'String'} ],
		proxy : {
			type : 'ajax',
			api : {
				read : './timeForTaskRead.json?unit=week'
			},
			reader : {
				type : 'json',
				root : 'results',
				idProperty : 'id'
			}
		}
	})
};

var editorTasksPlanner = Ext.create('Ext.jv.grid.plugin.RowEditingValidation', {
	clicksToEdit : 2,
	validationScheme : 'validationPlanner',
    validateEditSuccess : function() {
    	tasksGrid.store.sync();
    },
    listeners: {    	
		beforeedit: function(editor,e,eOpts ) {
						
			if(e.record.get('everysecond') == -1) {
				Ext.getCmp('tasksGridId').down('[dataIndex=everysecond]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootDate]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootTime]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=week]').getEditor().setDisabled(true);
				
				Ext.getCmp('tasksGridId').down('[dataIndex=minute]').getEditor().setDisabled(false);
				Ext.getCmp('tasksGridId').down('[dataIndex=hour]').getEditor().setDisabled(false);
			} else if(e.record.get('everysecond') >= 0) {
				Ext.getCmp('tasksGridId').down('[dataIndex=everysecond]').getEditor().setDisabled(false);
				Ext.getCmp('tasksGridId').down('[dataIndex=minute]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=hour]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootDate]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootTime]').getEditor().setDisabled(true);
			}
			
			if(e.record.get('day') == -1 && e.record.get('month') == -1) {
				Ext.getCmp('tasksGridId').down('[dataIndex=week]').getEditor().setDisabled(false);
				Ext.getCmp('tasksGridId').down('[dataIndex=day]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=month]').getEditor().setDisabled(true);
			} else {
				Ext.getCmp('tasksGridId').down('[dataIndex=week]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=day]').getEditor().setDisabled(false);
				Ext.getCmp('tasksGridId').down('[dataIndex=month]').getEditor().setDisabled(false);
			}
			
			if(e.record.get('minute') == -1 && e.record.get('hour') == -1 && e.record.get('everysecond') == -1 && e.record.get('day') == -1 && e.record.get('month') == -1 && e.record.get('week') == -1) {
				
				Ext.getCmp('tasksGridId').down('[dataIndex=minute]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=hour]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=everysecond]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=day]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=month]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=week]').getEditor().setDisabled(true);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootDate]').getEditor().setDisabled(false);
				Ext.getCmp('tasksGridId').down('[dataIndex=shootTime]').getEditor().setDisabled(false);
			}
		}
    }
});

var tasksGrid = new Ext.grid.Panel({
			columnLines : true,
			columns : [{dataIndex : 'id', header : 'Id', sortable : true, width : 50},
					{dataIndex : 'name', header : _label['name'], sortable : true, width : 150, editor: {xtype:'textfield'}},					
					{dataIndex : 'description', header : _label['description'], sortable : true, width : 150, editor: {xtype:'textfield'}},					
					{dataIndex : 'minute', header : _label['minute'], sortable : true, width : 80, renderer : columnWrap, 
						editor: {xtype:'combobox', queryMode: 'local', displayField: 'value', store: schedulerDataSources.minutesStore, valueField: 'id'}},
					{dataIndex : 'hour', header : _label['hour'], sortable : true, width : 80, renderer : columnWrap,
						editor: {xtype:'combobox', queryMode: 'local', displayField: 'value', store: schedulerDataSources.hoursStore, valueField: 'id'}},
					{dataIndex : 'everysecond', header : _label['everyseconds'], sortable : true, width : 80, renderer : columnWrap,
							editor: {xtype:'textfield'}},
					{dataIndex : 'day', header : _label['day'], sortable : true, width : 100, renderer : columnWrap,
						editor: {xtype:'combobox', queryMode: 'local', displayField: 'value', store: schedulerDataSources.daysStore, valueField: 'id'}},
					{dataIndex : 'month', header : _label['month'], sortable : true, width : 100, renderer : columnWrapMonths,
						editor: {xtype:'combobox', queryMode: 'local', displayField: 'value', store: schedulerDataSources.monthsStore, valueField: 'id'}},
					{dataIndex : 'week', header : _label['week'], sortable : true, width : 100, renderer : columnWrapWeeks,
						editor: {xtype:'combobox', queryMode: 'local', displayField: 'value', store: schedulerDataSources.weeksStore, valueField: 'id'}},					
					{dataIndex: 'shootDate' , xtype: 'datecolumn',  header: _label['date'], width: 150 , field: {xtype: 'datefield',format: 'd/m/Y'}},
					{dataIndex : 'shootTime', header : _label['time'], sortable : true, width : 150, editor: {xtype:'textfield'}}
			],
			frame : false,
			id : 'tasksGridId',
			plugins: [editorTasksPlanner],
			store : new Ext.data.JsonStore( {
				autoLoad : false,
				fields : [{name: 'id', type: 'int', defaultValue: 0}, {name: 'name', type: 'string'},{name: 'description', type: 'string'},
				    {name: 'isOneShoot', type: 'boolean'},{name: 'isPeriodically', type: 'boolean'},{name: 'shootDate', type: 'date' , dateFormat: 'Y-m-d'},
				    {name: 'shootTime', type: 'string'},
					{name: 'minute', type: 'String', defaultValue: '-1'}, {name: 'everysecond', type: 'String', defaultValue: '-1'},
					{name: 'hour', type: 'String', defaultValue: '-1'}, {name: 'day', type: 'String', defaultValue: '-1'},
					{name: 'month', type: 'String', defaultValue: '-1'}, {name: 'week', type: 'String', defaultValue: '-1'}],
				proxy : {
					type : 'ajax',
			        api : {
						read : './tasksRead.json',
						create : './tasksCreate.json',
						update : './tasksUpdate.json?_method=put',
						destroy : './tasksDestroy.json?_method=delete'
					},
					reader : {
						type : 'json',
						root : 'results'
					},
					extraParams:{		
						taskId: ""            
			        }
				},
				listeners : {
					write : function(store, operation) {
						if (operation.response.responseText) {
							var responseObj = Ext.decode(operation.response.responseText);
							App.setAlert(true, responseObj.message);
							if (responseObj.extraMessage) App.setAlert(true, responseObj.extraMessage);
							if (operation.action == 'update' || operation.action == 'create') {
								store.load();
							}
						}
					}
				}  
			}),
			title : _label['planner']
		}
);

tasksGrid.store.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj.message.indexOf('Error') != -1) {
			Ext.Msg.alert("", _error['connectionError']);
		} else {
			App.setAlert(false, responseObj.message);
			if (operation.action == 'create' || operation.action == 'update') {
				tasksGrid.store.load();
			} else {
				tasksGrid.store.remove();
			}
		}
	}
});

function columnWrap(value) {
	if (value == -1) {
		return '';
	} else if (value == -2) {
		return _label['every'];
	}
	return value;
}

function columnWrapMonths(value) {
	if (value == -1) {
		return '';
	} else if (value == -2) {
		return _label['every'];
	}
	return _months[value];
}

function columnWrapWeeks(value) {
	if (value == -1) {
		return '';
	} else if (value == -2) {
		return _label['every'];
	}
	return _weekDays[value];
}

function addTask() {
	var record = new tasksGrid.store.model(); //recordType
	editorTasksPlanner.cancelEdit();
	popupTask(record, true);
}

function editTask() {
    var record = tasksGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false, _alert['selectRecord']);
        return false;
    };
    editorTasksPlanner.cancelEdit();
    popupTask(record, false);
};

function deleteTask() {
    var record = tasksGrid.getSelectionModel().getSelection()[0];
    if (!record) {
    	App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    Ext.MessageBox.confirm('Delete Planner?', _message['delMsg']+" '"+ record.get('name')+"'?",  function(btn) {
        if(btn == 'yes') {
        	tasksGrid.store.proxy.extraParams.taskId = record.data.id;
        	tasksGrid.store.remove(record);
        	tasksGrid.store.sync();
        }
    });
};

function popupTask(record, add) {
    new Ext.Window({
		height: 450,
		id: 'tasksPopupWindow',
		bodyStyle:{"background-color":"#ffffff"},
		items : [ {
			xtype : 'form',
			waitMsgTarget: true,
			layout : 'absolute',
			frame : false,
			border : false,
			url : 'controller.validateForm.json',
			items: [	

			        { xtype: "radio", y : 110, x : 10, fieldLabel: "", id: "weekly1", name: 'rbGroup1', submitValue : false,
			        	checked:true
			        },
			        { xtype: "radio", y : 265, x : 10, fieldLabel: "", id: "weekly2", name: 'rbGroup1', submitValue : false,
			        	checked:false,
			        	listeners: {
			        		change : function(el,val){	

			        			if(val) {							

			        				Ext.getCmp('shootDate').setDisabled(false);
			        				Ext.getCmp('shootTime').setDisabled(false);	

			        				Ext.getCmp('rbHourly').setDisabled(true);
			        				Ext.getCmp('rbMinutes').setDisabled(true);
			        				Ext.getCmp('monthly').setDisabled(true);
			        				Ext.getCmp('weekly').setDisabled(true);
			        				Ext.getCmp('day').setDisabled(true);
			        				Ext.getCmp('month').setDisabled(true);						
			        				Ext.getCmp('minute').setDisabled(true);
			        				Ext.getCmp('hour').setDisabled(true);						
			        				Ext.getCmp('week').setDisabled(true);
			        				Ext.getCmp('everysecond').setDisabled(true);							
			        			} else {

			        				Ext.getCmp('monthly').setDisabled(false);
			        				Ext.getCmp('weekly').setDisabled(false);
			        				if(Ext.getCmp('monthly').getValue()) {
			        					Ext.getCmp('day').setDisabled(false);
			        					Ext.getCmp('month').setDisabled(false);
			        					Ext.getCmp('week').setDisabled(true);
			        				} else {
			        					Ext.getCmp('day').setDisabled(true);
			        					Ext.getCmp('month').setDisabled(true);
			        					Ext.getCmp('week').setDisabled(false);
			        				}

			        				Ext.getCmp('hour').setDisabled(false);
			        				Ext.getCmp('minute').setDisabled(false);
			        				Ext.getCmp('shootDate').setDisabled(true);
			        				Ext.getCmp('shootTime').setDisabled(true);	



			        				if(Ext.getCmp('rbMinutes').getValue()) {
			        					Ext.getCmp('everysecond').setDisabled(false);
			        					Ext.getCmp('minute').setDisabled(true);
			        					Ext.getCmp('hour').setDisabled(true);
			        				} else {
			        					Ext.getCmp('everysecond').setDisabled(true);
			        					Ext.getCmp('minute').setDisabled(false);
			        					Ext.getCmp('hour').setDisabled(false);
			        				}
			        			} 
			        		}
			        	}
			        },
			        {value: _label['name'] , x: 50 , xtype: 'displayfield' , y: 10},
			        {xtype: 'textfield', id: 'name', name: 'name', value: record.get('name'), x: 50, width: 150 , y: 30},
			        {value: _label['description'] , x: 240 , xtype: 'displayfield' , y: 10},
			        {xtype: 'textfield', id: 'description', name: 'description', value: record.get('description'), x: 240, width: 430 , y: 30},
			        {
			        	xtype: 'fieldset',
			        	x:50, y:60,
			        	width: 620,
			        	height:150,
			        	layout: 'absolute',
			        	defaultType: 'textfield',
			        	title: 'Periodically',
			        	items:
			        		[		
			        		 { xtype: "radio", y : 20, x : 0, fieldLabel: "", id: "rbHourly", name: 'rbGroup2', submitValue: false, checked:true,				
			        			 listeners: {
			        				 change : function(el,val) {				
			        					 Ext.getCmp('minute').setDisabled(true);
			        					 Ext.getCmp('hour').setDisabled(true);
			        					 Ext.getCmp('everysecond').setDisabled(false);		
			        				 }
			        			 }
			        		 },	
			        		 {value: _label['minute'] , x: 30 , xtype: 'displayfield' , y: 0},			
			        		 {xtype : 'combo', y : 20, x : 30, width: 100, displayField : 'value',	id : 'minute', name : 'minute', mode : 'local', value: record.get('minute') == -1 ? '-2' : record.get('minute'),
			        				 store : schedulerDataSources.minutesStore, triggerAction : 'all', queryMode: 'local', valueField : 'id'},
			        				 {value : _label['hour'], x : 150, xtype : 'displayfield', y : 0 },				
			        				 {xtype : 'combo', y : 20, x : 150, displayField : 'value', id : 'hour', name : 'hour', mode : 'local', value: record.get('hour') == -1 ? '-2' : record.get('hour'),
			        						 store : schedulerDataSources.hoursStore, triggerAction : 'all', queryMode: 'local', valueField : 'id', width : 100},

			        						 { xtype: "radio", y : 80, x : 0, fieldLabel: "", id: "rbMinutes", name: 'rbGroup2', submitValue : false, checked:false,
			        							 listeners: {
			        								 change : function(el,val) {	
			        									 Ext.getCmp('minute').setDisabled(false);
			        									 Ext.getCmp('hour').setDisabled(false);
			        									 Ext.getCmp('everysecond').setDisabled(true);							
			        								 }
			        							 }
			        						 },
			        						 {value : _label['everyseconds'], x : 30, xtype : 'displayfield', y : 60 },		
			        						 {xtype: 'numberfield' , hideTrigger: true, y : 80, x : 30, id : 'everysecond', name : 'everysecond', value: record.get('everysecond') == -1 ? '' : record.get('everysecond'),
			        								 width : 100, disabled: true, maxLength: 5, enforceMaxLength: true, minValue:1, maxValue: 86400, allowDecimals: false },						
			        								 { xtype: "radio", y : 20, x : 300, fieldLabel: "", id: "monthly", name: 'rbGroup',	submitValue : false,  checked:true,				
			        									 listeners: {
			        										 change : function(el,val) {						
			        											 Ext.getCmp('week').setDisabled(false);
			        											 Ext.getCmp('month').setDisabled(true);
			        											 Ext.getCmp('day').setDisabled(true);
			        										 }
			        									 }
			        								 },		
			        								 {value : _label['day'], x : 330, xtype : 'displayfield', y : 0 },		
			        								 {xtype : 'combo', y : 20, x : 330, displayField : 'value', id : 'day', name : 'day', mode : 'local', value: record.get('day') == -1 ? '-2' : record.get('day'),
			        										 store : schedulerDataSources.daysStore, triggerAction : 'all', queryMode: 'local', valueField : 'id', width : 100},
			        										 {value : _label['month'], x : 450, xtype : 'displayfield', y : 0 },		
			        										 {xtype : 'combo', y : 20, x : 450, displayField : 'value', id : 'month', name : 'month', mode : 'local', value: record.get('month') == -1 ? '-2' : record.get('month'),
			        												 store : schedulerDataSources.monthsStore, triggerAction : 'all', queryMode: 'local', valueField : 'id', width : 100},
			        												 { xtype: "radio", y : 80, x : 300, fieldLabel: "", id: "weekly", name: 'rbGroup', submitValue : false,  checked:false,
			        													 listeners: {
			        														 change : function(el,val) {							
			        															 Ext.getCmp('week').setDisabled(true);
			        															 Ext.getCmp('month').setDisabled(false);
			        															 Ext.getCmp('day').setDisabled(false);						
			        														 }
			        													 }
			        												 },
			        												 {value : _label['week'], x : 330, xtype : 'displayfield', y : 60 },		
			        												 {xtype : 'combo', y : 80, x : 330, displayField : 'value', id : 'week', name : 'week', mode : 'local', value: record.get('week') == -1 ? '-2' : record.get('week'),
			        														 store : schedulerDataSources.weeksStore, triggerAction : 'all', queryMode: 'local', valueField : 'id', width : 100, disabled: true}


			        												 ]
			        },

			        {
			        	xtype: 'fieldset',
			        	x:50, y:220,
			        	layout: 'absolute',
			        	width:620,
			        	height:100,
			        	title: 'One shot',
			        	items:
			        		[
			        		 {value: _label['date'] , xtype: 'displayfield', x: 10, y:0},
			        		 {format: 'd/m/Y' , id: 'shootDate', name: 'shootDate', x: 10 , xtype: 'datefield' , width: 100 , y: 20, disabled: true, value: record.get('shootDate')},
			        		 {value: _label['time'] , xtype: 'displayfield', x: 150, y:0},
			        		 {id: 'shootTime', name: 'shootTime', x: 150 , xtype: 'textfield' , width: 50 , y: 20,  maskRe: /[\d:]/i, disabled: true,
			        			 value: record.get('shootTime') == '' ? 'hh:mm' : record.get('shootTime'), listeners:{
			        				 focus : function(){
			        					 if(Ext.getCmp('textFieldOneShoot').getValue()!='')
			        						 Ext.getCmp('textFieldOneShoot').setValue('');										
			        				 }
			        			 }
			        		 }	

			        		 ]
			        },			
			        {text: _message['save'] , x: 265 , xtype: 'button' , width: 100 , y: 330 ,
			        	handler: function() {

			        		var form = this.up('form').getForm();

			        		form.submit( {
			        			waitMsg: _message["waitMessage"],
			        			params : {schemaType : 'validationPlanner'},
			        			success : function(form, action) {

			        				record.set('name', Ext.getCmp('name').getValue());
			        				record.set('description', Ext.getCmp('description').getValue());

			        				if(Ext.getCmp('weekly2').getValue()) {	

			        					record.set('isOneShoot', true);						
			        					record.set('isPeriodically', false);						
			        					record.set('shootDate', Ext.getCmp('shootDate').getValue());

			        					if(Ext.getCmp('shootTime').getValue().toString().length == 0 || Ext.getCmp('shootTime').getValue().toString() == "hh:mm") {							
			        						record.set('shootTime', '');
			        					} else {							
			        						if(Ext.getCmp('shootTime').getValue().indexOf(":") == -1) {
			        							App.setAlert(false , _error['invalidTimeFormatError']);
			        							return;		
			        						}
			        						record.set('shootTime', Ext.getCmp('shootTime').getValue());
			        					}

			        					record.set('minute', -1);
			        					record.set('everysecond', -1);
			        					record.set('hour', -1);
			        					record.set('day', -1);
			        					record.set('month', -1);
			        					record.set('week', -1);

			        				} else {
			        					record.set('isOneShoot', false);						
			        					record.set('isPeriodically', true);
			        					record.set('shootDate', null);
			        					record.set('shootTime', '');

			        					if(Ext.getCmp('rbHourly').getValue()) {
			        						var val = Ext.getCmp('minute').getValue();
			        						if (val) record.set('minute', val);
			        						val = Ext.getCmp('hour').getValue();
			        						if (val) record.set('hour', val);

			        						record.set('everysecond', -1);							
			        					} else {
			        						record.set('everysecond', Ext.getCmp('everysecond').getValue());

			        						record.set('minute', -1);
			        						record.set('hour', -1);
			        					}

			        					if(Ext.getCmp('monthly').getValue()) {

			        						val = Ext.getCmp('day').getValue();
			        						if (val) record.set('day', val);
			        						val = Ext.getCmp('month').getValue();
			        						if (val) record.set('month', val);

			        						record.set('week', -1);

			        					} else {
			        						record.set('day', -1);
			        						record.set('month', -1);

			        						val = Ext.getCmp('week').getValue();
			        						if (val) record.set('week', val);
			        					}												
			        				}					

			        				if (add) {
			        					tasksGrid.store.insert(0, record);
			        				}
			        				tasksGrid.store.sync();
			        				Ext.getCmp('tasksPopupWindow').close();
			        			},
			        			failure : function(form, action) {
			        				
			        			}
			        		});
			        	}},
			        	{text: _message['cancel'] , x: 370 , xtype: 'button' , width: 100 , y: 330 ,
			        		handler: function() {
			        			Ext.getCmp('tasksPopupWindow').close();
			        		}
			        	}],
		}],	
		layout: 'absolute',
		modal: true,
		resizable: false,
		title: _label['planner'],
		width: 700,
		height: 405
    }).show();
	
	if(record.get('isOneShoot')) {
		Ext.getCmp('weekly2').setValue(true);		
	} else if(record.get('week') != -1){
		Ext.getCmp('weekly').setValue(true);
	}
	
	if(record.get('hour') != '-1') {
		Ext.getCmp('rbHourly').setValue(true);
	} else if(record.get('everysecond') != '-1'){
		Ext.getCmp('rbMinutes').setValue(true);
	}
}


function helpScheduler() {
	if (Ext.getCmp('popupSchedulerHelp')) return;
	new Ext.Window( {
		height : 250,
		id : 'popupSchedulerHelp',
		layout : 'absolute',
		modal : true,
		resizable : false,
		bodyStyle : 'padding:10px;',
		title : _message['help'],
		html : _message['plannerHelpMessage'],
		items : [ {
			text : _message['ok'],
			xtype : 'button',
			width : 100,
			x : 280,
			y : 180,
			handler : function() {
				Ext.getCmp('popupSchedulerHelp').close();
			}
		} ],
		width : 665
	}).show(this);
}