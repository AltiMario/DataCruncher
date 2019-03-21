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
import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.SchemaEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class ApplicationsUpdateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		String json = request.getReader().readLine();
		//String json = request.getParameter("results");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ApplicationEntity applicationEntity = new ApplicationEntity ();
		applicationEntity = mapper.readValue(json , ApplicationEntity.class);
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		ReadList readList = schemasDao.readByApplicationId(-1, -1, applicationEntity.getIdApplication());
		if (readList != null && readList.getResults() != null) {
            @SuppressWarnings("unchecked")
			List<SchemaEntity> list = (List<SchemaEntity>)readList.getResults();
			for (SchemaEntity schemaEntity : list) {
				if(schemaEntity.getIsPlanned()){
					schemaEntity.setPlannedName(applicationEntity.getPlannedName());
					schemasDao.update(schemaEntity);
				}				
			}
		}
		out.write(mapper.writeValueAsBytes(appDao.update(applicationEntity)));
		out.flush();
		out.close();	
 		return null;
	}
}