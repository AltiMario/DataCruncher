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
package com.seer.datacruncher.constants;

import java.io.File;

public class FileInfo {
    public static String SCHEMA_LOCATION = "/schema";
    public static String SCHEMA_LIB_LOCATION = "schemaLib";
    public static String GENERATED_PACKAGE = "com.seer.datacruncher.generated.schema";
    public static String GENERATED_PATH = "com" + File.separator + "seer" + File.separator + "datacruncher"
            + File.separator + "generated" + File.separator + "schema";
    public static String GENERATED_FOLDER = "/generated";
    public static String CLASSPATH_FOLDER = "/WEB-INF/classes";
    public static String TESTS_WORKING_PATH;
    public static String EXIMPORT_FILE_EXTENSION =".jv";

}
