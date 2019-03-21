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

package com.datacruncher.utils.generic;

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.constants.FieldType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.constants.Tag;
import com.datacruncher.datastreams.DatastreamDTO;
import com.datacruncher.persistence.manager.QuickDBRecognizer;
import com.datacruncher.persistence.manager.StreamToDbDynamicObject;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import javax.management.ReflectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.*;

/**
 * Classe Singleton che fornisce dei metodi di Utility per l'applicazione
 * @author danilo & stanislav
 */
public class StreamsUtils implements DaoSet {

    static Logger log = Logger.getLogger(StreamsUtils.class);

    private StreamsUtils() {
    	//this constructor will never be invoked.
    }
    
    public static final String IS_ATTR = "isAttr";
    
    /**
     * Recursive method that parses all xml childs.
     * 
     * @param elem - current dom element.
     * @param xmlTextNodes - text node list
     */
	private static void getXmlChilds(Element elem, List<Element> xmlTextNodes) {
		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = elem.elementIterator(); i.hasNext();) {
			Element el = i.next();
			if (el.isTextOnly()) {
				xmlTextNodes.add(el);
			} else {
				getXmlChilds(el, xmlTextNodes);
			}
		}
	}

	/**
	 * Gets xml text nodes for stream.
	 * 
	 * @param streamXml  
	 * @return text node list
	 * @throws DocumentException
	 */
	public static List<Element> parseStreamXml(String streamXml) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(streamXml));
    	List<Element> xmlTextNodes = new ArrayList<Element>();		
    	getXmlChilds(document.getRootElement(), xmlTextNodes);
    	return xmlTextNodes;
	}
	
	/**
	 * Formats xml element path.
	 * Example: '/Path/branch/node' -> 'PATH/BRANCH/NODE'
	 * 
	 * @param path
	 * @return
	 */
	public static String formatPathForXmlNode(String path) {
		return path.toUpperCase()./*replaceAll("/", "_").*/substring(1);		
	}
	
	private static String getFieldPathPrefix(int streamType) {
		return streamType != StreamType.XML && streamType != StreamType.XMLEXI ? (Tag.TAG_ROOT).toUpperCase() + "/" : "";
	}
	
	/**
	 * Publishes stream to destination database.
	 * 
	 * @param datastreamDTO - keeps stream info
	 */
	public static void publishStreamToDB(DatastreamDTO datastreamDTO) {
		Long schemaId = datastreamDTO.getIdSchema();
		QuickDBRecognizer quickDBRecognizer = new QuickDBRecognizer(schemaId);
		log.debug("quickDBRecognizer started");
		try {
			List<Element> xmlTextNodes = parseStreamXml(datastreamDTO.getOutput());
			StreamToDbDynamicObject streamObj = quickDBRecognizer.traceDefine(xmlTextNodes);
			Map<String, Map<String, String>> schemaFields = streamObj.getInsertedFields();	
			//boolean isLoadedFields = streamObj.isLoadedFields();
			Object toPersist = streamObj.getObject();
			for (Element el : xmlTextNodes) {
				String fieldPath = formatPathForXmlNode(el.getPath());
				String fieldValue = el.getText();
				for (Map.Entry<String, Map<String, String>> entry : schemaFields.entrySet()) {
					String entryFieldPath = getFieldPathPrefix(datastreamDTO.getIdStreamType()) + entry.getValue().get("path");
					if (entry.getValue().get(IS_ATTR).equals("true")) {
						for (Object o : el.attributes()) {
							DefaultAttribute attr = (DefaultAttribute)o;
							String attrPath = fieldPath + "/" + attr.getName().toUpperCase();
							if (attrPath.equals(entryFieldPath)) {
								fieldPath = attrPath;
								fieldValue = attr.getValue();
								break;
							}
						}
					}
					if (entryFieldPath.equals(fieldPath)) {
						String fieldJavaType = entry.getValue().get("type");
						Method meth = toPersist.getClass().getMethod("set" + entry.getKey(), Class.forName(fieldJavaType));
						if (fieldJavaType.contains("String")) {
							meth.invoke(toPersist, fieldValue);
						} else if (fieldJavaType.contains("Long")) {
							meth.invoke(toPersist, Long.valueOf(fieldValue));
						} else if (fieldJavaType.contains("Double")) {
							meth.invoke(toPersist, Double.valueOf(fieldValue));
						} else if (fieldJavaType.contains("Integer")) {
							meth.invoke(toPersist, Integer.valueOf(fieldValue));
						} else if (fieldJavaType.contains("Date")) {
							//never invoked because now all dates are of string type
							meth.invoke(toPersist, new Date());
						}						
					}
				}
			}
			toPersist.getClass().getMethod("setPath", String.class).invoke(toPersist, getAllPaths(schemaFields));
			int inserito = quickDBRecognizer.insertTrace(streamObj);
			if (inserito > 0) {
				log.info("Stream published successfully");
			}
		} catch (ReflectionException ex) {
			log.error(ex);
		} catch (ClassNotFoundException ex) {
			log.error(ex);
		} catch (IOException ex) {
			log.error(ex);
		} catch (InstantiationException ex) {
			log.error(ex);
		} catch (IllegalAccessException ex) {
			log.error(ex);
		} catch (SecurityException ex) {
			log.error(ex);
		} catch (CannotCompileException ex) {
			log.error(ex);
		} catch (NoSuchFieldException ex) {
			log.error(ex);
		} catch (NoSuchMethodException ex) {
			log.error(ex);
		} catch (InvocationTargetException ex) {
			log.error(ex);
		} catch (NotFoundException ex) {
			log.error(ex);
		} catch (DocumentException e) {
			log.error("Stream parse exception", e);
		}
	}
	
	private static String getAllPaths(Map<String, Map<String, String>> schemaFields) {
		StringBuffer buf = new StringBuffer();
		for (Map.Entry<String, Map<String, String>> entry : schemaFields.entrySet()) {
			buf.append(entry.getKey()).append(" path:").append(entry.getValue().get("path")).append(", ");
		}
		return buf.length() > 0 ? buf.toString().substring(0, buf.length() - 2) : "";
	}
	
	/**
	 * Recursive method that checks whether parameter constName is in current set, 
	 * if yes - returns alternative name, otherwise - returns constName.
	 * 
	 * Example: constName='abc', set={'s', 'o'}, result='abc'
	 * if constName='abc', set={'s', 'o', 'abc'}, result='abc_1'
	 * if constName='abc', set={'s', 'o', 'abc', 'abc_1'}, result='abc_2'
	 * 
	 * Usage: gets unique names for schemaFieldTree.
	 * 
	 * @param constName - constant name basis
	 * @param name - changeable temporary name 
	 * @param counter - counter
	 * @param set - set 
	 *
	 */
	public static String recursiveGetUniqueFieldName(final String constName, String name, int counter, Set<String> set) {
		if (name == null) return "";
		String result = name;
		if ("id".equalsIgnoreCase(name)) {
			//field with name 'id' cannot be inserted since it's reserved by db, so add incremented number.
			name = constName + "_" + ++counter;
			return recursiveGetUniqueFieldName(constName, name, counter, set);			
		}
		for (String prepName : set) {
			if (prepName.equals(name)) {
				name = constName + "_" + ++counter;
				result = recursiveGetUniqueFieldName(constName, name, counter, set);
			}								
		}	
		return result;
	}
	
	/**
	 * Publishes loading stream (SchemaType == 4) to database.
	 * 
	 * @param datastreamDTO
	 */
	public static void loadingStreamToDB(DatastreamDTO datastreamDTO, SchemaEntity loadingStream) {
		Map<String, List<SchemaFieldEntity>> map = new HashMap<String, List<SchemaFieldEntity>>();
		for (SchemaFieldEntity field : schemasDao.retrieveAllLeaves(datastreamDTO.getIdSchema())) {
			if (field.getLinkToDb() != null && !field.getLinkToDb().isEmpty()) {
				String tableName = field.getLinkToDb().split("[.]")[0];
				List<SchemaFieldEntity> list = map.get(tableName);
				if (list == null) {
					list = new ArrayList<SchemaFieldEntity>();
					map.put(tableName, list);
				}
				list.add(field);
			}
		}
		log.info("___loadingStreamToDB map.size() = " + map.size());
		if (map.size() > 0) {
			Connection connection = ConnectionPoolsSet.getConnection(loadingStream.getIdDatabase());
			for (Map.Entry<String, List<SchemaFieldEntity>> entry : map.entrySet()) {
				List<SchemaFieldEntity> list = entry.getValue();
				String[] arrSqlFields = new String[list.size()];
				String[] arrSqlValues = new String[list.size()];
				List<Element> xmlTextNodes = null;
				try {
					xmlTextNodes = parseStreamXml(datastreamDTO.getOutput());
				} catch (DocumentException e) {
					log.error("LoadingStreamToDB :: Document parse exception", e);
				}
				int i = 0;
				for (Element el : xmlTextNodes) {
					String fieldPath = formatPathForXmlNode(el.getPath());
					String fieldValue = el.getText();
					for (SchemaFieldEntity schemaField : list) {
						if (fieldPath.equals(getFieldPathPrefix(datastreamDTO.getIdStreamType()) + schemaField.getPath("/"))
								&& schemaField.getLinkToDb() != null && !schemaField.getLinkToDb().isEmpty()) {
							String[] pathParts = schemaField.getLinkToDb().split("[.]");
							arrSqlFields[i] = pathParts[pathParts.length - 1];
							arrSqlValues[i] = schemaField.getIdFieldType() == FieldType.alphanumeric ? "'" + fieldValue + "'" : fieldValue;
							i++;
						}
					}
				}
				String sql = MessageFormat.format("INSERT INTO {0} ({1}) VALUES ({2})", entry.getKey(),
						CommonUtils.stringAsCommaSeparated(arrSqlFields), CommonUtils.stringAsCommaSeparated(arrSqlValues));
				try {
					connection.setAutoCommit(false);
					Statement stmt = null;
					try {
						stmt = connection.createStatement();
						log.info("___loadingStreamToDB sql = " + sql);
						stmt.executeUpdate(sql);
						log.info("___loadingStreamToDB exec update success");
					} finally {
						stmt.close();
					}
				} catch (SQLException e) {
					try {
						connection.rollback();
					} catch (SQLException e1) {
						//skip
					}
					log.error("LoadingStreamToDB :: sql statement execution exception", e);
				}
			}
			try {
				if (connection != null) {
					log.info("___loadingStreamToDB before commit");
					connection.commit();
					log.info("___loadingStreamToDB commit success");
				}
			} catch (SQLException e) {
				log.error("LoadingStreamToDB :: connection close exception", e);
			}

		}
	}
}

