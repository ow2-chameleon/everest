package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:52 PM
 */
public class BundleWiresResource extends AbstractResourceCollection {

    public static final String WIRES_PATH = "wires";

    public static final String REQUIREMENTS_PATH = "requirements";

    public static final String REQUIREMENT_PATH = "requirement";

    public static final String CAPABILITIES_PATH = "capabilities";

    public static final String CAPABILITY_PATH = "capability";

    private final Bundle m_bundle;

    List<BundleCapability> capabilities = new ArrayList<BundleCapability>();

    List<BundleRequirement> requirements = new ArrayList<BundleRequirement>();

    public BundleWiresResource(Path path, Bundle bundle) {
        super(path.addElements(WIRES_PATH));
        m_bundle = bundle;
        BundleWiring wiring = m_bundle.adapt(BundleWiring.class);
        if (wiring != null) {
            // get capabilities from all namespaces
            capabilities.addAll(wiring.getCapabilities(null));
            // get requirements from all namespaces
            requirements.addAll(wiring.getRequirements(null));
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (BundleCapability capability : capabilities) {
            metadataBuilder.set(uniqueCapabilityId(capability), metadataFrom(new ImmutableResourceMetadata.Builder(), capability).build());
        }
        for (BundleRequirement requirement : requirements) {
            metadataBuilder.set(uniqueRequirementId(requirement), metadataFrom(new ImmutableResourceMetadata.Builder(), requirement).build());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        Path capabilitiesPath = getPath().addElements(CAPABILITIES_PATH);
        DefaultResource.Builder builder = new Builder().fromPath(capabilitiesPath);
        for (BundleCapability capability : capabilities) {
            BundleCapabilityResource bundleCapabilityResource = new BundleCapabilityResource(capabilitiesPath, capability);
            builder.with(bundleCapabilityResource);
            builder.with(new DefaultRelation(bundleCapabilityResource.getPath(), Action.READ, capabilitiesPath.getLast() + ":" + bundleCapabilityResource.getPath().getLast()));
        }
        try {
            resources.add(builder.build());
        } catch (IllegalResourceException e) {
            // should never happen
        }

        Path requirementsPath = getPath().addElements(REQUIREMENTS_PATH);
        builder = new Builder().fromPath(requirementsPath);
        for (BundleRequirement requirement : requirements) {
            BundleRequirementResource bundleRequirementResource = new BundleRequirementResource(requirementsPath, requirement);
            builder.with(bundleRequirementResource);
            builder.with(new DefaultRelation(bundleRequirementResource.getPath(), Action.READ, requirementsPath.getLast() + ":" + bundleRequirementResource.getPath()));
        }
        try {
            resources.add(builder.build());
        } catch (IllegalResourceException e) {
            // should never happen
        }
        return resources;
    }
}