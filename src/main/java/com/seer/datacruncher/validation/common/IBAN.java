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

/**
 * Utility class to validate IBAN codes.
 *
 * The IBAN consists of a ISO 3166-1 alpha-2 country code, followed by two check 
 * digits (represented by kk in the examples below), and up to thirty alphanumeric 
 * characters for the domestic bank account number, called the BBAN (Basic Bank 
 * Account Number).
 *
 * <h1>Exampe usage scenario</h1>
 * <code><pre>IBAN iban = new IBAN("ES2153893489");
 * if (iban.isValid())
 * 		System.out.println("ok");
 * else
 * 		System.out.println("problem with iban: "+iban.getInvalidCause());
 * </pre></code>
 *
 * @author www.prowidesoftware.com
 * @since 3.3
 * @version $Id: IBAN.java,v 1.2 2010/10/17 01:22:01 zubri Exp $
 */
package com.seer.datacruncher.validation.common;
import com.seer.datacruncher.utils.validation.SingleValidation;
import com.seer.datacruncher.validation.ResultStepValidation;

import java.math.BigInteger;
import java.util.logging.Level;

public class IBAN implements SingleValidation {
    private static final BigInteger BD_97 = new BigInteger("97");
    private static final BigInteger BD_98 = new BigInteger("98");
    private String invalidCause = null;
    private String iban;
    private ResultStepValidation result = new ResultStepValidation();
    private static transient final java.util.logging.Logger log = java.util.logging.Logger.getLogger(IBAN.class.getName());

    /**
     * Get the IBAN
     * @return a string with the IBAN
     */
    public String getIban() {
        return iban;
    }

    /**
     * Set the IBAN
     * @param iban the IBAN to set
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Create an IBAN object with the given iban code.
     * This constructor does not perform any validation on the iban, only
     * @param iban the string to validate
     */
    public IBAN(String iban) {
        this.iban = iban;
    }

    public IBAN() {
    }
    /**
     * Completely validate an IBAN
     * Currently validation checks that the length is at least 5 chars:
     * (2 country code, 2 verifying digits, and 1 BBAN)
     * checks the country code to be valid an the BBAN to match the verifying digits
     *
     * @return ResultStepValidation
     */
    public boolean isValid(String iban) {
        this.iban=iban;
        return isValid();
    }
    public ResultStepValidation checkValidity(String iban){
        try{
            this.iban=iban;
            
            if(isValid()){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("IBAN: [" + iban + "] wrong.\n"+getInvalidCause());

            }
        } catch (Exception exception) {
            result.setValid(false);
            result.setMessageResult("IBAN: [" + iban + "] wrong.\n"+ exception.getMessage());
        }
        return result;
    }
    public boolean isValid() {
        if (this.iban==null)
            throw new IllegalStateException("Iban is null");
        invalidCause = null;
        final String code = removeNonAlpha(this.iban);
        final int len = code.length();
        if (len<4) {
            this.invalidCause="Too short (expected at least 4, got "+len+")";
            return false;
        }
        final String country = code.substring(0, 2);
        if (!ISOCountries.getInstance().isValid(country)) {
            this.invalidCause = "Invalid ISO country code: "+country;
            return false;
        }

        final StringBuffer bban = new StringBuffer(code.substring(4));
        if (bban.length()==0) {
            this.invalidCause="Empty Basic Bank Account Number";
            return false;
        }
        bban.append(code.substring(0, 4));
        if (log.isLoggable(Level.FINE)) log.fine("iban: "+bban);

        String workString = translateChars(bban);
        int mod = modulo97(workString);
        if (mod!=1) {
            this.invalidCause = "Verification failed (expected 1 and obtained "+mod+")";
            return false;
        }

        return true;
    }


    /**
     * Translate letters to numbers, also ignoring non alphanumeric characters
     *
     * @param bban  the string to validate
     * @return the translated value
     */
    public String translateChars(final StringBuffer bban) {
        final StringBuffer result = new StringBuffer();
        for (int i=0;i<bban.length();i++) {
            char c = bban.charAt(i);
            if (Character.isLetter(c)) {
                result.append(Character.getNumericValue(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     *
     * @param iban the string to validate
     * @return the resulting IBAN
     */
    public String removeNonAlpha(final String iban) {
        final StringBuffer result = new StringBuffer();
        for (int i=0;i<iban.length();i++) {
            char c = iban.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c) ) {
                result.append(c);
            }
        }
        return result.toString();
    }

    private int modulo97(String bban) {
        BigInteger b = new BigInteger(bban);
        b = b.divideAndRemainder(BD_97)[1];
        b = BD_98.min(b);
        b = b.divideAndRemainder(BD_97)[1];
        return b.intValue();
    }

    /**
     * Get a string with information about why the IBAN was found invalid
     * @return a human readable (english) string
     */
    public String getInvalidCause() {
        return invalidCause;
    }

}