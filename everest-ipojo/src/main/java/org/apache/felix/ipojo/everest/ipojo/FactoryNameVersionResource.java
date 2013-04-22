package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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
    @SuppressWarnings("deprecation")
    public FactoryNameVersionResource(Factory factory) {
        super(canonicalPathOf(factory));
        m_factory = factory;
        // Build the immutable metadata of this factory.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_factory.getName()); // String
        mb.set("version", m_factory.getVersion()); // String
        mb.set("className", m_factory.getClassName()); // String, deprecated but who cares?
        m_baseMetadata = mb.build();
    }

    @Override
    public synchronized ResourceMetadata getMetadata() {
        // Append mutable state to the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder(m_baseMetadata);


        mb.set("state", stateAsString(m_factory.getState())); // String
        mb.set("missingHandlers", m_factory.getMissingHandlers()); // List<String>

        return mb.build();
    }

    private static Path canonicalPathOf(Factory f) {
        // Canonical path is '/ipojo/factory/$factoryName/$factoryVersion'
        // If m_version == null, then $factoryVersion is the literal 'null'
        return FactoriesResource.PATH.addElements(f.getName(), String.valueOf(f.getVersion()));
    }

    private static String stateAsString(int state) {
        switch (state) {
            case Factory.VALID:
                return "valid";
            case Factory.INVALID:
                return "invalid";
            default:
                return "unknown";
        }
    }
}
