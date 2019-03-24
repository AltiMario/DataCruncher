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
package com.datacruncher.utils.mail;

import org.apache.log4j.Logger;
import org.springframework.core.io.AbstractResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamAttachmentDataSource extends AbstractResource {

	private Logger log = Logger.getLogger(this.getClass());
    private ByteArrayOutputStream outputStream;

    public StreamAttachmentDataSource(InputStream inputStream) {
        this.outputStream = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[256];
        try {
            while ((read = inputStream.read(buffer)) != -1) {
                getOutputStream().write(buffer, 0, read);
            }

        } catch (IOException e) {
        	log.error("Cannot create inputstream for mail attachment");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            	log.error("Cannot create inputstream for mail attachment");
            }
        }
    }

    public String getDescription() {
        return "Stream resource used for attachments";
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.outputStream.toByteArray());
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

}
