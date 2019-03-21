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
package com.seer.datacruncher.services.ftp;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.utils.CryptoUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class UserAuthenticationManager implements UserManager, DaoSet {
			
	/**
	 * 
	 */
	public UserAuthenticationManager() {
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#getUserByName(java.lang.String)
	 */
	@Override
	public User getUserByName(String username) throws FtpException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#getAllUserNames()
	 */
	@Override
	public String[] getAllUserNames() throws FtpException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#delete(java.lang.String)
	 */
	@Override
	public void delete(String username) throws FtpException {
		
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#save(org.apache.ftpserver.ftplet.User)
	 */
	@Override
	public void save(User user) throws FtpException {

	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#doesExist(java.lang.String)
	 */
	@Override
	public boolean doesExist(String username) throws FtpException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#authenticate(org.apache.ftpserver.ftplet.Authentication)
	 */
	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

			String userName = upauth.getUsername();
			String password = upauth.getPassword();
			if (userName == null) {
				throw new AuthenticationFailedException("Authentication failed");
			}
			if (password == null) {
				password = "";
			}
			
			String encPassword = new CryptoUtil().encrypt(password);
			BaseUser user = new BaseUser();
			ApplicationConfigEntity configEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.FTP);
			if(configEntity != null && configEntity.getUserName().equals(userName) && configEntity.getPassword().equals(encPassword)) {
				String homeDirectory = configEntity.getOutputDir();
				if(homeDirectory.indexOf("/") != -1) {
					homeDirectory = homeDirectory.substring(0, homeDirectory.indexOf("/"));
				}
				user.setName(configEntity.getUserName());
				user.setPassword(password);
				user.setEnabled(true);
				user.setHomeDirectory(homeDirectory);
				user.setMaxIdleTime(0);
				
				List<Authority> listPermission = new ArrayList<Authority>();
				
				Authority writeAuthority = new WritePermission();
				listPermission.add(writeAuthority);	        
				user.setAuthorities(listPermission);
				
				return user;
			}
		} else if (authentication instanceof AnonymousAuthentication) {

		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#getAdminName()
	 */
	@Override
	public String getAdminName() throws FtpException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.UserManager#isAdmin(java.lang.String)
	 */
	@Override
	public boolean isAdmin(String username) throws FtpException {
		return false;
	}
}
