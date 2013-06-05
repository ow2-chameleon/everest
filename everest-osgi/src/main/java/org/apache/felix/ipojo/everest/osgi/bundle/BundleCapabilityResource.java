package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResourceManager;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 12:43 PM
 */
public class BundleCapabilityResource extends AbstractResourceCollection {

    public final static String PACKAGE_RELATION = "package";

    private final List<BundleWire> m_wires = new ArrayList<BundleWire>();

    private final BundleCapability m_capability;
    private final boolean isPackage;

    public BundleCapabilityResource(Path path, BundleCapability bundleCapability) {
        super(path.addElements(uniqueCapabilityId(bundleCapability)));
        m_capability = bundleCapability;
        isPackage = m_capability.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE);
        List<Relation> relations = new ArrayList<Relation>();
        // calculate wires coming from this capability
        BundleRevision revision = m_capability.getRevision();
        if (revision != null) {
            BundleWiring wiring = revision.getWiring();
            if (wiring != null) {
                List<BundleWire> allWires = wiring.getProvidedWires(m_capability.getNamespace());
                for (BundleWire wire : allWires) {
                    if (wire.getCapability().equals(m_capability)) {
                        // and add a relation link
                        m_wires.add(wire);
                        String wireId = uniqueWireId(wire);
                        Path wirePath = BundleResourceManager.getInstance().getPath().addElements(
                                BundleWiresResource.WIRES_PATH,
                                wireId
                        );
                        relations.add(new DefaultRelation(wirePath, Action.READ, wireId));
                    }
                }
            }

            if (isPackage) {
                // add relation to package
                String packageId = uniqueCapabilityId(m_capability);
                Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
                relations.add(new DefaultRelation(packagePath, Action.READ, PACKAGE_RELATION));
                // add relation to bundle export package header
                long bundleId = revision.getBundle().getBundleId();
                Path exportPackagePath = BundleResourceManager.getInstance().getPath()
                        .addElements(Long.toString(bundleId), BundleHeadersResource.HEADERS_PATH, BundleHeadersResource.EXPORT_PACKAGE, packageId);
                relations.add(new DefaultRelation(exportPackagePath, Action.READ, BundleHeadersResource.EXPORT_PACKAGE));
            }
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataFrom(metadataBuilder, m_capability);
        return metadataBuilder.build();
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

    public List<BundleWire> getWires() {
        ArrayList<BundleWire> wires = new ArrayList<BundleWire>();
        wires.addAll(m_wires);
        return wires;
    }

}
