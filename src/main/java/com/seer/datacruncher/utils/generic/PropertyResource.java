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

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
*/
package com.seer.datacruncher.utils.generic;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyResource {

    private static Logger log = Logger.getLogger(PropertyResource.class);
	/**
	 * Property object to handle
	 */
	protected static final Properties prop = new Properties();

	/**
	 * Default constructor, loads the property file
	 */
	protected PropertyResource() {
		load();
	}

	private void load() {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getResourceName());
			try {
				prop.load(is);
			} catch (Exception e) {
                log.error("error reading "+getResourceName(), e);
			}
	}

	/**
	 * Gets the resource name for the managed property
	 * @return String containing the resource name
	 */
	protected abstract String getResourceName();
	
}
