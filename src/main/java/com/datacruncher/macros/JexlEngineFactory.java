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

package com.datacruncher.macros;

import org.apache.commons.jexl2.JexlEngine;

public class JexlEngineFactory {

	private static JexlEngine _instance = null;

	private JexlEngineFactory() {
	}

	public static synchronized JexlEngine getInstance() {
		if (_instance == null) {
			_instance = new JexlEngine();
			_instance.setCache(512);
			_instance.setLenient(false);
			_instance.setSilent(false);
		}
		return _instance;
	}
}
