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

package com.datacruncher.spring;

import com.datacruncher.constants.FieldType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.constants.Tag;
import com.datacruncher.jpa.Validate;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class GenerateDatastreamController implements Controller, DaoSet {

    Logger log = Logger.getLogger(this.getClass());
    private static final String NODATA = "NODATA";
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long idSchema = Long.parseLong(request.getParameter("idSchema"));
        SchemaEntity schemaEntity = schemasDao.find(idSchema);
        
        try {
            ServletOutputStream out;
            
            Validate instance = new Validate();
                                   
            if(StreamType.JSON == schemaEntity.getIdStreamType()) {
            	List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.rootForNonXML(idSchema);
            	StringBuilder strData = new StringBuilder();

            	for(SchemaFieldEntity fieldEntity : listSchemaFields) {            		
            		if(fieldEntity.getIdFieldType() == FieldType.all) {
            			StringBuilder strXML = new StringBuilder();
            			getChildTreeXml(fieldEntity.getIdSchemaField(), strXML, null);
            			strData.append("<" + fieldEntity.getName() + ">" + strXML.toString()  + "</" + fieldEntity.getName() + ">");
            		} else {
            			strData.append("<" + fieldEntity.getName() + ">" + NODATA + "</" + fieldEntity.getName() + ">");
            		}
            	}
            	
            	String xmlData = "<" + Tag.TAG_ROOT + ">" + strData.toString()  + "</" + Tag.TAG_ROOT + ">";
            	
            	XMLSerializer xmlSerializer = new XMLSerializer(); 
                JSON json = xmlSerializer.read(xmlData);  

                String strReplace = ">" + NODATA + "</";
            	xmlData = json.toString(2).replaceAll(strReplace, "></");
            	
                instance.setMessage(xmlData);
                instance.setSuccess(true);
                
            	response.setContentType("application/json");
                out = response.getOutputStream();
                ObjectMapper mapper = new ObjectMapper();
                out.write(mapper.writeValueAsBytes(instance));
                out.flush();
                out.close();
                
            } else {
            	StringBuilder strXML = new StringBuilder();
            	SchemaFieldEntity rootSchemaFieldsEntity = schemaFieldsDao.root(idSchema);
                
            	String strXmlData = getChildTreeXml(rootSchemaFieldsEntity.getIdSchemaField(), strXML, null);
            	strXmlData = "<" + rootSchemaFieldsEntity.getName() + ">" + strXmlData + "</" + rootSchemaFieldsEntity.getName() + ">";
            
            	TransformerFactory transformerFactory = TransformerFactory.newInstance();
            	Transformer transformer = transformerFactory.newTransformer();
            	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
            	StringReader xmlReader = new StringReader(strXmlData);
            	StringWriter xmlWriter = new StringWriter();
            	transformer.transform(new StreamSource(xmlReader), new StreamResult(xmlWriter));

            	String strReplace = ">" + NODATA + "</";
            	String strXMLData = xmlWriter.toString().replaceAll(strReplace, "></");
            	instance.setMessage(strXMLData);
            	instance.setSuccess(true);

            	response.setContentType("application/json");
            	out = response.getOutputStream();
            	ObjectMapper mapper = new ObjectMapper();
            	out.write(mapper.writeValueAsBytes(instance));
            	out.flush();
            	out.close();
            }
        } catch (Exception e) {
            log.error("Error while handling request for validating datastream",e);
        }

        return null;
    }
    
    public String getChildTreeXml(long idParent, StringBuilder strXML, String rootName) {
        	
    		ArrayList<SchemaFieldEntity> listChild = (ArrayList<SchemaFieldEntity>) schemaFieldsDao.listElemChild(idParent);
            
        	for (int cont = 0; cont < listChild.size(); cont++) {
              
               if (schemaFieldsDao.listElemChild(listChild.get(cont).getIdSchemaField()).size() == 0) {
                	strXML.append("<" + listChild.get(cont).getName() + ">" + NODATA  + "</" + listChild.get(cont).getName() + ">");
               } else {
                	strXML.append("<" + listChild.get(cont).getName() + ">");
                	getChildTreeXml(listChild.get(cont).getIdSchemaField(), strXML, listChild.get(cont).getName());
               }
            }
            if(rootName != null)
            	strXML.append("</" + rootName + ">");
            
            return strXML.toString();
    }
}
