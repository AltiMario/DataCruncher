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

package com.seer.datacruncher.profiler.bl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.StringAnalyticsPropertyDTO;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class StringAnalyticsBL {

	private String _dsn;
	private String _type;
	private String _condition;
	private String _table;
	private String _col;
	private String qp_1;
	private String qp_2;
	private String qp_3;
	private String q_s;
	private String _distinct;
	private String qc_1;
	private String qc_2;
	private String qc_3;

	public StringAnalyticsBL(String dsn, String type, String condition,
			String table, String column) {
		this._dsn = dsn;
		this._type = type;
		this._condition = condition;
		this._table = table;
		this._col = column;
	}

	public Object reCreateBotPane_like(boolean noGrid) {
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<StringAnalyticsPropertyDTO> items = new ArrayList<StringAnalyticsPropertyDTO>();
		int i = 0;
		int col_c_1 = 0;
		int col_c_2 = 0;
		int col_c_3 = 0;

		String q1_cont = this.qp_1;
		String q2_cont = this.qp_2;
		String q3_cont = this.qp_3;

		List<String> q1 = new ArrayList<String>();
		List<String> q2 = new ArrayList<String>();
		List<String> q3 = new ArrayList<String>();

		if (((q1_cont == null) || (q1_cont.compareTo("") == 0))
				&& ((q2_cont == null) || (q2_cont.compareTo("") == 0))
				&& ((q3_cont == null) || (q3_cont.compareTo("") == 0))) {
			System.out.println("No Search Pattern");
		}

		if ((!q1_cont.startsWith("%")) && (!q1_cont.endsWith("%"))
				&& (q1_cont.compareTo("") != 0))
			q1_cont = q1_cont + "%";
		if ((!q2_cont.startsWith("%")) && (!q2_cont.endsWith("%"))
				&& (q2_cont.compareTo("") != 0))
			q2_cont = q2_cont + "%";
		if ((!q3_cont.startsWith("%")) && (!q3_cont.endsWith("%"))
				&& (q3_cont.compareTo("") != 0)) {
			q3_cont = q3_cont + "%";
		}
		QueryBuilder s_prof = new QueryBuilder(this._dsn, this._table,
				this._col, RdbmsConnection.getDBType());

		boolean like = Integer.parseInt(this.q_s) == 0;
		String like_query_3;
		String like_query_1;
		String like_query_2;

		if (new Boolean(this._distinct)) {
			like_query_1 = s_prof.get_freq_like_query(q1_cont, like);
			like_query_2 = s_prof.get_freq_like_query(q2_cont, like);
			like_query_3 = s_prof.get_freq_like_query(q3_cont, like);
		} else {
			like_query_1 = s_prof.get_like_query(q1_cont, like);
			like_query_2 = s_prof.get_like_query(q2_cont, like);
			like_query_3 = s_prof.get_like_query(q3_cont, like);
		}
		try {
			RdbmsConnection.openConn();
			ResultSet rs_1 = RdbmsConnection.runQuery(like_query_1);

			while (rs_1.next()) {

				String q_value_1 = rs_1.getString("like_wise");
				if (new Boolean(this._distinct)) {
					String dup_row_count = rs_1.getString("row_count");
					q1.add("(" + dup_row_count + ") " + q_value_1);
				} else {
					q1.add(q_value_1);
				}

				i++;
				col_c_1++;
			}
			rs_1.close();

			ResultSet rs_2 = RdbmsConnection.runQuery(like_query_2);
			while (rs_2.next()) {
				if (col_c_2 == i) {
					i++;
				}
				String q_value_2 = rs_2.getString("like_wise");
				if (new Boolean(this._distinct)) {
					String dup_row_count = rs_2.getString("row_count");
					q2.add("(" + dup_row_count + ") " + q_value_2);
				} else {
					q2.add(q_value_2);
				}
			}
			rs_2.close();

			ResultSet rs_3 = RdbmsConnection.runQuery(like_query_3);

			while (rs_3.next()) {
				if (col_c_3 == i) {
					i++;
				}
				String q_value_3 = rs_3.getString("like_wise");
				if (new Boolean(this._distinct)) {
					String dup_row_count = rs_3.getString("row_count");
					q3.add("(" + dup_row_count + ") " + q_value_3);
				} else {
					q3.add(q_value_3);
				}
			}
			rs_3.close();

			RdbmsConnection.closeConn();
		} catch (SQLException e) {
			System.out.println("\n Like Query execution failed");
			System.out.println(e.getMessage());
		}

		String display = "{success: true, q1: \"" + col_c_1 + "\" , q2:\""
				+ col_c_2 + "\" , q3:\"" + col_c_3 + "\"}";
		StringAnalyticsPropertyDTO dto = null;
		if (!noGrid) {
			int len = Math.max(Math.max(q1.size(), q2.size()), q3.size());
			for (int ind = 0; ind < len; ind++) {
				dto = new StringAnalyticsPropertyDTO();
				dto.setQ1(q1.size() > ind ? q1.get(ind) : "");
				dto.setQ2(q2.size() > ind ? q2.get(ind) : "");
				dto.setQ3(q3.size() > ind ? q3.get(ind) : "");
				items.add(dto);
			}
			dbInfo.setItems(items);
			dbInfo.setTotalCount(10);

			return dbInfo;
		} else {
			return display;
		}

	}

	public Object reCreateBotPane_regex(boolean noGrid) {
		GridInfoDTO dbInfo = new GridInfoDTO();
		List<StringAnalyticsPropertyDTO> items = new ArrayList<StringAnalyticsPropertyDTO>();
		int col_c_1 = 0;
		int col_c_2 = 0;
		int col_c_3 = 0;
		List<String> q1 = new ArrayList<String>();
		List<String> q2 = new ArrayList<String>();
		List<String> q3 = new ArrayList<String>();
		String regex_query = "";

		QueryBuilder s_prof = new QueryBuilder(this._dsn, this._table,
				this._col, RdbmsConnection.getDBType());
		if (new Boolean(this._distinct))
			regex_query = s_prof.get_freq_all_query();
		else
			regex_query = s_prof.get_all_worder_query();
		try {
			Pattern q_pattern_1 = null;
			Pattern q_pattern_2 = null;
			Pattern q_pattern_3 = null;
			Matcher q_match_1 = null;
			Matcher q_match_2 = null;
			Matcher q_match_3 = null;
			boolean q1_b = false;
			boolean q2_b = false;
			boolean q3_b = false;
			String query1_str = "";
			String query2_str = "";
			String query3_str = "";
			int count = 0;

			query1_str = this.qp_1;
			query2_str = this.qp_2;
			query3_str = this.qp_3;

			if ((query1_str != null) && (query1_str.compareTo("") != 0)) {
				if (new Boolean(this.qc_1))
					q_pattern_1 = Pattern.compile(query1_str, 2);
				else
					q_pattern_1 = Pattern.compile(query1_str);
			}
			if ((query2_str != null) && (query2_str.compareTo("") != 0)) {
				if (new Boolean(this.qc_2))
					q_pattern_2 = Pattern.compile(query2_str, 2);
				else
					q_pattern_2 = Pattern.compile(query2_str);
			}
			if ((query3_str != null) && (query3_str.compareTo("") != 0)) {
				if (new Boolean(this.qc_3))
					q_pattern_3 = Pattern.compile(query3_str, 2);
				else
					q_pattern_3 = Pattern.compile(query3_str);
			}
			if ((q_pattern_1 == null) && (q_pattern_2 == null)
					&& (q_pattern_3 == null)) {
				System.out.println("No Search Pattern");
			}

			RdbmsConnection.openConn();
			ResultSet rs = RdbmsConnection.runQuery(regex_query);

			while (rs.next()) {
				String q_value = rs.getString("like_wise");
				if (q_value != null) {
					count++;

					if (q_pattern_1 != null)
						q_match_1 = q_pattern_1.matcher(q_value);
					if (q_pattern_2 != null)
						q_match_2 = q_pattern_2.matcher(q_value);
					if (q_pattern_3 != null) {
						q_match_3 = q_pattern_3.matcher(q_value);
					}
					if (q_match_1 != null)
						q1_b = q_match_1.find();
					if (q_match_2 != null)
						q2_b = q_match_2.find();
					if (q_match_3 != null) {
						q3_b = q_match_3.find();
					}
					if ((q1_b) || (q2_b) || (q3_b)) {
						// this.qtable.addRow();
					}
					if (new Boolean(this._distinct)) {
						String dup_row_count = rs.getString("row_count");
						if (q1_b) {
							// this.qtable.setTableValueAt("(" + dup_row_count +
							// ") " +
							// q_value, col_c_1++, 0);
							q1.add("(" + dup_row_count + ") " + q_value);
						}
						if (q2_b) {
							// this.qtable.setTableValueAt("(" + dup_row_count +
							// ") " +
							// q_value, col_c_2++, 1);
							q2.add("(" + dup_row_count + ") " + q_value);
						}
						if (q3_b) {
							// this.qtable.setTableValueAt("(" + dup_row_count +
							// ") " +
							// q_value, col_c_3++, 2);
							q3.add("(" + dup_row_count + ") " + q_value);
						}
					} else {
						if (q1_b) {
							// this.qtable.setTableValueAt(q_value, col_c_1++,
							// 0);
							q1.add(q_value);
						}
						if (q2_b) {
							// this.qtable.setTableValueAt(q_value, col_c_2++,
							// 1);
							q2.add(q_value);
						}
						if (q3_b) {
							// this.qtable.setTableValueAt(q_value, col_c_3++,
							// 2);
							q3.add(q_value);
						}
					}
				}
			}
			rs.close();
			RdbmsConnection.closeConn();
		} catch (SQLException e) {
			System.out.println("\n ERROR:Regex Query execution failed");
			System.out.println(e.getMessage());
		} catch (PatternSyntaxException e) {
			System.out.println("\n ERROR:Regex compilation error");
			System.out.println(e.getMessage());
		}
		// this.q1_c.setText(" Pattern_1 Count: " + col_c_1 + "  ");
		// this.q2_c.setText(" Pattern_2 Count: " + col_c_2 + "  ");
		// this.q3_c.setText(" Pattern_3 Count: " + col_c_3 + "  ");
		String display = "{success: true, q1: \"" + col_c_1 + "\" , q2:\""
				+ col_c_2 + "\" , q3:\"" + col_c_3 + "\"}";
		StringAnalyticsPropertyDTO dto = null;
		if (!noGrid) {
			int len = Math.max(Math.max(q1.size(), q2.size()), q3.size());
			for (int ind = 0; ind < len; ind++) {
				dto = new StringAnalyticsPropertyDTO();
				dto.setQ1(q1.size() > ind ? q1.get(ind) : "");
				dto.setQ2(q2.size() > ind ? q2.get(ind) : "");
				dto.setQ3(q3.size() > ind ? q3.get(ind) : "");
				items.add(dto);
			}
			dbInfo.setItems(items);
			dbInfo.setTotalCount(10);

			return dbInfo;
		} else {
			return display;
		}
	}

	public String getQp_1() {
		return qp_1;
	}

	public void setQp_1(String qp_1) {
		this.qp_1 = qp_1;
	}

	public String getQp_2() {
		return qp_2;
	}

	public void setQp_2(String qp_2) {
		this.qp_2 = qp_2;
	}

	public String getQp_3() {
		return qp_3;
	}

	public void setQp_3(String qp_3) {
		this.qp_3 = qp_3;
	}

	public String getQ_s() {
		return q_s;
	}

	public void setQ_s(String q_s) {
		this.q_s = q_s;
	}

	public String get_distinct() {
		return _distinct;
	}

	public void set_distinct(String _distinct) {
		this._distinct = _distinct;
	}

	public String getQc_1() {
		return qc_1;
	}

	public void setQc_1(String qc_1) {
		this.qc_1 = qc_1;
	}

	public String getQc_2() {
		return qc_2;
	}

	public void setQc_2(String qc_2) {
		this.qc_2 = qc_2;
	}

	public String getQc_3() {
		return qc_3;
	}

	public void setQc_3(String qc_3) {
		this.qc_3 = qc_3;
	}

}
