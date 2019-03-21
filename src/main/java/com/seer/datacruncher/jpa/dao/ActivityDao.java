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
package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.ReadOnlyTx;
import com.seer.datacruncher.jpa.entity.ActivityEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ActivityDao {
	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

	protected ActivityDao() {}
	/**
	 * @return ReadList
	 */
    @ReadOnlyTx
	public ReadList read() {
		String logMsg = "ActivityDao:read():";
		try {
			log.debug(logMsg + "Entry");
			ReadList readList = new ReadList();
			try {
				readList.setResults(em.createNamedQuery("ActivityEntity.findAll").getResultList());
			} catch (Exception exception) {
				log.error(logMsg + "Exception: " + exception);
				readList.setSuccess(false);
				readList.setMessage(I18n.getMessage("error.error") + "  : RoleDao - read");
				return readList;
			}
			readList.setSuccess(true);
			readList.setMessage(I18n.getMessage("success.listRecord"));
			return readList;
		} finally {
			log.debug(logMsg + "End");
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
    @Transactional
    public void init(){
		String logMsg = "ActivityDao:init():";
		ActivityEntity activityEntry;
		List<Long> result;
		try {
			log.debug(logMsg + "Entry");
			result = em.createNamedQuery("ActivityEntity.count").getResultList();
			if (result.get(0).longValue() == 0L) {
				Activity[] activities = Activity.values();
				for (Activity activity : activities) {
					activityEntry = new ActivityEntity(activity.getDbCode(), activity.getScriptCode(), activity.name());
                    em.persist(activityEntry);
				}
			}
		} catch (Exception exception) {
            log.error("ActivityDao - init : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}
}