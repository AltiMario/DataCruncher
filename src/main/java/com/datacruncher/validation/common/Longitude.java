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

import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;

/**
 * A class to represent a longitude
 */
public class Longitude implements SingleValidation {

    /**
     * The minimum allowed longitude
     */
    public static float MIN_LONGITUDE = Float.valueOf("-180.0000");

    /**
     * The maximum allowed longitude
     */
    public static float MAX_LONGITUDE = Float.valueOf("180.0000");

    /**
     * A method to validate a longitude value
     *
     * @param longitude the longitude to check is valid
     *
     * @return          true if the longitude is between the MIN and MAX longitude
     */
    public boolean isValid(float longitude) {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    public ResultStepValidation checkValidity(String longitude){
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValid(Float.valueOf(longitude))){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Latitude: [" + longitude + "] wrong.");
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Latitude: [" + longitude + "] wrong.");
        }
        return result;
    }
}