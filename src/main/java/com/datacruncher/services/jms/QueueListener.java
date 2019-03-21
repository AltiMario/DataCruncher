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

package com.datacruncher.services.jms;

import com.datacruncher.constants.Servers;
import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.ServersEntity;
import com.datacruncher.spring.AppContext;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This is a JMS listener class that will listen the JMS queue "DataCruncherInputQueue"
 * "DataCruncherInputQueue" is a input queue for application for XML validation using JMS.
 *  Its works same as a MessageDrivenBean
 */

@Component
public class QueueListener implements MessageListener, DaoSet {
	
	/**
	 * This method will invoke when a XML request message land on DataCruncherInputQueue queue and after process the message
	 * It will return the result on queue DataCruncherOutputQueue
	 *  
	 * 
	 * Important Note - The body of XML message will be the xml message for validation and the JMSCorrelationID of the message
	 * would be the combination of schemaId and random number like schemaId-RandamNumber [10-213432] [here 10 is schemaId and 213432 is 
	 * random number]. The same JMSCorrelationID would be send back with the result message at DataCruncherOutputQueue so the user can
	 * Identify that result message are belongs to which request message.
	 * 
	 */
    Logger log = Logger.getLogger(this.getClass());
    
	public void onMessage(final Message message) {
		synchronized (this) {
			
			
			ServersEntity serverEntity = serversDao.find(Servers.JMS.getDbCode());
			
			if(serverEntity != null && serverEntity.getIsActive() == 0) {
								
				String jmsCorrelationID = null;
				String result = "This service is stopped";
				
				QueueSender sender = AppContext.getApplicationContext().getBean(QueueSender.class);
				sender.send(result, jmsCorrelationID);
				
				log.info("JMS service is stopped");
				
				return;
			} 
			if (message instanceof TextMessage) {
				final TextMessage textMessage = (TextMessage) message;
				String result = null ;
				String jmsCorrelationID = null;
				try {
					jmsCorrelationID = textMessage.getJMSCorrelationID();
					log.info("CorrelationID - " + jmsCorrelationID);
					log.info("Request Message is - " + textMessage.getText());
					Long schemaId = null;

					// Validate Schema Id from JMSCorrelationID
					if(textMessage.getJMSCorrelationID() == null || textMessage.getJMSCorrelationID().isEmpty()) {
						result = "Message could not proccess. CorrelationID is missing.";
					}else {
						// break the schema id
						// CorrelationId format is <UserID>-<SchemaId>-<10 digit random number>
						String correlationId = textMessage.getJMSCorrelationID();
						String[] headerData = correlationId.split("-");
						if (headerData.length != 3) {
							result = "Message could not proccess. invlaid CorrelationID detected.";
							return;
						}else{
							//String id = textMessage.getJMSCorrelationID().substring(0, textMessage.getJMSCorrelationID().indexOf("-"));
							try{
								long userId = Long.parseLong(headerData[0]);
								String sId = headerData[1];
								schemaId = Long.parseLong(sId);							
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
							}catch(Exception ex) {
								result = "CorrelationID does not cantains the valid Data.";
								return;
							}		
						}
					}				
					// Validate XML message 
					if(textMessage.getText() == null || textMessage.getText().isEmpty()){
						result = "Message could not proccess. Message body is empty or null.";
					}

					if( result == null ) {
						DatastreamsInput datastreamsInput = new DatastreamsInput ();
						result =  datastreamsInput.datastreamsInput (textMessage.getText() , schemaId, null);
					}				
				} catch (final JMSException e) {
					e.printStackTrace();
					result = "Exception occured during process the message with Correlation ID["+jmsCorrelationID+"] - " + e.getMessage();
				} catch (final Exception e) {
					e.printStackTrace();
					result = "Exception occured during process the message with Correlation ID["+jmsCorrelationID+"] - " + e.getMessage();
				}finally {
					QueueSender sender = AppContext.getApplicationContext().getBean(QueueSender.class);
					sender.send(result, jmsCorrelationID);
				}
			}
		}
	}
}