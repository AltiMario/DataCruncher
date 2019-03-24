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

package com.datacruncher.services.scheduler;

import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.JobsEntity;
import com.datacruncher.spring.AppContext;
import com.datacruncher.spring.DisposableSchedulerFactoryBean;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class PlannerStarterJob implements DaoSet {
	
    private static Logger log = Logger.getLogger(PlannerStarterJob.class);
    private PlannerJobMap plannerJobMap;
    
	public void setPlannerJobMap(PlannerJobMap plannerJobMap) {
		this.plannerJobMap = plannerJobMap;
	}

	public void executeInternal() {
		ApplicationContext appContext = AppContext.getApplicationContext();
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) appContext)
				.getBeanFactory();
		beanFactory.registerBeanDefinition("plannerScheduler",
				BeanDefinitionBuilder.genericBeanDefinition(DisposableSchedulerFactoryBean.class.getName())
						.getBeanDefinition());
		Scheduler scheduler = (Scheduler) beanFactory.getBean("plannerScheduler");	
		Map<Long, String> errors = new HashMap<Long, String>();
		ReadList read = jobsDao.read();
		for (Object o : read.getResults()) {
			JobsEntity entity = (JobsEntity)o;
			Long id = entity.getId();
			PlannerJob job = new PlannerJob(id, scheduler);		
			plannerJobMap.put(id, job);
			if (entity.getIsActive() == 1) {
				try {
					job.schedule();
				} catch (SchedulerException e) {
                    JobsEntity jobEntity = jobsDao.find(id);
                    jobEntity.setIsActive(0);
                    jobsDao.update(jobEntity);
					errors.put(id, "PlannerStarterJob: planner's job can not be started, job id: " + id);
					log.error("PlannerStarterJob: planner's job can not be started, job id: " + id);
				}
			}
		}
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("PlannerStarterJob: scheduler start exception", e);
		}		
    }
};