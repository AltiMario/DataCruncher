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

import com.datacruncher.constants.Servers;
import com.datacruncher.jpa.entity.ServersEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ServersDao {

	Logger log = Logger.getLogger(this.getClass());

	@PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected ServersDao() {
	}
    @Transactional(readOnly = true)
	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("ServersEntity.findAll").getResultList());
		} catch (Exception exception) {
			log.error("ServersDao - read: " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : ServersDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public void init() {
		ServersEntity entity;
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("ServersEntity.count").getResultList();
			if (count.get(0).longValue() == 0L) {
				Servers[] servers = Servers.values();
				int active = 1;
				for (Servers server : servers) {
					if(server.getDbCode() == 4 || server.getDbCode() == 5)
						continue;
					entity = new ServersEntity(server.getDbCode(), server.getName(), active);
                    commonDao.persist(entity);
				}
			}
		} catch (Exception exception) {
			log.error("ServersDao - init : " + exception);
		}
	}

	public Update update(ServersEntity serversEntity) {
		Update update = new Update();
		try {

			commonDao.update(serversEntity);
		} catch (Exception exception) {
			log.error("ServersDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}
	
	public void setActive(long serverId, int isActive) {
		ServersEntity ent = find(serverId);
		if (ent != null) {
			ent.setIsActive(isActive);
			update(ent);
		}
	}

    @Transactional(readOnly = true)
	public ServersEntity find(long idServer) {
		ServersEntity en = null;
		try {
			// insert server db entries if no records found in JV_SERVERS 
			init();
			en = em.find(ServersEntity.class, idServer);
		} catch (Exception exception) {
			log.error("ServersDao - find : " + exception);
		}
		return en;
	}
}