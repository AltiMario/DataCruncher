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

package com.datacruncher.services;

import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

/*
* This class for testing purpose for get the result from JMS queue "DataCruncherOutputQueue"
*/

public class JmsGetMessage implements MessageListener
{
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private String USERNAME = "admin";
    private String PASSWORD = "admin";

    Logger log = Logger.getLogger(this.getClass());

    public static void main( String[] args )
    {
        JmsGetMessage jmsGetMessageTest = new JmsGetMessage();
        jmsGetMessageTest.run();
    }

    public void run()
    {
        try
        {
            // DataCruncherOutputQueue is the queue where application post the result messages
            String DataCruncherInputQueue = "DataCruncherOutputQueue" ;

            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            env.put(Context.PROVIDER_URL, "tcp://localhost:61616");

            InitialContext jndiContext = new InitialContext(env);
            connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connection = connectionFactory.createConnection(USERNAME,PASSWORD);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(DataCruncherInputQueue);

            consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onMessage(Message message)
    {
        try
        {
            if (message instanceof TextMessage)
            {
                TextMessage txtMessage = (TextMessage)message;
                System.out.println("Message Correlation Id: " + txtMessage.getJMSCorrelationID());
                System.out.println("Message received: " + txtMessage.getText());
            }
            else
            {
                System.out.println("Invalid message received.");
            }
        }
        catch (JMSException e)
        {
            System.out.println("Critical ERROR: "+e.getMessage());
        }
    }

}
