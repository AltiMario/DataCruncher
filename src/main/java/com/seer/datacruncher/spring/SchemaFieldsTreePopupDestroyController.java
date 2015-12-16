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
 */

package com.seer.datacruncher.spring;

import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.AlphanumericFieldValuesEntity;
import com.seer.datacruncher.jpa.entity.NumericFieldValuesEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaFieldsTreePopupDestroyController implements Controller, DaoSet {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idSchemaField = request.getParameter("idSchemaField");
        
        SchemaFieldEntity schemaFieldEntity = schemaFieldsDao.find(Long.parseLong(idSchemaField));
        SchemaEntity schemaEntity = schemasDao.find(schemaFieldEntity.getIdSchema());
        schemaEntity.setIsActive(0);
        schemasDao.update(schemaEntity);        
        
        delete(Long.parseLong(idSchemaField));
        Destroy destroy = new Destroy();
        destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));	
		
        ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(destroy));
		out.flush();
		out.close();
        return null;
    }

    public void delete(long idSchemaField) {
    	
        SchemaFieldEntity schemaFieldEntityObj = schemaFieldsDao.find(idSchemaField);
        if (schemaFieldEntityObj != null && schemaFieldEntityObj.getIdSchema() != 0) {
            schemasXSDDao.destroy(schemaFieldEntityObj.getIdSchema());
        }

        schemaFieldsDao.destroy(idSchemaField);
        deleteSchemaValues(idSchemaField);
        ArrayList<SchemaFieldEntity> listChild = (ArrayList<SchemaFieldEntity>) schemaFieldsDao
                .listAllChild(idSchemaField);
        for (int cont = 0; cont < listChild.size(); cont++) {
            delete(listChild.get(cont).getIdSchemaField());
        }
    }
    
    public void deleteSchemaValues(long idSchemaField) {
    	
    	List<AlphanumericFieldValuesEntity> listAlphaNumericFields = alphaFieldDao.listAlphanumericFieldValues(idSchemaField);
		if (listAlphaNumericFields != null && listAlphaNumericFields.size() > 0) {
			for (AlphanumericFieldValuesEntity alpha : listAlphaNumericFields) {
				alphaFieldDao.destroy(alpha.getIdAlphanumericFieldValue());
			}
		}
		
		List<NumericFieldValuesEntity> listNumericFields = numericFieldDao.listNumericFieldValues(idSchemaField);
		if (listNumericFields != null && listNumericFields.size() > 0) {
			for (NumericFieldValuesEntity numeric : listNumericFields) {
				numericFieldDao.destroy(numeric.getIdNumericFieldValue());
			}
		}
    }
}