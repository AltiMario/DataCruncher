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

package com.datacruncher.services.jms;

import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JMSQueueListenerProcessor implements Processor, DaoSet {
	private Logger log = Logger.getLogger(JMSQueueListenerProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		String logMsg = "JMSQueueListenerProcessor:process:";
		log.debug(logMsg+"Entry");
		String jmsCorrelationID = null;
		String message = null;
		String result = null;
		try {
			message = (String)exchange.getIn().getBody();
			jmsCorrelationID = (String)exchange.getIn().getHeader("JMSCorrelationID");
			log.debug(logMsg+"JMSCorreclationID:"+jmsCorrelationID);
			log.debug(logMsg+"Message:"+message);
			
			if(StringUtils.isEmpty(jmsCorrelationID)) {
				result = "Message could not proccess. CorrelationID is missing.";
				return;
			}
			
			String[] headerData = jmsCorrelationID.split("-");
			if (headerData != null && headerData.length != 3) {
				result = "Message could not proccess. invlaid CorrelationID detected.";
				return;
			}
			
			long userId = 0;
			long schemaId = 0;
			try {
				userId = Long.parseLong(headerData[0]);
				schemaId = Long.parseLong(headerData[1]);
				if(!usersDao.isUserAssoicatedWithSchema(userId, schemaId)){
					result = "User not authorized";
					return;
				}else{
					SchemaEntity schemaEntity = schemasDao.find(schemaId);	
					if(schemaEntity == null){
						result = "No schema found with id["+schemaId+"].";
						return;
					}
				}
			} catch (Exception ex) {
				result = "CorrelationID does not cantains the valid Data.";
				return;
			}
			
			// validate Message
			if(StringUtils.isEmpty(message)){
				result = "Message could not proccess. Message body is empty or null.";
			}
			
			// If all data set call for processing
			if (result == null) {
				DatastreamsInput datastreamsInput = new DatastreamsInput ();
				result =  datastreamsInput.datastreamsInput (message , schemaId, null);
			}
			System.out.println("Result:"+result);			
		} catch (Exception e) {
			result = "Exception occured during process the message with Correlation ID["+jmsCorrelationID+"] - " + e.getMessage();
		} finally {
			log.debug(logMsg+"Exit");
		}
	}
}
