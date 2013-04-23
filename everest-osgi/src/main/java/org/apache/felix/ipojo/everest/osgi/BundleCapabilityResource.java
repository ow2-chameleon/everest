package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.metadataFrom;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 12:43 PM
 */
public class BundleCapabilityResource extends DefaultReadOnlyResource {

    public static final String PROVIDED_WIRES_PATH = "provided-wire";

    private final Set<BundleWire> m_wires;
    private final BundleCapability m_capability;
    private final boolean isPackage;

    public BundleCapabilityResource(Path path, BundleCapability bundleCapability, Set<BundleWire> wires) {
        super(path);
        m_capability = bundleCapability;
        m_wires = wires;
        isPackage = m_capability.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE);
        List<Relation> relations = new ArrayList<Relation>();
        if (isPackage) {
            // add relation to package
            String packageId = uniqueCapabilityId(m_capability);
            Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
            relations.add(new DefaultRelation(packagePath, Action.READ, packageId));
            // add relation to bundle export package header
            long bundleId = m_capability.getRevision().getBundle().getBundleId();
            Path exportPackagePath = BundleResourceManager.getInstance().getPath()
                    .addElements(Long.toString(bundleId), BundleHeadersResource.HEADERS_PATH, BundleHeadersResource.EXPORT_PACKAGE, packageId);
            relations.add(new DefaultRelation(exportPackagePath, Action.READ, BundleHeadersResource.EXPORT_PACKAGE));
        }
        setRelations();
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
        for (BundleWire wire : m_wires) {
            resources.add(new ProvidedWireResource(getPath().add(Path.from(Path.SEPARATOR + PROVIDED_WIRES_PATH)), wire));
        }
        return resources;
    }
}
