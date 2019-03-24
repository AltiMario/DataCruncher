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

import com.datacruncher.jpa.*;
import com.datacruncher.jpa.entity.AlphanumericFieldValuesEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class AlphanumericFieldValuesDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;
	
	protected AlphanumericFieldValuesDao() {}	

	Logger log = Logger.getLogger(this.getClass());

    @ReadOnlyTx
	public ReadList read(long idAlphanumericSchemaField) {
		ReadList readList = new ReadList();
		try {
			Query q = em.createNamedQuery("AlphanumericFieldValuesEntity.findBySchemaField").setParameter(
					"idAlphanumericSchemaField", idAlphanumericSchemaField);
			readList.setResults(q.getResultList());
		} catch (Exception exception) {
			log.error("AlphanumericFieldValuesDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : AlphanumericFieldValuesDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

    public Create create(AlphanumericFieldValuesEntity alphanumericFieldValuesEntity) {
		Create create = new Create();
		if (alphanumericFieldValuesEntity.getValue().equals("")) {
			create.setSuccess(false);
			create.setResults(alphanumericFieldValuesEntity);
			create.setMessage(I18n.getMessage("error.requiredField"));
			return create;
		}
		try {
			commonDao.persist(alphanumericFieldValuesEntity);
		} catch (Exception exception) {
            log.error("AlphanumericFieldValuesDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(alphanumericFieldValuesEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(alphanumericFieldValuesEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(AlphanumericFieldValuesEntity alphanumericFieldValuesEntity) {
		Update update = new Update();
		if (alphanumericFieldValuesEntity.getValue().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return update;
		}
		try {
			commonDao.update(alphanumericFieldValuesEntity);
		} catch (Exception exception) {
            log.error("AlphanumericFieldValuesDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

    @Transactional
    public Destroy destroy(long idAlphanumericFieldValue) {
		Destroy destroy = new Destroy();
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("idAlphanumericFieldValue", idAlphanumericFieldValue);
		try {
			@SuppressWarnings("rawtypes")
			List result = em.createNamedQuery("AlphanumericFieldValuesEntity.findByAlphanumericFieldValue")
					.setParameter("idAlphanumericFieldValue", idAlphanumericFieldValue).getResultList();
			if (result != null && result.size() > 0) {
				em.remove(result.get(0));
			}
		} catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("AlphanumericFieldValuesDao - destroy : " + exception);
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
    @ReadOnlyTx
	public List<AlphanumericFieldValuesEntity> listAlphanumericFieldValues(long idAlphanumericSchemaField) {
		List<AlphanumericFieldValuesEntity> listAlphanumericFieldValuesEntity = null;
		try {
			listAlphanumericFieldValuesEntity = em.createNamedQuery("AlphanumericFieldValuesEntity.findBySchemaField")
					.setParameter("idAlphanumericSchemaField", idAlphanumericSchemaField).getResultList();
		} catch (Exception exception) {
			log.error("AlphanumericFieldValuesDao - listAlphanumericFieldValues : " + exception);
		}
		return listAlphanumericFieldValuesEntity;
	}

    @ReadOnlyTx
	public AlphanumericFieldValuesEntity find(long idAlphanumericSchemaField) {
		AlphanumericFieldValuesEntity alphanumericFieldValuesEntity = new AlphanumericFieldValuesEntity();
		try {
			alphanumericFieldValuesEntity = em.find(AlphanumericFieldValuesEntity.class, idAlphanumericSchemaField);
		} catch (Exception exception) {
			log.error("AlphanumericFieldValuesDao - find : " + exception);
		}
		return alphanumericFieldValuesEntity;
	}
}