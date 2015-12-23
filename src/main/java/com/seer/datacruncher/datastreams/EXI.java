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

package com.seer.datacruncher.datastreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;

public final class EXI {

	protected void encodeFile(String xmlInput, String exiOutput) throws SAXException, IOException, EXIException {

		OutputStream exiOS = new FileOutputStream(exiOutput);
		EXIResult exiResult = new EXIResult();
		exiResult.setOutputStream(exiOS);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(xmlInput));
		exiOS.close();
	}

	protected void decodeFile(String exiInput, String xmlOutput)
			throws SAXException, IOException, TransformerException, EXIException {

		SAXSource exiSource = new EXISource();
		XMLReader exiReader = exiSource.getXMLReader();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		InputStream exiIS = new FileInputStream(exiInput);
		exiSource = new SAXSource(new InputSource(exiIS));
		exiSource.setXMLReader(exiReader);
		OutputStream os = new FileOutputStream(xmlOutput);
		transformer.transform(exiSource, new StreamResult(os));
		os.close();
	}

	protected void encode(String xmlInput, String exiOutput) throws SAXException, IOException, EXIException {

		OutputStream exiOS = new FileOutputStream(exiOutput);
		EXIResult exiResult = new EXIResult();
		exiResult.setOutputStream(exiOS);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(xmlInput));
		exiOS.close();
	}

	protected String decode(String exiInput) throws SAXException, IOException, TransformerException, EXIException {
		SAXSource exiSource = new EXISource();
		XMLReader exiReader = exiSource.getXMLReader();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		InputStream exiIS = new ByteArrayInputStream(exiInput.getBytes());
		exiSource = new SAXSource(new InputSource(exiIS));
		exiSource.setXMLReader(exiReader);
		OutputStream os = new ByteArrayOutputStream();
		transformer.transform(exiSource, new StreamResult(os));
		os.close();
		return os.toString();
	}

	/**
	 * This method will use to convert EXI byte array into String
	 * 
	 * @param exiInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws EXIException
	 */
	public static String decode(byte[] exiInput) throws SAXException, IOException, TransformerException, EXIException {
		SAXSource exiSource = new EXISource();
		XMLReader exiReader = exiSource.getXMLReader();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		InputStream exiIS = new ByteArrayInputStream(exiInput);
		exiSource = new SAXSource(new InputSource(exiIS));
		exiSource.setXMLReader(exiReader);
		OutputStream os = new ByteArrayOutputStream();
		transformer.transform(exiSource, new StreamResult(os));
		os.close();
		return os.toString();
	}

	public static void encodeXmlToEXI(InputStream is, OutputStream os) throws SAXException, EXIException, IOException {
		// OutputStream exiOS = new FileOutputStream(exiOutput);
		EXIResult exiResult = new EXIResult();
		exiResult.setOutputStream(os);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(is));
		// exiOS.close();
	}

}
