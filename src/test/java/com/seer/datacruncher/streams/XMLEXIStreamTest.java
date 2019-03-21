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

package com.seer.datacruncher.streams;

import static org.junit.Assert.assertTrue;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.datastreams.DatastreamsInput;
import com.seer.datacruncher.datastreams.EXI;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;

/**
 * This class generates exi representaion of com/seer/DataCruncher/streams/datafiles/xmlTestStream.xml
 * and then validates it in DatastreamsInput.
 */
public class XMLEXIStreamTest extends AbstractStreamTest {
	
	@Test
	public void testXMLEXIStream() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(stream_file_path + xml_stream_file_name);
			EXI.encodeXmlToEXI(in, baos);
		} catch (SAXException e) {
			assertTrue("SAXException while exi generation", false);
		} catch (IOException e) {
			assertTrue("IOException while exi generation", false);
		} catch (EXIException e) {
			assertTrue("EXI Exception while exi generation", false);
		}
		DatastreamsInput datastreamsInput = new DatastreamsInput();
		String res = datastreamsInput.datastreamsInput(null, (Long) schemaEntity.getIdSchema(),
				baos.toByteArray(), true);
		assertTrue("XMLEXI-Stream validation failed", Boolean.parseBoolean(res));
	}

	@Override
    int getStreamType() {
        return StreamType.XMLEXI;
	}
}
