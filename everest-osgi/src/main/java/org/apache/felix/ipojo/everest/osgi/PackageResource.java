package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniquePackageId;
import static org.apache.felix.ipojo.everest.osgi.PackageResourceManager.PACKAGE_PATH;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 9:06 AM
 */
public class PackageResource extends DefaultReadOnlyResource {

    public static final String PROVIDER_BUNDLE_NAME = "provider-bundle";

    public static final String IMPORTER_BUNDLE_NAME = "importer-bundles";

    private final BundleCapability m_bundleCapability;
    private final String m_packageName;
    private final Version m_version;
    private final Map<String, Object> m_attributes;
    private final Map<String, String> m_directives;

    public PackageResource(BundleCapability bundleCapability) {
        super(PACKAGE_PATH.add(Path.from(uniquePackageId(bundleCapability.getRevision().getBundle().getBundleId(),
                (String) bundleCapability.getAttributes().get(PACKAGE_NAMESPACE),
                (Version) bundleCapability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE)))));
        m_bundleCapability = bundleCapability;
        m_attributes = bundleCapability.getAttributes();
        m_directives = bundleCapability.getDirectives();
        m_packageName = (String) m_attributes.get(PACKAGE_NAMESPACE);
        m_version = (Version) m_attributes.get(PACKAGE_VERSION_ATTRIBUTE);
    }

    public String getUniquePackageId() {
        return uniquePackageId(m_bundleCapability.getRevision().getBundle().getBundleId(), m_packageName, m_version);
    }

    public Bundle getBundle() {
        return m_bundleCapability.getRevision().getBundle();
    }

    public ResourceMetadata getSimpleMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(PACKAGE_NAMESPACE, m_packageName);
        metadataBuilder.set(PACKAGE_VERSION_ATTRIBUTE, m_version);
        metadataBuilder.set(CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE, getBundle().getSymbolicName());
        metadataBuilder.set(CAPABILITY_BUNDLE_VERSION_ATTRIBUTE, getBundle().getVersion());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder(getSimpleMetadata());
        for (Entry<String, Object> att : m_attributes.entrySet()) {
            metadataBuilder.set(att.getKey(), att.getValue());
        }
        for (Entry<String, String> dir : m_directives.entrySet()) {
            metadataBuilder.set(dir.getKey(), dir.getValue());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        // provider bundle
        Bundle bundle = m_bundleCapability.getRevision().getBundle();
        // create a link to bundle
        Resource bundleResource = BundleResourceManager.getInstance().getResource(BundleResourceManager.BUNDLE_PATH.add(Path.from(Path.SEPARATOR + bundle.getBundleId())).toString());
        if (bundleResource != null) {
            resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + PROVIDER_BUNDLE_NAME)), bundleResource));
        }

        // importers of this package
        ArrayList<Bundle> importers = new ArrayList<Bundle>();
        BundleWiring wiring = m_bundleCapability.getRevision().getBundle().adapt(BundleWiring.class);
        List<BundleWire> wires = wiring.getProvidedWires(PACKAGE_NAMESPACE);
        for (BundleWire wire : wires) {
            if (wire.getCapability().equals(m_bundleCapability)) {
                Bundle requirerBundle = wire.getRequirerWiring().getBundle();
                importers.add(requirerBundle);
            }
        }
        // create links to importer bundles
        resources.add(new ReadOnlyBundleSymlinksResource(getPath().add(Path.from(Path.SEPARATOR + IMPORTER_BUNDLE_NAME)), importers.toArray(new Bundle[0])));
        return resources;
    }
}
