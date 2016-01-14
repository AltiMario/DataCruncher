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

public class IntegerField extends AbstractJEXLField {

	public IntegerField(String value) {
		super(value);
	}

	@Override
	public Integer getValue() {
		return Integer.valueOf(value);
	}
}
