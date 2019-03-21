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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.profiler.bl.DbInfoBL;
import com.seer.datacruncher.profiler.util.CommonUtil;

import org.codehaus.jackson.map.ObjectMapper;

public class DbInfoController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			PrintWriter out = response.getWriter();
			DbInfoBL dbBL = new DbInfoBL();
			ObjectMapper mapper = new ObjectMapper();
			String json = "";
			String text = CommonUtil.notNullValue(request.getParameter("text"));
			if (CommonUtil.notNullValue(request.getParameter("type")).equals(
					"geninfo")) {
				json = mapper.writeValueAsString(dbBL.getGeneralInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("supinfo")) {
				json = mapper.writeValueAsString(dbBL.getSupportInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("limitinfo")) {
				json = mapper.writeValueAsString(dbBL.getLimitationInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("funcinfo")) {
				json = mapper.writeValueAsString(dbBL.getFunctionInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("cataloginfo")) {
				json = mapper.writeValueAsString(dbBL.getCatalogInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("standardsqlinfo")) {
				json = mapper.writeValueAsString(dbBL.getStandardSQLInfo());
			}

			else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("tableprivilegeinfo")) {
				json = mapper.writeValueAsString(dbBL.getTablePrivilege(text));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("columnprivilegeinfo")) {
				json = mapper.writeValueAsString(dbBL.getColumnPrivilege(text));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("tablemetadatainfo")) {
				json = mapper.writeValueAsString(dbBL.getTableMetaData(text));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("procedureinfo")) {
				json = mapper.writeValueAsString(dbBL.getProcedureInfo());
			}

			else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("parameterinfo")) {
				json = mapper.writeValueAsString(dbBL.getParameterInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("usersqlinfo")) {
				json = mapper.writeValueAsString(dbBL.getUserSQLInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("schemainfo")) {
				json = mapper.writeValueAsString(dbBL.getSchemaInfo());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("indexinfo")) {
				json = mapper.writeValueAsString(dbBL.IndexQuery(null));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("dbmetadatainfo")) {
				json = mapper.writeValueAsString(dbBL.DbMetaDataQuery());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("alltableprivinfo")) {
				json = mapper.writeValueAsString(dbBL.AllTablesPrivilegeQuery());
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("datainfo")) {
				json = mapper.writeValueAsString(dbBL.DataQuery(null));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("tabledatainfo")) {
				json = mapper.writeValueAsString(dbBL.DataQuery(text));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("tableindexinfo")) {
				json = mapper.writeValueAsString(dbBL.IndexQuery(text));
			} else if (CommonUtil.notNullValue(request.getParameter("type"))
					.equals("tablemodelinfo")) {
				// json = mapper.writeValueAsString(dbBL.IndexQuery(text));
				Hashtable data = dbBL.tableModelInfo(CommonUtil.notNullValue(request.getParameter("subType")));
				request.setAttribute("relationData", data);
				if (CommonUtil.notNullValue(request.getParameter("subType"))
						.equals("noFK")) {
					return new ModelAndView(
							"jsp/profiler/tableWithNoFK.jsp");
				} else if (CommonUtil.notNullValue(
						request.getParameter("subType")).equals("noPK")) {
					return new ModelAndView(
							"jsp/profiler/tableWithNoPK.jsp");
				}
			}
			out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}