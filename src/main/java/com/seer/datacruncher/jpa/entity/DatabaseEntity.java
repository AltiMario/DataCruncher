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

@Entity
@Table(name = "jv_databases")
@NamedQueries({
    @NamedQuery(name = "DatabaseEntity.findAll", query = "SELECT t FROM DatabaseEntity t"),
    @NamedQuery(name = "DatabaseEntity.findAllInDescOrder", query = "SELECT d FROM DatabaseEntity d ORDER BY d.idDatabase DESC"),
    @NamedQuery(name = "DatabaseEntity.findByIdDatabase", query = "SELECT t FROM DatabaseEntity t WHERE t.idDatabase = :idDatabase"),
    @NamedQuery(name = "DatabaseEntity.findByNome", query = "SELECT t FROM DatabaseEntity t WHERE t.name = :name"),
    @NamedQuery(name = "DatabaseEntity.findDuplicateByName", query = "SELECT COUNT (d) FROM DatabaseEntity d WHERE d.idDatabase != :idDatabase AND d.name = :name")
    })
public class DatabaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_database")
    private long idDatabase;
    
	@Column(name = "name" , unique = true)
    private String name;
	
	@Lob
    @Column(name = "description")
    private String description;
	
	@Column(name = "id_database_type")
	private int idDatabaseType;
	
	@Column(name = "host")
	private String host;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "database_name")
	private String databaseName;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "password")
	private String password;
	
	public long getIdDatabase() {
		return idDatabase;
	}

	public void setIdDatabase(long idDatabase) {
		this.idDatabase = idDatabase;
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
	
	public int getIdDatabaseType() {
		return idDatabaseType;
	}

	public void setIdDatabaseType(int idDatabaseType) {
		this.idDatabaseType = idDatabaseType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}