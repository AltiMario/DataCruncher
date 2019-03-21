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
 */package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.entity.UserEntity;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for small ajax methods that need not be introduced in bean context.
 */

@Controller
public class CommonsController {
	
	/**
	 * Is current user admin.
	 * 
	 * @return true/false as String
	 */
	@RequestMapping(value = "controller.isAdmin.json")
	@ResponseBody
	public String isAdmin(/* @RequestParam("name") String parameter */HttpSession session) {
		return String.valueOf(((UserEntity) session.getAttribute("user")).getIdRole() == Roles.ADMINISTRATOR.getDbCode());
	}
}
