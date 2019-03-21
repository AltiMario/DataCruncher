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

import com.datacruncher.constants.Alerts;
import com.datacruncher.constants.Roles;
import com.datacruncher.jpa.entity.UserApplicationsEntity;
import com.datacruncher.jpa.entity.UserEntity;
import com.datacruncher.jpa.entity.UserSchemasEntity;
import com.datacruncher.utils.CryptoUtil;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.Destroy;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.Update;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class UsersDao {

	Logger log = Logger.getLogger(this.getClass());
	private static final String EMAIL_PATTERN = ChecksTypeDao.EMAIL_PATTERN;

    @PersistenceContext
	private EntityManager em;

    @Autowired
    CommonDao commonDao;

	protected UsersDao() {
	}
	
	public UserEntity login(String userName, String password) {
		UserEntity userEntity = new UserEntity();
		try {
			@SuppressWarnings("unchecked")
			List<UserEntity> userList = em.createNamedQuery("UserEntity.findByUserName")
					.setParameter("userName", userName).getResultList();
			if (userList != null && userList.size() == 1) {
				String encPassword = new CryptoUtil().encrypt(password);
				userEntity = userList.get(0);
				if(!encPassword.equals(userEntity.getPassword())){
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
		}
		return userEntity;
	}
	
	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("UserEntity.findAll").getResultList());
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : UsersDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
    public ReadList getAdminUsers( String criteria) {
        StringBuilder queryStr = new StringBuilder("SELECT distinct u FROM UserEntity u WHERE u.idRole = 1 ");
        queryStr.append(criteria);

        ReadList readList = new ReadList();
        try {
            @SuppressWarnings("unchecked")
            List<UserEntity> result = em.createQuery(queryStr.toString())
                    .getResultList();
            if (result != null && result.size() > 0) {
                readList.setResults(result);
            }
        } catch (Exception exception) {
            log.error("UsersDao - getAdminUsers: " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + " : UsersDao - getAdminUsers");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
	public ReadList read(long createdBy) {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("UserEntity.findByCreatedBy").setParameter("createdBy", createdBy)
					.getResultList());
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : UsersDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	public ReadList read(int[] roleIds) {
		ReadList readList = new ReadList();
		String roles = "";
		for (int i : roleIds)
			roles += i + ",";
		if (roles.isEmpty()) return null;
		roles = roles.substring(0, roles.length() - 1);
		try {
			readList.setResults(em.createQuery(
					("SELECT u FROM UserEntity u WHERE u.idRole IN ( " + roles + ") ORDER BY u.idUser DESC")).getResultList());
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : UsersDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	/**
	 * Gets the list of all administrators in the system.
	 * 
	 * @return List of UserEntity
	 */
	public List<UserEntity> getAdmins() {
		int[] a = {Roles.ADMINISTRATOR.getDbCode()};
		ReadList r = read(a);
		List<UserEntity> list = new ArrayList<UserEntity>();
		if (r != null && r.getResults() != null)
			for (Object o : r.getResults())
				list.add((UserEntity) o);
		return list;
	}
	
	/**
	 * 
	 */
	public void init() {
		String logMsg = "UserDao:init():";
		UserEntity userEntry;
		try {
			log.debug(logMsg + "Entry");
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("UserEntity.count").getResultList();
			if (count.get(0).longValue() == 0L) {
				
				String encPassword = new CryptoUtil().encrypt("admin");
				userEntry = new UserEntity("admin", encPassword, "Administrator", "", "altimario@gmail.com", 1, 1, "en",
						Alerts.NEVER.getDbCode(), -1, null, "classic");
				commonDao.persist(userEntry);
			}
		} catch (Exception exception) {
			log.error("ChecksTypeDao - init : " + exception);
		} finally {
			log.debug(logMsg + "Exit");
		}
	}

	public Create create(UserEntity userEntity) {
		Create create = new Create();
		if (userEntity.getUserName().equals("")) {
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("label.userName") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (userEntity.getPassword().equals("")) {
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("label.password") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (userEntity.getEmail().equals("") || userEntity.getEmail().trim().length() == 0) {
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("label.email") + " : " + I18n.getMessage("error.requiredField"));
			return create;
		}
		if (!validEmail(userEntity.getEmail())) {
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("label.email") + " : " + I18n.getMessage("error.email.invalid"));
			return create;
		}
		if (!checkName(userEntity.getIdUser(), userEntity.getUserName())) {
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("label.name") + " : " + I18n.getMessage("error.alreadyExists"));
			return create;
		}
		try {
			String encPassword = new CryptoUtil().encrypt(userEntity.getPassword());
			userEntity.setPassword(encPassword);
			commonDao.persist(userEntity);
		} catch (Exception exception) {
			log.error("UserDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(userEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		create.setSuccess(true);
		create.setResults(userEntity);
		create.setMessage(I18n.getMessage("success.insRecord"));
		return create;
	}

	public Update update(UserEntity userEntity) {
		Update update = new Update();
		if (userEntity.getUserName().equals("")) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.userName") + " : " + I18n.getMessage("error.requiredField"));
			return update;
		}
		/*if (userEntity.getPassword().equals("")) {
			update.setSuccess(false);
			update.setMessage(context.getMessage("label.password" , null , Locale.getDefault()) + " : " + context.getMessage("error.requiredField" , null , Locale.getDefault()));			
			return update;
		}*/
		if (!checkName(userEntity.getIdUser(), userEntity.getUserName())) {
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("label.userName") + " : " + I18n.getMessage("error.alreadyExists"));
			return update;
		}
		try {
			List<UserApplicationsEntity> list = userEntity.getUserApplications();
			for (UserApplicationsEntity userApplicationsEntity : list) {
				commonDao.persist(userApplicationsEntity);
			}
			List<UserSchemasEntity> schemasList = userEntity.getUserSchemas();
			for (UserSchemasEntity userSchemasEntity : schemasList) {
				commonDao.persist(userSchemasEntity);
			}
			String encPassword = new CryptoUtil().encrypt(userEntity.getPassword());
			userEntity.setPassword(encPassword);
			commonDao.update(userEntity);
		} catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("UsersDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}

    @Transactional
	public Destroy destroyUserApps(long idUser) {
		Destroy destroy = new Destroy();
		try {
			@SuppressWarnings("unchecked")
			List<UserApplicationsEntity> result = em.createNamedQuery("UserApplicationsEntity.findByUserId")
					.setParameter("idUser", idUser).getResultList();
			if (result != null && result.size() > 0) {

				for (int i = result.size() - 1; i >= 0; i--)
					em.remove(result.get(i));
			}
		} catch (Exception exception) {
			log.error("UsersDao - destroy : " + exception);
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
	public Destroy destroyUserSchemas(long idUser) {
		Destroy destroy = new Destroy();
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("idUser", idUser);
		try {
			@SuppressWarnings("unchecked")
			List<UserApplicationsEntity> result = em.createNamedQuery("UserSchemasEntity.findByUserId")
					.setParameter("idUser", idUser).getResultList();
			if (result != null) {
				for (int i = result.size() - 1; i >= 0; i--)
					em.remove(result.get(i));
			}
		} catch (Exception exception) {
			log.error("UsersDao - destroy : " + exception);
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
	public Destroy destroy(long idUser) {
		Destroy destroy = new Destroy();
		try {
			UserEntity entity = em.find(UserEntity.class, idUser);
			if (entity != null) {
				em.remove(entity);
			} else {
				throw new EntityNotFoundException();
			}
		} catch (Exception exception) {
			log.error("UsersDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}
	
	private boolean checkName(Long idUser, String userName) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("UserEntity.countDuplicateByName").setParameter("idUser", idUser)
					.setParameter("userName", userName).getResultList();
			if (count.get(0).longValue() == 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("UsersDao - checkName : " + exception);
		}
		return false;
	}
	
	private boolean validEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		return pattern.matcher(email).matches();
	}
	
	public UserEntity find(long idUser) {
		UserEntity userEntity = new UserEntity();
		try {
			userEntity = em.find(UserEntity.class, idUser);
		} catch (Exception exception) {
			log.error("UsersDao - find : " + exception);
		}
		return userEntity;
	}
	
	public UserEntity findUserByNameNMailId(String userName, String email) {
		String logMsg = "UserDao:findByNameNMailId():";
		UserEntity userEntity = null;
		try {
			log.debug(logMsg + "Start");
			@SuppressWarnings("unchecked")
			List<UserEntity> result = em.createNamedQuery("UserEntity.findUserByNameNMailId")
					.setParameter("userName", userName).setParameter("email", email).getResultList();
			if (result != null && result.size() > 0)
				userEntity = result.get(0);
		} catch (Exception exception) {
			log.error(logMsg + "Exception : " + exception);
		} finally {
			log.debug(logMsg + "End");
		}
		return userEntity;
	}
	
	@SuppressWarnings("rawtypes")
	public List getUserApps(long idUser) {
		try {
			return em.createNamedQuery("UserApplicationsEntity.findByUserId").setParameter("idUser", idUser)
					.getResultList();
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getUserSchemas(long idUser) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("idUser", idUser);
		try {
			return em.createNamedQuery("UserSchemasEntity.findByUserId").setParameter("idUser", idUser).getResultList();
		} catch (Exception exception) {
			log.error("UsersDao - read : " + exception);
			return null;
		}
	}
	
	public boolean isUserAssoicatedWithSchema(long idUser, long idSchema) {
		try {
			@SuppressWarnings("unchecked")
			List<Long> count = em
					.createNamedQuery("UserSchemasEntity.findByUserIdNSchemaId")
					.setParameter("idUser", idUser)
					.setParameter("idSchema", idSchema).getResultList();
			if (count.get(0).longValue() != 0L) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			log.error("UsersDao - checkName : " + exception);
		}
		return false;
	}
	
	public void setActive(long userId, int isActive) {
		UserEntity ent = find(userId);
		if (ent != null) {			
			ent.setEnabled(isActive);				
			try {
                commonDao.update(ent);
			} catch (Exception exception) {
				log.error("UsersDao - set is active: " + exception);
			}	
		} 
	}

	/**
	 * Used for alerts.
	 * 
	 * @param criteria
	 * @param schemaId
	 * @return
	 */
	public ReadList getDataStreamUsers(String criteria, long schemaId) {
		String logMsg = "UsersDao: getDataStreamUsers:";
		// distinct here only needed for getUser() method, it does not needed
		// for other method but it doesn't break it so let it stay
		StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u FROM UserEntity u, UserSchemasEntity us, SchemaEntity s WHERE ");
		queryStr.append(" us.idUser = u.idUser AND us.idSchema = s.idSchema AND ");
		queryStr.append(" s.isAvailable = 1 AND ");
		if (schemaId > 0) {
			queryStr.append(MessageFormat.format(" us.idSchema = {0} AND ", schemaId));
		}
		queryStr.append(criteria);

		ReadList readList = new ReadList();
		try {
			@SuppressWarnings("unchecked")
			List<UserEntity> result = em.createQuery(queryStr.toString()).getResultList();
			if (result != null && result.size() > 0) {
				readList.setResults(result);
			}
		} catch (Exception exception) {
			log.error(logMsg + "Exception: " + exception);
			log.debug("Executed query:" + queryStr);
			exception.printStackTrace();
			readList.setSuccess(false);
			readList.setMessage(null);
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(null);
		return readList;
	}

}