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

import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.framework.ndtable.ResultSetToGrid;
import com.seer.datacruncher.profiler.framework.profile.InterTableInfo;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareTableBL {
	private String str1;
	private String str2;
	private String str3;
	private String str4;
	private String table1;
	private String table2;
	private String col1;
	private String col2;
	private byte multiple;
	private int mX;
	private String rb;
	
	private String ft;
	private long lg[];
	private boolean isEditable;
	private String link;

	public Map<String, List<String>> buttonClicked() {
		// if (rowsetPanel != null)
		// rowsetPanel.rowset.close();
		str1 = (String) table1;
		str2 = (String) table2;
		str3 = (String) col1;
		str4 = (String) col2;
		multiple = 0;
		mX = 0;
		if (rb.equals("0"))
			multiple = 0;
		else if (rb.equals("1"))
			multiple = 1;
		else if (rb.equals("2"))
			multiple = 2;
		else if (rb.equals("3")) {
			multiple = 3;
			mX = Integer.parseInt(ft);
			if (mX < 2)
				System.out.println("Match Value can not be < 2");
		}
		if (str1.equals(str2) && str3.equals(str4)) {
			System.out.println("Identical Fields to Compare");			
		}
		String st[] = InterTableInfo.getMatchCount(str1, str3, str2, str4,
				multiple, mX);
		lg = convertLong(st);
		Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
		List<String> tableA = new ArrayList<String>();
		tableA.add(st[0]);
		tableA.add(st[1]);
		tableA.add((lg[2] - lg[0]) + "");
		
		List<String> tableB = new ArrayList<String>();
		tableB.add(st[3]);
		tableB.add(st[4]);
		tableB.add((lg[5] - lg[3]) + "");
		dataMap.put("Table A", tableA);
		dataMap.put("Table B", tableB);
		//[['Total',122.0],['Unique',122.0],['Repeat',0.0],['Pattern',0.0],['Null',0.0]]
		
		// up1.setPreferredSize(new Dimension(50, 275));
		// up2.setPreferredSize(new Dimension(50, 275));
//		if(lg[1] + lg[2] <= 0L && lg[4] + lg[5] <= 0L){
//			System.out.println("Both Tables have 0 records. ");
//		}
//		else if(lg[1] + lg[2] > lg[4] + lg[5]){
//			
//		}
		// up2.setPreferredSize(new Dimension(50, (int)(100L + (175L * (lg[4] +
		// lg[5])) / (lg[1] + lg[2]))));
		// else
		// up1.setPreferredSize(new Dimension(50, (int)(100L + (175L * (lg[1] +
		// lg[2])) / (lg[4] + lg[5]))));
		// up2.repaint();
		// up2.revalidate();
		// up1.repaint();
		// up1.revalidate();
		// up1.setTitle("");
		// up1.setValues(new String[] {
		// st[0], st[1], st[2]
		// });
		// up1.setLeft(true);
		// up1.drawUniverseChart();
		// up2.setTitle("");
		// up2.setValues(new String[] {
		// st[3], st[4], st[5]
		// });
		// up2.drawUniverseChart();
		// ainit = true;
		// tagP.repaint();
		// ll1.setVisible(true);
		// ll2.setVisible(true);
		// ll3.setVisible(true);
		// ll4.setVisible(true);
		// ll5.setVisible(true);
		// ll6.setVisible(true);
		// rowsetP.removeAll();
		// tp.revalidate();
		// tp.repaint();
		
		return dataMap;
	}

	public TableGridDTO linkClicked() {
		TableGridDTO tableGridDTO = null;
		QueryBuilder qb;
		qb = new QueryBuilder(RdbmsConnection.getHValue("Database_DSN"), table1,
				col1, RdbmsConnection.getDBType());
		qb.setCTableCol(table2, col2);
		try {
			
			String s1 = link;
			String query = "";
			boolean isLeft = true;
			if (s1 != null
					&& s1.equals("TAM"))
				query = qb.get_match_value(multiple, mX, true, true);
			else if (s1 != null
					&& s1.equals("TANM"))
				query = qb.get_match_value(multiple, mX, false, true);
			else if (s1 != null
					&& s1.equals("TBNM")) {
				isLeft = false;
				query = qb.get_match_value(multiple, mX, false, false);
			} else {
				isLeft = false;
				query = qb.get_match_value(multiple, mX, true, false);
			}
			String pc = "";
			if (!isEditable) {
				RdbmsConnection.openConn();
				ResultSet resultset = RdbmsConnection.runQuery(query);
				tableGridDTO = ResultSetToGrid.generateTableGrid(resultset);
				// ReportTable _rt = SqlTablePanel.getSQLValue(resultset, true);
				// resultset.close();
				// RdbmsConnection.closeConn();
				// int cc = _rt.table.getColumnCount();
				// if(str1.equals(str2) || isLeft)
				// {
				// for(int i = 0; i < cc; i++)
				// {
				// String cn = _rt.table.getColumnName(i);
				// if(!cn.equals(str3))
				// continue;
				// pc = str3;
				// _rt.table.moveColumn(i, 0);
				// break;
				// }
				//
				// }
				// if(str1.equals(str2) || !isLeft)
				// {
				// for(int i = 0; i < cc; i++)
				// {
				// String cn = _rt.table.getColumnName(i);
				// if(!cn.equals(str4))
				// continue;
				// pc = str4;
				// if(str1.equals(str2))
				// _rt.table.moveColumn(i, 1);
				// else
				// _rt.table.moveColumn(i, 0);
				// break;
				// }
				//
				// }
				// rowsetP.removeAll();
				// rowsetP.add(_rt);
				// tp.revalidate();
				// tp.repaint();
				// } else
				// {
				// if(rowsetPanel != null)
				// rowsetPanel.rowset.close();
				// rowsetPanel = new JDBCRowsetPanel(query, true, pc);
				// rowsetP.removeAll();
				// rowsetP.add(rowsetPanel);
				// tp.revalidate();
				// tp.repaint();
			}

		} catch (Exception sql_e) {
			System.out.println((new StringBuilder("\n Exception ")).append(
					sql_e.getMessage()).toString());
		}
		return tableGridDTO;
	}

	public static long[] convertLong(String st[]) {
		long lg[] = new long[st.length];
		for (int i = 0; i < st.length; i++)
			if (st[i] == null)
				lg[i] = 0L;
			else
				try {
					lg[i] = Math.round(Double.parseDouble(st[i]));
				} catch (NumberFormatException e) {
					lg[i] = 0L;
					System.out.println((new StringBuilder(
							"\n Parsing Exception:")).append(st[i]).toString());
				}

		return lg;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public String getStr3() {
		return str3;
	}

	public void setStr3(String str3) {
		this.str3 = str3;
	}

	public String getStr4() {
		return str4;
	}

	public void setStr4(String str4) {
		this.str4 = str4;
	}

	public String getTable1() {
		return table1;
	}

	public void setTable1(String table1) {
		this.table1 = table1;
	}

	public String getTable2() {
		return table2;
	}

	public void setTable2(String table2) {
		this.table2 = table2;
	}

	public String getCol1() {
		return col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	public byte getMultiple() {
		return multiple;
	}

	public void setMultiple(byte multiple) {
		this.multiple = multiple;
	}

	public int getmX() {
		return mX;
	}

	public void setmX(int mX) {
		this.mX = mX;
	}

	

	public String getFt() {
		return ft;
	}

	public void setFt(String ft) {
		this.ft = ft;
	}

	public long[] getLg() {
		return lg;
	}

	public void setLg(long[] lg) {
		this.lg = lg;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getRb() {
		return rb;
	}

	public void setRb(String rb) {
		this.rb = rb;
	}
	
	
}
