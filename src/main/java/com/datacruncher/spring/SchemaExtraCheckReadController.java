/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

package com.datacruncher.spring;

import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.UserEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaExtraCheckReadController implements Controller, DaoSet {
    
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		String type = request.getParameter("type");
						
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
				
		if(type != null && type.equals("regExps")) {
            out.write(mapper.writeValueAsBytes(checksTypeDao.readRegExps(Integer.parseInt(start), Integer.parseInt(limit))));
		} else {
			out.write(mapper.writeValueAsBytes(checksTypeDao.readComplete(Integer.parseInt(start), Integer.parseInt(limit))));
		}
		out.flush();
		out.close();
		return null;
	}	
}