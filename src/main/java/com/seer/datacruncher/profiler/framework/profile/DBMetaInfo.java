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
import java.util.Hashtable;
import java.util.Vector;

import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.framework.rdbms.TableRelationInfo;

public class DBMetaInfo {

	private Hashtable hashtable;
	private Hashtable hashtable1;
	private Hashtable hashtable2;
	private DatabaseMetaData dbmd;

	public void getTableModelInfo() throws SQLException {
		RdbmsConnection.openConn();
		dbmd = RdbmsConnection.getMetaData();
		hashtable = new Hashtable();
		hashtable1 = new Hashtable();
		hashtable2 = new Hashtable();
		String s13 = RdbmsConnection.getHValue("Database_Catalog");
		s13 = "";
		String s19 = RdbmsConnection.getHValue("Database_SchemaPattern");
		s13 = s13.compareTo("") == 0 ? null : s13;
		s19 = s19.compareTo("") == 0 ? null : s19;
		Vector vector = RdbmsConnection.getTable();
		int l1 = vector.size();
		for (int j2 = 0; j2 < l1; j2++) {
			String s39 = (String) vector.elementAt(j2);
			try {
				TableRelationInfo TableRelationInfo = new TableRelationInfo(s39);
				if (RdbmsConnection.getDBType()
						.compareToIgnoreCase("ms_access") == 0) {
					ResultSet resultset9 = dbmd.getIndexInfo(s13, s19, s39,
							false, true);
					do {
						if (!resultset9.next())
							break;
						String s57 = resultset9.getString(9);
						String s63 = resultset9.getString(6);
						String s71 = resultset9.getString(3);
						if (s57 != null && s63 != null)
							if (s63.compareToIgnoreCase("primarykey") == 0) {
								TableRelationInfo.pk[TableRelationInfo.pk_c] = s57;
								TableRelationInfo.pk_index[TableRelationInfo.pk_c] = s63;
								TableRelationInfo.hasPKey = true;
								TableRelationInfo.pk_c++;
								TableRelationInfo.isRelated = true;
							} else if (s63.endsWith(s39)) {
								TableRelationInfo.fk[TableRelationInfo.fk_c] = s57;
								TableRelationInfo.fk_pKey[TableRelationInfo.fk_c] = null;
								TableRelationInfo.fk_pTable[TableRelationInfo.fk_c] = s63
										.substring(0, s63.lastIndexOf(s39));
								TableRelationInfo.hasFKey = true;
								TableRelationInfo.fk_c++;
								TableRelationInfo.isRelated = true;
							}
					} while (true);
					resultset9.close();
				} else {
					int l3 = 0;
					ResultSet resultset10 = dbmd.getPrimaryKeys(s13, s19, s39);
					do {
						if (!resultset10.next())
							break;
						String s64 = resultset10.getString(4);
						String s72 = resultset10.getString(6);
						if (s64 != null && s72 != null) {
							TableRelationInfo.pk[l3] = s64;
							TableRelationInfo.pk_index[l3] = s72;
							TableRelationInfo.hasPKey = true;
							l3++;
							TableRelationInfo.pk_c++;
						}
					} while (true);
					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getImportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s65 = resultset10.getString(3);
						String s73 = resultset10.getString(4);
						String s79 = resultset10.getString(7);
						String s83 = resultset10.getString(8);
						TableRelationInfo.fk[l3] = s83;
						TableRelationInfo.fk_pKey[l3] = s73;
						TableRelationInfo.fk_pTable[l3] = s65;
						TableRelationInfo.hasFKey = true;
						TableRelationInfo.fk_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}

					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getExportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s66 = resultset10.getString(3);
						String s74 = resultset10.getString(4);
						String s80 = resultset10.getString(7);
						String s84 = resultset10.getString(8);
						TableRelationInfo.pk_ex[l3] = s74;
						TableRelationInfo.pk_exKey[l3] = s84;
						TableRelationInfo.pk_exTable[l3] = s80;
						TableRelationInfo.hasExpKey = true;
						TableRelationInfo.exp_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}

					resultset10.close();
				}
				if (TableRelationInfo.isRelated)
					hashtable2.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else if (TableRelationInfo.hasPKey)
					hashtable.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else
					hashtable1.put(TableRelationInfo.tableName,
							TableRelationInfo);
			} catch (Exception exception) {
				System.out.println((new StringBuilder(
						"\n WARNING: Unknown Exception Happened for Table:"))
						.append(s39).toString());
				System.out.println((new StringBuilder("\n Message: ")).append(
						exception.getMessage()).toString());
				exception.printStackTrace();
			}
		}

		RdbmsConnection.closeConn();

	}

	public Hashtable getNoPKTable() {
		return hashtable1;
	}

	public Hashtable getOnlyPKTable() {
		return hashtable;
	}

	public Hashtable getRelatedTable() {
		return hashtable2;
	}
}
