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
import com.seer.datacruncher.jpa.entity.UserApplicationsEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ReadOnlyTx
public class UserApplicationsDao {

	Logger log = Logger.getLogger(this.getClass());
	
    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected UserApplicationsDao() {
    }
	
	public ReadList read() { 
		return null;
	}	

    public Create create(UserApplicationsEntity userApplicationsEntity) {
		Create create = new Create();
		try {
			commonDao.persist(userApplicationsEntity);
		} catch (Exception exception) {
			log.error("userApplicationsDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(userApplicationsEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(userApplicationsEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(UserEntity userEntity) {
		Update update = new Update();
		if (userEntity.getUserName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.userName") + " : " + I18n.getMessage("error.requiredField"));
			return update;
		}
		if (!checkName(userEntity.getIdUser(), userEntity.getUserName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.userName") + " : " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
			commonDao.update(userEntity);
		} catch (Exception exception) {
			log.error("UsersDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

	public Destroy destroy(long idUser) {
		Destroy destroy = new Destroy();
		try {
            commonDao.remove(UserApplicationsEntity.class, idUser);
		} catch(EntityNotFoundException ex) {
            throw ex;
        } catch (Exception exception) {
    		log.error("userApplicationsDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}
	
	private boolean checkName(Long idUser, String userName) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("UserEntity.countDuplicateByName").setParameter("idUser", idUser)
					.setParameter("userName", userName).getResultList();
			if (count.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("UserssDao - checkName : " + exception);
		}
		return false;
	}
	
	public UserEntity find(long idUser) {
		UserEntity userEntity = new UserEntity();
		try {
			userEntity = em.find(UserEntity.class, idUser);
		} catch (Exception exception) {
			log.error("UsersDao - find : " + exception);
		}
		return userEntity;
	}
	
	@SuppressWarnings("unchecked")
	public UserEntity findUserByNameNMailId(String userName, String email) {
		UserEntity userEntity = null;
		try {
			log.debug("UserEntity.findUserByNameNMailId" + "Start");
			List<UserEntity> result = em.createNamedQuery("UserEntity.findUserByNameNMailId")
					.setParameter("userName", userName).setParameter("email", email).getResultList();
			if (result != null && result.size() > 0)
				userEntity = result.get(0);
		} catch (Exception exception) {
			log.error("UserEntity.findUserByNameNMailId" + "Exception : " + exception);
		} finally {
			log.debug("UserEntity.findUserByNameNMailId" + "End");
		}
		return userEntity;
	}
	
	@Transactional(readOnly = true)
	public ReadList findByApplicationId(long appId) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("UserApplicationsEntity.findByApplicationId").setParameter("appId", appId)
					.getResultList());
		} catch (Exception exception) {
			log.error("UserApplicationsDao - findByApplicationId : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : UserApplicationsDao - findByApplicationId");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
}