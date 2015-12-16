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
 */package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.utils.generic.StreamsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaFieldsTreePopupReadController implements Controller, DaoSet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Set<String> fieldNames;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idSchema = request.getParameter("idSchema");
        fieldNames = Collections.synchronizedSet(new HashSet<String>());
        int idStreamType = schemasDao.find(Long.parseLong(idSchema)).getIdStreamType();
        ArrayNode rootArrayNode = objectMapper.createArrayNode();

        if (idStreamType == StreamType.XML || idStreamType == StreamType.XMLEXI || idStreamType == StreamType.JSON) {
            SchemaFieldEntity schemaFieldsEntity = new SchemaFieldEntity();
            schemaFieldsEntity = schemaFieldsDao.root(Long.parseLong(idSchema));
            // The Schema has fields in it therefore process those child elements in it
            if (schemaFieldsEntity != null) {
                ObjectNode rootObjectNode = getNode(schemaFieldsEntity);
                // Let the Schema name be the first Object to put in the JSON Object
                rootObjectNode.put("schemaName", schemasDao.find(Long.parseLong(idSchema)).getName());
                child(schemaFieldsEntity.getIdSchemaField(), schemaFieldsEntity.getIdFieldType(), rootObjectNode);
                rootArrayNode.add(rootObjectNode);

            } else if(idStreamType == StreamType.JSON){
            	List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.rootForNonXML(Long.parseLong(idSchema));
            	
    			if (listSchemaFields != null) {
    				for (int count = 0; count < listSchemaFields.size(); count++) {
    					SchemaFieldEntity childSchemaFieldEntity = listSchemaFields.get(count);

    					ObjectNode rootObjectNode = getNode(childSchemaFieldEntity);
    					if(childSchemaFieldEntity.getIdFieldType() == FieldType.all) {
    						rootObjectNode.put("schemaName", childSchemaFieldEntity.getName());
    						child(childSchemaFieldEntity.getIdSchemaField(), childSchemaFieldEntity.getIdFieldType(), rootObjectNode);
    						rootArrayNode.add(rootObjectNode);
    						
    					} else {
    						ObjectNode fieldObjectNode = getNode(childSchemaFieldEntity, true);
    						rootArrayNode.add(fieldObjectNode);
    					}
    				}
    			}
            } else {
                ObjectNode rootObjectNode = getNode();
                // The Schema has not fields in it therefore put it's name only in the JSON Object
                rootObjectNode.put("schemaName", schemasDao.find(Long.parseLong(idSchema)).getName());
                ServletOutputStream out = null;
                response.setContentType("application/json");
                out = response.getOutputStream();
                out.write(objectMapper.writeValueAsBytes(rootObjectNode));
                out.flush();
                out.close();

                return null;
            }
		} else if (idStreamType == StreamType.flatFileFixedPosition
				|| idStreamType == StreamType.flatFileDelimited
				|| idStreamType == StreamType.EXCEL) {
			List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao
					.listSchemaFields(Long.parseLong(idSchema));
			if (listSchemaFields != null) {
				for (int count = 0; count < listSchemaFields.size(); count++) {
					ObjectNode fieldObjectNode = getNode(listSchemaFields.get(count), true);
					rootArrayNode.add(fieldObjectNode);
				}
			}
		}
        
        ServletOutputStream out = null;
        response.setContentType("application/json");
        out = response.getOutputStream();
        out.write(objectMapper.writeValueAsBytes(rootArrayNode));
        out.flush();
        out.close();
        return null;
    }

    public void child(long idParent, int elementType, ObjectNode parentObjectNode) {
        ArrayList<SchemaFieldEntity> listChild = (ArrayList<SchemaFieldEntity>) schemaFieldsDao.listElemChild(idParent);
        if (listChild.size() == 0) {
            if (elementType > 3) {
                parentObjectNode.put("leaf", true);
            } else {
                parentObjectNode.put("children", objectMapper.createArrayNode());
            }
        } else {
            ArrayNode childArrayNode = objectMapper.createArrayNode();
            for (int count = 0; count < listChild.size(); count++) {
                ObjectNode childObjectNode = getNode(listChild.get(count));
                boolean isLeaf = false;
                
                if (schemaFieldsDao.listElemChild(listChild.get(count).getIdSchemaField()).size() == 0) {
                    if (listChild.get(count).getIdFieldType() > 3) {
                        childObjectNode.put("leaf", true);
                        isLeaf = true;                        
                    } else {
                        childObjectNode.put("children", objectMapper.createArrayNode());
                    }
                    List<SchemaFieldEntity> listFieldAttributes = schemaFieldsDao.findAllAttributes(listChild.get(count).getIdSchemaField());
                    if(listFieldAttributes != null && listFieldAttributes.size() > 0) {
                    	if(isLeaf) {
                       		childObjectNode.put("iconCls", "field_attribute_leaf");
                    	} else {
                    		childObjectNode.put("iconCls", "field_attribute_branch");
                    	}   	
                    }                    
                    childArrayNode.add(childObjectNode);
                } else {
                    childArrayNode.add(childObjectNode);
                    child(listChild.get(count).getIdSchemaField(), listChild.get(count).getIdFieldType(), childObjectNode);
                }
            }
            parentObjectNode.put("children", childArrayNode);
        }
    }

    private ObjectNode getNode() {
        return getNode(null);
    }

    private ObjectNode getNode(SchemaFieldEntity ent) {
        return getNode(ent, false);
    }

    private ObjectNode getNode(SchemaFieldEntity ent, boolean isLeaf) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", ent == null ? Long.valueOf(0) : ent.getIdSchemaField());
        node.put("text", ent == null ? "" : ent.getName());
        node.put("leaf", isLeaf);
        node.put("expanded", true);
        if (ent != null) {
        	List<SchemaFieldEntity> listFieldAttributes = schemaFieldsDao.findAllAttributes(ent.getIdSchemaField());
        	if(listFieldAttributes != null && listFieldAttributes.size() > 0) {
        		if(isLeaf) {
        			node.put("iconCls", "field_attribute_leaf");
        		} else {
        			node.put("iconCls", "field_attribute_branch");
        		}
        	}
            node.put("nodePath", ent.getPath("/"));
            String uniqueName = StreamsUtils.recursiveGetUniqueFieldName(ent.getName(), ent.getName(), 0, fieldNames);
            fieldNames.add(uniqueName);
            node.put("uniqueName", uniqueName);
            node.put("fieldType", FieldType.getFieldTypeAsStringById(ent.getIdFieldType()));
            if (ent.getLinkToDb() != null && !ent.getLinkToDb().isEmpty()) {
            	node.put("cls", "linked_node_to_db_green");
            	node.put("linkToDb", ent.getLinkToDb());
            }
        }
        return node;
    }
}