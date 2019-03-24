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

package com.datacruncher.spring;

import com.datacruncher.constants.FieldType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.jpa.entity.SchemaTriggerStatusEntity;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaDuplicateController implements Controller, DaoSet {
    private long schemaId = 0;


    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws ServletException, IOException {
        String primarySchemaId = request.getParameter("primarySchemaId");
        Create c = duplicateSchema(primarySchemaId);
        schemaId = c.getSuccess() ? ((SchemaEntity) c.getResults()).getIdSchema()
                : 0;
        SchemaEntity source = schemasDao.find(Long.parseLong(primarySchemaId));
        ReadList readTriggersList = schemaTriggerStatusDao.findByIdSchema(source.getIdSchema());
		if(CollectionUtils.isNotEmpty(readTriggersList.getResults())){
			SchemaTriggerStatusEntity schemaTriggerStatusEntity = (SchemaTriggerStatusEntity) readTriggersList
					.getResults().get(0);
            source.setSchemaEvents(schemaTriggerStatusEntity);
        }
		SchemaTriggerStatusEntity schemaTriggerStatusEntity = source.getSchemaEvents();
        if(schemaTriggerStatusEntity != null && schemaTriggerStatusEntity.getIdEventTrigger()>0){
            SchemaTriggerStatusEntity stse = new SchemaTriggerStatusEntity();
            stse.setIdEventTrigger(schemaTriggerStatusEntity.getIdEventTrigger());
            stse.setIdSchema(schemaId);
            stse.setIdStatus(schemaTriggerStatusEntity.getIdStatus());
        	Create create =  schemaTriggerStatusDao.create(stse);
        }
        
        int streamType = c.getSuccess() ? ((SchemaEntity) c.getResults()).getIdStreamType()
                : 0;
        Map<String, String> resMap = new HashMap<String, String>();
        String success = "true";
        if (schemaId > 0) {
            if(streamType == StreamType.XML || streamType == StreamType.XMLEXI || streamType == StreamType.JSON ){
                SchemaFieldEntity rootEntity = schemaFieldsDao.root(Long
                        .parseLong(primarySchemaId));
                // clone(duplicate) root node
                if (rootEntity != null) {
                    long fieldId = cloneNode(rootEntity, 0);
                    long rootFieldId = rootEntity.getIdSchemaField();
                    duplicateChildren(rootFieldId, fieldId);
                } else if(streamType == StreamType.JSON){
                	List<SchemaFieldEntity> rootNonXMLEntity = schemaFieldsDao.rootForNonXML(Long.parseLong(primarySchemaId));
                    if(rootNonXMLEntity != null){
                        for (SchemaFieldEntity schemaFieldEntity : rootNonXMLEntity) {
                        	if(schemaFieldEntity.getIdFieldType() == FieldType.all) {
                        		long fieldId = cloneNode(schemaFieldEntity, 0);
                        		long rootFieldId = schemaFieldEntity.getIdSchemaField();
                        		duplicateChildren(rootFieldId, fieldId);
                        	} else {
                        		cloneNode(schemaFieldEntity, 0);
                        	}
                        }
                    }
                }
            }
            List<SchemaFieldEntity>  rootNonXMLEntity;
            if(streamType == StreamType.flatFileDelimited || streamType == StreamType.flatFileFixedPosition || streamType == StreamType.EXCEL){
                rootNonXMLEntity = schemaFieldsDao.rootForNonXML(Long.parseLong(primarySchemaId));
                if(rootNonXMLEntity != null){
                    for (SchemaFieldEntity schemaFieldEntity : rootNonXMLEntity) {
                        cloneNode(schemaFieldEntity, 0);
                    }
                }
            }
        } else {
            success = "false";
            resMap.put("errMsg", c.getMessage());
        }
        resMap.put("success", success);
        response.getWriter().print(new JSONObject(resMap).toString());
        return null;
    }

    /**
     * Duplicates schema.
     *
     * @param schemaToDuplicate - id of the schema to duplicate
     * @return 'Create' object for the duplicated schema
     */
    private Create duplicateSchema(String schemaToDuplicate) {
        SchemaEntity source = schemasDao.find(Long.parseLong(schemaToDuplicate));
        SchemaEntity target = new SchemaEntity();
        target.setName("copy_of_" + source.getName());
        target.setDescription(source.getDescription());
        target.setIdApplication(source.getIdApplication());
        target.setIdDatabase(source.getIdDatabase());
        target.setIdSchemaType(source.getIdSchemaType());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setDelimiter(source.getDelimiter());
        target.setIdStreamType(source.getIdStreamType());
        target.setIdSchemaLib(source.getIdSchemaLib());
        target.setService(source.getService());
        target.setIdInputDatabase(source.getIdInputDatabase());
        target.setIsPlanned(source.getIsPlanned());
        target.setPublishToDb(source.getPublishToDb());
        target.setPlannedName(source.getPlannedName());
        target.setChrDelimiter(source.getChrDelimiter());
        target.setService(source.getService());
        target.setUserSchemas(source.getUserSchemas());
        target.setIsActive(source.getIsActive());
        target.setIsValid(source.getIsValid());
        target.setIsInValid(source.getIsInValid());
        target.setIsWarning(source.getIsWarning());
        target.setIdValidationDatabase(source.getIdValidationDatabase());
        target.setSchemaEvents(source.getSchemaEvents());
        target.setIsForecasted(source.getIsForecasted());
        target.setIsIndexedIncrement(source.getIsIndexedIncrement());
        target.setIsEventTrigger(source.getIsEventTrigger());
        target.setIdTriggerStatus(source.getIdTriggerStatus());
        target.setIsMongoDB(source.getIsMongoDB());
        return schemasDao.create(target);
    }

    /**
     * Duplicates all children nodes from source tree to new (target) tree.
     *
     * @param parentFieldId - current node of source tree
     * @param fieldId - id that is set to the target tree field
     */
    private void duplicateChildren(long parentFieldId, long fieldId) {
        List<SchemaFieldEntity> childList = schemaFieldsDao.listAllChild(parentFieldId);
        for (SchemaFieldEntity child : childList) {
            long id = cloneNode(child, fieldId);
            duplicateChildren(child.getIdSchemaField(), id);
        }
    }

    /**
     * Clones the schema field object.
     *
     * @param ent - schema field entity to clone
     * @param idParent - parent to set to cloned field
     * @return id of the cloned schema field
     */
    private long cloneNode(SchemaFieldEntity ent, long idParent) {
        SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
        schemaFieldEntity.setIdParent(idParent);
        schemaFieldEntity.setIdSchema(schemaId);
        schemaFieldEntity.setDescription(ent.getDescription());
        schemaFieldEntity.setElementOrder(ent.getElementOrder());
        schemaFieldEntity.setFillChar(ent.getFillChar());
        schemaFieldEntity.setFractionDigits(ent.getFractionDigits());
        schemaFieldEntity.setIdNumericType(ent.getIdNumericType());
        schemaFieldEntity.setIdAlign(ent.getIdAlign());
        schemaFieldEntity.setIdCheckType(ent.getIdCheckType());
        schemaFieldEntity.setIdDateFmtType(ent.getIdDateTimeType());
        schemaFieldEntity.setIdDateType(ent.getIdDateType());
        schemaFieldEntity.setIdFieldType(ent.getIdFieldType());
        schemaFieldEntity.setIdTimeType(ent.getIdTimeType());
        schemaFieldEntity.setMaxInclusive(ent.getMaxInclusive());
        schemaFieldEntity.setMaxLength(ent.getMaxLength());
        schemaFieldEntity.setMinInclusive(ent.getMinInclusive());
        schemaFieldEntity.setMinLength(ent.getMinLength());
        schemaFieldEntity.setName(ent.getName());
        schemaFieldEntity.setNillable(ent.getNillable());
        schemaFieldEntity.setSize(ent.getSize());
        schemaFieldEntity.setIsForecastable(ent.getIsForecastable());
        schemaFieldEntity.setForecastSpeed(ent.getForecastSpeed());
        schemaFieldEntity.setForecastAccuracy(ent.getForecastAccuracy());
        schemaFieldEntity.setIs_Attribute(ent.getIs_Attribute());
        schemaFieldEntity.setMaxOccurs(ent.getMaxOccurs());
        schemaFieldEntity.setErrorToleranceValue(ent.getErrorToleranceValue());
        schemaFieldEntity.setIndexIncremental(ent.isIndexIncremental());
        return schemaFieldsDao.create(schemaFieldEntity);
    }
}