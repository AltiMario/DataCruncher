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

import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.ReadOnlyTx;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.entity.ContactEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ContactsDao {

    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

    Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Get all the instances of current entity.
	 * 
	 * @return all the instances 
	 */
	public ReadList read() {
		return getReadResult(false);		
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
			return ((ContactEntity) resList.get(0)).getIdContact();
		return -1;
	}	
	
	/**
	 * Gets read result.
	 * 
	 * @param isSingleEntity false - get all instances, true - get single(first) instance
	 * @return
	 */
    @ReadOnlyTx
	private ReadList getReadResult(boolean isSingleEntity) {
		synchronized (ContactsDao.class) {
			Query query = em.createQuery("SELECT a FROM ContactEntity a ORDER BY a.idContact DESC");
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
				log.error("ContactsDao - read : " + exception);
				readList.setSuccess(false);
				readList.setMessage(I18n.getMessage("error.error") + " : ContactsDao - read");
				return readList;
			} 
			readList.setSuccess(true);
			readList.setMessage(I18n.getMessage("success.listRecord"));
		    return readList;	
		}	
	}

	public Create create(ContactEntity contactEntity) {
		Create create = new Create ();	
		
		try {
			commonDao.persist(contactEntity);
		} catch(Exception exception) {
            log.error("ContactsDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(contactEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));			
			return create;
		} 
		create.setSuccess(true);		
		create.setResults(contactEntity);
		if(contactEntity.getIsEmailSent() == 1) {
			create.setMessage(I18n.getMessage("success.emailSent"));
		} else {
			create.setMessage(I18n.getMessage("error.emailConfigError"));
		}
		return create;
	}

    @ReadOnlyTx
	public ContactEntity find(long idContact) {
		ContactEntity contactEntity = null;
		try {
			contactEntity = em.find(ContactEntity.class, idContact);
		} catch (Exception exception) {
			log.error("ContactsDao - find : " + exception);
		}
		return contactEntity;
	}
	
    public Update update(ContactEntity contactEntity) {
		
		Update update = new Update();
		
		try {
			commonDao.update(contactEntity);
		} catch(Exception exception) {
			log.error("ContactsDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));			
			return update;
		} 
		update.setResults(contactEntity);

		if(contactEntity.getIsEmailSent() == 1) {
		update.setSuccess(true);
			update.setMessage(I18n.getMessage("success.emailSent"));
		} else {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.emailConfigError"));
		}

		return update;
	}

	public void setEmailSent(long contactId, int isEmailSent) {
		ContactEntity ent = find(contactId);
		if (ent != null) {
			ent.setIsEmailSent(isEmailSent);
			update(ent);
		}
	}

    /**
     * Gets read result.
     *
     * @param idUser
     * @return
     */
    @Transactional(readOnly=true)
    public ReadList findEmailNotSent(long idUser) {
        synchronized (ContactsDao.class) {
            Query query = em.createQuery("SELECT model FROM ContactEntity model where model.isEmailSent = 0 and model.idUser=" + idUser);
            ReadList readList = new ReadList();
            try {
                @SuppressWarnings("unchecked")
                List<ContactEntity> listContactEntity = (List<ContactEntity>)query.getResultList();
                readList.setResults(query.getResultList());
                if(listContactEntity.size() > 0) {
                    readList.setSuccess(true);
                    readList.setMessage(I18n.getMessage("error.emailNotSent"));
                } else {
                    readList.setSuccess(false);
                }
            } catch(Exception exception) {
                log.error("ContactsDao - read : " + exception);
            }

            return readList;
        }
    }
    /**
     * Gets read result.
     *
     * @param idUser
     * @return
     */
    @Transactional(readOnly = true)
    public Create findByUserId(long idUser) {

        Create create = new Create();
        Query query = em.createQuery("SELECT model FROM ContactEntity model where model.isEmailSent = 0 and model.idUser="	+ idUser);

        try {
            @SuppressWarnings("unchecked")
            List<ContactEntity> listContactEntity = (List<ContactEntity>) query.getResultList();
            if (listContactEntity != null && listContactEntity.size() > 0) {
                create.setResults(listContactEntity.get(0));
                create.setSuccess(true);
            } else {
                create.setSuccess(false);
                create.setResults(null);
            }

        } catch (Exception exception) {
            log.error("ContactsDao - findByUserId : " + exception);
        }

        return create;
    }
}