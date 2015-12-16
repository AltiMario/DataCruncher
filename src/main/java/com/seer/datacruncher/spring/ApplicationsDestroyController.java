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
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.JobsEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.UserApplicationsEntity;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ApplicationsDestroyController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		String appId = request.getParameter("idApplication");
		if(appId.indexOf("/") != -1) {
			appId = appId.substring(0, appId.indexOf("/"));
		}
		
		ReadList readSchemaEntity = schemasDao.readByApplicationId(-1, -1, Long.parseLong(appId));
		List<SchemaEntity> listSchemaEntity = null;
		
		if(readSchemaEntity.isSuccess()) {
			listSchemaEntity = (List<SchemaEntity>)readSchemaEntity.getResults();
		}
		
		Destroy destroy = null;
		if(listSchemaEntity != null && listSchemaEntity.size() > 0) {
			for(SchemaEntity instance : listSchemaEntity) {
				destroy = schemasDao.destroy(instance.getIdSchema());
				
				if(!destroy.isSuccess()) {
					break;
				}
			}
		}

		ReadList readJobsEntity = jobsDao.readByApplicationId(Long.parseLong(appId));
		List<JobsEntity> listJobsEntity = (List<JobsEntity>)readJobsEntity.getResults();
		
		if(listJobsEntity != null && listJobsEntity.size() > 0) {
			for(JobsEntity instance : listJobsEntity) {
				destroy = jobsDao.destroy(instance.getId());
				if(!destroy.isSuccess()) {
					break;
				}
			}
		}

		
		ReadList readUserApplicationsEntity = userAppDao.findByApplicationId(Long.parseLong(appId));
		List<UserApplicationsEntity> listUserApplicationsEntity = (List<UserApplicationsEntity>)readUserApplicationsEntity.getResults();
		
		if(listUserApplicationsEntity != null && listUserApplicationsEntity.size() > 0) {
			for(UserApplicationsEntity instance : listUserApplicationsEntity) {
				destroy = userAppDao.destroy(instance.getIdUserApplication());
				if(!destroy.isSuccess()) {
					break;
				}
			}
		}
		
		destroy = appDao.destroy(Long.parseLong(appId));
				
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