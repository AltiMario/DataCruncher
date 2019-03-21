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

import com.datacruncher.constants.SchemaType;
import com.datacruncher.constants.Tag;
import com.datacruncher.datastreams.XSDFieldStorer;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.utils.generic.DomToOtherFormat;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SystemSchemas implements ResourceLoaderAware, DaoSet {

	private Logger log = Logger.getLogger(this.getClass());
	
	private String path;
	
 	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// check types should be initialized before, otherwise they will not
		// present at system schemas
		checksTypeDao.init();
		ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		try {
			ApplicationEntity appEnt = appDao.getSysApp();
			for (Resource res : Arrays.asList(resolver.getResources(path))) {
				//read content from path
	 		    InputSource is = new InputSource(res.getInputStream());
				DOMParser parser = new DOMParser();
				parser.parse(is);
				//create schema
				createSystemSchema(parser.getDocument(), appEnt.getIdApplication());				
			}
		} catch (SAXException e) {
			log.error("getXsdAttributeValueByTag :: sax exception", e);			
		} catch (IOException e) {
			log.error("Resources retrieve error", e);
		}
	}
	
	private void createSystemSchema(Document document, long idApp) {
		String systemSchemaName = getXsdAttributeValueByTag(document, Tag.TAG_SCHEMA_NAME);
		String systemStreamType = getXsdAttributeValueByTag(document, Tag.TAG_DATA_STREAM_TYPE);
		int systemSchemaType = SchemaType.VALIDATION;
		// check whether schema already exists
		if (!schemasDao.checkName(Long.valueOf(0), systemSchemaName, systemSchemaType))
			return;
		// create schema
		SchemaEntity schEnt = new SchemaEntity();
		schEnt.setIdApplication(idApp);
		schEnt.setIdStreamType(Integer.valueOf(systemStreamType));
		schEnt.setIdSchemaType(systemSchemaType);
		schEnt.setDescription("System schema used for form validation");
		schEnt.setName(systemSchemaName);
		schemasDao.create(schEnt);
		//create fields
		new XSDFieldStorer(schEnt.getIdSchema(), DomToOtherFormat.convertDomToXml(document).getBytes()).storeFields();
	}

	private String getXsdAttributeValueByTag(Document document, String tag) {
		NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
				String elName = el.getAttribute("name");
				if (elName.equals(tag) && el.hasAttribute("fixed")) {
					return el.getAttribute("fixed");
				}
			}
		}
		return null;
	}
}
