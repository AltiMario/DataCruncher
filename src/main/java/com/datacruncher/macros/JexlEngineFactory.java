/*
 *   DataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   DataCruncher is released under AGPL license.

 *   Copyright (c) 2019 Altimari Mario
 *   All rights reserved
 *
 *   Site: http://altimario.github.io/DataCruncher
 *   Contact:  altimario@gmail.com
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
