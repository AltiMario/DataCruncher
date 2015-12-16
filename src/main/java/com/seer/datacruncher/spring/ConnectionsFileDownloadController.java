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

import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.jpa.Validate;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ConnectionsEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class ConnectionsFileDownloadController extends MultiActionController implements DaoSet {

    private static Logger log = Logger.getLogger(ConnectionsFileDownloadController.class);
    private String keptXSD;
    
    public ModelAndView checkValidity(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	Long connId = Long.parseLong(request.getParameter("connId"));
		ConnectionsEntity connectionEntity = connectionsDao.find(connId);
		
		keptXSD = getFileContent(connId, connectionEntity);		
		Validate result = new Validate();
		if(keptXSD == null || keptXSD.trim().length() == 0) {			
			result.setSuccess(false);
		} else {			
			result.setSuccess(true);
		}		
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(new ObjectMapper().writeValueAsBytes(result));
		out.flush();
		out.close();		
		
		return null;
	}    

	public ModelAndView downloadXSD(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		Long connId = Long.parseLong(request.getParameter("connId"));
		ConnectionsEntity connectionEntity = connectionsDao.find(connId);
        try {
            String fileName = HttpUtils.encodeContentDispositionForDownload(request, connectionEntity.getFileName(), false);
            OutputStream ostr = response.getOutputStream();
            response.setContentType("application/binary");
            response.setHeader("Content-Disposition", fileName);
            ostr.write(keptXSD.getBytes());
            ostr.flush();
            ostr.close();
        } catch (Exception ex) {            
            log.error(ex.getMessage(), ex);        	
        }
        return null;
    }
	private String getFileContent(long connId, ConnectionsEntity connectionEntity) {
		String content = "";
		DefaultFileSystemManager fsManager = null;
		FileObject fileObject = null;
		try {
			
			fsManager = (DefaultFileSystemManager) VFS.getManager();
			int serviceId = connectionEntity.getService();
			
			String hostName = connectionEntity.getHost();
			String port = connectionEntity.getPort();
			String userName = connectionEntity.getUserName();
			String password = connectionEntity.getPassword();
			String inputDirectory = connectionEntity.getDirectory();
			String fileName = connectionEntity.getFileName();
			
			log.info("Trying to Server polling at server ["+ hostName + ":" + port + "] with user[" + userName + "].");
			
			String url = "";
			if(serviceId == Servers.SAMBA.getDbCode()) {
				 if(!fsManager.hasProvider("smb")) {
					 fsManager.addProvider("smb", new SmbFileProvider());
				 }
				 url = "smb://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory + "/" + fileName;
			} else if(serviceId == Servers.HTTP.getDbCode()) { 
				 if(!fsManager.hasProvider("http")) {					
					 fsManager.addProvider("http", new HttpFileProvider());
				 }
				 url = "http://" + hostName + ":" + port + "/" + inputDirectory + "/" + fileName;
			} else if(serviceId == Servers.FTP.getDbCode()) {
				if(!fsManager.hasProvider("ftp")) {
					 fsManager.addProvider("ftp", new SmbFileProvider());
				 }
				 url = "ftp://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory + "/" + fileName;
			}
			
			fileObject = fsManager.resolveFile(url);	
			
			if(fileObject == null || !fileObject.exists() || fileObject.getType().equals(FileType.IMAGINARY)) {
				return null;
			}
					 		   		    		
    		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileObject.getContent().getInputStream())); 
    		StringBuilder sb = new StringBuilder();
 
    		String line;
    		while ((line = fileReader.readLine()) != null) {
    			sb.append(line);
    		} 
     		
    		content = sb.toString();
    		
		} catch(Exception ex) {
			
		} finally {try {
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
		return content;
		
	}
}
