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

import java.sql.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


public class RdbmsNewConnection {

	private Connection conn;
	private String _d_type;
	private String _d_dsn;
	private String __d_protocol;
	private String _d_user;
	private String _d_passwd;
	private String _d_driver;
	private String _d_url;
	private Hashtable _h;
	private Vector table_v;
	private Vector tableDesc_v;

	public RdbmsNewConnection(Hashtable hashtable) throws SQLException {
		_d_type = "";
		_d_dsn = "";
		__d_protocol = "";
		_d_user = "";
		_d_passwd = "";
		_d_driver = "";
		_d_url = "";
		init(hashtable);
	}

	public boolean openConn() throws SQLException {
		if (conn != null && !conn.isClosed())
			return true;
		if (_d_driver == null || _d_driver.equals("")) {
			System.out
					.println("Driver Value Not Found - Check DB Driver field");
			System.out.println("\n ERROR: Driver Value Not Found");
			System.exit(0);
		}
		try {
			Class.forName(_d_driver);
		} catch (ClassNotFoundException classnotfoundexception) {
			System.out.println("Driver Class Not Found.");
			System.out
					.println("Look into System DSN if using jdbc:odbc bridge");
			System.out
					.println("Make sure Driver class is in classpath if using native");
			System.out.println((new StringBuilder("Driver Class Not Found:"))
					.append(classnotfoundexception.getMessage()).toString());
			return false;
		}
		try {
			String s = _d_url;
			if (s == null || "".equals(s)) {
				if (_d_type.compareToIgnoreCase("oracle_native") == 0)
					conn = DriverManager.getConnection(
							(new StringBuilder(String.valueOf(__d_protocol)))
									.append(":@").append(_d_dsn).toString(),
							_d_user, _d_passwd);
				else
					conn = DriverManager.getConnection(
							(new StringBuilder(String.valueOf(__d_protocol)))
									.append(":").append(_d_dsn).toString(),
							_d_user, _d_passwd);
			} else {
				conn = DriverManager.getConnection(_d_url, _d_user, _d_passwd);
			}
		} catch (Exception exception) {
			System.out.println("\n ERROR:Connection can not be created");
			System.out.println(exception.getMessage());
			return false;
		}
		return true;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		DatabaseMetaData databasemetadata = null;
		if (conn == null || conn.isClosed())
			return null;
		try {
			databasemetadata = conn.getMetaData();
		} catch (SQLException sqlexception) {
			System.out.println("\n ERROR: MetaData SQL Exception");
			System.out.println(sqlexception.getMessage());
		}
		return databasemetadata;
	}

	public void closeConn() throws SQLException {
		if (_d_type.compareToIgnoreCase("oracle_native") == 0 && conn != null
				&& !conn.isClosed()) {
			conn.close();
			conn = null;
		}
	}

	public void exitConn() throws SQLException {
		if (conn != null && !conn.isClosed())
			conn.close();
	}

	public PreparedStatement createQuery(String s) throws SQLException {
		if (conn == null || conn.isClosed()) {
			return null;
		} else {
			PreparedStatement preparedstatement = conn.prepareStatement(s);
			return preparedstatement;
		}
	}

	public ResultSet executeQuery(PreparedStatement preparedstatement)
			throws SQLException {
		ResultSet resultset = preparedstatement.executeQuery();
		return resultset;
	}

	public ResultSet runQuery(String s) throws SQLException {
		if (s.indexOf(" ?") == -1) {
			Statement statement;
			if (_d_type.compareToIgnoreCase("ms_access") == 0
					|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
				statement = conn.createStatement();
			else
				statement = conn.createStatement(1004, 1007);
			ResultSet resultset = statement.executeQuery(s);
			return resultset;
		}
		PreparedStatement preparedstatement = conn.prepareStatement(s);
		Vector dateVar[] = QueryBuilder.getDateCondition();
		for (int i = 0; i < dateVar[0].size(); i++) {
			String s1 = (String) dateVar[1].get(i);
			if (s1.compareToIgnoreCase("time") == 0)
				preparedstatement.setTime(i + 1,
						new Time(((Date) dateVar[0].get(i)).getTime()));
			if (s1.compareToIgnoreCase("date") == 0)
				preparedstatement.setDate(i + 1, new java.sql.Date(
						((Date) dateVar[0].get(i)).getTime()));
			if (s1.compareToIgnoreCase("timestamp") == 0)
				preparedstatement.setTimestamp(i + 1, new Timestamp(
						((Date) dateVar[0].get(i)).getTime()));
		}

		ResultSet resultset = preparedstatement.executeQuery();
		return resultset;
	}

	public ResultSet runQuery(String s, int i) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		statement.setMaxRows(i);
		ResultSet resultset = statement.executeQuery(s);
		return resultset;
	}

	public int executeUpdate(String s) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		return statement.executeUpdate(s);
	}

	public ResultSet execute(String s) throws SQLException {
		Statement statement = null;
		boolean flag = false;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		flag = statement.execute(s);
		if (flag)
			return statement.getResultSet();
		else
			return null;
	}

	private void init(Hashtable hashtable) throws SQLException {
		_d_type = (String) hashtable.get("Database_Type");
		_d_dsn = (String) hashtable.get("Database_DSN");
		__d_protocol = (String) hashtable.get("Database_Protocol");
		_d_driver = (String) hashtable.get("Database_Driver");
		_d_user = (String) hashtable.get("Database_User");
		_d_passwd = (String) hashtable.get("Database_Passwd");
		_d_url = (String) hashtable.get("Database_JDBC");
		_h = hashtable;
		table_v = new Vector();
		tableDesc_v = new Vector();
		exitConn();
	}

	public String getDBType() {
		return _d_type;
	}

	public String getHValue(String s) {
		return (String) _h.get(s);
	}

	public String setHValue(String key, String value) {
		return (String) _h.put(key, value);
	}

	public String checkAndReturnSql(String s) throws SQLException {
		Statement statement = null;
		conn.setAutoCommit(false);
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		statement.execute(s);
		conn.rollback();
		conn.setAutoCommit(true);
		return conn.nativeSQL(s);
	}

	public void populateTable() throws SQLException {
		String s = (String) _h.get("Database_SchemaPattern");
		String s1 = (String) _h.get("Database_TablePattern");
		String s2 = (String) _h.get("Database_TableType");
		String s3 = (String) _h.get("Database_Catalog");
		s3 = "";
		if (s == null || "".equals(s))
			s = null;
		if (s3 == null || "".equals(s3))
			s3 = null;
		if (s1 == null || "".equals(s1))
			s1 = null;
		if (s2 == null || "".equals(s2))
			s2 = "TABLE";
		if (openConn()) {
			DatabaseMetaData databasemetadata = getMetaData();
			ResultSet resultset;
			String s7;
			for (resultset = databasemetadata.getTables(s3, s, s1,
					s2.split(",")); resultset.next(); addTableDesc(s7)) {
				String s5 = resultset.getString(3);
				addTable(s5);
				s7 = resultset.getString(5);
			}

			resultset.close();
			closeConn();
		} else {
			System.out.println("Table Can not be populated");
		}
	}

	public Vector[] populateColumn(String tableName, String colPattern)
			throws SQLException {
		Vector avector[] = new Vector[2];
		avector[0] = new Vector();
		avector[1] = new Vector();
		String s = (String) _h.get("Database_SchemaPattern");
		String s1 = (String) _h.get("Database_TablePattern");
		String s3 = (String) _h.get("Database_Catalog");
		s3 = "";
		if (s == null || "".equals(s))
			s = null;
		if (s3 == null || "".equals(s3))
			s3 = null;
		if (s1 == null || "".equals(s1))
			s1 = null;
		if (openConn()) {
			DatabaseMetaData databasemetadata = getMetaData();
			ResultSet resultset = databasemetadata.getColumns(s3, s, tableName,
					null);
			int k = 0;
			while (resultset.next()) {
				String s31 = resultset.getString(3);
				if (s31.equals(tableName)) {
					String s4 = resultset.getString(4);
					int i1 = resultset.getInt(5);
					avector[0].add(k, s4);
					avector[1].add(k, new Integer(i1));
					k++;
				}
			}
			resultset.close();
			closeConn();
		} else {
			System.out.println("Column can not be populated");
		}
		return avector;
	}

	public void addTable(String s) {
		if (s == null) {
			return;
		} else {
			table_v.add(table_v.size(), s);
			return;
		}
	}

	public void addTableDesc(String s) {
		if (s == null)
			tableDesc_v.add(tableDesc_v.size(), "");
		else
			tableDesc_v.add(tableDesc_v.size(), s);
	}

	public Vector getTable() {
		return table_v;
	}

	public int getTableCount() {
		return table_v.size();
	}

	public Vector getTableDesc() {
		return tableDesc_v;
	}

	public String getProtocol() {
		return __d_protocol;
	}

	public String getUser() {
		return _d_user;
	}

	public String get_JDBC_URL() {
		return _d_url;
	}

	public void set_JDBC_URL(String url) {
		_d_url = url;
	}

	public String testConn() throws SQLException {
		String status = " Connection Failed. \n\n";
		exitConn();
		if (_d_driver == null || _d_driver.equals(""))
			return status = (new StringBuilder(String.valueOf(status))).append(
					"Driver Value Not Found - Check DB Driver field")
					.toString();
		try {
			Class.forName(_d_driver);
		} catch (ClassNotFoundException classnotfoundexception) {
			status = (new StringBuilder(String.valueOf(status))).append(
					"Driver Class Not Found. \n").toString();
			status = (new StringBuilder(String.valueOf(status))).append(
					"Look into System DSN if using jdbc:odbc bridge \n")
					.toString();
			status = (new StringBuilder(String.valueOf(status)))
					.append("Make sure Driver class is in classpath if using native \n")
					.toString();
			status = (new StringBuilder(String.valueOf(status)))
					.append("\n System Message:")
					.append(classnotfoundexception.getMessage()).toString();
			return status;
		}
		try {
			String s = _d_url;
			if (s == null || "".equals(s)) {
				if (_d_type.compareToIgnoreCase("oracle_native") == 0)
					conn = DriverManager.getConnection(
							(new StringBuilder(String.valueOf(__d_protocol)))
									.append(":@").append(_d_dsn).toString(),
							_d_user, _d_passwd);
				else
					conn = DriverManager.getConnection(
							(new StringBuilder(String.valueOf(__d_protocol)))
									.append(":").append(_d_dsn).toString(),
							_d_user, _d_passwd);
			} else {
				conn = DriverManager.getConnection(_d_url, _d_user, _d_passwd);
			}
		} catch (Exception exception) {
			status = (new StringBuilder(String.valueOf(status))).append(
					"\n System Message:").toString();
			return status = (new StringBuilder(String.valueOf(status))).append(
					exception.getMessage()).toString();
		}
		return status = "Connection Successful";
	}
}
