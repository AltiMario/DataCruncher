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

import com.seer.datacruncher.profiler.bl.MiscBL;
import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.GridUtil;

public class MiscController implements Controller {

    protected final Log logger = LogFactory.getLog(getClass());

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String action = CommonUtil.notNullValue(request.getParameter("action"));

		if(action.equals("validateCondition") || action.equals("applyCondition")){
			String table =  CommonUtil.notNullValue(request.getParameter("table"));
			String columnNames = CommonUtil.notNullValue(request.getParameter("columnNames"));
			String columnType = CommonUtil.notNullValue(request.getParameter("columnTypes"));
			String condition = CommonUtil.notNullValue(request.getParameter("condition"));
			String conditionValue = CommonUtil.notNullValue(request.getParameter("conditionValue"));
			String conditionType = CommonUtil.notNullValue(request.getParameter("conditionType"));
			String indexValue = CommonUtil.notNullValue(request.getParameter("index"));
			MiscBL mbl = new MiscBL(1, table, (Integer.parseInt(indexValue) -1));
			
			mbl.initMiscBL(columnNames, conditionType, conditionValue, columnType, condition);
			
			if(action.equals("validateCondition")){
				String status = mbl.validateCondtion();
				if(status != null){
					 out.print("{success: true, count: \"" + status + "\" , conditionQuery:\"" + mbl.cond + "\"}");
				}
				else{
					out.println("{\"success\": false}");
				}
			}
			else{
				@SuppressWarnings("unchecked")
				Hashtable<String, String> dbParams = (Hashtable<String, String>) request
						.getSession(true).getAttribute("dbConnectionData");
				if (dbParams != null) {
					request.setAttribute("serverName",
							CommonUtil.notNullValue(dbParams.get("Database_DSN")));
				}
				String selectedValue = CommonUtil.notNullValue(request
						.getParameter("table"));
				request.setAttribute("selectedValue", selectedValue);
				TableGridDTO tgDTO = mbl.applyCondition();
				GridUtil gridUtil = new GridUtil();
				gridUtil.generateGridData(tgDTO, false, null);
				request.setAttribute("gridColumns", gridUtil.getColumnNames());
				request.setAttribute("gridFields", gridUtil.getFieldNames());
				request.setAttribute("gridData", gridUtil.getData());
				
				request.setAttribute("rowCount", tgDTO.getRowValues().size());

				return new ModelAndView("jsp/profiler/info.jsp");
			}
		}
		return null;
		
	}

}