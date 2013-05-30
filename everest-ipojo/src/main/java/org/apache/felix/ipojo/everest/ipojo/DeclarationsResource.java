package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.extender.ExtensionDeclaration;
import org.apache.felix.ipojo.extender.InstanceDeclaration;
import org.apache.felix.ipojo.extender.TypeDeclaration;

/**
 * '/ipojo/declaration' resource.
 */
public class DeclarationsResource extends ResourceMap<Resource> {

    public static final Path PATH = IpojoResource.PATH.addElements("declaration");

    // Instance declarations, indexed by name.
    // However instance declaration name is not unique, so for each name we have a map of instance declarations.
    // This map is indexed by the order of arrival (ugly, but what else?).
    private final ResourceMap<ResourceMap<InstanceDeclarationResource>> m_instances = new ResourceMap<ResourceMap<InstanceDeclarationResource>>(PATH.addElements("instance"), true);

    // Type declarations, indexed by name.
    // However type declaration name is not unique, so for each name we have a map of type declarations
    // This map is indexed by the versions of the declared types.
    private final ResourceMap<ResourceMap<TypeDeclarationResource>> m_types = new ResourceMap<ResourceMap<TypeDeclarationResource>>(PATH.addElements("type"), true);

    // Extension declarations, indexed by name.
    private final ResourceMap<ExtensionDeclarationResource> m_extensions = new ResourceMap<ExtensionDeclarationResource>(PATH.addElements("extension"), true);

    public DeclarationsResource() {
        super(PATH, false);
        addResource(m_instances, "instances", "Instance declarations");
        addResource(m_types, "types", "Type declarations");
        addResource(m_extensions, "extensions", "Extension declarations");
    }

    // Callbacks

    public void addInstanceDeclaration(InstanceDeclaration instance) {
        // ipojo/declaration/instance/$name
        String name = instance.getInstanceName();
        Path path = m_instances.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            ResourceMap<InstanceDeclarationResource> namedInstances = m_instances.getResource(path);
            if (namedInstances == null) {
                // First declaration with this name.
                namedInstances = new ResourceMap<InstanceDeclarationResource>(path, true);
                m_instances.addResource(namedInstances,
                        String.format("instances[%s]", name),
                        String.format("Instances declared with name '%s'", name));
            }
            // ipojo/declaration/instance/$name/$index
            String index = String.valueOf(namedInstances.size());
            namedInstances.addResource(new InstanceDeclarationResource(path.addElements(index), instance),
                    String.format("instance[%s]", index),
                    String.format("Instance declaration with name '%s' and index %s", name, index));
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public void addTypeDeclaration(TypeDeclaration type) {
        // ipojo/declaration/type/$name
        String name = type.getComponentName();
        Path path = m_types.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            ResourceMap<TypeDeclarationResource> namedTypes = m_types.getResource(path);
            if (namedTypes == null) {
                // First declaration with this name.
                namedTypes = new ResourceMap<TypeDeclarationResource>(path, true);
                m_types.addResource(namedTypes,
                        String.format("types[%s]", name),
                        String.format("Types declared with name '%s'", name));
            }
            // ipojo/declaration/type/$name/$version
            String version = String.valueOf(type.getComponentVersion());
            namedTypes.addResource(new TypeDeclarationResource(path.addElements(version), type),
                    String.format("type[%s]", version),
                    String.format("Type declaration with name '%s' and version %s", name, version));
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public void addExtensionDeclaration(ExtensionDeclaration extension) {
        // ipojo/declaration/extensions/$name
        String name = extension.getExtensionName();
        Path path = m_extensions.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            m_extensions.addResource(new ExtensionDeclarationResource(path, extension),
                    String.format("extension[%s]", name),
                    String.format("Extension declaration with name '%s'", name));
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public void removeInstanceDeclaration(InstanceDeclaration instance) {
        // ipojo/declaration/instance/$name
        String name = instance.getInstanceName();
        Path path = m_instances.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            ResourceMap<InstanceDeclarationResource> namedInstances = m_instances.getResource(path);
            // Find in namedInstances the resource to remove
            InstanceDeclarationResource toRemove = null;
            for (Resource r : namedInstances.getResources()) {
                InstanceDeclarationResource rr = (InstanceDeclarationResource) r;
                if (rr.m_instance == instance) {
                    toRemove = rr;
                    break;
                }
            }
            namedInstances.removeResource(toRemove);
            if (namedInstances.isEmpty()) {
                // Last standing declaration with that name.
                m_instances.removeResource(namedInstances);
            }
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public void removeTypeDeclaration(TypeDeclaration type) {
        // ipojo/declaration/type/$name
        String name = type.getComponentName();
        Path path = m_types.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            ResourceMap<TypeDeclarationResource> namedTypes = m_types.getResource(path);
            // ipojo/declaration/type/$name/$version
            String version = String.valueOf(type.getComponentVersion());
            namedTypes.removePath(path.addElements(version));
            if (namedTypes.isEmpty()) {
                // Last standing declaration with that name
                m_types.removeResource(namedTypes);
            }
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    public void removeExtensionDeclaration(ExtensionDeclaration extension) {
        // ipojo/declaration/extensions/$name
        String name = extension.getExtensionName();
        Path path = m_extensions.getPath().addElements(name);
        m_lock.writeLock().lock();
        try {
            m_extensions.removePath(path);
        } finally {
            m_lock.writeLock().unlock();
        }
    }

}
