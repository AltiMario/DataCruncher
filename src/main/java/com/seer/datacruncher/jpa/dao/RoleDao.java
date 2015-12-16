/*
 * Copyright (c) 2015  www.see-r.com
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

import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.ReadOnlyTx;
import com.seer.datacruncher.jpa.entity.RoleEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@ReadOnlyTx
public class RoleDao {

	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected RoleDao() {
	}
	
	/**
	 * @return
	 */
	public ReadList read() {
		String logMsg = "RoleDao:read():";
		try {
			log.debug(logMsg + "Entry");
			ReadList readList = new ReadList();
			try {
				readList.setResults(em.createNamedQuery("RoleEntity.findAll").getResultList());
			} catch (Exception exception) {
				log.error(logMsg + "Exception: " + exception);
				readList.setSuccess(false);
				readList.setMessage(I18n.getMessage("error.error") + "  : RoleDao - read");
				return readList;
			}
			readList.setSuccess(true);
			readList.setMessage(I18n.getMessage("success.listRecord"));
			return readList;
		} finally {
			log.debug(logMsg + "End");
		}
	}
	
	/**
	 * 
	 */
	public void init() {
		String logMsg = "RoleDao:init():";
		RoleEntity roleEntry;
		try {
			log.debug(logMsg + "Entry");
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("RoleEntity.count").getResultList();
			if (count.get(0).longValue() == 0L) {
				roleEntry = new RoleEntity("Administrator", getAdminRoleDesc());
				commonDao.persist(roleEntry);
				roleEntry = new RoleEntity("Application Manager", getAppManagerRoleDesc());
				commonDao.persist(roleEntry);
				roleEntry = new RoleEntity("Operator", getOperatorRoleDesc());
				commonDao.persist(roleEntry);
				roleEntry = new RoleEntity("Dispatcher", getDispatcherRoleDesc());
				commonDao.persist(roleEntry);
				roleEntry = new RoleEntity("User", getUserRoleDesc());
				commonDao.persist(roleEntry);
			}
		} catch (Exception exception) {
			log.error("ChecksTypeDao - init : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}
	
	/**
	 * @param idRole
	 * @return
	 */
	public RoleEntity getRoleById(long idRole) {
		String logMsg = "RoleDao:getRoleById():";
		RoleEntity result = null;
		try {
			log.debug(logMsg + "Entry");
			result = em.find(RoleEntity.class, idRole);
		} catch (Exception exception) {
			log.error(logMsg + "Exception : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
		return result;
	}
	
	//------------------HELPERS-------------
	private String getAdminRoleDesc() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Responsibilities");
		sBuilder.append("- ").append("Manage your profile");
		sBuilder.append("- ").append("Manage applications");
		sBuilder.append("- ").append("Manage schemas");
		sBuilder.append("- ").append("Manage db");
		sBuilder.append("- ").append("Manages users");
		sBuilder.append("- ").append(
				"Promotes user to applications manager/operator/dispatcher");
		sBuilder.append("- ").append("Sends the data stream");
		sBuilder.append("- ").append("View report");
		return sBuilder.toString();
	}

	private String getAppManagerRoleDesc() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Responsibilities");
		sBuilder.append("- ").append("Manage your profile");
		sBuilder.append("- ").append("Manage your applications");
		sBuilder.append("- ").append("Manage your schemas");
		sBuilder.append("- ").append("Manage your db");
		sBuilder.append("- ").append("Promotes user to operator/dispatcher");
		sBuilder.append("- ").append("View report");
		return sBuilder.toString();
	}

	private String getOperatorRoleDesc() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Responsibilities");
		sBuilder.append("- ").append("Manage your profile");
		sBuilder.append("- ").append("Manage the schemas");
		sBuilder.append("- ").append("View report");
		return sBuilder.toString();
	}

	private String getDispatcherRoleDesc() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Responsibilities");
		sBuilder.append("- ").append("Manages your profile");
		sBuilder.append("- ").append("Sends the data stream of their schemas");
		return sBuilder.toString();
	}
	private String getUserRoleDesc() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Responsibilities");
		sBuilder.append("- ").append("Registers itself");
		return sBuilder.toString();
	}
}
