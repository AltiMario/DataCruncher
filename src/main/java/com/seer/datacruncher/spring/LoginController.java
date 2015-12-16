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

import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoginController implements Controller, DaoSet {
	
	private final Logger log = Logger.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		try {
			appInit();
		} catch (Exception e) {
			log.fatal("Unable to initialize system",e);
			return null;
		}

		UserEntity userEntity = new UserEntity ();
		userEntity = usersDao.login(userName , password);
		ServletOutputStream out = null;
		ObjectMapper mapper = new ObjectMapper();
 		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		response.setContentType("application/json");
		out = response.getOutputStream();
		if (userEntity != null) {
			if (userEntity.getEnabled() != 1) {
				out.write("notEnabled".getBytes());
			} else {
				ReadList readList = roleActivityDao.read(userEntity.getIdRole());
				userEntity.setRoleActivities((List<String>) readList.getResults());
				session.setAttribute("userId", userEntity.getIdUser());
				session.setAttribute("user", userEntity);
				if (CommonUtils.isEEModule() && isLicenseExpired()) {
					out.write(("licenseIsExpired%%").getBytes());
				}
				out.write(mapper.writeValueAsBytes(userEntity));
			}
		} else {
			out.write("invalid".getBytes());
		}
		out.flush();
		out.close();
 		return null;
	}

	//this method is overridden in other modules
	protected boolean isLicenseExpired() {
		return false;
	}

	// -------------HELPERS-------------
	private void appInit() throws Exception {
		try {
			roleDao.init();
			activityDao.init();
			roleActivityDao.init();
			usersDao.init();
			alertsDao.init();
		} catch (Exception exception) {
			throw exception;
		} finally {
		}
	}
}