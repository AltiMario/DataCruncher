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

package com.seer.datacruncher.profiler.framework.ndtable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.seer.datacruncher.profiler.dto.TableGridDTO;

public class ResultSetToGrid {
	public static TableGridDTO generateTableGrid(ResultSet rs) throws Exception {
		TableGridDTO tbDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String columnNames[] = new String[numberOfColumns];
		int columnTypes[] = new int[numberOfColumns];
		for (int i = 1; i < numberOfColumns + 1; i++) {
			columnNames[i - 1] = rsmd.getColumnLabel(i);
			columnTypes[i - 1] = rsmd.getColumnType(i);
		}
		tbDTO.setColumnNames(columnNames);
		Vector<Object> row_v = null;
		while (rs.next()) {
			row_v = new Vector<Object>();
			for (int i = 1; i < numberOfColumns + 1; i++)
				switch (columnTypes[i - 1]) {
				case -6:
				case 4: // '\004'
				case 5: // '\005'
					row_v.add(i - 1, new Integer(rs.getInt(i)));
					break;

				case -5:
				case 2: // '\002'
				case 3: // '\003'
				case 7: // '\007'
				case 8: // '\b'
					row_v.add(i - 1, new Double(rs.getDouble(i)));
					break;

				case 6: // '\006'
					row_v.add(i - 1, new Float(rs.getFloat(i)));
					break;

				case 2005:
					row_v.add(i - 1, rs.getClob(i));
					break;

				case 2004:
					row_v.add(i - 1, rs.getBlob(i));
					break;

				case -7:
				case 16: // '\020'
					row_v.add(i - 1, new Boolean(rs.getBoolean(i)));
					break;

				case 91: // '['
					row_v.add(i - 1, rs.getDate(i));
					break;

				case 92: // '\\'
					row_v.add(i - 1, rs.getTime(i));
					break;

				case 93: // ']'
					row_v.add(i - 1, rs.getTimestamp(i));
					break;

				case 2003:
					row_v.add(i - 1, rs.getArray(i));
					break;

				case 2006:
					row_v.add(i - 1, rs.getRef(i));
					break;

				case -2:
					row_v.add(i - 1, Byte.valueOf(rs.getByte(i)));
					break;

				case -4:
				case -3:
					row_v.add(i - 1, rs.getBytes(i));
					break;

				case 0: // '\0'
				case 70: // 'F'
				case 1111:
				case 2000:
				case 2001:
				case 2002:
					row_v.add(i - 1, rs.getObject(i));
					break;

				default:
					row_v.add(i - 1, rs.getString(i));
					break;
				}
			rowValues.add(row_v);
		}
		tbDTO.setRowValues(rowValues);
		return tbDTO;
	}
}
