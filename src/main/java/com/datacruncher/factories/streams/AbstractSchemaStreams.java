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

package com.datacruncher.factories.streams;

import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.spring.SchemaLinkedFieldsController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractSchemaStreams implements DaoSet {
	
	public abstract byte[] getDownloadableStreams();
	private Logger log = Logger.getLogger(this.getClass());
	
	protected int maxVertical;
	protected SchemaEntity schemaEnt;	
	protected SchemaFieldEntity root;
	protected List<Map<String, Object>> linkedFieldsPaths;
	private DocumentBuilder docBuilder;
	//1st String - "schemaId:fieldId", 2nd Long - childId, arr[0] - field path, arr[1] - field name, arr[2] - field entity
	private Map<String, Map<Long, Object[]>> childsMap;
	private List<SchemaFieldEntity> plainChildsList;
	
	public AbstractSchemaStreams(SchemaEntity schemaEnt, List<Map<String, Object>> linkedFieldsPaths) {
		this.schemaEnt = schemaEnt;
		this.linkedFieldsPaths = linkedFieldsPaths;
		root = schemaFieldsDao.root(schemaEnt.getIdSchema());
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException", e);
		}
		for (Map<String, Object> map : linkedFieldsPaths) {
			@SuppressWarnings("unchecked")
			List<String> vList = (List<String>) map.get(SchemaLinkedFieldsController._SQL_RESULT);
			if (vList != null && vList.size() > maxVertical) {
				maxVertical = vList.size();
			}
		}
		childsMap = new HashMap<String, Map<Long, Object[]>>();
	}	
	
	protected Document getNewDomDocument() {
		return docBuilder.newDocument();
	}
	
	private Map<Long, Object[]> getChilds(SchemaFieldEntity parentEnt) {
		Long schemaId = parentEnt.getIdSchema();
		Long fieldId = parentEnt.getIdSchemaField();
		Map<Long, Object[]> result = childsMap.get(schemaId + ":" + fieldId);
		if (result == null) {
			Map<Long, Object[]> fieldMap = new HashMap<Long, Object[]>();
			for (SchemaFieldEntity childNode : schemaFieldsDao.listAllChild(schemaId, fieldId)) {
				Object[] arr = {childNode.getPath("."), childNode.getName(), childNode};
				fieldMap.put(childNode.getIdSchemaField(), arr);
			}
			childsMap.put(schemaId + ":" + fieldId, fieldMap);
			result = fieldMap;
		}
		return result;
	}
	
	/**
	 * Put child nodes to DOM document for stream formats that have 
	 * root node (xml, json, ..)
	 * 
	 * @param doc
     * @param parentXmlNode
     * @param parentEnt
	 * @param rowNum
	 * @param linkedFieldsPaths
	 */	
	protected void recursiveListChilds(Document doc, Element parentXmlNode, SchemaFieldEntity parentEnt,
			final int rowNum, List<Map<String, Object>> linkedFieldsPaths) {
		for (Object[] childNode : getChilds(parentEnt).values()) {
			Element xmlNode = doc.createElement((String)childNode[1]);
			String text = getNodeText((String)childNode[0], rowNum, linkedFieldsPaths);
			xmlNode.setTextContent(text);
			SchemaFieldEntity sfEntity = (SchemaFieldEntity)childNode[2];
			if (sfEntity.getNillable() && text.isEmpty()) {
				//skip field if it's optional and has an empty value
				continue;
			}
			parentXmlNode.appendChild(xmlNode);
			recursiveListChilds(doc, xmlNode, sfEntity, rowNum, linkedFieldsPaths);
		}
	}
	
	/**
	 * Put child nodes to DOM document for stream formats that don't have 
	 * root node (flat file fixed, ..)
	 * 
	 * @param doc
	 * @param rowNum
	 * @param idSchema
	 * @param linkedFieldsPaths
	 */
	protected void plainListChilds(Document doc, final int rowNum, long idSchema,
			List<Map<String, Object>> linkedFieldsPaths) {
		if (plainChildsList == null) {
			plainChildsList = new ArrayList<SchemaFieldEntity>();
			plainChildsList = schemaFieldsDao.listAllChild(idSchema, 0);			
		}
		// rootNode - pseudo root node for stream formats that don't have root
		// nodes (flat file fixed, ..).
		// This pseudo node is needed for XML creation, child nodes can't be root
		// because of its multiplicity.		
		Element rootNode = doc.createElement("root");
		doc.appendChild(rootNode);
		for (SchemaFieldEntity childNode : plainChildsList) {
			Element xmlNode = doc.createElement(childNode.getName());
			//fieldLength - used for flat fixed position file generation in DomToOtherFormat.transform()
			String fieldLength = childNode.getSize() == null ? "" : ("%%" + childNode.getSize());
			String text = getNodeText(childNode.getPath("."), rowNum, linkedFieldsPaths);
			xmlNode.setTextContent(text + fieldLength);
			rootNode.appendChild(xmlNode);
		}
	}
	
	protected String getNodeText(String entPath, final int rowNum, List<Map<String, Object>> linkedFieldsPaths) {
		for (Map<String, Object> map : linkedFieldsPaths) {
			if (entPath.equals((String)map.get(SchemaLinkedFieldsController._ENTITY_PATH))) {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>)map.get(SchemaLinkedFieldsController._SQL_RESULT);
				if (list == null) return "";
				return rowNum > list.size() - 1 ? "" : list.get(rowNum);
			}
		}
		return "";
	}
}
