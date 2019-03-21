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

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;

import com.sun.rowset.JdbcRowSetImpl;

public class JDBCRowset {
	private JdbcRowSetImpl rows;
	private String[] col_name;
	private String[] tbl_name;
	private int[] col_type;
	private int rowC = 0;
	private int numberOfColumns = 0;
	private int rowPPage = 100;

	public JDBCRowset(String query, int maxRow, boolean editable)
			throws SQLException {
		try {
			String protocol = RdbmsConnection.getProtocol();
			String url = RdbmsConnection.get_JDBC_URL();
			if ((url == null) || ("".equals(url))) {
				if (RdbmsConnection.getHValue("Database_Type")
						.compareToIgnoreCase("oracle_native") == 0)
					this.rows = new JdbcRowSetImpl(protocol + ":@"
							+ RdbmsConnection.getHValue("Database_DSN"),
							RdbmsConnection.getHValue("Database_User"),
							RdbmsConnection.getHValue("Database_Passwd"));
				else
					this.rows = new JdbcRowSetImpl(protocol + ":"
							+ RdbmsConnection.getHValue("Database_DSN"),
							RdbmsConnection.getHValue("Database_User"),
							RdbmsConnection.getHValue("Database_Passwd"));
			} else
				this.rows = new JdbcRowSetImpl(url,
						RdbmsConnection.getHValue("Database_User"),
						RdbmsConnection.getHValue("Database_Passwd"));

			this.rows.setReadOnly(editable);
			this.rows.setCommand(query);
			if (maxRow > 1) {
				this.rows.setMaxRows(maxRow);
			}
			if (query.indexOf(" ?") != -1) {
				Vector[] dateVar = QueryBuilder.getDateCondition();
				for (int i = 0; i < dateVar[0].size(); i++) {
					String s1 = (String) dateVar[1].get(i);
					if (s1.compareToIgnoreCase("time") == 0)
						this.rows
								.setTime(
										i + 1,
										new Time(((java.util.Date) dateVar[0]
												.get(i)).getTime()));
					if (s1.compareToIgnoreCase("date") == 0)
						this.rows
								.setDate(
										i + 1,
										new java.sql.Date(
												((java.util.Date) dateVar[0]
														.get(i)).getTime()));
					if (s1.compareToIgnoreCase("timestamp") == 0)
						this.rows
								.setTimestamp(
										i + 1,
										new Timestamp(
												((java.util.Date) dateVar[0]
														.get(i)).getTime()));
				}
			}
			this.rows.execute();
			if (this.rows.last()) {
				this.rowC = this.rows.getRow();
			}
			createMD();
		} catch (SQLException e) {
			throw e;
		}
		this.rows.setAutoCommit(true);
	}

	public JDBCRowset(String query, boolean editable, Vector<Integer> vc_t,
			Vector<Object> vc_v) throws SQLException {
		try {
			String protocol = RdbmsConnection.getProtocol();
			String url = RdbmsConnection.get_JDBC_URL();
			if ((url == null) || ("".equals(url))) {
				if (RdbmsConnection.getHValue("Database_Type")
						.compareToIgnoreCase("oracle_native") == 0)
					this.rows = new JdbcRowSetImpl(protocol + ":@"
							+ RdbmsConnection.getHValue("Database_DSN"),
							RdbmsConnection.getHValue("Database_User"),
							RdbmsConnection.getHValue("Database_Passwd"));
				else
					this.rows = new JdbcRowSetImpl(protocol + ":"
							+ RdbmsConnection.getHValue("Database_DSN"),
							RdbmsConnection.getHValue("Database_User"),
							RdbmsConnection.getHValue("Database_Passwd"));
			} else
				this.rows = new JdbcRowSetImpl(url,
						RdbmsConnection.getHValue("Database_User"),
						RdbmsConnection.getHValue("Database_Passwd"));

			this.rows.setReadOnly(editable);
			this.rows.setCommand(query);

			int fromIndex = 0;
			for (int i = 0; i < vc_t.size(); i++) {
				fromIndex = query.indexOf(" ?", fromIndex);
				if (fromIndex == -1)
					return;
				fromIndex += 2;
				setQuery(i, ((Integer) vc_t.get(i)).intValue(), vc_v.get(i));
			}

			if (query.indexOf(" ?", fromIndex) != -1) {
				Vector[] dateVar = QueryBuilder.getDateCondition();
				for (int i = vc_t.size(); i < vc_t.size() + dateVar[0].size(); i++) {
					String s1 = (String) dateVar[1].get(i - vc_t.size());
					if (s1.compareToIgnoreCase("time") == 0)
						this.rows.setTime(
								i + 1,
								new Time(((java.util.Date) dateVar[0].get(i
										- vc_t.size())).getTime()));
					if (s1.compareToIgnoreCase("date") == 0)
						this.rows.setDate(
								i + 1,
								new java.sql.Date(((java.util.Date) dateVar[0]
										.get(i - vc_t.size())).getTime()));
					if (s1.compareToIgnoreCase("timestamp") == 0) {
						this.rows.setTimestamp(
								i + 1,
								new Timestamp(((java.util.Date) dateVar[0]
										.get(i - vc_t.size())).getTime()));
					}
				}
			}
			this.rows.execute();
			if (this.rows.last())
				this.rowC = this.rows.getRow();
		} catch (SQLException e) {
			throw e;
		}
		this.rows.setAutoCommit(true);
	}

	public Vector<Object>[] getRowCol(int fromIndex, int toIndex,
			Vector<Object>[] row_v) throws SQLException {
		this.rows.absolute(fromIndex);
		this.rows.previous();
		int counter = 0;

		if (toIndex >= fromIndex) {
			row_v = new Vector[toIndex - fromIndex + 1];
		}
		while ((this.rows.next()) && (toIndex >= fromIndex + counter)) {
			row_v[counter] = new Vector();
			for (int i = 1; i < this.col_name.length + 1; i++) {
				switch (this.col_type[(i - 1)]) {
				case -6:
				case 4:
				case 5:
					row_v[counter].add(i - 1, new Integer(this.rows.getInt(i)));
					break;
				case -5:
				case 2:
				case 3:
				case 7:
				case 8:
					row_v[counter].add(i - 1,
							new Double(this.rows.getDouble(i)));
					break;
				case 6:
					row_v[counter].add(i - 1, new Float(this.rows.getFloat(i)));
					break;
				case 2005:
					row_v[counter].add(i - 1, this.rows.getClob(i));
					break;
				case 2004:
					row_v[counter].add(i - 1, this.rows.getBlob(i));
					break;
				case -7:
				case 16:
					row_v[counter].add(i - 1,
							new Boolean(this.rows.getBoolean(i)));
					break;
				case 91:
					row_v[counter].add(i - 1, this.rows.getDate(i));
					break;
				case 92:
					row_v[counter].add(i - 1, this.rows.getTime(i));
					break;
				case 93:
					row_v[counter].add(i - 1, this.rows.getTimestamp(i));
					break;
				case 2003:
					row_v[counter].add(i - 1, this.rows.getArray(i));
					break;
				case 2006:
					row_v[counter].add(i - 1, this.rows.getRef(i));
					break;
				case -2:
					row_v[counter].add(i - 1,
							Byte.valueOf(this.rows.getByte(i)));
					break;
				case -4:
				case -3:
					row_v[counter].add(i - 1, this.rows.getBytes(i));
					break;
				case 0:
				case 70:
				case 1111:
				case 2000:
				case 2001:
				case 2002:
					row_v[counter].add(i - 1, this.rows.getObject(i));
					break;
				default:
					row_v[counter].add(i - 1, this.rows.getString(i));
				}
			}
			counter++;
		}
		return row_v;
	}

	public boolean updateCellVal(int row, int col, Object obj)
			throws SQLException {
		this.rows.absolute(row);
		boolean update = updateCell(col, obj);
		this.rows.updateRow();
		this.rows.refreshRow();
		return update;
	}

	public boolean updateCell(int i, Object o) throws SQLException {
		boolean changeToNull = false;
		try {
			switch (this.col_type[i]) {
			case -6:
			case 4:
			case 5:
				this.rows.updateInt(i + 1, ((Number) o).intValue());
				break;
			case -5:
			case 2:
			case 3:
			case 7:
			case 8:
				this.rows.updateDouble(i + 1, ((Number) o).doubleValue());
				break;
			case 6:
				this.rows.updateFloat(i + 1, ((Number) o).floatValue());
				break;
			case 2005:
				this.rows.updateClob(i + 1, (Clob) o);
				break;
			case 2004:
				this.rows.updateBlob(i + 1, (Blob) o);
				break;
			case -7:
			case 16:
				this.rows.updateBoolean(i + 1, ((Boolean) o).booleanValue());
				break;
			case 91:
				this.rows.updateDate(i + 1, new java.sql.Date(
						((java.util.Date) o).getTime()));
				break;
			case 92:
				this.rows.updateTime(i + 1,
						new Time(((java.util.Date) o).getTime()));
				break;
			case 93:
				this.rows.updateTimestamp(i + 1, new Timestamp(
						((java.util.Date) o).getTime()));
				break;
			case 2003:
				this.rows.updateArray(i + 1, (Array) o);
				break;
			case 2006:
				this.rows.updateRef(i + 1, (Ref) o);
				break;
			case -4:
			case -3:
			case -2:
			case 0:
			case 70:
			case 1111:
			case 2000:
			case 2001:
			case 2002:
				this.rows.updateObject(i + 1, o);
				break;
			default:
				if ("".equals(o.toString())) {
					this.rows.updateString(i + 1, null);
					changeToNull = true;
				} else {
					this.rows.updateString(i + 1, o.toString());
				}
				break;
			}
		} catch (ClassCastException e) {
			try {
				this.rows.updateString(i + 1, o.toString());
			} catch (Exception exp) {
				this.rows.updateObject(i + 1, null);
				System.out.println("\n Update Casting exception");
			}
		} catch (NullPointerException e) {
			this.rows.updateObject(i + 1, o);
		}
		return changeToNull;
	}

	private void setQuery(int i, int type, Object o) throws SQLException {
		try {
			switch (type) {
			case -6:
			case 4:
			case 5:
				this.rows.setInt(i + 1, ((Number) o).intValue());
				break;
			case -5:
			case 2:
			case 3:
			case 7:
			case 8:
				this.rows.setDouble(i + 1, ((Number) o).doubleValue());
				break;
			case 6:
				this.rows.setFloat(i + 1, ((Number) o).floatValue());
				break;
			case 2005:
				this.rows.setClob(i + 1, (Clob) o);
				break;
			case 2004:
				this.rows.setBlob(i + 1, (Blob) o);
				break;
			case -7:
			case 16:
				this.rows.setBoolean(i + 1, ((Boolean) o).booleanValue());
				break;
			case 91:
				this.rows.setDate(i + 1,
						new java.sql.Date(((java.util.Date) o).getTime()));
				break;
			case 92:
				this.rows.setTime(i + 1,
						new Time(((java.util.Date) o).getTime()));
				break;
			case 93:
				this.rows.setTimestamp(i + 1, new Timestamp(
						((java.util.Date) o).getTime()));
				break;
			case 2003:
				this.rows.setArray(i + 1, (Array) o);
				break;
			case 2006:
				this.rows.setRef(i + 1, (Ref) o);
				break;
			case -4:
			case -3:
			case -2:
			case 0:
			case 70:
			case 1111:
			case 2000:
			case 2001:
			case 2002:
				this.rows.setObject(i + 1, o);
				break;
			default:
				this.rows.setString(i + 1, o.toString());
			}
		} catch (ClassCastException e) {
			try {
				this.rows.setString(i + 1, o.toString());
			} catch (Exception exp) {
				this.rows.setObject(i + 1, null);
				System.out.println("\n Set Casting exception");
			}
		} catch (NullPointerException e) {
			this.rows.setObject(i + 1, o);
		}
	}

	public int pageCount() {
		if (this.rowC == 0)
			return 1;
		if (this.rowC % this.rowPPage == 0) {
			return this.rowC / this.rowPPage;
		}
		return this.rowC / this.rowPPage + 1;
	}

	public void close() {
		try {
			if (this.rows != null)
				this.rows.close();
		} catch (SQLException e) {
			System.out.println("\n Message:" + e.getMessage());
			System.out
					.println("\n WARNING: Rowset Connection can not be closed.");
		}
	}

	private void createMD() throws SQLException {
		ResultSetMetaData rsmd = this.rows.getMetaData();
		this.numberOfColumns = rsmd.getColumnCount();
		this.col_name = new String[this.numberOfColumns];
		this.tbl_name = new String[this.numberOfColumns];
		this.col_type = new int[this.numberOfColumns];

		for (int i = 1; i < this.numberOfColumns + 1; i++) {
			this.col_name[(i - 1)] = rsmd.getColumnName(i);
			this.tbl_name[(i - 1)] = rsmd.getTableName(i);
			this.col_type[(i - 1)] = rsmd.getColumnType(i);
		}
	}

	public String[] getColName() {
		return this.col_name;
	}

	public String[] getColType() {
		String[] colType = new String[this.col_type.length];
		for (int i = 0; i < this.col_type.length; i++) {
			colType[i] = SqlType.getTypeName(this.col_type[i]);
		}
		return colType;
	}

	public String[] getTableName() {
		return this.tbl_name;
	}

	public void moveToFirst() throws SQLException {
		this.rows.first();
	}

	public synchronized void insertRow(Object[] obj) throws SQLException {
		this.rows.moveToInsertRow();
		for (int j = 0; j < obj.length; j++)
			updateCell(j, obj[j]);
		this.rows.insertRow();
		this.rows.moveToCurrentRow();
	}

	public synchronized Object getObject(int row, int col) throws SQLException {
		if (row > this.rowC)
			return null;
		this.rows.absolute(row);
		Object obj = this.rows.getObject(col);
		if (obj == null) {
			try {
				return this.rows.getString(col);
			} catch (Exception e) {
				return null;
			}
		}
		return obj;
	}

	public int getRowCount() {
		return this.rowC;
	}

	public synchronized Object[] getRow(int rowId) throws SQLException {
		Object[] objA = new Object[this.numberOfColumns];
		for (int i = 0; i < this.numberOfColumns; i++) {
			objA[i] = getObject(rowId, i + 1);
		}
		return objA;
	}

	public synchronized void deleteRow(int rowId) {
		try {
			this.rows.absolute(rowId);
			this.rows.deleteRow();
		} catch (Exception e) {
			System.out.println("\n RowSet Delete Error for RowID:" + rowId);
			System.out.println("\n Error Message:" + e.getMessage());
		}
	}

	public JdbcRowSetImpl getRowset() {
		return this.rows;
	}

	public synchronized void setRowset(JdbcRowSetImpl rowset) {
		this.rows = rowset;
	}

	public void setrowPPage(int rows) {
		this.rowPPage = rows;
	}

	public int getrowPPage() {
		return this.rowPPage;
	}
}
