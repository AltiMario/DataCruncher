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

package com.datacruncher.persistence.manager;


import com.datacruncher.persistence.sources.TraceManager;
import com.datacruncher.persistence.sources.dto.DatabaseDestinationDTO;
import com.datacruncher.persistence.sources.dto.EnrichedTraceField;
import com.datacruncher.jpa.entity.DatabaseEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.utils.entity.DummyBean;
import com.datacruncher.utils.generic.StreamsUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import javax.management.ReflectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe che permette la configurazione e l'inserimento
 * di un flusso in un database conosciuto a runtime tramite
 * metadati presenti in un altro database di input
 * @author danilo & stanislav
 */
public class QuickDBRecognizer {

	private DatabaseDestinationDTO confDB = null;
	private Map<String, String> configOverrides = null;
	private static final String dummyBeanPackageWithDot = "com.datacruncher.utils.entity.";
	private String templateBean = dummyBeanPackageWithDot + "DummyBean";
	//private Map<String, String> insertedFields;
	private TraceManager traceManager = null;
	private Logger log = Logger.getLogger(QuickDBRecognizer.class);
	private static Map<String, StreamToDbDynamicObject> dynamicStreams = new HashMap<String, StreamToDbDynamicObject>();

    /**
     * Costruttore che permette la configurazione del database destinazione, del tracciato e dei campi che lo costituiscono
     * Come template del bean da persistere viene utilizzato ...quickdbrecognizer.persistence.entities.DummyBean presente nel progetto
     * creato come Entita' e con un id generato:<br><pre>
     *                                              &#64;Entity
     *                                              public class DummyBean implements Serializable {
     *                                                  &#64;Id
     *                                                  &#64;GeneratedValue
     *                                                  private Long id;
     *                                                  public Long getId() {
     *                                                     return id;
     *                                                  }
     *                                                  public void setId(Long id) {
     *                                                     this.id = id;
     *                                                  }
     *                                              }
     *                                            </pre>
     * @param traceName Nome tracciato da salvare
     * @param dialect       Dialetto da utilizzare per la connessione al database contenente la configurazione dei tracciati
     */

   /* public QuickDBRecognizer(String traceName) {
        traceManager = new TraceManager();
        insertedFields = new LinkedHashMap();
        confDB= traceManager.getDBDestinationConfiguration(traceName, null);
        log.warn("Creata configurazione Tracciato: "+confDB);
        configOverrides = new HashMap<String, Object>();
        configOverrides.put("hibernate.connection.url", confDB.getConnectionURL());
        log.warn("Conf Override hibernate.connection.url: "+confDB.getConnectionURL());
        configOverrides.put("hibernate.connection.username", confDB.getUsername());
        log.warn("Conf Override hibernate.connection.username: "+confDB.getUsername());
        configOverrides.put("hibernate.connection.password", confDB.getPassword());
        log.warn("Conf Override hibernate.connection.password: "+confDB.getUsername());
    }*/

    /**
     * Costruttore che permette la configurazione del database destinazione, del tracciato e dei campi che lo costituiscono
     * Come template del bean da persistere viene utilizzato ....quickdbrecognizer.persistence.entities.DummyBean presente nel progetto
     * creato come Entita' e con un id generato:<br><pre>
     *                                              &#64;Entity
     *                                              public class DummyBean implements Serializable {
     *                                                  &#64;Id
     *                                                  &#64;GeneratedValue
     *                                                  private Long id;
     *                                                  public Long getId() {
     *                                                     return id;
     *                                                  }
     *                                                  public void setId(Long id) {
     *                                                     this.id = id;
     *                                                  }
     *                                              }
     *                                            </pre>
     * @param traceName Nome tracciato da salvare
     * @param dialect       Dialetto da utilizzare per la connessione al database contenente la configurazione dei tracciati
     */

   /* public QuickDBRecognizer(String traceName, QuickDBManager.Dialect dialect) {
        traceManager = new TraceManager();
        insertedFields = new LinkedHashMap();
        confDB= traceManager.getDBDestinationConfiguration(traceName, dialect);
        log.warn("Creata configurazione Tracciato: "+confDB);
        configOverrides = new HashMap<String, Object>();
        configOverrides.put("hibernate.connection.url", confDB.getConnectionURL());
        log.warn("Conf Override hibernate.connection.url: "+confDB.getConnectionURL());
        configOverrides.put("hibernate.connection.username", confDB.getUsername());
        log.warn("Conf Override hibernate.connection.username: "+confDB.getUsername());
        configOverrides.put("hibernate.connection.password", confDB.getPassword());
        log.warn("Conf Override hibernate.connection.password: "+confDB.getUsername());
    }*/


    /**
     * Costruttore che permette la configurazione del database destinazione, del tracciato e dei campi che lo costituiscono
     * Come template del bean da persistere viene utilizzato ....quickdbrecognizer.persistence.entities.DummyBean presente nel progetto
     * creato come Entita' e con un id generato:<br><pre>
     *                                              &#64;Entity
     *                                              public class DummyBean implements Serializable {
     *                                                  &#64;Id
     *                                                  &#64;GeneratedValue
     *                                                  private Long id;
     *                                                  public Long getId() {
     *                                                     return id;
     *                                                  }
     *                                                  public void setId(Long id) {
     *                                                     this.id = id;
     *                                                  }
     *                                              }
     *                                            </pre>
     * @param traceId       Id del tracciato da salvare
     */

	public QuickDBRecognizer(long traceId) {
		traceManager = new TraceManager();
		//insertedFields = new LinkedHashMap<String, String>();
		traceManager.initializeFieldType(null);
		confDB = traceManager.getDBDestinationConfiguration(traceId);
		configOverrides = getConfigOverridesByDatabaseEntity(confDB.getDatabaseEntity());
	}
	
	public static Map<String, String> getConfigOverridesByDatabaseEntity(DatabaseEntity dbEnt) {
		Map<String, String> configOverrides = new HashMap<String, String>();
		configOverrides.put("hibernate.connection.url", DatabaseDestinationDTO.getConnectionUrlByDatabaseEntity(dbEnt));
		configOverrides.put("hibernate.connection.username", dbEnt.getUserName());
		configOverrides.put("hibernate.connection.password", dbEnt.getPassword());		
		return configOverrides;
	}

    /**
     * Costruttore per configurare il tracciato da importare e definire il bean da usare come template
     * @param traceName Nome tracciato da salvare
     * @param beanTemplate  Nome del bean (compreso nome package) del bean che verrà utilizzato come template
     *                      per la creazione dell'oggetto da persistere (tale bean deve esistere nel classpath ed essere compilato)
     * @param dialect       Dialetto da utilizzare per la connessione al database contenente la configurazione dei tracciati
     */
    /*public QuickDBRecognizer(String traceName, String beanTemplate, QuickDBManager.Dialect dialect) {
        this(traceName,dialect);
        templateBean=beanTemplate;
    }*/
    /**
     * Costruttore in cui vengono utilizzati dei parametri di default per definire il database di destinazione
     *              <pre>
     *                 Versione tracciato       1
     *                 Stato tracciato          1
     *                 Versione DB              5
     *                 Dialetto                 MYSQL
     *              </pre>
     * @param extConfigOverrides Proprietà del database di destinazione in cui bisogna inserire le tre entry (esempio riferito al database mysql in locale):
     *                           <pre>
     *                           hibernate.connection.url=jdbc:mysql://127.0.0.1:3306/nome-DB
     *                           hibernate.connection.username=root
     *                           hibernate.connection.password=root
     *                           </pre>
     * @param beanTemplate       Template del Bean che servirà per la costruzione della nuova classe da persistere.
     *                           Deve contenere almeno le annotazioni:
     *                           - Entity per dire a JPA che e' un'entita'
     *                           - id da associare alla chiave principale del tracciato
     * @param traceName      Nome del tracciato che si vuole definire (sara' anche il nome della tabella che lo conterra')
     */
    /*public QuickDBRecognizer(Map<String, Object> extConfigOverrides,String beanTemplate, String traceName) {
        this(traceName, QuickDBManager.Dialect.MYSQL);
        traceManager = new TraceManager();
        insertedFields = new LinkedHashMap();
        templateBean=beanTemplate;
        configOverrides = extConfigOverrides;
    }*/
	
	/**
	 * Gets schema name plus its version. Example: "Schema_1".
	 */
	public static String getSchemaNamePlusVersion(SchemaEntity schemaEnt) {
		return schemaEnt.getName().toUpperCase()
				+ (schemaEnt.getVersion().trim().length() == 0 ? "" : "_" + schemaEnt.getVersion());
	}
	
	/**
     * Defines the object that represents the schema to save to target database.
     * As a side effect, maintains a Map with the fields included in the plot, ordered through the
     * order number inside the schema.
     * 
     * Definisce l'Object che rappresenta il tracciato da persistere sul database destinazione
     * Come effetto collaterale, mantiene una Map con i campi inseriti nel tracciato, ordinati tramite il
     * proprio numero d'ordine all'interno del tracciato.
     * @return  L'Object da persistere
     * @throws ReflectionException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws CannotCompileException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NotFoundException
     */
	public StreamToDbDynamicObject traceDefine(List<Element> xmlTextNodes) throws ReflectionException,
			ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, SecurityException,
			CannotCompileException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException,
			NotFoundException {
		Object toReturn = null;
		StreamToDbDynamicObject result = new StreamToDbDynamicObject();
		SchemaEntity schemaEnt = confDB.getSchemaEntity();
		final String schemaName = getSchemaNamePlusVersion(schemaEnt);
		if (dynamicStreams.get(schemaName) != null) {
			return dynamicStreams.get(schemaName);
		}
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(pool.insertClassPath(new ClassClassPath(DummyBean.class)));
		CtClass pt = pool.get(templateBean);		
		ClassFile ccFile = pt.getClassFile();
		ccFile.setVersionToJava5();
		ConstPool constpool = ccFile.getConstPool();
		//add "Entity" annotation
		AnnotationsAttribute initAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		initAttr.addAnnotation(new Annotation("javax.persistence.Entity", constpool));
		pt.getClassFile().addAttribute(initAttr);
		//add annotations for field "id"
		CtField f = pt.getField("id");
		initAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);		
		initAttr.addAnnotation(new Annotation("javax.persistence.GeneratedValue", constpool));
		initAttr.addAnnotation(new Annotation("javax.persistence.Id", constpool));		
		f.getFieldInfo().addAttribute(initAttr);	
		//add annotations for field "path"
		CtField pathField = pt.getField("path");
		initAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);		
		initAttr.addAnnotation(new Annotation("javax.persistence.Column", constpool));
		initAttr.addAnnotation(new Annotation("javax.persistence.Lob", constpool));
		pathField.getFieldInfo().addAttribute(initAttr);			
		pt.stopPruning(true);
		pt.setName(dummyBeanPackageWithDot + schemaName);
		log.info("New Entity Name: " + dummyBeanPackageWithDot + schemaName);
		Map<String, Map<String, String>> insertedFields = new HashMap<String, Map<String, String>>();
		List<EnrichedTraceField> schemaFields = confDB.getSchemaFieldList();
		Map<String, EnrichedTraceField> preparedSchemaFields = new HashMap<String, EnrichedTraceField>();
		for (EnrichedTraceField etf : schemaFields) {
			String name = etf.getTraceField().getName().toUpperCase();
			name = StreamsUtils.recursiveGetUniqueFieldName(name, name, 0, preparedSchemaFields.keySet());
			preparedSchemaFields.put(name, etf);
		}
		for (Map.Entry<String, EnrichedTraceField> ent : preparedSchemaFields.entrySet()) {
			EnrichedTraceField enriched = ent.getValue();
			//boolean isLoadedFields = enriched.getTraceField() == null;
			String fieldTypeClassString = /*isLoadedFields ? "java.lang.String" :*/ enriched
					.getFieldType().getMappedType();
			if (fieldTypeClassString.contains("Date")) {
				//TODO: temporarily all dates are strings, but must to be a correct field "date"
				fieldTypeClassString = "java.lang.String";
			} else if (fieldTypeClassString.contains("Long") && enriched.getTraceField().getFractionDigits() != null) {
				// check if current numeric is decimal
				fieldTypeClassString = "java.lang.String";
			}			
			CtClass tipoJavaClass = ClassPool.getDefault().get(fieldTypeClassString);
			String upperCaseFieldName = ent.getKey();
			if (!insertedFields.containsKey(upperCaseFieldName)) {
				log.warn("DB Fiel Name " + upperCaseFieldName);
				log.warn("Fiel type " + fieldTypeClassString);
				f = new CtField(tipoJavaClass, upperCaseFieldName, pt);
				Map<String, String> map = new HashMap<String, String>();
				SchemaFieldEntity schemaFieldEntity = enriched.getTraceField();
				AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				Annotation annot = new Annotation("com.datacruncher.conf.annotations.Column", constpool);
				annot.addMemberValue("name", new StringMemberValue(upperCaseFieldName, constpool));
				if (schemaFieldEntity.getNillable() != null) {
					if (schemaFieldEntity.getNillable()) {
						annot.addMemberValue("nullable", new StringMemberValue("true", constpool));
					}
					if (!schemaFieldEntity.getNillable()) {
						annot.addMemberValue("nullable", new StringMemberValue("false", constpool));
					}
				}
				log.warn("Field length " + schemaFieldEntity.getMaxLength());
				if (schemaFieldEntity.getMaxLength() != null && schemaFieldEntity.getMaxLength() > 0) {
					annot.addMemberValue("length", new StringMemberValue(schemaFieldEntity.getMaxLength().toString(),
							constpool));
				}
				attr.addAnnotation(annot);
				f.getFieldInfo().addAttribute(attr);
				map.put("path", schemaFieldEntity.getPath("/"));
				map.put("type", fieldTypeClassString);
				map.put(StreamsUtils.IS_ATTR, String.valueOf(enriched.isAttribute()));
				pt.addField(f);
				insertedFields.put(upperCaseFieldName, map);
				CtMethod getMthd = CtNewMethod.make("public " + fieldTypeClassString + " get" + upperCaseFieldName
						+ "() { return " + upperCaseFieldName + "; }", pt);
				pt.addMethod(getMthd);
				CtMethod setMthd = CtNewMethod.make("public void set" + upperCaseFieldName + "(" + fieldTypeClassString
						+ " " + upperCaseFieldName + "" + ") { this." + upperCaseFieldName + "=" + upperCaseFieldName
						+ "; }", pt);
				pt.addMethod(setMthd);
			}
		}
		pt.writeFile();
		pt.defrost();
		@SuppressWarnings("rawtypes")
		Class clazz = pt.toClass();
		toReturn = clazz.newInstance();
		result.setInsertedFields(insertedFields);
		result.setLoadedFields(schemaEnt.getLoadedXSD());
		result.setObject(toReturn);
		dynamicStreams.put(schemaName, result);
		return result;
	}

    /**
     * Metodo utile per ottenere la lista dei campi (e relativi metodi get e set) dell'oggetto da persistere
     * @return  Map contenente come chiave il nome del campo e come valore il tipo java del campo stesso.
     */
    /*public Map<String, String> getInsertedFields() {
        return insertedFields;
    }*/
    
    /*private void removeAllInsertedFields() {
    	insertedFields.clear();
    }*/

    /**
     * Permette di persistere l'entita' creata nel database configurato nel db datacruncher
     * @param streamObj
     * @return          Return -1 se non e' riuscito ad inserire l'oggetto.
     */
	public int insertTrace(StreamToDbDynamicObject streamObj) {
		log.info("Insert Trace " + streamObj.getObject());
		int toReturn = -1;
		try {
			toReturn = QuickDBManager.insertQuery(streamObj, confDB.getDatabaseEntity().getIdDatabaseType(),
					configOverrides);
		} catch (Throwable ex) {
			log.error("ERROR: Object " + streamObj.getObject() + " not persisted in the database.\n" + ex.getMessage(), ex);
		}
		return toReturn;
	}
}
