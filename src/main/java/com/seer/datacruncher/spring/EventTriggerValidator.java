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

import com.seer.datacruncher.eventtrigger.DynamicClassLoader;
import com.seer.datacruncher.eventtrigger.EventTrigger;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class EventTriggerValidator implements Controller, DaoSet {
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String code = request.getParameter("code");		
		String name = request.getParameter("name");
		String addReq = request.getParameter("addReq");
		
		String result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		
		if(StringUtils.isEmpty(code) || StringUtils.isEmpty(name)){
			result = I18n.getMessage("error.trigger.invaliddata");//"Failed. Reason:Invalid Data";
		} else {
			name = name.trim();
			if(addReq.equalsIgnoreCase("true")){
				ReadList list = eventTriggerDao.findTriggersByName(name);
				if (list != null && CollectionUtils.isNotEmpty(list.getResults())) {
					result = I18n.getMessage("error.trigger.name.alreadyexist");//"Failed. Reason:Name alredy exist";
					out.write(mapper.writeValueAsBytes(result));
					out.flush();
					out.close();
					return null;
				}
			}
			try{
				File sourceDir = new File(System.getProperty("java.io.tmpdir"),"DataCruncher/src");  
				sourceDir.mkdirs();
				String classNamePack = name.replace('.', File.separatorChar);
				String srcFilePath = sourceDir + "" + File.separatorChar + classNamePack + ".java";
				File sourceFile =  new File(srcFilePath);
				if(sourceFile.exists()){
					sourceFile.delete();
				}
				FileUtils.writeStringToFile(new File(srcFilePath), code);
				DynamicClassLoader dynacode = DynamicClassLoader.getInstance();
				dynacode.addSourceDir(sourceDir);
				EventTrigger eventTrigger= (EventTrigger)dynacode.newProxyInstance(EventTrigger.class,name);
				boolean isValid = false;
				if (eventTrigger != null) {
					Class clazz = dynacode.getLoadedClass(name);
					if (clazz != null) {
						Class[] interfaces = clazz.getInterfaces();
						if(ArrayUtils.isNotEmpty(interfaces)){
							for (Class clz : interfaces) {
								if(clz.getName().equalsIgnoreCase("com.seer.datacruncher.eventtrigger.EventTrigger")){
									isValid = true;
								}
							}
						} else if(clazz.getSuperclass() != null && clazz.getSuperclass().getName().equalsIgnoreCase("com.seer.datacruncher.eventtrigger.EventTriggerImpl")) {
							isValid = true;
						}
					}
				}
				if(isValid){
					result = "Success";
				}else{
					result = I18n.getMessage("error.trigger.wrongimpl");//"Failed. Reason: Custom code should implement com.seer.datacruncher.eventtrigger.EventTrigger Interface";
				}
			}catch(Exception e){
				result = "Failed. Reason:"+e.getMessage();
			}
		}
		out.write(mapper.writeValueAsBytes(result));
		out.flush();
		out.close();
		return null;
	}
}