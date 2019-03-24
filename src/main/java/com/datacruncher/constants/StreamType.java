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

public final class StreamType {
    public static final int XML = 1;
    public static final int XMLEXI = 2;
    public static final int flatFileFixedPosition = 3;
    public static final int flatFileDelimited = 4;
    public static final int JSON = 5;
    public static final int EXCEL = 6;
    public static final int HL7 = 7;
    public static final int SWIFT = 8;
    public static final int EDI_CICA = 9;
    
    public static String getFileExtension(int streamType) {
    	switch (streamType) {
    		case XML : return "xml"; 
    		case XMLEXI : return "exi";
    		case flatFileDelimited : 
    		case flatFileFixedPosition : return "txt";
    		case JSON : return "json";
    		case EXCEL : return "xls";
    	}
    	return null;
    }
    
    public static String getContentType(int streamType) {
    	switch (streamType) {
    		case XML : return "application/xml"; 
    		case XMLEXI : return "application/exi";
    		case flatFileDelimited : 
    		case flatFileFixedPosition : return "text/plain";
    		case JSON : return "application/json";
    		case EXCEL : return "application/vnd.ms-excel";
    	}
    	return null;
    }
}

