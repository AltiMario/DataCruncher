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

package com.datacruncher.validation.common;

import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;

public class ServerPort implements SingleValidation {

    public static int MIN_PORT = 1;
    public static int MAX_PORT = 65535;


    public boolean isValid(int portNumber) {
        return portNumber >= MIN_PORT && portNumber <= MAX_PORT;
    }

    public ResultStepValidation checkValidity(String portNumber){
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValid(Integer.valueOf(portNumber))){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Port: [" + portNumber + "] wrong.");
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Port: [" + portNumber + "] wrong.");
        }
        return result;
    }
}