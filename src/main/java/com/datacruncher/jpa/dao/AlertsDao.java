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
package com.datacruncher.jpa.dao;

import com.datacruncher.constants.Alerts;
import com.datacruncher.jpa.entity.AlertEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.ReadList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class AlertsDao {
	Logger log = Logger.getLogger(this.getClass());

	@PersistenceContext
    private EntityManager em;

	/**
	 * @return ReadList
	 */
    @Transactional(readOnly = true)
	public ReadList read() {
		String logMsg = "AlertsDao:read():";
		log.debug(logMsg + "Entry");
		ReadList readList = new ReadList();
		try {
			try {
				readList.setResults(em.createNamedQuery("AlertEntity.findAll").getResultList());
			} catch (Exception exception) {
				log.error(logMsg + "Exception: " + exception);
				readList.setSuccess(false);
				readList.setMessage(I18n.getMessage("error.error") + "  : AlertsDao - read");
				return readList;
			}
		} finally {
			log.debug(logMsg + "Exit");
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	/**
	 * Initialize Alerts.
	 */
    @Transactional
	public void init() {
		String logMsg = "AlertsDao:init():";
		AlertEntity alertEntry;
		try {
			log.debug(logMsg + "Entry");
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("AlertEntity.count").getResultList();
			if (result.get(0).longValue() == 0L) {
				Alerts[] alerts = Alerts.values();
				for (Alerts alert : alerts) {
					alertEntry = new AlertEntity(alert.getDbCode(), alert.getDbName());
					em.persist(alertEntry);
				}
			}
		} catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(logMsg + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}
}
