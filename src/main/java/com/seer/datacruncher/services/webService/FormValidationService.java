/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.services.webService;

import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.spring.FormValidatorController;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.DomToOtherFormat;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;

@Path("/formvalidate")
public class FormValidationService implements DaoSet {

	private static final String RESERVED_CONST_SCHEMA_NUM = "fSchema";
	private static final String RESERVED_CONST_IS_JS_DISABLED = "isJsDisabled";
	private static final String RESERVED_CONST_TOKEN = "tokenParameter";
	private static final String RESPONSE_WRAPPER = "json2";
	private Logger log = Logger.getLogger(this.getClass());

	@Context
	private HttpServletResponse response;

	@Context
	private HttpServletRequest request;

	private static List<String> tokenList = Collections.synchronizedList(new ArrayList<String>());

	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String get(/* MultivaluedMap<String, String> formParams */) {
		String serverException = null;
		@SuppressWarnings("unchecked")
		Map<String, String[]> formParams = request.getParameterMap();
		String resp = null;
		try {
			Document doc = DomToOtherFormat.getDocBuilder().newDocument();
			Element rootElement = doc.createElement(Tag.TAG_ROOT);
			doc.appendChild(rootElement);
			long schemaId = 0;
			boolean isJsDisabled = false;
			boolean isTokenFound = false;
			for (Map.Entry<String, String[]> entry : formParams.entrySet()) {
				if (entry.getKey().equals(RESERVED_CONST_SCHEMA_NUM)) {
					String value = entry.getValue()[0];
					if (value != null && !value.isEmpty()) {
						schemaId = schemasDao.findByName(value).get(0).getIdSchema();
					}
					continue;
				} else if (entry.getKey().equals(RESERVED_CONST_IS_JS_DISABLED)) {
					isJsDisabled = true;
					continue;
				} else if (entry.getKey().equals(RESERVED_CONST_TOKEN)) {
					String token = entry.getValue()[0];
					if (token != null && !token.isEmpty() && tokenList.contains(token)) {
						tokenList.remove(token);
						isTokenFound = true;
					}
					continue;
				}
				Element element = doc.createElement(entry.getKey());
				String value = entry.getValue()[0];
				element.setTextContent(value);
				rootElement.appendChild(element);
			}
			if (!isTokenFound) {
				return formResponseOnError("notFoundToken", "Token not found");
			}
			if (schemaId == 0) {
				throw new RuntimeException(RESERVED_CONST_SCHEMA_NUM + " parameter is not specified on client side");
			}
			SchemaEntity schemaEnt = schemasDao.find(schemaId);
			if (schemaEnt.getIsActive() != 1) {
				return formResponseOnError("notActive", String.valueOf(schemaId));				
			}
			if (schemaEnt.getIsAvailable() != 1) {
				return formResponseOnError("notAvail", String.valueOf(schemaId));				
			}			
			FormValidatorController validator = new FormValidatorController();
			if (!isJsDisabled) {
				resp = validator.validateForm(schemaId, DomToOtherFormat.convertDomToXml(doc));
			} else {
				Map<String, String> errMap = validator.validateFormReturnMap(schemaId,
						DomToOtherFormat.convertDomToXml(doc));
				if (errMap != null) {
					request.setAttribute("errMap", errMap);
					request.getRequestDispatcher("/jsp/validationErrorJsDisabled.jsp").forward(request, response);
				}
			}
		} catch (ServletException e) {
			log.error("FormValidationService :: servlet forward exception", e);
			serverException = CommonUtils.getExceptionAsString(e);
		} catch (JSONException e) {
			log.error("FormValidationService :: json tranform exception", e);
			serverException = CommonUtils.getExceptionAsString(e);
		} catch (IOException e) {
			log.error("FormValidationService :: IO exception", e);
			serverException = CommonUtils.getExceptionAsString(e);
		} catch (Exception e) {
			log.error("FormValidationService :: Exception", e);
			serverException = CommonUtils.getExceptionAsString(e);
		}
		if (serverException != null) {
			JSONObject jsonReturn = new JSONObject();
			try {
				return RESPONSE_WRAPPER + "(" + jsonReturn.put("serverException", serverException).toString() + ")";
			} catch (JSONException e) {
				log.error("FormValidationService :: json tranform exception2", e);
			}
		}
		return RESPONSE_WRAPPER + "(" + resp + ")";
	}
	
	/**
	 * Forms warning or error response to render in client's popup.
	 * 
	 * @param field of json
	 * @param value of json
	 * @return
	 * @throws JSONException
	 */
	private String formResponseOnError(String field, String value) throws JSONException {
		JSONObject jsonReturn = new JSONObject();
		return RESPONSE_WRAPPER + "(" + jsonReturn.put(field, value).toString() + ")";
	}

	@Path("/gettoken")
	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String getToken() throws JSONException {
		JSONObject jsonReturn = new JSONObject();
		String token = (UUID.randomUUID()).toString();
		tokenList.add(token);
		return "json1(" + jsonReturn.put("success", token).toString() + ")";
	}
}
