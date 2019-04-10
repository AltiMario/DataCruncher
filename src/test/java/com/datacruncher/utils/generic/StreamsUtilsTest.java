package com.datacruncher.utils.generic;

import org.junit.Test;

import static org.junit.Assert.*;

public class StreamsUtilsTest {

    @Test
    public void formatJsonPathForXmlNode() {
        assertEquals("NAME", StreamsUtils.formatJsonPathForXmlNode("/jvRoot/name"));
    }
}