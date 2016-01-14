/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */
package com.seer.datacruncher.documentation;

import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;
import com.seer.datacruncher.documentation.graphics.ImageBuilder;
import com.seer.datacruncher.documentation.parse.XSDElementConverter;
import com.seer.datacruncher.documentation.shared.XSDElement;
import com.thoughtworks.xstream.XStream;
import org.apache.log4j.Logger;

import java.io.*;

public final class Xsd2png implements DaoSet {

	Logger log = Logger.getLogger(this.getClass());
    
	public OutputStream createImage(long idSchema,String fileXsd) {
        XStream xStream = new XStream();
        InputStream is = null;
        OutputStream os = null;
        String rootName;
        int idStreamType;

        try {
            is = new ByteArrayInputStream(fileXsd.getBytes());
            os = new ByteArrayOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        xStream.alias("xs:schema", XSDElement.class);
        xStream.registerConverter(new XSDElementConverter());
        XSDElement element = (XSDElement) xStream.fromXML(is);

        ImageBuilder builder = new ImageBuilder(os);
        XSDElement rootElement = null;
        idStreamType = (schemasDao.find(idSchema)).getIdStreamType();
        if (idStreamType == StreamType.XML || idStreamType == StreamType.XMLEXI) {
            rootName = (schemaFieldsDao.root(idSchema)).getName();
        }else{
            rootName = Tag.TAG_ROOT;
        }
        for (XSDElement xsdElement : element.getInnerElements()) {
            if ("element".equals(xsdElement.getType()) && rootName.equals(xsdElement.getName())) {
                rootElement = xsdElement;
                break;
            }
        }

        builder.buildImage(idSchema,rootElement);
        log.info("Building PNG complete");
		return os;
    	
    	
    }

	public void createFileImage (long idSchema,String fileXsd) {
        String PNG_FILE = "C:\\xsd2png.png";
        File pngFile = new File(PNG_FILE);
        System.out.println("++++ Initalizating XSD to PNG process");
        XStream xStream = new XStream();
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(fileXsd.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        xStream.alias("xs:schema", XSDElement.class);
        xStream.registerConverter(new XSDElementConverter());
        XSDElement element = (XSDElement) xStream.fromXML(is);
        System.out.println("++++ Parsing XSD object complete");

        ImageBuilder builder = new ImageBuilder(pngFile);
        XSDElement rootElement = null;
        for (XSDElement xsdElement : element.getInnerElements()) {
            if ("element".equals(xsdElement.getType())) {
                rootElement = xsdElement;
                break;
            }
        }

        builder.buildImage(idSchema,rootElement);
        System.out.println("++++ Building PNG complete");
    }
	
	 /**
      * @param arg --> id Schema.
      */
	public static void main(String arg) {
    	try {
    		Xsd2png xsd2png= new Xsd2png();
    		long idSchema = Long.parseLong(arg);
    		SchemaXSDEntity schemaXSDEntity = schemasXSDDao.read(idSchema);
        	xsd2png.createFileImage(idSchema,schemaXSDEntity.getSchemaXSD());
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
}
