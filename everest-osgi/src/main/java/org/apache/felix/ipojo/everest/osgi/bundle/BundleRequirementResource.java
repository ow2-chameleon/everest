package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.BUNDLE_NAMESPACE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.HOST_NAMESPACE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.RESOLUTION_DYNAMIC;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;

/**
 * Resource representing a {@code BundleRequirement}.
 */
public class BundleRequirementResource extends AbstractResourceCollection {

    /**
     * Wires linked to this bundle requirement
     */
    private final List<BundleWire> m_wires = new ArrayList<BundleWire>();

    /**
     * Represented bundle requirement
     */
    private final BundleRequirement m_requirement;

    /**
     * if this requirement is a package requirement
     */
    private final boolean isPackage;

    /**
     * if this requirement is a bundle requirement
     */
    private final boolean isBundle;

    /**
     * if this requirement is a host requirement
     */
    private final boolean isFragment;

    /**
     * Constructor for this bundle requirement resource
     *
     * @param path
     * @param bundleRequirement
     */
    public BundleRequirementResource(Path path, BundleRequirement bundleRequirement) {
        super(path.addElements(uniqueRequirementId(bundleRequirement)));
        m_requirement = bundleRequirement;
        isPackage = m_requirement.getNamespace().equals(PACKAGE_NAMESPACE);
        isBundle = m_requirement.getNamespace().equals(BUNDLE_NAMESPACE);
        isFragment = m_requirement.getNamespace().equals(HOST_NAMESPACE);
        List<Relation> relations = new ArrayList<Relation>();
        // calculate wires coming to this requirement
        BundleRevision revision = m_requirement.getRevision();
        if (revision != null) {
            String bundleId = Long.toString(revision.getBundle().getBundleId());
            BundleWiring wiring = revision.getWiring();
            if (wiring != null) {
                List<BundleWire> allWires = wiring.getRequiredWires(m_requirement.getNamespace());
                for (BundleWire wire : allWires) {
                    if (wire.getRequirement().equals(m_requirement)) {
                        // and add a relation link
                        m_wires.add(wire);
                        String wireId = uniqueWireId(wire);
                        Path wirePath = BundleResourceManager.getInstance().getPath().addElements(bundleId,
                                BundleResource.WIRES_PATH,
                                wireId
                        );
                        relations.add(new DefaultRelation(wirePath, Action.READ, wireId));
                    }
                }
            }
            String requirementId = OsgiResourceUtils.uniqueRequirementId(m_requirement);
            Relation relation = null;
            Path bundleHeadersPath = BundleResourceManager.getInstance().getPath().addElements(bundleId, BundleHeadersResource.HEADERS_PATH);
            // add relation to package import header
            if (isPackage) {
                String dynamicOrNot = RESOLUTION_DYNAMIC.equals(m_requirement.getDirectives().get(Constants.RESOLUTION_DIRECTIVE)) ? BundleHeadersResource.DYNAMIC_IMPORT_PACKAGE : BundleHeadersResource.IMPORT_PACKAGE;
                Path requirementPath = bundleHeadersPath.addElements(dynamicOrNot, requirementId);
                relation = new DefaultRelation(requirementPath, Action.READ, dynamicOrNot);
            }
            // add relation to require-bundle header
            if (isBundle) {
                Path requirementPath = bundleHeadersPath.addElements(BundleHeadersResource.REQUIRE_BUNDLE, requirementId);
                relation = new DefaultRelation(requirementPath, Action.READ, BundleHeadersResource.REQUIRE_BUNDLE);
            }
            if (isFragment) {
                Path requirementPath = bundleHeadersPath.addElements(BundleHeadersResource.FRAGMENT_HOST, requirementId);
                relation = new DefaultRelation(requirementPath, Action.READ, BundleHeadersResource.FRAGMENT_HOST);
            }
            relations.add(relation);
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataFrom(metadataBuilder, m_requirement);
        return metadataBuilder.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (BundleRequirement.class.equals(clazz)) {
            return (A) m_requirement;
        } else if (BundleRequirementResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public Map<String, String> getDirectives() {
        return m_requirement.getDirectives();
    }

    public Map<String, Object> getAttributes() {
        return m_requirement.getAttributes();
    }

    public boolean isPackage() {
        return isPackage;
    }

    public boolean isBundle() {
        return isBundle;
    }

    public List<BundleWire> getWires() {
        ArrayList<BundleWire> wires = new ArrayList<BundleWire>();
        wires.addAll(m_wires);
        return wires;
    }
}
