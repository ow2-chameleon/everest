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

import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.osgi.OsgiResourceUtils;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.BundleNamespace.BUNDLE_NAMESPACE;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.BundleNamespace.HOST_NAMESPACE;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.PackageNamespace.RESOLUTION_DYNAMIC;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.*;

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
    public BundleRequirementResource(Path path, BundleWiring hostWiring, BundleRequirement bundleRequirement) {
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
            //BundleWiring wiring = revision.getWiring();
            BundleWiring wiring = hostWiring;
            if (wiring != null) {
                List<BundleWire> allWires = wiring.getRequiredWires(m_requirement.getNamespace());
                for (BundleWire wire : allWires) {
                    if (wire.getRequirement().equals(m_requirement)) {
                        // and add a relation link
                        m_wires.add(wire);
                        String wireId = uniqueWireId(wire);
                        Path wirePath = BundleResourceManager.getInstance().getPath().addElements(Long.toString(hostWiring.getBundle().getBundleId()),
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
