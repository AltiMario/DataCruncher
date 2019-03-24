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

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.constants.StreamType;
import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.factories.streams.StreamGenerationResults;
import com.datacruncher.factories.streams.StreamGenerationUtils;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.AlphanumericFieldValuesEntity;
import com.datacruncher.jpa.entity.NumericFieldValuesEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.jpa.entity.SchemaSQLEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Dynamic grid response example (for read proxy):
 * {"data":[{"name":"John"}],"success":"true","metaData":{"root":"data",
 * "fields":[{"name":"name", "dataIndex":"name", "header":"Header"}]}}
 * 
 * All three fields are required: name, dataIndex, header.
 * 
 * 
 */

public class SchemaLinkedFieldsController extends MultiActionController implements DaoSet {

	@PersistenceContext
    protected EntityManager em;
	
	private ObjectMapper objectMapper = new ObjectMapper();
    private Logger log = Logger.getLogger(this.getClass());
    private static final String _HEADER = "header";
    public static final String _ENTITY_PATH = "ENTITY_PATH";

    public static final String _SQL_RESULT = "SQL_RESULT";
    private byte[] createFileContent;
    private Long createSchemaId;
    private String createSql;
    private StreamGenerationUtils streamGenUtils = new StreamGenerationUtils();

	public ModelAndView dynamicGrid(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String idSchema = request.getParameter("idSchema");
		String meta = request.getParameter("query");
        createSchemaId =  Long.parseLong(idSchema);
        String query = streamGenUtils.getSqlStr(createSchemaId,meta);
        createSql =  query;
        createFileContent ="".getBytes();
        boolean isSuccess = true;
		ObjectNode rootNode = objectMapper.createObjectNode();
		ArrayNode dataNode = objectMapper.createArrayNode();
		ObjectNode metaDataNode = objectMapper.createObjectNode();
		ArrayNode metaDataFieldsNode = objectMapper.createArrayNode();
		SchemaEntity schemaEnt = schemasDao.find(Long.parseLong(idSchema));
		List<Map<String, Object>> linkedFieldsPaths = streamGenUtils.getLinkedFieldsPaths(Long.parseLong(idSchema));
        Connection connection = ConnectionPoolsSet.getConnection(schemaEnt.getIdInputDatabase());
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int j = 0;
            StreamGenerationResults streamGenRes  = new StreamGenerationResults();
            streamGenRes.setSuccess(true);
            streamGenRes.setMessage("");
            streamGenRes.setObj(null);
            streamGenRes = streamGenUtils.linkColsWithFields(streamGenRes,rsmd,linkedFieldsPaths);
            if (streamGenRes.isSuccess()){
                List<Object[]> linkedFields = (List<Object[]>) streamGenRes.getObj();
                String[] fieldsName = new String[linkedFields.size()];
                while (rs.next()) {
                    ObjectNode node = objectMapper.createObjectNode();
                    for (int i = 1; i <= columnCount; i++) {
                        if (j == 0) {
                            Object[] mapColField = linkedFields.get(i);
                            Map<String, Object> map = (Map)mapColField[1];
                            String fieldName = (String) mapColField[0];
                            ObjectNode nodeHeader = objectMapper.createObjectNode();
                            nodeHeader.put("name", fieldName);
                            nodeHeader.put("dataIndex", fieldName);
                            nodeHeader.put("header", ((String) map.get(_HEADER)));
                            //any digit - column width will fit to header's text
                            nodeHeader.put("width", "300");
                            metaDataFieldsNode.add(nodeHeader);
                            fieldsName[i]= fieldName;
                        }
                        String value = rs.getObject(i)== null? "":rs.getObject(i).toString();
                        node.put(fieldsName[i], value);
						//node.put(fieldsName[i], rsmd.getColumnType(i) == Types.VARBINARY ? String.valueOf(rs.getInt(i))
							//	: rs.getObject(i).toString());
					}
                    j++;
                    dataNode.add(node);
                }
            }else{
                isSuccess = false;
                log.error(streamGenRes.getMessage());
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            isSuccess = false;
            log.error("Sql Query execution error", e);
        }
		metaDataNode.put("root", "data");
		metaDataNode.put("fields", metaDataFieldsNode);
		rootNode.put("metaData", metaDataNode);
		rootNode.put("data", dataNode);
		rootNode.put("success", isSuccess);
		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
		out.write(objectMapper.writeValueAsBytes((Object) rootNode));
		return null;
	}

	
    public ModelAndView checkStream(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long idSchema = Long.parseLong(request.getParameter("idSchema"));
        String query;
        if (idSchema == createSchemaId) {
            query =  createSql;
        }else{
            query = streamGenUtils.getSqlStr(createSchemaId,request.getParameter("query"));
            createSchemaId = idSchema;
            createSql =  query;
            createFileContent ="".getBytes();
        }
        Map<String, String> resMap = new HashMap<String, String>();
        String success = "true";
        String responseMsg = "";
        try{
            byte[] fileContent;
            if(new String(createFileContent).equals("")){
                fileContent = streamGenUtils.createStream(idSchema, query);
                createFileContent = fileContent;
                if (fileContent == null || fileContent.length==0) {
                    success = "false";
                    responseMsg = "Empty file content.";
                }
            }else{
                fileContent =  createFileContent;
            }
        } catch (Exception e) {
            log.error("Create file stream error: ", e);
            success = "false";
            responseMsg = "Create file stream error: "+e.getMessage();
		} finally {
			resMap.put("success", success);
			resMap.put("responseMsg", responseMsg);
			log.debug(new Date() + " - end  createStream: " + success);
			response.getWriter().print(new JSONObject(resMap).toString());
		}
		return null; 
    }

    public ModelAndView downloadStream(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        Long idSchema = Long.parseLong(request.getParameter("idSchema"));
        if (!idSchema.equals(createSchemaId))
            throw new RuntimeException("Create schema id is not matched with current schema!");
        try {
            byte[] fileContent = createFileContent;
            SchemaEntity schemaEnt = schemasDao.find(idSchema);
            String fileName = HttpUtils.encodeContentDispositionForDownload(request,
                    idSchema + "." + StreamType.getFileExtension(schemaEnt.getIdStreamType()), false);

            OutputStream ostr = response.getOutputStream();
            response.setContentType(getServletContext().getMimeType(fileName));
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-disposition", fileName);
            response.setContentLength(fileContent.length);

            ostr.write(fileContent);
            ostr.flush();
            ostr.close();
            log.debug( new Date()+" - end downloadStream");
        } catch (Exception e) {
            String failMsg = "Error saving file";
            log.error(failMsg, e);
            response.getWriter().print("{\"responseMsg\":\"" + failMsg + ".\",\"success\":\"false\"}");
            response.getWriter().flush();
            response.getWriter().close();
            return null;
        }
        return null;
    }


	/**
	 * Validate stream button handler.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public ModelAndView validateStream(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long idSchema = Long.parseLong(request.getParameter("idSchema"));
        String query;
        if (idSchema == createSchemaId) {
            query =  createSql;
        }else{
            query = streamGenUtils.getSqlStr(createSchemaId,request.getParameter("query"));
            createSchemaId = idSchema;
            createSql =  query;
            createFileContent ="".getBytes();
        }
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        try{
            byte[] fileContent;
            if(new String(createFileContent).equals("")){
                fileContent = streamGenUtils.createStream(idSchema, query);
                if (fileContent == null || fileContent.length==0)
                    fileContent="".getBytes();
                createFileContent = fileContent;
            }else{
                fileContent =  createFileContent;
            }

            String dataStream = new String(fileContent);
            DatastreamsInput datastreamsInput = new DatastreamsInput();
            int streamType = schemasDao.find(idSchema).getIdStreamType();
			if (streamType == StreamType.EXCEL) {
				datastreamsInput.setUploadedFileName(idSchema + "." + StreamType.getFileExtension(streamType));
			}

            out.write(datastreamsInput.datastreamsInput(dataStream, idSchema, fileContent).getBytes());
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            log.error("Validate file stream error: ", e);
            out.write(("{success: false, responseMsg: '" + e.getMessage() + "'}").getBytes());
            out.flush();
            out.close();
            return null;
        }

	}

	/**
	 * Used by edit query button. Gets sql string by meta query. 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public ModelAndView getSqlFromMeta(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long idSchema = Long.parseLong(request.getParameter("idSchema"));
        String meta = request.getParameter("query");
		String query = streamGenUtils.getSqlStr(idSchema,meta);
		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
		out.write(query.getBytes());
		out.flush();
		out.close();
		return null;
	}
	
	/**
	 * Checks whether user sql is correct.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public ModelAndView validateSql(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String meta = request.getParameter("query");

		String idSchema = request.getParameter("idSchema");
		SchemaEntity schemaEnt = schemasDao.find(Long.parseLong(idSchema));
		Connection connection = ConnectionPoolsSet.getConnection(schemaEnt.getIdInputDatabase());		
		Statement statement = null;
		ResultSet rs = null;
        StreamGenerationResults streamGenRes = new StreamGenerationResults();
        streamGenRes.setSuccess(true);
        streamGenRes.setMessage("");
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(meta);
            ResultSetMetaData rsmd = rs.getMetaData();
            streamGenRes = streamGenUtils.checkFieldsValidity(Long.parseLong(idSchema),rsmd);
        } catch (SQLException e) {
			//silent catch
            streamGenRes.setSuccess(false);
            streamGenRes.setMessage("SQL Exception.");
            log.error("ValidateSql - SQL Exception: "+e.getMessage());
        } catch (Exception e) {
            //silent catch
            streamGenRes.setSuccess(false);
            streamGenRes.setMessage("Exception: ");
            log.error("ValidateSql - Exception: "+e.getMessage());
        } finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (rs != null) {
					rs.close();
				}
				connection.close();
			} catch (SQLException e) {
				log.error("ValidateSql - Connection|statement|resultSet close exception", e);
			}
		}
		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        out.write(("{success:" + (streamGenRes.isSuccess() ? "true" : "false") + ", message: '" + streamGenRes.getMessage() + "'}").getBytes("UTF8"));
		out.flush();
		out.close();
		return null;
	}

	public ModelAndView getSqlByIdSchema(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean isSuccess = true;
		boolean isCustom = false;
		String idSchema = request.getParameter("idSchema");
		SchemaSQLEntity schemaSQL = schemaSQLDao.read(Long.parseLong(idSchema));
		String strSql = "";
		try {
			if (schemaSQL != null) {
				strSql = schemaSQL.getSchemaSQL();
				isCustom = schemaSQL.isCustomQuery();
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		response.setContentType("application/json");
		ServletOutputStream out = response.getOutputStream();
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.put("success", String.valueOf(isSuccess));
		resMap.put("strSql", strSql);
		resMap.put("isCustom", String.valueOf(isCustom));
		out.write(new JSONObject(resMap).toString().getBytes());
		out.flush();
		out.close();
		return null;
	}

	public ModelAndView setSqlByIdSchema(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String strSQL = request.getParameter("query");
		String idSchema = request.getParameter("idSchema");
		String isCustom = request.getParameter("isCustom");
		String meta = request.getParameter("meta");
		SchemaSQLEntity schemaSQLEntity = schemaSQLDao.read(Long.parseLong(idSchema));
		Create create;
		Update update;
		Map<String, String> resMap = new HashMap<String, String>();
		if (schemaSQLEntity == null) {
			schemaSQLEntity = new SchemaSQLEntity();
			schemaSQLEntity.setIdSchemaSQL(Long.parseLong(idSchema));
			schemaSQLEntity.setCustomQuery(Boolean.parseBoolean(isCustom));
			schemaSQLEntity.setSchemaSQL(strSQL);
			create = schemaSQLDao.create(schemaSQLEntity);
			resMap.put("success", String.valueOf(create.getSuccess()));
			resMap.put("message", create.getMessage());
		} else {
			schemaSQLEntity.setSchemaSQL(strSQL);
			schemaSQLEntity.setCustomQuery(Boolean.parseBoolean(isCustom));
			update = schemaSQLDao.update(schemaSQLEntity);
			resMap.put("success", String.valueOf(update.isSuccess()));
			resMap.put("message", update.getMessage());
		}
		SchemaEntity ent = schemasDao.find(Long.parseLong(idSchema));
		ent.setMetaCondition(meta);
		schemasDao.update(ent);
		response.setContentType("application/json");
		ServletOutputStream out = response.getOutputStream();
		out.write(new JSONObject(resMap).toString().getBytes());
		out.flush();
		out.close();
		return null;
	}

	public ModelAndView deleteSqlByIdSchema(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idSchema = request.getParameter("idSchema");
		ServletOutputStream out = response.getOutputStream();
		out.write(schemaSQLDao.destroy(Long.parseLong(idSchema)).isSuccess() ? "true".getBytes() : "false".getBytes());
		out.flush();
		out.close();
		return null;
	}
	
	public ModelAndView getMetaCondition(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idSchema = request.getParameter("idSchema");
		String result = schemasDao.find(Long.parseLong(idSchema)).getMetaCondition();
		ServletOutputStream out = response.getOutputStream();
		out.write(result == null ? "".getBytes() : result.getBytes());
		out.flush();
		out.close();
		return null;
	}
  
    public ModelAndView deleteLinkToDb(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String idField = request.getParameter("idField");
		SchemaFieldEntity ent = schemaFieldsDao.find(Long.parseLong(idField));
		if(ent.getLinkToDb().indexOf(".") == -1) {
			deleteLinkedTableFields(ent);
		}
		ent.setLinkToDb("");
		schemaFieldsDao.update(ent);
		return null;
	}
    private void deleteLinkedTableFields(SchemaFieldEntity fieldEntity) {
		try {
			Connection connection = getConnection(String.valueOf(fieldEntity.getIdSchema()), true);
			DatabaseMetaData md = connection.getMetaData();
			String tableName = fieldEntity.getLinkToDb();
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