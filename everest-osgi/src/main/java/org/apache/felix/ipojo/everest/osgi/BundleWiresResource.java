package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.*;
import java.util.Map.Entry;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.*;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:52 PM
 */
public class BundleWiresResource extends DefaultReadOnlyResource {

    public static final String WIRES_PATH = "wires";

    public static final String REQUIREMENTS_PATH = "requirements";

    public static final String REQUIREMENT_PATH = "requirement";

    public static final String CAPABILITIES_PATH = "capabilities";

    public static final String CAPABILITY_PATH = "capability";

    private final Bundle m_bundle;

    Map<BundleCapability, Set<BundleWire>> capabilities = new HashMap<BundleCapability, Set<BundleWire>>();

    Map<BundleRequirement, Set<BundleWire>> requirements = new HashMap<BundleRequirement, Set<BundleWire>>();

    public BundleWiresResource(Path path, Bundle bundle) {
        super(path.add(Path.from(Path.SEPARATOR + WIRES_PATH)));
        m_bundle = bundle;
        BundleWiring wiring = m_bundle.adapt(BundleWiring.class);

        // get provided wires from all namespaces
        List<BundleWire> providedWires = wiring.getProvidedWires(null);
        for (BundleWire providedWire : providedWires) {
            BundleCapability capability = providedWire.getCapability();
            if (capabilities.containsKey(providedWire.getCapability())) {
                capabilities.get(capability).add(providedWire);
            } else {
                Set<BundleWire> newWireSet = new HashSet<BundleWire>();
                newWireSet.add(providedWire);
                capabilities.put(capability, newWireSet);
            }
        }

        // get required wires from all namespaces
        Map<BundleRequirement, Set<BundleWire>> requirements = new HashMap<BundleRequirement, Set<BundleWire>>();
        List<BundleWire> requiredWires = wiring.getRequiredWires(null);
        for (BundleWire requiredWire : requiredWires) {
            BundleRequirement requirement = requiredWire.getRequirement();
            if (requirements.containsKey(requirement)) {
                requirements.get(requirement).add(requiredWire);
            } else {
                Set<BundleWire> newWireSet = new HashSet<BundleWire>();
                newWireSet.add(requiredWire);
                requirements.put(requirement, newWireSet);
            }
        }

    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (BundleCapability capability : capabilities.keySet()) {
            metadataBuilder.set(uniqueCapabilityId(capability), metadataFrom(new ImmutableResourceMetadata.Builder(), capability).build());
        }
        for (BundleRequirement requirement : requirements.keySet()) {
            metadataBuilder.set(uniqueRequirementId(requirement), metadataFrom(new ImmutableResourceMetadata.Builder(), requirement).build());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (Entry<BundleCapability, Set<BundleWire>> capabilityWires : capabilities.entrySet()) {
            resources.add(new BundleCapabilityResource(getPath().add(Path.from(Path.SEPARATOR + CAPABILITIES_PATH)), capabilityWires.getKey(), capabilityWires.getValue()));
        }
        for (Entry<BundleRequirement, Set<BundleWire>> requirementWires : requirements.entrySet()) {
            resources.add(new BundleRequirementResource(getPath().add(Path.from(Path.SEPARATOR + REQUIREMENTS_PATH)), requirementWires.getKey(), requirementWires.getValue()));
        }
        return resources;
    }
}