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

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ChartUtil {

	public static String getChartDataForPieChart(Vector avector[]) {

		if(avector != null && avector.length > 0) {
			Object[] fields = avector[0].toArray();
			Object[] values = avector[1].toArray();
			return generateDataSet(fields, values);
		}
		return "";
	}

	public static String getChartDataForBarChart(Double values[]) {
		String fields[] = (new String[] { "Total", "Unique", "Repeat",
				"Pattern", "Null" });
		return generateDataSet(fields, values);
	}
	
	public static String getBinAnalysisBarChart(String fields[], Double values[]) {		
		return generateDataSet(fields, values);
	}

	@SuppressWarnings("unchecked")
	public static String getChartDataForPatternChart(Vector avector[]) {

		if (avector == null)
			return null;
		byte byte0 = 25;
		if (avector[1].size() > byte0) {
			double d = 0.0D;
			double d1 = 0.0D;
			int i;
			for (; avector[0].size() > byte0; avector[1].removeElementAt(i)) {
				d1++;
				i = avector[0].size() - 1;
				d += ((Double) avector[1].elementAt(i)).doubleValue();
				avector[0].removeElementAt(i);
			}

			avector[0].add(byte0,
					(new StringBuilder("Others(")).append(Math.round(d1))
							.append(")").toString());
			avector[1].add(byte0, new Double(d));
		}
		Object[] fields = avector[0].toArray();
		Object[] values = avector[1].toArray();
		return generateDataSet(fields, values);
	}

	private static String generateDataSet(Object[] fields, Object[] values) {
		StringBuffer data = new StringBuffer();
		data.append("[");
		for (int index = 0; index < fields.length; index++) {
			data.append("[");
			data.append("'");
			data.append(fields[index]);
			data.append("'");
			data.append(",");
			data.append(values[index]);
			data.append("]");
			if (index != (fields.length - 1)) {
				data.append(",");
			}
		}
		data.append("]");
		return data.toString();
	}
	
	public static String generateDataSetFromMap(Map<String, List<String>> dataMap) {
		StringBuffer data = new StringBuffer();
		int index = 0;
		data.append("[");
		for(String table : dataMap.keySet()){
			List<String> values = dataMap.get(table);
			data.append("[");
			data.append("'");
			data.append(table);
			data.append("'");
			data.append(",");
			for(int i=0;i<values.size();i++){
				data.append(values.get(i));
				
				if (i == (values.size() - 1)) {
					data.append("]");
				}
				else{
					data.append(",");
				}
			}
			//data.append("]");
			if (index != (dataMap.size() - 1)) {
				data.append(",");
			}
			index++;
		}
		data.append("]");
		return data.toString();
	}
}
