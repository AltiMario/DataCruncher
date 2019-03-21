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

package com.datacruncher.spring;

import com.datacruncher.jpa.ReadList;
import com.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class TimeForTaskReadController implements Controller {
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ReadList readList = new ReadList();
		String unit = request.getParameter("unit");
		if (unit == null || unit.isEmpty()) return null;
		int max = unit.equals("minute") ? 60 : unit.equals("hour") ? 24 : unit
				.equals("day") ? 31 : unit.equals("month") ? 12 : unit
				.equals("week") ? 7 : 0;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", "-2");
		map.put("value", I18n.getMessage("combo.every", null, Locale.getDefault()));
		list.add(map);
		for (int i = 0; i < max; i++) {
			map = new HashMap<String, String>();
			map.put("id", String.valueOf(unit.equals("day") || unit.equals("month") || unit.equals("week") ? i + 1 : i));
			map.put("value", unit.equals("month") ? I18n.getMessage("month" + (i + 1), null, Locale.getDefault()) 
					: unit.equals("week") ? I18n.getMessage("day" + (i + 1), null, Locale.getDefault()) 
					: unit.equals("day") ? String.valueOf(i + 1)
					: String.valueOf(i));
			list.add(map);
		}
		readList.setResults(list);
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord", null,
				Locale.getDefault()));
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}
}