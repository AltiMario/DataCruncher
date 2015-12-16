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
import com.seer.datacruncher.jpa.entity.TasksEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@ReadOnlyTx
public class TasksDao {

	Logger log = Logger.getLogger(this.getClass());
	
    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected TasksDao() {
	}
	
	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("TasksEntity.findAll").getResultList());
		} catch (Exception exception) {
			log.error("TasksDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : TasksDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public Create create(TasksEntity entity) {
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
		if ( entity.getDay() != -1 && entity.getWeek() != -1) {
			create.setSuccess(false);
			create.setResults(entity);
			create.setMessage(I18n.getMessage("error.plannerWeekAndDay"));
			return create;
		}			
		try {

			commonDao.persist(entity);

		} catch (Exception exception) {
			log.error("TasksDao - create : " + exception);
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
	
	@ReadWriteNewTx
	public Update update(TasksEntity entity) {
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
		if (entity.getDay() != -1 && entity.getWeek() != -1) {
			update.setSuccess(false);
			update.setResults(entity);
			update.setMessage(I18n.getMessage("error.plannerWeekAndDay"));
			return update;
		}
		try {		

			commonDao.update(entity);

		} catch (Exception exception) {
			log.error("EntityDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

    @ReadWriteNewTx
	public Destroy destroy(long id) {
		Destroy destroy = new Destroy();
		try {
			commonDao.remove(TasksEntity.class, id);
		}catch(EntityNotFoundException ex){
            throw ex;
        }catch (Exception exception) {
			log.error("TasksDao - destroy : " + exception);
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
			List<Long> count = em.createNamedQuery("TasksEntity.countDuplicateByName").setParameter("id", id)
					.setParameter("name", name).getResultList();
			if (count.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("TasksDao - checkName : " + exception);
		}
		return false;
	}
	
	public TasksEntity find(long id) {
		TasksEntity taskEntity = new TasksEntity();
		try {
			taskEntity = em.find(TasksEntity.class, id);
		} catch (Exception exception) {
			log.error("TasksDao - find : " + exception);
		}
		return taskEntity;
	}
    @SuppressWarnings("unchecked")
    public List<TasksEntity> findByName(String name) {
        try {
            return em.createNamedQuery("TasksEntity.findByName").setParameter("name", name).getResultList();
        } catch (Exception exception) {
            log.error("TasksDao - findByName : " + exception);
            return null;
        }
    }
}