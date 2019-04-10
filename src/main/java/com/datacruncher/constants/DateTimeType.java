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
package com.datacruncher.constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Gli attributi presenti sono equivalenti (e devono restare tali) alla mappatura su DB e sul front-end
 */
public final class DateTimeType {
    // Date format
    public static final int FORMAT_DATETIME = 1;
    public static final int FORMAT_DATE = 2;
    public static final int FORMAT_TIME = 3;
    public static final int FORMAT_XSDDATETIME = 4;
    public static final int FORMAT_XSDDATE = 5;
    public static final int FORMAT_XSDTIME = 6;
    public static final int FORMAT_UNIXTIMESTAMP = 7;

    // Date Constants
    /**
     * dd/MM/yyyy
     */
    public static final int slashDDMMYYYY = 1;
    /**
     * dd-MM-yyyy
     */
    public static final int signDDMMYYYY = 2;
    /**
     * dd.MM.yyyy
     */
    public static final int dotDDMMYYYY = 3;
    /**
     * dd/MM/yy
     */
    public static final int slashDDMMYY = 4;
    /**
     * dd-MM-yy
     */
    public static final int signDDMMYY = 5;
    /**
     * dd.MM.yy
     */
    public static final int dotDDMMYY = 6;
    /**
     * yyyy/MM/dd
     */
    public static final int slashYYYYMMDD = 7;
    /**
     * yyyy-MM-dd
     */
    public static final int signYYYYMMDD = 8;
    /**
     * yyyy.MM.dd
     */
    public static final int dotYYYYMMDD = 9;
    /**
     * ddMMyyyy
     */
    public static final int DDMMYYYY = 10;
    /**
     * ddMMyy
     */
    public static final int DDMMYY = 11;
    /**
     * yyyyMMdd
     */
    public static final int YYYYMMDD = 12;

    // Time Constants
    /**
     * hh:mm:ss
     */
    public static final int dblpnthhmmss = 1;
    /**
     * hh.mm.ss
     */
    public static final int dothhmmss = 2;
    /**
     * hh:mm
     */
    public static final int dblpnthhmm = 3;
    /**
     * hh.mm
     */
    public static final int dothhmm = 4;
    /**
     * hh:mm:ss AM/PM
     */
    public static final int dblpntZhhmmss = 5;
    /**
     * hh.mm.ss AM/PM
     */
    public static final int dotZhhmmss = 6;
    /**
     * h:mm:ss
     */
    public static final int dblpnthmmss = 7;
    /**
     * h.mm.ss
     */
    public static final int dothmmss = 8;

    // XSD Date Type Constants
    public static final int xsdDateTime = 4;
    public static final int xsdDate = 5;
    public static final int xsdTime = 6;
    public static final int unixTimestamp = 7;

    public static String formatDate(int dateFormat, Integer dateType, Integer timeType, long timestamp) {
//        field.getIdFieldType(), field.getIdDateType(), field.getIdTimeType()
//        SchemaFieldEntity{idSchemaField=902, idFieldType=6, name='time1', idDateTimeType=3, idDateType=null, idTimeType=1}

        if (dateFormat == FORMAT_UNIXTIMESTAMP) {
            return String.valueOf(timestamp);
        }
        StringBuilder builder = new StringBuilder();
        final Date date = new Date(timestamp);
        switch (dateFormat) {
            case FORMAT_XSDDATETIME:
                dateType = signYYYYMMDD;
                timeType = dblpnthhmmss;
                break;
            case FORMAT_XSDDATE:
                dateType = signYYYYMMDD;
                break;
            case FORMAT_XSDTIME:
                timeType = dblpnthhmmss;
                break;
        }
        if (dateType != null && dateType != 0) {
            String pattern = null;
            switch (dateType) {
                case slashDDMMYYYY:
                    pattern = "dd/MM/yyyy";
                    break;
                case signDDMMYYYY:
                    pattern = "dd-MM-yyyy";
                    break;
                case dotDDMMYYYY:
                    pattern = "dd.MM.yyyy";
                    break;
                case slashDDMMYY:
                    pattern = "dd/MM/yy";
                    break;
                case signDDMMYY:
                    pattern = "dd-MM-yy";
                    break;
                case dotDDMMYY:
                    pattern = "dd.MM.yy";
                    break;
                case slashYYYYMMDD:
                    pattern = "yyyy/MM/dd";
                    break;
                case signYYYYMMDD:
                    pattern = "yyyy-MM-dd";
                    break;
                case dotYYYYMMDD:
                    pattern = "yyyy.MM.dd";
                    break;
                case DDMMYYYY:
                    pattern = "ddMMyyyy";
                    break;
                case DDMMYY:
                    pattern = "ddMMyy";
                    break;
                case YYYYMMDD:
                    pattern = "yyyyMMdd";
                    break;
            }
            builder.append(new SimpleDateFormat(pattern).format(date));
        }
        if (dateFormat == FORMAT_DATETIME) {
            builder.append(" ");
        } else if (dateFormat == FORMAT_XSDDATETIME) {
            builder.append("T");
        }
        if (timeType != null && timeType != 0) {
            String pattern = null;
            switch (timeType) {
                case dblpnthhmmss:
                    pattern = "HH:mm:ss";
                    break;
                case dothhmmss:
                    pattern = "HH.mm.ss";
                    break;
                case dblpnthhmm:
                    pattern = "HH:mm";
                    break;
                case dothhmm:
                    pattern = "HH.mm";
                    break;
                case dblpntZhhmmss:
                    pattern = "hh:mm:ss a";
                    break;
                case dotZhhmmss:
                    pattern = "hh.mm.ss a";
                    break;
                case dblpnthmmss:
                    pattern = "k:mm:ss";
                    break;
                case dothmmss:
                    pattern = "k.mm.ss";
                    break;
            }
            builder.append(new SimpleDateFormat(pattern).format(date));
        }
        return builder.toString();
    }
}