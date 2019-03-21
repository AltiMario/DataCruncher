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
import com.datacruncher.constants.SchemaType;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
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

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SchemaDbTreePopupReadController implements Controller, DaoSet {

	private ObjectMapper objectMapper = new ObjectMapper();
    private final int dbInputType = 1;
    private final int dbValidationType = 2;

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

	private static final String _TEXT = "text";
	private static final String _SEPARATOR= ": ";

	private Logger log = Logger.getLogger(this.getClass());
	private String catalogueName;
	private DatabaseMetaData md;
	private SchemaEntity schemaEntity;
    private long idDatabase;
    private String linkToDb;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Set<String> catalogesSet = new HashSet<String>();
		List<String> currentCatalogueTablesList = new ArrayList<String>();
		String idSchema = request.getParameter("idSchema");
        String dbType = request.getParameter("dbType");
        String fieldId = request.getParameter("fieldId");
        boolean isBranch = request.getParameter("isBranch") == null ? false : Boolean.valueOf(request.getParameter("isBranch"));
        if (fieldId != null && !fieldId.isEmpty()) {
        	linkToDb = schemaFieldsDao.find(Long.parseLong(fieldId)).getLinkToDb();
        }
        if (idSchema == null || idSchema.equals("-1")) return null;
		schemaEntity = schemasDao.find(Long.valueOf(idSchema));
		Object responseObj = null;
		boolean isOutgoingDb = true;
		Connection connection = null;
        boolean dbIsSet = false;

		if (Integer.parseInt(dbType) == dbInputType
				&& (schemaEntity.getInputToDb() || schemaEntity.getIdInputDatabase() > 0)) {
			dbIsSet = true;
			idDatabase = schemaEntity.getIdInputDatabase();
		} else if (Integer.parseInt(dbType) == dbInputType
				&& schemaEntity.getIdSchemaType() == SchemaType.STREAM_LOADING && schemaEntity.getIdDatabase() > 0) {
			dbIsSet = true;
			idDatabase = schemaEntity.getIdDatabase();
		} else if (Integer.parseInt(dbType) == dbValidationType && schemaEntity.getIdValidationDatabase() > 0) {
			dbIsSet = true;
			idDatabase = schemaEntity.getIdValidationDatabase();
		}
        if (dbIsSet) {
			try {
				connection = getConnection(isOutgoingDb);
				md = connection.getMetaData();
				ResultSet rs = md.getTables(null, null, "%", null);
				while (rs.next()) {
					catalogesSet.add(rs.getString("TABLE_CAT"));
					currentCatalogueTablesList.add(rs.getString("TABLE_NAME"));
				}
				connection.close();
				if (catalogesSet.size() > 1) {
					throw new RuntimeException("Must be only one catalogue (example: 'DataCruncher')");
				} else if (catalogesSet.size() == 1) {
					catalogueName = catalogesSet.iterator().next();
					ObjectNode rootObjectNode = getNode(catalogueName, DbFieldType.ROOT_FOLDER);
					childTables(rootObjectNode, currentCatalogueTablesList, isBranch);
					ArrayNode rootArrayNode = objectMapper.createArrayNode();
					rootArrayNode.add(rootObjectNode);
					responseObj = rootArrayNode;
				}
			} catch (SQLException e) {
				log.error("db meta info retrieval error", e);
			}
		} else {
			ObjectNode rootObjectNode = objectMapper.createObjectNode();
			rootObjectNode.put("isNotInputToDb", "false");
			responseObj = rootObjectNode;			
		}
		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		out.write(objectMapper.writeValueAsBytes(responseObj));
		out.flush();
		out.close();	
		return null;
	}
	
	/**
	 * Get database connection.
	 * 
	 * @param isOutgoingDb - 'false' to retrieve tree from incoming database (example: 'DataCruncher')
	 * 			'true' - to retrieve tree from outgoing database (database chosen from DatabaseEntity)
	 * @return
	 */
	private Connection getConnection(boolean isOutgoingDb) throws SQLException {
		Connection connection = null;
		if (!isOutgoingDb) {
			//isOutgoingDb = false for old realization
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
	
	private void childTables(ObjectNode parentObjectNode, List<String> currentCatalogueTablesList, boolean isBranch) throws SQLException {
		ArrayNode childArrayNode = objectMapper.createArrayNode();
		for (String tableName : currentCatalogueTablesList) {
			ObjectNode node = getNode(tableName, DbFieldType.FOLDER);
			if(isBranch) {
				ArrayNode childArray = objectMapper.createArrayNode();
				node.put("children", childArray);
				node.put("expanded", false);
				if (linkToDb != null && linkToDb.toLowerCase().equals(tableName.toLowerCase())) {
					node.put("can_be_deleted_menu", true);
				}
			} else {
				childFields(node, tableName);
			}
			node.put("id", tableName);
			childArrayNode.add(node);
		}		
		parentObjectNode.put("children", childArrayNode);
		parentObjectNode.put("expanded", true);
	}

	private void childFields(ObjectNode parentObjectNode, String tableName) throws SQLException {
		ArrayNode childArrayNode = objectMapper.createArrayNode();
		ResultSet rs = md.getColumns(catalogueName, null, tableName, null);
		while (rs.next()) {
			String colName = rs.getString("COLUMN_NAME");
			ObjectNode node = getNode(colName, DbFieldType.STRING, true);
			node.put(_TEXT, node.get(_TEXT).getTextValue() + rs.getString("TYPE_NAME").toLowerCase());
			String nodeId = tableName + "." + rs.getString("COLUMN_NAME");
			if (linkToDb != null && linkToDb.toLowerCase().equals(nodeId.toLowerCase())) {
				//this branch for 'database fields' tab, edit field. Tree menu option 'delete' link to db.
				node.put("can_be_deleted_menu", true);
			}
			node.put("var_name", nodeId);
			node.put("id", nodeId);
			node.put(
					"sql_text",
					MessageFormat.format("select {0} from {1} where {2}", colName, catalogueName.toUpperCase() + "."
							+ tableName.toUpperCase(), colName)
							+ " {0} {1} ");
			childArrayNode.add(node);
		}
		parentObjectNode.put("children", childArrayNode);
		parentObjectNode.put("expanded", false);
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
}