
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

package com.datacruncher.jpa.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author danilo
 */
@Entity
@Table(name = "jv_fields_types")

@NamedQueries({
    @NamedQuery(name = "FieldTypeEntity.findAll", query = "SELECT j FROM FieldTypeEntity j"),
    @NamedQuery(name = "FieldTypeEntity.findByIdFieldType", query = "SELECT j FROM FieldTypeEntity j WHERE j.idFieldType = :idFieldType"),
    @NamedQuery(name = "FieldTypeEntity.findByDescription", query = "SELECT j FROM FieldTypeEntity j WHERE j.description = :description"),
    @NamedQuery(name = "FieldTypeEntity.findByMappedType", query = "SELECT j FROM FieldTypeEntity j WHERE j.mappedType = :mappedType"),
    @NamedQuery(name = "FieldTypeEntity.findByFieldLength", query = "SELECT j FROM FieldTypeEntity j WHERE j.fieldLength = :fieldLength"),
    @NamedQuery(name = "FieldTypeEntity.count", query = "SELECT COUNT(j) FROM FieldTypeEntity j")})

public class FieldTypeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_field_type")
    private Integer idFieldType;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "mapped_type")
    private String mappedType;
    @Column(name = "field_length")
    private Integer fieldLength;

    public FieldTypeEntity() {
    }

    public FieldTypeEntity(Integer idFieldType) {
        this.idFieldType = idFieldType;
    }

    public FieldTypeEntity(Integer idFieldType, String description, String mappedType) {
        this.idFieldType = idFieldType;
        this.description = description;
        this.mappedType = mappedType;
    }

    public Integer getIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(Integer idFieldType) {
        this.idFieldType = idFieldType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMappedType() {
        return mappedType;
    }

    public void setMappedType(String mappedType) {
        this.mappedType = mappedType;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFieldType != null ? idFieldType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FieldTypeEntity)) {
            return false;
        }
        FieldTypeEntity other = (FieldTypeEntity) object;
        if ((this.idFieldType == null && other.idFieldType != null) || (this.idFieldType != null && !this.idFieldType.equals(other.idFieldType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FieldTypeEntity[idFieldType=" + idFieldType + "]";
    }

}
