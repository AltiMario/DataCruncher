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
package com.datacruncher.utils.schema;

import com.datacruncher.constants.FileInfo;
import com.datacruncher.junit.ResourceFile;
import com.datacruncher.utils.JavaCompilerFacade;
import com.datacruncher.xjc.JsonForm;
import com.datacruncher.xjc.ValidationAnnotationXjcPlugin;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugins;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

/**
 * @see https://jsonform.github.io/jsonform/playground/index.html
 */
public class JsonSchemaGenerationTest {

    private static final String JAXB_NS_PREFIX = "jaxb";
    private static final String JAXB_NS = "http://java.sun.com/xml/ns/jaxb";
    private long timestamp;
    private File targetDirectory;

    @Rule
    public ResourceFile schemaResourceFile = new ResourceFile("/datafiles/5.formValidationTest.xsd", System.getProperty("java.io.tmpdir"));

    @Before
    public void setUp() {
        if (FileInfo.TESTS_WORKING_PATH == null) {
            FileInfo.TESTS_WORKING_PATH = System.getProperty("user.dir") + "/tests";
        }
        timestamp = System.currentTimeMillis();
        targetDirectory = Paths.get(FileInfo.TESTS_WORKING_PATH,
                String.format("%s_%d", getClass().getSimpleName(), timestamp)).toFile();
        targetDirectory.mkdirs();
    }

    @After
    public void tearDown() throws Exception {
        if (targetDirectory.exists()) {
            FileUtils.deleteDirectory(targetDirectory);
        }
    }

    @Test
    public void testGenerate() throws Exception {
        patchSchema();
        // Generate JAXB entities from XSD with XJC
        final String packageName = String.format("%s.gen%d", FileInfo.GENERATED_PACKAGE, timestamp);
        final XsdSchemaSourceGenerator schemaSourceGenerator = new XsdSchemaSourceGenerator(new JaxbGenerationResults());
        schemaSourceGenerator.generateSourceFromSchema(
                targetDirectory.getPath(),
                packageName,
                schemaResourceFile.getFile().getPath(),
                new String[]{
                        "-extension",
                        ValidationAnnotationXjcPlugin.PLUGIN_OPTION,
                        "-" + JaxbValidationsPlugins.PLUGIN_OPTION_NAME,
                        "-" + JaxbValidationsPlugins.VERBOSE + "=false",
                });
        // Compile JAXB sources
        new JavaCompilerFacade()
                .useSource(targetDirectory)
                .useTarget(targetDirectory)
                .compile();
        ClassLoader loader = new URLClassLoader(new URL[]{targetDirectory.toURI().toURL()});
        List<String> classFiles = FileUtils
                .listFiles(targetDirectory, new String[]{"class"}, true)
                .stream()
                .map(f -> f.getPath())
                .collect(Collectors.toList());
        Class<?> rootClass = null;
        final Iterator<String> classFileIterator = classFiles.iterator();
        while (rootClass == null && classFileIterator.hasNext()) {
            String classCanonicalName = String.format("%s.%s",
                    packageName, FilenameUtils.getBaseName(classFileIterator.next()));
            Class<?> jaxbClass = loader.loadClass(classCanonicalName);
            if (jaxbClass.isAnnotationPresent(XmlRootElement.class)) {
                rootClass = jaxbClass;
            }
        }
        if (rootClass == null) {
            fail("Can't find root element class");
        }
        // Generate JSON schema
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
        JsonNode jsonSchema = schemaGenerator.generateJsonSchema(rootClass);
        String jsonSchemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
        // Create JSON Form descriptor
        JsonForm jsonForm = new JsonForm();
        final JsonNode properties = jsonSchema.get("properties");
        final List<String> requiredFields = jsonSchema.has("required")
                ? StreamSupport.stream(jsonSchema.get("required").spliterator(), false).map(a -> a.asText()).collect(Collectors.toList())
                : Collections.EMPTY_LIST;
        jsonForm.setSchema(properties);
        final ArrayNode formNode = mapper.createArrayNode();
        final Iterator<Map.Entry<String, JsonNode>> propertyIterator = jsonSchema.get("properties").fields();
        while (propertyIterator.hasNext()) {
            final ObjectNode fieldNode = mapper.createObjectNode();
            final Map.Entry<String, JsonNode> propertyEntry = propertyIterator.next();
            final ObjectNode property = (ObjectNode) propertyEntry.getValue();
            String propertyName = propertyEntry.getKey();
            if (requiredFields.contains(propertyName)) {
                property.put("required", true);
            }
            fieldNode.put("key", propertyName);
            formNode.add(fieldNode);
        }
        if (formNode.size() > 0) {
            final ObjectNode submitNode = mapper.createObjectNode();
            submitNode.put("type", "submit");
            submitNode.put("title", "Submit");
            formNode.add(submitNode);
        }
        jsonForm.setForm(formNode);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        File resultFile = Paths.get(FileInfo.TESTS_WORKING_PATH,
                String.format("%s-%d.json", FilenameUtils.getBaseName(schemaResourceFile.getFile().getPath()), timestamp)).toFile();
        FileUtils.writeStringToFile(resultFile, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonForm));

        assertFalse(jsonSchemaString.isEmpty());
        assertNotNull(properties);
        final JsonNode nameProperty = properties.get("name");
        assertNotNull(nameProperty);
        assertEquals(1, nameProperty.get("minLength").asInt());
        assertEquals(25, nameProperty.get("maxLength").asInt());
        assertEquals("[a-zA-Z0-9\\s.\\-]+", nameProperty.get("pattern").asText());
        final JsonNode ipProperty = properties.get("ip");
        assertNotNull(ipProperty);
        final JsonNode ipAnnotations = ipProperty.get("annotation");
        assertNotNull(ipAnnotations);
        assertTrue(ipAnnotations.isArray());
        String[] annotations = StreamSupport.stream(ipAnnotations.spliterator(), false)
                .map(a -> a.asText()).toArray(String[]::new);
        assertEquals(2, annotations.length);
        assertTrue(ArrayUtils.contains(annotations, "@inetaddress"));
        assertTrue(ArrayUtils.contains(annotations, "@domain"));
        assertNotNull(nameProperty);
        final JsonNode portProperty = properties.get("port");
        assertNotNull(portProperty);
        final JsonNode portAnnotations = portProperty.get("annotation");
        assertNotNull(portAnnotations);
        assertTrue(portAnnotations.isArray());
        annotations = StreamSupport.stream(portAnnotations.spliterator(), false)
                .map(a -> a.asText()).toArray(String[]::new);
        assertEquals(1, annotations.length);
        assertTrue(ArrayUtils.contains(annotations, "@serverport"));
    }

    /**
     * XJC plugin can't handle this appinfo:
     * <pre>
     *     &lt;xs:appinfo&gt;&#64;serverport&lt;/xs:appinfo&gt;
     * </pre>
     * <p>
     * So we need to patch it into this:
     * <pre>
     *     &lt;xs:appinfo&gt;&lt;dc:annotation&gt;&#64;inetaddress&lt;/dc:annotation&gt;&lt;/xs:appinfo&gt;
     * </pre>
     * Plus we add extensionBindingPrefixes to allow our plugin to handle these customizations
     *
     * @throws Exception
     */
    private void patchSchema() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(schemaResourceFile.getFile())) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);
            xmlDocument.getDocumentElement().setAttributeNS(
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                    XMLConstants.XMLNS_ATTRIBUTE + ":" + ValidationAnnotationXjcPlugin.DC_NS_PREFIX,
                    ValidationAnnotationXjcPlugin.DC_NS_URI);
            xmlDocument.getDocumentElement().setAttributeNS(
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE + ":" + JAXB_NS_PREFIX, JAXB_NS);
            xmlDocument.getDocumentElement().setAttributeNS(JAXB_NS, JAXB_NS_PREFIX + ":extensionBindingPrefixes", ValidationAnnotationXjcPlugin.DC_NS_PREFIX);
            NodeList nodeList = xmlDocument.getElementsByTagName("xs:appinfo");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final Element newNode = xmlDocument.createElementNS(ValidationAnnotationXjcPlugin.DC_NS_URI,
                        ValidationAnnotationXjcPlugin.DC_NS_PREFIX + ":" + ValidationAnnotationXjcPlugin.DC_ANNO_TAG);
                newNode.setTextContent(node.getTextContent());
                node.removeChild(node.getFirstChild());
                node.appendChild(newNode);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xmlDocument);
            try (FileOutputStream outputStream = new FileOutputStream(schemaResourceFile.getFile())) {
                StreamResult result = new StreamResult(outputStream);
                transformer.transform(source, result);
            }
        }
    }
}

