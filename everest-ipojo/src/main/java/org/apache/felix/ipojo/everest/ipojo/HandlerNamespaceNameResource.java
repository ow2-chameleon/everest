package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

/**
 * '/ipojo/handler/$namespace/$name' resource, where $namespace stands for the namespace of a handler, and $name for its name.
 */
public class HandlerNamespaceNameResource extends DefaultReadOnlyResource {

    /**
     * The underlying handler factory.
     */
    private final HandlerFactory m_handler;

    /**
     * The base immutable metadata of this resource.
     */
    private final ResourceMetadata m_baseMetadata;

    public HandlerNamespaceNameResource(HandlerFactory handler) {
        super(canonicalPathOf(handler));
        m_handler = handler;
        // Build the immutable metadata of this factory.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("namespace", m_handler.getNamespace()); // String
        mb.set("name", m_handler.getName()); // String
        m_baseMetadata = mb.build();
    }

    public static Path canonicalPathOf(HandlerFactory h) {
        // Canonical path is '/ipojo/handler/$namespace/$name'
        return HandlersResource.PATH.addElements(h.getNamespace(), h.getName());
    }

    @Override
    public ResourceMetadata getMetadata() {
        return m_baseMetadata;
    }
}
