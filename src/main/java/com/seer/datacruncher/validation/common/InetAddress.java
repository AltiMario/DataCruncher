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

/**
 * <p><b>InetAddress</b> validation and conversion routines (<code>java.net.InetAddress</code>).</p>
 *
 * <p>This class provides methods to validate a candidate IP address.
 *
 * <p>
 * This class is a Singleton; you can retrieve the instance via the {@link #getInstance()} method.
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
package com.seer.datacruncher.validation.common;

import com.seer.datacruncher.utils.validation.SingleValidation;
import com.seer.datacruncher.validation.ResultStepValidation;

import java.io.Serializable;

public class InetAddress implements SingleValidation,Serializable {

    private static final String IPV4_REGEX =
            "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";

    /**
     * Singleton instance of this class.
     */
    private static final InetAddress VALIDATOR = new InetAddress();

    /** IPv4 Regex */
    private final Regex ipv4Validator = new Regex(IPV4_REGEX);

    private String invalidCause = null;

    /**
     * Returns the singleton instance of this validator.
     * @return the singleton instance of this validator
    */
    public static InetAddress getInstance() {
        return VALIDATOR;
    }
    /**
     * Checks if the specified string is a valid IP address.
     * @param inetAddress the string to validate
     * @return true if the string validates as an IP address
     */
    public boolean isValid(String inetAddress) {
        return isValidInet4Address(inetAddress);
    }
    /**
     * Checks if the specified string is a valid IP address.
     * @param inetAddress the string to validate
     * @return ResultStepValidation
     */
    public ResultStepValidation checkValidity(String inetAddress) {
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValidInet4Address(inetAddress)){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("InetAddress: [" + inetAddress + "] wrong. "+invalidCause );
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("InetAddress: [" + inetAddress + "] wrong. " +e.getMessage());
        }
        return result;
    }

    /**
     * Validates an IPv4 address. Returns true if valid.
     * @param inet4Address the IPv4 address to validate
     * @return true if the argument contains a valid IPv4 address
     */
    private boolean isValidInet4Address(String inet4Address) {
        // verify that address conforms to generic IPv4 format
        String[] groups = ipv4Validator.match(inet4Address);

        if (groups == null){
            invalidCause = "Address does not conform to generic IPv4 format";
            return false;
        }

        // verify that address subgroups are legal
        for (int i = 0; i <= 3; i++) {
            String ipSegment = groups[i];
            if (ipSegment == null || ipSegment.length() <= 0) {
                invalidCause = "Segment "+ (i+1) + " is empty or NULL value";
                return false;
            }

            int iIpSegment;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            } catch(NumberFormatException e) {
                invalidCause = "Error Segment "+ (i+1) +" "+ e.getMessage();
                return false;
            }

            if (iIpSegment > 255) {
                invalidCause = "Segment "+ (i+1) + " is > 255";
                return false;
            }
        }
        return true;
    }
}