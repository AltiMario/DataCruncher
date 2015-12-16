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

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadWriteNewTx;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.I18n;

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
public class ApplicationConfigDao {

    @PersistenceContext
    private EntityManager em;
	
	Logger log = Logger.getLogger(this.getClass());

    @Autowired
    CommonDao commonDao;
    
	public Create create(ApplicationConfigEntity applicationConfigEntity) {
		Create create = new Create ();
		
		try {
			String encPassword = new CryptoUtil().encrypt(applicationConfigEntity.getPassword());
			applicationConfigEntity.setPassword(encPassword);
			commonDao.persist(applicationConfigEntity);
		} catch(Exception exception) {
			log.error("ApplicationConfigDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(applicationConfigEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));			
			return create;
		} 
		create.setSuccess(true);
		create.setResults(applicationConfigEntity);
		create.setMessage(I18n.getMessage("success.databaseConfigSaved"));		
		return create;
	}	

    public String checkApplicationConfiguration() {
        String success = "true";
        try {
            @SuppressWarnings("unchecked")
            
            List<Long> count = em.createNamedQuery("UserEntity.count").getResultList();
            if (count.get(0).longValue() == 0L) {
				success = "false";
			}
        } catch(Exception exception) {
            log.error("ApplicationConfigDao - init : " + exception);
		}
        return success;
	}
    public boolean isAlertDispatcherMailSet(){
        boolean ret =false;
        ApplicationConfigEntity appConfigEntity = findByConfigType(ApplicationConfigType.EMAIL);
        if(appConfigEntity != null)
            ret=true;
        return ret;
    }
    public ApplicationConfigEntity findByConfigType(int configType) {
    	ApplicationConfigEntity applicationConfigEntity = null;
		try {
			Query query = em
					.createQuery("SELECT a FROM ApplicationConfigEntity a where a.configType = :configType");
			query.setParameter("configType", configType);
			applicationConfigEntity = (ApplicationConfigEntity)query.getSingleResult();
		} catch (Exception exception) {
			log.debug("ApplicationConfigDao - find : " + exception);
		}
		return applicationConfigEntity;
	}
    
    @ReadWriteNewTx
	public Update update(ApplicationConfigEntity applicationConfigEntity) {
		Update update = new Update();
		try {
			String encPassword = new CryptoUtil().encrypt(applicationConfigEntity.getPassword());
			applicationConfigEntity.setPassword(encPassword);
			em.merge(applicationConfigEntity);
		} catch(Exception exception) {
			log.error("ApplicationConfigDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));			
			return update;
		} 
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));		
		return update;
	}
    
    public Destroy destroy(int configType) {
		Destroy destroy = new Destroy();
		
		try {
						
			ApplicationConfigEntity appConfigEntity = findByConfigType(configType);

			if(appConfigEntity != null) {
				em.remove(appConfigEntity);
			} else {
				destroy.setSuccess(false);
				destroy.setResults(null);			
				return destroy;
			}
		} catch (EntityNotFoundException ex){
			throw ex;
        } catch(Exception exception) {
			log.error("ApplicationConfigDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);			
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));		
		return destroy;
	}
}