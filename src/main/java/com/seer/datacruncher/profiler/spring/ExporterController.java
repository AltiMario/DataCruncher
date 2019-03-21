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

package com.seer.datacruncher.profiler.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.seer.datacruncher.profiler.util.CommonUtil;

public class ExporterController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String type = CommonUtil.notNullValue(request
				.getParameter("exportaction"));
		String columns = CommonUtil.notNullValue(request
				.getParameter("exportcolumns"));
		String data = CommonUtil.notNullValue(request
				.getParameter("exportdata"));

		if (type.equals("csv")) {
			PrintWriter out = response.getWriter();
			response.setContentType("application/csv");
			response.setHeader("content-disposition",
					"attachment;filename=analysis_data.csv"); // set the file
																// name to
																// whatever
																// required..
			out.println(columns.replace("&&&&&", ","));
			for (String strData : data.split("@@@@@")) {
				out.println(strData.replace("&&&&&", ","));
			}
			out.flush();
			out.close();
		} else if (type.equals("xml")) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/xml");
			response.setHeader("content-disposition",
					"attachment;filename=analysis_data.xml"); // set the file
																// name to
																// whatever
																// required..
			try {
				StringBuffer xml = new StringBuffer(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
				xml.append("<table><header>");
				String colArr[] = columns.split("&&&&&");
				for (String col : colArr) {
					xml.append("<columnName>" + col + "</columnName>");
				}
				xml.append("</header>");

				for (String strData : data.split("@@@@@")) {
					xml.append("<row>");
					int ind = 0;
					for (String val : strData.split("&&&&&")) {
						xml.append("<" + colArr[ind] + ">" + val + "</"
								+ colArr[ind] + "/>");
						ind++;
					}
					xml.append("</row>");
				}
				xml.append("</table>");
				out.print(xml.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.flush();
			out.close();
		} else if (type.equals("excel")) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment; filename=analysis_data.xls");
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("new sheet");
			String colArr[] = columns.split("&&&&&");
			short ind = 0;
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setFont(font);
			Row row = sheet.createRow(0);
			for (String col : colArr) {
				Cell cell = row.createCell(ind);
				cell.setCellValue(col);
				cell.setCellStyle(style);
				ind++;
			}
			ind = 1;
			for (String strData : data.split("@@@@@")) {
				Row valRow = sheet.createRow(ind);
				short cellInd = 0;
				for (String val : strData.split("&&&&&")) {
					valRow.createCell(cellInd).setCellValue(val);
					cellInd++;
				}
				ind++;
			}

			// Write the output to a file
			OutputStream resOout = response.getOutputStream();
			wb.write(resOout);
			resOout.close();

		}

		return null;
	}

}