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

package com.seer.datacruncher.profiler.framework.rdbms;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class RdbmsConnection {

	private static Connection conn;
	private static String _d_type = "";
	private static String _d_dsn = "";
	private static String __d_protocol = "";
	private static String _d_user = "";
	private static String _d_passwd = "";
	private static String _d_driver = "";
	private static String _d_url = "";
	private static Hashtable _h;
	private static Vector table_v;
	private static Vector tableDesc_v;

	public RdbmsConnection() {
	}

	public static void openConn() throws SQLException {
		if (conn != null && !conn.isClosed())
			return;
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
			System.exit(0);
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
		} catch (SQLException exception) {
			System.out.println("\n ERROR:Connection can not be created");
			System.out.println(exception.getMessage());
			throw exception;
		}
	}

	public static DatabaseMetaData getMetaData() throws SQLException {
		DatabaseMetaData databasemetadata = null;
		try {
			databasemetadata = conn.getMetaData();
		} catch (SQLException sqlexception) {
			System.out.println("\n ERROR: MetaData SQL Exception");
			System.out.println(sqlexception.getMessage());
		}
		return databasemetadata;
	}

	public static void closeConn() throws SQLException {
		if (_d_type.compareToIgnoreCase("oracle_native") == 0) {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		} else if (conn != null && !conn.isClosed())
			conn.close();
	}

	public static void exitConn() throws SQLException {
		if (conn != null && !conn.isClosed())
			conn.close();
	}

	public static PreparedStatement createQuery(String s) throws SQLException {
		PreparedStatement preparedstatement = conn.prepareStatement(s);
		return preparedstatement;
	}

	public static ResultSet executeQuery(PreparedStatement preparedstatement)
			throws SQLException {
		ResultSet resultset = preparedstatement.executeQuery();
		return resultset;
	}

	public static ResultSet runQuery(String s) throws SQLException {
		if (conn == null || conn.isClosed())
			return null;
		if (s == null || "".equals(s))
			return null;
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

	public static ResultSet runQuery(String s, int i) throws SQLException {
		if (conn == null || conn.isClosed())
			return null;
		if (s == null || "".equals(s))
			return null;
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

	public static int executeUpdate(String s) throws SQLException {
		Statement statement;
		if (_d_type.compareToIgnoreCase("ms_access") == 0
				|| _d_type.compareToIgnoreCase("oracle_odbc") == 0)
			statement = conn.createStatement();
		else
			statement = conn.createStatement(1004, 1007);
		return statement.executeUpdate(s);
	}

	public static ResultSet execute(String s) throws SQLException {
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

	public static void init(Hashtable hashtable) throws SQLException {
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

	public static String getDBType() {
		return _d_type;
	}

	public static String getHValue(String s) {
		return (String) _h.get(s);
	}

	public static String setHValue(String key, String value) {
		return (String) _h.put(key, value);
	}

	public static String checkAndReturnSql(String s) throws SQLException {
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

	public static void populateTable(String cat, String schema,
			String tableNamePattern, String types[]) throws SQLException {
		if (table_v.isEmpty()) {
			openConn();
			DatabaseMetaData databasemetadata = getMetaData();
			ResultSet resultset;
			String s7;
			for (resultset = databasemetadata.getTables(cat, schema,
					tableNamePattern, types); resultset.next(); addTableDesc(s7)) {
				String s5 = resultset.getString(3);
				addTable(s5);
				s7 = resultset.getString(5);
			}

			resultset.close();
			closeConn();
		}
	}

	public static void addTable(String s) {
		if (s == null) {
			return;
		} else {
			table_v.add(table_v.size(), s);
			return;
		}
	}

	public static void addTableDesc(String s) {
		if (s == null)
			tableDesc_v.add(tableDesc_v.size(), "");
		else
			tableDesc_v.add(tableDesc_v.size(), s);
	}

	public static Vector getTable() {
		return table_v;
	}

	public static int getTableCount() {
		return table_v.size();
	}

	public static Vector getTableDesc() {
		return tableDesc_v;
	}

	public static String getProtocol() {
		return __d_protocol;
	}

	public static String getUser() {
		return _d_user;
	}

	public static String testConn() throws SQLException {
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

	public static String get_JDBC_URL() {
		return _d_url;
	}

	public static void set_JDBC_URL(String _d_url) {
		_d_url = _d_url;
	}

}
