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

package com.datacruncher.spring;

import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.Validate;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ValidateDatastreamController implements Controller {

    Logger log = Logger.getLogger(this.getClass());

    static String mashapeDataCruncherMethod = "validateDataStream";

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idSchema = request.getParameter("idSchema");
        String dataStream = request.getParameter("dataStream");

        try {
            ServletOutputStream out;

            DatastreamsInput datastreamsInput = new DatastreamsInput ();
            datastreamsInput.datastreamsInput(dataStream , Long.parseLong(idSchema), null);

            Validate instance = new Validate();
            instance.setMessage(datastreamsInput.getResultMsg());
            instance.setSuccess(datastreamsInput.getTotalSuccess());

            response.setContentType("application/json");
            out = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            out.write(mapper.writeValueAsBytes(instance));
            out.flush();
            out.close();

        } catch (Exception e) {
            log.error("Error while handling request for validating datastream",e);
        }

        return null;
    }
}
