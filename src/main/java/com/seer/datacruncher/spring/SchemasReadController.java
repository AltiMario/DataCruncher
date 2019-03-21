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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaTriggerStatusEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemasReadController implements Controller, DaoSet {
    
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity)session.getAttribute("user");
		if (user == null) {
			return null;
		}
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		
		if(start == null || limit == null) {
			start = "-1";
			limit = "-1";
		}
		ObjectMapper mapper = new ObjectMapper();
		long appId = -1;
		String appIds = request.getParameter("appIds");  
        String   paramIdSchemaType  = null;
        List<String> idSchemaTypeList = null;
		int idSchemaType = -1;
		
		if(appIds != null && (appIds.trim().length() == 0 || appIds.equals("-1"))) {
			appIds = null;
		}

        if (request.getParameter("idSchemaType") != null ){
            idSchemaType = -1;
            paramIdSchemaType  = request.getParameter("idSchemaType");
            if ((paramIdSchemaType.indexOf(",", 0)) < 0){
                // 1 condition
               idSchemaType = Integer.parseInt(request.getParameter("idSchemaType"));
            }else{
                //more condition
                idSchemaTypeList = Arrays.asList(StringUtils.splitPreserveAllTokens(paramIdSchemaType,","));
            }
        }
        String strAppId = request.getParameter("appId");
		if (strAppId != null && !strAppId.trim().isEmpty()) {
			appId = Integer.valueOf(strAppId);
			if (appId == 0) appId = -1;
		}
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		ReadList readList = null;

		if(user.getIdRole() == Roles.ADMINISTRATOR.getDbCode()){
             if(idSchemaTypeList != null){
                readList = schemasDao.readBySchemaTypeId(Integer.parseInt(start), Integer.parseInt(limit), idSchemaTypeList, appIds);
             }else{
              	readList = schemasDao.readBySchemaTypeId(Integer.parseInt(start), Integer.parseInt(limit), idSchemaType, appIds);
             }
		}else{
			readList = schemasDao.read(Integer.parseInt(start), Integer.parseInt(limit), user.getIdUser());
		}
				
		//FIXME: Check with Mario
		
		@SuppressWarnings("unchecked")
		List<SchemaEntity> SchemaEntities = (List<SchemaEntity>)readList.getResults();
		if(SchemaEntities != null && SchemaEntities.size() > 0) {
			for (SchemaEntity schemaEntity : SchemaEntities) {
				ReadList readTriggersList = schemaTriggerStatusDao.findByIdSchema(schemaEntity.getIdSchema());
				if(CollectionUtils.isNotEmpty(readTriggersList.getResults())){
					SchemaTriggerStatusEntity schemaTriggerStatusEntity = (SchemaTriggerStatusEntity) readTriggersList
						.getResults().get(0);
					schemaEntity.setSchemaEvents(schemaTriggerStatusEntity);
				}	
			}
		}
		
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}
	
	//---------------HELPERS-------------
	private List<Long> getAppIds(String applicationIds){
		String[] appIds = applicationIds.split(",");
		List<Long> aIdCol = new ArrayList<Long>(appIds.length);
		for (String appId : appIds) {
			aIdCol.add(Long.parseLong(appId));
		}
		return aIdCol;
	}
}