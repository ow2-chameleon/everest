package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.IPOJO_ROOT_PATH;

/**
 * Resource representation for all iPOJO handlers.
 */
public class IpojoHandlerRootResource extends DefaultReadOnlyResource {

    /**
     * The path of the iPOJO handler resource manager.
     */
    public static final Path IPOJO_HANDLER_ROOT_PATH = IPOJO_ROOT_PATH.add(Path.from("/handler"));

    /**
     * The iPOJO handler factories.
     */
    private final Map<String, HandlerFactory> m_handlers = new HashMap<String, HandlerFactory>();

    public IpojoHandlerRootResource() {
        super(IPOJO_HANDLER_ROOT_PATH);
    }

    /**
     * Add an iPOJO handler.
     * @param handler the arriving iPOJO handler
     */
    void addHandler(HandlerFactory handler) {
        synchronized (m_handlers) {
            String k = handler.getNamespace() + ':' + handler.getHandlerName();
            m_handlers.put(k, handler);
        }
    }

    /**
     * Remove an iPOJO handler.
     * @param handler the leaving iPOJO handler
     */
    void removeHandler(HandlerFactory handler) {
        synchronized (m_handlers) {
            String k = handler.getNamespace() + ':' + handler.getHandlerName();
            m_handlers.remove(k);
        }
    }

}
