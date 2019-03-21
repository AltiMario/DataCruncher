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

package com.seer.datacruncher.profiler.framework.util;


import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;

public class KeyValueParser
{
  private static int token;
  private static int key = 0;
  private static String str_key = ""; private static String str_value = "";

  public static Hashtable<String, String> parseFile(String filename) {
    Hashtable _hash = new Hashtable();
    try
    {
      FileReader rd = new FileReader(filename);
      StreamTokenizer st = new StreamTokenizer(rd);

      st.wordChars(95, 95);

      st.ordinaryChars(48, 57);
      st.wordChars(48, 57);

      st.slashSlashComments(true);
      st.slashStarComments(true);
      st.commentChar(35);

      while ((KeyValueParser.token = st.nextToken()) != -1)
      {
        double num;
        String squoteVal;
        switch (token)
        {
        case -2:
          num = st.nval;
          break;
        case -3:
          if (key == 0) {
            if (str_key.compareTo("") == 0)
              str_key = st.sval;
            else
              str_key = str_key + " " + st.sval;
          }
          break;
        case 34:
          if (key == 1) {
            str_value = st.sval;
            key -= 1;
          }
          break;
        case 39:
          squoteVal = st.sval;
          break;
        case 10:
          break;
        case -1:
          break;
        default:
          char ch = (char)st.ttype;
          if (key == 0) {
            if (ch == '=')
              key = 1;
            else
              str_key += ch;
          }
          else str_value += ch;

          break;
        }

        if ((str_key != "") && (str_value != "")) {
          _hash.put(str_key, str_value);
          str_key = "";
          str_value = "";
        }
      }
      rd.close();
      return _hash;
    } catch (IOException e) {
      System.out.println("\n IO Exception happened:" + filename);
      System.out.println(e.getMessage());
    }

    return _hash;
  }

  public static void main(String[] args) {
    Hashtable key_val = parseFile(args[0]);
    Enumeration e = key_val.keys();
    while (e.hasMoreElements()) {
      String key = ((String)e.nextElement()).toString();
      System.out.println(key + " = " + (String)key_val.get(key));
    }
  }
}