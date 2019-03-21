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
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.AlertEntity;
import com.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AlertReadController implements Controller, DaoSet {

	private Logger log = Logger.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String logMsg = "AlertReadController:handleRequest:";
		log.debug(logMsg + "Entry");

		ObjectMapper mapper = null;
		ServletOutputStream out = null;
		try {
			alertsDao.init();
			mapper = new ObjectMapper();
			response.setContentType("application/json");
			out = response.getOutputStream();
			ReadList readList = alertsDao.read();
			@SuppressWarnings("unchecked")
			List<AlertEntity> list = (List<AlertEntity>) readList.getResults();
			if (list != null && list.size() > 0) {
				List<AlertEntity> tempList = new ArrayList<AlertEntity>(list.size());
				AlertEntity aEntity = null;
				for (AlertEntity alertEntity : list) {
					String aName = alertEntity.getAlertName();
					long id = alertEntity.getIdAlert();
					aEntity = new AlertEntity(id, I18n.getMessage(aName));
					tempList.add(aEntity);
					aEntity = null;
				}
				readList.setResults(tempList);
			} else {
				log.fatal(logMsg + " No Alerts Available");
			}
			out.write(mapper.writeValueAsBytes(readList));
			out.flush();
			out.close();
		} catch (NoSuchMessageException e) {
			log.error(logMsg + "NoSuchMessageException:" + e, e);
		} catch (IOException e) {
			log.error(logMsg + "IOException:" + e, e);
		} catch (Exception e) {
			log.error(logMsg + "Exception:" + e, e);
		} finally {
			log.debug(logMsg + "Exit");
		}
		return null;
	}
}