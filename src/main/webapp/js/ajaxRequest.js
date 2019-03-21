/*
 *   DataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   DataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  altimario@gmail.com
 */

var DEPLOYMENT_PATH = '';
var DOMAIN_PREFIX = 'http://localhost:8008' + DEPLOYMENT_PATH;
var meWindow = this.window;
/**
 * Post request to service to validate a form.
 * 
 * @param {Object} current element
 * @param {String} validation schema id
 */
function request(currEl, validationSchema) {
	var form = null;
	if (currEl.tagName == 'FORM') {
		form = currEl;
	} else {
		var el = currEl.parentNode;
		while (el && el.tagName != 'FORM') {
			el = el.parentNode;
		}
		if (el && el.tagName == 'FORM') {
			form = el;
		} else {
			alert('Form for validation is not found');
			return;
		}		
	}
	var jsonp = new JSONP('json1');
	jsonp.get(DOMAIN_PREFIX + '/rest/formvalidate/gettoken', null,
		function(data) {
			if ('success' in data) {
				var token = data.success;
				var paramsObj = getParametersObject(form);
				paramsObj.fSchema = validationSchema;
				paramsObj.tokenParameter = token;
				var jsonp2 = new JSONP('json2');
				jsonp2.get(DOMAIN_PREFIX + '/rest/formvalidate', paramsObj,
					function(obj) {
						var msgNoToken = "Token not found";
						if ('notFoundToken' in obj) { 
							alert("Communication error: " + msgNoToken);
							return;
						} else if ('serverException' in obj) {
							alert(obj.serverException);
							return;
						} else if ('notActive' in obj) {
							alert('Schema with id = \'' + obj.notActive + '\' is not active');
							return;
						} else if ('notAvail' in obj) {
							alert('Schema with id = \'' + obj.notAvail + '\' is not available');
							return;							
						}
						clearForm(form);
						var errors = obj.errors;
						if (errors) {
							for (var i in errors) {
								var arr = getFormElementsByName(form, i);
								if (arr.length == 0) arr = getFormElementsById(form, i);
								if (arr.length > 0) {
									//.split('<br>').join('') equals to replaceAll() but faster
									arr[0].title = errors[i].split('<br>').join(' ');
									arr[0].className += ' redInput';
								} else {
									alert('Failed validation Input type is not found within document');
								}
							}
						} else {
							form.submit();
						}
					}
				);
			}		
		}
	);	
}

/**
 * Gets XMLHttpRequest.
 */
function getXMLHTTP() {
	var xmlhttp;
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xmlhttp;
}

/**
 * getElementsByName() works only for document, so here's implementation for form. 
 * 
 * @param {Object} form
 * @param {Object} name
 */
function getFormElementsByName(form, name) {
	var doc = form.ownerDocument;
	var arr = doc.getElementsByName(name);
	var resArr = new Array();
	for (var i = 0; i < arr.length; i++) {
		if (hasParent(arr[i], form)) {
			resArr.push(arr[i]);
		}
	}
	return resArr;
}

/**
 * getElementsById() works only for document, so here's implementation for form. 
 * 
 * @param {Object} form
 * @param {Object} name
 */
function getFormElementsById(form, name) {
	var doc = form.ownerDocument;
	var el = doc.getElementById(name);
	var resArr = new Array();
	if (el && hasParent(el, form)) {
		resArr.push(el);
	}
	return resArr;
}

/**
 * Checks whether el has parent 'parent'.
 * 
 * @param {Object} el
 * @param {Object} parent
 */
function hasParent(el, parent) {
	var e = el.parentNode;
	while (e && e != parent) {
		e = e.parentNode;
	}
	return (e && e == parent) ? true : false;
}

/**
 * Retrieve all input names (or ids if there's no name) within form.
 * 
 * @param {Object} form
 * @return {Array} 
 */
function getInputs(form) {
	var excludeTypes = ['button', 'checkbox', 'color', 'file',
			'hidden', 'image', 'password', 'radio', 'range', 'reset', 'search',
			'submit', 'tel'];
	var x = form.getElementsByTagName("input");
	var arr = new Array();
	for (var j = x.length - 1; j >= 0; j--) {
		if (excludeTypes.indexOf(x[j].type) == -1) {
			arr.push(x[j]);
		}
	}
	return arr;
}

/**
 * Form a parameters object for post request.
 *
 * @param {Object} form
 * @return {Object} 
 */
function getParametersObject(form) {
	var parsObj = {};
	var inputs = getInputs(form);	
	for (var j = inputs.length - 1; j >= 0; j--) {
		var s = inputs[j].name;
		if (s == '') s = inputs[j].id;
		if (s != '') {
			//pars += s + '=' + inputs[j].value + '&';
			parsObj[s] = inputs[j].value;
		}	
	}
	return parsObj;
}

/**
 * Clear validation highlights (error hints and red color highlighted inputs).  
 * 
 * @param form
 */
function clearForm(form) {
	var inputs = []; 
	inputs = getInputs(form);
	for (var j = inputs.length - 1; j >= 0; j--) {
		var i = inputs[j];
		i.title = '';
		i.className = '';		
	}
}
 
/**
 * Dump object attributes.
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


/*
* Lightweight JSONP fetcher
* Copyright 2010-2012 Erik Karlsson. All rights reserved.
* BSD licensed
* 
* This code is greatly modified by stanly_mainly according to DataCruncher needs
* Original code can be found here http://www.berthojoris.com/menu/blog/artikel/Lightweight-JSONP-javascript-library.aspx
*/
 
function JSONP (wrapperName) {
	var counter = 0, head, query, window = meWindow;
	this.load = function(url) {
		var script = document.createElement('script'), done = false;
		script.src = url;
		script.async = true;
		script.charset = "UTF-8";
		script.onload = script.onreadystatechange = function() {
			if (!done
					&& (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
				done = true;
				script.onload = script.onreadystatechange = null;
				if (script && script.parentNode) {
					script.parentNode.removeChild(script);
				}
			}
		};
		if (!head) {
			head = document.getElementsByTagName('head')[0];
		}
		head.appendChild(script);
	}
	
	this.encode = function(str) {
		return encodeURIComponent(str);
	}
	
	this.get = function(url, params, callback, callbackName) {
		query = (url || '').indexOf('?') === -1 ? '?' : '&';
		params = params || {};
		var key;
		for (key in params) {
			if (params.hasOwnProperty(key)) {
				query += this.encode(key) + "=" + this.encode(params[key]) + "&";
			}
		}
		var jsonp = wrapperName;
		window[jsonp] = function(data) {
			callback(data);
			try {
				delete window[jsonp];
			} catch (e) {
			}
			window[jsonp] = null;
		};
		this.load(url + query);
		return jsonp;
	}
}

