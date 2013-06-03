package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.extender.ExtensionDeclaration;
import org.apache.felix.ipojo.extender.Status;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.*;

/**
 * '/ipojo/declaration/extension/$name' resource.
 */
public class ExtensionDeclarationResource extends DefaultReadOnlyResource {

    /**
     * The underlying ExtensionDeclaration service.
     */
    private final WeakReference<ExtensionDeclaration> m_extension;

    /**
     * Creates an ExtensionDeclarationResource for the given extension declaration.
     *
     * @param extension extension declaration service object
     * @param ref       extension declaration service reference
     */
    public ExtensionDeclarationResource(ExtensionDeclaration extension, ServiceReference<ExtensionDeclaration> ref) {
        super(EXTENSION_DECLARATIONS.addElements(extension.getExtensionName()),
                new ImmutableResourceMetadata.Builder()
                        .set("name", extension.getExtensionName())
                        .build());
        m_extension = new WeakReference<ExtensionDeclaration>(extension);
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
        ExtensionDeclaration e = m_extension.get();
        ResourceMetadata m = super.getMetadata();
        if (e == null) {
            // Reference has been released
            return m;
        }
        // Add dynamic metadata
        Status s = e.getStatus();
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
        if (clazz == ExtensionDeclaration.class) {
            // Returns null if reference has been released
            return clazz.cast(m_extension.get());
        } else {
            return super.adaptTo(clazz);
        }
    }
}
