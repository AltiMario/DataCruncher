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
 */

package com.datacruncher.spring;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LanguageController implements Controller {
	private final Logger log = Logger.getLogger(this.getClass());

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String language = null;
		try {
			language = request.getParameter("language");
			if (language == null || language.trim().length() == 0) {
				language = "en";
			}
		} catch (Exception exception) {
			log.error("Error occured during fetching the client locale." + exception);
		} finally {
			// Set default locale on basis on language
			if ("en".equals(language)) {
				Locale.setDefault(Locale.ENGLISH);
			} else if ("it".equals(language)) {
				Locale.setDefault(Locale.ITALIAN);
			} else if ("de".equals(language)) {
				Locale.setDefault(Locale.GERMAN);
			} else if ("ru".equals(language)) {
				Locale locale = new Locale("ru", "RU");
				Locale.setDefault(locale);
			} else {
				// Default is US
				Locale.setDefault(Locale.ENGLISH);
			}
		}
		return null;
	}
}