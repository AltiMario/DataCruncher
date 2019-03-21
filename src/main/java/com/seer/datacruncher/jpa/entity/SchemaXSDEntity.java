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

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author danilo
 */
@Entity
@Table(name = "jv_schema_xsd")
@NamedQueries({
    @NamedQuery(name = "SchemaXSDEntity.findAll", query = "SELECT j FROM SchemaXSDEntity j"),
    @NamedQuery(name = "SchemaXSDEntity.findByIdSchemaXSD", query = "SELECT j FROM SchemaXSDEntity j WHERE j.idSchemaXSD = :idSchemaXSD"),
    @NamedQuery(name = "SchemaXSDEntity.findByIdCheckType", query = "SELECT j FROM SchemaXSDEntity j WHERE j.idSchemaXSD in (SELECT DISTINCT t.idSchema FROM SchemaFieldEntity t WHERE t.idCheckType = :idCheckType)")
})
public class SchemaXSDEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "id_schema_xsd")
    private Long idSchemaXSD;
    
    @Lob
    @Column(name = "schema_xsd")
    private String schemaXSD;
    
	@Column(name = "is_schema_vers_inc_needed", nullable = false)
	private Boolean isVersIncreaseNeeded = false;    

    public SchemaXSDEntity() {
    }

    public SchemaXSDEntity(Long idSchemaXSD) {
        this.idSchemaXSD = idSchemaXSD;
    }

    public Long getIdSchemaXSD() {
        return idSchemaXSD;
    }

    public void setIdSchemaXSD(Long idSchemaXSD) {
        this.idSchemaXSD = idSchemaXSD;
    }

    public String getSchemaXSD() {
        return schemaXSD;
    }

    public void setSchemaXSD(String schemaXSD) {
        this.schemaXSD = schemaXSD;
    }

    public Boolean getIsVersIncreaseNeeded() {
		return isVersIncreaseNeeded;
	}

	public void setIsVersIncreaseNeeded(Boolean isVersIncreaseNeeded) {
		this.isVersIncreaseNeeded = isVersIncreaseNeeded;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (idSchemaXSD != null ? idSchemaXSD.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SchemaXSDEntity)) {
            return false;
        }
        SchemaXSDEntity other = (SchemaXSDEntity) object;
        if ((this.idSchemaXSD == null && other.idSchemaXSD != null) || (this.idSchemaXSD != null && !this.idSchemaXSD.equals(other.idSchemaXSD))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.seer.datacruncher.jpa.entity.JvSchemasXsd[idSchemaXSD=" + idSchemaXSD + "]";
    }
}
