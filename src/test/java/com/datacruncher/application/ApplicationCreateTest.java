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

package com.datacruncher.application;

import static org.junit.Assert.assertTrue;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.dao.ApplicationsDao;
import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.UserApplicationsEntity;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class ApplicationCreateTest extends AbstractJUnit4SpringContextTests {

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
	public void testCreateApplication() throws java.text.ParseException {

		ApplicationEntity applicationEntity = new ApplicationEntity();

		applicationEntity.setName(properties.getProperty("applicationName"));
		applicationEntity.setIsActive(1);
		applicationEntity.setDescription(properties.getProperty("description"));
		String pattern = "yyyy-MM-dd";
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			startDate = format.parse(properties.getProperty("startDate"));
			endDate = format.parse(properties.getProperty("endDate"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		applicationEntity.setStartDate(startDate);
		applicationEntity.setEndDate(endDate);
		List<UserApplicationsEntity> userApplications = new ArrayList<UserApplicationsEntity>();
		UserApplicationsEntity userApplicationsEntity = new UserApplicationsEntity();
		userApplications.add(userApplicationsEntity);
		applicationEntity.setUserApplications(userApplications);


		Create create = appDao.create(applicationEntity);
		assertTrue(create.getMessage(), create.getSuccess());
	}
}
