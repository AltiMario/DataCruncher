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

import com.datacruncher.services.scheduler.PlannerJobMap;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.JobsEntity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.SchedulerException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class JobsUpdateController implements Controller, DaoSet {
	
	Logger log = Logger.getLogger(this.getClass());	
    private PlannerJobMap plannerJobMap;

	public void setPlannerJobMap(PlannerJobMap plannerJobMap) {
		this.plannerJobMap = plannerJobMap;
	}	
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String json = request.getReader().readLine();
		ObjectMapper mapper = new ObjectMapper();
		JobsEntity entity = new JobsEntity();
		entity = mapper.readValue(json, JobsEntity.class);
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(jobsDao.update(entity)));
		out.flush();
		out.close();
		try {
			plannerJobMap.get(entity.getId()).unschedule();
		} catch (SchedulerException e) {
			log.error("Job can not be unscheduled (stopped)", e);
		}	
		return null;
	}
}