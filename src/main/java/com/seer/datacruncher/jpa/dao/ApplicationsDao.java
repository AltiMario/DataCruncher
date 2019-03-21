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

import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.ReadWriteNewTx;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ApplicationsDao {

    private static final String DEFAULT_APP = "default_app";

	@PersistenceContext
    private EntityManager em;
	
	Logger log = Logger.getLogger(this.getClass());

    @Autowired
    CommonDao commonDao;
	/**
	 * Get all the instances of current entity.
	 * 
	 * @return all the instances 
	 */
	public ReadList read() {
		return getReadResult(false);		
	}

    public ReadList read(long idUser) {
		Query query = em
				.createQuery("SELECT a FROM ApplicationEntity a, UserApplicationsEntity ua where ua.idApplication=a.idApplication and ua.idUser= :idUser ORDER BY a.idApplication DESC");
		query.setParameter("idUser", idUser);
		ReadList readList = new ReadList();
		try {
			readList.setResults(query.getResultList());
		} catch (Exception exception) {
			log.error("ApplicationsDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : ApplicationsDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	/**
	 * Get single instance of current entity.
	 * 
	 * @return single instance 
	 */	
	public ReadList readSingleResult() {
		return getReadResult(true);
	}
	
	/**
	 * Get single instance id of current entity.
	 * 
	 * @return single instance 
	 */	
	public long readSingleResultId() {
		ReadList read = readSingleResult();
		@SuppressWarnings("rawtypes")
		List resList = read.getResults();
		if (resList.size() == 1)
			return ((ApplicationEntity) resList.get(0)).getIdApplication();
		return -1;
	}	
	
	/**
	 * Gets read result.
	 * 
	 * @param isSingleEntity false - get all instances, true - get single(first) instance
	 * @return
	 */
	private ReadList getReadResult(boolean isSingleEntity) {
		synchronized (ApplicationsDao.class) {
			Query query = em.createQuery("SELECT a FROM ApplicationEntity a ORDER BY a.idApplication DESC");
			ReadList readList = new ReadList();
			try {
				if (isSingleEntity) {
					List<Object> list = new ArrayList<Object>();
					if (query.getResultList() != null && query.getResultList().size() > 0) 
						list.add(query.getResultList().get(0));
					readList.setResults(list);
				} else {
					readList.setResults(query.getResultList());
				}
			} catch(Exception exception) {
				//createEntityManager("DataCruncher",null);
				log.error("ApplicationsDao - read : " + exception);
				readList.setSuccess(false);
				readList.setMessage(I18n.getMessage("error.error") + " : ApplicationsDao - read");
				return readList;
			} 
			readList.setSuccess(true);
			readList.setMessage(I18n.getMessage("success.listRecord"));
		    return readList;	
		}	
	}

	public Create create(ApplicationEntity applicationEntity) {
		Create create = new Create ();
		if (applicationEntity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(applicationEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));			
			return create;
		}
		if (!checkName(applicationEntity.getIdApplication() , applicationEntity.getName())) {
			create.setSuccess(false);
			create.setResults(applicationEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));			
			return create;
		}
		if (applicationEntity.getStartDate() != null && applicationEntity.getEndDate() != null && applicationEntity.getStartDate().after(applicationEntity.getEndDate())) {
			create.setSuccess(false);
			create.setResults(applicationEntity);
			create.setMessage(I18n.getMessage("error.startDateEarlierEnd"));			
			return create;
		}
		try {
			commonDao.persist(applicationEntity);
		} catch(Exception exception) {
			log.error("ApplicationsDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(applicationEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));			
			return create;
		} 
		create.setSuccess(true);
		create.setResults(applicationEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));		
		return create;
	}

    @ReadWriteNewTx
	public Update update(ApplicationEntity applicationEntity) {
		Update update = new Update();
		if (applicationEntity.getName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));			
			return update;
		}
		if (!checkName(applicationEntity.getIdApplication() , applicationEntity.getName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));			
			return update;
		}
		if (applicationEntity.getStartDate() != null && applicationEntity.getEndDate() != null && applicationEntity.getStartDate().after(applicationEntity.getEndDate())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.startDateEarlierEnd"));			
			return update;
		}
		try {
			em.merge(applicationEntity);
		} catch(Exception exception) {
			log.error("ApplicationsDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));			
			return update;
		} 
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));		
		return update;
	}

	public Destroy destroy(long idApplication) {
		Destroy destroy = new Destroy();
				
		try {
            commonDao.findByNamedQueryAndRemove(ApplicationEntity.class,
                                            "ApplicationEntity.findByIdApplication",
                                            "idApplication",
                                            idApplication);
		} catch (EntityNotFoundException ex){
            throw ex;
        } catch(Exception exception) {
			log.error("ApplicationsDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);			
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));		
		return destroy;
	}

	private boolean checkName(Long idApplication , String name) {
		Query query = em.createQuery("SELECT COUNT (a) FROM ApplicationEntity a WHERE a.idApplication != :idApplication AND a.name = :name");
		query.setParameter("idApplication" , idApplication);
		query.setParameter("name" , name);
		try {
			if ((Long) query.getSingleResult() == 0) {
				return true;
			} else {
				return false;
			}
		} catch(Exception exception) {
			log.error("ApplicationsDao - checkName : " + exception);
		} 
		
		return false;
	}

	private boolean checkDelete(Long idApplication) {
		Query query = em.createQuery("SELECT COUNT (s) FROM SchemaEntity s WHERE s.idApplication = :idApplication");
		query.setParameter("idApplication" , idApplication);
		try {
			if ((Long) query.getSingleResult() == 0) {
				return true;
			} else {
				return false;
			}
		} catch(Exception exception) {
			log.error("ApplicationsDao - checkDelete : " + exception);
		} 		
		return false;
	}
	
	public ApplicationEntity find(long idApplication) {
		ApplicationEntity applicationEntity = null;
		try {
			applicationEntity = em.find(ApplicationEntity.class, idApplication);
		} catch (Exception exception) {
			log.error("ApplicationsDao - find : " + exception);
		}
		return applicationEntity;
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationEntity> findByName(String name) {
		try {
			return em.createNamedQuery("ApplicationEntity.findByName").setParameter("name", name).getResultList();
		} catch (Exception exception) {
			log.error("ApplicationsDao - find : " + exception);
			return null;
		}
	}

	public void setActive(long applicationId, int isActive) {
		ApplicationEntity ent = find(applicationId);
		if (ent != null) {
			ent.setIsActive(isActive);
			update(ent);
		}
	}

    public void init() {
        ApplicationEntity applicationEntity;
        try {
            @SuppressWarnings("unchecked")
            List<Long> result = em.createNamedQuery("ApplicationEntity.count").getResultList();
            if (result.get(0) == 0L) {
                applicationEntity = new ApplicationEntity();
                applicationEntity.setDescription("Default application, ready to test");
                applicationEntity.setName(DEFAULT_APP);
                applicationEntity.setIsActive(1);
                commonDao.persist(applicationEntity);
            }
        } catch(Exception exception) {
            log.error("ApplicationDao - init : " + exception);
		}
	}
    
	/**
	 * Gets default application. Note: init of this (default) application is
	 * always invoked before this method.
	 * 
	 * @return default application
	 */
    @Transactional(readOnly = true)
	public ApplicationEntity getDefaultApp() {
		return (ApplicationEntity) em.createNamedQuery("ApplicationEntity.findByName").setParameter("name", DEFAULT_APP)
				.getResultList().get(0);
	}
    
    /**
     * Get system application "system_app" that has schemas for form validation. If not exists - create app.
     * 
     * @return sysApp
     */
	public ApplicationEntity getSysApp() {
		ApplicationEntity applicationEntity = null;
		try {
			if ((Long) em.createNamedQuery("ApplicationEntity.isSysAppExists").getResultList().get(0) == 0) {
				applicationEntity = new ApplicationEntity();
				applicationEntity.setDescription("System application that contains validation schemas");
				applicationEntity.setName("system_app");
				applicationEntity.setIsActive(1);
				commonDao.persist(applicationEntity);
			} else {
				applicationEntity = (ApplicationEntity) em.createNamedQuery("ApplicationEntity.getSysApp").getResultList().get(0);
			}
		} catch (Exception exception) {
			log.error("ApplicationDao - createSysAppIfNotExists : " + exception);
		}
		return applicationEntity;
	}
}