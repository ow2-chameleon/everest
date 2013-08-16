/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.servlet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Json utilities.
 */
public class JsonUtils {

    public static String CONTENT_TYPE = "application/json";

    private static Json default_singleton = new Json();
    private static Json request_json;

    public  static Json get() {
        return default_singleton;
    }

    public  static Json get(HttpServletRequest request) {
        // Once we have the request-based mapper, we keep it.
        if (request_json == null) {
            SimpleModule everest = new SimpleModule("Everest");
            everest.addSerializer(new PathSerializer(request, EverestServlet.EVEREST_SERVLET_PATH));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(everest);
            request_json =  new Json(mapper);
        }

        return request_json;
    }


    public static  class Json {
        private final ObjectMapper mapper;

        public Json() {
            this(new ObjectMapper());
        }

        public Json(ObjectMapper mapper) {
            this.mapper = mapper;
            this.mapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer("null"));
        }

        public ObjectMapper getMapper() {
            return mapper;
        }

        /**
         * Convert a JsonNode to its string representation.
         */
        public String stringify(JsonNode json) {
            return json.toString();
        }

        /**
         * Parse a String representing a json, and return it as a JsonNode.
         */
        public JsonNode parse(String src) {
            try {
                return getMapper().readValue(src, JsonNode.class);
            } catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }

        /**
         * Convert an object to JsonNode.
         *
         * @param data Value to convert in Json.
         */
        public JsonNode toJson(final Object data) {
            try {
                return getMapper().valueToTree(data);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Converts a JsonNode to a Java value
         *
         * @param json Json value to convert.
         * @param clazz Expected Java value type.
         */
        public <A> A fromJson(JsonNode json, Class<A> clazz) {
            try {
                return getMapper().treeToValue(json, clazz);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Creates a new empty ObjectNode.
         */
        public ObjectNode newObject() {
            return getMapper().createObjectNode();
        }
    }

    /**
     * JsonSerializer that translates {@code null} keys in Java map by a customizable field name.
     */
    private static class NullKeySerializer extends JsonSerializer<Object> {

        private final String nullFieldName;

        private NullKeySerializer(String nullFieldName) {
            this.nullFieldName = nullFieldName;
        }

        @Override
        public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) throws IOException {
            jsonGenerator.writeFieldName(nullFieldName);
        }
    }

}
