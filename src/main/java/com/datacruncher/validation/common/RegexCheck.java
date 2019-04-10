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
package com.datacruncher.validation.common;

import com.datacruncher.utils.generic.I18n;
import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;

import java.text.MessageFormat;

public class RegexCheck extends Regex implements SingleValidation {
    public RegexCheck(String regex) {
        super(regex);
    }

    public RegexCheck(String regex, boolean caseSensitive) {
        super(regex, caseSensitive);
    }

    public RegexCheck(String[] regexs) {
        super(regexs);
    }

    public RegexCheck(String[] regexs, boolean caseSensitive) {
        super(regexs, caseSensitive);
    }

    @Override
    public ResultStepValidation checkValidity(String code) {
        final String regexResult = validate(code);
        ResultStepValidation result = new ResultStepValidation();
        result.setValid(regexResult != null);
        result.setMessageResult(MessageFormat.format(I18n.getMessage("error.validationPatternMismatch"), code));
        return result;
    }
}
