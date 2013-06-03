package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.Status;
import org.apache.felix.ipojo.extender.TypeDeclaration;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_BUNDLES;
import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_SERVICES;

/**
 * '/ipojo/declaration/type' resource.
 */
public class TypeDeclarationResource extends DefaultReadOnlyResource {

    private final WeakReference<TypeDeclaration> m_type;

    public TypeDeclarationResource(TypeDeclaration declaration, ServiceReference<TypeDeclaration> ref) {
        super(IpojoRootResource.TYPE_DECLARATIONS.addElements(declaration.getComponentName(), String.valueOf(declaration.getComponentVersion())),
                new ImmutableResourceMetadata.Builder()
                        .set("name", declaration.getComponentName())
                        .set("version", declaration.getComponentVersion())
                        .set("extension", declaration.getExtension())
                        .set("isPublic", declaration.isPublic())
                        .build()
        );
        m_type = new WeakReference<TypeDeclaration>(declaration);
        // Set the immutable relations
        setRelations(
                new DefaultRelation(
                        PATH_TO_OSGI_SERVICES.addElements(String.valueOf(ref.getProperty(Constants.SERVICE_ID))),
                        Action.READ,
                        "service",
                        "The ExtensionDeclaration OSGi service"),
                new DefaultRelation(
                        PATH_TO_OSGI_BUNDLES.addElements(String.valueOf(ref.getBundle().getBundleId())),
                        Action.READ,
                        "bundle",
                        "The declaring OSGi bundle"));
    }

    @Override
    public ResourceMetadata getMetadata() {
        TypeDeclaration t = m_type.get();
        ResourceMetadata m = super.getMetadata();
        if (t == null) {
            // Reference has been released
            return m;
        }
        Status s = t.getStatus();
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
        if (clazz == TypeDeclaration.class) {
            // Returns null if reference has been released
            return clazz.cast(m_type.get());
        } else {
            return super.adaptTo(clazz);
        }
    }

}
