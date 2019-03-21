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

import com.seer.datacruncher.constants.DateTimeType;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.Update;
import com.seer.datacruncher.jpa.entity.*;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.I18n;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public class SchemaFieldsDao {
	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected SchemaFieldsDao() {
	}

    @Transactional(readOnly = true)
	public SchemaFieldEntity find(long idSchemaField) {
		SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
		try {
			schemaFieldEntity = em.find(SchemaFieldEntity.class, idSchemaField);
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - find : " + exception);
		}
		return schemaFieldEntity;
	}

    @Transactional(readOnly = true)
	public Long findUnixDataType(long idSchema) {
		long numElem = 0;
		@SuppressWarnings("unchecked")
		List<Long> result = em.createNamedQuery("SchemaFieldEntity.findNumUnixDataType")
				.setParameter("idSchema", idSchema).getResultList();
		numElem = result.get(0).longValue();
		return numElem;
	
	}

    @Transactional(readOnly = true)
	public long findNumExtraCheck(long idSchema) {
		long numElem = 0;
		long numUnixDate = 0;
		@SuppressWarnings("unchecked")
		List<Long> result = em.createNamedQuery("SchemaFieldEntity.findNumExtraCheck")
				.setParameter("idSchema", idSchema).getResultList();
		numElem = result.get(0).longValue();
		numUnixDate =findUnixDataType(idSchema);
		numElem = numElem + numUnixDate;
		return numElem;
	}

	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public List<String> findExtraCheck(long idSchema) {
		List<String> elem = null;
		try {
			elem = em.createNamedQuery("SchemaFieldEntity.findExtraCheck").setParameter("idSchema", idSchema)
					.getResultList();
			if (findUnixDataType(idSchema) > 0) {
				if (elem == null)
					elem  = Arrays.asList("@unixDate");
				else
					elem.add("@unixDate");
			}
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - findExtraCheck : " + exception);
		}
		return elem;
	}
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<String> findSchemaFieldCheckTypes(long idSchemaField) {
        List<String> elem = null;
        try {
            elem = em.createNamedQuery("SchemaFieldEntity.findBySchemFieldId")
                    .setParameter("idSchemaField", idSchemaField)
                    .getResultList();
        } catch (Exception exception) {
            log.error("SchemaFieldsDao - findSchemaFieldCheckTypes : " + exception);
        }
        return elem;
    }

    public Map<String, Set<String>> getMapExtraCheck(long idSchema) {
        Map<String, Set<String>> mapExtraCheck = new HashMap<String, Set<String>>();
        ChecksTypeEntity checksTypeEntity;
        SchemaFieldEntity schemaFieldEntity;
        String pathField;
        String checkInfoClass;
        Set<String> checkSetClass = new HashSet<String>();
        try {
            String root= "";
            SchemaEntity schemaEntity = em.find(SchemaEntity.class, idSchema);
            int idStreamType = schemaEntity.getIdStreamType();
            if (idStreamType != StreamType.XML && idStreamType != StreamType.XMLEXI) {
                root = Tag.TAG_ROOT+"/";
            }
            @SuppressWarnings("unchecked")
            List resultListField = em.createNamedQuery("SchemaFieldEntity.findFieldsWithExtraCheck")
                    .setParameter("idSchema", idSchema).getResultList();
            if (resultListField != null && resultListField.size()>0) {
                String packageStmt = "com.seer.datacruncher.validation.";
                for (Iterator ii = resultListField.iterator(); ii.hasNext();) {
                    long idSchemaField  = Long.parseLong(ii.next().toString());

                    schemaFieldEntity = find(idSchemaField);
                    pathField = root+schemaFieldEntity.getOrigPath("/");


                    List resultList = em.createNamedQuery("ChecksTypeEntity.findLogicalCheckBySchemaFieldId")
                            .setParameter("idSchemaField", idSchemaField).getResultList();
                    if (resultList != null && resultList.size()>0) {
                        String className= "";
                        String tokenRule;
                        String extraCheckType = "";

                        for (Iterator i = resultList.iterator(); i.hasNext();) {
                            checksTypeEntity = (ChecksTypeEntity)   i.next();
                            className = checksTypeEntity.getClassName();
                            tokenRule = checksTypeEntity.getTokenRule();
                            if(className == null && tokenRule.equals("@spellcheck")){
                               className = ""+checksTypeEntity.getIdCheckType();
                            }
                            extraCheckType = checksTypeEntity.getExtraCheckType();
                            int fEnd = tokenRule.indexOf(":"); // find end
                            if(fEnd < 0){
                                if (!tokenRule.equals("@spellcheck"))
                                    tokenRule = "singleValidation";
                            } else{
                                tokenRule = "multipleValidation";
                            }

                            if(extraCheckType.equalsIgnoreCase("Custom code")){
                                if(checksTypeEntity.getClassName().contains("com.seer.datacruncher.validation.")){
                                    packageStmt = "";
                                }
                            }
                            if(StringUtils.isEmpty(packageStmt)){
                                checkInfoClass = String.valueOf(idSchema) + ":"+ tokenRule + "_"+ extraCheckType +"-com.seer.datacruncher.validation." +checksTypeEntity.getClassName();
                                checkSetClass.add(checkInfoClass);
                                checkInfoClass ="";
                            }else{
                                checkInfoClass = String.valueOf(idSchema) + ":"+ tokenRule + "_"+ extraCheckType +"-" +className;
                                checkSetClass.add(checkInfoClass);
                                checkInfoClass ="";
                            }
                        } 
                    }

                    if ( schemaFieldEntity.getIdFieldType() == FieldType.date && schemaFieldEntity.getIdDateTimeType() == DateTimeType.unixTimestamp){
                        checkInfoClass = String.valueOf(idSchema) + ":singleValidation_Coded-com.seer.datacruncher.validation.common.UnixTimestamp";
                        checkSetClass.add(checkInfoClass);
                        checkInfoClass ="";
                    }
                    if (pathField.equals("")){
                        checkSetClass =  new HashSet<String>();
                    }else {
                        mapExtraCheck.put(pathField,checkSetClass);
                        checkSetClass = new HashSet<String>();
                        pathField = "";
                    } 
                    
                }
            }

        } catch (Exception exception) {
            log.error("SchemaFieldsDao - getMapExtraCheck : " + exception);
        }
        return mapExtraCheck ;
    }

    @Transactional(readOnly = true)
	public SchemaFieldEntity root(long idSchema) {
		SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
		try {
			@SuppressWarnings("unchecked")
			List<SchemaFieldEntity> result = em.createNamedQuery("SchemaFieldEntity.findSchemaRoot")
					.setParameter("idSchema", idSchema).getResultList();
			if (result != null && result.size() == 1) {
				schemaFieldEntity = result.get(0);
			} else {
				schemaFieldEntity = null;
			}
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - root : " + exception);
		}
		return schemaFieldEntity;
	}
	
    public List<SchemaFieldEntity> listAllChild(long idParent) {
        return listAllChild(-1, idParent);
	}
    public List<SchemaFieldEntity> listElemChild(long idParent) {
        return listElemChild(-1, idParent);
    }
    public List<SchemaFieldEntity> listAttrChild( long idParent) {
        return listAttrChild(-1, idParent);
    }
    public List<SchemaFieldEntity> listAllChild(long idSchema, long idParent) {
        return listChild(idSchema, idParent, 0);
    }
    public List<SchemaFieldEntity> listElemChild(long idSchema, long idParent) {
        return listChild(idSchema, idParent, 1);
    }
    public List<SchemaFieldEntity> listAttrChild(long idSchema, long idParent) {
        return listChild(idSchema, idParent, 2);
    }
    public List<SchemaFieldEntity> listAttr(long idSchema) {
        return listChild(idSchema, -1, 3);
    }
	@SuppressWarnings("unchecked")
    private List<SchemaFieldEntity> listChild(long idSchema, long idParent, int type) {
        String queryName ="";
		List<SchemaFieldEntity> listChild = null;
		try {
			if (idSchema == -1) {
                if (type  == 0){
                    queryName = "SchemaFieldEntity.findAllByParentId";
                }else if (type  == 1){
                    queryName = "SchemaFieldEntity.findElemByParentId";
                }else if (type  == 2){
                    queryName = "SchemaFieldEntity.findAttrByParentId";
                }
				listChild = em.createNamedQuery(queryName).setParameter("idParent", idParent).getResultList();
			} else {
                if (idParent == -1) {
                    listChild = em.createNamedQuery("SchemaFieldEntity.findAttrByIdSchema").setParameter("idSchema", idSchema).getResultList();
                }else{
                    if (type  == 0){
                        queryName = "SchemaFieldEntity.findAllByParentIdAndSchemaId";
                    }else if (type  == 1){
                        queryName = "SchemaFieldEntity.findElemByParentIdAndSchemaId";
                    }else if (type  == 2){
                        queryName = "SchemaFieldEntity.findAttrByParentIdAndSchemaId";
                    }
                    listChild = em.createNamedQuery(queryName).setParameter("idParent", idParent)
                            .setParameter("idSchema", idSchema).getResultList();
                }
			}
		} catch (Exception exception) {
            if (type  == 0){
                log.error("SchemaFieldsDao - listAllChild : " + exception);
            } else if (type  == 1){
                log.error("SchemaFieldsDao - listElemChild : " + exception);
            } else if (type  == 2){
                log.error("SchemaFieldsDao - listAttrChild : " + exception);
		    } else if (type  == 3){
                log.error("SchemaFieldsDao - listAttr : " + exception);
            }
        }
		return listChild;
	}
	
	@SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public List<SchemaFieldEntity> listSchemaFields(long idSchema) {
		List<SchemaFieldEntity> listSchemaFields = null;
		try {
			listSchemaFields = em.createNamedQuery("SchemaFieldEntity.findByIdSchema")
					.setParameter("idSchema", idSchema).getResultList();
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - listSchemaFields : " + exception);
		}
		return listSchemaFields;
	}

    @Transactional
	public void destroy(long idSchemaField) {
		try {
			SchemaFieldEntity schemaFieldEntity = em.find(SchemaFieldEntity.class, idSchemaField);
			em.remove(schemaFieldEntity);
			AlphanumericFieldValuesEntity alphaNumFieldValueEntity = em.find(AlphanumericFieldValuesEntity.class,
					idSchemaField);
			if (alphaNumFieldValueEntity != null)
				em.remove(alphaNumFieldValueEntity);
			NumericFieldValuesEntity numericFieldValuesEntity = em.find(NumericFieldValuesEntity.class, idSchemaField);
			if (numericFieldValuesEntity != null)
				em.remove(numericFieldValuesEntity);
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - destroy : " + exception);
		}
	}

    public Update update(SchemaFieldEntity schemaFieldEntity) {
    	Update update = new Update();
		try {
			commonDao.update(schemaFieldEntity);
		} catch(Exception exception) {
			log.error("SchemaFieldsDao - update : " + exception);
		}
        update.setResults(schemaFieldEntity);
        update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));

		return update;
	}
	
	public Long create(SchemaFieldEntity schemaFieldEntity) {
		try {
			commonDao.persist(schemaFieldEntity);
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - create : " + exception);
			return null;
		}
		return schemaFieldEntity.getIdSchemaField();
	}

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public List<SchemaFieldEntity> rootForNonXML(long idSchema) {
		List<SchemaFieldEntity> schemaFieldList = null;
		try {
			schemaFieldList = em.createNamedQuery("SchemaFieldEntity.findSchemaRoot")
					.setParameter("idSchema", idSchema).getResultList();
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - root : " + exception);
		}
		return schemaFieldList;
	}

	/**
	 * Gets maximum element order number in current fields level.
	 * 
	 * @return
	 */
	public int getMaxOrderInLevel(long idSchema, long idParent) {
        List<SchemaFieldEntity> list = listAllChild(idSchema, idParent);
		int max = 0;
		for (SchemaFieldEntity ent : list) {
			int i = ent.getElementOrder();
			if (i > max) {
				max = i;
			}
		}
		return max;
	}
	
	/**
	 * Increments all elements in current level that are upper than elementOrder.
	 * 
	 * @param idSchema
	 * @param idNewParent
	 * @param elementOrder
	 */
    @Transactional
	public void incrementUpperElementOrder(long idSchema, long idNewParent, int elementOrder) {
		try {
			em.createNamedQuery("SchemaFieldEntity.incrementUpperElementOrder").setParameter("idSchema", idSchema)
					.setParameter("idParent", idNewParent).setParameter("elementOrder", elementOrder).executeUpdate();
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - incrementUpperElementOrder : " + exception);
		}
	}	
	
	/**
	 * Decrements all elements in current level that are upper than elementOrder.
	 * 
	 * @param idSchema
	 * @param idNewParent
	 * @param elementOrder
	 */
    @Transactional
	public void decrementUpperElementOrder(long idSchema, long idNewParent, int elementOrder) {
		try {
			em.createNamedQuery("SchemaFieldEntity.decrementUpperElementOrder").setParameter("idSchema", idSchema)
					.setParameter("idParent", idNewParent).setParameter("elementOrder", elementOrder).executeUpdate();
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - decrementUpperElementOrder : " + exception);
		}
	}		
	
	/**
	 * Gets schema field by given path and schema id.
	 * 
	 * @param path
	 * @param schemaId
	 * @param pathSeparator
	 * @return
	 */
	public SchemaFieldEntity getFieldByPath(String path, long schemaId, String pathSeparator) {
		List<SchemaFieldEntity> list = listSchemaFields(schemaId);
		for (SchemaFieldEntity ent : list) {
			if (path.toUpperCase().endsWith(ent.getPath(pathSeparator).toUpperCase())) {
				return ent;
			}
		}
		return null;
	}

  public SchemaFieldEntity getSchemaAllFieldByName(long idSchema, String name) {
      SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity();
      try {
          @SuppressWarnings("unchecked")
          List<SchemaFieldEntity> result = em.createNamedQuery("SchemaFieldEntity.findAllByNameAndSchemaId")
                  .setParameter("idSchema", idSchema).setParameter("name", name).getResultList();
          if (result != null && result.size() > 0) {
              schemaFieldEntity = result.get(0);
          } else {
              schemaFieldEntity = null;
          }
      } catch (Exception exception) {
          log.error("SchemaFieldsDao - root : " + exception);
      }
      return schemaFieldEntity;
  }

    @Transactional
    public void init() {
        SchemaFieldEntity schemaFieldEntity;
        try {
            @SuppressWarnings("unchecked")
            List<Long> result = em.createNamedQuery("SchemaFieldEntity.count").getResultList();
            if (result.get(0) == 0L) {
                schemaFieldEntity = new SchemaFieldEntity();
				long defaultSchemaId = ((SchemasDao) AppContext.getApplicationContext().getBean("SchemasDao")).getDefaultSchema()
						.getIdSchema();
                //Adding Root Node
                schemaFieldEntity.setIdParent(0);
                schemaFieldEntity.setIdSchema(defaultSchemaId);
                schemaFieldEntity.setName("root");
                schemaFieldEntity.setIdFieldType(FieldType.all);
                schemaFieldEntity.setIdCheckType(0);
                schemaFieldEntity.setElementOrder(0);
                commonDao.persist(schemaFieldEntity);
                long schemaFieldID = schemaFieldEntity.getIdSchemaField();

                //Adding Leaf Node
                schemaFieldEntity = new SchemaFieldEntity ();
                schemaFieldEntity.setIdParent(schemaFieldID);
                schemaFieldEntity.setIdSchema(defaultSchemaId);
                schemaFieldEntity.setName("child");
                schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
                schemaFieldEntity.setNillable(false);
                schemaFieldEntity.setIdCheckType(0);
                schemaFieldEntity.setIdAlign(1);
                schemaFieldEntity.setElementOrder(0);

                commonDao.persist(schemaFieldEntity);
            }
        } catch(Exception exception) {
            em.getTransaction().rollback();
            log.error("SchemaFieldDao - init : " + exception);
        }
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<SchemaFieldEntity> findAllAttributes(long idSchemaField) {
        List<SchemaFieldEntity> listSchemaFields = null;
        try {
            listSchemaFields = em.createNamedQuery("SchemaFieldEntity.findAllAttributes")
                    .setParameter("idSchemaField", idSchemaField).getResultList();
        } catch (Exception exception) {
            log.error("SchemaFieldsDao - listSchemaFields : " + exception);
        }
        return listSchemaFields;
    }
    
    @Transactional
   	public Destroy destroySchemFieldCheckTypes(long idSchemaField) {
   		Destroy destroy = new Destroy();
   		Map<String, Long> params = new HashMap<String, Long>();
   		params.put("idSchemaField", idSchemaField);
   		try {
   			@SuppressWarnings("unchecked")
   			List<SchemaFieldCheckTypesEntity> result = em.createNamedQuery("SchemaFieldEntity.findBySchemFieldId")
   					.setParameter("idSchemaField", idSchemaField).getResultList();
   			if (result != null) {
   				for (int i = result.size() - 1; i >= 0; i--)
   					em.remove(result.get(i));
   			}
   		} catch (Exception exception) {
   			log.error("destroy : " + exception);
   			destroy.setSuccess(false);
   			destroy.setResults(null);
   			return destroy;
   		}
   		destroy.setSuccess(true);
   		destroy.setResults(null);
   		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
   		return destroy;
   	}
    @Transactional
   	public Destroy destroySchemaFields(long idSchemaField) {
   		Destroy destroy = new Destroy();
   		try {
   			@SuppressWarnings("unchecked")
   			List<SchemaFieldEntity> listSchemaFields = em.createNamedQuery("SchemaFieldEntity.findAllByParentId")
				.setParameter("idParent", idSchemaField).getResultList();
   			if (listSchemaFields != null) {
   				for(SchemaFieldEntity instance : listSchemaFields) {
   					em.remove(instance);
   				}
   			}
   		} catch (Exception exception) {
   			log.error("destroy : " + exception);
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
	public List<SchemaFieldEntity> findAllByParentId(long idSchemaField) {
		List<SchemaFieldEntity> listSchemaFields = null;
		try {
			listSchemaFields = em.createNamedQuery("SchemaFieldEntity.findAllByParentId").setParameter("idParent", idSchemaField).getResultList();
		} catch (Exception exception) {
			log.error("SchemaFieldsDao - find : " + exception);
		}
		return listSchemaFields;
	}
}
