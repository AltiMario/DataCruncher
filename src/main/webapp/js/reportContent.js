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
var pass;
var graphLoadTimer;
pass=0;

var d = new Date();
var detailedStateYear = d.getFullYear();
var monthlyStateYear = d.getFullYear();
var annualStateYear = d.getFullYear();
var detailedStateMonth = d.getMonth()+1;
var monthlyStateMonth = d.getMonth()+1;
var detailedStateDay = d.getDate();

//set only once at app startup default appId to stores
function setDefaultAppForReports(store) {
	if (store.getCount() != 0 ) {
		var idApp = store.getAt(0).get('idApplication');
		comboApplication.setValue(idApp);
		comboAnnualApplication.setValue(idApp);
		comboDetailedApplication.setValue(idApp);
		storeAnnualSchema.proxy.extraParams.appIds = idApp;
		storeSchema.proxy.extraParams.appIds = idApp;
		
	}
}

// Models	
Ext.define('realTime', {
    extend: 'Ext.data.Model',
    fields: [
		'time',
		'dataStreamKO', 
        'dataStreamOK', 
        'dataStreamWarn'
    ]
});

Ext.define('StateGraphBarChartModel', {
    extend: 'Ext.data.Model',
    fields: [
		'day',
		'dataStreamKO', 
        'dataStreamOK', 
        'dataStreamWarn'
		
    ]
});

Ext.define('AnnualStateGraphBarChartModel', {
    extend: 'Ext.data.Model',
    fields: [
		'day',
		'dataStreamKO', 
        'dataStreamOK', 
        'dataStreamWarn'
    ]
});

Ext.define('DetailedStateGraphBarChartModel', {
    extend: 'Ext.data.Model',
    fields: [
		'day',
		'schemaName',
		'dataStreamKO', 
        'dataStreamOK', 
        'dataStreamWarn'
		
    ]
});

Ext.define('state', {
    extend: 'Ext.data.Model',
    fields: ['name','value']	
});
Ext.define('year', {
    extend: 'Ext.data.Model',
    fields: ['name','value']
});

// day Store
var dayStore = Ext.create('store.json', {
	fields: ['id', 'name'],
	data: []
});
// month Store
var monthStore = Ext.create('store.json', {
	fields: ['id', 'name'],
	data: [
		{'id':1,'name':_label['January']},
		{'id':2,'name':_label['February']},
		{'id':3,'name':_label['March']},
		{'id':4,'name':_label['April']},
		{'id':5,'name':_label['May']},
		{'id':6,'name':_label['June']},
		{'id':7,'name':_label['July']},
		{'id':8,'name':_label['August']},
		{'id':9,'name':_label['September']},
		{'id':10,'name':_label['October']},
		{'id':11,'name':_label['November']},
		{'id':12,'name':_label['December']}			
	]
});
// year Store
var yearStore = new Ext.data.Store({
	autoSave : false,
	autoLoad : false,
	model : 'year',
	proxy : {
		type : 'ajax',
		api : {
			read : './stateGraph.json?req=yearList'
		},
		reader : {
			type : 'json',
			root : 'results',
			successProperty : "success",
			messageProperty : 'message'
		}
	}
});
// Real Time Graph Store
var realTimeGraphStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'realTime',	
	proxy: {
		type: 'ajax',
        api: {
			read    : './realTimeGraph.json'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		},
		writer: {
			type: 'json',
			writeAllFields:true

		}
	}
});

function doGetRealTimeLoadData() {		
	clearTimeout(graphLoadTimer);
	graphLoadTimer = setTimeout(function() {
		if (pass % 3 === 0) {	
			
			if(Ext.getCmp('content').layout.getActiveItem().id == 'realTimeGraph') {
				realTimeGraphStore.load({
					scope   : this,
					addRecords: true
				});
			} else if(Ext.getCmp('content').layout.getActiveItem().id == 'annualStateGraph') {
				
				annualBarChartStateStore.load({
					scope   : this,
					updateRecords : true
				});
				/*_report annualStateGraphStore.load({
					scope   : this,
					updateRecords : true
				});*/
			} else if(Ext.getCmp('content').layout.getActiveItem().id == 'monthlyStateGraph'){
				
				barChartStateStore.load({
					scope   : this,
					updateRecords : true
				});
				stateGraphStore.load({
					scope   : this,
					updateRecords : true
				}); 
			} else if(Ext.getCmp('content').layout.getActiveItem().id == 'detailedStateGraph'){
				
				barChartStateDetailedStore.load({
					scope   : this,
					updateRecords : true
				});
				/*detailedStateGraphStore.load({
					scope   : this,
					updateRecords : true
				});*/ 
			}
		}
		if(realTimeGraphStore.getCount()>=10){
			realTimeGraphStore.removeAt(0);
		}
		doGetRealTimeLoadData();		
		pass++;
	}, 1000);
}

var colors = ['#FF0000', '#00FF00', '#FFFF00'];
				  
Ext.chart.theme.Browser = Ext.extend(Ext.chart.theme.Base, {
        constructor: function(config) {
            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                colors: colors
            }, config));
        }
    });

var realTimeGraph = Ext.create('Ext.chart.Chart', {	
	id: 'realTimeGraph',
	title: _label['realTimeData'],
    width: 800,
    height: 600,
	animate: false,
	theme: 'Browser:gradients',
	store: realTimeGraphStore,
	background: {
		fill: '#fff'
	},
	legend: {
		position: 'bottom'
	},
	axes:[
		{
			title: _label['numDataStream'],
			type: 'Numeric',
			position: 'left',
			fields: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
			grid: true,
			labelTitle: {
                font: '13px Arial'
            },
            label: {
                font: '11px Arial'
            }			
		},
		{
			title: _label['currentTimeValidation'],
			type: 'Category',
			position: 'bottom',
			fields: ['time'],				
			label: {
				 rotate: {
					degrees: 0                            
				 } 
			},
			width:100
		}			
	],
	 series: [{
                type: 'area',
                highlight: false,
                axis: 'left',
                xField: 'name',
                yField: ['dataStreamKO', 'dataStreamOK', 'dataStreamWarn'],
                title: [ _label['streamLoggingKO'],_label['streamLoggingOK'],_label['streamLoggingWarning']],
				style: {
                    lineWidth: 1,
                    stroke: '#666',					
                    opacity: 0.86
                }
            }]
});

/*comboAnnualApplication.store.on('load',function(store) {	
	if (store.getCount() != 0 ) {
		alert('here');
		comboAnnualApplication.setValue(store.getAt(0).get('idApplication'));
		storeAnnualSchema.proxy.api.read = './schemasRead.json?appId='+ store.getAt(0).get('idApplication');
		//storeAnnualSchema.load();
	}
});*/

/**
 * Annual report combos
 */
var comboAnnualApplication = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',	
	store: storeApplications,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idApplication',	
	listeners:{
        'select': function(combo){
        	//_report annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue();

        	var appId = combo.getValue();
        	storeAnnualSchema.proxy.extraParams.appIds = appId;
        	storeAnnualSchema.load();
        	
			/*_report annualStateGraphStore.load(function(records, options,sucess){				
				annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue();
			});*/
			
			annualBarChartStateStore.proxy.api.read = './stateGraphBarChart.json?rptType=annual&month=null&year='+ annualStateYear + '&appId=' + combo.getValue();
			annualBarChartStateStore.load();
		}
    }
});

var storeAnnualSchema = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'schemas',
	idProperty:'idSchema',
	proxy: {
		type: 'ajax',
        api: {
			read    : './schemasRead.json'
		},
		extraParams: {
            idSchemaType: "1,2",
            appIds: null 
        },
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		}
	}
});

var comboAnnualSchema = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',
	store: storeAnnualSchema,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idSchema',
	listeners:{
        'select': function(){
        	        	
        	/*_report annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
			annualStateGraphStore.load(function(records, options,sucess){				
				annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
			});*/
			
			annualBarChartStateStore.proxy.api.read = './stateGraphBarChart.json?rptType=annual&month=null&year='+ annualStateYear + '&appId=' + comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
			annualBarChartStateStore.load();
		}   	
    }
});


storeAnnualSchema.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.message.indexOf('Error') != -1) {
				Ext.Msg.alert("", _error['connectionError']);
			}
		}
	}
});

storeAnnualSchema.on('load', function(store) {
	comboAnnualSchema.store.insert(0, {
		idSchema : '-1',
		name : 'All'
	});
	comboAnnualSchema.setValue(-1);
	comboAnnualSchema.store.sync();
});

var comboAnnualYear = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',
	store: yearStore,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'value',
	listeners:{
        'select': function(){
        	annualStateYear = this.getValue();
        	/*_report annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
        	annualStateGraphStore.load(function(records, options,sucess){				
        		annualStateGraphStore.proxy.api.read = './stateGraph.json?month=null&year='+ annualStateYear +'&appId='+comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
			});*/
			
        	annualBarChartStateStore.proxy.api.read = './stateGraphBarChart.json?rptType=annual&month=null&year='+ annualStateYear +'&appId='+ comboAnnualApplication.getValue()+ '&schemaId='+comboAnnualSchema.getValue();
        	annualBarChartStateStore.load();
		}
    }
});

comboAnnualYear.store.on('load', function(store) {
	comboAnnualYear.setValue(annualStateYear);
});

/*comboDetailedApplication.store.on('load',function(store) {	
	if (store.getCount() != 0 ) {
		comboDetailedApplication.setValue(store.getAt(0).get('idApplication'));
	}
});*/

var annualBarChartStateStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'AnnualStateGraphBarChartModel',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraphBarChart.json?rptType=annual&month=null&year=' + annualStateYear + '&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		},
		writer: {
			type: 'json',
			writeAllFields:true
		}
	}
});
annualBarChartStateStore.proxy.addListener('exception', function (proxy, response, operation) {
	if (response) {		
		var responseObj = Ext.decode(response.responseText);
		if(responseObj){
			if(responseObj.success==false){
				App.setAlert(false , responseObj.message);				
			}
		}			
	}		
	
});

/**
 * Monthly state combos
 */
var comboApplication = Ext.create('Ext.form.field.ComboBox', {
	x : 20,
	y : 50,
	displayField : 'name',
	forceSelection : true,
	queryMode : 'local',
	store : storeApplications,
	triggerAction : 'all',
	typeAhead : true,
	valueField : 'idApplication',
	id : 'comboApplication',
	listeners : {
		'select' : function() {
			storeSchema.proxy.extraParams.appIds = this.getValue();
			storeSchema.load();

			stateGraphStore.proxy.api.read = './stateGraph.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear + '&appId='
					+ this.getValue();
			stateGraphStore.load();

			barChartStateStore.proxy.api.read = './stateGraphBarChart.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear
					+ '&appId=' + this.getValue();
			barChartStateStore.load();
		}
	}
});

var storeSchema = new Ext.data.Store({
	autoSave : false,
	autoLoad : false,
	model : 'schemas',
	idProperty : 'idSchema',
	proxy : {
		type : 'ajax',
		api : {
			read : './schemasRead.json'
		},
		extraParams : {
			idSchemaType : "1,2",
			appIds : null
		},
		reader : {
			type : 'json',
			root : 'results',
			successProperty : "success",
			messageProperty : 'message'
		}
	}
});
storeSchema.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.message.indexOf('Error') != -1) {
				Ext.Msg.alert("", _error['connectionError']);
			}
		}
	}
});

var comboSchema = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	x : 190,
	y : 50,
	forceSelection: true,
	queryMode: 'local',
	store: storeSchema,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idSchema',
	listeners:{
        'select': function(){
			stateGraphStore.proxy.api.read = './stateGraph.json?month='+ monthlyStateMonth +'&year='+ monthlyStateYear +'&appId='+comboApplication.getValue()+ '&schemaId='+comboSchema.getValue();
			stateGraphStore.load(/*function(records, options,sucess){				
				stateGraphStore.proxy.api.read = './stateGraph.json?month='+ monthlyStateMonth +'&year='+ monthlyStateYear +'&appId='+comboApplication.getValue()+ '&schemaId='+comboSchema.getValue();
			}*/);
			
			barChartStateStore.proxy.api.read = './stateGraphBarChart.json?month=' + monthlyStateMonth + '&year='+ monthlyStateYear + '&appId=' + comboApplication.getValue()+ '&schemaId='+comboSchema.getValue();
			barChartStateStore.load(/*function(records, options,sucess){				
				barChartStateStore.proxy.api.read = './stateGraphBarChart.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear + '&appId=' + comboApplication.getValue()+ '&schemaId=' + comboSchema.getValue();
			}*/);
		}
    }
});

comboSchema.store.on('load',function(store) {	
	comboSchema.store.insert(0,{idSchema: '-1', name: 'All'});
	comboSchema.setValue(-1);
	comboSchema.store.sync();	
});

var comboYear = Ext.create('Ext.form.field.ComboBox', {
	displayField : 'name',
	x : 360,
	y : 50,
	forceSelection : true,
	queryMode : 'local',
	store : yearStore,
	triggerAction : 'all',
	typeAhead : true,
	valueField : 'value',
	listeners : {
		'select' : function() {
			monthlyStateYear = this.getValue();
			stateGraphStore.proxy.api.read = './stateGraph.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear + '&appId='
					+ comboApplication.getValue() + '&schemaId=' + comboSchema.getValue();
			stateGraphStore.load();

			barChartStateStore.proxy.api.read = './stateGraphBarChart.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear
					+ '&appId=' + comboApplication.getValue() + '&schemaId=' + comboSchema.getValue();
			barChartStateStore.load();
		}
	}
});

comboYear.store.on('load', function(store) {
	comboYear.setValue(monthlyStateYear);
	comboMonth.setValue(monthlyStateMonth);
});

var comboMonth = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	x : 530,
	y : 50,
	forceSelection: true,
	queryMode: 'local',
	store: monthStore,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'id',
	listeners:{
        'select': function(){
        	monthlyStateMonth = this.getValue();
			stateGraphStore.proxy.api.read = './stateGraph.json?month='+ monthlyStateMonth +'&year='+ monthlyStateYear +'&appId='+comboApplication.getValue()+ '&schemaId='+comboSchema.getValue();
			stateGraphStore.load();
			
			barChartStateStore.proxy.api.read = './stateGraphBarChart.json?month='+ monthlyStateMonth +'&year='+ monthlyStateYear +'&appId='+comboApplication.getValue()+ '&schemaId='+comboSchema.getValue();
			barChartStateStore.load();
		}
    }
});

var stateGraphStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'state',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraph.json?month=null&year=null&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		}
	}
});
stateGraphStore.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.success == false) {
				App.setAlert(false, responseObj.message);
			}
		}
	}
});

var barChartStateStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'StateGraphBarChartModel',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraphBarChart.json?month=' + monthlyStateMonth + '&year=' + monthlyStateYear + '&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		}
	}
});

barChartStateStore.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.success == false) {
				App.setAlert(false, responseObj.message);
			}
		}
	}
});

/*comboApplication.store.on('load',function(store) {	
	if (store.getCount() != 0 ) {
		Ext.getCmp('comboApplication').setValue(store.getAt(0).get('idApplication'));
		storeSchema.proxy.api.read = './schemasRead.json?appId='+ store.getAt(0).get('idApplication');
		//storeSchema.load();
	}
});*/


/**
 * Detailed report stores
 */
var comboDetailedApplication = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	forceSelection: true,
	queryMode: 'local',	
	store: storeApplications,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'idApplication',	
	listeners:{
        'select': function(){
			//detailedStateGraphStore.proxy.api.read = './stateGraph.json?rptType=detailed&day=' + detailedStateDay + '&month='+ detailedStateMonth +'&year='+ detailedStateYear +'&appId='+comboDetailedApplication.getValue();
			//detailedStateGraphStore.load();
			
			barChartStateDetailedStore.proxy.api.read = './stateGraphBarChart.json?rptType=detailed&day=' + detailedStateDay + '&month=' + detailedStateMonth + '&year='+ detailedStateYear + '&appId=' + comboDetailedApplication.getValue();
			barChartStateDetailedStore.load(); 
		}
    }
});


var comboDetailedYear = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	flex : 1, 
	forceSelection: true,
	queryMode: 'local',
	store: yearStore,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'value',
	listeners:{
        'select': function(){        	
        	detailedStateYear = this.getValue();
        	changeNumberOfDays();
        	/*detailedStateGraphStore.proxy.api.read = './stateGraph.json?rptType=detailed&day=' + detailedStateDay + '&month='+ detailedStateMonth +'&year='+ detailedStateYear +'&appId='+comboDetailedApplication.getValue();
			detailedStateGraphStore.load(function(records, options,sucess){				
				detailedStateGraphStore.proxy.api.read = './stateGraph.json?rptType=detailed&day=' + detailedStateDay + '&month='+ detailedStateMonth +'&year='+ detailedStateYear +'&appId='+comboDetailedApplication.getValue();
			});*/
			
			barChartStateDetailedStore.proxy.api.read = './stateGraphBarChart.json?rptType=detailed&day=' + detailedStateDay + '&month=' + detailedStateMonth + '&year='+ detailedStateYear + '&appId=' + comboDetailedApplication.getValue();
			barChartStateDetailedStore.load();
		}
    }
});

var comboDetailedDay = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	flex : 1, 
	x : 0, y : 0,
	forceSelection: true,
	queryMode: 'local',
	store: dayStore,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'id',
	listeners:{
        'select': function(){
        	detailedStateDay = this.getValue();
			//detailedStateGraphStore.proxy.api.read = './stateGraph.json?rptType=detailed&day=' + detailedStateDay + '&month='+ detailedStateMonth +'&year='+ detailedStateYear +'&appId='+comboDetailedApplication.getValue();
			//detailedStateGraphStore.load();
			
			barChartStateDetailedStore.proxy.api.read = './stateGraphBarChart.json?rptType=detailed&day=' + detailedStateDay + '&month=' + detailedStateMonth + '&year='+ detailedStateYear + '&appId=' + comboDetailedApplication.getValue();
			barChartStateDetailedStore.load();
		}
    }
});

var comboDetailedMonth = Ext.create('Ext.form.field.ComboBox', {
	displayField: 'name',
	flex : 1, 
	x : 0, y : 0,
	forceSelection: true,
	queryMode: 'local',
	store: monthStore,
	triggerAction: 'all',
	typeAhead: true,
	valueField: 'id',
	listeners:{
        'select': function(){
        	detailedStateMonth = this.getValue();
        	changeNumberOfDays();
			//detailedStateGraphStore.proxy.api.read = './stateGraph.json?rptType=detailed&day=' + detailedStateDay + '&month='+ detailedStateMonth +'&year='+ detailedStateYear +'&appId='+comboDetailedApplication.getValue();
			//detailedStateGraphStore.load();
			
			barChartStateDetailedStore.proxy.api.read = './stateGraphBarChart.json?rptType=detailed&day=' + detailedStateDay + '&month=' + detailedStateMonth + '&year='+ detailedStateYear + '&appId=' + comboDetailedApplication.getValue();
			barChartStateDetailedStore.load(); 
		}
    }
});

var barChartStateDetailedStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'DetailedStateGraphBarChartModel',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraphBarChart.json?rptType=detailed&day=' + detailedStateDay + '&month=' + detailedStateMonth + '&year=' + detailedStateYear + '&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		},
		writer: {
			type: 'json',
			writeAllFields:true
		}
	}
});
barChartStateStore.proxy.addListener('exception', function(proxy, response, operation) {
	if (response) {
		var responseObj = Ext.decode(response.responseText);
		if (responseObj) {
			if (responseObj.success == false) {
				App.setAlert(false, responseObj.message);
			}
		}
	}
});

/**
 * End of store's declaration
 */

/*var detailedStateGraphStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'state',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraph.json?month=null&year=null&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		}
	}
});
detailedStateGraphStore.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {		
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.success==false){
					App.setAlert(false , responseObj.message);
				}
			}			
		}		
		
});*/
/*_report var annualStateGraphStore = new Ext.data.Store({
	autoSave: false,
	autoLoad: false,
	model: 'state',
	proxy: {
		type: 'ajax',
        api: {        	
			read    : './stateGraph.json?month=null&year=null&appId=null&schemaId=null'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: "success",
			messageProperty: 'message'
		},
		writer: {
			type: 'json',
			writeAllFields:true

		}
	}
});
annualStateGraphStore.proxy.addListener('exception', function (proxy, response, operation) {
		if (response) {		
			var responseObj = Ext.decode(response.responseText);
			if(responseObj){
				if(responseObj.success==false){
					App.setAlert(false , responseObj.message);					
				}
			}			
		}		
		
});*/



function initComboDetailedDate() {	
	comboDetailedYear.setValue(detailedStateYear);
	comboDetailedMonth.setValue(detailedStateMonth);
	changeNumberOfDays();
	comboDetailedDay.setValue(detailedStateDay);
};


var colors = ['#FF0000', '#00FF00', '#FFFF00'];
Ext.chart.theme.Category1 = Ext.extend(Ext.chart.theme.Base, {
constructor: function(config) {
   Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
       colors: colors
   }, config));
}
});

Ext.chart.theme.PieChart1 = Ext.extend(Ext.chart.theme.Base, {
	constructor: function(config) {
	   Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
	       colors: colors
	   }, config));
	}
});

var barChartConfig = {
	anchor : '100% 70%',
	xtype: 'chart',
	theme: 'Category1',
	store: barChartStateStore,
	title: _label['bar'],
	legend: {
		position: 'bottom'
	},
	axes: [{
		type: 'Numeric',
		position: 'left',
		minimum: 0,
		decimals: 0,
		//maximum: 10,
		fields: ['dataStreamKO','dataStreamOK','dataStreamWarn'],		
		title: _label['numDataStream'],
		labelTitle: {
			font: 'bold 14px Arial'
		},
		label: {
			font: '11px Arial'
		}
	},{
		type: 'Category',
		position: 'bottom',
		fields: ['day'],
		title: _label['dayMonth'],
		labelTitle: {
			font: 'bold 14px Arial'
		}
	}],
	series: [{
		type: 'column',
		xField: 'day',
		yField: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
        title: [ _label['streamLoggingKO'],_label['streamLoggingOK'],_label['streamLoggingWarning']],
        label: {
        	renderer: function(value) {
        		if(value != 0)
        			return Ext.String.ellipsis(value, 15, false);
        		else
        			return '';
            },
        	rotate: {
        		degrees: 270                            
			},				
           display: 'insideEnd',
           field: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
           'text-anchor': 'middle'
        },
        tips: {
            trackMouse: true,
            width: 100,
            height: 28,
            renderer: function(storeItem, item) {
            	this.setTitle(String(item.value[1]) + " "+ _label['datastreams']);
            }
        }
	}]
};
var detailedChartConfig = {
		flex: 1,
		xtype: 'chart',
		theme: 'Category1',
		store: barChartStateDetailedStore,
		title: _label['bar'],
		legend: {
			position: 'bottom'
		},
		axes: [{
			type: 'Numeric',
			position: 'left',
			minimum: 0,
			decimals: 0,
			fields: ['dataStreamKO','dataStreamOK','dataStreamWarn'],		
			title: _label['numDataStream']
		},{
			type: 'Category',
			position: 'bottom',
			fields: ['schemaName'],
			label: {
				renderer: function(value) {
	        		if(value != 0)
	        			return Ext.String.ellipsis(value, 13, false);
	        		else
	        			return '';
	            },
                rotate: {
                    degrees: 270
                }
            }
		}],
		series: [{
			type: 'column',
			xField: 'day',
			yField: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
	        title: [ _eventStatus['KO'],_eventStatus['OK'],_eventStatus['Warning']],
	        label: {
	        	renderer: function(value) {
	        		if(value != 0)
	        			return value;
	        		else
	        			return '';
                },
	        	rotate: {
	        		degrees: 270                            
				},				
	           display: 'insideEnd',
	           field: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
	           'text-anchor': 'middle'
	        },
	        tips: {
                trackMouse: true,
                width: 100,
                height: 28,
                renderer: function(storeItem, item) {
                	this.setTitle(String(item.value[1]) + " "+ _label['datastreams']);
                }
            }	        	
		}]
	};
var annualBarChartConfig = {
		flex: 1,
		xtype: 'chart',
		theme: 'Category1',
		store: annualBarChartStateStore,
		title: _label['bar'],
		legend: {
			position: 'bottom'
		},
		axes: [{
            type: 'Numeric',
            position: 'bottom',
            fields: ['dataStreamKO','dataStreamOK','dataStreamWarn'],	
            minimum: 0,
            label: {
                renderer: Ext.util.Format.numberRenderer('0,0')
            },
            grid: true,
            title: _label['numDataStream']
        }, {
            type: 'Category',
            position: 'left',
            fields: ['day'],
            title: _label['monthYear']
        }],
		series: [{
			type: 'bar',
			axis: 'bottom',			
			xField: 'day',
			yField: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
            title: [ _label['streamLoggingKO'],_label['streamLoggingOK'],_label['streamLoggingWarning']],
            label: {
	        	renderer: function(value) {
	        		if(value != 0)
	        			return Ext.String.ellipsis(value, 15, false);
	        		else
	        			return '';
                },
	           display: 'insideEnd',
	           field: ['dataStreamKO','dataStreamOK','dataStreamWarn'],
	           'text-anchor': 'middle'
	        },
	        tips: {
                trackMouse: true,
                width: 100,
                renderer: function(storeItem, item) {
                	this.setTitle(String(item.value[1]) + " "+ _label['datastreams']);
                }
            }
		}]
	};

var pieChartConfig = {	
		position : 'right',
		flex : 1,
        xtype: 'chart',
        animate: {
            duration: 250
        },
        store: stateGraphStore,
        shadow: true,
   		height: 150,
        //theme: 'Base:gradients',
        theme: 'PieChart1',
        series: [{
            donut: 30,
            type: 'pie',
            field: 'value',
            showInLegend: false,         
            tips: {
                trackMouse: true,
                width: 140,
                height: 40,
                renderer: function(storeItem, item) {
                    //calculate percentage.
                    var total = 0;
                    stateGraphStore.each(function(rec) {
                        total += rec.get('value');						
                    });
                    this.setTitle(storeItem.get('name') + ': ' + Math.round(storeItem.get('value') / total * 100) + '%' + '\n'+" Count:"+storeItem.get('value'));
                }
            },
            highlight: {
                segment: {
                    margin: 20
                }
            },
            labelTitle: {
                font: '13px Arial'
            },
            label: {
                field: 'name',
                display: 'rotate',
                contrast: true,
                font: '12px Arial'
            }
        }]
		
    };

var stateGraph = Ext.create('Ext.panel.Panel', {
	title: _label['monthStGraph'],
	id: 'monthlyStateGraph',
	layout : 'anchor',
	items: [
		{	
			anchor : '100% 30%',
			layout : 'hbox',
			border : false,
			items : [
		 		{
					layout : 'absolute',
					position : 'center',
					border : false,
					flex : 5,
					items : [
			 			comboApplication,
			 			comboSchema,
			 			comboYear,
			 			comboMonth
		 			]
		 		},
		 		pieChartConfig
 			]
 		},
		barChartConfig
	]
});

var detailedStateGraph = Ext.create('Ext.Panel', {
	title: _label['detailedStGraph'],
	layout: 'fit',
	id: 'detailedStateGraph',
	items: [{
		xtype: 'container',
		layout: {
			type: 'vbox',
			align: 'stretch'
		},
		items: [{
			xtype: 'container',			
			flex: 1,
			layout: {
				type: 'hbox',
				align: 'stretch'
			},
			margins: '7 5 5 5',
			padding: '0 0 2 0',
			items: [
				comboDetailedApplication,
				comboDetailedYear,
				comboDetailedMonth,
				comboDetailedDay,
				{ xtype: 'container', flex : 3}
			]
		},
		{
			flex: 12,
			frame : false,
			xtype: 'container',
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: [
				detailedChartConfig                    
			]
		}]
	}]
	
});
var annualStateGraph = Ext.create('Ext.Panel', {
	title: _label['annualStGraph'],
	layout: 'fit',
	id:'annualStateGraph',
	items: [{
		xtype: 'container',
		layout: {
			type: 'vbox',
			align: 'stretch'
		},
		items: [{
			flex: 1,
			xtype: 'container',
			layout: {
				type: 'hbox',
				align: 'stretch'
			},
			margins: '7 5 5 5',
			padding: '0 0 2 0',
			items: [
			      comboAnnualApplication,
			      comboAnnualSchema,
			      comboAnnualYear
			]
		}, {
			flex: 12,
			frame: false,
			xtype: 'container',
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: [
				annualBarChartConfig                   
			]
		}]
	}]		
});

function helpReports(){
	popupReportsHelp();
}
function changeNumberOfDays() {
	var numberOfDays = -1;
	var m = [31,28,31,30,31,30,31,31,30,31,30,31];
	if (detailedStateMonth != 2) {
		numberOfDays = m[detailedStateMonth - 1];
	} else if (detailedStateYear%4 != 0) {
		numberOfDays = m[1];
	} else if (detailedStateYear%100 == 0 && detailedStateYear%400 != 0) {
		numberOfDays = m[1];
	} else {
		numberOfDays = m[1] + 1;		
	}
	dayStore.removeAll();
	for(var count = 1; count <= numberOfDays; count++) {
		dayStore.add ({
			id: count ,
			name: count.toString()
		});
	}
	detailedStateDay = d.getDate();
	comboDetailedDay.setValue(d.getDate());
}