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

package com.seer.datacruncher.profiler.framework.rdbms;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.Date;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

public class SqlType {
	public static Class<?> getClass(int i) {
		switch (i) {
		case 2003:
			return Array.class;
		case -6:
		case 4:
		case 5:
			return Integer.class;
		case -5:
		case 2:
		case 3:
		case 7:
		case 8:
			return Double.class;
		case 6:
			return Float.class;
		case 2005:
			return SerialClob.class;
		case -1:
		case 1:
		case 12:
			return String.class;
		case 2004:
			return SerialBlob.class;
		case -2:
			return Byte.TYPE;
		case -4:
		case -3:
			// return [B.class;
		case -7:
		case 16:
			return Boolean.class;
		case 91:
		case 92:
		case 93:
			return Date.class;
		case 2006:
			return Reference.class;
		case 2002:
			return Struct.class;
		}

		return new Object().getClass();
	}

	public static String getTypeName(int i) {
		String s = "";
		switch (i) {
		case 2003:
			return s = "Array";
		case -5:
			return s = "Big Integer";
		case -2:
			return s = "Binary";
		case -7:
			return s = "Bit";
		case 2004:
			return s = "Blob";
		case 16:
			return s = "Boolean";
		case 1:
			return s = "Char";
		case 2005:
			return s = "Clob";
		case 70:
			return s = "DataLink";
		case 91:
			return s = "Date";
		case 3:
			return s = "Decimal";
		case 2001:
			return s = "Distinct";
		case 8:
			return s = "Double";
		case 6:
			return s = "Float";
		case 4:
			return s = "Integer";
		case 2000:
			return s = "Java Object";
		case -4:
			return s = "Long VarBinary";
		case -1:
			return s = "Long VarChar";
		case 0:
			return s = "Null";
		case 2:
			return s = "Numeric";
		case 1111:
			return s = "DB Specific";
		case 7:
			return s = "Real";
		case 2006:
			return s = "Ref";
		case 5:
			return s = "Small Integer";
		case 2002:
			return s = "Structure";
		case 92:
			return s = "Time";
		case 93:
			return s = "TimeStamp";
		case -6:
			return s = "Tiny Integer";
		case -3:
			return s = "VarBinary";
		case 12:
			return s = "VarChar";
		}
		s = "Undefined";
		return s;
	}

	public static String getMetaTypeName(String type) {
		String s = "String";
		if ((type.compareToIgnoreCase("Big Integer") == 0)
				|| (type.compareToIgnoreCase("Decimal") == 0)
				|| (type.compareToIgnoreCase("Double") == 0)
				|| (type.compareToIgnoreCase("Float") == 0)
				|| (type.compareToIgnoreCase("Integer") == 0)
				|| (type.compareToIgnoreCase("Numeric") == 0)
				|| (type.compareToIgnoreCase("Real") == 0)
				|| (type.compareToIgnoreCase("Small Integer") == 0)
				|| (type.compareToIgnoreCase("Tiny Integer") == 0)) {
			s = "Number";
		}
		if ((type.compareToIgnoreCase("Date") == 0)
				|| (type.compareToIgnoreCase("Time") == 0)
				|| (type.compareToIgnoreCase("TimeStamp") == 0)) {
			s = "Date";
		}
		return s;
	}
}
