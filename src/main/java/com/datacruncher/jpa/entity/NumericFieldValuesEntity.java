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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author danilo
 */
@Entity
@Table(name = "jv_numeric_schema_values")
@NamedQueries({
    @NamedQuery(name = "NumericFieldValuesEntity.findAll", query = "SELECT n FROM NumericFieldValuesEntity n"),
    @NamedQuery(name = "NumericFieldValuesEntity.findByIdNumericFieldValue", query = "SELECT n FROM NumericFieldValuesEntity n WHERE n.idNumericFieldValue = :idNumericFieldValue"),
    @NamedQuery(name = "NumericFieldValuesEntity.findByIdNumericSchemaField", query = "SELECT n FROM NumericFieldValuesEntity n WHERE n.idNumericSchemaField = :idNumericSchemaField"),
    @NamedQuery(name = "NumericFieldValuesEntity.findByIdNumericSchemaFieldOrderDesc", query = "SELECT n FROM NumericFieldValuesEntity n WHERE n.idNumericSchemaField = :idNumericSchemaField ORDER BY n.idNumericSchemaField DESC"),
    @NamedQuery(name = "NumericFieldValuesEntity.findByIdSchema", query = "SELECT n FROM NumericFieldValuesEntity n WHERE n.idSchema = :idSchema"),
    @NamedQuery(name = "NumericFieldValuesEntity.findByValue", query = "SELECT n FROM NumericFieldValuesEntity n WHERE n.value = :value")
 })
public class NumericFieldValuesEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Basic(optional = false)
    @Column(name = "id_numeric_field_value")
    private Long idNumericFieldValue;
    @Column(name = "id_numeric_schema_field")
    private Long idNumericSchemaField;
    @Column(name = "id_schema")
    private long idSchema;
    @Column(name = "value")
    private String value;

    public NumericFieldValuesEntity() {
    }

    public NumericFieldValuesEntity(Long idNumericFieldValue) {
        this.idNumericFieldValue = idNumericFieldValue;
    }

    public Long getIdNumericFieldValue() {
        return idNumericFieldValue;
    }

    public void setIdNumericFieldValue(Long idNumericFieldValue) {
        this.idNumericFieldValue = idNumericFieldValue;
    }

    public Long getIdNumericSchemaField() {
        return idNumericSchemaField;
    }

    public void setIdNumericSchemaField(Long idNumericSchemaField) {
        this.idNumericSchemaField = idNumericSchemaField;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idNumericFieldValue != null ? idNumericFieldValue.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NumericFieldValuesEntity)) {
            return false;
        }
        NumericFieldValuesEntity other = (NumericFieldValuesEntity) object;
        if ((this.idNumericFieldValue == null && other.idNumericFieldValue != null) || (this.idNumericFieldValue != null && !this.idNumericFieldValue.equals(other.idNumericFieldValue))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.datacruncher.jpa.entity.NumericSchemaValuesEntity[idNumericFieldValue=" + idNumericFieldValue + "]";
    }

}
