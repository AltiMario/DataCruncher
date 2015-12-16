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

package com.seer.datacruncher.services.scheduler;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ConnectionsEntity;
import com.seer.datacruncher.jpa.entity.JobsEntity;
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerSetStatusJob implements DaoSet {
    private static Logger log = Logger.getLogger(PlannerSetStatusJob.class);
    private PlannerJobMap plannerJobMap;

    public PlannerSetStatusJob() {
        this.plannerJobMap = (PlannerJobMap) AppContext.getApplicationContext().getBean("plannerJobMapBean");
    }

    public boolean activeJob(long jobId){
        int isActive = 1;
        return setStatusJob(jobId,isActive);
    }
    public boolean disableJob(long jobId){
        int isActive = 0;
        return setStatusJob(jobId,isActive);
    }
    private boolean setStatusJob(long jobId,int isActive){
        Map<String, String> resMap = new HashMap<String, String>();
        boolean isSuccess = true;
        JobsEntity jobEnt = jobsDao.find(jobId);
        String errMsg;
        if(jobEnt.getIdEventTrigger() > 0) {
            if (jobEnt.getIdScheduler() == 0) {
                isSuccess = doErrorActions(resMap, "error.jobNoPlannerField", null);
            } else if ((errMsg = isPlannerCorrect(tasksDao.find(jobEnt.getIdScheduler()))) != null) {
                resMap.put("msg", errMsg);
                isSuccess = false;
            }
        } else {
            if (jobEnt.getIdApplication() == 0) {
                resMap.put("msg",
                        MessageFormat.format(I18n.getMessage("error.emptyField"), I18n.getMessage("db_fields.application")));
                isSuccess = false;
            } else if (jobEnt.getIdSchema() == 0) {
                resMap.put("msg",
                        MessageFormat.format(I18n.getMessage("error.emptyField"), I18n.getMessage("db_fields.schemas")));
                isSuccess = false;
            } else if (jobEnt.getIdScheduler() == 0) {
                isSuccess = doErrorActions(resMap, "error.jobNoPlannerField", null);
            } else if (jobEnt.getIdConnection() != 0 &&(!isConnectionSuccess(connectionsDao.find(jobEnt.getIdConnection())))) {
                isSuccess = doErrorActions(resMap, "error.jobNoConnection", null);
            } else if ((errMsg = isPlannerCorrect(tasksDao.find(jobEnt.getIdScheduler()))) != null) {
                resMap.put("msg", errMsg);
                isSuccess = false;
            }
        }

        if(isSuccess) {
            PlannerJob plannerJob = plannerJobMap.get(jobId);
            if (isActive == 1) {
                try {
                    plannerJob.schedule();
                    resMap.put("msg", I18n.getMessage("success.jobActivated"));
                } catch (SchedulerException e) {
                    isSuccess = doErrorActions(resMap, "error.jobNotScheduled", e);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    plannerJob.unschedule();
                    resMap.put("msg", I18n.getMessage("success.jobDeactivated"));
                } catch (SchedulerException e) {
                    isSuccess = doErrorActions(resMap, "error.jobNotUnScheduled", e);
                }
            }
        }
        if(isSuccess) {
            jobsDao.setActive(jobId, isActive);
        }
        resMap.put("success", isSuccess ? "true" : "false");
        log.debug(resMap.get("msg"));
        return isSuccess;
    }

    private String isPlannerCorrect(TasksEntity ent) {
        int m = ent.getMinute();
        int h = ent.getHour();
        int d = ent.getDay();
        int month = ent.getMonth();
        int week = ent.getWeek();
        String value = "value";
        String nlsId = "nlsId";
        List<Map<String, String>> list = new ArrayList<Map<String,String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put(value, String.valueOf(m));
        map.put(nlsId, "minute"); //minute's nls not used
        list.add(map);
        map = new HashMap<String, String>();
        map.put(value, String.valueOf(h));
        map.put(nlsId, "db_fields.hour");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(value, String.valueOf(d));
        map.put(nlsId, "db_fields.days");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(value, String.valueOf(month));
        map.put(nlsId, "db_fields.month");
        list.add(map);
        boolean isStarted = false;
        int i = 0;
        String resStr = "";
        for (Map<String, String> mapL : list) {
            int j = Integer.parseInt(mapL.get(value));
            if (isStarted && j == -1 && !(i == 2 && week != -1)) {
                resStr += I18n.getMessage(mapL.get(nlsId)) + ", ";
            }
            if (Integer.parseInt(mapL.get(value)) != -1) isStarted = true;
            i++;
        }
        if (!resStr.isEmpty())
            resStr = MessageFormat.format(I18n.getMessage("error.plannerIncomplete"),
                    resStr.substring(0, resStr.length() - 2));
        return resStr.isEmpty() ? null : resStr;
    }

    /**
     * Checks whether connection is established.
     *
     * @param ent - ConnectionEntity
     * @return true/false
     */
    private boolean isConnectionSuccess(ConnectionsEntity ent) {
        boolean success;
        success = ent == null || checkServiceRunning(ent);
        return success;
    }
    private boolean checkServiceRunning(ConnectionsEntity conn) {

        boolean success = true;
        String url = "";
        DefaultFileSystemManager fsManager =  null;
        String userName = "";
        String password = "";
        String hostName = "";
        String port = "";
        String inputDirectory = "";
        String fileName = "";

        if(conn != null) {
            userName = conn.getUserName();
            password = conn.getPassword();
            hostName = conn.getHost();
            port = conn.getPort();
            inputDirectory = conn.getDirectory();
            fileName = conn.getFileName();
        }


        if(conn.getIdConnType() ==  GenericType.DownloadTypeConn ){
            if(fileName == null || fileName.trim().length() == 0) {
                return false;
            }
            inputDirectory = inputDirectory+ "/" + fileName;
        }
        try {
            fsManager = (DefaultFileSystemManager) VFS.getManager();
            if(conn.getService() == Servers.SAMBA.getDbCode()) {
                if(!fsManager.hasProvider("smb")) {
                    fsManager.addProvider("smb", new SmbFileProvider());
                }
                url = "smb://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory;
            } else if(conn.getService() == Servers.HTTP.getDbCode()) {
                if(!fsManager.hasProvider("http")) {
                    fsManager.addProvider("http", new HttpFileProvider());
                }
                url = "http://" + hostName + ":" + port + "/" + inputDirectory;
            }else if( conn.getService() == Servers.FTP.getDbCode()){
                if(!fsManager.hasProvider("ftp")) {
                    fsManager.addProvider("ftp", new FtpFileProvider());
                }
                url = "ftp://" + userName + ":" + password + "@" + hostName + ":" + port + "/" + inputDirectory;
            }

            final FileObject fileObject = fsManager.resolveFile(url);

            if(fileObject == null || !fileObject.exists()) {
                success = false;
            }

        } catch(Exception ex) {
            success = false;
        } finally {
            if(fsManager != null)
                fsManager.close();
        }
        return success;
    }
    
    private boolean doErrorActions(Map<String, String> resMap, String msg, Exception e) {
        resMap.put("msg", I18n.getMessage(msg));
        if (e != null) log.error(e);
        return false;
    }
}
