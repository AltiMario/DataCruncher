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

import com.datacruncher.constants.SchemaType;
import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.entity.*;
import com.datacruncher.spring.AppContext;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;
import com.datacruncher.jpa.entity.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class SchemasDao {

	private static final String DEFAULT_SCHEMA = "defaultSchema";

	Logger log = Logger.getLogger(this.getClass());
	
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected SchemasDao() {
	}

    @Transactional(readOnly=true)
	public ReadList readBySchemaTypeId(int start, int limit, int idSchemaType, String appIds) {
		ReadList readList = new ReadList();
		try {
			String countQueryName = "SchemaEntity.countAll";
			String queryName = "SchemaEntity.findAll";
			if(appIds != null && appIds.trim().length() > 0) {
				
				if(idSchemaType == -1) {
					queryName = "SchemaEntity.findByApplicationIds";
					countQueryName = "SchemaEntity.countByApplicationIds";
				} else {
					queryName = "SchemaEntity.findAllByAppIdAndSchemaType";
					countQueryName = "SchemaEntity.countAllByAppIdAndSchemaType";					
				}
			} else if(idSchemaType == -1) {
				queryName = "SchemaEntity.find";
				countQueryName = "SchemaEntity.count";
			}
			
			Query query = em.createNamedQuery(queryName);
			Query countQuery = em.createNamedQuery(countQueryName);
			
			if(idSchemaType != -1) {
				query.setParameter("idSchemaType", idSchemaType);
				countQuery.setParameter("idSchemaType", idSchemaType);
			}
			
			if(appIds != null && appIds.trim().length() > 0) {
				List<Long> listAppIds = new ArrayList<Long>();
				for(String value : appIds.split(",")) {
					listAppIds.add(Long.valueOf(value));
				}
				query.setParameter("appIds", listAppIds);
				countQuery.setParameter("appIds", listAppIds);
			}
			
			if(start > 0 && limit > 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}

			readList.setResults(query.getResultList());
			
			List<Long> count = countQuery.getResultList();			
			readList.setTotal(count.get(0).longValue());
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("SchemasDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : SchemasDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
    @Transactional(readOnly=true)
    public ReadList readBySchemaTypeId(int start, int limit, List<String> listIdSchemaTypes, String appIds) {
        ReadList readList = new ReadList();
        try {
        	String countQueryName = "SchemaEntity.countAll";
        	String queryName = "SchemaEntity.findAll";
        	if(appIds != null && appIds.trim().length() > 0) {
        		queryName = "SchemaEntity.findAllByAppIdAndSchemaType";
        		countQueryName = "SchemaEntity.countAllByAppIdAndSchemaType";
        	}
        	
        	if(listIdSchemaTypes == null || listIdSchemaTypes.size() == 0) {
				queryName = "SchemaEntity.findByApplicationIds";
				countQueryName = "SchemaEntity.countByApplicationIds";
			}
        	
            List<Integer> idSchemaType = new ArrayList<Integer>();
            for (int i = 0; i < listIdSchemaTypes.size(); i++) {
                idSchemaType.add(Integer.valueOf(listIdSchemaTypes.get(i)));
            }
            
            Query query = em.createNamedQuery(queryName);
            Query countQuery = em.createNamedQuery(countQueryName);
            
            query.setParameter("idSchemaType", idSchemaType);
            countQuery.setParameter("idSchemaType", idSchemaType);
            	 
            if(appIds != null && appIds.trim().length() > 0) {
				List<Long> listAppIds = new ArrayList<Long>();
				for(String value : appIds.split(",")) {
					listAppIds.add(Long.valueOf(value));
				}
				query.setParameter("appIds", listAppIds);
				countQuery.setParameter("appIds", listAppIds);
			}
            
            if(start > 0 && limit > 0) {
            	query.setFirstResult(start);
       	 		query.setMaxResults(limit);
            }
            
            readList.setResults(query.getResultList());
            
            List<Long> count = countQuery.getResultList();
            readList.setTotal(count.get(0).longValue());
        } catch (Exception exception) {
            log.error("SchemasDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + "  : SchemasDao - read");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        em.clear();
        return readList;
    }
    
    @Transactional(readOnly = true)
	public ReadList read(int start, int limit, long idUser) {
		ReadList readList = new ReadList();
		try {
			Query query = em.createNamedQuery("SchemaEntity.findByUserId");
			query.setParameter("idUser", idUser);
			
			if(start > 0 && limit > 0) {
		       	query.setFirstResult(start);
		    	query.setMaxResults(limit);
		    }
			readList.setResults(query.getResultList());
		} catch (Exception exception) {
			log.error("SchemasDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : SchemasDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

    @Transactional(readOnly = true)
	public ReadList readByApplicationId(int start, int limit, List<Long> appIds) {
		ReadList readList = new ReadList();
		try {
			List<Long> count = em.createNamedQuery("SchemaEntity.countByApplicationIds")
								 .setParameter("appIds", appIds)
								 .getResultList();
			Query query = em.createNamedQuery("SchemaEntity.findByApplicationIds");
			query.setParameter("appIds", appIds);
			if(start > 0 && limit > 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
			readList.setResults(query.getResultList());
			readList.setTotal(count.get(0).longValue());
		} catch (Exception exception) {
			log.error("SchemasDao - readByApplicationId : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : SchemasDao - readByApplicationId");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

    @Transactional(readOnly = true)
	public ReadList readByApplicationId(int start, int limit, long appId) {
		ReadList readList = new ReadList();
		try {
			Query countQuery = em.createNamedQuery("SchemaEntity.countByApplicationId");
			Query query = em.createNamedQuery("SchemaEntity.findByApplicationId");
			
			query.setParameter("appId", appId);
			countQuery.setParameter("appId", appId);
			
			if(start > 0 && limit > 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
			readList.setResults(query.getResultList());
			
			List<Long> count = countQuery.getResultList();
			readList.setTotal(count.get(0).longValue());
		} catch (Exception exception) {
			log.error("SchemasDao - readByApplicationId : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : SchemasDao - readByApplicationId");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}	
	
	private boolean isSchemaNameMatches(String str) {
        return str.matches("\\w+");
	}

	public Create create(SchemaEntity schemaEntity) {
		Create create = new Create();
		if (schemaEntity.getName().equals("")) {
			create.setSuccess(false);
			create.setResults(schemaEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!checkName(schemaEntity.getIdSchema(), schemaEntity.getName(), schemaEntity.getIdSchemaType())) {
			create.setSuccess(false);
			create.setResults(schemaEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		if (schemaEntity.getStartDate() != null && schemaEntity.getEndDate() != null
				&& schemaEntity.getStartDate().after(schemaEntity.getEndDate())) {
			create.setSuccess(false);
			create.setMessage(I18n.getMessage("error.startDateEarlierEnd"));
			return create;
		}
		if (!isSchemaNameMatches(schemaEntity.getName())) {
			create.setSuccess(false);
			create.setMessage(I18n.getMessage("error.schemaNameNotMatch"));
			return create;
		}
		try {
			commonDao.persist(schemaEntity);
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("SchemasDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(null);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(schemaEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(SchemaEntity schemaEntity) {
		Update update = new Update();
		if (schemaEntity.getName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.requiredField"));			
			return update;
		}
		if (!checkName(schemaEntity.getIdSchema() , schemaEntity.getName(),schemaEntity.getIdSchemaType())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));			
			return update;
		}
		if (schemaEntity.getStartDate() != null && schemaEntity.getEndDate() != null && schemaEntity.getStartDate().after(schemaEntity.getEndDate())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.startDateEarlierEnd"));			
			return update;
		}
		if (!isSchemaNameMatches(schemaEntity.getName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.schemaNameNotMatch"));			
			return update;			
		}						
		/*SchemaEntity persEnt = find(schemaEntity.getIdSchema());
		if (!entitiesEqual(persEnt, schemaEntity)) {
			increaseSchemaVersion(schemaEntity);
		}  */
		try {
			commonDao.update(schemaEntity);
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
	
	private void increaseSchemaVersion(SchemaEntity ent) {
		Long new_ver;
		if (ent.getVersion().trim().equals("")) {
			new_ver = new Long(1);
		} else {
			new_ver = new Long(ent.getVersion()) + 1;
		}
		ent.setVersion(new_ver.toString());
	}

	public void increaseSchemaVersion(long schemaId) {
		SchemaEntity ent = find(schemaId);
		increaseSchemaVersion(ent);
		try {
			commonDao.update(ent);
		} catch (Exception exception) {
    			log.error("JobsDao - set is active: " + exception);
		}
	}

    @Transactional
	public Destroy destroy(long idSchema) {
		Destroy destroy = new Destroy();
		try {
			SchemaEntity schemaEntity = em.find(SchemaEntity.class, idSchema);
			if (schemaEntity != null)
				em.remove(schemaEntity);
			@SuppressWarnings("unchecked")
			List<SchemaFieldEntity> schemaFieldEntityList = em.createNamedQuery("SchemaFieldEntity.findByIdSchema")
					.setParameter("idSchema", idSchema).getResultList();
			if (schemaFieldEntityList != null) {
				for (int i = schemaFieldEntityList.size() - 1; i >= 0; i--) {
                    @SuppressWarnings("unchecked")
					List<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeEntityList = em.createNamedQuery("SchemaFieldEntity.findBySchemFieldId")
							.setParameter("idSchemaField", idSchema).getResultList();
					if(schemaFieldCheckTypeEntityList != null) {
						for(SchemaFieldCheckTypesEntity instance : schemaFieldCheckTypeEntityList) {
							em.remove(instance);
						}
					}
					em.remove(schemaFieldEntityList.get(i));
				}
			}
            @SuppressWarnings("unchecked")
            List<SchemaTriggerStatusEntity> schemaTriggerStatusEntityList = em.createNamedQuery("SchemaTriggerStatusEntity.findByIdSchema")
                    .setParameter("idSchema", idSchema).getResultList();
            if (schemaTriggerStatusEntityList != null) {
                for (int i = schemaTriggerStatusEntityList.size() - 1; i >= 0; i--)
                    em.remove(schemaTriggerStatusEntityList.get(i));
            }
			@SuppressWarnings("unchecked")
			List<SchemaXSDEntity> schemaXSDEntityList = em.createNamedQuery("SchemaXSDEntity.findByIdSchemaXSD")
					.setParameter("idSchemaXSD", idSchema).getResultList();
			if (schemaXSDEntityList != null) {
				for (int i = schemaXSDEntityList.size() - 1; i >= 0; i--)
					em.remove(schemaXSDEntityList.get(i));
			}
            @SuppressWarnings("unchecked")
            List<SchemaSQLEntity> schemaSQLEntityList = em.createNamedQuery("SchemaSQLEntity.findByIdSchemaSQL")
                    .setParameter("idSchemaSQL", idSchema).getResultList();
            if (schemaSQLEntityList  != null) {
                for (int i = schemaSQLEntityList .size() - 1; i >= 0; i--)
                    em.remove(schemaSQLEntityList .get(i));
            }
            @SuppressWarnings("unchecked")
            List<DatastreamEntity> datastreamEntityList = em.createNamedQuery("DatastreamEntity.findByIdSchema")
            		.setParameter("idSchema", idSchema).getResultList();
            if (datastreamEntityList  != null && datastreamEntityList.size() > 0) {
                for (int i = datastreamEntityList.size() - 1; i >= 0; i--)
                    em.remove(datastreamEntityList.get(i));
            }
            @SuppressWarnings("unchecked")
            List<FileEntity> fileEntityList = em.createNamedQuery("FileEntity.findBySchemaId")
            		.setParameter("idSchema", idSchema).getResultList();
            if (fileEntityList  != null && fileEntityList.size() > 0) {
                for (int i = fileEntityList.size() - 1; i >= 0; i--)
                    em.remove(fileEntityList.get(i));
            }
            @SuppressWarnings("unchecked")
            List<AlphanumericFieldValuesEntity> alphanumericFieldValuesEntityList = em.createNamedQuery("AlphanumericFieldValuesEntity.findByIdSchema")
            		.setParameter("idSchema", idSchema).getResultList();
            if (alphanumericFieldValuesEntityList  != null && alphanumericFieldValuesEntityList.size() > 0) {
                for (int i = alphanumericFieldValuesEntityList.size() - 1; i >= 0; i--)
                    em.remove(alphanumericFieldValuesEntityList.get(i));
            }
            @SuppressWarnings("unchecked")
            List<NumericFieldValuesEntity> numericFieldValuesEntityList = em.createNamedQuery("NumericFieldValuesEntity.findByIdSchema")
            		.setParameter("idSchema", idSchema).getResultList();
            if (numericFieldValuesEntityList  != null && numericFieldValuesEntityList.size() > 0) {
                for (int i = numericFieldValuesEntityList.size() - 1; i >= 0; i--)
                    em.remove(numericFieldValuesEntityList.get(i));
            }
            @SuppressWarnings("unchecked")
            List<UserSchemasEntity> userSchemasEntityList = em.createNamedQuery("UserSchemasEntity.findBySchemaId")
            		.setParameter("idSchema", idSchema).getResultList();
            if (userSchemasEntityList  != null && userSchemasEntityList.size() > 0) {
                for (int i = userSchemasEntityList.size() - 1; i >= 0; i--)
                    em.remove(userSchemasEntityList.get(i));
            }            
			destroyCustomErrors(idSchema);
		} catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("SchemasDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

	protected void destroyCustomErrors(long idSchema) {
		@SuppressWarnings("unchecked")
		List<CustomErrorEntity> customErrorsEntityList = em.createNamedQuery("CustomErrorEntity.findBySchemaId")
				.setParameter("schemaId", idSchema).getResultList();
		if (customErrorsEntityList != null) {
			for (CustomErrorEntity cee : customErrorsEntityList) {
				em.remove(cee);
			}
		}
	}
	
	public boolean checkName(Long idSchema, String name, int idSchemaType) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("SchemaEntity.countDuplicateByName")
					.setParameter("idSchema", idSchema).setParameter("name", name)
                    .setParameter("idSchemaType",idSchemaType).getResultList();
			if (result.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("SchemasDao - checkName : " + exception);
		}
		return false;
	}

    @Transactional(readOnly = false)
    public SchemaEntity find(long idSchema) {
		SchemaEntity schemaEntity = new SchemaEntity();
		try {
			schemaEntity = em.find(SchemaEntity.class, idSchema);
		} catch (Exception exception) {
			log.error("SchemasDao - find : " + exception);
		}
		return schemaEntity;
	}

    public void setActive(long schemaId, int isActive) {
		SchemaEntity ent = find(schemaId);
		if (ent != null) {
			ent.setIsActive(isActive);
			try {
				commonDao.update(ent);
			} catch (Exception exception) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				log.error("SchemasDao - set is active: " + exception);
			}
		}
	}
    @Transactional(readOnly = true)
    public boolean isActive(long schemaId) {
        boolean isActive=false;
        try {
            SchemaEntity ent = find(schemaId);
            if (ent != null) {

               if (ent.getIsActive()>0) isActive = true;
            }
            return isActive;
        }catch (Exception exception) {
            log.error("SchemasDao - isActive: " + exception);
            return isActive;
        }

    }
    @Transactional(readOnly = true)
    public boolean isAvailable(long schemaId) {
        boolean isAvailable=false;
        try {
            SchemaEntity ent = find(schemaId);
            if (ent != null) {

                if (ent.getIsAvailable()>0) isAvailable = true;
            }
            return isAvailable;
        }catch (Exception exception) {
            log.error("SchemasDao - isAvailable: " + exception);
            return isAvailable;
        }

    }
	@SuppressWarnings("unchecked")
	public List<SchemaEntity> findByName(String name) {
		try {
			return em.createNamedQuery("SchemaEntity.findByName").setParameter("name", name).getResultList();
		} catch (Exception exception) {
			log.error("SchemasDao - findByName : " + exception);
			return null;
		}
	}
	
	/**
	 * Gets all leaves of current SchemaEntity.
	 * 
	 * @return List<SchemaFieldEntity> - all leaves list
	 */
    @Transactional(readOnly = true)
	public List<SchemaFieldEntity> retrieveAllLeaves(long idSchema) {
		@SuppressWarnings("unchecked")
		List<SchemaFieldEntity> schemaFieldEntityList = em.createNamedQuery("SchemaFieldEntity.findLeavesByIdSchema")
				.setParameter("idSchema", idSchema).getResultList();
		return schemaFieldEntityList;
	}

    public void init() {
        SchemaEntity schemaEntity;
        try {
            @SuppressWarnings("unchecked")
            List<Long> result = em.createNamedQuery("SchemaEntity.count").getResultList();
            if (result.get(0) == 0L) {
                schemaEntity = new SchemaEntity();
                schemaEntity.setDescription("Default schema, ready to test");
                schemaEntity.setName(DEFAULT_SCHEMA);
                schemaEntity.setIsActive(1);
				schemaEntity.setIdApplication(((ApplicationsDao) AppContext.getApplicationContext().getBean("ApplicationsDao"))
						.getDefaultApp().getIdApplication());
				schemaEntity.setIdStreamType(StreamType.XML);
                schemaEntity.setIdSchemaType(SchemaType.VALIDATION);
                commonDao.persist(schemaEntity);
            }
        } catch(Exception exception) {
            log.error("SchemaDao - init : " + exception);
        }
    }
    
	/**
	 * Gets default schema. Note: init of this (default) schema is
	 * always invoked before this method.
	 * 
	 * @return default schema
	 */    
    @Transactional(readOnly = true)
	public SchemaEntity getDefaultSchema() {
		return (SchemaEntity) em.createNamedQuery("SchemaEntity.findByName").setParameter("name", DEFAULT_SCHEMA).getResultList()
				.get(0);
	}

    /**
     * Checks whether current schema has a link with loading stream (schema type == 4).
     * 
     * @param schemaEntity - current schema
     * @return loading stream (schema type == 4)
     */
	@Transactional(readOnly = true)
	public SchemaEntity hasLoadingStream(SchemaEntity schemaEntity) {
		@SuppressWarnings("unchecked")
		List<SchemaEntity> list = em
				.createQuery("SELECT e from SchemaEntity e Where e.idLinkedSchema = :idLinkedSchema")
				.setParameter("idLinkedSchema", (int) schemaEntity.getIdSchema()).getResultList();
		return list.size() == 0 ? null : list.get(0);
	}

}