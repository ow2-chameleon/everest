package org.apache.felix.ipojo.everest.osgi.packages;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:54 AM
 */
public class PackageResourceManager extends AbstractResourceCollection {

    public static final String PACKAGE_ROOT_NAME = "packages";

    public static final Path PACKAGE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + PACKAGE_ROOT_NAME));

    private Map<String, PackageResource> m_packageResourceByPackageIdMap = new HashMap<String, PackageResource>();

    private static final PackageResourceManager instance = new PackageResourceManager();

    public static PackageResourceManager getInstance() {
        return instance;
    }

    public PackageResourceManager() {
        super(PACKAGE_PATH);
    }

    public void addPackagesFrom(Bundle bundle) {
        synchronized (m_packageResourceByPackageIdMap) {
            BundleRevision revision = bundle.adapt(BundleRevision.class);
            if (revision != null) {
                List<BundleCapability> bundleCapabilities = revision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
                if (!bundleCapabilities.isEmpty()) {
                    for (BundleCapability bc : bundleCapabilities) {
                        PackageResource packageResource = new PackageResource(bc);
                        String uniquePackageId = packageResource.getUniquePackageId();
                        PackageResource oldPackage = m_packageResourceByPackageIdMap.put(uniquePackageId, packageResource);
                        if (oldPackage != null) {
                            Everest.postResource(ResourceEvent.UPDATED, packageResource);
                        } else {
                            Everest.postResource(ResourceEvent.CREATED, packageResource);
                        }
                    }
                }
            }
        }
    }

    public void removePackagesFrom(Bundle bundle) {
        synchronized (m_packageResourceByPackageIdMap) {
            ArrayList<String> packageIds = new ArrayList<String>();
            for (PackageResource pr : m_packageResourceByPackageIdMap.values()) {
                if (bundle.getBundleId() == pr.getBundleId()) {
                    packageIds.add(pr.getUniquePackageId());
                }
            }
            if (!packageIds.isEmpty()) {
                for (String s : packageIds) {
                    PackageResource removedPackage = m_packageResourceByPackageIdMap.remove(s);
                    Everest.postResource(ResourceEvent.DELETED, removedPackage);
                }
            }
        }
    }

//    @Override
//    public ResourceMetadata getMetadata() {
//        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
//        synchronized (m_packageResourceByPackageIdMap) {
//            for (Map.Entry<String, PackageResource> e : m_packageResourceByPackageIdMap.entrySet()) {
//                metadataBuilder.set(e.getKey(), e.getValue().getSimpleMetadata());
//            }
//        }
//        return metadataBuilder.build();
//    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        synchronized (m_packageResourceByPackageIdMap) {
            resources.addAll(m_packageResourceByPackageIdMap.values());
        }
        return resources;
    }

    public static Builder relationsBuilder(Path path, List<BundleCapability> capabilities) {
        DefaultResource.Builder builder = new Builder().fromPath(path);
        for (BundleCapability capability : capabilities) {
            if (capability != null) {
                String packageId = uniqueCapabilityId(capability);
                Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
                builder.with(new DefaultRelation(packagePath, Action.READ, packageId));
            }
        }
        return builder;
    }

}
