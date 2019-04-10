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
package com.datacruncher.spring;

public class AlphanumericFieldValuesDestroyPayload {
    private long idAlphanumericFieldValue;
    private long idAlphanumericSchemaField;
    private long idSchema;
    private String value;

    public long getIdAlphanumericFieldValue() {
        return idAlphanumericFieldValue;
    }

    public void setIdAlphanumericFieldValue(long idAlphanumericFieldValue) {
        this.idAlphanumericFieldValue = idAlphanumericFieldValue;
    }

    public long getIdAlphanumericSchemaField() {
        return idAlphanumericSchemaField;
    }

    public void setIdAlphanumericSchemaField(long idAlphanumericSchemaField) {
        this.idAlphanumericSchemaField = idAlphanumericSchemaField;
    }

    public long getIdSchema() {
        return idSchema;
    }

    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
