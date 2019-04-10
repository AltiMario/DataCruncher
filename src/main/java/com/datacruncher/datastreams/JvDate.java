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

import com.datacruncher.constants.DateTimeType;

public class JvDate {

    public static final String RULE_PREFIX = "@jvDate:";
    private static final Integer EMPTY_VALUE = new Integer(0);

    private Integer dateTimeType;
    private Integer dateType;
    private Integer timeType;

    public JvDate() {
    }

    public JvDate(Integer dateTimeType, Integer dateType, Integer timeType) {
        this.dateTimeType = dateTimeType;
        this.dateType = dateType;
        this.timeType = timeType;
    }

    public static JvDate parse(String content) {
        JvDate result = null;
        String arr[] = content.trim().split(":")[1].split("-");
        if (arr.length == 3) {
            result = new JvDate();
            Integer type = Integer.parseInt(arr[0]);
            if (type != EMPTY_VALUE) {
                result.setDateTimeType(type);
            }
            type = Integer.parseInt(arr[1]);
            if (type != EMPTY_VALUE) {
                result.setDateType(type);
            }
            type = Integer.parseInt(arr[2]);
            if (type != EMPTY_VALUE) {
                result.setTimeType(type);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s%d-%d-%d", RULE_PREFIX,
                dateTimeType != null ? dateTimeType : EMPTY_VALUE,
                dateType != null ? dateType : EMPTY_VALUE,
                timeType != null ? timeType : EMPTY_VALUE);
    }

    public boolean isDateTime() {
        return getDateTimeType() == DateTimeType.FORMAT_DATETIME;
    }

    public boolean isDate() {
        return getDateTimeType() == DateTimeType.FORMAT_DATE;
    }

    public boolean isTime() {
        return getDateTimeType() == DateTimeType.FORMAT_TIME;
    }

    public Integer getDateTimeType() {
        return dateTimeType;
    }

    public void setDateTimeType(Integer dateTimeType) {
        this.dateTimeType = dateTimeType;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }
}
