/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
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