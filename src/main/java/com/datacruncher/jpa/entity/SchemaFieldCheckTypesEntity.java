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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;

@SuppressWarnings("serial")
@Entity
@Table(name = "jv_schema_fields_check_types")
public class SchemaFieldCheckTypesEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_schema_field_check_type")
	private long idSchemaFieldCheckType;

	@JsonBackReference
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(referencedColumnName = "id_schema_field",name = "id_schema_field")
	private SchemaFieldEntity schemaFieldEntity;

	@Column(name = "id_check_type")
	private long idCheckType;

	
	
	/**
	 * @return the idSchemaFieldCheckType
	 */
	public long getIdSchemaFieldCheckType() {
		return idSchemaFieldCheckType;
	}

	/**
	 * @param idSchemaFieldCheckType
	 *            the idSchemaFieldCheckType to set
	 */
	public void setIdSchemaFieldCheckType(long idSchemaFieldCheckType) {
		this.idSchemaFieldCheckType = idSchemaFieldCheckType;
	}

	/**
	 * @return the schemaFieldEntity
	 */
	public SchemaFieldEntity getSchemaFieldEntity() {
		return schemaFieldEntity;
	}

	/**
	 * @param schemaFieldEntity
	 *            the schemaFieldEntity to set
	 */
	public void setSchemaFieldEntity(SchemaFieldEntity schemaFieldEntity) {
		this.schemaFieldEntity = schemaFieldEntity;
	}

	/**
	 * @return the idCheckType
	 */
	public long getIdCheckType() {
		return idCheckType;
	}

	/**
	 * @param idCheckType
	 *            the idCheckType to set
	 */
	public void setIdCheckType(long idCheckType) {
		this.idCheckType = idCheckType;
	}
}