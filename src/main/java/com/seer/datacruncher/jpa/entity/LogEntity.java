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


import com.seer.datacruncher.constants.LogJobStatus;
import com.seer.datacruncher.persistence.exeptions.IllegalArgumentExcpetion;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name="jv_log")

@NamedQueries({
        @NamedQuery(name = "LogEntity.deleteRows",query = "DELETE FROM LogEntity j WHERE  j.logDateTime <= :date "),
        @NamedQuery(name = "LogEntity.deleteAllRows",query = "DELETE FROM LogEntity j"),
        @NamedQuery(name = "LogEntity.selectAllRows",query = "SELECT model FROM LogEntity model order by idlog desc"),
        @NamedQuery(name = "LogEntity.selectId",query = "SELECT max(j.idlog)  FROM LogEntity j"),
        @NamedQuery(name = "LogEntity.deleteMaxRows",query = "DELETE FROM LogEntity j WHERE  j.idlog <= :idlog "),
        @NamedQuery(name = "LogEntity.countAll",query = "SELECT count (model) FROM LogEntity model" ),
        @NamedQuery(name = "LogEntity.findByIdLog", query = "SELECT j FROM LogEntity j WHERE j.idlog = :idLog"),
        @NamedQuery(name = "LogEntity.searchAllRows",query = "SELECT model FROM LogEntity model  where model.message like :message order by idlog desc"),
        @NamedQuery(name = "LogEntity.countSearchRows",query = "SELECT count (model) FROM LogEntity model where model.message like :message" )
        })

public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column (name= "id_log")
    private long idlog;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_timestamp")
    private Date logDateTime;

    @Column(name = "id_status")
    private int idStatus = 0;

    @Lob
    @Column (name= "message")
    private String message;

    @PrePersist
    @PreUpdate
    protected void prePersist() throws IllegalArgumentExcpetion {
        /*idStatus = 0 (info)
        * idStatus = 1 (error)
        * idStatus = 2 (warn)
        * */
        if(this.idStatus < LogJobStatus.logInfo || this.idStatus > LogJobStatus.logWarn){
            throw new IllegalArgumentExcpetion("The field \"ID status\" can only contain the following values: "+LogJobStatus.logInfo+", "+LogJobStatus.logError+", "+LogJobStatus.logWarn);
        }

    }

    public long getIdlog() {
        return idlog;
    }

    public void setIdlog(long idlog) {
        this.idlog = idlog;
    }

    public Date getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(Date logDateTime) {
        this.logDateTime = logDateTime;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
