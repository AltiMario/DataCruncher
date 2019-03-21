/*
 *   DataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   DataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  altimario@gmail.com
 */

package com.seer.datacruncher.macros;

public abstract class AbstractJEXLField {
	
	protected String value;	
	
	public AbstractJEXLField(String value) {
		this.value = value;		
	}
	
	public abstract Object getValue();	
}
