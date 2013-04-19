package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * '/ipojo/handler/$namespace' resource, where $namespace stands for the namespace of a handler.
 */
public class HandlerNamespaceResource extends DefaultReadOnlyResource {

    private final Map<String, HandlerNamespaceNameResource> m_names = new LinkedHashMap<String, HandlerNamespaceNameResource>();

    public HandlerNamespaceResource(String namespace) {
        super(HandlersResource.PATH.addElements(namespace));
    }

    public void addHandlerName(HandlerFactory handler) {
        synchronized (m_names) {
            m_names.put(handler.getHandlerName(), new HandlerNamespaceNameResource(handler));
        }
    }

    public boolean removeHandlerName(HandlerFactory handler) {
        synchronized (m_names) {
            m_names.remove(handler.getHandlerName());
            return m_names.isEmpty();
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_names) {
            return new ArrayList<Resource>(m_names.values());
        }
    }

}
