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
package com.seer.datacruncher.alerts.job;

import com.seer.datacruncher.alerts.AlertDispatcher;
import com.seer.datacruncher.alerts.AlertsData;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;

import java.util.List;

import org.apache.log4j.Logger;

public class AllFailureStreamsHandler extends AlertsDataHandler implements DaoSet {
	private Logger log = Logger.getLogger(AllFailureStreamsHandler.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.seer.datacruncher.alerts.AlertsDataHandler#handle(java.util.List)
	 */
	@Override
	public void handle(List<DatastreamEntity> dataStream) throws Exception {
		String logMsg = "AllFailureStreamsHandler:handle:";
		log.debug(logMsg + getAlertType());
		AlertDispatcher alertDispatcher;
		try {
			if (userList != null && userList.size() > 0) {
                dataStream=setSchemaName(dataStream);
				alertDispatcher = alertsDispatchMethods.get("mail");
				AlertsData alertsData = new AlertsData();
				alertsData.setDataStreams(dataStream);
				alertsData.setUsers(userList);
				alertsData.setAlertType(getAlertType());
                alertsData.setTotStream(getTotStream());
				boolean dispatchStatus = alertDispatcher.dispatchAlert(alertsData);
				log.debug(logMsg + "Dispatch Status :" + dispatchStatus);
			} else {
				log.debug(logMsg + " No Users found to process");
			}
		} catch (Exception e) {
			log.error(logMsg + "Exception:" + e.getMessage(), e);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}
    private List<DatastreamEntity>  setSchemaName(List<DatastreamEntity> dataStreams){
        try{
            DatastreamEntity  dataStream;
            for (int i = 0; i < dataStreams.size(); i++) {
                dataStream = dataStreams.get(i);
                String name= schemasDao.find(dataStream.getIdSchema()).getName();
                if(name == null)
                    name=dataStream.getIdSchema()+"";
                dataStream.setSchemaName(name);
                dataStreams.set(i,dataStream);

            }
            return dataStreams;
        }catch (Exception e) {
            return dataStreams;
        }
    }
}