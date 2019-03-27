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
package com.datacruncher.jpa.entity;

import org.apache.commons.lang.StringUtils;

public class ValidationCheckInfo {
    private long idSchema;
    private String checkType;
    private String extraCheckType;
    private String className;

    public ValidationCheckInfo(long idSchema, String checkType, String extraCheckType, String className) {
        this.idSchema = idSchema;
        this.checkType = checkType;
        this.extraCheckType = extraCheckType;
        this.className = className;
    }

    public static ValidationCheckInfo parse(String checkInfo) {
        Long idSchema = Long.parseLong(checkInfo.substring(0, checkInfo.indexOf(":")));
        String checkType = checkInfo.substring(checkInfo.indexOf(":") + 1, checkInfo.lastIndexOf("_"));
        String extraCheckType = checkInfo.substring(checkInfo.indexOf("_") + 1, checkInfo.lastIndexOf("-"));
        String className = checkInfo.substring(checkInfo.lastIndexOf("-") + 1);
        return new ValidationCheckInfo(idSchema, checkType, extraCheckType, className);
    }

    @Override
    public String toString() {
        return String.format("%d:%s_%s-%s", idSchema, checkType, extraCheckType, className);
    }

    public long getIdSchema() {
        return idSchema;
    }

    public String getCheckType() {
        return checkType;
    }

    public String getExtraCheckType() {
        return extraCheckType;
    }

    public String getClassName() {
        return className;
    }
}
