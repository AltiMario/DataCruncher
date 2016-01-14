/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.jpa.entity;

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