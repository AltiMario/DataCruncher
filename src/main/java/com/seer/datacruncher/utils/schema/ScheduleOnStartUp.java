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

package com.seer.datacruncher.utils.schema;

import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;
import com.seer.datacruncher.validation.SpellChecker;
import com.seer.datacruncher.validation.custom.ComuniItaliani;
import com.seer.datacruncher.validation.custom.NomiItaliani;
import org.apache.log4j.Logger;

import java.util.List;

public class ScheduleOnStartUp implements DaoSet {
	private static Logger log = Logger.getLogger(ScheduleOnStartUp.class.getName());

	public void executeInternal() {
        logDao.setInfoLogMessage("DataCruncher start");
		SchemaCodeGenerator codeGenerator = new SchemaCodeGenerator();
		List<SchemaXSDEntity> schemaXsdEntityList = schemasXSDDao.findAll();
		if (schemaXsdEntityList != null && schemaXsdEntityList.size() > 0) {
			for (SchemaXSDEntity entity : schemaXsdEntityList) {
				try {
					//skip system schemas
					/*if (schemasDao.find(entity.getIdSchemaXSD()) != null
							&& schemasDao.find(entity.getIdSchemaXSD()).getIsSystem()) {
						continue;
					} */
					//xsdId == schemaId
					long xsdId = entity.getIdSchemaXSD();
					if (schemasDao.find(xsdId) == null) {
						// this can happen then only schema deleted from DB and
						// its xsd representation still persists
						schemasXSDDao.destroy(xsdId);
					} else {
						codeGenerator.generateJAXBStuffFromSchema(xsdId, entity.getSchemaXSD().getBytes());
					}
				} catch (SchemaParsingException e) {
                    logDao.setErrorLogMessage("Error in the class generation on startup "+e.getMessage());
					log.error("Error in the class generation on startup ", e);
				}
			}
		}

		// load in memory the dictionaries
		try {
			new SpellChecker();
			log.info("Dictionaries loaded");
		} catch (Exception e) {
            logDao.setErrorLogMessage("Error in dictionary stored in memory"+e.getMessage());
			log.error("Error in dictionary stored in memory ", e);
		}

		// load in memory the list of italian cities
		try {
			new ComuniItaliani();
			log.info("'Comuni italiani' loaded");
		} catch (Exception e) {
            logDao.setErrorLogMessage("Error in 'Comuni italiani' stored in memory "+e.getMessage());
			log.error("Error in 'Comuni italiani' stored in memory ", e);
		}

		// load in memory the list of italian male names, female names and surnames
		try {
			new NomiItaliani();
			log.info("'Nomi italiani' loaded");
		} catch (Exception e) {
			log.error("Error in 'Nomi italiani' stored in memory ", e);
		}
		
        // load in memory the list standard library
        try {
            schemaLibDao.init();
            log.info("'Standard Library' loaded");
        } catch (Exception e) {
            logDao.setErrorLogMessage("Error in 'Standard Library' stored in memory:"+e.getMessage());
            log.error("Error in 'Standard Library' stored in memory ", e);
        }

        // load in memory the list default checks
        try {
            checksTypeDao.init();
            log.info("'Default Checks' loaded");
        } catch (Exception e) {
            log.error("Error in 'Default Checks' stored in memory ", e);
        }

        //load default data
        try {
            creditsDao.init();
            appDao.init();
            schemasDao.init();
            schemaFieldsDao.init();
            eventTriggerDao.init();
            schemaTriggerStatusDao.init();
            jobsDao.setAllJobsWorkFalse();
            logDao.setInfoLogMessage("Default data loaded");
            log.info("Default data loaded");
        } catch (Exception e) {
            logDao.setErrorLogMessage("Error in loading default data:"+e.getMessage());
            log.error("Error in loading default data", e);
        }
        try{
            long maxRow= 1000L;
            Destroy destroy=logDao.deteteRows(maxRow);
            if(!destroy.isSuccess()){
                logDao.setErrorLogMessage("Error in deleting log table:"+destroy.getMessage());
                log.error("Error in deleting log table. "+ destroy.getMessage());
            }
        } catch (Exception e) {
            logDao.setErrorLogMessage("Error in deleting log table:"+e.getMessage());
            log.error("Error in deleting log table", e);
        }
	}
}