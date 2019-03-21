/*
 * Copyright (c) 2019  Altimari Mario
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

package com.seer.datacruncher.profiler.dto.dbinfo;

public class SqlTypeInfoPropertyDTO {

	private String name;
	private String datatype;
	private String precision;
	private String prefix;
	private String suffix;

	private String param;
	private String nullable;
	private String casesensitive;
	private String searchable;
	private String autoincremental;

	private String unsigned;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getCasesensitive() {
		return casesensitive;
	}

	public void setCasesensitive(String casesensitive) {
		this.casesensitive = casesensitive;
	}

	public String getSearchable() {
		return searchable;
	}

	public void setSearchable(String searchable) {
		this.searchable = searchable;
	}

	public String getAutoincremental() {
		return autoincremental;
	}

	public void setAutoincremental(String autoincremental) {
		this.autoincremental = autoincremental;
	}

	public String getUnsigned() {
		return unsigned;
	}

	public void setUnsigned(String unsigned) {
		this.unsigned = unsigned;
	}

}
