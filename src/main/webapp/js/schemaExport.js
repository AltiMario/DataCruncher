/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

function schemaExportToXSD() {
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
                iFrame.src = './schemaExportToXSD.json?action=downloadXSD&idSchema=' + record.get('idSchema');
                iFrame.style.display = "none";
                document.body.appendChild(iFrame);
            } else {
                callAlert(recordResult.responseMsg);
            }
        },
        url : './schemaExportToXSD.json?action=checkValidity'
    });
}
