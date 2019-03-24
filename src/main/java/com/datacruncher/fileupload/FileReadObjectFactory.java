/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

package com.datacruncher.fileupload;

import org.apache.log4j.Logger;

public class FileReadObjectFactory {
	private static Logger logger = Logger.getLogger(FileReadObjectFactory.class);
	/**
	 * Method will return the Object of FileReadObject inherited classes  on basis of 
	 * file name extension. If file extension does not match with FileReadObject than TxtFileReadObject will
	 * be return as default FileRead Object 
	 * @param fileName
	 * @return
	 */
	public static FileReadObject getFileReadObject(String fileName) {

		if(fileName == null || "".equals(fileName.trim())){
			logger.error("Supplied file name are null or empty.");
			return null;
		}
		else if(fileName.endsWith(FileExtensionType.EXCEL_97.getAbbreviation()))
			return new Excel_97_FileReadObject();
		
		else if(fileName.endsWith(FileExtensionType.EXCEL_2007.getAbbreviation()))
			return new Excel_2007_FileReadObject();
		
		
		else if(fileName.endsWith(FileExtensionType.ZIP.getAbbreviation()))
			return new ZipFileReadObject();

		return null;
	}
}
