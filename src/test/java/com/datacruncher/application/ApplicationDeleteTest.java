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

package com.datacruncher.application;

import static org.junit.Assert.assertTrue;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.dao.ApplicationsDao;
import com.datacruncher.jpa.entity.ApplicationEntity;

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
public class ApplicationDeleteTest extends AbstractJUnit4SpringContextTests{

	private Properties properties;
    @Autowired
    ApplicationContext ctx;

    private ApplicationsDao appDao;
	@Before
	public void setUp() throws Exception {

        appDao = (ApplicationsDao) ctx.getBean("ApplicationsDao");

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
	public void testDeleteApplication() {

		String applicationName = properties.getProperty("edit_applicationName");

		List<ApplicationEntity> listApplicationEntity = appDao.findByName(applicationName);

		if (listApplicationEntity == null || listApplicationEntity.size() == 0) {
			assertTrue("Application record not found", false);
			return;
		}

		ApplicationEntity applicationEntity = listApplicationEntity.get(0);

		

		Destroy destroy = appDao.destroy(applicationEntity.getIdApplication());

		assertTrue(destroy.getMessage(), destroy.isSuccess());
	}
}
