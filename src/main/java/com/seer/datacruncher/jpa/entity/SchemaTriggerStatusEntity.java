/*
 * Copyright (c) 2015  www.see-r.com
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

import javax.persistence.*;

@Entity
@Table(name = "jv_schema_trigger_status")

@NamedQueries({
        @NamedQuery(name = "SchemaTriggerStatusEntity.count", query = "SELECT COUNT (s) FROM SchemaTriggerStatusEntity s"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.countByIdSchema", query = "SELECT COUNT (s) FROM SchemaTriggerStatusEntity s WHERE s.idSchema = :idSchema"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.findAll", query="SELECT s FROM SchemaTriggerStatusEntity s ORDER BY s.idSchemaTriggerStatus ASC"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.find", query="SELECT s FROM SchemaTriggerStatusEntity s WHERE s.idSchemaTriggerStatus = :idSchemaTriggerStatus"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.findByIdSchema", query="SELECT s FROM SchemaTriggerStatusEntity s WHERE s.idSchema = :idSchema ORDER BY s.idSchemaTriggerStatus ASC"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.findByIdEventTrigger", query="SELECT s FROM SchemaTriggerStatusEntity s WHERE s.idEventTrigger = :idEventTrigger"),
        @NamedQuery(name = "SchemaTriggerStatusEntity.findByIdSchemaAndIdStatus", query="SELECT s FROM SchemaTriggerStatusEntity s WHERE s.idSchema = :idSchema AND s.idStatus = :idStatus ORDER BY s.idSchemaTriggerStatus ASC")
})

public class SchemaTriggerStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_schema_trigger_status")
    private long idSchemaTriggerStatus;

    @Column(name = "id_schema")
    private long idSchema;

    @Column(name = "id_event_trigger")
    private long idEventTrigger;

    @Column(name = "id_status")
    private int idStatus = 0;

    public long getIdSchemaTriggerStatus() {
        return idSchemaTriggerStatus;
    }

    public void setIdSchemaTriggerStatus(long idSchemaTriggerStatus) {
        this.idSchemaTriggerStatus = idSchemaTriggerStatus;
    }

    public long getIdSchema() {
        return idSchema;
    }

    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
    }

    public long getIdEventTrigger() {
        return idEventTrigger;
    }

    public void setIdEventTrigger(long idEventTrigger) {
        this.idEventTrigger = idEventTrigger;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    @PrePersist
    @PreUpdate
    protected void prePersist() throws IllegalArgumentExcpetion {
        if(this.idStatus < GenericType.okEvent || this.idStatus > GenericType.warnEvent){
            throw new IllegalArgumentExcpetion("The field \"ID status\" can only contain the following values: "+GenericType.okEvent+", "+GenericType.koEvent+", "+GenericType.warnEvent);
        }

    }
}
