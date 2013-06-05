package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:12 PM
 */
public class BundleWireResource extends DefaultReadOnlyResource {

    public static final String WIRE_CAPABILITY = "capability";

    public static final String WIRE_REQUIREMENT = "requirement";

    private final BundleWire m_wire;

    public BundleWireResource(Path path, BundleWire wire) {
        super(path.addElements(uniqueWireId(wire)));
        m_wire = wire;

        //find capability
        BundleCapability capability = m_wire.getCapability();
        String capabilityId = OsgiResourceUtils.uniqueCapabilityId(capability);
        Path capabilityPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(capability.getRevision().getBundle().getBundleId()),
                BundleResource.CAPABILITIES_PATH,
                capabilityId
        );

        //find requirement
        BundleRequirement requirement = m_wire.getRequirement();
        String requirementId = OsgiResourceUtils.uniqueRequirementId(requirement);
        Path requirementPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(requirement.getRevision().getBundle().getBundleId()),
                BundleResource.REQUIREMENTS_PATH,
                requirementId
        );

        setRelations(
                new DefaultRelation(capabilityPath, Action.READ, WIRE_CAPABILITY),
                new DefaultRelation(requirementPath, Action.READ, WIRE_REQUIREMENT));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BundleResource.REQUIREMENTS_PATH, uniqueRequirementId(m_wire.getRequirement()));
        metadataBuilder.set(BundleResource.CAPABILITIES_PATH, uniqueCapabilityId(m_wire.getCapability()));
        return metadataBuilder.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (BundleWire.class.equals(clazz)) {
            return (A) m_wire;
        } else if (BundleWireResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public String getCapabilityId() {
        return uniqueCapabilityId(m_wire.getCapability());
    }

    public String getRequirementId() {
        return uniqueRequirementId(m_wire.getRequirement());
    }

}
