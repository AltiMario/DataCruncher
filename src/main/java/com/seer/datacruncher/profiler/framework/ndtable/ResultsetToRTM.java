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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.TablePrivilegePropertyDTO;

public class ResultsetToRTM {

	public ResultsetToRTM() {
	}

	public static ReportTableModel getSQLValue(ResultSet rs, boolean format)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String col_name[] = new String[numberOfColumns];
		int col_type[] = new int[numberOfColumns];
		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}

		ReportTableModel rt;
		if (format)
			rt = new ReportTableModel(col_name, false, true);
		else
			rt = new ReportTableModel(col_name, false, false);
		Vector row_v;
		for (; rs.next(); rt.addFillRow(row_v)) {
			row_v = new Vector();
			for (int i = 1; i < numberOfColumns + 1; i++)
				switch (col_type[i - 1]) {
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

		}

		return rt;
	}

	public static synchronized Vector getMD5Value(ResultSet rs)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String col_name[] = new String[numberOfColumns];
		int col_type[] = new int[numberOfColumns];
		Vector row_v = new Vector();
		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}

		while (rs.next()) {
			String rowString = "";
			for (int i = 1; i < numberOfColumns + 1; i++)
				switch (col_type[i - 1]) {
				case -6:
				case 4: // '\004'
				case 5: // '\005'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append((new Integer(rs.getInt(i))).toString())
							.toString();
					break;

				case -5:
				case 2: // '\002'
				case 3: // '\003'
				case 7: // '\007'
				case 8: // '\b'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append((new Double(rs.getDouble(i))).toString())
							.toString();
					break;

				case 6: // '\006'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append((new Float(rs.getFloat(i))).toString())
							.toString();
					break;

				case 2005:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getClob(i).toString()).toString();
					break;

				case 2004:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getBlob(i).toString()).toString();
					break;

				case -7:
				case 16: // '\020'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append((new Boolean(rs.getBoolean(i))).toString())
							.toString();
					break;

				case 91: // '['
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getDate(i).toString()).toString();
					break;

				case 92: // '\\'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getTime(i).toString()).toString();
					break;

				case 93: // ']'
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getTimestamp(i).toString()).toString();
					break;

				case 2003:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getArray(i).toString()).toString();
					break;

				case 2006:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getRef(i).toString()).toString();
					break;

				case -2:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getByte(i)).toString();
					break;

				case -4:
				case -3:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getBytes(i).toString()).toString();
					break;

				case 0: // '\0'
				case 70: // 'F'
				case 1111:
				case 2000:
				case 2001:
				case 2002:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getObject(i).toString()).toString();
					break;

				default:
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(rs.getString(i)).toString();
					break;
				}

			BigInteger number = getMD5(rowString);
			if (number != null)
				row_v.add(number);
		}
		return row_v;
	}

	public static BigInteger getMD5(String input) {
		BigInteger number = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte messageDigest[] = md.digest(input.getBytes());
			number = new BigInteger(1, messageDigest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return number;
	}

	public static synchronized ReportTableModel matchMD5Value(ResultSet rs,
			Vector hashValue, boolean match) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String col_name[] = new String[numberOfColumns];
		int col_type[] = new int[numberOfColumns];
		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}

		ReportTableModel rt = new ReportTableModel(col_name, false, true);
		while (rs.next()) {
			String rowString = "";
			Vector row_v = new Vector();
			for (int i = 1; i < numberOfColumns + 1; i++)
				switch (col_type[i - 1]) {
				case -6:
				case 4: // '\004'
				case 5: // '\005'
					Integer intval = new Integer(rs.getInt(i));
					row_v.add(i - 1, intval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(intval.toString()).toString();
					break;

				case -5:
				case 2: // '\002'
				case 3: // '\003'
				case 7: // '\007'
				case 8: // '\b'
					Double dobval = new Double(rs.getDouble(i));
					row_v.add(i - 1, dobval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(dobval.toString()).toString();
					break;

				case 6: // '\006'
					Float floval = new Float(rs.getFloat(i));
					row_v.add(i - 1, floval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(floval.toString()).toString();
					break;

				case 2005:
					java.sql.Clob cloval = rs.getClob(i);
					row_v.add(i - 1, cloval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(cloval.toString()).toString();
					break;

				case 2004:
					java.sql.Blob bloval = rs.getBlob(i);
					row_v.add(i - 1, bloval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(bloval.toString()).toString();
					break;

				case -7:
				case 16: // '\020'
					Boolean booval = new Boolean(rs.getBoolean(i));
					row_v.add(i - 1, booval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(booval.toString()).toString();
					break;

				case 91: // '['
					Date datval = rs.getDate(i);
					row_v.add(i - 1, datval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(datval.toString()).toString();
					break;

				case 92: // '\\'
					Time timval = rs.getTime(i);
					row_v.add(i - 1, timval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(timval.toString()).toString();
					break;

				case 93: // ']'
					Timestamp tstval = rs.getTimestamp(i);
					row_v.add(i - 1, tstval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(tstval.toString()).toString();
					break;

				case 2003:
					java.sql.Array araval = rs.getArray(i);
					row_v.add(i - 1, araval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(araval.toString()).toString();
					break;

				case 2006:
					java.sql.Ref refval = rs.getRef(i);
					row_v.add(i - 1, refval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(refval.toString()).toString();
					break;

				case -2:
					byte bytval = rs.getByte(i);
					row_v.add(i - 1, Byte.valueOf(bytval));
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(bytval).toString();
					break;

				case -4:
				case -3:
					byte btsval[] = rs.getBytes(i);
					row_v.add(i - 1, btsval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(btsval.toString()).toString();
					break;

				case 0: // '\0'
				case 70: // 'F'
				case 1111:
				case 2000:
				case 2001:
				case 2002:
					Object objval = rs.getObject(i);
					row_v.add(i - 1, objval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(objval.toString()).toString();
					break;

				default:
					String strval = rs.getString(i);
					row_v.add(i - 1, strval);
					rowString = (new StringBuilder(String.valueOf(rowString)))
							.append(strval).toString();
					break;
				}

			BigInteger number = getMD5(rowString);
			if (number != null) {
				if (match && hashValue.contains(number))
					rt.addFillRow(row_v);
				if (!match && !hashValue.contains(number))
					rt.addFillRow(row_v);
			}
		}
		return rt;
	}
}
