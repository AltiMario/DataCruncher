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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.jpa.entity.ServersEntity;
import com.seer.datacruncher.services.ftp.FtpServerHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ServerSetIsActiveController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long serverId = Long.parseLong(request.getParameter("serverId"));
		int isActive = Integer.parseInt(request.getParameter("isActive"));

		ApplicationConfigEntity configEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);

		serversDao.setActive(serverId, isActive);

		try {
			if (serverId == Servers.FTP.getDbCode() && isActive == 1) {
				FtpServerHandler ftpServerHandler = new FtpServerHandler(configEntity.getServerPort());
				ftpServerHandler.init();
			} else if (serverId == Servers.FTP.getDbCode() && isActive == 0) {
				FtpServerHandler ftpServerHandler = new FtpServerHandler(configEntity.getServerPort());
				ftpServerHandler.destroy();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}