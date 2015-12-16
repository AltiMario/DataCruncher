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

package com.seer.datacruncher.profiler.framework.rdbms;

public class TableRelationInfo {

	private static int DEFAULT_SIZE = 128;
	public String tableName;
	public int tableId;
	public String pk[];
	public String pk_index[];
	public String pk_ex[];
	public String pk_exTable[];
	public String pk_exKey[];
	public String fk[];
	public String fk_pKey[];
	public String fk_pTable[];
	public boolean isShown;
	public boolean isRelated;
	public boolean hasPKey;
	public boolean hasFKey;
	public boolean hasExpKey;
	public int fk_c;
	public int pk_c;
	public int exp_c;
	public int r_i;
	public int level;

	public TableRelationInfo() {
		pk = new String[DEFAULT_SIZE];
		pk_index = new String[DEFAULT_SIZE];
		pk_ex = new String[DEFAULT_SIZE];
		pk_exTable = new String[DEFAULT_SIZE];
		pk_exKey = new String[DEFAULT_SIZE];
		fk = new String[DEFAULT_SIZE];
		fk_pKey = new String[DEFAULT_SIZE];
		fk_pTable = new String[DEFAULT_SIZE];
		isShown = false;
		isRelated = false;
		hasPKey = false;
		hasFKey = false;
		hasExpKey = false;
		fk_c = 0;
		pk_c = 0;
		exp_c = 0;
		r_i = -1;
		level = 0;
	}

	public TableRelationInfo(String t_name) {
		pk = new String[DEFAULT_SIZE];
		pk_index = new String[DEFAULT_SIZE];
		pk_ex = new String[DEFAULT_SIZE];
		pk_exTable = new String[DEFAULT_SIZE];
		pk_exKey = new String[DEFAULT_SIZE];
		fk = new String[DEFAULT_SIZE];
		fk_pKey = new String[DEFAULT_SIZE];
		fk_pTable = new String[DEFAULT_SIZE];
		isShown = false;
		isRelated = false;
		hasPKey = false;
		hasFKey = false;
		hasExpKey = false;
		fk_c = 0;
		pk_c = 0;
		exp_c = 0;
		r_i = -1;
		level = 0;
		tableName = t_name;
	}

	public void print_table() {
		System.out.println((new StringBuilder("\n ___ ")).append(tableName)
				.append(" ____").toString());
		for (int i = 0; i < pk_c; i++)
			System.out.println((new StringBuilder("\n PK --")).append(pk[i])
					.toString());

		if (!isRelated)
			System.out.println("\n Table NOT Related");
		for (int i = 0; i < exp_c; i++)
			System.out.println((new StringBuilder("\n Exported PK -- \""))
					.append(pk_ex[i]).append("\" \"").append(pk_exTable[i])
					.append("\"  \" ").append(pk_exKey[i]).append("\"")
					.toString());

		for (int i = 0; i < fk_c; i++)
			System.out.println((new StringBuilder("\n FK --\"")).append(fk[i])
					.append("\" \"").append(fk_pKey[i]).append("\" \"")
					.append(fk_pTable[i]).append("\"").toString());

	}

}
