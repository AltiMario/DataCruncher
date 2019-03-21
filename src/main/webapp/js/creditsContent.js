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

Ext.define('credits', {
    extend: 'Ext.data.Model',
    fields: [
		{name:'idCredits', type:'int', defaultValue:0},
		{name: 'name',   type: 'string'},
        {name: 'description', type: 'string'},
        {name : 'link' , type: 'string'}
    ],
	idProperty:'idCredits'
});

var readCredits = new Ext.data.Store({
	autoSave: false,
	autoLoad: true,
	model: 'credits',
	proxy: {
		type: 'ajax',
        api: {
			read    : './creditsRead.json'
		},
        reader: {
            type: 'json',
            root: 'results',
			successProperty: 'success',
			messageProperty: 'message',
			idProperty: 'idCredits'
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

var columnsCredits = [
	{dataIndex: 'name' , header: _label['name'] , sortable: true , width: 200},
	{dataIndex: 'description' , header: _label['description'] , sortable: true ,renderer: columnWrap , width: 650},
	{dataIndex: 'link' , header: _label['link'] , sortable: true , width: 200, renderer: renderHyperLink}
];

var creditsGrid = new Ext.grid.GridPanel({
	columnLines: true,
	columns: columnsCredits,
	frame: false,
	id: 'creditsGrid',
	store: readCredits,
	title: _label['credits_sw']
});

function columnWrap(val){
    return '<div style="white-space:normal;">'+ val +'</div>';
}
//renderer function
function renderHyperLink(val) {
    return '<a href="http://'+ val + '" target="_blank"> ' + val  +'</a>';
}