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
