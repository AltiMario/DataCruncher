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

import com.datacruncher.jpa.entity.SchemaSQLEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.entity.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class SchemaSQLDao {
    Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected SchemaSQLDao() {
    }

    @Transactional(readOnly = true)
    public SchemaSQLEntity read(long idSchemaSQL) {
        SchemaSQLEntity schemaSQLEntity   = null;
        try {
            @SuppressWarnings("rawtypes")
            List sqlList = em.createNamedQuery("SchemaSQLEntity.findByIdSchemaSQL")
                    .setParameter("idSchemaSQL", idSchemaSQL)
                    .getResultList();
            if (sqlList != null && sqlList.size() != 0)
                schemaSQLEntity = (SchemaSQLEntity) sqlList.get(0);
        } catch (Exception exception) {
            log.error("SchemaSQLDao - read : " + exception);
            return schemaSQLEntity;
        }
        return schemaSQLEntity;
    }

    public Create create(SchemaSQLEntity schemaSQLEntity) {
        Create create = new Create ();
        try {
            if (!checkSchema(schemaSQLEntity.getIdSchemaSQL())) {
                log.error("SchemaSQLDao - create : " + I18n.getMessage("error.statusSchema"));
                create.setSuccess(false);
                create.setResults(schemaSQLEntity);
                create.setMessage(I18n.getMessage("error.statusSchema"));
                return create;
            }
            commonDao.persist(schemaSQLEntity);
        } catch(Exception exception) {
            log.error("SchemaSQLDao - create : " + exception);
            create.setSuccess(false);
            create.setResults(schemaSQLEntity);
            create.setMessage("");
            return create;
        }
        create.setSuccess(true);
        create.setResults(null);
        create.setMessage("");
        return create;
    }
    private boolean checkSchema(Long idSchema) throws Exception{
        @SuppressWarnings("unchecked")
        List<Long> result = em.createNamedQuery("SchemaEntity.countBySchemaId")
                .setParameter("idSchema", idSchema).getResultList();
        return result.get(0) > 0L;

    }

    @Transactional
    public Destroy destroy(long idSchemaSQL) {
        Destroy destroy = new Destroy();
        try {
            commonDao.remove(SchemaSQLEntity.class, idSchemaSQL);
        }catch (EntityNotFoundException ex) {
            log.error("SchemaSQLDao - destroy : " + ex);
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.noFoundRecord"));
            destroy.setResults(null);
            return destroy;
        }catch (Exception exception) {
            log.error("SchemaSQLDao - destroy : " + exception);
            destroy.setSuccess(false);
            destroy.setResults(null);
            return destroy;
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage("");
        return destroy;
    }


    public Update update(SchemaSQLEntity ent) {
        Update update = new Update();
        try {
            commonDao.update(ent);
        } catch(Exception exception) {
            log.error("SchemaSQLDao - update : " + exception);
            update.setSuccess(false);
            update.setMessage(I18n.getMessage("error.noUpdateRecord"));
            return update;
        }
        update.setSuccess(true);
        update.setMessage(I18n.getMessage("success.updateRecord"));
        return update;
    }


}
