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

import com.datacruncher.constants.FieldType;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaFieldsTreePopupCreateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idSchema = request.getParameter("idSchema");
		String idParent = request.getParameter("idParent");
		String isAttribute = request.getParameter("isAttribute");
		String leaf = request.getParameter("leaf");
		ObjectMapper mapper = new ObjectMapper();
		SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
		schemaFieldEntity.setIdParent(Long.parseLong(idParent));
		schemaFieldEntity.setIdSchema(Long.parseLong(idSchema));
		
		if ("1".equals(isAttribute) || "true".equals(isAttribute)) {
			schemaFieldEntity.setIs_Attribute(true);			
		} else {
			schemaFieldEntity.setIs_Attribute(false);			
		}
		
		if (leaf.equals("true")) {
			schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
			schemaFieldEntity.setNillable(false);
			schemaFieldEntity.setIdCheckType(0);
			schemaFieldEntity.setIdAlign(1);
			schemaFieldEntity.setFillChar(" ");
			
            if (Long.parseLong(idParent) > 0){
                if (schemaFieldsDao.find(Long.parseLong(idParent)).getIdFieldType()  == FieldType.all) {
                    schemaFieldEntity.setMaxOccurs(1);
                } else{
                    schemaFieldEntity.setMaxOccurs(0);
                }
            }else{
                schemaFieldEntity.setMaxOccurs(1);
            }

		} else {
			schemaFieldEntity.setIdFieldType(FieldType.all);
		}
		schemaFieldEntity.setElementOrder(schemaFieldsDao.getMaxOrderInLevel(schemaFieldEntity.getIdSchema(),
				schemaFieldEntity.getIdParent()) + 1);
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(("{idSchemaField : '" + mapper.writeValueAsString(schemaFieldsDao.create(schemaFieldEntity)) + "'}")
				.getBytes());
		out.flush();
		out.close();
		return null;
	}
}