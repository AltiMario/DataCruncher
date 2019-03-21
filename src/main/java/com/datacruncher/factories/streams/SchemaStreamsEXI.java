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

package com.datacruncher.factories.streams;

import com.datacruncher.datastreams.EXI;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.utils.generic.DomToOtherFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;

public class SchemaStreamsEXI extends AbstractSchemaStreams {
	
	private Logger log = Logger.getLogger(this.getClass());	

	public SchemaStreamsEXI(SchemaEntity schemaEnt, List<Map<String, Object>> linkedFieldsPaths) {
		super(schemaEnt, linkedFieldsPaths);
	}

	@Override
	public byte[] getDownloadableStreams() {
		if (maxVertical == 0) return null;
		StringBuffer result = new StringBuffer();	
		result.append("<_root_>\n");
		for (int i = 0; i < maxVertical; i++) {
			Document doc = getNewDomDocument();
			Element xmlNode = doc.createElement(root.getName() + i);
			xmlNode.setTextContent(getNodeText(root.getPath("."), i, linkedFieldsPaths));
			doc.appendChild(xmlNode);
			recursiveListChilds(doc, xmlNode, root, i, linkedFieldsPaths);
			result.append(DomToOtherFormat.convertDomToXml(doc)).append("\n");
		}
		result.append("</_root_>");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		try {
			EXI.encodeXmlToEXI(new ByteArrayInputStream(result.toString().getBytes("UTF-8")), baos);
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException", e);
		} catch (SAXException e) {
			log.error("SAXException", e);
		} catch (EXIException e) {
			log.error("EXIException", e);
		} catch (IOException e) {
			log.error("IOException", e);
		}
		return baos.toByteArray();
	}
}
