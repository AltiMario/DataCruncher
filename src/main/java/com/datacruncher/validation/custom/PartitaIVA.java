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

package com.datacruncher.validation.custom;

import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;

public class PartitaIVA implements SingleValidation {
    private ResultStepValidation result = new ResultStepValidation();
    private boolean isValid(String partitaIva) {
        if (partitaIva == null || partitaIva.length() == 0) {
            result.setMessageResult("Empty or NULL value");
            return false;
        }
        int i, c, s;
        if (partitaIva.length() != 11) {
            if ( partitaIva.length() > 11) {
                result.setMessageResult("Too long (expected 11, got "+partitaIva.length()+")");
            }else{
                result.setMessageResult("Too short (expected 11, got "+partitaIva.length()+")");
            }
                return false;
        }
        for (i = 0; i < 11; i++) {
            if (partitaIva.charAt(i) < '0' || partitaIva.charAt(i) > '9') {
                result.setMessageResult("Invalid character at position "+ i);
                return false;
            }
        }
        s = 0;
        for (i = 0; i <= 9; i += 2) {
            s += partitaIva.charAt(i) - '0';
        }
        for (i = 1; i <= 9; i += 2) {
            c = 2 * (partitaIva.charAt(i) - '0');
            if (c > 9) {
                c = c - 9;
            }
            s += c;
        }
        return (10 - s % 10) % 10 == partitaIva.charAt(10) - '0';
    }
    public ResultStepValidation checkValidity(String partitaIva){
        try{
            result.setMessageResult("");
            if(isValid(partitaIva)){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Partita Iva: [" + partitaIva + "] wrong. "+ result.getMessageResult());
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Partita Iva: [" + partitaIva + "] wrong. " +e.getMessage());
        }
        return result;
    }
}