/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
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
