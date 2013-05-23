package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.ExtensionDeclaration;
import org.apache.felix.ipojo.extender.Status;

/**
 * '/ipojo/declaration/extension/$name' resource.
 */
public class ExtensionDeclarationResource extends DefaultReadOnlyResource {

    private final ExtensionDeclaration m_extension;
    private final ImmutableResourceMetadata m_baseMetadata;

    public ExtensionDeclarationResource(Path path, ExtensionDeclaration extension) {
        super(path);
        m_extension = extension;

        // Build the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_extension.getExtensionName()); // String
        m_baseMetadata = mb.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        Status s = m_extension.getStatus();
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder(m_baseMetadata);
        b.set("status.isBound", s.isBound()); // Boolean
        b.set("status.message", s.getMessage()); // String
        b.set("status.throwable", s.getThrowable()); // Serializable
        return b.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == ExtensionDeclaration.class) {
            return (A) m_extension;
        }
        return null;
    }
}
