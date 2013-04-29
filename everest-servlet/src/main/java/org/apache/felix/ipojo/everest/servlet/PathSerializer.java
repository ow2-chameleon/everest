package org.apache.felix.ipojo.everest.servlet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.felix.ipojo.everest.services.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
