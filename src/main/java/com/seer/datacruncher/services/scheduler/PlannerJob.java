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

package com.seer.datacruncher.services.scheduler;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.jpa.dao.ConnectionsDao;
import com.seer.datacruncher.jpa.dao.JobsDao;
import com.seer.datacruncher.jpa.dao.SchemasDao;
import com.seer.datacruncher.jpa.dao.TasksDao;
import com.seer.datacruncher.jpa.entity.ConnectionsEntity;
import com.seer.datacruncher.jpa.entity.JobsEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.services.ServiceScheduledJob;
import com.seer.datacruncher.spring.AppContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.ApplicationContext;

public class PlannerJob {
	
	private static Logger log = Logger.getLogger(PlannerJob.class);	
	private Trigger trigger;
	private TriggerBuilder<Trigger> triggerBuilder;
	private JobDetail jobDetails;
	private Scheduler scheduler;
	private static final String TRIGGER_ID = "plannerJobTrigger";
	private String triggerName;
	private long jobId;
	private JobsEntity jobEnt;
	private ConnectionsEntity connEnt;

	/**
	 * Constructor.
	 * 
	 * @param scheduler - Scheduler which operates current job
	 * @param idPostfix - id postfix
	 */	
	public PlannerJob(long idPostfix, Scheduler scheduler) {
		this.scheduler = scheduler;
		triggerName = TRIGGER_ID + idPostfix;
		jobId = idPostfix;
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		jobEnt = (ctx.getBean(JobsDao.class)).find(jobId);
		connEnt = (ctx.getBean(ConnectionsDao.class)).find(jobEnt.getIdConnection());
		jobDetails = JobBuilder.newJob(ServiceScheduledJob.class).withIdentity(TRIGGER_ID + idPostfix).build();
		triggerBuilder = TriggerBuilder.newTrigger().forJob(jobDetails).withIdentity(TRIGGER_ID + idPostfix);
	}
	
	/**
	 * Start current job.
	 * @throws SchedulerException 
	 * 
	 */
	public void schedule() throws SchedulerException {
		ApplicationContext ctx = AppContext.getApplicationContext();
		
		jobEnt = (ctx.getBean(JobsDao.class)).find(jobId);
		connEnt = (ctx.getBean(ConnectionsDao.class)).find(jobEnt.getIdConnection());
		TasksEntity plannerEnt = (ctx.getBean(TasksDao.class)).find(jobEnt.getIdScheduler());
		if (plannerEnt != null) {
			
			if(plannerEnt.getEverysecond() > 59) {
		    	if(plannerEnt.getWeek() > 0) {
		    		trigger =   triggerBuilder 
						    	.withIdentity(triggerName, "DEFAULT")
						    	.startAt(new Date())  
						    	.forJob(jobDetails)
						    	.withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
						    					.onDaysOfTheWeek(plannerEnt.getWeek())
						    					.withIntervalInSeconds(plannerEnt.getEverysecond()))
						    .build();
		    	} else {
		    		trigger = triggerBuilder 
					    .withIdentity(triggerName, "DEFAULT")
					    .startAt(new Date())  
					    .forJob(jobDetails) 
					    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
					    					.withIntervalInSeconds(plannerEnt.getEverysecond())
					    					.repeatForever())
					    .build();
		    	}
                log.info("Generated cron expression for jobId=" + jobId + ", plannerEnt: " + plannerEnt.getName() );
			} else {
				String cron = CronUtils.getCronMask(plannerEnt);
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
				trigger = triggerBuilder.build();
				log.info("Generated cron expression for jobId=" + jobId + ", is: " + cron);
			}
		}
		

			Map<String, String> jobData = new HashMap<String, String>();
			jobData.put("jobId", String.valueOf(jobId));
			jobData.put("connectionId", String.valueOf(jobEnt.getIdConnection()));
			jobData.put("schedulerId", String.valueOf(jobEnt.getIdScheduler()));
            jobData.put("schemaId", String.valueOf(jobEnt.getIdSchema()));
            jobData.put("eventTriggerId", String.valueOf(jobEnt.getIdEventTrigger()));
            if(plannerEnt.getEverysecond() > 59 && (plannerEnt.getDay() > 0 || plannerEnt.getMonth() > 0)) {
            	jobData.put("day", String.valueOf(plannerEnt.getDay()));
            	jobData.put("month", String.valueOf(plannerEnt.getMonth()));
            }
        if (connEnt != null) {
			jobData.put("serviceId", String.valueOf(connEnt.getService()));			
			jobData.put("ftpServerIp", connEnt.getHost());
			jobData.put("port", connEnt.getPort());
			jobData.put("userName", connEnt.getUserName());
			jobData.put("password", connEnt.getPassword());
			jobData.put("inputDirectory", connEnt.getDirectory());
			jobData.put("fileName", connEnt.getFileName());
        }
			// jobData.put("outputDirectory", "outputdir");
			jobDetails.getJobDataMap().putAll(jobData);

		scheduler.scheduleJob(jobDetails, trigger);
	}
	
	/**
	 * Stop current job.
	 * @throws SchedulerException 
	 * 
	 */
	public void unschedule() throws SchedulerException {
		scheduler.unscheduleJob(org.quartz.TriggerKey.triggerKey(triggerName, "DEFAULT"));			
	}
}
