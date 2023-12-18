package com.mexx.comfy.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.vertx.core.json.JsonObject;
import java.io.IOException;

class JsonObjectSerializer extends JsonSerializer<JsonObject> {
    JsonObjectSerializer() {
    }

    public void serialize(JsonObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObject(value.getMap());
    }
}
