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

package com.datacruncher.utils.generic;

import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Converts DOM stream to other stream format (exi, flatfile, ...)
 * 
 */
public class DomToOtherFormat implements DaoSet {
	
	private static Logger log = Logger.getLogger(DomToOtherFormat.class);	
	private DomToOtherFormat() {}
	private static TransformerFactory factory; 
	private static Transformer transformer;
	private static DocumentBuilder docBuilder;
	
	static {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("DomToOtherFormat :: DocumentBuilderFactory init exception", e);
		}
		factory = TransformerFactory.newInstance();
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			log.error("TransformerConfigurationException", e);			
		}
		//next option enables XML with tabs, otherwise XML in one line
		//transformer.setOutputProperty(OutputKeys.INDENT, "yes");			
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	}
 	
	/**
	 * Converts DOM document to schema stream format.
	 * 
	 * @param doc
	 * @param schemaEnt
	 * @return
	 */
	public static String transform(Document doc, SchemaEntity schemaEnt) {
		String result = null;
		switch (schemaEnt.getIdStreamType()) {
			case StreamType.XML :
				result = convertDomToXml(doc);
				break;
			case StreamType.JSON : 
				try {
					result = XML.toJSONObject(convertDomToXml(doc)).toString();					
				} catch (JSONException e) {
					log.error("Conversion exception: from XML to Json", e);
				}			
				break;		
			case StreamType.flatFileFixedPosition :
				result = "";
				Node root = getRootNodeOfDocument(doc);
				NodeList childs = root.getChildNodes();
				int k = childs.getLength();
				for (int i = 0; i < k; i++) {
					String text = childs.item(i).getTextContent();
					// '%%' coded in plainListChilds()
					String[] arr = text.split("%%");
					if (arr.length == 2) {
						int size = Integer.parseInt(arr[1]);
						text = arr[0];
						if (text.length() >= size) {
							result += text.substring(0, size);
						} else {
							result += text;
							for (int j = text.length(); j < size; j++) {
								result += " ";
							}
						}
					}
				}
				break;
			case StreamType.flatFileDelimited : 
				result = "";
				root = getRootNodeOfDocument(doc);
				childs = root.getChildNodes();
				String delimiter = schemaEnt.getDelimiter();
				for (int i = 0; i < childs.getLength(); i++) {
					result += childs.item(i).getTextContent() + delimiter;
				}
				if (result.length() > 0 && result.endsWith(";")) {
					result = result.substring(0, result.length() - 1);
				}
				break;
		}
		return result;
	}
	
	/**
	 * Gets DOM document as XML string.
	 * 
	 * @param doc - DOM document
	 * @return
	 */
	public static String convertDomToXml(Document doc) {		
		DOMSource source = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		StreamResult streamResult = new StreamResult(sw);			
		try {		
			transformer.transform(source, streamResult);			
		} catch (TransformerException e) {
			log.error("TransformerException", e);
		}
		return sw.toString();
	}
	
	/**
	 * Get root node of DOM document. 
	 * 
	 * @param doc - document
	 * @return Node - root node
	 */
	public static Node getRootNodeOfDocument(Document doc) {
		NodeList list = doc.getChildNodes();
		if (list.getLength() != 1) { 
			throw new RuntimeException("In dom document must be only one root node");
		}
		return list.item(0);		
	}
	
	/**
	 * Gets DocumentBuilder to create DOM documents.
	 * 
	 * @return
	 */
	public synchronized static DocumentBuilder getDocBuilder() {
		return docBuilder;
	}

	/**
	 * Checks whether current string is XML.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isXml(String s) {
		DocumentBuilder localDocBuilder = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			localDocBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("DomToOtherFormat isXml :: DocumentBuilderFactory init exception", e);
		}
		//disable printing errors to console (Content is not allowed in prolog )
		localDocBuilder.setErrorHandler(null);
		try {
			localDocBuilder.setErrorHandler(null);
			localDocBuilder.parse(new ByteArrayInputStream(s.getBytes()));
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			log.error("isXml IOException", e);
		}
		return true;
	}
}
