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

package com.seer.datacruncher.profiler.bl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.dto.TopBottomDTO;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class InfoBL {

	public GridInfoDTO generateMinMaxValueGrid(String sd, String st, String sc) {

		GridInfoDTO dbInfo = new GridInfoDTO();
		List<TopBottomDTO> items = new ArrayList<TopBottomDTO>();
		List<Object> tops = new ArrayList<Object>();
		List<Object> bottoms = new ArrayList<Object>();
		QueryBuilder querybuilder = new QueryBuilder(sd, st, sc,
				RdbmsConnection.getDBType());
		String top_sel_query = querybuilder.top_query(true, "top_count", "20");
		String bottom_sel_query = querybuilder.bottom_query(true,
				"bottom_count", "20");

		try {
			RdbmsConnection.openConn();
			ResultSet rs = RdbmsConnection.runQuery(top_sel_query);
			int counter = 0;
			String top_val;
			for (; rs.next();) {
				tops.add(rs.getString("top_count"));

			}

			rs.close();
			rs = RdbmsConnection.runQuery(bottom_sel_query);
			counter = 0;
			String bot_val;
			for (; rs.next();) {
				bottoms.add(rs.getString("bottom_count"));
			}

			rs.close();
			RdbmsConnection.closeConn();

			Vector<Object> vec = null;
			TopBottomDTO tbDTO = null;
			for (int i = 0; i < tops.size(); i++) {
				tbDTO = new TopBottomDTO();
				tbDTO.setTop(tops.get(i).toString());
				tbDTO.setBottom(bottoms.get(i).toString());
				items.add(tbDTO);

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);
		return dbInfo;
	}

}
