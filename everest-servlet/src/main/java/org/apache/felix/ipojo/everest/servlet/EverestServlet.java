package org.apache.felix.ipojo.everest.servlet;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The Everest servlet.
 */
@Component(immediate = true)
@Instantiate
public class EverestServlet extends HttpServlet {


    public static final String EVEREST_SERVLET_PATH = "/everest";

    @Requires
    private EverestService everest;

    @Bind
    public void bindHttp(HttpService service) throws ServletException, NamespaceException {
         service.registerServlet(EVEREST_SERVLET_PATH, this, null, null);
    }

    @Unbind
    public void unbindHttp(HttpService service) throws ServletException, NamespaceException {
        service.unregister(EVEREST_SERVLET_PATH);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Everest servlet called");

        Writer stream = resp.getWriter();

        // Translate request
        DefaultRequest request = translate(req);
        if (request == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            resp.setContentType("application/json");
            stream.write("{error:\"The HTTP request cannot be translate to Everest\"}");
            stream.close();
            resp.flushBuffer();
            return;
        }

        try {
            Resource resource = everest.process(request);
            toJSON(resource, stream); // This writes the json answer in the stream.
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            stream.close();
            resp.flushBuffer();
        } catch (IllegalActionOnResourceException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            stream.write("{\"error\":\"Illegal action on resource\", \"path\":\"" + request.path() + "\", " +
                    "\"action\":\"" + request.action() + "\", \"message\":\"" + e.getMessage() + "\"}");
            stream.close();
            resp.flushBuffer();
        } catch (ResourceNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json");
            stream.write("{\"error\":\"Illegal action on resource\", \"path\":\"" + request.path() + "\", " +
                    "\"action\":\"" + request.action() + "\", \"message\":\"" + e.getMessage() + "\"}");
            stream.close();
            resp.flushBuffer();
        }
    }

    private void toJSON(Resource resource, Writer writer) throws IOException {
        JsonFactory factory = new JsonFactory();
        // configure, if necessary:
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        JsonGenerator generator = factory.createGenerator(writer);

        // Main object wrapper.
        generator.writeStartObject();

        // Metadata
        for (Map.Entry<String, Object> entry : resource.getMetadata().entrySet()) {
            toJSON(generator, entry.getKey(), entry.getValue());
        }

        // Link

        // End of main object
        generator.writeEndObject();

        generator.close();
    }

    private void toJSON(JsonGenerator generator, String fieldName, Object value) throws IOException {
        if (fieldName != null) {
            generator.writeFieldName(fieldName);
        }
        if (value == null) {
            generator.writeNull();
        } else if (value instanceof String) {
            generator.writeString(value.toString());
        } else if (value instanceof Integer) {
            generator.writeNumber((Integer) value);
        } else if (value instanceof Double) {
            generator.writeNumber((Double) value);
        } else if (value instanceof Boolean) {
            generator.writeBoolean((Boolean) value);
        } else if (value instanceof Collection) {
            generator.writeStartArray();
            for (Object o : ((Collection) value)) {
                toJSON(generator, null, o);
            }
            generator.writeEndArray();
        } else if (value instanceof Map) {
            generator.writeStartObject();
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) value).entrySet()) {
                generator.writeFieldName(entry.getKey().toString());
                toJSON(generator, null, entry.getValue());
            }
            generator.writeEndObject();
        }
    }

    private DefaultRequest translate(HttpServletRequest request) {
        System.out.println("Path info : " + request.getPathInfo());
        Path path = Path.from(request.getPathInfo());
        Action action;
        if ("GET".equals(request.getMethod())) {
            action = Action.GET;
        } else if ("PUT".equals(request.getMethod())) {
            action = Action.PUT;
        } else if ("POST".equals(request.getMethod())) {
            action = Action.POST;
        } else if ("DELETE".equals(request.getMethod())) {
            action = Action.DELETE;
        } else {
            return null; // Unsupported request.
        }
        return new DefaultRequest(action, path, request.getParameterMap()); // TODO Detect JSON.
    }
}
