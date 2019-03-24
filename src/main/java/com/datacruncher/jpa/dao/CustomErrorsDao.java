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

import com.datacruncher.jpa.entity.CustomErrorEntity;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CustomErrorsDao{
    Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
	CommonDao commonDao;

    @Transactional(readOnly = true)
	public ReadList read(long schemaId) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("CustomErrorEntity.findBySchemaId")
					.setParameter("schemaId", schemaId).getResultList());
		} catch (Exception exception) {
			log.error("CustomErrorDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : CustomErrorDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public Create create(CustomErrorEntity ent) {
		Create create = new Create();
		if (ent.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(ent);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!checkName(ent.getIdSchema(), ent.getId(), ent.getName())) {
			create.setSuccess(false);
			create.setResults(ent);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
            commonDao.persist(ent);
		} catch (Exception exception) {
			log.error("CustomEntity - create : " + exception);
			create.setSuccess(false);
			create.setResults(null);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(ent);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(CustomErrorEntity ent) {
		Update update = new Update();
		if (ent.getName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " - " + I18n.getMessage("error.requiredField"));
			return update;
		}
		if (!checkName(ent.getIdSchema(), ent.getId(), ent.getName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
            commonDao.update(ent);
		} catch (Exception exception) {
			log.error("CustomErrorDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

	@Transactional
	public Destroy destroy(long idCustomError) {
		Destroy destroy = new Destroy();
		try {
			CustomErrorEntity entity = em.find(CustomErrorEntity.class, idCustomError);
			if (entity != null) {
				em.remove(entity);
			} else {
				throw new EntityNotFoundException();
			}
		} catch (Exception exception) {
			log.error("CustomErrorDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

	/**
	 * Checks for custom error entity duplicates.
	 * 
	 * @param schemaId
	 *            - schema Id
     * @param fieldId
     *            - field Id
	 * @param name
	 *            - custom error name
	 * @return boolean True/false
	 */
    @Transactional(readOnly = true)
	private boolean checkName(Long schemaId, Long fieldId, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("CustomErrorEntity.countDuplicateByName")
					.setParameter("id", fieldId).setParameter("name", name).setParameter("schemaId", schemaId)
					.getResultList();
			if (count.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("CustomErrorsDao - checkName : " + exception);
		}
		return false;
	}
    @Transactional(readOnly = true)
	public CustomErrorEntity find(long id) {
		CustomErrorEntity ent = new CustomErrorEntity();
		try {
			ent = em.find(CustomErrorEntity.class, id);
		} catch (Exception exception) {
			log.error("CustomErrorDao - find : " + exception);
		}
		return ent;
	}
}