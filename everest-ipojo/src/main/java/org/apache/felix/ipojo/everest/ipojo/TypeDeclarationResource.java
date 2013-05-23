package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.Status;
import org.apache.felix.ipojo.extender.TypeDeclaration;

/**
 * '/ipojo/declaration/type' resource.
 */
public class TypeDeclarationResource extends DefaultReadOnlyResource {

    private final TypeDeclaration m_type;
    private final ResourceMetadata m_baseMetadata;

    public TypeDeclarationResource(Path path, TypeDeclaration declaration) {
        super(path);
        m_type = declaration;

        // Build the immutable metadata of this type.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_type.getComponentName()); // String
        mb.set("version", m_type.getComponentVersion()); // String
        mb.set("extension", m_type.getExtension()); // String
        mb.set("isPublic", m_type.isPublic()); // String
        m_baseMetadata = mb.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        Status s = m_type.getStatus();
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder(m_baseMetadata);
        b.set("status.isBound", s.isBound()); // Boolean
        b.set("status.message", s.getMessage()); // String
        b.set("status.throwable", s.getThrowable()); // Serializable
        return b.build();
    }

}
