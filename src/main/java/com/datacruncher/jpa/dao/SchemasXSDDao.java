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

import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.entity.SchemaXSDEntity;
import com.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

//@ReadOnlyTx
public class SchemasXSDDao {
	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected SchemasXSDDao() {
	}

    @Transactional(readOnly = true)
	public SchemaXSDEntity read(Long idSchemaXSD) {
		SchemaXSDEntity schemaXSDEntity = null;
		try {
			@SuppressWarnings("rawtypes")
			List listXSDEntity = em.createNamedQuery("SchemaXSDEntity.findByIdSchemaXSD")
					.setParameter("idSchemaXSD", idSchemaXSD).getResultList();
			if (listXSDEntity != null && listXSDEntity.size() != 0)
				schemaXSDEntity = (SchemaXSDEntity) listXSDEntity.get(0);
		} catch (Exception exception) {
			log.error("SchemasXSDDao - find : " + exception);
		}
		return schemaXSDEntity;
	}

	public Create create(SchemaXSDEntity schemaXSDEntity) {
		Create create = new Create ();		
		try {
			commonDao.persist(schemaXSDEntity);
		} catch(Exception exception) {
			log.error("SchemasXSDDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(schemaXSDEntity);
			create.setMessage("");			
			return create;
		}
		create.setSuccess(true);
		create.setResults(null);
		create.setMessage("");		
		return create;
	}

	public Destroy destroy(long idSchemaXSD) {
		Destroy destroy = new Destroy();
		try {
            commonDao.removeNoThrow(SchemaXSDEntity.class, idSchemaXSD);
        }
        catch (Exception exception) {
			log.error("SchemasXSDDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage("");
		return destroy;
	}

	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public ArrayList<SchemaXSDEntity> findAll() {
		ArrayList<SchemaXSDEntity> listXSDEntity = new ArrayList<SchemaXSDEntity>();
		try {
			listXSDEntity = (ArrayList<SchemaXSDEntity>) em.createNamedQuery("SchemaXSDEntity.findAll").getResultList();
		} catch (Exception exception) {
			log.error("SchemasXSDDao - findAll : " + exception);
		}
		return listXSDEntity;
	}

	public Update update(SchemaXSDEntity ent) {
		Update update = new Update();	
		try {
			commonDao.update(ent);
		} catch(Exception exception) {
			log.error("SchemasDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));		
		return update;
	}
    @Transactional(readOnly = true)
	public SchemaXSDEntity find(long idSchema) {
		SchemaXSDEntity ent = null;
		try {
			ent = em.find(SchemaXSDEntity.class, idSchema);
		} catch (Exception exception) {
			log.error("SchemasXSDDao - find : " + exception);
		}
		return ent;
	}	
	
	public void setVersIncreaseNeeded(long schemaId, boolean isNeeded) {
		SchemaXSDEntity ent = find(schemaId);
		if (ent != null) {
			ent.setIsVersIncreaseNeeded(isNeeded);
			ent.setJsonForm(null);
			update(ent);
		}
	}
}