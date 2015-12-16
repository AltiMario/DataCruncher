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

package com.seer.datacruncher.spring;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

public class HttpUtils {
	
    private static final int BYTE_MAX_VALUE = 255;
    private static final int BYTE_SIZE = BYTE_MAX_VALUE + 1;
	
    /**
     * Encode content disposition for download. !!Not tested with Opera!!
     * 
     * @param request the request
     * @param fileName name of the file
     * @param isInline true/false
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static String encodeContentDispositionForDownload(HttpServletRequest request, String fileName, boolean isInline)
			throws IOException {
		if (fileName == null) {
			throw new IllegalArgumentException("Value of the \"filename\" parameter cannot be null!");
		}
		String contentDisposition = isInline ? "inline; " : "attachment; ";
		String agent = request.getHeader("USER-AGENT").toLowerCase();
		if (agent != null && agent.indexOf("opera") == -1 && agent.indexOf("msie") != -1) {
			// IE
			contentDisposition += "filename=\"" + toHexString(fileName) + "\"";
		} else {
			// Firefox and others
			contentDisposition += "filename=\"" + MimeUtility.encodeText(fileName, "utf8", "B") + "\"";
		}
		return contentDisposition;
	}
    
    /**
     * String to hex converter.
     * 
     * @param s - string to convert
     * @return string
     * @throws UnsupportedEncodingException ex
     */
	public static String toHexString(final String s) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= BYTE_MAX_VALUE && !Character.isWhitespace(c)) {
				sb.append(c);
			} else {
				byte[] b;
				b = Character.toString(c).getBytes("utf8");
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += BYTE_SIZE;
					}
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

}
