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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class CommonDao {

    @PersistenceContext
    EntityManager entityManger;

    @Transactional(propagation = Propagation.REQUIRED, readOnly=false)
    public <T> void persist( T entity){

        entityManger.persist(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly=false)
    public <T> void update(T entity){
        entityManger.merge(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly=false)
    public <T> void remove(Class<T> className, Long id) throws  EntityNotFoundException {
        T  entity = entityManger.find(className, id);
        if (entity == null)
            throw new EntityNotFoundException();
        entityManger.remove(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly=false)
    public <T> void removeNoThrow(Class<T> className, Long id) {
        T  entity = entityManger.find(className, id);
        if (entity != null)
            entityManger.remove(entity);
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly=false)
    public <T> void findByNamedQueryAndRemove(Class<T> className, String namedQuery, String paramName, Long paramValue){

        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) entityManger.createNamedQuery(namedQuery).setParameter(paramName, paramValue).getResultList();

        if (list != null && list.size() > 0) {
            T entityToDelete = list.get(0);
            entityManger.remove(entityToDelete);
        }
    }
}