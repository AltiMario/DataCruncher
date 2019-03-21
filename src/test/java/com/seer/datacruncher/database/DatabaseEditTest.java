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

package com.seer.datacruncher.database;

import static org.junit.Assert.assertTrue;
import com.seer.datacruncher.constants.DatabaseType;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.dao.DatabasesDao;
import com.seer.datacruncher.jpa.entity.DatabaseEntity;

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
@ContextConfiguration("classpath:test-config.xml")
public class DatabaseEditTest  extends AbstractJUnit4SpringContextTests  {

	private Properties properties;

    @Autowired
    ApplicationContext ctx;

    private DatabasesDao dbDao;
	@Before
	public void setUp() throws Exception {

        dbDao = (DatabasesDao) ctx.getBean("DatabasesDao");
		
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
	public void testEditDatabase() {
				
		String name =  properties.getProperty("name");
		 		
		List<DatabaseEntity> listDatabaseEntity = dbDao.findByName(name);
		
		if(listDatabaseEntity == null || listDatabaseEntity.size() == 0) {
			assertTrue("Database record not found", false);
			return;
		}
			
		DatabaseEntity databaseEntity = listDatabaseEntity.get(0);
		 
		databaseEntity.setName(properties.getProperty("edit_name"));
		
		String databaseType = properties.getProperty("edit_databasetype");
		
		if(databaseType.equalsIgnoreCase("MySQL"))
			databaseEntity.setIdDatabaseType(DatabaseType.MySQL);
		else if(databaseType.equalsIgnoreCase("Oracle"))
				databaseEntity.setIdDatabaseType(DatabaseType.ORACLE);
		else if(databaseType.equalsIgnoreCase("SQLServer"))
			databaseEntity.setIdDatabaseType(DatabaseType.SQLServer);
		else if(databaseType.equalsIgnoreCase("PostgreSQL"))
			databaseEntity.setIdDatabaseType(DatabaseType.PostgreSQL);
		else if(databaseType.equalsIgnoreCase("DB2"))
			databaseEntity.setIdDatabaseType(DatabaseType.DB2);
		else if(databaseType.equalsIgnoreCase("SQLite"))
			databaseEntity.setIdDatabaseType(DatabaseType.SQLite);
		else if(databaseType.equalsIgnoreCase("Firebird"))
			databaseEntity.setIdDatabaseType(DatabaseType.Firebird);
		else if(databaseType.equalsIgnoreCase("SAPDB"))
			databaseEntity.setIdDatabaseType(DatabaseType.SAPDB);
		else if(databaseType.equalsIgnoreCase("HSQLDB"))
			databaseEntity.setIdDatabaseType(DatabaseType.HSQLDB);
		
		databaseEntity.setHost(properties.getProperty("edit_databasehost"));
		databaseEntity.setPort(properties.getProperty("edit_databaseport"));
		databaseEntity.setDatabaseName(properties.getProperty("edit_databasename"));
		databaseEntity.setUserName(properties.getProperty("edit_databaseusername"));
		databaseEntity.setPassword(properties.getProperty("edit_databasepassword"));
		databaseEntity.setDescription(properties.getProperty("edit_databasedesc"));
								
		
		
		Update update = dbDao.update(databaseEntity);		
		assertTrue(update.getMessage(),update.isSuccess());
	}
}
