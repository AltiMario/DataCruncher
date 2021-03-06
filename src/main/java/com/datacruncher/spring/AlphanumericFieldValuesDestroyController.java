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
package com.datacruncher.spring;

import com.datacruncher.jpa.dao.DaoSet;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AlphanumericFieldValuesDestroyController implements Controller, DaoSet {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final AlphanumericFieldValuesDestroyPayload payload =
                mapper.readValue(request.getInputStream(), AlphanumericFieldValuesDestroyPayload.class);
        schemasXSDDao.destroy(payload.getIdSchema());
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        out.write(mapper.writeValueAsBytes(alphaFieldDao.destroy(payload.getIdAlphanumericFieldValue())));
        out.flush();
        out.close();
        return null;
    }
}