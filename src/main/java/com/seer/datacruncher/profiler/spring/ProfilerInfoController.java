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

package com.seer.datacruncher.profiler.spring;

import com.seer.datacruncher.profiler.bl.InfoBL;
import com.seer.datacruncher.profiler.framework.profile.FirstInformation;
import com.seer.datacruncher.profiler.framework.profile.QueryDialog;
import com.seer.datacruncher.profiler.framework.profile.TableMetaInfo;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.util.ChartUtil;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.GridUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class ProfilerInfoController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		@SuppressWarnings("unchecked")
		Hashtable<String, String> dbParams = (Hashtable<String, String>) request
				.getSession(true).getAttribute("dbConnectionData");
		if (dbParams != null) {
			request.setAttribute("serverName",
					CommonUtil.notNullValue(dbParams.get("Database_DSN")));
		}
		String selectedValue = CommonUtil.notNullValue(request
				.getParameter("selectedValue"));
		request.setAttribute("selectedValue", selectedValue);
		String parentNodeValue = CommonUtil.notNullValue(request
				.getParameter("parent"));
		request.setAttribute("parentValue", parentNodeValue);
		ObjectMapper mapper = new ObjectMapper();
		if (CommonUtil.notNullValue(request.getParameter("action")).equals(
				"minMaxValues")) {			
			InfoBL ibl = new InfoBL();
			String data = mapper.writeValueAsString(ibl.generateMinMaxValueGrid(
					CommonUtil.notNullValue(dbParams.get("Database_DSN")),
					parentNodeValue, selectedValue.split(":")[0]));
			PrintWriter out = response.getWriter();
			out.println(data);
			return null;
		} else if (CommonUtil.notNullValue(request.getParameter("tab")).equals(
				"analysis")) {
			String queryString = CommonUtil.notNullValue(request
					.getParameter("queryString"));
			Vector vector = RdbmsConnection.getTable();
			String s = parentNodeValue;
			int i = vector.indexOf(s);
			Vector avector[] = (Vector[]) null;
			avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
			QueryDialog querydialog = new QueryDialog(1, s, avector);
			List<String> listPrimaryKeys = new ArrayList<String>();
			
			try {			
				RdbmsConnection.openConn();
				DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
				
				ResultSet resultset = dbmd.getPrimaryKeys(null, null, s);
				while (resultset.next()) {
					listPrimaryKeys.add(resultset.getString("COLUMN_NAME"));
				}
				RdbmsConnection.closeConn();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			try {
				querydialog.executeAction(queryString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("rowCount", querydialog.getRowCount());
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridData(querydialog.getTableGridDTO(), false, listPrimaryKeys);
			request.setAttribute("gridColumns", gridUtil.getColumnNames());
			GridUtil gridUtilEdit = new GridUtil();
			gridUtilEdit.generateGridData(querydialog.getTableGridDTO(), true, listPrimaryKeys);
			request.setAttribute("gridColumnsEdit", gridUtilEdit.getColumnNames());
			request.setAttribute("gridFields", gridUtil.getFieldNames());
			request.setAttribute("gridData", gridUtil.getData());
			request.setAttribute("systemDate",
					new Date(System.currentTimeMillis()).toString());
			return new ModelAndView("jsp/profiler/analysis.jsp");

		} else if (CommonUtil.notNullValue(request.getParameter("isLeaf"))
				.equals("true")) {
			String s = dbParams.get("Database_DSN");
			String s1 = parentNodeValue;
			String s2 = selectedValue.split(":")[0];
			QueryBuilder querybuilder = new QueryBuilder(s, s1, s2,
					RdbmsConnection.getDBType());
			Double profileValues[] = FirstInformation
					.getProfileValues(querybuilder);
			Vector patternValues[] = FirstInformation
					.getPatternValues(querybuilder);
			Vector distValues[] = getDistributionValues(querybuilder, request);
			// showBar(ad);
			// showPatternChart(avector);
			String pieChartData = ChartUtil.getChartDataForPieChart(distValues);
			String barChartData = ChartUtil
					.getChartDataForBarChart(profileValues);
			String patternChartData = ChartUtil
					.getChartDataForPieChart(patternValues);
			request.setAttribute("pieChartData", pieChartData);
			request.setAttribute("barChartData", barChartData);
			request.setAttribute("patternChartData", patternChartData);
			return new ModelAndView("jsp/profiler/charts.jsp");
		} else {
			Vector vector = RdbmsConnection.getTable();
			String s = selectedValue;
			int i = vector.indexOf(s);
			Vector avector[] = (Vector[]) null;
			avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
			QueryDialog querydialog = new QueryDialog(1, s, avector);
			try {
				querydialog.executeAction("");
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("rowCount", querydialog.getRowCount());
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridData(querydialog.getTableGridDTO(), false, null);
			request.setAttribute("gridColumns", gridUtil.getColumnNames());
			request.setAttribute("colCount", querydialog.getTableGridDTO().getColumnNames().length);
			request.setAttribute("gridFields", gridUtil.getFieldNames());
			request.setAttribute("gridData", gridUtil.getData());
			return new ModelAndView("jsp/profiler/info.jsp");
		}

	}

	public Vector[] getDistributionValues(QueryBuilder querybuilder,
			HttpServletRequest request) {
		Vector avector[] = FirstInformation.getDistributionValues(querybuilder);

		request.setAttribute("minValue", FirstInformation.getMinVal());
		request.setAttribute("maxValue", FirstInformation.getMaxVal());

		return avector;
	}

}