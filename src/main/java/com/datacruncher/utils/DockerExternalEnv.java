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

package com.datacruncher.utils;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

/**
 * Dynamic external environment to apply dockerization
 */
public class DockerExternalEnv implements PersistenceUnitPostProcessor {

    private String sqlhost;
    private String nosqlhost;

    public DockerExternalEnv(){
        sqlhost=System.getProperty("sqlhost");
        nosqlhost=System.getProperty("nosqlhost");
    }

    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        if (sqlhost != null && !sqlhost.equals(""))
            pui.getProperties().setProperty("hibernate.connection.url", sqlhost );
        if (nosqlhost != null && !nosqlhost.equals(""))
            pui.getProperties().setProperty("kundera.nodes", nosqlhost);
    }

}
