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

package com.datacruncher.services;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import java.net.URL;

/*
An example for sending a datastream to our webservice
*/

public class SendToWebservice {
    public static void main(String[] args) {
        try {
            String url = "http://localhost:8080/services/DatastreamsInputWS";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(url));
            call.setOperationName("datastreamsInputWS");
            call.setPassword("admin");
            call.setUsername("pwd"); //authorize the user (dispatcher) for that schema
            Object[] params = new Object[2];
            params[0] =
                    "1;2;6\n" +
                    "3;4;5\n" +
                    "4;;6"; //csv as example
            params[1] = (long) 1;  //id schema
            Object result = call.invoke(params);
            System.out.println("Result : " + result.toString());
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}