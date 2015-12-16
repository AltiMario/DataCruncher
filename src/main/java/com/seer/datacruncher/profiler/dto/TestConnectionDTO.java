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

package com.seer.datacruncher.profiler.dto;

public class TestConnectionDTO {
	String dbType;
	String dsn;
	String user;
	String passwd;
	String driver;
	String protocol;
	String catalog;
	String schemaPattern;
	String tablePattern;
	String colPattern;
	String showType;
	String jdbcCs;
	boolean comparion;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDsn() {
		return dsn;
	}

	public void setDsn(String dsn) {
		this.dsn = dsn;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchemaPattern() {
		return schemaPattern;
	}

	public void setSchemaPattern(String schemaPattern) {
		this.schemaPattern = schemaPattern;
	}

	public String getTablePattern() {
		return tablePattern;
	}

	public void setTablePattern(String tablePattern) {
		this.tablePattern = tablePattern;
	}

	public String getColPattern() {
		return colPattern;
	}

	public void setColPattern(String colPattern) {
		this.colPattern = colPattern;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getJdbcCs() {
		return jdbcCs;
	}

	public void setJdbcCs(String jdbcCs) {
		this.jdbcCs = jdbcCs;
	}

	public boolean isComparion() {
		return comparion;
	}

	public void setComparion(boolean comparion) {
		this.comparion = comparion;
	}

}
