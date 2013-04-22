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

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:12 PM
 */
public class RequiredWireResource extends DefaultReadOnlyResource {

    public static final String WIRE_CAPABILITY = "wire-capability";

    BundleWire m_wire;

    public RequiredWireResource(Path path, BundleWire wire) {
        super(path.add(Path.from(Path.SEPARATOR + wire.hashCode())));
        m_wire = wire;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BundleWiresResource.REQUIREMENT_PATH, uniqueRequirementId(m_wire.getRequirement()));
        metadataBuilder.set(BundleWiresResource.CAPABILITY_PATH, uniqueCapabilityId(m_wire.getCapability()));
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        //find capability
        BundleCapability capability = m_wire.getCapability();
        String capabilityId = OsgiResourceUtils.uniqueCapabilityId(capability);
        Resource bundleRes = getChild(BundleResourceManager.getInstance(), Long.toString(capability.getRevision().getBundle().getBundleId()));
        Resource capabilitiesRes = getChild(bundleRes, BundleWiresResource.WIRES_PATH + Path.SEPARATOR + BundleWiresResource.CAPABILITIES_PATH);
        Resource capabilityRes = getChild(capabilitiesRes, capabilityId);
        if (capabilityRes != null) {
            resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + WIRE_CAPABILITY)), capabilityRes));
        }
        return resources;
    }

}
