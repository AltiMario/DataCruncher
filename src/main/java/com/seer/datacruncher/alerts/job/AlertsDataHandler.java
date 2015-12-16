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
package com.seer.datacruncher.alerts.job;

import com.seer.datacruncher.alerts.AlertDispatcher;
import com.seer.datacruncher.constants.Alerts;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;


import java.util.List;
import java.util.Map;

public abstract class AlertsDataHandler {
	
	protected Map<String,AlertDispatcher> alertsDispatchMethods;
	protected String alertType;
    protected List<UserEntity> userList;
    protected int totStream =0;
	/**
	 * @param dataStream -
	 * @throws Exception  -
	 */
	public abstract void handle(List<DatastreamEntity> dataStream) throws Exception;


    /*
	protected String getCriteria(List<DatastreamEntity> dataStream,StreamStatus[] streamStatuses,Alerts alert){
		StringBuilder criteria = new StringBuilder();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

		criteria.append("(");

        for (DatastreamEntity datastreamEntity : dataStream) {
            if(criteria.toString().length()>2){
                criteria.append(" OR ");
            }
            criteria.append(" d.idDatastream = ").append(datastreamEntity.getIdDatastream());
        }

		criteria.append(") ");
		criteria.append(" AND ");
		StringBuilder sCriteria = new StringBuilder("(");
		for (StreamStatus streamStatus : streamStatuses) {
			if(sCriteria.toString().length()>2){
				sCriteria.append(" OR ");
			}
			sCriteria.append(" d.checked=").append(streamStatus.getDbCode());
		}
		sCriteria.append(")");
		criteria.append(sCriteria.toString());
		criteria.append(" AND u.idAlert=").append(alert.getDbCode());
		return criteria.toString();
	}
	//----------HELPERS----------------
	protected Alerts getUserAlert(String streamStatus){
		Alerts alerts;
		if("Daily".equalsIgnoreCase(getAlertType())){
			if("Invalid".equalsIgnoreCase(streamStatus)){
				alerts = Alerts.ERRORS_ONE_A_DAY;
			}else if("Warning".equalsIgnoreCase(streamStatus)){
				alerts = Alerts.WARNING_ONE_A_DAY;
			}else {
				alerts = Alerts.ALL_ONE_A_DAY;
			}
		}else{
			if("Invalid".equalsIgnoreCase(streamStatus)){
				alerts = Alerts.ERRORS_EVERY_TIME;
			}else if("Warning".equalsIgnoreCase(streamStatus)){
				alerts = Alerts.WARNINGS_EVERY_TIME;
			}else {
				alerts = Alerts.ALL_EVERY_TIME;
			}
		}
		return alerts;
	}*/
	
	//----------SETTERS--------------
	/**
	 * @param alertsDispatchMethods the alertsDispatchMethods to set
	 */
	public void setAlertsDispatchMethods(
			Map<String, AlertDispatcher> alertsDispatchMethods) {
		this.alertsDispatchMethods = alertsDispatchMethods;
	}

	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}

	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
    }

    public int getTotStream() {
        return totStream;
    }

    public void setTotStream(int totStream) {
        this.totStream = totStream;
    }
}
