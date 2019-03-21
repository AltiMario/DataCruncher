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
 */package com.datacruncher.spring;

import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.JobsEntity;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class JobsReadController implements Controller, DaoSet {
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		ReadList instance = jobsDao.read();
		List<JobsEntity> listJobs = (List<JobsEntity>)instance.getResults();
		
		for(JobsEntity entity : listJobs) {
				
			if(entity.getIdApplication() > 0)
				appDao.find(entity.getIdApplication()).getName();
			if(entity.getIdConnection() > 0)
				connectionsDao.find(entity.getIdConnection()).getName();
			if(entity.getIdSchema() > 0)
				schemasDao.find(entity.getIdSchema()).getName();
			if(entity.getIdScheduler() > 0)
				tasksDao.find(entity.getIdScheduler()).getName();
			
			if(entity.getIdEventTrigger() > 0)
				eventTriggerDao.findEventTriggerById(entity.getIdEventTrigger());
		}
		
		out.write(mapper.writeValueAsBytes(instance));
		out.flush();
		out.close();
		return null;
	}
}