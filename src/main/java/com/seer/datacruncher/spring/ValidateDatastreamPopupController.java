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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.datastreams.DatastreamsInput;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ValidateDatastreamPopupController implements Controller {
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {		
		String idSchema = request.getParameter("idSchema");
 		String datastream = request.getParameter("datastream");
 		
 		datastream = datastream.replaceAll(">\\s*<", "><");
 		
 		DatastreamsInput datastreamsInput = new DatastreamsInput ();
 		response.setContentType("application/json");
        ServletOutputStream out =  response.getOutputStream();
		out.write(datastreamsInput.datastreamsInput(datastream , Long.parseLong(idSchema), null).getBytes("UTF8"));
		out.flush();
		out.close();
 		
 		return null;
	}
}