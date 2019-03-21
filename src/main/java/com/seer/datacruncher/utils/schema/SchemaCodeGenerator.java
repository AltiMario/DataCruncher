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

package com.seer.datacruncher.utils.schema;

import com.sun.tools.xjc.Driver;
import com.seer.datacruncher.constants.FileInfo;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SchemaCodeGenerator implements DaoSet {

    Logger log = Logger.getLogger(this.getClass());
    private JaxbGenerationResults genResults = new JaxbGenerationResults();

    public SchemaCodeGenerator() {

        if (AppContext.getApplicationContext().containsBean("testDummyBean")) {
            if (FileInfo.TESTS_WORKING_PATH == null)
                FileInfo.TESTS_WORKING_PATH = System.getProperty("user.dir") + "/tests";
        }
    }

    /**
     * This a public method for API that took the application context, schemaId
     * and schema cantaints and using helper methods it's create class files of
     * schema.
     *
     * @param schemaId
     * @param schemaCantaints
     * @throws com.seer.datacruncher.utils.schema.SchemaParsingException
     */
    public JaxbGenerationResults generateJAXBStuffFromSchema(long schemaId, byte[] schemaCantaints) throws SchemaParsingException {
        ApplicationContext context = AppContext.getApplicationContext();
        String genLocation = null;
        String schemaFile = "";

        // Clean already generated Class and Java files first if exists.
        cleanAllGenerated(schemaId);
        genResults.addSuccessfulResult("cleanAllGenerated");

        if (schemasDao.find(schemaId).getIdSchemaType() == SchemaType.STANDARD) {
            try {
                String xsdPath = FileInfo.SCHEMA_LIB_LOCATION + File.separator + schemaLibDao.find(schemasDao.find(schemaId).getIdSchemaLib()).getLibPath() + File.separator + schemaLibDao.find(schemasDao.find(schemaId).getIdSchemaLib()).getLibFile() + ".xsd";
                File f = new ClassPathResource(xsdPath).getFile();
                schemaFile = f.getAbsolutePath();


            } catch (IOException e) {
                genResults.addFailureResult("Getting Schema Library folder path", e);
            }
        } else {
            schemaFile = saveSchemaOnDisk(context, schemaId, schemaCantaints);
            genResults.addSuccessfulResult("saveSchemaOnDisk");
        }

        try {
            String filePath = FileInfo.GENERATED_FOLDER + File.separator + schemaId;
            File f = context.containsBean("testDummyBean") ? new File(FileInfo.TESTS_WORKING_PATH + filePath) : CommonUtils.getResourceFile(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            genLocation = f.getAbsolutePath();
            genResults.addSuccessfulResult("Getting 'generated' folder path");
        } catch (IOException e) {
            genResults.addFailureResult("Getting 'generated' folder path", e);
        }


        // generate source from schema
        new XsdSchemaSourceGenerator(genResults).generateSourceFromSchema(
                genLocation, FileInfo.GENERATED_PACKAGE + schemaId, schemaFile);
        genResults.addSuccessfulResult("Source from schema generation");

        // compile generated
        compileGenerated(context, schemaId);
        genResults.addSuccessfulResult("Generated files compilation");

        // clean generated after compile
        cleanGeneratedSourceFiles(context, schemaId);
        genResults.addSuccessfulResult("Clean generated source files");

        return genResults;
    }

    /**
     * Gets generation results.
     *
     * @return generation results
     */
    public JaxbGenerationResults getGenResults() {
        return genResults;
    }

    /**
     * This method clear all generated Class and Java file. It must be call when
     * a schema deleted.
     *
     * @param schemaId
     */
    public void cleanAllGenerated(long schemaId) {
        cleanGeneratedSourceFiles(AppContext.getApplicationContext(), schemaId);
        cleanGeneratedClassFiles(AppContext.getApplicationContext(), schemaId);
    }

    /**
     * This is a method that used to save the schema file on desk.
     *
     * @param context
     * @param schemaId
     * @param schemaCantaints
     * @return
     * @throws SchemaParsingException
     */
    public String saveSchemaOnDisk(ApplicationContext context, long schemaId, byte[] schemaCantaints)
            throws SchemaParsingException {
        try {
            String schemaFileStr = FileInfo.SCHEMA_LOCATION + File.separator + schemaId + File.separator + schemaId + ".xsd";
            String schemaDirStr = FileInfo.SCHEMA_LOCATION + File.separator + schemaId;

            // Create Path for schema file
            File schemaFilePath = context.containsBean("testDummyBean") ? new File(FileInfo.TESTS_WORKING_PATH + schemaDirStr) : CommonUtils.getResourceFile(schemaDirStr);
            if (!schemaFilePath.exists()) {
                schemaFilePath.mkdirs();
            }
            // Create empty schema file
            File schemaFile = context.containsBean("testDummyBean") ? new File(FileInfo.TESTS_WORKING_PATH + schemaFileStr) : CommonUtils.getResourceFile(schemaFileStr);
            if (!schemaFile.exists())
                schemaFile.createNewFile();

            // Copy contains in create schema file in schema folder in previous
            // step
            FileOutputStream fos = new FileOutputStream(schemaFile);
            fos.write(schemaCantaints);
            fos.close();
            return schemaFile.getAbsolutePath();
        } catch (IOException e) {
            genResults.addFailureResult("Saving schema on disk", e);
            throw new SchemaParsingException("Schema File could not be able to store on disk.", e);
        }
    }

    /**
     * This method compile all JAXB generated files.
     *
     * @throws IOException
     */
    private void compileGenerated(ApplicationContext context, long schemaId) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            String genLocation = context.containsBean("testDummyBean") ? FileInfo.TESTS_WORKING_PATH + FileInfo.GENERATED_FOLDER :
                    CommonUtils.getResourceFile(FileInfo.GENERATED_FOLDER).getAbsolutePath();
            genLocation = genLocation + File.separator + schemaId;
            File generatedDir = new File(genLocation + File.separator + FileInfo.GENERATED_PATH + schemaId);

            String compileDir = context.containsBean("testDummyBean") ? FileInfo.TESTS_WORKING_PATH :
                    CommonUtils.getResourceFile(FileInfo.CLASSPATH_FOLDER).getAbsolutePath();

            File[] fList = generatedDir.listFiles();

            List<String> list = new ArrayList<String>();
            if (fList == null) return;
            for (File file : fList) {
                if (file.getPath().endsWith(".java"))
                    list.add(file.getPath());

            }
            String[] str = new String[list.size() + 4];
            str[0] = "-d";
            str[1] = compileDir;
            str[2] = "-sourcepath";
            str[3] = genLocation;

            for (int i = 4; i < str.length; i++) {
                str[i] = list.get(i - 4);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            int res = compiler.run(null, System.out, ps, str);
            if (res != 0) {
                String errMsg = "Compilation failed: generated files compilation error - [" + genLocation + "].";
                genResults.addFailureResult("Generated files compilation error", errMsg + "\n\r" + baos.toString());
                log.error(baos.toString());
            }
        } catch (IOException e) {
            genResults.addFailureResult("Generated files compilation error", e);
        }
    }

    /**
     * Clear the generated JAVA file after class file generate.
     *
     * @param context
     * @param schemaId
     */
    private void cleanGeneratedSourceFiles(ApplicationContext context, long schemaId) {
        String genLocation;
        try {
            if (context.containsBean("testDummyBean")) {
                genLocation = FileInfo.TESTS_WORKING_PATH + FileInfo.GENERATED_FOLDER;
            } else {
                Resource r = context.getResource(FileInfo.GENERATED_FOLDER);
                if (r.exists()) {
                    genLocation = context.getResource(FileInfo.GENERATED_FOLDER).getFile().getAbsolutePath();
                } else {
                    // if file not found then exit without cleaning
                    return;
                }
            }
            genLocation = genLocation + File.separator + schemaId;
            File generatedDir = new File(genLocation + File.separator + FileInfo.GENERATED_PATH + schemaId);
            if (generatedDir.exists()) {
                File[] fList = generatedDir.listFiles();
                if (fList != null) {
                    for (File file : fList) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            genResults.addFailureResult("Clean generated source files", e);
        }
    }

    private void cleanGeneratedClassFiles(ApplicationContext context, long schemaId) {
        try {
            String filePath = FileInfo.CLASSPATH_FOLDER + File.separator + FileInfo.GENERATED_PATH + schemaId;
            File f = null;
            if (context.containsBean("testDummyBean")) {
                f = new File(FileInfo.TESTS_WORKING_PATH + filePath);
            } else {
                Resource r = context.getResource(filePath);
                if (r.exists()) {
                    f = context.getResource(filePath).getFile();
                } else {
                    //if no generated classes then exit without cleaning
                    return;
                }
            }
            if (f.exists()) {
                File[] fList = f.listFiles();
                if (fList != null) {
                    for (File file : fList) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            genResults.addFailureResult("Clean generated class files", e);
        }
    }
}
