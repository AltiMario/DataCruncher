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

DataCruncher.views.ReceivedTrackGrid = Ext.extend(Ext.ux.TouchGridPanel, {
    multiSelect : false,

    selectedIndex : null,

    selections : null,
            
    initComponent : function(){
        var config = {
            store       : DataCruncher.stores.ReceivedTracks,
            plugins: [new Ext.ux.touch.PagingToolbar()],
            dockedItems : [{
                xtype : "toolbar",
                dock  : "top",
                title : "Received Tracks",
                defaults : {
                    scope : this
                },
                items : [{
                    text : 'Tracks',
                    ui : 'back',
                    handler : function(){
                        this.selectedIndex = null;
                        DataCruncher.viewport.setActiveItem(DataCruncher.views.trackList, {
                            type: 'slide',
                            direction: 'right'
                        });
                    }
                }, {
                    xtype : 'spacer'
                }, {
                    text : 'Cancella',
                    handler : function(){
                        this.deleteRow();
                    }
                }, {
                    text : 'Modifica',
                    handler : this.modifyDatastream
                }]
            }],
            colModel    : [{
                header   : "Id",
                mapping  : "idDatastream",
                style    : "text-align: center;",
                flex     : 1
            },{
                header   : "Message",
                mapping  : "message",                
                flex : 10
            },{
                header   : "Checked",
                mapping  : "checked",
                style    : "text-align: center;",
                flex     : 1
            }],
            listeners: {
                beforeselect: function(dataview, nodes, selections) {
                    console.log(selections);
                },
                containertap: function(dataview, e) {
                    console.log(dataview);
                },
                itemdoubletap: function(dataview, index, el, e) {
                    console.log(index);
                },
                itemswipe: function(dataview, index, el, e) {
                    console.log(index);
                },
                itemtap: function(dataview, index, el, e) {
                    this.selectedIndex = index;
                },
                selectionchange: function(selectionModel, selections) {
                    this.selections = selections;
                    console.log(selectionModel, selections);
                }
            }
        }
        
        Ext.apply(this, config);
        DataCruncher.views.ReceivedTrackGrid.superclass.initComponent.apply(this, arguments);
    },

    deleteRow : function(index){
        var record = this.getView().getSelectedRecords()[0];

        if(record){
            var row = this.getRow(record),
            newRecIndex = this.getStore().indexOf(record) + 1;
            
            if(this.getStore().getAt(newRecIndex)){
                this.getView().getSelectionModel().select(newRecIndex);
            }

            this.getStore().remove(record);
            Ext.get(row).remove();
            
            Ext.Ajax.request({
                url : DataCruncher.util.actions.deleteActionUrl,
                params : {
                    results : record.get('idDatastream')
                },
                callback : function(a,b,c){
                    console.log(a,b,c);
                }
            });            
        }
    },

    modifyDatastream : function(){
        if(this.selectedIndex !== null){
            if(!this.datastreamPanel){
                this.validPathArea = new Ext.form.TextArea({
                    id : 'validpath_area',
                    height : 230
                });
                
                this.datastreamPanel = new Ext.Panel({
                    draggable : true,
                    floating : true,
                    modal: true,
                    centered: true,
                    width : DataCruncher.util.getOverlayWidth(500),
                    height : DataCruncher.util.getOverlayHeight(300),
                    layout : 'fit',
                    items : this.validPathArea,
                    showAnimation : 'slide',
                    dockedItems : [{
                        xtype : 'toolbar',
                        dock : 'top',
                        title : 'Valida Tracciato'
                    }, {
                        xtype : 'toolbar',
                        dock : 'bottom',
                        defaults : {
                            scope : this
                        },
                        items : [{
                            xtype : 'spacer'
                        }, {
                            text : 'Valida',
                            handler : function(){
                                this.validate();
                                this.datastreamPanel.hide();
                            }
                        }, {
                            text : 'Chiudi',
                            handler : function(){
                                if(this.validationMsgSheet){
                                    this.validationMsgSheet.hide();
                                }
                                this.datastreamPanel.hide();
                            }
                        }]
                    }],
                    listeners : {
                        scope : this,
                        hide : function(){
                        }
                    }
                });
            }

            this.datastreamPanel.show();

            var rec = this.getView().getRecord(this.getRow(this.selectedIndex));
            
            this.validPathArea.setValue(rec.get('datastream'));
        }
    },

    validate : function(){
        Ext.Ajax.request({
            scope : this,
            url : DataCruncher.util.actions.validateStreamUrl,
            params : {
                idSchema : this.selections[0].get('idSchema'),
                datastream : this.validPathArea.getValue()
            },
            callback : function(options, status, response){
                var msg = this.messageBox();
                msg.updateMsg(response.responseText, true);
                msg.show();
            }
        });
    },

    messageBox : function(){
        if(!this.validationMsgSheet){
            ValidationMsgSheet = Ext.extend(Ext.Sheet, {
                cls : 'validatormsg',
                height  : 100,
                width : 400,
                stretchX: false,
                stretchY: false,
                enter : 'top',
                exit : 'top',
                constructor : function(config){
                    Ext.apply(this, config);
                    ValidationMsgSheet.superclass.constructor.call(this);
                },
                
                items : [{
                    id : 'data_holder'
                }],

                updateMsg : function(msg, autohide){
                    Ext.getCmp('data_holder').update(msg);
                    if(autohide){
                        var task = new Ext.util.DelayedTask(function(){
                            this.hide();
                        }, this);
                        task.delay(2000);
                    }
                }
            });

            this.validationMsgSheet = new ValidationMsgSheet();
        }

        return this.validationMsgSheet;
    }
});


