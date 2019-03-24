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

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name="CustomErrorEntity.findBySchemaId", query="SELECT d FROM CustomErrorEntity d where d.idSchema = :schemaId ORDER BY d.id ASC "),
	@NamedQuery(name="CustomErrorEntity.countDuplicateByName", query="SELECT COUNT (d) FROM CustomErrorEntity d WHERE d.id <> :id AND d.name = :name AND d.idSchema = :schemaId ")
})

@Entity
@Table(name = "jv_custom_errors")
public class CustomErrorEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "id_schema")
	private long idSchema;	

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "description")
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdSchema() {
		return idSchema;
	}

	public void setIdSchema(long idSchema) {
		this.idSchema = idSchema;
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}