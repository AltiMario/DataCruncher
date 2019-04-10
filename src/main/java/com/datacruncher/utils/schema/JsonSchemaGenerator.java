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

import com.datacruncher.constants.DateTimeType;
import com.datacruncher.constants.FileInfo;
import com.datacruncher.datastreams.JvDate;
import com.datacruncher.utils.JavaCompilerFacade;
import com.datacruncher.xjc.ValidationAnnotationXjcPlugin;
import com.datacruncher.xjc.XsdDate;
import com.datacruncher.xjc.XsdDateTime;
import com.datacruncher.xjc.XsdTime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugins;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import scala.Predef;
import scala.collection.JavaConverters;

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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonSchemaGenerator {
    private static final String JAXB_NS_PREFIX = "jaxb";
    private static final String JAXB_NS = "http://java.sun.com/xml/ns/jaxb";
    private static final String INPUTTYPE_DATE = "date";
    private static final String INPUTTYPE_TIME = "time";
    private static final String INPUTTYPE_DATETIME = "datetime-local";
    private static final String ANNOTATION_UNIXDATE = "@unixDate";
    private final File xsdSchemaFile;
    private final ObjectMapper objectMapper;

    public JsonSchemaGenerator(File xsdSchemaFile) {
        this.xsdSchemaFile = xsdSchemaFile;
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode generate(File workingDirectory) throws Exception {
        patchXsdSchema();
        compileXsdSchema(workingDirectory);
        JsonNode jsonSchema = generateJsonSchema(workingDirectory);
        patchJsonSchema(jsonSchema);
        return jsonSchema;
    }

    private void compileXsdSchema(File workingDirectory) throws Exception {
        final String packageName = String.format("%s.gen%d", FileInfo.GENERATED_PACKAGE, System.currentTimeMillis());
        final XsdSchemaSourceGenerator schemaSourceGenerator = new XsdSchemaSourceGenerator(new JaxbGenerationResults());
        schemaSourceGenerator.generateSourceFromSchema(
                workingDirectory.getPath(),
                packageName,
                xsdSchemaFile.getPath(),
                new String[]{
                        "-extension",
                        ValidationAnnotationXjcPlugin.PLUGIN_OPTION,
                        "-" + JaxbValidationsPlugins.PLUGIN_OPTION_NAME,
                        "-" + JaxbValidationsPlugins.VERBOSE + "=false",
                });
        new JavaCompilerFacade()
                .useSource(workingDirectory)
                .useTarget(workingDirectory)
                .compile();
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
    protected void patchXsdSchema() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(xsdSchemaFile)) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);
            final Element rootElement = xmlDocument.getDocumentElement();
            rootElement.setAttributeNS(
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                    XMLConstants.XMLNS_ATTRIBUTE + ":" + ValidationAnnotationXjcPlugin.DC_NS_PREFIX,
                    ValidationAnnotationXjcPlugin.DC_NS_URI);
            rootElement.setAttributeNS(
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE + ":" + JAXB_NS_PREFIX, JAXB_NS);
            rootElement.setAttributeNS(JAXB_NS, JAXB_NS_PREFIX + ":extensionBindingPrefixes", ValidationAnnotationXjcPlugin.DC_NS_PREFIX);
            rootElement.setAttributeNS(JAXB_NS, JAXB_NS_PREFIX + ":version", "2.1");
            final NodeList rootChildNodes = rootElement.getChildNodes();
            for (int i = 0; i < rootChildNodes.getLength(); i++) {
                final Node annoNode = rootChildNodes.item(i);
                if ("annotation".equals(annoNode.getLocalName())) {
                    //rootElement.removeChild(annoNode);
                    Node appInfoNode = null;
                    final NodeList annoChildNodes = annoNode.getChildNodes();
                    for (int j = 0; j < annoChildNodes.getLength(); j++) {
                        final Node annoChildNode = annoChildNodes.item(i);
                        if (annoChildNode == null) {
                            continue;
                        }
                        if ("appinfo".equalsIgnoreCase(annoChildNode.getLocalName())) {
                            appInfoNode = annoChildNode;
                            // appinfo for schema element is used to store macro, ignore it for JSON schema
                            if (appInfoNode.hasChildNodes()) {
                                appInfoNode.removeChild(appInfoNode.getFirstChild());
                            }
                        } else {
                            annoNode.removeChild(annoChildNode);
                        }
                    }
                    if (appInfoNode == null) {
                        appInfoNode = xmlDocument.createElementNS(
                                annoNode.getNamespaceURI(), annoNode.getPrefix() + ":appinfo");
                        annoNode.appendChild(appInfoNode);
                    }
                    final Element bindingsNode = xmlDocument.createElementNS(
                            JAXB_NS, JAXB_NS_PREFIX + ":globalBindings");
                    appInfoNode.appendChild(bindingsNode);
                    Element javaTypeNode = xmlDocument.createElementNS(
                            JAXB_NS, JAXB_NS_PREFIX + ":javaType");
                    javaTypeNode.setAttribute("name", XsdDate.class.getCanonicalName());
                    javaTypeNode.setAttribute("xmlType", "xs:date");
                    bindingsNode.appendChild(javaTypeNode);
                    javaTypeNode = xmlDocument.createElementNS(
                            JAXB_NS, JAXB_NS_PREFIX + ":javaType");
                    javaTypeNode.setAttribute("name", XsdTime.class.getCanonicalName());
                    javaTypeNode.setAttribute("xmlType", "xs:time");
                    bindingsNode.appendChild(javaTypeNode);
                    javaTypeNode = xmlDocument.createElementNS(
                            JAXB_NS, JAXB_NS_PREFIX + ":javaType");
                    javaTypeNode.setAttribute("name", XsdDateTime.class.getCanonicalName());
                    javaTypeNode.setAttribute("xmlType", "xs:dateTime");
                    bindingsNode.appendChild(javaTypeNode);

                }
            }
            NodeList nodeList = xmlDocument.getElementsByTagName("xs:appinfo");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                if (StringUtils.isBlank(node.getTextContent())) {
                    continue;
                }
                final Element newNode = xmlDocument.createElementNS(ValidationAnnotationXjcPlugin.DC_NS_URI,
                        ValidationAnnotationXjcPlugin.DC_NS_PREFIX + ":" + ValidationAnnotationXjcPlugin.DC_ANNO_TAG);
                newNode.setTextContent(node.getTextContent());
                node.removeChild(node.getFirstChild());
                node.appendChild(newNode);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(xmlDocument);
            try (FileOutputStream outputStream = new FileOutputStream(xsdSchemaFile)) {
                StreamResult result = new StreamResult(outputStream);
                transformer.transform(source, result);
            }
        }
    }

    private JsonNode generateJsonSchema(File workingDirectory) throws Exception {
        ClassLoader loader = new URLClassLoader(new URL[]{workingDirectory.toURI().toURL()},
                Thread.currentThread().getContextClassLoader());
        List<String> classFiles = FileUtils
                .listFiles(workingDirectory, new String[]{"class"}, true)
                .stream()
                .map(f -> f.getPath())
                .collect(Collectors.toList());
        Class<?> rootClass = null;
        final Iterator<String> classFileIterator = classFiles.iterator();
        while (rootClass == null && classFileIterator.hasNext()) {
            String classFilePath = classFileIterator.next();
            String classCanonicalName = StringUtils.removeStart(classFilePath, workingDirectory.getPath());
            classCanonicalName = StringUtils.removeStart(classCanonicalName, Character.toString(File.separatorChar));
            classCanonicalName = StringUtils.removeEnd(classCanonicalName, ".class");
            classCanonicalName = classCanonicalName.replace(File.separatorChar, '.');
            Class<?> jaxbClass = loader.loadClass(classCanonicalName);
            if (jaxbClass.isAnnotationPresent(XmlRootElement.class)) {
                rootClass = jaxbClass;
            }
        }
        if (rootClass == null) {
            throw new Exception("Can't find root element class");
        }
        scala.collection.immutable.Map<String, String> customType2FormatMapping = JavaConverters.mapAsScalaMapConverter(
                new HashMap<String, String>() {{
                    put("com.datacruncher.xjc.XsdDate", "date");
                    put("com.datacruncher.xjc.XsdTime", "time");
                    put("com.datacruncher.xjc.XsdDateTime", "datetime-local");
                }}
        ).asScala().toMap(
                Predef.$conforms()
        );
        final JsonSchemaConfig html5SchemaConfig = JsonSchemaConfig.vanillaJsonSchemaDraft4();
        final JsonSchemaConfig jsonSchemaConfig = new JsonSchemaConfig(
                html5SchemaConfig.autoGenerateTitleForProperties(),
                html5SchemaConfig.defaultArrayFormat(),
                html5SchemaConfig.useOneOfForOption(),
                html5SchemaConfig.useOneOfForNullables(),
                html5SchemaConfig.usePropertyOrdering(),
                html5SchemaConfig.hidePolymorphismTypeProperty(),
                html5SchemaConfig.disableWarnings(),
                html5SchemaConfig.useMinLengthForNotNull(),
                html5SchemaConfig.useTypeIdForDefinitionName(),
                customType2FormatMapping,
                html5SchemaConfig.useMultipleEditorSelectViaProperty(),
                html5SchemaConfig.uniqueItemClasses(),
                html5SchemaConfig.classTypeReMapping(),
                html5SchemaConfig.jsonSuppliers(),
                html5SchemaConfig.subclassesResolver(),
                html5SchemaConfig.failOnUnknownProperties());
        com.kjetland.jackson.jsonSchema.JsonSchemaGenerator schemaGenerator =
                new com.kjetland.jackson.jsonSchema.JsonSchemaGenerator(objectMapper, jsonSchemaConfig);
        return schemaGenerator.generateJsonSchema(rootClass);
    }

    private void patchJsonSchema(JsonNode jsonSchema) {
        final List<String> requiredFields = jsonSchema.has("required")
                ? StreamSupport.stream(jsonSchema.get("required").spliterator(), false).map(a -> a.asText()).collect(Collectors.toList())
                : Collections.EMPTY_LIST;
        final Iterator<Map.Entry<String, JsonNode>> propertyIterator = jsonSchema.get("properties").fields();
        while (propertyIterator.hasNext()) {
            final Map.Entry<String, JsonNode> propertyEntry = propertyIterator.next();
            final ObjectNode property = (ObjectNode) propertyEntry.getValue();
            String propertyName = propertyEntry.getKey();
            // TODO Remove patch for required property when JSON Form will support JSON schema draft 04 or newer
            if (requiredFields.contains(propertyName)) {
                property.put("required", true);
            }
            if (property.has("format")) {
                property.put("type", property.get("format").asText());
                property.remove("format");
            }
            if (property.has(ValidationAnnotationXjcPlugin.DC_ANNO_TAG)) {
                final JsonNode annotationNode = property.get(ValidationAnnotationXjcPlugin.DC_ANNO_TAG);
                if (annotationNode.isArray()) {
                    final ArrayNode annotationValuesNode = (ArrayNode) annotationNode;
                    if (annotationValuesNode.size() == 1) {
                        String annotationValue = annotationValuesNode.get(0).asText().trim();
                        // Force date type
                        if (annotationValue.startsWith(JvDate.RULE_PREFIX)) {
                            final JvDate jvDate = JvDate.parse(annotationValue);
                            String type = property.has("type") ? property.get("type").asText() : "";
                            switch (jvDate.getDateTimeType()) {
                                case DateTimeType.FORMAT_DATETIME:
                                    type = INPUTTYPE_DATETIME;
                                    break;
                                case DateTimeType.FORMAT_DATE:
                                    type = "date";
                                    break;
                                case DateTimeType.FORMAT_TIME:
                                    type = "time";
                                    break;
                            }
                            property.put("type", type);
                            property.remove(ValidationAnnotationXjcPlugin.DC_ANNO_TAG);
                        } else if (ANNOTATION_UNIXDATE.equalsIgnoreCase(annotationValue)) {
                            property.put("type", INPUTTYPE_DATETIME);
                            property.remove(ValidationAnnotationXjcPlugin.DC_ANNO_TAG);
                        }
                    }
                }
            }
            // Replacing client-side pattern validation by AJAX check
            if (property.has("pattern")) {
                if (property.has("type")
                        && !INPUTTYPE_DATETIME.equals(property.get("type").asText())
                        && !INPUTTYPE_DATE.equals(property.get("type").asText())
                        && !INPUTTYPE_TIME.equals(property.get("type").asText())) {
                    property.put("regex", property.get("pattern").asText());
                }
                property.remove("pattern");
            }
        }
    }
}
