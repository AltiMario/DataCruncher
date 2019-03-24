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

/**
 *
 * @author danilo & stanislav
 */
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.ejb.Ejb3Configuration;

public class QuickDBManager {

    public static final Logger logWriter = Logger.getLogger(QuickDBManager.class);

    /**
     * !! This enum not used. All configs are taken from persistence.xml
     * 
     * Mantiene i nomi delle classi per i dialetti di hibernate.
     */
    public static enum Dialect {

        ORACLE("org.hibernate.dialect.OracleDialect"),
        MYSQL("org.hibernate.dialect.MySQLDialect"),
        DB2("org.hibernate.dialect.DB2Dialect"),
        POSTGRESQL("org.hibernate.dialect.PostgreSQLDialect"),
        SQLSERVER("org.hibernate.dialect.SQLServerDialect"),
        SQLITE("org.hibernate.dialect.SQLiteDialect"),
        HSQLDB("org.hibernate.dialect.HSQLDialect"),
        FIREBIRD("org.hibernate.dialect.FirebirdDialect"),
        SAPDB("org.hibernate.dialect.SAPDBDialect");       
        
        private String dialectClass;

        private Dialect(String dialectClass) {
            this.dialectClass = dialectClass;
        }

        public String getDialectClass() {
            return dialectClass;
        }
    }
    
    /**
     * Metodo che permette la persistenza di un'oggetto che mappa un'entita' JPA
     * @param streamObj                Oggetto da persistere
     * @param tipoDB           Nome dialetto da usare per la persistenza
     * @param configOverrides   Map contenente le èroprieta' per la connessione al giusto database
     * @return                  1 se l'oggetto e' stato persistito con successo, 0 altrimenti.
     * @throws Throwable
     */
	public static int insertQuery(StreamToDbDynamicObject streamObj, int tipoDB, @SuppressWarnings("rawtypes") Map configOverrides) throws Throwable {
		int returnedValue = 0;
		Object o = streamObj.getObject();
		logWriter.warn("QuickDBManager.insertQuery :configOverrides " + configOverrides);
		logWriter.warn("QuickDBManager.insertQuery :object to persist " + o.getClass().toString() + ", in DB type: "
				+ tipoDB);
		EntityManager em = null;
		if (streamObj.getEntityManager() == null) {
			Ejb3Configuration cfg = new Ejb3Configuration();
			cfg.configure("" + tipoDB + "_QuickDBRecognizerPU", configOverrides);			
			EntityManagerFactory emf = cfg.addAnnotatedClass(o.getClass()).buildEntityManagerFactory();
			em = emf.createEntityManager();
			streamObj.setEntityManager(em);
		} else {
			em = streamObj.getEntityManager();
		}
		em.getTransaction().begin();
		logWriter.warn("Open Transition");
		try {
			em.merge(o);
			logWriter.warn("Ogetto persistito");
			em.getTransaction().commit();
			returnedValue = 1;
		} catch (Throwable e) {
			em.getTransaction().rollback();
			logWriter.error("DB Rollback " + e);
			throw new Throwable(e.getMessage());
		}
		return returnedValue;
	}
}
