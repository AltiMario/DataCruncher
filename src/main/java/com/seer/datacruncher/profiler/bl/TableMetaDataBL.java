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

package com.seer.datacruncher.profiler.bl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.seer.datacruncher.profiler.dto.TableMetaDataDTO;
import com.seer.datacruncher.profiler.dto.TreeDTO;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class TableMetaDataBL {

	private int index;
	private TableMetaDataDTO loadTableMetaData(
			Hashtable<String, String> dbParams) {
		try {

			// String dbSchemaPattern = dbParams.get("Database_SchemaPattern");
			// String dbTablePattern = dbParams.get("Database_TablePattern");
			// String tableType = dbParams.get("Database_TableType");
			// String dbCatalog = dbParams.get("Database_Catalog");
			// dbCatalog = "";
			// RdbmsConnection.populateTable(dbCatalog.compareTo("") == 0 ?
			// (dbCatalog = null)
			// : dbCatalog, dbSchemaPattern.compareTo("") == 0 ?
			// (dbSchemaPattern = null) : dbSchemaPattern, dbTablePattern
			// .compareTo("") == 0 ? (dbTablePattern = null) : dbTablePattern,
			// tableType.split(","));

			TableMetaDataDTO tmDTO = new TableMetaDataDTO();
			tmDTO.setTables(RdbmsConnection.getTable());
			tmDTO.setTableDesc(RdbmsConnection.getTableDesc());

			return tmDTO;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<TreeDTO> loadTableMetaData(String tableName,
			String dbSchemaPattern) {
		ArrayList<TreeDTO> tree = new ArrayList<TreeDTO>();
		try {
			RdbmsConnection.openConn();
			DatabaseMetaData databasemetadata = RdbmsConnection.getMetaData();
			ResultSet resultset;
			TreeDTO obj = null;
			
			for (resultset = databasemetadata.getColumns("", dbSchemaPattern,
					tableName, null); resultset.next();) {
				String s8 = resultset.getString(4);
				String columDetails = (new StringBuilder(String.valueOf(s8)))
						.append(":").toString();
				columDetails = (new StringBuilder(String.valueOf(columDetails)))
						.append(resultset.getString(6)).toString();
				obj = new TreeDTO();

				obj.setId("child_node" + (index++));

				obj.setText(columDetails);
				obj.setLeaf(true);
				obj.setCls("folder");
				tree.add(obj);
			}
			RdbmsConnection.closeConn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tree;
	}

	public ArrayList<TreeDTO> loadTree(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		@SuppressWarnings("unchecked")
		Hashtable<String, String> dbParams = (Hashtable<String, String>) session
				.getAttribute("dbConnectionData");
		TableMetaDataDTO tmDTO = this.loadTableMetaData(dbParams);
		ArrayList<TreeDTO> tree = new ArrayList<TreeDTO>();
		TreeDTO obj = null;
		if(tmDTO.getTables() != null && tmDTO.getTables().size() > 0) {
			int tableCount = tmDTO.getTables().size();
			index = 0;
			for (int i = 0; i < tableCount;) {

				String tableName = (String) tmDTO.getTables().get(i);
				obj = new TreeDTO();
				if (i == 0) {
					obj.setId("root_node");
				} else {
					obj.setId("root_node" + i);
				}
				obj.setText(tableName);
				obj.setLeaf(false);
				obj.setCls("folder");
				ArrayList<TreeDTO> children = this.loadTableMetaData(tableName,	dbParams.get("Database_SchemaPattern"));
				obj.setChildren(children);
				tree.add(obj);
				i++;
			}
		}
		return tree;
	}
}
