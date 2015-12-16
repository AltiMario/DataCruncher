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

package com.seer.datacruncher.profiler.bl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.framework.dataquality.QualityCheck;
import com.seer.datacruncher.profiler.framework.ndtable.ResultSetToGrid;
import com.seer.datacruncher.profiler.framework.ndtable.ResultsetToRTM;
import com.seer.datacruncher.profiler.framework.profile.TableMetaInfo;
import com.seer.datacruncher.profiler.framework.rdbms.JDBCRowset;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsNewConnection;
import com.seer.datacruncher.profiler.util.DataQualityUtil;
import com.seer.datacruncher.profiler.util.GridUtil;

public class DataQualityBL {

	private Vector<Integer> mrowI;
	private int matchI = -1;
	private boolean commit;
	private List<Object> commitValues;

	public String loadTableNames(Hashtable<String, String> dbParams) {
		String tableNames = null;
		try {
			RdbmsConnection.openConn();
			String dbSchemaPattern = dbParams.get("Database_SchemaPattern");
			String dbTablePattern = dbParams.get("Database_TablePattern");
			String tableType = dbParams.get("Database_TableType");
			String dbCatalog = dbParams.get("Database_Catalog");
			dbCatalog = "";
			RdbmsConnection
					.populateTable(
							dbCatalog.compareTo("") == 0 ? (dbCatalog = null)
									: dbCatalog,
							dbSchemaPattern.compareTo("") == 0 ? (dbSchemaPattern = null)
									: dbSchemaPattern,
							dbTablePattern.compareTo("") == 0 ? (dbTablePattern = null)
									: dbTablePattern, tableType.split(","));
			Vector tables = RdbmsConnection.getTable();
			Vector vector1[] = null;
			vector1 = TableMetaInfo.populateTable(5, 0, 1, vector1);
			RdbmsConnection.closeConn();
			tableNames = DataQualityUtil.generateTableColumnNames(tables, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNames;
	}

	public String loadColumnNames() {
		String columnNames = null;
		try {
			Map<String, String> tableColumns = new HashMap<String, String>();
			Vector tables = RdbmsConnection.getTable();
			for (int ind = 0; ind < tables.size(); ind++) {
				Vector vector[] = null;
				vector = TableMetaInfo.populateTable(5, ind, ind + 1, vector);

				columnNames = DataQualityUtil.generateTableColumnNames(
						vector[0], vector[1]);
				tableColumns.put(tables.get(ind).toString(), columnNames);
			}
			columnNames = DataQualityUtil.generateColumnNames(tableColumns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnNames;
	}

	public String loadColumnNames(Vector tables) {
		String columnNames = null;
		try {
			Map<String, String> tableColumns = new HashMap<String, String>();
			for (int ind = 0; ind < tables.size(); ind++) {
				Vector vector[] = null;
				vector = TableMetaInfo.populateTable(5, ind, ind + 1, vector);

				columnNames = DataQualityUtil.generateTableColumnNames(
						vector[0], vector[1]);
				tableColumns.put(tables.get(ind).toString(), columnNames);
			}
			columnNames = DataQualityUtil.generateColumnNames(tableColumns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnNames;
	}

	public String getDuplicates(String table, Vector columns, String queryString) {
		try {
			QueryBuilder qb = new QueryBuilder(
					RdbmsConnection.getHValue("Database_DSN"), table,
					RdbmsConnection.getDBType());
			String query = qb.get_table_duprow_query(columns, queryString);
			return runQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String runQuery(String query) throws Exception {
		try {
			RdbmsConnection.openConn();
			ResultSet rs = RdbmsConnection.runQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			TableGridDTO tgDTO = ResultSetToGrid.generateTableGrid(rs);
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";
			rs.close();
			RdbmsConnection.closeConn();
			return data;

		} catch (SQLException sqle) {
			return null;
		}

	}

	public String getCaseFormattedGrid(String table, Vector columns, int type,
			String queryString) {
		try {
			return caseFormatAction(table, columns, type, queryString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String caseFormatAction(String table, Vector col, int formatType,
			String queryString) throws SQLException {
		char defChar = '.';
		if (formatType == 4) {
			Locale defLoc = Locale.getDefault();

			if (!defLoc.equals(Locale.US) && !defLoc.equals(Locale.UK)
					&& !defLoc.equals(Locale.CANADA)) {
				// String response = JOptionPane.showInputDialog(null,
				// "Please enter the end of Line Character ?",
				// "Language End Line Input", 3);
				// if (response != null && !"".equals(response))
				// defChar = response.charAt(0);
			}
		}
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_tableAll_query();
		if (!"".equals(queryString))
			query = (new StringBuilder(String.valueOf(query)))
					.append(" WHERE ").append(queryString).toString();
		JDBCRowset rows = new JDBCRowset(query, -1, false);
		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.caseFormat(rows, col.get(0).toString(),
				formatType, defChar);
		this.mrowI = qc.getrowIndex();
		this.matchI = qc.getColMatchIndex();
		if (commit) {
			for (int i = 0; i < this.mrowI.size(); i++) {
				try {
					rows.updateCellVal(
							((Integer) this.mrowI.get(i)).intValue(),
							this.matchI, this.getCommitValues().get(i));
				} catch (Exception exp) {
					System.out.println("\n Error: Update Cell Error:"
							+ exp.getMessage());
				}
			}
			rows.close();
			return "{\"success\": true}";
		} else {
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";

			return data;
		}

	}

	public String repalceNullAction(String table, Vector col,
			String replaceWith, String queryString) throws SQLException {
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_tableAll_query();
		if (!"".equals(queryString))
			query = (new StringBuilder(String.valueOf(query)))
					.append(" WHERE ").append(queryString).toString();
		JDBCRowset rows = new JDBCRowset(query, -1, false);
		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.nullReplace(rows, col.get(0).toString(),
				replaceWith);

		this.mrowI = qc.getrowIndex();
		this.matchI = qc.getColMatchIndex();
		
		if(this.mrowI == null) {
			return null;
		}
		
		if (commit) {
			for (int i = 0; i < this.mrowI.size(); i++) {
				try {
					rows.updateCellVal(
							((Integer) this.mrowI.get(i)).intValue(),
							this.matchI, this.getCommitValues().get(i));
				} catch (Exception exp) {
					System.out.println("\n Error: Update Cell Error:"
							+ exp.getMessage());
				}
			}
			rows.close();
			return "{\"success\": true}";
		} else {
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";
			return data;
		}
	}

	public String matchAction(String table, Vector col, boolean isMatch,
			String queryString, String type, Object pattern[])
			throws SQLException {

		if (pattern.length == 0)
			return null;
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_tableAll_query();
		if (!"".equals(queryString))
			query = (new StringBuilder(String.valueOf(query)))
					.append(" WHERE ").append(queryString).toString();
		JDBCRowset rows = new JDBCRowset(query, -1, false);
		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.patternMatch(rows, col.get(0).toString(), type,
				pattern, isMatch);
		this.mrowI = qc.getrowIndex();
		this.matchI = qc.getColMatchIndex();
		if (commit) {
			for (int i = 0; i < this.mrowI.size(); i++) {
				try {
					rows.updateCellVal(
							((Integer) this.mrowI.get(i)).intValue(),
							this.matchI, this.getCommitValues().get(i));
				} catch (Exception exp) {
					System.out.println("\n Error: Update Cell Error:"
							+ exp.getMessage());
				}
			}
			rows.close();
			return "{\"success\": true}";
		} else {
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";
			return data;
		}

	}

	public String similarAction(String table, Vector col, String queryString, int sType[], int sImp[], String skiptf[])
			throws SQLException {
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_selCol_query(col.toArray(), queryString);
		JDBCRowset rows = new JDBCRowset(query, -1, false);
		// SimilarityCheckPanel sc = new SimilarityCheckPanel(this.rows);
		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.searchTableIndex(rows, sType, sImp, skiptf);
		GridUtil gridUtil = new GridUtil();
		gridUtil.generateGridDataWithDataIndex(tgDTO);

		String data = gridUtil.getData();
		data = "{\"totalCount\":12," + "\"items\":" + data + "}";
		return data;
	}

	public String searchAction(String table, Vector<?> col,
			Hashtable<String, String> filter, String queryString)
			throws SQLException {
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_tableAll_query();
		if (!"".equals(queryString)) {
			query = query + " WHERE " + queryString;
		}
		JDBCRowset rows = new JDBCRowset(query, -1, false);

		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.searchReplace(rows, col.get(0).toString(),
				filter);
		this.mrowI = qc.getrowIndex();
		this.matchI = qc.getColMatchIndex();
		if (commit) {
			for (int i = 0; i < this.mrowI.size(); i++) {
				try {
					rows.updateCellVal(
							((Integer) this.mrowI.get(i)).intValue(),
							this.matchI, this.getCommitValues().get(i));
				} catch (Exception exp) {
					System.out.println("\n Error: Update Cell Error:"
							+ exp.getMessage());
				}
			}
			rows.close();
			return "{\"success\": true}";
		} else {
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";
			return data;
		}

	}

	public String disceetSearchAction(String table, Vector col,
			Vector<String> token, boolean match, String queryString)
			throws SQLException {
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_tableAll_query();
		if (!"".equals(queryString)) {
			query = query + " WHERE " + queryString;
		}
		JDBCRowset rows = new JDBCRowset(query, -1, false);
		QualityCheck qc = new QualityCheck();
		TableGridDTO tgDTO = qc.discreetSearch(rows, col.get(0).toString(),
				token, match);
		this.mrowI = qc.getrowIndex();
		this.matchI = qc.getColMatchIndex();
		if (commit) {
			for (int i = 0; i < this.mrowI.size(); i++) {
				try {
					rows.updateCellVal(
							((Integer) this.mrowI.get(i)).intValue(),
							this.matchI, this.getCommitValues().get(i));
				} catch (Exception exp) {
					System.out.println("\n Error: Update Cell Error:"
							+ exp.getMessage());
				}
			}
			rows.close();
			return "{\"success\": true}";
		} else {
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridDataWithDataIndex(tgDTO);

			String data = gridUtil.getData();
			data = "{\"totalCount\":12," + "\"items\":" + data + "}";
			return data;
		}

	}

	public String incAction(String table, Vector col, boolean isInclusive,
			String queryString) {
		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), table,
				RdbmsConnection.getDBType());
		String query = qb.get_inclusive_query(col, isInclusive);
		if (!"".equals(queryString)) {
			query = query + " AND (" + queryString + ")";
		}

		try {
			JDBCRowset rows = new JDBCRowset(query, -1, false);
			QualityCheck qc = new QualityCheck();
			TableGridDTO tgDTO = qc.createIncExc(rows);
			this.mrowI = qc.getrowIndex();
			this.matchI = qc.getColMatchIndex();
			if (commit) {
				for (int i = 0; i < this.mrowI.size(); i++) {
					try {
						rows.updateCellVal(
								((Integer) this.mrowI.get(i)).intValue(),
								this.matchI, this.getCommitValues().get(i));
					} catch (Exception exp) {
						System.out.println("\n Error: Update Cell Error:"
								+ exp.getMessage());
					}
				}
				rows.close();
				return "{\"success\": true}";
			} else {
				GridUtil gridUtil = new GridUtil();
				gridUtil.generateGridDataWithDataIndex(tgDTO);

				String data = gridUtil.getData();
				data = "{\"totalCount\":12," + "\"items\":" + data + "}";
				return data;
			}
		} catch (SQLException sql_e) {
			sql_e.printStackTrace();
		}
		return null;
	}

	public String compareAction(String lTable, Vector lCol, String rTable,
			Vector rCol, Hashtable<String, String> _dbParam, boolean mr) {

		Vector colName_v = new Vector();
		for (int i = 0; i < rCol.size(); i++) {
			String colName = (String) rCol.get(i);
			if (colName_v.contains(colName)) {
				System.out.println("Duplicate Mapping at Row:");
			}
			colName_v.add(colName);
		}

		QueryBuilder qb = new QueryBuilder(
				RdbmsConnection.getHValue("Database_DSN"), lTable,
				RdbmsConnection.getDBType());
		String s1 = qb.get_selCol_query(lCol.toArray(), "");
		try {

			RdbmsConnection.openConn();
			ResultSet resultset = RdbmsConnection.runQuery(s1);
			Vector hashNumber = ResultsetToRTM.getMD5Value(resultset);
			resultset.close();
			RdbmsConnection.closeConn();

			// int index = this._rTableC.getSelectedIndex();
			String tableName = rTable;
			RdbmsNewConnection newConn = new RdbmsNewConnection(_dbParam);
			qb = new QueryBuilder(newConn.getHValue("Database_DSN"), tableName,
					newConn.getDBType());
			s1 = qb.get_selCol_query(colName_v.toArray(), "");

			if (newConn.openConn()) {
				resultset = newConn.runQuery(s1);
				boolean matval = false;
				if (mr)
					matval = true;
				TableGridDTO tgDTO = matchMD5Value(resultset, hashNumber,
						matval);
				resultset.close();
				newConn.closeConn();
				GridUtil gridUtil = new GridUtil();
				gridUtil.generateGridDataWithDataIndex(tgDTO);

				String data = gridUtil.getData();
				data = "{\"totalCount\":12," + "\"items\":" + data + "}";
				return data;

			}
		} catch (SQLException ee) {
			System.out.println("SQL Exeption in MD5:" + ee.getMessage());
		} catch (Exception ee) {
			System.out.println("Exeption in MD5:" + ee.getMessage());
		} finally {

		}

		return null;
	}

	public synchronized TableGridDTO matchMD5Value(ResultSet rs,
			Vector hashValue, boolean match) throws SQLException {
		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String col_name[] = new String[numberOfColumns];
		int col_type[] = new int[numberOfColumns];
		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnLabel(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}

		tgDTO.setColumnNames(col_name);
		// ReportTableModel rt = new ReportTableModel(col_name, false, true);
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
				if (match && hashValue.contains(number)) {
					rowValues.add(row_v);
				}
				if (!match && !hashValue.contains(number)) {
					rowValues.add(row_v);
				}
			}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
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

	private void commit() {

	}

	public boolean isCommit() {
		return commit;
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

	public List<Object> getCommitValues() {
		return commitValues;
	}

	public void setCommitValues(List<Object> commitValues) {
		this.commitValues = commitValues;
	}

}
