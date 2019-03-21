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

Ext.onReady(function() {
	Ext.QuickTips.init();

	var dbInfoMenu = Ext.create('Ext.menu.Menu', {
		id : 'dbInfoMenu',
		style : {
			overflow : 'visible'
		},
		items : [ {
			text : 'General Info',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, {
			text : 'Support Info',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, {
			text : 'Limitation Info',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-', {
			text : 'Functions Info',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, {
			text : 'Type Info',
			menu : {
				items : [

				{
					text : 'Standard SQL Type Info',
					checked : false,
					group : 'theme',

				}, {
					text : 'User Define Type Info',
					checked : false,
					group : 'theme',

				} ]
			}
		}, {
			text : 'Object Info',
			menu : {
				items : [ {
					text : 'Catalog Info',
					checked : true,
					group : 'theme',

				}, {
					text : 'Schema Info',
					checked : false,
					group : 'theme',

				}, '-', {
					text : 'Procedure Info',
					checked : true,
					group : 'theme',

				}, {
					text : 'Parameter Info',
					checked : true,
					group : 'theme',

				}, '-', {
					text : 'Index Info',
					checked : true,
					group : 'theme',

				} ]
			}
		}, '-', {
			text : 'Table Model Info',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Summary Info',
			menu : {
				items : [

				{
					text : 'DB Meta Data Info',
					checked : false,
					group : 'theme',

				}, {
					text : 'Table Meta Data Info',
					checked : false,
					group : 'theme',

				}, {
					text : 'Data Info',
					checked : false,
					group : 'theme',

				} ]
			}
		}, {
			text : 'Privilege Info',
			menu : {
				items : [

				{
					text : 'All Tables Info Info',
					checked : false,
					group : 'theme',

				}, {
					text : 'Table Info',
					checked : false,
					group : 'theme',

				}, {
					text : 'Column Info',
					checked : false,
					group : 'theme',

				} ]
			}
		}, ]
	});

	var fileMenu = Ext.create('Ext.menu.Menu', {
		id : 'fileMenu',
		style : {
			overflow : 'visible'
		},
		items : [ {
			text : 'Open',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, {
			text : 'Show Console',
			menu : '' // <-- submenu by reference
		} ]
	});

	var toolsMenu = Ext.create('Ext.menu.Menu', {
		id : 'toolsMenu',
		style : {
			overflow : 'visible'
		},
		items : [ {
			text : 'SQL Interface',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Import File',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Create Format',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Search DB',
			menu : '' // <-- submenu by reference
		} ]
	});

	var dataQualityMenu = Ext.create('Ext.menu.Menu', {
		id : 'dataQualityMenu',
		style : {
			overflow : 'visible'
		},
		items : [ {
			text : 'Duplicate',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Similarity',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Standardisation',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-', {
			text : 'Replace Null',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		},'-', {
			text : 'InComplete',
			menu : {
				items : [

				{
					text : 'AND (Inclusive)',
					checked : false,
					group : 'theme',

				}, {
					text : 'OR (Exclusive)',
					checked : false,
					group : 'theme',

				} ]
			}
		},'-', {
			text : 'Formatted',
			menu : {
				items : [ {
					text : 'Match',
					checked : false,
					group : 'theme',

				}, {
					text : 'No Match',
					checked : false,
					group : 'theme',

				} ]
			}
		}, '-', {
			text : 'Case Format',
			menu : {
				items : [

				{
					text : 'UPPER CASE',
					checked : false,
					group : 'theme',

				}, {
					text : 'lower case',
					checked : false,
					group : 'theme',

				}, {
					text : 'Title Case',
					checked : false,
					group : 'theme',

				}, {
					text : 'Sentence Case',
					checked : false,
					group : 'theme',

				} ]
			}
		}, '-',{
			text : 'Descrete Range',
			menu : {
				items : [

				{
					text : 'Descrete Match',
					checked : false,
					group : 'theme',

				}, {
					text : 'Descrete No Match',
					checked : false,
					group : 'theme',

				} ]
			}
		}, '-',{
			text : 'Cardinality',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		}, '-',{
			text : 'Cardinality Editable',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		},'-', {
			text : 'Table Comparison',
			iconCls : 'calendar',
			menu : '' // <-- submenu by reference
		} ]
	});

	var tb = Ext.create('Ext.toolbar.Toolbar');
	tb.suspendLayout = true;
	tb.render('toolbar');
	dbInfoMenu.add(' ');

	tb.add({
		icon : 'preview.png',
		cls : 'x-btn-text-icon',
		text : 'File',
		menu : fileMenu
	});

	tb.add({
		icon : 'preview.png',
		cls : 'x-btn-text-icon',
		text : 'DB Info',
		menu : dbInfoMenu
	});

	tb.add({
		icon : 'preview.png',
		cls : 'x-btn-text-icon',
		text : 'Tools',
		menu : toolsMenu
	});
	
	tb.add({
		icon : 'preview.png',
		cls : 'x-btn-text-icon',
		text : 'Data Quality',
		menu : dataQualityMenu
	});

	tb.suspendLayout = false;
	tb.doLayout();

	function onItemClick(item) {
		// TODO
	}

});
