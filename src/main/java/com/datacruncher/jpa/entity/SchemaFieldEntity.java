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

package com.datacruncher.jpa.entity;

import com.datacruncher.jpa.dao.DaoServices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

@SuppressWarnings("serial")
@Entity
@Table(name = "jv_schema_fields")
@NamedQueries({
        /* used in jv-ee*/
        @NamedQuery(name = "SchemaFieldEntity.findAllByNameAndSchemaId", query="SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema AND s.name = :name"),

        @NamedQuery(name = "SchemaFieldEntity.findByIdSchema", query = "SELECT t FROM SchemaFieldEntity t WHERE t.idSchema = :idSchema ORDER BY t.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findLeavesByIdSchema", query = "SELECT t FROM SchemaFieldEntity t WHERE t.idSchema = :idSchema and t.idFieldType > 3 ORDER BY t.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findNumExtraCheck", query = "SELECT count(j) FROM ChecksTypeEntity j, SchemaFieldCheckTypesEntity S WHERE j.idCheckType = s.idCheckType AND j.tokenRule != null AND s.schemaFieldEntity.idSchemaField IN(SELECT t.idSchemaField FROM SchemaFieldEntity t Where t.idSchema = :idSchema)"),
        @NamedQuery(name = "SchemaFieldEntity.findFieldsWithExtraCheck", query = "SELECT DISTINCT s.idSchemaField FROM SchemaFieldEntity s, ChecksTypeEntity c , SchemaFieldCheckTypesEntity  t WHERE s.idSchema = :idSchema  AND s.idSchemaField = t.schemaFieldEntity.idSchemaField  AND t.idCheckType = c.idCheckType AND c.tokenRule != null AND (c.className != null OR c.tokenRule='@spellcheck')"),
        @NamedQuery(name = "SchemaFieldEntity.findFieldsExtraCheck", query = "SELECT s,c  FROM SchemaFieldEntity s, ChecksTypeEntity c , SchemaFieldCheckTypesEntity  t WHERE s.idSchema = :idSchema  AND s.idSchemaField = t.schemaFieldEntity.idSchemaField  AND t.idCheckType = c.idCheckType AND c.tokenRule != null AND (c.className != null OR c.tokenRule='@spellcheck') ORDER BY c.className ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findFieldsUnixDataType", query = "SELECT t FROM SchemaFieldEntity t WHERE t.idSchema = :idSchema AND t.idFieldType = 6 AND t.idDateTimeType = 7 "),
        @NamedQuery(name = "SchemaFieldEntity.findNumUnixDataType", query = "SELECT count(t.idSchemaField) FROM SchemaFieldEntity t WHERE t.idSchema = :idSchema AND t.idFieldType = 6 AND t.idDateTimeType = 7 "),
        @NamedQuery(name = "SchemaFieldEntity.findExtraCheck", query = "SELECT j.tokenRule FROM ChecksTypeEntity j, SchemaFieldCheckTypesEntity S WHERE j.idCheckType = s.idCheckType AND j.tokenRule != null AND s.schemaFieldEntity.idSchemaField IN(SELECT t.idSchemaField FROM SchemaFieldEntity t Where t.idSchema = :idSchema)"),
        @NamedQuery(name = "SchemaFieldEntity.findSchemaRoot", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema AND s.idParent = 0"),
        @NamedQuery(name = "SchemaFieldEntity.findAllByParentId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchemaField != :idParent and s.idParent = :idParent  ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findAllByParentIdAndSchemaId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema and s.idParent = :idParent  ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findElemByParentId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchemaField != :idParent and s.idParent = :idParent and s.is_Attribute = false ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findElemByParentIdAndSchemaId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema and s.idParent = :idParent and s.is_Attribute = false ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findAttrByParentId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchemaField != :idParent and s.idParent = :idParent and s.is_Attribute = true ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.findAttrByParentIdAndSchemaId", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema and s.idParent = :idParent and s.is_Attribute = true ORDER BY s.elementOrder ASC"),
        @NamedQuery(name = "SchemaFieldEntity.count", query = "SELECT COUNT (f) FROM SchemaFieldEntity f, SchemaEntity s WHERE f.idSchema = s.idSchema"),
        @NamedQuery(name = "SchemaFieldEntity.findAllAttributes", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idParent = :idSchemaField AND s.is_Attribute = true"),
        @NamedQuery(name = "SchemaFieldEntity.findAttrByIdSchema", query = "SELECT s FROM SchemaFieldEntity s WHERE s.idSchema = :idSchema and s.is_Attribute = true"),
        @NamedQuery(name = "SchemaFieldEntity.incrementUpperElementOrder",
                query = "UPDATE SchemaFieldEntity SET element_order = element_order + 1 "
                        + " WHERE id_schema = :idSchema "
                        + " AND id_parent = :idParent "
                        + " AND element_order >= :elementOrder "),
        @NamedQuery(name = "SchemaFieldEntity.decrementUpperElementOrder",
                query = "UPDATE SchemaFieldEntity SET element_order = element_order - 1 "
                        + " WHERE id_schema = :idSchema "
                        + " AND id_parent = :idParent "
                        + " AND element_order > :elementOrder ") ,
        @NamedQuery(name = "SchemaFieldEntity.deleteIdExtraCheck",
                query = "UPDATE SchemaFieldEntity s SET s.idCheckType = 0 "
                        + " WHERE s.idCheckType = :idCheckType "
        ),
        @NamedQuery(name="SchemaFieldEntity.findBySchemFieldId", query="SELECT s from SchemaFieldCheckTypesEntity s WHERE s.schemaFieldEntity.idSchemaField = :idSchemaField")
})
public class SchemaFieldEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_schema_field")
    private long idSchemaField;

    @Column(name = "id_schema")
    private long idSchema;

    @Column(name = "id_parent")
    private long idParent;

    @Column(name = "id_field_type")
    private int idFieldType;

    @Column(name = "id_check_type")
    private long idCheckType;

    @Column(name = "id_custom_error")
    private long idCustomError;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "element_order")
    private int elementOrder;

    @Column(name = "min_lenght" , nullable = true)
    private Integer minLength;

    @Column(name = "max_lenght" , nullable = true)
    private Integer maxLength;

    @Column(name = "min_inclusive" , nullable = true)
    private Double minInclusive;

    @Column(name = "max_inclusive" , nullable = true)
    private Double maxInclusive;

    @Column(name = "fraction_digits" , nullable = true)
    private Integer fractionDigits;

    @Column(name = "id_date_time_type", nullable = true)
    private Integer idDateTimeType;

    @Column(name = "id_date_type", nullable = true)
    private Integer idDateType;

    @Column(name = "id_time_type", nullable = true)
    private Integer idTimeType;

    @Column(name = "nillable" , nullable = true)
    private Boolean nillable;

    @Column(name = "id_align" , nullable = true)
    private Integer idAlign;

    @Column(name = "fill_char" , nullable = true)
    private String fillChar;

    @Column(name = "size" , nullable = true)
    private String size;

    @Column(name = "is_forecastable", nullable = false)
    private boolean isForecastable = false;

    @Column(name = "forecast_speed", nullable = false)
    private Integer forecastSpeed = 50;

    @Column(name = "forecast_accuracy", nullable = false)
    private Integer forecastAccuracy = 50;

    @Column(name = "is_attribute" , nullable = false)
    private boolean is_Attribute = false;

    @Column(name = "max_occurs" , nullable = false)
    private Integer maxOccurs = 1;

    @Lob
    @Column(name = "link_to_db")
    private String linkToDb;

    @Column(name = "id_numeric_type", nullable = false)
    private int idNumericType = 1;

    @Column(name = "error_tolerance_value")
    private int errorToleranceValue;

    @Column(name = "index_incremental")    
    private boolean indexIncremental;
    
	@Column(name = "error_type", nullable = false)
	private int errorType;	    

	@JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.ALL},mappedBy="schemaFieldEntity")
    private Set<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeSet = new HashSet<SchemaFieldCheckTypesEntity>();

	public boolean hasCustomError() {
        return idCustomError != 0;
    }

	public long getIdSchemaField() {
        return idSchemaField;
    }

    public void setIdSchemaField(long idSchemaField) {
        this.idSchemaField = idSchemaField;
    }

    public long getIdSchema() {
        return idSchema;
    }

    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
    }

    public long getIdParent() {
        return idParent;
    }

    public void setIdParent(long idParent) {
        this.idParent = idParent;
    }

    public int getIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(int idFieldType) {
        this.idFieldType = idFieldType;
    }

    public long getIdCheckType() {
        return idCheckType;
    }

    public void setIdCheckType(long idCheckType) {
        this.idCheckType = idCheckType;
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

    public int getElementOrder() {
        return elementOrder;
    }

    public void setElementOrder(int elementOrder) {
        this.elementOrder = elementOrder;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Double getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(Double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Double getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(Double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Integer getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(Integer fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public Integer getIdDateTimeType() {
        return idDateTimeType;
    }

    public void setIdDateFmtType(Integer idDateTimeType) {
        this.idDateTimeType = idDateTimeType;
    }

    public Integer getIdDateType() {
        return idDateType;
    }

    public void setIdDateType(Integer idDateType) {
        this.idDateType = idDateType;
    }

    public Integer getIdTimeType() {
        return idTimeType;
    }

    public void setIdTimeType(Integer idTimeType) {
        this.idTimeType = idTimeType;
    }

    public Boolean getNillable() {
        return nillable;
    }

    public void setNillable(Boolean nillable) {
        this.nillable = nillable;
    }

    public Integer getIdAlign() {
        return idAlign;
    }

    public void setIdAlign(Integer idAlign) {
        this.idAlign = idAlign;
    }

    public String getFillChar() {
        return fillChar;
    }

    public void setFillChar(String fillChar) {
        this.fillChar = fillChar;
    }

    public SchemaFieldEntity getParent() {
        if (getIdParent() == 0) return null;
        return DaoServices.getSchemaFieldsDao().find(getIdParent());
    }

    /**
     * Gets Upper path of current schemaField.
     * Example of path with delimiter '.' : "Root.branch.fieldName"
     *
     * @param delimiter
     * @return
     */
    public String getPath(String delimiter) {
        String result = getOrigPath(delimiter);
        return result.toUpperCase();
    }
    /**
     * Gets path of current schemaField.
     * Example of path with delimiter '.' : "Root.branch.fieldName"
     *
     * @param delimiter
     * @return
     */
    public String getOrigPath(String delimiter) {
        String result = "";
        SchemaFieldEntity ent = this;
        List<String> list = new ArrayList<String>();
        int i = 0;
        do {
            if (i++ != 0) ent = ent.getParent();
            if(ent != null) list.add(ent.getName());
        } while (ent != null && ent.getIdParent() != 0);
        for (int j = list.size() - 1; j >= 0; j--) {
            result += (j == list.size() - 1 ? "" : delimiter) + list.get(j);
        }
        return result;
    }
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public long getIdCustomError() {
        return idCustomError;
    }

    public void setIdCustomError(long idCustomError) {
        this.idCustomError = idCustomError;
    }

    public boolean getIsForecastable() {
        return isForecastable;
    }

    public void setIsForecastable(boolean isForecastable) {
        this.isForecastable = isForecastable;
    }

    public Integer getForecastSpeed() {
        return forecastSpeed;
    }

    public void setForecastSpeed(Integer forecastSpeed) {
        this.forecastSpeed = forecastSpeed;
    }

    public Integer getForecastAccuracy() {
        return forecastAccuracy;
    }

    public void setForecastAccuracy(Integer forecastAccuracy) {
        this.forecastAccuracy = forecastAccuracy;
    }
    public boolean getIs_Attribute() {
        return is_Attribute;
    }

    public void setIs_Attribute(boolean is_Attribute) {
        this.is_Attribute = is_Attribute;
    }
    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public String getLinkToDb() {
        return linkToDb;
    }

    public void setLinkToDb(String linkToDb) {
        this.linkToDb = linkToDb;
    }

    public int getIdNumericType() {
        return idNumericType;
    }

    public void setIdNumericType(int idNumericType) {
        this.idNumericType = idNumericType;
    }

    public int getErrorToleranceValue() {
        return errorToleranceValue;
    }

    public void setErrorToleranceValue(int errorToleranceValue) {
        this.errorToleranceValue = errorToleranceValue;
    }

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public boolean isIndexIncremental() {
		return indexIncremental;
	}

	public void setIndexIncremental(boolean indexIncremental) {
		this.indexIncremental = indexIncremental;
	}

	/**
	 * @return the schemaFieldCheckTypeSet
	 */
	public Set<SchemaFieldCheckTypesEntity> getSchemaFieldCheckTypeSet() {
		return schemaFieldCheckTypeSet;
	}

	/**
	 * @param schemaFieldCheckTypeSet the schemaFieldCheckTypeSet to set
	 */
	public void setSchemaFieldCheckTypeSet(
			Set<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeSet) {
		this.schemaFieldCheckTypeSet = schemaFieldCheckTypeSet;
	} 
}