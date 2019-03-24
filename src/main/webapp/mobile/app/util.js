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

DataCruncher.util = {
    actions : function(){
    	var jsonDir = '../';    	
        return {
        	loginUrl : jsonDir + 'login.json',
        	logoutUrl: jsonDir + 'logout.json',
        	datastreamReadUrl : jsonDir + 'datastreamsReceivedPopupRead.json',
            schemaReadUrl : jsonDir + 'schemasRead.json?idSchemaType=1',
            deleteActionUrl : jsonDir + 'datastreamsReceivedPopupDestroy.json',
            validateStreamUrl : jsonDir + 'validateDatastreamPopup.json'
        };        
    }(),

    getOverlayHeight : function(height){
        var desiredHeight = Math.min(Ext.Element.getViewportHeight(), height) * 0.9;

        return desiredHeight;
    },

    getOverlayWidth : function(width){
        var desiredWidth = Math.min(Ext.Element.getViewportWidth(), width) * 0.9;

        return desiredWidth;
    }
}