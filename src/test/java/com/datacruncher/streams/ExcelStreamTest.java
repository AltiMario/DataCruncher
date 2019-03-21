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

package com.datacruncher.streams;

import com.datacruncher.constants.StreamType;
import com.datacruncher.datastreams.DatastreamsInput;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ExcelStreamTest extends AbstractStreamTest {
	
	@Test
	public void testExcelStream() {
		String fileName = properties.getProperty("excel_test_stream_file_name");
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(stream_file_path + fileName);
		byte[] arr = null;
		try {
			arr = IOUtils.toByteArray(in);
		} catch (IOException e) {
			assertTrue("IOException while excel test file reading", false);
		}
		DatastreamsInput datastreamsInput = new DatastreamsInput();
		datastreamsInput.setUploadedFileName(fileName);
		String res = datastreamsInput.datastreamsInput(null, (Long) schemaEntity.getIdSchema(), arr, true);
		assertTrue("Excel file validation failed", Boolean.parseBoolean(res));
	}

	@Override
	int getStreamType() {
		return StreamType.EXCEL;
	}
}
