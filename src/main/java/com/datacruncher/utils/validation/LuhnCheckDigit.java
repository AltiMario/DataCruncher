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

/**
 * Modulus 10 <b>Luhn</b> Check Digit calculation/validation.
 * <p>
 * Luhn check digits are used, for example, by:
 * <ul>
 *    <li><a href="http://en.wikipedia.org/wiki/Credit_card">Credit Card Numbers</a></li>
 *    <li><a href="http://en.wikipedia.org/wiki/IMEI">IMEI Numbers</a> - International
 *        Mobile Equipment Identity Numbers</li>
 * </ul>
 * Check digit calculation is based on <i>modulus 10</i> with digits in
 * an <i>odd</i> position (from right to left) being weighted 1 and <i>even</i>
 * position digits being weighted 2 (weighted values greater than 9 have 9 subtracted).
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Luhn_algorithm">Wikipedia</a>
 * for more details.
 *
 * @version $Revision: 909009 $ $Date: 2010-02-11 15:59:45 +0100 (gio, 11 feb 2010) $
 * @since Validator 1.4
 */

package com.datacruncher.utils.validation;

public final class LuhnCheckDigit extends ModulusCheckDigit {

    /** Singleton Luhn Check Digit instance */
    public static final CheckDigit LUHN_CHECK_DIGIT = new LuhnCheckDigit();

    /** weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = new int[] {2, 1};

    /**
     * Construct a modulus 10 Luhn Check Digit routine.
     */
    public LuhnCheckDigit() {
        super(10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values > 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right 
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        int weightedValue = (charValue * weight);
        return (weightedValue > 9 ? (weightedValue - 9) : weightedValue);
    }
}
