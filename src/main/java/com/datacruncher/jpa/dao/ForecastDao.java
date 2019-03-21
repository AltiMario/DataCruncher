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
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.entity.ForecastEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ForecastDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    Logger log = Logger.getLogger(this.getClass());

    public ReadList read() {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("ForecastEntity.findAll").getResultList());
        } catch (Exception exception) {
            log.error("ForecastDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : ForecastDao - read");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }

    public Create create(ForecastEntity forecastEntity) {
        Create create = new Create();

        try {
            commonDao.persist(forecastEntity);
        } catch (Exception exception) {
            log.error("ForecastDao - create : " + exception);
            create.setSuccess(false);
            create.setResults(null);
            create.setMessage(I18n.getMessage("error.noInsRecord"));
            return create;
        }
        create.setSuccess(true);
        create.setResults(forecastEntity);
        create.setMessage(I18n.getMessage("success.insRecord"));
        return create;
    }

    public Update update(ForecastEntity forecastEntity) {
        Update update = new Update();
        try {
            commonDao.update(forecastEntity);
        } catch (Exception exception) {
            log.error("ForecastDao - update : " + exception);
            update.setSuccess(false);
            update.setMessage(I18n.getMessage("error.noUpdateRecord"));
            return update;
        }
        update.setSuccess(true);
        update.setMessage(I18n.getMessage("success.updateRecord"));
        return update;
    }

    public Destroy destroy(long idForecast) {
        Destroy destroy = new Destroy();
        try {
            commonDao.remove(ForecastEntity.class, idForecast);
        } catch(EntityNotFoundException ex ){
            throw ex;
        } catch (Exception exception) {
            log.error("ForecastDao - destroy : " + exception);
            destroy.setSuccess(false);
            destroy.setResults(null);
            return destroy;
        }
        destroy.setSuccess(true);
        destroy.setResults(null);
        destroy.setMessage(I18n.getMessage("success.fieldCanc"));
        return destroy;
    }


    public void init() {
        ForecastEntity forecastEntity;
        try {
            @SuppressWarnings("unchecked")
            List<Long> result = em.createNamedQuery("ForecastEntity.count").getResultList();
            if (result.get(0) == 0L) {
                forecastEntity = new ForecastEntity();
                forecastEntity.setACValue(123);
                forecastEntity.setCount(2);
                forecastEntity.setDimension(3);
                forecastEntity.setFitness(12);
                forecastEntity.setIdForecast(1);
                forecastEntity.setMaxACatLags1(33);
                forecastEntity.setMaxACatLags2(44);
                forecastEntity.setMaxACValues1(22);
                forecastEntity.setMaxACValues2(55);
                forecastEntity.setMean(0);
                forecastEntity.setSequenceLength(14);
                forecastEntity.setSignificativeLags(13);
                forecastEntity.setSize(12);
                commonDao.persist(forecastEntity);
            }
        } catch(Exception exception) {
            log.error("ForecastDao - init : " + exception);
        }
    }
}