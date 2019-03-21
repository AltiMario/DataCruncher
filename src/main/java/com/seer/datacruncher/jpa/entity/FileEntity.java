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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.Basic;

@Entity
@Table(name = "jv_files")
@NamedQueries({
	@NamedQuery(name="FileEntity.countBySchemaId", query="SELECT count (f) FROM FileEntity f WHERE f.idSchema = :idSchema"),
	@NamedQuery(name="FileEntity.findDuplicateByName",query="SELECT COUNT (f) FROM FileEntity f WHERE f.idFile != :idFile AND f.idSchema = :idSchema AND f.name = :name"),
	@NamedQuery(name="FileEntity.findBySchemaId", query="SELECT f FROM FileEntity f WHERE f.idSchema = :idSchema")
})
public class FileEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_file")
    private long idFile;
	
	@Column(name = "id_schema")
    private long idSchema;
	
	@Column(name = "name")
    private String name;

	@Lob
    @Column(name = "description")
    private String description;
	
	@Column(name="content_type")
	private String contentType;
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name="content")
	private byte[] content;

	public long getIdFile() {
		return idFile;
	}

	public void setIdFile(long idFile) {
		this.idFile = idFile;
	}
	
	public String getName() {
		return name;
	}

	public long getIdSchema() {
        return idSchema;
    }
    
    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
