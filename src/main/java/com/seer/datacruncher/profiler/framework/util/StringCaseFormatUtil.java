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

public class StringCaseFormatUtil {
	public static String toUpperCase(String s) {
		return s.toUpperCase();
	}

	public static String toLowerCase(String s) {
		return s.toLowerCase();
	}

	public static String toTitleCase(String s) {
		s = s.toLowerCase();
		int strl = s.length();
		char[] holder = new char[strl];
		boolean titleActive = true;

		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if ((titleActive) || (i == 0)) {
				nextC = Character.toTitleCase(nextC);
				titleActive = false;
			}
			if (Character.isWhitespace(nextC)) {
				titleActive = true;
			}
			holder[i] = nextC;
			i++;
		}

		return new String(holder);
	}

	public static String toSentenceCase(String s, char endOfLineSym) {
		s = s.toLowerCase();

		int strl = s.length();
		char[] holder = new char[strl];
		boolean sentenceActive = true;

		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (((sentenceActive) || (i == 0))
					&& (Character.isLetterOrDigit(nextC))) {
				nextC = Character.toUpperCase(nextC);
				sentenceActive = false;
			}

			if ((Character.getType(nextC) == 13)
					|| (Character.getType(nextC) == 14)
					|| (nextC == endOfLineSym)) {
				sentenceActive = true;
			}
			holder[i] = nextC;
			i++;
		}

		return new String(holder);
	}

	public static boolean isUpperCase(String s) {
		int strl = s.length();
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (!Character.isUpperCase(nextC))
				return false;
			i++;
		}
		return true;
	}

	public static boolean isLowerCase(String s) {
		int strl = s.length();
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if (!Character.isLowerCase(nextC))
				return false;
			i++;
		}
		return true;
	}

	public static boolean isTitleCase(String s) {
		int strl = s.length();
		int i = 0;
		boolean titleActive = true;
		while (i < strl) {
			char nextC = s.charAt(i);
			if ((!titleActive)
					&& ((Character.isTitleCase(nextC)) || (Character
							.isUpperCase(nextC)))) {
				return false;
			}
			if ((titleActive) || (i == 0)) {
				if ((!Character.isTitleCase(nextC))
						&& (!Character.isUpperCase(nextC)))
					return false;
				titleActive = false;
			}
			if (Character.isWhitespace(nextC))
				titleActive = true;
			i++;
		}
		return true;
	}

	public static boolean isSentenceCase(String s, char endOfLineSym) {
		int strl = s.length();
		boolean sentenceActive = true;
		int i = 0;
		while (i < strl) {
			char nextC = s.charAt(i);
			if ((!sentenceActive) && (Character.isLetterOrDigit(nextC))
					&& (Character.isUpperCase(nextC))) {
				return false;
			}

			if (((sentenceActive) || (i == 0))
					&& (Character.isLetterOrDigit(nextC))) {
				if (!Character.isUpperCase(nextC))
					return false;
				sentenceActive = false;
			}

			if ((Character.getType(nextC) == 13)
					|| (Character.getType(nextC) == 14)
					|| (nextC == endOfLineSym))
				sentenceActive = true;
			i++;
		}
		return true;
	}
}