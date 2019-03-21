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
 */package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.constants.Roles;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaTriggerStatusEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.jpa.entity.UserSchemasEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemasCreateController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (user == null) {
			return null;
		}
		String json = request.getReader().readLine();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		SchemaEntity schemaEntity = mapper.readValue(json, SchemaEntity.class);
		schemaEntity.setDescription(schemaEntity.getDescription().replace('\u200b', ' '));
		/*if(schemaEntity.getPublishToDb() == false){
			schemaEntity.setIdDatabase(0);
		}
        if(schemaEntity.getInputToDb() == false){
            schemaEntity.setIdValidationDatabase(0);
        }*/

		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
		Create create = schemasDao.create(schemaEntity);
        SchemaTriggerStatusEntity schemaTriggerStatusEntity = schemaEntity.getSchemaEvents();
        if(create.getSuccess() && schemaTriggerStatusEntity.getIdEventTrigger()>0){
            schemaTriggerStatusEntity.setIdSchema(schemaEntity.getIdSchema());
            create = schemaTriggerStatusDao.create(schemaTriggerStatusEntity);
            schemaEntity.setSchemaEvents(schemaTriggerStatusEntity);
        }
        if(create.getSuccess()){

            if (user.getIdRole() != Roles.ADMINISTRATOR.getDbCode()) {
                if (hasCreateRole(user)) {
                    UserSchemasEntity userSchemasEntity = new UserSchemasEntity();
                    userSchemasEntity.setIdSchema(schemaEntity.getIdSchema());
                    userSchemasEntity.setIdUser(user.getIdUser());
                    create = userSchemasDao.create(userSchemasEntity);
                }
            }
        }
		out.write(mapper.writeValueAsBytes(create));
		out.flush();
		out.close();
		return null;
	}

	// ---------------------HELPERS------------------------------
	private boolean hasCreateRole(UserEntity user) {
		List<String> userRoleActivities = user.getRoleActivities();
		for (String role : userRoleActivities) {
			if (role.equals(Activity.SCHEMA_ADD.getScriptCode())) {
				return true;
			}
		}
		return false;
	}
}