/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.macros;

import com.seer.datacruncher.constants.FieldType;

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
