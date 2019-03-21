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
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.entity.SchemaTriggerStatusEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class SchemaTriggerStatusDao {
    Logger log = Logger.getLogger(this.getClass().getName());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected SchemaTriggerStatusDao(){}

    @Transactional(readOnly = true)
    public Long findEventByIdSchema(long idSchema) {
        long numElem;
        @SuppressWarnings("unchecked")
        List<Long> result = em.createNamedQuery("SchemaTriggerStatusEntity.countByIdSchema")
                .setParameter("idSchema", idSchema).getResultList();
        numElem = result.get(0);
        return numElem;

    }

    public Create create(SchemaTriggerStatusEntity schemaTriggerStatusEntity) {
        Create create = new Create();

        try {
            int ret =  checkEntity(schemaTriggerStatusEntity.getIdSchema(), schemaTriggerStatusEntity.getIdEventTrigger());
            switch (ret)  {
                case 1:
                    create.setSuccess(false);
                    create.setResults(null);
                    create.setMessage(I18n.getMessage("error.statusSchema"));
                    return create;
                case 2:
                    create.setSuccess(false);
                    create.setResults(null);
                    create.setMessage(I18n.getMessage("error.statusEvent"));
                    return create;
            }
            commonDao.persist(schemaTriggerStatusEntity);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("SchemaTriggerStatusDao - create : " + exception);
            create.setSuccess(false);
            create.setResults(null);
            create.setMessage(I18n.getMessage("error.noInsRecord"));
            return create;
        }
        create.setSuccess(true);
        create.setResults(schemaTriggerStatusEntity);
        create.setMessage(I18n.getMessage("success.insRecord"));
        return create;
    }

    public Update update(SchemaTriggerStatusEntity schemaTriggerStatusEntity) {
        Update update = new Update();
        try {
            commonDao.update(schemaTriggerStatusEntity);
        } catch(Exception exception) {
            log.error("SchemaTriggerStatusDao - update : " + exception);
            update.setSuccess(false);
            update.setResults(null);
            update.setMessage(I18n.getMessage("error.noUpdateRecord"));
        }
        update.setResults(schemaTriggerStatusEntity);
        update.setSuccess(true);
        update.setMessage(I18n.getMessage("success.updateRecord"));

        return update;
    }
    @Transactional
    public Destroy destroyEventsBySchema(long idSchema) {
        Destroy destroy = new Destroy();
        try {
            @SuppressWarnings("unchecked")
            List<SchemaTriggerStatusEntity> schemaTriggerStatusEntityList = em.createNamedQuery("SchemaTriggerStatusEntity.findByIdSchema")
                    .setParameter("idSchema", idSchema).getResultList();
            if (schemaTriggerStatusEntityList != null) {
                for (int i = schemaTriggerStatusEntityList.size() - 1; i >= 0; i--)
                    em.remove(schemaTriggerStatusEntityList.get(i));
            }

        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("SchemaTriggerStatusDao - destroyEventsBySchema : " + exception);
            destroy.setSuccess(false);
            destroy.setResults(null);
            return destroy;
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage(I18n.getMessage("success.fieldCanc"));
    return destroy;
}
    @Transactional
    public Destroy destroy(long idSchemaTriggerStatus) {
        Destroy destroy = new Destroy();
        try {
            commonDao.remove(SchemaTriggerStatusEntity.class, idSchemaTriggerStatus);
        } catch (EntityNotFoundException ex) {
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.noFoundRecord"));
            destroy.setResults(null);
            return destroy;
        } catch(Exception exception) {
            log.error("SchemaTriggerStatusDao - destroy : " + exception);
            destroy.setSuccess(false);
            destroy.setResults(null);
            destroy.setMessage(I18n.getMessage("error.noDelRecord"));
            return destroy;
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage(I18n.getMessage("success.fieldCanc"));
        return destroy;
    }
    
    @Transactional(readOnly = true)
    public ReadList findByIdSchema(long idSchema) {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("SchemaTriggerStatusEntity.findByIdSchema")
                    .setParameter("idSchema", idSchema)
                    .getResultList());
        } catch (Exception exception) {
            log.error("EventTriggerDao - findByIdSchema : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + "  : SchemaTriggerStatusDao - findByIdSchema");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
    @Transactional
    @SuppressWarnings("unchecked")
    public void init() {
        SchemaTriggerStatusEntity triggerStatusEntity;
        try {

            List<Long> events = em.createNamedQuery("EventTriggerEntity.countAll").getResultList();
            List<Long> schemas = em.createNamedQuery("SchemaEntity.count").getResultList();
            if (events.get(0) == 1L && schemas.get(0) == 1L ) {
                List<Long> result = em.createNamedQuery("SchemaTriggerStatusEntity.count").getResultList();
                if ((result.get(0) == 0L)) {
                    triggerStatusEntity = new SchemaTriggerStatusEntity();
                    triggerStatusEntity.setIdEventTrigger(1);
                    triggerStatusEntity.setIdSchema(1);
                    triggerStatusEntity.setIdStatus(0);
                    commonDao.persist(triggerStatusEntity);
                }
            }
        } catch(Exception exception) {
            log.error("SchemaTriggerStatusDao - init : " + exception);
        }
    }
    private int checkEntity(Long idSchema, Long idEventTrigger) throws Exception{
        @SuppressWarnings("unchecked")
        List<Long> result = em.createNamedQuery("SchemaEntity.countBySchemaId")
                .setParameter("idSchema", idSchema).getResultList();
        if (result.get(0).longValue() > 0L) {
            @SuppressWarnings("unchecked")
            List<Long> result2 = em.createNamedQuery("EventTriggerEntity.countByIdEvent")
                    .setParameter("idEventTrigger", idEventTrigger).getResultList();
            if (result2.get(0).longValue() > 0L) {
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }

    }
}
