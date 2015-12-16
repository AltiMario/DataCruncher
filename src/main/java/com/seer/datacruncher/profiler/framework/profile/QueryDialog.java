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

package com.seer.datacruncher.profiler.framework.profile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.framework.ndtable.ResultSetToGrid;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class QueryDialog {

	protected Vector cb_v;
	protected Vector wt_v;
	protected Vector tt_v;
	protected Vector aoc_v;
	protected Vector t_type_v;
	protected int index;
	private int capacity;

	private Vector __type_v;
	private Vector dateVar[];
	private String t_cond;
	private String a_cond;

	private int LEVEL;
	private String _table;
	private String _column;
	public String cond;
	public int response;
	private String rowCount;
	private TableGridDTO tableGridDTO;

	// public ReportTable _rt;
	public QueryDialog(int i, String s, Vector avector[]) {
		cb_v = new Vector();
		wt_v = new Vector();
		tt_v = new Vector();
		aoc_v = new Vector();
		t_type_v = new Vector();
		index = 0;
		capacity = 0;
		dateVar = new Vector[2];
		response = 0;
		rowCount = "0";

		LEVEL = i;
		_table = s;

		Enumeration enumeration = avector[0].elements();
		int j = 0;

		__type_v = avector[1];

	}

	public void executeAction(String queryString) throws Exception {
		String s = "validate";
		if (s.equals("validate")) {
			if (LEVEL == 1) {
				QueryBuilder querybuilder = new QueryBuilder(
						RdbmsConnection.getHValue("Database_DSN"), _table,
						RdbmsConnection.getDBType());
				t_cond = querybuilder.get_tableCount_query();
				a_cond = querybuilder.get_tableAll_query();
			}
			if (LEVEL == 2) {
				QueryBuilder querybuilder = new QueryBuilder(
						RdbmsConnection.getHValue("Database_DSN"), _table,
						_column, RdbmsConnection.getDBType());
				t_cond = querybuilder.get_tableCount_query();
				a_cond = "";
			}
			String s1 = t_cond;
			dateVar[0] = new Vector();
			dateVar[1] = new Vector();
			cond = queryString;
			if (cond == null)
				return;
			s1 = cond.equals("") ? s1 : (new StringBuilder(String.valueOf(s1)))
					.append(" WHERE ").append(cond).toString();
			runValidation(s1);
		}
		s = "apply";
		if (s.equals("apply")) {
			String s2 = a_cond;
			if (!"".equals(s2)) {
				s2 = cond.equals("") ? s2 : (new StringBuilder(
						String.valueOf(s2))).append(" WHERE ").append(cond)
						.toString();
				runApply(s2);
			}

			response = 1;
			if (LEVEL == 2)
				QueryBuilder.setDateCondition(dateVar);
		}
	}

	private void runValidation(String s) throws Exception {
		PreparedStatement preparedstatement;
		RdbmsConnection.openConn();
		preparedstatement = RdbmsConnection.createQuery(s);

		try {
			for (int i = 0; i < dateVar[0].size(); i++) {
				String s1 = (String) dateVar[1].get(i);
				if (s1.compareToIgnoreCase("time") == 0)
					preparedstatement.setTime(i + 1, new Time(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("date") == 0)
					preparedstatement.setDate(i + 1, new java.sql.Date(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("timestamp") == 0)
					preparedstatement.setTimestamp(i + 1, new Timestamp(
							((Date) dateVar[0].get(i)).getTime()));
			}

			ResultSet resultset;
			for (resultset = preparedstatement.executeQuery(); resultset.next();)
				rowCount = resultset.getString("row_count");

			resultset.close();
			RdbmsConnection.closeConn();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
		}
		return;
	}

	private String condString() {
		String s2 = "";
		int i = 0;
		String s3 = "";
		for (int j = 0; j < index; j++) {
			String s4 = ((JTextField) tt_v.get(j)).getText();
			if (RdbmsConnection.getDBType().compareToIgnoreCase("mysql") != 0)
				s4 = (new StringBuilder("\"")).append(s4).append("\"")
						.toString();
			JComboBox jcombobox = (JComboBox) cb_v.get(j);
			int k = jcombobox.getSelectedIndex();
			if (k > 1) {
				String s5 = ((JFormattedTextField) wt_v.get(j)).getText()
						.trim();
				s5 = s5.replace('"', '\'');
				String s6 = jcombobox.getSelectedItem().toString();
				String s7 = ((JTextField) t_type_v.get(j)).getText();
				String s1;
				switch (k) {
				case 2: // '\002'
				case 3: // '\003'
					s1 = (new StringBuilder(String.valueOf(s4))).append(s6)
							.toString();
					break;

				case 4: // '\004'
				case 5: // '\005'
					if (s5 == null || s5.equals("")) {
						JOptionPane
								.showMessageDialog(
										null,
										"Varibale can not be null or Empty \n  Enter %Str% or %str or Str% ",
										"Variable Format Error", 0);
						return null;
					}
					if (!s5.startsWith("%") && !s5.endsWith("%"))
						s5 = (new StringBuilder(String.valueOf(s5)))
								.append("%").toString();
					if (!s5.startsWith("'"))
						s5 = (new StringBuilder("'")).append(s5).append("'")
								.toString();
					s1 = (new StringBuilder(String.valueOf(s4))).append(s6)
							.append(s5).toString();
					break;

				default:
					if (s5 == null || s5.equals("")) {
						JOptionPane.showMessageDialog(null,
								"Varibale can not be null or Empty ",
								"Variable Format Error", 0);
						return null;
					}
					if (s7.compareToIgnoreCase("time") == 0
							|| s7.compareToIgnoreCase("date") == 0
							|| s7.compareToIgnoreCase("timestamp") == 0
							|| s7.toUpperCase().contains("DATE")) {
						SimpleDateFormat simpledateformat = new SimpleDateFormat(
								"dd/MM/yyyy hh:mm:ss");
						simpledateformat.setLenient(true);
						Date date = simpledateformat.parse(s5,
								new ParsePosition(0));
						if (date == null) {

							JOptionPane
									.showMessageDialog(
											null,
											(new StringBuilder(
													" Could not Parse"))
													.append(s5)
													.append("\nEnter date in dd/MM/yyyy hh:mm:ss Format")
													.toString(),
											"Date Format error", 0);
							return null;
						}
						dateVar[0].add(i, date);
						dateVar[1].add(i, s7);
						i++;
						s5 = "?";
					} else if (s7.indexOf("Char") != -1 && !s5.startsWith("'"))
						s5 = (new StringBuilder("'")).append(s5).append("'")
								.toString();
					s1 = (new StringBuilder(String.valueOf(s4))).append(s6)
							.append(s5).toString();
					break;
				}
				if (!s3.equals(""))
					s1 = (new StringBuilder(String.valueOf(s3))).append(s1)
							.toString();
				s3 = ((JComboBox) aoc_v.get(j)).getSelectedItem().toString();
				s2 = (new StringBuilder(String.valueOf(s2))).append(s1)
						.toString();
			}
		}

		return s2;
	}

	private void runApply(String s) throws Exception {
		PreparedStatement preparedstatement;
		RdbmsConnection.openConn();
		preparedstatement = RdbmsConnection.createQuery(s);
		if (preparedstatement == null) {

			return;
		}
		try {
			for (int i = 0; i < dateVar[0].size(); i++) {
				String s1 = (String) dateVar[1].get(i);
				if (s1.compareToIgnoreCase("time") == 0)
					preparedstatement.setTime(i + 1, new Time(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("date") == 0)
					preparedstatement.setDate(i + 1, new java.sql.Date(
							((Date) dateVar[0].get(i)).getTime()));
				if (s1.compareToIgnoreCase("timestamp") == 0)
					preparedstatement.setTimestamp(i + 1, new Timestamp(
							((Date) dateVar[0].get(i)).getTime()));
			}

			// preparedstatement.setMaxRows(((Number)
			// tf.getValue()).intValue());
			ResultSet resultSet = preparedstatement.executeQuery();
			tableGridDTO = ResultSetToGrid.generateTableGrid(resultSet);
			// _rt = SqlTablePanel.getSQLValue(resultset, true);
			resultSet.close();
			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception) {

		}
		return;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public TableGridDTO getTableGridDTO() {
		return tableGridDTO;
	}

	public void setTableGridDTO(TableGridDTO tableGridDTO) {
		this.tableGridDTO = tableGridDTO;
	}

}
