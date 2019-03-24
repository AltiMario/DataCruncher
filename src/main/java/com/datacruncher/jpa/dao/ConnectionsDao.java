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

import com.datacruncher.jpa.*;
import com.datacruncher.jpa.entity.ConnectionsEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectionsDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    Logger log = Logger.getLogger(this.getClass());

	protected ConnectionsDao() {
	}

    @ReadOnlyTx
    public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("ConnectionsEntity.findAllInDescOrder").getResultList());
		} catch (Exception exception) {
			log.error("ConnectionsDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : ConnectionsDao - read");
			return readList;
		}
		readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public Create create(ConnectionsEntity entity) {
		Create create = new Create();
		if (entity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!checkName(entity.getId(), entity.getName())) {
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
			commonDao.persist(entity);
		} catch (Exception exception) {
            log.error("ConnectionsDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(entity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

    public Update update(ConnectionsEntity entity) {
		Update update = new Update();
		if (entity.getName().equals("")) {
			update.setSuccess(false);
			update.setResults(entity);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return update;
		}
		if (!checkName(entity.getId(), entity.getName())) {
			update.setSuccess(false);
			update.setResults(entity);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
			commonDao.update(entity);
		} catch (Exception exception) {
            log.error("ConnectionsDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

    public Destroy destroy(long id) {
		Destroy destroy = new Destroy();
		try {
                commonDao.remove(ConnectionsEntity.class,id);
		}   catch(EntityNotFoundException ex){
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.noFoundRecord"));
            destroy.setResults(null);
            return destroy;
        }   catch (Exception exception) {
			log.error("ConnectionsDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

    @ReadOnlyTx
	private boolean checkName(Long id, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("ConnectionsEntity.findDuplicateByName").setParameter("id", id)
					.setParameter("name", name).getResultList();
			if (result.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("ConnectionsDao - checkName : " + exception);
		}
		return false;
	}

    @ReadOnlyTx
	public ConnectionsEntity find(long id) {
		ConnectionsEntity entity = null;
		try {
			entity = em.find(ConnectionsEntity.class, id);
		} catch (Exception exception) {
			log.error("ConnectionsDao - find : " + exception);
		}
		return entity;
	}

    @ReadOnlyTx
    public ReadList readByIdConnType(int idConnType) {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("ConnectionsEntity.findAllByIdConnType")
                    .setParameter("idConnType", idConnType)
                    .getResultList());
        } catch (Exception exception) {
            log.error("ConnectionsDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : ConnectionsDao - readByIdConnType");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
}