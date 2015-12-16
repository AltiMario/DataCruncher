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

package com.seer.datacruncher.services;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.datastreams.DatastreamsInput;
import com.seer.datacruncher.eventtrigger.EventTrigger;
import com.seer.datacruncher.factories.streams.StreamGenerationUtils;
import com.seer.datacruncher.fileupload.FileExtensionType;
import com.seer.datacruncher.jpa.dao.ConnectionsDao;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ConnectionsEntity;
import com.seer.datacruncher.jpa.entity.EventTriggerEntity;
import com.seer.datacruncher.jpa.entity.JobsEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.CommonUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ServiceScheduledJob extends QuartzJobBean implements DaoSet {
	private static Logger log = Logger.getLogger(ServiceScheduledJob.class);

	@Override
	protected synchronized void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        long jobId = arg0.getJobDetail().getJobDataMap().getLong("jobId");
        JobsEntity jobEntity = jobsDao.find(jobId);
        if(!jobEntity.isWorking()){

            if(jobsDao.setWorkStatus(jobId,true)){
                try{
                    long eventTriggerId = arg0.getJobDetail().getJobDataMap().getString("eventTriggerId") == null ? -1l : Long.parseLong(arg0.getJobDetail().getJobDataMap().getString("eventTriggerId"));
                    if(eventTriggerId > 0) {
                        EventTriggerEntity entity = eventTriggerDao.findEventTriggerById(eventTriggerId);
                        String className = entity.getName();
                        try {
                            String sourceCode = entity.getCode();
                            EventTrigger eventTrigger;
                            String response;
                            eventTrigger = (EventTrigger)CommonUtils.getClassInstance(className,"com.seer.datacruncher.eventtrigger.EventTrigger",EventTrigger.class,sourceCode);
                            assert eventTrigger != null;
                            response = eventTrigger.trigger();
                            log.info("Response From EventTrigger("+className+") :"+response);
                        } catch(Exception e) {
                            e.printStackTrace();
                            log.error("EventTrigger("+className+") :"+e.getMessage(),e);
                            logDao.setErrorLogMessage("EventTrigger("+className+") :"+e.getMessage());
                        }catch(NoClassDefFoundError err){
                            log.error("EventTrigger("+className+") :"+err.getMessage(),err);
                            logDao.setErrorLogMessage("EventTrigger("+className+") :"+err.getMessage());
                        }
                        return;
                    }

                    int day = arg0.getJobDetail().getJobDataMap().getString("day") == null ? -1 : Integer.parseInt(arg0.getJobDetail().getJobDataMap().getString("day"));
                    int month = arg0.getJobDetail().getJobDataMap().getString("month") == null ? -1 : Integer.parseInt(arg0.getJobDetail().getJobDataMap().getString("month"));
                    if((day > 0 && day != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) || (month > 0 && month != (Calendar.getInstance().get(Calendar.MONTH) + 1))) {
                        return;
                    }
                    StandardFileSystemManager fsManager = new StandardFileSystemManager();
                    boolean isDataStream = true;
                    try {
                        fsManager.init();
                        long schemaId = arg0.getJobDetail().getJobDataMap().getLong("schemaId");
                        long schedulerId = arg0.getJobDetail().getJobDataMap().getLong("schedulerId");
                        //long jobId = arg0.getJobDetail().getJobDataMap().getLong("jobId");
                        long connectionId = arg0.getJobDetail().getJobDataMap().getLong("connectionId");

                        String datastream ="";
                        int idSchemaType = schemasDao.find(schemaId).getIdSchemaType();

                        TasksEntity taskEntity = tasksDao.find(schedulerId);
                        //JobsEntity jobEntity = jobsDao.find(jobId);
                        if(taskEntity.getIsOneShoot()) {
                            jobEntity.setIsActive(0);
                            jobsDao.update(jobEntity);
                        }
                        if(idSchemaType == SchemaType.GENERATION ){
                            StreamGenerationUtils sgu = new StreamGenerationUtils();
                            datastream = sgu.getStream(schemaId);

                            log.debug("Content stream: "+ schemaId);

                            if(datastream.trim().length() > 0){
                                log.debug("Datastream to validate: "+datastream);
                                DatastreamsInput datastreamsInput = new DatastreamsInput();
                                String result = datastreamsInput.datastreamsInput(datastream, schemaId, null);
                                log.debug("Validation result: "+result);
                            } else{
                                isDataStream = false;
                                log.debug("No datastream create");
                            }
                        }
                        if(connectionId !=0){
                            int serviceId = Integer.parseInt(arg0.getJobDetail().getJobDataMap().getString("serviceId"));

                            String hostName = arg0.getJobDetail().getJobDataMap().getString("ftpServerIp");
                            String port = arg0.getJobDetail().getJobDataMap().getString("port");
                            String userName = arg0.getJobDetail().getJobDataMap().getString("userName");
                            String password = arg0.getJobDetail().getJobDataMap().getString("password");
                            String inputDirectory = arg0.getJobDetail().getJobDataMap().getString("inputDirectory");
                            String fileName = arg0.getJobDetail().getJobDataMap().getString("fileName");

                            ConnectionsEntity conn;

                            conn = connectionsDao.find(connectionId);

                            if (inputDirectory == null || inputDirectory.trim().length() == 0) {
                                inputDirectory = fileName;
                            }else if(!(conn.getIdConnType() ==  GenericType.uploadTypeConn && serviceId == Servers.HTTP.getDbCode())){
                                inputDirectory = inputDirectory+ "/" + fileName;
                            }

                            log.info("(jobId:"+jobEntity.getName() + ") - Trying to Server polling at server ["+ hostName + ":" + port + "] with user[" + userName + "].");
                            String url = "";
                            if(serviceId == Servers.SAMBA.getDbCode()) {
                                 if(!fsManager.hasProvider("smb")) {
                                     fsManager.addProvider("smb", new SmbFileProvider());
                                 }
                                 url = "smb://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory;
                            } else if(serviceId == Servers.HTTP.getDbCode()) {
                                 if(!fsManager.hasProvider("http")) {
                                     fsManager.addProvider("http", new HttpFileProvider());
                                 }
                                 url = "http://" + hostName + ":" + port + "/" + inputDirectory;
                            }  else if(serviceId == Servers.FTP.getDbCode()) {
                                if(!fsManager.hasProvider("ftp")) {
                                    fsManager.addProvider("ftp", new FtpFileProvider());
                                }
                                url = "ftp://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory;
                            }
                            log.info("url:"+url);
                            final FileObject fileObject = fsManager.resolveFile(url);

                            if(conn.getIdConnType() ==  GenericType.DownloadTypeConn ){

                                if (conn.getFileDateTime() != null && conn.getFileDateTime().getTime() == fileObject.getContent().getLastModifiedTime()) {
                                    log.info("There is no New or Updated '"+fileName+"' file on server to validate. Returning ...");
                                    return;
                                } else {
                                    log.info("There is New or Updated '"+fileName+"' file on server to validate. Validating ...");
                                    ConnectionsEntity connection = connectionsDao.find(connectionId);
                                    connection.setFileDateTime(new Date(fileObject.getContent().getLastModifiedTime()));
                                    ApplicationContext ctx = AppContext.getApplicationContext();
                                    ConnectionsDao connDao = (ctx.getBean(ConnectionsDao.class));

                                    if(connDao != null) {
                                        connDao.update(connection);
                                    }

                                    Map<String, byte[]> resultMap = new HashMap<String, byte[]>();
                                    byte data[] = new byte[(int)fileObject.getContent().getSize()];
                                    fileObject.getContent().getInputStream().read(data);
                                    resultMap.put(fileObject.getName().getBaseName(), data);

                                    Set<String> keySet = resultMap.keySet();
                                    Iterator<String> itr = keySet.iterator();
                                    while (itr.hasNext()) {

                                        String strFileName = itr.next();
                                        String result = "";
                                        try {

                                            Long longSchemaId = schemaId;
                                            SchemaEntity schemaEntity = schemasDao.find(longSchemaId);
                                            if (schemaEntity == null) {
                                                result = "No schema found in database with Id [" + longSchemaId + "]";
                                                log.error(result);
                                                logDao.setErrorLogMessage(result);
                                            }
                                            else {
                                                if(strFileName.endsWith(FileExtensionType.ZIP.getAbbreviation())) {
                                                    // Case 1: When user upload a Zip file - All ZIP entries should be validate one by one
                                                    ZipInputStream inStream = null;
                                                    try{
                                                        inStream = new ZipInputStream(new ByteArrayInputStream(resultMap.get(fileName)));
                                                        ZipEntry entry ;
                                                        while (!(isStreamClose(inStream)) && (entry = inStream.getNextEntry()) != null) {
                                                            if(! entry.isDirectory()){
                                                                DatastreamsInput datastreamsInput = new DatastreamsInput ();
                                                                datastreamsInput.setUploadedFileName(entry.getName());
                                                                byte[] byteInput = IOUtils.toByteArray(inStream);
                                                                result+= datastreamsInput.datastreamsInput(
                                                                        new String(byteInput) , longSchemaId , byteInput);
                                                            }
                                                            inStream.closeEntry();
                                                        }
                                                        log.debug(result);
                                                    }catch(IOException ex){
                                                        result = "Error occured during fetch records from ZIP file.";
                                                        log.error(result);
                                                        logDao.setErrorLogMessage(result);
                                                    }finally{
                                                        if(inStream != null )
                                                            inStream.close();
                                                    }
                                                }else {
                                                    DatastreamsInput datastreamsInput = new DatastreamsInput ();
                                                    datastreamsInput.setUploadedFileName(strFileName);
                                                    result = datastreamsInput.datastreamsInput(new String(resultMap.get(strFileName)),
                                                            longSchemaId,  resultMap.get(strFileName));
                                                    log.debug(result);
                                                }
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            result = "Exception occured during process the message for xml file " + strFileName + " Error - "
                                                    + ex.getMessage();
                                            log.error(result);
                                            logDao.setErrorLogMessage(result);
                                        }
                                    }
                                }
                            } else if(isDataStream && (conn.getIdConnType() ==  GenericType.uploadTypeConn) ) {

                                File uploadFile = File.createTempFile(fileName, ".tmp");

                                try {
                                    BufferedWriter bw = new BufferedWriter(new FileWriter(uploadFile));
                                    bw.write(datastream);
                                    bw.flush();
                                    bw.close();
                                } catch(IOException ioex) {
                                    log.error("Datastream file can't be created");
                                    logDao.setErrorLogMessage("Datastream file can't be created");
                                    return;
                                }

                                if (serviceId == Servers.HTTP.getDbCode()) {
                                    try {
                                        HttpClient httpclient = new HttpClient();
                                        PostMethod method = new PostMethod(url);

                                        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                                new DefaultHttpMethodRetryHandler(3, false));

                                        Part[] parts = new Part[] { new FilePart("file", uploadFile.getName(), uploadFile) };
                                        method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
                                        method.setDoAuthentication(true);

                                        int statusCode = httpclient.executeMethod(method);

                                        String responseBody = new String(method.getResponseBody());

                                        if (statusCode != HttpStatus.SC_OK) {
                                            throw new HttpException(method.getStatusLine().toString());
                                        } else {
                                            System.out.println(responseBody);
                                        }

                                        method.releaseConnection();

                                    } catch (Exception ex) {
                                        log.error("Exception occurred during uploading of file at HTTP Server: " + ex.getMessage());
                                        logDao.setErrorLogMessage("Exception occurred during uploading of file at HTTP Server: " + ex.getMessage());
                                    }
                                } else {
                                    try {
                                        FileObject localFileObject = fsManager.resolveFile(uploadFile.getAbsolutePath());
                                        fileObject.copyFrom(localFileObject, Selectors.SELECT_SELF);
                                        System.out.println("File uploaded at : " + new Date());
                                        if(uploadFile.exists()) {
                                            uploadFile.delete();
                                        }
                                    } catch(Exception ex) {
                                        log.error("Exception occurred during uploading of file: " + ex.getMessage());
                                        logDao.setErrorLogMessage("Exception occurred during uploading of file: " + ex.getMessage());
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Error " + ": " + ex.getMessage());
                    } finally {
                        fsManager.close();
                    }
                }finally {
                    jobsDao.setWorkStatus(jobId,false);
                }
            } else{
                log.error("Can not set "+jobEntity.getName()+"working.");
            }
        } else{
            log.debug("Job "+jobEntity.getName()+" is working.");
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
