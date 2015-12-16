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
 * Check Digit calculation/validation error.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
package com.seer.datacruncher.utils.validation;

public class CheckDigitException extends Exception {

    /**
     * Construct an Exception with no message.
     */
    public CheckDigitException() {
    }

    /**
     * Construct an Exception with a message.
     *
     * @param msg The error message.
     */
    public CheckDigitException(String msg) {
        super(msg);
    }

    /**
     * Construct an Exception with a message and
     * the underlying cause.
     *
     * @param msg The error message.
     * @param cause The underlying cause of the error
     */
    public CheckDigitException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
