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

var loadingStreamFieldsTree = Ext.create('Ext.tree.Panel', {
	autoScroll : true,
	frame : false,
	id : 'loadingStreamFieldsTree',
	store : new Ext.data.TreeStore({
		url : 'schemaFieldsTreePopupRead.json',
		autoSync : true,
		proxy : {
			type : 'ajax'
		},
		fields : [ {
			name : 'id',
			type : 'String'
		}, {
			name : 'text',
			type : 'String'
		}, {
			name : 'linkToDb',
			type : 'String'
		} ]
	}),
	// singleExpand: true,
	viewConfig : {
		plugins : {
			ptype : 'treeviewdragdrop'
		},
		style : {
			overflowY : 'scroll',
			overflowX : 'scroll'
		}
	},

	listeners : {
		itemclick : function(view, record, item, index, event) {
			loadingStreamFieldsHandler(view, record, event);
		},
		itemcontextmenu : function(view, record, item, index, event) {
			event.stopEvent();
			loadingStreamFieldsHandler(view, record, event);
		},
	/*
	 * itemmove : function(node, oldParent, newParent, index, options) {
	 * Ext.Ajax.request({ params: { idSchemaField: node.get('id'), idNewParent:
	 * newParent.get('id'), idOldParent: oldParent.get('id'), elementOrder:
	 * index + 1 }, url: 'schemaFieldsTreePopupMove.json', success :
	 * function(result) { recordResult = Ext.JSON.decode(result.responseText);
	 * if (!eval(recordResult.success)) {
	 * generationStreamFieldsTree.store.load(); } } }); }
	 */
	},
	rootVisible : true,
	useArrows : true
});

function loadingStreamFieldsHandler(treeview, record, event) {
	if (!record.get('leaf')) {
		callAlert('Please select field node');
		return false;
	}
	var itemSelected = loadingStreamGrid.getSelectionModel().getSelection()[0];
	if (!itemSelected) {
		App.setAlert(false, _alert['selectRecord']);
		return false;
	}
	var menu = new Ext.menu.Menu({
		items : [ {
			iconCls : 'linkToDb',
			handler : function() {
				popupTrackField(record.data, 'modify', 'leaf', itemSelected.get('idSchema'));
			},
			text : _label['linkToDatabase']
		} ]
	});
	menu.showAt(event.getPageX(), event.getPageY());
}

