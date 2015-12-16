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

package com.seer.datacruncher.profiler.dto.dbinfo;

public class FunctionPropertyDTO {

	private String stringfx;
	private String numericfx;
	private String datefx;
	private String systemfx;
	private String sqlkeyword;

	public String getStringfx() {
		return stringfx;
	}

	public void setStringfx(String stringfx) {
		this.stringfx = stringfx;
	}

	public String getNumericfx() {
		return numericfx;
	}

	public void setNumericfx(String numericfx) {
		this.numericfx = numericfx;
	}

	public String getDatefx() {
		return datefx;
	}

	public void setDatefx(String datefx) {
		this.datefx = datefx;
	}

	public String getSystemfx() {
		return systemfx;
	}

	public void setSystemfx(String systemfx) {
		this.systemfx = systemfx;
	}

	public String getSqlkeyword() {
		return sqlkeyword;
	}

	public void setSqlkeyword(String sqlkeyword) {
		this.sqlkeyword = sqlkeyword;
	}

}
