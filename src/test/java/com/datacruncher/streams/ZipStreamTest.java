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

package com.datacruncher.streams;

import static org.junit.Assert.assertTrue;

import com.datacruncher.constants.StreamType;
import com.datacruncher.datastreams.DatastreamsInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ZipStreamTest extends AbstractStreamTest{
	@Test
	public void testZipStream() {
		String fileName = properties.getProperty("zip_test_stream_file_name");
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(stream_file_path + fileName);
		byte[] arr = null;
		try {
			arr = IOUtils.toByteArray(in);
		} catch (IOException e) {
			assertTrue("IOException while Zip test file reading", false);
		}

		ZipInputStream inStream = null;
		try{
			inStream = new ZipInputStream(new ByteArrayInputStream(arr));
			ZipEntry entry ;
			while (!(isStreamClose(inStream)) && (entry = inStream.getNextEntry()) != null) {
				if(! entry.isDirectory()){
					DatastreamsInput datastreamsInput = new DatastreamsInput ();
					datastreamsInput.setUploadedFileName(entry.getName());
					byte[] byteInput = IOUtils.toByteArray(inStream);
					String res = datastreamsInput.datastreamsInput(null, (Long) schemaEntity.getIdSchema(), byteInput, true);
					assertTrue("Zip file validation failed", Boolean.parseBoolean(res));
				}
				inStream.closeEntry();
			}
		}catch(IOException ex){
			assertTrue("Error occured during fetch records from ZIP file.",false);
		}finally{
			if(in != null )
				try {
					in.close();
				} catch (IOException e){
				}
		}
	}

	@Override
	int getStreamType() {
		return StreamType.EXCEL;
	}
	
	private boolean isStreamClose(ZipInputStream inStream) {
		try {
			Class<?> c = inStream.getClass();
			Field in;
			in = c.getDeclaredField("closed");
			in.setAccessible(true);
			Boolean inReader = (Boolean) in.get(inStream);
			return inReader;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
}
