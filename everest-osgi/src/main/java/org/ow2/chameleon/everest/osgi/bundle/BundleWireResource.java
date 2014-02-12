/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.osgi.bundle;

import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.OsgiResourceUtils;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.*;

/**
 * Resource representing a {@code BundleWire}.
 */
public class BundleWireResource extends DefaultReadOnlyResource<Object> {

    /**
     * Relation name for the linked capability
     */
    public static final String WIRE_CAPABILITY = "capability";

    /**
     * Relation name for the linked requirement
     */
    public static final String WIRE_REQUIREMENT = "requirement";

    /**
     * Represented bundle wire
     */
    private final BundleWire m_wire;

    /**
     * Constructor for this bundle wire resource
     *
     * @param path
     * @param wire
     */
    public BundleWireResource(Path path, BundleWire wire) {
        super(path.addElements(uniqueWireId(wire)));
        m_wire = wire;

        //find capability
        BundleCapability capability = m_wire.getCapability();
        String capabilityId = OsgiResourceUtils.uniqueCapabilityId(capability);
        Path capabilityPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(m_wire.getProviderWiring().getBundle().getBundleId()),
                BundleResource.CAPABILITIES_PATH,
                capabilityId
        );

        //find requirement
        BundleRequirement requirement = m_wire.getRequirement();
        String requirementId = OsgiResourceUtils.uniqueRequirementId(requirement);
        Path requirementPath = BundleResourceManager.getInstance().getPath().addElements(
                Long.toString(m_wire.getRequirerWiring().getBundle().getBundleId()),
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
        metadataBuilder.set(WIRE_REQUIREMENT, uniqueRequirementId(m_wire.getRequirement()));
        metadataBuilder.set(WIRE_CAPABILITY, uniqueCapabilityId(m_wire.getCapability()));
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
