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
package com.seer.datacruncher.utils.generic;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.seer.datacruncher.constants.ReportType;

public class ReportsUtils {
	
	private ReportsUtils() {
		// never invoked
	}
	
	public static synchronized Date getStartDate(ReportType type, Calendar calendar) {
		if (type == ReportType.DETAILED) {
			setHourMinSecAsNull(calendar);
			return calendar.getTime();
		} else if (type == ReportType.ANNUAL || type == ReportType.MONTHLY) {
			return calendar.getTime();
		} else if (type == ReportType.REAL_TIME) {
			Calendar startDate = (Calendar) calendar.clone();
			startDate.add(Calendar.SECOND, -3);
			return startDate.getTime();
		}
		throw new IllegalArgumentException("ReportsUtils :: unsupported report type");		
	}
	
	public static synchronized Date getEndDate(ReportType type, Calendar calendar) {
		if (type == ReportType.MONTHLY || type == ReportType.DETAILED) {
            Calendar endDate = new GregorianCalendar();
            endDate.setTime(calendar.getTime());
            endDate.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            if (type == ReportType.MONTHLY) {
            	setHourMinSecAsNull(calendar);
            }
            return endDate.getTime();
		} else if (type == ReportType.ANNUAL) {
            Calendar endDate = new GregorianCalendar();
            endDate.setTime(calendar.getTime());
            endDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            return endDate.getTime();			
		} else if (type == ReportType.REAL_TIME) {
			return calendar.getTime();
		}		
		throw new IllegalArgumentException("ReportsUtils :: unsupported report type");		
	}
	
	public static synchronized Date getDonutStartDate(int year, int month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public static synchronized Date getDonutEndDate(int year, int month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
	
	private static void setHourMinSecAsNull(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}
}
