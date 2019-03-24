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

package com.datacruncher.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet for testing file upload (returns JSON list of files with name, size and md5 checksum for each)
 */
public class FileEchoServlet extends HttpServlet {
    private static final long serialVersionUID = -7219359840799516368L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ArrayList<FileInfo> files = new ArrayList<FileInfo>();
        for (Part part : request.getParts()) {
            if (part == null || part.getSize() <= 0) {
                continue;
            }
            FileInfo fileInfo = new FileInfo();
            // TODO part.getSubmittedFileName() introduced in Servlet 3.1
            fileInfo.setFileName(part.getName());
            final byte[] content = IOUtils.toByteArray(part.getInputStream());
            fileInfo.setFileSize(content.length);
            fileInfo.setMd5(DigestUtils.md5Hex(content));
            files.add(fileInfo);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(files.toArray(new FileInfo[0])));
    }
}
