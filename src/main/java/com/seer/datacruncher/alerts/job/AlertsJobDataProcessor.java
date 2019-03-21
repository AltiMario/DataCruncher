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
package com.seer.datacruncher.alerts.job;

import com.seer.datacruncher.constants.Alerts;
import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;

import java.util.*;

import org.apache.log4j.Logger;


public class AlertsJobDataProcessor implements DaoSet {
	
	private Logger log = Logger.getLogger(AlertsJobDataProcessor.class);
	private Map<String,AlertsDataHandler> alertsDataHandlers;
	private String alertType;
    private Date startDate;
    private Date endDate;
    private List<UserEntity> userList;
    private List<UserEntity> adminList;


    /**
	 * @param allStreams  --
	 */
    public void processJobData(List<DatastreamEntity> allStreams){
        String logMsg = "AlertsJobDataProcessor:processDailyJobData:";
        AlertsDataHandler alertsDataHandler;
        long totStream=0L;
        try{
            if("Daily".equalsIgnoreCase(getAlertType())){

                if(allStreams!=null && allStreams.size()>0){
                    if(allStreams.size() == GenericType.maxEmailStream){
                        totStream = datastreamsDao.countInvalidDataStreams(startDate,endDate);
                    }else{
                        totStream = allStreams.size();
                    }
                    if(getAdminList()!=null && getAdminList().size()>0){
                        // All Admin
                        alertsDataHandler = alertsDataHandlers.get("All");
                        alertsDataHandler.setAlertType(getAlertType());
                        alertsDataHandler.setUserList(getAdminList());
                        alertsDataHandler.setTotStream((int)totStream);
                        alertsDataHandler.handle(allStreams);

                    }
                    if(getUserList()!=null && getUserList().size()>0){
                        List<UserEntity> userList = getUserList();
                        for (UserEntity anUserList : userList) {
                            List<UserEntity> users = new ArrayList<UserEntity>();
                            totStream=0L;
                            users.add(anUserList);
                            String sStatus = getStreamStatus((int) anUserList.getIdAlert());
                            @SuppressWarnings("unchecked")
                            ReadList dataStreamList= datastreamsDao.getInvalidDataStreamsByUser(
                                    getStartDate(), getEndDate(), anUserList,
                                    sStatus);
                            if(allStreams.size() == GenericType.maxEmailStream){
                                totStream = datastreamsDao.countInvalidDataStreamsByUser(
                                        getStartDate(), getEndDate(), anUserList,sStatus);
                            }
                            if (dataStreamList.getResults() != null && dataStreamList.getResults().size() > 0) {
                                @SuppressWarnings("unchecked")
                                List<DatastreamEntity> dataStreams = (List<DatastreamEntity>) dataStreamList.getResults();
                                alertsDataHandler = alertsDataHandlers.get("All");
                                alertsDataHandler.setAlertType(getAlertType());
                                alertsDataHandler.setUserList(users);
                                if(totStream>GenericType.maxEmailStream)
                                    alertsDataHandler.setTotStream((int)totStream);
                                alertsDataHandler.handle(dataStreams);
                            }
                        }
                    }
                }
            }else{
                alertsDataHandler = alertsDataHandlers.get("All");
                alertsDataHandler.setAlertType(getAlertType());
                alertsDataHandler.setUserList(userList);
                alertsDataHandler.handle(allStreams);
            }
        } catch(Exception e){
            log.error(logMsg+"Exception:"+e.getMessage(),e);
        } finally{
            log.debug(logMsg+"Exit");
        }
    }
	//------------SETTERS & GETTERS ---------------
	/**
	 * @param alertsDataHandlers the alertsDataHandlers to set, filled from spring context
	 */
	public void setAlertsDataHandlers(
			Map<String, AlertsDataHandler> alertsDataHandlers) {
		this.alertsDataHandlers = alertsDataHandlers;
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

    public List<UserEntity> getUserList() {
        return userList;
    }

    public void setUserList(List<UserEntity> userList) {
        this.userList = userList;
    }

    public List<UserEntity> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<UserEntity> adminList) {
        this.adminList = adminList;
    }

    protected String getStreamStatus(int dbCode){
        Alerts alerts = Alerts.getAlert(dbCode);
        String alertType = StreamStatus.Invalid.getDbCode()+ ","+StreamStatus.Warning.getDbCode();
        if("Errors_one_a_day".equalsIgnoreCase(alerts.getDbName())){
            alertType = StreamStatus.Invalid.getDbCode();
        } else if("Warning_one_a_day".equalsIgnoreCase(alerts.getDbName())){
            alertType = StreamStatus.Warning.getDbCode();
        }
        return alertType;
    }
}
