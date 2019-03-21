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

var forecastWin;
var forecastForm;
var forecastEventWin;
var forecastEventForm;

var storeForecast = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	idProperty:'idForecast',
	pageSize: 20,
	fields: ['mean'],
	proxy: {
		type: 'ajax',
		api: {
			read    : './forecastContentPopup.json'
		},
		reader: {
			type: 'json',
			root: 'results',
			successProperty: "success",
			messageProperty: 'message',
			idProperty:'idForecast'
		}
	}
});
storeForecast.proxy.addListener('exception', function (proxy, response, operation) {
		
});

var columnForecast = [	
       	{dataIndex: 'mean' , editor: {xtype:'textfield'} , header: _label['name'] ,  sortable: true , width: '100%'}
];
var forecastGrid = Ext.create('Ext.grid.Panel', {
	columnLines: true,
	columns: columnForecast,
	id: 'forecastGrid',
	selModel: Ext.create('Ext.selection.RowModel', { 
		mode:'SINGLE'
	}),
	store: storeForecast,
	x: 10,
    y: 220,
    height: 200,
    hideHeaders: true
});

Ext.define('Ext.form.action.JsonForecastSubmit', {
    extend:'Ext.form.action.Submit',
    alternateClassName: 'Ext.form.Action.JsonSubmit',
    alias: 'formaction.JsonForecastSubmit',
    type: 'JsonSubmit',

    run : function() {

        var encodedParams = Ext.encode(forecastForm.getValues());


    }
});

function initForecastFormWithData(recordForecastResult) {	
    Ext.getCmp("size").setValue(recordForecastResult.results[0].size);
    Ext.getCmp("mean").setValue(recordForecastResult.results[0].mean);
}

function showForecastForm() {
    Ext.Ajax.request( {
        url : './forecastContentPopup.json',
        success : function(result) {
            recordForecastResult = Ext.JSON.decode(result.responseText);
            initForecastFormWithData(recordForecastResult);
        }
    });
    if (!forecastWin) {
        forecastForm = Ext.widget('form', {
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

            items: [
                    {name: 'csvFileLabel' , id: 'csvFileLabel' , xtype: 'label', text: _label['csvFileLabel'] },
                    {name: 'file' , id: 'form-file' , emptyText: _label['selectImportFile'], xtype: 'fileuploadfield' ,
                    	listeners:{
                    		change: function(obj, filename, options){
                    			this.fireEvent('upload',  filename);
                    			Ext.getCmp("btnImport").enable();
                    		}
                    	}
                    },
                    { 	xtype : 'fieldset',
                    	height : 140,
                    	width : 430,
                    	title : _label['dataAnalysis'],
                    	layout : 'absolute',
                    	items : [                   	         
                    	         
                    	         {
                    	        	id : 'size',
                    	        	name : 'size',
                    	        	labelAlign : 'top',
                    	        	fieldLabel : _label['size'],
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	         }, {
                    	        	id : 'signifacativeLags',
                    	        	name : 'signifacativeLags',
                    	        	fieldLabel : _label['significativeLags'],
                    	        	labelAlign : 'top',
                    	        	x : 160,
                    	        	xtype : 'textfield',
                    	        	width : 150                    	        	
                    	        }, {
                    	        	id : 'mean',
                    	        	name : 'mean',
                    	        	fieldLabel : _label['mean'],
                    	        	labelAlign : 'top',
                    	        	x : 320,
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	        }, {
                    	        	id : 'maxACValues',
                    	        	name : 'maxACValues',
                    	        	fieldLabel : _label['maxACValues'],
                    	        	labelAlign : 'top',
                    	        	x : 480,
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	        }, {
                    	        	id : 'maxACatLags',
                    	        	name : 'maxACatLags',
                    	        	fieldLabel : _label['maxACatLags'],
                    	        	labelAlign : 'top',
                    	        	x : 640,
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	        },
                    	        {
                    	        	id : 'maxACValues1',
                    	        	name : 'maxACValues1',
                    	        	x : 480,
                    	        	y : 50,
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	        }, {
                    	        	id : 'maxACatLags1',
                    	        	name : 'maxACatLags1',
                    	        	x : 640,
                    	        	y : 50,
                    	        	xtype : 'textfield',
                    	        	width : 150
                    	        },                    	        
                    	        { 
                    	        	xtype: 'progressbar', 
                    	            text: '100%',
                    	            width: '100%',
                    	            x: 0,
                    	            y: 90,
                    	            value: 5
                    	        }
                    	  ]
                    },
                    { 	xtype : 'fieldset',
                    	height : 120,
                    	width : 510,
                    	title : _label['eventSpaceEstimation'],
                    	layout : 'absolute',
                    	items : [                   	         
                    	         {
                    	        	id : 'count',
                    	        	name : 'count',
                    	        	labelAlign : 'top',
                    	        	fieldLabel : _label['count'],
                    	        	xtype : 'textfield',
                    	        	width : 100
                    	         }, {
                    	        	id : 'dimension',
                    	        	name : 'dimension',
                    	        	fieldLabel : _label['dimension'],
                    	        	labelAlign : 'top',
                    	        	x : 110,
                    	        	xtype : 'textfield',
                    	        	width : 100                    	        	
                    	        }, {
                    	        	id : 'sequenceLength',
                    	        	name : 'sequenceLength',
                    	        	fieldLabel : _label['sequenceLength'],
                    	        	labelAlign : 'top',
                    	        	x : 220,
                    	        	xtype : 'textfield',
                    	        	width : 200
                    	        },
                    	        {
                    	        	id : 'acfLabel',
                    	        	name : 'acfLabel',
                    	        	x : 0,
                    	        	y : 60,
                    	        	xtype : 'label',
                    	        	text : _label['ACFValue'],
                    	        	width : 100
                    	        }, {
                    	        	id : 'acfValue',
                    	        	name : 'acfValue',
                    	        	x : 70,
                    	        	y : 60,
                    	        	xtype : 'textfield',
                    	        	width : 100
                    	        }
                    	  ]
                    },
                    { 	xtype : 'panel',
                    	height : 100,
                    	width : 510,
                    	border: false,
                    	layout : 'absolute',
                    	items : [
                    	         {name: 'btnForecasting' , id: 'btnForecasting' , xtype: 'button', text: _message['forecasting'], width: 100,
                    	        	 handler: function(){
        								 Ext.getCmp('forecastwindow').close();
        								 showForecastEventForm();
        							 }
                    	         },
                    	         {name: 'btnSave' , id: 'btnSave' , xtype: 'button', text: _message['save'], width: 50, x: 750 }
                    	]
                    }            
            ]
        });

        forecastWin = new Ext.Window({
            title: _label['forecasting'],
            id: 'forecastwindow',
            closeAction: 'hide',
            width: 850,
            height: 470,
            minHeight: 400,
            layout: 'fit',
            resizable: false,
            bodyStyle:{"background-color":"#ffffff"},
            modal: true,
            items: forecastForm
        });
    }
    forecastWin.show();
}

function showForecastEventForm() {
    storeForecast.load();
    if(!forecastEventWin)  {
    	forecastEventForm = Ext.widget('form', {
    		layout: 'absolute',
    		xtype: 'panel',
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

            items: [
                    {name: 'forecastLabel' , id: 'forecastLabel' , xtype: 'label', text: _label['forecastLabel'], width: '100%' },
                    {
        	        	id : 'numberOfEvents',
        	        	name : 'numberOfEvents',
        	        	xtype: 'numberfield',
        	        	minValue: 0,
        	        	hideTrigger: true,
        	        	width : 100,
        	        	x: 10,
        	        	y: 40,
        	        	value: 0,
        	        	fieldStyle: 'text-align: right;',
        	        	listeners : {
        	                change : function (f, e){
        	                	
        	                	if(Ext.getCmp('numberOfEvents').value == null) {
        	                		Ext.getCmp('numberOfEvents').value = 0;
        	                	}
        	                	
        	                	Ext.getCmp("lblProgress").setText("Forecasting 0 out of " + Ext.getCmp('numberOfEvents').value);
        	                }
        	        	}
        	         }, {
        	        	id : 'btnStart',
        	        	name : 'btnStart',
        	        	text : _label['start'],
        	        	labelAlign : 'top',
        	        	x : 120,
        	        	y: 40,
        	        	xtype : 'button',
        	        	width : 80                    	        	
        	        }, 
                    { 
                       	xtype: 'progressbar', 
                        text: '100%',
                        width: '97%',
                        x: 10,
                        y: 70,
                        value: 5
                    },
                    { 
                       	xtype: 'label', 
                        text: _label['remainingElaboration'],
                        width: 200,
                        x: 10,
                        y: 100
                    },
                    { 
                       	xtype: 'label', 
                        text: 'Forecasting 0 out of 0',
                        width: 200,
                        id: 'lblProgress',
                        name: 'lblProgress',
                        x: 400,
                        y: 130
                    },
                    { 
                       	xtype: 'progressbar', 
                        text: '0%',
                        width: '97%',
                        x: 10,
                        y: 160,
                        value: 0
                    },
                    { 
                       	xtype: 'label', 
                        text: _label['forecastedValues'],
                        width: 200,
                        x: 10,
                        y: 200
                    },
                    forecastGrid
        	        ]
        	});
    	
    		forecastEventWin = new Ext.Window({
    			title: _label['forecasting'],
    			id: 'forecastwindow1',
    			closeAction: 'hide',
    			width: 850,
    			height: 470,
    			minHeight: 400,
    			layout: 'fit',
    			resizable: false,
    			bodyStyle:{"background-color":"#ffffff"},
    			modal: true,
    			items: forecastEventForm
    		});
      	}
         
    	forecastEventWin.show();
}