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

public final class FieldType {
	private FieldType() {}
	
	// Branch
    public static final int all = 1;
    public static final int choice = 2;
    public static final int sequence = 3;
	// Leaf
    public static final int alphanumeric = 4;
    public static final int numeric = 5;
    public static final int date = 6;
    
    public static final String ALPHANUMERIC = "Alphanumeric";
    public static final String NUMERIC = "Numeric";
    public static final String DATE = "Date";
    
	public static String getFieldTypeAsStringById(int id) {
		return id == FieldType.alphanumeric ? FieldType.ALPHANUMERIC : id == FieldType.numeric ? FieldType.NUMERIC
				: id == FieldType.date ? FieldType.DATE : "";
	}
}
