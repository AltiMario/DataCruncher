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

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.XSDFieldImporter.XsdSchemaFieldInfo;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.*;
import com.seer.datacruncher.utils.schema.JaxbGenerationResults;
import com.seer.datacruncher.utils.schema.SchemaCodeGenerator;
import com.seer.datacruncher.validation.ResultStepValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XSDFieldStorer implements DaoSet {

    private ResultStepValidation result = new ResultStepValidation();
    List<String> filedImporterResults = new ArrayList<String>();
    private long idSchema; 
    private byte[] xsdStream;
    
    public XSDFieldStorer(long idSchema, byte[] xsdStream) {
     this.idSchema = idSchema;
     this.xsdStream = xsdStream;


    }
    public ResultStepValidation storeFields(){
        String importerResults;
        result.setValid(true);
        result.setMessageResult("");
        if (result.isValid()){
            try {

                JaxbGenerationResults results = new SchemaCodeGenerator()
                        .generateJAXBStuffFromSchema(idSchema,
                                xsdStream);

                List<Map<String, String>> jaxbGenerationResults = results
                        .getGenerationResults();

                boolean isXSDInvalid = false;
                if (jaxbGenerationResults != null
                        && jaxbGenerationResults.size() > 0) {
                    for (Map<String, String> map : jaxbGenerationResults) {
                        if (map.get("success").equals("false")
                                && map.get("stackTrace").contains("XJC compiler could not able to parse schema")) {
                            isXSDInvalid = true;
                        }
                    }
                }

                if (isXSDInvalid) {
                    result.setValid(false);
                    result.setMessageResult("2");
                } else {
                    SchemaXSDEntity schemaXSDEntity = new SchemaXSDEntity();
                    schemaXSDEntity.setIdSchemaXSD(idSchema);
                    schemaXSDEntity.setSchemaXSD(new String(xsdStream));
                    XsdSchemaFieldInfo schemaFieldInfo;
                    try{
                        schemaFieldInfo = XSDFieldImporter.parseXSD(schemaXSDEntity);
                        filedImporterResults = schemaFieldInfo.getFieldInfoResults();
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.setValid(false);
                        result.setMessageResult("Schema could not be parsed.");
                        return result;
                    }
                    if (result.isValid())
                        saveSchemaFieldInfo(schemaFieldInfo);

                    if (result.isValid()){
                        Create create = schemasXSDDao.create(schemaXSDEntity);
                        if(create.getSuccess()){
                            if (filedImporterResults.size()>0){
                                importerResults = "<br><b>Parse schema warning:</b>";
                                int i=1;
                                for (String res : filedImporterResults) {
                                    importerResults = importerResults+ "<br>"+i+") " + res ;
                                    i++;
                                }
                                SchemaEntity schemaEntity= schemasDao.find(this.idSchema);
                                schemaEntity.setDescription(importerResults);
                                schemasDao.update(schemaEntity);
                                result.setMessageResult(importerResults);
                            }else{
                                result.setMessageResult(create.getMessage());
                            }
                        }else{
                            result.setValid(false);
                            result.setMessageResult(create.getMessage());
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setValid(false);
                result.setMessageResult(("Schema could not be parsed"));
                return result;
            }
        }

         
        return result;
    }
    private  void saveSchemaFieldInfo(XsdSchemaFieldInfo schemaFieldInfo) {
        String name = "";
        try{
            result.setValid(true);
            result.setMessageResult("");
            List<SchemaFieldEntity> topLevelFields = schemaFieldInfo
                    .getTopLevelFields();
            int idStreamType = schemasDao.find(schemaFieldInfo.getIdSchema()).getIdStreamType() ;
            for (SchemaFieldEntity schemaFieldEntity : topLevelFields) {
                name = schemaFieldEntity.getName();
                saveSchemaFieldEntity(schemaFieldEntity, schemaFieldInfo,idStreamType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setValid(false);
            result.setMessageResult("Error field ["+name+"]." + e.getMessage());
        }
    }
    private void saveSchemaFieldEntity(SchemaFieldEntity schemaFieldEntity,
                                      XsdSchemaFieldInfo schemaFieldInfo,
                                      int idStreamType) {
        Long id = 0l;
        schemaFieldEntity.setIdSchema(schemaFieldInfo.getIdSchema());
        if (!schemaFieldEntity.getName().equalsIgnoreCase(Tag.TAG_ROOT)) {
            id = schemaFieldsDao.create(schemaFieldEntity); 
        }
        if (id == null) {
            result.setValid(false);
            String msg = result.getMessageResult().trim().length()>0 ?result.getMessageResult()+"<br>" :"";
            result.setMessageResult(msg+"Error field ["+schemaFieldEntity.getName()+"] create.");
        }else{
            List<SchemaFieldEntity> fields = schemaFieldInfo
                    .getChildren(schemaFieldEntity);
            if (schemaFieldEntity.getIdFieldType()> 3) {
                // is a leaf field
                List<String> enums = schemaFieldInfo
                        .getFieldEnumeration(schemaFieldEntity);
                if (enums != null) {
                    if (schemaFieldEntity.getIdFieldType()==  FieldType.alphanumeric ){
                        for (String e : enums) {
                            AlphanumericFieldValuesEntity entity = new AlphanumericFieldValuesEntity();
                            entity.setIdAlphanumericSchemaField(id);
                            entity.setIdSchema(schemaFieldInfo.getIdSchema());
                            entity.setValue(e);

                            alphaFieldDao.create(entity);
                        }
                    } else if (schemaFieldEntity.getIdFieldType() == FieldType.numeric) {
                        for (String e : enums) {
                            NumericFieldValuesEntity entity = new NumericFieldValuesEntity();
                            entity.setIdNumericSchemaField(id);
                            entity.setIdSchema(schemaFieldInfo.getIdSchema());
                            entity.setValue(e);

                            numericFieldDao.create(entity);
                        }
                    }
                }
            }

            if (fields != null) {

                for (SchemaFieldEntity childField : fields) {
                    childField.setIdParent(id);
                    if(idStreamType == StreamType.flatFileFixedPosition && childField.getIs_Attribute()){
                        boolean fixAttribute = true;
                        //SchemaFieldEntity parentField = schemaFieldsDao.find(id);
                        if (childField.getName().equals(Tag.TAG_SIZE)){
                            schemaFieldEntity.setSize(childField.getSize());
                        }else if (childField.getName().equals(Tag.TAG_ALIGN)) {
                            schemaFieldEntity.setIdAlign(childField.getIdAlign());
                        }else if (childField.getName().equals(Tag.TAG_FILL)){
                            schemaFieldEntity.setFillChar(childField.getFillChar());
                        }else{
                            saveSchemaFieldEntity(childField, schemaFieldInfo,idStreamType);
                            fixAttribute = false;
                        }
                        if(fixAttribute){
                            schemaFieldsDao.update(schemaFieldEntity);
                        }
                    }else{
                        saveSchemaFieldEntity(childField, schemaFieldInfo, idStreamType);
                    }

                }
            }
        }
    }

    
}
