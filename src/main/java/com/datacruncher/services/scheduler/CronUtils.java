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

package com.datacruncher.services.scheduler;

import com.datacruncher.jpa.entity.TasksEntity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CronUtils {
	
	/**
	 * Cron utilities class. 
	 * 
	 */
    static Logger log = Logger.getLogger(CronUtils.class);
	private CronUtils() {
		//This constructor will never be invoked.
	}
	
	/**
	 * Gets cron mask by planner(task) entity.
	 * 
	 * @param ent - planner(task) entity
	 * @return cron mask (Example: "0 * * * * ?")
	 */
	public static String getCronMask(TasksEntity ent) {
		// In DB for m, h, d, month, week '-1' means 'Not chosen', '-2' means
		// 'Every'
		Map<String, String> cronExp = new HashMap<String, String>();
		cronExp.put("second", "0");
		cronExp.put("minute", "*");
		cronExp.put("hour", "*");

		cronExp.put("day", "*");
		cronExp.put("month", "*");
		cronExp.put("weekday", "?");

		cronExp.put("year", "*");

		String resp = "0 ";
		if (ent.getIsOneShoot()) {
			String strTime = ent.getShootTime();
			String strDate = new SimpleDateFormat("dd MM yyyy").format(ent
					.getShootDate());
			String data[] = strDate.split(" ");

			if (strTime != null && strTime.indexOf(":") != -1) {
				resp += strTime.substring(strTime.indexOf(":") + 1) + " ";
				resp += strTime.substring(0, strTime.indexOf(":")) + " ";
			} else {
				resp += "0 0 ";
			}

			resp += data[0] + " " + data[1] + " " + "? " + data[2];

			return resp;
		} else {
			int m = ent.getMinute();
			int es = ent.getEverysecond();
			int h = ent.getHour();
			int d = ent.getDay();
			int month = ent.getMonth();
			int week = ent.getWeek();
			
			if (es > -1) {
				cronExp.put("second", String.valueOf("0/" + es));
			} else {
				if(h > 0)
					cronExp.put("hour", String.valueOf(h));
				if(m > 0)
					cronExp.put("minute", String.valueOf(m));
			}

			if (week > -1) {
				cronExp.put("weekday", String.valueOf(week));
				cronExp.put("day", "?");
			} else {
				if(d > 0)
					cronExp.put("day", String.valueOf(d));
				if(month > 0)
					cronExp.put("month", String.valueOf(month));

				log.info(resp);
			}
			
			resp = cronExp.get("second") + " " + cronExp.get("minute") + " " + cronExp.get("hour") + " " + cronExp.get("day") + " " + cronExp.get("month") + " " + cronExp.get("weekday") + " " + cronExp.get("year");
			return resp;
		}
	}
}
