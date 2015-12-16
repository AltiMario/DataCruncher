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

package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.jpa.*;
import com.seer.datacruncher.jpa.entity.JobsEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@ReadOnlyTx
public class JobsDao {

	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected JobsDao() {}

    @Transactional
    public boolean setWorkStatus(long jobId,boolean bWork) {
        synchronized (this) {
            JobsEntity ent = find(jobId);
            if (ent != null) {
                log.debug("Set job "+ent.getName()+" setWorking:" + bWork);
                ent.setWorking(bWork);
                try {
                    commonDao.update(ent);
                } catch (Exception exception) {
                    log.error("JobsDao - setWorkStatus : " + exception);
                    return false;
                }
            }
        }
        return true;
    }

	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("JobsEntity.findAll").getResultList());
		} catch (Exception exception) {
			log.error("JobsDao - read : " + exception);
			readList.setSuccess(false);
			return readList;
		}
		readList.setSuccess(true);
		return readList;
	}

	public Create create(JobsEntity entity) {
		Create create = new Create();
		if (entity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if ((entity.getIdApplication() != 0 && entity.getIdSchema() == 0) ||
				(entity.getIdApplication() == 0 && entity.getIdSchema() != 0)) {
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("error.appAndSchemaNotMapped"));
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
            log.error("JobsDao - create : " + exception);
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
    @Transactional
	public Update update(JobsEntity entity) {
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
		boolean isAppAndSchemaMapped = true;
		long appId = entity.getIdApplication();
		ReadList read = DaoServices.getSchemasDao().readByApplicationId(-1, -1, appId);
		@SuppressWarnings("unchecked")
		List<SchemaEntity> list = (List<SchemaEntity>) read.getResults();
		isAppAndSchemaMapped = false;
		if (list != null) {
			for (SchemaEntity se : list)
				if (se.getIdSchema() == entity.getIdSchema()) {
					isAppAndSchemaMapped = true;
					break;
				}
		}
		if (!isAppAndSchemaMapped && (entity.getIdApplication() != 0 || entity.getIdSchema() != 0)) {
			update.setSuccess(false);
			update.setResults(entity);
			update.setMessage(I18n.getMessage("error.appAndSchemaNotMapped"));
			return update;
		}
		entity.setIsActive(0);
		synchronized (this) {
			try {
                commonDao.update(entity);
			} catch (Exception exception) {

				log.error("JobsDao - update : " + exception);
				update.setSuccess(false);
				update.setMessage(I18n.getMessage("error.noUpdateRecord"));
				return update;
			}
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}
    @Transactional
    public boolean setAllJobsWorkFalse() {
        ReadList read = read();
        try{
            for (Object o : read.getResults()) {
                JobsEntity ent = (JobsEntity) o;
                if(ent.isWorking()) {
                    ent.setWorking(false);
                    commonDao.update(ent);
                }
            }
            return true;
        }catch(Exception e){
            log.error("JobsDao - setAllWorkFalse : " + e.getMessage());
            return false;    
        }
        
    }
    @Transactional
	public Destroy destroy(long id) {
		Destroy destroy = new Destroy();
		try {
            commonDao.remove(JobsEntity.class, id);
		} catch(EntityNotFoundException ex) {
            log.error("JobsDao - destroy : " + ex);
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.noFoundRecord"));
            destroy.setResults(null);
            return destroy;
        } catch (Exception exception) {
			log.error("JobsDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

	private boolean checkName(Long id, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> list = em.createNamedQuery("JobsEntity.countDuplicateByName").setParameter("id", id)
					.setParameter("name", name).getResultList();
			if (list.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("JobsDao - checkName : " + exception);
		}
		return false;
	}
    @Transactional
	public void setActive(long jobId, int isActive) {
        synchronized (this) {
            JobsEntity ent = find(jobId);
            if (ent != null) {
                log.debug("Set job "+ent.getName()+" isActive:" + isActive);
                ent.setIsActive(isActive);
                try {
                    commonDao.update(ent);
                } catch (Exception exception) {
                    log.error("JobsDao - set is active: " + exception);
                }
            }
        }
	}

	public JobsEntity find(long id) {
		JobsEntity entity = null;
		try {
			entity = em.find(JobsEntity.class, id);
		} catch (Exception exception) {
			log.error("JobsDao - find : " + exception);
		}
		return entity;
	}

    @SuppressWarnings("unchecked")
    public List<JobsEntity> findByName(String name) {
        try {

            return em.createNamedQuery("JobsEntity.findByName").setParameter("name", name).getResultList();
        } catch (Exception exception) {
            log.error("JobsDao - findByName : " + exception);
            return null;
        }
    }
    
    @Transactional(readOnly = true)
	public ReadList readByApplicationId(long appId) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("JobsEntity.findByApplicationId").setParameter("appId", appId)
					.getResultList());
		} catch (Exception exception) {
			log.error("JobsDao - readByApplicationId : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : JobsDao - readByApplicationId");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
}