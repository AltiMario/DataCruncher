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

package com.datacruncher.persistence.manager;

import com.datacruncher.constants.DatabaseType;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

public class DBConnectionChecker {
	
	//Note in the example it is used mysql and postgresql,eventually 
	//in the other cases like oracle , it should better add a field
	//to the enum as separator for the connection string url
	public static enum JDBCDriver {
		
		MYSQL(DatabaseType.MySQL_driver, DatabaseType.MySQL_url, DatabaseType.MySQL_conn, DatabaseType.MySQL),
        ORACLE(DatabaseType.ORACLE_driver,DatabaseType.ORACLE_url,DatabaseType.ORACLE_conn,DatabaseType.ORACLE),
		SQLSERVER(DatabaseType.SQLServer_driver,DatabaseType.SQLServer_url,DatabaseType.SQLServer_conn,DatabaseType.SQLServer),
        POSTGRESQL(DatabaseType.PostgreSQL_driver,DatabaseType.PostgreSQL_url,DatabaseType.PostgreSQL_conn,DatabaseType.PostgreSQL),
		DB2(DatabaseType.DB2_driver,DatabaseType.DB2_url,DatabaseType.DB2_conn,DatabaseType.DB2),
		SQLITE(DatabaseType.SQLite_driver,DatabaseType.SQLite_url,DatabaseType.SQLite_conn,DatabaseType.SQLite),
		FIREBIRD(DatabaseType.Firebird_driver,DatabaseType.Firebird_url,DatabaseType.Firebird_conn,DatabaseType.Firebird),
		SAPDB(DatabaseType.SAPDB_driver,DatabaseType.SAPDB_url,DatabaseType.SAPDB_conn,DatabaseType.SAPDB),
		HSQLDB(DatabaseType.HSQLDB_driver,DatabaseType.HSQLDB_url,DatabaseType.HSQLDB_conn,DatabaseType.HSQLDB);
		
		private String drivername;
		private String connectionurl;
        private String databaseconn;
		private int id;

		private JDBCDriver(String drivername, String connectionurl,String databaseconn, int id) {
			this.drivername = drivername;
			this.connectionurl = connectionurl;
            this.databaseconn  = databaseconn;
			this.id = id;
		}

		private String getJDBCDriverClass() {
			return drivername;
		}
        private String getDatabaseConnStr() {
            return databaseconn;
        }
		public static String getJDBCDriverClassById(int id) {
			for (JDBCDriver driver : values()) {
				if (driver.getId() == id) {
					return driver.getJDBCDriverClass();										
				}				
			}
			throw new RuntimeException("No jdbc driver found!");
		}

		private String getConnectionUrl() {
			return connectionurl;
		}
		
		private int getId() {
			return id;
		}
	}

	private Configuration cfg;
	private boolean status = false;
	private boolean checkParams = true;
	private int attempts = 0; //number of attempts
	private int timeout = 20; //number of seconds before timeout
	private Session session = null;
    private final Logger log = Logger.getLogger(this.getClass());

	private String database_type;
	private String host;
	private int port;
	private String database_name;
	private String username;
	private String password;


	public DBConnectionChecker(String database_type, String host,
			int port, String database_name, String username, String password) {

		//initialize the fields 
		this.database_type = database_type;
		this.host = host;
		this.port = port;
		this.database_name = database_name;
		this.username = username;
		this.password = password;
		if (this.database_type.equals("") || this.host.equals("") ||
				this.port == 0 || this.database_name.equals("") ||
                this.username.equals("")) {

			checkParams = false;

		}else{

			//identify the appropriate driver
			String db_type = database_type.toUpperCase();

			//Added following line of code by KGandhi			
			db_type = db_type.replaceAll(" ", "");

			JDBCDriver driver = JDBCDriver.valueOf(db_type);

			//load the appropriate driver
			String drivername = driver.getJDBCDriverClass();

            //
            String databaseConnStr = driver.getDatabaseConnStr();

			try {
				Class.forName(drivername);
			} catch (ClassNotFoundException e) {
                log.error("Driver error: "+ e.getMessage());
			}

			//set the new configuration
			cfg = new Configuration();
			cfg.setProperty("hibernate.connection.driver_class", drivername);
			cfg.setProperty("hibernate.connection.username", this.username);
			cfg.setProperty("hibernate.connection.password", this.password);
			QuickDBManager.Dialect dialect = QuickDBManager.Dialect.valueOf(db_type.toUpperCase());
			cfg.setProperty("hibernate.dialect", dialect.getDialectClass());
			cfg.setProperty("hibernate.connection.release_mode", "on_close");
            String connectionurl = driver.getConnectionUrl() + host + ":" + port + databaseConnStr + this.database_name;

			cfg.setProperty("hibernate.connection.url", connectionurl);

			//part reserved to connection pool : in this case we need 
			//just one connection
			cfg.setProperty("c3p0.min_size", String.valueOf(1));
			cfg.setProperty("c3p0.max_size", String.valueOf(1));
			cfg.setProperty("c3p0.max_statements", String.valueOf(0));
			cfg.setProperty("c3p0.timeout", String.valueOf(timeout));
			cfg.setProperty("testConnectionOnCheckin", String.valueOf(true));
		}
	}

	public void useSession() { 
		SessionFactory sessions = null;

		// return if host unreachable 
		if(!isHostReachable(host, port)){
			status = false;
			attempts--;
			
			//after three attempts the timeout is doubled
			if(attempts % 3 == 0) {
				attempts = 0;
				timeout = 2 * timeout;
			}
			return;
		}
		try {			
			sessions = cfg.buildSessionFactory();
			session = sessions.openSession(); // open a new Session			
			session.doWork(
					new Work() {         
					public void execute(java.sql.Connection connection)
							throws SQLException {
						//connection accepted without problems
						status = true;
						attempts++;
						if(attempts % 3 == 0 && timeout / 2 > 0) {
							//three attempts success : timeout is
							//divided by 2 and remains > 0
							timeout /= 2;
						}
					}     
					} 	
			);
		}catch(Throwable ex) {
			//connection attempt error : the status is set to false
			status = false;
			attempts--;
			
			//after three attempts the timeout is doubled
			if(attempts % 3 == 0) {
				attempts = 0;
				timeout = 2 * timeout;
			}
		}finally {
			try{
				if(session != null)
					session.close();
			}catch(Exception e) {
                log.error("Session connection error on "+host+": "+ e.getMessage());
			}
			try{
				if(sessions != null)
					sessions.close();
			}catch(Exception e) {
                log.error("Closing session connection error on "+host+": "+ e.getMessage());
			}			
		}
	}

	public void closeConnection() {
		if(session != null && session.isConnected()) {
			session.close();
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean getStatus() { 
		return status;
	}
	public boolean getcheckParams() { 
		return checkParams;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DBConnectionChecker) {
			DBConnectionChecker o = (DBConnectionChecker)obj;
			return this.database_type.equals(o.database_type) 
			&& this.host.equals(o.host)
			&& this.port == o.port
			&& this.database_name.equals(o.database_name)
			&& this.username.equals(o.username)
			&& this.password.equals(o.password);
		}
		return false;
	}

	/**
	 * Method will check that either given host is reachable with given open port
	 * @return
	 */
	private boolean isHostReachable(String host , int port) {
		Socket socket = null;
		boolean result = false;
		try{
			InetAddress address = null;
			address = InetAddress.getByName(host);

			if(address.isReachable(4000)){
				// If host is reachable the test the port availability.
				socket = new Socket(address, port);
				result =  true;
			}else{
				result = false;
			}
		}catch(Exception e){
			result = false;
            log.error("Opening connection error on " + host + ": " + e.getMessage());
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
                log.error("Closing connection error on " + host + ": " + e.getMessage());
			}
		}
		return result;
	}
}
