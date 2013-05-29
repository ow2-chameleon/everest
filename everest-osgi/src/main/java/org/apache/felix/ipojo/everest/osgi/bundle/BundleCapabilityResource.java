package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResourceManager;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.metadataFrom;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 12:43 PM
 */
public class BundleCapabilityResource extends AbstractResourceCollection {

    public final static String PACKAGE_RELATION = "package";

    private final List<ProvidedWireResource> m_wires = new ArrayList<ProvidedWireResource>();
    private final BundleCapability m_capability;
    private final boolean isPackage;

    public BundleCapabilityResource(Path path, BundleCapability bundleCapability) {
        super(path.addElements(uniqueCapabilityId(bundleCapability)));
        m_capability = bundleCapability;
        // calculate wires coming from this capability
        List<BundleWire> allWires = m_capability.getRevision().getWiring().getProvidedWires(m_capability.getNamespace());
        for (BundleWire wire : allWires) {
            if (wire.getCapability().equals(m_capability)) {
                m_wires.add(new ProvidedWireResource(getPath(), wire));
            }
        }

        isPackage = m_capability.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE);
        List<Relation> relations = new ArrayList<Relation>();
        if (isPackage) {
            // add relation to package
            String packageId = uniqueCapabilityId(m_capability);
            Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
            relations.add(new DefaultRelation(packagePath, Action.READ, PACKAGE_RELATION));
            // add relation to bundle export package header
            long bundleId = m_capability.getRevision().getBundle().getBundleId();
            Path exportPackagePath = BundleResourceManager.getInstance().getPath()
                    .addElements(Long.toString(bundleId), BundleHeadersResource.HEADERS_PATH, BundleHeadersResource.EXPORT_PACKAGE, packageId);
            relations.add(new DefaultRelation(exportPackagePath, Action.READ, BundleHeadersResource.EXPORT_PACKAGE));
        }
        setRelations(relations);
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
        resources.addAll(m_wires);
        return resources;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (BundleCapability.class.equals(clazz)) {
            return (A) m_capability;
        } else if (BundleCapabilityResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public Map<String, String> getDirectives() {
        return m_capability.getDirectives();
    }

    public Map<String, Object> getAttributes() {
        return m_capability.getAttributes();
    }

    public boolean isPackage() {
        return isPackage;
    }

    public List<ProvidedWireResource> getWires() {
        ArrayList<ProvidedWireResource> wires = new ArrayList<ProvidedWireResource>();
        wires.addAll(m_wires);
        return wires;
    }

}
