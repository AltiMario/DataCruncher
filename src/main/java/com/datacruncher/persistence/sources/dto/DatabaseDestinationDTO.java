/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

package com.datacruncher.persistence.sources.dto;

import com.datacruncher.jpa.entity.DatabaseEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.persistence.sources.TraceManager;

import java.util.List;

/**
 * Class DTO keeps destination database info, also keeps schema info for inserting to db
 * @author danilo
 */
public class DatabaseDestinationDTO {

	private static final String IP = "IP";
	private static final String PORT = "PORT";
	// private final String SID="SID";
	private static final String DBNAME = "DB";

    //private ApplicationEntity applicationEntity;
    private DatabaseEntity database;
    private SchemaEntity schemaEntity;
    private List<EnrichedTraceField> schemaFieldList;

    /*public ApplicationEntity getApplicationEntity() {
        return applicationEntity;
    }

    public void setApplicationEntity(ApplicationEntity applicationEntity) {
        this.applicationEntity = applicationEntity;
    }*/

    public SchemaEntity getSchemaEntity() {
        return schemaEntity;
    }

    public void setSchemaEntity(SchemaEntity schemaEntity) {
        this.schemaEntity = schemaEntity;
    }

    public DatabaseEntity getDatabaseEntity() {
        return database;
    }

    public void setDatabaseEntity(DatabaseEntity database) {
        this.database = database;
    }
    
    /**
     * Get fields list for schema.
     * @return Map con la lista dei campi del tracciato in oggetto
     *         ordinati per numero d'ordine
     */
    public List<EnrichedTraceField> getSchemaFieldList() {
        return schemaFieldList;
    }

    public void setSchemaFieldList(List<EnrichedTraceField> schemaFieldList) {
        this.schemaFieldList = schemaFieldList;
    }

    /**
     * Gets destination database url.
     * @return  URL creato a partire dal template presente nel file quickdbrecognizer.properties
     *          Per ora sono stati considerati i seguenti database:
     *          <pre>
     *           oracle-connection-url-template
     *           mysql-connection-url-template
     *           db2-connection-url-template
     *           sqlserver-connection-url-template
     *           postgresql-connection-url-template
     *          </pre>
     */
	public String getConnectionURL() {
		return getConnectionUrlByDatabaseEntity(this.database);
	}
	
	public static String getConnectionUrlByDatabaseEntity(DatabaseEntity ent) {
		String ip = ent.getHost();
		String port = ent.getPort();
		String dbName = ent.getDatabaseName();
		String urlDBTemplate = null;
		if (ent.getIdDatabaseType() == 1) {
			urlDBTemplate = TraceManager.config.getProperty("mysql-connection-url-template");
		}
		if (ent.getIdDatabaseType() == 2) {
			urlDBTemplate = TraceManager.config.getProperty("oracle-connection-url-template");
		}
		if (ent.getIdDatabaseType() == 3) {
			urlDBTemplate = TraceManager.config.getProperty("sqlserver-connection-url-template");
		}
		if (ent.getIdDatabaseType() == 4) {
			urlDBTemplate = TraceManager.config.getProperty("postgresql-connection-url-template");
		}
		if (ent.getIdDatabaseType() == 5) {
			urlDBTemplate = TraceManager.config.getProperty("db2-connection-url-template");
		}
		return urlDBTemplate.replace(IP, ip).replace(PORT, port).replace(DBNAME, dbName);		
	}

    public String getUsername() {
        return this.database.getUserName();
    }

    public String getPassword() {
        return this.database.getPassword();
    }
}
