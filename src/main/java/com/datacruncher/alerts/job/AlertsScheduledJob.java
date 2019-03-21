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
package com.datacruncher.alerts.job;

import com.datacruncher.constants.AlertAuditStatus;
import com.datacruncher.constants.Alerts;
import com.datacruncher.constants.GenericType;
import com.datacruncher.jpa.entity.AlertsAuditEntity;
import com.datacruncher.jpa.entity.DatastreamEntity;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.spring.AppContext;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class AlertsScheduledJob extends QuartzJobBean implements DaoSet {

	private Logger log = Logger.getLogger(AlertsScheduledJob.class);
    private List<UserEntity> userList = null;
    private List<UserEntity> adminList= null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org
	 * .quartz.JobExecutionContext)
	 */

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		String logMsg = "AlertsScheduledJob:executeInternal:";
		AlertsJobDataProcessor alertsJobDataProcessor;
		try {
            if(applicationConfigDao.isAlertDispatcherMailSet()){
                if (getUser()) {
                    ReadList readList;
                    Date startDate = getJobStartDate();
                    Date endDate = new GregorianCalendar().getTime();
                    long hrsDiff = (endDate.getTime() - startDate.getTime()) / ((1000 * 60 * 60));
                    if (hrsDiff >= 24) {
                        long totStream;
                        readList = datastreamsDao.getInvalidDataStreams(startDate,endDate);

                        if (readList.getResults() != null && readList.getResults().size() > 0) {
                            if(readList.getResults().size() == GenericType.maxEmailStream){
                                totStream = datastreamsDao.countInvalidDataStreams(startDate,endDate);
                            } else{
                                totStream = readList.getResults().size();
                            }
                            // insert entry into AlertAudit Table
                            AlertsAuditEntity alertAuditEntity = new AlertsAuditEntity();
                            alertAuditEntity.setJobStartDate(startDate);
                            alertAuditEntity.setJobEndDate(endDate);
                            alertAuditEntity.setStreamsFound(totStream);
                            alertAuditEntity.setStatus(AlertAuditStatus.SUCCESS.getDbCode());
                            Create create = alertsAuditDao.create(alertAuditEntity);
                            if(create.getSuccess()){
                                if(log.isDebugEnabled()){
                                    log.debug(logMsg+"Audit record successfully insert in JV_ALERTS_AUDIT");
                                }
                            }
                            @SuppressWarnings("unchecked")
                            List<DatastreamEntity> dataStreams = (List<DatastreamEntity>) readList.getResults();
                            alertsJobDataProcessor = AppContext.getApplicationContext().getBean(AlertsJobDataProcessor.class);
                            alertsJobDataProcessor.setAlertType("Daily");
                            alertsJobDataProcessor.setStartDate(startDate);
                            alertsJobDataProcessor.setEndDate(endDate);
                            alertsJobDataProcessor.setUserList(userList);
                            alertsJobDataProcessor.setAdminList(adminList);
                            alertsJobDataProcessor.processJobData(dataStreams);

                        } else {
                            log.info(logMsg + "No DataStreams found to process");
                        }

                    }else{
                        log.info(logMsg+"Job execution is skipping since the difference between last run and current should be 24 hrs.");
                    }
                } else {
                    log.debug(logMsg + " No Users found to process");
                }
            }
		} catch (Exception e) {
			log.error(logMsg + "Exception :" + e.getMessage(), e);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}

	// ---------------HELPERS-----------------
	private Date getJobStartDate(){
		try{
			Calendar startDate = new GregorianCalendar();
			ReadList readList = alertsAuditDao.getMaxJobEndDate();
			if (readList.getResults() != null && readList.getResults().size() > 0) {
				if (readList.getResults().get(0) != null) {
					return (Date)readList.getResults().get(0);
				}
			}
			startDate.add(Calendar.DATE, -1);
			return startDate.getTime();
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return null;
	}
    @SuppressWarnings(value = "unchecked")
	private boolean getUser() {
		boolean ret = false;
		String criteria = "( u.idAlert IN ( ";
		Alerts[] alerts = Alerts.values();
		for (Alerts alert : alerts) {
			if (alert.getDbName().contains("one_a_day"))
				criteria += alert.getDbCode() + ",";
		}
		criteria = criteria.substring(0, criteria.length() - 1) + "))";

		ReadList AdminReadList = usersDao.getAdminUsers(" AND " + criteria);
		ReadList readList = usersDao.getDataStreamUsers(criteria, -1);
		if (AdminReadList.getResults() != null && AdminReadList.getResults().size() > 0) {
			adminList = (List<UserEntity>) AdminReadList.getResults();
			ret = true;
		}
		if (readList.getResults() != null && readList.getResults().size() > 0) {
			userList = (List<UserEntity>) readList.getResults();
			ret = true;
		}
		return ret;
	}
}