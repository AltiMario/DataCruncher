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
package com.datacruncher.spring;

import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.EventTriggerEntity;
import com.datacruncher.jpa.entity.UserEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class EventTriggerReadController implements Controller, DaoSet {

	private static String SYSTEM_TRIGGER = "System Trigger";
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response)  throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		boolean isAllTrigger = Boolean.valueOf(request.getParameter("isAllTrigger") == null ? "false" : request.getParameter("isAllTrigger"));
		
		ObjectMapper mapper = new ObjectMapper();
 		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
 		ServletOutputStream out;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		ReadList readList = null;
		
		if(isAllTrigger) {
			readList = eventTriggerDao.read();
			List<EventTriggerEntity> listAllTriggers = (List<EventTriggerEntity>)readList.getResults();
			for(EventTriggerEntity instance : listAllTriggers) {
				if(instance.isSystemType()) {
					instance.setName(SYSTEM_TRIGGER);
				}
			}
		} else {
			readList = eventTriggerDao.show(Integer.parseInt(start), Integer.parseInt(limit));
		}
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}

}
