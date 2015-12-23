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

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.persistence.manager.DBConnectionChecker;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 
public class ApplicationConfigCreateController implements Controller, DaoSet {

	@PersistenceContext
    private EntityManager em;
	
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
		
		Create create = new Create();
				
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();	
		
		String configType = request.getParameter("configType");
		
		if(configType.equals("database")) {
			if(checkDatabaseConnection(request)) {

                ApplicationConfigEntity appConfigEntity = new ApplicationConfigEntity();
                appConfigEntity.setConfigType(ApplicationConfigType.DATABASE);
				appConfigEntity.setDatabaseName(request.getParameter("databaseName"));
				appConfigEntity.setHost(request.getParameter("host"));
				appConfigEntity.setIdDatabaseType(Integer.parseInt(request.getParameter("idDatabaseType")));
				appConfigEntity.setPassword(request.getParameter("password"));
				appConfigEntity.setPort(request.getParameter("port"));
				appConfigEntity.setUserName(request.getParameter("userName"));
				try {
					Configuration conf = new Configuration().configure();
					conf.setProperty("hibernate.connection.url", em.getEntityManagerFactory().getProperties().get("hibernate.connection.url") + "?relaxAutoCommit=" + em.getEntityManagerFactory().getProperties().get("hibernate.connection.autocommit"));
					new SchemaExport(conf).create(true, true);
				} catch(Exception ex) {
					//ex.printStackTrace();
				}
				
				create = applicationConfigDao.create(appConfigEntity);				
			} else {
				create.setMessage(I18n.getMessage("error.databaseConnectionError"));
				create.setSuccess(false);
			}
		} else if(configType.equals("userProfile")) {
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			long idAlert = Long.parseLong(request.getParameter("idAlert").isEmpty() ? "0" : request.getParameter("idAlert"));
			String surname = request.getParameter("surname");
			String language = request.getParameter("language");
			String dob = request.getParameter("dob");
			Date date = null;
			
			if(dob != null && dob.trim().length() > 0) {
				try {
					date = new SimpleDateFormat("dd/MMM/yyyy").parse(dob);				
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			UserEntity userEntity = new UserEntity(userName, password, name, surname, email, 1, 1, language, idAlert, -1, date, "classic");
			create = usersDao.create(userEntity);
			create.setMessage(I18n.getMessage("success.userProfileSaved"));
			
		} else if(configType.equals("ftp")) {
            ApplicationConfigEntity appConfigEntity = new ApplicationConfigEntity();
			appConfigEntity.setConfigType(ApplicationConfigType.FTP);			
			appConfigEntity.setUserName(request.getParameter("userName"));
			appConfigEntity.setPassword(request.getParameter("password"));			
			appConfigEntity.setInputDir(request.getParameter("inputDirectory"));
			appConfigEntity.setOutputDir(request.getParameter("outputDirectory"));

			create = applicationConfigDao.create(appConfigEntity);		
			create.setMessage(I18n.getMessage("success.ftpConfigSaved"));
		} else if(configType.equals("email")) {
            ApplicationConfigEntity appConfigEntity = new ApplicationConfigEntity();
            String serverUrl= getServerURL(request.getRequestURL().toString());
            if(!serverUrl.equals("")) {
                appConfigEntity.setConfigType(ApplicationConfigType.APPLURL);
                appConfigEntity.setHost(serverUrl);

                System.out.println(request.getRequestURL().toString());
                create = applicationConfigDao.create(appConfigEntity);

                appConfigEntity = new ApplicationConfigEntity();
            }
			appConfigEntity.setConfigType(ApplicationConfigType.EMAIL);			
			appConfigEntity.setUserName(request.getParameter("userName"));
			appConfigEntity.setPassword(request.getParameter("password"));			
			appConfigEntity.setHost(request.getParameter("host"));
			appConfigEntity.setPort(request.getParameter("port"));
			appConfigEntity.setProtocol(request.getParameter("protocol"));
			appConfigEntity.setEncoding(request.getParameter("encoding"));
			appConfigEntity.setSmtpsTimeout(request.getParameter("smtpstimeout"));
			appConfigEntity.setIsStarTtls(Integer.parseInt(request.getParameter("starttls") == null ? "0" : request.getParameter("starttls")));
			appConfigEntity.setIsSmtpsAuthenticate(Integer.parseInt(request.getParameter("smtpsAuthenticate") == null ? "0" : request.getParameter("smtpsAuthenticate")));
			
			create = applicationConfigDao.create(appConfigEntity);		
			create.setMessage(I18n.getMessage("success.emailConfigSaved"));
		}
		
		ObjectMapper mapper = new ObjectMapper();				
		
		out.write(mapper.writeValueAsBytes(create));
		out.flush();
		out.close();
 		
 		return null;
	}
	private boolean checkDatabaseConnection(HttpServletRequest request) {
		
		try {		
			int idDatabaseType = Integer.parseInt(request.getParameter("idDatabaseType"));
			String host = request.getParameter("host");
			int port = Integer.parseInt(request.getParameter("port"));
			String databaseName = request.getParameter("databaseName");
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			
			String dbName = getDatabaseTypeName(idDatabaseType);
			
			DBConnectionChecker dbConn = new DBConnectionChecker(dbName, host, port, databaseName, userName, password);
			dbConn.useSession();
						
			return dbConn.getStatus();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	private String getDatabaseTypeName(int idDatabaseType) {
		
		String dbName = "";
		
		if (idDatabaseType == 1) {
			dbName = "MySql";
		} else if (idDatabaseType == 2) {
			dbName = "Oracle";
		} else if (idDatabaseType == 3) {
			dbName = "SQL Server";
		} else if (idDatabaseType == 4) {
			dbName = "PostgreSQL";
		} else if (idDatabaseType == 5) {
			dbName = "DB2";
		} else if (idDatabaseType == 6) {
			dbName = "SQLite";
		} else if (idDatabaseType == 7) {
			dbName = "Firebird";
		} else if (idDatabaseType == 8) {
			dbName = "SAPDB";
		} else if (idDatabaseType == 9) {
			dbName = "HSQLDB";
		}
		
		return dbName;
	}
    private String getServerURL(String requestURL) {

        if(requestURL == null || requestURL.trim().length() == 0) {
            return "";
        }

        String serverUrl = "";
        String strTemp = requestURL;
        try {

            if (strTemp.indexOf("/") != -1) {
                int idx = strTemp.lastIndexOf("/") ;
                serverUrl = strTemp.substring(0, idx+1);
            }
        } catch (Exception ex) {
            serverUrl = "";
        }
        return serverUrl;
    }
}