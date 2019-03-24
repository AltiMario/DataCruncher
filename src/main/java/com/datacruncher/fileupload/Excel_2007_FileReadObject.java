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

import com.datacruncher.constants.Tag;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Class is used to parse all cell of Excel-2007 and return all result as a String that could 
 * be further use for validate excel data.
 * @author Naveen
 *
 */
public class Excel_2007_FileReadObject implements FileReadObject, DaoSet {
	
	private static Logger logger = Logger.getLogger(Excel_2007_FileReadObject.class);
	
	@Override
	public String parseStream(long schemaId, InputStream ios) {
		List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.listSchemaFields(schemaId);
		StringBuffer sb = new StringBuffer();
		try{
			XSSFWorkbook myWorkBook = new XSSFWorkbook(ios); 
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);			
			Iterator<Row> rowIter = mySheet.rowIterator(); 
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");			

			while(rowIter.hasNext()){
				int j = 0;
				XSSFRow myRow = (XSSFRow) rowIter.next();
				Iterator<Cell> cellIter = myRow.cellIterator();
				sb.append("<"+ Tag.TAG_ROOT+">");
				while(cellIter.hasNext()){
					XSSFCell myCell = (XSSFCell) cellIter.next();
                    String fieldValue = myCell.toString().trim();
                    if (!listSchemaFields.get(j).getNillable() || fieldValue.length() > 0) {
					    sb.append("<"+listSchemaFields.get(j).getName()+">").append(myCell.toString().replaceAll("&","&amp;")).append("</"+listSchemaFields.get(j).getName()+">");
                    }
                    j++;
					
					if(j == listSchemaFields.size() && cellIter.hasNext() ) {
						return I18n.getMessage("error.numberFieldsNoMatch");
					}
					else if(!cellIter.hasNext() && j != listSchemaFields.size()) {
						return I18n.getMessage("error.numberFieldsNoMatch");
					}					
				}				
				sb.append("</"+Tag.TAG_ROOT+">\n");
			}

		}catch(IOException e){
			logger.error("Error occured during fetch records from excel file.", e);
			return "Could not able to parse Excel file. " + e.getMessage();
		}
		return sb.toString();
	}

}
