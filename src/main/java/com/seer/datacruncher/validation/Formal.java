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

package com.seer.datacruncher.validation;

import com.seer.datacruncher.constants.FileInfo;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.datastreams.DatastreamDTO;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.schema.SchemaParsingException;
import com.seer.datacruncher.utils.schema.SchemaValidator;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Formal implements DaoSet {

    private final Logger log = Logger.getLogger(this.getClass());

    public synchronized ResultStepValidation formalValidation(DatastreamDTO datastreamDTO) {
        ResultStepValidation result = new ResultStepValidation();
        long idSchema = datastreamDTO.getIdSchema();
        Map<String, String> resMap;
        try {
            SchemaValidator schemaValidator = new SchemaValidator();
            resMap = schemaValidator.validateSchema(idSchema);
            SchemaXSDEntity schemaXSDEntity = schemasXSDDao.read(idSchema);
            if(schemaValidator.isValidationSuccessful()){
                if (schemaXSDEntity.getIsVersIncreaseNeeded()) {
                    schemasXSDDao.setVersIncreaseNeeded(idSchema, false);
                    schemasDao.increaseSchemaVersion(idSchema);
                }
                Object jaxbObj = validateXML(idSchema, datastreamDTO.getOutput());
                if(jaxbObj != null){
                    result.setMessageResult(I18n.getMessage("success.validationOK"));
                    result.setValid(true);
                    result.setJaxbObject(jaxbObj);
                }else{
                    result.setValid(false);
                    result.setJaxbObject(null);
                }
            }else{
                result.setMessageResult(resMap.get("responseMsg"));
                result.setValid(false);
            }
        } catch (SAXException exception) {
            String msg = exception.getMessage();
            boolean isWarning = isWarning(msg, idSchema);
            String errMsg = getErrorMessage(msg, idSchema, isWarning);
            result.setMessageResult(errMsg);
            result.setWarning(isWarning);
            result.addFailedNodePath(getFailedNodePath(msg) + "$$" + errMsg);
            result.setValid(false);
        } catch (Exception exception) {
            log.error("Formal Validation - Exception : " + exception);
            result.setMessageResult(I18n.getMessage("error.system"));
            result.setValid(false);
        }
        return result;
    }

    protected String getErrorMessage(String msg, long idSchema, boolean isWarning) {
		return I18n.getMessage(isWarning ? "error.validationFormalWarn" : "error.validationFormal") + ": " + errorMsgFilter(msg);
	}

    protected String errorMsgFilter(String exceptionMessage) {
        if (exceptionMessage != null) {
            String message = exceptionMessage;
            String[] messageFilter = exceptionMessage.split(":");
            if (messageFilter.length > 1) {
                message = messageFilter[messageFilter.length - 1];
            }
            return message;
        } else
            return "";
    }
    
    /**
     * Checks whether problem field is warning.
     * 
     * @param msg
     * @param idSchema
     * @return true: warning / false: error
     */
    private boolean isWarning(String msg, long idSchema) {
		String[] customErrorsPaths = msg.split("%%");
		if (customErrorsPaths.length > 1) {
			List<SchemaFieldEntity> list = schemaFieldsDao.listSchemaFields(idSchema);
			for (String elem : customErrorsPaths) {
				for (SchemaFieldEntity ent : list) {
					if (ent.getPath("\\").toUpperCase().equals(elem.toUpperCase())) {
						if (ent.getErrorType() == StreamStatus.Warning.getCode()) {
							return true;
						}
					}
				}
			}
		}
    	return false;
    }
    
    /**
     * Get failed node path from error message.
     * 
     * @param msg
     * @return
     */
    private String getFailedNodePath(String msg) {
		String[] arr = msg.split("%%");
		String res = null;
		if (arr.length > 1) {
			res = arr[0];
		}
		return res;
    }

    /**
     * This method used to validate the xml over schema. but before call this
     * method user must call generateJAXBStuffFromSchema to generate required
     * JAXB objects.
     *
     * @return
     * @throws com.seer.datacruncher.utils.schema.SchemaParsingException
     * @throws IOException
     * @throws SAXException
     * @throws JAXBException
     */
    public boolean  validateXML(long schemaId, byte[] xml) throws SchemaParsingException, JAXBException, SAXException,
            IOException {
        Object jaxbObj = validateSchema(AppContext.getApplicationContext(), schemaId, null, xml, null);
        return jaxbObj != null;
    }

    public Object validateXML(long schemaId, String xml) throws SchemaParsingException, JAXBException, SAXException,
            IOException {
        return validateSchema(AppContext.getApplicationContext(), schemaId, null, xml.getBytes("UTF8"), null);
    }

    public boolean validateXML(long schemaId, InputStream xml) throws SchemaParsingException, JAXBException,
            SAXException, IOException {
        Object jaxbObj = validateSchema(AppContext.getApplicationContext(), schemaId, null, null, xml);
        return jaxbObj != null;
    }

    public boolean validateXML(long schemaId, File xml) throws SchemaParsingException, JAXBException, SAXException,
            IOException {
        Object jaxbObj = validateSchema(AppContext.getApplicationContext(), schemaId, xml, null, null);
        return jaxbObj != null;
    }


    /**
     * Method unmarshal the xml contains to jaxb object.
     */
    private Object validateSchema(ApplicationContext context, long schemaId, File xmlFile, byte[] xmlString,
                                  InputStream inputStream) throws SchemaParsingException, IOException, SAXException {
        ValidationEventCollector validationCollector = new ValidationEventCollector();
        try {
            JAXBContext jc;
            String genLocation;
            if (context.containsBean("testDummyBean") ) {
                //this branch used only in test cases. It loads class path of generated schema.
                genLocation = FileInfo.TESTS_WORKING_PATH + "/";
            } else {
                genLocation = CommonUtils.getResourceFile(FileInfo.CLASSPATH_FOLDER).getAbsolutePath();
            }

            File f = new File(genLocation);
            @SuppressWarnings("deprecation")
            URL[] urls = new URL[] {f.toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            try {
                loader.loadClass(FileInfo.GENERATED_PACKAGE + schemaId + ".ObjectFactory");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            jc = JAXBContext.newInstance(FileInfo.GENERATED_PACKAGE + schemaId, loader);

            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

            String schemaFileStr = FileInfo.SCHEMA_LOCATION + File.separator + schemaId + File.separator + schemaId + ".xsd";
            File schemaFile = context.containsBean("testDummyBean") ? new File(FileInfo.TESTS_WORKING_PATH + schemaFileStr) : CommonUtils.getResourceFile(schemaFileStr);
            if (schemasDao.find(schemaId).getIdSchemaType() == SchemaType.STANDARD) {
                schemaFileStr = FileInfo.SCHEMA_LIB_LOCATION + File.separator +schemaLibDao.find(schemasDao.find(schemaId).getIdSchemaLib()).getLibPath()+ File.separator + schemaLibDao.find(schemasDao.find(schemaId).getIdSchemaLib()).getLibFile()+".xsd";
                schemaFile = new ClassPathResource(schemaFileStr).getFile() ;
            }
            Schema schema = sf.newSchema(schemaFile);

            u.setSchema(schema);
            u.setEventHandler(validationCollector);

            if (xmlFile != null) {
                //by now it looks that this branch is never invoked
                return u.unmarshal(xmlFile);
            } else if (xmlString != null) {

                InputStream inStream= new ByteArrayInputStream(xmlString);
                Reader reader = new InputStreamReader(inStream,"UTF-8");

                InputSource is = new InputSource(reader);
                is.setEncoding("UTF-8");

                DOMParser parser = new DOMParser();
                parser.parse(is);
                Document document = parser.getDocument();
                return u.unmarshal(document);

            } else if (inputStream != null) {
                //by now it looks that this branch is never invoked
                return u.unmarshal(inputStream);
            } else {
                throw new SchemaParsingException("XML input is not provided.");
            }
        } catch (JAXBException jaxbe) {
            String msgRet = "";
            Set<String> set = new HashSet<String>();
            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                String failedXmlNodePath = getFailedXmlNodePath(locator.getNode());
                if (failedXmlNodePath != null) set.add(failedXmlNodePath);
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                msgRet += msg + " Line " + line + " Column " + column + ". \n";
            }
            //custom errors block
            if (set.size() > 0) {
                for (String s : set) {
                    msgRet = s + "%%" + msgRet;
                }
            }
            throw new SAXException(msgRet);
        }
    }

    /**
     * Gets the path of the node which failed during validation.
     *
     * @param node
     * @return path of the node which failed during validation
     */
    private String getFailedXmlNodePath(Node node) {
        if (node == null) return null;
        Node tmpNode = node;
        String str = node.getNodeName();
        while (tmpNode.getParentNode() != null) {
            tmpNode = tmpNode.getParentNode();
            str = tmpNode.getNodeName() + "\\" + str;
        }
        return str.startsWith("#document") ? str.substring("#document".length() + 1) : str;
    }
}