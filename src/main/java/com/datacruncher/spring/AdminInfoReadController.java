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
 */package com.datacruncher.spring;

import com.datacruncher.listeners.TimeStampServlet;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.AdminInfoEntity;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AdminInfoReadController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
				
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		Date currentDate = Calendar.getInstance().getTime();
		UserEntity userEntity = (UserEntity)request.getSession().getAttribute("user");
		
		String strDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDate);
		String userName = "";
		String ipAddress = getServerIPAddress(request.getRequestURL().toString());
		
		if(userEntity != null)
			userName = userEntity.getUserName();
		
		List<AdminInfoEntity> listAdminInfos = new ArrayList<AdminInfoEntity>();
		
		AdminInfoEntity adminInfoEntity = new AdminInfoEntity();
		adminInfoEntity.setName(I18n.getMessage("label.currentUser"));
		adminInfoEntity.setValue(userName);
		listAdminInfos.add(adminInfoEntity);
		
		adminInfoEntity = new AdminInfoEntity();
		adminInfoEntity.setName(I18n.getMessage("label.currentDateTime"));
		adminInfoEntity.setValue(strDate);
		listAdminInfos.add(adminInfoEntity);
		
		adminInfoEntity = new AdminInfoEntity();
		adminInfoEntity.setName(I18n.getMessage("label.starupOn"));
		adminInfoEntity.setValue(TimeStampServlet.serverStartupTime);
		listAdminInfos.add(adminInfoEntity);
		
		adminInfoEntity = new AdminInfoEntity();
		adminInfoEntity.setName(I18n.getMessage("label.serverIP"));
		adminInfoEntity.setValue(ipAddress);
		listAdminInfos.add(adminInfoEntity);
		
		adminInfoEntity = new AdminInfoEntity();
		adminInfoEntity.setName(I18n.getMessage("label.validatedStream"));
		adminInfoEntity.setValue(String.valueOf(datastreamsDao.getTotalValidatedStreams()));
		listAdminInfos.add(adminInfoEntity);
		
		ReadList readList = new ReadList();
		readList.setResults(listAdminInfos);	
		readList.setSuccess(true);
				
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}
	
	private String getServerIPAddress(String requestURL) {
		
		if(requestURL == null || requestURL.trim().length() == 0) {
			return "";
		}
		
		String ipAddress = "";
		String strTemp = requestURL;
		try {
			if (strTemp.indexOf("http://") != -1) {
				strTemp = strTemp.substring(7);
			}

			if (strTemp.indexOf("/") != -1) {
				strTemp = strTemp.substring(0, strTemp.indexOf("/"));
				if (strTemp.indexOf(":") != -1) {
					ipAddress = strTemp.substring(0, strTemp.indexOf(":"));
				} else {
					ipAddress = strTemp;
				}
			} else {
				ipAddress = strTemp;
			}
						
			InetAddress ownIP = InetAddress.getByName(ipAddress);
			ipAddress = ownIP.getHostAddress();
			
		} catch (UnknownHostException uhex) {
			ipAddress = "";
		} catch (Exception ex) {
			ipAddress = "";
		}
		return ipAddress;
	}
}