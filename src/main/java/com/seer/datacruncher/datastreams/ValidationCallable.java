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

package com.seer.datacruncher.datastreams;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.seer.datacruncher.connection.ConnectionPoolsSet;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.constants.ValidationStep;
import com.seer.datacruncher.eventtrigger.EventTriggerBuilder;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.EventTriggerEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.persistence.manager.QuickDBRecognizer;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.generic.StreamsUtils;
import com.seer.datacruncher.validation.DatastreamsValidator;
import com.seer.datacruncher.validation.ForcastedFieldsValidation;
import com.seer.datacruncher.validation.IndexIncrementalValidation;

public class ValidationCallable implements Callable<Map<String, Object>>, DaoSet {

	private Long idSchema;
	private SchemaEntity schemaEntity;
	private String stream;
	private byte[] bytes;
	private boolean isUnitTest;

	private boolean okEvent = false;
	private boolean koEvent = false;
	private boolean warnEvent = false;
	private List<EventTriggerEntity> okEventList;
	private List<EventTriggerEntity> koEventList;
	private List<EventTriggerEntity> warnEventList;
	private Object jvObject;
	private long numElemChecked;
	private ApplicationEntity appEntity;
	private String defaultNsLib;
	private boolean isLast;
	private boolean isFirst;

	ValidationCallable(Long idSchema, SchemaEntity schemaEntity, String stream, byte[] bytes, boolean isUnitTest,
			boolean okEvent, boolean koEvent, boolean warnEvent, List<EventTriggerEntity> okEventList,
			List<EventTriggerEntity> koEventList, List<EventTriggerEntity> warnEventList, Object object,
			long numElemChecked, ApplicationEntity appEntity, String defaultNsLib, boolean last, boolean first) {
		this.idSchema = idSchema;
		this.schemaEntity = schemaEntity;
		this.stream = stream;
		this.bytes = bytes;
		this.isUnitTest = isUnitTest;
		this.okEvent = okEvent;
		this.koEvent = koEvent;
		this.warnEvent = warnEvent;
		this.okEventList = okEventList;
		this.koEventList = koEventList;
		this.warnEventList = warnEventList;
		this.jvObject = object;
		this.numElemChecked = numElemChecked;
		this.appEntity = appEntity;
		this.defaultNsLib = defaultNsLib;
		this.isLast = last;
		this.isFirst = first;
	}

	@Override
	public Map<String, Object> call() {
		Map<String, Object> resMap = new HashMap<String, Object>();
		boolean bSuccess = true;
		int idStreamType = schemaEntity.getIdStreamType();

		DatastreamDTO datastreamDTO = new DatastreamDTO();
		DatastreamEntity datastreamEntity = new DatastreamEntity();

		setupDatastream(idStreamType, datastreamDTO, datastreamEntity);

		if (!isUnitTest) {
			if (appEntity != null && appEntity.getIsActive() != null) {
				if (appEntity.getIsActive() == 0) {
					resMap.put(DatastreamsInput._SINGLE_STR_RESP, I18n.getMessage("error.deactivatedApplication"));
					resMap.put(DatastreamsInput._SINGLE_SUCC_RESP, false);
					return resMap;
				} else if (schemaEntity.getIsActive() != null && schemaEntity.getIsActive() == 0) {
					resMap.put(DatastreamsInput._SINGLE_STR_RESP, I18n.getMessage("error.deactivatedSchema"));
					resMap.put(DatastreamsInput._SINGLE_SUCC_RESP, false);
					return resMap;
				}
			}
		}

		if (schemaEntity.getIdSchemaType() == SchemaType.STANDARD) { // Standard schema
			if (datastreamDTO.getSuccess()) {
				DatastreamsValidator dsValidator = new DatastreamsValidator();
				dsValidator.standardValidation(datastreamDTO, schemaEntity);

				if (datastreamDTO.getSuccess()) {
					datastreamEntity.setChecked(1);
				} else if (!datastreamDTO.getSuccess() && !datastreamDTO.isWarning()) {
					datastreamEntity.setChecked(0);
					bSuccess = false;
				} else if (!datastreamDTO.getSuccess() && datastreamDTO.isWarning()) {
					datastreamEntity.setChecked(2);
					bSuccess = false;
				}
			} else {
				bSuccess = false;
			}
		} else {

			// Before validation of index incremental, DB must be erased
			if ( isFirst && (schemaEntity.getIdDatabase() != 0) && (schemaEntity.getIsIndexedIncrement() == true)) {
				// After that last datastream was save (in case a user submits a CSV file) 
				// all informations saved on DB are cleaned
				try {
					deleteData();
				} catch (SQLException e) {
				}
			}

			if (!datastreamDTO.getSuccess()) {
				bSuccess = false;
			}
			else {
				
				boolean isWarning;
				boolean isKO;
				boolean isNotAvailable;
				
				if (schemaEntity.getIsAvailable() != null && schemaEntity.getIsAvailable() == 0) {
					isWarning = true;
					isNotAvailable = true;
					isKO = false;

				} else {
					DatastreamsValidator dsValidator = new DatastreamsValidator();
					dsValidator.allValidation(datastreamDTO, numElemChecked);
					isWarning = !datastreamDTO.getSuccess() && datastreamDTO.isWarning();
					isKO = !datastreamDTO.getSuccess() && !datastreamDTO.isWarning();
					isNotAvailable = false;
				}

				if (datastreamDTO.getSuccess() || isWarning) {
					
					datastreamEntity.setChecked(isWarning ? 2 : 1);

					synchronized ("mutex") {
						if (!isUnitTest && schemaEntity.getPublishToDb()
								&& datastreamDTO.getErrorLevel() != ValidationStep.FORMAL) {
							if (!isWarning/* success */ || (isWarning && schemaEntity.getIsWarnTolerance())) {
								StreamsUtils.publishStreamToDB(datastreamDTO);
							}
						}
						SchemaEntity loadingStream = schemasDao.hasLoadingStream(schemaEntity);
						if (loadingStream != null) {
							StreamsUtils.loadingStreamToDB(datastreamDTO, loadingStream);
						}
						if (!isNotAvailable) {
							String res = ForcastedFieldsValidation.validate(schemaEntity.getIdSchema());
							if (res != null) {
								isWarning = true;
								datastreamDTO.setWarning(true);
								datastreamDTO.setSuccess(false);
								datastreamDTO.setMessage(res);
								datastreamEntity.setChecked(2);
								
							}

						}
					}
					
					if (isNotAvailable) {
						datastreamDTO.setWarning(true);
						datastreamDTO.setSuccess(false);
						datastreamDTO.setMessage(I18n.getMessage("error.unavailableSchema"));
						datastreamEntity.setChecked(2);
						datastreamEntity.setMessage(I18n.getMessage("error.unavailableSchema"));
					}
					
					bSuccess = manageWarning(datastreamDTO, isWarning);

				} else if (isKO) {
					datastreamEntity.setChecked(0);
					bSuccess = false;

					if (koEvent) {
						startEvent(koEventList, datastreamDTO);
					}

				}
			} // Datastream success
			
			if (isLast) {

				String res = IndexIncrementalValidation.validate(schemaEntity.getIdSchema());
				boolean isWarning = false;
				if (res != null) {
					isWarning = true;
					datastreamDTO.setWarning(true);
					datastreamDTO.setSuccess(false);
					datastreamDTO.setMessage(res);
					datastreamEntity.setChecked(2);
				}

				bSuccess = manageWarning(datastreamDTO, isWarning);

			}			
		}

		datastreamEntity.setMessage(datastreamDTO.getMessage());
		if ((schemaEntity.getIsValid() == 1 && datastreamDTO.getSuccess() && !datastreamDTO.isWarning())
				|| (schemaEntity.getIsInValid() == 1 && !datastreamDTO.getSuccess() && !datastreamDTO.isWarning())
				|| (schemaEntity.getIsWarning() == 1 && !datastreamDTO.getSuccess() && datastreamDTO.isWarning())) {
			resMap.put(DatastreamsInput._SINGLE_STREAMENT_RESP, datastreamEntity);
		}
		resMap.put(DatastreamsInput._SINGLE_STR_RESP, datastreamDTO.getMessage().replaceAll("\n", "<br>"));
		resMap.put(DatastreamsInput._SINGLE_SUCC_RESP, bSuccess);
		return resMap;

	}

	private boolean manageWarning(DatastreamDTO datastreamDTO, boolean isWarning) {
		
		if (okEvent && !isWarning) {
			startEvent(okEventList, datastreamDTO);
		}
		if (warnEvent && isWarning) {
			startEvent(warnEventList, datastreamDTO);
		}

		return ! isWarning;
		
	}

	private void setupDatastream(int idStreamType, DatastreamDTO datastreamDTO, DatastreamEntity datastreamEntity) {
		datastreamDTO.setIdSchema(idSchema);
		datastreamDTO.setIdStreamType(schemaEntity.getIdStreamType());
		datastreamDTO.setInput(stream);
		datastreamDTO.setJvObject(jvObject);

		datastreamEntity.setIdSchema(datastreamDTO.getIdSchema());
		if (schemaEntity.getIdSchemaType() == SchemaType.STANDARD) {
			datastreamDTO.setSuccess(true);
			datastreamDTO.setOutput(datastreamDTO.getInput());
			datastreamDTO.setInput(datastreamDTO.getOutput().replaceAll(" xmlns=" + defaultNsLib + " ", " "));
			datastreamEntity.setDatastream(datastreamDTO.getInput());
		} else {
			datastreamEntity.setDatastream(datastreamDTO.getInput());

			if (idStreamType == StreamType.XML) {
				datastreamDTO.setSuccess(true);
				datastreamDTO.setOutput(datastreamDTO.getInput());
			} else if (idStreamType == StreamType.XMLEXI) {
				try {
					datastreamDTO.setInput(EXI.decode(bytes));
					datastreamEntity.setDatastream(datastreamDTO.getInput());
					datastreamDTO.setSuccess(true);
					datastreamDTO.setOutput(datastreamDTO.getInput());
				} catch (Exception e) {
					datastreamDTO.setSuccess(false);
					datastreamDTO.setMessage("Exi  Error");
					datastreamEntity.setChecked(0);
				}
			} else if (idStreamType == StreamType.flatFileFixedPosition) {
				CreateXMLFromFlatFileFixedPosition createXMLFromFlatFileFixedPosition = new CreateXMLFromFlatFileFixedPosition();
				createXMLFromFlatFileFixedPosition.createXMLFromFlatFileFixedPosition(datastreamDTO);
			} else if (idStreamType == StreamType.flatFileDelimited) {
				CreateXMLFromFlatFileDelimited createXMLFromFlatFileDelimited = new CreateXMLFromFlatFileDelimited();
				createXMLFromFlatFileDelimited.createXMLFromFlatFileDelimited(datastreamDTO);
			} else if (idStreamType == StreamType.JSON) {
				CreateXMLFromJSONFile createXMLFromJSONFile = new CreateXMLFromJSONFile();
				createXMLFromJSONFile.createXml(datastreamDTO);
			} else if (idStreamType == StreamType.EXCEL) {
				if (datastreamDTO.getInput().trim().startsWith("<" + Tag.TAG_ROOT + ">")) {
					datastreamDTO.setSuccess(true);
					datastreamDTO.setOutput(datastreamDTO.getInput());
				} else {
					// If message not starts with Root tag means its an
					// error message. Set the error message.
					datastreamDTO.setSuccess(false);
					datastreamDTO.setMessage(datastreamDTO.getInput());
				}
			}
		}
	}

	private void deleteData() throws SQLException {
		String schemaName = QuickDBRecognizer.getSchemaNamePlusVersion(schemaEntity);
		Connection connection = ConnectionPoolsSet.getConnection(schemaEntity.getIdDatabase());
		String deletesql = MessageFormat.format("DELETE FROM {0}", schemaName);
		Statement statement = connection.createStatement();
		statement.executeUpdate(deletesql);
		statement.close();

	}

	private void startEvent(List<EventTriggerEntity> eventList, DatastreamDTO datastreamDTO) {
		EventTriggerBuilder.getEventTrigger(eventList, datastreamDTO);
	}
}
