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

package com.datacruncher.jpa.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Its the base class for all DAO classes that class have some common methods that required in most of the DAO classes.
 * @author Naveen
 *
 */
public abstract class BaseDao {
	/**
	 * This map will hold the entity manager with his persistence unit name. So if entityManager already exist
	 * user will get the entitymanager from here else will create new and then save into this map
	 */
	private static Map<String, EntityManager> persistenceUnitEntitManagerMap = new HashMap<String, EntityManager>();
	
	/**
	 * This map will hold the entityManager and entityManager factory. so user can get the EntityManagerFactory using entitymanager.
	 */
	private static Map<EntityManager, EntityManagerFactory> entityManagerAndEMFactoryMap = new HashMap<EntityManager, EntityManagerFactory>();
	
	/**
	 * This Method is Deprecated 
	 * use the JPATemplate to perform ORM instead of direct use of EntityManager
	 * 
	 * Method will return the entityManager object on basis of persistenceUnit.
	 * @param persistenceUnit
	 * @return
	 * This Method is Deprecated use the JPATemplate to perform ORM instead of direct use of EntityManager
	 */
	@Deprecated
	public static EntityManager getEntityManager(String persistenceUnit){	

		EntityManager em = persistenceUnitEntitManagerMap.get(persistenceUnit);
		if(em != null && em.isOpen() 
				&& entityManagerAndEMFactoryMap.get(em).isOpen()) {
			return em;			
		}else{
			em = createEntityManager(persistenceUnit, null);
		}
		return em;
		
		}
	
	/**
	 * This Method is Deprecated 
	 * use the JPATemplate to perform ORM instead of direct use of EntityManager
	 * 
	 * Its a synchronized method that will used to create entityManager object if its not exist. 
	 * @param persistenceUnit
	 * @return
	 * 
	 */
	@Deprecated
	private static synchronized EntityManager createEntityManager(String persistenceUnit, Properties properties){	
		
		EntityManagerFactory entityManagerFactory = null;
		
		if(properties != null)
			entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, properties);
		else
			entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManagerAndEMFactoryMap.put(entityManager, entityManagerFactory);
		persistenceUnitEntitManagerMap.put(persistenceUnit, entityManager);
		return entityManager;
		
	}
	/**
	 * This Method is Deprecated use the JPATemplate to perform ORM instead of direct use of EntityManager
	 * @param persistenceUnit
	 * @param properties
	 * @return
	 */
	@Deprecated
	public static EntityManager getEntityManager(String persistenceUnit,
			Properties properties) {
		EntityManager em = persistenceUnitEntitManagerMap.get(persistenceUnit);
		if(em != null && em.isOpen()) {
			return em;		
		}else{
			return createEntityManager(persistenceUnit, properties);
		}
		
	}
}
