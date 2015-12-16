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

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.UserApplicationsEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
 
public class ApplicationsCreateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		String json = request.getReader().readLine();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ApplicationEntity applicationEntity = new ApplicationEntity ();
		applicationEntity = mapper.readValue(json , ApplicationEntity.class);
		applicationEntity.setDescription(applicationEntity.getDescription().replace('\u200b',' '));		

		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		Create create = null;
		create = appDao.create(applicationEntity);
		if(user.getIdRole() != Roles.ADMINISTRATOR.getDbCode()){
			if(hasCreateRole(user)){
				 UserApplicationsEntity userApplicationEntity = new UserApplicationsEntity();
				 userApplicationEntity.setIdApplication(applicationEntity.getIdApplication());
				 userApplicationEntity.setIdUser(user.getIdUser());
				 userAppDao.create(userApplicationEntity);
			}
		}	
		out.write(mapper.writeValueAsBytes(create));
		out.flush();
		out.close();
 		
 		return null;
	}
	//---------------------HELPERS------------------------------
	private boolean hasCreateRole(UserEntity user){
		List<String> userRoleActivities = user.getRoleActivities();
		for (String role : userRoleActivities) {
			if(role.equals(Activity.APPLICATION_ADD.getScriptCode())){
				return true;
			}
		}
		return false;
	}
}

