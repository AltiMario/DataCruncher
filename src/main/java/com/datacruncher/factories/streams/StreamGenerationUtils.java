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

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.constants.FieldType;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.jpa.entity.SchemaSQLEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StreamGenerationUtils implements DaoSet {
    private Logger log = Logger.getLogger(this.getClass());
    private static final String _HEADER = "header";
    private static final String _PATH = "db_path";
    private static final String _WHERE = "where";
    private static final String _META_QUERY_PATTERN = "(\\w+[.]\\w+)( (>|<|<=|>=|=|!=) ('[a-zA-Z_0-9\\.]+'))?";
    private static final String _ENTITY_PATH = "ENTITY_PATH";
    private static final String _SQL_RESULT = "SQL_RESULT";


    public static String getPathParam() {
        return _PATH;
    }

    public static String getEntityPathParam() {
        return _ENTITY_PATH;
    }

    public static String getSqlResultParam() {
        return _SQL_RESULT;
    }

    /**
     * Add "where" sql clauses to linkedFieldsPaths list according to meta info.
     *
     * @param meta
     * @param linkedFieldsPaths
     */
    public static void proceedWhereClauses(String meta, List<Map<String, Object>> linkedFieldsPaths) {
        if (meta != null && !meta.isEmpty()) {
            for (String singleMeta : meta.split(";")) {
                Pattern pattern = Pattern.compile(_META_QUERY_PATTERN, Pattern.CASE_INSENSITIVE);
                System.out.println(singleMeta);
                Matcher matcher = pattern.matcher(singleMeta.trim());
                matcher.matches();
                String path = matcher.group(1);
                boolean isWhereExists = matcher.group(2) != null;
                String whereClause = isWhereExists ? path.split("[.]")[1] + matcher.group(2) : "";
                for (Map<String, Object> map : linkedFieldsPaths) {
                    if (path.equals(map.get(_PATH)) && isWhereExists) {
                        String prefix = map.get(_WHERE) == null ? " WHERE " : " AND ";
                        map.put(_WHERE, (map.get(_WHERE) == null ? "" : map.get(_WHERE)) + prefix + whereClause);
                    } else if (path.equals(map.get(_PATH)) && !isWhereExists) {
                        map.put(_WHERE, "");
                    }
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public byte[] createStream(long idSchema, String query) {
		byte[] fileContent;
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		SchemaEntity schemaEnt = schemasDao.find(idSchema);
		List<Map<String, Object>> linkedFieldsPaths = getLinkedFieldsPaths(idSchema);
		if (linkedFieldsPaths.size() > 0 && schemaEnt.getIdInputDatabase() > 0) {
			connection = ConnectionPoolsSet.getConnection(schemaEnt.getIdInputDatabase());
			try {
				statement = connection.createStatement();
				List<String> vList;
				for (Map<String, Object> map : linkedFieldsPaths) {
					vList = new ArrayList<String>();
					map.put(_SQL_RESULT, vList);
				}
				rs = statement.executeQuery(query);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						for (int count = 0; count < linkedFieldsPaths.size(); count++) {
							if (i == (count + 1)) {
								Map<String, Object> map = linkedFieldsPaths.get(count);
								vList = (List<String>) map.get(_SQL_RESULT);
								Object obj = rs.getObject(i);
								if (obj != null) {
									vList.add(rs.getObject(i).toString());
								} else {
									vList.add("");
								}
								map.put(_SQL_RESULT, vList);
							}
						}
					}
				}
			} catch (SQLException e) {
				log.error("Sql Query execution error", e);
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ignored) {
					}
				}
				if (statement != null) {
					try {
						statement.close();
					} catch (Exception ignored) {
					}
				}
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						log.error("SQL Exception during connection closure", e);
					}
				}
			}
		}
        fileContent = StreamsToDownloadFactory.getStreamsInstance(schemaEnt, linkedFieldsPaths)
                .getDownloadableStreams();
        return fileContent;
    }

    public static List<String> getLinkedTables(long idSchema) {
        List<String> linkedTables = new ArrayList<String>();
        String oldTable="";
        for (SchemaFieldEntity sfe : schemaFieldsDao.listSchemaFields(Long.valueOf(idSchema))) {
            String path = sfe.getLinkToDb();
            if (sfe.getIdFieldType() >= FieldType.alphanumeric && path != null && !path.isEmpty()) {
                String table = path.split("[.]")[0];
                if(linkedTables.size()== 0){
                    linkedTables.add(table);
                    oldTable = table;
                }else if (!table.equals(oldTable)) {
                    if(!linkedTables.contains(table)) {
                        linkedTables.add(table);
                    }
                    oldTable = table;
                }
            }
        }
        return linkedTables;
    }
    public static List<Map<String, Object>> getLinkedFieldsPaths(long idSchema) {
        List<Map<String, Object>> linkedFieldsPaths = new ArrayList<Map<String, Object>>();
        for (SchemaFieldEntity sfe : schemaFieldsDao.listSchemaFields(Long.valueOf(idSchema))) {
            String path = sfe.getLinkToDb();
            if (sfe.getIdFieldType() >= FieldType.alphanumeric && path != null && !path.isEmpty()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(_PATH, path);
                String entPath = sfe.getPath(".");
                map.put(_HEADER, entPath.toLowerCase() + " -> " + path);
                map.put(_ENTITY_PATH, entPath);

                linkedFieldsPaths.add(map);
            }
        }
        return linkedFieldsPaths;
    }
    public String getStream(long idSchema){
        try {
            String query = getSqlStr(idSchema, null);
            byte[] fileContent = createStream(idSchema, query);
            String datastream = new String(fileContent);
            return datastream;
        } catch (Exception e) {
            log.error("getSqlStr - error:",e);
            return null;
        }

    }
    /*private String getSqlStr(long idSchema) {
        SchemaSQLEntity schemaSQL = schemaSQLDao.read(idSchema);
        String strSql = "";
        try {
            if (schemaSQL != null) {
                strSql = schemaSQL.getSchemaSQL();
            }else{
                SchemaEntity schema = schemasDao.find(idSchema);
                strSql = schema.getMetaCondition();
            }
        } catch (Exception e) {
            log.error("getSqlStr - error:",e);
            return strSql;
        }
        return strSql;
    }*/
    public String getSqlStr(long idSchema,String meta){
        String query = "";
        String sqlInDb = getCustomQuery(idSchema);
        if (sqlInDb != null && !sqlInDb.isEmpty()) {
            query = sqlInDb;
        } else {
            List<Map<String, Object>> linkedFieldsPaths = getLinkedFieldsPaths(idSchema);
            proceedWhereClauses(meta, linkedFieldsPaths);
            String firstTable = null;
            String fields = "";
            String whereClause = "";
            boolean firstField = true;
            List<String> queryList = new ArrayList<String>();
            List<String> linkedTables = getLinkedTables(idSchema);
            for (String currentTable : linkedTables) {
                firstField = true;
                for (int i = 0; i < linkedFieldsPaths.size(); i++) {
                    Map<String, Object> map = linkedFieldsPaths.get(i);
                    String tableName = ((String) map.get(_PATH)).split("[.]")[0];
                    String fieldName = ((String) map.get(_PATH)).split("[.]")[1];
                    String streamFieldName = ((String) map.get(_ENTITY_PATH)).toLowerCase();

                    firstTable = currentTable;

                    if (tableName.equals(currentTable)) {
                        fields += (firstField? "" : ", ") + fieldName + " as " + "'"+streamFieldName+"'";
                        whereClause += (map.get(_WHERE) == null || ((String) map.get(_WHERE)).isEmpty()) ? "" :
                                (firstField || whereClause.isEmpty() ? "" : " and ")
                                        + ((String) map.get(_WHERE)).substring(6);
                    } else{
                        fields += (firstField? "" : ", ") + "''" + " as " + "'"+streamFieldName+"'";
                    }
                    firstField = false;
                }

                query = MessageFormat.format("select {0} \n from {1} \n where {2}", fields, firstTable, whereClause);
                //if there's no where conditions -> cut off 'where'
                if (query.trim().endsWith("where")) query = query.substring(0, query.length() - 6);
                queryList.add(query);
                query = "";
                fields = "";
                whereClause = "";
            }
            for (String qry : queryList) {
                query += (query == ""? "" : " \nUNION ALL \n") +  qry;
            }
        }
        return query;
    }
    public String getCustomQuery(long idSchema) {
        SchemaSQLEntity schemaSQL = schemaSQLDao.read(idSchema);
        String strSql = null;
        try {
            if (schemaSQL != null) {
                strSql = schemaSQL.getSchemaSQL().trim();
                if (strSql.equals("") || strSql.isEmpty())
                    strSql = null;
            }
            return strSql;
        } catch (Exception e) {
            log.error("getSqlStr - error:",e);
            return null;
        }
    }
    public StreamGenerationResults linkColsWithFields(StreamGenerationResults streamGenRes,ResultSetMetaData rsmd,List<Map<String, Object>> linkedFieldsPaths)
            throws SQLException {
        String fieldName="";
        int linkedCols = 0;
        List<Object[]> linkedFields = new ArrayList<Object[]>();
        linkedFields.add(new Object[3]);
        int columnCount = rsmd.getColumnCount();
        int fieldCount = linkedFieldsPaths.size();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = (rsmd.getColumnLabel(i)).toLowerCase();
            Object[] mapColField = new Object[3];
            //mapColField[1] = new Integer(i);  // column index
            mapColField[0] = columnName;  // field Name
            mapColField[1] = null;  // map field
            mapColField[2] = new Boolean(false); //linked
            linkedFields.add(i,mapColField);
            for (Map<String, Object> map : linkedFieldsPaths) {
                String currFieldName = ((String) map.get(_ENTITY_PATH)).toLowerCase();
                if (columnName.equals(currFieldName)) {
                    fieldName = ((String) map.get(_PATH)).replaceAll("[.]", "/");
                    mapColField = linkedFields.get(i);
                    mapColField[0] = fieldName;  // field Name
                    mapColField[1] = map;  // map field
                    mapColField[2] = new Boolean(true); //linked
                    linkedFields.set(i,mapColField);
                    linkedCols++;
                    linkedFieldsPaths.remove(map);
                    break;
                }
            }
        }
        if (linkedCols < fieldCount){
            streamGenRes.setSuccess( false);
            String msg = "";
            for(int intIndex = 1; intIndex < linkedFields.size(); intIndex++){
                Object[] mapColField = linkedFields.get(intIndex);
                if(!(Boolean)mapColField[2]){
                    msg += (msg.equals("")? "" : "\n") + "The field " +mapColField[0]+" linked with the column " + intIndex + " does not exist or was previously linked." ;

                }
            }
            streamGenRes.setMessage(msg);
        }else{
            streamGenRes.setObj(linkedFields);
        }
        return streamGenRes;
    }
    public StreamGenerationResults checkFieldsValidity(long idSchema,ResultSetMetaData rsmd) {
        StreamGenerationResults streamGenRes  = new StreamGenerationResults();
        streamGenRes.setSuccess(true);
        streamGenRes.setMessage("");
        streamGenRes.setObj(null);
        try{
            List<Map<String, Object>> linkedFieldsPaths = getLinkedFieldsPaths(idSchema);
            int columnCount = rsmd.getColumnCount();
            int fieldCount = linkedFieldsPaths.size();
            if (columnCount < fieldCount){
                streamGenRes.setSuccess( false);
                streamGenRes.setMessage("The number of columns in the query is less than the number of linked fields in the schema.");
            }else if (columnCount > fieldCount){
                streamGenRes.setSuccess( false);
                streamGenRes.setMessage("The number of columns in the query is greater than the number of linked fields in the schema.");
            }else {
                streamGenRes = linkColsWithFields(streamGenRes,rsmd,linkedFieldsPaths);
            }
        }catch (Exception e) {
            streamGenRes.setSuccess(false);
            streamGenRes.setMessage("checkFieldsValidity - Exception: "+e.getMessage());
        }
        return streamGenRes;
    }
}