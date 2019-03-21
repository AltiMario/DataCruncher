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
 */

package com.datacruncher.spring;


import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.fileupload.FileExtensionType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class ValidateFilePopupController extends MultiActionController {
    public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws IOException {
    	String idSchema = request.getParameter("idSchema");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
		MultipartFile multipartFile = multipartRequest.getFile("file"); 
		String resMsg = "";
		
		if(multipartFile.getOriginalFilename().endsWith(FileExtensionType.ZIP.getAbbreviation())) {
			// Case 1: When user upload a Zip file - All ZIP entries should be validate one by one		
			ZipInputStream inStream =  null;
			try{
				inStream = new ZipInputStream(multipartFile.getInputStream());
				ZipEntry entry ;
				while (!(isStreamClose(inStream)) && (entry = inStream.getNextEntry()) != null) {
					if(! entry.isDirectory()){
						DatastreamsInput datastreamsInput = new DatastreamsInput ();
						datastreamsInput.setUploadedFileName(entry.getName());	
						byte[] byteInput = IOUtils.toByteArray(inStream);
						resMsg+= datastreamsInput.datastreamsInput(
								new String(byteInput) , Long.parseLong(idSchema), byteInput);
					}
					inStream.closeEntry();
				}
			}catch(IOException ex){
				resMsg = "Error occured during fetch records from ZIP file.";
			}finally{
				if(inStream != null)
					inStream.close();
			}
		}else{
			// Case 1: When user upload a single file. In this cae just validate a single stream 
			String datastream = new String(multipartFile.getBytes());
			DatastreamsInput datastreamsInput = new DatastreamsInput ();
			datastreamsInput.setUploadedFileName(multipartFile.getOriginalFilename());

			resMsg = datastreamsInput.datastreamsInput(
					datastream , Long.parseLong(idSchema), multipartFile.getBytes());
			
		}
 		String msg = resMsg.replaceAll("'", "\"").replaceAll("\\n", " ");
 		msg = msg.trim();
 		response.setContentType("text/html");
		ServletOutputStream out = null;
		out = response.getOutputStream();
		out.write(("{success: "+ true +" , message:'"+ msg +"',	" +	 "}").getBytes());
 		out.flush();
		out.close();
		return null;
    }
	/**
	 * This method will use to know about the IO Stream is closed or Open.
	 * There some issue in ZipInputStream.getNextEntry() some time its throws Exception of 'stream close' instead of return null.
	 * So this is a method will get private field [closed] of InputStream class so program could know that stream is open or closed.
	 * @return boolean
	 * It will return true if stream is closed else return false
	 */
	private boolean isStreamClose(ZipInputStream inStream) {
		try {
			Class c = inStream.getClass();
			Field in;
			in = c.getDeclaredField("closed");
			in.setAccessible(true);
			Boolean inReader = (Boolean) in.get(inStream);
			return inReader;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
}