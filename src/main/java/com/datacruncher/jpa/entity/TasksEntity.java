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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "jv_tasks")
@NamedQueries({
	@NamedQuery(name="TasksEntity.findAll", query="SELECT e FROM TasksEntity e ORDER BY e.id DESC"),
	@NamedQuery(name="TasksEntity.countDuplicateByName", query="SELECT COUNT(e) FROM TasksEntity e WHERE e.id != :id AND e.name = :name"),
    @NamedQuery(name="TasksEntity.findByName", query="SELECT e from TasksEntity e Where e.name = :name")
})
public class TasksEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "minute")
	private int minute = -1;
	
	@Column(name = "hour")
	private int hour = -1;	
	
	@Column(name = "day")
	private int day = -1;
	
	@Column(name = "month")
	private int month = -1;	
	
	@Column(name = "week")
	private int week = -1;		
	
	@Column(name = "is_periodically", nullable = false)
    private boolean isPeriodically = false;
	
	@Column(name = "is_oneshoot", nullable = false)
    private boolean isOneShoot = false;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "shoot_date")
    private Date shootDate;
    
    @Column(name = "shoot_time")
    private String shootTime;    
    
    @Column(name = "everysecond")
	private int everysecond = -1;	
    
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
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
    
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}	
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}	
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}	
	
	public int getWeek() {
		return week;
	}
	
	public void setWeek(int week) {
		this.week = week;
	}
	
	public Date getShootDate() {
		return shootDate;
	}
	public void setShootDate(Date shootDate) {
		this.shootDate = shootDate;
	}
	
	public String getShootTime() {
		return shootTime;
	}
	public void setShootTime(String shootTime) {
		this.shootTime = shootTime;
	}

	public boolean getIsPeriodically() {
		return isPeriodically;
	}
	public void setIsPeriodically(boolean isPeriodically) {
		this.isPeriodically = isPeriodically;
	}	
	
	public boolean getIsOneShoot() {
		return isOneShoot;
	}
	public void setIsOneShoot(boolean isOneShoot) {
		this.isOneShoot = isOneShoot;
	}

	public int getEverysecond() {
		return everysecond;
	}

	public void setEverysecond(int everysecond) {
		this.everysecond = everysecond;
	}	
}