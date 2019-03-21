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

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.MaskFormatter;

public class FormatCheck {

	public FormatCheck() {
	}

	public static Double parseNumber(Object value, Object pattern[]) {
		Double d = null;
		if (value instanceof Double)
			return d = (Double) value;
		DecimalFormat format = new DecimalFormat();
		for (int i = 0; i < pattern.length; i++)
			try {
				format.applyPattern(pattern[i].toString());
				d = new Double(format.parse(value.toString()).doubleValue());
				if (d != null)
					break;
			} catch (Exception exception) {
			}

		return d;
	}

	public static Date parseDate(Object value, Object pattern[]) {
		Date d = null;
		if (value instanceof Date)
			return d = (Date) value;
		SimpleDateFormat format = new SimpleDateFormat();
		for (int i = 0; i < pattern.length; i++)
			try {
				format.applyPattern(pattern[i].toString());
				d = format.parse(value.toString(), new ParsePosition(0));
				if (d != null)
					break;
			} catch (Exception exception) {
			}

		return d;
	}

	public static Object parseString(String value, Object pattern[]) {
		MaskFormatter format = new MaskFormatter();
		format.setValueContainsLiteralCharacters(false);
		Object d = null;
		for (int i = 0; i < pattern.length; i++)
			try {
				format.setMask(pattern[i].toString());
				d = format.stringToValue(value);
				if (d != null)
					break;
			} catch (Exception exception) {
			}

		return d;
	}

	public static StringBuffer toFormat(String value, String mask) {
		if (value == null)
			return null;
		StringBuffer output = new StringBuffer();
		int maskIndex = 0;
		for (int i = 0; maskIndex < mask.length() && i < value.length(); i++) {
			char c_m = mask.charAt(maskIndex);
			if (c_m == '#' && Character.isDigit(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'U' && Character.isLetter(value.charAt(i)))
				output.append(Character.toUpperCase(value.charAt(i)));
			else if (c_m == 'L' && Character.isLetter(value.charAt(i)))
				output.append(Character.toLowerCase(value.charAt(i)));
			else if (c_m == '?' && Character.isLetter(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'A'
					&& (Character.isLetter(value.charAt(i)) || Character
							.isDigit(value.charAt(i))))
				output.append(value.charAt(i));
			else if (c_m == '*')
				output.append(value.charAt(i));
			else if (c_m == 'H'
					&& (Character.isDigit(value.charAt(i))
							|| value.charAt(i) == 'a' || value.charAt(i) == 'A'
							|| value.charAt(i) == 'b' || value.charAt(i) == 'B'
							|| value.charAt(i) == 'c' || value.charAt(i) == 'C'
							|| value.charAt(i) == 'd' || value.charAt(i) == 'D'
							|| value.charAt(i) == 'e' || value.charAt(i) == 'E'
							|| value.charAt(i) == 'f' || value.charAt(i) == 'F'))
				output.append(value.charAt(i));
			else if (c_m == '\'' && value.length() > i + 1) {
				i++;
				maskIndex++;
				output.append(value.charAt(i));
			} else {
				output.append(mask.charAt(maskIndex));
				i--;
			}
			maskIndex++;
		}

		return output;
	}

	public static StringBuffer phoneFormat(String value, String mask) {
		if (value == null)
			return null;
		StringBuffer output = new StringBuffer();
		int maskIndex = mask.length() - 1;
		for (int i = value.length() - 1; maskIndex >= 0 && i >= 0; i--) {
			char c_m = mask.charAt(maskIndex);
			if (c_m == '#' && Character.isDigit(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'U' && Character.isLetter(value.charAt(i)))
				output.append(Character.toUpperCase(value.charAt(i)));
			else if (c_m == 'L' && Character.isLetter(value.charAt(i)))
				output.append(Character.toLowerCase(value.charAt(i)));
			else if (c_m == '?' && Character.isLetter(value.charAt(i)))
				output.append(value.charAt(i));
			else if (c_m == 'A'
					&& (Character.isLetter(value.charAt(i)) || Character
							.isDigit(value.charAt(i))))
				output.append(value.charAt(i));
			else if (c_m == '*')
				output.append(value.charAt(i));
			else if (c_m == 'H'
					&& (Character.isDigit(value.charAt(i))
							|| value.charAt(i) == 'a' || value.charAt(i) == 'A'
							|| value.charAt(i) == 'b' || value.charAt(i) == 'B'
							|| value.charAt(i) == 'c' || value.charAt(i) == 'C'
							|| value.charAt(i) == 'd' || value.charAt(i) == 'D'
							|| value.charAt(i) == 'e' || value.charAt(i) == 'E'
							|| value.charAt(i) == 'f' || value.charAt(i) == 'F')) {
				output.append(value.charAt(i));
			} else {
				output.append(mask.charAt(maskIndex));
				i++;
			}
			maskIndex--;
		}

		if (maskIndex == -1)
			return output.reverse();
		for (; maskIndex >= 0; maskIndex--) {
			char c_m = mask.charAt(maskIndex);
			if (c_m == '#' || c_m == 'U' || c_m == 'L' || c_m == '?'
					|| c_m == 'A' || c_m == '*' || c_m == 'H')
				output.append('0');
			else
				output.append(c_m);
		}

		return output.reverse();
	}

	public static Number validateNumber(String format, String number) {
		DecimalFormat form = null;
		Number v = null;
		try {
			form = new DecimalFormat(format.trim());
			v = form.parse(number.trim());
		} catch (Exception p_e) {
			System.out.println((new StringBuilder("Format Error:")).append(
					p_e.getMessage()).toString());
			return null;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Number");
			return v;
		} else {
			return v;
		}
	}

	public static Date validateDate(String format, String date) {
		SimpleDateFormat form = null;
		Date v = null;
		try {
			form = new SimpleDateFormat(format.trim());
			v = form.parse(date.trim());
		} catch (Exception p_e) {
			System.out.println((new StringBuilder("Format Error:")).append(
					p_e.getMessage()).toString());
			return null;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Date");
			return v;
		} else {
			return v;
		}
	}

	public static Object validateString(String format, String str) {
		MaskFormatter form = null;
		Object v = null;
		try {
			form = new MaskFormatter(format.trim());
			v = form.stringToValue(str.trim());
		} catch (Exception p_e) {
			System.out.println((new StringBuilder("Format Error:")).append(
					p_e.getMessage()).toString());
			return v;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse String");
			return v;
		} else {
			return v;
		}
	}

	public static Object validatePhone(String format, String phone) {
		MaskFormatter form = null;
		Object v = null;
		try {
			form = new MaskFormatter(format.trim());
			v = form.stringToValue(phone.trim());
		} catch (Exception p_e) {
			System.out.println((new StringBuilder("Format Error:")).append(
					p_e.getMessage()).toString());
			return v;
		}
		if (v == null) {
			System.out.println("Format Error: Could Not Parse Phone");
			return v;
		} else {
			return v;
		}
	}
}
