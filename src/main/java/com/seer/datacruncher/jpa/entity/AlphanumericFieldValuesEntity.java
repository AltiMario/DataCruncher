/*
 * Copyright (c) 2015  www.see-r.com
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

package com.seer.datacruncher.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_alphanumeric_schema_values")
@NamedQueries({
    @NamedQuery(name = "AlphanumericFieldValuesEntity.findAll", 
    		query = "SELECT j FROM AlphanumericFieldValuesEntity j"),
    @NamedQuery(name="AlphanumericFieldValuesEntity.findByAlphanumericFieldValue", 
    		query="SELECT a FROM AlphanumericFieldValuesEntity a WHERE a.idAlphanumericFieldValue = :idAlphanumericFieldValue"),
    @NamedQuery(name = "AlphanumericFieldValuesEntity.findBySchemaField", 
    		query = "SELECT a FROM AlphanumericFieldValuesEntity a WHERE a.idAlphanumericSchemaField = :idAlphanumericSchemaField ORDER BY a.idAlphanumericSchemaField DESC"),
    @NamedQuery(name = "AlphanumericFieldValuesEntity.findByIdSchema", query = "SELECT a FROM AlphanumericFieldValuesEntity a WHERE a.idSchema = :idSchema ORDER BY a.idAlphanumericFieldValue ASC")})
public class AlphanumericFieldValuesEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_alphanumeric_field_value")
    private long idAlphanumericFieldValue;
	
	@Column(name = "id_alphanumeric_schema_field")
	private long idAlphanumericSchemaField;
	
	@Column(name = "id_schema")
	private long idSchema;
	
	@Column(name = "value")
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