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
import com.seer.datacruncher.jpa.entity.MacroEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MacrosDao {

	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
	CommonDao commonDao;
	protected MacrosDao(){}

    @Transactional(readOnly = true)
	public ReadList read(long schemaId) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("MacroEntity.findBySchemaId").setParameter("schemaId", schemaId)
					.getResultList());
		} catch (Exception exception) {
			log.error("MacrosDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : MacrosDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

    @Transactional(readOnly = true)
	public MacroEntity getMacroEntityByName(String macroName) {
		@SuppressWarnings("unchecked")
		List<MacroEntity> result = em.createNamedQuery("MacroEntity.findByName").setParameter("name", macroName)
				.getResultList();
		if (result != null && result.size() != 0)
			return result.get(0);
		return null;
	}
	
	public Create create(MacroEntity macroEntity) {
		Create create = new Create();
		if (macroEntity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(macroEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!checkName(macroEntity.getIdMacro(), macroEntity.getName())) {
			create.setSuccess(false);
			create.setResults(macroEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
            commonDao.persist(macroEntity);
		} catch (Exception exception) {
			log.error("MacrosDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(null);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(macroEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}
	
	public Update update(MacroEntity macroEntity) {
		Update update = new Update();
		if (macroEntity.getName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " - " + I18n.getMessage("error.requiredField"));
			return update;
		}
		if (!checkName(macroEntity.getIdMacro(), macroEntity.getName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
            commonDao.update(macroEntity);
		} catch (Exception exception) {
			em.getTransaction().rollback();
			log.error("MacrosDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}
    @Transactional
	public Destroy destroy(long idMacro) {
		Destroy destroy = new Destroy();
		try {
			MacroEntity entity = em.find(MacroEntity.class, idMacro);
			if (entity != null) {
				em.remove(entity);
			} else {
				throw new EntityNotFoundException();
			}
		} catch (Exception exception) {
			log.error("MacrosDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

    @Transactional(readOnly = true)
	private boolean checkName(Long idMacro, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("MacroEntity.countDuplicateByName").setParameter("idMacro", idMacro)
					.setParameter("name", name).getResultList();
			if (count.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("MacrosDao - checkName : " + exception);
		}
		return false;
	}
	
	public void setActive(long macroId, int isActive) {
		MacroEntity ent = find(macroId);
		if (ent != null) {
			ent.setIsActive(isActive);
			try {
                commonDao.update(ent);
			} catch (Exception exception) {
				log.error("MacroEntity - set is active: " + exception);
			}
		}
	}
    @Transactional(readOnly = true)
	public MacroEntity find(long idMacro) {
		MacroEntity macroEntity = new MacroEntity();
		try {
			macroEntity = em.find(MacroEntity.class, idMacro);
		} catch (Exception exception) {
			log.error("MacroDao - find : " + exception);
		}
		return macroEntity;
	}
	
	/*private boolean checkDelete(Long idMacro) {
		EntityManager entityManager = getEntityManager("DataCruncher");
		Query query = entityManager.createQuery("SELECT COUNT (s) FROM MacroEntity s WHERE s.idMacro = :idMacro");
		query.setParameter("idMacro" , idMacro);
		try {
			if ((Long) query.getSingleResult() == 0) {
				return true;
			} else {
				return false;
			}
		} catch(Exception exception) {
			log.error("MacrosDao - checkDelete : " + exception);
		}
		
		return false;
	}*/
}