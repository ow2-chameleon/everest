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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.ow2.chameleon.everest.services.Path;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A serializer of path.
 * It computes the url.
 */
public class PathSerializer extends JsonSerializer<Path> {

    private final String m_server;

    public PathSerializer(HttpServletRequest request, String servletPath) {
        if (request != null) {
            m_server = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
                servletPath;
        } else {
            // No request, just keep the path as it is.
            m_server = "";
        }
    }

    @Override
    public void serialize(Path path, JsonGenerator generator, SerializerProvider provider) throws
            IOException {
        generator.writeString(m_server + path.toString());
    }

    @Override
    public Class<Path> handledType() {
        return Path.class;
    }


}
