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
 * Modulus 10 <b>EAN-13</b> / <b>UPC</b> / <b>ISBN-13</b> Check Digit
 * calculation/validation.
 * <p>
 * Check digit calculation is based on <i>modulus 10</i> with digits in
 * an <i>odd</i> position (from right to left) being weighted 1 and <i>even</i>
 * position digits being weighted 3.
 * <p>
 * For further information see:
 * <ul>
 *   <li>EAN-13 - see 
 *       <a href="http://en.wikipedia.org/wiki/European_Article_Number">Wikipedia - 
 *       European Article Number</a>.</li>
 *   <li>UPC - see
 *       <a href="http://en.wikipedia.org/wiki/Universal_Product_Code">Wikipedia -
 *       Universal Product Code</a>.</li>
 *   <li>ISBN-13 - see 
 *       <a href="http://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 *       Standard Book Number (ISBN)</a>.</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
package com.datacruncher.utils.validation;

public final class EAN13CheckDigit extends ModulusCheckDigit {

    /** Singleton EAN-13 Check Digit instance */
    public static final CheckDigit EAN13_CHECK_DIGIT = new EAN13CheckDigit();

    /** weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = new int[] {3, 1};

    /**
     * Construct a modulus 10 Check Digit routine for EAN/UPC.
     */
    public EAN13CheckDigit() {
        super(10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.</p>
     *
     * <p>For EAN-13 (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>three</b>.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right 
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        return (charValue * weight);
    }
}