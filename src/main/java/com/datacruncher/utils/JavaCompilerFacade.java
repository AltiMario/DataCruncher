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
package com.datacruncher.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

public class JavaCompilerFacade {
    private static final Logger LOGGER = Logger.getLogger(JavaCompilerFacade.class);

    private File sourceDirectory;
    private File targetDirectory;
    private IOFileFilter fileFilter;

    public JavaCompilerFacade useSource(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public JavaCompilerFacade useTarget(File targetDirectory) {
        this.targetDirectory = targetDirectory;
        return this;
    }

    public JavaCompilerFacade useSourceFilter(IOFileFilter fileFilter) {
        this.fileFilter = fileFilter;
        return this;
    }

    public void compile() throws Exception {
        if (fileFilter == null) {
            fileFilter = new SuffixFileFilter(".java");
        }
        final String[] sourceFiles = FileUtils
                .listFiles(sourceDirectory, fileFilter, TrueFileFilter.INSTANCE)
                .stream()
                .map(f -> f.getPath())
                .toArray(String[]::new);
        if (sourceFiles.length == 0) {
            throw new Exception("No source files");
        }
        String[] compileArgs = new String[]{
                "-d",
                targetDirectory.getPath(),
                "-sourcepath",
                sourceDirectory.getPath(),
        };
        compileArgs = ArrayUtils.addAll(compileArgs, sourceFiles);
        LOGGER.debug(StringUtils.join(compileArgs, " "));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compileResult = compiler.run(null, System.out, System.err, compileArgs);
        if (compileResult != 0) {
            throw new Exception("Compile error");
        }
    }
}
