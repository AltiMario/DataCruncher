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
package com.datacruncher.alerts.job;

import com.datacruncher.spring.AppContext;
import com.datacruncher.jpa.entity.DatastreamEntity;
import com.datacruncher.jpa.entity.UserEntity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AlertsForEveryTimeTask implements Runnable {

	private Logger log = Logger.getLogger(this.getClass());
	private DatastreamEntity dataStreamEntity;
    private List<UserEntity> users;
	

	public AlertsForEveryTimeTask(DatastreamEntity dataStreamEntity,List<UserEntity> users) {
		this.dataStreamEntity = dataStreamEntity;
        this.users = users;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String logMsg = "AlertsForEveryTimeTask:call:";
		log.debug(logMsg + " idDatastream:" + dataStreamEntity.getIdDatastream());
		AlertsJobDataProcessor alertsJobDataProcessor;
		try {
            List<DatastreamEntity> dataStream = new ArrayList<DatastreamEntity>();
                dataStream.add(dataStreamEntity);
			alertsJobDataProcessor = AppContext.getApplicationContext()
					.getBean(AlertsJobDataProcessor.class);
			alertsJobDataProcessor.setAlertType("EveryTime");
            alertsJobDataProcessor.setUserList(users);
			alertsJobDataProcessor.processJobData(dataStream);
		} catch (Exception exception) {
			log.error(logMsg + "Exception:" + exception,exception);
		}
	}
}