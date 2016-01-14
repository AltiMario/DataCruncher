/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.documentation.parse;

import com.seer.datacruncher.documentation.shared.XSDElement;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Iterator;

public class XSDElementConverter implements Converter {

    @SuppressWarnings("rawtypes")
    @Override
    public boolean canConvert(Class clazz) {
        return XSDElement.class.equals(clazz);
    }

    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {
        String key = null;
        String name = null;
        String type = null;
        String value = null;

        Iterator<String> iterator = reader.getAttributeNames();
        XSDElement element = new XSDElement();

        type = reader.getNodeName();
        type = type.substring(type.indexOf(':') + 1);
        element.setType(type);

        while (iterator.hasNext()) {
            key = iterator.next();
            value = reader.getAttribute(key);
            element.getAttributes().put(key, value);

            if ("maxOccurs".equalsIgnoreCase(key) && "unbounded".equalsIgnoreCase(value)) {
                element.setSolidLine(Boolean.FALSE);
            }
        }

        if (element.getAttributes().containsKey("name")) {
            name = element.getAttributes().get("name");
        }

        if (((name == null) || "".equals(name)) && element.getAttributes().containsKey("ref")) {
            name = element.getAttributes().get("ref");
        }

        if ((name == null) || "".equals(name)) {
            name = type;
        }

        element.setName(name);

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            element.getInnerElements().add((XSDElement) context.convertAnother(element, XSDElement.class, new XSDElementConverter()));

            key = reader.getNodeName();

            if (!XSDElement.howMany.containsKey(key)) {
                XSDElement.howMany.put(key, 0);
            }

            XSDElement.howMany.put(key, XSDElement.howMany.get(key) + 1);

            reader.moveUp();
        }

        return element;
    }

}
