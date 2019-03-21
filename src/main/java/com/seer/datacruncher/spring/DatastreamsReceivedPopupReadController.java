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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.utils.generic.DomToOtherFormat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DatastreamsReceivedPopupReadController implements Controller,
		DaoSet {

	Logger log = Logger.getLogger(this.getClass());
	
	public static void main(String[] args) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringReader xmlReader = new StringReader("<root>michele e' bello</root>");
		StringWriter xmlWriter = new StringWriter();
		transformer.transform(new StreamSource(xmlReader), new StreamResult(xmlWriter));
		System.out.println(xmlWriter.toString());
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String idSchema = request.getParameter("idSchema");
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		String fields = request.getParameter("fields");
		String searchValue = request.getParameter("query");
		
		if(fields != null) {
			fields  = fields.replaceAll("\\[", "");
			fields  = fields.replaceAll("\\]", "");
			fields  = fields.replaceAll("\"", "");
		}
		
		SchemaEntity schemaEntity = schemasDao.find(Long.parseLong(idSchema));
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out = null;
		response.setContentType("application/json");
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		out = response.getOutputStream();
		
		ReadList readList = datastreamsDao.read(Long.parseLong(idSchema), Integer.parseInt(start),
									Integer.parseInt(limit), schemaEntity, fields, searchValue);
        @SuppressWarnings("unchecked")
		List<DatastreamEntity> listDatastreamDTO = (List<DatastreamEntity>)readList.getResults();
		if(listDatastreamDTO != null && listDatastreamDTO.size() > 0) {
			try {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						
				for(DatastreamEntity dtoInstance : listDatastreamDTO) {
					if ( DomToOtherFormat.isXml(dtoInstance.getDatastream())) {
						try {
							StringReader xmlReader = new StringReader(dtoInstance.getDatastream());
							StringWriter xmlWriter = new StringWriter();
							transformer.transform(new StreamSource(xmlReader), new StreamResult(xmlWriter));
							dtoInstance.setDatastream(xmlWriter.toString());
						}
						catch (Throwable t) {
							dtoInstance.setDatastream(dtoInstance.getDatastream());
						}
					}
					else {
						dtoInstance.setDatastream(dtoInstance.getDatastream());
					}
				}
			} catch(Exception exception) {
				log.error("DatastremsReceived - Indenting XML : " + exception);
			}
		}
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();
		return null;
	}
}