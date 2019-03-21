
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
 * Represents all fields of the object in readable way. 
 * Usage example: alert(dump(obj))
 * 
 * @param {Object} obj
 * @return {TypeName} 
 */
function dump(obj) {
	var str = "";
	for (var i in obj) {
		str += i + " = " + obj[i] + "\n";
	}
	return str;
}

/**
 * Analog of java methos messageFormat().
 * Example: '{0} + {1} = {2}'.jv_format(2, -1, 1) -> '2 + -1 = 1'
 * 
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.jv_format = function() {
    var args = arguments;
    return this.replace(/\{(\d+)\}/g, function() {
        return args[arguments[1]];
    });
};

/**
 * Reverse of a string.
 * Example: 'dcba'.jv_reverse() -> 'abcd'
 * 
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.jv_reverse = function() {
    var s = "";
    var i = this.length;
    while (i > 0) {
        s += this.substring(i - 1, i);
        i--;
    }
    return s;
}

/**
 * Removes quotation marks for string.
 * Example: 'abc' -> abc
 * 
 * @param {Object} str
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.withoutQuots = function(str) {
	return this.substring(1, this.length - 1);
};

/**
 * Trim of a string.
 * Example: '  abcd  '.jv_trim() -> 'abcd'
 * 
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.jv_trim = function() {
	return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '')
}

/**
 * Checks whether string starts with prefix.
 * 
 * @param {Object} prefix
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.startsWith = function(str) {
    return this.indexOf(str) == 0;
};


/**
 * Checks whether string ends with suffix.
 * 
 * @param {Object} suffix
 * @memberOf {TypeName} 
 * @return {TypeName} 
 */
String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

/**
 * Show simple alert window (extJs).
 * 
 * @param {Object} content - html content of the window
 */
function callAlert(content) {
	    new Ext.Window({ 
			height: 140,
			width: 220,
			layout: 'absolute',
			modal: true,
			resizable: false,
		    title: _label['alert'],
			html : '<div style="padding: 5px;">' + content + '</div>',
			buttons : [
				{text: 'Ok', xtype: 'button' , width: 100, 
					handler: function() {
						this.ownerCt.ownerCt.close();
					}
				}
			]
	    }).show(); 
}