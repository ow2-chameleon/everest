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

package org.ow2.chameleon.everest.internals;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Utility method handling bean construction from Maps and JSON Strings
 */
public class JSONUtils {

    private static ObjectMapper MAPPER = null;

    static {
        // Check whether we can load ObjectMapper or not
        try {
            JSONUtils.class.getClassLoader().loadClass(ObjectMapper.class.getName());
            MAPPER = new ObjectMapper();
        } catch (ClassNotFoundException e) {
            // Serialization disabled.
        }
    }

    /**
     * Tests if the String possibly represents a valid JSON String.<br>
     * Valid JSON strings are:
     * <ul>
     * <li>"null"</li>
     * <li>starts with "[" and ends with "]"</li>
     * <li>starts with "{" and ends with "}"</li>
     * </ul>
     */
    public static boolean mayBeJSON(String string) {
        return string != null
                && ("null".equals(string)
                || (string.startsWith("[") && string.endsWith("]")) || (string.startsWith("{") && string.endsWith("}")));
    }

    public static <T> T instantiate(String json, Class<T> clazz) {
        if (MAPPER == null || !mayBeJSON(json)) {
            return null;
        } else {
            try {
                return MAPPER.readValue(json, clazz);
            } catch (IOException e) {
                System.err.println("Cannot build bean from json string : " + e.getMessage());
                return null;
            }
        }
    }


}
