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

package com.seer.datacruncher.profiler.framework.profile;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.seer.datacruncher.profiler.framework.ndtable.ReportTableModel;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.framework.rdbms.TableRelationInfo;

public class TableMetaInfo {
	private static DatabaseMetaData dbmd;

	public static ReportTableModel populateTable(int i, int j, int k,
			ReportTableModel reporttable) {
		try {
			RdbmsConnection.openConn();
			dbmd = RdbmsConnection.getMetaData();
			switch (i) {
			case 1:
				reporttable = IndexQuery(j, k, reporttable);
				break;
			case 2:
				reporttable = MetaDataQuery(j, k, reporttable);
				break;
			case 3:
				reporttable = PrivilegeQuery(j, k, reporttable);
				break;
			case 4:
				reporttable = DataQuery(j, k, reporttable);
			}

			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception) {
			System.out.println(sqlexception.getMessage());
			System.out
					.println("\n WARNING: Exception in Variable Query Panel ");
			return reporttable;
		}
		return reporttable;
	}

	public static Vector<?>[] populateTable(int i, int j, int k,
			Vector<?>[] avector) {
		try {
			RdbmsConnection.openConn();
			dbmd = RdbmsConnection.getMetaData();
			switch (i) {
			case 5:
				avector = ColumnDataQuery(j, k, avector);
			}

			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception) {
			System.out.println(sqlexception.getMessage());
			System.out
					.println("\n WARNING: Exception in Variable Column Data Query Panel ");
			return avector;
		}

		return avector;
	}

	private static ReportTableModel IndexQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = RdbmsConnection.getHValue("Database_Catalog");
		s = "";
		String s1 = RdbmsConnection.getHValue("Database_SchemaPattern");
		s = s.compareTo("") != 0 ? s : null;
		s1 = s1.compareTo("") != 0 ? s1 : null;
		Vector vector = RdbmsConnection.getTable();
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Column", "Index", "Type", "Qualifier", "IsUnique",
					"Asc/Dsc", "Cardinality", "Pages", "Filter" });
		else
			reporttable.cleanallRow();
		try {
			for (int k = i; k < j; k++) {
				String s2 = (String) vector.elementAt(k);
				ResultSet resultset = dbmd.getIndexInfo(s, s1, s2, false, true);

				while (resultset.next()) {
					boolean flag = resultset.getBoolean(4);
					String s3 = !flag ? "False" : "True";
					String s4 = resultset.getString(5);
					String s5 = resultset.getString(6);
					String s6 = "";
					short word0 = resultset.getShort(7);
					switch (word0) {
					case 0:
						s6 = "Statistic";
						break;
					case 1:
						s6 = "Clustered";
						break;
					case 2:
						s6 = "Hashed";
						break;
					default:
						s6 = "Type UnKnown";
					}

					String s7 = resultset.getString(9);
					String s8 = resultset.getString(10);
					String s9 = resultset.getString(11);
					String s10 = resultset.getString(12);
					String s11 = resultset.getString(13);
					if ((s7 != null) && (s5 != null)) {
						String[] as = { s2, s7, s5, s6, s4, s3, s8, s9, s10,
								s11 };
						reporttable.addFillRow(as);
					}
				}
				resultset.close();
			}
		} catch (SQLException ee) {
			System.out.println("Exception:" + ee.getMessage());
			return reporttable;
		}

		return reporttable;
	}

	private static ReportTableModel MetaDataQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector vector = RdbmsConnection.getTable();
		ResultSet resultset = null;
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Column", "Type", "Size", "Precision", "Radix", "Remark",
					"Default", "Bytes", "Ordinal Pos", "Nullable" });
		else {
			reporttable.cleanallRow();
		}
		for (int k = i; k < j; k++) {
			String s2 = (String) vector.elementAt(k);
			resultset = dbmd.getColumns(s1, s, s2, null);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					String s4 = resultset.getString(4);
					String s5 = resultset.getString(6);
					String s6 = resultset.getString(7);
					String s7 = resultset.getString(9);
					String s8 = resultset.getString(10);
					String s9 = resultset.getString(12);
					String s10 = resultset.getString(13);
					String s11 = resultset.getString(16);
					String s12 = resultset.getString(17);
					String s13 = resultset.getString(18);
					String[] as = { s2, s4, s5, s6, s7, s8, s9, s10, s11, s12,
							s13 };
					reporttable.addFillRow(as);
				}
			}
		}

		resultset.close();
		return reporttable;
	}

	private static ReportTableModel PrivilegeQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		int k = 0;
		Vector vector = RdbmsConnection.getTable();
		ResultSet resultset = null;
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Grantor", "Grantee", "Privileges", "Grantable" });
		else
			reporttable.cleanallRow();
		for (int l = i; l < j; l++) {
			String s2 = (String) vector.elementAt(l);
			resultset = dbmd.getTablePrivileges(s1, s, s2);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					k++;
					String s4 = resultset.getString(4);
					String s5 = resultset.getString(5);
					String s6 = resultset.getString(6);
					String s7 = resultset.getString(7);
					String[] as = { s3, s4, s5, s6, s7 };
					reporttable.addFillRow(as);
				}
			}
		}

		resultset.close();
		if (k == 0)
			System.out
					.println("Tables do not Exist \n Or You might not have permisson to run this query ");
		return reporttable;
	}

	private static ReportTableModel DataQuery(int i, int j,
			ReportTableModel reporttable) throws SQLException {
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		s1 = "";
		String s2 = RdbmsConnection.getHValue("Database_DSN");
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector vector = RdbmsConnection.getTable();
		String s3 = RdbmsConnection.getDBType();
		if (reporttable == null)
			reporttable = new ReportTableModel(new String[] { "Table",
					"Column", "Record", "Unique", "Pattern", "Null", "Zero",
					"Empty" });
		else {
			reporttable.cleanallRow();
		}
		synchronized (RdbmsConnection.class) {
			for (int k = i; k < j; k++) {
				try {
					if (s3.compareToIgnoreCase("oracle_native") == 0) {
						RdbmsConnection.openConn();
						dbmd = RdbmsConnection.getMetaData();
					}
					String s17 = (String) vector.elementAt(k);
					Vector vector1 = new Vector();
					ResultSet resultset = dbmd.getColumns(s1, s, s17, null);

					while (resultset.next()) {
						String s18 = resultset.getString(3);
						if (s18.equals(s17)) {
							String s19 = resultset.getString(4);
							vector1.add(s19);
						}
					}
					resultset.close();
					if (s3.compareToIgnoreCase("oracle_native") == 0)
						RdbmsConnection.closeConn();
					String[] as;
					for (Enumeration enumeration = vector1.elements(); enumeration
							.hasMoreElements(); reporttable.addFillRow(as)) {
						String s10 = "0";
						String s11 = "0";
						String s12 = "0";
						String s13 = "0";
						String s14 = "0";
						String s15 = "0";
						String s20 = (String) enumeration.nextElement();
						QueryBuilder querybuilder = new QueryBuilder(s2, s17,
								s20, s3);
						String s4 = querybuilder.count_query_w(false,
								"row_count");
						String s5 = querybuilder.count_query_w(true,
								"row_count");
						String s6 = querybuilder.get_nullCount_query_w("Null");
						String s7 = querybuilder.get_zeroCount_query_w("0");
						String s8 = querybuilder.get_zeroCount_query_w("''");
						String s9 = querybuilder.get_pattern_query();
						if (s3.compareToIgnoreCase("oracle_native") == 0)
							RdbmsConnection.openConn();
						try {
							for (resultset = RdbmsConnection.runQuery(s4); resultset
									.next();) {
								s10 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception) {
							s10 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s5); resultset
									.next();) {
								s11 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception1) {
							s11 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s6); resultset
									.next();) {
								s12 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception2) {
							s12 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s7); resultset
									.next();) {
								s13 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception3) {
							s13 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s8); resultset
									.next();) {
								s14 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception4) {
							s14 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s9); resultset
									.next();) {
								s15 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception5) {
							s15 = "N/A";
						}
						as = new String[] { s17, s20, s10, s11, s15, s12, s13,
								s14 };

						if (s3.compareToIgnoreCase("oracle_native") == 0)
							RdbmsConnection.closeConn();
					}
				} catch (SQLException ee) {
					return reporttable;
				}

			}
		}
		return reporttable;
	}

	private static Vector<?>[] ColumnDataQuery(int i, int j, Vector[] avector)
			throws SQLException {
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector vector = RdbmsConnection.getTable();
		avector = new Vector[2];
		avector[0] = new Vector();
		avector[1] = new Vector();
		int k = 0;

		for (int l = i; l < j; l++) {
			String s2 = (String) vector.elementAt(l);
			ResultSet resultset = dbmd.getColumns(s1, s, s2, null);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					String s4 = resultset.getString(4);
					int i1 = resultset.getInt(5);
					avector[0].add(k, s4);
					avector[1].add(k, new Integer(i1));
					k++;
				}
			}
			resultset.close();
		}
		return avector;
	}

	public static TableRelationInfo getTableRelationInfo(String cat,
			String sch, String table) throws SQLException {
		TableRelationInfo TableRelationInfo = new TableRelationInfo(table);
		RdbmsConnection.openConn();
		dbmd = RdbmsConnection.getMetaData();
		if (RdbmsConnection.getDBType().compareToIgnoreCase("ms_access") == 0) {
			ResultSet resultset9 = dbmd.getIndexInfo(cat, sch, table, false,
					true);

			while (resultset9.next()) {
				String s57 = resultset9.getString(9);
				String s63 = resultset9.getString(6);
				String s71 = resultset9.getString(3);
				if ((s57 != null) && (s63 != null))
					if (s63.compareToIgnoreCase("primarykey") == 0) {
						TableRelationInfo.pk[TableRelationInfo.pk_c] = s57;
						TableRelationInfo.pk_index[TableRelationInfo.pk_c] = s63;
						TableRelationInfo.hasPKey = true;
						TableRelationInfo.pk_c += 1;
						TableRelationInfo.isRelated = true;
					} else if (s63.endsWith(table)) {
						TableRelationInfo.fk[TableRelationInfo.fk_c] = s57;
						TableRelationInfo.fk_pKey[TableRelationInfo.fk_c] = null;
						TableRelationInfo.fk_pTable[TableRelationInfo.fk_c] = s63
								.substring(0, s63.lastIndexOf(table));
						TableRelationInfo.hasFKey = true;
						TableRelationInfo.fk_c += 1;
						TableRelationInfo.isRelated = true;
					}
			}
			resultset9.close();
		} else {
			int l3 = 0;
			ResultSet resultset10 = dbmd.getPrimaryKeys(cat, sch, table);

			while (resultset10.next()) {
				String s64 = resultset10.getString(4);
				String s72 = resultset10.getString(6);
				if ((s64 != null) && (s72 != null)) {
					TableRelationInfo.pk[l3] = s64;
					TableRelationInfo.pk_index[l3] = s72;
					TableRelationInfo.hasPKey = true;
					l3++;
					TableRelationInfo.pk_c += 1;
				}
			}
			resultset10.close();

			l3 = 0;
			for (resultset10 = dbmd.getImportedKeys(cat, sch, table); resultset10
					.next();) {
				String s65 = resultset10.getString(3);
				String s73 = resultset10.getString(4);
				String s79 = resultset10.getString(7);
				String s83 = resultset10.getString(8);
				TableRelationInfo.fk[l3] = s83;
				TableRelationInfo.fk_pKey[l3] = s73;
				TableRelationInfo.fk_pTable[l3] = s65;
				TableRelationInfo.hasFKey = true;
				TableRelationInfo.fk_c += 1;
				TableRelationInfo.isRelated = true;
				l3++;
			}
			resultset10.close();

			l3 = 0;
			for (resultset10 = dbmd.getExportedKeys(cat, sch, table); resultset10
					.next();) {
				String s66 = resultset10.getString(3);
				String s74 = resultset10.getString(4);
				String s80 = resultset10.getString(7);
				String s84 = resultset10.getString(8);
				TableRelationInfo.pk_ex[l3] = s74;
				TableRelationInfo.pk_exKey[l3] = s84;
				TableRelationInfo.pk_exTable[l3] = s80;
				TableRelationInfo.hasExpKey = true;
				TableRelationInfo.exp_c += 1;
				TableRelationInfo.isRelated = true;
				l3++;
			}
			resultset10.close();
		}
		RdbmsConnection.closeConn();
		return TableRelationInfo;
	}

	public static ReportTableModel getSuperTableInfo(String cat, String sch,
			String _table) throws SQLException {
		ReportTableModel _rt = new ReportTableModel(new String[] { "Table",
				"Super Table" });
		RdbmsConnection.openConn();
		dbmd = RdbmsConnection.getMetaData();

		ResultSet rs = dbmd.getSuperTables(cat, sch, _table);
		while (rs.next()) {
			String table = rs.getString(3);
			if (_table.equals(table)) {
				String[] row = { table, rs.getString(4) };
				_rt.addFillRow(row);
			}
		}
		rs.close();
		RdbmsConnection.closeConn();
		return _rt;
	}

	public static ReportTableModel getColumnDefaultValue(String cat,
			String sch, String _table) throws SQLException {
		ReportTableModel _rt = new ReportTableModel(new String[] { "Column",
				"Default Value" });
		RdbmsConnection.openConn();
		dbmd = RdbmsConnection.getMetaData();

		ResultSet rs = dbmd.getColumns(cat, sch, _table, null);
		while (rs.next()) {
			String table = rs.getString(3);
			if (_table.equals(table)) {
				String col = rs.getString(4);
				String def = rs.getString(13);
				String[] row = { col, def };
				_rt.addFillRow(row);
			}
		}
		rs.close();
		RdbmsConnection.closeConn();
		return _rt;
	}
}