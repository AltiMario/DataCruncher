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

package com.datacruncher.spring;

import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.DatastreamEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DownloadStreamController implements Controller, DaoSet {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	ServletOutputStream out = response.getOutputStream();
        long idDataStream = Long.parseLong(request.getParameter("id_datastream"));
        ReadList readList = datastreamsDao.read(idDataStream);
        
        if(readList.getResults() != null && readList.getResults().size() > 0) {
            
        	DatastreamEntity datastreamInstance = (DatastreamEntity)readList.getResults().get(0);
            
        	SchemaEntity schemaEntity = schemasDao.find(datastreamInstance.getIdSchema());
            String fileExt = StreamType.getFileExtension(schemaEntity.getIdStreamType());
            
            response.setContentType(StreamType.getContentType(schemaEntity.getIdStreamType()));
            response.setHeader("content-disposition", (new StringBuilder("attachment;filename=")).append(idDataStream).append(".").append(fileExt).toString());
            
            byte data[] = null;
            
            try {
                
                if(schemaEntity.getIdStreamType() == StreamType.XML || schemaEntity.getIdStreamType() == StreamType.XMLEXI || schemaEntity.getIdStreamType() == StreamType.JSON || schemaEntity.getIdStreamType() == StreamType.flatFileDelimited || schemaEntity.getIdStreamType() == StreamType.flatFileFixedPosition) {
                    String dataStreamReceived = datastreamInstance.getDatastream();
                    if(schemaEntity.getIdStreamType() == StreamType.XML || schemaEntity.getIdStreamType() == StreamType.XMLEXI) {
                    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
        				Transformer transformer = transformerFactory.newTransformer();
        				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        						
        				StringReader xmlReader = new StringReader(dataStreamReceived);
        				StringWriter xmlWriter = new StringWriter();
        				transformer.transform(new StreamSource(xmlReader), new StreamResult(xmlWriter));
        				dataStreamReceived = xmlWriter.toString();
                    }
                	data = dataStreamReceived.getBytes();
                } else if(schemaEntity.getIdStreamType() == StreamType.EXCEL) {
                    data = getDataForXMLFile(schemaEntity.getIdSchema(), datastreamInstance.getDatastream());
                }

                out.write(data);
                out.flush();
                out.close();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        } else {
            response.setContentType("text/plain");
            response.setHeader("content-disposition", (new StringBuilder("attachment;filename=")).append(idDataStream).append(".txt").toString());
            out.flush();
            out.close();
        }
        return null;
    }

    private byte[] getDataForXMLFile(long idSchema, String datastream) {
    	               
    	Document document = null;
    	
    	try {
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        	document = docBuilder.parse(new InputSource(new StringReader(datastream)));
        	        	
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Data");
        HSSFRow headerRow = sheet.createRow(0);
        
        List<SchemaFieldEntity> listFields = schemaFieldsDao.listSchemaFields(idSchema);
        
        int colCounter = 0;
        
        HSSFRow row = sheet.createRow(1);
        String tagValue;
        
        for(SchemaFieldEntity instance : listFields) {
            tagValue = document.getElementsByTagName(instance.getName()).item(0).getTextContent();
            headerRow.createCell(colCounter).setCellValue(instance.getName());
            row.createCell(colCounter++).setCellValue(tagValue);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(bos);
            bos.close();
        } catch(Exception ioexception2) { 
        	
        }
        
        return bos.toByteArray();
    }
}
