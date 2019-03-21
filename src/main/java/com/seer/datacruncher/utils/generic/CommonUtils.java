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

package com.seer.datacruncher.utils.generic;

import com.seer.datacruncher.constants.ApplicationConfigType;
import com.seer.datacruncher.constants.FileInfo;
import com.seer.datacruncher.eventtrigger.DynamicClassLoader;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationConfigEntity;
import com.seer.datacruncher.spring.AppContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



public class CommonUtils  implements DaoSet {
	
	private static DocumentBuilderFactory docFactory;
	private static DocumentBuilder docBuilder;
	private static XPathFactory xpathFactory;
	private static XPath xpathInstance;
	static {
		try {
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
	        xpathFactory = XPathFactory.newInstance();
	        xpathInstance = xpathFactory.newXPath();
		} catch (ParserConfigurationException e) {
			org.apache.log4j.Logger.getLogger(CommonUtils.class).error("CommonUtils :: DocumentBuilderFactory init exception", e);
		}
	}
	
	private CommonUtils() {}
	
	/**
	 * MongoDB support is only inside EE module for Datastream entity. 
	 * 
	 * @return 'true' if MongoDB support exists and is checked in the form, 'false' otherwise
	 */
	public static synchronized boolean isMongoDB() {
        return getMongoChecked();
	}

    //TODO: get the value from the checkbox
    private static boolean getMongoChecked() {
        return false;
    }

    /**
     * Checks whether current application contains a module
     *
     * @return 'true' if module exists
     */
    public static synchronized boolean isModule() {
        boolean result = true;
        try {
            AppContext.getApplicationContext().getBean("isModule");
        } catch (NoSuchBeanDefinitionException e) {
            result = false;
        }
        return result;
    }
    
	/**
	 * Represents exception stack trace as string.
	 * 
	 * @param e - Exception
	 * @return exception stack trace as string
	 */
	public static String getExceptionAsString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}	


    /**
	 * Gets velocity template file name by its short name.
	 * 
	 * @param templateBaseName
	 * @return
	 */
	private static String getVelocityTemplateName(String templateBaseName) {
		StringBuilder templatePath = new StringBuilder();
		templatePath.append(templateBaseName);
		templatePath.append("_").append(Locale.getDefault().getLanguage());
		templatePath.append(".vm");
		return templatePath.toString();
	}
	
	/**
	 * Merge velocity template for email.
	 * 
	 * @param velocityEngine
	 * @param mailTemplate
	 * @param model
	 * @return merged string
	 */
	public static String mergeVelocityTemplateForEmail(org.apache.velocity.app.VelocityEngine velocityEngine,
			String mailTemplate, Map<String, String> model) {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, CommonUtils.getVelocityTemplateName(mailTemplate),
				"UTF-8", model);
	}

    public static File getResourceFile(String path) throws IOException {
        ApplicationContext context = AppContext.getApplicationContext();
        Resource r = context.getResource(path);
        if (r.exists()) {
            return r.getFile();
        } else {
            return new File(AppContext.getApplicationContext().getResource("/").getFile().getAbsolutePath() + path);
        }
    }
    
    /**
     * Checks whether current persistence unit is SQL SERVER (MS SQL).
     * 
     * @param em
     * @return
     */
    public synchronized static boolean isMsSql(EntityManager em) {
		return em == null ? false : em.getEntityManagerFactory() == null ? false : em.getEntityManagerFactory().getProperties()
				.get("hibernate.connection.driver_class") == null ? false : !em.getEntityManagerFactory().getProperties()
				.get("hibernate.connection.driver_class").equals("net.sourceforge.jtds.jdbc.Driver") ? false : true;
    }
    
    /**
     * Checks whether current string is JSON.
     * 
     * @param test
     * @return
     */
	public synchronized static boolean isJSON(String test) {
		boolean valid = false;
		try {
			new JSONObject(test);
			valid = true;
		} catch (JSONException ex) {
			valid = false;
		}
		return valid;
	}
	
	/**
	 * GZIP encode.
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public synchronized static byte[] gzipEncode(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		return obj.toByteArray();
	}

    /**
     * GZIP decode.
     * 
     * @param bytes
     * @return
     * @throws IOException
     */
	public static String gzipDecode(byte[] bytes) throws IOException {
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
		String outStr = "";
		String line;
		while ((line = bf.readLine()) != null) {
			outStr += line;
		}
		return outStr;
	}

    /**
     * Splits <tt>str</tt> around matches of the delimiter character.
     *
     * @param   str
     *          the string to split
     *
     * @param   delimiter
     *          the delimiting field string
     *
     * @param   chrDelim
     *          the delimiting character
     *
     * @return  the array of strings computed by splitting string
     *          around matches of the delimiter character.
     *
     **/
    public static String[] fieldSplit(String str, String delimiter, char chrDelim) {
        if ((str.indexOf(chrDelim, 0)) < 0){
            return   StringUtils.splitPreserveAllTokens(str,delimiter);
        }else{
            ArrayList<String> list = new ArrayList<String>();
            String record;
            List<String> streamsList = Arrays.asList(StringUtils.splitPreserveAllTokens(str,delimiter));
            int fEnd;
            for (int i = 0; i < streamsList.size(); i++) {
                record=streamsList.get(i);
                if ((record.indexOf(chrDelim, 0)) < 0){
                    list.add(record);
                }else{
                    if (record.startsWith(chrDelim+"")) { // check in start field
                        fEnd = record.indexOf(chrDelim, 1); // find end
                        if(fEnd < 0){ //not found
                            if((i+1) < streamsList.size()){
                                streamsList.set(i+1,record+delimiter+streamsList.get(i+1));
                            }else{
                                list.add(record);
                            }
                        }else{
                            list.add(record);
                        }
                    }
                }
            }
            int resultSize = list.size();
            String[] result = new String[resultSize];
            return list.subList(0, resultSize).toArray(result);
        }
    }
/**
 * Splits <tt>str</tt> around matches of the newline character.
 *
 * @param   str
 *          the string to split
 *
 * @param   delimiter
 *          the delimiting field string
 *
 * @param   chrDelim
 *          the delimiting character
 *
 * @return  the array of strings computed by splitting string
 *          around matches of the newline character.      
 *         
 **/
    public static String[] lineSplit(String str, String delimiter, char chrDelim) {
        String linedelimiter= "\n";
        String spltdelim ="\\";

        if ((str.indexOf(chrDelim, 0)) < 0){
            return   str.split(spltdelim+linedelimiter);
        }else{
            ArrayList<String> list = new ArrayList<String>();
            String record;
            List<String> streamsList = Arrays.asList(str.split(spltdelim+linedelimiter));
            int fStart;
            int fEnd;
            boolean checkfields = true;
            for (int i = 0; i < streamsList.size(); i++) {
                record=streamsList.get(i);
                if ((record.indexOf(chrDelim, 0)) < 0){
                        list.add(record);
                }else{  
                    if (record.startsWith(chrDelim+"")) { // check in first field
                        fEnd = record.indexOf(chrDelim+delimiter, 1); // find end
                        if(fEnd < 0){ //not found
                            if((i+1) < streamsList.size()){
                                streamsList.set(i+1,record+linedelimiter+streamsList.get(i+1));
                            }else{
                                list.add(record);
                            }
                            checkfields = false;
                        }else{
                            checkfields = true;
                        }
                    }
                    if(checkfields){
                        fStart = record.indexOf(delimiter+chrDelim, 1);
                        while(fStart < record.length()){
                            if(fStart > 0){
                                fEnd=record.indexOf(chrDelim+delimiter, fStart);
                                if(fEnd > 0){
                                    fStart = record.indexOf(delimiter+chrDelim, fEnd);
                                    if(fStart < 0){
                                        list.add(record);
                                        fStart = record.length();
                                    }
                                }else{
                                    fEnd=record.indexOf(chrDelim, fStart+2);
                                    if(fEnd < 0){
                                        if((i+1) < streamsList.size()){
                                            streamsList.set(i+1,record+linedelimiter+streamsList.get(i+1));
                                        }else{
                                            list.add(record);
                                        }
                                    } else{
                                        if(record.charAt(record.length()-1) == chrDelim){ //last field
                                            list.add(record);
                                        } else{
                                            if((i+1) < streamsList.size()){
                                                streamsList.set(i+1,record+linedelimiter+streamsList.get(i+1));
                                            }else{
                                                list.add(record);
                                            }
                                        }
                                    }
                                    fStart = record.length();
                                }
                            } else{
                                fStart = record.length();
                            }
                        }
                    }
                }
            }
            int resultSize = list.size();
            String[] result = new String[resultSize];
            return list.subList(0, resultSize).toArray(result);
        }
    }
    /**
     * Method parse the schema file and identify the xpath expression for nodes that have annotation value
     * annotation could be @spellchaeck , @partitaiva, @codicefiscale, etc.
     * @param xmlSchema
     * @param annotation

     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws IOException
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public static Set<String> parseSchemaAndGetXPathSetForAnnotation(ByteArrayInputStream xmlSchema, String annotation)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document doc = docBuilder.parse(xmlSchema);
        XPathExpression expr = xpathInstance.compile("//annotation/appinfo/text()");

        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (annotation.equals(nodes.item(i).getNodeValue())) {
                set.add(fetchXPathXpressionOfNode(nodes.item(i)));
            }
        }
        return set;
    }
    /**
     * That is a helping method that take Node and it iterate backword till root node and return the xpath expression
     * to reach that Node.
     * @param node
     * @return
     */
    private static String fetchXPathXpressionOfNode(Node node) {
        String nodeXPathExpression = null;
        Node n = node.getParentNode();
        while(n != null) {
            if(n.getNodeName() != null && (n.getNodeName().endsWith("element") || n.getNodeName().endsWith("attribute"))) {
                if(nodeXPathExpression == null) {
                    nodeXPathExpression  = n.getAttributes().getNamedItem("name").getNodeValue();
                }else {
                    nodeXPathExpression = n.getAttributes().getNamedItem("name").getNodeValue() + "/" + nodeXPathExpression;
                }
            }
            n = n.getParentNode();
        }
        return nodeXPathExpression;
    }

    public static Object  getJaxbObjectFromXML(long schemaId, String xmlString) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(FileInfo.GENERATED_PACKAGE+schemaId);
        ByteArrayInputStream input = new ByteArrayInputStream (xmlString.getBytes());
        Unmarshaller u = jc.createUnmarshaller();
        return u.unmarshal( input);
    }

    public static Document createXMLDocument(byte[] xmlStream) throws Exception {
        InputStream inStream= new ByteArrayInputStream(xmlStream);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();

        return builder.parse(inStream);
    }
    public static NodeList readXMLNodes(Document doc, String xpathExpression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xpathExpression);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        return (NodeList) result;
    }
    public static String getExceptionMessage(Exception ex) {
        String msg= ex.getMessage();
        if (msg != null){
            return msg;
        }else{
            try{
                msg="";
                StackTraceElement[] trace = ex.getStackTrace();
                Throwable e1 = ex.getCause();
                if(e1 != null){
                    for (int i = 0; i < trace.length; i++){
                        if(e1 != null){
                            msg= e1.getMessage();
                            if (msg != null){
                                System.out.println(msg);
                                break;
                            }else{
                                e1= e1.getCause();
                            }
                        }
                    }
                }
                return msg;
            }catch (Exception e){
                return msg;
            }
        }
    }
    
    /**
     * Safe long to int conversion.
     * 
     * @param l - long
     * @return int
     */
	public static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

    public static boolean createFileIfNotExist(String className, String sourceCode,File sourceDir) throws Exception{
        boolean isCreated = false;
        sourceDir.mkdirs();
        String classNamePack = className.replace('.', File.separatorChar);
        String srcFilePath = sourceDir + "" + File.separatorChar + classNamePack + ".java";
        File sourceFile =  new File(srcFilePath);
        if(!sourceFile.exists()){
            isCreated = true;
            FileUtils.writeStringToFile(new File(srcFilePath), sourceCode);
        }
        return isCreated;

    }
    public static Object getClassInstance(String className,String implementedClassName, Class<?> implementedClass, String sourceCode) throws Exception {
        DynamicClassLoader dynacode = DynamicClassLoader.getInstance();
        Class<?> dynaClass = dynacode.getLoadedClass(className);
        File sourceDir = new File(System.getProperty("java.io.tmpdir"),"DataCruncher/src");
        boolean createFileInTemp = false;
        if (dynaClass == null) {
            createFileInTemp = true;
        }else{
            boolean isExist = false;
            @SuppressWarnings("rawtypes")
			Class[] interfaces = dynaClass.getInterfaces();
            if(ArrayUtils.isNotEmpty(interfaces)){
                for (Class<?> clz : interfaces) {
                    if ((clz.getName().equalsIgnoreCase(implementedClassName))) {
                        isExist = true;
                    }
                }
            }
            if(!isExist){
                createFileInTemp = true;
            }
        }
        if(createFileInTemp){
            boolean isCreated = CommonUtils.createFileIfNotExist(className, sourceCode, sourceDir);
            if(isCreated || dynaClass == null){
                dynacode.addSourceDir(sourceDir);
                return dynacode.newProxyInstance(implementedClass,className);
            }
        }
        return dynaClass.newInstance();

    }

    /**
     * <p>Checks if the field isn't null and length of the field is greater
     * than zero not including whitespace.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if blank or null.
     */
    public static boolean isBlankOrNull(String value) {
        return ((value == null) || (value.trim().length() == 0));
    }

    /*
        check if the substring is present in a list of strings
     */
    public static boolean isSubStrPresentInList(List<?> elemChecked, String sub){
        int i=0;
        boolean found=false;
        String str;
        while (!found && i<elemChecked.size()) {
            str= (String) elemChecked.get(i);
            found=str.startsWith(sub);
            i++;
        }
        return found;
    }

    /**
     * Method took the XPATH set ,XML and returns the List of value for all matching element of Jaxb object.
     * There's also a method with the same signature except Set -> String
     *
     * @param xml
     * @param xpathXpressionsSet
     * @param jaxbObject
     * @return
     * @throws Exception
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static Map<String, String> parseXMLandInvokeDoSomething(ByteArrayInputStream xml, Set<String> xpathXpressionsSet,
                                                                   Object jaxbObject) throws Exception, SAXException, IOException, XPathExpressionException {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> itr = xpathXpressionsSet.iterator();
        while (itr.hasNext()) {
            String strXPath = itr.next();
            List<String> xPath = breakXPathInList(strXPath);
            if (jaxbObject == null)
                return map;
            if (jaxbObject.getClass().isAnnotationPresent(XmlRootElement.class)) {
                XmlRootElement rootAnnotation = jaxbObject.getClass().getAnnotation(XmlRootElement.class);
                if (rootAnnotation.name().equals(xPath.get(0))) {
                    recursiveIterateObjectWithXPath(jaxbObject, strXPath, xPath, 1, map);
                }
            } else {
                return map;
            }
        }
        return map;
    }

    /**
     * This method is the same as previous one, but operates only with one set entity (see xpathXpressionsSet).
     *
     * @param xml
     * @param xpathXpression
     * @param jaxbObject
     * @return
     * @throws Exception
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static String parseXMLandInvokeDoSomething(ByteArrayInputStream xml, String xpathXpression, Object jaxbObject)
            throws Exception, SAXException, IOException, XPathExpressionException {
        Map<String, String> map = new HashMap<String, String>();
        List<String> xPath = breakXPathInList(xpathXpression);
        if (jaxbObject == null)
            return null;
        if (jaxbObject.getClass().isAnnotationPresent(XmlRootElement.class)) {
            XmlRootElement rootAnnotation = jaxbObject.getClass().getAnnotation(XmlRootElement.class);
            if (rootAnnotation.name().equals(xPath.get(0))) {
                recursiveIterateObjectWithXPath(jaxbObject, xpathXpression, xPath, 1, map);
            }
        }
        //below map values to arrayList casting
        return map.size() == 1 ? (new ArrayList<String>(map.values())).get(0) : null;
    }


    /**
     * Break XPATH string into the String ArrayList
     * @param arg
     * @return
     */
    private static List<String> breakXPathInList(String arg){
        ArrayList<String> arrayList = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(arg, "/");
        while(st.hasMoreTokens())
            arrayList.add(st.nextToken());
        return arrayList;
    }


    /**
     * Method recursively iterate all fields
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private static void recursiveIterateObjectWithXPath(Object obj, String strXPath, List<String> xPath, int location,
                                                        Map<String, String> map) throws IllegalArgumentException, IllegalAccessException {
        if (obj == null)
            return;
        if (location > xPath.size())
            return;
        Field fields[] = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String xmlElement = null;
            String xmlElementRef = null;
            String xmlAttribute = null;
            if (f.isAnnotationPresent(XmlElement.class)) {
                xmlElement = f.getAnnotation(XmlElement.class).name();
            } else if (f.isAnnotationPresent(XmlElementRef.class)) {
                xmlAttribute = f.getAnnotation(XmlElementRef.class).name();
            } else if (f.isAnnotationPresent(XmlAttribute.class)) {
                xmlAttribute = f.getAnnotation(XmlAttribute.class).name();
            }

            if (xPath.get(location).equals(xmlElement) || xPath.get(location).equals(f.getName())
                    || xPath.get(location).equals(xmlElementRef)
                    || xPath.get(location).equals(xmlAttribute)) {
                Object fieldObject = f.get(obj);
                if (location + 1 == xPath.size()) {
                    if (fieldObject instanceof JAXBElement) {
                        map.put(strXPath, "" + ((JAXBElement<?>) fieldObject).getValue());
                    } else {
                        if(fieldObject != null)
                            map.put(strXPath, fieldObject.toString());
                    }
                    return;
                } else {
                    if (f.get(obj) instanceof List) {
                        for (Object o : (List<?>) f.get(obj)) {
                            recursiveIterateObjectWithXPath(o, strXPath, xPath, location + 1, map);
                        }
                    } else {
                        recursiveIterateObjectWithXPath(f.get(obj), strXPath, xPath, location + 1, map);
                    }
                }
            }
        }
    }
    /**
     * Download Stream Url .
     *
     * @return
     * @throws Exception
     */
    public static String getDownloadStreamUrl()  {
        String jsp= "downloadstream.json?id_datastream=";
        ApplicationConfigEntity applicationConfigEntity = applicationConfigDao.findByConfigType(ApplicationConfigType.APPLURL);
        String outStr = "";
        if(applicationConfigEntity!= null)
            outStr = applicationConfigEntity.getHost() +  jsp;
        return outStr;
    }
    
	/**
	 * Gets string array as a comma separated string.
	 * 
	 * @param arr
	 * @return
	 */
	public static String stringAsCommaSeparated(String[] arr) {
		String res = "";
		for (int i = 0; i < arr.length; i++) {
			res += arr[i] + ", ";
		}
		return res.endsWith(", ") ? res.substring(0, res.length() - 2) : res;
	}
}
