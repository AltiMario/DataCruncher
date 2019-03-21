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

package com.datacruncher.services;


import com.datacruncher.junit.ResourceFile;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class ServiceScheduledJobTest {

    public static final String CONTEXT_PATH = "/testapp";
    public static final String SERVLET_PATH = "/upload";
    private Server server;

    private Properties properties;

    private int port;

    @Rule
    public ResourceFile resourceFile = new ResourceFile("/datafiles/zipTestStream.zip", System.getProperty("java.io.tmpdir"));

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream("test.properties");
        try {
            properties.load(propertiesStream);
        } catch (Exception e) {
            fail("Failed in loading test.properties file");
        }
        port = Integer.parseInt(properties.getProperty("jetty.port"));
        Log.setLog(new StdErrLog());
        server = new Server(port);
        server.setStopAtShutdown(true);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_PATH);
        server.setHandler(context);
        final ServletHolder servletHolder = new ServletHolder(new FileEchoServlet());
        servletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null, 10485760L, 20971520L, 6291456));
        context.addServlet(servletHolder, SERVLET_PATH);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        resourceFile.deleteFile();
        server.stop();
    }

    @Test
    public void testPostFileRemote() throws IOException, HttpException {
        assertNotNull(resourceFile);
        final ServiceScheduledJob serviceScheduledJob = new ServiceScheduledJob();
        String url = String.format("http://localhost:%d%s%s", port, CONTEXT_PATH, SERVLET_PATH);
        String responseBody = serviceScheduledJob.postFileRemote(url, resourceFile.getFile());
        assertTrue(StringUtils.isNotBlank(responseBody));
        final FileInfo[] files = new ObjectMapper().readValue(responseBody, FileInfo[].class);
        assertNotNull(files);
        assertEquals(1, files.length);
        final FileInfo expectedFileInfo = new FileInfo("file", 8512, "afda2c987d35a7f0cdebb926d78d5238");
        assertEquals(expectedFileInfo, files[0]);
    }
}
