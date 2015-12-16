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

import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class UsersReadController implements Controller, DaoSet {
	
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity loggedUser = (UserEntity)session.getAttribute("user");
		
		if (loggedUser == null) {
			return null;
		}
 		ObjectMapper mapper = new ObjectMapper();
 		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
 		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		ReadList readList = null;
		long roleId = loggedUser.getIdRole();
		if (roleId == Roles.OPERATOR.getDbCode()
				|| roleId == Roles.DISPATCHER.getDbCode()
				|| roleId == Roles.USER.getDbCode()) {
			UserEntity userEntity = usersDao.find(loggedUser.getIdUser());
			readList = new ReadList();
			readList.setSuccess(true);
			readList.setMessage(I18n.getMessage("success.listRecord"));
			List<UserEntity> list = new ArrayList<UserEntity>();
			list.add(userEntity);
			readList.setResults(list);
		}else if(roleId == Roles.ADMINISTRATOR.getDbCode()){
			readList = usersDao.read();
		}else if(roleId == Roles.APPLICAITON_MANAGER.getDbCode()){
			/*int[] roles = { 
				Roles.OPERATOR.getDbCode(),
				Roles.DISPATCHER.getDbCode(), 
				Roles.USER.getDbCode() 
			};
			readList = usersDao.read(roles);*/
			readList = usersDao.read(loggedUser.getIdUser());
		}
		@SuppressWarnings("unchecked")
		List<UserEntity> users = (List<UserEntity>)readList.getResults();
		for (UserEntity user : users) {
			String encPassword = user.getPassword();
			try {
				String plainPassword = new CryptoUtil().decrypt(encPassword);
				user.setPassword(plainPassword);
			} catch (Exception e) {
			}
			user.setUserApplications(usersDao.getUserApps(user.getIdUser()));
			user.setUserSchemas(usersDao.getUserSchemas(user.getIdUser()));
		}
		if(roleId == Roles.APPLICAITON_MANAGER.getDbCode()){
			users.add(loggedUser);
		}
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
 			
 		return null;
	}
}