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

/**
 * @class Menupad.Viewport
 * @extends Ext.Panel
 * This is a default generated class which would usually be used to initialize your application's
 * main viewport. By default this is simply a welcome screen that tells you that the app was
 * generated correctly.
 */
 
DataCruncher.Viewport = Ext.extend(Ext.Panel, {
    id        : 'viewport',
    layout: {
        type: 'card',
        align: 'stretch'
    },
    fullscreen: true,
	initComponent: function() {
		var config = {};
		Ext.apply(this, config);
        //put instances of cards into app.views namespace
        Ext.apply(DataCruncher.views, {
        	loginForm: new DataCruncher.views.LoginForm(),
			trackList: new DataCruncher.views.TrackList(),
			receivedTrackGrid: new DataCruncher.views.ReceivedTrackGrid()			
        });
        //put instances of cards into viewport
        Ext.apply(this, {
            items: [
                DataCruncher.views.loginForm,
				DataCruncher.views.trackList,
				DataCruncher.views.receivedTrackGrid
            ]
        });
        DataCruncher.Viewport.superclass.initComponent.apply(this, arguments);
    },	
});