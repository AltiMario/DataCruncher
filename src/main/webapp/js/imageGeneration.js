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

function schemaXSDExportToPNG(){
    var record = schemasGrid.getSelectionModel().getSelection()[0];
    if (!record) {
        App.setAlert(false , _alert['selectRecord']);
        return false;
    };
    editorSchemas.cancelEdit();
    Ext.Ajax.request( {
        params : {
            idSchema : record.get('idSchema')
        },
        success: function (result) {
            recordResult = Ext.JSON.decode(result.responseText);
            if (eval(recordResult.success)) {
                var iFrame = document.createElement("iframe");
                iFrame.src = './schemaXSDExportToPNG.json?action=downloadPNG&idSchema='  + record.get('idSchema');
                iFrame.style.display = "none";
                document.body.appendChild(iFrame);
            } else {
                callAlert(recordResult.responseMsg);
            }
        },
        url : './schemaXSDExportToPNG.json?action=checkValidity'
    });
};