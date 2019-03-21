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
@Table(name = "jv_schema_lib")

@NamedQueries({
        @NamedQuery(name = "SchemaLibEntity.count", query = "SELECT COUNT (j) FROM SchemaLibEntity j"),
        @NamedQuery(name = "SchemaLibEntity.findAll", query = "SELECT j FROM SchemaLibEntity j"),
        @NamedQuery(name = "SchemaLibEntity.findByLibTypeAndVersion", query = "SELECT j FROM SchemaLibEntity j WHERE j.libType = :libType AND j.version = :version"),
        @NamedQuery(name = "SchemaLibEntity.findByLibType", query = "SELECT j FROM SchemaLibEntity j WHERE j.libType = :libType"),
        @NamedQuery(name = "SchemaLibEntity.findByVersion", query = "SELECT j FROM SchemaLibEntity j WHERE j.version = :version"),

        @NamedQuery(name = "SchemaLibEntity.findAllByAvailability", query = "SELECT j FROM SchemaLibEntity j"),
        @NamedQuery(name = "SchemaLibEntity.findByLibTypeAndVersionAndAvailability", query = "SELECT j FROM SchemaLibEntity j WHERE j.libType = :libType AND j.version = :version"),
        @NamedQuery(name = "SchemaLibEntity.findByLibTypeAndAvailability", query = "SELECT j FROM SchemaLibEntity j WHERE j.libType = :libType"),
        @NamedQuery(name = "SchemaLibEntity.findByVersionAndAvailability", query = "SELECT j FROM SchemaLibEntity j WHERE j.version = :version")

})

public class SchemaLibEntity{

    @Id
    @Basic(optional = false)
    @Column(name = "id_schema_lib")
    private Long idSchemaLib;

    @Column(name = "lib_type")
    private int libType;

    @Column(name = "version")
    private String version;

    @Column(name = "default_ns_lib")
    private String defaultNsLib;

    @Column(name = "lib_path")
    private String libPath;

    @Column(name = "lib_name")
    private String libName;

    @Column(name = "lib_file")
    private String libFile;

	/**
	 * Default constructor otherwise hibernate finder methods will be failed on this entity.
	 */
    public SchemaLibEntity() {
    }

    public SchemaLibEntity(Long idSchemaLib,int libType,String version,String libPath,String defaultNsLib, String libFile,String libName) {
        this.idSchemaLib = idSchemaLib;
        this.libType = libType;
        this.version = version;
        this.libPath = libPath;
        this.defaultNsLib= defaultNsLib;
        this.libFile = libFile;
        this.libName = libName;

    }

    public Long getIdSchemaLib() {
        return idSchemaLib;
    }

    public void setIdSchemaLib(Long idSchemaLib) {
        this.idSchemaLib = idSchemaLib;
    }

    public int getLibType() {
        return libType;
    }

    public void setLibType(int libType) {
        this.libType = libType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDefaultNsLib() {
        return defaultNsLib;
    }

    public void setDefaultNsLib(String defaultNsLib) {
        this.defaultNsLib = defaultNsLib;
    }
    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }
    public String getLibFile() {
        return libFile;
    }

    public void setLibFile(String libFile) {
        this.libFile = libFile;
    }

}