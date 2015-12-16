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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;

public class FirstInformation {

	private static String minVal = "";
	private static String maxVal = "";

	public FirstInformation() {
	}

	public static Double[] getProfileValues(QueryBuilder querybuilder) {
		String s = querybuilder.count_query_w(false, "row_count");
		String s1 = querybuilder.count_query_w(true, "row_count");
		String s2 = querybuilder.get_nullCount_query_w("Null");
		String s3 = querybuilder.get_zeroCount_query_w("0");
		String s4 = querybuilder.get_zeroCount_query_w("''");
		String s5 = querybuilder.get_pattern_query();
		double d = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		Object obj = null;
		try {
			RdbmsConnection.openConn();
		} catch (SQLException sqlexception) {
			System.out.println("\n Error: Could not open Connection");
			return null;
		}
		try {
			ResultSet resultset;
			for (resultset = RdbmsConnection.runQuery(s); resultset.next();)
				d = resultset.getDouble("row_count");

			resultset.close();
		} catch (SQLException sqlexception1) {
			d = -1D;
		}
		try {
			ResultSet resultset1;
			for (resultset1 = RdbmsConnection.runQuery(s1); resultset1.next();)
				d1 = resultset1.getDouble("row_count");

			resultset1.close();
		} catch (SQLException sqlexception2) {
			d1 = -1D;
		}
		try {
			ResultSet resultset2;
			for (resultset2 = RdbmsConnection.runQuery(s2); resultset2.next();)
				d2 = resultset2.getDouble("equal_count");

			resultset2.close();
		} catch (SQLException sqlexception3) {
			d2 = -1D;
		}
		try {
			ResultSet resultset3;
			for (resultset3 = RdbmsConnection.runQuery(s5); resultset3.next();)
				d5 = resultset3.getDouble("row_count");

			resultset3.close();
		} catch (SQLException sqlexception4) {
			d5 = -1D;
		}
		try {
			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception5) {
			System.out.println("\n Warning: Could not close Connection");
		}
		Double ad[];
		return ad = (new Double[] { d, d1, d1 == -1D ? -1D : d - d1, d5, d2 });
	}

	public static Vector[] getPatternValues(QueryBuilder querybuilder) {
		String s;
		int i;
		Vector avector[];
		s = querybuilder.get_freq_query();
		i = 0;
		avector = new Vector[2];
		avector[0] = new Vector();
		avector[1] = new Vector();
		ResultSet resultset;
		try {
			RdbmsConnection.openConn();
			resultset = RdbmsConnection.runQuery(s);
			if (resultset == null)
				return null;

			while (resultset.next()) {
				double d = resultset.getDouble("row_count");
				if (d < 1.0D)
					break;
				String s1 = resultset.getString("like_wise");
				avector[0].add(i, s1);
				avector[1].add(i, new Double(d));
				i++;
			}
			if (resultset != null)
				resultset.close();
			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception) {
			System.out.println("\n Warning: Could not Get Pattern Information");
			return null;
		}
		return avector;
	}

	public static Vector[] getDistributionValues(QueryBuilder querybuilder) {
		String s;
		String s1;
		int i;
		Vector avector[];
		double d;
		s = querybuilder.get_freq_query_wnull();
		s1 = querybuilder.get_pattern_all_query();
		i = 0;
		avector = new Vector[2];
		avector[0] = new Vector();
		avector[1] = new Vector();
		d = 0.0D;
		try {
			RdbmsConnection.openConn();
			ResultSet resultset;
			for (resultset = RdbmsConnection.runQuery(s1); resultset.next();)
				d = resultset.getDouble("row_count");

			resultset.close();
			if (d <= 0.0D)
				return null;

			double d1 = d / 6D;
			double d2 = 0.0D;
			double d3 = 0.0D;
			ResultSet resultset1;
			for (resultset1 = RdbmsConnection.runQuery(s); resultset1.next();) {
				d--;
				if (d1 < 1.0D) {
					double d4 = resultset1.getDouble("row_count");
					String s2 = resultset1.getString("like_wise");
					avector[0].add(i, s2);
					avector[1].add(i, new Double(d4));
					if (i == 0)
						setMinVal(s2);
					if (d == 0.0D)
						setMaxVal(s2);
					i++;
				} else {
					double d5 = resultset1.getDouble("row_count");
					d2 += d5;
					if (i == 0 && d3 == 0.0D)
						setMinVal(resultset1.getString("like_wise"));
					if (d3 < d1 && d != 0.0D) {
						d3++;
					} else {
						String s3 = resultset1.getString("like_wise");
						avector[0].add(i, s3);
						avector[1].add(i, new Double(d2));
						if (d == 0.0D)
							setMaxVal(s3);
						d2 = 0.0D;
						d3 = 0.0D;
						i++;
					}
				}
			}

			resultset1.close();
			RdbmsConnection.closeConn();
		} catch (SQLException sqlexception) {
			System.out
					.println("\n Error: Could not Get Distribution Information");
			System.out.println((new StringBuilder("\n ")).append(
					sqlexception.getMessage()).toString());
			return null;
		}
		return avector;
	}

	public static void setMaxVal(String max) {
		maxVal = max;
	}

	public static String getMaxVal() {
		return maxVal;
	}

	public static void setMinVal(String min) {
		minVal = min;
	}

	public static String getMinVal() {
		return minVal;
	}

}
