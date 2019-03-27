package com.datacruncher.jpa.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationCheckInfoTest {

    @Test
    public void parse() {
        ValidationCheckInfo checkInfo = ValidationCheckInfo.parse("294:singleValidation_Coded-common.InetAddress");
        assertEquals(294, checkInfo.getIdSchema());
        assertEquals("singleValidation", checkInfo.getCheckType());
        assertEquals("Coded", checkInfo.getExtraCheckType());
        assertEquals("common.InetAddress", checkInfo.getClassName());
        checkInfo = ValidationCheckInfo.parse("294:singleValidation_Custom Code-SampleValidation");
        assertEquals(294, checkInfo.getIdSchema());
        assertEquals("singleValidation", checkInfo.getCheckType());
        assertEquals("Custom Code", checkInfo.getExtraCheckType());
        assertEquals("SampleValidation", checkInfo.getClassName());
    }
}