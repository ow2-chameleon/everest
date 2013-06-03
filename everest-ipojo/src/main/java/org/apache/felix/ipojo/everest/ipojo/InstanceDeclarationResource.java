package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.InstanceDeclaration;
import org.apache.felix.ipojo.extender.Status;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_BUNDLES;
import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_SERVICES;

/**
 * '/ipojo/declaration/instance' resource.
 */
public class InstanceDeclarationResource extends DefaultReadOnlyResource {

    /**
     * The underlying InstanceDeclarationResource service.
     */
    private final WeakReference<InstanceDeclaration> m_instance;

    /**
     * The reference of the InstanceDeclarationResource service.
     */
    final ServiceReference<InstanceDeclaration> m_ref;

    public InstanceDeclarationResource(String index, InstanceDeclaration declaration, ServiceReference<InstanceDeclaration> ref) {
        super(IpojoRootResource.INSTANCE_DECLARATIONS.addElements(declaration.getInstanceName(), index),
                new ImmutableResourceMetadata.Builder()
                        .set("name", declaration.getInstanceName())
                        .set("factory.name", declaration.getComponentName())
                        .set("factory.version", declaration.getComponentVersion())
                        .set("configuration", declaration.getConfiguration())
                        .build());
        m_instance = new WeakReference<InstanceDeclaration>(declaration);
        m_ref = ref;
        // Set the immutable relations
        setRelations(
                new DefaultRelation(
                        PATH_TO_OSGI_SERVICES.addElements(String.valueOf(ref.getProperty(Constants.SERVICE_ID))),
                        Action.READ,
                        "service",
                        "The InstanceDeclaration OSGi service"),
                new DefaultRelation(
                        PATH_TO_OSGI_BUNDLES.addElements(String.valueOf(ref.getBundle().getBundleId())),
                        Action.READ,
                        "bundle",
                        "The declaring OSGi bundle"));
    }

    @Override
    public ResourceMetadata getMetadata() {
        InstanceDeclaration i = m_instance.get();
        ResourceMetadata m = super.getMetadata();
        if (i == null) {
            // Reference has been released
            return m;
        }
        // Add dynamic metadata
        Status s = i.getStatus();
        return new ImmutableResourceMetadata.Builder(m)
                .set("status.isBound", s.isBound()) // Boolean
                .set("status.message", s.getMessage()) // String
                .set("status.throwable", s.getThrowable()) // Serializable
                .build();
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == InstanceDeclaration.class) {
            return clazz.cast(m_instance.get());
        }
        return super.adaptTo(clazz);
    }

}
