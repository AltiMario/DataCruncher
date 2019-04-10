package com.datacruncher.constants;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DateTimeTypeTest {

    @Test
    public void formatDate() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 3);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 11);
        long timestamp = calendar.getTimeInMillis();
        // XSD
        assertEquals("2019-04-03T09:10:11", DateTimeType.formatDate(DateTimeType.FORMAT_XSDDATETIME, null, null, timestamp));
        assertEquals("2019-04-03", DateTimeType.formatDate(DateTimeType.FORMAT_XSDDATE, null, null, timestamp));
        assertEquals("09:10:11", DateTimeType.formatDate(DateTimeType.FORMAT_XSDTIME, null, null, timestamp));
        // Date
        assertEquals("03/04/2019", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.slashDDMMYYYY, null, timestamp));
        assertEquals("03-04-2019", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.signDDMMYYYY, null, timestamp));
        assertEquals("03.04.2019", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.dotDDMMYYYY, null, timestamp));
        assertEquals("03/04/19", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.slashDDMMYY, null, timestamp));
        assertEquals("03-04-19", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.signDDMMYY, null, timestamp));
        assertEquals("03.04.19", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.dotDDMMYY, null, timestamp));
        assertEquals("2019/04/03", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.slashYYYYMMDD, null, timestamp));
        assertEquals("2019-04-03", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.signYYYYMMDD, null, timestamp));
        assertEquals("2019.04.03", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.dotYYYYMMDD, null, timestamp));
        assertEquals("03042019", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.DDMMYYYY, null, timestamp));
        assertEquals("030419", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.DDMMYY, null, timestamp));
        assertEquals("20190403", DateTimeType.formatDate(DateTimeType.FORMAT_DATE, DateTimeType.YYYYMMDD, null, timestamp));
        // Time
        assertEquals("09:10:11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpnthhmmss, timestamp));
        assertEquals("09.10.11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dothhmmss, timestamp));
        assertEquals("09:10", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpnthhmm, timestamp));
        assertEquals("09.10", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dothhmm, timestamp));
        assertEquals("09:10:11 AM", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpntZhhmmss, timestamp));
        assertEquals("09.10.11 AM", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dotZhhmmss, timestamp));
        assertEquals("9:10:11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpnthmmss, timestamp));
        assertEquals("9.10.11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dothmmss, timestamp));
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        timestamp = calendar.getTimeInMillis();
        assertEquals("07:10:11 PM", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpntZhhmmss, timestamp));
        assertEquals("07.10.11 PM", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dotZhhmmss, timestamp));
        assertEquals("19:10:11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dblpnthmmss, timestamp));
        assertEquals("19.10.11", DateTimeType.formatDate(DateTimeType.FORMAT_TIME, null, DateTimeType.dothmmss, timestamp));
    }
}