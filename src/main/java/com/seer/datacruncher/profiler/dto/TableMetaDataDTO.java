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

import java.util.Vector;

public class TableMetaDataDTO {

	private Vector<String> tables;
	private Vector<String> tableDesc;

	public Vector<String> getTables() {
		return tables;
	}

	public void setTables(Vector<String> tables) {
		this.tables = tables;
	}

	public Vector<String> getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(Vector<String> tableDesc) {
		this.tableDesc = tableDesc;
	}

}
