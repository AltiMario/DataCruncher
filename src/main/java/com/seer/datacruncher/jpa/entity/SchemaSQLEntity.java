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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "jv_schema_sql")
@NamedQueries({
        @NamedQuery(name = "SchemaSQLEntity.findByIdSchemaSQL", query = "SELECT s FROM SchemaSQLEntity s WHERE s.idSchemaSQL = :idSchemaSQL")

})
public class SchemaSQLEntity {

    @Id
    @Basic(optional = false)
    @Column(name = "id_schema_sql")
    private Long idSchemaSQL;

    @Lob
    @Column(name = "schema_sql")
    private String schemaSQL;

    @Column(name = "custom_query", nullable = false)
    private boolean customQuery = false;

    public Long getIdSchemaSQL() {
        return idSchemaSQL;
    }

    public void setIdSchemaSQL(Long idSchemaSQL) {
        this.idSchemaSQL = idSchemaSQL;
    }

    public String getSchemaSQL() {
        return schemaSQL;
    }

    public void setSchemaSQL(String schemaSQL) {
        this.schemaSQL = schemaSQL;
    }

    public boolean isCustomQuery() {
        return customQuery;
    }

    public void setCustomQuery(boolean customQuery) {
        this.customQuery = customQuery;
    }

}
