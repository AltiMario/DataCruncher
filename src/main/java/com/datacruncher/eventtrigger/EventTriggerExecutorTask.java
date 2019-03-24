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
import com.datacruncher.jpa.entity.EventTriggerEntity;
import com.datacruncher.utils.generic.CommonUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

public class EventTriggerExecutorTask implements Callable<Void>{
    private final Logger log = Logger.getLogger(this.getClass());
	private EventTriggerEntity entity;
    private DatastreamDTO datastreamDTO;
	
	public EventTriggerExecutorTask(EventTriggerEntity entity, DatastreamDTO datastreamDTO){
		this.entity = entity;
        this.datastreamDTO = datastreamDTO;
	}
	
	@Override
	public Void call() throws Exception {
		String className = entity.getName();
		String sourceCode = entity.getCode();
        EventTrigger eventTrigger;
        String response;
        eventTrigger = (EventTrigger)CommonUtils.getClassInstance(className,"EventTrigger",EventTrigger.class,sourceCode);
        assert eventTrigger != null;
        eventTrigger.setDatastreamDTO(this.datastreamDTO);
        response = eventTrigger.trigger();
		log.info("Response From EventTrigger("+className+") :"+response);
		return null;
	}
}
