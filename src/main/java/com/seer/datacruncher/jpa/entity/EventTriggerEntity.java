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

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "jv_event_trigger")
@NamedQueries({
    @NamedQuery(name = "EventTriggerEntity.count", query = "SELECT COUNT (j) FROM EventTriggerEntity j WHERE j.isSystemType = false"),
    @NamedQuery(name = "EventTriggerEntity.countAll", query = "SELECT COUNT (j) FROM EventTriggerEntity j"),
    @NamedQuery(name = "EventTriggerEntity.countByIdEvent", query="SELECT COUNT (j) FROM EventTriggerEntity j WHERE j.idEventTrigger= :idEventTrigger"),
    @NamedQuery(name = "EventTriggerEntity.findAll", query="SELECT d FROM EventTriggerEntity d ORDER BY d.name ASC, d.idEventTrigger DESC"),
    @NamedQuery(name = "EventTriggerEntity.findAllNoSys", query="SELECT d FROM EventTriggerEntity d WHERE d.isSystemType = false ORDER BY d.name ASC, d.idEventTrigger DESC"),
    @NamedQuery(name = "EventTriggerEntity.findByIdSchemaAndIdStatus", query = "SELECT e FROM EventTriggerEntity e WHERE e.idEventTrigger in (SELECT DISTINCT s.idEventTrigger FROM SchemaTriggerStatusEntity s WHERE s.idSchema = :idSchema  AND s.idStatus = :idStatus)"),
    @NamedQuery(name = "EventTriggerEntity.findByName", query="SELECT d FROM EventTriggerEntity d where d.name = :name"),
    @NamedQuery(name = "EventTriggerEntity.findByIdEventTrigger", query="SELECT d FROM EventTriggerEntity d where d.idEventTrigger = :idEventTrigger")
})

public class EventTriggerEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_event_trigger")
    private long idEventTrigger;
    
	@Column(name = "name" , unique = true)
    private String name;
	
	@Lob
    @Column(name = "description")
    private String description;
	
	@Lob
    @Column(name = "code")
    private String code;

    @Column(name = "is_system_type", nullable = false)
    private boolean isSystemType = false;

    @Transient
    private SchemaTriggerStatusEntity eventSchemas  = new SchemaTriggerStatusEntity();

    public SchemaTriggerStatusEntity getEventSchemas() {
        return eventSchemas;
    }

    public void setEventSchemas(SchemaTriggerStatusEntity eventSchemas) {
        this.eventSchemas = eventSchemas;
    }
	/**
	 * @return the idEventTrigger
	 */
	public long getIdEventTrigger() {
		return idEventTrigger;
	}

	/**
	 * @param idEventTrigger the idEventTrigger to set
	 */
	public void setIdEventTrigger(long idEventTrigger) {
		this.idEventTrigger = idEventTrigger;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

    public boolean isSystemType() {
        return isSystemType;
    }

    public void setSystemType(boolean systemType) {
        isSystemType = systemType;
    }
}