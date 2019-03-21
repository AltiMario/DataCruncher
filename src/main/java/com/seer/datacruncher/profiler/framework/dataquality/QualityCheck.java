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

package com.seer.datacruncher.profiler.framework.dataquality;

import com.sun.rowset.JdbcRowSetImpl;
import com.seer.datacruncher.profiler.dto.TableGridDTO;
import com.seer.datacruncher.profiler.framework.rdbms.JDBCRowset;
import com.seer.datacruncher.profiler.framework.rdbms.SqlType;
import com.seer.datacruncher.profiler.framework.util.StringCaseFormatUtil;
import org.apache.lucene.queryParser.QueryParser;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class QualityCheck {

	private int matchI;
	private Vector mrowI;
	// private SimilarityCheckLucene _simcheck;

	public QualityCheck() {
		matchI = -1;
	}

	public TableGridDTO searchReplace(JDBCRowset rows, String col,
			Hashtable filter) throws SQLException {
		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		String col_name[] = rows.getColName();
		String colType[] = rows.getColType();
		String add_col[] = new String[col_name.length + 1];
		for (int j = 0; j < col_name.length; j++) {
			if (!col.equals(col_name[j]))
				continue;
			matchI = j;
			break;
		}

		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);
		add_col[0] = (new StringBuilder(String.valueOf(col_name[matchI])))
				.append(" Editable").toString();
		for (int i = 0; i < col_name.length; i++)
			add_col[i + 1] = col_name[i];

		tgDTO.setColumnNames(add_col);
		mrowI = new Vector();
		int rowC = rows.getRowCount();
		int mrowC = 0;
		Vector<Object> row_v = null;
		for (int i = 0; i < rowC; i++) {
			row_v = new Vector<Object>();
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj != null) {
				String value = obj.toString().trim().replaceAll("\\s+", " ");
				String valueTok[] = value.split(" ");
				Enumeration en = filter.keys();
				while (en.hasMoreElements()) {
					String key = ((String) en.nextElement()).toString();
					boolean matchFound = false;
					for (int j = 0; j < valueTok.length; j++) {
						try {
							if (Pattern.matches(key, valueTok[j])) {
								String newvalue = (String) filter.get(key);
								valueTok[j] = newvalue;
								matchFound = true;
							}
							continue;
						} catch (PatternSyntaxException pe) {
							System.out.println((new StringBuilder(
									"\n Pattern Compile Exception:")).append(
									pe.getMessage()).toString());
						}
						break;
					}

					if (!matchFound)
						continue;
					try {
						String newValue = "";
						for (int j = 0; j < valueTok.length; j++) {
							if (!newValue.equals(""))
								newValue = (new StringBuilder(
										String.valueOf(newValue))).append(" ")
										.toString();
							newValue = (new StringBuilder(
									String.valueOf(newValue))).append(
									valueTok[j]).toString();
						}

						try {
							if (metaType.toUpperCase().contains("NUMBER"))
								replace = Double.valueOf(Double
										.parseDouble(newValue));
							else if (metaType.toUpperCase().contains("DATE"))
								replace = (new SimpleDateFormat("dd-MM-yyyy"))
										.parse(newValue);
							else
								replace = new String(newValue);
						} catch (Exception exp) {
							System.out
									.println((new StringBuilder(
											"\n WANING: Could not Parse Input String:"))
											.append(newValue).toString());
						}
						Object objA[] = rows.getRow(i + 1);
						Object add_obj[] = new Object[objA.length + 1];
						add_obj[0] = replace;
						row_v.add(add_obj[0]);
						for (int k = 0; k < objA.length; k++) {
							add_obj[k + 1] = objA[k];
							row_v.add(add_obj[k + 1]);
						}
						rowValues.add(row_v);

						mrowI.add(mrowC++, Integer.valueOf(i + 1));

						break;
					} catch (SQLException se) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(se.getMessage()).toString());
					} catch (Exception ex) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(ex.getMessage()).toString());
						ex.printStackTrace();
					}
				}
			}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO nullReplace(JDBCRowset rows, String col,
			String replaceWith) throws SQLException {
		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		String col_name[] = rows.getColName();
		String colType[] = rows.getColType();
		String add_col[] = new String[col_name.length + 1];
		for (int j = 0; j < col_name.length; j++) {
			if (!col.equals(col_name[j]))
				continue;
			matchI = j;
			break;
		}

		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);
		try {
			if (metaType.toUpperCase().contains("NUMBER"))
				replace = Double.valueOf(Double.parseDouble(replaceWith));
			else if (metaType.toUpperCase().contains("DATE"))
				replace = (new SimpleDateFormat("dd-MM-yyyy"))
						.parse(replaceWith);
			else
				replace = new String(replaceWith);
		} catch (Exception exp) {
			System.out.println((new StringBuilder(
					"\n WANING: Could not Parse Input String:")).append(
					replaceWith).toString());
		}
		add_col[0] = (new StringBuilder(String.valueOf(col_name[matchI])))
				.append(" Editable").toString();
		for (int i = 0; i < col_name.length; i++)
			add_col[i + 1] = col_name[i];

		tgDTO.setColumnNames(add_col);
		mrowI = new Vector();
		Vector<Object> row_v = null;
		int rowC = rows.getRowCount();
		int mrowC = 0;
		for (int i = 0; i < rowC; i++) {
			row_v = new Vector<Object>();
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj == null || "".equals(obj.toString()))
				try {
					Object objA[] = rows.getRow(i + 1);
					Object add_obj[] = new Object[objA.length + 1];
					add_obj[0] = replace;
					row_v.add(add_obj[0]);
					for (int k = 0; k < objA.length; k++) {
						add_obj[k + 1] = objA[k];
						row_v.add(add_obj[k + 1]);
					}
					rowValues.add(row_v);
					mrowI.add(mrowC++, Integer.valueOf(i + 1));
				} catch (SQLException se) {
					System.out.println((new StringBuilder("\n Exception :"))
							.append(se.getMessage()).toString());
				} catch (Exception ex) {
					System.out.println((new StringBuilder("\n Exception :"))
							.append(ex.getMessage()).toString());
					ex.printStackTrace();
				}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO patternMatch(JDBCRowset rows, String col, String type,
			Object pattern[], boolean isMatch) throws SQLException {
		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		String col_name[] = rows.getColName();
		String add_col[] = new String[col_name.length + 1];
		for (int j = 0; j < col_name.length; j++) {
			if (!col.equals(col_name[j]))
				continue;
			matchI = j;
			break;
		}

		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		add_col[0] = (new StringBuilder(String.valueOf(col_name[matchI])))
				.append(" Editable").toString();
		for (int i = 0; i < col_name.length; i++)
			add_col[i + 1] = col_name[i];

		tgDTO.setColumnNames(add_col);
		mrowI = new Vector();
		int rowC = rows.getRowCount();
		int mrowC = 0;
		Vector<Object> row_v = null;
		for (int i = 0; i < rowC; i++) {
			row_v = new Vector<Object>();
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj != null) {
				boolean isObjMatch = false;
				if (type.equals("Number")) {
					Double d = FormatCheck.parseNumber(obj.toString(), pattern);
					if (d != null && isMatch)
						isObjMatch = true;
					if (d == null && !isMatch)
						isObjMatch = true;
				} else if (type.equals("Date")) {
					Date d = FormatCheck.parseDate(obj.toString(), pattern);
					if (d != null && isMatch)
						isObjMatch = true;
					if (d == null && !isMatch)
						isObjMatch = true;
				} else {
					Object d = FormatCheck.parseString(obj.toString(), pattern);
					if (d != null && isMatch)
						isObjMatch = true;
					if (d == null && !isMatch)
						isObjMatch = true;
				}
				if (isObjMatch)
					try {
						Object objA[] = rows.getRow(i + 1);
						Object add_obj[] = new Object[objA.length + 1];
						add_obj[0] = obj;
						row_v.add(add_obj[0]);
						for (int k = 0; k < objA.length; k++) {
							add_obj[k + 1] = objA[k];
							row_v.add(add_obj[k + 1]);
						}

						rowValues.add(row_v);
						mrowI.add(mrowC++, Integer.valueOf(i + 1));
					} catch (SQLException se) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(se.getMessage()).toString());
					} catch (Exception ex) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(ex.getMessage()).toString());
						ex.printStackTrace();
					}
			}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO caseFormat(JDBCRowset rows, String col, int formatType,
			char defChar) throws SQLException {

		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		String col_name[] = rows.getColName();

		String add_col[] = new String[col_name.length + 1];
		for (int j = 0; j < col_name.length; j++) {
			if (!col.equals(col_name[j]))
				continue;
			matchI = j;
			break;
		}

		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		add_col[0] = (new StringBuilder(String.valueOf(col_name[matchI])))
				.append(" Editable").toString();
		for (int i = 0; i < col_name.length; i++)
			add_col[i + 1] = col_name[i];

		tgDTO.setColumnNames(add_col);
		mrowI = new Vector();
		Vector<Object> row_v = null;
		int rowC = rows.getRowCount();
		int mrowC = 0;
		for (int i = 0; i < rowC; i++) {
			row_v = new Vector<Object>();
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj != null) {
				String searchFormat = obj.toString();
				String valueFormat = null;
				boolean isObjMatch = true;
				switch (formatType) {
				case 1: // '\001'
					if (!StringCaseFormatUtil.isUpperCase(searchFormat)) {
						valueFormat = StringCaseFormatUtil
								.toUpperCase(searchFormat);
						isObjMatch = false;
					}
					break;

				case 2: // '\002'
					if (!StringCaseFormatUtil.isLowerCase(searchFormat)) {
						valueFormat = StringCaseFormatUtil
								.toLowerCase(searchFormat);
						isObjMatch = false;
					}
					break;

				case 3: // '\003'
					if (!StringCaseFormatUtil.isTitleCase(searchFormat)) {
						valueFormat = StringCaseFormatUtil
								.toTitleCase(searchFormat);
						isObjMatch = false;
					}
					break;

				case 4: // '\004'
					if (!StringCaseFormatUtil.isSentenceCase(searchFormat,
							defChar)) {
						valueFormat = StringCaseFormatUtil.toSentenceCase(
								searchFormat, defChar);
						isObjMatch = false;
					}
					break;
				}
				if (!isObjMatch)
					try {
						String value = valueFormat;
						Object objA[] = rows.getRow(i + 1);
						Object add_obj[] = new Object[objA.length + 1];
						add_obj[0] = value;
						row_v.add(add_obj[0]);
						for (int k = 0; k < objA.length; k++) {
							add_obj[k + 1] = objA[k];
							row_v.add(add_obj[k + 1]);
						}

						rowValues.add(row_v);
						mrowI.add(mrowC++, Integer.valueOf(i + 1));
					} catch (SQLException se) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(se.getMessage()).toString());
					} catch (Exception ex) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(ex.getMessage()).toString());
						ex.printStackTrace();
					}
			}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO discreetSearch(JDBCRowset rows, String col,
			Vector token, boolean match) throws SQLException {
		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		String col_name[] = rows.getColName();
		String colType[] = rows.getColType();
		String add_col[] = new String[col_name.length + 1];
		for (int j = 0; j < col_name.length; j++) {
			if (!col.equals(col_name[j]))
				continue;
			matchI = j;
			break;
		}

		if (matchI < 0) {
			System.out
					.println("Selected Column is Not matching Database Columns");
			return null;
		}
		Object replace = null;
		String metaType = SqlType.getMetaTypeName(colType[matchI]);
		add_col[0] = (new StringBuilder(String.valueOf(col_name[matchI])))
				.append(" Editable").toString();
		for (int i = 0; i < col_name.length; i++)
			add_col[i + 1] = col_name[i];

		tgDTO.setColumnNames(add_col);

		mrowI = new Vector();
		int rowC = rows.getRowCount();
		int mrowC = 0;
		Vector<Object> row_v = null;
		for (int i = 0; i < rowC; i++) {
			row_v = new Vector<Object>();
			Object obj = rows.getObject(i + 1, matchI + 1);
			if (obj != null) {
				String value = obj.toString();
				int tokenI = 0;
				boolean matchFound = false;
				while (tokenI < token.size()) {
					String key = (String) token.elementAt(tokenI++);
					try {
						if (!Pattern.matches(key, value))
							continue;
						matchFound = true;
						break;
					} catch (PatternSyntaxException pe) {
						System.out.println((new StringBuilder(
								"\n Pattern Compile Exception:")).append(
								pe.getMessage()).toString());
					}
				}
				if (matchFound == match)
					try {
						String newValue = value;
						try {
							if (metaType.toUpperCase().contains("NUMBER"))
								replace = Double.valueOf(Double
										.parseDouble(newValue));
							else if (metaType.toUpperCase().contains("DATE"))
								replace = (new SimpleDateFormat("dd-MM-yyyy"))
										.parse(newValue);
							else
								replace = new String(newValue);
						} catch (Exception exp) {
							System.out
									.println((new StringBuilder(
											"\n WANING: Could not Parse Input String:"))
											.append(newValue).toString());
						}
						Object objA[] = rows.getRow(i + 1);
						Object add_obj[] = new Object[objA.length + 1];
						add_obj[0] = replace;
						row_v.add(add_obj[0]);
						for (int k = 0; k < objA.length; k++) {
							add_obj[k + 1] = objA[k];
							row_v.add(add_obj[k + 1]);
						}

						rowValues.add(row_v);

						mrowI.add(mrowC++, Integer.valueOf(i + 1));
					} catch (SQLException se) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(se.getMessage()).toString());
					} catch (Exception ex) {
						System.out
								.println((new StringBuilder("\n Exception :"))
										.append(ex.getMessage()).toString());
						ex.printStackTrace();
					}
			}
		}
		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO createIncExc(JDBCRowset rowSet) throws SQLException {

		TableGridDTO tgDTO = new TableGridDTO();
		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
		JdbcRowSetImpl rows = rowSet.getRowset();
		ResultSetMetaData rsmd = rows.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		String[] col_name = new String[numberOfColumns];
		int[] col_type = new int[numberOfColumns];
		for (int i = 1; i < numberOfColumns + 1; i++) {
			col_name[i - 1] = rsmd.getColumnName(i);
			col_type[i - 1] = rsmd.getColumnType(i);
		}
		tgDTO.setColumnNames(col_name);
		int fromIndex = 1;
		int toIndex = 100;
		rows.absolute(fromIndex);
		rows.previous();
		int counter = 0;

		for (; rows.next() && toIndex >= fromIndex + counter; counter++) {
			Vector row_v = new Vector();
			for (int i = 1; i < col_name.length + 1; i++)
				switch (col_type[i - 1]) {
				case -6:
				case 4: // '\004'
				case 5: // '\005'
					row_v.add(i - 1, new Integer(rows.getInt(i)));
					break;

				case -5:
				case 2: // '\002'
				case 3: // '\003'
				case 7: // '\007'
				case 8: // '\b'
					row_v.add(i - 1, new Double(rows.getDouble(i)));
					break;

				case 6: // '\006'
					row_v.add(i - 1, new Float(rows.getFloat(i)));
					break;

				case 2005:
					row_v.add(i - 1, rows.getClob(i));
					break;

				case 2004:
					row_v.add(i - 1, rows.getBlob(i));
					break;

				case -7:
				case 16: // '\020'
					row_v.add(i - 1, new Boolean(rows.getBoolean(i)));
					break;

				case 91: // '['
					row_v.add(i - 1, rows.getDate(i));
					break;

				case 92: // '\\'
					row_v.add(i - 1, rows.getTime(i));
					break;

				case 93: // ']'
					row_v.add(i - 1, rows.getTimestamp(i));
					break;

				case 2003:
					row_v.add(i - 1, rows.getArray(i));
					break;

				case 2006:
					row_v.add(i - 1, rows.getRef(i));
					break;

				case -2:
					row_v.add(i - 1, Byte.valueOf(rows.getByte(i)));
					break;

				case -4:
				case -3:
					row_v.add(i - 1, rows.getBytes(i));
					break;

				case 0: // '\0'
				case 70: // 'F'
				case 1111:
				case 2000:
				case 2001:
				case 2002:
					row_v.add(i - 1, rows.getObject(i));
					break;

				default:
					row_v.add(i - 1, rows.getString(i));
					break;
				}
			rowValues.add(row_v);

		}

		tgDTO.setRowValues(rowValues);
		return tgDTO;
	}

	public TableGridDTO searchTableIndex(JDBCRowset rowSet,int sType[], int sImp[], String skiptf[]) {
//		TableGridDTO tgDTO = new TableGridDTO();
//		List<Vector<Object>> rowValues = new ArrayList<Vector<Object>>();
//		_simcheck = new SimilarityCheckLucene(rowSet);
//		_simcheck.makeIndex();
//	    if (!this._simcheck.openIndex()) {
//	      return null;
//	    }
//	    String colName[] = rowSet.getColName();
//	    String[] newColN = new String[colName.length + 1];
//	    boolean isRowSet = true;
//	    int rowC = rowSet.getRowCount();
//	    newColN[0] = "Delete Editable";
//	    for (int i = 0; i < colName.length; i++) {
//	      newColN[(i + 1)] = colName[i];
//	    }
//	    tgDTO.setColumnNames(newColN);
//	    //this.outputRT = new ReportTable(newColN, false, true);
//	    Vector skipVC = new Vector();
//	    Hashtable parentMap = new Hashtable();
//
//	    for (int i = 0; i < rowC; i++)
//	      if ((isRowSet) || (!skipVC.contains(Integer.valueOf(i))))
//	      {
//	        if ((!isRowSet) || (!skipVC.contains(Integer.valueOf(i + 1))))
//	        {
//	          String queryString = getQString(rowSet, i, sType, sImp, skiptf, colName);
//	          if ((queryString != null) && (!queryString.equals("")))
//	          {
//	            Query qry = this._simcheck.parseQuery(queryString);
//	            Hits hit = this._simcheck.searchIndex(qry);
//	            if ((hit != null) && (hit.length() > 1))
//	            {
//	              for (int j = 0; j < hit.length(); j++) {
//	                try {
//	                  Document doc = hit.doc(j);
//	                  String rowid = doc.get("at__rowid__");
////	                  parentMap.put(Integer.valueOf(this.outputRT.table.getRowCount()), 
////	                    Integer.valueOf(Integer.parseInt(rowid)));
//	                  Object[] row = (Object[])null;
//	                  if (!isRowSet){
//	                    //row = this._rt.getRow(Integer.parseInt(rowid));
//	                  }
//	                  else
//	                    row = rowSet.getRow(Integer.parseInt(rowid));
//	                  Object[] newRow = new Object[row.length + 1];
//	                  Vector row_v = new Vector();
//	                  boolean del = false;
//	                  newRow[0] = Boolean.valueOf(del);
//	                  row_v.add(0, Boolean.valueOf(del));
//	                  for (int k = 0; k < row.length; k++){
//	                    newRow[(k + 1)] = row[k];
//	                    row_v.add((k + 1), row[k]);
//	                  }
//	                  rowValues.add(row_v);
//	                  //this.outputRT.addFillRow(newRow);
//	                  skipVC.add(Integer.valueOf(Integer.parseInt(rowid)));
//	                } catch (Exception e) {
//	                  System.out.println("\n " + e.getMessage());
//	                  System.out.println("\n Error: Can not open Document");
//	                }
//	              }
//	              //this.outputRT.addNullRow();
//	              Vector row_v = new Vector();
//	              row_v.add(0, Boolean.valueOf(false));
//	              for (int k = 0; k < colName.length; k++){	                  
//	                    row_v.add((k + 1), "");
//	              }
//	              rowValues.add(row_v);
//	            }
//	          }
//	        }
//	      }
//	    this._simcheck.closeSeachIndex();
//	    tgDTO.setRowValues(rowValues);
//		return tgDTO;
		return null;
	  }
	
	private String getQString(JDBCRowset _rows, int rowid, int sType[], int sImp[], String skiptf[], String colName[]) {
	    String queryString = "";
	    Object[] row = (Object[])null;
	   
	    
      try {
        row = _rows.getRow(rowid + 1);
      } catch (Exception e) {
        System.out.println("\n Row Fetch Error:" + e.getMessage());
        e.printStackTrace();
      }
	    

	    if (row == null) {
	      return "";
	    }
	    for (int j = 0; j < row.length; j++) {
	      int type = sType[j];
	      int imp = sImp[j] + 1;
	      String multiWordQuery = "";
	      String skipText = skiptf[j];
	      boolean skip = true;
	      String[] skiptoken = (String[])null;

	      if ((skipText != null) && (!skipText.equals(""))) {
	        skip = false;
	        skipText = skipText.trim().replaceAll(",", " ");
	        skipText = skipText.trim().replaceAll("\\s+", " ");
	        skiptoken = skipText.split(" ");
	      }

	      if (row[j] != null) {
	        String term = row[j].toString();
	        term.trim();
	        boolean matchF = false;
	        switch (type) {
	        case 0:
	          break;
	        case 1:
	          
	          if (!skip) {
	            for (int k = 0; k < skiptoken.length; k++)
	              if (skiptoken[k].compareToIgnoreCase(term) == 0) {
	                matchF = true;
	                break;
	              }
	          }
	          if (!matchF) break;
	          break;
	        case 2:
	        case 3:
	          term = term.replaceAll(",", " ");
	          term = term.replaceAll("\\s+", " ");
	          String[] token = term.split(" ");
	          String newTerm = "";
	          for (int i = 0; i < token.length; i++)
	            if ((token[i] != null) && (!"".equals(token[i])))
	            {
	              //boolean matchF = false;
	              if (!skip) {
	                for (int k = 0; k < skiptoken.length; k++)
	                  if (skiptoken[k].compareToIgnoreCase(token[i]) == 0) {
	                    matchF = true;
	                    break;
	                  }
	              }
	              if (!matchF)
	              {
	                if ((!newTerm.equals("")) && (type == 3))
	                  newTerm = newTerm + " AND ";
	                if ((!newTerm.equals("")) && (type == 2))
	                  newTerm = newTerm + " OR ";
	                newTerm = newTerm + colName[j] + ":" + 
	                  QueryParser.escape(token[i]) + "~^" + imp + 
	                  " ";
	              }
	            }
	          multiWordQuery = newTerm;
	          break;
	        case 4:
	          //boolean matchF = false;
	          if (!skip) {
	            for (int k = 0; k < skiptoken.length; k++)
	              if (skiptoken[k].compareToIgnoreCase(term) == 0) {
	                matchF = true;
	                break;
	              }
	          }
	          if (matchF) {
	            continue;
	          }
	          if (term.length() > 4) {
	            term = term.substring(0, 4);
	          }
	          break;
	        case 5:
	          //boolean matchF = false;
	          if (!skip) {
	            for (int k = 0; k < skiptoken.length; k++)
	              if (skiptoken[k].compareToIgnoreCase(term) == 0) {
	                matchF = true;
	                break;
	              }
	          }
	          if (matchF) {
	            continue;
	          }
	          if (term.length() > 4) {
	            term = term.substring(term.length() - 4, term.length());
	          }
	          break;
	        }

	        if (!queryString.equals(""))
	          queryString = queryString + " AND ";
	        if ((type == 2) || (type == 3))
	          queryString = queryString + multiWordQuery;
	        else if (type == 1)
	          queryString = queryString + colName[j] + ":\"" + term + "\"^" + imp;
	        else if (type == 4)
	          queryString = queryString + colName[j] + ":" + 
	            QueryParser.escape(term.trim()) + "*^" + imp;
	        else if (type == 5)
	          queryString = queryString + colName[j] + ":*" + 
	            QueryParser.escape(term.trim()) + "^" + imp;
	      }
	      else if ((type != 0) && (imp > 1)) {
	        return "";
	      }
	    }
	    return queryString;
	  }
	public void setrowIndex(Vector mrowI) {
		this.mrowI = mrowI;
	}

	public Vector getrowIndex() {
		return mrowI;
	}

	public int getColMatchIndex() {
		return matchI;
	}
}
