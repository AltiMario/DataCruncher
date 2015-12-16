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
 * <b>ISBN-10</b> and <b>ISBN-13</b> Code Validation.
 * <p>
 * This validator validates the code is either a valid ISBN-10 
 * (using a {@link com.seer.datacruncher.utils.validation.Code} with the {@link com.seer.datacruncher.utils.validation.ISBN10CheckDigit})
 * or a valid ISBN-13 code (using a {@link com.seer.datacruncher.utils.validation.Code} with the
 * the {@link com.seer.datacruncher.utils.validation.EAN13CheckDigit} routine).
 * <p>
 * The <code>validate()</code> methods return the ISBN code with formatting
 * characters removed if valid or <code>null</code> if invalid.
 * <p>
 * This validator also provides the facility to convert ISBN-10 codes to
 * ISBN-13 if the <code>convert</code> property is <code>true</code>.
 * <p>
 * From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number). ISBN-13 codes are
 * <a href="http://en.wikipedia.org/wiki/European_Article_Number">EAN</a>
 * codes, for more information see:</p>
 *
 * <ul>
 *   <li><a href="http://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 *       Standard Book Number (ISBN)</a>.</li>
 *   <li>EAN - see 
 *       <a href="http://en.wikipedia.org/wiki/European_Article_Number">Wikipedia - 
 *       European Article Number</a>.</li>
 *   <li><a href="http://www.isbn.org/standards/home/isbn/transition.asp">ISBN-13
 *       Transition details</a>.</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
package com.seer.datacruncher.validation.common;

import com.seer.datacruncher.utils.validation.*;
import com.seer.datacruncher.validation.ResultStepValidation;

import java.io.Serializable;

public class ISBN implements SingleValidation, Serializable {

    private static final String SEP = "(?:\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";

    /**
     * ISBN-10 consists of 4 groups of numbers separated by either dashes (-)
     * or spaces.  The first group is 1-5 characters, second 1-7, third 1-6,
     * and fourth is 1 digit or an X.
     */
    static final String ISBN10_REGEX     =
            "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";

    /**
     * ISBN-13 consists of 5 groups of numbers separated by either dashes (-)
     * or spaces.  The first group is 978 or 979, the second group is 
     * 1-5 characters, third 1-7, fourth 1-6, and fifth is 1 digit.
     */
    static final String ISBN13_REGEX     =
            "^(978|979)(?:(\\d{10})|(?:" + SEP + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9])))$";

    /** ISBN Code Validator (which converts ISBN-10 codes to ISBN-13 */
    private static final ISBN ISBN_VALIDATOR = new ISBN();

    /** ISBN Code Validator (which converts ISBN-10 codes to ISBN-13 */
    private static final ISBN ISBN_VALIDATOR_NO_CONVERT = new ISBN(false);


    /** ISBN-10 Code Validator */
    private Code isbn10 = new Code(ISBN10_REGEX, 10, ISBN10CheckDigit.ISBN10_CHECK_DIGIT);

    /** ISBN-13 Code Validator */
    private Code isbn13 = new Code(ISBN13_REGEX, 13, EAN13CheckDigit.EAN13_CHECK_DIGIT);

    private final boolean convert;

    /**
     * Return a singleton instance of the ISBN validator which
     * converts ISBN-10 codes to ISBN-13.
     *
     * @return A singleton instance of the ISBN validator.
     */
    public static ISBN getInstance() {
        return ISBN_VALIDATOR;
    }

    /**
     * Return a singleton instance of the ISBN validator specifying
     * whether ISBN-10 codes should be converted to ISBN-13.
     *
     * @param convert <code>true</code> if valid ISBN-10 codes
     * should be converted to ISBN-13 codes or <code>false</code>
     * if valid ISBN-10 codes should be returned unchanged.
     * @return A singleton instance of the ISBN validator.
     */
    public static ISBN getInstance(boolean convert) {
        return (convert ? ISBN_VALIDATOR : ISBN_VALIDATOR_NO_CONVERT);
    }

    /**
     * Construct an ISBN validator which converts ISBN-10 codes
     * to ISBN-13.
     */
    public ISBN() {
        this(true);
    }

    /**
     * Construct an ISBN validator indicating whether
     * ISBN-10 codes should be converted to ISBN-13.
     *
     * @param convert <code>true</code> if valid ISBN-10 codes
     * should be converted to ISBN-13 codes or <code>false</code>
     * if valid ISBN-10 codes should be returned unchanged.
     */
    public ISBN(boolean convert) {
        this.convert = convert;
    }

    /**
     * Check the code is either a valid ISBN-10 or ISBN-13 code.
     *
     * @param code The code to validate.
     * @return <code>true</code> if a valid ISBN-10 or 
     * ISBN-13 code, otherwise <code>false</code>.
     */
    public boolean isValid(String code) {
        return (isValidISBN13(code) || isValidISBN10(code));
    }
    /**
     * Check the code is either a valid ISBN-10 or ISBN-13 code.
     *
     * @param code The code to validate.
     * @return <code>ResultStepValidation</code>
     */
    public ResultStepValidation checkValidity(String code){
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValid(code.toUpperCase())){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("ISBN: [" + code + "] wrong." );
            }
        }catch (Exception e) {
            result.setValid(false);
            result.setMessageResult("ISBN: [" + code + "] wrong.\n"+ e.getMessage());
        }
        return result;
    }
    /**
     * Check the code is a valid ISBN-10 code.
     *
     * @param code The code to validate.
     * @return <code>true</code> if a valid ISBN-10 
     * code, otherwise <code>false</code>.
     */
    public boolean isValidISBN10(String code) {
        return isbn10.isValid(code);
    }

    /**
     * Check the code is a valid ISBN-13 code.
     *
     * @param code The code to validate.
     * @return <code>true</code> if a valid ISBN-13 
     * code, otherwise <code>false</code>.
     */
    public boolean isValidISBN13(String code) {
        return isbn13.isValid(code);
    }

    /**
     * Check the code is either a valid ISBN-10 or ISBN-13 code.
     * <p>
     * If valid, this method returns the ISBN code with
     * formatting characters removed (i.e. space or hyphen).
     * <p>
     * Converts an ISBN-10 codes to ISBN-13 if 
     * <code>convertToISBN13</code> is <code>true</code>.
     *
     * @param code The code to validate.
     * @return A valid ISBN code if valid, otherwise <code>null</code>.
     */
    public String validate(String code) {
        String result = validateISBN13(code);
        if (result == null) {
            result = validateISBN10(code);
            if (result != null && convert) {
                result = convertToISBN13(result);
            }
        }
        return result;
    }

    /**
     * Check the code is a valid ISBN-10 code.
     * <p>
     * If valid, this method returns the ISBN-10 code with
     * formatting characters removed (i.e. space or hyphen).
     *
     * @param code The code to validate.
     * @return A valid ISBN-10 code if valid,
     * otherwise <code>null</code>.
     */
    public String validateISBN10(String code) {
        Object result = isbn10.validate(code);
        return (result == null ? null : result.toString());
    }

    /**
     * Check the code is a valid ISBN-13 code.
     * <p>
     * If valid, this method returns the ISBN-13 code with
     * formatting characters removed (i.e. space or hyphen).
     *
     * @param code The code to validate.
     * @return A valid ISBN-13 code if valid,
     * otherwise <code>null</code>.
     */
    public String validateISBN13(String code) {
        Object result = isbn13.validate(code);
        return (result == null ? null : result.toString());
    }

    /**
     * Convert an ISBN-10 code to an ISBN-13 code.
     * <p>
     * This method requires a valid ISBN-10 with NO formatting
     * characters.
     *
     * @param isbn10 The ISBN-10 code to convert
     * @return A converted ISBN-13 code or <code>null</code>
     * if the ISBN-10 code is not valid
     */
    public String convertToISBN13(String isbn10) {

        if (isbn10 == null) {
            return null;
        }

        String input = isbn10.trim();
        if (input.length() != 10) {
            throw new IllegalArgumentException("Invalid length " + input.length() + " for '" + input + "'");
        }

        // Calculate the new ISBN-13 code
        String isbn13 = "978" + input.substring(0, 9);
        try {
            String checkDigit = this.isbn13.getCheckDigit().calculate(isbn13);
            isbn13 += checkDigit;
            return isbn13;
        } catch (CheckDigitException e) {
            throw new IllegalArgumentException("Check digit error for '" + input + "' - " + e.getMessage());
        }

    }
}