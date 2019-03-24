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

/*
Gli attributi presenti sono equivalenti (e devono restare tali) alla mappatura su DB e sul front-end
*/
package com.datacruncher.constants;

public final class ApplicationConfigType {
	private ApplicationConfigType() {}
	
    public static final int DATABASE = 1;
    public static final int FTP = 2;
    public static final int EMAIL = 3;
    public static final int APPLURL = 4;
    
}
