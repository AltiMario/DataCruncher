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

package com.seer.datacruncher.services.webService;

import com.seer.datacruncher.constants.Servers;
import com.seer.datacruncher.datastreams.DatastreamsInput;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ServersEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.log4j.Logger;

import javax.jws.WebService;
import java.util.Locale;

@WebService
public class DatastreamsInputWS implements DaoSet {

	private static Logger log = Logger.getLogger(DatastreamsInputWS.class);

	public String datastreamsInputWS(String datastream, Long idSchema) throws Exception {
		try {
			Locale locale;
			MessageContext messageContext = MessageContext.getCurrentContext();
			UserEntity user = (UserEntity)messageContext.getProperty("user");
			if(!usersDao.isUserAssoicatedWithSchema(user.getIdUser(), idSchema)){
				locale = new Locale(user.getLanguage());
				throw new AxisFault(I18n.getMessage("error.user.notauthorized", null, locale));
			}
			DatastreamsInput datastreamsInput = new DatastreamsInput();
			ServersEntity serverEntity = serversDao.find(Servers.WEBSERVICE.getDbCode());
			if (serverEntity == null) {
				log.fatal("No Service available");
				throw new AxisFault("No Service available");
			} else if (serverEntity.getIsActive() == 0) {
				log.fatal("Service Temporarily Unavailable");
				throw new AxisFault("Service Temporarily Unavailable");
			}
			return datastreamsInput.datastreamsInput(datastream, idSchema, null);
		} catch (Exception e) {
			throw new AxisFault(e.getMessage(), e);
		}
	}
}