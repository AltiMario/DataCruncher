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

package com.seer.datacruncher.services;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/*
* This class for testing purpose to post the message on server for validation.
*/

public class JmsPostMessage {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private String USERNAME = "admin";
    private String PASSWORD = "admin";
    /**
     * @param args
     * @throws NamingException
     * @throws JMSException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

        // Important Note - This class is a sample client code to post the message and its not a functional part of
        // DataCruncher application. You might face error if you directly run this file.
        // Solution - Either took this file and run in same other eclipse project or if you want to run it from
        // same application than remove the entries from jndi.properties.

        long schemaId = 13;
        long userId = 1;
        String xmlStringMsg  = "<root><name>Kunal Gandhi</name></root>";
        for(int i= 0 ; i < 1 ; i++){
            new JmsPostMessage().sendMessageToServer(userId, schemaId, xmlStringMsg);
            Thread.sleep(100);
        }
    }

    private void sendMessageToServer (long userId,long schemaId , String xmlStringMsg) throws NamingException, JMSException {
        // Queue Name that feed the XML message to system.

        try{
            String DataCruncherInputQueue = "DataCruncherInputQueue" ;

            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            env.put(Context.PROVIDER_URL, "tcp://localhost:61616");

            InitialContext jndiContext = new InitialContext(env);
            connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connection = connectionFactory.createConnection(USERNAME,PASSWORD);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(DataCruncherInputQueue);
            producer = session.createProducer(destination);

            Message message = session.createTextMessage(xmlStringMsg);
            message.setJMSCorrelationID(userId+"-"+schemaId+"-"+System.currentTimeMillis());
            producer.send(message);

        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            // Close the resources after send the message
            if(producer != null)
                producer.close();
            if(session != null)
                session.close();
            if(connection != null)
                connection.close();
        }
    }

}
