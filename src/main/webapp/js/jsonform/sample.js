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
    makeRequest(validateTemporal);
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
                if ('key' in item && ('annotation' in schema[item.key] || 'regex' in schema[item.key])) {
                    item.onChange = fieldChanged;
                }
            });
            $('form').jsonForm(jsonFormData);
            $('form .form-group').addClass('col-md-4');
            var rows = [];
            $('form .form-group').each(function(i, element) {
                var row;
                var rowIndex = Math.floor(i / 3);
                if (rowIndex >= rows.length) {
                    rows[rowIndex] = $('<div>', { 'class': 'row'});
                }
                row = rows[rowIndex];
                if (rowIndex % 2 !== 0) {
                    row.css('background-color', 'lightgray');
                }
                row.append(element);
            });
            var button = $('form input[type=submit]');
            button.css('margin', '1em 0 0 1em');
            $('form div').empty().append(rows).append(button);
            button.click(submitForm);
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

function validateTemporal(params) {
    $('#formError').hide().empty();
    var jsonpFormField = new JSONP('jsonFormTemporal');
    jsonpFormField.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate/temporal', params, function(data) {
        var errorMessage = '';
        if ('errors' in data && data.errors.length > 0) {
            errorMessage = data.errors.join('<br>');
        } else if ('notFoundToken' in data) {
            errorMessage = 'Token not found';
        } else if ('serverException' in data) {
            errorMessage = data.serverException;
        } else if ('notActive' in data) {
            errorMessage = 'Schema is not active';
        } else if ('notAvail' in data) {
            errorMessage = 'Schema is not available';
        }
        if (errorMessage) {
            $('#formError').show().html(errorMessage);
        } else {
            makeRequest(validateForm);
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
    var jsonpFormValidate = new JSONP('jsonFormValidate');
    jsonpFormValidate.get(APPLICATION_CONTEXT_URL + '/rest/formvalidate/validate-jsonform', params, function(data) {
        if (typeof data === 'string') {
            $('#formError').show().html(data);
        } else if ('notFoundToken' in data) {
            alert("Token not found");
            return;
        } else if ('errors' in data) {
            var errors = data.errors;
            if (errors) {
                for (var fieldName in errors) {
                    displayFieldError(fieldName, errors[fieldName].split('<br>').join(' '));
                }
            }
            $('.jsonform-errortext:hidden').prev('input').css('background-color', '#5cb85c77');
            $('.jsonform-errortext:visible').prev('input').css('background-color', '#eea23677');
        }
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
    $(event.target).css('background-color', 'white');
    var name = $(event.target).attr('name');
    var value = $(event.target).val();
    if (value !== null && value !== '') {
        var params = {
            'name': name,
            'value': value,

        };
        if ('annotation' in schema[name]) {
            params.annotation = schema[name].annotation;
        }
        if ('regex' in schema[name]) {
            params.regex = schema[name].regex;
        }
        makeRequest(validateFieldValue, params);
    }
}
