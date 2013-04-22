package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.getChild;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.metadataFrom;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 12:43 PM
 */
public class BundleCapabilityResource extends DefaultReadOnlyResource {

    public static final String PROVIDED_WIRES_PATH = "provided-wire";

    private static final String PROVIDER_PACKAGE = "provider-package-export";

    private final Set<BundleWire> m_wires;
    private final BundleCapability m_capability;
    private final boolean isPackage;

    public BundleCapabilityResource(Path path, BundleCapability bundleCapability, Set<BundleWire> wires) {
        super(path);
        m_capability = bundleCapability;
        m_wires = wires;
        isPackage = m_capability.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE) ? true : false;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataFrom(metadataBuilder, m_capability);
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        if (isPackage) {
            // add link to package export
            String packageId = OsgiResourceUtils.uniqueCapabilityId(m_capability);
            Resource packageRes = getChild(PackageResourceManager.getInstance(), packageId);
            if (packageRes != null) {
                resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + PROVIDER_PACKAGE)), packageRes));
            }
        }
        for (BundleWire wire : m_wires) {
            resources.add(new ProvidedWireResource(getPath().add(Path.from(Path.SEPARATOR + PROVIDED_WIRES_PATH)), wire));
        }
        return resources;
    }
}
