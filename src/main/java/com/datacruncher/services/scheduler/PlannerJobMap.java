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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

public class PlannerJobMap {
	
    private Map<Long, PlannerJob> jobMap;	
	private static Logger log = Logger.getLogger(PlannerJobMap.class);	
	
	/**
	 * Gets jobs' map.
	 * 
	 * @return Never returns null, only jobs' map
	 */
	public Map<Long, PlannerJob> getJobMap() {
		return jobMap == null ? new HashMap<Long, PlannerJob>() : jobMap;
	}
	
	/**
	 * Adds job to jobs' map.
	 * 
	 * @param id - job id
	 * @param job - PlannerJob
	 */
	public void put(Long id, PlannerJob job) {
		if (jobMap == null) jobMap = new HashMap<Long, PlannerJob>();
		jobMap.put(id, job);
	}	
	
	/**
	 * Gets job by id.
	 * 
	 * @param id - job id
	 * @return - PlannerJob
	 */
	public PlannerJob get(Long id) {
		if (jobMap == null) jobMap = new HashMap<Long, PlannerJob>();
		return jobMap.get(id);
	}	
	
	/**
	 * Delete job by id.
	 * 
	 * @param id - job id
	 */
	public void delete(Long id) {
		PlannerJob job = get(id);
		try {
			job.unschedule();
		} catch (SchedulerException e) {
			log.error("Job can not be unscheduled", e);	
		}
		jobMap.remove(id);			
	}		
}
