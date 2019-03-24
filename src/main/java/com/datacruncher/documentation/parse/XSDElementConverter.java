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

package com.datacruncher.documentation.parse;

import com.datacruncher.documentation.shared.XSDElement;
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
