package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.InstanceDeclaration;
import org.apache.felix.ipojo.extender.Status;

/**
 * '/ipojo/declaration/instance' resource.
 */
public class InstanceDeclarationResource extends DefaultReadOnlyResource {

    final InstanceDeclaration m_instance;
    private final ResourceMetadata m_baseMetadata;

    public InstanceDeclarationResource(Path path, InstanceDeclaration declaration) {
        super(path);
        m_instance = declaration;

        // Build the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_instance.getInstanceName()); // String
        mb.set("factory.name", m_instance.getComponentName()); // String, deprecated but who cares?
        mb.set("factory.version", m_instance.getComponentVersion()); // String
        mb.set("configuration", m_instance.getConfiguration()); // ??? may be problematic !!!
        m_baseMetadata = mb.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        Status s = m_instance.getStatus();
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder(m_baseMetadata);
        b.set("status.isBound", s.isBound()); // Boolean
        b.set("status.message", s.getMessage()); // String
        b.set("status.throwable", s.getThrowable()); // Serializable
        return b.build();
    }

}
