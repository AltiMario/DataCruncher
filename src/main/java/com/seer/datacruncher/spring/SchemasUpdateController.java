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

import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaTriggerStatusEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemasUpdateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String json = request.getReader().readLine();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        SchemaEntity schemaEntity = mapper.readValue(json, SchemaEntity.class);
		
        if(schemaEntity.getIdDatabase() > 0) {
        	schemaEntity.setPublishToDb(true);
        }
        if(schemaEntity.getIdValidationDatabase() > 0) {
        	schemaEntity.setInputToDb(true);
        }
        
        SchemaTriggerStatusEntity newSchemaTriggerStatusEntity = schemaEntity.getSchemaEvents();
        Create createTrigger = new Create();
        createTrigger.setSuccess(true);
        createTrigger.setMessage("");
        Update update;

        ReadList readTriggersList = schemaTriggerStatusDao.findByIdSchema(schemaEntity.getIdSchema());
        if(CollectionUtils.isNotEmpty(readTriggersList.getResults())){
            if(schemaEntity.getIsEventTrigger()){
                SchemaTriggerStatusEntity schemaTriggerStatusEntity = (SchemaTriggerStatusEntity) readTriggersList
                        .getResults().get(0);
                if(schemaTriggerStatusEntity.getIdEventTrigger() != newSchemaTriggerStatusEntity.getIdEventTrigger()||
                        schemaTriggerStatusEntity.getIdStatus() !=  newSchemaTriggerStatusEntity.getIdStatus()
                        ){
                    schemaTriggerStatusEntity.setIdEventTrigger(newSchemaTriggerStatusEntity.getIdEventTrigger());
                    schemaTriggerStatusEntity.setIdStatus(newSchemaTriggerStatusEntity.getIdStatus());
                    update = schemaTriggerStatusDao.update(schemaTriggerStatusEntity);
                    if(update.isSuccess()){
                        schemaEntity.setSchemaEvents(schemaTriggerStatusEntity);
                    }else{
                        createTrigger.setSuccess(false);
                        createTrigger.setMessage("Event update error:"+ update.getMessage());
                    }
                }

            }else{
                Destroy destroy = schemaTriggerStatusDao.destroyEventsBySchema(schemaEntity.getIdSchema());
                if(!destroy.isSuccess()){
                    createTrigger.setSuccess(false);
                    createTrigger.setMessage("Event delete error:"+ destroy.getMessage());
                }
            }
        }else{
            if(schemaEntity.getIsEventTrigger()){
                newSchemaTriggerStatusEntity.setIdSchema(schemaEntity.getIdSchema());
                createTrigger = schemaTriggerStatusDao.create(newSchemaTriggerStatusEntity);
                if(createTrigger.getSuccess())
                    schemaEntity.setSchemaEvents(newSchemaTriggerStatusEntity);
            }
        }

        if(createTrigger.getSuccess()){
		    update = schemasDao.update(schemaEntity);
        }else{
            update = new Update();
            update.setSuccess(createTrigger.getSuccess());
            update.setMessage("Event "+ createTrigger.getMessage());
        }
	
		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
		out.write(mapper.writeValueAsBytes(update));
		out.flush();
		out.close();
		return null;
	}
}