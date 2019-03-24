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
package com.datacruncher.eventtrigger;

import com.datacruncher.datastreams.DatastreamDTO;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.EventTriggerEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class EventTriggerBuilder implements DaoSet {
    private static Logger log = Logger.getLogger(EventTriggerBuilder.class.getName());
    public static void getEventTrigger(List<EventTriggerEntity> eventList, DatastreamDTO datastreamDTO){
    	if(CollectionUtils.isNotEmpty(eventList)){
    		for (EventTriggerEntity eventTriggerEntity : eventList) {
    			try {
    				EventTriggerExecutorTask eventTriggerExecutorTask= new EventTriggerExecutorTask(eventTriggerEntity, datastreamDTO);
    				eventTriggerExecutorTask.call();
				} catch (Exception e) {
					e.printStackTrace();
                    log.error("EventTriggerBuilder error:"+ e.getMessage(),e);
                    logDao.setErrorLogMessage("Error in Trigger building:"+e.getMessage());
				}
			}
    	}
    }
}
