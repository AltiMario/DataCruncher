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
import com.seer.datacruncher.jpa.entity.CustomErrorEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CustomErrorsReadController implements Controller, DaoSet {

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String schemaId = request.getParameter("schemaId");
		String isComboStr = request.getParameter("isCombo");
		boolean isCombo = isComboStr == null ? false : Boolean.valueOf(isComboStr);
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		ReadList read = customErrorsDao.read(Long.parseLong(schemaId));
		if (isCombo) {
			//no cute code below, but there's no other way
			CustomErrorEntity ent = new CustomErrorEntity();
			ent.setId(-7);
			ent.setName(I18n.getMessage("message.add_new"));
			@SuppressWarnings("rawtypes")
			List list = read.getResults();
			list.add(ent);
			read.setResults(list);
		}
		out.write(mapper.writeValueAsBytes(read));
		out.flush();
		out.close();
		return null;
	}
}