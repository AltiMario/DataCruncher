/**
 * @class Ext.jv.grid.plugin.RowEditingValidation
 * @extends Ext.ux.grid.plugin.RowEditing
 * @xtype jv.rowediting
 * 
 * Improve Ext.ux.grid.plugin.RowEditing, form server validation implemented.<br/>
 * 
 * @author      stanly <br/>
 * @date        2012-12-21  <br/>
 * @version     1.0   <br/>
 *
 */
Ext.define('Ext.jv.grid.plugin.RowEditingValidation', {
    extend: 'Ext.ux.grid.plugin.RowEditing',
    alias: 'jv.rowediting', 
    
    /**
     * Required parameter. Schema to be applied for validation.
     * 
     * @param {Object} 
     * @memberOf {TypeName} 
     */
    validationScheme : '',
    
    /**
     * Method to be invoked if form validation succeed. 
     * 
     * @param {Object} 
     * @memberOf {TypeName} 
     */
    validateEditSuccess: Ext.emptyFn,
    
    /**
     * Validation result.
     * @param {Object}
     * @memberOf {TypeName} 
     */
    isSuccessValidation : true,
    excludeParams : '',    
    /**
     * Server response object.
     * @param {Object}
     * @memberOf {TypeName} 
     */
    ob : null,
    
    /**
	 * Init: set 'validateedit' capture, server request, events suspended until request completed.
	 * @param {Object} grid
     * @memberOf {TypeName} 
     */
    init: function(grid) {
        var me = this;
		var f = function(eventName) {
			if (eventName == 'validateedit') {
				var form = me.getEditor();
				me.suspendEvents(true);
				var excludeFields = '';			
				if(me.excludeParams.length > 0) {
					if(me.excludeParams.indexOf(',') == -1) {
						var fieldValue = this.getEditor().getForm().findField(me.excludeParams).getValue();
						if(fieldValue == null || fieldValue == '') {
							excludeFields = me.excludeParams;
						}
					} else {
						//TODO: when multiple field given in excludeParams value.
					}
				}

				form.submit( {
					waitMsg : _message["waitMessage"],
					url : 'controller.validateForm.json',
					params : {
						schemaType : me.validationScheme,
						excludeParams : excludeFields
					},
					success : function(form, action) {
						me.isSuccessValidation = true;								
						me.resumeEvents();
					},
					failure : function(form, action) {
						me.ob = action.result;
						me.isSuccessValidation = false;
						me.resumeEvents();
					}
				}); 
			}
		};
        Ext.util.Observable.capture(me, f);
		me.on('validateedit', me.val);
		me.on('canceledit', me.cancelEditing, grid);
        me.callParent(arguments);
    },
    
    /**
     * This event resumed after server validation completes.
     * 
     * @param {Object} editor
     * @param {Object} e
     * @memberOf {TypeName} 
     */
	val: function(editor, e) {
	    if (this.isSuccessValidation) {
			this.validateEditSuccess();
		} else {
			this.startEdit(e.record, e.colIdx);
			this.getEditor().getForm().markInvalid(this.ob.errors);
		}
	},
    
	/**
	 * Revert grid changes on 'cancel' button click.
	 * 
	 * @param {Object} grid
	 */
    cancelEditing : function(grid) {
		grid.store.load();
    }
});