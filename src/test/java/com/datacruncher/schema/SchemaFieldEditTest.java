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

package com.datacruncher.schema;

import static org.junit.Assert.assertTrue;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.dao.SchemaFieldsDao;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

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
 * @author KGandhi This junit test class updates fields of schema.
 */
@ContextConfiguration("classpath:test-config.xml")
public class SchemaFieldEditTest extends AbstractJUnit4SpringContextTests implements DaoSet {

	private Properties properties;
    @Autowired
    ApplicationContext ctx;

    SchemaFieldsDao schemaFieldsDao;
    @Before
	public void setUp() throws Exception {

        schemaFieldsDao = (SchemaFieldsDao) ctx.getBean("SchemaFieldsDao");
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.properties");

		try {
			properties.load(in);
		} catch (Exception e) {
			assertTrue("Failed in loading test.properties file", false);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEditSchemaField() {

		String schemaName = properties.getProperty("schemaname");

		List<SchemaEntity> listSchemaEntity = schemasDao.findByName(schemaName);

		if (listSchemaEntity == null || listSchemaEntity.size() == 0) {
			assertTrue("Schema record not found", false);
			return;
		}

		SchemaEntity schemaEntity = listSchemaEntity.get(0);

		// Editing Leaf Node
		SchemaFieldEntity schemaFieldEntity = schemaFieldsDao.root(schemaEntity.getIdSchema());

		List<SchemaFieldEntity> listLeafFields = schemaFieldsDao.listElemChild(schemaFieldEntity.getIdSchemaField());

		if (listLeafFields == null || listLeafFields.size() == 0) {
			assertTrue("Child Node not found", false);
			return;
		} else {
			for (int counter = 0; counter < listLeafFields.size() && counter < 4; counter++) {
				schemaFieldEntity = listLeafFields.get(counter);
				schemaFieldEntity.setName(properties.getProperty("edit_field" + (counter + 1) + "_name"));
				schemaFieldsDao.update(schemaFieldEntity);
			}
		}

	}
}
