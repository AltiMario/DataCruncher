/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.utils.generic.I18n;

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