/*
 * Copyright (c) 2015  www.see-r.com
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

package com.seer.datacruncher.validation.custom;

import com.seer.datacruncher.validation.ResultStepValidation;
import com.seer.datacruncher.utils.validation.SingleValidation;

public class PhoneNumberExtractorIT implements SingleValidation {

    private ResultStepValidation result = new ResultStepValidation();
    public ResultStepValidation checkValidity(String text){
        PhoneNumberExtractor p = new PhoneNumberExtractor();
        p.setLanguageSelect("it");
        if(p.isValid(text)){
            result.setWarning(true);
        }else{
            result.setValid(false);
        }
        result.setMessageResult(p.result.getMessageResult());
        return result;
    }
}
