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

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.persistence.exeptions.IllegalArgumentExcpetion;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "jv_connections")
@NamedQueries({
	@NamedQuery(name="ConnectionsEntity.findById", query="SELECT e FROM ConnectionsEntity e WHERE e.id = :id" ),
	@NamedQuery(name="ConnectionsEntity.findAllInDescOrder",query="SELECT e FROM ConnectionsEntity e ORDER BY e.id DESC"),
    @NamedQuery(name="ConnectionsEntity.findAllByIdConnType",query="SELECT c FROM ConnectionsEntity c WHERE c.idConnType = :idConnType  ORDER BY c.id DESC"),
	@NamedQuery(name="ConnectionsEntity.findDuplicateByName", query="SELECT COUNT(e) FROM ConnectionsEntity e WHERE e.id != :id AND e.name = :name")
})
public class ConnectionsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "host")
	private String host;	
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "username")
	private String userName;	
	
	@Column(name = "password")
	private String password;	
	
	@Column(name = "filename")
	private String fileName;	
	
	@Column(name = "directory")
	private String directory;		
	
	@Column(name = "service")
	private int service;		
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "file_timestamp")
    private Date fileDateTime;

    @Column(name = "id_conn_type")
    private int idConnType = 1;
	
	@Transient
	private String status;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
	public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }     
    
	public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }     
        
	public int getService() {
        return service;
    }
    
    public void setService(int service) {
        this.service = service;
    }

	public Date getFileDateTime() {
		return fileDateTime;
	}

	public void setFileDateTime(Date fileDateTime) {
		this.fileDateTime = fileDateTime;
	}

    public int getIdConnType() {
        return idConnType;
    }

    public void setIdConnType(int idConnType) {
        this.idConnType = idConnType;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    @PrePersist
    @PreUpdate
    protected void prePersist() throws IllegalArgumentExcpetion {
        if(this.idConnType < GenericType.DownloadTypeConn || this.idConnType > GenericType.uploadTypeConn){
            throw new IllegalArgumentExcpetion("The field \"ID status\" can only contain the following values: "+ GenericType.DownloadTypeConn+", "+GenericType.uploadTypeConn);
        }

    }
}