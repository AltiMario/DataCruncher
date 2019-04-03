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
import java.io.IOException;
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
    private final ObjectMapper objectMapper;
    private JsonNode jsonSchema;

    public JsonFormBuilder() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonForm build(File xsdSchemaFile, File workingDirectory) throws Exception {
        final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(xsdSchemaFile);
        jsonSchema = jsonSchemaGenerator.generate(workingDirectory);
        return createJsonForm();
    }

    /**
     * Assumes that JSON schema was set by {@link #setJsonSchema(JsonNode)} or {@link #setJsonSchema(String)}
     *
     * @return
     * @throws Exception
     * @see com.datacruncher.utils.schema.JsonSchemaGenerator
     */
    public JsonForm build() throws Exception {
        return createJsonForm();
    }

    private JsonForm createJsonForm() {
        JsonForm jsonForm = new JsonForm();
        final JsonNode properties = jsonSchema.get("properties");
        jsonForm.setSchema(properties);
        final List<String> requiredFields = jsonSchema.has("required")
                ? StreamSupport.stream(jsonSchema.get("required").spliterator(), false).map(a -> a.asText()).collect(Collectors.toList())
                : Collections.EMPTY_LIST;
        final ArrayNode formNode = objectMapper.createArrayNode();
        final Iterator<Map.Entry<String, JsonNode>> propertyIterator = jsonSchema.get("properties").fields();
        while (propertyIterator.hasNext()) {
            final ObjectNode fieldNode = objectMapper.createObjectNode();
            final Map.Entry<String, JsonNode> propertyEntry = propertyIterator.next();
            fieldNode.put("key", propertyEntry.getKey());
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

    public JsonNode getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(JsonNode jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public void setJsonSchema(String jsonSchemaContent) throws IOException {
        this.jsonSchema = objectMapper.readTree(jsonSchemaContent);
    }
}
