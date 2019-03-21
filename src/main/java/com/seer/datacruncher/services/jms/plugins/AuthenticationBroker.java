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
package com.seer.datacruncher.services.jms.plugins;

import com.seer.datacruncher.constants.Activity;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.I18n;

import java.util.List;
import java.util.Locale;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;

public class AuthenticationBroker extends BrokerFilter implements DaoSet {

	public AuthenticationBroker(Broker broker) {
		super(broker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.activemq.broker.BrokerFilter#addConnection(org.apache.activemq
	 * .broker.ConnectionContext, org.apache.activemq.command.ConnectionInfo)
	 */
	@Override
	public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {

		String remoteAddress = context.getConnection().getRemoteAddress();
		String userName = info.getUserName();
		String password = info.getPassword();
		Locale locale = null;
		if (remoteAddress != null && remoteAddress.indexOf("vm://") == -1) {
			UserEntity user = usersDao.login(userName, password);
			if (user == null) {
				throw new SecurityException(I18n.getMessage("error.user.invalidCredentials"));
			} else if (user.getEnabled() != 1) {
				locale = new Locale(user.getLanguage());
				throw new SecurityException(I18n.getMessage("error.user.notenabled", null, locale));
			} else {
				ReadList readList = roleActivityDao.read(user.getIdRole());
				@SuppressWarnings("unchecked")
				List<String> activities = (List<String>) readList.getResults();
				if (!activities.contains(Activity.SCHEMA_VALIDATE_DATASTREAM.getScriptCode())) {
					locale = new Locale(user.getLanguage());
					throw new SecurityException(I18n.getMessage("error.user.notauthorized", null, locale));
				}
			}
		}
		super.addConnection(context, info);
	}
}