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

import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.services.scheduler.PlannerJobMap;
import com.seer.datacruncher.services.scheduler.SchedulerUtils;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 

public class TasksDestroyController implements Controller, DaoSet {

	private PlannerJobMap plannerJobMap;
    
	public void setPlannerJobMap(PlannerJobMap plannerJobMap) {
		this.plannerJobMap = plannerJobMap;
	}		

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String taskId = request.getParameter("taskId");
		
		if(taskId != null && taskId.startsWith("/"))
			taskId = taskId.substring(1);
		
		boolean isAnyJobDeactivated = SchedulerUtils.stopJobsByConnectedField(plannerJobMap,
				SchedulerUtils.FIELD_PLANNER, Long.parseLong(taskId));
		Destroy destroy = tasksDao.destroy(Long.parseLong(taskId));
		if (isAnyJobDeactivated)
			destroy.setExtraMessage(I18n.getMessage("success.linkedJobsDeacivated"));
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(destroy));
		out.flush();
		out.close();
		return null;
	}
}