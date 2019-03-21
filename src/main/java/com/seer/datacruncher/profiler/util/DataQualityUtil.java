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

package com.seer.datacruncher.profiler.util;

import java.util.Map;
import java.util.Vector;

import com.seer.datacruncher.profiler.framework.rdbms.SqlType;


public class DataQualityUtil {

	public static String generateTableColumnNames(Vector<String> tables, Vector<Integer> types) {

		StringBuffer data = new StringBuffer();
		int counter = 1;
		data.append("[");

		for (String table : tables) {
			data.append("[");
			data.append("'");
			data.append(table);
			data.append("'");
			data.append(",");
			data.append("'");
			if(types != null){
				data.append(SqlType.getTypeName(types.get(counter -1)));
			}
			else{
				data.append(table);
			}
			data.append("'");
			data.append("]");
			if (counter != tables.size()) {
				data.append(",");
			}
			counter = counter + 1;
		}
		data.append("]");
		return data.toString();
	}

	public static String generateColumnNames(Map<String, String> tableColumns) {

		StringBuffer data = new StringBuffer();
		int counter = 1;
		data.append("{");

		for (String table : tableColumns.keySet()) {
			data.append("'");
			data.append(table);
			data.append("'");
			data.append(":");
			data.append(tableColumns.get(table));
			if (counter != tableColumns.size()) {
				data.append(",");
			}
			counter = counter + 1;
		}
		data.append("}");
		return data.toString();
	}
}
