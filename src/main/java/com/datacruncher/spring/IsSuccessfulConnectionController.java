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

import com.datacruncher.constants.GenericType;
import com.datacruncher.constants.Servers;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.ConnectionsEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
 

public class IsSuccessfulConnectionController implements Controller, DaoSet {
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String success;
		try {
			int service = request.getParameter("service") != null ? Integer.parseInt(request.getParameter("service")) : -1;
			String connID = request.getParameter("id");
            success = checkServiceRunning(service, connID);
        } catch (Exception e) {
			success = "false";
		}
		Map<String, String> resMap = new HashMap<String, String>();		
		resMap.put("success", success);
		response.getWriter().print(new JSONObject(resMap).toString());
		return null;
	}
	private String checkServiceRunning(int service, String connID) {
		
		String success = "true";
		String url = "";
		ConnectionsEntity conn = connectionsDao.find(Long.parseLong(connID));
		DefaultFileSystemManager fsManager =  null;
		FileObject fileObject = null;
		String userName = "";
		String password = "";
		String hostName = "";
		String port = "";
		String inputDirectory = "";
		String fileName = "";
        int connType = 1;
		
		if(conn != null) {
			userName = conn.getUserName();
			password = conn.getPassword();
			hostName = conn.getHost();
			port = conn.getPort();
			inputDirectory = conn.getDirectory();
			fileName = conn.getFileName();
            connType = conn.getIdConnType();
            
		}

		if(connType ==  GenericType.DownloadTypeConn ){
            if (fileName == null || fileName.trim().length() == 0) {
			    return "false";
            }else{
                fileName = "/" + fileName;
            }
		}else{
            fileName = "";
        }
		
		try {
			fsManager = (DefaultFileSystemManager) VFS.getManager();
			if(service == Servers.SAMBA.getDbCode()) {
				 if(!fsManager.hasProvider("smb")) {
					 fsManager.addProvider("smb", new SmbFileProvider());
				 }
				 url = "smb://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory +  fileName;
			} else if(service == Servers.HTTP.getDbCode()){
				 if(!fsManager.hasProvider("http")) {					
					 fsManager.addProvider("http", new HttpFileProvider());
				 }
				 url = "http://" + hostName + ":" + port + "/" + inputDirectory +  fileName;
			}else if( service == Servers.FTP.getDbCode()){
                if(!fsManager.hasProvider("ftp")) {
                    fsManager.addProvider("ftp", new FtpFileProvider());
                }
                url = "ftp://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory +  fileName;
            }



			fileObject = fsManager.resolveFile(url);

			if(fileObject == null || !fileObject.exists()) {
				success = "false";
			}


            if(connType ==  GenericType.DownloadTypeConn ){
                if(fileObject.getType().equals(FileType.IMAGINARY)) {
                    success = "false";
                }
                byte data[] = new byte[(int)fileObject.getContent().getSize()];
                fileObject.getContent().getInputStream().read(data);
            }
    	    
		} catch(Exception ex) {			
			success = "false";		
		} finally {
			try {
				if (fileObject != null) {
					fileObject.close();
				}
				if (fsManager != null) {
					fsManager.freeUnusedResources();
					fsManager.close();
					fsManager = null;
				}
			} catch (Exception ex) {

			} finally {
				fileObject = null;
				fsManager = null;
			}
		}
		
		return success;
	}
}