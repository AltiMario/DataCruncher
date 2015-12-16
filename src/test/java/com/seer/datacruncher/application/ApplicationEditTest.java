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

package com.seer.datacruncher.application;

import static org.junit.Assert.assertTrue;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.UserApplicationsEntity;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration("classpath:test-config.xml")
public class ApplicationEditTest extends AbstractJUnit4SpringContextTests implements DaoSet {

	private Properties properties;

	@Before
	public void setUp() throws Exception {

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
	public void testEditApplication() throws java.text.ParseException {

		String applicationName = properties.getProperty("applicationName");


		List<ApplicationEntity> listApplicationEntity = appDao.findByName(applicationName);

		if (listApplicationEntity == null || listApplicationEntity.size() == 0) {
			assertTrue("Application record not found", false);
			return;
		}

		ApplicationEntity applicationEntity = listApplicationEntity.get(0);

		applicationEntity.setName(properties.getProperty("edit_applicationName"));
		applicationEntity.setIsActive(1);
		applicationEntity.setDescription(properties.getProperty("edit_description"));
		String pattern = "yyyy-MM-dd";
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			startDate = format.parse(properties.getProperty("edit_startDate"));
			endDate = format.parse(properties.getProperty("edit_endDate"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		applicationEntity.setStartDate(startDate);
		applicationEntity.setEndDate(endDate);
		List<UserApplicationsEntity> userApplications = new ArrayList<UserApplicationsEntity>();
		UserApplicationsEntity userApplicationsEntity = new UserApplicationsEntity();
		userApplications.add(userApplicationsEntity);
		applicationEntity.setUserApplications(userApplications);

		

		Update update = appDao.update(applicationEntity);
		assertTrue(update.getMessage(), update.isSuccess());
	}
}
