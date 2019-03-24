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

package com.datacruncher.persistence.sources.dto;

import com.datacruncher.jpa.entity.FieldTypeEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;

public class EnrichedTraceField {

    private FieldTypeEntity fieldType;
    private SchemaFieldEntity traceField;
    private String path;
    private boolean isAttribute;

    public SchemaFieldEntity getTraceField() {
        return traceField;
    }

    public void setTraceField(SchemaFieldEntity traceField) {
        this.traceField = traceField;
    }

    public FieldTypeEntity getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEntity fieldType) {
        this.fieldType = fieldType;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isAttribute() {
		return isAttribute;
	}

	public void setAttribute(boolean isAttribute) {
		this.isAttribute = isAttribute;
	}
}
