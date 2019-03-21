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

package com.seer.datacruncher.profiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.codehaus.jackson.map.ObjectMapper;

import com.seer.datacruncher.profiler.dto.GridColumnDTO;
import com.seer.datacruncher.profiler.dto.GridFieldDTO;
import com.seer.datacruncher.profiler.dto.TableGridDTO;

public class GridUtil {
	private String columnNames;
	private String data;
	private String fieldNames;
	
	public void generateGridData(TableGridDTO tableGridDTO, boolean textField, List<String> listPrimaryKeys) {
		try {
			String columnNames[] = tableGridDTO.getColumnNames();
			List<Vector<Object>> rowValues = tableGridDTO.getRowValues();
			List<GridColumnDTO> columns = new ArrayList<GridColumnDTO>();
			List<GridFieldDTO> fields = new ArrayList<GridFieldDTO>();
			StringBuffer data = new StringBuffer();
			int counter = 1;
			data.append("[");
			boolean generateColumnsAndFields = true;
			for (Vector<Object> singleRow : rowValues) {
				data.append("[");
				GridColumnDTO gridColumn = null;
				GridFieldDTO gridField = null;
				for (int ind = 0; ind < columnNames.length; ind++) {
					if (generateColumnsAndFields) {
						gridColumn = new GridColumnDTO();
						gridField = new GridFieldDTO();
						gridField.setName(columnNames[ind]);
						gridColumn.setText(columnNames[ind]);
						gridColumn.setDataIndex((columnNames[ind]));
						if (textField && listPrimaryKeys != null && !listPrimaryKeys.contains(gridField.getName())) {
							gridColumn.setEditor("textfield");
						}
						gridColumn.setSortable(true);
						//gridColumn.setWidth((100 / columnNames.length) + "%");
						gridColumn.setWidth("80px");
						columns.add(gridColumn);
						fields.add(gridField);
					}
					data.append("\"");
					if(listPrimaryKeys != null && listPrimaryKeys.contains(columnNames[ind])) {
						data.append((int)Double.parseDouble(singleRow.get(ind).toString()));
					} else {
						
						String strData = singleRow.get(ind) == null ? "" : singleRow.get(ind).toString();
						if(strData.indexOf("\"") != -1) {
							strData = strData.replaceAll("\"", "&#34;");
						}
						
						if(strData.indexOf("\\") != -1) {
							strData = strData.replaceAll("\\\\", "&#92;");
						}
						data.append(strData);
					}
					data.append("\"");
					if (ind != (columnNames.length - 1)) {
						data.append(",");
					} else {
						data.append("]");
						if (counter != rowValues.size()) {
							data.append(",");
						}

					}

				}
				generateColumnsAndFields = false;
				counter = counter + 1;
			}
			data.append("]");
			ObjectMapper mapper = new ObjectMapper();
			setColumnNames(mapper.writeValueAsString(columns));
			setFieldNames(mapper.writeValueAsString(fields));
			setData(data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateGridDataWithDataIndex(TableGridDTO tableGridDTO) {
		
		if (tableGridDTO != null) {

			String columnNames[] = tableGridDTO.getColumnNames();
			List<Vector<Object>> rowValues = tableGridDTO.getRowValues();

			StringBuffer data = new StringBuffer();
			int counter = 1;
			data.append("[");
			for (Vector<Object> singleRow : rowValues) {
				data.append("{");

				for (int ind = 0; ind < columnNames.length; ind++) {
					data.append("'");
					data.append(columnNames[ind]);
					data.append("'");
					data.append(":");

					data.append("\"");
					data.append(singleRow.get(ind));
					data.append("\"");
					if (ind != (columnNames.length - 1)) {
						data.append(",");
					} else {
						data.append("}");
						if (counter != rowValues.size()) {
							data.append(",");
						}

					}

				}

				counter = counter + 1;
			}
			data.append("]");
			setData(data.toString());
		}
	}

	public String getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String columnNames) {
		this.columnNames = columnNames;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String fieldNames) {
		this.fieldNames = fieldNames;
	}

}
