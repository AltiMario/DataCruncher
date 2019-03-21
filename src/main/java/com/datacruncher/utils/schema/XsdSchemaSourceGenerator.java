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

import com.sun.tools.xjc.Driver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class XsdSchemaSourceGenerator {
    private final JaxbGenerationResults genResults;

    public XsdSchemaSourceGenerator(JaxbGenerationResults genResults) {
        this.genResults = genResults;
    }

    /**
     * This method generate the java file from Schema . Its is same as XJC
     * compile does.
     *
     * @param genLocation
     * @param genPackage
     * @param schemaLocation
     * @throws IOException
     */
    void generateSourceFromSchema(String genLocation, String genPackage, String schemaLocation) throws SchemaParsingException {
        String infoMsg = "Source from schema generation";
        String errMsg = "XJC compiler could not able to parse schema - [" + schemaLocation + "].";
        try {
            String[] str = new String[]{"-d", genLocation, "-p", genPackage, schemaLocation};
            System.setProperty("java.net.useSystemProxies", "true");
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                PrintStream ps = new PrintStream(baos);
                int result = Driver.run(str, System.out, ps);
                if (result != 0) {
                    genResults.addFailureResult(infoMsg, errMsg + "\n\r" + baos.toString());
                }
            }
        } catch (Exception e) {
            genResults.addFailureResult(infoMsg, e);
            throw new SchemaParsingException(errMsg);
        }
    }
}
