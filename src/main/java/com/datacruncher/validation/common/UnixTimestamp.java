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

import com.datacruncher.validation.ResultStepValidation;
import com.datacruncher.utils.validation.SingleValidation;
import org.apache.log4j.Logger;

import java.util.Date;

public class UnixTimestamp implements SingleValidation {
	private final Logger log = Logger.getLogger(this.getClass());
    private ResultStepValidation result = new ResultStepValidation();

    public ResultStepValidation checkValidity(String unix_time){

		try{
			Date date = new Date ();
			date.setTime( Long.valueOf(unix_time)*1000);
            result.setValid(true);
		}catch (Exception e) {
            log.error("Unix Timestamp Exception : " + e);
            result.setValid(false);
            result.setMessageResult("Unix Timestamp: [" + unix_time + "] wrong.\n"+ e.getMessage());
        }
        return result;
	}
}
