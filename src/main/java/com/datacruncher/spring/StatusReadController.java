/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

package com.datacruncher.spring;


import com.datacruncher.persistence.manager.DBConnectionChecker;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.DatabaseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class StatusReadController implements Controller, DaoSet {
	
	private List<DBConnectionChecker> list = new ArrayList<DBConnectionChecker>();
	
	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {

		//create a new list of database configurations from the request
		List<DBConnectionChecker> newlist = new ArrayList<DBConnectionChecker>();
		
		if(request.getParameter("count") != null) {
			
			int count = Integer.parseInt(request.getParameter("count"));
		
		
			for(int i = 0; i < count; i++) {
				String var_port = request.getParameter("port" + i);
				if (var_port == "" ){
					var_port = "0" ;
					}
				newlist.add(new DBConnectionChecker(
					request.getParameter("idDatabaseType" + i), 
					request.getParameter("host" + i), 
					Integer.parseInt(var_port), 
					request.getParameter("databaseName" + i), 
					request.getParameter("userName" + i),
					request.getParameter("password" + i)
					));
			}
		} else if(request.getParameter("idDatabase") != null) {
			DatabaseEntity databaseEntity = dbDao.find(Long.parseLong(request.getParameter("idDatabase")));
			String databaseType = "";
			if(databaseEntity.getIdDatabaseType() == 1) {
				databaseType = "MySql";
			} else if(databaseEntity.getIdDatabaseType() == 2) {
				databaseType = "Oracle";
			} else if(databaseEntity.getIdDatabaseType() == 3) {
				databaseType = "SQL Server";
			} else if(databaseEntity.getIdDatabaseType() == 4) {
				databaseType = "PostgreSQL";
			} else if(databaseEntity.getIdDatabaseType() == 5) {
				databaseType = "DB2";
			} else if(databaseEntity.getIdDatabaseType() == 6) {
				databaseType = "SQLite";
			} else if(databaseEntity.getIdDatabaseType() == 7) {
				databaseType = "Firebird";
			} else if(databaseEntity.getIdDatabaseType() == 8) {
				databaseType = "SAPDB";
			} else if(databaseEntity.getIdDatabaseType() == 9) {
				databaseType = "HSQLDB";
			}
			
			newlist.add(new DBConnectionChecker(
					databaseType, 
					databaseEntity.getHost(), 
					Integer.parseInt(databaseEntity.getPort()), 
					databaseEntity.getDatabaseName(), 
					databaseEntity.getUserName(),
					databaseEntity.getPassword()
					));
		}
		//check if the old list is empty: if not copy the old status and timeout and of
		//elements that compare in the two list to the new list
		if(list != null) {
			for(DBConnectionChecker item : list) {
				
				//newlist contains the item of the old list
				//copy status, timeout and session
				if(newlist.contains(item)) {	
					int index = newlist.indexOf(item);
					newlist.get(index).setStatus(item.getStatus());
					newlist.get(index).setTimeout(item.getTimeout());
					newlist.get(index).setSession(item.getSession());
				}
				
				//the item will not appear in the new list 
				//the connection to the database will be eliminated
				else {
						item.closeConnection();
				}
			}
			
			
		
		}
		
		//list is now the newlist : the elements of old list will
		//be removed
		list = newlist;
		
		//open a new session for the elements that haven't
		for(DBConnectionChecker item : list) {
			if(item.getcheckParams()){
				if(item.getSession() == null){
					item.useSession();
				}
			}
		}
		
		//Create the answer body
		StringBuilder body = new StringBuilder();
		for(DBConnectionChecker item : list) {
			if(item.getStatus() == true)
				body.append('1');
			else
				body.append('0');
		}
		

 		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.println(body.toString());
		out.flush();
		out.close();
 			
 		return null;
	}
}
