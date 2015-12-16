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

package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.constants.ReportType;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManagerFactory;

/**
 * 
 * Implementation of this class is in EE-module. 
 *
 */
public class MongoDbDao {
	
	
	/**
	 * Gets mongoDB entityManagerFactory.
	 * 
	 * @return
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		return null;
	}
	

	/**
	 * Gets data for Annual, Detailed, Monthly reports.
	 *
	 * @return reports data
	 */
	public static List<?> getList(ReportType type, int appId, int schemaId, Calendar currentDate) {
		return null;
	}
	
	/**
	 * Gets data for Donut and RealTime report. 
	 * 
	 * @return
	 */
	public static List<?> getList(int appId, int schemaId, int year, int month, Calendar currentDate, boolean isDonut) {
		return null;
	}
	
}
