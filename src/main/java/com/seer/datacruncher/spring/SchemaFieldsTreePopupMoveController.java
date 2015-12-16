/*
 * Copyright (c) 2015  www.see-r.com
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
 */package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaFieldsTreePopupMoveController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long idSchemaField = Long.valueOf(request.getParameter("idSchemaField"));
		long idNewParent = Long.valueOf(request.getParameter("idNewParent"));
		long idOldParent = Long.valueOf(request.getParameter("idOldParent"));		
		int elementOrder = Integer.parseInt(request.getParameter("elementOrder"));
		SchemaFieldEntity schemaFieldEntity = schemaFieldsDao.find(idSchemaField);
		Map<String, String> resMap = new HashMap<String, String>();
		String success = "true";		
		long idSchema = schemaFieldEntity.getIdSchema();
		if (idNewParent == 0) {
			SchemaEntity schEnt = schemasDao.find(idSchema);
			if (schEnt.getIdStreamType() == StreamType.XML || schEnt.getIdStreamType() == StreamType.XMLEXI) {
				success = "false";					
			}
		}
		if (success.equals("true")) {
			schemaFieldsDao.decrementUpperElementOrder(idSchema, idOldParent, schemaFieldEntity.getElementOrder());
			schemaFieldsDao.incrementUpperElementOrder(idSchema, idNewParent, elementOrder);
			schemasXSDDao.destroy(idSchema);
			schemaFieldEntity.setIdParent(idNewParent);
			schemaFieldEntity.setElementOrder(elementOrder);
			int oldParentType = schemaFieldsDao.find(idOldParent) == null ? -1 : schemaFieldsDao.find(idOldParent).getIdFieldType() ;
            if(idOldParent >0 && idNewParent > 0 ){
                int newParentType = schemaFieldsDao.find(idNewParent) == null ? -1 : schemaFieldsDao.find(idNewParent).getIdFieldType() ;
                if (oldParentType != newParentType) {
                    if (newParentType  == FieldType.all) {
                        schemaFieldEntity.setMaxOccurs(1);
                    } else{
                        schemaFieldEntity.setMaxOccurs(0);
                    }
                }
            }

			schemaFieldsDao.update(schemaFieldEntity);
		}
		resMap.put("success", success);
		response.getWriter().print(new JSONObject(resMap).toString());		
		return null;
	}
}