/*
 * Copyright (c) 2015  www.see-r.com
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

package com.seer.datacruncher.streams;

import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.datastreams.DatastreamsInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import static org.junit.Assert.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class DelimiterStreamTest extends AbstractStreamTest {
	
	@Test
	public void testDelimitedStream() {
		InputStream in  = this.getClass().getClassLoader().getResourceAsStream(stream_file_path + flat_file_delimited_file_name);
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(in, writer, "UTF-8");
		} catch (IOException e) {
			assertTrue("IOException while delimited file reading", false);
		}
		String stream = writer.toString();		
		DatastreamsInput datastreamsInput = new DatastreamsInput();
		String res = datastreamsInput.datastreamsInput(stream, (Long) schemaEntity.getIdSchema(), null, true);
		assertTrue("Delimited file validation failed", Boolean.parseBoolean(res));
	}

	@Override
    int getStreamType() {
        return StreamType.flatFileDelimited;
	}
}
