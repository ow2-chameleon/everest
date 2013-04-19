package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * '/ipojo/handler' resource.
 */
public class HandlersResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("handler");

    private final Map<String, HandlerNamespaceResource> m_handlers = new LinkedHashMap<String, HandlerNamespaceResource>();

    public HandlersResource() {
        super(PATH);
    }

    public void addHandler(HandlerFactory handler) {
        HandlerNamespaceResource r;
        synchronized (m_handlers) {
            String ns = handler.getNamespace();
            r = m_handlers.get(ns);
            if (r == null) {
                r = new HandlerNamespaceResource(ns);
                m_handlers.put(ns, r);
            }
        }
        r.addHandlerName(handler);
    }

    public void removeHandler(HandlerFactory handler) {
        synchronized (m_handlers) {
            String ns = handler.getNamespace();
            HandlerNamespaceResource r = m_handlers.get(ns);
            if (r.removeHandlerName(handler)) {
                m_handlers.remove(ns);
            }
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_handlers) {
            return new ArrayList<Resource>(m_handlers.values());
        }
    }

}
