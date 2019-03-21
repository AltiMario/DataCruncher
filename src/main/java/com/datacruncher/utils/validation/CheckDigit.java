
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
 * <b>Check Digit</b> calculation and validation.
 * <p>
 * The logic for validating check digits has previously been
 * embedded within the logic for specific code validation, which
 * includes other validations such as verifying the format
 * or length of a code. {@link CheckDigit} provides for separating out
 * the check digit calculation logic enabling it to be more easily
 * tested and reused.
 * <p>
 * Although Commons Validator is primarily concerned with validation,
 * {@link CheckDigit} also defines behaviour for calculating/generating check
 * digits, since it makes sense that users will want to (re-)use the
 * same logic for both. The {@link com.datacruncher.validation.common.ISBN}
 * makes specific use of this feature by providing the facility to validate ISBN-10 codes
 * and then convert them to the new ISBN-13 standard.
 * <p>
 * {@link CheckDigit} is used by the new generic
 * <a href="..\Code.html">Code</a> implementation.
 * <p>
 * <h3>Implementations</h3>
 * See the 
 * <a href="package-summary.html">Package Summary</a> for a full
 * list of implementations provided within Commons Validator.
 *
 * @see Code
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
package com.datacruncher.utils.validation;

public interface CheckDigit {

    /**
     * Calculate the <i>Check Digit</i> for a code.
     *
     * @param code The code to calculate the Check Digit for.
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs.
     */
    public String calculate(String code) throws CheckDigitException;

    /**
     * Validate the check digit for the code.
     *
     * @param code The code to validate.
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>.
     */
    public boolean isValid(String code);

}
