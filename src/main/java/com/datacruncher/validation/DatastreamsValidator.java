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

package com.datacruncher.validation;

import com.datacruncher.datastreams.DatastreamDTO;
import com.datacruncher.constants.ValidationStep;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.spring.AppContext;
import com.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;

public class DatastreamsValidator implements DaoSet {

	private final Logger log = Logger.getLogger(this.getClass());

	public DatastreamDTO allValidation(DatastreamDTO datastreamDTO, long numElemChecked) {
		ResultStepValidation resultValidation;
		try {
				Formal formal = (Formal)AppContext.getApplicationContext().getBean("FormalValidation");
				resultValidation = formal.formalValidation(datastreamDTO);
				if (!resultValidation.isValid) {
					datastreamDTO.setErrorLevel(ValidationStep.FORMAL);
				}
				if (resultValidation.isValid() && resultValidation.getJaxbObject() != null) {
                    datastreamDTO.setJaxbObject(resultValidation.getJaxbObject());
                    if (numElemChecked > 0)
                        resultValidation = new Logical().logicalValidation(datastreamDTO, resultValidation.getJaxbObject());

					if (resultValidation.isValid()) {
                        MacroRulesValidation macro = (MacroRulesValidation) AppContext.getApplicationContext().getBean(
                                "MacroRulesValidation");
                        resultValidation = macro.doValidation(datastreamDTO);
					}
				}
			datastreamDTO.setSuccess(resultValidation.isValid());
			datastreamDTO.setMessage(resultValidation.getMessageResult());
			datastreamDTO.setWarning(resultValidation.isWarning());
		} catch (Exception exception) {
			datastreamDTO.setSuccess(false);
			datastreamDTO.setMessage(I18n.getMessage("error.system"));
			log.error("ValidateXML - Exception : " + exception);
		}
		return datastreamDTO;
	}
    public DatastreamDTO standardValidation(DatastreamDTO datastreamDTO, SchemaEntity schemaEntity) {
        ResultStepValidation resultValidation;
        try {
            Temporary temporary = new Temporary();
            resultValidation = temporary.temporaryValidation(schemaEntity, appDao.find(schemaEntity.getIdApplication()));
            if (resultValidation.isValid()) {
                Formal formal = (Formal)AppContext.getApplicationContext().getBean("FormalValidation");
                resultValidation = formal.formalValidation(datastreamDTO);
            }
            datastreamDTO.setSuccess(resultValidation.isValid());
            datastreamDTO.setMessage(resultValidation.getMessageResult());
            datastreamDTO.setWarning(resultValidation.isWarning());
        } catch (Exception exception) {
            datastreamDTO.setSuccess(false);
            datastreamDTO.setMessage(I18n.getMessage("error.system"));
            log.error("ValidateXML - Standard - Exception : " + exception);
        }
        return datastreamDTO;
    }
}