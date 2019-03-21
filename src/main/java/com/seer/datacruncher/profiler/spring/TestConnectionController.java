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

package com.seer.datacruncher.profiler.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.profiler.bl.DataQualityBL;
import com.seer.datacruncher.profiler.bl.TestConnectionBL;
import com.seer.datacruncher.profiler.dto.TestConnectionDTO;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsNewConnection;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.DataQualityUtil;

public class TestConnectionController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		TestConnectionDTO tcDTO = new TestConnectionDTO();
		tcDTO.setDbType(request.getParameter("common_db_type"));
		tcDTO.setDsn(request.getParameter("db_name"));
		tcDTO.setUser(request.getParameter("user"));
		tcDTO.setPasswd(request.getParameter("password"));
		tcDTO.setDriver(request.getParameter("db_driver"));
		tcDTO.setProtocol(request.getParameter("db_protocol"));
		tcDTO.setCatalog(request.getParameter("data_catalog"));
		tcDTO.setSchemaPattern(request.getParameter("db_schema_pattern"));
		tcDTO.setTablePattern(request.getParameter("db_table_pattern"));
		tcDTO.setColPattern(request.getParameter("db_column_pattern"));
		tcDTO.setShowType(request.getParameter("db_show_type"));
		tcDTO.setComparion(CommonUtil.notNullValue(
				request.getParameter("comparison")).equals("true"));
		tcDTO.setJdbcCs("");

		TestConnectionBL tcBL = new TestConnectionBL();

		boolean success = tcBL.testDatabaseConnection(tcDTO, request);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		if (success) {

			if (tcDTO.isComparion()) {
				HttpSession session = request.getSession(true);
				@SuppressWarnings("unchecked")
				Hashtable<String, String> dbParams = (Hashtable<String, String>) session
						.getAttribute("dbConnectionDataForComparison");
				try {
					RdbmsNewConnection newConn = new RdbmsNewConnection(
							dbParams);
					newConn.openConn();
					newConn.populateTable();
					Vector table_v = newConn.getTable();
					// String tableName = (String)table_v.get(0);
					// Vector[] avector = newConn.populateColumn(tableName,
					// null);
					newConn.closeConn();

					DataQualityBL dqbl = new DataQualityBL();
					out.println("{\"success\": true, tables:"
							+ DataQualityUtil.generateTableColumnNames(table_v,
									null) + ", columns: "
							+ dqbl.loadColumnNames(table_v) + "}");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				out.println("{\"success\": true}");
			}
		} else {
			out.println("{\"success\": false}");
		}

		return null;
	}

}