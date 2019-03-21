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

package com.seer.datacruncher.documentation.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XSDElement {

    public static Map<String, Integer> howMany = new HashMap<String, Integer>();

    private String name;
    private String type;
    private Boolean solidLine;
    private Map<String, String> attributes;
    private List<XSDElement> innerElements;

    /**
     *
     */
    public XSDElement() {
        super();

        solidLine = Boolean.TRUE;
        attributes = new HashMap<String, String>();
        innerElements = new ArrayList<XSDElement>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the solidLine
     */
    public Boolean getSolidLine() {
        return solidLine;
    }

    /**
     * @param solidLine the solidLine to set
     */
    public void setSolidLine(Boolean solidLine) {
        this.solidLine = solidLine;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the innerElements
     */
    public List<XSDElement> getInnerElements() {
        return innerElements;
    }

    /**
     * @param innerElements the innerElements to set
     */
    public void setInnerElements(List<XSDElement> innerElements) {
        this.innerElements = innerElements;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        return "XSDElement [name=" + name + ", solidLine=" + solidLine
                + ", attributes=" + attributes + ", innerElements="
                + innerElements + "]";
    }

}
