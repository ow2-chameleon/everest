package org.apache.felix.ipojo.everest.osgi.packages;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResourceManager;
import org.apache.felix.ipojo.everest.services.*;
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
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 9:06 AM
 */
public class PackageResource extends AbstractResourceCollection {

    public static final String PROVIDER_BUNDLE_NAME = "provider-bundle";

    public static final String IMPORTER_BUNDLE_NAME = "importer-bundles";

    public static final String PACKAGE_IN_USE = "in-use";

    private final BundleCapability m_bundleCapability;
    private final String m_packageName;
    private final Version m_version;
    private final Map<String, Object> m_attributes;
    private final Map<String, String> m_directives;

    // importers of this package
    private ArrayList<Bundle> importers = new ArrayList<Bundle>();

    public PackageResource(BundleCapability bundleCapability) {
        super(PackageResourceManager.PACKAGE_PATH.addElements(uniqueCapabilityId(bundleCapability)));
        m_bundleCapability = bundleCapability;
        m_attributes = bundleCapability.getAttributes();
        m_directives = bundleCapability.getDirectives();
        m_packageName = (String) m_attributes.get(PACKAGE_NAMESPACE);
        m_version = (Version) m_attributes.get(PACKAGE_VERSION_ATTRIBUTE);

        calculateImporters();
        // provider bundle
        Bundle bundle = m_bundleCapability.getRevision().getBundle();
        Path bundlePath = BundleResourceManager.getInstance().getPath().addElements(Long.toString(bundle.getBundleId()));

        // Set relations
        setRelations(
                new DefaultRelation(bundlePath, Action.READ, PROVIDER_BUNDLE_NAME)
        );

    }

    public ResourceMetadata getSimpleMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(PACKAGE_NAMESPACE, m_packageName);
        metadataBuilder.set(PACKAGE_VERSION_ATTRIBUTE, m_version);
        metadataBuilder.set(CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE, getBundle().getSymbolicName());
        metadataBuilder.set(CAPABILITY_BUNDLE_VERSION_ATTRIBUTE, getBundle().getVersion());
        calculateImporters();
        metadataBuilder.set(PACKAGE_IN_USE, !importers.isEmpty());
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
        calculateImporters();
        // create links to importer bundles
        Builder builder = BundleResourceManager.relationsBuilder(getPath().addElements(IMPORTER_BUNDLE_NAME), importers);
        try {
            resources.add(builder.build());
        } catch (IllegalResourceException e) {
            // should never happen
        }
        return resources;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (PackageResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    private Bundle getBundle() {
        return m_bundleCapability.getRevision().getBundle();
    }

    public String getUniquePackageId() {
        return uniqueCapabilityId(m_bundleCapability);
    }

    public String getPackageName() {
        return m_packageName;
    }

    public Version getVersion() {
        return m_version;
    }

    public long getBundleId() {
        return this.getBundle().getBundleId();
    }

    public String getBundleSymbolicName() {
        return this.getBundle().getSymbolicName();
    }

    public Version getBundleVersion() {
        return this.getBundle().getVersion();
    }

    public boolean isUsed() {
        calculateImporters();
        return !importers.isEmpty();
    }

    public Map<String, Object> getAttributes() {
        return m_attributes;
    }

    public Map<String, String> getDirectives() {
        return m_directives;
    }

    private void calculateImporters() {
        importers.clear();
        synchronized (importers) {
            // calculate importers
            Bundle bundle = m_bundleCapability.getRevision().getBundle();
            if (bundle != null) {
                BundleWiring wiring = bundle.adapt(BundleWiring.class);
                if (wiring != null) {
                    List<BundleWire> wires = wiring.getProvidedWires(PACKAGE_NAMESPACE);
                    if (wires != null) {
                        for (BundleWire wire : wires) {
                            if (wire.getCapability().equals(m_bundleCapability)) {
                                Bundle requirerBundle = wire.getRequirerWiring().getBundle();
                                importers.add(requirerBundle);
                            }
                        }
                    }
                }
            }
        }

    }

}
