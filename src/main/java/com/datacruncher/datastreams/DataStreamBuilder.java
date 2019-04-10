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
package com.datacruncher.datastreams;

import com.datacruncher.constants.FieldType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.constants.Tag;
import com.datacruncher.jpa.dao.SchemaFieldsDao;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataStreamBuilder {
    private static final Logger LOGGER = Logger.getLogger(DataStreamBuilder.class);
    private static final String NODATA = "NODATA";
    private final SchemaFieldsDao schemaFieldsDao;
    private final StringBuilder outputBuilder;
    private Map<String, Object> fieldValues;

    public DataStreamBuilder(SchemaFieldsDao schemaFieldsDao) {
        this.schemaFieldsDao = schemaFieldsDao;
        outputBuilder = new StringBuilder();
    }

    public String build(SchemaEntity schemaEntity) throws Exception {
        return StreamType.JSON == schemaEntity.getIdStreamType()
                ? buildJson(schemaEntity.getIdSchema())
                : buildXml(schemaEntity.getIdSchema());
    }

    public String buildJson(long schemaId) {
        List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.rootForNonXML(schemaId);
        outputBuilder.append("<").append(Tag.TAG_ROOT).append(">");
        for (SchemaFieldEntity fieldEntity : listSchemaFields) {
            final String fieldName = fieldEntity.getName();
            outputBuilder.append("<").append(fieldName).append(">");
            if (fieldEntity.getIdFieldType() == FieldType.all) {
                addChildTreeXml(fieldEntity.getIdSchemaField(), false);
            } else {
                outputBuilder.append(getValue(fieldName.toUpperCase(), false));
            }
            outputBuilder.append("</").append(fieldName).append(">");
        }
        outputBuilder.append("</").append(Tag.TAG_ROOT).append(">");

        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(outputBuilder.toString());
        return json.toString(2);
    }

    public String buildXml(long schemaId) throws Exception {
        SchemaFieldEntity rootSchemaFieldsEntity = schemaFieldsDao.root(schemaId);
        outputBuilder.append("<").append(rootSchemaFieldsEntity.getName()).append(">");
        addChildTreeXml(rootSchemaFieldsEntity.getIdSchemaField(), true);
        outputBuilder.append("</").append(rootSchemaFieldsEntity.getName()).append(">");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        try (StringReader xmlReader = new StringReader(outputBuilder.toString());
             StringWriter xmlWriter = new StringWriter()) {
            transformer.transform(new StreamSource(xmlReader), new StreamResult(xmlWriter));
            return xmlWriter.toString();//.replaceAll(String.format(">%s</", NODATA), "></")
        }
    }

    public void addChildTreeXml(long idParent, boolean emptyNoData) {
        ArrayList<SchemaFieldEntity> listChild = (ArrayList<SchemaFieldEntity>) schemaFieldsDao.listElemChild(idParent);
        for (int i = 0; i < listChild.size(); i++) {
            final String fieldName = listChild.get(i).getName();
            if (schemaFieldsDao.listElemChild(listChild.get(i).getIdSchemaField()).size() == 0) {
                outputBuilder
                        .append("<").append(fieldName).append(">")
                        .append(getValue(fieldName.toUpperCase(), emptyNoData))
                        .append("</").append(fieldName).append(">");
            } else {
                outputBuilder.append("<").append(fieldName).append(">");
                addChildTreeXml(listChild.get(i).getIdSchemaField(), emptyNoData);
                outputBuilder.append("</").append(fieldName).append(">");
            }
        }
    }

    private String getValue(String fieldName, boolean emptyNoData) {
        return fieldValues != null && fieldValues.containsKey(fieldName)
                ? String.valueOf(fieldValues.get(fieldName))
                : (emptyNoData ? "" : NODATA);
    }

    public DataStreamBuilder setFieldValues(Map<String, Object> fieldValues) {
        this.fieldValues = fieldValues;
        return this;
    }
}
