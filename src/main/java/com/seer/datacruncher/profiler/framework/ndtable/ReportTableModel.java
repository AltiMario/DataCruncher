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

package com.seer.datacruncher.profiler.framework.ndtable;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.seer.datacruncher.profiler.framework.rdbms.SqlType;

public class ReportTableModel implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Vector<Object> row_v = new Vector();
	private Vector<Object> column_v = new Vector();
	private int col_size = 0;
	private DefaultTableModel tabModel;
	private boolean _isEditable = false;
	private boolean showClass = false;
	private int[] classType = null;

	public ReportTableModel(String[] col) {
		addColumns(col);
		createTable(false);
	}

	public ReportTableModel(Object[] col) {
		addColumns(col);
		createTable(false);
	}

	public ReportTableModel(Object[] col, boolean isEditable) {
		addColumns(col);
		createTable(isEditable);
	}

	public ReportTableModel(String[] col, boolean isEditable) {
		addColumns(col);
		createTable(isEditable);
	}

	public ReportTableModel(String[] col, boolean isEditable, boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		this.showClass = colClass;
	}

	public ReportTableModel(String[] col, int[] sqlType, boolean isEditable,
			boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		this.showClass = colClass;
		this.classType = sqlType;
	}

	public ReportTableModel(Object[] col, boolean isEditable, boolean colClass) {
		addColumns(col);
		createTable(isEditable);
		this.showClass = colClass;
	}

	public ReportTableModel(String less, String more, String b_less1,
			String b_more1, String b_less2, String b_more2) {
		String[] columnNames = {
				"<html><b><i>Values</i></b></html>",
				"<html><b>Aggregate</i></b></html>",
				"<html><b> &lt;  <i>" + less + "</i></b></html>",
				"<html><b> &gt;  <i>" + more + "</i></b></html>",
				"<html><b><i>" + b_less1 + "</i>&lt;&gt;<i>" + b_more1
						+ "</i></b></html>",
				"<html><b><i>" + b_less2 + "</i>&lt;&gt;<i>" + b_more2
						+ "</i></b></html>" };

		String[][] data = {
				{ "<html><b>COUNT</b></html>", "", "", "", "", "" },
				{ "<html><b>AVG</b></html>", "", "", "", "", "" },
				{ "<html><b>MAX</b></html>", "", "", "", "", "" },
				{ "<html><b>MIN</b></html>", "", "", "", "", "" },
				{ "<html><b>SUM</b></html>", "", "", "", "", "" },
				{ "<html><b>DUPLICATE</b></html>", "", "", "", "", "" } };

		addColumns(columnNames);
		addRows(data);

		createTable(false);
	}

	private void createTable(final boolean isEditable) {
		this._isEditable = isEditable;
		this.tabModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				String colN = getColumnName(col);
				if ((!isEditable) && (!colN.endsWith("Editable"))) {
					return false;
				}
				return true;
			}

			public Class<?> getColumnClass(int col) {
				if (ReportTableModel.this.showClass) {
					if (ReportTableModel.this.classType != null) {
						return SqlType
								.getClass(ReportTableModel.this.classType[col]);
					}
					for (int i = 0; i < getRowCount(); i++)
						if (getValueAt(i, col) != null)
							return getValueAt(i, col).getClass();
					return new Object().getClass();
				}
				return new Object().getClass();
			}
		};
		this.tabModel.setDataVector(this.row_v, this.column_v);
	}

	public void setValueAt(String s, int row, int col) {
		if ((row < 0) || (col < 0))
			return;
		this.tabModel.setValueAt(s, row, col);
	}

	public void setValueAt(Object s, int row, int col) {
		if ((row < 0) || (col < 0))
			return;
		this.tabModel.setValueAt(s, row, col);
	}

	private void addColumns(String[] colName) {
		for (int i = 0; i < colName.length; i++) {
			this.column_v.addElement(colName[i]);
			this.col_size = i;
		}
	}

	private void addColumns(Object[] colName) {
		for (int i = 0; i < colName.length; i++) {
			this.column_v.addElement(colName[i].toString());
			this.col_size = i;
		}
	}

	private void addRows(String[][] rowData) {
		for (int i = 0; i < rowData.length; i++) {
			Vector newRow = new Vector();
			for (int j = 0; j < rowData[i].length; j++)
				newRow.addElement(rowData[i][j]);
			this.row_v.addElement(newRow);
		}
	}

	public void addFillRow(String[] rowset) {
		Vector newRow = new Vector();
		for (int j = 0; j < rowset.length; j++) {
			newRow.addElement(rowset[j]);
		}
		this.row_v.addElement(newRow);
	}

	public void addFillRow(Object[] rowset) {
		Vector newRow = new Vector();
		for (int j = 0; j < rowset.length; j++) {
			newRow.addElement(rowset[j]);
		}
		this.row_v.addElement(newRow);
	}

	public void addFillRow(Vector<?> rowset) {
		this.row_v.addElement(rowset);
	}

	public void addRow() {
		Vector newRow = new Vector();
		for (int j = 0; j < this.col_size; j++) {
			newRow.addElement("");
		}
		this.row_v.addElement(newRow);
	}

	public void addNullRow() {
		Vector newRow = new Vector();
		for (int j = 0; j < this.col_size; j++)
			newRow.addElement(null);
		this.row_v.addElement(newRow);
	}

	public void addColumn(String name) {
		TableColumn col = new TableColumn(this.tabModel.getColumnCount());

		col.setHeaderValue(name);
		this.column_v.addElement(name);
		this.tabModel.addColumn(col);
		this.tabModel.fireTableStructureChanged();
	}

	public void addRows(int startRow, int noOfRows) {
		int col_c = this.tabModel.getColumnCount();
		Object[] row = new Object[col_c];
		for (int i = 0; i < noOfRows; i++)
			this.tabModel.insertRow(startRow, row);
		this.tabModel.fireTableRowsInserted(startRow, noOfRows);
	}

	public void removeRows(int startRow, int noOfRows) {
		for (int i = 0; i < noOfRows; i++)
			this.tabModel.removeRow(startRow);
		this.tabModel.fireTableRowsDeleted(startRow, noOfRows);
	}

	public Object[] copyRow(int startRow) {
		int col_c = this.tabModel.getColumnCount();
		Object[] row = new Object[col_c];
		for (int i = 0; i < col_c; i++)
			row[i] = this.tabModel.getValueAt(startRow, i);
		return row;
	}

	public void pasteRow(int startRow, Vector<Object[]> row) {
		int row_c = this.tabModel.getRowCount();
		int col_c = this.tabModel.getColumnCount();
		int vci = 0;
		int saveR = row_c - (startRow + row.size());
		if (saveR < 0) {
			System.out.println("Not Enough Rows left to paste " + row.size()
					+ " Rows \n Use 'Insert Clip' instead");
			return;
		}

		for (int i = row.size() - 1; i >= 0; i--) {
			Object[] a = (Object[]) row.elementAt(vci++);
			col_c = col_c > a.length ? a.length : col_c;
			for (int j = 0; j < col_c; j++)
				this.tabModel.setValueAt(a[j], startRow + i, j);
		}
	}

	public void pasteRow(int startRow, Object[] row) {
		int row_c = this.tabModel.getRowCount();
		int col_c = this.tabModel.getColumnCount();

		int saveR = row_c - (startRow + 1);
		if (saveR < 0) {
			System.out
					.println("Not Enough Rows left to paste 1 Row \n Use 'Insert Clip' instead");

			return;
		}

		Object[] a = row;
		col_c = col_c > a.length ? a.length : col_c;
		for (int j = 0; j < col_c; j++)
			this.tabModel.setValueAt(a[j], startRow, j);
	}

	public DefaultTableModel getModel() {
		return this.tabModel;
	}

	public boolean isRTEditable() {
		return this._isEditable;
	}

	public static ReportTableModel copyTable(ReportTableModel rpt,
			boolean editable, boolean showClass) {
		if (rpt == null)
			return null;
		int colC = rpt.tabModel.getColumnCount();
		int rowC = rpt.tabModel.getRowCount();
		String[] colName = new String[colC];

		for (int i = 0; i < colC; i++) {
			colName[i] = rpt.tabModel.getColumnName(i);
		}
		ReportTableModel newRT = new ReportTableModel(colName, editable,
				showClass);
		for (int i = 0; i < rowC; i++) {
			newRT.addRow();
			for (int j = 0; j < colC; j++)
				newRT.tabModel.setValueAt(rpt.tabModel.getValueAt(i, j), i, j);
		}
		return newRT;
	}

	public Object[] getRow(int rowIndex) {
		int colC = this.tabModel.getColumnCount();
		Object[] obj = new Object[colC];
		if ((rowIndex < 0) || (rowIndex >= this.tabModel.getRowCount()))
			return obj;
		for (int i = 0; i < colC; i++) {
			obj[i] = this.tabModel.getValueAt(rowIndex, i);
		}
		return obj;
	}

	public void cleanallRow() {
		int i = this.tabModel.getRowCount();
		removeRows(0, i);
	}
}