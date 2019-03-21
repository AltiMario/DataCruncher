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

import com.seer.datacruncher.jpa.Validate;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.validation.common.Regex;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class RegularExpressionCheckController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Logger log = Logger.getLogger(this.getClass());
		String regularExpression = request.getParameter("regularExpression");
		String message = "";
		boolean success = true;
		try {
			new Regex(regularExpression);
			message = I18n.getMessage("success.RegExprValid");
		} catch (Exception ex) {
			success = false;
			log.error("Regular expression malformed! : " + ex.getMessage());
			message = I18n.getMessage("error.RegExprNotValid") + " : " + ex.getMessage();
		}
		ObjectMapper mapper = new ObjectMapper();
		
		Validate validate = new Validate();
		validate.setMessage(message);
		validate.setSuccess(success);
		
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(validate));
		out.flush();
		out.close();
		return null;
	}
}