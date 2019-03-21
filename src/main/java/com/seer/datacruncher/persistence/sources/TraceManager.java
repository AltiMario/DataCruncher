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

package com.seer.datacruncher.persistence.sources;


import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.FieldTypeEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.persistence.sources.dto.DatabaseDestinationDTO;
import com.seer.datacruncher.persistence.sources.dto.EnrichedTraceField;
import com.seer.datacruncher.spring.AppContext;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Class that allows you to generate the database configuration.
 * 
 * Classe che permette di generare la configurazione del database da utilizzare per
 * la persistenza del tracciato in ingresso.
 * 
 * @author danilo
 */
public class TraceManager implements DaoSet {
    
	public static final Properties config;
	static Logger log = Logger.getLogger(TraceManager.class);

	static {
		config = new Properties();
		try {
			FileInputStream fis = new FileInputStream(AppContext.getApplicationContext()
					.getResource("WEB-INF/classes/META-INF/JVquickdbrecognizer.properties").getFile());			
			config.load(fis);
		} catch (IOException ex) {
			log.error("Error loading properties file " + ex);
			ex.printStackTrace();
		}
	}

    /**
     *
     * @param recordToInsert   Record to insert
     * @return
     */
	public int initializeFieldType(FieldTypeEntity recordToInsert) {
		EntityManager em = entityManager;
		FieldTypeEntity result = null;
		int toReturn = 0;
		try{
			if (em != null) {
				Query query = em.createNamedQuery("FieldTypeEntity.count");
				if ((Long) query.getSingleResult() == 0) {
					em.getTransaction().begin();
					@SuppressWarnings("unused")
					Object[] toInsert = new Object[4];
					if (recordToInsert == null) {
						recordToInsert = new FieldTypeEntity();
						Set<Object> keys = config.keySet();
						@SuppressWarnings("rawtypes")
						Iterator it = keys.iterator();
						while (it.hasNext()) {
							String key = (String) it.next();
							if (key.contains("tipicampi.")) {
								log.warn("Insert line with key: " + key);
								int firstIndex = (key.indexOf(".") + 1);
								String description = key.substring(firstIndex);
								recordToInsert.setDescription(description);
								String value = config.getProperty(key);
								String[] splittedValues = value.split(",");
								recordToInsert.setIdFieldType(new Integer(splittedValues[0]));
								recordToInsert.setMappedType(splittedValues[1]);
								if (!splittedValues[2].equals("null")) {
									recordToInsert.setFieldLength(new Integer(splittedValues[2]));
								}
								result = em.merge(recordToInsert);
							}
						}
					} else {
						result = em.merge(recordToInsert);
					}
					em.getTransaction().commit();
					if (result != null)
						toReturn = 1;
				}
			}
		}catch(Exception e) {
			if(em != null)
				em.getTransaction().rollback();
			throw new RuntimeException(e);
		}
		return toReturn;
	}
     
    /**
     * Metodo che permette di recuperare tutti i campi di un determinato tracciato
     * @param idSchema   Id del tracciato di cui recuperare i campi
     * @return              Map che ha come chiave il numero d'ordine del campo e come valore
     *                      il campo del tracciato col tipo corrispondente.
     */
	private List<EnrichedTraceField> getTraceFieldList(long idSchema) {
		List<EnrichedTraceField> campiTracciatoMap = null;        
		List<SchemaFieldEntity> listaCampiTracciato = null;
		boolean skip = false;
		listaCampiTracciato = schemaFieldsDao.listSchemaFields(idSchema);
		if (listaCampiTracciato != null && !listaCampiTracciato.isEmpty()) {
			campiTracciatoMap = new ArrayList<EnrichedTraceField>();
			Map<Integer, FieldTypeEntity> mapFieldType = getFieldTypeList();
			for (SchemaFieldEntity campo : listaCampiTracciato) {
				skip = false;
				log.warn("CAMPO: " + campo.getName());
				EnrichedTraceField campoArricchito = new EnrichedTraceField();
				FieldTypeEntity tipoCampo = mapFieldType.get(campo.getIdFieldType());
				if (tipoCampo == null) {
					skip = true;
				}
                    /*if(tipoCampo!=null) {
                        log.warn("tipo campo: "+tipoCampo.toString());
                        String nameTipoCampo = tipoCampo.getDescription();
                        Integer lunghezzaTipoCampoInt = tipoCampo.getFieldLength();
                        Integer maxLengthInt = campo.getMaxLength();
                        if(maxLengthInt!=null && lunghezzaTipoCampoInt!=null) {
                            int lunghezzaTipoCampo = lunghezzaTipoCampoInt.intValue();
                            int maxLegthCurrentField=maxLengthInt.intValue();
                            if(maxLegthCurrentField>lunghezzaTipoCampo) {
                                Set<Integer> keys =mapFieldType.keySet();
                                Iterator<Integer> it= keys.iterator();
                                while(it.hasNext()) {
                                    Integer key = it.next();
                                    FieldTypeEntity current = mapFieldType.get(key);
                                    if(current.getFieldLength()!=null) {
                                        if((current.getFieldLength().intValue()>=maxLegthCurrentField) &&
                                                    (nameTipoCampo.contains(current.getDescription()) || (current.getDescription().contains(nameTipoCampo)))) {
                                            tipoCampo = mapFieldType.get(key);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        skip=true;
                    }*/
				if (!skip) {
					campoArricchito.setTraceField(campo);
					campoArricchito.setFieldType(tipoCampo);
					campoArricchito.setAttribute(campo.getIs_Attribute());
					campiTracciatoMap.add(campoArricchito);
				}
			}
		}
		return campiTracciatoMap;
    }

    /**
     * Metodo che permette di recuperare tutti i campi di un determinato tracciato
     * @return              Map che ha come chiave il numero d'ordine del campo e come valore
     *                      il campo del tracciato col tipo corrispondente.
     */
	@SuppressWarnings("unchecked")
	private Map<Integer, FieldTypeEntity> getFieldTypeList() {
		LinkedHashMap<Integer, FieldTypeEntity> fieldTypeMap = null;
        EntityManager em = entityManager;
        List<FieldTypeEntity> fieldTypeList = null;
		if (em != null) {
			Query query = em.createNamedQuery("FieldTypeEntity.findAll");
			fieldTypeList = (List<FieldTypeEntity>) query.getResultList();
			if (fieldTypeList != null && !fieldTypeList.isEmpty()) {
				fieldTypeMap = new LinkedHashMap<Integer, FieldTypeEntity>();
				for (FieldTypeEntity campo : fieldTypeList) {
					fieldTypeMap.put(campo.getIdFieldType(), campo);
				}
			}

		}
		return fieldTypeMap;
	}

    /**
     * Metodo che permette la configurazione del database di destinazione e la costruzione
     * del tracciato e dei campi che lo compongono
     * @param idTrace       Id of managed trace
     * @return                  DTO that mapped the db configuration, trace and trace fields.
     */
    public DatabaseDestinationDTO getDBDestinationConfiguration(long idTrace) {
		DatabaseDestinationDTO dbConfig = new DatabaseDestinationDTO();
		// gets schema entity
		dbConfig.setSchemaEntity(schemasDao.find(idTrace));
		dbConfig.setSchemaFieldList(getTraceFieldList(dbConfig.getSchemaEntity().getIdSchema()));
		// dbConfig.setApplicationEntity(getApplicationById(dbConfig.getSchemaEntity().getIdApplication(),dialect));
		dbConfig.setDatabaseEntity(dbDao.find(dbConfig.getSchemaEntity().getIdDatabase()));
		return dbConfig;
    }
}
