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

package com.datacruncher.schema;

import static org.junit.Assert.assertTrue;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.ApplicationsDao;
import com.datacruncher.jpa.dao.DatabasesDao;
import com.datacruncher.jpa.dao.SchemasDao;
import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.DatabaseEntity;
import com.datacruncher.jpa.entity.SchemaEntity;

import java.io.InputStream;
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
 * @author KGandhi This junit test class creates new schema
 */
@ContextConfiguration("classpath:test-config.xml")
public class SchemaCreateTest extends AbstractJUnit4SpringContextTests {

	private Properties properties;

    @Autowired
    ApplicationContext ctx;

    SchemasDao schemasDao;
    ApplicationsDao appDao;
    DatabasesDao dbDao;


    @Before
	public void setUp() throws Exception {

        appDao = (ApplicationsDao)ctx.getBean("ApplicationsDao");
        schemasDao = (SchemasDao) ctx.getBean("SchemasDao");
        dbDao = (DatabasesDao)ctx.getBean("DatabasesDao");

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
	public void testCreateSchema() {

		SchemaEntity schemaEntity = new SchemaEntity();
		ReadList applicaitonReadList = appDao.read();

		if (applicaitonReadList.getResults() != null && applicaitonReadList.getResults().size() > 0) {
			schemaEntity.setIdApplication(((ApplicationEntity) applicaitonReadList.getResults().get(0))
					.getIdApplication());
		} else {
			assertTrue("Application not found", false);
		}

		ReadList databaseReadList = dbDao.read();

		if (databaseReadList.getResults() != null && databaseReadList.getResults().size() > 0) {
			schemaEntity.setIdDatabase(((DatabaseEntity) databaseReadList.getResults().get(0)).getIdDatabase());
		}

		String schemaName = properties.getProperty("schemaname");
		schemaEntity.setName(schemaName);

		schemaEntity.setIdSchemaType(1);
		schemaEntity.setLoadedXSD(false);
		schemaEntity.setDelimiter("");
		schemaEntity.setDescription("?");

		schemaEntity.setIsActive(1);

		Create create = schemasDao.create(schemaEntity);
		assertTrue(create.getMessage(), create.getSuccess());
	}
}
