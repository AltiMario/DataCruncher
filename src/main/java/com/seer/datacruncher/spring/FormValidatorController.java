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

import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.DatastreamDTO;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.utils.generic.DomToOtherFormat;
import com.seer.datacruncher.validation.Formal;
import com.seer.datacruncher.validation.Logical;
import com.seer.datacruncher.validation.ResultStepValidation;
import com.seer.datacruncher.validation.Temporary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Controller
public class FormValidatorController implements DaoSet {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	private static final String _PARAM_SCHEMA_TYPE = "schemaType";
	private static final String EXCLUDE_PARAMS = "excludeParams";
	
	/**
	 * Validate form.
	 * Income request MIME: application/x-www-form-urlencoded; charset=UTF-8
	 * 
	 * @return true/false as String
	 * @throws IOException
	 */
	@RequestMapping(value = "controller.validateForm.json")
	public void validateFormAndWriteResponse(@RequestParam(_PARAM_SCHEMA_TYPE) String schemaType, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
            String formParametersXml = getXmlFromUrlEncoded(request);
			PrintWriter out = null;
			response.setContentType("application/json");
			out = response.getWriter();
			out.print(validateForm(schemasDao.findByName(schemaType).get(0).getIdSchema(), formParametersXml));
			out.flush();
			out.close();
		} catch (JSONException e) {
			log.error("FormValidatorController :: validateFormAndWriteResponse", e);
		}
	}
	
	/**
	 * Validates form and returns the validation result as a JSON string.
	 * This result is special formed, it can be understood by extJs for later
	 * highlight (red color) of wrong form fields.
	 * 
	 * @param schemaId
	 * @param formParametersXml
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public String validateForm(long schemaId, String formParametersXml) throws IOException,
			JSONException {
		Map<String, String> err = validateFormReturnMap(schemaId, formParametersXml);
		JSONObject jsonReturn = new JSONObject();
		if (err == null) {
			jsonReturn.put("success", true);
		} else {
			jsonReturn.put("success", false);
			jsonReturn.put("errors", new JSONObject(err));
		}
		return jsonReturn.toString();
	}
	
	/**
	 * Validates form and returns the validation result as a map.
	 * 
	 * @param schemaId
	 * @param formParametersXml
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public Map<String, String> validateFormReturnMap(long schemaId, String formParametersXml) throws IOException, JSONException {
		DatastreamDTO dataStreamDTO = new DatastreamDTO();
		dataStreamDTO.setIdSchema(schemaId);
		dataStreamDTO.setSuccess(true);
		dataStreamDTO.setOutput(formParametersXml);
        Temporary temporary = new Temporary();
        SchemaEntity schemaEnt = schemasDao.find(schemaId);
        ApplicationEntity appEnt = appDao.find(schemaEnt.getIdApplication());
		ResultStepValidation resultValidation = temporary.temporaryValidation(schemaEnt,
				appEnt);
		//firstStagesFail: temporary validation mismatch or not available or not active schema
		boolean firstStagesFail = !resultValidation.isValid() || appEnt.getIsActive() != 1
				|| schemaEnt.getIsActive() != 1 || schemaEnt.getIsAvailable() != 1;
		if (resultValidation.isValid()) {
			Formal formal = (Formal) AppContext.getApplicationContext().getBean("FormalValidation");
			resultValidation = formal.formalValidation(dataStreamDTO);
			if (resultValidation.getJaxbObject() != null) {
				dataStreamDTO.setJaxbObject(resultValidation.getJaxbObject());
				if (schemaFieldsDao.findNumExtraCheck(schemaId) > 0)
					resultValidation = new Logical().logicalValidation(dataStreamDTO, resultValidation.getJaxbObject());
			}
		}
		dataStreamDTO.setMessage(resultValidation.getMessageResult());
		dataStreamDTO.setSuccess(resultValidation.isValid());
		dataStreamDTO.setWarning(resultValidation.isWarning());		
		storeStream(dataStreamDTO);
		Map<String, String> err = null;		
		if (!resultValidation.isValid() && !firstStagesFail) {
			err = new HashMap<String, String>();
			for (String pathWithErrMsg : resultValidation.getFailedNodesPaths()) {
				//pathWithErrMsg example: 'root\field$$errorMessageForHint'
				String fieldInfo = pathWithErrMsg.substring(pathWithErrMsg.indexOf("\\") + 1);
				String fieldName = fieldInfo.split("[$$]")[0];
				String fieldErrMsg = fieldInfo.split("[$][$]")[1];
				if (err.get(fieldName) == null) {
					err.put(fieldName, fieldErrMsg);
				} else {
					//when more than 1 errors for one field
					err.put(fieldName, err.get(fieldName) + "<br>" + fieldErrMsg);
				}
			}	
		}
		return err;
	}
	
	/**
	 * Create xml from submitted form data. 
	 * 
	 * @param request
	 * @return - xml string
	 * @throws IOException
	 */
	private String getXmlFromUrlEncoded(HttpServletRequest request) throws IOException {
		Document doc = DomToOtherFormat.getDocBuilder().newDocument();
		Element rootElement = doc.createElement(Tag.TAG_ROOT);
		doc.appendChild(rootElement);
		boolean isEntered = false;
		String excludeParams = request.getParameter(EXCLUDE_PARAMS) == null ? "" : request.getParameter(EXCLUDE_PARAMS);
		for (Object o : request.getParameterMap().entrySet()) {
			isEntered = true;
			@SuppressWarnings("unchecked")
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) o;
			// skip params that don't belong to editor fields
			if (entry.getKey().equals(_PARAM_SCHEMA_TYPE) || entry.getKey().indexOf(EXCLUDE_PARAMS) != -1 || excludeParams.indexOf(entry.getKey()) != -1)
				continue;
			Element element = doc.createElement(entry.getKey());
			element.setTextContent(entry.getValue()[0]);
			rootElement.appendChild(element);
		}
		return isEntered ? DomToOtherFormat.convertDomToXml(doc) : null;
	}
	
	/**
	 * Stores stream to database.
	 * 
	 * @param datastreamDTO
	 */
	private void storeStream(DatastreamDTO datastreamDTO) {
        boolean isWarning = !datastreamDTO.getSuccess() && datastreamDTO.isWarning();
        boolean isKO = !datastreamDTO.getSuccess() && !datastreamDTO.isWarning();
		DatastreamEntity datastreamEntity = new DatastreamEntity();
		datastreamEntity.setChecked(isWarning ? 2 : isKO ? 0 : 1);
		datastreamEntity.setIdSchema(datastreamDTO.getIdSchema());
		datastreamEntity.setDatastream(datastreamDTO.getOutput());		
		datastreamEntity.setMessage(datastreamDTO.getMessage());
		datastreamsDao.create(datastreamEntity);
	}
}
