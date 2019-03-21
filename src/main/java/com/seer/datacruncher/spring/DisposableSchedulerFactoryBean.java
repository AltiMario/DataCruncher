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

package com.seer.datacruncher.spring;

import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class DisposableSchedulerFactoryBean extends SchedulerFactoryBean {	
	
	/**
	 * This class was implemented to avoid SchedulerFactoryBean memory leaks that happen after tomcat shutdown. 
	 * Always use this class instead of SchedulerFactoryBean.
	 * 
	 * Explanations: http://forum.springsource.org/showthread.php?34672-Quartz-doesn-t-shutdown&p=370060#post370060
	 * 
	 * @author Stanislav	  
	 */
	
	@Override
	public void destroy() throws SchedulerException {
		if (this.getScheduler().isStarted()) {
			try {
				this.getScheduler().shutdown(true);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
