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

import com.seer.datacruncher.jpa.*;
import com.seer.datacruncher.jpa.entity.FileEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ReadOnlyTx
public class FileDao {
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected FileDao() {
	}

	public Create create(FileEntity fileEntity) {
		Create create = new Create();
		if (!checkName(fileEntity.getIdFile(), fileEntity.getIdSchema(), fileEntity.getName())) {
			create.setSuccess(false);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		if (!checkSchema(fileEntity.getIdSchema())) {
			create.setSuccess(false);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
			commonDao.persist(fileEntity);
		} catch (Exception exception) {
			log.error("FileDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(null);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(fileEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}
	
	public FileEntity getElement(long idFile) {
		FileEntity fileEntity = null;
		try {
			fileEntity = em.find(FileEntity.class, idFile);
		} catch (Exception exception) {
			log.error("FileDao - getElement (idFile:" + idFile + "): " + exception);
		}
		return fileEntity;
	}
	
	public ReadList read(long idSchema, int start, int limit) {
		@SuppressWarnings("unchecked")
		List<Long> count = em.createNamedQuery("FileEntity.countBySchemaId").setParameter("idSchema", idSchema)
				.getResultList();
		Query query = em.createQuery("SELECT f FROM FileEntity f WHERE f.idSchema = :idSchema ORDER BY f.idFile DESC");
		query.setParameter("idSchema", idSchema);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		ReadList readList = new ReadList();
		try {
			readList.setTotal(count.get(0).longValue());
			readList.setResults(query.getResultList());
		} catch (Exception exception) {
			log.error("FileDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(null);
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(null);
		return readList;
	}

    @Transactional
	public Destroy destroy(long idFile) {
		Destroy destroy = new Destroy();
		try {
            commonDao.remove(FileEntity.class, idFile);
		} catch (EntityNotFoundException ex) {
            destroy.setSuccess(false);
            destroy.setMessage(I18n.getMessage("error.associatedFile"));
            destroy.setResults(null);
            return destroy;
        } catch(Exception exception) {
			log.error("FileDao - destroy : " + exception);
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
	public byte[] getFileContent(long idFile) {
		byte[] content = null;
		try {
			FileEntity entity = em.find(FileEntity.class, idFile);
			content = entity.getContent();
		} catch (Exception exception) {
			log.error("FileDao - getFileContent (idFile:" + idFile + "): " + exception);
		}
		return content;
	}
	
	private boolean checkName(Long idFile ,Long idSchema, String name) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("FileEntity.findDuplicateByName").setParameter("idFile", idFile)
					.setParameter("name", name).setParameter("idSchema", idSchema).getResultList();
			if (result.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("FileDao - checkName : " + exception);
		}
		return false;
	}
	
	private boolean checkSchema(Long idSchema) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("SchemaEntity.countBySchemaId")
					.setParameter("idSchema", idSchema).getResultList();
			if (result.get(0).longValue() == 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception exception) {
			log.error("FileDao - checkSchema : " + exception);
		}
		return false;
	}	
}