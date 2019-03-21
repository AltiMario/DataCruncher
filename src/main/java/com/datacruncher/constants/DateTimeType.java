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

/*
Gli attributi presenti sono equivalenti (e devono restare tali) alla mappatura su DB e sul front-end
*/

package com.datacruncher.constants;

public final class DateTimeType {
	// Date Constants
	public static final int slashDDMMYYYY	= 1;	// dd/MM/yyyy
    public static final int signDDMMYYYY	= 2; 	// dd-MM-yyyy
    public static final int dotDDMMYYYY		= 3;	// dd.MM.yyyy    
	public static final int slashDDMMYY		= 4;	// dd/MM/yy 
	public static final int signDDMMYY		= 5;	// dd-MM-yy
	public static final int dotDDMMYY		= 6;	// dd.MM.yy	
	public static final int slashYYYYMMDD	= 7;	// yyyy/MM/dd
	public static final int signYYYYMMDD	= 8;	// yyyy-MM-dd
	public static final int dotYYYYMMDD		= 9;	// yyyy.MM.dd
	public static final int DDMMYYYY		= 10;	// ddMMyyyy
	public static final int DDMMYY			= 11;	// ddMMyy
	public static final int YYYYMMDD		= 12;	// yyyyMMdd
	// Time Constants
	public static final int dblpnthhmmss	= 1; 	// hh:mm:ss 
	public static final int dothhmmss		= 2; 	// hh.mm.ss
	public static final int dblpnthhmm		= 3; 	// hh:mm
	public static final int dothhmm			= 4; 	// hh.mm
	public static final int dblpntZhhmmss	= 5; 	// hh:mm:ss AM/PM
	public static final int dotZhhmmss		= 6; 	// hh.mm.ss AM/PM
    public static final int dblpnthmmss	    = 7; 	// h:mm:ss
    public static final int dothmmss		= 8; 	// h.mm.ss
	
	// XSD Date Type Constants
	public static final int xsdDateTime		= 4;
	public static final int xsdDate			= 5;	
    public static final int xsdTime			= 6; 
    public static final int unixTimestamp	= 7;
    
}