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

package com.seer.datacruncher.schema;

import static org.junit.Assert.assertTrue;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.dao.SchemaFieldsDao;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * 
 * @author KGandhi
 * This junit test class creates three fields for schema.
 */
@ContextConfiguration("classpath:test-config.xml")
public class SchemaFieldCreateTest extends AbstractJUnit4SpringContextTests implements DaoSet {

	private Properties properties;

    @Autowired
    ApplicationContext ctx;

    private SchemaFieldsDao schemaFieldsDao;

    @Before
	public void setUp() throws Exception {

        schemaFieldsDao = (SchemaFieldsDao) ctx.getBean("SchemaFieldsDao");
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.properties");
		
		try {
			properties.load(in);
		} catch (Exception e) {
			assertTrue("Failed in loading test.properties file",false);			
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateSchemaField() {
								
		String schemaName =   properties.getProperty("schemaname"); 
				

		List<SchemaEntity> listSchemaEntity = schemasDao.findByName(schemaName);
		
		
		if(listSchemaEntity == null || listSchemaEntity.size() == 0) {
			assertTrue("Schema record not found", false);
			return;
		}
		
		SchemaEntity schemaEntity = listSchemaEntity.get(0);
				
		//Adding Root Node
		SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity ();
		schemaFieldEntity.setIdParent(0);
		schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());
		
		schemaFieldEntity.setName(properties.getProperty("field_root"));
		schemaFieldEntity.setDescription("?");
		schemaFieldEntity.setIdFieldType(FieldType.all);
		
		schemaFieldEntity.setIdCheckType(0);		
				
		
		schemaFieldEntity.setElementOrder(0);
		
		long schemaFieldID = schemaFieldsDao.create(schemaFieldEntity);
		
		//Adding Leaf Node
		schemaFieldEntity = new SchemaFieldEntity ();
		schemaFieldEntity.setIdParent(schemaFieldID);
		schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());
		
		schemaFieldEntity.setName(properties.getProperty("field1_name"));
		schemaFieldEntity.setDescription("?");
		schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
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
		
		schemaFieldEntity.setName(properties.getProperty("field2_name"));
		schemaFieldEntity.setDescription("?");
		schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
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
		schemaFieldEntity.setDescription("?");
		schemaFieldEntity.setIdFieldType(FieldType.numeric);
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
		
		schemaFieldEntity.setName(properties.getProperty("field4_name"));
		schemaFieldEntity.setDescription("?");
		schemaFieldEntity.setIdFieldType(FieldType.date);
		schemaFieldEntity.setNillable(false);
		schemaFieldEntity.setIdCheckType(0);
		schemaFieldEntity.setIdDateFmtType(1);
		schemaFieldEntity.setIdDateType(1);
		schemaFieldEntity.setIdTimeType(1);
		schemaFieldEntity.setMaxLength(19);
		
		schemaFieldEntity.setElementOrder(0);
		
		schemaFieldsDao.create(schemaFieldEntity);
	}
}
