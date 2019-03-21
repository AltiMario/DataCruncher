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

package com.datacruncher.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "jv_jobs")
@NamedQueries({
	@NamedQuery(name="JobsEntity.findAll", query="SELECT e FROM JobsEntity e ORDER BY e.id DESC"),
	@NamedQuery(name="JobsEntity.countDuplicateByName",query="SELECT COUNT(e) FROM JobsEntity e WHERE e.id != :id AND e.name = :name"),
    @NamedQuery(name="JobsEntity.findByName", query="SELECT e from JobsEntity e Where e.name = :name"),
    @NamedQuery(name="JobsEntity.findByApplicationId", query="SELECT e FROM JobsEntity e WHERE e.idApplication = :appId ORDER BY e.id desc")
})
public class JobsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "id_application")
	private long idApplication;	
	
	@Column(name = "id_schema")
	private long idSchema;		
	
	@Column(name = "id_connection")
	private long idConnection;		
	
	@Column(name = "id_scheduler")
	private long idScheduler;	
	
	@Column(name = "is_active")
	private int isActive;	
	
	@Column(name = "is_stream_generated")
	private int isStreamGenerated;
	
	@Column(name = "id_event_trigger")	
	private int idEventTrigger;

    @Column(name = "is_working", nullable = false)
    private boolean isWorking = false;

    @Lob
    @Column(name = "description")
    private String description;

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
    
	public long getIdApplication() {
		return idApplication;
	}
	
	public void setIdApplication(long idApplication) {
		this.idApplication = idApplication;
	}      
	
	public long getIdSchema() {
		return idSchema;
	}
	
	public void setIdSchema(long idSchema) {
		this.idSchema = idSchema;
	} 
	
	public long getIdConnection() {
		return idConnection;
	}
	
	public void setIdConnection(long idConnection) {
		this.idConnection = idConnection;
	} 	
	
	public long getIdScheduler() {
		return idScheduler;
	}
	
	public void setIdScheduler(long idScheduler) {
		this.idScheduler = idScheduler;
	} 		
	
	public int getIsActive() {
		return isActive;
	}
	
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}	
	
	public int getIsStreamGenerated() {
		return isStreamGenerated;
	}
	
	public void setIsStreamGenerated(int isStreamGenerated) {
		this.isStreamGenerated = isStreamGenerated;
	}

	public int getIdEventTrigger() {
		return idEventTrigger;
	}

	public void setIdEventTrigger(int idEventTrigger) {
		this.idEventTrigger = idEventTrigger;
	}
    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}