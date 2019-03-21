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

import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.utils.generic.DomToOtherFormat;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generates downloadable content of XML, JSON, flatFileDelimited, flatFileFixedPosition type.
 *
 */

public class SchemaStreamsBasic extends AbstractSchemaStreams {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public SchemaStreamsBasic(SchemaEntity schemaEnt, List<Map<String, Object>> linkedFieldsPaths) {
		super(schemaEnt, linkedFieldsPaths);
	}

	@Override
	public byte[] getDownloadableStreams() {
		StringBuffer result = new StringBuffer();	
		for (int i = 0; i < maxVertical; i++) {
			Document doc = getNewDomDocument();			
			if (root == null) {
				//stream formats without root node (flat file fixed, ..)
				plainListChilds(doc, i, schemaEnt.getIdSchema(), linkedFieldsPaths);
			} else {
				//stream formats with root node (xml, json, ..)
				Element xmlNode = doc.createElement(root.getName());						
				xmlNode.setTextContent(getNodeText(root.getPath("."), i, linkedFieldsPaths));
				doc.appendChild(xmlNode);
				recursiveListChilds(doc, xmlNode, root, i, linkedFieldsPaths);	
			}
			result.append(DomToOtherFormat.transform(doc, schemaEnt)).append("\n");
		}		
		byte[] ret = null;
		try {
			ret = result.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Get Bytes of utf-8", e);			
		}
		return ret;
	}

}
