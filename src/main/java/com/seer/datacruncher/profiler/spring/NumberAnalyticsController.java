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
import javax.servlet.ServletOutputStream;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.profiler.bl.NumberAnalyticsBL;
import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.util.ChartUtil;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.GridUtil;

import org.codehaus.jackson.map.ObjectMapper;

public class NumberAnalyticsController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Hashtable<String, String> dbParams = (Hashtable<String, String>) request
				.getSession(true).getAttribute("dbConnectionData");
		String dsn = "";
		if (dbParams != null) {
			dsn = CommonUtil.notNullValue(dbParams.get("Database_DSN"));
		}
		ObjectMapper mapper = new ObjectMapper();
		String condition = CommonUtil.notNullValue(request
				.getParameter("condition"));
		String table = CommonUtil.notNullValue(request.getParameter("table"));
		String column = CommonUtil.notNullValue(request.getParameter("column"))
				.split("\\:")[0];
		String type = CommonUtil.notNullValue(request.getParameter("column"))
				.split("\\:")[1];
		String action = CommonUtil.notNullValue(request.getParameter("action"));
		if (action.equals("binAnalysis")) {
			String text1 = CommonUtil.notNullValue(request
					.getParameter("text1"));
			String text2 = CommonUtil.notNullValue(request
					.getParameter("text2"));
			String text3 = CommonUtil.notNullValue(request
					.getParameter("text3"));
			String text4 = CommonUtil.notNullValue(request
					.getParameter("text4"));
			String text5 = CommonUtil.notNullValue(request
					.getParameter("text5"));
			String text6 = CommonUtil.notNullValue(request
					.getParameter("text6"));
			String text7 = CommonUtil.notNullValue(request
					.getParameter("text7"));
			String text8 = CommonUtil.notNullValue(request
					.getParameter("text8"));
			String text9 = CommonUtil.notNullValue(request
					.getParameter("text9"));
			String text10 = CommonUtil.notNullValue(request
					.getParameter("text10"));
			String text11 = CommonUtil.notNullValue(request
					.getParameter("text11"));

			String binNames1 = CommonUtil.notNullValue(request
					.getParameter("binNames1"));
			String binNames2 = CommonUtil.notNullValue(request
					.getParameter("binNames2"));
			String binNames3 = CommonUtil.notNullValue(request
					.getParameter("binNames3"));
			String binNames4 = CommonUtil.notNullValue(request
					.getParameter("binNames4"));
			String binNames5 = CommonUtil.notNullValue(request
					.getParameter("binNames5"));
			String binNames6 = CommonUtil.notNullValue(request
					.getParameter("binNames6"));
			String binNames7 = CommonUtil.notNullValue(request
					.getParameter("binNames7"));
			String binNames8 = CommonUtil.notNullValue(request
					.getParameter("binNames8"));
			String binNames9 = CommonUtil.notNullValue(request
					.getParameter("binNames9"));
			String binNames10 = CommonUtil.notNullValue(request
					.getParameter("binNames10"));

			String text[] = { text1, text2, text3, text4, text5, text6, text7,
					text8, text9, text10, text11 };

			NumberAnalyticsBL nabl = new NumberAnalyticsBL(dsn, type,
					condition, table, column);
			try {
				PrintWriter out = response.getWriter();
				Double[] values = nabl.fillXValues(text);
				String[] fields = { binNames1, binNames2, binNames3, binNames4,
						binNames5, binNames6, binNames7, binNames8, binNames9,
						binNames10 };
				String chartData = ChartUtil.getBinAnalysisBarChart(fields,
						values);
				out.print("{success: true, barChartData:\"" + chartData + "\"}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (action.equals("numberProfiler")) {
			String aggr = CommonUtil.notNullValue(request.getParameter("aggr"));
			String less = CommonUtil.notNullValue(request.getParameter("less"));
			String more = CommonUtil.notNullValue(request.getParameter("more"));
			String between1 = CommonUtil.notNullValue(request
					.getParameter("between1"));
			String between2 = CommonUtil.notNullValue(request
					.getParameter("between2"));
			String text2 = CommonUtil.notNullValue(request
					.getParameter("text2"));
			String text3 = CommonUtil.notNullValue(request
					.getParameter("text3"));
			String text4 = CommonUtil.notNullValue(request
					.getParameter("text4"));
			String text5 = CommonUtil.notNullValue(request
					.getParameter("text5"));
			String text6 = CommonUtil.notNullValue(request
					.getParameter("text6"));
			String text7 = CommonUtil.notNullValue(request
					.getParameter("text7"));

			NumberAnalyticsBL nabl = new NumberAnalyticsBL(dsn, type,
					condition, table, column);
			nabl.setAggr(aggr);
			nabl.setLess(less);
			nabl.setMore(more);
			nabl.setBetween1(between1);
			nabl.setBetween2(between2);
			nabl.setText2(text2);
			nabl.setText3(text3);
			nabl.setText4(text4);
			nabl.setText5(text5);
			nabl.setText6(text6);
			nabl.setText7(text7);
			ServletOutputStream out = null;
			response.setContentType("application/json");
			out = response.getOutputStream();			
			out.write(mapper.writeValueAsBytes(nabl.numberProfile()));
			out.flush();
			out.close();
			
		} else {
			String tab = CommonUtil.notNullValue(request.getParameter("tab"));
			NumberAnalyticsBL nabl = new NumberAnalyticsBL(dsn, type,
					condition, table, column);

			TableGridDTO tgDTO = nabl.generateStatisticGrids(new Integer(tab));
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridData(tgDTO, false, null);
			request.setAttribute("gridColumns", gridUtil.getColumnNames());
			request.setAttribute("gridFields", gridUtil.getFieldNames());
			request.setAttribute("gridData", gridUtil.getData());
			request.setAttribute("tabId", tab);
			return new ModelAndView("jsp/profiler/advancenumberanal.jsp");

		}

		return null;
	}

}