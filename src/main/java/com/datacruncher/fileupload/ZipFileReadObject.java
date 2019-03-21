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

package com.datacruncher.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class ZipFileReadObject implements FileReadObject {
	private static Logger logger = Logger.getLogger(ZipFileReadObject.class);
	@Override
	public String parseStream(long schemaId, InputStream ios) {
		StringBuffer sb = new StringBuffer();
		try{
			ZipInputStream inStream = new ZipInputStream(ios);
			ZipEntry entry ;
			while (!(isStreamClose(inStream)) && (entry = inStream.getNextEntry()) != null) {
				if(! entry.isDirectory()){
					FileReadObject fileReadObject = FileReadObjectFactory.getFileReadObject(entry.getName());				
					sb.append(fileReadObject.parseStream(schemaId, inStream)+"\n");
				}
			}
		}catch(IOException ex){
			logger.error("Error occured during fetch records from ZIP file.", ex);
			return "";
		}
		return sb.toString();
	}
	/**
	 * This method will use to know about the IO Stream is closed or Open.
	 * There some issue in ZipInputStream.getNextEntry() some time its throws Exception of 'stream close' instead of return null.
	 * So this is a method will get private field [closed] of InputStream class so program could know that stream is open or closed.
	 * @return 
	 * It will return true if stream is closed else return false
	 */
	private boolean isStreamClose(ZipInputStream inStream) {
		try {
			Class c = inStream.getClass();
			Field in;
			in = c.getDeclaredField("closed");
			in.setAccessible(true);
			Boolean inReader = (Boolean) in.get(inStream);
			return inReader;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
}
