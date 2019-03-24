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

package com.datacruncher.validation.custom;

import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;

public class EmailExisting implements SingleValidation {
    private String invalidCause = null;
    private ResultStepValidation result = new ResultStepValidation();
    public boolean isValid(String strEmail) {
        Email email= Email.getInstance(true);
        boolean ret = email.isValidAndExist(strEmail);
        invalidCause = email.getInvalidCause();
        return ret;
    }
    @Override
    public ResultStepValidation checkValidity(String email){
        try{
            if(isValid(email)){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("EmailExisting: [" + email + "] wrong.\n"+invalidCause);
            }
        } catch (Exception exception) {
            result.setValid(false);
            result.setMessageResult("EmailExisting: [" + email + "] wrong.\n"+exception.getMessage());
        }
        return result;
    }
}
