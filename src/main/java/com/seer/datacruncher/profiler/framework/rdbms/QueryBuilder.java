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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.seer.datacruncher.profiler.framework.profile.TableMetaInfo;

public class QueryBuilder {

	private String _dsn;
	private String _table;
	private String _column;
	private String _dtype;
	private String _table1;
	private String _column1;
	private static boolean isCond = false;
	private static String _cond_q = "";
	private static Vector dateVar[];

	public QueryBuilder(String Dsn, String Table, String Column, String DBType) {
		_dsn = Dsn;
		_table = Table;
		_column = Column;
		_dtype = DBType;
		if (_dtype.compareToIgnoreCase("mysql") != 0) {
			if (!_table.startsWith("\""))
				_table = (new StringBuilder("\"")).append(_table).append("\"")
						.toString();
			if (!_column.startsWith("\""))
				_column = (new StringBuilder("\"")).append(_column)
						.append("\"").toString();
		}
		String cat = RdbmsConnection.getHValue("Database_Catalog");
		if (cat != null && !"".equals(cat))
			_table = (new StringBuilder(String.valueOf(cat))).append(".")
					.append(_table).toString();
	}

	public QueryBuilder(String Dsn, String Table, String DBType) {
		_dsn = Dsn;
		_table = Table;
		_column = "";
		_dtype = DBType;
		if (_dtype.compareToIgnoreCase("mysql") != 0
				&& !_table.startsWith("\""))
			_table = (new StringBuilder("\"")).append(_table).append("\"")
					.toString();
		String cat = RdbmsConnection.getHValue("Database_Catalog");
		if (cat != null && !"".equals(cat))
			_table = (new StringBuilder(String.valueOf(cat))).append(".")
					.append(_table).toString();
	}

	public QueryBuilder(String Dsn, String DBType) {
		_dsn = Dsn;
		_dtype = DBType;
	}

	public void setCTableCol(String Table, String Column) {
		_table1 = Table;
		_column1 = Column;
		if (_dtype.compareToIgnoreCase("mysql") != 0) {
			if (!_table1.startsWith("\""))
				_table1 = (new StringBuilder("\"")).append(_table1)
						.append("\"").toString();
			if (!_column1.startsWith("\""))
				_column1 = (new StringBuilder("\"")).append(_column1)
						.append("\"").toString();
		}
		String cat = RdbmsConnection.getHValue("Database_Catalog");
		if (cat != null && !"".equals(cat))
			_table1 = (new StringBuilder(String.valueOf(cat))).append(".")
					.append(_table1).toString();
	}

	public String get_tableAll_query() {
		String allTable = (new StringBuilder("SELECT * FROM ")).append(_table)
				.toString();
		return allTable;
	}

	public String get_tableCount_query() {
		String allCount = (new StringBuilder(
				"SELECT count(*) as row_count FROM ")).append(_table)
				.toString();
		return allCount;
	}

	public String count_query(boolean distinct, String col_name) {
		String count_query = "";
		if (!distinct) {
			count_query = (new StringBuilder(" SELECT count(")).append(_column)
					.append(") as ").append(col_name).append(" FROM ")
					.append(_table).toString();
			if (isCond)
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" WHERE ").append(_cond_q).toString();
		} else {
			count_query = (new StringBuilder("SELECT count(*) as "))
					.append(col_name).append(" FROM ( SELECT DISTINCT ")
					.append(_column).append(" FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" IS NOT NULL ")
					.toString();
			if (isCond)
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" AND ").append(_cond_q).toString();
			if (_dtype.compareToIgnoreCase("sql_server") == 0
					|| _dtype.compareToIgnoreCase("mysql") == 0)
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" ) as AS1").toString();
			else
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" )").toString();
		}
		return count_query;
	}

	public String count_query_w(boolean distinct, String col_name) {
		String count_query = "";
		if (!distinct) {
			count_query = (new StringBuilder(" SELECT count(")).append(_column)
					.append(") as ").append(col_name).append(" FROM ")
					.append(_table).toString();
		} else {
			count_query = (new StringBuilder("SELECT count(*) as "))
					.append(col_name).append(" FROM ( SELECT DISTINCT ")
					.append(_column).append(" FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" IS NOT NULL ")
					.toString();
			if (_dtype.compareToIgnoreCase("sql_server") == 0
					|| _dtype.compareToIgnoreCase("mysql") == 0)
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" ) as AS1").toString();
			else
				count_query = (new StringBuilder(String.valueOf(count_query)))
						.append(" )").toString();
		}
		return count_query;
	}

	public String bottom_query(boolean distinct, String col_name, String num) {
		String distinct_str = "";
		String bottom_sel_query = "";
		if (distinct)
			distinct_str = " DISTINCT ";
		if (_dtype.compareToIgnoreCase("oracle_native") == 0
				|| _dtype.compareToIgnoreCase("oracle_odbc") == 0) {
			bottom_sel_query = (new StringBuilder(" SELECT ")).append(_column)
					.append(" as ").append(col_name).append(" FROM ")
					.append(" (SELECT ").append(distinct_str).append(_column)
					.append(" FROM ").append(_table).toString();
			if (isCond)
				bottom_sel_query = (new StringBuilder(
						String.valueOf(bottom_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			bottom_sel_query = (new StringBuilder(
					String.valueOf(bottom_sel_query))).append(" order by ")
					.append(_column).append(") WHERE rownum <= ").append(num)
					.toString();
		} else if (_dtype.compareToIgnoreCase("mysql") == 0) {
			bottom_sel_query = (new StringBuilder(" SELECT "))
					.append(distinct_str).append(" ").append(_column)
					.append(" as ").append(col_name).append(" FROM ")
					.append(_table).toString();
			if (isCond)
				bottom_sel_query = (new StringBuilder(
						String.valueOf(bottom_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			bottom_sel_query = (new StringBuilder(
					String.valueOf(bottom_sel_query))).append(" order by ")
					.append(_column).append(" LIMIT 0,").append(num).toString();
		} else {
			bottom_sel_query = (new StringBuilder(" SELECT "))
					.append(distinct_str).append(" TOP ").append(num)
					.append(" ").append(_column).append(" as ")
					.append(col_name).append(" FROM ").append(_table)
					.toString();
			if (isCond)
				bottom_sel_query = (new StringBuilder(
						String.valueOf(bottom_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			bottom_sel_query = (new StringBuilder(
					String.valueOf(bottom_sel_query))).append(" order by ")
					.append(_column).toString();
		}
		return bottom_sel_query;
	}

	public String top_query(boolean distinct, String col_name, String num) {
		String distinct_str = "";
		String top_sel_query = "";
		if (distinct)
			distinct_str = " DISTINCT ";
		if (_dtype.compareToIgnoreCase("oracle_native") == 0
				|| _dtype.compareToIgnoreCase("oracle_odbc") == 0) {
			top_sel_query = (new StringBuilder(" SELECT ")).append(_column)
					.append(" as ").append(col_name).append(" FROM ")
					.append(" (SELECT ").append(distinct_str).append(_column)
					.append(" FROM ").append(_table).toString();
			if (isCond)
				top_sel_query = (new StringBuilder(
						String.valueOf(top_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			top_sel_query = (new StringBuilder(String.valueOf(top_sel_query)))
					.append(" order by ").append(_column)
					.append(" desc ) WHERE rownum <= ").append(num).toString();
		} else if (_dtype.compareToIgnoreCase("mysql") == 0) {
			top_sel_query = (new StringBuilder(" SELECT "))
					.append(distinct_str).append(" ").append(_column)
					.append(" as ").append(col_name).append(" FROM ")
					.append(_table).toString();
			if (isCond)
				top_sel_query = (new StringBuilder(
						String.valueOf(top_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			top_sel_query = (new StringBuilder(String.valueOf(top_sel_query)))
					.append(" order by ").append(_column)
					.append(" desc LIMIT 0,").append(num).toString();
		} else {
			top_sel_query = (new StringBuilder(" SELECT "))
					.append(distinct_str).append(" TOP ").append(num)
					.append(" ").append(_column).append(" as ")
					.append(col_name).append(" FROM ").append(_table)
					.toString();
			if (isCond)
				top_sel_query = (new StringBuilder(
						String.valueOf(top_sel_query))).append(" WHERE ")
						.append(_cond_q).toString();
			top_sel_query = (new StringBuilder(String.valueOf(top_sel_query)))
					.append(" order by ").append(_column).append(" desc ")
					.toString();
		}
		return top_sel_query;
	}

	public String aggr_query(String status, int index, String min_value,
			String max_value) {
		String count = "";
		String avg = "";
		String max = "";
		String min = "";
		String sum = "";
		String aggr_query = "";
		String total_count = status.substring(0, 1);
		int total_sel = (new Integer(total_count)).intValue();
		if (total_sel == 0)
			return aggr_query;
		if (status.charAt(1) == 'Y') {
			count = (new StringBuilder("count(")).append(_column)
					.append(") as row_count ").toString();
			if (total_sel > 1) {
				total_sel--;
				count = (new StringBuilder(String.valueOf(count))).append(",")
						.toString();
			}
		}
		if (status.charAt(2) == 'Y') {
			avg = (new StringBuilder("avg(")).append(_column)
					.append(") as avg_count ").toString();
			if (total_sel > 1) {
				total_sel--;
				avg = (new StringBuilder(String.valueOf(avg))).append(",")
						.toString();
			}
		}
		if (status.charAt(3) == 'Y') {
			max = (new StringBuilder("max(")).append(_column)
					.append(") as max_count ").toString();
			if (total_sel > 1) {
				total_sel--;
				max = (new StringBuilder(String.valueOf(max))).append(",")
						.toString();
			}
		}
		if (status.charAt(4) == 'Y') {
			min = (new StringBuilder("min(")).append(_column)
					.append(") as min_count ").toString();
			if (total_sel > 1) {
				total_sel--;
				min = (new StringBuilder(String.valueOf(min))).append(",")
						.toString();
			}
		}
		if (status.charAt(5) == 'Y')
			sum = (new StringBuilder(" sum(")).append(_column)
					.append(") as sum_count ").toString();
		if (index == 0) {
			aggr_query = (new StringBuilder("SELECT ")).append(count)
					.append(avg).append(max).append(min).append(sum)
					.append(" FROM ").append(_table).toString();
			if (isCond)
				aggr_query = (new StringBuilder(String.valueOf(aggr_query)))
						.append(" WHERE ").append(_cond_q).toString();
		}
		if (index == 1) {
			aggr_query = (new StringBuilder("SELECT ")).append(count)
					.append(avg).append(max).append(min).append(sum)
					.append(" FROM ").append(_table).append(" WHERE ")
					.append(_column).append(" < ").append(min_value).toString();
			if (isCond)
				aggr_query = (new StringBuilder(String.valueOf(aggr_query)))
						.append(" and ").append(_cond_q).toString();
		}
		if (index == 2) {
			aggr_query = (new StringBuilder("SELECT ")).append(count)
					.append(avg).append(max).append(min).append(sum)
					.append(" FROM ").append(_table).append(" WHERE ")
					.append(_column).append(" > ").append(max_value).toString();
			if (isCond)
				aggr_query = (new StringBuilder(String.valueOf(aggr_query)))
						.append(" and ").append(_cond_q).toString();
		}
		if (index == 3) {
			aggr_query = (new StringBuilder("SELECT ")).append(count)
					.append(avg).append(max).append(min).append(sum)
					.append(" FROM ").append(_table).append(" WHERE ")
					.append(_column).append(" > ").append(min_value)
					.append(" and ").append(_column).append(" < ")
					.append(max_value).toString();
			if (isCond)
				aggr_query = (new StringBuilder(String.valueOf(aggr_query)))
						.append(" and ").append(_cond_q).toString();
		}
		return aggr_query;
	}

	public String dist_count_query(int index, String min_value, String max_value) {
		String dist_count_query = (new StringBuilder(
				"SELECT count(*) as dist_count FROM ( SELECT DISTINCT "))
				.append(_column).append(" FROM ").append(_table).toString();
		if (isCond)
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" WHERE ")
					.append(_cond_q).toString();
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0)
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" ) as AS1")
					.toString();
		else
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" )").toString();
		if (index == 1)
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" WHERE ")
					.append(_column).append(" < ").append(min_value).toString();
		if (index == 2)
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" WHERE ")
					.append(_column).append(" > ").append(max_value).toString();
		if (index == 3)
			dist_count_query = (new StringBuilder(
					String.valueOf(dist_count_query))).append(" WHERE ")
					.append(_column).append(" > ").append(min_value)
					.append(" and ").append(_column).append(" < ")
					.append(max_value).toString();
		return dist_count_query;
	}

	public String get_like_query(String like_str, boolean like) {
		String like_query = "";
		if (like)
			like_query = (new StringBuilder("SELECT ")).append(_column)
					.append(" as like_wise FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" LIKE ")
					.append("'").append(like_str).append("'").toString();
		else
			like_query = (new StringBuilder("SELECT ")).append(_column)
					.append(" as like_wise FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" NOT LIKE ")
					.append("'").append(like_str).append("'").toString();
		if (isCond)
			like_query = (new StringBuilder(String.valueOf(like_query)))
					.append(" and ").append(_cond_q).toString();
		return like_query;
	}

	public String get_all_query() {
		String all_query = (new StringBuilder("SELECT ")).append(_column)
				.append(" as like_wise FROM ").append(_table).toString();
		if (isCond)
			all_query = (new StringBuilder(String.valueOf(all_query)))
					.append(" WHERE ").append(_cond_q).toString();
		all_query = (new StringBuilder(String.valueOf(all_query)))
				.append(" order by ").append(_column).toString();
		return all_query;
	}

	public String get_all_query_wcond_wnull() {
		String all_query = (new StringBuilder("SELECT ")).append(_column)
				.append(" as like_wise FROM ").append(_table).append(" WHERE ")
				.append(_column).append(" IS NOT NULL").toString();
		all_query = (new StringBuilder(String.valueOf(all_query)))
				.append(" order by ").append(_column).toString();
		return all_query;
	}

	public String get_freq_query_wnull() {
		String freq_query = (new StringBuilder("SELECT count( "))
				.append(_column).append(" ) as row_count,").append(_column)
				.append(" as like_wise FROM ").append(_table).append(" WHERE ")
				.append(_column).append(" IS NOT NULL").toString();
		freq_query = (new StringBuilder(String.valueOf(freq_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 0 order by ").append(_column)
				.toString();
		return freq_query;
	}

	public String get_all_worder_query() {
		String all_query = (new StringBuilder("SELECT ")).append(_column)
				.append(" as like_wise FROM ").append(_table).toString();
		if (isCond)
			all_query = (new StringBuilder(String.valueOf(all_query)))
					.append(" WHERE ").append(_cond_q).toString();
		return all_query;
	}

	public String get_nullCount_query(String equalTo) {
		String equal_query = (new StringBuilder(
				"SELECT count(*) as equal_count FROM ")).append(_table)
				.append(" WHERE ").append(_column).append(" Is ")
				.append(equalTo).toString();
		if (isCond)
			equal_query = (new StringBuilder(String.valueOf(equal_query)))
					.append(" and ").append(_cond_q).toString();
		return equal_query;
	}

	public String get_nullCount_query_w(String equalTo) {
		String equal_query = (new StringBuilder(
				"SELECT count(*) as equal_count FROM ")).append(_table)
				.append(" WHERE ").append(_column).append(" Is ")
				.append(equalTo).toString();
		return equal_query;
	}

	public String get_zeroCount_query(String equalTo) {
		String equal_query = (new StringBuilder("SELECT count( "))
				.append(_column).append(" ) as equal_count FROM ")
				.append(_table).append(" WHERE ").append(_column).append(" = ")
				.append(equalTo).toString();
		if (isCond)
			equal_query = (new StringBuilder(String.valueOf(equal_query)))
					.append(" and ").append(_cond_q).toString();
		return equal_query;
	}

	public String get_zeroCount_query_w(String equalTo) {
		String equal_query = (new StringBuilder("SELECT count( "))
				.append(_column).append(" ) as equal_count FROM ")
				.append(_table).append(" WHERE ").append(_column).append(" = ")
				.append(equalTo).toString();
		return equal_query;
	}

	public String get_prep_query() {
		String prep_query = (new StringBuilder("SELECT count( "))
				.append(_column).append(" ) as row_count FROM ").append(_table)
				.append(" WHERE ").append(_column).append(" >= ? and ")
				.append(_column).append(" < ?").toString();
		if (isCond)
			prep_query = (new StringBuilder(String.valueOf(prep_query)))
					.append(" and ").append(_cond_q).toString();
		return prep_query;
	}

	public String get_freq_query() {
		String freq_query = (new StringBuilder("SELECT count( "))
				.append(_column).append(" ) as row_count, ").append(_column)
				.append(" as like_wise FROM ").append(_table).toString();
		freq_query = (new StringBuilder(String.valueOf(freq_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 1 order by 1 desc").toString();
		return freq_query;
	}

	public String get_pattern_query() {
		String pattern_query = "SELECT count(*) as row_count FROM ( ";
		pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
				.append("SELECT ").append(_column).append(" FROM ")
				.append(_table).toString();
		pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 1 ").toString();
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0)
			pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
					.append(" ) as AS1").toString();
		else
			pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
					.append(" )").toString();
		return pattern_query;
	}

	public String get_pattern_all_query() {
		String pattern_query = "SELECT count(*) as row_count FROM ( ";
		pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
				.append("SELECT ").append(_column).append(" FROM ")
				.append(_table).toString();
		pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
				.append(" WHERE ").append(_column).append(" IS NOT NULL ")
				.toString();
		pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 0 ").toString();
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0)
			pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
					.append(" ) as AS1").toString();
		else
			pattern_query = (new StringBuilder(String.valueOf(pattern_query)))
					.append(" )").toString();
		return pattern_query;
	}

	public String get_freq_all_query() {
		String freq_all_query = (new StringBuilder("SELECT ")).append(_column)
				.append(" as like_wise, count( ").append(_column)
				.append(" ) as row_count FROM ").append(_table).toString();
		if (isCond)
			freq_all_query = (new StringBuilder(String.valueOf(freq_all_query)))
					.append(" WHERE ").append(_cond_q).toString();
		freq_all_query = (new StringBuilder(String.valueOf(freq_all_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 0 order by 2 desc").toString();
		return freq_all_query;
	}

	public String get_freq_like_query(String like_str, boolean like) {
		String freq_like_query = "";
		if (like)
			freq_like_query = (new StringBuilder("SELECT ")).append(_column)
					.append(" as like_wise, count( ").append(_column)
					.append(" ) as row_count FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" LIKE ")
					.append("'").append(like_str).append("'").toString();
		else
			freq_like_query = (new StringBuilder("SELECT ")).append(_column)
					.append(" as like_wise, count( ").append(_column)
					.append(" ) as row_count FROM ").append(_table)
					.append(" WHERE ").append(_column).append(" NOT LIKE ")
					.append("'").append(like_str).append("'").toString();
		if (isCond)
			freq_like_query = (new StringBuilder(
					String.valueOf(freq_like_query))).append(" and ")
					.append(_cond_q).toString();
		freq_like_query = (new StringBuilder(String.valueOf(freq_like_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(") > 0 order by 2 desc").toString();
		return freq_like_query;
	}

	public String get_match_count(byte multiple, int mX) {
		String m_count_query;
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0
				|| _dtype.compareToIgnoreCase("ms_access") == 0)
			m_count_query = (new StringBuilder(
					" SELECT count(*) as row_count,sum(AS1.row_count) as row_sum FROM ( SELECT count("))
					.append(_table).append(".").append(_column)
					.append(") as row_count FROM ").append(_table).toString();
		else
			m_count_query = (new StringBuilder(
					" SELECT count(*) as row_count,sum(row_count) as row_sum FROM ( SELECT count("))
					.append(_table).append(".").append(_column)
					.append(") as row_count FROM ").append(_table).toString();
		if (!_table.equals(_table1))
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append(",").append(_table1).append(" WHERE ")
					.append(_table).append(".").append(_column).append(" = ")
					.append(_table1).append(".").append(_column1)
					.append(" AND ").toString();
		else
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append(" WHERE ").toString();
		m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
				.append(_table).append(".").append(_column)
				.append(" IS NOT NULL GROUP BY ").append(_table).append(".")
				.append(_column).append(" HAVING ").append(" count(")
				.append(_table).append(".").append(_column).append(") ")
				.toString();
		if (_dtype.compareToIgnoreCase("sql_server") == 0
				|| _dtype.compareToIgnoreCase("ms_access") == 0
				|| _dtype.compareToIgnoreCase("mysql") == 0) {
			if (multiple == 0)
				m_count_query = (new StringBuilder(
						String.valueOf(m_count_query))).append("= 1 ) as AS1")
						.toString();
			else if (multiple == 1)
				m_count_query = (new StringBuilder(
						String.valueOf(m_count_query))).append(">= 1 ) as AS1")
						.toString();
			else if (multiple == 2)
				m_count_query = (new StringBuilder(
						String.valueOf(m_count_query))).append("> 1 ) as AS1")
						.toString();
			else if (multiple == 3)
				m_count_query = (new StringBuilder(
						String.valueOf(m_count_query))).append("= ").append(mX)
						.append(" ) as AS1").toString();
		} else if (multiple == 0)
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append("= 1 ) ").toString();
		else if (multiple == 1)
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append(">= 1 ) ").toString();
		else if (multiple == 2)
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append("> 1 ) ").toString();
		else if (multiple == 3)
			m_count_query = (new StringBuilder(String.valueOf(m_count_query)))
					.append("= ").append(mX).append(" ) ").toString();
		return m_count_query;
	}

	public String get_match_value(byte multiple, int mX, boolean match,
			boolean isLeft) {
		String m_match_query = "";
		String match_str = "";
		if (match)
			match_str = " IN (";
		else
			match_str = " NOT IN (";
		if (!_table.equals(_table1) && !isLeft)
			m_match_query = (new StringBuilder(" SELECT *  FROM "))
					.append(_table1).append(" WHERE ").append(_column1)
					.append(match_str).toString();
		else
			m_match_query = (new StringBuilder(" SELECT *  FROM "))
					.append(_table).append(" WHERE ").append(_column)
					.append(match_str).toString();
		if (!_table.equals(_table1))
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append(" SELECT ").append(_table).append(".")
					.append(_column).append(" FROM ").append(_table)
					.append(",").append(_table1).append(" WHERE ")
					.append(_table).append(".").append(_column).append(" = ")
					.append(_table1).append(".").append(_column1)
					.append("  AND ").append(_table).append(".")
					.append(_column).append(" IS NOT NULL GROUP BY ")
					.append(_table).append(".").append(_column)
					.append(" HAVING ").append(" count(").append(_table)
					.append(".").append(_column).append(") ").toString();
		else
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append(" SELECT ").append(_table).append(".")
					.append(_column).append(" FROM ").append(_table)
					.append(" WHERE ").append(_table).append(".")
					.append(_column).append(" IS NOT NULL GROUP BY ")
					.append(_table).append(".").append(_column)
					.append(" HAVING ").append(" count(").append(_table)
					.append(".").append(_column).append(") ").toString();
		if (multiple == 0)
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append("= 1 )").toString();
		else if (multiple == 1)
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append(">= 1 )").toString();
		else if (multiple == 2)
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append("> 1 )").toString();
		else if (multiple == 3)
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append("= ").append(mX).append(")").toString();
		if (isLeft)
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append(" ORDER BY ").append(_column).toString();
		else
			m_match_query = (new StringBuilder(String.valueOf(m_match_query)))
					.append(" ORDER BY ").append(_column1).toString();
		return m_match_query;
	}

	public String get_all_freq_query(boolean isDup) {
		String freq_query = (new StringBuilder("SELECT * FROM "))
				.append(_table).append(" WHERE ").append(_column)
				.append(" IN ").toString();
		freq_query = (new StringBuilder(String.valueOf(freq_query)))
				.append("( SELECT ").append(_column).append(" FROM (")
				.toString();
		freq_query = (new StringBuilder(String.valueOf(freq_query)))
				.append(" SELECT ").append(_column).append(" FROM ")
				.append(_table).toString();
		if (isCond)
			freq_query = (new StringBuilder(String.valueOf(freq_query)))
					.append(" WHERE ").append(_cond_q).toString();
		freq_query = (new StringBuilder(String.valueOf(freq_query)))
				.append(" group by ").append(_column).append(" having count(")
				.append(_column).append(")").toString();
		if (isDup)
			freq_query = (new StringBuilder(String.valueOf(freq_query)))
					.append(" > 1 ) ) ").toString();
		else
			freq_query = (new StringBuilder(String.valueOf(freq_query)))
					.append(" = 1 ) )").toString();
		return freq_query;
	}

	public String get_like_table(String searchS, int index, boolean isCount) {
		Vector avector[] = (Vector[]) null;
		avector = TableMetaInfo.populateTable(5, index, index + 1, avector);
		String columns = "";
		if (avector == null)
			return null;
		for (int j = 0; j < avector[0].size() - 1; j++)
			if (_dtype.compareToIgnoreCase("mysql") != 0)
				columns = (new StringBuilder(String.valueOf(columns)))
						.append("\"").append(avector[0].elementAt(j))
						.append("\" LIKE '%").append(searchS).append("%' OR ")
						.toString();
			else
				columns = (new StringBuilder(String.valueOf(columns)))
						.append(avector[0].elementAt(j)).append(" LIKE '%")
						.append(searchS).append("%' OR ").toString();

		if (avector[0].size() != 0)
			if (_dtype.compareToIgnoreCase("mysql") != 0)
				columns = (new StringBuilder(String.valueOf(columns)))
						.append("\"")
						.append(avector[0].elementAt(avector[0].size() - 1))
						.append("\" LIKE '%").append(searchS).append("%'")
						.toString();
			else
				columns = (new StringBuilder(String.valueOf(columns)))
						.append(avector[0].elementAt(avector[0].size() - 1))
						.append(" LIKE '%").append(searchS).append("%'")
						.toString();
		String tb_like_query = "";
		if (isCount)
			tb_like_query = (new StringBuilder("SELECT count(*) as COUNT FROM "))
					.append(_table).append(" WHERE ").append(columns)
					.toString();
		else
			tb_like_query = (new StringBuilder("SELECT *  FROM "))
					.append(_table).append(" WHERE ").append(columns)
					.toString();
		return tb_like_query;
	}

	public String get_tb_value(boolean isOrd) {
		String table = _table.charAt(0) != '"' ? _table : _table.replaceAll(
				"\"", "");
		Vector vector = RdbmsConnection.getTable();
		int i = vector.indexOf(table);
		Vector avector[] = (Vector[]) null;
		avector = TableMetaInfo.populateTable(5, i, i + 1, avector);
		String columns = "";
		if (avector == null)
			return null;
		for (int j = 0; j < avector[0].size() - 1; j++)
			if (_dtype.compareToIgnoreCase("mysql") != 0)
				columns = (new StringBuilder(String.valueOf(columns)))
						.append("\"").append(avector[0].elementAt(j))
						.append("\"").append(",").toString();
			else
				columns = (new StringBuilder(String.valueOf(columns)))
						.append(avector[0].elementAt(j)).append(",").toString();

		if (avector[0].size() != 0)
			if (_dtype.compareToIgnoreCase("mysql") != 0)
				columns = (new StringBuilder(String.valueOf(columns)))
						.append("\"")
						.append(avector[0].elementAt(avector[0].size() - 1))
						.append("\"").toString();
			else
				columns = (new StringBuilder(String.valueOf(columns))).append(
						avector[0].elementAt(avector[0].size() - 1)).toString();
		String tb_query = (new StringBuilder("SELECT ")).append(columns)
				.append(" FROM ").append(_table).toString();
		if (isCond)
			tb_query = (new StringBuilder(String.valueOf(tb_query)))
					.append(" WHERE ").append(_cond_q).toString();
		if (isOrd)
			tb_query = (new StringBuilder(String.valueOf(tb_query)))
					.append(" order by ").append(_column).toString();
		return tb_query;
	}

	public String[] get_mapping_query(Hashtable tb, Vector tableV) {
		String map_query[] = new String[tb.size()];
		String cat = RdbmsConnection.getHValue("Database_Catalog");
		int index = 0;
		for (Enumeration e = tb.keys(); e.hasMoreElements();) {
			String cols = "";
			String table = (String) e.nextElement();
			tableV.add(table);
			Vector vc = (Vector) tb.get(table);
			if (vc == null)
				System.out.println((new StringBuilder(
						"\n ERROR:Could not Find:")).append(table).toString());
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !table.startsWith("\""))
				table = (new StringBuilder("\"")).append(table).append("\"")
						.toString();
			if (cat != null && !"".equals(cat))
				table = (new StringBuilder(String.valueOf(cat))).append(".")
						.append(table).toString();
			for (int i = 0; i < vc.size(); i++) {
				String col = (String) vc.elementAt(i);
				if (_dtype.compareToIgnoreCase("mysql") != 0
						&& !col.startsWith("\""))
					col = (new StringBuilder("\"")).append(col).append("\"")
							.toString();
				if ("".equals(cols))
					cols = (new StringBuilder(String.valueOf(table)))
							.append(".").append(col).toString();
				else
					cols = (new StringBuilder(String.valueOf(cols)))
							.append(",").append(table).append(".").append(col)
							.toString();
			}

			map_query[index] = (new StringBuilder("SELECT ")).append(cols)
					.append(" FROM ").append(table).toString();
			index++;
		}

		return map_query;
	}

	public Vector get_synch_mapping_query(Vector table_s, Vector column_s) {
		Vector synch_map_query = new Vector();
		String cat = RdbmsConnection.getHValue("Database_Catalog");
		for (int index = 0; index < table_s.size(); index++) {
			String table = (String) table_s.get(index);
			String col = (String) column_s.get(index);
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !table.startsWith("\""))
				table = (new StringBuilder("\"")).append(table).append("\"")
						.toString();
			if (cat != null && !"".equals(cat))
				table = (new StringBuilder(String.valueOf(cat))).append(".")
						.append(table).toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !col.startsWith("\""))
				col = (new StringBuilder("\"")).append(col).append("\"")
						.toString();
			String query = (new StringBuilder("SELECT ")).append(table)
					.append(".").append(col).append(" FROM ").append(table)
					.toString();
			synch_map_query.add(query);
		}

		return synch_map_query;
	}

	public String get_table_duprow_query(Vector col_vc, String cond) {
		String dup_row_query = "";
		String columns = "";
		String column = "";
		for (Enumeration cols = col_vc.elements(); cols.hasMoreElements();) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !column.startsWith("\""))
				column = (new StringBuilder("\"")).append(column).append("\"")
						.toString();
			if ("".equals(columns))
				columns = (new StringBuilder(String.valueOf(columns))).append(
						column).toString();
			else
				columns = (new StringBuilder(String.valueOf(columns)))
						.append(",").append(column).toString();
		}

		dup_row_query = (new StringBuilder("SELECT count(")).append(column)
				.append(") as count").append(",").append(columns)
				.append(" from ").append(_table).toString();
		if (!"".equals(cond))
			dup_row_query = (new StringBuilder(String.valueOf(dup_row_query)))
					.append(" WHERE ").append(cond).toString();
		dup_row_query = (new StringBuilder(String.valueOf(dup_row_query)))
				.append(" GROUP BY ").append(columns)
				.append(" HAVING COUNT(*) > 1 ").toString();
		return dup_row_query;
	}

	public String get_equal_query(Vector col_vc, String condition) {
		String equal_query = "";
		String columns = "";
		String column = "";
		for (Enumeration cols = col_vc.elements(); cols.hasMoreElements();) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !column.startsWith("\""))
				column = (new StringBuilder("\"")).append(column).append("\"")
						.toString();
			if (cols.hasMoreElements())
				column = (new StringBuilder(String.valueOf(column))).append(
						"= ? AND ").toString();
			else
				column = (new StringBuilder(String.valueOf(column))).append(
						"= ? ").toString();
			columns = (new StringBuilder(String.valueOf(columns))).append(
					column).toString();
		}

		equal_query = (new StringBuilder("SELECT * from ")).append(_table)
				.append(" WHERE ").append(columns).toString();
		if (condition != null && !"".equals(condition))
			equal_query = (new StringBuilder(String.valueOf(equal_query)))
					.append(" AND (").append(condition).append(")").toString();
		return equal_query;
	}

	public String get_inclusive_query(Vector col_vc, boolean isInclusive) {
		String inclusive_query = "";
		String columns = "";
		String column = "";
		String inclusive = "";
		if (isInclusive)
			inclusive = " AND ";
		else
			inclusive = " OR ";
		for (Enumeration cols = col_vc.elements(); cols.hasMoreElements();) {
			column = cols.nextElement().toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !column.startsWith("\""))
				column = (new StringBuilder("\"")).append(column).append("\"")
						.toString();
			if (cols.hasMoreElements())
				column = (new StringBuilder(String.valueOf(column)))
						.append(" IS NULL").append(inclusive).toString();
			else
				column = (new StringBuilder(String.valueOf(column))).append(
						" IS NULL").toString();
			columns = (new StringBuilder(String.valueOf(columns))).append(
					column).toString();
		}

		inclusive_query = (new StringBuilder("SELECT * from ")).append(_table)
				.append(" WHERE ").append(columns).toString();
		return inclusive_query;
	}

	public String get_selCol_query(Object col[], String cond) {
		String selColQuery = "";
		String column = "";
		for (int i = 0; i < col.length; i++) {
			String colN = col[i].toString();
			if (_dtype.compareToIgnoreCase("mysql") != 0
					&& !colN.startsWith("\""))
				colN = (new StringBuilder("\"")).append(colN).append("\"")
						.toString();
			if (!"".equals(column))
				column = (new StringBuilder(String.valueOf(column)))
						.append(",").toString();
			column = (new StringBuilder(String.valueOf(column))).append(colN)
					.toString();
		}

		selColQuery = (new StringBuilder("SELECT ")).append(column)
				.append(" FROM ").append(_table).toString();
		if (cond != null && !"".equals(cond))
			selColQuery = (new StringBuilder(String.valueOf(selColQuery)))
					.append(" WHERE ").append(cond).toString();
		return selColQuery;
	}

	public static void setCond(String query) {
		isCond = true;
		_cond_q = (new StringBuilder("(")).append(query).append(")").toString();
	}

	public static void unsetCond() {
		isCond = false;
		_cond_q = "";
	}

	public static String getCond() {
		if (isCond)
			return _cond_q;
		else
			return "";
	}

	public static Vector[] getDateCondition() {
		if (dateVar == null)
			return dateVar = new Vector[2];
		else
			return dateVar;
	}

	public static void setDateCondition(Vector vc[]) {
		dateVar = vc;
	}

}
