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
 */package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;

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

public class ApplicationsReadController implements Controller, DaoSet {
    
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		
 		ObjectMapper mapper = new ObjectMapper();
 		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
 		ServletOutputStream out;
		response.setContentType("application/json");
		out = response.getOutputStream();
		long userId = user.getIdUser();
		
		ReadList readList = null;
		// Display all available application to admin user and display assigned applications to other role users
		if(user.getIdRole() == Roles.ADMINISTRATOR.getDbCode()){
			readList = appDao.read();
		}else{
			readList = appDao.read(userId);
		}
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
 			
 		return null;
	}
}