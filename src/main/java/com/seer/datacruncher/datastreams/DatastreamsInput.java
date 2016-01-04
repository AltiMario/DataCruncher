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

package com.seer.datacruncher.datastreams;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.datastreams.threads.PersistStreamsAndSendAlertsThread;
import com.seer.datacruncher.fileupload.FileExtensionType;
import com.seer.datacruncher.fileupload.FileReadObject;
import com.seer.datacruncher.fileupload.FileReadObjectFactory;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.EventTriggerEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.validation.Logical;
import com.seer.datacruncher.validation.ResultStepValidation;
import com.seer.datacruncher.validation.Temporary;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class DatastreamsInput implements DaoSet {

	private final static Logger log = Logger.getLogger(DatastreamsInput.class);
	private String uploadedFileName;
    List<EventTriggerEntity> okEventList;
    List<EventTriggerEntity> koEventList;
    List<EventTriggerEntity> warnEventList;
    private boolean okEvent = false;
    private boolean koEvent = false;
    private boolean warnEvent = false;	
    private boolean bTotalSuccess;
    private String resultMsg = "";
    
    public final static String _SINGLE_SUCC_RESP = "bTotalSuccess";
    public final static String _SINGLE_STR_RESP = "bTotalResultString";
    public final static String _SINGLE_STREAMENT_RESP = "streamEntityResonse";
    private static int poolSize = 7;
    
	static {
		Properties properties = new Properties();
		InputStream in = DatastreamsInput.class.getClassLoader().getResourceAsStream("main.properties");
		try {
			properties.load(in);
			poolSize = Integer.parseInt(properties.get("validation.pool_size").toString());
		} catch (IOException e) {
			log.error("error reading main.properties", e);
		}		
	}    

	public void setUploadedFileName(String uploadedFileName) {
		this.uploadedFileName = uploadedFileName;
	}

	public DatastreamsInput() {
	}
	
    public String datastreamsInput(String datastream, Long idSchema,byte[] bytes){
        return datastreamsInput(datastream, idSchema, bytes, null);
    }

	public String datastreamsInput(String datastream, Long idSchema, byte[] bytes, Object object) {
		SchemaEntity schemaEntity = schemasDao.find(idSchema);
		if (schemaEntity != null && schemaEntity.getIsPlanned() && schemaEntity.getPlannedName() > 0) {
			TasksEntity tasksEntity = tasksDao.find(schemaEntity.getPlannedName());
			boolean isPlanned = true;
			Calendar calendar = Calendar.getInstance();
			if (tasksEntity != null) {
				if (tasksEntity.getIsOneShoot()) {
					Date shootDate = tasksEntity.getShootDate();
					String shootTime = tasksEntity.getShootTime();
					Calendar shootDateTime = Calendar.getInstance();
					shootDateTime.setTime(shootDate);
					if (calendar.get(Calendar.YEAR) != shootDateTime.get(Calendar.YEAR)) {
						isPlanned = false;
					} else if (calendar.get(Calendar.MONTH) != shootDateTime.get(Calendar.MONTH)) {
						isPlanned = false;
					} else if (calendar.get(Calendar.DAY_OF_MONTH) != shootDateTime.get(Calendar.DAY_OF_MONTH)) {
						isPlanned = false;
					} else if (shootTime.trim().length() > 0 && !shootTime.equalsIgnoreCase("hh:mm")) {
						int hour = Integer.parseInt(shootTime.substring(0, shootTime.indexOf(":")));
						int min = Integer.parseInt(shootTime.substring(shootTime.indexOf(":") + 1));
						if (calendar.get(Calendar.HOUR_OF_DAY) != hour) {
							isPlanned = false;
						} else if (calendar.get(Calendar.MINUTE) != min) {
							isPlanned = false;
						}
					}
				} else if (tasksEntity.getIsPeriodically()) {
					if (tasksEntity.getMonth() >= 0 && (calendar.get(Calendar.MONTH) + 1) != tasksEntity.getMonth()) {
						isPlanned = false;
					} else if (tasksEntity.getDay() >= 0 && calendar.get(Calendar.DAY_OF_MONTH) != tasksEntity.getDay()) {
						isPlanned = false;
					} else if (tasksEntity.getWeek() >= 0 && calendar.get(Calendar.DAY_OF_WEEK) != tasksEntity.getWeek()) {
						isPlanned = false;
					} else if (tasksEntity.getHour() >= 0 && calendar.get(Calendar.HOUR_OF_DAY) != tasksEntity.getHour()) {
						isPlanned = false;
					} else if (tasksEntity.getMinute() >= 0 && calendar.get(Calendar.MINUTE) != tasksEntity.getMinute()) {
						isPlanned = false;
					}
				}
			}
			if (!isPlanned) {
				return I18n.getMessage("error.datastreamnotplanned.invalid");
			}
		}
		return datastreamsInput(datastream, idSchema, bytes, false, object);
	}
	
    public String datastreamsInput(String datastream, Long idSchema, byte[] bytes, boolean isUnitTest){
        return datastreamsInput(datastream, idSchema, bytes, isUnitTest, null);
    }
    
    public String datastreamsInput(String datastream, Long idSchema, byte[] bytes, boolean isUnitTest, Object object){
        String resultMsg = "";
		SchemaEntity schemaEntity = schemasDao.find(idSchema);
		if (schemaEntity == null) {
			resultMsg = I18n.getMessage("error.schema.invalid");
			return isUnitTest ? "false" : resultMsg;
		}
		int idStreamType = schemaEntity.getIdStreamType();
        ApplicationEntity appEntity = appDao.find(schemaEntity.getIdApplication());
        Temporary temporary = new Temporary();
        ResultStepValidation resultValidation = temporary.temporaryValidation(schemaEntity, appEntity);
        if (resultValidation.isValid()) {
            List<String> streamsList = parseStream(datastream, bytes, idSchema, idStreamType);
            int i = 1;

            if (streamsList.size() > 0) {
                long numEvents = schemaTriggerStatusDao.findEventByIdSchema(idSchema);
                bTotalSuccess = true;
                if (numEvents > 0) {
                    checkEventTrigger(idSchema);
                }
                ExecutorService executor = Executors.newFixedThreadPool(poolSize);
                if ( schemaEntity.getIsIndexedIncrement() ) {
                	executor = Executors.newSingleThreadExecutor();
                }
                long numElemChecked = schemaFieldsDao.findNumExtraCheck(idSchema);
                String defaultNsLib = schemaEntity.getIdSchemaLib() == 0 ? null : schemaLibDao.find(schemaEntity.getIdSchemaLib()).getDefaultNsLib();
                List<Future<Map<String, Object>>> list = new ArrayList<Future<Map<String, Object>>>();

                int index = -1;
                for (String stream : streamsList) {
                	index ++;
                	if ( index == 0 && 
                			schemaEntity.getIdStreamType() == StreamType.flatFileDelimited && 
                			schemaEntity.getIdSchemaType() == SchemaType.VALIDATION && 
                			schemaEntity.isNoHeader()) {
                		continue;
                	}
                		
                    Callable<Map<String, Object>> callee = new ValidationCallable(
                    		idSchema, 
                    		schemaEntity, 
                    		stream, 
                    		bytes, 
                    		isUnitTest,
                            okEvent, 
                            koEvent, 
                            warnEvent, 
                            okEventList, 
                            koEventList, 
                            warnEventList, 
                            object, 
                            numElemChecked, 
                            appEntity,
                            defaultNsLib);
                    Future<Map<String, Object>> submit = executor.submit(callee);
                    list.add(submit);
                }
                Map<String, Object> futureRes;
                try {
                	if (!isUnitTest) {
                		PersistStreamsAndSendAlertsThread persStreams = new PersistStreamsAndSendAlertsThread(list, executor);
                		persStreams.start();
                		persStreams.join();
                	}
                    for (Future<Map<String, Object>> future : list) {
                        try {
                            futureRes = future.get();
                            resultMsg += (streamsList.size() == 1 ? "" : i++ + ".Stream: ")
                                    + futureRes.get(_SINGLE_STR_RESP) + "<br><br>";
                            bTotalSuccess = bTotalSuccess && (Boolean) futureRes.get(_SINGLE_SUCC_RESP);
                        } catch (ExecutionException e) {
                            log.error("ExecutionException", e);
                        }
                    }
                } catch (InterruptedException e1) {
                    log.error("InterruptedException", e1);
                }
                executor.shutdown();
                Logical.mapExtraCheck = new HashMap<String, Set<String>>();
            } else {
                bTotalSuccess = false;
                resultMsg = I18n.getMessage("error.validationTraceNoValid");
            }
        } else {
            bTotalSuccess = false;
            resultMsg = resultValidation.getMessageResult();
        }
        this.resultMsg = resultMsg;
		return isUnitTest ? String.valueOf(bTotalSuccess) : resultMsg;
	}


    @SuppressWarnings("unchecked")
    private void checkEventTrigger(Long idSchema){
        ReadList readList = eventTriggerDao.findByIdSchemaAndIdStatus(idSchema, GenericType.okEvent);
        if (readList.getResults() != null && readList.getResults().size() > 0) {
            okEvent = true;
            okEventList =  (List<EventTriggerEntity>)  readList.getResults();
        }else{
            okEvent = false;
        }
        readList = eventTriggerDao.findByIdSchemaAndIdStatus(idSchema,GenericType.koEvent);
        if (readList.getResults() != null && readList.getResults().size() > 0) {
            koEvent = true;
            koEventList =  (List<EventTriggerEntity>)  readList.getResults();
        }else{
            koEvent = false;
        }
        readList = eventTriggerDao.findByIdSchemaAndIdStatus(idSchema,GenericType.warnEvent);
        if (readList.getResults() != null && readList.getResults().size() > 0) {
            warnEvent = true;
            warnEventList =  (List<EventTriggerEntity>)  readList.getResults();
        }else{
            warnEvent = false;
        }

    }

	/**
	 * Parses current multi datastream.
	 * 
	 * @param datastream DataStream
	 * @param idStreamType
	 *            - xml/exi/..
	 * @return separated streams
	 */
	private List<String> parseStream(String datastream, byte[] bytes,
			Long idSchema, int idStreamType) {
		List<String> streamsList = new ArrayList<String>();
		try {
            if(schemasDao.find(idSchema).getIdSchemaType() == SchemaType.STANDARD){
                String defNameSpace = schemaLibDao.find(schemasDao.find(idSchema).getIdSchemaLib()).getDefaultNsLib();
                String str = datastream.replaceAll("\\r|\\n|\\t", " ").trim();
                if (str.startsWith("<?xml")) {
                    // xml prolog remove
                    str = str.replaceFirst("<\\?xml .*\\?>", "");
                    str = str.trim();
                }
                if (str.startsWith("<")) {
                    int r= str.lastIndexOf("</");
                    String closingNode = str.substring(r);

                    closingNode = closingNode.trim();
                    String nodeName = closingNode.substring(closingNode.indexOf("/")+1, closingNode.length()-1);
                    streamsList = Arrays.asList(str.split(closingNode));
                    for (int i = 0; i < streamsList.size(); i++) {
                        String stream=streamsList.get(i).replaceFirst(nodeName,nodeName+" xmlns="+ defNameSpace +" ");
                        streamsList.set(i, stream + closingNode);
                    }
                }
            }else{
                if (idStreamType == StreamType.XML  ) {
                    String str = datastream.replaceAll("\\r|\\n|\\t", " ").trim();
                    if (str.startsWith("<?xml")) {
                        // xml prolog remove
                        str = str.replaceFirst("<\\?xml .*\\?>", "");
                        str = str.trim();
                    }
                    if (str.startsWith("<")) {
                        int r= str.lastIndexOf("</");
                        String closingNode = str.substring(r);
                        closingNode = closingNode.trim();
                        streamsList = Arrays.asList(str.split(closingNode));
                        for (int i = 0; i < streamsList.size(); i++) {
                            streamsList.set(i, streamsList.get(i) + closingNode);
                        }
                    }
                }else if (idStreamType == StreamType.flatFileFixedPosition) {
                    datastream= datastream.replaceAll("\\r\\n", "\\\n");
                    datastream= datastream.replaceAll("\\r", "\\\n");
                    streamsList = Arrays.asList(datastream.split("\\n"));
                    for (int i = 0; i < streamsList.size(); i++) {
                        // '/r' appear in file with '/r/n' but in textarea appear
                        // only '/n'
                        streamsList
                                .set(i, streamsList.get(i).replaceAll("\\r", ""));
                    }
                } else if (idStreamType == StreamType.flatFileDelimited) {
                    datastream= datastream.replaceAll("\\r\\n", "\\\n");
                    datastream= datastream.replaceAll("\\r", "\\\n");
                    Character chrDelim = schemasDao.find(idSchema).getChrDelimiter().charAt(0);
                    if (chrDelim.toString().trim().length() == 1) {
                        streamsList = Arrays.asList(CommonUtils.lineSplit(datastream, schemasDao.find(idSchema).getDelimiter(),chrDelim));
                    }else{
                        streamsList = Arrays.asList(datastream.split("\\n"));
                    }
                        for (int i = 0; i < streamsList.size(); i++) {
                        // '/r' appear in file with '/r/n' but in textarea appear
                        // only '/n'
                        streamsList.set(i, streamsList.get(i).replaceAll("\\r", ""));
                    }
                } else if (idStreamType == StreamType.XMLEXI) {
                    streamsList.add(datastream);
                } else if (idStreamType == StreamType.JSON) {
                    String str = datastream.replaceAll("\\r|\\n|\\t", "").trim();
                    datastream = str.replaceAll("\\}\\{", "\\}--\\{");
                    streamsList = Arrays.asList(datastream.split("--"));
                    for (int i = 0; i < streamsList.size(); i++) {
                        streamsList.set(i, streamsList.get(i));
                    }
                } else if (idStreamType == StreamType.EXCEL) {
                    if (!(uploadedFileName.endsWith(FileExtensionType.EXCEL_97
                            .getAbbreviation()) || uploadedFileName
                            .endsWith(FileExtensionType.EXCEL_2007
                                    .getAbbreviation()))) {
                        streamsList.add("File is not a MS Excel file.");
                        return streamsList;
                    }
                    FileReadObject fileReadObject = FileReadObjectFactory
                            .getFileReadObject(uploadedFileName);
                    datastream = fileReadObject.parseStream(idSchema,
                            new ByteArrayInputStream(bytes));

                    String str = datastream.replaceAll("\\r|\\n|\\t", " ").trim();
                    if (str.startsWith("<?xml")) {
                        // xml prolog remove
                        str = str.replaceFirst("<\\?xml .*\\?>", "");
                        str = str.trim();
                    }
                    if (str.startsWith("<")) {
                        String rootNodeName = str.substring(1, str.indexOf(">"));
                        String closingNode = "</" + rootNodeName + ">";
                        streamsList = Arrays.asList(str.split(closingNode));
                        for (int i = 0; i < streamsList.size(); i++) {
                            streamsList.set(i, streamsList.get(i) + closingNode);
                        }
                    } else {
                        streamsList.add(datastream);
                    }
                }
            }
		} catch (Exception e) {
			log.error("Error while parsing stream", e);
		}
		return streamsList;
	}

	public boolean getTotalSuccess() {
		return bTotalSuccess;
	}
	
	public String getResultMsg() {
		return resultMsg;
	}	
}