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

import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.utils.generic.I18n;

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

public class SchemasLibReadController implements Controller, DaoSet {
    
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		
		String type = request.getParameter("type");
		String streamType = request.getParameter("streamType");
		String version = request.getParameter("version");
		String idSchemaLib = request.getParameter("idSchemaLib");
        String list = request.getParameter("list");

		ObjectMapper mapper = new ObjectMapper();		
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		ReadList readList = new ReadList();
		
		if(type != null && streamType != null) {
			if(type.equals("version"))
				readList = schemaLibDao.readByLibType(Integer.parseInt(streamType));
			else if(type.equals("libName")) {
				readList = schemaLibDao.readByLibTypeAndVersion(Integer.parseInt(streamType),version);
			}
				
		} else if(idSchemaLib != null && idSchemaLib.trim().length() > 0) {
			Update update = new Update();
			update.setResults(schemaLibDao.find(Long.parseLong(idSchemaLib)));
			update.setSuccess(true);
			update.setMessage(I18n.getMessage("success.listRecord"));
			
			out.write(mapper.writeValueAsBytes(update));
			out.flush();
			out.close();
			return null;
		} else {

            if(list != null){
                String start = request.getParameter("start");
                String limit = request.getParameter("limit");
                readList = schemaLibDao.read(Integer.parseInt(start), Integer.parseInt(limit));
            }else{
                readList.setResults(schemaLibDao.findAll());
                readList.setSuccess(true);
                readList.setMessage(I18n.getMessage("success.listRecord"));
            }

		}
		
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}
}