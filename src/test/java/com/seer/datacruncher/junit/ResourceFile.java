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

package com.seer.datacruncher.junit;

import org.junit.rules.ExternalResource;

import java.io.*;
import java.nio.charset.Charset;

public class ResourceFile extends ExternalResource {
    String res;
    String parent;
    File file = null;
    InputStream stream;

    public ResourceFile(String res) {
        this(res, ".");
    }

    public ResourceFile(String res, String parent) {
        this.res = res;
        this.parent = parent;
    }

    public File getFile() throws IOException {
        if (file == null) {
            createFile();
        }
        return file;
    }

    public InputStream getInputStream() {
        return stream;
    }

    public InputStream createInputStream() {
        return getClass().getResourceAsStream(res);
    }

    public String getContent() throws IOException {
        return getContent("utf-8");
    }

    public String getContent(String charSet) throws IOException {
        InputStreamReader reader = new InputStreamReader(createInputStream(),
                Charset.forName(charSet));
        char[] tmp = new char[4096];
        StringBuilder b = new StringBuilder();
        try {
            while (true) {
                int len = reader.read(tmp);
                if (len < 0) {
                    break;
                }
                b.append(tmp, 0, len);
            }
            reader.close();
        } finally {
            reader.close();
        }
        return b.toString();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        stream = getClass().getResourceAsStream(res);
    }

    @Override
    protected void after() {
        try {
            stream.close();
        } catch (IOException e) {
            // ignore
        }
        if (file != null) {
            file.delete();
        }
        super.after();
    }

    private void createFile() throws IOException {
        file = new File(parent, res);
        file.getParentFile().mkdirs();
        InputStream stream = getClass().getResourceAsStream(res);
        try {
            file.createNewFile();
            FileOutputStream ostream = null;
            try {
                ostream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = stream.read(buffer);
                    if (len < 0) {
                        break;
                    }
                    ostream.write(buffer, 0, len);
                }
            } finally {
                if (ostream != null) {
                    ostream.close();
                }
            }
        } finally {
            stream.close();
        }
    }

    public void deleteFile() {
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}