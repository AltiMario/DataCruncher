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

import com.seer.datacruncher.profiler.bl.CompareTableBL;
import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.util.ChartUtil;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.GridUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CompareTableController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String action = CommonUtil.notNullValue(request.getParameter("action"));
		CompareTableBL ctbl = new CompareTableBL();
		ctbl.setTable1(CommonUtil.notNullValue(request.getParameter("table1")));
		ctbl.setTable2(CommonUtil.notNullValue(request.getParameter("table2")));
		ctbl.setCol1(CommonUtil.notNullValue(request.getParameter("col1")));
		ctbl.setCol2(CommonUtil.notNullValue(request.getParameter("col2")));
		ctbl.setRb(CommonUtil.notNullValue(request.getParameter("relation")));
		ctbl.setFt(CommonUtil.notNullValue(request.getParameter("ft")));
		ctbl.setLink(CommonUtil.notNullValue(request.getParameter("link")));
		
		boolean editable = new Boolean(CommonUtil.notNullValue(request.getParameter("editable")));
		if (action.equals("link")) {
			TableGridDTO tgDTO = ctbl.linkClicked();
			GridUtil gridUtil = new GridUtil();
			gridUtil.generateGridData(tgDTO, editable, null);
			out.println("{\"success\": true, \"data\":" + gridUtil.getData()
					+ ", \"columns\":" + gridUtil.getColumnNames()
					+ ", \"fields\":" + gridUtil.getFieldNames() + "}");
		} else {
			Map<String, List<String>> dataMap = ctbl.buttonClicked();
			String data = ChartUtil.generateDataSetFromMap(dataMap);
			out.println("{\"success\": true, \"data\":" + data + "}");
		}

		return null;
	}

}