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
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.entity.EventTriggerEntity;
import com.seer.datacruncher.jpa.entity.SchemaTriggerStatusEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class EventTriggerDao {
	
	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected EventTriggerDao() {}

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ReadList show(int start, int limit) {
        ReadList readList = new ReadList();
        try {
        	List<Long> count = em.createNamedQuery("EventTriggerEntity.count").getResultList();
        	
        	Query query = em.createNamedQuery("EventTriggerEntity.findAllNoSys");
        	if(limit > -1) {
        		query.setFirstResult(start);
        		query.setMaxResults(limit);
        	}
        	readList.setTotal(count.get(0).longValue());
                readList.setResults(query.getResultList());
        } catch (Exception exception) {
            log.error("EventTriggerDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : EventTriggerDao - read");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
	public ReadList read() {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("EventTriggerEntity.findAll").getResultList());
        } catch (Exception exception) {
            log.error("EventTriggerDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : EventTriggerDao - read");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ReadList findByIdSchemaAndIdStatus(long idSchema, int idStatus) {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("EventTriggerEntity.findByIdSchemaAndIdStatus")
                    .setParameter("idSchema", idSchema)
                    .setParameter("idStatus", idStatus)
                    .getResultList());
        } catch (Exception exception) {
            log.error("EventTriggerDao - findByIdSchemaAndIdStatus : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + "  : EventTriggerDao - findByIdSchemaAndIdStatus");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
    
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ReadList findTriggersByName(String name) {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("EventTriggerEntity.findByName")
                    .setParameter("name", name)
                    .getResultList());
        } catch (Exception exception) {
            log.error("EventTriggerDao - findByName : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + "  : EventTriggerDao - findByName");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
    
    public Create create(EventTriggerEntity eventTriggerEntity) {
		Create create = new Create ();
		try {
            create.setSuccess(true);
            create.setResults(eventTriggerEntity);
            create.setMessage(I18n.getMessage("success.insRecord"));
            commonDao.persist(eventTriggerEntity);
		} catch(Exception exception) {
			log.error("EventTriggerDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(eventTriggerEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		return create;
	}

    public Update update(EventTriggerEntity eventTriggerEntity) {
		Update update = new Update();
		try {
			commonDao.update(eventTriggerEntity);
		} catch(Exception exception) {
			log.error("ChecksTypeDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}
    
    @Transactional
	public Destroy destroy(long idEventTrigger) {
		Destroy destroy = new Destroy();
		try {
			EventTriggerEntity eventTriggerEntity = em.find(EventTriggerEntity.class, idEventTrigger);

            assert eventTriggerEntity != null;

		    em.remove(eventTriggerEntity);

            @SuppressWarnings("unchecked")
            List<SchemaTriggerStatusEntity> schemaTriggerStatusEntityList = em.createNamedQuery("SchemaTriggerStatusEntity.findByIdEventTrigger")
                    .setParameter("idEventTrigger", idEventTrigger).getResultList();
            if (schemaTriggerStatusEntityList != null) {
                for (int i = schemaTriggerStatusEntityList.size() - 1; i >= 0; i--)
                    em.remove(schemaTriggerStatusEntityList.get(i));
            }
		} catch (Exception exception) {
			log.error("ChecksTypeDao - destroy : " + exception);
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
    public void init() {
        EventTriggerEntity triggerEntity;
        try {
            @SuppressWarnings("unchecked")
            List<Long> result = em.createNamedQuery("EventTriggerEntity.countAll").getResultList();
            if ((result.get(0) == 0L)) {
                triggerEntity = new EventTriggerEntity();
                triggerEntity.setDescription("Sample trigger class ready for testing");
                triggerEntity.setName("SampleTrigger");
                triggerEntity.setCode("" +
                        "import com.seer.datacruncher.datastreams.DatastreamDTO;\n\n" +
                        "public class SampleTrigger implements com.seer.datacruncher.eventtrigger.EventTrigger{\n" +
                        "    private DatastreamDTO datastreamDTO;\n" +
                        "    public String trigger(){\n" +
                        "         if(getDatastreamDTO() == null) { return \"Response from sample trigger\"; } \n else { return  \"Response from sample, managed the stream:\" + getDatastreamDTO().getOutput(); } " +
                        "    }\n" +
                        "    public DatastreamDTO getDatastreamDTO(){\n" +
                        "        return this.datastreamDTO;\n" +
                        "    }\n" +
                        "    public void setDatastreamDTO(DatastreamDTO datastreamDTO){\n" +
                        "        this.datastreamDTO = datastreamDTO;\n" +
                        "    }\n" +
                        "}");
                commonDao.persist(triggerEntity);
            }
        } catch(Exception exception) {
            log.error("EventTriggerDao - init : " + exception);
        }
    }
    
    @Transactional(readOnly = true)
    public EventTriggerEntity findEventTriggerById(long idEventTrigger) {
        try {
        	 return (EventTriggerEntity)em.createNamedQuery("EventTriggerEntity.findByIdEventTrigger")
        			 				.setParameter("idEventTrigger", idEventTrigger).getSingleResult();
        } catch (Exception exception) {
        	exception.printStackTrace();
            log.error("EventTriggerDao - findEventTriggerById : " + exception);
        }
        return null;
    }
}
