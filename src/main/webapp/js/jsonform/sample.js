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

var schema;
var schemaName = (new URL(window.location.href)).searchParams.get('schema');

function submitForm(event) {
    event.preventDefault();
    makeRequest(validateForm);
}

function makeRequest(requestDelegate, params) {
	var jsonpToken = new JSONP('json1');
	jsonpToken.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate/gettoken', null, function(data) {
        if (!('success' in data)) {
            return;
        }
        if (!params) {
            params = {};
        }
        params.schema = schemaName;
        params.tokenParameter = data.success;
        requestDelegate(params);
    });
}

function requestJsonForm(params) {
    var jsonpForm = new JSONP('jsonForm');
    jsonpForm.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate/jsonform', params, function(data) {
        if ('schema' in data && 'form' in data) {
            var jsonFormData = data;
            schema = jsonFormData.schema;
            jsonFormData.form.forEach(function(item, i, arr) {
                if ('key' in item && 'annotation' in schema[item.key]) {
                    item.onChange = fieldChanged;
                }
            });
            $('form').jsonForm(jsonFormData);
            $('form input[type=submit]').click(submitForm);
        } else if ('errors' in data) {
            alert(data.errors.join('\n'));
        }
    });
}

function validateFieldValue(params) {
    var fieldName = params.name;
    $('input[name="' + fieldName + '"]').css('background-color', 'white');
    var jsonpFormField = new JSONP('jsonFormField');
    jsonpFormField.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate/field', params, function(data) {
        if ('errors' in data) {
            displayFieldError(fieldName, data.errors.join('<br>'));
        }
    });
}

function validateForm(params) {
    var form = $('form');
    if (!form.length) {
        return;
    }
    form = form[0];
    var paramsObj = getParametersObject(form);
    for (var attrname in paramsObj) {
        params[attrname] = paramsObj[attrname];
    }
    $('.jsonform-errortext').hide();
    $('.jsonform-errortext').prev('input').css('background-color', 'white');
    var jsonpFormValidate = new JSONP('json2');
    jsonpFormValidate.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate', params, function(data) {
        var msgNoToken = "Token not found";
        if ('notFoundToken' in data) {
            alert("Communication error: " + msgNoToken);
            return;
        }
        var errors = data.errors;
        if (errors) {
            for (var fieldName in errors) {
                displayFieldError(fieldName, errors[fieldName].split('<br>').join(' '));
            }
        }
        $('.jsonform-errortext:hidden').prev('input').css('background-color', '#5cb85c77');
        $('.jsonform-errortext:visible').prev('input').css('background-color', '#eea23677');
    });
}

function displayFieldError(fieldName, errorMessage) {
    var fieldInput = $('input[name="' + fieldName + '"]');
    fieldInput.css('background-color', '#eea23677');
    var errorContainer = fieldInput.next('.jsonform-errortext');
    if (errorContainer) {
        errorContainer.html(errorMessage);
        errorContainer.show();
    }
}

function fieldChanged(event) {
    $(event.target).next('.jsonform-errortext').hide();
    var name = $(event.target).attr('name');
    var value = $(event.target).val();
    if (value) {
        var params = {
            'name': name,
            'value': value,
            'annotation': schema[name].annotation
        };
        makeRequest(validateFieldValue, params);
    }
}
