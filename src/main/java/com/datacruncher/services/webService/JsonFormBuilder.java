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
package com.datacruncher.services.webService;

import com.datacruncher.utils.schema.JsonSchemaGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @see https://jsonform.github.io/jsonform/playground/index.html
 */
public class JsonFormBuilder {

    public JsonForm build(File xsdSchemaFile, File workingDirectory) throws Exception {
        final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(xsdSchemaFile);
        final JsonNode jsonSchema = jsonSchemaGenerator.generate(workingDirectory);
        JsonForm jsonForm = new JsonForm();
        final JsonNode properties = jsonSchema.get("properties");
        jsonForm.setSchema(properties);
        final List<String> requiredFields = jsonSchema.has("required")
                ? StreamSupport.stream(jsonSchema.get("required").spliterator(), false).map(a -> a.asText()).collect(Collectors.toList())
                : Collections.EMPTY_LIST;
        final ObjectMapper objectMapper = new ObjectMapper();
        final ArrayNode formNode = objectMapper.createArrayNode();
        final Iterator<Map.Entry<String, JsonNode>> propertyIterator = jsonSchema.get("properties").fields();
        while (propertyIterator.hasNext()) {
            final ObjectNode fieldNode = objectMapper.createObjectNode();
            final Map.Entry<String, JsonNode> propertyEntry = propertyIterator.next();
            final ObjectNode property = (ObjectNode) propertyEntry.getValue();
            String propertyName = propertyEntry.getKey();
            // TODO Remove this when JSON Form will support JSON schema draft 04 or newer
            if (requiredFields.contains(propertyName)) {
                property.put("required", true);
            }
            fieldNode.put("key", propertyName);
            formNode.add(fieldNode);
        }
        if (formNode.size() > 0) {
            final ObjectNode submitNode = objectMapper.createObjectNode();
            submitNode.put("type", "submit");
            submitNode.put("title", "Submit");
            formNode.add(submitNode);
        }
        jsonForm.setForm(formNode);
        return jsonForm;
    }
}
