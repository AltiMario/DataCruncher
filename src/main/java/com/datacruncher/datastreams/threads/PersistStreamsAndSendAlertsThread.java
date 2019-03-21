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

package com.datacruncher.datastreams.threads;

import com.datacruncher.alerts.job.AlertsForEveryTimeTask;
import com.datacruncher.constants.Alerts;
import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.DatastreamEntity;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.datacruncher.jpa.entity.UserEntity;
import org.apache.log4j.Logger;

public class PersistStreamsAndSendAlertsThread extends Thread implements DaoSet {

	private List<Future<Map<String, Object>>> futureList;
    private ExecutorService executor;
	private final static Logger log = Logger.getLogger(PersistStreamsAndSendAlertsThread.class);

	public PersistStreamsAndSendAlertsThread(List<Future<Map<String, Object>>> list, ExecutorService executor) {
		futureList = list;
		this.executor = executor;
	}

	@Override
	public void run() {
		try {
            Map<DatastreamEntity, List<UserEntity>> datastreamEntityList = new HashMap<DatastreamEntity, List<UserEntity>>();
			for (Future<Map<String, Object>> future : futureList) {
				Map<String, Object> map = future.get();
				Object o = map.get(DatastreamsInput._SINGLE_STREAMENT_RESP);
				if (o != null) {
					DatastreamEntity ent = (DatastreamEntity) map.get(DatastreamsInput._SINGLE_STREAMENT_RESP);
					datastreamsDao.create(ent);

                    if (ent.getChecked() != 1 && applicationConfigDao.isAlertDispatcherMailSet() && schemasDao.isAvailable(ent.getIdSchema())){
                        String name= schemasDao.find(ent.getIdSchema()).getName();
                        if(name == null)
                            name = ent.getIdSchema()+"";
                        ent.setSchemaName(name);
                        List<UserEntity> usersMail=getUser(ent);
                        if(usersMail != null && usersMail.size()>0){
                            datastreamEntityList.put(ent, usersMail);
                        }
                    }
				}
			}
            for (Map.Entry<DatastreamEntity, List<UserEntity>> entry : datastreamEntityList.entrySet()) {
                AlertsForEveryTimeTask alertTask = new AlertsForEveryTimeTask(entry.getKey(),entry.getValue() );
                executor.execute(new Thread(alertTask));
			}
		} catch (InterruptedException e) {
			log.error("PersistStreamsAndSendAlertsThread :: InterruptedException", e);
		} catch (ExecutionException e) {
			log.error("PersistStreamsAndSendAlertsThread :: ExecutionException", e);
		}
	}
    @SuppressWarnings(value = "unchecked")
    private List<UserEntity> getUser(DatastreamEntity ent){
        String criteria =" (u.idAlert = "+Alerts.getAlert("All_every_time").getDbCode()+
                " OR u.idAlert= ";
        if (ent.getChecked()== 0){
            criteria +=Alerts.getAlert("Errors_every_time").getDbCode()+")" ;
        } else if (ent.getChecked()== 2){
            criteria +=Alerts.getAlert("Warnings_every_time").getDbCode()+")" ;
        }
        ReadList AdminReadList = usersDao.getAdminUsers(" AND " + criteria);

        //criteria = " d.idDatastream = "+ ent.getIdDatastream()+ criteria;

        ReadList readList = usersDao.getDataStreamUsers(criteria, ent.getIdSchema());
        if (readList.getResults() != null && readList.getResults().size() > 0) {

            if (AdminReadList.getResults() != null && AdminReadList.getResults().size() > 0) {
                List<UserEntity> userList = (List<UserEntity>) readList.getResults();
                for (int i = 0; i < AdminReadList.getResults().size(); i++)
                    userList.add((UserEntity) AdminReadList.getResults().get(i));

                return userList;
            } else{
                return (List<UserEntity>) readList.getResults();
            }

        }else{
            if (AdminReadList.getResults() != null && AdminReadList.getResults().size() > 0)
                return (List<UserEntity>) AdminReadList.getResults();

        }
        return null;
    }
}
