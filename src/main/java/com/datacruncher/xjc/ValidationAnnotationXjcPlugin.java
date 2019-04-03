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
package com.datacruncher.xjc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.impl.ElementDecl;
import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;

import java.util.*;

public class ValidationAnnotationXjcPlugin extends Plugin {
    private static final String PLUGIN_OPTION_NAME = "XValidationAnnotationXjcPlugin";
    public static final String PLUGIN_OPTION = "-" + PLUGIN_OPTION_NAME;
    public static final String DC_NS_URI = "https://github.com/AltiMario/DataCruncher";
    public static final String DC_NS_PREFIX = "dc";
    public static final String DC_ANNO_TAG = "annotation";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Map<String, String> orphanProperties;

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) {
        try {
            outline.getClasses().forEach(
                    (ClassOutline classOutline) -> {
                        CClassInfo classInfo = classOutline.target;
                        List<CPropertyInfo> properties = classInfo.getProperties();
                        classOutline.getImplClass().fields().forEach((String name, JFieldVar field) -> {
                            CPropertyInfo property = classInfo.getProperties().stream()
                                    .filter(it -> it.getName(false).equals(field.name()))
                                    .findAny()
                                    .orElseThrow(() -> new IllegalStateException("Can't find property [" + field.name() + "] in class [" + classInfo.getTypeName() + "]"));
                            ArrayNode annotations = objectMapper.createArrayNode();
                            for (CPluginCustomization customization : property.getCustomizations()) {
                                if (DC_ANNO_TAG.equals(customization.element.getLocalName())) {
                                    annotations.add(customization.element.getTextContent());
                                    customization.markAsAcknowledged();
                                }
                            }
                            if (annotations.size() > 0) {
                                JAnnotationUse annotation = field.annotate(JsonSchemaInject.class);
                                try {
                                    ObjectNode node = objectMapper.createObjectNode();
                                    node.set(DC_ANNO_TAG, annotations);
                                    annotation.param("json", objectMapper.writeValueAsString(node));
                                } catch (JsonProcessingException e) {
                                    logger.error(e);
                                }
                            }
                        });
//                        JAnnotationUse classAnnotation = classOutline.getImplClass().annotate(JsonSchemaInject.class);
//                        final JAnnotationArrayMember annotationStrings = classAnnotation.paramArray("strings");
//                        for (Map.Entry<String, String> orphanProperty : orphanProperties.entrySet()) {
//                            JAnnotationUse annotationUse = annotationStrings.annotate(JsonSchemaString.class);
//                            annotationUse.param("path", orphanProperty.getKey());
//                            annotationUse.param("value", orphanProperty.getValue());
//                        }
                    });
            return true;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        super.postProcessModel(model, errorHandler);
        orphanProperties = new TreeMap<>();
        model.getElementMappings(null).values().forEach(e -> {
            if (e.getProperty().getSchemaComponent() instanceof ElementDecl) {
                orphanProperties.put(e.getElementName().getLocalPart(),
                        ((ElementDecl) e.getProperty().getSchemaComponent()).getFixedValue().toString());
            }
        });
    }

    @Override
    public String getOptionName() {
        return PLUGIN_OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) {
        return 1;
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(DC_NS_URI);
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return DC_NS_URI.equals(nsUri) && DC_ANNO_TAG.equals(localName);
    }
}
