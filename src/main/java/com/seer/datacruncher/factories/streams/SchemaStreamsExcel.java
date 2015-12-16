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

package com.seer.datacruncher.factories.streams;

import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.utils.generic.DomToOtherFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SchemaStreamsExcel extends AbstractSchemaStreams {
	
	private Logger log = Logger.getLogger(this.getClass());	

	public SchemaStreamsExcel(SchemaEntity schemaEnt, List<Map<String, Object>> linkedFieldsPaths) {
		super(schemaEnt, linkedFieldsPaths);
	}

	@Override
	public byte[] getDownloadableStreams() {
		if (maxVertical == 0) return null;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet("Sheet 1"); 
		HSSFRow headerRow = s.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		for (int i = 0; i < maxVertical; i++) {
			Document doc = getNewDomDocument();			
			plainListChilds(doc, i, schemaEnt.getIdSchema(), linkedFieldsPaths);
			NodeList childList = DomToOtherFormat.getRootNodeOfDocument(doc).getChildNodes();
			HSSFRow row = s.createRow(i + 1);
			for (int j = 0; j < childList.getLength(); j++) {
				Node child = childList.item(j);
				if (i == 0) {
					HSSFCell cell = headerRow.createCell(j);
					cell.setCellValue(child.getNodeName());
					cell.setCellStyle(style);
				}
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(child.getTextContent());
			}
		}		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    wb.write(bos);
		} catch (IOException e) {
			log.error("IO Exception, excel generation", e);
		} finally {
		    try {
				bos.close();
			} catch (IOException e) {
				log.error("IO stream closure exception, excel generation", e);
			}
		}
		return bos.toByteArray();
	}
}
