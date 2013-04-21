package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniquePackageId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:46 PM
 */
public class ReadOnlyPackageSymlinksResource extends DefaultReadOnlyResource {

    BundleCapability[] m_capabilities;

    public ReadOnlyPackageSymlinksResource(Path path, BundleCapability[] capability) {
        super(path);
        m_capabilities = capability;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        if (m_capabilities != null) {
            for (BundleCapability capability : m_capabilities) {
                ImmutableResourceMetadata.Builder packageMetadataBuilder = new ImmutableResourceMetadata.Builder();
                // calculate unique package Id
                long bundleId = capability.getRevision().getBundle().getBundleId();
                String packageName = (String) capability.getAttributes().get(PACKAGE_NAMESPACE);
                Version version = (Version) capability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE);
                String packageId = uniquePackageId(bundleId, packageName, version);
                packageMetadataBuilder.set(PACKAGE_NAMESPACE, packageName);
                packageMetadataBuilder.set(PACKAGE_VERSION_ATTRIBUTE, version);
                packageMetadataBuilder.set(CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE, capability.getRevision().getBundle().getSymbolicName());
                packageMetadataBuilder.set(CAPABILITY_BUNDLE_VERSION_ATTRIBUTE, capability.getRevision().getBundle().getVersion());
                metadataBuilder.set(packageId, packageMetadataBuilder.build());
            }
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        if (m_capabilities != null) {
            for (BundleCapability capability : m_capabilities) {
                // calculate unique package Id
                long bundleId = capability.getRevision().getBundle().getBundleId();
                String packageName = (String) capability.getAttributes().get(PACKAGE_NAMESPACE);
                Version version = (Version) capability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE);
                String packageId = uniquePackageId(bundleId, packageName, version);
                Path usesPath = getPath().add(Path.from(Path.SEPARATOR + packageId));
                Resource resource = PackageResourceManager.getInstance().getResource(PackageResourceManager.PACKAGE_PATH.add(Path.from(Path.SEPARATOR + packageId)).toString());
                if (resource != null) {
                    resources.add(new SymbolicLinkResource(usesPath, resource));
                }
            }
        }
        return resources;
    }
}
