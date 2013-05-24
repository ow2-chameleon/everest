package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.ExtensionDeclaration;
import org.apache.felix.ipojo.extender.InstanceDeclaration;
import org.apache.felix.ipojo.extender.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * '/ipojo/declaration' resource.
 */
public class DeclarationsResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("declaration");

    // Instance declarations, indexed by name.
    // However instance declaration name is not unique, so for each name we have a map of instance declarations.
    // This map is indexed by the order of arrival (ugly, but what else?).
    private final ResourceMap<ResourceMap<InstanceDeclarationResource>> m_instances = new ResourceMap<ResourceMap<InstanceDeclarationResource>>(PATH.addElements("instance"));

    // Type declarations, indexed by name.
    // However type declaration name is not unique, so for each name we have a map of type declarations
    // This map is indexed by the versions of the declared types.
    private final ResourceMap<ResourceMap<TypeDeclarationResource>> m_types = new ResourceMap<ResourceMap<TypeDeclarationResource>>(PATH.addElements("type"));

    // Extension declarations, indexed by name.
    private final ResourceMap<ExtensionDeclarationResource> m_extensions = new ResourceMap<ExtensionDeclarationResource>(PATH.addElements("extension"));

    public DeclarationsResource() {
        super(PATH);
    }

    // Callbacks

    public void addInstanceDeclaration(InstanceDeclaration instance) {
        synchronized (this) {
            // '/ipojo/declaration/instance/$name'
            Path path = m_instances.getPath().addElements(instance.getInstanceName());
            ResourceMap<InstanceDeclarationResource> declarationsWithName = m_instances.getResource(path);
            if (declarationsWithName == null) {
                // First declaration with this name.
                declarationsWithName = new ResourceMap<InstanceDeclarationResource>(path);
                m_instances.addResource(declarationsWithName);
            }
            // '/ipojo/declaration/instance/$name/$index', where index is the current size of declarationsWithName
            Path subPath = path.addElements(String.valueOf(declarationsWithName.size()));
            declarationsWithName.addResource(new InstanceDeclarationResource(subPath, instance));
        }
    }

    public void removeInstanceDeclaration(InstanceDeclaration instance) {
        synchronized (this) {
            // '/ipojo/declaration/instance/$name'
            Path path = m_instances.getPath().addElements(instance.getInstanceName());
            ResourceMap<InstanceDeclarationResource> declarationsWithName = m_instances.getResource(path);
            // Find the InstanceDeclarationResource to remove
            InstanceDeclarationResource resource = null;
            for (Resource r : declarationsWithName.getResources()) {
                InstanceDeclarationResource rr = (InstanceDeclarationResource) r;
                if (rr.m_instance == instance) {
                    resource = rr;
                    break;
                }
            }
            // Remove the declaration
            declarationsWithName.removeResource(resource);
            if (declarationsWithName.isEmpty()) {
                // It was the last standing declaration with that name
                m_instances.removeResource(declarationsWithName);
            }
        }
    }

    public void addTypeDeclaration(TypeDeclaration type) {
        synchronized (this) {
            // '/ipojo/declaration/type/$name'
            Path path = m_types.getPath().addElements(type.getComponentName());
            ResourceMap<TypeDeclarationResource> declarationsWithName = m_types.getResource(path);
            if (declarationsWithName == null) {
                // First declaration with this name.
                declarationsWithName = new ResourceMap<TypeDeclarationResource>(path);
                m_types.addResource(declarationsWithName);
            }
            // Generate key : the index is the version of the factory
            String v = type.getComponentVersion();
            // '/ipojo/declaration/type/$name/$version'
            Path subPath = path.addElements(v == null ? "null" : v);
            declarationsWithName.addResource(new TypeDeclarationResource(subPath, type));
        }
    }

    public void removeTypeDeclaration(TypeDeclaration type) {
        synchronized (this) {
            // '/ipojo/declaration/type/$name'
            Path path = m_types.getPath().addElements(type.getComponentName());
            ResourceMap<TypeDeclarationResource> declarationsWithName = m_types.getResource(path);
            // Get the index of the TypeDeclarationResource to remove
            String v = type.getComponentVersion();
            Path version = path.addElements(v == null ? "null" : v);
            // Remove the declaration
            declarationsWithName.removePath(version);
            if (declarationsWithName.isEmpty()) {
                // It was the last standing declaration with that name
                m_types.removeResource(declarationsWithName);
            }
        }
    }

    public void addExtensionDeclaration(ExtensionDeclaration extension) {
        synchronized (this) {
            // '/ipojo/declaration/extensions/$name'
            Path path = m_extensions.getPath().addElements(extension.getExtensionName());
            m_extensions.addResource(new ExtensionDeclarationResource(path, extension));
        }
    }

    public void removeExtensionDeclaration(ExtensionDeclaration extension) {
        synchronized (this) {
            // '/ipojo/declaration/extensions/$name'
            Path path = m_extensions.getPath().addElements(extension.getExtensionName());
            m_extensions.removePath(path);
        }
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> l = new ArrayList<Resource>();
        synchronized (this) {
            l.add(m_instances);
            l.add(m_types);
            l.add(m_extensions);
        }
        return l;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        synchronized (this) {
            b.set("instance", m_instances.getMetadata());
            b.set("type", m_types.getMetadata());
            b.set("extension", m_extensions.getMetadata());
        }
        return b.build();
    }

}
