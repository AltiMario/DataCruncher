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

/**
 * @class Menu
 * @extends Ext.data.Model
 * The MenuGroup model
 */

DataCruncher.models.ReceivedTracks = Ext.regModel("ReceivedTracks", {
    fields: [{
        name: "idSchema",
        type: "int"
    },{
        name: "idDatastream",
        type: "int"
    },{
        name: "datastream",
        type: "string"
    },{
        name: "checked",
        type: "boolean"
    },{
        name: "message",
        type: "string"
    }]
});
