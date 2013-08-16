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

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A set of HTTP utility methods
 */
public class HttpUtils {
    
    public static int OK = HttpServletResponse.SC_OK;
    public static int CREATED = HttpServletResponse.SC_CREATED;
    public static int NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
    public static int NOT_ALLOWED = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
    public static int NOT_IMPLEMENTED = HttpServletResponse.SC_NOT_IMPLEMENTED;
    
    

    public static boolean isHead(HttpServletRequest req) {
        return "HEAD".equals(req.getMethod());
    }

    public static boolean isGet(HttpServletRequest req) {
        return "GET".equals(req.getMethod());
    }

    public static boolean isPost(HttpServletRequest req) {
        return "POST".equals(req.getMethod());
    }

    public static boolean isDelete(HttpServletRequest req) {
        return "DELETE".equals(req.getMethod());
    }

    public static boolean isPut(HttpServletRequest req) {
        return "PUT".equals(req.getMethod());
    }

    public static HttpResult ok() {
        return new HttpResult().code(OK);
    }

    public static HttpResult ok(String content) {
        return new HttpResult().code(OK).body(content);
    }

    public static HttpResult ok(JsonNode json) {
        return new HttpResult()
                .code(OK)
                .type(JsonUtils.CONTENT_TYPE)
                .body(JsonUtils.get().stringify(json));
    }

    public static HttpResult created() {
        return new HttpResult().code(CREATED);
    }

    public static HttpResult created(String content) {
        return new HttpResult().code(CREATED).body(content);
    }

    public static HttpResult created(JsonNode json) {
        return new HttpResult()
                .code(CREATED)
                .type(JsonUtils.CONTENT_TYPE)
                .body(JsonUtils.get().stringify(json));
    }

    public static HttpResult notfound(JsonNode json) {
        return new HttpResult()
                .code(NOT_FOUND)
                .type(JsonUtils.CONTENT_TYPE)
                .body(JsonUtils.get().stringify(json));
    }

    public static HttpResult notallowed(JsonNode json) {
        return new HttpResult()
                .code(NOT_ALLOWED)
                .type(JsonUtils.CONTENT_TYPE)
                .body(JsonUtils.get().stringify(json));
    }

    public static HttpResult notimplemented(JsonNode json) {
        return new HttpResult()
                .code(NOT_IMPLEMENTED)
                .type(JsonUtils.CONTENT_TYPE)
                .body(JsonUtils.get().stringify(json));
    }

    public static class HttpResult {
        private int code;
        private String body;
        private Map<String, String> headers;
        private String contentType;

        public HttpResult() {
            headers = new LinkedHashMap<String, String>();
        }

        public HttpResult type(String type)  {
            this.contentType = type;
            return this;
        }

        public HttpResult code(int code) {
            this.code = code;
            return this;
        }

        public HttpResult body(String body) {
            this.body = body;
            return this;
        }

        public HttpResult location(String location) {
            return header("Location", location);
        }

        public HttpResult header(String header, String value) {
            this.headers.put(header, value);
            return this;
        }

        public void wrap(HttpServletResponse response) throws IOException {
            response.setStatus(code);
            response.setContentType(contentType);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
            if (body != null) {
                PrintWriter writer = response.getWriter();
                writer.write(body);
                writer.close();
            }
            response.flushBuffer();
        }
    }
}
