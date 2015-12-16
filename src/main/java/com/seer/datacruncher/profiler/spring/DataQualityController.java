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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.profiler.bl.DataQualityBL;
import com.seer.datacruncher.profiler.framework.util.DiscreetRange;
import com.seer.datacruncher.profiler.framework.util.KeyValueParser;
import com.seer.datacruncher.profiler.util.CommonUtil;

public class DataQualityController implements Controller, ServletContextAware {

	protected final Log logger = LogFactory.getLog(getClass());
	private String filePath;
	private ServletContext servletContext;
	public void init() {
		// Get the file location where it would be stored.
		filePath =servletContext.getInitParameter("file-upload");
	}
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			PrintWriter out = response.getWriter();
			DataQualityBL dqbl = new DataQualityBL();

			String json = "";
			String table = CommonUtil.notNullValue(request
					.getParameter("table"));
			Vector<String> columns = new Vector<String>();
			String columnValues = CommonUtil.notNullValue(request
					.getParameter("columns"));

			String queryString = CommonUtil.notNullValue(request
					.getParameter("queryString"));
			for (String col : columnValues.split(",")) {
				columns.add(col);
			}
			String subAction = CommonUtil.notNullValue(request
					.getParameter("subAction"));
			String commitValue = CommonUtil.notNullValue(request
					.getParameter("commitValue"));
			if (!commitValue.equals("")) {
				List newValues = Arrays.asList(commitValue.split(","));
				dqbl.setCommitValues(newValues);
			}
			dqbl.setCommit(subAction.equals("commit"));
			if (CommonUtil.notNullValue(request.getParameter("action")).equals(
					"duplicate")) {
				json = dqbl.getDuplicates(table, columns, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("uppercase")) {
				json = dqbl
						.getCaseFormattedGrid(table, columns, 1, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("lowercase")) {
				json = dqbl
						.getCaseFormattedGrid(table, columns, 2, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("titlecase")) {
				json = dqbl
						.getCaseFormattedGrid(table, columns, 3, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("sentencecase")) {
				json = dqbl
						.getCaseFormattedGrid(table, columns, 4, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("replacenull")) {
				String replaceWith = CommonUtil.notNullValue(request
						.getParameter("replace"));
				json = dqbl.repalceNullAction(table, columns, replaceWith,
						queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("format")) {
				String formatType = CommonUtil.notNullValue(request
						.getParameter("formatType"));
				String formatValues = CommonUtil.notNullValue(request
						.getParameter("formatValues"));

				json = dqbl.matchAction(table, columns, true, queryString,
						formatType, formatValues.split(","));

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("formatnomatch")) {
				String formatType = CommonUtil.notNullValue(request
						.getParameter("formatType"));
				String formatValues = CommonUtil.notNullValue(request
						.getParameter("formatValues"));

				json = dqbl.matchAction(table, columns, false, queryString,
						formatType, formatValues.split(","));

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("standard")) {
				String fileName = filePath
						+ CommonUtil.notNullValue(request
								.getParameter("filePath"));
				Hashtable filterHash = KeyValueParser.parseFile(fileName);
				json = dqbl.searchAction(table, columns, filterHash,
						queryString);

			}

			else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("descrete")) {
				String text = CommonUtil.notNullValue(request
						.getParameter("text"));
				String delimeter = CommonUtil.notNullValue(request
						.getParameter("delimeter"));
				Vector token = DiscreetRange.tokenizeText(text, delimeter);

				json = dqbl.disceetSearchAction(table, columns, token, true,
						queryString);
			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("descretenomatch")) {
				String text = CommonUtil.notNullValue(request
						.getParameter("text"));
				String delimeter = CommonUtil.notNullValue(request
						.getParameter("delimeter"));
				Vector token = DiscreetRange.tokenizeText(text, delimeter);

				json = dqbl.disceetSearchAction(table, columns, token, false,
						"");
			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("inclusive")) {
				json = dqbl.incAction(table, columns, true, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("exclusive")) {
				json = dqbl.incAction(table, columns, false, queryString);

			} else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("comparison")) {
				HttpSession session = request.getSession(true);
				@SuppressWarnings("unchecked")
				Hashtable<String, String> dbParams = (Hashtable<String, String>) session
						.getAttribute("dbConnectionDataForComparison");
				String rTable = CommonUtil.notNullValue(request
						.getParameter("rTable"));
				Vector rColumns = new Vector();
				String rColumnValues = CommonUtil.notNullValue(request
						.getParameter("rColumns"));
				for (String col : rColumnValues.split(",")) {
					rColumns.add(col);
				}
				boolean mr = CommonUtil.notNullValue(
						request.getParameter("comMatch")).equals("true");
				json = dqbl.compareAction(table, columns, rTable, rColumns,
						dbParams, mr);

			}

			else if (CommonUtil.notNullValue(request.getParameter("action"))
					.equals("similarity")) {

				String searchCriteria = CommonUtil.notNullValue(request
						.getParameter("searchCriteria"));
				String importance = CommonUtil.notNullValue(request
						.getParameter("importance"));
				String skipWords = CommonUtil.notNullValue(request
						.getParameter("skipWords"));
				int sc[] = new int[searchCriteria.split(",").length];
				for (int i = 0; i < searchCriteria.split(",").length; i++) {
					sc[i] = Integer.parseInt(searchCriteria.split(",")[i]);
				}

				int imp[] = new int[importance.split(",").length];
				for (int i = 0; i < importance.split(",").length; i++) {
					imp[i] = Integer.parseInt(importance.split(",")[i]);
				}
				json = dqbl.similarAction(table, columns, queryString, sc, imp,
						skipWords.split(","));
			}
			out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void setServletContext(ServletContext arg0) {
		this.servletContext = arg0;
		
	}	

}