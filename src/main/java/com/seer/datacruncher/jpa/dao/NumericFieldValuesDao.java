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

import com.seer.datacruncher.jpa.*;
import com.seer.datacruncher.jpa.entity.NumericFieldValuesEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ReadOnlyTx
public class NumericFieldValuesDao {

	Logger log = Logger.getLogger(this.getClass());
	
	protected NumericFieldValuesDao(){};

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    public ReadList read(long idNumericSchemaField) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("NumericFieldValuesEntity.findByIdNumericSchemaFieldOrderDesc")
					.setParameter("idNumericSchemaField", idNumericSchemaField).getResultList());
		} catch (Exception exception) {
			log.error("NumericFieldValuesDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : NumericFieldValuesDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

    public Create create(NumericFieldValuesEntity numericFieldValuesEntity) {
		Create create = new Create ();
		if (numericFieldValuesEntity.getValue().equals("")) {
			create.setSuccess(false);
			create.setResults(numericFieldValuesEntity);
			create.setMessage(I18n.getMessage("error.requiredField"));			
			return create;
		}		
		try {		
			commonDao.persist (numericFieldValuesEntity);
		} catch(Exception exception) {
            log.error("NumericFieldValuesDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(numericFieldValuesEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));			
			return create;
		}
		create.setSuccess(true);
		create.setResults(numericFieldValuesEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));		
		return create;
	}

    @Transactional
    public Update update(NumericFieldValuesEntity numericFieldValuesEntity) {
		Update update = new Update();
		if (numericFieldValuesEntity.getValue().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return update;
		}
		try {
			commonDao.update(numericFieldValuesEntity);
		} catch (Exception exception) {
			log.error("NumericFieldValuesDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

    @Transactional
    public Destroy destroy(long idNumericFieldValue) {
		Destroy destroy = new Destroy();
		try {
			NumericFieldValuesEntity entity = em.find(NumericFieldValuesEntity.class, idNumericFieldValue);
			if (entity != null) {
				em.remove(entity);
			} else {
				throw new EntityNotFoundException();
			}
		} catch (Exception exception) {
			log.error("NumericFieldValuesDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}
	
	@SuppressWarnings("unchecked")
	public List<NumericFieldValuesEntity> listNumericFieldValues(long idNumericSchemaField) {
		List<NumericFieldValuesEntity> listNumericFieldValuesEntity = null;
		try {
			listNumericFieldValuesEntity = em
					.createNamedQuery("NumericFieldValuesEntity.findByIdNumericSchemaFieldOrderDesc")
					.setParameter("idNumericSchemaField", idNumericSchemaField).getResultList();
		} catch (Exception exception) {
			log.error("NumericFieldValuesDao - listNumericFieldValues : " + exception);
		}
		return listNumericFieldValuesEntity;
	}
	
	public NumericFieldValuesEntity find(long idNumericSchemaField) {
		NumericFieldValuesEntity numericFieldValuesEntity = new NumericFieldValuesEntity();
		try {
			numericFieldValuesEntity = em.find(NumericFieldValuesEntity.class, idNumericSchemaField);
		} catch (Exception exception) {
			log.error("NumericFieldValuesDao - find : " + exception);
		}
		return numericFieldValuesEntity;
	}
}