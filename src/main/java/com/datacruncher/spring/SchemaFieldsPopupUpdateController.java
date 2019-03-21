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

package com.datacruncher.spring;

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.constants.DateTimeType;
import com.datacruncher.constants.FieldType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.AlphanumericFieldValuesEntity;
import com.datacruncher.jpa.entity.NumericFieldValuesEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldCheckTypesEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaFieldsPopupUpdateController implements Controller, DaoSet {

	private ObjectMapper objectMapper = new ObjectMapper();
	private static final String _TEXT = "text";
	private static final String _SEPARATOR= ": ";

	//private Logger log = Logger.getLogger(this.getClass());
	private String catalogueName;
	private DatabaseMetaData md;
	
    @PersistenceContext
    protected EntityManager em;
    
    private enum DbFieldType {
		STRING(""),
		INT(""),
		FOLDER("table"),
		ROOT_FOLDER("database");
		
		private final String additinalTextForTree;	
		
		DbFieldType(String addText) {
			additinalTextForTree = addText;
		}
		
		String additionalText() {
			return additinalTextForTree;
		}
	}
    
    public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        String idSchema = request.getParameter("idSchema");
        String idSchemaField = request.getParameter("idSchemaField");
        String idFieldType = request.getParameter("idFieldType");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String minLenght = request.getParameter("minLenght");
        String maxLenght = request.getParameter("maxLenght");
        String nillableAlphanumeric = request.getParameter("nillableAlphanumeric");
        String idCheckType = request.getParameter("extraCheck");
        String minInclusive = request.getParameter("minInclusive");
        String maxInclusive = request.getParameter("maxInclusive");
        String fractionDigits = request.getParameter("fractionDigits");
        String nillableNumeric = request.getParameter("nillableNumeric");
        String numericType = request.getParameter("numericType");
        String idDateTimeType = request.getParameter("idDateTimeType");
        String idDateType = request.getParameter("idDateType");
        String idTimeType = request.getParameter("idTimeType");
        String isForecastable = request.getParameter("isForecastable");
        String forecastAccuracy = request.getParameter("forecastAccuracy");
        String forecastSpeed = request.getParameter("forecastSpeed");
        String nillableDate = request.getParameter("nillableDate");
        String size = request.getParameter("size");
        String idCustomError = request.getParameter("idCustomError");
        String maxOccurs = request.getParameter("maxOccurs");
        String linkToDb = request.getParameter("linkToDb");
        String isAttribute = request.getParameter("isAttribute");
        String errorType= request.getParameter("errorType");
		String selectedExtraCheckVals = request.getParameter("selectedExtraCheck");
        int errorToleranceValue = Integer.parseInt(request.getParameter("errorToleranceValue") == null ? "-1" : request.getParameter("errorToleranceValue"));
        boolean indexIncremental = Boolean.parseBoolean(request.getParameter("indexIncrementalValue"));
        SchemaEntity schemaEntity = schemasDao.find(Long.parseLong(idSchema));
        SchemaFieldEntity schemaFieldEntity = schemaFieldsDao.find(Long.parseLong(idSchemaField));
        schemasXSDDao.destroy(schemaFieldEntity.getIdSchema());
        String oldLinkToDb = schemaFieldEntity.getLinkToDb();
        schemaFieldEntity.setIdFieldType(Integer.parseInt(idFieldType));
        schemaFieldEntity.setName(name);
        schemaFieldEntity.setDescription(description.replace('\u200b',' '));
        schemaFieldEntity.setErrorToleranceValue(errorToleranceValue);
        schemaFieldEntity.setIndexIncremental(indexIncremental);
        if (errorType != null)
            schemaFieldEntity.setErrorType(Integer.parseInt(request.getParameter("errorType")));
        
        if ("1".equals(isAttribute) || "true".equals(isAttribute)) {
            schemaFieldEntity.setIs_Attribute(true);
        } else {
            schemaFieldEntity.setIs_Attribute(false);
        }
        
        if (maxOccurs != null && !maxOccurs.equals("")) {
            schemaFieldEntity.setMaxOccurs(Integer.parseInt(maxOccurs));
        }else{
            schemaFieldEntity.setMaxOccurs(1);
        }
        
        boolean isLinkedToDbChanged = false;
        if (linkToDb != null && !linkToDb.isEmpty()) {
        	schemaFieldEntity.setLinkToDb(linkToDb);
        	isLinkedToDbChanged = true;
        }
                        
        if (idCustomError != null && !idCustomError.isEmpty() && Long.parseLong(idCustomError) != -7) {
            schemaFieldEntity.setIdCustomError(Long.parseLong(idCustomError));
        }
        if (schemaFieldEntity.getIdFieldType() == FieldType.alphanumeric) {
        	if (minLenght != null && !minLenght.equals("")) {
                schemaFieldEntity.setMinLength(Integer.parseInt(minLenght));
            } else {
                schemaFieldEntity.setMinLength(null);
            }
            if (maxLenght != null && !maxLenght.equals("")) {
                schemaFieldEntity.setMaxLength(Integer.parseInt(maxLenght));
            } else {
                schemaFieldEntity.setMaxLength(null);
            }

            if (idCheckType != null && !idCheckType.equals("")) {
                schemaFieldEntity.setIdCheckType(Integer.parseInt(idCheckType));
            } else {
                schemaFieldEntity.setIdCheckType(0);
            }

            if(StringUtils.isNotEmpty(selectedExtraCheckVals)){
            	// Delete existing entries from database
            	schemaFieldsDao.destroySchemFieldCheckTypes(Long.parseLong(idSchemaField));
            	String[] extraCheckIds = selectedExtraCheckVals.split(",");
            	Set<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeSet = new HashSet<SchemaFieldCheckTypesEntity>(extraCheckIds.length);
            	SchemaFieldCheckTypesEntity schemaFieldCheckTypesEntity;
            	for (String extraCheck : extraCheckIds) {
            		schemaFieldCheckTypesEntity = new SchemaFieldCheckTypesEntity();
            		schemaFieldCheckTypesEntity.setIdCheckType(Long.parseLong(extraCheck));
            		schemaFieldCheckTypesEntity.setSchemaFieldEntity(schemaFieldEntity);
            		schemaFieldCheckTypeSet.add(schemaFieldCheckTypesEntity);
            		schemaFieldCheckTypesEntity = null;
				}
            	schemaFieldEntity.setSchemaFieldCheckTypeSet(schemaFieldCheckTypeSet);
            }else{
                List<String> list= schemaFieldsDao.findSchemaFieldCheckTypes(Long.parseLong(idSchemaField));
                if(list.size()>0){
                    // Delete existing entries from database
                    schemaFieldsDao.destroySchemFieldCheckTypes(Long.parseLong(idSchemaField));
                    Set<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeSet = new HashSet<SchemaFieldCheckTypesEntity>();
                    schemaFieldEntity.setSchemaFieldCheckTypeSet(schemaFieldCheckTypeSet);
                }
            }
            
            if (schemaEntity.getIdStreamType() == StreamType.flatFileFixedPosition){
                String idAlignAlphanumeric = request.getParameter("idAlignAlphanumeric");
                String fillCharAlphanumeric = request.getParameter("fillCharAlphanumeric");
                schemaFieldEntity.setNillable(false);
                schemaFieldEntity.setSize(size);
                if (idAlignAlphanumeric != null && !idAlignAlphanumeric.equals("")) {
                    schemaFieldEntity.setIdAlign(Integer.parseInt(idAlignAlphanumeric));
                } else {
                    schemaFieldEntity.setIdAlign(null);
                }
                if (fillCharAlphanumeric != null && !fillCharAlphanumeric.equals("")) {
                    schemaFieldEntity.setFillChar(fillCharAlphanumeric);
                } else {
                    schemaFieldEntity.setFillChar(null);
                }
            }else{
                if (nillableAlphanumeric != null && (nillableAlphanumeric.equals("1") || nillableAlphanumeric.equals("true"))) {
                    schemaFieldEntity.setNillable(true);
                } else {
                    schemaFieldEntity.setNillable(false);
                }
            }

        }
        if (schemaFieldEntity.getIdFieldType() == FieldType.numeric) {
            if (!minInclusive.equals("")) {
                schemaFieldEntity.setMinInclusive(Double.parseDouble(minInclusive));
            } else {
                schemaFieldEntity.setMinInclusive(null);
            }
            if (!maxInclusive.equals("")) {
                schemaFieldEntity.setMaxInclusive(Double.parseDouble(maxInclusive));
            } else {
                schemaFieldEntity.setMaxInclusive(null);
            }
            if (!fractionDigits.equals("")) {
                schemaFieldEntity.setFractionDigits(Integer.parseInt(fractionDigits));
            } else {
                schemaFieldEntity.setFractionDigits(null);
            }
            if (numericType.equals("1") ) {
                schemaFieldEntity.setIdNumericType(1);
            } else {
                schemaFieldEntity.setIdNumericType(2);
            }

            schemaFieldEntity.setIsForecastable(Boolean.parseBoolean(isForecastable));
            schemaFieldEntity.setForecastSpeed(Integer.parseInt(forecastSpeed));
            schemaFieldEntity.setForecastAccuracy(Integer.parseInt(forecastAccuracy));

            if (schemaEntity.getIdStreamType() == StreamType.flatFileFixedPosition){
                String idAlignNumeric = request.getParameter("idAlignNumeric");
                String fillCharNumeric = request.getParameter("fillCharNumeric");
                schemaFieldEntity.setNillable(false);
                schemaFieldEntity.setSize(size);
                if (!idAlignNumeric.equals("")) {
                    schemaFieldEntity.setIdAlign(Integer.parseInt(idAlignNumeric));
                } else {
                    schemaFieldEntity.setIdAlign(null);
                }
                if (!fillCharNumeric.equals("")) {
                    schemaFieldEntity.setFillChar(fillCharNumeric);
                } else {
                    schemaFieldEntity.setFillChar(null);
                }
            }else{
                if (nillableNumeric.equals("1") || nillableNumeric.equals("true")) {
                    schemaFieldEntity.setNillable(true);
                } else {
                    schemaFieldEntity.setNillable(false);
                }
            }
        }
        if (schemaFieldEntity.getIdFieldType() == FieldType.date) {
            int maxLength = 0;
            schemaFieldEntity.setIdDateFmtType(Integer.parseInt(idDateTimeType));
            if (!idDateType.equals("")) {
                schemaFieldEntity.setIdDateType(Integer.parseInt(idDateType));
                switch (Integer.parseInt(idDateType)) {
                    case DateTimeType.DDMMYY:
                        maxLength = 6;
                        break;
                    case DateTimeType.slashDDMMYY:
                    case DateTimeType.signDDMMYY:
                    case DateTimeType.dotDDMMYY:
                    case DateTimeType.DDMMYYYY:
                    case DateTimeType.YYYYMMDD:
                        maxLength = 8;
                        break;
                    case DateTimeType.slashDDMMYYYY:
                    case DateTimeType.signDDMMYYYY:
                    case DateTimeType.dotDDMMYYYY:
                    case DateTimeType.slashYYYYMMDD:
                    case DateTimeType.signYYYYMMDD:
                    case DateTimeType.dotYYYYMMDD:
                        maxLength = 10;
                        break;
                }
            } else {
                schemaFieldEntity.setIdDateType(null);
            }
            if (!idTimeType.equals("")) {
                schemaFieldEntity.setIdTimeType(Integer.parseInt(idTimeType));
                if(maxLength > 0) {
                    maxLength = maxLength + 1;
                }
                switch (Integer.parseInt(idTimeType)) {
                    case DateTimeType.dblpnthhmm:
                    case DateTimeType.dothhmm:
                        maxLength = maxLength + 5;
                        break;
                    case DateTimeType.dblpnthhmmss:
                    case DateTimeType.dothhmmss:
                        maxLength = maxLength + 8;
                        break;
                    case DateTimeType.dblpntZhhmmss:
                    case DateTimeType.dotZhhmmss:
                        maxLength = maxLength + 11;
                        break;
                    case DateTimeType.dblpnthmmss:
                    case DateTimeType.dothmmss:
                        maxLength = maxLength + 7;
                        break;
                }
            } else {
                schemaFieldEntity.setIdTimeType(null);
            }
            schemaFieldEntity.setMaxLength(maxLength);
            if (schemaEntity.getIdStreamType() == StreamType.flatFileFixedPosition){
                schemaFieldEntity.setNillable(false);
                schemaFieldEntity.setSize(maxLength+"");
            }else{
                if (nillableDate.equals("1") || nillableDate.equals("true")) {
                    schemaFieldEntity.setNillable(true);
                } else {
                    schemaFieldEntity.setNillable(false);
                }
            }
            int dateTimeType = schemaFieldEntity.getIdDateTimeType();
			if ((dateTimeType == DateTimeType.xsdDateTime ||
                 dateTimeType == DateTimeType.xsdDate ||
                 dateTimeType == DateTimeType.xsdTime ||
                 dateTimeType == DateTimeType.unixTimestamp )
                 && schemaEntity.getIdStreamType() == StreamType.flatFileFixedPosition) {
				String idAlignDateTime = request.getParameter("idAlignDateTime");
				String fillCharDateTime = request.getParameter("fillCharDateTime");
				schemaFieldEntity.setSize(size);
				if (!idAlignDateTime.equals("")) {
					schemaFieldEntity.setIdAlign(Integer
							.parseInt(idAlignDateTime));
				} else {
					schemaFieldEntity.setIdAlign(null);
				}
				if (!fillCharDateTime.equals("")) {
					schemaFieldEntity.setFillChar(fillCharDateTime);
				} else {
					schemaFieldEntity.setFillChar(null);
				}
			} else {				
				schemaFieldEntity.setIdAlign(null);
				schemaFieldEntity.setFillChar(null);
			}
        }
        
        Update update = schemaFieldsDao.update(schemaFieldEntity);
        if(update.isSuccess()) {
        	schemaEntity.setIsActive(0);
        	schemasDao.update(schemaEntity);
        }
        if(linkToDb != null && linkToDb.trim().length() > 0 && linkToDb.indexOf(".") == -1) {
        	if(oldLinkToDb == null || oldLinkToDb.trim().length() == 0) {        		
        		addLinkedTableFields(schemaFieldEntity, linkToDb);
        	} else if(!linkToDb.equals(oldLinkToDb)) {
        		deleteLinkedTableFields(schemaFieldEntity, oldLinkToDb);
        		addLinkedTableFields(schemaFieldEntity, linkToDb);
        	}        	
        }
        if (isLinkedToDbChanged) {
        	update.setExtraMessage("isLinkedToDbChanged");
        }
        ObjectMapper mapper = new ObjectMapper();

		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        out.write(mapper.writeValueAsBytes(update));
		out.flush();
		out.close();
 		
        return null;
    }
    private void addLinkedTableFields(SchemaFieldEntity rootEntity, String tableName) {
		try {
			Connection connection = getConnection(String.valueOf(rootEntity.getIdSchema()), true);
			md = connection.getMetaData();
			ArrayNode childArrayNode = objectMapper.createArrayNode();
			ObjectNode parentObjectNode = getNode(catalogueName, DbFieldType.ROOT_FOLDER);

			ObjectNode node = getNode(tableName, DbFieldType.FOLDER);
			childFields(node, tableName, rootEntity);

			node.put("id", tableName);
			childArrayNode.add(node);

			parentObjectNode.put("children", childArrayNode);
			parentObjectNode.put("expanded", true);
		} catch (SQLException sqex) {
			sqex.printStackTrace();
		}
    }
    private void childFields(ObjectNode parentObjectNode, String tableName, SchemaFieldEntity rootEntity) throws SQLException {
		ResultSet rs = md.getColumns(catalogueName, null, tableName, null);
		int count = 1;
		while (rs.next()) {
			String colName = rs.getString("COLUMN_NAME");
			String linkToDb = tableName + "." + colName;
			SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
			schemaFieldEntity.setName(colName);
			schemaFieldEntity.setIdAlign(1);
			schemaFieldEntity.setDescription(rootEntity.getDescription());
			schemaFieldEntity.setElementOrder(count++);
			schemaFieldEntity.setFillChar(rootEntity.getFillChar());
			schemaFieldEntity.setForecastAccuracy(rootEntity.getForecastAccuracy());
			schemaFieldEntity.setForecastSpeed(rootEntity.getForecastSpeed());
			schemaFieldEntity.setIdAlign(rootEntity.getIdAlign());
			schemaFieldEntity.setIdCheckType(rootEntity.getIdCheckType());
			schemaFieldEntity.setIdCustomError(rootEntity.getIdCustomError());
			schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
			schemaFieldEntity.setErrorToleranceValue(rootEntity.getErrorToleranceValue());
			schemaFieldEntity.setIndexIncremental(rootEntity.isIndexIncremental());
			schemaFieldEntity.setIdNumericType(rootEntity.getIdNumericType());
			schemaFieldEntity.setMaxOccurs(rootEntity.getMaxOccurs());
			schemaFieldEntity.setErrorType(rootEntity.getErrorType());
			schemaFieldEntity.setIdSchema(rootEntity.getIdSchema());
			schemaFieldEntity.setIdParent(rootEntity.getIdSchemaField());
			schemaFieldEntity.setLinkToDb(linkToDb);
			schemaFieldsDao.create(schemaFieldEntity);
		}
	}
    
    /**
	 * Gets the tree node.
	 * 
	 * @param text - text to appear in a tree
	 * @param type - node type
	 * @return
	 */	
	private ObjectNode getNode(String text, DbFieldType type) {
		return getNode(text, type, false);
	}	

	/**
	 * Gets the tree node.
	 * 
	 * @param text - text to appear in a tree
	 * @param type - node type
	 * @param isLeaf - leaf/not leaf
	 * @return
	 */
	private ObjectNode getNode(String text, DbFieldType type, boolean isLeaf) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put(_TEXT, text + _SEPARATOR + type.additionalText());
		node.put("leaf", isLeaf);		
		return node;
	}
	/**
	 * Get database connection.
	 * 
	 * @param isOutgoingDb - 'false' to retrieve tree from incoming database (example: 'DataCruncher')
	 * 			'true' - to retrieve tree from outgoing database (database chosen from DatabaseEntity)
	 * @return
	 */
	private Connection getConnection(String idSchema, boolean isOutgoingDb) throws SQLException {
		Connection connection = null;
		SchemaEntity schemaEntity = schemasDao.find(Long.valueOf(idSchema));
		long idDatabase = schemaEntity.getIdInputDatabase();
		if (!isOutgoingDb) {
			//here's old hibernate dependency
			/*Session session = (Session) em.getDelegate();	
			SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
			ConnectionProvider cp = sfi.getConnectionProvider();			
			connection = cp.getConnection();*/				
		} else {
			connection = ConnectionPoolsSet.getConnection(idDatabase);
		}		
		return connection;
	}
	private void deleteLinkedTableFields(SchemaFieldEntity fieldEntity, String tableName) {
		try {
			Connection connection = getConnection(String.valueOf(fieldEntity.getIdSchema()), true);
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, tableName, null);
			
			List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.findAllByParentId(fieldEntity.getIdSchemaField());
	   			
			while (rs.next()) {
				String colName = rs.getString("COLUMN_NAME");
				String linkToDb = tableName + "." + colName;
				if (listSchemaFields != null) {					
	   				for(SchemaFieldEntity instance : listSchemaFields) {
	   						if(colName.equals(instance.getName()) && instance.getLinkToDb().equals(linkToDb)) {
	   							delete(instance.getIdSchemaField());
	   							break;
	   						}
	   				}
	   			}
			}
		} catch (SQLException sqex) {
			sqex.printStackTrace();
		}
    }
public void delete(long idSchemaField) {
    	
        SchemaFieldEntity schemaFieldEntityObj = schemaFieldsDao.find(idSchemaField);
        if (schemaFieldEntityObj != null && schemaFieldEntityObj.getIdSchema() != 0) {
            schemasXSDDao.destroy(schemaFieldEntityObj.getIdSchema());
        }

        schemaFieldsDao.destroy(idSchemaField);
        deleteSchemaValues(idSchemaField);
        ArrayList<SchemaFieldEntity> listChild = (ArrayList<SchemaFieldEntity>) schemaFieldsDao
                .listAllChild(idSchemaField);
        for (int cont = 0; cont < listChild.size(); cont++) {
            delete(listChild.get(cont).getIdSchemaField());
        }
    }
    
    public void deleteSchemaValues(long idSchemaField) {
    	
    	List<AlphanumericFieldValuesEntity> listAlphaNumericFields = alphaFieldDao.listAlphanumericFieldValues(idSchemaField);
		if (listAlphaNumericFields != null && listAlphaNumericFields.size() > 0) {
			for (AlphanumericFieldValuesEntity alpha : listAlphaNumericFields) {
				alphaFieldDao.destroy(alpha.getIdAlphanumericFieldValue());
			}
		}
		
		List<NumericFieldValuesEntity> listNumericFields = numericFieldDao.listNumericFieldValues(idSchemaField);
		if (listNumericFields != null && listNumericFields.size() > 0) {
			for (NumericFieldValuesEntity numeric : listNumericFields) {
				numericFieldDao.destroy(numeric.getIdNumericFieldValue());
			}
		}
    }
}
