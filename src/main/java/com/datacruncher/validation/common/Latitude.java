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

import com.datacruncher.validation.ResultStepValidation;
import com.datacruncher.utils.validation.SingleValidation;

/**
 * A class to represent a latitude
 */
public class Latitude implements SingleValidation {

    /**
     * The minimum allowed latitude
     */
    public static float MIN_LATITUDE = Float.valueOf("-90.0000");

    /**
     * The maximum allowed latitude
     */
    public static float MAX_LATITUDE = Float.valueOf("90.0000");


    /**
     * A method to validate a latitude value
     *
     * @param latitude the latitude to check is valid
     *
     * @return         true if the latitude is within the MIN and MAX latitude
     */
    public boolean isValid(float latitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
    }

    public ResultStepValidation checkValidity(String latitude){
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValid(Float.valueOf(latitude))){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Latitude: [" + latitude + "] wrong.");
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Latitude: [" + latitude + "] wrong.");
        }
        return result;
    }
}