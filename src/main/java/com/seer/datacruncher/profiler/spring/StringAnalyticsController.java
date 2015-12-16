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

import com.seer.datacruncher.profiler.bl.StringAnalyticsBL;
import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.util.CommonUtil;
import org.codehaus.jackson.map.ObjectMapper;


public class StringAnalyticsController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Hashtable<String, String> dbParams = (Hashtable<String, String>) request
				.getSession(true).getAttribute("dbConnectionData");
		String dsn = "";
		if (dbParams != null) {
			dsn = CommonUtil.notNullValue(dbParams.get("Database_DSN"));
		}

		String condition = CommonUtil.notNullValue(request
				.getParameter("condition"));
		String table = CommonUtil.notNullValue(request.getParameter("table"));
		String column = CommonUtil.notNullValue(request.getParameter("column"))
				.split("\\:")[0];
		String type = CommonUtil.notNullValue(request.getParameter("column"))
				.split("\\:")[1];

		String noGrid = CommonUtil.notNullValue(request.getParameter("action"));
		String qp_1 = CommonUtil.notNullValue(request.getParameter("qp_1"));
		String qp_2 = CommonUtil.notNullValue(request.getParameter("qp_2"));
		String qp_3 = CommonUtil.notNullValue(request.getParameter("qp_3"));
		String qc_1 = CommonUtil.notNullValue(request.getParameter("qc_1"));
		String qc_2 = CommonUtil.notNullValue(request.getParameter("qc_2"));
		String qc_3 = CommonUtil.notNullValue(request.getParameter("qc_3"));
		String q_s = CommonUtil.notNullValue(request.getParameter("q_s"));
		String _distinct = CommonUtil.notNullValue(request
				.getParameter("_distinct"));

		StringAnalyticsBL sabl = new StringAnalyticsBL(dsn, type, condition,
				table, column);
		sabl.setQp_1(qp_1);
		sabl.setQp_2(qp_2);
		sabl.setQp_3(qp_3);
		sabl.setQ_s(q_s);
		sabl.setQc_1(qc_1);
		sabl.setQc_2(qc_2);
		sabl.setQc_3(qc_3);
		sabl.set_distinct(_distinct);

		PrintWriter out = response.getWriter();
		if (noGrid.equals("noGrid")) {
			if (q_s.equals("2")) {
				out.print(sabl.reCreateBotPane_regex(true));
			} else {
				out.print(sabl.reCreateBotPane_like(true));
			}
		} else {
			ObjectMapper mapper = new ObjectMapper();
			if (q_s.equals("2")) {
				out.print(mapper.writeValueAsBytes((GridInfoDTO) sabl
						.reCreateBotPane_regex(false)));
			} else {
				out.print(mapper.writeValueAsBytes((GridInfoDTO) sabl
						.reCreateBotPane_like(false)));
			}
		}

		return null;
	}

}