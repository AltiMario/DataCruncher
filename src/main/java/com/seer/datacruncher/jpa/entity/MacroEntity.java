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

import javax.persistence.*;

@Entity
@Table(name = "jv_macros")
@NamedQueries({
	@NamedQuery(name = "MacroEntity.findByName", query              = "SELECT d FROM MacroEntity d where d.name=:name"),
	@NamedQuery(name = "MacroEntity.findBySchemaId", query          = "SELECT d FROM MacroEntity d where d.idSchema = :schemaId ORDER BY d.idMacro ASC "),
	@NamedQuery(name = "MacroEntity.countDuplicateByName", query    = "SELECT COUNT (d) FROM MacroEntity d WHERE d.idMacro <> :idMacro AND d.name = :name") ,
    @NamedQuery(name = "MacroEntity.findAll", query                 = "SELECT j FROM MacroEntity j ORDER BY j.name ASC"),
    @NamedQuery(name = "MacroEntity.count", query                   = "SELECT COUNT (j) FROM MacroEntity j")
})
public class MacroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_macro")
    private long idMacro;
    
	@Column(name = "name" , unique = true)
    private String name;
	
	@Lob
    @Column(name = "description")
    private String description;
	
	@Lob
    @Column(name = "vars")
    private String vars;	
	
	@Column(name = "id_schema")
	private long idSchema;
	
	@Lob
	@Column(name = "rule")
	private String rule;	
	
	@Lob
	@Column(name = "rule_simple")
	private String ruleSimple;	
	
	@Column(name = "is_active", columnDefinition="int default 1", nullable = false)
	private int isActive;	
	
	@Column(name = "error_type")
	private int errorType;	
	
	public long getIdSchema() {
		return idSchema;
	}

	public void setIdSchema(long idSchema) {
		this.idSchema = idSchema;
	}

	public long getIdMacro() {
		return idMacro;
	}

	public void setIdMacro(long idMacro) {
		this.idMacro = idMacro;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getRuleSimple() {
		return ruleSimple;
	}

	public void setRuleSimple(String ruleSimple) {
		this.ruleSimple = ruleSimple;
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
    
	public String getVars() {
        return vars;
    }

    public void setVars(String vars) {
        this.vars = vars;
    }
    
	public int getIsActive() {
		return isActive;
	}
	
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}    
}