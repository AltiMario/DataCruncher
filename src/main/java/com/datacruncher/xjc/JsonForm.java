package com.datacruncher.xjc;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonForm {

    private JsonNode schema;
    private JsonNode form;

    public JsonNode getSchema() {
        return schema;
    }

    public void setSchema(JsonNode schema) {
        this.schema = schema;
    }

    public JsonNode getForm() {
        return form;
    }

    public void setForm(JsonNode form) {
        this.form = form;
    }
}
