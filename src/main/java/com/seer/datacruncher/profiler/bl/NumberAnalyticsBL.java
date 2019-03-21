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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.seer.datacruncher.profiler.dto.GridInfoDTO;
import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.dto.dbinfo.NumProfilePropertyDTO;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class NumberAnalyticsBL {

	private String _dsn;
	private String _type;
	private String _condition;
	private String _table;
	private String _col;
	private Hashtable<String, String> table_info;
	private String aggr;
	private String text2;
	private String less;
	private String text3;
	private String more;
	private String text4;
	private String text5;
	private String between1;
	private String text6;
	private String text7;
	private String between2;
	private Double[] r_values = new Double[10];
	public int scaledXValues[];
	private int color_index = 0;
	public double zoomFactor = 1.0D;

	public NumberAnalyticsBL(String dsn, String type, String condition,
			String table, String column) {
		this._dsn = dsn;
		this._type = type;
		this._condition = condition;
		this._table = table;
		this._col = column;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableGridDTO generateStatisticGrids(Integer tab) {
		TableGridDTO tableGridDTO = new TableGridDTO();

		List<Vector<Object>> rowValues1 = new ArrayList<Vector<Object>>();
		List<Vector<Object>> rowValues2 = new ArrayList<Vector<Object>>();
		List<Vector<Object>> rowValues3 = new ArrayList<Vector<Object>>();
		QueryBuilder a_q = new QueryBuilder(this._dsn, this._table, this._col,
				RdbmsConnection.getDBType());
		double count_d = 0.0D;
		double avg_d = 0.0D;
		double max_d = 0.0D;
		double min_d = 0.0D;
		double sum_d = 0.0D;

		String aggr_query = a_q.aggr_query("5YYYYY", 0, "0", "0");

		try {
			RdbmsConnection.openConn();
			ResultSet rs = RdbmsConnection.runQuery(aggr_query);

			while (rs.next()) {
				String count_str = rs.getString("row_count");
				String avg_str = rs.getString("avg_count");
				String max_str = rs.getString("max_count");
				String min_str = rs.getString("min_count");
				String sum_str = rs.getString("sum_count");

				count_d = Double.valueOf(count_str).doubleValue();
				if (count_d <= 0.0D) {
					System.out.println("No Data to Analyse");
					rs.close();
				}

				avg_d = Double.valueOf(avg_str).doubleValue();
				max_d = Double.valueOf(max_str).doubleValue();
				min_d = Double.valueOf(min_str).doubleValue();
				sum_d = Double.valueOf(sum_str).doubleValue();

				rowValues2.add(new Vector(Arrays.asList(new String[] {
						"Sample Size", count_str })));
				rowValues2.add(new Vector(Arrays.asList(new String[] {
						"Maximum ", max_str })));
				rowValues2.add(new Vector(Arrays.asList(new String[] {
						"Minimum ", min_str })));
				rowValues2.add(new Vector(Arrays.asList(new String[] { "Range",
						Double.toString(max_d - min_d) })));
				rowValues2.add(new Vector(Arrays.asList(new String[] {
						"Summation", sum_str })));
				rowValues2.add(new Vector(Arrays.asList(new String[] { "Mean",
						avg_str })));

			}

			rs.close();

			String all_q = a_q.get_all_query();
			double variance = 0.0D;
			double aad = 0.0D;
			double skew = 0.0D;
			double kurt = 0.0D;

			long[] perc_a = new long[21];
			int arr_i = 0;
			double[] perv_a = new double[21];
			int dataset_c = 1;

			perc_a[0] = Math.round(count_d / 100.0D);
			if (perc_a[0] == 0L) {
				arr_i = 1;
				perv_a[0] = 0.0D;
			}

			for (int i = 1; i < 20; i++) {
				perc_a[i] = Math.round(5 * i * count_d / 100.0D);
				if (perc_a[i] == 0L) {
					arr_i++;
					perv_a[i] = 0.0D;
				}
			}
			perc_a[20] = Math.round(99.0D * count_d / 100.0D);
			if (perc_a[20] == 0L) {
				arr_i = 21;
				perv_a[20] = 0.0D;
			}

			rs = RdbmsConnection.runQuery(all_q);
			while (rs.next()) {
				String q_value = rs.getString("like_wise");
				if ((q_value == null) || (q_value.equals(""))) {
					System.out
							.println("\n WARNING: Null or Empty value ignored - might affect result");
				} else {
					double d = Double.valueOf(q_value).doubleValue();

					if ((arr_i < 21) && (dataset_c == perc_a[arr_i])) {
						while ((arr_i < 20)
								&& (perc_a[(arr_i + 1)] == perc_a[arr_i])) {
							perv_a[arr_i] = d;
							arr_i++;
						}
						perv_a[arr_i] = d;
						arr_i++;
					}

					aad += Math.abs(d - avg_d) / count_d;
					variance += Math.pow(d - avg_d, 2.0D) / (count_d - 1.0D);
					skew += Math.pow(d - avg_d, 3.0D);
					kurt += Math.pow(d - avg_d, 4.0D);

					dataset_c++;
				}
			}
			rs.close();
			RdbmsConnection.closeConn();

			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Avg. Absolute Dev.(AAD)", Double.toString(aad) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] { "Variance",
					Double.toString(variance) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Std. Dev.(SD)", Double.toString(Math.sqrt(variance)) })));
			rowValues2.add(new Vector(
					Arrays.asList(new String[] {
							"Std. Error of Mean(SE)",
							Double.toString(Math.sqrt(variance)
									/ Math.sqrt(count_d)) })));
			rowValues2
					.add(new Vector(Arrays.asList(new String[] {
							"Skewness",
							Double.toString(skew
									/ ((count_d - 1.0D) * Math.pow(variance,
											1.5D))) })));
			rowValues2
					.add(new Vector(Arrays.asList(new String[] {
							"Kurtosis",
							Double.toString(kurt
									/ ((count_d - 1.0D) * Math.pow(variance,
											2.0D))) })));

			rowValues3.add(new Vector(Arrays.asList(new String[] { "1",
					Double.toString(perv_a[0]), Long.toString(perc_a[0]) })));

			for (int i = 1; i < 20; i++) {
				rowValues3.add(new Vector(Arrays.asList(new String[] {
						Integer.toString(i * 5), Double.toString(perv_a[i]),
						Long.toString(perc_a[i]) })));

			}
			rowValues3.add(new Vector(Arrays.asList(new String[] { "99",
					Double.toString(perv_a[20]), Long.toString(perc_a[20]) })));

			rowValues2.add(new Vector(Arrays.asList(new String[] { "", "" })));

			rowValues2.add(new Vector(Arrays.asList(new String[] { "Mid Range",
					Double.toString((max_d + min_d) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(1%-99%)",
					Double.toString((perv_a[0] + perv_a[20]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(5%-95%)",
					Double.toString((perv_a[1] + perv_a[19]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(10%-90%)",
					Double.toString((perv_a[2] + perv_a[18]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(15%-85%)",
					Double.toString((perv_a[3] + perv_a[17]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(20%-80%)",
					Double.toString((perv_a[4] + perv_a[16]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(25%-75%)",
					Double.toString((perv_a[5] + perv_a[15]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(30%-70%)",
					Double.toString((perv_a[6] + perv_a[14]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(35%-65%)",
					Double.toString((perv_a[7] + perv_a[13]) / 2.0D) })));
			rowValues2.add(new Vector(Arrays.asList(new String[] {
					"Mid Range(40%-60%)",
					Double.toString((perv_a[8] + perv_a[12]) / 2.0D) })));

		} catch (Exception sql_e) {
			System.out.println("\n ERROR: Advance Query execution failed");
			System.out.println(sql_e.getMessage());
		}

		String query = a_q.get_freq_all_query();
		try {
			RdbmsConnection.openConn();
			ResultSet rs = RdbmsConnection.runQuery(query);
			while (rs.next()) {

				String col_name = rs.getString("like_wise");
				String col_count = rs.getString("row_count");

				double col_count_d = Double.valueOf(col_count).doubleValue();

				rowValues1.add(new Vector(Arrays.asList(new String[] {
						col_name, col_count,
						Double.toString(col_count_d / count_d * 100.0D) })));

			}

			rs.close();
			RdbmsConnection.closeConn();
		} catch (Exception sql_e) {
			System.out.println("\n ERROR: Frequency Query execution failed");
			System.out.println(sql_e.getMessage());
		}
		if (tab == 1) {
			tableGridDTO.setColumnNames(new String[] { "Record Value",
					"Frequency", "% Freq." });
			tableGridDTO.setRowValues(rowValues1);
		} else if (tab == 2) {
			tableGridDTO.setColumnNames(new String[] { "Range Metric",
					"Metric Value" });
			tableGridDTO.setRowValues(rowValues2);
		} else {
			tableGridDTO.setColumnNames(new String[] { "Percentile %",
					"Record Upper Value", "Samples Below" });
			tableGridDTO.setRowValues(rowValues3);
		}

		return tableGridDTO;
	}

	public GridInfoDTO numberProfile() {

		GridInfoDTO dbInfo = new GridInfoDTO();
		List<NumProfilePropertyDTO> items = new ArrayList<NumProfilePropertyDTO>();
		NumProfilePropertyDTO property1 = new NumProfilePropertyDTO();
		property1.setValue("COUNT");
		NumProfilePropertyDTO property2 = new NumProfilePropertyDTO();
		property2.setValue("AVG");
		NumProfilePropertyDTO property3 = new NumProfilePropertyDTO();
		property3.setValue("MAX");
		NumProfilePropertyDTO property4 = new NumProfilePropertyDTO();
		property4.setValue("MIN");
		NumProfilePropertyDTO property5 = new NumProfilePropertyDTO();
		property5.setValue("SUM");
		NumProfilePropertyDTO property6 = new NumProfilePropertyDTO();
		property6.setValue("DUPLICATE");
		if (this.table_info == null) {
			System.out.println("Select a Column to Profile");
		}

		String dsn_str = _dsn;
		String type_str = _type;
		String tbl_str = _table;
		String col_str = _col;

		QueryBuilder q_factory = new QueryBuilder(dsn_str, tbl_str, col_str,
				RdbmsConnection.getDBType());

		try {
			RdbmsConnection.openConn();
		} catch (SQLException sqlexc) {
			System.out.println("\n Open Connection Exception");
			JOptionPane.showMessageDialog(null, sqlexc.getMessage(),
					"Error Message", 0);
		}

		String aggr_sel = "";
		String less_sel = "";
		String more_sel = "";
		String bet1_sel = "";
		String bet2_sel = "";
		String aggr_query = "";
		String less_query = "";
		String more_query = "";
		String bet1_query = "";
		String bet2_query = "";
		String null_query = "";
		String zero_query = "";
		String neg_query = "";
		String less_val = "";
		String more_val = "";
		String b1_less_val = "";
		String b1_more_val = "";
		String b2_less_val = "";
		String b2_more_val = "";
		String[] values = { "" };
		String aggr_count = "";
		String less_count = "";
		String more_count = "";
		String bet1_count = "";
		String bet2_count = "";
		String aggr_dist = "";
		String less_dist = "";
		String more_dist = "";
		String bet1_dist = "";
		String bet2_dist = "";
		boolean dup_chk = true;
		long aggr_dup_count = 0L;
		long less_dup_count = 0L;
		long more_dup_count = 0L;
		long bet1_dup_count = 0L;
		long bet2_dup_count = 0L;

		aggr_sel = this.aggr;

		aggr_query = q_factory.aggr_query(aggr_sel, 0, "0", "0");
		if (dup_chk) {
			aggr_count = q_factory.aggr_query("1YNNNN", 0, "0", "0");
			aggr_dist = q_factory.dist_count_query(0, "0", "0");
		}

		if (this.text2 != null) {
			less_val = this.text2.toString();
			less_sel = this.less;
			less_query = q_factory.aggr_query(less_sel, 1, less_val, "0");
			if (dup_chk) {
				less_count = q_factory.aggr_query("1YNNNN", 1, less_val, "0");
				less_dist = q_factory.dist_count_query(1, less_val, "0");
			}

		}

		if (this.text3 != null) {
			more_val = this.text3.toString();
			more_sel = this.more;
			more_query = q_factory.aggr_query(more_sel, 2, "0", more_val);
			if (dup_chk) {
				more_count = q_factory.aggr_query("1YNNNN", 2, "0", more_val);
				more_dist = q_factory.dist_count_query(2, "0", more_val);
			}

		}

		if ((this.text4 != null) && (this.text5 != null)) {
			b1_less_val = this.text4.toString();
			b1_more_val = this.text5.toString();

			if (new Double(b1_less_val).doubleValue() > new Double(b1_more_val)
					.doubleValue()) {
				System.out
						.println("\n WARNING:Less Val is more than More Value");
			} else {
				bet1_sel = this.between1;
				bet1_query = q_factory.aggr_query(bet1_sel, 3, b1_less_val,
						b1_more_val);
				if (dup_chk) {
					bet1_count = q_factory.aggr_query("1YNNNN", 3, b1_less_val,
							b1_more_val);
					bet1_dist = q_factory.dist_count_query(3, b1_less_val,
							b1_more_val);
				}
			}

		}

		if ((this.text6 != null) && (this.text7 != null)) {
			b2_less_val = this.text6.toString();
			b2_more_val = this.text7.toString();

			if (new Double(b2_less_val).doubleValue() > new Double(b2_more_val)
					.doubleValue()) {
				System.out.println("\n Less Val is more than More Value");
			} else {
				bet2_sel = this.between2;
				bet2_query = q_factory.aggr_query(bet2_sel, 3, b2_less_val,
						b2_more_val);
				if (dup_chk) {
					bet2_count = q_factory.aggr_query("1YNNNN", 3, b2_less_val,
							b2_more_val);
					bet2_dist = q_factory.dist_count_query(3, b2_less_val,
							b2_more_val);
				}
			}

		}

		// ReportTable table = new ReportTable(less_val, more_val, b1_less_val,
		// b1_more_val, b2_less_val, b2_more_val);
		try {

			if (!aggr_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(aggr_query);
				while (rs.next()) {
					values = createColValue(rs, aggr_sel);
				}

				rs.close();
				// table.setColValue(1, values);

				property1.setAggr(values[0]);
				property2.setAggr(values[1]);
				property3.setAggr(values[2]);
				property4.setAggr(values[3]);
				property5.setAggr(values[4]);
			} else {
				property1.setAggr("");
				property2.setAggr("");
				property3.setAggr("");
				property4.setAggr("");
				property5.setAggr("");
			}

			if ((!aggr_count.equals("")) && (!aggr_dist.equals(""))) {
				String ag = "";
				String dg = "";
				ResultSet rs = RdbmsConnection.runQuery(aggr_count);
				while (rs.next())
					ag = rs.getString("row_count");
				rs.close();

				rs = RdbmsConnection.runQuery(aggr_dist);
				while (rs.next())
					dg = rs.getString("dist_count");
				rs.close();

				aggr_dup_count = getDuplicateCount(ag, dg);
				// table.setTableValueAt(Long.toString(aggr_dup_count), 5, 1);
				property6.setAggr(Long.toString(aggr_dup_count));
			} else {
				property6.setAggr("");
			}

			if (!less_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(less_query);
				while (rs.next()) {
					values = createColValue(rs, less_sel);
				}

				rs.close();
				// table.setColValue(2, values);
				property1.setLess(values[0]);
				property2.setLess(values[1]);
				property3.setLess(values[2]);
				property4.setLess(values[3]);
				property5.setLess(values[4]);
			} else {
				property1.setLess("");
				property2.setLess("");
				property3.setLess("");
				property4.setLess("");
				property5.setLess("");
			}

			if ((!less_count.equals("")) && (!less_dist.equals(""))) {
				String ag = "";
				String dg = "";
				ResultSet rs = RdbmsConnection.runQuery(less_count);
				while (rs.next())
					ag = rs.getString("row_count");
				rs.close();

				rs = RdbmsConnection.runQuery(less_dist);
				while (rs.next())
					dg = rs.getString("dist_count");
				rs.close();

				less_dup_count = getDuplicateCount(ag, dg);
				// table.setTableValueAt(Long.toString(less_dup_count), 5, 2);
				property6.setLess(Long.toString(less_dup_count));
			} else {
				property6.setLess("");
			}

			if (!more_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(more_query);
				while (rs.next()) {
					values = createColValue(rs, more_sel);
				}

				rs.close();
				// table.setColValue(3, values);
				property1.setMore(values[0]);
				property2.setMore(values[1]);
				property3.setMore(values[2]);
				property4.setMore(values[3]);
				property5.setMore(values[4]);
			} else {
				property1.setMore("");
				property2.setMore("");
				property3.setMore("");
				property4.setMore("");
				property5.setMore("");
			}

			if ((!more_count.equals("")) && (!more_dist.equals(""))) {
				String ag = "";
				String dg = "";
				ResultSet rs = RdbmsConnection.runQuery(more_count);
				while (rs.next())
					ag = rs.getString("row_count");
				rs.close();

				rs = RdbmsConnection.runQuery(more_dist);
				while (rs.next())
					dg = rs.getString("dist_count");
				rs.close();

				more_dup_count = getDuplicateCount(ag, dg);
				// table.setTableValueAt(Long.toString(more_dup_count), 5, 3);
				property6.setMore(Long.toString(less_dup_count));
			} else {
				property6.setMore("");
			}

			if (!bet1_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(bet1_query);
				while (rs.next()) {
					values = createColValue(rs, bet1_sel);
				}

				rs.close();
				// table.setColValue(4, values);
				property1.setBetween1(values[0]);
				property2.setBetween1(values[1]);
				property3.setBetween1(values[2]);
				property4.setBetween1(values[3]);
				property5.setBetween1(values[4]);
			} else {
				property1.setBetween1("");
				property2.setBetween1("");
				property3.setBetween1("");
				property4.setBetween1("");
				property5.setBetween1("");
			}

			if ((!bet1_count.equals("")) && (!bet1_dist.equals(""))) {
				String ag = "";
				String dg = "";
				ResultSet rs = RdbmsConnection.runQuery(bet1_count);
				while (rs.next())
					ag = rs.getString("row_count");
				rs.close();

				rs = RdbmsConnection.runQuery(bet1_dist);
				while (rs.next())
					dg = rs.getString("dist_count");
				rs.close();
				bet1_dup_count = getDuplicateCount(ag, dg);
				// table.setTableValueAt(Long.toString(bet1_dup_count), 5, 4);
				property6.setBetween1(Long.toString(bet1_dup_count));
			} else {
				property6.setBetween1("");
			}

			if (!bet2_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(bet2_query);
				while (rs.next()) {
					values = createColValue(rs, bet2_sel);
				}

				rs.close();
				// table.setColValue(5, values);
				property1.setBetween2(values[0]);
				property2.setBetween2(values[1]);
				property3.setBetween2(values[2]);
				property4.setBetween2(values[3]);
				property5.setBetween2(values[4]);
			} else {
				property1.setBetween2("");
				property2.setBetween2("");
				property3.setBetween2("");
				property4.setBetween2("");
				property5.setBetween2("");
			}

			if ((!bet2_count.equals("")) && (!bet2_dist.equals(""))) {
				String ag = "";
				String dg = "";
				ResultSet rs = RdbmsConnection.runQuery(bet2_count);
				while (rs.next())
					ag = rs.getString("row_count");
				rs.close();

				rs = RdbmsConnection.runQuery(bet2_dist);
				while (rs.next())
					dg = rs.getString("dist_count");
				rs.close();
				bet2_dup_count = getDuplicateCount(ag, dg);
				// table.setTableValueAt(Long.toString(bet2_dup_count), 5, 5);
				property6.setBetween2(Long.toString(bet2_dup_count));
			} else {
				property6.setBetween2("");
			}

			if (!null_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(null_query);
				while (rs.next()) {
					this.table_info.put("Null_Count",
							rs.getString("equal_count"));
				}
				rs.close();
			}

			if (!zero_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(zero_query);
				while (rs.next()) {
					this.table_info.put("Zero_Count",
							rs.getString("equal_count"));
				}
				rs.close();
			}

			if (!neg_query.equals("")) {
				ResultSet rs = RdbmsConnection.runQuery(neg_query);
				while (rs.next()) {
					this.table_info.put("Neg_Count", rs.getString("row_count"));
				}
				rs.close();
			}

			RdbmsConnection.closeConn();
		} catch (SQLException sqlexc) {
			System.out.println("\n Running Query Exception");
			JOptionPane.showMessageDialog(null, sqlexc.getMessage(),
					"Error Message", 0);
		} finally {

		}
		// addTable(table);

		items.add(property1);
		items.add(property2);
		items.add(property3);
		items.add(property4);
		items.add(property5);
		items.add(property6);
		dbInfo.setItems(items);
		dbInfo.setTotalCount(10);

		return dbInfo;
	}

	private String[] createColValue(ResultSet rs, String sel) {
		String[] values = { "" };
		String count_str = "";
		String avg_str = "";
		String max_str = "";
		String min_str = "";
		String sum_str = "";
		try {
			if (sel.charAt(1) == 'Y')
				count_str = rs.getString("row_count");
			if (sel.charAt(2) == 'Y')
				avg_str = rs.getString("avg_count");
			if (sel.charAt(3) == 'Y')
				max_str = rs.getString("max_count");
			if (sel.charAt(4) == 'Y')
				min_str = rs.getString("min_count");
			if (sel.charAt(5) == 'Y') {
				sum_str = rs.getString("sum_count");
			}
			values = new String[] { count_str, avg_str, max_str, min_str,
					sum_str };
		} catch (SQLException sqlexc) {
			System.out.println("\n ERROR: Createing Column Value Exception");
			JOptionPane.showMessageDialog(null, sqlexc.getMessage(),
					"Error Message", 0);
		}
		return values;
	}

	private long getDuplicateCount(String dup_count, String dup_dist) {
		long i_dup_count = 0L;
		i_dup_count = Math.round(Double.parseDouble(dup_count)
				- Double.parseDouble(dup_dist));
		return i_dup_count;
	}

	public Double[] fillXValues(String text[]) throws SQLException {

		double[] values = new double[text.length];
		QueryBuilder q_factory = new QueryBuilder(this._dsn, this._table,
				this._col, RdbmsConnection.getDBType());
		String q_str = q_factory.get_prep_query();
		try {
			RdbmsConnection.openConn();
			PreparedStatement stmt = RdbmsConnection.createQuery(q_str);
			if (stmt == null) {
				System.out.println("\n ERROR:Bin Query Null");
				return null;
			}

			for (int i = 0; i < text.length; i++) {
				values[i] = Double.parseDouble(text[i]);
				if (i != 0) {
					int ic = q_str.indexOf(" ?", 0);
					if (ic != -1) {
						stmt.setDouble(1, values[(i - 1)]);
					}
					ic = q_str.indexOf(" ?", ic + 1);
					if (ic != -1) {
						stmt.setDouble(2, values[i]);
					}
					ic = q_str.indexOf(" ?", ic + 1);
					if (ic != -1) {
						Vector[] dateVar = QueryBuilder.getDateCondition();
						for (int j = 0; j < dateVar[0].size(); j++) {
							String s1 = (String) dateVar[1].get(j);
							if (s1.compareToIgnoreCase("time") == 0)
								stmt.setTime(
										j + 3,
										new Time(((java.util.Date) dateVar[0]
												.get(j)).getTime()));
							if (s1.compareToIgnoreCase("date") == 0)
								stmt.setDate(
										j + 3,
										new java.sql.Date(
												((java.util.Date) dateVar[0]
														.get(j)).getTime()));
							if (s1.compareToIgnoreCase("timestamp") == 0) {
								stmt.setTimestamp(
										j + 3,
										new Timestamp(
												((java.util.Date) dateVar[0]
														.get(j)).getTime()));
							}
						}
					}
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						double val = rs.getDouble("row_count");
						this.r_values[(i - 1)] = val;
					}
					rs.close();
				}
			}
			RdbmsConnection.closeConn();
		} catch (SQLException e) {
			System.out.println("\n SQL Exception in Bin Query");
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Error Message", 0);
			throw e;
		}
		return this.r_values;
	}

//	 public void scaleValuesUniformly()
//     {
//        double d = max(r_values);
//        double d1 = min(r_values);
//        double d2 = 0.0D;
//       
//        d2 = d / (double)(getHeight() - 50);
//        scale = d2 * zoomFactor;
//        for(int j = 0; j < scaledXValues.length; j++)
//            scaledXValues[j] = scale != 0.0D ? (int)(25D + xValues[j] / scale) : (int)(25D + xValues[j]);
//
//     }
	 
	 public static double max(double ad[])
     {
       if(ad == null || ad.length == 0)
           return 0.0D;
       double d = ad[0];
       for(int i = 0; i < ad.length; i++)
           if(ad[i] > d)
               d = ad[i];

       return d;
     }
	 public static double min(double ad[])
     {
        if(ad == null || ad.length == 0)
            return 0.0D;
        double d = ad[0];
        for(int i = 0; i < ad.length; i++)
            if(ad[i] < d)
                d = ad[i];

        return d;
     }
	public String[] getNValues(String[] binName) {
		String[] values = new String[binName.length];
		for (int i = 0; i < binName.length; i++) {
			values[i] = binName[i];
		}

		return values;
	}

	public Double[] getXValues() {
		return this.r_values;
	}

	public int getColorIndex() {
		return this.color_index;
	}

	// public double[] getBboundary() {
	// return this.values;
	// }
	public Hashtable<String, String> getTable_info() {
		return table_info;
	}

	public void setTable_info(Hashtable<String, String> table_info) {
		this.table_info = table_info;
	}

	public String getAggr() {
		return aggr;
	}

	public void setAggr(String aggr) {
		this.aggr = aggr;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		if (!text2.equals("")) {
			this.text2 = text2;
		}
	}

	public String getLess() {
		return less;
	}

	public void setLess(String less) {
		this.less = less;
	}

	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		if (!text3.equals("")) {
			this.text3 = text3;
		}
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getText4() {
		return text4;
	}

	public void setText4(String text4) {
		if (!text4.equals("")) {
			this.text4 = text4;
		}
	}

	public String getText5() {
		return text5;
	}

	public void setText5(String text5) {
		if (!text5.equals("")) {
			this.text5 = text5;
		}
	}

	public String getBetween1() {
		return between1;
	}

	public void setBetween1(String between1) {
		this.between1 = between1;
	}

	public String getText6() {
		return text6;
	}

	public void setText6(String text6) {
		if (!text6.equals("")) {
			this.text6 = text6;
		}
	}

	public String getText7() {
		return text7;
	}

	public void setText7(String text7) {
		if (!text7.equals("")) {
			this.text7 = text7;
		}
	}

	public String getBetween2() {
		return between2;
	}

	public void setBetween2(String between2) {
		this.between2 = between2;
	}

}
