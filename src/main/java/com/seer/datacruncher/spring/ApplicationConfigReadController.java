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
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.CryptoUtil;

import java.io.IOException;
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

public class ApplicationConfigReadController implements Controller, DaoSet {
    
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		
 		ObjectMapper mapper = new ObjectMapper();
 		ServletOutputStream out;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		ReadList readList = new ReadList();
		
		String configType = request.getParameter("configType");
		ApplicationConfigEntity appConfigEntity = null;
		if(configType.equals("email")) {			
			appConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.EMAIL);
		} else if(configType.equals("ftp")) {
			appConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
		} 
		if(appConfigEntity != null) {
			List<ApplicationConfigEntity> listConfigEntity = new ArrayList<ApplicationConfigEntity>();
			appConfigEntity.setPassword(new CryptoUtil().decrypt(appConfigEntity.getPassword()));
			listConfigEntity.add(appConfigEntity);
			readList.setResults(listConfigEntity);
			readList.setSuccess(true);
		} else {
			readList.setSuccess(false);
			readList.setMessage("Issue in getting config data");
		}
		
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
 		return null;
	}
}