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

package com.datacruncher.utils.schema;


import com.datacruncher.constants.SchemaType;
import com.datacruncher.datastreams.CreateXSDJAXB;
import com.datacruncher.spring.AppContext;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaXSDEntity;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class SchemaValidator implements DaoSet {

	private Logger log = Logger.getLogger(this.getClass());
	private boolean isValidationSucc = false;
	private String schemaCantaints = null;
	

	public Map<String, String> validateSchema(long schemaId) {
		Map<String, String> resMap = new HashMap<String, String>();
		String success = "true";
		String responseMsg = "";
		SchemaXSDEntity schemaXSDEntity = schemasXSDDao.read(schemaId);
        String createRoot = "standard schema";
		if (schemaXSDEntity == null) {
			success = "false";
			try {
				schemaXSDEntity = new SchemaXSDEntity();
				schemaXSDEntity.setIdSchemaXSD(schemaId);
				if (schemasDao.find(schemaId).getIdSchemaType() != SchemaType.STANDARD) {
					CreateXSDJAXB createXSDJAXB = (CreateXSDJAXB) AppContext.getApplicationContext().getBean(
							"CreateXSDJAXB");
					createRoot = createXSDJAXB.createRoot(schemaId);
					schemaXSDEntity.setIsVersIncreaseNeeded(true);
					schemaXSDEntity.setSchemaXSD(createRoot);

				} else {
					schemaXSDEntity.setSchemaXSD(createRoot);
				}
				schemasXSDDao.create(schemaXSDEntity);

				schemaXSDEntity = schemasXSDDao.read(schemaId);
				if (schemaXSDEntity != null) {
					SchemaCodeGenerator generator = new SchemaCodeGenerator();
					JaxbGenerationResults results = null;
					schemaCantaints = schemaXSDEntity.getSchemaXSD();
					try {
						results = generator.generateJAXBStuffFromSchema(schemaId, schemaCantaints.getBytes());

					} catch (SchemaParsingException e) {
						success = "false";
						log.error("Error in the class generation on schema validation ", e);
					} finally {
						String msg = "";
						if (results == null) {
							results = generator.getGenResults();
						}
						for (Map<String, String> map : results.getGenerationResults()) {
							boolean isSucc = map.get("success").equals("true");
							msg += (isSucc ? "success" : "failed") + ": " + map.get("msg") + "<br>";
							if (!isSucc) {
								msg += "<br>" + "Stack trace: <br>" + map.get("stackTrace");
								break;
							}
						}
						if (!results.isGenerationSuccessful()) {
							success = "false";
						} else {
							success = "true";

						}
						responseMsg = msg;
					}
				} else {
					String failMsg = I18n.getMessage("error.XsdCreate");
					log.error(failMsg);
					success = "false";
					responseMsg = failMsg;
				}
			} catch (Exception e) {
				responseMsg = I18n.getMessage("error.XsdCreate");
				success = "false";
                log.error(responseMsg +": "+e.getMessage());
			}
		} else {
			schemaCantaints = schemaXSDEntity.getSchemaXSD();
		}
		resMap.put("success", success);
		resMap.put("responseMsg", responseMsg);		
		isValidationSucc = Boolean.parseBoolean(success);
		if (!isValidationSucc) {
			schemaCantaints = "";
			schemasXSDDao.destroy(schemaId);
		}
		return resMap;
	}
	
	public boolean isValidationSuccessful() {
		return isValidationSucc;
	}
	
	public String getSchemaCantaints() {
		return schemaCantaints;
	}
}
