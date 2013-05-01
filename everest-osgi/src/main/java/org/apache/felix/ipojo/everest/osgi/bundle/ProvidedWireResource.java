package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueRequirementId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:08 PM
 */
public class ProvidedWireResource extends DefaultReadOnlyResource {

    public static final String WIRE_REQUIREMENT = "requirement";

    private final BundleWire m_wire;

    public ProvidedWireResource(Path path, BundleWire wire) {
        super(path.addElements("wire" + wire.hashCode()));
        m_wire = wire;

        //find requirement
        BundleRequirement requirement = m_wire.getRequirement();
        String requirementId = OsgiResourceUtils.uniqueRequirementId(requirement);
        Path requirementPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(requirement.getRevision().getBundle().getBundleId()),
                BundleWiresResource.WIRES_PATH,
                BundleWiresResource.REQUIREMENTS_PATH,
                requirementId
        );
        setRelations(new DefaultRelation(requirementPath, Action.READ, WIRE_REQUIREMENT));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BundleWiresResource.REQUIREMENT_PATH, uniqueRequirementId(m_wire.getRequirement()));
        metadataBuilder.set(BundleWiresResource.CAPABILITY_PATH, uniqueCapabilityId(m_wire.getCapability()));
        return metadataBuilder.build();
    }

}
