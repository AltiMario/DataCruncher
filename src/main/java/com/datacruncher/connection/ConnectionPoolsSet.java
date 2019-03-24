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

package com.datacruncher.connection;

import com.datacruncher.persistence.manager.DBConnectionChecker;
import com.datacruncher.persistence.manager.QuickDBRecognizer;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.DatabaseEntity;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * There's a set of connection pools in this class. Every 
 * connection pool corresponds to one database entity.
 */
public class ConnectionPoolsSet implements DaoSet {
    private static Logger log = Logger.getLogger(ConnectionPoolsSet.class);
	private static Map<Long, BoneCP> connPoolsSet = Collections.synchronizedMap(new HashMap<Long, BoneCP>());

	private ConnectionPoolsSet() {
		//never invoked
	}
	
	/**
	 * Gets connection by database id.  
	 * 
	 * @param id - database id
	 * @return Connection 
	 */
	public static Connection getConnection(Long id) {
		BoneCP connectionPool = connPoolsSet.get(id) != null ? connPoolsSet.get(id) : createConnectionPool(id);
		Connection connection = null;
		if(connectionPool != null) {
			try {
				connection = connectionPool.getConnection();
			} catch (SQLException e) {
				log.error("Get connection fail. Connection pool db.id = " + id, e);
			}
		}
		return connection;
    }

	/**
	 * Destroys connection pool. 
	 * 
	 * @param id - database id
	 */
	public static void destroyPool(Long id) {
		BoneCP pool = connPoolsSet.get(id);
		if (pool == null) {
			return;
		}
		pool.shutdown();
        String connPoolName = (connPoolsSet.get(id)).toString();
		connPoolsSet.remove(id);
        log.info("Close Conn ("+id+"):"+connPoolName);
	}
	
	/**
	 * Destroys all connection pools. 
	 * Invoked on application context destroy event.
	 * 
	 */
	public static void destroyAllPools() {
		for (BoneCP pool : connPoolsSet.values()) {
            log.info("INFO: Shutdown Pool: "+pool.toString());
			pool.shutdown();
		}
		connPoolsSet.clear();
	}
	
	private static BoneCP createConnectionPool(final Long id) {
		DatabaseEntity dbEntity = dbDao.find(id);
		if (dbEntity == null) {
			throw new RuntimeException("Connection pool creation: DB entity not found. Db.id = " + id);
		}
		try {
			Class.forName(DBConnectionChecker.JDBCDriver.getJDBCDriverClassById(dbEntity.getIdDatabaseType()));
		} catch (ClassNotFoundException e) {
			Logger.getLogger(ConnectionPoolsSet.class).error("db get connection error", e);
		}
		BoneCPConfig config = new BoneCPConfig();
		Map<String, String> map = QuickDBRecognizer.getConfigOverridesByDatabaseEntity(dbEntity);
		config.setJdbcUrl(map.get("hibernate.connection.url"));
		config.setUsername(map.get("hibernate.connection.username"));
		config.setPassword(map.get("hibernate.connection.password"));
//        config.setIdleConnectionTestPeriodInMinutes(60);
        config.setDefaultAutoCommit(true);
        config.setMinConnectionsPerPartition(5);
        config.setMaxConnectionsPerPartition(10);
        config.setPartitionCount(3);
        config.setAcquireIncrement(15);
        config.setConnectionTimeoutInMs(5 * 1000);
//        config.setCloseConnectionWatch(true);
//        config.setIdleMaxAgeInMinutes(20);
//        config.setStatementsCacheSize(100);
//        config.setReleaseHelperThreads(3);
        BoneCP pool= null;
		try {
            pool = new BoneCP(config);
            if (pool != null )
			    connPoolsSet.put(id, pool);
		} catch (SQLException e) {
			log.error("Fail of BoneCP connection pool initialisation for db.id = " + id, e);
		}
        log.info("Open Conn ("+id+"):"+(connPoolsSet.get(id) == null ? "" : connPoolsSet.get(id)).toString());
		return pool;
	}
}
