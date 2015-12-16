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

package com.seer.datacruncher.services.scheduler;

import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.spring.ExpiredLicenseEmails;
import com.seer.datacruncher.utils.generic.CommonUtils;
import org.apache.log4j.Logger;
import org.quartz.*;

import java.util.HashMap;
import java.util.Map;

public class AlertsStarterJob {
    private Logger log = Logger.getLogger(this.getClass());
    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void executeInternal() {
        if (CommonUtils.isEEModule() || CommonUtils.isModule()) {
            String cron ="0 0 1 * * ?";
            JobKey jobKeyLicenseEmail = new JobKey("jobLicenseEmail", "group1");

            JobDetail jobLicenseEmail = JobBuilder.newJob(ExpiredLicenseEmails.class)
                    .withIdentity(jobKeyLicenseEmail).build();

            Trigger triggerLicenseEmail = TriggerBuilder
                    .newTrigger()
                    .withIdentity("dummyTriggerLicenseEmail", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            try {
                ExpiredLicenseEmails  expiredLicenseEmails = (ExpiredLicenseEmails)AppContext.getApplicationContext().getBean("expiredLicenseEmails");
                Map<String, Object> jobData = new HashMap<String, Object>();
                jobData.put("mailTemplate", expiredLicenseEmails.getMailTemplate());
                jobData.put("mailFrom", expiredLicenseEmails.getMailFrom());
                jobData.put("velocityEngine", expiredLicenseEmails.getVelocityEngine());
                jobLicenseEmail.getJobDataMap().putAll(jobData);
                scheduler.scheduleJob(jobLicenseEmail, triggerLicenseEmail);
                log.info("Generated cron expression for jobLicenseEmail , is: " + cron);
            } catch (SchedulerException e) {
                log.error("AlertsStarterJob: license planner's job can not be started");
            }
        }
    }

}
