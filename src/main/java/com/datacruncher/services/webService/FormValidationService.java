/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */
package com.datacruncher.services.webService;

import com.datacruncher.constants.DateTimeType;
import com.datacruncher.constants.Tag;
import com.datacruncher.datastreams.DataStreamBuilder;
import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.*;
import com.datacruncher.spring.FormValidatorController;
import com.datacruncher.utils.generic.CommonUtils;
import com.datacruncher.utils.generic.DomToOtherFormat;
import com.datacruncher.utils.schema.SchemaValidator;
import com.datacruncher.validation.*;
import com.datacruncher.validation.common.RegexCheck;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Path("/formvalidate")
public class FormValidationService implements DaoSet {

    private static final String PARAMETER_SCHEMA = "schema";
    private static final String PARAMETER_IS_JS_DISABLED = "isJsDisabled";
    private static final String PARAMETER_TOKEN = "tokenParameter";
    private static final String FUNCTION_TOKEN_RESPONSE = "json1";
    private static final String FUNCTION_GET_RESPONSE = "json2";
    private static final String FUNCTION_JSONFORM_RESPONSE = "jsonForm";
    private static final String FUNCTION_JSONFORMFIELD_RESPONSE = "jsonFormField";
    private static final String FUNCTION_JSONFORMTEMPORAL_RESPONSE = "jsonFormTemporal";
    private static final String FUNCTION_JSONFORMVALIDATE_RESPONSE = "jsonFormValidate";
    private Logger log = Logger.getLogger(this.getClass());

    @Context
    private HttpServletResponse response;

    @Context
    private HttpServletRequest request;

    private static List<String> tokenList = Collections.synchronizedList(new ArrayList<String>());

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String get(/* MultivaluedMap<String, String> formParams */) {
        String serverException = null;
        @SuppressWarnings("unchecked")
        Map<String, String[]> formParams = request.getParameterMap();
        String resp = null;
        try {
            Document doc = DomToOtherFormat.getDocBuilder().newDocument();
            Element rootElement = doc.createElement(Tag.TAG_ROOT);
            doc.appendChild(rootElement);
            boolean isJsDisabled = false;
            boolean isTokenFound = false;
            SchemaEntity schemaEntity = null;
            for (Map.Entry<String, String[]> entry : formParams.entrySet()) {
                if (entry.getKey().equals(PARAMETER_SCHEMA)) {
                    String value = entry.getValue()[0];
                    if (value != null && !value.isEmpty()) {
                        schemaEntity = schemasDao.findByName(value).get(0);
                    }
                    continue;
                } else if (entry.getKey().equals(PARAMETER_IS_JS_DISABLED)) {
                    isJsDisabled = true;
                    continue;
                } else if (entry.getKey().equals(PARAMETER_TOKEN)) {
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
            if (schemaEntity == null) {
                throw new RuntimeException(PARAMETER_SCHEMA + " parameter is not specified on client side");
            }
            long schemaId = schemaEntity.getIdSchema();
            if (schemaEntity.getIsActive() != 1) {
                return formResponseOnError("notActive", String.valueOf(schemaId));
            }
            if (schemaEntity.getIsAvailable() != 1) {
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
                return FUNCTION_GET_RESPONSE + "(" + jsonReturn.put("serverException", serverException).toString() + ")";
            } catch (JSONException e) {
                log.error("FormValidationService :: json tranform exception2", e);
            }
        }
        return createJsonpResult(FUNCTION_GET_RESPONSE, resp);
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
        return createJsonpResult(FUNCTION_GET_RESPONSE, new JSONObject().put(field, value).toString());
    }

    @Path("/gettoken")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken() throws JSONException {
        JSONObject jsonReturn = new JSONObject();
        String token = (UUID.randomUUID()).toString();
        tokenList.add(token);
        return createJsonpResult(FUNCTION_TOKEN_RESPONSE, jsonReturn.put("success", token).toString());
    }

    @Path("/jsonform")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String getJsonForm(@Context UriInfo uriInfo) {
        try {
            String token = uriInfo.getQueryParameters().getFirst(PARAMETER_SCHEMA);
            if (token != null && !token.isEmpty() && tokenList.contains(token)) {
                return formResponseOnError("notFoundToken", "Token not found");
            }
            final String jsonFormContent = generateJsonForm(uriInfo);
            return createJsonpResult(FUNCTION_JSONFORM_RESPONSE, jsonFormContent);
        } catch (Exception e) {
            log.error(e);
            return createJsonpResult(FUNCTION_JSONFORM_RESPONSE, e);
        }
    }

    @Path("/jsonform-no-security")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJsonFormNoSecurity(@Context UriInfo uriInfo) throws Exception {
        final String jsonFormContent = generateJsonForm(uriInfo);
        return Response.ok(jsonFormContent).build();
    }

    public String generateJsonForm(UriInfo uriInfo) throws Exception {
        SchemaEntity schemaEntity = getSchemaEntity(uriInfo);
        SchemaXSDEntity schemaXsdEntity = getXsdEntity(schemaEntity);
        File workingDirectory = createWorkingDirectory();
        try {
            String jsonFormContent = schemaXsdEntity.getJsonForm();
            if (StringUtils.isBlank(jsonFormContent)) {
                final JsonFormBuilder jsonFormBuilder = new JsonFormBuilder();
                final File xsdSchemaFile = createXsdSchemaFile(workingDirectory, schemaEntity, schemaXsdEntity);
                JsonForm jsonForm = jsonFormBuilder.build(xsdSchemaFile, workingDirectory);
                final JsonNode jsonSchema = jsonFormBuilder.getJsonSchema();
                ((ObjectNode) jsonSchema).put("title", schemaEntity.getDescription());
                addCustomErrors(schemaEntity, jsonSchema);
                jsonFormContent = jsonForm.toString();
                schemaXsdEntity.setJsonForm(jsonFormContent);
                schemasXSDDao.update(schemaXsdEntity);
            }
            return jsonFormContent;
        } finally {
            try {
                FileUtils.deleteDirectory(workingDirectory);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    @Path("/field")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String validateJsonFormField(@Context UriInfo uriInfo) {
        try {
            String token = uriInfo.getQueryParameters().getFirst(PARAMETER_SCHEMA);
            if (token != null && !token.isEmpty() && tokenList.contains(token)) {
                return formResponseOnError("notFoundToken", "Token not found");
            }
            String value = uriInfo.getQueryParameters().getFirst("value");
            final String fieldName = uriInfo.getQueryParameters().getFirst("name");
            final String regexPattern = uriInfo.getQueryParameters().getFirst("regex");
            SchemaEntity schemaEntity = getSchemaEntity(uriInfo);
            final SchemaFieldEntity field = schemaFieldsDao.getSchemaAllFieldByName(schemaEntity.getIdSchema(), fieldName);
            if (field != null && field.isDateField()) {
                value = formatDateFieldValue(field, value);
            }
            CompositeResultStepValidation compositeResult = new CompositeResultStepValidation();
            if (StringUtils.isNotBlank(regexPattern)) {
                final RegexCheck regexCheck = new RegexCheck(regexPattern);
                final ResultStepValidation regexValidationResult = regexCheck.checkValidity(value);
                compositeResult.addResult(regexValidationResult);
            }
            final String rulesAnnotation = uriInfo.getQueryParameters().getFirst("annotation");
            if (StringUtils.isNotBlank(rulesAnnotation)) {
                String[] rules = rulesAnnotation.split(",");
                final Logical logical = new Logical();
                for (String rule : rules) {
                    final ChecksTypeEntity checkType = checksTypeDao.getChecksTypeByDescr(rule.trim());
                    if (!checkType.isSingleValidation() || !checkType.isCoded()) {
                        continue;
                    }
                    ResultStepValidation ruleValidationResult = logical.performSingleCheck(checkType, value);
                    compositeResult.addResult(ruleValidationResult);
                }
            }
            JsonFormErrorResult result = new JsonFormErrorResult();
            result.setErrors(compositeResult.getErrorMessages());
            return createJsonpResult(FUNCTION_JSONFORMFIELD_RESPONSE, result);
        } catch (Exception e) {
            log.error(e);
            return createJsonpResult(FUNCTION_JSONFORMFIELD_RESPONSE, e);
        }
    }

    @Path("/temporal")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String validateJsonFormTemporal(@Context UriInfo uriInfo) {
        try {
            String token = uriInfo.getQueryParameters().getFirst(PARAMETER_SCHEMA);
            if (token != null && !token.isEmpty() && tokenList.contains(token)) {
                return formResponseOnError("notFoundToken", "Token not found");
            }
            JsonFormErrorResult result = new JsonFormErrorResult();
            SchemaEntity schemaEntity = getSchemaEntity(uriInfo);
            ApplicationEntity appEntity = appDao.find(schemaEntity.getIdApplication());
            try {
                ValidationUtils.checkActive(appEntity, schemaEntity);
            } catch (Exception e) {
                result.getErrors().add(e.getMessage());
            }
            Temporary temporary = new Temporary();
            ResultStepValidation ruleValidationResult = temporary.temporaryValidation(schemaEntity, appEntity);
            if (!ruleValidationResult.isValid()) {
                result.getErrors().add(ruleValidationResult.getMessageResult());
            }
            return createJsonpResult(FUNCTION_JSONFORMTEMPORAL_RESPONSE, result);
        } catch (Exception e) {
            log.error(e);
            return createJsonpResult(FUNCTION_JSONFORMTEMPORAL_RESPONSE, e);
        }
    }

    @Path("/validate-jsonform")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String validateJsonForm(@Context UriInfo uriInfo) {
        String serverException = null;
        @SuppressWarnings("unchecked")
        Map<String, String[]> formParams = request.getParameterMap();
        String resp = null;
        try {
            Document doc = DomToOtherFormat.getDocBuilder().newDocument();
            Element rootElement = doc.createElement(Tag.TAG_ROOT);
            doc.appendChild(rootElement);
            boolean isTokenFound = false;
            SchemaEntity schemaEntity = null;
            Map<String, Object> fieldValues = new HashMap<>();
            for (Map.Entry<String, String[]> entry : formParams.entrySet()) {
                if (entry.getKey().equals(PARAMETER_SCHEMA)) {
                    String value = entry.getValue()[0];
                    if (value != null && !value.isEmpty()) {
                        schemaEntity = schemasDao.findByName(value).get(0);
                    }
                    continue;
                } else if (entry.getKey().equals(PARAMETER_TOKEN)) {
                    String token = entry.getValue()[0];
                    if (token != null && !token.isEmpty() && tokenList.contains(token)) {
                        tokenList.remove(token);
                        isTokenFound = true;
                    }
                    continue;
                } else {
                    fieldValues.put(entry.getKey().toUpperCase(), entry.getValue()[0]);
                }
                Element element = doc.createElement(entry.getKey());
                String value = entry.getValue()[0];
                element.setTextContent(value);
                rootElement.appendChild(element);
            }
            if (!isTokenFound) {
                return formResponseOnError("notFoundToken", "Token not found");
            }
            if (schemaEntity == null) {
                throw new RuntimeException(PARAMETER_SCHEMA + " parameter is not specified on client side");
            }
            long schemaId = schemaEntity.getIdSchema();
            if (schemaEntity.getIsActive() != 1) {
                return formResponseOnError("notActive", String.valueOf(schemaId));
            }
            if (schemaEntity.getIsAvailable() != 1) {
                return formResponseOnError("notAvail", String.valueOf(schemaId));
            }

            // Date field should be converted from timestamp to formatted value
            final List<SchemaFieldEntity> dateFields =
                    schemaFieldsDao.findDateTimeFields(schemaId);
            for (SchemaFieldEntity field : dateFields) {
                String fieldName = field.getName().toUpperCase();
                if (!fieldValues.containsKey(fieldName)) {
                    continue;
                }
                fieldValues.put(fieldName, formatDateFieldValue(field, String.valueOf(fieldValues.get(fieldName))));
            }

            String dataStreamContent = new DataStreamBuilder(schemaFieldsDao)
                    .setFieldValues(fieldValues)
                    .build(schemaEntity);
            resp = new DatastreamsInput().datastreamsInput(dataStreamContent, schemaId, null);
        } catch (Exception e) {
            log.error("FormValidationService :: Exception", e);
            serverException = CommonUtils.escapeJsonString(CommonUtils.getExceptionAsString(e));
        }
        if (serverException != null) {
            JSONObject jsonReturn = new JSONObject();
            try {
                return FUNCTION_JSONFORMVALIDATE_RESPONSE + "(" + jsonReturn.put("serverException", serverException).toString() + ")";
            } catch (JSONException e) {
                log.error("FormValidationService :: json tranform exception2", e);
            }
        }
        return createJsonpResult(FUNCTION_JSONFORMVALIDATE_RESPONSE, String.format("'%s'", resp));
    }

    private String formatDateFieldValue(SchemaFieldEntity field, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String result = value;
        if (field != null && field.isDateField()) {
            try {
                long timestamp = Long.parseLong(value);
                result = DateTimeType.formatDate(
                        field.getIdDateTimeType(), field.getIdDateType(), field.getIdTimeType(), timestamp);
            } catch (NumberFormatException e) {
                log.warn(e);
            }
        }
        return result;
    }

    /**
     * Add 'error' property to JSON schema fields that have custom error message defined
     *
     * @param schemaEntity
     * @param jsonSchema
     */
    private void addCustomErrors(SchemaEntity schemaEntity, JsonNode jsonSchema) {
        final Map<String, Long> fieldMap = schemaFieldsDao.listSchemaFields(schemaEntity.getIdSchema()).stream()
                .filter(f -> f.hasCustomError())
                .collect(Collectors.toMap(k -> k.getName().toUpperCase(), v -> (Long) v.getIdCustomError()));
        if (fieldMap.isEmpty()) {
            return;
        }
        final Map<Long, String> errorMap = customErrorsDao.read(schemaEntity.getIdSchema()).getResults().stream()
                .map(e -> (CustomErrorEntity) e)
                .collect(Collectors.toMap(k -> (Long) k.getId(), v -> v.getDescription()));
        if (fieldMap.isEmpty()) {
            return;
        }
        final Iterator<Map.Entry<String, JsonNode>> propertyIterator = jsonSchema.get("properties").fields();
        while (propertyIterator.hasNext()) {
            final Map.Entry<String, JsonNode> propertyEntry = propertyIterator.next();
            final ObjectNode property = (ObjectNode) propertyEntry.getValue();
            if (fieldMap.containsKey(propertyEntry.getKey().toUpperCase())) {
                final Long errorId = fieldMap.get(propertyEntry.getKey().toUpperCase());
                if (errorMap.containsKey(errorId)) {
                    property.put("error", errorMap.get(errorId));
                }
            }
        }
    }

    private SchemaEntity getSchemaEntity(UriInfo uriInfo) throws Exception {
        final String schemaName = uriInfo.getQueryParameters().getFirst("schema");
        final List<SchemaEntity> schemaEntityList = schemasDao.findByName(schemaName.trim());
        if (schemaEntityList.isEmpty()) {
            throw new Exception(String.format("Schema not found: %s", schemaName));
        }
        return schemaEntityList.get(0);
    }

    private SchemaXSDEntity getXsdEntity(SchemaEntity schemaEntity) throws Exception {
        SchemaValidator schemaValidator = new SchemaValidator();
        Map<String, String> validateSchemaMap = schemaValidator.validateSchema(schemaEntity.getIdSchema());
        if (!schemaValidator.isValidationSuccessful()) {
            log.error(CommonUtils.toJsonString(validateSchemaMap));
            throw new Exception(String.format("Schema XSD not found: %d", schemaEntity.getIdSchema()));
        }
        return schemasXSDDao.read(schemaEntity.getIdSchema());
    }

    private File createWorkingDirectory() {
        return Paths.get(System.getProperty("java.io.tmpdir"),
                getClass().getSimpleName() + System.currentTimeMillis()).toFile();
    }

    private File createXsdSchemaFile(File workingDirectory, SchemaEntity schemaEntity, SchemaXSDEntity schemaXsdEntity)
            throws IOException {
        File xsdSchemaFile = new File(workingDirectory,
                String.format("schema-%d-%d.xsd", schemaEntity.getIdSchema(), Thread.currentThread().getId()));
        FileUtils.writeStringToFile(xsdSchemaFile, schemaXsdEntity.getSchemaXSD());
        return xsdSchemaFile;
    }

    private String createJsonpResult(String function, Exception e) {
        return createJsonpResult(function, CommonUtils.getExceptionAsString(e));
    }

    private String createJsonpResult(String function, JsonFormErrorResult result) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        return createJsonpResult(function, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

    private String createJsonpResult(String function, String result) {
        return String.format("%s(%s)", function, result);
    }
}
