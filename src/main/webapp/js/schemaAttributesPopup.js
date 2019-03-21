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

/*
 * Popup schema attribute.
 *
 * @param {Object} record
 * @param {Object} action
 * @param {Object} attributeType
 * @param {boolean} isGenerationStream: true for schemas generation, false for schema validation
 * @return {TypeName}
 */
function popupSchemaAttribute(record, action, schemaTypeId) {
    	
    var isGenerationStream = false;
    if(schemaTypeId == 2) {
    	isGenerationStream = true;
    }
    
	function addAlphanumericAttributeValues() {
        editorAlphanumericAttributeValues.cancelEdit();
        Ext.MessageBox.show({
            title: _label["newValue"],
            prompt: true,
            buttons: Ext.MessageBox.OKCANCEL,
            fn: function(button , value) {
                if (button == 'ok') {
                    var record = new alphanumericAttributeValuesGrid.store.model();
                    record.set('idSchema' , Ext.getCmp('idAttributeSchema').getValue());
                    record.set('idAlphanumericSchemaField' , Ext.getCmp('idSchemaAttribute').getValue());
                    record.set('value' , value);
                    alphanumericAttributeValuesGrid.store.insert(0 , record);
                    alphanumericAttributeValuesGrid.store.sync();
                }
            }
        });
    };

    function deleteAlphanumericAttributeValues() {
        var record = alphanumericAttributeValuesGrid.getSelectionModel().getSelection()[0];
        if (!record) {
            App.setAlert(false , _alert['selectRecord']);
            return false;
        };
        alphanumericAttributeValuesGrid.store.remove(record);
        alphanumericAttributeValuesGrid.store.sync();
    };
    function addNumericAttributeValues() {
        editorNumericAttributeValues.cancelEdit();
        Ext.MessageBox.show({
            title:_label["newValue"],
            prompt: true,
            buttons: Ext.MessageBox.OKCANCEL,
            fn: function(button , value) {
                if (button == 'ok') {
                    var record = new numericAttributeValuesGrid.store.model();
                    record.set('idSchema' , Ext.getCmp('idAttributeSchema').getValue());
                    record.set('idNumericSchemaField' , Ext.getCmp('idSchemaAttribute').getValue());
                    record.set('value' , value);
                    numericAttributeValuesGrid.store.insert(0 , record);
                    numericAttributeValuesGrid.store.sync();
                }
            }
        });
    };

    function deleteNumericAttributeValues() {
        var record = numericAttributeValuesGrid.getSelectionModel().getSelection()[0];
        if (!record) {
            App.setAlert(false , _alert['selectRecord']);
            return false;
        };
        numericAttributeValuesGrid.store.remove(record);
        numericAttributeValuesGrid.store.sync();
    };
    
    function deleteSchemaAttribute() {  	    	
    	
    	Ext.Ajax.request({
    			params: {
    				idSchemaField: idSchemaField
    			},
    			url: 'schemaFieldsTreePopupDestroy.json'
    	});    		
    };
    
    var storeAttributeType =  Ext.create('Ext.data.ArrayStore', {
        data : [[4 , _message['alphanumeric']] , [5 , _message['numeric']] , [6 , _label['date']]],
        fields: ['idFieldType' , 'name']
    });

    var attributeGrid = isGenerationStream ? generationStreamGrid : schemasGrid;
    var idAttributeSchemaType = attributeGrid.getSelectionModel().getSelection()[0].get('idStreamType');
    var attributeSchemaId = attributeGrid.getSelectionModel().getSelection()[0].get("idSchema");

    var mainAttributeTabItem = [
        {value:_label['name']  , x: 5 , xtype: 'displayfield' , y: 5},
        {id: 'attributeName' , x: 5 , xtype: 'textfield' , width: 150 , y: 25},
        {id: 'idAttributeTypeLabel' , value:_message['type'] , x: 165 , xtype: 'displayfield' , y: 5},
        {displayField:'name' , id: 'idAttributeType' , queryMode: 'local' , store: storeAttributeType , triggerAction: 'all' , valueField: 'idFieldType' , x: 165 , xtype: 'combo', width: 150 , y: 25 ,forceSelection: true,
            listeners: {
                select: function() {
                	
                    if (Ext.getCmp('idAttributeType').getValue() == 4) {
                        Ext.getCmp("alphanumericAttributeTab").setDisabled(false);
                        Ext.getCmp("numericAttributeTab").setDisabled(true);
                        Ext.getCmp("dateAttributeTimeTab").setDisabled(true);

                        if (idAttributeSchemaType == 3) {
                            Ext.getCmp("extraAttributeCheck").setValue(1);
                            if (Ext.getCmp('idAttributeAlignAlphanumeric').getValue() == "") {
                                Ext.getCmp('idAttributeAlignAlphanumeric').setValue(1);
                                Ext.getCmp('fillAttributeCharAlphanumeric').inputValue=1;//setValue(1);
                                Ext.getCmp('textAttributeFillCharAlphanumeric').setDisabled(true);
                                Ext.getCmp('textAttributeFillCharAlphanumeric').setValue("");
                            }
                        } else {
                            if (Ext.getCmp("nillableAttributeAlphanumeric").getValue() == "") {
                                Ext.getCmp("nillableAttributeAlphanumeric").setValue(0);
                                Ext.getCmp("extraAttributeCheck").setValue(1);
                            }
                            Ext.getCmp("alphanumericAttributeSizeLabel").setVisible(false);
                            Ext.getCmp("alphanumericAttributeSize").setVisible(false);

                            Ext.getCmp("nillableAttributeAlphanumericLabel").setPosition(225, 5);
                            Ext.getCmp("nillableAttributeAlphanumeric").setPosition(225, 25);
                            Ext.getCmp("extraAttributeCheckLabel").setPosition(390, 5);
                            Ext.getCmp("extraAttributeCheck").setPosition(390, 25);
                            Ext.getCmp("nillableAttributeAlphanumeric").setWidth(100);
                        }

                    } else if(Ext.getCmp("idAttributeType").getValue() == 5) {
                        Ext.getCmp("alphanumericAttributeTab").setDisabled(true);
                        Ext.getCmp("numericAttributeTab").setDisabled(false);
                        Ext.getCmp("dateAttributeTimeTab").setDisabled(true);
                        if (Ext.getCmp("nillableAttributeNumeric").getValue() == "" || Ext.getCmp("nillableAttributeNumeric").getValue() ==null) {
                            Ext.getCmp('numericAttributeType').setValue(1);
                            Ext.getCmp("fractionAttributeDigitsLabel").setDisabled(true);
                            Ext.getCmp("fractionAttributeDigits").setDisabled(true);
                            Ext.getCmp("fractionAttributeDigits").setValue("");
                            Ext.getCmp("nillableAttributeNumeric").setValue(0);
                        }
                        if (idAttributeSchemaType == 3) {
                            if (Ext.getCmp('idAttributeAlignNumeric').getValue() == "" || Ext.getCmp('idAttributeAlignNumeric').getValue() == null) {
                                Ext.getCmp('idAttributeAlignNumeric').setValue(1);
                                Ext.getCmp('fillAttributeCharNumeric').inputValue=1;//setValue(1);
                                Ext.getCmp('textAttributeFillCharNumeric').setDisabled(true);
                                Ext.getCmp('textAttributeFillCharNumeric').setValue("");
                            }
                        } else {

                            Ext.getCmp("numericAttributeSizeLabel").setVisible(false);
                            Ext.getCmp("numericAttributeSize").setVisible(false);

                            Ext.getCmp("nillableAttributeNumericLabel").setPosition(325, 5);
                            Ext.getCmp("nillableAttributeNumeric").setPosition(325, 25);
                        }
                    } else if(Ext.getCmp("idAttributeType").getValue() == 6) {
                        Ext.getCmp("alphanumericAttributeTab").setDisabled(true);
                        Ext.getCmp("numericAttributeTab").setDisabled(true);
                        Ext.getCmp("dateAttributeTimeTab").setDisabled(false);
                        if (Ext.getCmp("nillableAttributeDate").getValue() == "" || Ext.getCmp("nillableAttributeDate").getValue() == null) {
                            Ext.getCmp('idAttributeDateTimeType').setValue(1);
                            Ext.getCmp("dateAttributeTypeLabel").setDisabled(false);
                            Ext.getCmp("idAttributeDateType").setDisabled(false);
                            Ext.getCmp('idAttributeDateType').setValue(1);
                            Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
                            Ext.getCmp("idAttributeTimeType").setDisabled(false);
                            Ext.getCmp('idAttributeTimeType').setValue(1);
                            Ext.getCmp("nillableAttributeDate").setValue(0);
                        }
                    }
                }
            }
        },
		{xtype : 'fieldset', title : _label['relevance'], x : 540, y : 5, width : 93, height : 70,
        	padding : '0 0 0 5' /* need to set all paddings manually, otherwise default values*/,
			items : [
				{ xtype : 'radiogroup', columns : 1, id : 'errorTypeAttrRadioId',
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
        {value: _label['description'] , x: 5 , xtype: 'displayfield' , y: 90},
        {height: 220 , id:'attributeDescription' , x: 5 , xtype: 'htmleditor' , width: 630 , y: 110}
    ];

    var mainAttributeTab = {
        items: mainAttributeTabItem,
        id: 'mainAttributeTab',
        frame: false,
        layout: 'absolute',
        title: _label['attributeDetail'],
        xtype: 'panel'
    };

    var storeAttributeNillable =  Ext.create('Ext.data.ArrayStore', {
        data : [[0 ,_message['notOptional']] , [1 ,_message['optional'] ]],
        fields: ['idNillable' , 'name']
    });

    if (!Ext.ModelManager.isRegistered('spellAttributeRecords')){
        Ext.define('spellAttributeRecords', {
            extend: 'Ext.data.Model',
            fields: [
                {name: 'idExtraCheck', mapping: 'idCheckType'},
                {name: 'name', mapping: 'name'}
            ]
        });
    }

    var storeAttributeExtraCheck = new Ext.data.Store({
        autoSave: false,
        autoLoad: false,
        model: 'spellAttributeRecords',
        proxy: {
            type: 'ajax',
            api: {
                read    : './checksTypeRead.json'
            },
            reader: {
                type: 'json',
                root: 'results',
                idProperty: 'idCheckType'
            }
        },
        listeners: {
            load: function(store,records,options) {

                var sRecord = Ext.create('spellAttributeRecords', {
                    idExtraCheck: 0,
                    name: ''
                });
                storeAttributeExtraCheck.add(sRecord);
                storeAttributeExtraCheck.sort('idExtraCheck', 'ASC');
            }
        }
    });
    //storeAttributeExtraCheck.load();
    var comboAttributeChecks = new Ext.form.field.ComboBox({
        id: 'extraAttributeCheck' ,
        displayField:'name' ,
        valueField: 'idExtraCheck' ,
        store: storeAttributeExtraCheck ,
        queryMode: 'local' ,
        triggerAction: 'all' ,
        x: 450 , width: 250 , y: 25,
        forceSelection: true ,
        value: record.idCheckType,
        listeners: {
            afterrender:function(component, options){
                Ext.getCmp('extraAttributeCheck').setValue(record.idCheckType);
                if (Ext.getCmp("extraAttributeCheck").getValue() == "") {
                    Ext.getCmp("extraAttributeCheck").setValue(0);
                }
            }
        }
    });
	var idSF = record.id;
	if(action == 'modify'){
		idSF = record.data.idSchemaField;
	}
	var storeExtraCheck = new Ext.data.Store({
        autoSave: false,
        autoLoad: true,
        model: 'spellRecords',
        proxy: {
            type: 'ajax',
            api: {
                read    : './checksTypeRead.json?leftPane=true&idSchemaField='+idSF
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
        {text: "Extra Check", flex: 1, dataIndex: 'name',id:'extraCheckName',autoScroll:true, renderer: columnWrap}
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
        }, x: 340 , width: 150 , y: 5,autoScroll: true,height:280,
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
                read    : './checksTypeRead.json?leftPane=false&idSchemaField='+idSF
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
        }, x: 500 , width: 150 , y: 5,autoScroll: true,height:280,
        store            : secondGridStore,
        columns          : columns,
        stripeRows       : true,
		title			  :'Extra Check Selected',
        margins          : '0 2 0 0'
    });

    var storeAttributeNumericType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 ,_message['integer']] , [2 ,_message['decimal']]],
        fields: ['idNumericType' , 'name']
    });


    var storeAttributeAlign =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['left']] , [2 , _message['right']]],
        fields: ['idAlign' , 'name']
    });

    var storeAttributeAlignForNumeric =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['left']] , [2 , _message['right']]],
        fields: ['idAlign' , 'name']
    });

    var storeAttributeDateTimeType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , _message['dateAndTime']] , [2 ,  _message['date']] , [3 , _message['hour']],
            [4 , _message['XSDdateAndTime']] , [5 ,  _message['XSDdate']] , [6 , _message['XSDhour']], [7 , _message['timestampUnix']]],
        fields: ['idAttributeDateTimeType' , 'name']
    });
    var storeAttributeDateType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , 'dd/MM/yyyy'] , [2 , 'dd-MM-yyyy'] , [3 , 'dd.MM.yyyy'] , [10 , 'ddMMyyyy'] , [4 , 'dd/MM/yy'] , [5 , 'dd-MM-yy'] , [6 , 'dd.MM.yy'] , [11 , 'ddMMyy'] , [7 , 'yyyy/MM/dd'] , [8 , 'yyyy-MM-dd'] , [9 , 'yyyy.MM.dd'] , [12 , 'yyyyMMdd']],
        fields: ['idAttributeDateType' , 'name']
    });

    var storeAttrinbuteTimeType =  Ext.create('Ext.data.ArrayStore', {
        data : [[1 , 'hh:mm:ss'], [2 , 'hh.mm.ss'],[3 , 'hh:mm'],[4 , 'hh.mm'],[5 , 'hh:mm:ss AM/PM'],
                [6 , 'hh.mm.ss AM/PM'],[7 , 'h:mm:ss'], [8 , 'h.mm.ss']],
        fields: ['idAttributeTimeType' , 'name']
    });

    if (!Ext.ModelManager.isRegistered('alphanumericAttributeValues')){
        Ext.define('alphanumericAttributeValues', {
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


    var storeAlphanumericAttributeValues = new Ext.data.Store({
        autoSave: false,
        model: 'alphanumericAttributeValues',
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
    storeAlphanumericAttributeValues.proxy.addListener('exception', function (proxy, response, operation) {
        if (response) {
            var responseObj = Ext.decode(response.responseText);
            App.setAlert(false , responseObj.message);
            if (operation.action == 'create') {
                storeAlphanumericAttributeValues.load();
            } else {
                storeAlphanumericAttributeValues.remove();
            }
        }
    });
    var editorAlphanumericAttributeValues = Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 1,
        listeners: {
            afteredit: function() {
                alphanumericAttributeValuesGrid.store.sync();
            }
        }
    });
    var columnsAlphanumericAttributeValues = [
        {align: 'center' , dataIndex: 'idAlphanumericFieldValue' , header: 'Id' , sortable: true , width: 50},
        {dataIndex: 'value' , editor: {xtype:'textfield'} , header: _label['value'] ,  sortable: true , width: 450}
    ];

    var tbarAlphanumericAttributeValues = [
        {iconCls: 'schema_add' , handler: addAlphanumericAttributeValues , text: _message['add']} , '-' ,
        {iconCls: 'schema_delete' , handler: deleteAlphanumericAttributeValues , text: _message['delete']}
    ];

    var alphanumericAttributeValuesGridHeight;
    var alphanumericAttributeValuesGridY;

    alphanumericAttributeValuesGridHeight = 250;
    alphanumericAttributeValuesGridY = 60;

    var alphanumericAttributeValuesGrid =  Ext.create('Ext.grid.Panel', {
        columnLines: true,
        columns: columnsAlphanumericAttributeValues,
        frame: false,
        id: 'alphanumericAttributeValuesGrid',
        plugins: [editorAlphanumericAttributeValues],
        selModel: Ext.create('Ext.selection.RowModel', {
            mode:'SINGLE'
        }),
        store: storeAlphanumericAttributeValues,
        tbar: tbarAlphanumericAttributeValues,
        title: _label['allowedValues'],
        
		height: 230,
		width:320,
        x: 5,
        y: 60
    });


    var alphanumericAttributeTab = {
        items: [
	        {id: 'idAttributeSchema' , xtype: 'hidden'},
	        {id: 'idSchemaAttribute' , xtype: 'hidden'},
	        {value:_message['minLength'] , x: 5 , xtype: 'displayfield' , y: 5},
	        {id: 'minLength' , x: 5 , xtype: 'numberfield' , hideTrigger: true, minValue: 1, width: 100 , y: 25, allowDecimals : false},
	        {value:_message['maxLength'] , x: 115 , xtype: 'displayfield' , y: 5},
	        {id: 'maxLength' , x: 115 , xtype: 'numberfield' , hideTrigger: true, minValue: 1, width: 100 , y: 25, allowDecimals : false},
	        {id: 'alphanumericAttributeSizeLabel' , value:_message['sizeField'] , x: 225 , xtype: 'displayfield' , y: 5},
	        {id: 'alphanumericAttributeSize' , x: 225 , xtype: 'numberfield', hideTrigger: true, minValue: 1, allowBlank: false, width: 100 , y: 25, value:'1', allowDecimals : false},
	        {id: 'nillableAttributeAlphanumericLabel' , value:_message['optional'] , x: 285 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'nillableAttributeAlphanumeric' , queryMode: 'local' , store: storeAttributeNillable , triggerAction: 'all' , valueField: 'idNillable' , x: 285 , xtype: 'combo', width: 100 , y: 25},
	        firstGrid,			
			secondGrid,
	        {id: 'idAttributeAlignAlphanumericLabel' , value: _message['alignment'] , x: 5 , xtype: 'displayfield' , y: 95},
	        {displayField:'name' , id: 'idAttributeAlignAlphanumeric' , queryMode: 'local' , store: storeAttributeAlign , triggerAction: 'all' , valueField: 'idAlign' , x: 5 , xtype: 'combo', width: 150 , y: 115},
	        {id: 'fillAttributeCharAlphanumericLabel' , value: _message['fillCharacter'] , x: 165 , xtype: 'displayfield' , y: 95},
	        {id: 'fillAttributeCharAlphanumeric' , x: 165 , xtype: 'radiogroup', width: 120 , y: 110 ,
	            items: [{boxLabel: _label['space'] , id: 'spaceAlphanumeric1' , inputValue: 1 , name: 'radioButtonAlphanumeric', checked:true,
	                handler : function(radio, checked) {
	                    if(checked == true) {
	                        Ext.getCmp('textAttributeFillCharAlphanumeric').setDisabled(true);
	                        Ext.getCmp('textAttributeFillCharAlphanumeric').setValue("");
	                    }
	                }
	            },
	                {boxLabel: _label['other'] , id: 'otherAttributeAlphanumeric' , inputValue: 2 , name: 'radioButtonAlphanumeric',
	                    handler : function(radio, checked) {
	                        if(checked == true) {
	                            Ext.getCmp('textAttributeFillCharAlphanumeric').setDisabled(false);
	                            Ext.getCmp('textAttributeFillCharAlphanumeric').setValue("");
	                            Ext.getCmp('textAttributeFillCharAlphanumeric').focus();
	                        }
	                    }
	                }
	            ]
	        },
	        {id: 'textAttributeFillCharAlphanumeric' , x:280 , xtype: 'textfield' , width: 35 , y: 110, disabled: true},
	        alphanumericAttributeValuesGrid
        ],
        id: 'alphanumericAttributeTab',
        frame: false,
        layout: 'absolute',
        title: _message['alphanumeric'],
        xtype: 'panel'
    };

    if (!Ext.ModelManager.isRegistered('numericAttributeValuesModel')){
        Ext.define('numericAttributeValuesModel', {
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

    var storeNumericAttributeValues = new Ext.data.Store({
        autoSave: false,
        model: 'numericAttributeValuesModel',
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
    storeNumericAttributeValues.proxy.addListener('exception', function (proxy, response, operation) {
        if (response) {
            var responseObj = Ext.decode(response.responseText);
            App.setAlert(false , responseObj.message);
            if (operation.action == 'create') {
                storeNumericAttributeValues.load();
            } else {
                storeNumericAttributeValues.remove();
            }
        }
    });
    var editorNumericAttributeValues = Ext.create('Ext.grid.plugin.RowEditing', {
        clicksToEdit: 1,
        listeners: {
            afteredit: function() {
                numericAttributeValuesGrid.store.sync();
            }
        }
    });

    var columnsNumericAttributeValues = [
        {align: 'center' , dataIndex: 'idNumericFieldValue' , header: 'Id' ,   sortable: true , width: 50},
        {dataIndex: 'value' , editor: {xtype:'textfield'} , header: _label['value'] ,  sortable: true , width: 450}
    ];

    var tbarNumericAttributeValues = [
        {iconCls: 'schema_add' , handler: addNumericAttributeValues , text: _message['add']} , '-' ,
        {iconCls: 'schema_delete' , handler: deleteNumericAttributeValues , text: _message['delete']}
    ];
    var numericAttributeValuesGrid = Ext.create('Ext.grid.Panel', {
        columnLines: true,
        columns: columnsNumericAttributeValues,
        frame: false,
        id: 'numericAttributeValuesGrid',
        plugins: [editorNumericAttributeValues],
        selModel: Ext.create('Ext.selection.RowModel', {
            mode:'SINGLE'
        }),
        store: storeNumericAttributeValues,
        tbar: tbarNumericAttributeValues,
        title: _label['allowedValues'],
        height: 160,
        x: 5,
        y: 130
    });

    var numericAttributeTab = {
        items: [
	        {id: 'idAttributeSchema' , xtype: 'hidden'},
	        {id: 'idSchemaAttribute' , xtype: 'hidden'},
	        {value: _message['minInclusive'] , x: 5 , xtype: 'displayfield' , y: 5},
            {id: 'attributeMinInclusiveInt' , x: 5 , xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, allowDecimals : false },
	        {id: 'attributeMinInclusiveDecimal' ,    x: 5 , xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, hidden: true},
	        {value:_message['maxInclusive'] , x: 165 , xtype: 'displayfield' , y: 5},
	        {id: 'attributeMaxInclusiveInt' , x: 165 ,xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, allowDecimals : false},
            {id: 'attributeMaxInclusiveDecimal' , x: 165 ,xtype: 'numberfield' , hideTrigger: true, width: 150 , y: 25, hidden: true},
	        {id: 'numericAttributeSizeLabel' , value:_message['sizeField'] , x: 325 , xtype: 'displayfield' , y: 5},
	        {id: 'numericAttributeSize' , x: 325 , xtype: 'numberfield', hideTrigger: true, minValue: 1,allowBlank: false, width: 50 , y: 25, allowDecimals : false},
	        {id: 'nillableAttributeNumericLabel', value: _message['optional'], x: 385 , xtype: 'displayfield' , y: 5},
	        {displayField: 'name' , id: 'nillableAttributeNumeric' , queryMode: 'local' , store: storeAttributeNillable, triggerAction: 'all' , valueField: 'idNillable' , x: 385 , xtype: 'combo', width: 150 , y: 25},
	        {value: _message['type'] , x: 5 , xtype: 'displayfield' , y: 50},
	        {displayField: 'name' , id: 'numericAttributeType' , queryMode: 'local' , store : storeAttributeNumericType , triggerAction : 'all', valueField : 'idNumericType' , x : 5 , xtype : 'combo' , width : 150 , y : 70 ,
	            listeners : {
	                collapse : function() {
	                    Ext.getCmp('fractionAttributeDigits').setValue("");
                        Ext.getCmp("attributeMinInclusiveInt").setValue("");
                        Ext.getCmp("attributeMinInclusiveDecimal").setValue("");
                        Ext.getCmp("attributeMaxInclusiveInt").setValue("");
                        Ext.getCmp("attributeMaxInclusiveDecimal").setValue("");

                        if (Ext.getCmp("numericAttributeType").getValue() == 1) {
	                        Ext.getCmp("fractionAttributeDigitsLabel").setDisabled(true);
	                        Ext.getCmp("fractionAttributeDigits").setDisabled(true);

                            Ext.getCmp("attributeMinInclusiveInt").setVisible(true);
                            Ext.getCmp("attributeMaxInclusiveInt").setVisible(true);

                            Ext.getCmp("attributeMinInclusiveDecimal").setVisible(false);
                            Ext.getCmp("attributeMaxInclusiveDecimal").setVisible(false);
	                    } else {
	                        Ext.getCmp("fractionAttributeDigitsLabel").setDisabled(false);
	                        Ext.getCmp("fractionAttributeDigits").setDisabled(false);
                            Ext.getCmp('fractionAttributeDigits').setValue("");

                            Ext.getCmp("attributeMinInclusiveDecimal").setVisible(true);
                            Ext.getCmp("attributeMaxInclusiveDecimal").setVisible(true);

                            Ext.getCmp("attributeMinInclusiveInt").setVisible(false);
                            Ext.getCmp("attributeMaxInclusiveInt").setVisible(false);
	                    }
	                }
	            }
	        },
	        {id: 'fractionAttributeDigitsLabel' , value: _message['decimalPlaces'] , x: 165 , xtype: 'displayfield' , y: 50},
	        {
	            xtype : 'fieldset',
	            height : 78,
	            width : 270,
	            id : 'forecastingAttributeSlidesPanel',
	            y : 50,
	            x : 325,
	            title : _label['forecasting'],
	            layout : 'absolute',
	            items : [
	                {
	                    xtype : 'checkboxfield',
	                    id : 'forecastAttributeCheckbox'
	                },
	                Ext.create('Ext.slider.Single', {
	                    x : 40,
	                    width: 200,
	                    value: 50,
	                    id : 'forecastAttributeSpeedSlider',
	                    fieldLabel : 'Speed',
	                    increment: 2,
	                    minValue: 0,
	                    maxValue: 100,
	                    listeners : {
	                        change : function(slider, newVal) {
	                            Ext.getCmp('forecastAttributeAccuracySlider').setValue(100 - newVal);
	                        }
	                    }
	                }),
	                Ext.create('Ext.slider.Single', {
	                    x : 40,
	                    y : 20,
	                    width: 200,
	                    value: 50,
	                    id : 'forecastAttributeAccuracySlider',
	                    fieldLabel : 'Accuracy',
	                    increment: 2,
	                    minValue: 0,
	                    maxValue: 100,
	                    listeners : {
	                        change : function(slider, newVal) {
	                            Ext.getCmp('forecastAttributeSpeedSlider').setValue(100 - newVal);
	                        }
	                    }
	                })
	            ]
	        },
	        {id: 'fractionAttributeDigits' , x: 165 , xtype: 'textfield' , width: 150 , y: 70, disabled: true},
	        {id: 'idAttributeAlignNumericLabel' , value:_message['alignment'] , x: 325 , xtype: 'displayfield' , y: 50},
	        {displayField:'name' , id: 'idAttributeAlignNumeric' , queryMode: 'local' , store: storeAttributeAlignForNumeric , triggerAction: 'all' , valueField: 'idAlign' , x: 325 , xtype: 'combo', width: 150 , y: 70},
	        {id: 'fillAttributeCharNumericLabel' , value:  _message['fillCharacter'] , x: 490 , xtype: 'displayfield' , y: 50},
	        {id: 'fillAttributeCharNumeric' , x: 490 , xtype: 'radiogroup', width: 120 , y: 65 ,
	            items: [{boxLabel: _label['space'] , id: 'spaceAttributeNumeric' , inputValue: 1 , name: 'radioButtonNumeric1', checked:true,
	                handler : function(radio, checked) {
	                    if(checked == true) {
	                        Ext.getCmp('textAttributeFillCharNumeric').setDisabled(true);
	                        Ext.getCmp('textAttributeFillCharNumeric').setValue("");
	                    }
	                }
	            },
	                {boxLabel: _label['other'] , id: 'otherAttributeNumeric' , inputValue: 2 , name: 'radioButtonNumeric1',
	                    handler : function(radio, checked) {
	                        if(checked == true) {
	                            Ext.getCmp('textAttributeFillCharNumeric').setDisabled(false);
	                            Ext.getCmp('textAttributeFillCharNumeric').setValue("");
	                            Ext.getCmp('textAttributeFillCharNumeric').focus();
	                        }
	                    }
	                }
	            ]
	        },
	        {id: 'textAttributeFillCharNumeric' , x:605 , xtype: 'textfield' , width: 35 , y:65, disabled: true},
	        numericAttributeValuesGrid
		],
        id: 'numericAttributeTab',
        frame: false,
        layout: 'absolute',
        title: _message['numeric'] ,
        xtype: 'panel',
        disabled: true
    };

    var dateAttributeTimeTab = {
        items: [
	        {id: 'idAttributeSchema' , xtype: 'hidden'},
	        {id: 'idSchemaAttribute' , xtype: 'hidden'},
	        {value: _message['format'] , x: 5 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idAttributeDateTimeType' , queryMode: 'local' , store: storeAttributeDateTimeType , triggerAction: 'all' , valueField: 'idAttributeDateTimeType' , x: 5 , xtype: 'combo', width: 150 , y: 25 ,
	            listeners: {
	                select: function(){
	                    if (Ext.getCmp('idAttributeDateTimeType').getValue() == 1) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idAttributeDateType").setDisabled(false);
	                        Ext.getCmp("idAttributeDateType").setValue(1);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(false);
	                        Ext.getCmp("idAttributeTimeType").setValue(1);
	                    } else if(Ext.getCmp("idAttributeDateTimeType").getValue() == 2) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idAttributeDateType").setDisabled(false);
	                        Ext.getCmp("idAttributeDateType").setValue(1);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setValue(null);
	                    } else if(Ext.getCmp("idAttributeDateTimeType").getValue() == 3) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setValue(null);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(false);
	                        Ext.getCmp("idAttributeTimeType").setValue(1);
	                    } else if(Ext.getCmp("idAttributeDateTimeType").getValue() == 4 || Ext.getCmp("idAttributeDateTimeType").getValue() == 5 || Ext.getCmp("idAttributeDateTimeType").getValue() == 6 || Ext.getCmp("idAttributeDateTimeType").getValue() == 7) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setValue(null);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setValue(null);
                        }
                    }
                }
	        },
	        {id: 'dateAttributeTypeLabel' , value: _label['dateType'] , x: 165 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idAttributeDateType' , queryMode: 'local' , store: storeAttributeDateType , triggerAction: 'all' , valueField: 'idAttributeDateType' , x: 165 , xtype: 'combo', width: 150 , y: 25},
	        {id: 'timeAttributeTypeLabel' , value: _label['timeType'] , x: 325 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'idAttributeTimeType' , queryMode: 'local' , store: storeAttrinbuteTimeType , triggerAction: 'all' , valueField: 'idAttributeTimeType' , x: 325 , xtype: 'combo', width: 150 , y: 25,
	            listeners: {
	                afterrender:function(component, options){
	                    if(Ext.getCmp("idAttributeDateTimeType").getValue() == 2) {
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setValue(null);
	                    } else if(Ext.getCmp("idAttributeDateTimeType").getValue() == 3) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setValue(null);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(false);
	                    }  else if(Ext.getCmp("idAttributeDateTimeType").getValue() == 4 || Ext.getCmp("idAttributeDateTimeType").getValue() == 5 || Ext.getCmp("idAttributeDateTimeType").getValue() == 6 || Ext.getCmp("idAttributeDateTimeType").getValue() == 7) {
	                        Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setDisabled(true);
	                        Ext.getCmp("idAttributeDateType").setValue(null);
	                        Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setDisabled(true);
	                        Ext.getCmp("idAttributeTimeType").setValue(null);
	                    }
	                }
	            }
	        },
	        {id: 'nillableAttributeDateLabel',value: _message['optional'] , x: 485 , xtype: 'displayfield' , y: 5},
	        {displayField:'name' , id: 'nillableAttributeDate' , queryMode: 'local' , store: storeAttributeNillable, triggerAction: 'all' , valueField: 'idNillable' , x: 485 , xtype: 'combo', width: 150 , y: 25}
        ],
        id: 'dateAttributeTimeTab',
        frame: false,
        layout: 'absolute',
        title: _message['date'],
        xtype: 'panel',
        disabled: true
    };
    var tabAttributeArray = [mainAttributeTab, alphanumericAttributeTab, numericAttributeTab, dateAttributeTimeTab];
    if (isGenerationStream) {
        var dbAttributesTab = {
            frame: false,
            layout: 'fit',
            title: _label['db_fields'],
            xtype: 'panel',
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
                    }
                }
            ]
        }
        tabAttributeArray.push(dbAttributesTab);
    }
	var selectedExtraCheck="";
    var popupSchemaAttributePanel = Ext.create('Ext.tab.Panel', {
        activeTab: 0 ,
        border: false,
        height: 370,
        id: 'popupSchemaAttributePanel' ,
        items: tabAttributeArray,
        width: 653,
        buttonAlign : 'center',
        buttons : [
            {text: _message["save"] , x: 225 , width: 100 , y: 294 ,
                handler: function() {
                	//Ext.getCmp('schemaAttributesGrid').store.insert(0, record);                	          	
                    if (Ext.getCmp('attributeName').getValue() == "") {
                        App.setAlert(false , _alert['nameFieldRequired']);
                        return false;
                    }
                    var re = new RegExp('[^-_A-Za-z0-9]');
                    if(Ext.getCmp('attributeName').getValue().match(re)){
                        App.setAlert(false , _message['nameFieldInvalidFormate']);
                        return false;
                    }
                    //checks if first letter of field is (a..z or A..Z or _)
                    if (!validateField(Ext.getCmp('attributeName').getValue(),/^[a-zA-Z_]/)) {
                        Ext.MessageBox.show({
                            title: _label["alert"],
                            buttons: Ext.MessageBox.OK,
                            msg: _message["nameFieldInvalidFormate"],
                            icon: Ext.MessageBox.WARNING
                        });
                        return false;
                    }
                    var fillChar;
                    var lenMax;
					if ( Ext.getCmp('idAttributeType').getValue() == 4) {
                        if (idAttributeSchemaType == 3){
                            if (!Ext.getCmp('alphanumericAttributeSize').getValue()) {
                                App.setAlert(false , _alert['sizeFieldRequired']);
                                return false;
                            }else if (Ext.getCmp('maxLength').getValue()> Ext.getCmp('alphanumericAttributeSize').getValue()) {
                                App.setAlert(false , _alert['invalidMaxCompareValue']);
                                return false;
                            }
                            if (Ext.getCmp('textAttributeFillCharAlphanumeric').getValue() != "") {
                                fillChar = Ext.getCmp('textAttributeFillCharAlphanumeric').getValue();
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
                    } else if (Ext.getCmp('idAttributeType').getValue() == 5) {
                        if (idAttributeSchemaType == 3){
                            if (!Ext.getCmp('numericAttributeSize').getValue()) {
                                App.setAlert(false , _alert['sizeFieldRequired']);
                                return false;
                            }
                            var maxInclusive= "";
                            var maxInclusiveLen= 0;
                            if(Ext.getCmp("numericAttributeType").getValue() == 1 && Ext.getCmp('attributeMaxInclusiveInt').getValue()) {
                                maxInclusive = Ext.getCmp('attributeMaxInclusiveInt').getValue()+'';
                            } else{
                                maxInclusive = Ext.getCmp('attributeMaxInclusiveDecimal').getValue()+'';
                            }
                            maxInclusiveLen= maxInclusive.length;
                            if (maxInclusive !== "" && (maxInclusiveLen > Ext.getCmp('numericAttributeSize').getValue())){
                                App.setAlert(false , _alert['invalidMaxCompareValue']);
                                return false;
                            }
                            if (Ext.getCmp('textAttributeFillCharNumeric').getValue() != "") {
                                fillChar = Ext.getCmp('textAttributeFillCharNumeric').getValue();
                            } else {
                                fillChar = ' ';
                            }
                        }
                        if(Ext.getCmp("numericAttributeType").getValue() == 1) {
                            if (Ext.getCmp('attributeMinInclusiveInt').getValue() && Ext.getCmp('attributeMaxInclusiveInt').getValue() ) {
                                if (Ext.getCmp('attributeMinInclusiveInt').getValue() > Ext.getCmp('attributeMaxInclusiveInt').getValue()) {
                                    App.setAlert(false , _alert['invalidMinInclusiveCompareValue']);
                                    return false;
                                }
                            }
                        } else {
                            if (Ext.getCmp('attributeMinInclusiveDecimal').getValue()  && Ext.getCmp('attributeMaxInclusiveDecimal').getValue()) {
                                if (Ext.getCmp('attributeMinInclusiveDecimal').getValue() > Ext.getCmp('attributeMaxInclusiveDecimal').getValue()) {
                                    App.setAlert(false , _alert['invalidMinInclusiveCompareValue']);
                                    return false;
                                }
                            }
                        }
                    } else {
                        fillChar = '';
                    }
                    
                    Ext.Ajax.request({
                        params: {
                            idSchema: record.idSchema,
                            idSchemaField: record.idSchemaField,
                            idFieldType: Ext.getCmp('idAttributeType').getValue(),
                            name: Ext.getCmp('attributeName').getValue(),
                            description: Ext.getCmp('attributeDescription').getValue(),
                            minLenght: Ext.getCmp('minLength').getValue(),
                            maxLenght: Ext.getCmp('maxLength').getValue(),
                            nillableAlphanumeric: Ext.getCmp('nillableAttributeAlphanumeric').getValue(),
                            extraCheck: Ext.getCmp('extraAttributeCheck').getValue(),
                            idAlignAlphanumeric: Ext.getCmp('idAttributeAlignAlphanumeric').getValue(),
                            fillCharAlphanumeric: fillChar,
                            minInclusive: Ext.getCmp("numericAttributeType").getValue() == 1 ? Ext.getCmp('attributeMinInclusiveInt').getValue() : Ext.getCmp("attributeMinInclusiveDecimal").getValue(),
                            maxInclusive: Ext.getCmp("numericAttributeType").getValue() == 1 ? Ext.getCmp('attributeMaxInclusiveInt').getValue() : Ext.getCmp("attributeMaxInclusiveDecimal").getValue(),
                            fractionDigits: Ext.getCmp('fractionAttributeDigits').getValue(),
                            numericType :Ext.getCmp('numericAttributeType').getValue(),
                            nillableNumeric: Ext.getCmp('nillableAttributeNumeric').getValue(),
                            idAlignNumeric: Ext.getCmp('idAttributeAlignNumeric').getValue(),
                            fillCharNumeric: fillChar,
                            isForecastable : Ext.getCmp('forecastCheckbox').getValue(),
                            maxOccurs : 0,
                            forecastSpeed : Ext.getCmp('forecastSpeedSlider').getValue(),
                            forecastAccuracy : Ext.getCmp('forecastAccuracySlider').getValue(),
                            idDateTimeType: Ext.getCmp('idAttributeDateTimeType').getValue(),
                            idDateType: Ext.getCmp('idAttributeDateType').getValue(),
                            idTimeType: Ext.getCmp('idAttributeTimeType').getValue(),
                            nillableDate: Ext.getCmp('nillableAttributeDate').getValue(),
                            idCustomError : Ext.getCmp('customErrorCombo') ? Ext.getCmp('customErrorCombo').getValue() : null,
                            size: (idAttributeSchemaType == 3 && Ext.getCmp('idAttributeType').getValue() == 4) ? Ext.getCmp('alphanumericAttributeSize').getValue() : Ext.getCmp('numericAttributeSize').getValue(),
                            isAttribute: 1,
							errorType: Ext.getCmp('errorTypeAttrRadioId').getChecked()[0].getSubmitValue(),
							selectedExtraCheck:selectedExtraCheck                            
                        },
                        success: function (response) {
                        	
                        	var responseObj = Ext.decode(response.responseText);
                			var success = responseObj.success;
                			
                			if(success) {
                				
                				if(action == 'add')
                					App.setAlert(true , _alert['addRecord']);
                				else if(action == 'modify')
                					App.setAlert(true , _alert['updateRecord']);
                				
                				action = "save";
                				Ext.getCmp('schemaAttributesGrid').store.load();
                				Ext.getCmp('popupSchemaAttribute').close();                				                				
                			}             			
                			isAttributeChanged = true;
                            return;
                        },
                        url: './schemaFieldsPopupUpdate.json'
                    });
                }
            },
            {text: _message["cancel"] , x: 330, width: 100 , y: 294 ,
                handler: function() {       
                	Ext.getCmp('popupSchemaAttribute').close();                    
                   // if (Ext.getCmp('customErrorCombo')) Ext.getCmp('customErrorCombo').reset();
                }
            }
        ]
    });
    var attributeWinClose = false;
    var idSchemaField;
    new Ext.Window( {
		border : false,
		height : 410,
		disabled : false,
		id : 'popupSchemaAttribute',
		items : popupSchemaAttributePanel,
		layout : 'absolute',
		listeners : {
			beforeclose : function(panel, options) {
				attributeWinClose = true;
				if (action == 'add') {
					deleteSchemaAttribute();
				}
			},
			beforedestroy : function(panel, options) {
				if (attributeWinClose == false) {
					if (action == 'add') {
						deleteSchemaAttribute();
					}
				}
			},
            afterrender: function() { 	
                if (isGenerationStream) {
					gsfDataStores.dbTree.proxy.extraParams.idAttributeSchema = attributeSchemaId;
					//setRootNode is an analog of store.load()
					gsfDataStores.dbTree.setRootNode({
							id: '0',
							leaf: false,
							expanded: true 
					});
                }
            }
        },
        modal: true,
        resizable: false,
        title: _label['attributes'],
        width: 665
    }).show();
   
    if(record.data!='undefined'){
    	if(action == 'add') 
    		idSchemaField = record.id;
    	else 
    		idSchemaField = record.data.idSchemaField;
    } else {
        idSchemaField = record.get('id');
    }
    
    Ext.Ajax.request({
        params: {
            idSchemaField: idSchemaField
        },
        success: function (response) {
            record = Ext.decode(response.responseText);
            Ext.getCmp('attributeName').setValue(record.name);
            
            Ext.getCmp('idAttributeType').setValue(record.idFieldType);
            Ext.getCmp('attributeDescription').setValue(record.description);
            
            Ext.getCmp('errorTypeAttrRadioId').getComponent(0).setValue(record.errorType == 0);
            Ext.getCmp('errorTypeAttrRadioId').getComponent(1).setValue(record.errorType == 2);

            if (idAttributeSchemaType != 3) {
                Ext.getCmp("idAttributeAlignAlphanumericLabel").setVisible(false);
                Ext.getCmp("idAttributeAlignAlphanumeric").setVisible(false);
                Ext.getCmp("fillAttributeCharAlphanumericLabel").setVisible(false);
                Ext.getCmp("fillAttributeCharAlphanumeric").setVisible(false);
                Ext.getCmp("textAttributeFillCharAlphanumeric").setVisible(false);
                Ext.getCmp("idAttributeAlignNumericLabel").setVisible(false);
                Ext.getCmp("idAttributeAlignNumeric").setVisible(false);
                Ext.getCmp("fillAttributeCharNumericLabel").setVisible(false);
                Ext.getCmp("fillAttributeCharNumeric").setVisible(false);
                Ext.getCmp("textAttributeFillCharNumeric").setVisible(false);
                Ext.getCmp("alphanumericAttributeSizeLabel").setVisible(false);
                Ext.getCmp("alphanumericAttributeSize").setVisible(false);
                Ext.getCmp("numericAttributeSizeLabel").setVisible(false);
                Ext.getCmp("numericAttributeSize").setVisible(false);

                Ext.getCmp("nillableAttributeAlphanumericLabel").setPosition(225, 5);
                Ext.getCmp("nillableAttributeAlphanumeric").setPosition(225, 25);
               // Ext.getCmp("extraAttributeCheckLabel").setPosition(390, 5);
                Ext.getCmp("extraAttributeCheck").setPosition(390, 25);
                Ext.getCmp("nillableAttributeAlphanumeric").setWidth(100);

                Ext.getCmp("nillableAttributeNumericLabel").setPosition(325, 5);
                Ext.getCmp("nillableAttributeNumeric").setPosition(325, 25);


            } else {
                Ext.getCmp("nillableAttributeAlphanumericLabel").setVisible(false);
                Ext.getCmp("nillableAttributeAlphanumeric").setVisible(false);//.setWidth(100);
                Ext.getCmp("nillableAttributeNumericLabel").setVisible(false);
                Ext.getCmp("nillableAttributeNumeric").setVisible(false);
                Ext.getCmp("nillableAttributeDateLabel").setVisible(false);
                Ext.getCmp('nillableAttributeDate').setVisible(false);

                Ext.getCmp("extraAttributeCheckLabel").setPosition(335, 5);
                Ext.getCmp("extraAttributeCheck").setPosition(335, 25);
            }
            
            if (record.idFieldType == 1 || record.idFieldType == 2 || record.idFieldType == 3) {
                Ext.getCmp("alphanumericAttributeTab").setDisabled(true);
                Ext.getCmp("numericAttributeTab").setDisabled(true);
                Ext.getCmp("dateAttributeTimeTab").setDisabled(true);
            } else if (record.idFieldType == 4) {//alphanumeric

                Ext.getCmp("alphanumericAttributeTab").setDisabled(false);
                Ext.getCmp("numericAttributeTab").setDisabled(true);
                Ext.getCmp("dateAttributeTimeTab").setDisabled(true);
                Ext.getCmp('idAttributeSchema').setValue(record.idSchema);
                Ext.getCmp('idSchemaAttribute').setValue(record.idSchemaField);
                Ext.getCmp('minLength').setValue(record.minLength);
                Ext.getCmp('maxLength').setValue(record.maxLength);
                Ext.getCmp('alphanumericAttributeSize').setValue(record.size);
                Ext.getCmp('extraAttributeCheck').setValue(record.idCheckType);

                if (Ext.getCmp("extraAttributeCheck").getValue() == "") {
                    Ext.getCmp("extraAttributeCheck").setValue(0);
                }
                
                if (idAttributeSchemaType == 3) {
                    Ext.getCmp('idAttributeAlignAlphanumeric').setValue(record.idAlign);
                    if (record.fillChar == ' ') {
                        Ext.getCmp('fillAttributeCharAlphanumeric').inputValue=1;//setValue(1);
                        Ext.getCmp('textAttributeFillCharAlphanumeric').setDisabled(true);
                        Ext.getCmp('textAttributeFillCharAlphanumeric').setValue("");
                    } else {
                        Ext.getCmp("otherAttributeAlphanumeric").setValue(true);
                        Ext.getCmp('fillAttributeCharAlphanumeric').inputValue=2;//setValue(2);
                        Ext.getCmp('textAttributeFillCharAlphanumeric').setDisabled(false);
                        Ext.getCmp('textAttributeFillCharAlphanumeric').setValue(record.fillChar);
                    }
                }else{
                    if(record.nillable==true){
                        Ext.getCmp('nillableAttributeAlphanumeric').setValue(1);
                    }else{
                        Ext.getCmp('nillableAttributeAlphanumeric').setValue(0);
                    }
                }
                storeAlphanumericAttributeValues.load({params:{idSchemaField: record.idSchemaField}});
            } else if (record.idFieldType == 5) {//numeric
                Ext.getCmp("alphanumericAttributeTab").setDisabled(true);
                Ext.getCmp("numericAttributeTab").setDisabled(false);
                Ext.getCmp("dateAttributeTimeTab").setDisabled(true);
                Ext.getCmp('idAttributeSchema').setValue(record.idSchema);
                Ext.getCmp('idSchemaAttribute').setValue(record.idSchemaField);


                Ext.getCmp('numericAttributeSize').setValue(record.size);
                Ext.getCmp('forecastCheckbox').setValue(record.isForecastable);
                Ext.getCmp('forecastAccuracySlider').setValue(record.forecastAccuracy);
                Ext.getCmp('forecastSpeedSlider').setValue(record.forecastSpeed);

                Ext.getCmp('numericAttributeType').setValue(record.idNumericType);
                Ext.getCmp("fractionAttributeDigits").setValue(record.fractionDigits);
                if (record.idNumericType == 1) {
                    Ext.getCmp("fractionAttributeDigitsLabel").setDisabled(true);
                    Ext.getCmp("fractionAttributeDigits").setDisabled(true);
                    Ext.getCmp('attributeMinInclusiveInt').setValue(record.minInclusive);
                    Ext.getCmp('attributeMaxInclusiveInt').setValue(record.maxInclusive);

                } else {
                    Ext.getCmp("fractionAttributeDigitsLabel").setDisabled(false);
                    Ext.getCmp("fractionAttributeDigits").setDisabled(false);
                    Ext.getCmp('attributeMinInclusiveDecimal').setValue(record.minInclusive);
                    Ext.getCmp('attributeMaxInclusiveDecimal').setValue(record.maxInclusive);

                    Ext.getCmp("attributeMinInclusiveInt").setVisible(false);
                    Ext.getCmp("attributeMaxInclusiveInt").setVisible(false);
                    Ext.getCmp("attributeMinInclusiveDecimal").setVisible(true);
                    Ext.getCmp("attributeMaxInclusiveDecimal").setVisible(true);
                }
                if (idAttributeSchemaType == 3) {
                    Ext.getCmp('idAttributeAlignNumeric').setValue(record.idAlign);
                    if (record.fillChar == ' ') {
                        Ext.getCmp('fillAttributeCharNumeric').inputValue=1;//setValue(1);
                        Ext.getCmp('textAttributeFillCharNumeric').setDisabled(true);
                        Ext.getCmp('textAttributeFillCharNumeric').setValue("");
                    } else {
                        Ext.getCmp("otherAttributeNumeric").setValue(true);
                        Ext.getCmp('fillAttributeCharNumeric').inputValue=2;//setValue(2);
                        Ext.getCmp('textAttributeFillCharNumeric').setDisabled(false);
                        Ext.getCmp('textAttributeFillCharNumeric').setValue(record.fillChar);
                    }
                }else{
                    if(record.nillable==true){
                        Ext.getCmp('nillableAttributeNumeric').setValue(1);
                    }else{
                        Ext.getCmp('nillableAttributeNumeric').setValue(0);
                    }
                }

                storeNumericAttributeValues.load({params:{idSchemaField: record.idSchemaField}});
            } else if (record.idFieldType == 6) {//date
                Ext.getCmp("alphanumericAttributeTab").setDisabled(true);
                Ext.getCmp("numericAttributeTab").setDisabled(true);
                Ext.getCmp("dateAttributeTimeTab").setDisabled(false);
                Ext.getCmp('idSchemaAttribute').setValue(record.idSchemaField);
                if (record.idDateType != null && record.idTimeType != null) {
                    Ext.getCmp('idAttributeDateTimeType').setValue(1);
                    Ext.getCmp("dateAttributeTypeLabel").setDisabled(false);
                    Ext.getCmp("idAttributeDateType").setDisabled(false);
                    Ext.getCmp('idAttributeDateType').setValue(record.idDateType);
                    Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
                    Ext.getCmp("idAttributeTimeType").setDisabled(false);
                    Ext.getCmp('idAttributeTimeType').setValue(record.idTimeType);
                } else if (record.idDateType != null) {
                    Ext.getCmp('idAttributeDateTimeType').setValue(2);
                    Ext.getCmp("dateAttributeTypeLabel").setDisabled(false);
                    Ext.getCmp("idAttributeDateType").setDisabled(false);
                    Ext.getCmp('idAttributeDateType').setValue(record.idDateType);
                    Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
                    Ext.getCmp("idAttributeTimeType").setDisabled(true);
                } else if (record.idTimeType != null) {
                    Ext.getCmp('idAttributeDateTimeType').setValue(3);
                    Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
                    Ext.getCmp("idAttributeDateType").setDisabled(true);
                    Ext.getCmp("timeAttributeTypeLabel").setDisabled(false);
                    Ext.getCmp("idAttributeTimeType").setDisabled(false);
                    Ext.getCmp('idAttributeTimeType').setValue(record.idTimeType);
                } else{
                    if (record.idDateTimeType == 4) {
                        Ext.getCmp('idAttributeDateTimeType').setValue(4);
                    }else if (record.idDateTimeType == 5) {
                        Ext.getCmp('idAttributeDateTimeType').setValue(5);
                    }else if (record.idDateTimeType == 6) {
                        Ext.getCmp('idAttributeDateTimeType').setValue(6);
                    }else if (record.idDateTimeType == 7) {
                        Ext.getCmp('idAttributeDateTimeType').setValue(7);
                    }
                    Ext.getCmp("dateAttributeTypeLabel").setDisabled(true);
                    Ext.getCmp("idAttributeDateType").setDisabled(true);
                    Ext.getCmp("timeAttributeTypeLabel").setDisabled(true);
                    Ext.getCmp("idAttributeTimeType").setDisabled(true);
                }
                if (idAttributeSchemaType != 3) {
                    if(record.nillable==true){
                        Ext.getCmp('nillableAttributeDate').setValue(1);
                    }else{
                        Ext.getCmp('nillableAttributeDate').setValue(0);
                    }
                }
            }
            
            Ext.getCmp("popupSchemaAttribute").setDisabled(false);
            
           /* try {
                addCustomAttributeErrorCombo(attributeSchemaId, fieldType, record.idCustomError);
            } catch(e) {
                //ignore if no method (for core module)
            }*/
            
            Ext.Ajax.request( {
                params : {
                    schemaId : attributeSchemaId
                },
                url : './schemaFieldIsForecasted.json',
                success : function(result) {
                    recordResult = Ext.JSON.decode(result.responseText);
                    Ext.getCmp("forecastingAttributeSlidesPanel").setDisabled(
                        !eval(recordResult.isForecasted));
                }
            });
        },
        url: './schemaFieldsPopupRead.json'
    });
};

/* function addCustomAttributeErrorCombo(schemaId, attributeType, valueCE) {
	var customAttributeErrorsStore = Ext.data.StoreManager.lookup('customErrorsStore');
	var isEEVersion = new Boolean();
	isEEVersion = customAttributeErrorsStore;
	if (isEEVersion) {
		customAttributeErrorsStore.proxy.extraParams.schemaId = schemId;
		customAttributeErrorsStore.proxy.extraParams.isCombo = 'true';
		customAttributeErrorsStore.load();
	}
	var cmp = Ext.getCmp('mainTab');	
	var checkSchemaAttributesPopupEE = function() {
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
						id : 'customAttributeErrorCombo',
						displayField : 'name',
						queryMode : 'local',
						store : customAttributeErrorsStore,
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
									customErrorsHandlers.addCustomAttributeErrorCombo(combo,
											customAttributeErrorsStore, schemId);
								}
							}
						} 
			});
			cmp.insert(3, label);
			cmp.insert(4, combos);
		} else {
			Ext.defer(checkSchemaAttributesPopupEE, 100);
		}
	};
	checkSchemaAttributesPopupEE.call(this);
} */