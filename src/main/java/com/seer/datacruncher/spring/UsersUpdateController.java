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

import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserApplicationsEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.jpa.entity.UserSchemasEntity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class UsersUpdateController implements Controller, DaoSet {
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String json = request.getReader().readLine();
		ObjectMapper mapper = new ObjectMapper();
		UserEntity userEntity = new UserEntity();
		userEntity = getUserEntity(json);
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		usersDao.destroyUserApps(userEntity.getIdUser());
		usersDao.destroyUserSchemas(userEntity.getIdUser());
		out.write(mapper.writeValueAsBytes(usersDao.update(userEntity)));
		out.flush();
		out.close();
	
		UserEntity sessionInstance = (UserEntity)request.getSession().getAttribute("user");
		sessionInstance.setTheme(userEntity.getTheme());
		request.getSession().setAttribute("user", sessionInstance);
		return null;
	}
	//-------------------HELPERS---------------
	
	private UserEntity getUserEntity(String json){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		JsonFactory factory = new MappingJsonFactory(mapper);
		JsonParser jsonParser;
		JsonToken currentToken;
		UserEntity userEntity = new UserEntity();
		try {
			jsonParser = factory.createJsonParser(json);
			currentToken = jsonParser.nextToken();
		    if (currentToken != JsonToken.START_OBJECT) {
		      //return;
		    }
		    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
		    	String fieldName = jsonParser.getCurrentName();
		    	currentToken = jsonParser.nextToken();
		    	if("idUser".equalsIgnoreCase(fieldName)){
		    		userEntity.setIdUser(jsonParser.readValueAsTree().getLongValue());
		    	} else if("userName".equalsIgnoreCase(fieldName)){
		    		userEntity.setUserName(jsonParser.readValueAsTree().getTextValue());
		    	} else if("name".equalsIgnoreCase(fieldName)){
		    		userEntity.setName(jsonParser.readValueAsTree().getTextValue());
		    	} else if("password".equalsIgnoreCase(fieldName)){
		    		userEntity.setPassword(jsonParser.readValueAsTree().getTextValue());
		    	}else if("surname".equalsIgnoreCase(fieldName)){
		    		userEntity.setSurname(jsonParser.readValueAsTree().getTextValue());
		    	} else if("email".equalsIgnoreCase(fieldName)){
		    		userEntity.setEmail(jsonParser.readValueAsTree().getTextValue());
		    	} else if("language".equalsIgnoreCase(fieldName)){
		    		userEntity.setLanguage(jsonParser.readValueAsTree().getTextValue());
		    	} else if("idRole".equalsIgnoreCase(fieldName)){
		    		userEntity.setIdRole(jsonParser.readValueAsTree().getLongValue());
		    	} else if("idAlert".equalsIgnoreCase(fieldName)){
		    		userEntity.setIdAlert(jsonParser.readValueAsTree().getLongValue());
		    	}else if("enabled".equalsIgnoreCase(fieldName)){
		    		userEntity.setEnabled(jsonParser.readValueAsTree().getIntValue());
		    	}else if("createdBy".equalsIgnoreCase(fieldName)){
		    		userEntity.setCreatedBy(jsonParser.readValueAsTree().getLongValue());
		    	}else if("dateOfBirth".equalsIgnoreCase(fieldName)){
		    		String dateStr = jsonParser.readValueAsTree().getTextValue();
		    		Date date = null;
		    		if(dateStr!=null && dateStr.length()>0){
		    			try {
							date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
						}
		    		}
		    		userEntity.setDateOfBirth(date);
		    	} else if ("userApplications".equalsIgnoreCase(fieldName)) {
		    		List<UserApplicationsEntity> userApplications = new ArrayList<UserApplicationsEntity>();
		    		UserApplicationsEntity userApplicationsEntity = null;
		    		if (currentToken == JsonToken.START_ARRAY) {
		    			 while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
		    				 userApplicationsEntity = new UserApplicationsEntity();
		    				 long applicationId = jsonParser.readValueAsTree().getLongValue();
		    				 userApplicationsEntity.setIdApplication(applicationId);
		    				 userApplicationsEntity.setIdUser(userEntity.getIdUser());
		    				 userApplications.add(userApplicationsEntity);
		    				 userApplicationsEntity = null;
		    			 }
		    		 }
		    		userEntity.setUserApplications(userApplications);
		    	}	else if ("userSchemas".equalsIgnoreCase(fieldName)) {
		    		List<UserSchemasEntity> userSchemas = new ArrayList<UserSchemasEntity>();
		    		UserSchemasEntity userSchemasEntity = null;
		    		if (currentToken == JsonToken.START_ARRAY) {
		    			 while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
		    				 userSchemasEntity = new UserSchemasEntity();
		    				 long schemaId = jsonParser.readValueAsTree().getLongValue();
		    				 userSchemasEntity.setIdSchema(schemaId);
		    				 userSchemasEntity.setIdUser(userEntity.getIdUser());
		    				 userSchemas.add(userSchemasEntity);
		    				 userSchemasEntity = null;
		    			 }
		    		 }
		    		userEntity.setUserSchemas(userSchemas);
		    	} else if("theme".equalsIgnoreCase(fieldName)){
		    		userEntity.setTheme(jsonParser.readValueAsTree().getTextValue());
		    	}
		    }
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userEntity;
	}
}