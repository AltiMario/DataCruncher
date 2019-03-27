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
package com.datacruncher.services.webService;

import com.datacruncher.constants.FileInfo;
import com.datacruncher.junit.ResourceFile;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class JsonFormBuilderTest {

    private File targetDirectory;

    @Rule
    public ResourceFile schemaResourceFile = new ResourceFile("/datafiles/5.formValidationTest.xsd", System.getProperty("java.io.tmpdir"));

    @Before
    public void setUp() {
        if (FileInfo.TESTS_WORKING_PATH == null) {
            FileInfo.TESTS_WORKING_PATH = System.getProperty("user.dir") + "/tests";
        }
        targetDirectory = Paths.get(FileInfo.TESTS_WORKING_PATH,
                String.format("%s_%d", getClass().getSimpleName(), System.currentTimeMillis())).toFile();
        targetDirectory.mkdirs();
    }

    @After
    public void tearDown() throws Exception {
        if (targetDirectory.exists()) {
            FileUtils.deleteDirectory(targetDirectory);
        }
    }

    @Test
    public void testGenerate() throws Exception {
        final JsonForm jsonForm = new JsonFormBuilder().build(schemaResourceFile.getFile(), targetDirectory);
        File resultFile = Paths.get(FileInfo.TESTS_WORKING_PATH, String.format("%s-%d.json",
                FilenameUtils.getBaseName(schemaResourceFile.getFile().getPath()),
                System.currentTimeMillis())).toFile();
        FileUtils.writeStringToFile(resultFile, jsonForm.toString());

        assertNotNull(jsonForm);
        final JsonNode schema = jsonForm.getSchema();
        assertNotNull(schema);
        final JsonNode nameProperty = schema.get("name");
        assertNotNull(nameProperty);
        assertEquals(1, nameProperty.get("minLength").asInt());
        assertEquals(25, nameProperty.get("maxLength").asInt());
        assertEquals("[a-zA-Z0-9\\s.\\-]+", nameProperty.get("pattern").asText());
        final JsonNode ipProperty = schema.get("ip");
        assertNotNull(ipProperty);
        final JsonNode ipAnnotations = ipProperty.get("annotation");
        assertNotNull(ipAnnotations);
        assertTrue(ipAnnotations.isArray());
        String[] annotations = StreamSupport.stream(ipAnnotations.spliterator(), false)
                .map(a -> a.asText()).toArray(String[]::new);
        assertEquals(2, annotations.length);
        assertTrue(ArrayUtils.contains(annotations, "@inetaddress"));
        assertTrue(ArrayUtils.contains(annotations, "@domain"));
        assertNotNull(nameProperty);
        final JsonNode portProperty = schema.get("port");
        assertNotNull(portProperty);
        final JsonNode portAnnotations = portProperty.get("annotation");
        assertNotNull(portAnnotations);
        assertTrue(portAnnotations.isArray());
        annotations = StreamSupport.stream(portAnnotations.spliterator(), false)
                .map(a -> a.asText()).toArray(String[]::new);
        assertEquals(1, annotations.length);
        assertTrue(ArrayUtils.contains(annotations, "@serverport"));
    }
}

