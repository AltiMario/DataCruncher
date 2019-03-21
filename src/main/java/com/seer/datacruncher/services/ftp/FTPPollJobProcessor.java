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

package com.seer.datacruncher.services.ftp;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.datastreams.DatastreamsInput;
import com.seer.datacruncher.fileupload.FileExtensionType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.JVPropertyPlaceholderConfigurer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.remote.FtpComponent;
import org.apache.camel.component.file.remote.FtpEndpoint;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Praveen
 *
 */
public class FTPPollJobProcessor  implements Processor, DaoSet {
	private static Logger log = Logger.getLogger(FTPPollJobProcessor.class);
	
	private JVPropertyPlaceholderConfigurer propertyConfigurer;
	
	public FTPPollJobProcessor(){
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String result = "";
		GenericFile file = (GenericFile)exchange.getIn().getBody();
		Message message = exchange.getOut();
		String inputFileName = file.getFileName();
		Map<String, byte[]> resultMap = new HashMap<String, byte[]>();
		if(isValidFileName(inputFileName)){
			long schemaId = getSchemaIdUsingFileName(inputFileName);
			long userId = getUserIdFromFileName(inputFileName);
			if(!usersDao.isUserAssoicatedWithSchema(userId, schemaId)){
				result = "User not authorized";
			}else{
				resultMap.put(inputFileName, file.getBody().toString().getBytes());
				SchemaEntity schemaEntity = schemasDao.find(schemaId);
				if (schemaEntity == null){
					result = "No schema found in database with Id [" + schemaId + "]";	
				}else {
					if(inputFileName.endsWith(FileExtensionType.ZIP.getAbbreviation())) {
						// Case 1: When user upload a Zip file - All ZIP entries should be validate one by one	
						ZipInputStream inStream = null;
						try{
							inStream = new ZipInputStream(new ByteArrayInputStream(resultMap.get(inputFileName)));
							ZipEntry entry ;
							while (!(isStreamClose(inStream)) && (entry = inStream.getNextEntry()) != null) {
								if(! entry.isDirectory()){
									DatastreamsInput datastreamsInput = new DatastreamsInput ();
									datastreamsInput.setUploadedFileName(entry.getName());
									byte[] byteInput = IOUtils.toByteArray(inStream);
									result+= datastreamsInput.datastreamsInput(
											new String(byteInput) , schemaId , byteInput);
								}
								inStream.closeEntry();
							}
						}catch(IOException ex){
							result = "Error occured during fetch records from ZIP file.";
						}finally{
							if(inStream != null )
								inStream.close();
						}
					}else{
						DatastreamsInput datastreamsInput = new DatastreamsInput ();
						datastreamsInput.setUploadedFileName(inputFileName);
						result = datastreamsInput.datastreamsInput(new String(resultMap.get(inputFileName)),
								schemaId,  resultMap.get(inputFileName));
					}
				}
			}
		}else{
			result = "File Name not in specified format.";
		}

		// Store in Ftp location
		CamelContext context = exchange.getContext();
		FtpComponent component = context.getComponent("ftp", FtpComponent.class); 
		FtpEndpoint<?> endpoint = (FtpEndpoint<?>) 
				component.createEndpoint(getFTPEndPoint());
		
		Exchange outExchange = endpoint.createExchange();
		outExchange.getIn().setBody(result);
		outExchange.getIn().setHeader("CamelFileName", getFileNameWithoutExtensions(inputFileName) + ".txt");
		Producer producer = endpoint.createProducer();
		producer.start();
		producer.process(outExchange);
		producer.stop();
	}
	
	/**
	 * @param propertyConfigurer the propertyConfigurer to set
	 */
	public void setPropertyConfigurer(
			JVPropertyPlaceholderConfigurer propertyConfigurer) {
		this.propertyConfigurer = propertyConfigurer;
	}

	//------------Helpers----------------
	private String getFTPEndPoint(){
		ApplicationConfigEntity configEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
		
		String ftpServer = propertyConfigurer.getProps().getProperty("ftp.server");
		String userName = configEntity.getUserName();
		String password = new CryptoUtil().decrypt(configEntity.getPassword());
		String outputDir = configEntity.getOutputDir();
		String endPoint = "ftp://" + ftpServer + ":" + configEntity.getServerPort() + "/" + outputDir + "?username="
				+ userName + "&password=" + password + "&binary=true";
		// if output Directory is not set store the file in root directory.
		if(StringUtils.isBlank(outputDir)){
			outputDir = "/";
		}
		return endPoint;
	}
	
	private String getFileNameWithoutExtensions(String fileName){
		return (fileName == null) ? null : fileName.split("\\.")[0];
	}
	private boolean isValidFileName(String fileName) {
		boolean isValidFileName = false;
		String[] fileNameChunks = fileName.split("-");
		int chunksLength = fileNameChunks.length;
		if (chunksLength == 3) {
			isValidFileName = true;
		}
		return isValidFileName;
	}
	
	private Long getSchemaIdUsingFileName(String fileName) {		
		try{
			String[] data = fileName.split("-");
			String sId = data[1];
			return Long.parseLong(sId);
		}catch(Exception ex){
			return -1L;			
		}
	}
	private Long getUserIdFromFileName(String fileName){
		try{
			String[] data = fileName.split("-");
			String userId = data[0];
			return Long.parseLong(userId);
		}catch(Exception ex){
			return -1L;			
		}
	}
	private boolean isStreamClose(ZipInputStream inStream) {
		try {
			@SuppressWarnings("rawtypes")
			Class c = inStream.getClass();
			Field in;
			in = c.getDeclaredField("closed");
			in.setAccessible(true);
			Boolean inReader = (Boolean) in.get(inStream);
			return inReader;
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}
	
}
