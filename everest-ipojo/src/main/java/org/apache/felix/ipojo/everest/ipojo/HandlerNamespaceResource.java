package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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
            m_names.put(handler.getName(), new HandlerNamespaceNameResource(handler));
        }
    }

    public boolean removeHandlerName(HandlerFactory handler) {
        synchronized (m_names) {
            m_names.remove(handler.getName());
            return m_names.isEmpty();
        }
    }

    @Override
    public List<Resource> getResources() {
        synchronized (m_names) {
            return new ArrayList<Resource>(m_names.values());
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        synchronized (m_names) {
            // For each namespace
            for (String name : m_names.keySet()) {
                b.set(name, m_names.get(name).getMetadata());
            }
        }
        return b.build();
    }

    @Override
    public List<Relation> getRelations() {
        // TODO aggregate relations of m_names
        return super.getRelations();
    }
}
