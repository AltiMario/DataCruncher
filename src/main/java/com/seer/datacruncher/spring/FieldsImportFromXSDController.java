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

import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.XSDFieldStorer;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.validation.ResultStepValidation;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;

public class FieldsImportFromXSDController extends MultiActionController implements DaoSet {
	
    private byte[] xsdStream;
    
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		String idSchemaXSD = request.getParameter("idSchemaXSD");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");
		response.setContentType("text/html");
		ServletOutputStream out = response.getOutputStream();
		if (!multipartFile.getOriginalFilename().endsWith(".xsd")) {
			out.write(("{success:false, message: '1'}").getBytes());
			out.flush();
			out.close();
			return null;
		}
		SchemaEntity schemaEntity = schemasDao.find(Long.parseLong(idSchemaXSD));
		this.xsdStream = multipartFile.getBytes();
		ResultStepValidation result = checkAndChangeSchemaParameters(schemaEntity);
		if (result.isValid()) {
			result = new XSDFieldStorer(Long.parseLong(idSchemaXSD), xsdStream).storeFields();
		}
		out.write(("{success: " + String.valueOf(result.isValid()) + ", message: '" + result.getMessageResult() + "'}")
				.getBytes());
		out.flush();
		out.close();
		return null;
	}
    
    private ResultStepValidation checkAndChangeSchemaParameters(SchemaEntity schemaEntity){
        ResultStepValidation result = new ResultStepValidation();
        try {
            result.setValid(true);
            result.setMessageResult("");
            InputStream inStream= new ByteArrayInputStream(xsdStream);
            Reader reader = new InputStreamReader(inStream,"UTF-8");

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            DOMParser parser = new DOMParser();
            parser.parse(is);
            Document document =  parser.getDocument();
            NodeList nodeList = document.getChildNodes().item(0).getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node= nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    String elName =  new String(el.getAttribute("name").getBytes()) ;
                    if (elName.equals(Tag.TAG_ROOT)){
                        result.setValid(false);
                        result.setMessageResult(("Schema could not be imported. Because it use the reserved word <b>"+ Tag.TAG_ROOT+"</b>"));
                        return result;
                    }else{
                        if (el.hasAttribute("fixed") ){
                            if (elName.equals(Tag.TAG_SCHEMA_NAME)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue(schemaEntity.getName());
                            }else if (elName.equals(Tag.TAG_DATA_STREAM_TYPE)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue(String.valueOf(schemaEntity.getIdStreamType()));
                            }else if (elName.equals(Tag.TAG_VALIDITY_START_DATE)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue("");
                                if(schemaEntity.getStartDate() != null)
                                    el.getAttributes().getNamedItem("fixed").setNodeValue(new SimpleDateFormat("yyyy-MM-dd").format(schemaEntity.getStartDate()));
                            }else if (elName.equals(Tag.TAG_VALIDITY_END_DATE)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue("");
                                if(schemaEntity.getEndDate() != null)
                                    el.getAttributes().getNamedItem("fixed").setNodeValue(new SimpleDateFormat("yyyy-MM-dd").format(schemaEntity.getEndDate()));
                            }else if (elName.equals(Tag.TAG_DESCRIPTION)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue("");
                                if(schemaEntity.getDescription().trim().length() > 0)
                                    el.getAttributes().getNamedItem("fixed").setNodeValue("");
                            }else if (elName.equals(Tag.TAG_DELIMITER_CHAR)){
                                el.getAttributes().getNamedItem("fixed").setNodeValue("");
                                if(schemaEntity.getIdStreamType() == StreamType.flatFileDelimited)
                                    el.getAttributes().getNamedItem("fixed").setNodeValue(schemaEntity.getDelimiter());
                            }
                        }
                    }
                }
            }

            Source source = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            Result res = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, res);

            String xsd =  stringWriter.getBuffer().toString();
            xsdStream = xsd.getBytes();
            return result;
        } catch (Exception io) {
            io.printStackTrace();
            result.setValid(false);
            result.setMessageResult(("Schema could not be modified."));
            return result;
        }

    }
}
