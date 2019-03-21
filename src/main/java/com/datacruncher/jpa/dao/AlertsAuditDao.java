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
package com.datacruncher.jpa.dao;

import com.datacruncher.jpa.entity.AlertsAuditEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.ReadOnlyTx;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AlertsAuditDao {
	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

	protected AlertsAuditDao(){}
	
	/**
	 * @return ReadList
	 */
    @ReadOnlyTx
    @SuppressWarnings("unchecked")
	public ReadList getMaxJobEndDate() {
		ReadList readList = new ReadList();
		try {
			List<AlertsAuditEntity> result = em.createNamedQuery("AlertsAuditEntity.findMaxEndDate").getResultList();
			if (result != null && result.size() > 0) {
				readList.setSuccess(true);
				readList.setResults(result);
				readList.setMessage(null);
			}
		} catch (Exception exception) {
			log.error("AlertsAuditDao - getMaxJobEndDate : " + exception);
			readList.setSuccess(false);
			readList.setMessage(null);
			return readList;
		}
		return readList;
	}

    public Create create(AlertsAuditEntity alertAuditEntity) {
		Create create = new Create();
		try {
            commonDao.persist(alertAuditEntity);
            create.setSuccess(true);
            create.setResults(alertAuditEntity);
            create.setMessage(I18n.getMessage("success.insRecord"));
            return create;
        } catch (Exception exception) {
			log.error("AlertsAuditDao - create : " + exception);
            create.setSuccess(false);
			create.setResults(alertAuditEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
            return create;
		}

	}
}
