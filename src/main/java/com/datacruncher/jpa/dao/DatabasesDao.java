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

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.jpa.entity.DatabaseEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabasesDao {

	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected DatabasesDao(){}
	
	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("DatabaseEntity.findAllInDescOrder").getResultList());
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("DatabasesDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : DatabasesDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public Create create(DatabaseEntity databaseEntity) {
		Create create = new Create();
		if (databaseEntity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(databaseEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!checkName(databaseEntity.getIdDatabase(), databaseEntity.getName())) {
			create.setSuccess(false);
			create.setResults(databaseEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
			commonDao.persist(databaseEntity);
		} catch (Exception exception) {
			log.error("DatabasesDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(null);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(databaseEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(DatabaseEntity databaseEntity) {
		Update update = new Update();
		if (databaseEntity.getName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " - " + I18n.getMessage("error.requiredField"));
			return update;
		}
		if (!checkName(databaseEntity.getIdDatabase(), databaseEntity.getName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
			commonDao.update(databaseEntity);
		} catch (Exception exception) {
            em.getTransaction().rollback();
			log.error("DatabasesDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		ConnectionPoolsSet.destroyPool(databaseEntity.getIdDatabase());
		return update;
	}

	public Destroy destroy(long idDatabase) {
		Destroy destroy = new Destroy();
		try {
			if ( em.createNamedQuery("SchemaEntity.findAllReferencing").setParameter("idDatabase", idDatabase).getResultList().size() > 0 ) {
	            destroy.setSuccess(false);
	            destroy.setMessage(I18n.getMessage("error.schemaInDB"));
	            destroy.setResults(null);
	            return destroy;
			}
			else {
	            commonDao.remove(DatabaseEntity.class, idDatabase);
			}
        } catch (EntityNotFoundException ex) {
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.schemaInDB"));
            destroy.setResults(null);
            return destroy;
        } catch(Exception exception) {
			log.error("DatabasesDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		ConnectionPoolsSet.destroyPool(idDatabase);		
		return destroy;
	}
	
	private boolean checkName(Long idDatabase, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("DatabaseEntity.findDuplicateByName")
					.setParameter("idDatabase", idDatabase).setParameter("name", name).getResultList();
			if (result.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("DatabasesDao - checkName : " + exception);
		}
		return false;
	}
	
	/*private boolean checkDelete(Long idDatabase) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("SchemaEntity.countByDatabaseId")
					.setParameter("idDatabase", idDatabase).getResultList();
			if (result.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("DatabasesDao - checkDelete : " + exception);
		}
		return false;
	}*/
	
	@SuppressWarnings("unchecked")
	public List<DatabaseEntity> findByName(String name) {
		try {
			return em.createNamedQuery("DatabaseEntity.findByNome").setParameter("name", name).getResultList();
		} catch (Exception exception) {
			log.error("DatabasesDao - checkDelete : " + exception);
			return null;
		}
	}
	
	public DatabaseEntity find(long id) {
		DatabaseEntity ent = null;
		try {
			ent = em.find(DatabaseEntity.class, id);
		} catch (Exception exception) {
			log.error("DatabaseDao - find : " + exception);
		}
		return ent;
	}	
}