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

package com.datacruncher.utils.schema;

import com.datacruncher.constants.FileInfo;
import com.datacruncher.junit.ResourceFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class JsonSchemaGenerationTest {

    @Rule
    public ResourceFile schemaResourceFile = new ResourceFile("/datafiles/5.formValidationTest.xsd", System.getProperty("java.io.tmpdir"));

    @Before
    public void setUp() {
        if (FileInfo.TESTS_WORKING_PATH == null) {
            FileInfo.TESTS_WORKING_PATH = System.getProperty("user.dir") + "/tests";
        }
    }

    @Test
    public void testGenerate() throws Exception {
        final File targetDirectory = Paths.get(FileInfo.TESTS_WORKING_PATH,
                String.format("%s_%d", getClass().getSimpleName(), System.currentTimeMillis())).toFile();
        targetDirectory.mkdirs();
        final XsdSchemaSourceGenerator schemaSourceGenerator = new XsdSchemaSourceGenerator(new JaxbGenerationResults());
        schemaSourceGenerator.generateSourceFromSchema(
                targetDirectory.getPath(), FileInfo.GENERATED_PACKAGE, schemaResourceFile.getFile().getPath());
        final String[] sourceFiles = FileUtils
                .listFiles(targetDirectory, new String[]{"java"}, true)
                .stream()
                .map(f -> f.getPath())
                .toArray(String[]::new);
        String[] compileArgs = new String[]{
                "-d",
                targetDirectory.getPath(),
                "-sourcepath",
                targetDirectory.getPath(),
        };
        compileArgs = ArrayUtils.addAll(compileArgs, sourceFiles);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compileResult = compiler.run(null, System.out, System.out, compileArgs);
        if (compileResult != 0) {
            fail("Compile error");
        }
        ClassLoader loader = new URLClassLoader(new URL[]{targetDirectory.toURI().toURL()});
        final Class<?> rootClass = loader.loadClass(String.format("%s.JvRoot", FileInfo.GENERATED_PACKAGE));
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema jsonSchema = schemaGen.generateSchema(rootClass);
        String jsonSchemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
        JsonNode jsonNode = mapper.readTree(jsonSchemaString);
        assertNotNull(jsonNode);
        assertFalse(jsonSchemaString.isEmpty());
        final JsonNode properties = jsonNode.get("properties");
        assertNotNull(properties);
        assertNotNull(properties.get("name"));
        assertNotNull(properties.get("ip"));
        assertNotNull(properties.get("port"));
    }
}

