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

package com.seer.datacruncher.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jv_application_config")

public class ApplicationConfigEntity {
	
	public static final int FTP_DEFAULT_SAFE_PORT = 10021;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_application_config")
    private long idApplicationConfig;
    
    @Column(name = "config_type" , unique = true)
    private int configType;
    
    @Column(name = "id_database_type")
	private int idDatabaseType;
	
	@Column(name = "host")
	private String host;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "database_name")
	private String databaseName;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "password")
	private String password;
    
	@Column(name = "input_dir")
	private String inputDir;
	
	@Column(name = "output_dir")
	private String outputDir;
	
	@Column(name = "server_port")
	private int serverPort;
	
	@Column(name = "protocol")
	private String protocol;
	
	@Column(name = "encoding")
	private String encoding;
	
	@Column(name = "smtps_timeout")
	private String smtpsTimeout;
	
	@Column(name = "starttls")
	private Integer isStarTtls = 0;
	
	@Column(name = "smtps_authenticate")
	private Integer isSmtpsAuthenticate = 0;
		
	public long getIdApplicationConfig() {
		return idApplicationConfig;
	}

	public void setIdApplicationConfig(long idApplicationConfig) {
		this.idApplicationConfig = idApplicationConfig;
	}
	
	public int getConfigType() {
		return configType;
	}

	public void setConfigType(int configType) {
		this.configType = configType;
	}
	
	public int getIdDatabaseType() {
		return idDatabaseType;
	}

	public void setIdDatabaseType(int idDatabaseType) {
		this.idDatabaseType = idDatabaseType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInputDir() {
		return inputDir;
	}

	public void setInputDir(String inputDir) {
		this.inputDir = inputDir;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSmtpsTimeout() {
		return smtpsTimeout;
	}

	public void setSmtpsTimeout(String smtpsTimeout) {
		this.smtpsTimeout = smtpsTimeout;
	}

	public Integer getIsStarTtls() {
		return isStarTtls;
	}

	public void setIsStarTtls(Integer isStarTtls) {
		this.isStarTtls = isStarTtls;
	}

	public Integer getIsSmtpsAuthenticate() {
		return isSmtpsAuthenticate;
	}

	public void setIsSmtpsAuthenticate(Integer isSmtpsAuthenticate) {
		this.isSmtpsAuthenticate = isSmtpsAuthenticate;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}	    
	
}