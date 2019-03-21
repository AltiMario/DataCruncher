
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

function popupReportsHelp(){
	if (Ext.getCmp('popupReportsHelp')) return;	
	new Ext.Window({
		height: 250,
		id: 'popupReportsHelp',
		layout: 'absolute',
		modal: true,
		resizable: false,
		bodyStyle:'padding:10px;',
	    title: _message['help'],	 
        html: _message['reportsHelpMessage'],        
	    items:[
	         {text: _message['ok'] , xtype: 'button' , width: 100, x:280, y:180,
			handler: function() {
				Ext.getCmp('popupReportsHelp').close();
			}
	    }],
	    width: 665
    }).show(this);
}