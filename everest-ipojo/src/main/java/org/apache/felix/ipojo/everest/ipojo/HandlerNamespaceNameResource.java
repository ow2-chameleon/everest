package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.felix.ipojo.everest.ipojo.FactoryNameVersionResource.stateAsString;
import static org.apache.felix.ipojo.everest.ipojo.IpojoResource.PATH_TO_BUNDLES;

/**
 * '/ipojo/handler/$namespace/$name' resource, where $namespace stands for the namespace of a handler, and $name for its name.
 */
public class HandlerNamespaceNameResource extends DefaultReadOnlyResource {

    /**
     * The underlying handler factory.
     */
    private final HandlerFactory m_handler;

    /**
     * Flag indicating if the underlying HandlerFactory service still exists.
     */
    private volatile boolean m_isStale = false;

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

        // Relations
        List<Relation> relations = new ArrayList<Relation>();

        // Add relation 'bundle' to READ the bundle that declares this handler
        relations.add(new DefaultRelation(PATH_TO_BUNDLES.addElements(String.valueOf(m_handler.getBundleContext().getBundle().getBundleId())), Action.READ, "bundle"));

        // Add relation 'requiredHandler:$ns:$name' to READ the handlers required by this factory
        @SuppressWarnings("unchecked")
        List <String> required = (List < String>) m_handler.getRequiredHandlers();
        for (String nsName : required) {
            int i = nsName.lastIndexOf(':');
            String ns = nsName.substring(0, i);
            String name = nsName.substring(i+1);
            relations.add(new DefaultRelation(HandlersResource.PATH.addElements(ns, name), Action.READ, "requiredHandler:" + nsName));
        }

        setRelations(relations);
    }

    /**
     * Set this handler resource as stale. It happens when the underlying HandlerFactory service vanishes.
     */
    void setStale() {
        m_isStale = true;
    }

    public static Path canonicalPathOf(HandlerFactory h) {
        // Canonical path is '/ipojo/handler/$namespace/$name'
        return HandlersResource.PATH.addElements(h.getNamespace(), h.getName());
    }

    @Override
    public ResourceMetadata getMetadata() {
        // Append mutable state to the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder(m_baseMetadata);

        mb.set("state", stateAsString(m_handler.getState())); // String

        // Some factory getters miserably fail when the factory is stale.
        mb.set("missingHandlers", !m_isStale ? m_handler.getMissingHandlers() : Collections.emptyList()); // List<String>

        return mb.build();
    }
}
