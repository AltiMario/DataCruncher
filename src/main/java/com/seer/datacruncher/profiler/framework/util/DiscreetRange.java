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

package com.seer.datacruncher.profiler.framework.util;


import java.util.Vector;

public class DiscreetRange
{
  public static boolean isMatch(Vector<Object> disObj, Object obj)
  {
    if ((obj == null) || (disObj == null) || (disObj.size() == 0))
      return false;
    return disObj.contains(obj);
  }

  public static Vector<Object> matchedSet(Vector<Object> disObj, Vector<Object> inputObj, boolean match)
  {
    Vector subSet = new Vector();
    int i = 0;
    while (i < inputObj.size()) {
      if ((isMatch(disObj, inputObj.elementAt(i))) && 
        (match)) {
        subSet.add(inputObj.elementAt(i));
      }
      if ((!isMatch(disObj, inputObj.elementAt(i))) && 
        (!match)) {
        subSet.add(inputObj.elementAt(i));
      }
    }
    return subSet;
  }

  public static Vector<Object[]> matchedSetArray(Vector<Object> disObj, Vector<Object[]> inputObj, boolean match, int index)
  {
    Vector subSet = new Vector();
    int i = 0;
    while (i < inputObj.size()) {
      Object[] objArray = (Object[])inputObj.elementAt(i);
      if ((isMatch(disObj, objArray[index])) && 
        (match)) {
        subSet.add(objArray);
      }
      if ((!isMatch(disObj, objArray[index])) && 
        (!match)) {
        subSet.add(objArray);
      }
    }
    return subSet;
  }

  public static Vector<String> tokenizeText(String text, String token) {
    if ((token == null) || (text == null) || ("".equals(text)) || ("".equals(token)))
      return null;
    String[] tokenA = text.trim().split(token);
    int i = 0;
    Vector vec = new Vector();
    while (i < tokenA.length)
      vec.add(tokenA[(i++)]);
    return vec;
  }
}