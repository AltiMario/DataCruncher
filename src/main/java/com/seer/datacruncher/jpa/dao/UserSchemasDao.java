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
import com.seer.datacruncher.jpa.entity.UserSchemasEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@ReadOnlyTx
public class UserSchemasDao {

	Logger log = Logger.getLogger(this.getClass());
	
    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

	protected UserSchemasDao() {
	}

	public ReadList read() {
		return null;
	}

	public Create create(UserSchemasEntity userSchemasEntity) {
		Create create = new Create();
		try {

			commonDao.persist(userSchemasEntity);

		} catch (Exception exception) {
			log.error("userSchemasDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(userSchemasEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(userSchemasEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

    @Transactional
	public Destroy destroy(long idSchema) {
		Destroy destroy = new Destroy();
		try {
			@SuppressWarnings("unchecked")
			List<UserSchemasEntity> userSchemasEntityList = em.createNamedQuery("UserSchemasEntity.findBySchemaId")
					.setParameter("idSchema", idSchema).getResultList();
			if (userSchemasEntityList != null) {
				for (int i = userSchemasEntityList.size() - 1; i >= 0; i--)
					em.remove(userSchemasEntityList.get(i));
			}
		} catch (Exception exception) {
			log.error("UserSchemasDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}
}
