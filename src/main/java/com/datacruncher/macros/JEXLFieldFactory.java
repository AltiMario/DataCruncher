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

import com.datacruncher.constants.FieldType;

public class JEXLFieldFactory {
	
	private JEXLFieldFactory() {
	}
	
	public static AbstractJEXLField getField(String type, String value) {
		if (type.equals(FieldType.ALPHANUMERIC)) {
			return new StringField(value);
		} else if (type.equals(FieldType.NUMERIC)) {
			return new IntegerField(value);
		}else if (type.equals(FieldType.DATE)) {
			return new StringField(value);
		} 
		return null;
	}
}
