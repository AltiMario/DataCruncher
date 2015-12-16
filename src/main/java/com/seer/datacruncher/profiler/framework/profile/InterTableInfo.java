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


import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import com.seer.datacruncher.profiler.framework.ndtable.ReportTableModel;
import com.seer.datacruncher.profiler.framework.rdbms.JDBCRowset;
import com.seer.datacruncher.profiler.framework.rdbms.QueryBuilder;
import com.seer.datacruncher.profiler.framework.rdbms.RdbmsConnection;


public class InterTableInfo
{
  public static String[] getMatchCount(String table1, String col1, String table2, String col2, byte multiple, int mX)
  {
    QueryBuilder qb = new QueryBuilder(
      RdbmsConnection.getHValue("Database_DSN"), table1, col1, 
      RdbmsConnection.getDBType());
    qb.setCTableCol(table2, col2);
    String q1 = qb.get_match_count(multiple, mX);
    String[] st = new String[6];
    try
    {
      RdbmsConnection.openConn();

      ResultSet rs = RdbmsConnection.runQuery(q1);
      while (rs.next()) {
        String row_count = rs.getString("row_count");
        st[0] = row_count;
        String row_sum = rs.getString("row_sum");
        st[3] = row_sum;
      }

      rs.close();

      q1 = qb.get_nullCount_query_w("Null");
      rs = RdbmsConnection.runQuery(q1);
      while (rs.next()) {
        String null_count = rs.getString("equal_count");
        st[1] = null_count;
      }
      rs.close();

      q1 = qb.count_query_w(true, "row_count");
      rs = RdbmsConnection.runQuery(q1);
      while (rs.next()) {
        String row_count = rs.getString("row_count");
        st[2] = row_count;
      }
      rs.close();

      qb = new QueryBuilder(RdbmsConnection.getHValue("Database_DSN"), table2, 
        col2, RdbmsConnection.getDBType());

      q1 = qb.get_nullCount_query_w("Null");
      rs = RdbmsConnection.runQuery(q1);
      while (rs.next()) {
        String null_count = rs.getString("equal_count");
        st[4] = null_count;
      }
      rs.close();

      q1 = qb.count_query_w(false, "row_count");
      rs = RdbmsConnection.runQuery(q1);
      while (rs.next()) {
        String row_count = rs.getString("row_count");
        st[5] = row_count;
      }
      rs.close();

      RdbmsConnection.closeConn();
    } catch (SQLException e) {
      System.out.println("\n match count execution failed");
      System.out.println(e.getMessage());
    }
    return st;
  }

  public static void loadQuery(final String[] query, final ReportTableModel _rt, final Vector<String> unique_table_s, final Hashtable<String, Integer> _ht)
  {
    final int count = _rt.getModel().getRowCount();

    Thread[] tid = new Thread[query.length];

    for (int qIndex = 0; qIndex < query.length; qIndex++) {
      final int cIndex = qIndex;
      tid[cIndex] = new Thread(new Runnable() {
        public void run() {
          int fcount = 0;
          String[] tbl = (String[])null;
          String[] col = (String[])null;
          try {
            JDBCRowset rs = new JDBCRowset(query[cIndex], 0, true);

            tbl = rs.getTableName();

            col = rs.getColName();

            rs.moveToFirst();

            for (int c = 0; c < count; c++) {
              Object[] obj = new Object[col.length];
              for (int i = 0; i < col.length; i++) {
                Integer index = (Integer)_ht.get(
                  (String)unique_table_s
                  .get(cIndex) + col[i]);
                if (index != null)
                {
                  obj[i] = _rt.getModel().getValueAt(c, 
                    index.intValue());
                }
              }
              try { rs.insertRow(obj);
              } catch (SQLException sql_e) {
                System.out.println("\n Row Id:" + (c + 1) + 
                  " Error-" + sql_e.getMessage() + 
                  " For Table: " + 
                  (String)unique_table_s.get(cIndex));
                fcount++;
              }
            }

            rs.close();
          } catch (SQLException e) {
            System.out.println("\n Error-" + e.getMessage() + 
              " For Table: " + (String)unique_table_s.get(cIndex));
          }
          System.out.println("\n " + (count - fcount) + " of Total " + 
            count + " Rows Inserted Successfully in table :" + 
            (String)unique_table_s.get(cIndex));
        }
      });
      tid[cIndex].start();
    }

    for (int i = 0; i < query.length; i++)
      try {
        tid[i].join();
      } catch (Exception e) {
        System.out.println("\n Thread Error:" + e.getMessage());
      }
  }

  public static void synchQuery(final String[] query, ReportTableModel _rt, final Vector<String> table_s, final Vector<String> column_s, final Hashtable<String, Integer> _ht, final String[] queryString)
  {
    final int count = _rt.getModel().getRowCount();
    final Object[][] stored = new Object[query.length][count];
    final int[] cI = new int[query.length];
    Thread[] tid = new Thread[query.length];

    for (int qindex = 0; qindex < query.length; qindex++) {
      final int cIndex = qindex;
      tid[cIndex] = new Thread(new Runnable() {
        public void run() {
          String tbl = (String)table_s.get(cIndex);
          String col = (String)column_s.get(cIndex);
          String newQuery = query[cIndex];
          Integer tab_index = (Integer)_ht.get(tbl + col);
          if (tab_index == null)
            return;
          if ((queryString[tab_index.intValue()] != null) && 
            (!""
            .equals(queryString[tab_index.intValue()])))
            newQuery = newQuery + " WHERE " + 
              queryString[tab_index.intValue()];
          cI[cIndex] = tab_index.intValue();
          try
          {
            JDBCRowset rs = new JDBCRowset(newQuery, count, false);
            for (int c = 0; c < count; c++)
              stored[cIndex][c] = rs.getObject(c + 1, 1);
            rs.close();
          } catch (SQLException e) {
            System.out.println("\n Error-" + e.getMessage() + 
              " For Table: " + tbl);
          }
        }
      });
      tid[cIndex].start();
    }
    for (int i = 0; i < query.length; i++) {
      try {
        tid[i].join();
      } catch (Exception e) {
        System.out.println("\n Thread Error:" + e.getMessage());
      }
    }
    for (int c = 0; c < count; c++)
      for (int j = 0; j < query.length; j++)
        _rt.getModel().setValueAt(stored[j][c], c, cI[j]);
  }
}