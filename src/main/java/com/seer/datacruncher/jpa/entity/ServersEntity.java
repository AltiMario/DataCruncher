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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "jv_servers")

@NamedQueries({
		@NamedQuery(name = "ServersEntity.findAll", query = "SELECT j FROM ServersEntity j"),
		@NamedQuery(name = "ServersEntity.count", query = "SELECT COUNT(j) FROM ServersEntity j")})
		
public class ServersEntity {
	@Id
    @Basic(optional = false)
    @Column(name = "id")
    private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "is_active")
	private int isActive;
	
    public ServersEntity() {}
    
    
    /**
	 * @param id
	 * @param name
	 * @param isActive
	 */
	public ServersEntity(long id, String name, int isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}



	public ServersEntity(String name) {
        this.name = name;
        this.isActive = 1;
    }	
	
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
    
	public int getIsActive() {
		return isActive;
	}
	
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
}