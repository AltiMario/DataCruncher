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
import com.datacruncher.utils.JavaCompilerFacade;
import com.datacruncher.xjc.ValidationAnnotationXjcPlugin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugins;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JsonSchemaGenerator {
    private static final String JAXB_NS_PREFIX = "jaxb";
    private static final String JAXB_NS = "http://java.sun.com/xml/ns/jaxb";
    private final File xsdSchemaFile;

    public JsonSchemaGenerator(File xsdSchemaFile) {
        this.xsdSchemaFile = xsdSchemaFile;
    }

    public JsonNode generate(File workingDirectory) throws Exception {
        patchXsdSchema();
        compileXsdSchema(workingDirectory);
        JsonNode jsonSchema = generateJsonSchema(workingDirectory);
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
    private void patchXsdSchema() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(xsdSchemaFile)) {
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
        final ObjectMapper objectMapper = new ObjectMapper();
        com.kjetland.jackson.jsonSchema.JsonSchemaGenerator schemaGenerator =
                new com.kjetland.jackson.jsonSchema.JsonSchemaGenerator(objectMapper);
        return schemaGenerator.generateJsonSchema(rootClass);
    }
}
