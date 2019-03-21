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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.framework.ndtable.ResultSetToGrid;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class MiscBL {

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
	public String tC;

	public MiscBL(int i, String s, int index) {
		cb_v = new Vector();
		wt_v = new Vector();
		tt_v = new Vector();
		aoc_v = new Vector();
		t_type_v = new Vector();
		this.index = index;
		capacity = 0;
		dateVar = new Vector[2];
		response = 0;
		tC = "0";
		LEVEL = i;
		_table = s;

		int j = 0;

		// __type_v = avector[1];

	}

	public void initMiscBL(String columnNames, String conditionTypes,
			String conditionValues, String columnTypes, String condition) {
		String arr[] = columnNames.split(",");
		tt_v.addAll(Arrays.asList(arr));

		arr = columnTypes.split(",");
		t_type_v.addAll(Arrays.asList(arr));

		arr = conditionTypes.split(",");
		aoc_v.addAll(Arrays.asList(arr));

		arr = conditionValues.split(",");
		wt_v.addAll(Arrays.asList(arr));

		arr = condition.split(",");
		cb_v.addAll(Arrays.asList(arr));

	}

	public String validateCondtion() {		
		String s = generateQuery(false);
		runValidation(s);
		return tC;
	}

	private String generateQuery(boolean isApply){		
		if (LEVEL == 1) {
			QueryBuilder querybuilder = new QueryBuilder(
					RdbmsConnection.getHValue("Database_DSN"), _table,
					RdbmsConnection.getDBType());
			t_cond = querybuilder.get_tableCount_query();
			a_cond = querybuilder.get_tableAll_query();
		}
		if (LEVEL == 2) {
			QueryBuilder querybuilder = new QueryBuilder(
					RdbmsConnection.getHValue("Database_DSN"), _table, _column,
					RdbmsConnection.getDBType());
			t_cond = querybuilder.get_tableCount_query();
			a_cond = "";
		}
		String s1 = t_cond;
		if(isApply){
			s1 = a_cond;
		}
		dateVar[0] = new Vector();
		dateVar[1] = new Vector();
		cond = condString();
		if (cond == null) {
			return null;
		}
		s1 = cond.equals("") ? s1 : (new StringBuilder(String.valueOf(s1)))
				.append(" WHERE ").append(cond).toString();
		
		return s1;
	}
	private String condString() {
		Map<Integer, String> conMap = new HashMap<Integer, String>();
		conMap.put(2, " IS NULL");
		conMap.put(3, " IS NOT NULL");
		conMap.put(4, " LIKE");
		conMap.put(5, " NOT LIKE");
		conMap.put(6, " =");
		conMap.put(7, " <>");

		Map<String, String> conTypeMap = new HashMap<String, String>();
		conTypeMap.put("OR", " OR ");
		conTypeMap.put("AND", " AND ");

		String s2 = "";
		int i = 0;
		String s3 = "";
		for (int j = 0; j < index; j++) {
			String s4 = (tt_v.get(j)).toString();
			if (RdbmsConnection.getDBType().compareToIgnoreCase("mysql") != 0)
				s4 = (new StringBuilder("\"")).append(s4).append("\"")
						.toString();
			String conType = cb_v.get(j).toString();
			int k = Integer.parseInt(conType);
			if (k > 1) {
				String s5 = (wt_v.size() > j) ? wt_v.get(j).toString().trim()
						: "";

				s5 = s5.replace('"', '\'');
				String s6 = conMap.get(k);
				String s7 = t_type_v.get(j).toString();
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
						System.out
								.println("Varibale can not be null or Empty \n  Enter %Str% or %str or Str% ");
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
						System.out
								.println("Varibale can not be null or Empty ");
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

							System.out
									.println((new StringBuilder(
											" Could not Parse"))
											.append(s5)
											.append("\nEnter date in dd/MM/yyyy hh:mm:ss Format")
											.toString());
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
				s3 = conTypeMap.get(aoc_v.get(j).toString());
				s2 = (new StringBuilder(String.valueOf(s2))).append(s1)
						.toString();
			}
		}

		return s2;
	}

	private void runValidation(String s) {
		try {
			PreparedStatement preparedstatement;
			RdbmsConnection.openConn();
			preparedstatement = RdbmsConnection.createQuery(s);
			if (preparedstatement == null) {
				System.out.println("\n ERROR:Validation Query Null");
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

				ResultSet resultset;
				for (resultset = preparedstatement.executeQuery(); resultset
						.next(); System.out.println((new StringBuilder(
						" Query Success\n")).append(tC).append(" Rows Found")
						.toString())) {
					tC = resultset.getString("row_count");
				}

				resultset.close();
				RdbmsConnection.closeConn();
			} catch (SQLException sqlexception) {
				System.out.println("\n SQL Exception in Adhoc Query");
				System.out.println(sqlexception.getMessage());
				tC = null;
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TableGridDTO applyCondition() {
		String s = generateQuery(true);		
		TableGridDTO tgDTO = runApply(s);
		response = 1;
		if (LEVEL == 2)
			QueryBuilder.setDateCondition(dateVar);
		return tgDTO;
	}

	private TableGridDTO runApply(String s) { 
		TableGridDTO tableGridDTO = null;
		try {
			PreparedStatement preparedstatement;
			RdbmsConnection.openConn();
			preparedstatement = RdbmsConnection.createQuery(s);
			if (preparedstatement == null) {
				System.out.println("\n ERROR:Bin Query Null");				
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
				ResultSet resultset = preparedstatement.executeQuery();
				tableGridDTO = ResultSetToGrid.generateTableGrid(resultset);
			
				resultset.close();
				RdbmsConnection.closeConn();
			} catch (SQLException sqlexception) {
				System.out.println("\n SQL Exception in Applying Adhoc Query");
				JOptionPane.showMessageDialog(null, sqlexception.getMessage(),
						"Error Message", 0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableGridDTO;
	}
}
