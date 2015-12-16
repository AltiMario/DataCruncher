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

package com.seer.datacruncher.profiler.spring;

import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.profiler.framework.profile.FirstInformation;
import com.seer.datacruncher.profiler.framework.profile.QueryDialog;
import com.seer.datacruncher.profiler.framework.profile.TableMetaInfo;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.util.CommonUtil;
import com.seer.datacruncher.profiler.util.GridUtil;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class ProfilerInfoUpdateController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		@SuppressWarnings("unchecked")
		Hashtable<String, String> dbParams = (Hashtable<String, String>) request
				.getSession(true).getAttribute("dbConnectionData");
		if (dbParams != null) {
			request.setAttribute("serverName",
					CommonUtil.notNullValue(dbParams.get("Database_DSN")));
		}
		String selectedValue = CommonUtil.notNullValue(request
				.getParameter("selectedValue"));
		
		request.setAttribute("selectedValue", selectedValue);
		String tableName = CommonUtil.notNullValue(request
				.getParameter("parent"));
		
		request.setAttribute("parentValue", tableName);
		
		ObjectMapper mapper = new ObjectMapper();
		
		Vector vector = RdbmsConnection.getTable();

		int i = vector.indexOf(tableName);

		Vector avector[] = (Vector[]) null;
		avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
		
		QueryDialog querydialog = new QueryDialog(1, tableName, avector);
		try {
			querydialog.executeAction("");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String strColumnName = "";

		List<String> listPrimaryKeys = new ArrayList<String>();
		Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
		
		try {
		
			RdbmsConnection.openConn();
			DatabaseMetaData dbmd = RdbmsConnection.getMetaData();

			ResultSet resultset = dbmd.getPrimaryKeys(null, null, tableName);
			while (resultset.next()) {
				listPrimaryKeys.add(resultset.getString("COLUMN_NAME"));
			}
			
			resultset = dbmd.getColumns(null, null, tableName, null);
						
			while (resultset.next()) {
				strColumnName = resultset.getString(4);				
				mapColumnNames.put(strColumnName, resultset.getInt(5));				
			}
					
			RdbmsConnection.closeConn();
		} catch(Exception ex) {
			ex.printStackTrace();
		}	
		
		Map<String, Integer> mapPrimaryKeys = new HashMap<String, Integer>();
		
		if(strColumnName.trim().length() > 0 ) {
			try {				
				JSONArray array = new JSONArray(request.getParameter("data"));
				
				for(int count = 0; count < array.length(); count++) {
					JSONObject jsonObject = new JSONObject(array.get(count).toString());
					
					StringBuilder queryString = new StringBuilder();
					Iterator<String> keyIterator = jsonObject.keys();
					
					while(keyIterator.hasNext()) {
						String strKey = keyIterator.next();
					
						if(listPrimaryKeys.contains(strKey)) {
							mapPrimaryKeys.put(strKey, ((int)Double.parseDouble(jsonObject.get(strKey).toString())));
							continue;
						}
						if(jsonObject.get(strKey) != null) {
						
							if(mapColumnNames.get(strKey) ==  4 || mapColumnNames.get(strKey) == 5 || mapColumnNames.get(strKey) == -6) {
								queryString.append(strKey + "=" + Integer.parseInt(jsonObject.get(strKey).toString()) + ",");
							} else if(mapColumnNames.get(strKey) ==  2 || mapColumnNames.get(strKey) ==  3 || mapColumnNames.get(strKey) == 7 || mapColumnNames.get(strKey) ==  6 || mapColumnNames.get(strKey) ==  -5) {
								queryString.append(strKey + "=" + jsonObject.get(strKey) + ",");
							} else if(mapColumnNames.get(strKey) == 91 || mapColumnNames.get(strKey) == 92 || mapColumnNames.get(strKey) == 93) {
								queryString.append(strKey + "=" + jsonObject.get(strKey) + ",");
							} else if(mapColumnNames.get(strKey) == -7 || mapColumnNames.get(strKey) == 16 || mapColumnNames.get(strKey) == -3 || mapColumnNames.get(strKey) == -4) {
								queryString.append(strKey + "=" + jsonObject.get(strKey) + ",");
							} else if(mapColumnNames.get(strKey) == -1 || mapColumnNames.get(strKey) == 1 || mapColumnNames.get(strKey) == 12) {
								queryString.append(strKey + "=\"" + jsonObject.get(strKey) + "\",");
							}
						}
					}
					StringBuilder whereClause = new StringBuilder(" where ");
					
					for(String primaryKey : listPrimaryKeys) {
						whereClause.append(primaryKey + "=" + mapPrimaryKeys.get(primaryKey).intValue()) ;
						whereClause.append(" and ");
					}
					String strWhereClause = whereClause.toString();				
					strWhereClause = strWhereClause.substring(0, strWhereClause.lastIndexOf("and"));
					
					queryString = new StringBuilder("UPDATE " + tableName + " SET ").append(queryString.toString().substring(0, queryString.toString().length() - 1));
					queryString.append(strWhereClause);
					
					RdbmsConnection.openConn();
					RdbmsConnection.executeUpdate(queryString.toString());
					RdbmsConnection.closeConn();
				}				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		Update update = new Update();
		update.setSuccess(true);

		GridUtil gridUtil = new GridUtil();
		gridUtil.generateGridData(querydialog.getTableGridDTO(), false, null);
		update.setResults(gridUtil.getData());
		
		out.write(mapper.writeValueAsBytes(update));
		out.flush();
		out.close();
		
		return null;
	}

	public Vector[] getDistributionValues(QueryBuilder querybuilder,
			HttpServletRequest request) {
		Vector avector[] = FirstInformation.getDistributionValues(querybuilder);

		request.setAttribute("minValue", FirstInformation.getMinVal());
		request.setAttribute("maxValue", FirstInformation.getMaxVal());

		return avector;
	}
}