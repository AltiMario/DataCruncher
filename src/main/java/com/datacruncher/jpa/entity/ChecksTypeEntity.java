
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

import javax.persistence.*;

@Entity
@Table(name = "jv_checks_types")

@NamedNativeQuery(
		name="ChecksTypeEntity.findAllComplete",
		query="SELECT id_check_type, token_rule, name, description, tags, is_system_rule, " +
				"nls_id, value, class_name, extra_check_type FROM jv_checks_types "+
				"UNION SELECT ((SELECT COUNT(*) FROM jv_checks_types)+id_macro) as id_check_type, null as token_rule, name, description, tags, true as is_system_rule , " +
				" null as nls_id, " +
				"'MACRO' as value, null as class_name, null as extra_check_type " +
				"FROM jv_macros ORDER BY name ASC",
		resultClass=ChecksTypeEntity.class
)

@NamedQueries({
		@NamedQuery(name = "ChecksTypeEntity.findAll", query         		= "SELECT j FROM ChecksTypeEntity j ORDER BY j.name ASC"),
		@NamedQuery(name = "ChecksTypeEntity.count", query           		= "SELECT COUNT (j) FROM ChecksTypeEntity j"),
		@NamedQuery(name = "ChecksTypeEntity.findAllRegExps", query         = "SELECT j FROM ChecksTypeEntity j where j.extraCheckType='Regular Expression' ORDER BY j.name ASC"),
		@NamedQuery(name = "ChecksTypeEntity.findAllCustomCodes", query     = "SELECT j FROM ChecksTypeEntity j where j.extraCheckType='Custom Code' ORDER BY j.name ASC"),
		@NamedQuery(name = "ChecksTypeEntity.findCustomCodesByName", query  = "SELECT j FROM ChecksTypeEntity j where j.extraCheckType='Custom Code' AND j.name=:name"),
		@NamedQuery(name = "ChecksTypeEntity.countRegExps", query 			= "SELECT COUNT (j) FROM ChecksTypeEntity j where j.extraCheckType='Regular Expression'"),
		@NamedQuery(name = "ChecksTypeEntity.findByIdChecksType", query 	= "SELECT j FROM ChecksTypeEntity j WHERE j.idCheckType = :idCheckType"),
		@NamedQuery(name = "ChecksTypeEntity.findByTokenRule", query	    = "SELECT j FROM ChecksTypeEntity j WHERE j.tokenRule = :tokenRule"),
		@NamedQuery(name = "ChecksTypeEntity.findByName", query 			= "SELECT j FROM ChecksTypeEntity j WHERE j.name = :name"),
		@NamedQuery(name = "ChecksTypeEntity.countSpellChecks", query 		= "SELECT COUNT(j) FROM ChecksTypeEntity j WHERE j.tokenRule LIKE '%spellcheck%'"),
		@NamedQuery(name = "ChecksTypeEntity.findSpellChecks", query		= "SELECT j FROM ChecksTypeEntity j WHERE j.tokenRule LIKE '%spellcheck%'"),
		@NamedQuery(name = "ChecksTypeEntity.findBySchemaFieldId", query	= "SELECT j FROM ChecksTypeEntity j, SchemaFieldCheckTypesEntity S WHERE j.idCheckType = s.idCheckType AND s.schemaFieldEntity.idSchemaField=:idSchemaField"),
		@NamedQuery(name = "ChecksTypeEntity.findLogicalCheckBySchemaFieldId", query  = "SELECT c  FROM ChecksTypeEntity c , SchemaFieldCheckTypesEntity  t WHERE  t.schemaFieldEntity.idSchemaField = :idSchemaField AND t.idCheckType = c.idCheckType AND c.tokenRule != null AND (c.className != null OR c.tokenRule='@spellcheck') ORDER BY c.className ASC")
})

public class ChecksTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "id_check_type")
	private long idCheckType;

	@Basic(optional = true)
	@Column(name = "token_rule")
	private String tokenRule;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "tags")
	private String tags;

	@Column(name = "is_system_rule", nullable = false)
	private boolean isSystemRule = true;

	@Transient
	private boolean isRegExp = true;

	@Column(name = "extra_check_type", nullable = false)
	private String extraCheckType;

	@Column(name = "nls_id")
	private String nlsId;

	@Lob
	@Column(name = "value")
	private String value;

	@Transient
	private String type;

	@Column(name = "class_name")
	private String className;

	public ChecksTypeEntity() {
	}

	public ChecksTypeEntity(String tokenRule, String name, String description, String tags, String extraCheckType, boolean isSystemRule, String nlsId, String value, String className) {
		this.tokenRule = tokenRule;
		this.name = name;
		this.description = description;
		this.tags = tags;
		this.extraCheckType = extraCheckType;
		this.isSystemRule = isSystemRule;
		this.nlsId = nlsId;
		this.value = value;
		this.className = className;
	}

	public long getIdCheckType() {
		return idCheckType;
	}

	public void setIdCheckType(long idCheckType) {
		this.idCheckType = idCheckType;
	}

	public String getTokenRule() {
		return tokenRule;
	}

	public void setTokenRule(String tokenRule) {
		this.tokenRule = tokenRule;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getNlsId() {
		return nlsId;
	}

	public void setNlsId(String nlsId) {
		this.nlsId = nlsId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSystemRule() {
		return isSystemRule;
	}

	public void setSystemRule(boolean isSystemRule) {
		this.isSystemRule = isSystemRule;
	}

	public boolean isRegExp() {
		return isRegExp;
	}

	public void setRegExp(boolean isRegExp) {
		this.isRegExp = isRegExp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the extraCheckType
	 */
	public String getExtraCheckType() {
		return extraCheckType;
	}

	/**
	 * @param extraCheckType the extraCheckType to set
	 */
	public void setExtraCheckType(String extraCheckType) {
		this.extraCheckType = extraCheckType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this.idCheckType == ((ChecksTypeEntity)obj).getIdCheckType()){
			return true;
		}
		return false;
	}


}
