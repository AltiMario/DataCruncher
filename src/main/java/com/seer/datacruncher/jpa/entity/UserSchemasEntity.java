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
@Table(name = "jv_user_schemas")
@NamedQueries({
	@NamedQuery(name="UserSchemasEntity.findAll" , query="SELECT e from UserSchemasEntity e"),
	@NamedQuery(name="UserSchemasEntity.findById", query="SELECT e from UserSchemasEntity e WHERE e.idUserSchema = :id"),
	@NamedQuery(name="UserSchemasEntity.findBySchemaId", query="SELECT e from UserSchemasEntity e WHERE e.idSchema = :idSchema"),
	@NamedQuery(name="UserSchemasEntity.findByUserId", query="SELECT e from UserSchemasEntity e WHERE e.idUser = :idUser"),
	@NamedQuery(name="UserSchemasEntity.findByUserIdNSchemaId", query="SELECT COUNT (e) from UserSchemasEntity e WHERE e.idUser = :idUser AND e.idSchema = :idSchema")
})
public class UserSchemasEntity {
	
	@Column(name="id_user")
	private long idUser;

	@Column(name="id_schema")
	private long idSchema;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_user_schema")
	private long idUserSchema;
	
	/**
	 * 
	 */
	public UserSchemasEntity() {
	}

	/**
	 * @return the idUser
	 */
	public long getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return the idSchema
	 */
	public long getIdSchema() {
		return idSchema;
	}

	/**
	 * @param idSchema the idSchema to set
	 */
	public void setIdSchema(long idSchema) {
		this.idSchema = idSchema;
	}

	/**
	 * @return the idUserSchema
	 */
	public long getIdUserSchema() {
		return idUserSchema;
	}

	/**
	 * @param idUserSchema the idUserSchema to set
	 */
	public void setIdUserSchema(long idUserSchema) {
		this.idUserSchema = idUserSchema;
	}
}