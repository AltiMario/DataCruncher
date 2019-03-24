/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

/**
 * @class DataCruncher
 * @extends Ext.Controller
 * The DataCruncher controller
 */
Ext.regController("DataCruncher", {
	showLogin: function(){
		this.application.viewport.setActiveItem(
			DataCruncher.views.loginForm,
			{
				type: 'slide',
				direction: 'left'
			}
		);
	},
	logout: function(){
	},
	schemaList: function() {
        DataCruncher.stores.Tracks.load();
		if(localStorage.getItem("loginstatus") != undefined) {
			if(localStorage.getItem("loginstatus") == "true") {
				this.application.viewport.setActiveItem(
					DataCruncher.views.trackList,
					{
						type: 'slide',
						direction: 'left'
					}
				);
			}else{
				this.application.viewport.setActiveItem(
					DataCruncher.views.loginForm,
					{
						type: 'slide',
						direction: 'left'
					}
				);
			}
		}
		
		if (!DataCruncher.views.trackList) {        
            this.application.viewport.add(DataCruncher.views.trackList);
            this.application.viewport.doLayout();
        }
    }
});