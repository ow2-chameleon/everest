package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueRequirementId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:12 PM
 */
public class RequiredWireResource extends DefaultReadOnlyResource {

    public static final String WIRE_CAPABILITY = "capability";

    private final BundleWire m_wire;

    public RequiredWireResource(Path path, BundleWire wire) {
        super(path.addElements("wire" + wire.hashCode()));
        m_wire = wire;

        //find capability
        BundleCapability capability = m_wire.getCapability();
        String capabilityId = OsgiResourceUtils.uniqueCapabilityId(capability);
        Path capabilityPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(capability.getRevision().getBundle().getBundleId()),
                BundleWiresResource.WIRES_PATH,
                BundleWiresResource.CAPABILITIES_PATH,
                capabilityId
        );
        setRelations(new DefaultRelation(capabilityPath, Action.READ, WIRE_CAPABILITY));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BundleWiresResource.REQUIREMENT_PATH, uniqueRequirementId(m_wire.getRequirement()));
        metadataBuilder.set(BundleWiresResource.CAPABILITY_PATH, uniqueCapabilityId(m_wire.getCapability()));
        return metadataBuilder.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (BundleWire.class.equals(clazz)) {
            return (A) m_wire;
        } else {
            return null;
        }
    }

}
