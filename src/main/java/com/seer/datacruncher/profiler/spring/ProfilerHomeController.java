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

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seer.datacruncher.profiler.bl.DataQualityBL;

import java.io.IOException;
import java.util.Hashtable;

public class ProfilerHomeController implements Controller {

    protected final Log logger = LogFactory.getLog(getClass());

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	DataQualityBL dqbl = new DataQualityBL();
		HttpSession session = request.getSession(true);
		@SuppressWarnings("unchecked")
		Hashtable<String, String> dbParams = (Hashtable<String, String>) session
				.getAttribute("dbConnectionData");
		request.setAttribute("tableNames", dqbl.loadTableNames(dbParams));
		request.setAttribute("columnNames", dqbl.loadColumnNames());
		
        return new ModelAndView("jsp/profiler/home.jsp");
    }

}