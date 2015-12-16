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
package com.seer.datacruncher.services.webService.handlers;

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;
import java.util.Locale;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

public class AuthenticationHandler extends BasicHandler implements DaoSet {

	private Logger log = Logger.getLogger(AuthenticationHandler.class);
	private static final long serialVersionUID = 5387135062759064636L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
	 */
	@Override
	public void invoke(MessageContext messageContext) throws AxisFault {
		String logMsg = "AuthenticationHandler:invoke:";
		Locale locale = null;
		String userName = messageContext.getUsername();
		String password = messageContext.getPassword();
		if (log.isDebugEnabled()) {
			log.debug(logMsg + "UserName:" + userName);
			// log.debug(logMsg+"Password:"+password);
		}
		UserEntity user = usersDao.login(userName, password);
		if (user == null) {
			throw new AxisFault(I18n.getMessage("error.user.invalidCredentials", null, Locale.getDefault()));
		} else if (user.getEnabled() != 1) {
			locale = new Locale(user.getLanguage());
			throw new AxisFault(I18n.getMessage("error.user.notenabled", null, locale));
		} else {
			ReadList readList = roleActivityDao.read(user.getIdRole());
			@SuppressWarnings("unchecked")
			List<String> activities = (List<String>) readList.getResults();
			if (!activities.contains(Activity.SCHEMA_VALIDATE_DATASTREAM.getScriptCode())) {
				locale = new Locale(user.getLanguage());
				throw new AxisFault(I18n.getMessage("error.user.notauthorized", null, locale));
			}
		}
		messageContext.setProperty("user",user);
	}
}