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

DataCruncher.stores.ReceivedTracks = new Ext.data.Store({
    //autoLoad : true,
    model: 'ReceivedTracks',
    sorters: 'idDatastream',
    pageSize : 10,
    listeners : {
        load : function(){
            //sort by a single field
            DataCruncher.stores.ReceivedTracks.sort('idDatastream', 'DESC');
        }
    }
});