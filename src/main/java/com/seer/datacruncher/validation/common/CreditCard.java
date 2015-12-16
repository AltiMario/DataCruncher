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
 * <p>Perform credit card validations.</p>
 * <p>
 * By default, all supported card types are allowed.  You can specify which 
 * cards should pass validation by configuring the validation options.  For 
 * example,<br/><code>CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.AMEX + CreditCardValidator.VISA);</code>
 * configures the validator to only pass American Express and Visa cards.
 * If a card type is not directly supported by this class, you can implement
 * the CreditCardType interface and pass an instance into the 
 * <code>addAllowedCardType</code> method.
 * </p>
 * For a similar implementation in Perl, reference Sean M. Burke's
 * <a href="http://www.speech.cs.cmu.edu/~sburke/pub/luhn_lib.html">script</a>.
 * More information is also available
 * <a href="http://www.merriampark.com/anatomycc.htm">here</a>.
 *
 * @version $Revision: 1128380 $ $Date: 2011-05-27 18:15:02 +0200 (ven, 27 mag 2011) $
 * @since Validator 1.4
 */
package com.seer.datacruncher.validation.common;

import com.seer.datacruncher.utils.validation.CheckDigit;
import com.seer.datacruncher.utils.validation.Code;
import com.seer.datacruncher.utils.validation.LuhnCheckDigit;
import com.seer.datacruncher.utils.validation.SingleValidation;
import com.seer.datacruncher.validation.ResultStepValidation;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class CreditCard implements SingleValidation, Serializable {

    /**
     * Option specifying that no cards are allowed.  This is useful if
     * you want only custom card types to validate so you turn off the
     * default cards with this option.
     * <br/>
     * <pre>
     * CreditCardValidator v = new CreditCardValidator(CreditCardValidator.NONE);
     * v.addAllowedCardType(customType);
     * v.isValid(aCardNumber);
     * </pre>
     */
    public static final long NONE = 0;

    /**
     * Option specifying that American Express cards are allowed.
     */
    public static final long AMEX = 1 << 0;

    /**
     * Option specifying that Visa cards are allowed.
     */
    public static final long VISA = 1 << 1;

    /**
     * Option specifying that Mastercard cards are allowed.
     */
    public static final long MASTERCARD = 1 << 2;

    /**
     * Option specifying that Discover cards are allowed.
     */
    public static final long DISCOVER = 1 << 3;

    /**
     * Option specifying that Diners cards are allowed.
     */
    public static final long DINERS = 1 << 4;

    /**
     * The CreditCardTypes that are allowed to pass validation.
     */
    private final List<Code> cardTypes = new ArrayList<Code>();

    /**
     * Luhn checkdigit validator for the card numbers.
     */
    private static final CheckDigit LUHN_VALIDATOR = LuhnCheckDigit.LUHN_CHECK_DIGIT;

    /** American Express (Amex) Card Validator */
    public static final Code AMEX_VALIDATOR = new Code("^(3[47]\\d{13})$", LUHN_VALIDATOR);

    /** Diners Card Validator */
    public static final Code DINERS_VALIDATOR = new Code("^(30[0-5]\\d{11}|3095\\d{10}|36\\d{12}|3[8-9]\\d{12})$", LUHN_VALIDATOR);

    /** Discover Card regular expressions */
    private static final Regex DISCOVER_REGEX = new Regex(new String[] {"^(6011\\d{12})$", "^(64[4-9]\\d{13})$", "^(65\\d{14})$"});

    /** Discover Card Validator */
    public static final Code DISCOVER_VALIDATOR = new Code(DISCOVER_REGEX, LUHN_VALIDATOR);

    /** Mastercard Card Validator */
    public static final Code MASTERCARD_VALIDATOR = new Code("^(5[1-5]\\d{14})$", LUHN_VALIDATOR);

    /** Visa Card Validator */
    public static final Code VISA_VALIDATOR = new Code("^(4)(\\d{12}|\\d{15})$", LUHN_VALIDATOR);

    private ResultStepValidation result = new ResultStepValidation();
    /**
     * Create a new CreditCardValidator with default options.
     */
    public CreditCard() {
        this(AMEX + VISA + MASTERCARD + DISCOVER);
    }

    /**
     * Create a new CreditCardValidator with the specified options.
     * @param options Pass in
     * CreditCardValidator.VISA + CreditCardValidator.AMEX to specify that 
     * those are the only valid card types.
     */
    public CreditCard(long options) {
        super();

        if (isOn(options, VISA)) {
            this.cardTypes.add(VISA_VALIDATOR);
        }

        if (isOn(options, AMEX)) {
            this.cardTypes.add(AMEX_VALIDATOR);
        }

        if (isOn(options, MASTERCARD)) {
            this.cardTypes.add(MASTERCARD_VALIDATOR);
        }

        if (isOn(options, DISCOVER)) {
            this.cardTypes.add(DISCOVER_VALIDATOR);
        }

        if (isOn(options, DINERS)) {
            this.cardTypes.add(DINERS_VALIDATOR);
        }
    }

    /**
     * Create a new CreditCardValidator with the specified {@link Code}s.
     * @param creditCardValidators Set of valid code validators
     */
    public CreditCard(Code[] creditCardValidators) {
        if (creditCardValidators == null) {
            throw new IllegalArgumentException("Card validators are missing");
        }
        for (int i = 0; i < creditCardValidators.length; i++) {
            cardTypes.add(creditCardValidators[i]);
        }
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return Whether the card number is valid.
     */
    private boolean isValid(String card) {
        if (card == null || card.length() == 0) {
            return false;
        }        
        
        for(Code type : cardTypes) {         
            if (type.isValid(card)) {
                return true;
            }
        }
        return false;
    }

    public ResultStepValidation checkValidity(String card){
       try{
            if(isValid(card)){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Credit Card: [" + card + "] wrong.");

            }
       }catch(Exception e)  {
           result.setValid(false);
           result.setMessageResult("Credit Card: [" + card + "] wrong. " +e.getMessage());
       }
       return result;
    }
    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return The card number if valid or <code>null</code>
     * if invalid.
     */
    public Object validate(String card) {
        if (card == null || card.length() == 0) {
            return null;
        }
        Object result = null;
        
        for(Code type : cardTypes) {        
            result = type.validate(card);
            if (result != null) {
                return result ;
            }
        }
        return null;

    }
    /**
     * Tests whether the given flag is on.  If the flag is not a power of 2 
     * (ie. 3) this tests whether the combination of flags is on.
     *
     * @param options The options specified.
     * @param flag Flag value to check.
     *
     * @return whether the specified flag value is on.
     */
    private boolean isOn(long options, long flag) {
        return (options & flag) > 0;
    }

}
