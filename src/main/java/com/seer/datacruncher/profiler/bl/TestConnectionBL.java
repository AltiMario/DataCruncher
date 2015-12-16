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

import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.seer.datacruncher.profiler.dto.TestConnectionDTO;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsNewConnection;

public class TestConnectionBL {

	public boolean testDatabaseConnection(TestConnectionDTO tcDTO,
			HttpServletRequest request) {
		try {
			Hashtable<String, String> _dbparam = new Hashtable<String, String>();
			if (tcDTO.getDbType() != null)
				_dbparam.put("Database_Type", tcDTO.getDbType());
			if (tcDTO.getDsn() != null)
				_dbparam.put("Database_DSN", tcDTO.getDsn());
			if (tcDTO.getUser() != null)
				_dbparam.put("Database_User", tcDTO.getUser());
			if (tcDTO.getPasswd() != null)
				_dbparam.put("Database_Passwd", tcDTO.getPasswd());
			if (tcDTO.getDriver() != null)
				_dbparam.put("Database_Driver", tcDTO.getDriver());
			if (tcDTO.getProtocol() != null)
				_dbparam.put("Database_Protocol", tcDTO.getProtocol());
			if (tcDTO.getCatalog() != null)
				_dbparam.put("Database_Catalog", tcDTO.getCatalog());
			if (tcDTO.getSchemaPattern() != null)
				_dbparam.put("Database_SchemaPattern", tcDTO.getSchemaPattern());
			if (tcDTO.getTablePattern() != null)
				_dbparam.put("Database_TablePattern", tcDTO.getTablePattern());
			if (tcDTO.getColPattern() != null)
				_dbparam.put("Database_ColumnPattern", tcDTO.getColPattern());
			if (tcDTO.getShowType() != null)
				_dbparam.put("Database_TableType", tcDTO.getShowType());
			if (tcDTO.getJdbcCs() != null)
				_dbparam.put("Database_JDBC", tcDTO.getJdbcCs());

			String status = null;
			if(tcDTO.isComparion()){
				RdbmsNewConnection newCon = new RdbmsNewConnection(_dbparam);				
				status = newCon.testConn();
			}
			else{
				RdbmsConnection.init(_dbparam);
				status = RdbmsConnection.testConn();
			}
			if (status.equals("Connection Successful")) {
				HttpSession session = request.getSession(true);
				// Removing password before saving values in the session
				// _dbparam.remove("Database_Passwd");
				if(tcDTO.isComparion()){
					session.setAttribute("dbConnectionDataForComparison", _dbparam);
				}
				else{
					session.setAttribute("dbConnectionData", _dbparam);
				}
				return true;
			}
			System.out.println(status);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
