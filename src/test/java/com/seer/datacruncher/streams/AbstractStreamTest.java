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

package com.seer.datacruncher.streams;

import static org.junit.Assert.assertTrue;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.DatabaseEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration("classpath:test-config.xml")
public abstract class AbstractStreamTest extends AbstractJUnit4SpringContextTests implements DaoSet {	

	protected String stream_file_path;
	protected String xml_stream_file_name;
    protected String flat_file_delimited_file_name;
    protected String flat_file_fixedposition_file_name;
    protected String json_test_stream_file_name;
    protected String zip_test_stream_file_name;
    
	protected Properties properties;
	protected SchemaEntity schemaEntity;
	private String schemaName;
	
	abstract int getStreamType();
    @Autowired
    ApplicationContext ctx;

    @Before
	public void setUp() throws Exception {		
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.properties");
		schemaName = null;
		try {
			properties.load(in);
			schemaName = "unit_test_" + properties.getProperty("schemaname");
            stream_file_path = properties.getProperty("stream_file_path");
            flat_file_fixedposition_file_name = properties.getProperty("flat_file_fixedposition_file_name");
            xml_stream_file_name = properties.getProperty("xml_stream_file_name");
            flat_file_delimited_file_name = properties.getProperty("flat_file_delimited_file_name");
            json_test_stream_file_name = properties.getProperty("json_test_stream_file_name");
            zip_test_stream_file_name = properties.getProperty("zip_test_stream_file_name");
		} catch (Exception e) {
			assertTrue("Failed in loading test.properties file",false);			
		}	
		createSchema();
		createSchemaFields();	
	}
	
	private void createSchema() {
		schemaEntity = new SchemaEntity();
		//setting application to schema
		ApplicationEntity appEnt = new ApplicationEntity();
		appEnt.setName("test_streams_application");
		Create create = appDao.create(appEnt);
		if (create.getSuccess()) {
			schemaEntity.setIdApplication(appEnt.getIdApplication());
		} else {
			assertTrue("Application not set for test schema (streams unit test)", false);
		}
		ReadList databaseReadList = dbDao.read();
		if (databaseReadList.getResults() != null && databaseReadList.getResults().size() > 0) {
			schemaEntity.setIdDatabase(((DatabaseEntity) databaseReadList.getResults().get(0)).getIdDatabase());
		}
		schemaEntity.setIdSchemaType(1);
		schemaEntity.setName(schemaName);
		schemaEntity.setIdStreamType(getStreamType());
		schemaEntity.setLoadedXSD(false);
		schemaEntity.setDelimiter("");
		schemaEntity.setDescription("?");
		schemaEntity.setIsActive(1);
		if (getStreamType() == StreamType.flatFileDelimited) {
			schemaEntity.setDelimiter(properties.getProperty("schema_delimited_delimiter"));
		}
				
		create = schemasDao.create(schemaEntity);
		assertTrue(create.getMessage(), create.getSuccess());			
	}
	
	private void createSchemaFields() {
		SchemaFieldEntity schemaFieldEntity = null;
		boolean isXml = getStreamType() == StreamType.XML || getStreamType() == StreamType.XMLEXI;
		long schemaFieldID = 0;		
		if (isXml) {
			// Adding Root Node
			schemaFieldEntity = new SchemaFieldEntity();
			schemaFieldEntity.setIdParent(0);
			schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());
			schemaFieldEntity.setName(properties.getProperty("field_root"));
			schemaFieldEntity.setIdFieldType(FieldType.all);
			schemaFieldEntity.setIdCheckType(0);
			schemaFieldEntity.setElementOrder(0);
			schemaFieldID = schemaFieldsDao.create(schemaFieldEntity);
		}
		// Adding Leaf Node
		schemaFieldEntity = new SchemaFieldEntity();
		schemaFieldEntity.setIdParent(schemaFieldID);
		schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());
		schemaFieldEntity.setName(properties.getProperty("field1_name"));
		schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
		if (getStreamType() == StreamType.flatFileFixedPosition) {
			schemaFieldEntity.setSize(properties.getProperty("schema_fixedpos_size_fild1"));
		}
		schemaFieldEntity.setNillable(false);
		schemaFieldEntity.setIdCheckType(0);
		schemaFieldEntity.setIdAlign(1);
		schemaFieldEntity.setFillChar(" ");
		schemaFieldEntity.setElementOrder(0);
		schemaFieldsDao.create(schemaFieldEntity);
		//Adding Leaf Node
		schemaFieldEntity = new SchemaFieldEntity ();
		schemaFieldEntity.setIdParent(schemaFieldID);
		schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());		
		schemaFieldEntity.setName(properties.getProperty("field3_name"));
		if (getStreamType() == StreamType.flatFileFixedPosition) {
			schemaFieldEntity.setSize(properties.getProperty("schema_fixedpos_size_fild3"));
		}	
		schemaFieldEntity.setIdFieldType(FieldType.numeric);
		schemaFieldEntity.setNillable(false);
		schemaFieldEntity.setIdCheckType(0);
		schemaFieldEntity.setIdAlign(1);
		schemaFieldEntity.setFillChar(" ");					
		schemaFieldEntity.setElementOrder(0);
		schemaFieldsDao.create(schemaFieldEntity);	
	}

	@After
	public void tearDown() throws Exception {
		//attention: order of below code is very important. It's better not to change the order. 	
		long appId = schemaEntity.getIdApplication();
		Destroy destroy = schemasDao.destroy(schemaEntity.getIdSchema());	
		appDao.destroy(appId);			
		assertTrue(destroy.getMessage(), destroy.isSuccess());	
	}		
}
