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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "jv_schemas", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "id_schema_type" }) })

@NamedQueries({
		@NamedQuery(name = "SchemaEntity.findAll", query = "SELECT e from SchemaEntity e Where e.idSchemaType in(:idSchemaType)  ORDER BY e.idSchema DESC"),
		@NamedQuery(name = "SchemaEntity.findById", query = "SELECT e from SchemaEntity e Where e.idSchema = :idSchema"),
		@NamedQuery(name = "SchemaEntity.findByName", query = "SELECT e from SchemaEntity e Where e.name = :name"),
		@NamedQuery(name = "SchemaEntity.countByApplicationId", query = "SELECT COUNT (s) FROM SchemaEntity s WHERE s.idApplication = :appId"),
		@NamedQuery(name = "SchemaEntity.countByDatabaseId", query = "SELECT COUNT (s) FROM SchemaEntity s WHERE s.idDatabase = :idDatabase"),
		@NamedQuery(name = "SchemaEntity.countBySchemaId", query = "SELECT COUNT (s) FROM SchemaEntity s WHERE s.idSchema = :idSchema"),
		@NamedQuery(name = "SchemaEntity.countDuplicateByName", query = "SELECT COUNT (s) FROM SchemaEntity s WHERE s.idSchema != :idSchema AND s.name = :name AND s.idSchemaType = :idSchemaType"),
		@NamedQuery(name = "SchemaEntity.findByUserId", query = "SELECT a FROM SchemaEntity a, UserSchemasEntity us where us.idSchema=a.idSchema and us.idUser= :idUser ORDER BY a.idSchema DESC"),
		@NamedQuery(name = "SchemaEntity.findByApplicationIds", query = "SELECT s FROM SchemaEntity s WHERE s.idApplication in ( :appIds ) ORDER BY s.idSchema DESC"),
		@NamedQuery(name = "SchemaEntity.findByApplicationId", query = "SELECT s FROM SchemaEntity s WHERE s.idApplication = :appId ORDER BY s.idSchema DESC"),
	    @NamedQuery(name = "SchemaEntity.findAllReferencing", query = "SELECT s FROM SchemaEntity s WHERE s.idDatabase = :idDatabase"),
		@NamedQuery(name = "SchemaEntity.count", query = "SELECT COUNT (j) FROM SchemaEntity j WHERE j.idApplication <> 1"),
		@NamedQuery(name = "SchemaEntity.find", query = "SELECT e FROM SchemaEntity e WHERE e.idApplication <> 1"),
		@NamedQuery(name = "SchemaEntity.findAllByAppIdAndSchemaType", query = "SELECT e from SchemaEntity e Where e.idSchemaType in(:idSchemaType) and e.idApplication in (:appIds) ORDER BY e.idSchema DESC"),
		@NamedQuery(name = "SchemaEntity.countAllByAppIdAndSchemaType", query = "SELECT COUNT (e) from SchemaEntity e Where e.idSchemaType in(:idSchemaType) and e.idApplication in (:appIds)"),
		@NamedQuery(name = "SchemaEntity.countAll", query = "SELECT COUNT (e) from SchemaEntity e Where e.idSchemaType in(:idSchemaType)"),
		@NamedQuery(name = "SchemaEntity.countByApplicationIds", query = "SELECT COUNT (s) FROM SchemaEntity s WHERE s.idApplication in ( :appIds )") })
public class SchemaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_schema")
	private long idSchema;

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "id_application")
	private long idApplication;

	@Column(name = "id_database", nullable = false)
	private long idDatabase = 0;

	@Column(name = "id_input_database")
	private long idInputDatabase;

	@Column(name = "id_stream_type")
	private int idStreamType;

	/**
	 * Used for 'Stream loading' type
	 */
	@Column(name = "id_linked_schema", nullable = false)
	private int idLinkedSchema = 0;

	@Column(name = "delimiter")
	private String delimiter;
	
	@Column(name = "no_header")
	private boolean noHeader;

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "version")
	private String version = "";

	/**
	 * loadedXSD never used. Delete this?
	 */
	@Column(name = "loaded_xsd", nullable = false)
	private boolean loadedXSD = false;

	@Column(name = "publish_to_db", nullable = false)
	private boolean publishToDb = false;

	@Column(name = "input_to_db", nullable = false)
	private boolean inputToDb = false;

	@Column(name = "planned", nullable = false)
	private boolean isPlanned = false;

	@Column(name = "forecasted", nullable = false)
	private boolean isForecasted = false;

	@Column(name = "indexed", nullable = false)
	private boolean isIndexedIncrement = false;

	@Column(name = "planned_name")
	private long plannedName;

	@Column(name = "id_schema_type")
	private int idSchemaType;

	@Column(name = "service")
	private int service;

	@Column(name = "chr_delimiter")
	private String chrDelimiter = "\0";

	@Column(name = "id_schema_lib")
	private long idSchemaLib;

	@Column(name = "id_validation_database", nullable = false)
	private long idValidationDatabase = 0;

	@Lob
	@Column(name = "meta_condition")
	private String metaCondition;

	@Column(name = "event_trigger", nullable = false)
	private boolean isEventTrigger = false;

	@Column(name = "warn_tolerance", nullable = false)
	private boolean isWarnTolerance = false;

	@Transient
	private String idVersionLibrary;

	@Transient
	private List<UserSchemasEntity> userSchemas = new ArrayList<UserSchemasEntity>();

	@Column(name = "active")
	private Integer isActive = 1;

	@Column(name = "valid")
	private Integer isValid = 1;

	@Column(name = "invalid")
	private Integer isInValid = 1;

	@Column(name = "warning")
	private Integer isWarning = 1;

	@Column(name = "available")
	private Integer isAvailable = 1;

	@Column(name = "mongodb", nullable = false)
	private boolean isMongoDB = false;

	/**
	 * @return the userSchemas
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
	@Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	public List<UserSchemasEntity> getUserSchemas() {
		return userSchemas;
	}

	/**
	 * @param userSchemas
	 *            the userSchemas to set
	 */
	public void setUserSchemas(List<UserSchemasEntity> userSchemas) {
		this.userSchemas = userSchemas;
	}

	@Transient
	private SchemaTriggerStatusEntity schemaEvents = new SchemaTriggerStatusEntity();

	public SchemaTriggerStatusEntity getSchemaEvents() {
		return this.schemaEvents;
	}

	public void setSchemaEvents(SchemaTriggerStatusEntity schemaEvents) {
		this.schemaEvents = schemaEvents;
	}

	public void setIdEventTrigger(long idEventTrigger) {
		SchemaTriggerStatusEntity schemaEvent = getSchemaEvents();
		schemaEvent.setIdEventTrigger(idEventTrigger);
		setSchemaEvents(schemaEvent);
	}

	public void setIdTriggerStatus(String idTriggerStatus) {
		SchemaTriggerStatusEntity schemaEvent = getSchemaEvents();
		schemaEvent.setIdStatus(Integer.parseInt(idTriggerStatus));
		setSchemaEvents(schemaEvent);
	}

	public long getIdEventTrigger() {
		return getSchemaEvents().getIdEventTrigger();
	}

	public String getIdTriggerStatus() {
		return String.valueOf(getSchemaEvents().getIdStatus());
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public Integer getIsInValid() {
		return isInValid;
	}

	public void setIsInValid(Integer isInValid) {
		this.isInValid = isInValid;
	}

	public Integer getIsWarning() {
		return isWarning;
	}

	public void setIsWarning(Integer isWarning) {
		this.isWarning = isWarning;
	}

	public long getIdSchema() {
		return idSchema;
	}

	public void setIdSchema(long idSchema) {
		this.idSchema = idSchema;
	}

	public void setIdDatabase(long idDatabase) {
		this.idDatabase = idDatabase;
	}

	public void setIdInputDatabase(long idInputDatabase) {
		this.idInputDatabase = idInputDatabase;
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

	public long getIdApplication() {
		return idApplication;
	}

	public void setIdApplication(long idApplication) {
		this.idApplication = idApplication;
	}

	public long getIdDatabase() {
		return idDatabase;
	}

	public long getIdInputDatabase() {
		return idInputDatabase;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public boolean isNoHeader() {
		return noHeader;
	}

	public void setNoHeader(boolean noHeader) {
		this.noHeader = noHeader;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getLoadedXSD() {
		return loadedXSD;
	}

	public void setLoadedXSD(Boolean loadedXSD) {
		this.loadedXSD = loadedXSD;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getPublishToDb() {
		return publishToDb;
	}

	public Boolean getInputToDb() {
		return inputToDb;
	}

	public void setPublishToDb(Boolean publishToDb) {
		this.publishToDb = publishToDb;
	}

	public void setInputToDb(Boolean inputToDb) {
		this.inputToDb = inputToDb;
	}

	public boolean getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public boolean getIsForecasted() {
		return isForecasted;
	}

	public void setIsForecasted(boolean isForecasted) {
		this.isForecasted = isForecasted;
	}

	public boolean getIsIndexedIncrement() {
		return isIndexedIncrement;
	}

	public void setIsIndexedIncrement(boolean isIndexedIncrement) {
		this.isIndexedIncrement = isIndexedIncrement;
	}

	public long getPlannedName() {
		return plannedName;
	}

	public void setPlannedName(long plannedName) {
		this.plannedName = plannedName;
	}

	public int getIdSchemaType() {
		return idSchemaType;
	}

	public void setIdSchemaType(int idSchemaType) {
		this.idSchemaType = idSchemaType;
	}

	public int getIdStreamType() {
		return idStreamType;
	}

	public void setIdStreamType(int idStreamType) {
		this.idStreamType = idStreamType;
	}

	public int getService() {
		return service;
	}

	public void setService(int service) {
		this.service = service;
	}

	public String getChrDelimiter() {
		return chrDelimiter;
	}

	public void setChrDelimiter(String chrDelimiter) {
		this.chrDelimiter = chrDelimiter;
	}

	public long getIdSchemaLib() {
		return idSchemaLib;
	}

	public void setIdSchemaLib(long idSchemaLib) {
		this.idSchemaLib = idSchemaLib;
	}

	public String getMetaCondition() {
		return metaCondition;
	}

	public void setMetaCondition(String metaCondition) {
		this.metaCondition = metaCondition;
	}

	public String getIdVersionLibrary() {
		return idVersionLibrary;
	}

	public void setIdVersionLibrary(String idVersionLibrary) {
		this.idVersionLibrary = idVersionLibrary;
	}

	public long getIdValidationDatabase() {
		return idValidationDatabase;
	}

	public void setIdValidationDatabase(long idValidationDatabase) {
		this.idValidationDatabase = idValidationDatabase;
	}

	public boolean getIsEventTrigger() {
		return isEventTrigger;
	}

	public void setIsEventTrigger(boolean isEventTrigger) {
		this.isEventTrigger = isEventTrigger;
	}

	public boolean getIsWarnTolerance() {
		return isWarnTolerance;
	}

	public void setIsWarnTolerance(boolean isWarnTolerance) {
		this.isWarnTolerance = isWarnTolerance;
	}

	public Integer getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Integer isAvailable) {
		this.isAvailable = isAvailable;
	}

	public int getIdLinkedSchema() {
		return idLinkedSchema;
	}

	public void setIdLinkedSchema(int idLinkedSchema) {
		this.idLinkedSchema = idLinkedSchema;
	}

	public boolean getIsMongoDB() {
		return isMongoDB;
	}

	public void setIsMongoDB(boolean isMongoDB) {
		this.isMongoDB = isMongoDB;
	}
}
