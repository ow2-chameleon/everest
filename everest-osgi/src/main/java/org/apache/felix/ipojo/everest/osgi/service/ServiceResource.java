package org.apache.felix.ipojo.everest.osgi.service;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleRelationsResource;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResourceManager;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResourceManager;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.packageNamesFromService;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.apache.felix.ipojo.everest.osgi.service.ServiceResourceManager.SERVICES_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 11:39 AM
 */
public class ServiceResource extends AbstractResourceCollection {

    public static final String FROM_BUNDLE_NAME = "from-bundle";

    public static final String USES_BUNDLES_NAME = "using-bundles";

    public static final String FROM_PACKAGE_NAME = "from-package";

    private ServiceReference m_serviceReference;

    public ServiceResource(ServiceReference serviceReference) {
        super(SERVICES_PATH.addElements(Long.toString((Long) serviceReference.getProperty(Constants.SERVICE_ID))));
        m_serviceReference = serviceReference;
        List<Relation> relations = new ArrayList<Relation>();
        // Bundle from which this service is registered
        Bundle bundle = m_serviceReference.getBundle();
        Path bundlePath = BundleResourceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + bundle.getBundleId()));
        relations.add(new DefaultRelation(bundlePath, Action.READ, FROM_BUNDLE_NAME));
        //Package of the bundle that is exposed for this service
        String[] packageNames = packageNamesFromService(m_serviceReference);
        BundleRevision rev = bundle.adapt(BundleRevision.class);
        List<BundleCapability> capabilities = rev.getDeclaredCapabilities(PACKAGE_NAMESPACE);
        BundleCapability capability = null;
        //TODO go find the package
        for (BundleCapability cap : capabilities) {
            for (String packageName : packageNames) {
                if (cap.getAttributes().get(PACKAGE_NAMESPACE).equals(packageName)) {
                    //System.out.println(serviceReference.getProperty(Constants.OBJECTCLASS)+" - "+packageName);
                    capability = cap;
                }
            }
        }
        if (capability != null) {
            Path packagePath = PackageResourceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + uniqueCapabilityId(capability)));
            relations.add(new DefaultRelation(packagePath, Action.READ, FROM_PACKAGE_NAME));
        }

        // Create relations
        setRelations(relations);
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
        // Uses Bundles
        Bundle[] uses = m_serviceReference.getUsingBundles();
        resources.add(new BundleRelationsResource(getPath().addElements(USES_BUNDLES_NAME), uses));
        return resources;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (ServiceReference.class.equals(clazz)) {
            return (A) m_serviceReference;
        } else if (ServiceResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    public long getServiceId() {
        return (Long) m_serviceReference.getProperty(Constants.SERVICE_ID);
    }

    public String[] getObjectClass() {
        return (String[]) m_serviceReference.getProperty(Constants.OBJECTCLASS);
    }

    public long fromBundle() {
        return m_serviceReference.getBundle().getBundleId();
    }

}
