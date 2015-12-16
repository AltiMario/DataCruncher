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

import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.entity.LogEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


public class LogDao {
    Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    public Create create(LogEntity logEntity) {
        Create create = new Create();
        try {
            commonDao.persist(logEntity);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("LogDao - create : " + exception);
            create.setSuccess(false);
            create.setResults(null);
            create.setMessage(I18n.getMessage("error.noInsRecord"));
            return create;
        }
        create.setSuccess(true);
        create.setResults(logEntity);
        create.setMessage(I18n.getMessage("success.insRecord"));
        return create;
    }
    @Transactional
    public Destroy deleteAllRows() {
        Destroy destroy = new Destroy();
        try {
            try {
                em.createNamedQuery("LogEntity.deleteAllRows").executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("LogDao - destroy : " + e.getMessage());
            }
        } catch (Exception exception) {
            log.error("LogDao - destroy : " + exception);
            destroy.setSuccess(false);
            destroy.setResults(null);
            return destroy;
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage(I18n.getMessage("success.fieldCanc"));
        return destroy;
    }
    public ReadList getReadResult(int start, int limit, String searchString) {

    	List<Long> count = em.createNamedQuery("LogEntity.countAll").getResultList();   	
    	
        if(searchString != null && searchString.trim().length() > 0) {        	
        	count = em.createNamedQuery("LogEntity.countSearchRows").setParameter("message", '%' + searchString + '%').getResultList();
        }
        Query query = em.createNamedQuery("LogEntity.selectAllRows");
        if(searchString != null && searchString.trim().length() > 0) {
        	query = em.createNamedQuery("LogEntity.searchAllRows").setParameter("message", '%' + searchString + '%');
        }
        query.setFirstResult(start);
        query.setMaxResults(limit);
        ReadList readList = new ReadList();
        try {
            readList.setTotal(count.get(0).longValue());
            readList.setResults(query.getResultList());
        } catch(Exception exception) {
            log.error("LogDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : LogDao - read");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }

    @Transactional
    public void setInfoLogMessage(String message){
        int status=0;
        LogEntity logEntity = new LogEntity();
        logEntity.setIdStatus(status);
        logEntity.setLogDateTime(new java.util.Date());
        logEntity.setMessage(message);
        create(logEntity);
    }
    @Transactional
    public void setErrorLogMessage(String message){
        int status=1;
        LogEntity logEntity = new LogEntity();
        logEntity.setIdStatus(status);
        logEntity.setLogDateTime(new java.util.Date());
        logEntity.setMessage(message);
        create(logEntity);
    }
    @Transactional
    public void setWarnLogMessage(String message){
        int status=2;
        LogEntity logEntity = new LogEntity();
        logEntity.setIdStatus(status);
        logEntity.setLogDateTime(new java.util.Date());
        logEntity.setMessage(message);
        create(logEntity);
    }
    @Transactional
    public Destroy deteteRows(Long totrow) {
        Destroy destroy = new Destroy();
        try {
            Long idlog= 0L;
            String maxId = em.createNamedQuery("LogEntity.selectId").getSingleResult().toString();
            idlog = Long.parseLong(maxId) - totrow;
            if(idlog>0L)
                em.createNamedQuery("LogEntity.deleteMaxRows").setParameter("idlog", idlog ).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("LogDao - deleteRows : " + e.getMessage());
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage(I18n.getMessage("success.fieldCanc"));
        return destroy;
    }
    public Destroy deleteRow(long idLog) {
        Destroy destroy = new Destroy();

        try {
            commonDao.findByNamedQueryAndRemove(LogEntity.class,
                    "LogEntity.findByIdLog",
                    "idLog",
                    idLog);
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
}
