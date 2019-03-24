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

import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.RoleEntity;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.utils.generic.I18n;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class UsersDestroyController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String userId = request.getParameter("userId");
		
		if(userId != null && userId.startsWith("/"))
			userId = userId.substring(1);
		
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		Destroy destroy = usersDao.destroy(Long.parseLong(userId));
		if (destroy.isSuccess()) {
			usersDao.destroyUserApps(Long.parseLong(userId));
			usersDao.destroyUserSchemas(Long.parseLong(userId));
		}
		out.write(mapper.writeValueAsBytes(destroy));
		out.flush();
		out.close();
		
		return null;
	}
	public ModelAndView validateBeforeDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		
		UserEntity userToDelete = usersDao.find(Long.parseLong(request.getParameter("userId")));
		Destroy destroy = new Destroy();
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		if(userToDelete != null ) {			
			RoleEntity role = roleDao.getRoleById(userToDelete.getIdRole());	
			if(userToDelete.getIdUser() == loggedInUser.getIdUser()) {
				destroy.setMessage(I18n.getMessage("error.deleteLoggedInUserError"));
				destroy.setSuccess(false);
			}else if("Administrator".equals(role.getRoleName())) {
				destroy.setMessage(I18n.getMessage("error.deleteAdminUserError"));
				destroy.setSuccess(false);
			}else {
				destroy.setMessage(I18n.getMessage("error.deleteUserWarning"));
				destroy.setSuccess(true);
			}
		}else {
			destroy.setMessage(I18n.getMessage("error.userNotExistError"));
			destroy.setSuccess(false);
		}
		out.write(mapper.writeValueAsBytes(destroy));
		out.flush();
		out.close();
		return null;
	}
}