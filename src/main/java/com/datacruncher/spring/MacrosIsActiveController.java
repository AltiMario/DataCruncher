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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.utils.generic.I18n;

public class MacrosIsActiveController implements Controller, DaoSet {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long macroId = Long.parseLong(request.getParameter("macroId"));
        int isActive = Integer.parseInt(request.getParameter("isActive"));
        String resMsg = I18n.getMessage(isActive == 1 ? "success.macroActivated" : "success.macroDeactivated");
        Map<String, String> resMap = new HashMap<String, String>();
        macrosDao.setActive(macroId, isActive);
        long schemaId = macrosDao.find(macroId).getIdSchema();
        boolean isSuccess = true;
        if (schemasXSDDao.find(schemaId) != null) {
            schemasXSDDao.destroy(schemaId);
        }
        resMap.put("success", String.valueOf(isSuccess));
        resMap.put("msg", resMsg);
        response.getWriter().print(new JSONObject(resMap).toString());
        return null;
    }
}