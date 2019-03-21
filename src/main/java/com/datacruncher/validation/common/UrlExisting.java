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
import org.apache.log4j.Logger;

import java.net.URL;
import java.net.URLConnection;

public class UrlExisting implements SingleValidation {
    private final Logger log = Logger.getLogger(this.getClass());
    private ResultStepValidation result = new ResultStepValidation();

    public ResultStepValidation checkValidity(String urlStr){
        try {
            Url urljv = new Url();
            if (urljv.isValid(urlStr)) {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                conn.connect();
                result.setValid(true);
            }
            else {
                result.setValid(false);
                result.setMessageResult("The URL [" + urlStr + "] is not formally valid.");
            }
        }catch (Exception e) {
            log.error("The connection couldn't be established : " + e);
            result.setValid(false);
            result.setMessageResult("The connection at [" + urlStr + "] couldn't be established.");
        }
        return result;
    }
}
