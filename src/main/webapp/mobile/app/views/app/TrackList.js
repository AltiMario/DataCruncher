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

DataCruncher.views.TrackList = Ext.extend(Ext.Panel, {
    initComponent : function(){
        var config = {            
            dockedItems : 
				{
					dock: 'top',
					xtype: 'toolbar',
					title: 'Track List',
					type: 'light',
					items : [
						{
							xtype : 'spacer'
						},
						{
							text : 'Logout',
							ui: 'confirm',
							handler: this.onLogOutAction,
							scope: this
						}
					],
				},
            items : [{
                xtype : 'list',
                itemTpl : '{name}',
                store : DataCruncher.stores.Tracks,
                listeners : {
                    scope : this,
                    itemtap : function(dataview, index, el, e){
					    var trackRecord = DataCruncher.stores.Tracks.getAt(index);

                        DataCruncher.stores.ReceivedTracks.setProxy(new Ext.data.AjaxProxy({                   
                            url: DataCruncher.util.actions.datastreamReadUrl,
                            reader: {
                                root: 'results',
                                totalProperty : 'total'
                            },
                            extraParams : {
                                idSchema : trackRecord.get('idSchema'),
                                start : 0
                            }
                        }));

                        DataCruncher.stores.ReceivedTracks.load({
                            params : {
                                start : 0
                            }
                        });
						
						DataCruncher.viewport.setActiveItem(DataCruncher.views.receivedTrackGrid, {
                            type: 'slide',
                            direction: 'left'
                        });
                    }
                }
            }]
        }
        
        Ext.apply(this, config);
        DataCruncher.views.TrackList.superclass.initComponent.apply(this, arguments);

        this.addEvents({
            trackitemtap : true
        });		
    },
	onLogOutAction: function() {
		Ext.Ajax.request({
			url: DataCruncher.util.actions.logoutUrl,//'../logout.json',			
			success: function (result) {
				localStorage.setItem("loginstatus", false);
				Ext.dispatch({
					controller: 'DataCruncher',
					action    : 'showLogin',
					historyUrl: "DataCruncher/logout"		
				});									
			},
			failure: function(resuilt){
				alert("failure");
			}
		});		
	},
});