package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.getChild;
import static org.apache.felix.ipojo.everest.osgi.ServiceResourceManager.SERVICES_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 11:39 AM
 */
public class ServiceResource extends DefaultReadOnlyResource {

    public static final String FROM_BUNDLE_NAME = "from-bundle";

    public static final String USES_BUNDLES_NAME = "using-bundles";

    public static final String FROM_PACKAGE_NAME = "from-package";

    private ServiceReference m_serviceReference;

    public ServiceResource(ServiceReference serviceReference) {
        super(SERVICES_PATH.add(Path.from(Path.SEPARATOR + Long.toString((Long) serviceReference.getProperty(Constants.SERVICE_ID)))));
        m_serviceReference = serviceReference;
    }

    public ResourceMetadata getSimpleMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(Constants.SERVICE_ID, m_serviceReference.getProperty(Constants.SERVICE_ID));
        metadataBuilder.set(Constants.OBJECTCLASS, m_serviceReference.getProperty(Constants.OBJECTCLASS));
        metadataBuilder.set(FROM_BUNDLE_NAME, m_serviceReference.getBundle().getBundleId());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String s : m_serviceReference.getPropertyKeys()) {
            metadataBuilder.set(s, m_serviceReference.getProperty(s));
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();

        // Bundle from which this service is registered
        Bundle bundle = m_serviceReference.getBundle();
        // TODO Wow should reconsider this!!
        Resource bundleResource = getChild(BundleResourceManager.getInstance(), Path.SEPARATOR + bundle.getBundleId());
        if (bundleResource != null) {
            resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + FROM_BUNDLE_NAME)), bundleResource));
        }

        // Uses Bundles
        Bundle[] uses = m_serviceReference.getUsingBundles();
        resources.add(new ReadOnlyBundleSymlinksResource(getPath().add(Path.from(Path.SEPARATOR + USES_BUNDLES_NAME)), uses));

        //Package of the bundle that is exposed for this service
        //TODO find the package exporting this service...
        return resources;
    }
}
