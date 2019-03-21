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

import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.JobsEntity;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

public class SchedulerUtils implements DaoSet {
	
    public static String FIELD_PLANNER = "FIELD_PLANNER";
    public static String FIELD_CONNECTION = "FIELD_CONNECTION";	
    public static String FIELD_SCHEMA = "FIELD_SCHEMA";
	private static Logger log = Logger.getLogger(SchedulerUtils.class);	    
    
    private SchedulerUtils() {
    	//this constructor will never be invoked.
    }

	/**
	 * Stops all active jobs that are connected to current entity with fieldId. Example: if planner entity
	 * changed, then method gets all active jobs that connected to this planner,
	 * unschedules these jobs, and sets activity = false.
	 * 
	 * @param plannerMap - PlannerMap
	 * @param fieldId - id of current entity (planner, connection, application, schema)
	 * @param fieldValue - value of current entity
	 * @return isAnyJobDeactivated - true/false
	 */
	public static boolean stopJobsByConnectedField(PlannerJobMap plannerMap, String fieldId, long fieldValue) {
		ReadList read = jobsDao.read();
		boolean isAnyJobDeactivated = false;
		for (Object o : read.getResults()) {
			JobsEntity ent = (JobsEntity) o;
			if (ent.getIsActive() == 1) {
				long value = FIELD_PLANNER.equals(fieldId) ? ent.getIdScheduler() : FIELD_CONNECTION.equals(fieldId) ? ent
						.getIdConnection() : FIELD_SCHEMA.equals(fieldId) ? ent.getIdSchema() : 0;
				if (value != 0 && value == fieldValue) {
					ent.setIsActive(0);
					jobsDao.update(ent);
					PlannerJob job = plannerMap.getJobMap().get(ent.getId());
					try {
						job.unschedule();
						isAnyJobDeactivated = true;
					} catch (SchedulerException e) {
						log.error("Job can not be unscheduled!", e);
					}
				}
			}
		}
		return isAnyJobDeactivated;
	}    
}
