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
 */

package com.seer.datacruncher.profiler.bl;

import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.dto.PropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.DataInfoPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.FunctionPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.IndexPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.ParameterPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.ProcedurePropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.SqlTypeInfoPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.TableMetaDataPropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.TablePrivilegePropertyDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.UserTypeSqlInfoDTO;
import com.seer.datacruncher.profiler.framework.profile.DBMetaInfo;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;
import com.seer.datacruncher.profiler.framework.rdbms.SqlType;
import com.seer.datacruncher.profiler.framework.rdbms.TableRelationInfo;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class DbInfoBL {
	public GridInfoDTO getGeneralInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<PropertyDTO> items = new ArrayList<PropertyDTO>();
		PropertyDTO property = null;
		try {
			String s1 = dbmd.getDatabaseProductName();
			property = new PropertyDTO();
			property.setProperty("Database Product");
			property.setValue(s1);
			items.add(property);
		} catch (UnsupportedOperationException unsupportedoperationexception) {
		}
		try {
			String s2 = dbmd.getDatabaseProductVersion();
			property = new PropertyDTO();
			property.setProperty("Database Versio");
			property.setValue(s2);
			items.add(property);
		} catch (UnsupportedOperationException unsupportedoperationexception1) {
		}
		try {
			String s3 = dbmd.getURL();
			property = new PropertyDTO();
			property.setProperty("URL for this DBMS");
			property.setValue(s3);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception2) {
		}
		try {
			String s4 = dbmd.getDriverName();
			property = new PropertyDTO();
			property.setProperty("JDBC Driver Name");
			property.setValue(s4);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception3) {
		}
		try {
			String s5 = dbmd.getDriverVersion();
			property = new PropertyDTO();
			property.setProperty("JDBC Driver Version");
			property.setValue(s5);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception4) {
		}
		try {
			String s6 = dbmd.getExtraNameCharacters();
			property = new PropertyDTO();
			property.setProperty("EXTRA characters used in unquoted identifier names");
			property.setValue(s6);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception5) {
		}
		try {
			String s7 = dbmd.getIdentifierQuoteString();
			property = new PropertyDTO();
			property.setProperty("String used to quote SQL identifiers");
			property.setValue(s7);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception6) {
		}
		try {
			String s8 = dbmd.getCatalogSeparator();
			property = new PropertyDTO();
			property.setProperty("Separator between a Catalog and Table name");
			property.setValue(s8);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception7) {
		}
		try {
			String s9 = dbmd.getCatalogTerm();
			property = new PropertyDTO();
			property.setProperty("Database vendor preferred term for CATALOG");
			property.setValue(s9);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception8) {
		}
		try {
			boolean flag = dbmd.isCatalogAtStart();
			property = new PropertyDTO();
			property.setProperty("Catalog appears at the start of a fully qualified table name");
			property.setValue(flag ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception9) {
		}
		try {
			String s10 = dbmd.getSchemaTerm();
			property = new PropertyDTO();
			property.setProperty("Database vendor preferred term for SCHEMA");
			property.setValue(s10);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception10) {
		}
		try {
			String s11 = dbmd.getProcedureTerm();
			property = new PropertyDTO();
			property.setProperty("Database vendor preferred term for PROCEDURE");
			property.setValue(s11);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception11) {
		}
		try {
			String s12 = dbmd.getSearchStringEscape();
			property = new PropertyDTO();
			property.setProperty("String that can be used to escape wildcard characters");
			property.setValue(s12);
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception12) {
		}
		try {
			boolean flag1 = dbmd.allProceduresAreCallable();
			property = new PropertyDTO();
			property.setProperty("All Procedures are Callbale");
			property.setValue(flag1 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception13) {
		}
		try {
			boolean flag2 = dbmd.allTablesAreSelectable();
			property = new PropertyDTO();
			property.setProperty("All Table are Selectable");
			property.setValue(flag2 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception14) {
		}
		try {
			boolean flag3 = dbmd.isReadOnly();
			property = new PropertyDTO();
			property.setProperty("Database is Read Only");
			property.setValue(flag3 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception15) {
		}
		try {
			boolean flag4 = dbmd.locatorsUpdateCopy();
			property = new PropertyDTO();
			property.setProperty("Updates made to a LOB  made on a copy or to LOB");
			property.setValue(flag4 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception16) {
		}
		try {
			boolean flag5 = dbmd.nullPlusNonNullIsNull();
			property = new PropertyDTO();
			property.setProperty("Concatenates NULL and non-NULL as NULL");
			property.setValue(flag5 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception17) {
		}
		try {
			boolean flag6 = dbmd.nullsAreSortedAtEnd();
			property = new PropertyDTO();
			property.setProperty("Nulls are Sorted at End");
			property.setValue(flag6 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception18) {
		}
		try {
			boolean flag7 = dbmd.nullsAreSortedHigh();
			property = new PropertyDTO();
			property.setProperty("Nulls are Sorted High");
			property.setValue(flag7 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception19) {
		}
		try {
			boolean flag8 = dbmd.storesLowerCaseIdentifiers();
			property = new PropertyDTO();
			property.setProperty("Unquoted SQL identifiers stored in lower case");
			property.setValue(flag8 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception20) {
		}
		try {
			boolean flag9 = dbmd.storesLowerCaseQuotedIdentifiers();
			property = new PropertyDTO();
			property.setProperty("Quoted SQL identifiers stored in lower case");
			property.setValue(flag9 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception21) {
		}
		try {
			boolean flag10 = dbmd.usesLocalFiles();
			property = new PropertyDTO();
			property.setProperty("Database stores tables in a local file");
			property.setValue(flag10 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception22) {
		}
		try {
			boolean flag11 = dbmd.usesLocalFilePerTable();
			property = new PropertyDTO();
			property.setProperty("Database uses a file for each table");
			property.setValue(flag11 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception23) {
		}
		try {
			boolean flag12 = dbmd.dataDefinitionIgnoredInTransactions();
			property = new PropertyDTO();
			property.setProperty("Database ignores a data definition statement within a transaction");
			property.setValue(flag12 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception24) {
		}
		try {
			boolean flag13 = dbmd.doesMaxRowSizeIncludeBlobs();
			property = new PropertyDTO();
			property.setProperty("Max Row Size includes Blob");
			property.setValue(flag13 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception25) {
		}

		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		RdbmsConnection.closeConn();

		return dbInfo;
	}

	public GridInfoDTO getSupportInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<PropertyDTO> items = new ArrayList<PropertyDTO>();
		PropertyDTO property = null;
		try {
			boolean flag14 = dbmd.supportsAlterTableWithAddColumn();
			property = new PropertyDTO();
			property.setProperty("Database supports ALTER TABLE with ADD column");
			property.setValue(flag14 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception) {
		}
		try {
			boolean flag15 = dbmd.supportsAlterTableWithDropColumn();
			property = new PropertyDTO();
			property.setProperty("Database supports ALTER TABLE with DROP column");
			property.setValue(flag15 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception1) {
		}
		try {
			boolean flag16 = dbmd.supportsANSI92EntryLevelSQL();
			property = new PropertyDTO();
			property.setProperty("Databse supports ANSI192 Entry Level SQL");
			property.setValue(flag16 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception2) {
		}
		try {
			boolean flag17 = dbmd.supportsANSI92IntermediateSQL();
			property = new PropertyDTO();
			property.setProperty("Databse supports ANSI192 Intermediate  SQL");
			property.setValue(flag17 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception3) {
		}
		try {
			boolean flag18 = dbmd.supportsANSI92FullSQL();
			property = new PropertyDTO();
			property.setProperty("Database supports ANSI192 Full SQL");
			property.setValue(flag18 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception4) {
		}
		try {
			boolean flag19 = dbmd.supportsBatchUpdates();
			property = new PropertyDTO();
			property.setProperty("Database support Batch Update");
			property.setValue(flag19 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception5) {
		}
		try {
			boolean flag20 = dbmd.supportsCatalogsInDataManipulation();
			property = new PropertyDTO();
			property.setProperty("Database support catalog name in a data manipulation statement");
			property.setValue(flag20 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception6) {
		}
		try {
			boolean flag21 = dbmd.supportsCatalogsInIndexDefinitions();
			property = new PropertyDTO();
			property.setProperty("Database support catalog name in Index Definition statement");
			property.setValue(flag21 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception7) {
		}
		try {
			boolean flag22 = dbmd.supportsCatalogsInPrivilegeDefinitions();
			property = new PropertyDTO();
			property.setProperty("Database support catalog name in Privilege Definition statement");
			property.setValue(flag22 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception8) {
		}
		try {
			boolean flag23 = dbmd.supportsCatalogsInProcedureCalls();
			property = new PropertyDTO();
			property.setProperty("Database support catalog name in Procedure Call statement");
			property.setValue(flag23 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception9) {
		}
		try {
			boolean flag24 = dbmd.supportsCatalogsInTableDefinitions();
			property = new PropertyDTO();
			property.setProperty("Database support catalog name in Table Definition statement");
			property.setValue(flag24 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception10) {
		}
		try {

			boolean flag25 = dbmd.supportsColumnAliasing();
			property = new PropertyDTO();
			property.setProperty("Database supports Column Aliasing");
			property.setValue(flag25 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception11) {
		}
		try {

			boolean flag26 = dbmd.supportsConvert();
			property = new PropertyDTO();
			property.setProperty("Database supports the CONVERT for two given SQL types");
			property.setValue(flag26 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception12) {
		}
		try {

			boolean flag27 = dbmd.supportsCoreSQLGrammar();
			property = new PropertyDTO();
			property.setProperty("Database supports ODBC core SQL grammar");
			property.setValue(flag27 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception13) {
		}
		try {

			boolean flag28 = dbmd.supportsCorrelatedSubqueries();
			property = new PropertyDTO();
			property = new PropertyDTO();
			property.setProperty("Database supports Correlated Subqueries");
			property.setValue(flag28 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception14) {
		}
		try {

			boolean flag29 = dbmd
					.supportsDataDefinitionAndDataManipulationTransactions();
			property = new PropertyDTO();
			property.setProperty("Database supports both data definition and data manipulation statements within a transaction");
			property.setValue(flag29 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception15) {
		}
		try {

			boolean flag30 = dbmd.supportsDataManipulationTransactionsOnly();
			property = new PropertyDTO();
			property.setProperty("Database supports data manipulation statements only within a transaction");
			property.setValue(flag30 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception16) {
		}
		try {

			boolean flag31 = dbmd.supportsExpressionsInOrderBy();
			property = new PropertyDTO();
			property.setProperty("Database supports Expressions in ORDER BY lists");
			property.setValue(flag31 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception17) {
		}
		try {

			boolean flag32 = dbmd.supportsExtendedSQLGrammar();
			property = new PropertyDTO();
			property.setProperty("Database supports ODBC Extended SQL Grammar");
			property.setValue(flag32 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception18) {
		}
		try {

			boolean flag33 = dbmd.supportsFullOuterJoins();
			property = new PropertyDTO();
			property.setProperty("Database supports Full nested Outer Join");
			property.setValue(flag33 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception19) {
		}
		try {

			boolean flag34 = dbmd.supportsGetGeneratedKeys();
			property = new PropertyDTO();
			property.setProperty("Auto-generated keys can be retrieved after a statement has been executed");
			property.setValue(flag34 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception20) {
		}
		try {

			boolean flag35 = dbmd.supportsGroupBy();
			property = new PropertyDTO();
			property.setProperty("Database supports some form of GROUP BY");
			property.setValue(flag35 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception21) {
		}
		try {

			boolean flag36 = dbmd.supportsGroupByUnrelated();
			property = new PropertyDTO();
			property.setProperty("Database supports using a column that is not in the SELECT statement in a GROUP BY clause");
			property.setValue(flag36 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception22) {
		}
		try {

			boolean flag37 = dbmd.supportsIntegrityEnhancementFacility();
			property = new PropertyDTO();
			property.setProperty("Database supports the SQL Integrity Enhancement Facility");
			property.setValue(flag37 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception23) {
		}
		try {

			boolean flag38 = dbmd.supportsLikeEscapeClause();
			property = new PropertyDTO();
			property.setProperty("Database supports specifying a LIKE escape clause");
			property.setValue(flag38 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception24) {
		}
		try {

			boolean flag39 = dbmd.supportsLimitedOuterJoins();
			property = new PropertyDTO();
			property.setProperty("Database has limited support for outer joins");
			property.setValue(flag39 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception25) {
		}
		try {

			boolean flag40 = dbmd.supportsMinimumSQLGrammar();
			property = new PropertyDTO();
			property.setProperty("Database supports the ODBC Minimum SQL grammar");
			property.setValue(flag40 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception26) {
		}
		try {
			boolean flag41 = dbmd.supportsNamedParameters();
			property = new PropertyDTO();
			property.setProperty("Database supports named parameters to callable statements");
			property.setValue(flag41 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception27) {
		}
		try {
			boolean flag42 = dbmd.supportsNonNullableColumns();
			property = new PropertyDTO();
			property.setProperty("Columns in this database may be defined as non-nullable");
			property.setValue(flag42 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception28) {
		}
		try {
			boolean flag43 = dbmd.supportsOrderByUnrelated();
			property = new PropertyDTO();
			property.setProperty("Database supports using a column that is not in the SELECT statement in an ORDER BY clause");
			property.setValue(flag43 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception29) {
		}
		try {
			boolean flag44 = dbmd.supportsOuterJoins();
			property = new PropertyDTO();
			property.setProperty("Database supports some form of Outer Join");
			property.setValue(flag44 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception30) {
		}
		try {
			boolean flag45 = dbmd.supportsPositionedUpdate();
			property = new PropertyDTO();
			property.setProperty("Database supports positioned UPDATE statements");
			property.setValue(flag45 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception31) {
		}
		try {
			boolean flag46 = dbmd.supportsPositionedDelete();
			property = new PropertyDTO();
			property.setProperty("Database supports positioned DELETE statements");
			property.setValue(flag46 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception32) {
		}
		try {
			boolean flag47 = dbmd.supportsSavepoints();
			property = new PropertyDTO();
			property.setProperty("Database supports Save Points");
			property.setValue(flag47 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception33) {
		}
		try {
			boolean flag48 = dbmd.supportsStatementPooling();
			property = new PropertyDTO();
			property.setProperty("Database supports statement pooling");
			property.setValue(flag48 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception34) {
		}
		try {
			boolean flag49 = dbmd.supportsSubqueriesInQuantifieds();
			property = new PropertyDTO();
			property.setProperty("Database supports subqueries in quantified expressions");
			property.setValue(flag49 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception35) {
		}
		try {
			boolean flag50 = dbmd.supportsSubqueriesInIns();
			property = new PropertyDTO();
			property.setProperty("Database supports subqueries in IN statements");
			property.setValue(flag50 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception36) {
		}
		try {
			boolean flag51 = dbmd.supportsSubqueriesInExists();
			property = new PropertyDTO();
			property.setProperty("Database supports subqueries in EXISTS expressions");
			property.setValue(flag51 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception37) {
		}
		try {
			boolean flag52 = dbmd.supportsTransactions();
			property = new PropertyDTO();
			property.setProperty("Database supports transactions");
			property.setValue(flag52 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception38) {
		}
		try {
			boolean flag53 = dbmd.supportsTableCorrelationNames();
			property = new PropertyDTO();
			property.setProperty("Database supports table correlation names");
			property.setValue(flag53 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception39) {
		}
		try {
			boolean flag54 = dbmd.supportsUnion();
			property = new PropertyDTO();
			property.setProperty("Database supports SQL UNION");
			property.setValue(flag54 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception40) {
		}
		try {
			boolean flag55 = dbmd.supportsUnionAll();
			property = new PropertyDTO();
			property.setProperty("Database supports SQL UNION ALL");
			property.setValue(flag55 ? "YES" : "NO");
			items.add(property);

		} catch (UnsupportedOperationException unsupportedoperationexception41) {
		}

		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		RdbmsConnection.closeConn();
		return dbInfo;

	}

	public GridInfoDTO getLimitationInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<PropertyDTO> items = new ArrayList<PropertyDTO>();
		PropertyDTO property = null;

		property = new PropertyDTO();
		property.setProperty("");
		property.setValue("Value of \"0\" means Undefined or Unlimited");
		items.add(property);

		int i = dbmd.getMaxBinaryLiteralLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Binary Literal Length");
		property.setValue(String.valueOf(i));
		items.add(property);

		int j = dbmd.getMaxCatalogNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Catalog Name Length");
		property.setValue(String.valueOf(j));
		items.add(property);

		int i1 = dbmd.getMaxCharLiteralLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Character Literal Length");
		property.setValue(String.valueOf(i1));
		items.add(property);

		int k1 = dbmd.getMaxColumnNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Column Name Length");
		property.setValue(String.valueOf(k1));
		items.add(property);

		int i2 = dbmd.getMaxColumnsInGroupBy();
		property = new PropertyDTO();
		property.setProperty("Maximum Column in GROUP BY clause");
		property.setValue(String.valueOf(i2));
		items.add(property);

		int k2 = dbmd.getMaxColumnsInIndex();
		property = new PropertyDTO();
		property.setProperty("Maximum Column in Index");
		property.setValue(String.valueOf(k2));
		items.add(property);

		int i3 = dbmd.getMaxColumnsInOrderBy();
		property = new PropertyDTO();
		property.setProperty("Maximum Column in ORDER BY clause");
		property.setValue(String.valueOf(i3));
		items.add(property);

		int k3 = dbmd.getMaxColumnsInSelect();
		property = new PropertyDTO();
		property.setProperty("Maximum Column in SELECT clause");
		property.setValue(String.valueOf(k3));
		items.add(property);

		int i4 = dbmd.getMaxColumnsInTable();
		property = new PropertyDTO();
		property.setProperty("Maximum Column in a TABLE");
		property.setValue(String.valueOf(i4));
		items.add(property);

		int j4 = dbmd.getMaxConnections();
		property = new PropertyDTO();
		property.setProperty("Maximum Connections");
		property.setValue(String.valueOf(j4));
		items.add(property);

		int k4 = dbmd.getMaxCursorNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Cursor Name Length");
		property.setValue(String.valueOf(k4));
		items.add(property);

		int l4 = dbmd.getMaxIndexLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Index Length");
		property.setValue(String.valueOf(l4));
		items.add(property);

		int i5 = dbmd.getMaxProcedureNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum  Procedure Name Length");
		property.setValue(String.valueOf(i5));
		items.add(property);

		int k6 = dbmd.getMaxRowSize();
		property = new PropertyDTO();
		property.setProperty("Maximum Row Size");
		property.setValue(String.valueOf(k6));
		items.add(property);

		int l6 = dbmd.getMaxSchemaNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Binary Literal Length");
		property.setValue(String.valueOf(l6));
		items.add(property);

		int i7 = dbmd.getMaxStatementLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Schema Name Length");
		property.setValue(String.valueOf(i7));
		items.add(property);

		int j7 = dbmd.getMaxStatements();
		property = new PropertyDTO();
		property.setProperty("Maximum Statements Count");
		property.setValue(String.valueOf(j7));
		items.add(property);

		int k7 = dbmd.getMaxTableNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum Table Name Lengt");
		property.setValue(String.valueOf(k7));
		items.add(property);

		int l7 = dbmd.getMaxTablesInSelect();
		property = new PropertyDTO();
		property.setProperty("Maximum Tables in SELECT clause");
		property.setValue(String.valueOf(l7));
		items.add(property);

		int i8 = dbmd.getMaxUserNameLength();
		property = new PropertyDTO();
		property.setProperty("Maximum User Name Length");
		property.setValue(String.valueOf(i8));
		items.add(property);

		RdbmsConnection.closeConn();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);

		return dbInfo;
	}

	public GridInfoDTO getFunctionInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<FunctionPropertyDTO> items = new ArrayList<FunctionPropertyDTO>();
		FunctionPropertyDTO property = null;
		String as[] = (String[]) null;
		String as1[] = (String[]) null;
		String as2[] = (String[]) null;
		String as3[] = (String[]) null;
		String as4[] = (String[]) null;
		int j3 = 0;

		String s48 = dbmd.getStringFunctions();
		as = s48.split(",");
		String s54 = dbmd.getNumericFunctions();
		as1 = s54.split(",");
		String s61 = dbmd.getTimeDateFunctions();
		as2 = s61.split(",");
		String s70 = dbmd.getSystemFunctions();
		as3 = s70.split(",");
		String s77 = dbmd.getSQLKeywords();
		as4 = s77.split(",");

		int maxLength = Math
				.max(as.length,
						Math.max(
								as2.length,
								Math.max(as3.length,
										Math.max(as4.length, as1.length))));
		for (int ind = 0; ind < maxLength; ind++) {

			property = new FunctionPropertyDTO();

			if (as.length > ind) {
				if (as[ind].compareTo("") != 0) {
					property.setStringfx((new StringBuilder(String
							.valueOf(as[ind]))).append("( )").toString());
				} else {
					property.setStringfx("");
				}
			}

			if (as1.length > ind) {
				if (as1[ind].compareTo("") != 0) {
					property.setNumericfx((new StringBuilder(String
							.valueOf(as1[ind]))).append("( )").toString());
				} else {
					property.setNumericfx("");
				}
			}

			if (as2.length > ind) {
				if (as2[ind].compareTo("") != 0) {
					property.setDatefx((new StringBuilder(String
							.valueOf(as2[ind]))).append("( )").toString());
				} else {
					property.setDatefx("");
				}
			}

			if (as3.length > ind) {
				if (as3[ind].compareTo("") != 0) {
					property.setSystemfx((new StringBuilder(String
							.valueOf(as3[ind]))).append("( )").toString());
				} else {
					property.setSystemfx("");
				}
			}

			if (as4.length > ind) {
				if (as4[ind].compareTo("") != 0) {
					property.setSqlkeyword((as4[ind]));
				} else {
					property.setSqlkeyword("");
				}
			}
			items.add(property);
		}

		RdbmsConnection.closeConn();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);

		return dbInfo;
	}

	public GridInfoDTO getCatalogInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<PropertyDTO> items = new ArrayList<PropertyDTO>();
		PropertyDTO property = null;
		ResultSet resultset = dbmd.getCatalogs();
		int k = 0;
		do {
			if (!resultset.next())
				break;
			String s21 = resultset.getString(1);
			if (s21.compareTo("") != 0) {
				property = new PropertyDTO();
				property.setProperty((new StringBuilder()).append(++k)
						.toString());
				property.setValue(s21);
				items.add(property);
			}
		} while (true);
		resultset.close();
		RdbmsConnection.closeConn();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);

		return dbInfo;
	}

	public GridInfoDTO getStandardSQLInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<SqlTypeInfoPropertyDTO> items = new ArrayList<SqlTypeInfoPropertyDTO>();
		SqlTypeInfoPropertyDTO property = null;
		ResultSet resultset1;

		for (resultset1 = dbmd.getTypeInfo(); resultset1.next();) {
			String s15 = resultset1.getString(1);
			String s22 = SqlType.getTypeName(resultset1.getInt(2));
			String s27 = resultset1.getString(3);
			String s32 = resultset1.getString(4);
			String s36 = resultset1.getString(5);
			String s43 = resultset1.getString(6);
			short word2 = resultset1.getShort(7);
			String s55 = "";
			switch (word2) {
			case 0: // '\0'
				s55 = "No";
				break;

			case 1: // '\001'
				s55 = "Yes";
				break;

			case 2: // '\002'
				s55 = "Unknown";
				break;

			default:
				s55 = "UnSupported NULLable type";
				break;
			}
			String s62 = resultset1.getBoolean(8) ? "True" : "False";
			short word4 = resultset1.getShort(9);
			String s78 = "";
			switch (word4) {
			case 0: // '\0'
				s78 = "No";
				break;

			case 1: // '\001'
				s78 = "Limited (Only LIKE Supported)";
				break;

			case 2: // '\002'
				s78 = "Limited (Except LIKE Supported)";
				break;

			case 3: // '\003'
				s78 = "Yes";
				break;

			default:
				s78 = "UnSupported NULLable type";
				break;
			}
			String s82 = resultset1.getBoolean(10) ? "True" : "False";
			String s86 = resultset1.getBoolean(12) ? "True" : "False";

			property = new SqlTypeInfoPropertyDTO();
			property.setName(s15);
			property.setDatatype(s22);
			property.setPrecision(s27);
			property.setPrefix(s32);
			property.setSuffix(s36);
			property.setParam(s43);
			property.setNullable(s55);
			property.setCasesensitive(s62);
			property.setSearchable(s78);
			property.setUnsigned(s82);
			property.setAutoincremental(s86);
			items.add(property);

		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		resultset1.close();
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO getUserSQLInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<UserTypeSqlInfoDTO> items = new ArrayList<UserTypeSqlInfoDTO>();
		UserTypeSqlInfoDTO property = null;
		ResultSet resultset2;

		for (resultset2 = dbmd.getUDTs(null, null, null, null); resultset2
				.next();) {
			String s16 = resultset2.getString(1);
			String s23 = resultset2.getString(2);
			String s28 = resultset2.getString(3);
			String s33 = resultset2.getString(4);
			int l2 = resultset2.getInt(5);
			String s44 = "";
			switch (l2) {
			case 2000:
				s44 = "Java Object";
				break;

			case 2002:
				s44 = "Structure";
				break;

			case 2001:
				s44 = "Distinct";
				break;

			default:
				s44 = "UnSupported java type";
				break;
			}
			String s49 = resultset2.getString(6);
			String s56 = SqlType.getTypeName(resultset2.getShort(7));
			property = new UserTypeSqlInfoDTO();
			property.setName(s28);
			property.setKlass(s33);
			property.setDatatype(s44);
			property.setBasetype(s56);
			property.setRemark(s49);
			property.setCategory(s16);
			property.setSchema(s23);
			items.add(property);
		}

		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		resultset2.close();
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO getSchemaInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<PropertyDTO> items = new ArrayList<PropertyDTO>();
		PropertyDTO property = null;
		ResultSet resultset3 = dbmd.getSchemas();
		int l = 0;
		do {
			if (!resultset3.next())
				break;
			String s24 = resultset3.getString(1);
			if (s24.compareTo("") != 0) {
				property = new PropertyDTO();
				property.setProperty((new StringBuilder()).append(++l)
						.toString());
				property.setValue(s24);
				items.add(property);
			}
		} while (true);
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		resultset3.close();
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO getParameterInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<ParameterPropertyDTO> items = new ArrayList<ParameterPropertyDTO>();
		ParameterPropertyDTO property = null;
		ResultSet resultset5 = dbmd.getProcedureColumns(null, null, null, null);

		String as7[];
		for (; resultset5.next();) {
			String s18 = resultset5.getString(1);
			String s26 = resultset5.getString(2);
			String s30 = resultset5.getString(3);
			String s35 = resultset5.getString(4);
			String s38 = "";
			short word1 = resultset5.getShort(5);
			switch (word1) {
			case 0: // '\0'
				s38 = "Unknown Parameter";
				break;

			case 1: // '\001'
				s38 = "IN Parameter";
				break;

			case 2: // '\002'
				s38 = "IN OUT Parameter";
				break;

			case 3: // '\003'
				s38 = "Result Column in ResultSet";
				break;

			case 4: // '\004'
				s38 = "OUT Parameter";
				break;

			case 5: // '\005'
				s38 = "Procedure return Value";
				break;

			default:
				s38 = "UnSupported Type";
				break;
			}
			String s50 = "";
			short word3 = resultset5.getShort(12);
			switch (word3) {
			case 0: // '\0'
				s50 = "No";
				break;

			case 1: // '\001'
				s50 = "Yes";
				break;

			case 2: // '\002'
				s50 = "Unknown";
				break;

			default:
				s50 = "UnSupported NULLable type";
				break;
			}
			property = new ParameterPropertyDTO();
			property.setParamter(s35);
			property.setType(s38);
			property.setNullable(s50);
			property.setProcedure(s30);
			property.setSchema(s26 == null ? s18 : s26);
			property.setCategory(s18);

			items.add(property);

		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		if (resultset5 != null)
			resultset5.close();
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO getProcedureInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		ResultSet resultset4 = dbmd.getProcedures(null, null, null);
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<ProcedurePropertyDTO> items = new ArrayList<ProcedurePropertyDTO>();
		ProcedurePropertyDTO property = null;
		// if(resultset4 != null)
		// rtm__ = new ReportTableModel(new String[] {
		// "Procedure", "Remark", "Type", "Schema", "Category"
		// });
		String as5[];
		for (; resultset4.next();) {
			String s17 = resultset4.getString(1);
			String s25 = resultset4.getString(2);
			String s29 = resultset4.getString(3);
			String s34 = resultset4.getString(7);
			String s37 = "";
			short word0 = resultset4.getShort(8);
			switch (word0) {
			case 0: // '\0'
				s37 = "May/May Not return result";
				break;

			case 1: // '\001'
				s37 = "Does Not return result";
				break;

			case 2: // '\002'
				s37 = "Returns result";
				break;

			default:
				s37 = "Type not supported";
				break;
			}
			property = new ProcedurePropertyDTO();
			property.setProcedure(s29);
			property.setRemark(s34);
			property.setType(s37);
			property.setSchema(s25 == null ? s17 : s25);
			property.setCategory(s17);

			items.add(property);

		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		resultset4.close();
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public void getTableModelInfo() throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		Hashtable hashtable = new Hashtable();
		Hashtable hashtable1 = new Hashtable();
		Hashtable hashtable2 = new Hashtable();
		String s13 = RdbmsConnection.getHValue("Database_Catalog");
		s13 = "";
		String s19 = RdbmsConnection.getHValue("Database_SchemaPattern");
		s13 = s13.compareTo("") == 0 ? null : s13;
		s19 = s19.compareTo("") == 0 ? null : s19;
		Vector vector = RdbmsConnection.getTable();
		int l1 = vector.size();
		for (int j2 = 0; j2 < l1; j2++) {
			String s39 = (String) vector.elementAt(j2);
			try {
				TableRelationInfo TableRelationInfo = new TableRelationInfo(s39);
				if (RdbmsConnection.getDBType()
						.compareToIgnoreCase("ms_access") == 0) {
					ResultSet resultset9 = dbmd.getIndexInfo(s13, s19, s39,
							false, true);
					do {
						if (!resultset9.next())
							break;
						String s57 = resultset9.getString(9);
						String s63 = resultset9.getString(6);
						String s71 = resultset9.getString(3);
						if (s57 != null && s63 != null)
							if (s63.compareToIgnoreCase("primarykey") == 0) {
								TableRelationInfo.pk[TableRelationInfo.pk_c] = s57;
								TableRelationInfo.pk_index[TableRelationInfo.pk_c] = s63;
								TableRelationInfo.hasPKey = true;
								TableRelationInfo.pk_c++;
								TableRelationInfo.isRelated = true;
							} else if (s63.endsWith(s39)) {
								TableRelationInfo.fk[TableRelationInfo.fk_c] = s57;
								TableRelationInfo.fk_pKey[TableRelationInfo.fk_c] = null;
								TableRelationInfo.fk_pTable[TableRelationInfo.fk_c] = s63
										.substring(0, s63.lastIndexOf(s39));
								TableRelationInfo.hasFKey = true;
								TableRelationInfo.fk_c++;
								TableRelationInfo.isRelated = true;
							}
					} while (true);
					resultset9.close();
				} else {
					int l3 = 0;
					ResultSet resultset10 = dbmd.getPrimaryKeys(s13, s19, s39);
					do {
						if (!resultset10.next())
							break;
						String s64 = resultset10.getString(4);
						String s72 = resultset10.getString(6);
						if (s64 != null && s72 != null) {
							TableRelationInfo.pk[l3] = s64;
							TableRelationInfo.pk_index[l3] = s72;
							TableRelationInfo.hasPKey = true;
							l3++;
							TableRelationInfo.pk_c++;
						}
					} while (true);
					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getImportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s65 = resultset10.getString(3);
						String s73 = resultset10.getString(4);
						String s79 = resultset10.getString(7);
						String s83 = resultset10.getString(8);
						TableRelationInfo.fk[l3] = s83;
						TableRelationInfo.fk_pKey[l3] = s73;
						TableRelationInfo.fk_pTable[l3] = s65;
						TableRelationInfo.hasFKey = true;
						TableRelationInfo.fk_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}

					resultset10.close();
					l3 = 0;
					for (resultset10 = dbmd.getExportedKeys(s13, s19, s39); resultset10
							.next();) {
						String s66 = resultset10.getString(3);
						String s74 = resultset10.getString(4);
						String s80 = resultset10.getString(7);
						String s84 = resultset10.getString(8);
						TableRelationInfo.pk_ex[l3] = s74;
						TableRelationInfo.pk_exKey[l3] = s84;
						TableRelationInfo.pk_exTable[l3] = s80;
						TableRelationInfo.hasExpKey = true;
						TableRelationInfo.exp_c++;
						TableRelationInfo.isRelated = true;
						l3++;
					}

					resultset10.close();
				}
				if (TableRelationInfo.isRelated)
					hashtable2.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else if (TableRelationInfo.hasPKey)
					hashtable.put(TableRelationInfo.tableName,
							TableRelationInfo);
				else
					hashtable1.put(TableRelationInfo.tableName,
							TableRelationInfo);
			} catch (Exception exception) {
				System.out.println((new StringBuilder(
						"\n WARNING: Unknown Exception Happened for Table:"))
						.append(s39).toString());
				System.out.println((new StringBuilder("\n Message: ")).append(
						exception.getMessage()).toString());
				exception.printStackTrace();
			}
		}

		RdbmsConnection.closeConn();

	}

	public GridInfoDTO getTableMetaData(String tb_pattern) throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		String s14 = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s20 = RdbmsConnection.getHValue("Database_Catalog");
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TableMetaDataPropertyDTO> items = new ArrayList<TableMetaDataPropertyDTO>();
		TableMetaDataPropertyDTO property = null;
		s20 = "";
		int j1 = 0;
		String s31 = tb_pattern;
		// rtm__ = new ReportTableModel(new String[] {
		// "Table", "Column", "Type", "Size", "Precision", "Radix", "Remark",
		// "Default", "Bytes", "Ordinal Pos",
		// "Nullable"
		// });
		if (s31 == null || s31.compareTo("") == 0)
			return dbInfo;
		ResultSet resultset6;
		String as11[];
		for (resultset6 = dbmd.getColumns(s20.equals("") ? (s20 = null) : s20,
				s14.equals("") ? (s14 = null) : s14, s31, null); resultset6
				.next();) {
			j1++;
			String s40 = resultset6.getString(3);
			String s45 = resultset6.getString(4);
			String s51 = resultset6.getString(6);
			String s58 = resultset6.getString(7);
			String s67 = resultset6.getString(9);
			String s75 = resultset6.getString(10);
			String s81 = resultset6.getString(12);
			String s85 = resultset6.getString(13);
			String s87 = resultset6.getString(16);
			String s88 = resultset6.getString(17);
			String s89 = resultset6.getString(18);

			property = new TableMetaDataPropertyDTO();
			property.setTable(s40);
			property.setColumn(s45);
			property.setType(s51);
			property.setSize(s58);
			property.setPrecision(s67);
			property.setRadix(s75);
			property.setRemark(s81);
			property.setDefaults(s85);
			property.setBytes(s87);
			property.setOriginalpos(s87);
			property.setNullable(s89);

			items.add(property);

		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		resultset6.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permission to run query on this table");
			return dbInfo;
		} else {
			RdbmsConnection.closeConn();
			return dbInfo;
		}
	}

	public GridInfoDTO getTablePrivilege(String tb_pattern) throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		String s14 = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s20 = RdbmsConnection.getHValue("Database_Catalog");
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TablePrivilegePropertyDTO> items = new ArrayList<TablePrivilegePropertyDTO>();
		TablePrivilegePropertyDTO property = null;
		s20 = "";
		int j1 = 0;
		String s31 = tb_pattern;
		ResultSet resultset7 = dbmd.getTablePrivileges(
				s20.compareTo("") == 0 ? (s20 = null) : s20,
				s14.compareTo("") == 0 ? (s14 = null) : s14,
				s31.compareTo("") == 0 ? (s31 = null) : s31);
		// rtm__ = new ReportTableModel(new String[] {
		// "Table", "Grantor", "Grantee", "Privileges", "Grantable"
		// });
		if (s31 == null || s31.compareTo("") == 0)
			return dbInfo;
		String as8[];
		for (; resultset7.next();) {
			j1++;
			String s41 = resultset7.getString(3);
			String s46 = resultset7.getString(4);
			String s52 = resultset7.getString(5);
			String s59 = resultset7.getString(6);
			String s68 = resultset7.getString(7);

			property = new TablePrivilegePropertyDTO();
			property.setTable(s41);
			property.setGrantor(s46);
			property.setGrantee(s52);
			property.setPrivileges(s59);
			property.setGrantable(s68);

			items.add(property);

		}

		resultset7.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permisson to run query on this table");
			return dbInfo;
		} else {
			dbInfo.setItems(items);
			dbInfo.setTotalCount(10);
			RdbmsConnection.closeConn();
			return dbInfo;
		}
	}

	public GridInfoDTO getColumnPrivilege(String tb_pattern)
			throws SQLException {
		RdbmsConnection.openConn();
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		String s14 = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s20 = RdbmsConnection.getHValue("Database_Catalog");
		s20 = "";
		int j1 = 0;
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TablePrivilegePropertyDTO> items = new ArrayList<TablePrivilegePropertyDTO>();
		TablePrivilegePropertyDTO property = null;
		// rtm__ = new ReportTableModel(new String[] {
		// "Table", "Column", "Grantor", "Grantee", "Privileges", "Grantable"
		// });
		String s31 = tb_pattern;
		if (s31 == null || s31.compareTo("") == 0)
			return dbInfo;
		ResultSet resultset8;
		String as9[];
		for (resultset8 = dbmd.getColumnPrivileges(
				s20.compareTo("") == 0 ? (s20 = null) : s20,
				s14.compareTo("") == 0 ? (s14 = null) : s14, s31, null); resultset8
				.next();) {
			j1++;
			String s42 = resultset8.getString(3);
			String s47 = resultset8.getString(4);
			String s53 = resultset8.getString(5);
			String s60 = resultset8.getString(6);
			String s69 = resultset8.getString(7);
			String s76 = resultset8.getString(8);

			property = new TablePrivilegePropertyDTO();
			property.setTable(s42);
			property.setGrantor(s47);
			property.setGrantee(s53);
			property.setPrivileges(s69);
			property.setGrantable(s76);

			items.add(property);

		}

		resultset8.close();
		if (j1 == 0) {
			System.out
					.println("Table does not Exist \n Or You might not have permission to run query on this Table's column");
			return dbInfo;
		} else {
			dbInfo.setItems(items);
			dbInfo.setTotalCount(10);
			RdbmsConnection.closeConn();
			return dbInfo;
		}
	}

	public GridInfoDTO IndexQuery(String table) throws SQLException {
		RdbmsConnection.openConn();
		// 0,1
		String s = RdbmsConnection.getHValue("Database_Catalog");
		s = "";
		String s1 = RdbmsConnection.getHValue("Database_SchemaPattern");
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<IndexPropertyDTO> items = new ArrayList<IndexPropertyDTO>();
		IndexPropertyDTO property = null;
		s = s.compareTo("") != 0 ? s : null;
		s1 = s1.compareTo("") != 0 ? s1 : null;
		Vector vector = null;
		if (table == null) {
			vector = RdbmsConnection.getTable();
		} else {
			vector = new Vector();
			vector.add(table);
		}

		try {
			for (int k = 0; k < vector.size(); k++) {
				String s2 = (String) vector.elementAt(k);
				ResultSet resultset = dbmd.getIndexInfo(s, s1, s2, false, true);

				while (resultset.next()) {
					boolean flag = resultset.getBoolean(4);
					String s3 = !flag ? "False" : "True";
					String s4 = resultset.getString(5);
					String s5 = resultset.getString(6);
					String s6 = "";
					short word0 = resultset.getShort(7);
					switch (word0) {
					case 0:
						s6 = "Statistic";
						break;
					case 1:
						s6 = "Clustered";
						break;
					case 2:
						s6 = "Hashed";
						break;
					default:
						s6 = "Type UnKnown";
					}

					String s7 = resultset.getString(9);
					String s8 = resultset.getString(10);
					String s9 = resultset.getString(11);
					String s10 = resultset.getString(12);
					String s11 = resultset.getString(13);
					if ((s7 != null) && (s5 != null)) {
						property = new IndexPropertyDTO();
						property.setTable(s2);
						property.setColumn(s7);
						property.setIndex(s5);
						property.setType(s6);
						property.setQualifier(s4);

						property.setIsUnique(s3);
						property.setAscdsc(s8);
						property.setCardinality(s9);
						property.setPages(s10);
						property.setFilter(s11);
						items.add(property);

					}
				}
				resultset.close();
			}
		} catch (SQLException ee) {
			System.out.println("Exception:" + ee.getMessage());
			return dbInfo;
		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO DbMetaDataQuery() throws SQLException {
		RdbmsConnection.openConn();
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TableMetaDataPropertyDTO> items = new ArrayList<TableMetaDataPropertyDTO>();
		TableMetaDataPropertyDTO property = null;
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector vector = RdbmsConnection.getTable();
		ResultSet resultset = null;

		for (int k = 0; k < vector.size(); k++) {
			String s2 = (String) vector.elementAt(k);
			resultset = dbmd.getColumns(s1, s, s2, null);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					String s4 = resultset.getString(4);
					String s5 = resultset.getString(6);
					String s6 = resultset.getString(7);
					String s7 = resultset.getString(9);
					String s8 = resultset.getString(10);
					String s9 = resultset.getString(12);
					String s10 = resultset.getString(13);
					String s11 = resultset.getString(16);
					String s12 = resultset.getString(17);
					String s13 = resultset.getString(18);
					property = new TableMetaDataPropertyDTO();
					property.setTable(s2);
					property.setColumn(s4);
					property.setType(s5);
					property.setSize(s6);
					property.setPrecision(s7);
					property.setRadix(s8);
					property.setRemark(s9);
					property.setDefaults(s10);
					property.setBytes(s11);
					property.setOriginalpos(s12);
					property.setNullable(s13);

					items.add(property);

				}
			}
		}

		resultset.close();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		RdbmsConnection.closeConn();
		return dbInfo;
	}

	public GridInfoDTO AllTablesPrivilegeQuery() throws SQLException {
		RdbmsConnection.openConn();
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TablePrivilegePropertyDTO> items = new ArrayList<TablePrivilegePropertyDTO>();
		TablePrivilegePropertyDTO property = null;
		s1 = "";
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		int k = 0;
		Vector vector = RdbmsConnection.getTable();
		ResultSet resultset = null;
		Set<String> setData = new HashSet<String>();
		for (int l = 0; l < vector.size(); l++) {
			String s2 = (String) vector.elementAt(l);
			resultset = dbmd.getTablePrivileges(s1, s, s2);
			while (resultset.next()) {
				String s3 = resultset.getString(3);
				if (s3.equals(s2)) {
					k++;
					String s4 = resultset.getString(4);
					String s5 = resultset.getString(5);
					String s6 = resultset.getString(6);
					String s7 = resultset.getString(7);
					if(setData.add(s3 + "_" + s6)) {
						property = new TablePrivilegePropertyDTO();
						property.setTable(s3);
						property.setGrantor(s4);
						property.setGrantee(s5);
						property.setPrivileges(s6);
						property.setGrantable(s7);
					
						items.add(property);
					}

				}
			}
		}

		resultset.close();
		RdbmsConnection.closeConn();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		if (k == 0)
			System.out
					.println("Tables do not Exist \n Or You might not have permisson to run this query ");
		return dbInfo;
	}

	public GridInfoDTO DataQuery(String table) throws SQLException {
		RdbmsConnection.openConn();
		String s = RdbmsConnection.getHValue("Database_SchemaPattern");
		String s1 = RdbmsConnection.getHValue("Database_Catalog");
		DatabaseMetaData dbmd = RdbmsConnection.getMetaData();
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<DataInfoPropertyDTO> items = new ArrayList<DataInfoPropertyDTO>();
		DataInfoPropertyDTO property = null;
		s1 = "";
		String s2 = RdbmsConnection.getHValue("Database_DSN");
		s1 = s1.compareTo("") != 0 ? s1 : null;
		s = s.compareTo("") != 0 ? s : null;
		Vector vector = null;
		if (table == null) {
			vector = RdbmsConnection.getTable();
		} else {
			vector = new Vector();
			vector.add(table);
		}
		String s3 = RdbmsConnection.getDBType();

		synchronized (RdbmsConnection.class) {
			for (int k = 0; k < vector.size(); k++) {
				try {

					String s17 = (String) vector.elementAt(k);
					Vector vector1 = new Vector();
					ResultSet resultset = dbmd.getColumns(s1, s, s17, null);

					while (resultset.next()) {
						String s18 = resultset.getString(3);
						if (s18.equals(s17)) {
							String s19 = resultset.getString(4);
							vector1.add(s19);
						}
					}
					resultset.close();

					String[] as;
					for (Enumeration enumeration = vector1.elements(); enumeration
							.hasMoreElements();) {
						String s10 = "0";
						String s11 = "0";
						String s12 = "0";
						String s13 = "0";
						String s14 = "0";
						String s15 = "0";
						String s20 = (String) enumeration.nextElement();
						QueryBuilder querybuilder = new QueryBuilder(s2, s17,
								s20, s3);
						String s4 = querybuilder.count_query_w(false,
								"row_count");
						String s5 = querybuilder.count_query_w(true,
								"row_count");
						String s6 = querybuilder.get_nullCount_query_w("Null");
						String s7 = querybuilder.get_zeroCount_query_w("0");
						String s8 = querybuilder.get_zeroCount_query_w("''");
						String s9 = querybuilder.get_pattern_query();

						try {
							for (resultset = RdbmsConnection.runQuery(s4); resultset
									.next();) {
								s10 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception) {
							s10 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s5); resultset
									.next();) {
								s11 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception1) {
							s11 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s6); resultset
									.next();) {
								s12 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception2) {
							s12 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s7); resultset
									.next();) {
								s13 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception3) {
							s13 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s8); resultset
									.next();) {
								s14 = resultset.getString("equal_count");
							}
							resultset.close();
						} catch (SQLException sqlexception4) {
							s14 = "N/A";
						}
						try {
							for (resultset = RdbmsConnection.runQuery(s9); resultset
									.next();) {
								s15 = resultset.getString("row_count");
							}
							resultset.close();
						} catch (SQLException sqlexception5) {
							s15 = "N/A";
						}
						property = new DataInfoPropertyDTO();
						property.setTable(s17);
						property.setColumn(s20);
						property.setRecord(s10);
						property.setUnique(s11);
						property.setPattern(s15);
						property.setNall(s12);
						property.setZero(s13);
						property.setEmpty(s14);
						items.add(property);

					}
				} catch (SQLException ee) {
					return dbInfo;
				}

			}
		}
		RdbmsConnection.closeConn();
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		return dbInfo;
	}

	public Hashtable tableModelInfo(String type) {
		try {
			DBMetaInfo dbMetaInfo = new DBMetaInfo();
			dbMetaInfo.getTableModelInfo();
			if (type.equals("noFK")) {
				Hashtable hashtable = dbMetaInfo.getOnlyPKTable();
				// String nofkString = generateNOFKTables(hashtable);
				return hashtable;
			} else if (type.equals("noPK")) {
				Hashtable hashtable1 = dbMetaInfo.getNoPKTable();
				// String nopkString = generateNOPKTables(hashtable1);
				return hashtable1;
			}
			else{
				Hashtable hashtable2 = dbMetaInfo.getRelatedTable();
				return hashtable2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String generateNOFKTables(
			Hashtable<String, TableRelationInfo> hashtable) {
		List<String> pkList = null;
		StringBuffer data = new StringBuffer();
		int main = 0;
		data.append("{");
		for (String table : hashtable.keySet()) {
			data.append(table);
			data.append(":");
			data.append("[");

			TableRelationInfo trInfo = (TableRelationInfo) hashtable.get(table);
			pkList = new ArrayList<String>();
			for (String pk : trInfo.pk) {
				if (pk != null) {
					pkList.add(pk);
				}
			}
			int ind = 0;
			for (String pk : pkList) {

				data.append(pk);
				if (ind != (pkList.size() - 1)) {
					data.append(",");
				}
				ind++;
			}
			data.append("]");
			if (main != (hashtable.size() - 1)) {
				data.append(",");
			}
			main++;
		}
		data.append("}");
		return data.toString();
	}

	private String generateNOPKTables(
			Hashtable<String, TableRelationInfo> hashtable) {

		StringBuffer data = new StringBuffer();
		int main = 0;
		data.append("[");
		for (String table : hashtable.keySet()) {
			data.append(table);

			if (main != (hashtable.size() - 1)) {
				data.append(",");
			}
			main++;
		}
		data.append("]");
		return data.toString();
	}

}
