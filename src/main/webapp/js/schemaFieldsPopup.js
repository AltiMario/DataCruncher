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

function validateField(elementValue, pattern) {
    return pattern.test(elementValue);
}
/*
 * Popup schema field.
 *
 * @param {Object} record
 * @param {Object} action add/modify
 * @param {Object} fieldType
 * @param {boolean} isGenerationStream: true for schemas generation, false for schema validation
 * @return {TypeName}
 */
function popupSchemaField(record, action, fieldType, isGenerationStream) {
    function addAlphanumericFieldValues() {
        editorAlphanumericFieldValues.cancelEdit();
        Ext.MessageBox.show({
            title: _label["newValue"],
            prompt: true,
            buttons: Ext.MessageBox.OKCANCEL,
            fn: function(button , value) {
                if (button == 'ok') {
                    var record = new alphanumericFieldValuesGrid.store.model();
                    record.set('idSchema' , Ext.getCmp('idSchema').getValue());
                    record.set('idAlphanumericSchemaField' , Ext.getCmp('idSchemaField').getValue());
                    record.set('value' , value);
                    alphanumericFieldValuesGrid.store.insert(0 , record);
                    alphanumericFieldValuesGrid.store.sync();
                }
            }
        });
    }

    function deleteAlphanumericFieldValues() {
        var record = alphanumericFieldValuesGrid.getSelectionModel().getSelection()[0];
        if (!record) {
            App.setAlert(false , _alert['selectRecord']);
            return false;
        }
        alphanumericFieldValuesGrid.store.remove(record);
        alphanumericFieldValuesGrid.store.sync();
    }
    function addNumericFieldValues() {
        editorNumericFieldValues.cancelEdit();
        
        new Ext.Window({
			id: 'popupAllowValue',
			border: false,
			bodyStyle:{"background-color":"#ffffff"},
			items: [	
			   {id: 'allowValue' , x: 10 , xtype: 'numberfield' , hideTrigger: true, width: 220 , y: 15, allowDecimals : true},    
			   {id: 'btnOK', name: 'btnOK', text: _message["ok"], x: 45 , xtype: 'button' , width: 70 , y: 60 ,
					handler: function(){
						var value = Ext.getCmp('allowValue').getValue();
						var record = new numericFieldValuesGrid.store.model();
	                    record.set('idSchema' , Ext.getCmp('idSchema').getValue());
	                    record.set('idNumericSchemaField' , Ext.getCmp('idSchemaField').getValue());
	                    record.set('value' , value);
	                    numericFieldValuesGrid.store.insert(0 , record);
	                    numericFieldValuesGrid.store.sync();
	                    Ext.getCmp('popupAllowValue').close();
					}
					
				},
				{text: _message["cancel"] , x: 125 , xtype: 'button' , width: 70 , y: 60 ,
		        	handler: function(){
		        		Ext.getCmp('popupAllowValue').close();
		        	}
		        }
			],
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: _label["newValue"],
		    width: 250,
		    height: 120
	    }).show();      
    }

    function deleteNumericFieldValues() {
        var record = numericFieldValuesGrid.getSelectionModel().getSelection()[0];
        if (!record) {
            App.setAlert(false , _alert['selectRecord']);
            return false;
        }
        numericFieldValuesGrid.store.remove(record);
        numericFieldValuesGrid.store.sync();
    }
    var storeFieldType;
    if (fieldType == 'branch') {
        storeFieldType =  Ext.create('Ext.data.ArrayStore', {
            data : [[1 , 'All'] , [2 , 'Choice'] , [3 , 'Sequence']],
            fields: ['idFieldType' , 'name']
        });
    } else {
        storeFieldType =  Ext.create('Ext.data.ArrayStore', {
            data : [[4 , _message['alphanumeric']] , [5 , _message['numeric']] , [6 , _label['date']]],
            fields: ['idFieldType' , 'name']
        });
    }

    var sg = isGenerationStream ? generationStreamGrid : schemasGrid;
    var idStreamType = sg.getSelectionModel().getSelection()[0].get('idStreamType');
    var schemId = sg.getSelectionModel().getSelection()[0].get("idSchema");
    var idSchemaType = isGenerationStream ? 2 : sg.getSelectionModel().getSelection()[0].get('idSchemaType');
    var initLinkedFieldValue = null;
    var mainTabItem = [
        {value:_label['name']  , x: 5 , xtype: 'displayfield' , y: 5},
        {id: 'name' , x: 5 , xtype: 'textfield' , width: 150 , y: 25},
        {id: 'idFieldTypeLabel' , value:_message['type'] , x: 165 , xtype: 'displayfield' , y: 5},
        {displayField:'name' , id: 'idFieldType' , queryMode: 'local' , store: storeFieldType , triggerAction: 'all' , valueField: 'idFieldType' , x: 165 , xtype: 'combo', width: 150 , y: 25 ,forceSelection: true,
            listeners: {
                select: function() {
                    if (Ext.getCmp('idFieldType').getValue() == 4) {
                        Ext.getCmp("alphanumericTab").setDisabled(false);
                        Ext.getCmp("numericTab").setDisabled(true);
                        Ext.getCmp("dateTimeTab").setDisabled(true);

                        if (idStreamType == 3) {
                            if (Ext.getCmp('idAlignAlphanumeric').getValue() == "") {
                                Ext.getCmp('idAlignAlphanumeric').setValue(1);
                                Ext.getCmp('fillCharAlphanumeric').inputValue=1;//setValue(1);
                                Ext.getCmp('textFillCharAlphanumeric').setDisabled(true);
                                Ext.getCmp('textFillCharAlphanumeric').setValue("");
                            }
                        } else {
                            if (Ext.getCmp("nillableAlphanumeric").getValue() == "") {
                                Ext.getCmp("nillableAlphanumeric").setValue(0);
                            }
                            Ext.getCmp("alphanumericSizeLabel").setVisible(false);
                            Ext.getCmp("alphanumericSize").setVisible(false);

                            Ext.getCmp("nillableAlphanumericLabel").setPosition(225, 5);
                            Ext.getCmp("nillableAlphanumeric").setPosition(225, 25);
                            Ext.getCmp("nillableAlphanumeric").setWidth(100);
                        }

                    } else if(Ext.getCmp("idFieldType").getValue() == 5) {
                        Ext.getCmp("alphanumericTab").setDisabled(true);
                        Ext.getCmp("numericTab").setDisabled(false);
                        Ext.getCmp("dateTimeTab").setDisabled(true);
                        if (Ext.getCmp("nillableNumeric").getValue() == "" || Ext.getCmp("nillableNumeric").getValue() ==null) {
                            Ext.getCmp('numericType').setValue(1);
                            Ext.getCmp("fractionDigitsLabel").setDisabled(true);
                            Ext.getCmp("fractionDigits").setDisabled(true);
                            Ext.getCmp("fractionDigits").setValue("");
                            Ext.getCmp("nillableNumeric").setValue(0);
                        }
                        if (idStreamType == 3) {
                            if (Ext.getCmp('idAlignNumeric').getValue() == "" || Ext.getCmp('idAlignNumeric').getValue() == null) {
                                Ext.getCmp('idAlignNumeric').setValue(1);
                                Ext.getCmp('fillCharNumeric').inputValue=1;//setValue(1);
                                Ext.getCmp('textFillCharNumeric').setDisabled(true);
                                Ext.getCmp('textFillCharNumeric').setValue("");
                            }
                        } else {

                            Ext.getCmp("numericSizeLabel").setVisible(false);
                            Ext.getCmp("numericSize").setVisible(false);
                            
                            Ext.getCmp("nillableNumericLabel").setPosition(5, 95);
                            Ext.getCmp("nillableNumeric").setPosition(5, 115);
                        }
                    } else if(Ext.getCmp("idFieldType").getValue() == 6) {
                        Ext.getCmp("alphanumericTab").setDisabled(true);
                        Ext.getCmp("numericTab").setDisabled(true);
                        Ext.getCmp("dateTimeTab").setDisabled(false);
                        Ext.getCmp("idAlignDateTime").setValue(1);
                        Ext.getCmp('fillCharDateTime').inputValue=1;
                        Ext.getCmp('textFillCharDateTime').setDisabled(true);
                        Ext.getCmp('textFillCharDateTime').setValue("");
                        	
                        if (Ext.getCmp("nillableDate").getValue() == "" || Ext.getCmp("nillableDate").getValue() == null) {
                            Ext.getCmp('idDateTimeType').setValue(1);
                            Ext.getCmp("dateTypeLabel").setDisabled(false);
                            Ext.getCmp("idDateType").setDisabled(false);
                            Ext.getCmp('idDateType').setValue(1);
                            Ext.getCmp("timeTypeLabel").setDisabled(false);
                            Ext.getCmp("idTimeType").setDisabled(false);
                            Ext.getCmp('idTimeType').setValue(1);
                            Ext.getCmp("nillableDate").setValue(0);
                        }
                    }
                }
            }
        },
        {id: 'idMaxOccurs' , value:_label['maxOccurs'] , x: 340 , xtype: 'displayfield' , y: 5, disabled: true },
        {id: 'idUnbounded' , name: "rbGroup1", x: 340 , xtype: 'radio' , y: 25, boxLabel: _label['unbounded'], disabled: true },
        {id: 'idOther' , name: "rbGroup1", x: 430 , xtype: 'radio' , y: 25, boxLabel: _label['other'], disabled: true, checked:true,
        	listeners: {
				change : function(el,val) {
					
					if(val) {
						Ext.getCmp('idOtherTextField').setDisabled(false);
						Ext.getCmp('idOtherTextField').focus();
					} else {
						Ext.getCmp('idOtherTextField').setDisabled(true);
					}
				}
        	}
        },
        {id: 'idOtherTextField' , x: 490 , xtype: 'textfield', y: 25, width:30, value:"1", disabled: true },

		{xtype : 'fieldset', title : _label['relevance'], x : 540, y : 5, width : 93, height : 70,
        	hidden : fieldType == 'branch',
        	padding : '0 0 0 5' /* need to set all paddings manually, otherwise default values*/,
			items : [
				{ xtype : 'radiogroup', columns : 1, id : 'errorTypeRadioId',
				  vertical : false, 
				  items : [ {
							boxLabel : _message['error'],
							inputValue : 0,
							name : 'rb',
							checked : true
						}, {
							boxLabel : _label['streamLoggingWarning'],
							inputValue : 2,
							name : 'rb'
						} ]
				}
			]
		},
							 
		{ 	//not used?
        	id : 'linkToDbTextField',
			labelAlign : 'top',
			y : 5,
			x : 550,
			hidden : !isGenerationStream,
			disabled : fieldType == 'branch',
			fieldLabel : _label['linkToDb'],
			xtype : 'textfield',
			width : 80
		},
        {value: _label['description'] , x: 5 , xtype: 'displayfield' , y: 90},
        {height: 220 , id:'description' , x: 5 , xtype: 'htmleditor' , width: 630 , y: 110}
    ];

    var mainTab = {
        items: mainTabItem,
        id: 'mainTab',
        frame: false,
        border: false,
        layout: 'absolute',
        title: _label['fieldDetail'],
        xtype: 'panel'
    };

    var storeNillable =  Ext.create('Ext.data.ArrayStore', {
        data : [[0 ,_message['notOptional']] , [1 ,_message['optional'] ]],
        fields: ['idNillable' , 'name']
    });

    if (!Ext.ModelManager.isRegistered('spellRecords')){
        Ext.define('spellRecords', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'idExtraCheck', mapping: 'idCheckType'},
                {name: 'name', mapping: 'name'}
            ]
        });
    }

    var storeExtraCheck = new Ext.data.Store({
        autoSave: false,
        autoLoad: true,
        model: 'spellRecords',
        proxy: {
            type: 'ajax',
            api: {
                read    : './checksTypeRead.json?leftPane=true&idSchemaField='+record.id
            },
            reader: {
                type: 'json',
                root: 'results',
                idProperty: 'idCheckType'
            }
        },
        listeners: {
            load: function(store,records,options) {
                storeExtraCheck.sort('name', 'ASC');
            }
        }
    });
    
    function columnWrap(val){
		return '<div style="white-space:normal !important;">'+ val +'</div>';
	}
	var columns = [
        {text: "Extra Check", flex: 1, dataIndex: 'name',id:'extraCheckName',renderer: columnWrap }
    ];
	
	var firstGrid = Ext.create('Ext.grid.Panel', {
        multiSelect: true,
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'secondGridDDGroup'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {}
            }
        }, x: 160 , width: 230 , y: 5,autoScroll: true,height:320,
        store            : storeExtraCheck,
        columns          : columns,
        stripeRows       : true,
		title			  :'Extra Check Availables',
        margins          : '0 2 0 0'
    });
	
	var secondGridStore = Ext.create('Ext.data.Store', {
       autoSave: false,
        autoLoad: true,
        model: 'spellRecords',
        proxy: {
            type: 'ajax',
            api: {
                read    : './checksTypeRead.json?leftPane=false&idSchemaField='+record.id
            },
            reader: {
                type: 'json',
                root: 'results',
                idProperty: 'idCheckType'
            }
        },
        listeners: {
            load: function(store,records,options) {
                storeExtraCheck.sort('name', 'ASC');
            }
        }
    });
	
	var secondGrid = Ext.create('Ext.grid.Panel', {
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'secondGridDDGroup',
                dropGroup: 'firstGridDDGroup'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {					                   
                }
            }
        }, x: 400 , width: 240 , y: 5,autoScroll: true,height:320,
        store            : secondGridStore,
        columns          : columns,
        stripeRows       : true,
		title			  :'Extra Check Selected',
        margins          : '0 2 0 0'
    });
	
	
	var storeNumericType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 ,_message['integer']] , [2 ,_message['decimal']]],
        fields: ['idNumericType' , 'name']
    });


    var storeAlign =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['left']] , [2 , _message['right']]],
        fields: ['idAlign' , 'name']
    });

    var storeAlignForNumeric =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['left']] , [2 , _message['right']]],
        fields: ['idAlign' , 'name']
    });
    
    var storeAlignForDateTime =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['left']] , [2 , _message['right']]],
        fields: ['idAlign' , 'name']
    });

    var storeDateTimeType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['dateAndTime']] , [2 ,  _message['date']] , [3 , _message['hour']],
            [4 , _message['XSDdateAndTime']] , [5 ,  _message['XSDdate']] , [6 , _message['XSDhour']], [7 , _message['timestampUnix']]],
        fields: ['idDateTimeType' , 'name']
    });
    var storeDateType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , 'dd/MM/yyyy'] , [2 , 'dd-MM-yyyy'] , [3 , 'dd.MM.yyyy'] , [10 , 'ddMMyyyy'] , [4 , 'dd/MM/yy'] , [5 , 'dd-MM-yy'] , [6 , 'dd.MM.yy'] , [11 , 'ddMMyy'] , [7 , 'yyyy/MM/dd'] , [8 , 'yyyy-MM-dd'] , [9 , 'yyyy.MM.dd'] , [12 , 'yyyyMMdd']],
        fields: ['idDateType' , 'name']
    });

    var storeTimeType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , 'hh:mm:ss'], [2 , 'hh.mm.ss'],[3 , 'hh:mm'],[4 , 'hh.mm'],[5 , 'hh:mm:ss AM/PM'],
                [6 , 'hh.mm.ss AM/PM'],[7 , 'h:mm:ss'], [8 , 'h.mm.ss']],
        fields: ['idTimeType' , 'name']
    });

    if (!Ext.ModelManager.isRegistered('alphanumericFieldValues')){
        Ext.define('alphanumericFieldValues', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'idAlphanumericFieldValue', type:'int', defaultValue:0},
                {name: 'idSchema', type: 'string'},
                {name: 'idAlphanumericSchemaField', type: 'string'},
                {name : 'value' , type: 'string'}
            ],
            idProperty:'idAlphanumericFieldValue'
        });
    }


    var storeAlphanumericFieldValues = new Ext.data.Store({
        autoSave: false,
        model: 'alphanumericFieldValues',
        idProperty: 'idAlphanumericFieldValue',
        proxy: {
            type: 'ajax',
            api: {
                read    : './alphanumericFieldValuesRead.json',
                create  : './alphanumericFieldValuesCreate.json',
                update  : './alphanumericFieldValuesUpdate.json?_method=put',
                destroy : './alphanumericFieldValuesDestroy.json?_method=delete&anfId='
            },
            reader: {
                type: 'json',
                root: 'results',
                idProperty: 'idAlphanumericFieldValue',
                successProperty: 'success',
                messageProperty: 'message'
            },
            writer: {
                type: 'json',
                writeAllFields:true
            }
        },

        listeners: {
            write: function(store, operation){
                if (operation.response.responseText) {
                    var responseObj = Ext.decode(operation.response.responseText);
                    App.setAlert(true,responseObj.message);
                }
            }
        }
    });
    storeAlphanumericFieldValues.proxy.addListener('exception', function (proxy, response, operation) {
        if (response) {
            var responseObj = Ext.decode(response.responseText);
            App.setAlert(false , responseObj.message);
            if (operation.action == 'create') {
                storeAlphanumericFieldValues.load();
            } else {
                storeAlphanumericFieldValues.remove();
            }
        }
    });
    var editorAlphanumericFieldValues = Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 1,
        listeners: {
            afteredit: function() {
                alphanumericFieldValuesGrid.store.sync();
            }
        }
    });
    var columnsAlphanumericFieldValues = [
        {dataIndex: 'value' , editor: {xtype:'textfield'} , header: _label['value'] ,  sortable: true , flex: 1}
    ];

    var tbarAlphanumericFieldValues = [
        {iconCls: 'schema_add' , handler: addAlphanumericFieldValues , text: _message['add']} , '-' ,
        {iconCls: 'schema_delete' , handler: deleteAlphanumericFieldValues , text: _message['delete']}
    ];

    var alphanumericFieldValuesGrid =  Ext.create('Ext.grid.Panel', {
        columnLines: true,
        columns: columnsAlphanumericFieldValues,
        frame: false,
        id: 'alphanumericFieldValuesGrid',
        plugins: [editorAlphanumericFieldValues],
        selModel: Ext.create('Ext.selection.RowModel', {
            mode:'SINGLE'
        }),
        store: storeAlphanumericFieldValues,
        tbar: tbarAlphanumericFieldValues,
        title: _label['allowedValues'],
        height: 230,
		width:145,
        x: 5,
        y: 100
    });


    var alphanumericTab = {
        items: [
	        {id: 'idSchema' , xtype: 'hidden'},
	        {id: 'idSchemaField' , xtype: 'hidden'},
	        {value:_message['minLength'] , x: 5 , xtype: 'displayfield' , y: 5},
	        {id: 'minLenght' , x: 5 , xtype: 'numberfield' , hideTrigger: true, minValue: 1, width: 60 , y: 25, allowDecimals : false},
	        {value:_message['maxLength'] , x: 80 , xtype: 'displayfield' , y: 5},
	        {id: 'maxLenght' , x: 80 , xtype: 'numberfield' , hideTrigger: true, minValue: 1, width: 60 , y: 25, allowDecimals : false},
	        {id: 'alphanumericSizeLabel' , value:_message['sizeField'] , x: 5 , xtype: 'displayfield' , y: 55},
	        {id: 'alphanumericSize' , x: 5 , xtype: 'numberfield', hideTrigger: true, minValue: 1, allowBlank: false, width: 100 , y: 75, value:'1', allowDecimals : false},
	        {id: 'nillableAlphanumericLabel' , value:_message['optional'] , x: 5 , xtype: 'displayfield' , y: 50},
	        {displayField:'name' , id: 'nillableAlphanumeric' , queryMode: 'local' , store: storeNillable , triggerAction: 'all' , valueField: 'idNillable' , x: 5 , xtype: 'combo', width: 100 , y: 70},
	        firstGrid,			
			secondGrid,
	        {id: 'idAlignAlphanumericLabel' , value: _message['alignment'] , x: 120 , xtype: 'displayfield' , y: 55},
	        {displayField:'name' , id: 'idAlignAlphanumeric' , queryMode: 'local' , store: storeAlign , triggerAction: 'all' , valueField: 'idAlign' , x: 120 , xtype: 'combo', width: 100 , y: 75},
	        {id: 'fillCharAlphanumericLabel' , value: _message['fillCharacter'] , x: 260 , xtype: 'displayfield' , y: 55},
	        {id: 'fillCharAlphanumeric' , x: 260 , xtype: 'radiogroup', width: 120 , y: 75 ,
	            items: [
					{boxLabel: _label['space'] , id: 'spaceAlphanumeric' , inputValue: 1 , name: 'radioButtonAlphanumeric', checked:true,
						handler : function(radio, checked) {
							if(checked == true) {
								Ext.getCmp('textFillCharAlphanumeric').setDisabled(true);
								Ext.getCmp('textFillCharAlphanumeric').setValue("");
							}
						}
					},
	                {boxLabel: _label['other'] , id: 'otherAlphanumeric' , inputValue: 2 , name: 'radioButtonAlphanumeric',
	                    handler : function(radio, checked) {
	                        if(checked == true) {
	                            Ext.getCmp('textFillCharAlphanumeric').setDisabled(false);
	                            Ext.getCmp('textFillCharAlphanumeric').setValue("");
	                            Ext.getCmp('textFillCharAlphanumeric').focus();
	                        }
	                    }
	                }
	            ]
	        },
	        {id: 'textFillCharAlphanumeric' , x:375 , xtype: 'textfield' , width: 35 , y: 75, disabled: true},
	        alphanumericFieldValuesGrid
        ],
        id: 'alphanumericTab',
        frame: false,
        border: false,
        layout: 'absolute',
        title: _message['alphanumeric'],
        xtype: 'panel'
    };

    if (!Ext.ModelManager.isRegistered('numericFieldValuesModel')){
        Ext.define('numericFieldValuesModel', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'idNumericFieldValue', type:'string'},
                {name: 'idSchema', type:'int', defaultValue:0},
                {name: 'idNumericSchemaField', type: 'string'},
                {name : 'value' , type: 'string'}
            ],
            idProperty:'idNumericFieldValue'
        });
    }

    var storeNumericFieldValues = new Ext.data.Store({
        autoSave: false,
        model: 'numericFieldValuesModel',
        proxy: {
            type: 'ajax',
            api: {
                read    : './numericFieldValuesRead.json',
                create  : './numericFieldValuesCreate.json',
                update  : './numericFieldValuesUpdate.json?_method=put',
                destroy : './numericFieldValuesDestroy.json?_method=delete&nfId='
            },
            reader: {
                type: 'json',
                root: 'results',
                idProperty: 'idNumericFieldValue',
                successProperty: 'success',
                messageProperty: 'message'
            },
            writer: {
                type: 'json',
                writeAllFields:true
            }
        },
        listeners: {
            write: function(store, operation){
                if (operation.response.responseText) {
                    var responseObj = Ext.decode(operation.response.responseText);
                    App.setAlert(true,responseObj.message);
                }
            }
        }
    });
    storeNumericFieldValues.proxy.addListener('exception', function (proxy, response, operation) {
        if (response) {
            var responseObj = Ext.decode(response.responseText);
            App.setAlert(false , responseObj.message);
            if (operation.action == 'create') {
                storeNumericFieldValues.load();
            } else {
                storeNumericFieldValues.remove();
            }
        }
    });
    var editorNumericFieldValues = Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 1,
        listeners: {
            afteredit: function() {
                numericFieldValuesGrid.store.sync();
            }
        }
    });

    var columnsNumericFieldValues = [
        {align: 'center' , dataIndex: 'idNumericFieldValue' , header: 'Id' ,   sortable: true , width: 50},
        {dataIndex: 'value' , editor: {xtype:'textfield'} , header: _label['value']  ,  sortable: true , width: 450, editor: {xtype: 'numberfield' , hideTrigger: true, allowDecimals : true} }
    ];

    var tbarNumericFieldValues = [
        {iconCls: 'schema_add' , handler: addNumericFieldValues , text: _message['add']} , '-' ,
        {iconCls: 'schema_delete' , handler: deleteNumericFieldValues , text: _message['delete']}
    ];
    var numericFieldValuesGrid = Ext.create('Ext.grid.Panel', {
        columnLines: true,
        columns: columnsNumericFieldValues,
        frame: false,
        id: 'numericFieldValuesGrid',
        plugins: [editorNumericFieldValues],
        selModel: Ext.create('Ext.selection.RowModel', {
            mode:'SINGLE'
        }),
        store: storeNumericFieldValues,
        tbar: tbarNumericFieldValues,
        title: _label['allowedValues'],
        height: 190,
        x: 5,
        y: 140
    });
    
    var numericTab = {
        items: [
	        {id: 'idSchema' , xtype: 'hidden'},
	        {id: 'idSchemaField' , xtype: 'hidden'},
	        {value: _message['minInclusive'] , x: 5 , xtype: 'displayfield' , y: 5},
	        {id: 'minInclusiveInt' , x: 5 , xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, allowDecimals : false },
	        {id: 'minInclusiveDecimal' , x: 5 , xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, hidden: true },
	        {value:_message['maxInclusive'] , x: 165 , xtype: 'displayfield' , y: 5},
	        {id: 'maxInclusiveInt' , x: 165 ,xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, allowDecimals : false},
	        {id: 'maxInclusiveDecimal' , x: 165 ,xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, hidden: true},
	        {id: 'nillableNumericLabel', value: _message['optional'], x: 385 , xtype: 'displayfield' , y: 5},
	        {displayField: 'name' , id: 'nillableNumeric' , queryMode: 'local' , store: storeNillable, triggerAction: 'all' , valueField: 'idNillable' , x: 385 , xtype: 'combo', width: 150 , y: 25},
	        {id: 'errorLabel', value: _message['errorTolerance'], x: 165, xtype: 'displayfield' , y: 95},
	        {id: 'chkBoxError', xtype: 'checkboxfield', x: 165, y: 115, 
	        	listeners: {
					change : function(checkbox,value){
						Ext.getCmp('idSliderField').setDisabled(!value);
					}
	        	}
	        },

            {id: 'indexIncremental', value: "Index incremental", x: 300, xtype: 'displayfield' , y: 95},
            {id: 'chkBoxIndexIncremental', xtype: 'checkboxfield', x: 300, y: 115 },	                     

	        {id: 'idSliderField', xtype: 'sliderfield', x: 190, y: 115, width: 90, value: 0, minValue: 0, maxValue: 60, disabled: true},
	        {value: _message['type'] , x: 5 , xtype: 'displayfield' , y: 50},
	        {displayField: 'name' , id: 'numericType' , queryMode: 'local' , store : storeNumericType , triggerAction : 'all', valueField : 'idNumericType' , x : 5 , xtype : 'combo' , width : 150 , y : 70 ,
	            listeners : {
	                collapse : function() {
	                    Ext.getCmp('fractionDigits').setValue("");
	                    Ext.getCmp("minInclusiveInt").setValue("");
                        Ext.getCmp("maxInclusiveInt").setValue("");
                        Ext.getCmp("minInclusiveDecimal").setValue("");
                        Ext.getCmp("maxInclusiveDecimal").setValue("");
                        
	                    if (Ext.getCmp("numericType").getValue() == 1) {
	                        Ext.getCmp("fractionDigitsLabel").setDisabled(true);
	                        Ext.getCmp("fractionDigits").setDisabled(true);
	                        Ext.getCmp("minInclusiveInt").setVisible(true);
	                        Ext.getCmp("maxInclusiveInt").setVisible(true);
	                        Ext.getCmp("minInclusiveDecimal").setVisible(false);
	                        Ext.getCmp("maxInclusiveDecimal").setVisible(false);
	                    } else {
	                        Ext.getCmp("fractionDigitsLabel").setDisabled(false);
	                        Ext.getCmp("fractionDigits").setDisabled(false);
	                        Ext.getCmp("fractionDigits").setValue("");
	                        Ext.getCmp("minInclusiveInt").setVisible(false);
	                        Ext.getCmp("maxInclusiveInt").setVisible(false);
	                        Ext.getCmp("minInclusiveDecimal").setVisible(true);
	                        Ext.getCmp("maxInclusiveDecimal").setVisible(true);
	                    }
	                }
	            }
	        },
	        	        
	        {id: 'fractionDigitsLabel' , value: _message['decimalPlaces'] , x: 165 , xtype: 'displayfield' , y: 50},

	        {
	            xtype : 'fieldset',
	            height : 78,
	            width : 270,
	            id : 'forecastingSlidesPanel',
	            y : 5,
	            x : 325,
	            title : _label['forecasting'],
	            layout : 'absolute',
	            items : [
	                {
	                    xtype : 'checkboxfield',
	                    id : 'forecastCheckbox'
	                },
	                Ext.create('Ext.slider.Single', {
	                    x : 40,
	                    width: 200,
	                    value: 50,
	                    id : 'forecastSpeedSlider',
	                    fieldLabel : 'Speed',
	                    increment: 2,
	                    minValue: 0,
	                    maxValue: 100,
	                    listeners : {
	                        change : function(slider, newVal) {
	                            Ext.getCmp('forecastAccuracySlider').setValue(100 - newVal);
	                        }
	                    }
	                }),
	                Ext.create('Ext.slider.Single', {
	                    x : 40,
	                    y : 20,
	                    width: 200,
	                    value: 50,
	                    id : 'forecastAccuracySlider',
	                    fieldLabel : 'Accuracy',
	                    increment: 2,
	                    minValue: 0,
	                    maxValue: 100,
	                    listeners : {
	                        change : function(slider, newVal) {
	                            Ext.getCmp('forecastSpeedSlider').setValue(100 - newVal);
	                        }
	                    }
	                })
	            ]
	        },
	        	        
	        {id: 'numericSizeLabel' , value:_message['sizeField'] , x: 5 , xtype: 'displayfield' , y: 95},
	        {id: 'numericSize' , x: 5 , xtype: 'numberfield', hideTrigger: true, minValue: 1,allowBlank: false, width: 120 , y: 115, allowDecimals : false},	        
	        {id: 'fractionDigits' , x: 165 , xtype: 'textfield' , width: 150 , y: 70, disabled: true},
	        {id: 'idAlignNumericLabel' , value:_message['alignment'] , x: 165 , xtype: 'displayfield' , y: 95},
	        {displayField:'name' , id: 'idAlignNumeric' , queryMode: 'local' , store: storeAlignForNumeric , triggerAction: 'all' , valueField: 'idAlign' , x: 165 , xtype: 'combo', width: 150 , y: 115},
	        {id: 'fillCharNumericLabel' , value:  _message['fillCharacter'] , x: 330 , xtype: 'displayfield' , y: 95},
	        {id: 'fillCharNumeric' , x: 330 , xtype: 'radiogroup', width: 120 , y: 115 ,
	            items: [{boxLabel: _label['space'] , id: 'spaceNumeric' , inputValue: 1 , name: 'radioButtonNumeric', checked:true,
	                handler : function(radio, checked) {
	                    if(checked == true) {
	                        Ext.getCmp('textFillCharNumeric').setDisabled(true);
	                        Ext.getCmp('textFillCharNumeric').setValue("");
	                    }
	                }
	            },
	                {boxLabel: _label['other'] , id: 'otherNumeric' , inputValue: 2 , name: 'radioButtonNumeric',
	                    handler : function(radio, checked) {
	                        if(checked == true) {
	                            Ext.getCmp('textFillCharNumeric').setDisabled(false);
	                            Ext.getCmp('textFillCharNumeric').setValue("");
	                            Ext.getCmp('textFillCharNumeric').focus();
	                        }
	                    }
	                }
	            ]
	        },
	        {id: 'textFillCharNumeric' , x:445 , xtype: 'textfield' , width: 35 , y:115, disabled: true},
	        numericFieldValuesGrid
		],
        id: 'numericTab',
        frame: false,
        border: false,
        layout: 'absolute',
        title: _message['numeric'] ,
        xtype: 'panel'
    };

    var dateTimeTab = {
        items: [
	        {id: 'idSchema' , xtype: 'hidden'},
	        {id: 'idSchemaField' , xtype: 'hidden'},
	        {value: _message['format'] , x: 5 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idDateTimeType' , queryMode: 'local' , store: storeDateTimeType , triggerAction: 'all' , valueField: 'idDateTimeType' , x: 5 , xtype: 'combo', width: 150 , y: 25 ,
	            listeners: {
	                select: function(){
	                	Ext.getCmp("idAlignDateTimeLabel").setVisible(false);
                        Ext.getCmp("idAlignDateTime").setVisible(false);
                        Ext.getCmp("fillCharDateTimeLabel").setVisible(false);
                        Ext.getCmp("fillCharDateTime").setVisible(false);
                        Ext.getCmp("textFillCharDateTime").setVisible(false);
                        Ext.getCmp("dateTimeSizeLabel").setVisible(false);
                        Ext.getCmp("dateTimeSize").setVisible(false);
	                    if (Ext.getCmp('idDateTimeType').getValue() == 1) {
	                        Ext.getCmp("dateTypeLabel").setDisabled(false);
	                        Ext.getCmp("idDateType").setDisabled(false);
	                        Ext.getCmp("idDateType").setValue(1);
	                        Ext.getCmp("timeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idTimeType").setDisabled(false);
	                        Ext.getCmp("idTimeType").setValue(1);
	                    } else if(Ext.getCmp("idDateTimeType").getValue() == 2) {
	                        Ext.getCmp("dateTypeLabel").setDisabled(false);
	                        Ext.getCmp("idDateType").setDisabled(false);
	                        Ext.getCmp("idDateType").setValue(1);
	                        Ext.getCmp("timeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idTimeType").setDisabled(true);
	                        Ext.getCmp("idTimeType").setValue(null);
	                    } else if(Ext.getCmp("idDateTimeType").getValue() == 3) {
	                        Ext.getCmp("dateTypeLabel").setDisabled(true);
	                        Ext.getCmp("idDateType").setDisabled(true);
	                        Ext.getCmp("idDateType").setValue(null);
	                        Ext.getCmp("timeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idTimeType").setDisabled(false);
	                        Ext.getCmp("idTimeType").setValue(1);
	                    } else if(Ext.getCmp("idDateTimeType").getValue() == 4 || Ext.getCmp("idDateTimeType").getValue() == 5 || Ext.getCmp("idDateTimeType").getValue() == 6 || Ext.getCmp("idDateTimeType").getValue() == 7) {
	                    	Ext.getCmp("dateTypeLabel").setDisabled(true);
                    		Ext.getCmp("idDateType").setDisabled(true);
                    		Ext.getCmp("idDateType").setValue(null);
                    		Ext.getCmp("timeTypeLabel").setDisabled(true);
                    		Ext.getCmp("idTimeType").setDisabled(true);
                    		Ext.getCmp("idTimeType").setValue(null);	                        
                    		
	                    	if (idStreamType == 3) {
	                    		Ext.getCmp("idAlignDateTimeLabel").setVisible(true);
	                    		Ext.getCmp("idAlignDateTime").setVisible(true);
	                    		Ext.getCmp("fillCharDateTimeLabel").setVisible(true);
	                    		Ext.getCmp("fillCharDateTime").setVisible(true);
	                    		Ext.getCmp("textFillCharDateTime").setVisible(true);
	                    		Ext.getCmp("dateTimeSizeLabel").setVisible(true);
	                    		Ext.getCmp("dateTimeSize").setVisible(true);
	                    		Ext.getCmp("idAlignDateTime").setValue(1);
	                    	}
	                    }
	                }
	            }
	        },
	        {id: 'dateTypeLabel' , value: _label['dateType'] , x: 165 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idDateType' , queryMode: 'local' , store: storeDateType , triggerAction: 'all' , valueField: 'idDateType' , x: 165 , xtype: 'combo', width: 150 , y: 25},
	        {id: 'timeTypeLabel' , value: _label['timeType'] , x: 325 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idTimeType' , queryMode: 'local' , store: storeTimeType , triggerAction: 'all' , valueField: 'idTimeType' , x: 325 , xtype: 'combo', width: 150 , y: 25,
	            listeners: {
	                afterrender:function(component, options){
	                    if(Ext.getCmp("idDateTimeType").getValue() == 2) {
	                        Ext.getCmp("timeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idTimeType").setDisabled(true);
	                        Ext.getCmp("idTimeType").setValue(null);
	                    } else if(Ext.getCmp("idDateTimeType").getValue() == 3) {
	                        Ext.getCmp("dateTypeLabel").setDisabled(true);
	                        Ext.getCmp("idDateType").setDisabled(true);
	                        Ext.getCmp("idDateType").setValue(null);
	                        Ext.getCmp("timeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idTimeType").setDisabled(false);
	                    }  else if(Ext.getCmp("idDateTimeType").getValue() == 4 || Ext.getCmp("idDateTimeType").getValue() == 5 || Ext.getCmp("idDateTimeType").getValue() == 6 || Ext.getCmp("idDateTimeType").getValue() == 7) {
	                        Ext.getCmp("dateTypeLabel").setDisabled(true);
	                        Ext.getCmp("idDateType").setDisabled(true);
	                        Ext.getCmp("idDateType").setValue(null);
	                        Ext.getCmp("timeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idTimeType").setDisabled(true);
	                        Ext.getCmp("idTimeType").setValue(null);
	                    }
	                }
	            }
	        },
	        {id: 'nillableDateLabel',value: _message['optional'] , x: 485 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'nillableDate' , queryMode: 'local' , store: storeNillable, triggerAction: 'all' , valueField: 'idNillable' , x: 485 , xtype: 'combo', width: 150 , y: 25},
	        {id: 'dateTimeSizeLabel' , value:_message['sizeField'] , x: 5 , xtype: 'displayfield' , y: 60, hidden: true},
	        {id: 'dateTimeSize' , x: 5 , xtype: 'numberfield', hideTrigger: true, minValue: 1,allowBlank: false, width: 120 , y: 80, allowDecimals : false, hidden: true},
	        {id: 'idAlignDateTimeLabel' , value:_message['alignment'] , x: 165 , xtype: 'displayfield' , y: 60, hidden: true},
	        {displayField:'name' , id: 'idAlignDateTime' , queryMode: 'local' , store: storeAlignForDateTime , triggerAction: 'all' , valueField: 'idAlign' , x: 165 , xtype: 'combo', width: 150 , y: 80, hidden: true},
	        {id: 'fillCharDateTimeLabel' , value:  _message['fillCharacter'] , x: 330 , xtype: 'displayfield' , y: 60, hidden: true},
	        {id: 'fillCharDateTime' , x: 330 , xtype: 'radiogroup', width: 120 , y: 80 , hidden: true,
	            items: [{boxLabel: _label['space'] , id: 'spaceDateTime' , inputValue: 1 , name: 'radioButtonDateTime', checked:true,
	                handler : function(radio, checked) {
	                    if(checked == true) {
	                        Ext.getCmp('textFillCharDateTime').setDisabled(true);
	                        Ext.getCmp('textFillCharDateTime').setValue("");
	                    }
	                }
	            },
	                {boxLabel: _label['other'] , id: 'otherDateTime' , inputValue: 2 , name: 'radioButtonDateTime',
	                    handler : function(radio, checked) {
	                        if(checked == true) {
	                            Ext.getCmp('textFillCharDateTime').setDisabled(false);
	                            Ext.getCmp('textFillCharDateTime').setValue("");
	                            Ext.getCmp('textFillCharDateTime').focus();
	                        }
	                    }
	                }
	            ]
	        },
	        {id: 'textFillCharDateTime' , x:445 , xtype: 'textfield' , width: 35 , y: 80, disabled: true, hidden: true}
	    ],
        id: 'dateTimeTab',
        frame: false,
        border: false,
        layout: 'absolute',
        title: _message['date'],
        xtype: 'panel'
    };
    var tabArray = [mainTab, alphanumericTab, numericTab, dateTimeTab];
    if (isGenerationStream) {
        var dbFieldsTab = {
            frame: false,
            layout: 'fit',
            title: _label['db_fields'],
            xtype: 'panel',
            disabled: sg.getSelectionModel().getSelection()[0].get('idInputDatabase') <= 0 || fieldType == 'branch',
            items: [
                {
                    xtype : 'treepanel',
                    id : 'dbTreeSchema',
                    disabled : false,
                    collapsible : false,
                    store : gsfDataStores.dbTree,
                    rootVisible : false,
                    useArrows : false,
                    scroll : false,
                    viewConfig : {
                        style : {
                            overflowY : 'scroll'
                            //overflowX : 'scroll'
                        }
                    },
                    listeners : {
                    	afterrender : function() {
                    		if (gsfDataStores.dbTree.isLoading()) {
								var mask = new Ext.LoadMask(Ext.getBody(), 
									{	msg : _message['waitMessage'], 
										store : gsfDataStores.dbTree
									}
								);
								mask.show();
							}
                    	},
                    	itemclick : function(view, rec, item, index, e) {
							Ext.create('Ext.menu.Menu', {
								items: [
								        {iconCls: 'alertIcon', 
								        	handler: function() {
												Ext.Ajax.request({
		                        					success: function (resp, opts) {
														var tree = isGenerationStream ? generationStreamFieldsTree : schemaFieldsTree;													
														tree.getStore().load();
														gsfDataStores.dbTree.load();
							                    		if (gsfDataStores.dbTree.isLoading()) {
															var mask = new Ext.LoadMask(Ext.getBody(), 
																{	msg : _message['waitMessage'], 
																	store : gsfDataStores.dbTree
																}
															);
															mask.show();
														}
							                    		Ext.getCmp('linkToDbTextField').setValue() == '';
														App.setAlert(true, _label['deleteLinkToDb']);
													},
		                        					params : {
		                        						action : 'deleteLinkToDb',
		                        						idField : record.idSchemaField
		                        					},
								                    url: './schemaLinkedFields.json'
												});
								        	}, 
								        	disabled: !eval(rec.get('can_be_deleted_menu')), 
								        	text: _label['deleteLinkToDb']}
								]
							}).showAt(e.getXY())
                    	}
                    }
                }
            ]
        }
        tabArray.push(dbFieldsTab);
    }
	var selectedExtraCheck="";
    var popupSchemaFieldPanel = Ext.create('Ext.tab.Panel', {
        activeTab: 0 ,
        border: false,
        height: 400,
        id: 'popupSchemaFieldPanel' ,
        items: tabArray,
        width: 653,
        buttonAlign : 'center',
        buttons : [
            {text: _message["save"] , x: 225 , width: 100 , y: 294 ,
                handler: function() {
                    if (Ext.getCmp('name').getValue() == "") {
                        App.setAlert(false , _alert['nameFieldRequired']);
                        return false;
                    }
                    var re = new RegExp('[^-_A-Za-z0-9]');
                    if(Ext.getCmp('name').getValue().match(re)){
                        App.setAlert(false , _message['nameFieldInvalidFormate']);
                        return false;
                    }
                    //checks if first letter of field is (a..z or A..Z or _)
                    if (!validateField(Ext.getCmp('name').getValue(),/^[a-zA-Z_]/)) {
                        Ext.MessageBox.show({
                            title: _label["alert"],
                            buttons: Ext.MessageBox.OK,
                            msg: _message["nameFieldInvalidFormate"],
                            icon: Ext.MessageBox.WARNING
                        });
                        return false;
                    }
                    var fillChar;
                    if ( Ext.getCmp('idFieldType').getValue() == 4) {
                        if (idStreamType == 3){
                            if (!Ext.getCmp('alphanumericSize').getValue()) {
                                App.setAlert(false , _alert['sizeFieldRequired']);
                                return false;
                            }else if (Ext.getCmp('maxLenght').getValue()> Ext.getCmp('alphanumericSize').getValue()) {
                                App.setAlert(false , _alert['invalidMaxCompareValue']);
                                return false;
                            }
                            if (Ext.getCmp('textFillCharAlphanumeric').getValue() != "") {
                                fillChar = Ext.getCmp('textFillCharAlphanumeric').getValue();
                            } else {
                                fillChar = ' ';
                            }
                        }
                        if (Ext.getCmp('minLenght').getValue() && Ext.getCmp('maxLenght').getValue()) {
                            if (Ext.getCmp('minLenght').getValue() > Ext.getCmp('maxLenght').getValue()) {
                                App.setAlert(false , _alert['invalidMinCompareValue']);
                                return false;
                            }
                        }
						
						var selectedExtraCheckLen = secondGrid.getStore().data.length;
						for(i =0;i<selectedExtraCheckLen;i++){
							selectedExtraCheck = selectedExtraCheck+secondGrid.getStore().data.items[i].get('idExtraCheck')+",";
						}						
                    } else if (Ext.getCmp('idFieldType').getValue() == 5) {
                        if (idStreamType == 3){
                            if (!Ext.getCmp('numericSize').getValue()) {
                                App.setAlert(false , _alert['sizeFieldRequired']);
                                return false;
                            }

                            var maxInclusive= "";
                            var maxInclusiveLen= 0;
                            if(Ext.getCmp("numericType").getValue() == 1 && Ext.getCmp('maxInclusiveInt').getValue()) {
                                maxInclusive = Ext.getCmp('maxInclusiveInt').getValue()+'';
                            } else{
                                maxInclusive = Ext.getCmp('maxInclusiveDecimal').getValue()+'';
                            }
                            maxInclusiveLen= maxInclusive.length;
                            if (maxInclusive !== "" && (maxInclusiveLen > Ext.getCmp('numericSize').getValue())){
                                App.setAlert(false , _alert['invalidMaxCompareValue']);
                                return false;
                            }
                            if (Ext.getCmp('textFillCharNumeric').getValue() != "") {
                                fillChar = Ext.getCmp('textFillCharNumeric').getValue();
                            } else {
                                fillChar = ' ';
                            }
                        }
                        if(Ext.getCmp("numericType").getValue() == 1) {
                            if (Ext.getCmp('minInclusiveInt').getValue() && Ext.getCmp('maxInclusiveInt').getValue() ) {
                                if (Ext.getCmp('minInclusiveInt').getValue() > Ext.getCmp('maxInclusiveInt').getValue()) {
                                    App.setAlert(false , _alert['invalidMinInclusiveCompareValue']);
                                    return false;
                                }
                            }
                        } else {
                            if (Ext.getCmp('minInclusiveDecimal').getValue()  && Ext.getCmp('maxInclusiveDecimal').getValue()) {
                                if (Ext.getCmp('minInclusiveDecimal').getValue() > Ext.getCmp('maxInclusiveDecimal').getValue()) {
                                    App.setAlert(false , _alert['invalidMinInclusiveCompareValue']);
                                    return false;
                                }
                            }
                        }
                    } else if (Ext.getCmp('idFieldType').getValue() == 6) {
                        
                    	if (idStreamType == 3) {
                    		if(Ext.getCmp("idDateTimeType").getValue() == 4 || Ext.getCmp("idDateTimeType").getValue() == 5 || Ext.getCmp("idDateTimeType").getValue() == 6 || Ext.getCmp("idDateTimeType").getValue() == 7) {
                    			if (!Ext.getCmp('dateTimeSize').getValue()) {
                    				App.setAlert(false , _alert['sizeFieldRequired']);
                    				return false;
                    			}
                    			if (Ext.getCmp('textFillCharDateTime').getValue() != "") {
                    				fillChar = Ext.getCmp('textFillCharDateTime').getValue();
                    			} else {
                    				fillChar = ' ';
                    			}
                    		}
                    	}
                    }
                    
                    //link to the selected field of the Database fields tree.                    
                    var lCmp = Ext.getCmp('dbTreeSchema');
                    var linkToDbSelection = (lCmp && lCmp.getSelectionModel() && lCmp.getSelectionModel().getSelection()[0])
                    	? lCmp.getSelectionModel().getSelection()[0].get("id") : record.linkToDb;
                    	
                    //if value in the textfield 'DB field' of 'Field detail' tab is deleted -> delete linkToDb in tree
                    if (Ext.getCmp('linkToDbTextField').getValue() == '' && initLinkedFieldValue && initLinkedFieldValue != '') {
                    	linkToDbSelection = '';
                    }
                    	
                    Ext.Ajax.request({
                        params: {
                            idSchema: record.idSchema,
                            idSchemaField: record.idSchemaField,
                            idFieldType: Ext.getCmp('idFieldType').getValue(),
                            name: Ext.getCmp('name').getValue(),
                            description: Ext.getCmp('description').getValue(),
                            minLenght: Ext.getCmp('minLenght').getValue(),
                            maxLenght: Ext.getCmp('maxLenght').getValue(),
                            nillableAlphanumeric: Ext.getCmp('nillableAlphanumeric').getValue(),
                            idAlignAlphanumeric: Ext.getCmp('idAlignAlphanumeric').getValue(),
                            fillCharAlphanumeric: fillChar,
                            minInclusive: Ext.getCmp("numericType").getValue() == 1 ? Ext.getCmp('minInclusiveInt').getValue() : Ext.getCmp("minInclusiveDecimal").getValue(),
                            maxInclusive: Ext.getCmp("numericType").getValue() == 1 ? Ext.getCmp('maxInclusiveInt').getValue() : Ext.getCmp("maxInclusiveDecimal").getValue(),
                            fractionDigits: Ext.getCmp('fractionDigits').getValue(),
                            nillableNumeric: Ext.getCmp('nillableNumeric').getValue(),
                            numericType :Ext.getCmp('numericType').getValue(),
                            idAlignNumeric: Ext.getCmp('idAlignNumeric').getValue(),
                            fillCharNumeric: fillChar,
                            isForecastable : Ext.getCmp('forecastCheckbox').getValue(),
                            maxOccurs : Ext.getCmp('idMaxOccurs').isDisabled() ? 1 : Ext.getCmp('idUnbounded').getValue() ? 0 : Ext.getCmp('idOtherTextField').getValue(),
                            forecastSpeed : Ext.getCmp('forecastSpeedSlider').getValue(),
                            forecastAccuracy : Ext.getCmp('forecastAccuracySlider').getValue(),
                            idDateTimeType: Ext.getCmp('idDateTimeType').getValue(),
                            idDateType: Ext.getCmp('idDateType').getValue(),
                            idTimeType: Ext.getCmp('idTimeType').getValue(),
                            nillableDate: Ext.getCmp('nillableDate').getValue(),
                            idCustomError : Ext.getCmp('customErrorCombo') ? Ext.getCmp('customErrorCombo').getValue() : null,
                            size: (idStreamType == 3 && Ext.getCmp('idFieldType').getValue() == 4) ? Ext.getCmp('alphanumericSize').getValue() : Ext.getCmp('idFieldType').getValue() == 5 ? Ext.getCmp('numericSize').getValue() : Ext.getCmp('dateTimeSize').getValue(),
							linkToDb : linkToDbSelection,
							idAlignDateTime: Ext.getCmp('idAlignDateTime').getValue(),
		                    fillCharDateTime: fillChar,
		                    errorToleranceValue: Ext.getCmp('chkBoxError').getValue() == true ? Ext.getCmp('idSliderField').getValue() : -1,
		                    
		                    // Index incremental
		                    indexIncrementalValue: Ext.getCmp('chkBoxIndexIncremental').getValue() == true ? "true" : "false",

		                    errorType: Ext.getCmp('errorTypeRadioId').getChecked()[0].getSubmitValue(),
                            selectedExtraCheck:selectedExtraCheck
                        },
                        success: function (response) {
                            if (Ext.getCmp('customErrorCombo')) Ext.getCmp('customErrorCombo').reset();
                            var tree = isGenerationStream ? generationStreamFieldsTree : schemaFieldsTree;
                            var currNode = tree.getStore().getNodeById(record.idSchemaField);
                            //currNode.data.text = Ext.getCmp('name').getValue();
                            currNode.set('text', Ext.getCmp('name').getValue());
                            action = "save";
                            if (isAttributeChanged || Ext.decode(response.responseText).extraMessage == 'isLinkedToDbChanged') {
                            	//cutom tree block: field modify
                            	//tree.store.load();
                            	isAttributeChanged = false;
                            } else {
                            	//generated tree block: field modify
                            	//tree.store.load({node : currNode});
                            }               
                            Ext.getCmp('popupSchemaField').close();
                            storeSchemas.load();
                            return;
                        },
                        url: './schemaFieldsPopupUpdate.json'
                    });
                }
            },
            {text: _message["cancel"] , x: 330, width: 100 , y: 294 ,
                handler: function() {
                	if(isAttributeChanged) {
                		var tree = isGenerationStream ? generationStreamFieldsTree : schemaFieldsTree;
                		tree.store.load();
                		isAttributeChanged = false;
                	}                	
                    Ext.getCmp('popupSchemaField').close();
                    if (Ext.getCmp('customErrorCombo')) Ext.getCmp('customErrorCombo').reset();
                }
            },
            {text: "Attributes" , x: 380, width: 100 , y: 294 ,hidden: (idSchemaType == 1 && (idStreamType == 1 || idStreamType == 2)) ? false : true,
                handler: function() {                    	
                	popupAttributesList(record.idSchemaField, idSchemaType);
                }
            }
        ]
    });
    var winClose = false;
    new Ext.Window( {
		border : false,
		height : 440,
		disabled : true,
		id : 'popupSchemaField',
		items : popupSchemaFieldPanel,
		layout : 'absolute',
		bodyStyle:{"background-color":"#ffffff"},
		listeners : {
			beforeclose : function(panel, options) {
				winClose = true;
				if (action == 'add') {
					deleteSchemaField();
				}
			},
			beforedestroy : function(panel, options) {
				if (winClose == false) {
					if (action == 'add') {
						deleteSchemaField();
					}
				}
			}
        },
        modal: true,
        resizable: false,
        title: _label['trackField'],
        width: 665
    }).show();


    var recordId;
    if(record.data!='undefined'){
        recordId = record.id;
    }else{
        recordId = record.get('id');
    }

    Ext.Ajax.request({
        params: {
            idSchemaField: recordId
        },
        success: function (response) {
            record = Ext.decode(response.responseText);
            Ext.getCmp('name').setValue(record.name);
            
            if(record.parent && (record.parent.idFieldType == 2 || record.parent.idFieldType == 3)) {
            	
            	Ext.getCmp('idMaxOccurs').setDisabled(false);
               	Ext.getCmp('idUnbounded').setDisabled(false);
               	Ext.getCmp('idOther').setDisabled(false);
               	Ext.getCmp('idOtherTextField').setDisabled(false); 
            	
               	if(record.name != null) {
               		
            		if(record.maxOccurs > 0) {
                		Ext.getCmp('idOther').setValue(true);
                		Ext.getCmp('idOtherTextField').setValue(record.maxOccurs);
                	} else {            		
                		Ext.getCmp('idOtherTextField').setDisabled(true);
                		Ext.getCmp('idUnbounded').setValue(true);
                	}
            	}
            	           	
            } else {
            		
            	Ext.getCmp('idMaxOccurs').setDisabled(true);
               	Ext.getCmp('idUnbounded').setDisabled(true);
               	Ext.getCmp('idOther').setDisabled(true);
               	Ext.getCmp('idOtherTextField').setDisabled(true);           	
            }            
            
            Ext.getCmp('idFieldType').setValue(record.idFieldType);
            Ext.getCmp('description').setValue(record.description);
            initLinkedFieldValue = record.linkToDb;
            Ext.getCmp('linkToDbTextField').setValue(record.linkToDb);
            Ext.getCmp('errorTypeRadioId').getComponent(0).setValue(record.errorType == 0);
            Ext.getCmp('errorTypeRadioId').getComponent(1).setValue(record.errorType == 2);

            if (idStreamType != 3) {
                Ext.getCmp("idAlignAlphanumericLabel").setVisible(false);
                Ext.getCmp("idAlignAlphanumeric").setVisible(false);
                Ext.getCmp("fillCharAlphanumericLabel").setVisible(false);
                Ext.getCmp("fillCharAlphanumeric").setVisible(false);
                Ext.getCmp("textFillCharAlphanumeric").setVisible(false);
                Ext.getCmp("idAlignNumericLabel").setVisible(false);
                Ext.getCmp("idAlignNumeric").setVisible(false);
                Ext.getCmp("fillCharNumericLabel").setVisible(false);
                Ext.getCmp("fillCharNumeric").setVisible(false);
                Ext.getCmp("textFillCharNumeric").setVisible(false);
                Ext.getCmp("alphanumericSizeLabel").setVisible(false);
                Ext.getCmp("alphanumericSize").setVisible(false);
                Ext.getCmp("numericSizeLabel").setVisible(false);
                Ext.getCmp("numericSize").setVisible(false);

                Ext.getCmp("nillableAlphanumericLabel").setPosition(5, 50);
                Ext.getCmp("nillableAlphanumeric").setPosition(5, 70);
                
                Ext.getCmp("nillableAlphanumeric").setWidth(100);

                Ext.getCmp("nillableNumericLabel").setPosition(5, 95);
                Ext.getCmp("nillableNumeric").setPosition(5, 115);

            } else {
            	Ext.getCmp("idAlignAlphanumericLabel").setVisible(false);
                Ext.getCmp("idAlignAlphanumeric").setVisible(false);
                Ext.getCmp("idAlignNumericLabel").setVisible(false);
                Ext.getCmp("idAlignNumeric").setVisible(false);
                Ext.getCmp("fillCharAlphanumericLabel").setVisible(false);
                Ext.getCmp("fillCharAlphanumeric").setVisible(false);
                Ext.getCmp("textFillCharAlphanumeric").setVisible(false);
                Ext.getCmp("nillableAlphanumericLabel").setVisible(false);
                Ext.getCmp("nillableAlphanumeric").setVisible(false);//.setWidth(100);
                Ext.getCmp("nillableNumericLabel").setVisible(false);
                Ext.getCmp("nillableNumeric").setVisible(false);
                Ext.getCmp("nillableDateLabel").setVisible(false);
                Ext.getCmp('nillableDate').setVisible(false);

            }
            if (record.idFieldType == 1 || record.idFieldType == 2 || record.idFieldType == 3) {
                Ext.getCmp("alphanumericTab").setDisabled(true);
                Ext.getCmp("numericTab").setDisabled(true);
                Ext.getCmp("dateTimeTab").setDisabled(true);
            } else if (record.idFieldType == 4) {//alphanumeric

                Ext.getCmp("alphanumericTab").setDisabled(false);
                Ext.getCmp("numericTab").setDisabled(true);
                Ext.getCmp("dateTimeTab").setDisabled(true);
                Ext.getCmp('idSchema').setValue(record.idSchema);
                Ext.getCmp('idSchemaField').setValue(record.idSchemaField);
                Ext.getCmp('minLenght').setValue(record.minLength);
                Ext.getCmp('maxLenght').setValue(record.maxLength);
                Ext.getCmp('alphanumericSize').setValue(record.size);
                                
                if (idStreamType == 3) {
                    Ext.getCmp('idAlignAlphanumeric').setValue(record.idAlign);
                    if (record.fillChar == ' ') {
                        Ext.getCmp('fillCharAlphanumeric').inputValue=1;//setValue(1);
                        Ext.getCmp('textFillCharAlphanumeric').setDisabled(true);
                        Ext.getCmp('textFillCharAlphanumeric').setValue("");
                    } else {
                        Ext.getCmp("otherAlphanumeric").setValue(true);
                        Ext.getCmp('fillCharAlphanumeric').inputValue=2;//setValue(2);
                        Ext.getCmp('textFillCharAlphanumeric').setDisabled(false);
                        Ext.getCmp('textFillCharAlphanumeric').setValue(record.fillChar);
                    }
                }else{
                    if(record.nillable==true){
                        Ext.getCmp('nillableAlphanumeric').setValue(1);
                    }else{
                        Ext.getCmp('nillableAlphanumeric').setValue(0);
                    }
                }
                storeAlphanumericFieldValues.load({params:{idSchemaField: record.idSchemaField}});
            } else if (record.idFieldType == 5) {//numeric
                Ext.getCmp("alphanumericTab").setDisabled(true);
                Ext.getCmp("numericTab").setDisabled(false);
                Ext.getCmp("dateTimeTab").setDisabled(true);
                Ext.getCmp('idSchema').setValue(record.idSchema);
                Ext.getCmp('idSchemaField').setValue(record.idSchemaField);                
                Ext.getCmp('numericSize').setValue(record.size);
                Ext.getCmp('forecastCheckbox').setValue(record.isForecastable);
                Ext.getCmp('forecastAccuracySlider').setValue(record.forecastAccuracy);
                Ext.getCmp('forecastSpeedSlider').setValue(record.forecastSpeed);

                Ext.getCmp('numericType').setValue(record.idNumericType);
                Ext.getCmp("fractionDigits").setValue(record.fractionDigits);

                if(record.errorToleranceValue == -1) {
                	Ext.getCmp("idSliderField").setValue(0);                	
                	Ext.getCmp('idSliderField').setDisabled(true);
                	Ext.getCmp("chkBoxError").setValue(false);
                } else {
                	Ext.getCmp("idSliderField").setValue(record.errorToleranceValue);                	
                	Ext.getCmp('idSliderField').setDisabled(true);
                	Ext.getCmp("chkBoxError").setValue(true);
                }
                
            	Ext.getCmp("chkBoxIndexIncremental").setValue(record.indexIncremental);

                if(Ext.getCmp("numericType").getValue() == 1) {
                    Ext.getCmp("fractionDigitsLabel").setDisabled(true);
                    Ext.getCmp("fractionDigits").setDisabled(true);
                	Ext.getCmp('minInclusiveInt').setValue(record.minInclusive);
                	Ext.getCmp('maxInclusiveInt').setValue(record.maxInclusive);                	
                } else {
                    Ext.getCmp("fractionDigitsLabel").setDisabled(false);
                    Ext.getCmp("fractionDigits").setDisabled(false);
                	Ext.getCmp("minInclusiveDecimal").setValue(record.minInclusive);
                	Ext.getCmp("maxInclusiveDecimal").setValue(record.maxInclusive);
                	
                	Ext.getCmp("minInclusiveInt").setVisible(false);
                	Ext.getCmp("maxInclusiveInt").setVisible(false);
                	Ext.getCmp("minInclusiveDecimal").setVisible(true);
                	Ext.getCmp("maxInclusiveDecimal").setVisible(true);
                	
                }
                if (idStreamType == 3) {
                    Ext.getCmp('idAlignNumeric').setValue(record.idAlign);
                    if (record.fillChar == ' ') {
                        Ext.getCmp('fillCharNumeric').inputValue=1;//setValue(1);
                        Ext.getCmp('textFillCharNumeric').setDisabled(true);
                        Ext.getCmp('textFillCharNumeric').setValue("");
                    } else {
                        Ext.getCmp("otherNumeric").setValue(true);
                        Ext.getCmp('fillCharNumeric').inputValue=2;//setValue(2);
                        Ext.getCmp('textFillCharNumeric').setDisabled(false);
                        Ext.getCmp('textFillCharNumeric').setValue(record.fillChar);
                    }
                }else{
                    if(record.nillable==true){
                        Ext.getCmp('nillableNumeric').setValue(1);
                    }else{
                        Ext.getCmp('nillableNumeric').setValue(0);
                    }
                }

                storeNumericFieldValues.load({params:{idSchemaField: record.idSchemaField}});
            } else if (record.idFieldType == 6) {//date
                Ext.getCmp("alphanumericTab").setDisabled(true);
                Ext.getCmp("numericTab").setDisabled(true);
                Ext.getCmp("dateTimeTab").setDisabled(false);
                Ext.getCmp('idSchemaField').setValue(record.idSchemaField);
                if (record.idDateType != null && record.idTimeType != null) {
                    Ext.getCmp('idDateTimeType').setValue(1);
                    Ext.getCmp("dateTypeLabel").setDisabled(false);
                    Ext.getCmp("idDateType").setDisabled(false);
                    Ext.getCmp('idDateType').setValue(record.idDateType);
                    Ext.getCmp("timeTypeLabel").setDisabled(false);
                    Ext.getCmp("idTimeType").setDisabled(false);
                    Ext.getCmp('idTimeType').setValue(record.idTimeType);
                } else if (record.idDateType != null) {
                    Ext.getCmp('idDateTimeType').setValue(2);
                    Ext.getCmp("dateTypeLabel").setDisabled(false);
                    Ext.getCmp("idDateType").setDisabled(false);
                    Ext.getCmp('idDateType').setValue(record.idDateType);
                    Ext.getCmp("timeTypeLabel").setDisabled(true);
                    Ext.getCmp("idTimeType").setDisabled(true);
                } else if (record.idTimeType != null) {
                    Ext.getCmp('idDateTimeType').setValue(3);
                    Ext.getCmp("dateTypeLabel").setDisabled(true);
                    Ext.getCmp("idDateType").setDisabled(true);
                    Ext.getCmp("timeTypeLabel").setDisabled(false);
                    Ext.getCmp("idTimeType").setDisabled(false);
                    Ext.getCmp('idTimeType').setValue(record.idTimeType);
                } else {
                	if (idStreamType == 3) {
                		Ext.getCmp("idAlignDateTimeLabel").setVisible(true);
                		Ext.getCmp("idAlignDateTime").setVisible(true);
                		Ext.getCmp("fillCharDateTimeLabel").setVisible(true);
                		Ext.getCmp("fillCharDateTime").setVisible(true);
                		Ext.getCmp("textFillCharDateTime").setVisible(true);
                		Ext.getCmp("dateTimeSizeLabel").setVisible(true);
                		Ext.getCmp("dateTimeSize").setVisible(true);
                		
                		Ext.getCmp("idAlignDateTime").setValue(record.idAlign);
                		Ext.getCmp("dateTimeSize").setValue(record.size);
                		
                		if (record.fillChar == ' ') {
                            Ext.getCmp('fillCharDateTime').inputValue=1;
                            Ext.getCmp('textFillCharDateTime').setDisabled(true);
                            Ext.getCmp('textFillCharDateTime').setValue("");
                        } else {
                            Ext.getCmp("otherDateTime").setValue(true);
                            Ext.getCmp('fillCharDateTime').inputValue=2;//setValue(2);
                            Ext.getCmp('textFillCharDateTime').setDisabled(false);
                            Ext.getCmp('textFillCharDateTime').setValue(record.fillChar);
                        }
                	}
                    
                    if (record.idDateTimeType == 4) {
                        Ext.getCmp('idDateTimeType').setValue(4);
                    }else if (record.idDateTimeType == 5) {
                        Ext.getCmp('idDateTimeType').setValue(5);
                    }else if (record.idDateTimeType == 6) {
                        Ext.getCmp('idDateTimeType').setValue(6);
                    }else if (record.idDateTimeType == 7) {
                        Ext.getCmp('idDateTimeType').setValue(7);
                    }
                    Ext.getCmp("dateTypeLabel").setDisabled(true);
                    Ext.getCmp("idDateType").setDisabled(true);
                    Ext.getCmp("timeTypeLabel").setDisabled(true);
                    Ext.getCmp("idTimeType").setDisabled(true);
                }
                if (idStreamType != 3) {
                    if(record.nillable==true){
                        Ext.getCmp('nillableDate').setValue(1);
                    }else{
                        Ext.getCmp('nillableDate').setValue(0);
                    }
                }
            }
            Ext.getCmp("popupSchemaField").setDisabled(false);
            addCustomErrorCombo(schemId, fieldType, record.idCustomError);
            
            Ext.Ajax.request( {
                params : {
                    schemaId : schemId
                },
                url : './schemaFieldIsForecasted.json',
                success : function(result) {
                    recordResult = Ext.JSON.decode(result.responseText);
                    Ext.getCmp("forecastingSlidesPanel").setDisabled(
                        !eval(recordResult.isForecasted));
                }
            });
            if (isGenerationStream) {
				gsfDataStores.dbTree.proxy.extraParams.idSchema = schemId;
				gsfDataStores.dbTree.proxy.extraParams.fieldId = recordId;
				//setRootNode is an analog to store.load()
				gsfDataStores.dbTree.setRootNode({
					id: '0',
					leaf: false,
					expanded: true 
				});	
				var j = 0;
				var counter = 0;
				var check = function() { 
					var node = gsfDataStores.dbTree.getNodeById(record.linkToDb);
					if (node) {
						node.bubble(function(node) {
							//set green color to connected node only to the leaf node
							if (j++ == 0) { 
								node.set('cls', 'linked_node_to_db_green');
							}
							node.expand();
						});			
					} else {
						if (counter++ < 50) Ext.defer(check, 100);
					}
				};
				check.call(this);
            }
        },
        url: './schemaFieldsPopupRead.json'
    });
}

function addCustomErrorCombo(schemId, fieldType, valueCE) {
	var customErrorsStore = Ext.data.StoreManager.lookup('customErrorsStore');
	var isEEVersion = new Boolean();
	isEEVersion = customErrorsStore;
	if (isEEVersion) {
		customErrorsStore.proxy.extraParams.schemaId = schemId;
		customErrorsStore.proxy.extraParams.isCombo = 'true';
		customErrorsStore.load();
	}
	if (fieldType == 'branch') {
		if (Ext.getCmp('customErrorLabel')) {
			Ext.getCmp('customErrorLabel').hide();
		}
	}
	var cmp = Ext.getCmp('mainTab');	
	var checkSchemaFieldsPopupEE = function() {
		if (cmp) {
			var label = new Ext.form.field.Display( {
				value : _message['customErrors'],
				id : 'customErrorLabel',
				x : 340,
				xtype : 'displayfield',
				y : 45,
				disabled : !isEEVersion
			});
			var combos = new Ext.form.field.ComboBox({
						id : 'customErrorCombo',
						displayField : 'name',
						queryMode : 'local',
						store : customErrorsStore,
						valueField : 'id',
						value : valueCE == 0 ? '' : valueCE, 
						x : 340,
						xtype : 'combo',
						width : 150,
						y : 65,
						disabled : !isEEVersion,
						listeners : {
							select : function(combo, records) {
								//if 'add new' selected, -7 code of this option
								if (records[0].get('id') == -7) {
									//clearValue(): remove select from 'add new'
									combo.clearValue();
									customErrorsHandlers.addCustomError(customErrorsStore, schemId);
								}
							}
						} 
			});
			cmp.insert(3, label);
			cmp.insert(4, combos);
		} else {
			Ext.defer(checkSchemaFieldsPopupEE, 100);
		}
	};
	//Ext.defer(checkSchemaFieldsPopupEE, 1000);
	checkSchemaFieldsPopupEE.call(this);
}