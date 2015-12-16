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

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jv_applications")
@NamedQueries({
    @NamedQuery(name = "ApplicationEntity.findAll", query = "SELECT t FROM ApplicationEntity t"),
    @NamedQuery(name = "ApplicationEntity.findByIdApplication", query = "SELECT t FROM ApplicationEntity t WHERE "
    + "t.idApplication = :idApplication"),
    @NamedQuery(name = "ApplicationEntity.findByName", query = "SELECT t FROM ApplicationEntity t WHERE t.name = :name"),
    @NamedQuery(name = "ApplicationEntity.findByUserId", query ="SELECT a FROM ApplicationEntity a, UserApplicationsEntity ua " +
    		" where ua.idApplication=a.idApplication and ua.idUser= :idUser ORDER BY a.idApplication DESC"),
    @NamedQuery(name = "ApplicationEntity.findAllDescOrder" , query="SELECT a FROM ApplicationEntity a ORDER BY a.idApplication DESC"),
    @NamedQuery(name = "ApplicationEntity.countByAppIdAndName" , query = "SELECT COUNT (a) FROM ApplicationEntity a WHERE " +
    		" a.idApplication != :idApplication AND a.name = :name"),
    @NamedQuery(name = "ApplicationEntity.count", query = "SELECT COUNT (j) FROM ApplicationEntity j  WHERE j.name <> 'system_app'"),
    @NamedQuery(name = "ApplicationEntity.isSysAppExists", query = "SELECT COUNT (j) FROM ApplicationEntity j WHERE j.name='system_app'"),
    @NamedQuery(name = "ApplicationEntity.getSysApp", query = "SELECT j FROM ApplicationEntity j WHERE j.name='system_app'")
})

public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_application")
    private long idApplication;
    
    @Column(name = "name" , unique = true)
    private String name;
    
    @Lob
    @Column(name = "description")
    private String description;
	
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;
    
    @Column(name = "is_active")
	private Integer isActive = 1;
	
    @Column(name = "is_planned", nullable = false)
    private boolean isPlanned = false;
    
    @Column(name = "planned_name")
    private long plannedName;
    
    @Column(name = "is_site_generated", nullable = false)
    private boolean isSiteGenerated = false;
    
    @Transient
    private List<UserApplicationsEntity> userApplications  = new ArrayList<UserApplicationsEntity>();
    /**
	 * 
	 */
	public ApplicationEntity() {
	}
	
	public ApplicationEntity(long idApplication) {
		this.idApplication = idApplication;
	}
	

	/**
	 * @param idApplication
	 * @param name
	 * @param description
	 * @param startDate
	 * @param endDate
	 */
	public ApplicationEntity(long idApplication, String name,
			String description, Date startDate, Date endDate) {
		this.idApplication = idApplication;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	
    public long getIdApplication() {
        return idApplication;
    }
    
    public void setIdApplication(long idApplication) {
        this.idApplication = idApplication;
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
	/**
	 * @return the userApplications
	 */
	@OneToMany(fetch = FetchType.LAZY,  cascade = {CascadeType.ALL}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<UserApplicationsEntity> getUserApplications() {
		return userApplications;
	}
	/**
	 * @param userApplications the userApplications to set
	 */
	public void setUserApplications(List<UserApplicationsEntity> userApplications) {
		this.userApplications = userApplications;
	}
	
	public Integer getIsActive() {
		return isActive;
	}
	
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public boolean getIsPlanned() {
		return isPlanned;
	}

	public void setPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public long getPlannedName() {
		return plannedName;
	}

	public void setPlannedName(long plannedName) {
		this.plannedName = plannedName;
	}

	public boolean getIsSiteGenerated() {
		return isSiteGenerated;
	}

	public void setIsSiteGenerated(boolean isSiteGenerated) {
		this.isSiteGenerated = isSiteGenerated;
	}	
}