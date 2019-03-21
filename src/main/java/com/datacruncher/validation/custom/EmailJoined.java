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
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class EmailJoined implements SingleValidation {
    private String invalidCause = null;
    private ResultStepValidation result = new ResultStepValidation();
    private char[] chrDel  = {';',',','-',' '} ;
    public boolean isValid(String strEmail) {
        Email email= Email.getInstance(true);
        boolean ret = email.isValid(strEmail);
        invalidCause = email.getInvalidCause();
        return ret;
    }
    @Override
    public ResultStepValidation checkValidity(String emails){
        String email;
        boolean foundChr = false;
        char chrDelimiter=chrDel[0];
        result.setValid(true);
        try{
            for (char aChrDel : chrDel) {
                if ((emails.indexOf(aChrDel, 0)) < 0) {
                    foundChr = false;
                } else {
                    foundChr = true;
                    chrDelimiter = aChrDel;
                    break;
                }
            }
            if(foundChr) {
                int last=emails.lastIndexOf(chrDelimiter);
                if(last == emails.length()-1)
                    emails= emails.substring(0,emails.length()-1);
                List<String> emailList = Arrays.asList(StringUtils.split(emails.trim(), chrDelimiter));
                for (String anEmailList : emailList) {
                    email = anEmailList;
                    if (!isValid(email.trim())) {
                        result.setValid(false);
                        result.setMessageResult("EmailJoined: [" + email + "] wrong.\n" + invalidCause);
                    }
                }
            }else{
                email = emails;
                if(isValid(email)){
                    result.setValid(true);
                }else{
                    result.setValid(false);
                    result.setMessageResult("EmailJoined: [" + email + "] wrong.\n"+invalidCause);
                }
            }
        } catch (Exception exception) {
            result.setValid(false);
            result.setMessageResult("EmailJoined: [" + emails + "] wrong.\n"+exception.getMessage());
        }
        return result;
    }
}
