package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import static org.apache.felix.ipojo.everest.ipojo.IpojoUtil.elementToMap;

/**
 * '/ipojo/factory/$name/$version' resource, where $name stands for the name of a factory, and $version for its version.
 */
public class FactoryNameVersionResource extends DefaultResource {

    /**
     * The represented factory.
     */
    private final Factory m_factory;

    /**
     * The base immutable metadata of this resource.
     */
    private final ResourceMetadata m_baseMetadata;

    //TODO add relation to factory declaration
    //TODO add relation to instances created by this factory
    //TODO add relation to declaring bundle
    //TODO add relation to Factory service
    //TODO add relation to used handlers

    /**
     * @param factory the factory represented by this resource
     */
    public FactoryNameVersionResource(Factory factory) {
        super(canonicalPathOf(factory));
        m_factory = factory;

        // Build the immutable metadata of this factory.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", factory.getName());
        mb.set("version", factory.getVersion());
        mb.set("componentMetadata", elementToMap(m_factory.getComponentMetadata())); // Map<String, ...>
        mb.set("requiredHandlers", m_factory.getRequiredHandlers()); // List<String>
        m_baseMetadata = mb.build();
    }

    @Override
    public synchronized ResourceMetadata getMetadata() {
        // Append mutable state to the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder(m_baseMetadata);

        // State
        String state;
        switch (m_factory.getState()) {
            case Factory.VALID:
                state = "valid";
                break;
            case Factory.INVALID:
                state = "invalid";
                break;
            default:
                // Very weird...
                state = "unknown";
        }
        mb.set("state", state);

        // Missing handlers
        mb.set("missingHandlers", m_factory.getMissingHandlers()); // List<String>

        // Description
        mb.set("description", elementToMap(m_factory.getComponentMetadata())); // Map<String, ...>

        return mb.build();
    }

    public static Path canonicalPathOf(Factory f) {
        // Canonical path is '/ipojo/factory/$factoryName/$factoryVersion'
        // If m_version == null, then $factoryVersion is the literal 'null'
        return FactoriesResource.PATH.addElements(f.getName(), String.valueOf(f.getVersion()));
    }
}
