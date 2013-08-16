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
import org.ow2.chameleon.everest.osgi.packages.PackageResourceManager;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.*;

/**
 * Resource representing a {@code BundleCapability}.
 */
public class BundleCapabilityResource extends AbstractResourceCollection {

    /**
     * Relation name for package
     */
    public final static String PACKAGE_RELATION = "package";

    /**
     * Wires linked to this capability
     */
    private final List<BundleWire> m_wires = new ArrayList<BundleWire>();

    /**
     * Represented bundle capability
     */
    private final BundleCapability m_capability;

    /**
     * if this is a package
     */
    private final boolean isPackage;

    /**
     * Constructor for bundle capability resource
     *
     * @param path
     * @param bundleCapability
     */
    public BundleCapabilityResource(Path path, BundleWiring hostWiring, BundleCapability bundleCapability) {
        super(path.addElements(uniqueCapabilityId(bundleCapability)));
        m_capability = bundleCapability;
        isPackage = m_capability.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE);
        List<Relation> relations = new ArrayList<Relation>();
        // calculate wires coming from this capability
        BundleRevision revision = m_capability.getRevision();
        if (revision != null) {
            String bundleId = Long.toString(revision.getBundle().getBundleId());
            //BundleWiring wiring = revision.getWiring();
            BundleWiring wiring = hostWiring;
            if (wiring != null) {
                List<BundleWire> allWires = wiring.getProvidedWires(m_capability.getNamespace());
                for (BundleWire wire : allWires) {
                    if (wire.getCapability().equals(m_capability)) {
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

            if (isPackage) {
                // add relation to package
                String packageId = uniqueCapabilityId(m_capability);
                Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
                relations.add(new DefaultRelation(packagePath, Action.READ, PACKAGE_RELATION));
                // add relation to bundle export package header
                Path exportPackagePath = BundleResourceManager.getInstance().getPath()
                        .addElements(bundleId, BundleHeadersResource.HEADERS_PATH, BundleHeadersResource.EXPORT_PACKAGE, packageId);
                relations.add(new DefaultRelation(exportPackagePath, Action.READ, BundleHeadersResource.EXPORT_PACKAGE));
            }
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataFrom(metadataBuilder, m_capability);
        return metadataBuilder.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (BundleCapability.class.equals(clazz)) {
            return (A) m_capability;
        } else if (BundleCapabilityResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public Map<String, String> getDirectives() {
        return m_capability.getDirectives();
    }

    public Map<String, Object> getAttributes() {
        return m_capability.getAttributes();
    }

    public boolean isPackage() {
        return isPackage;
    }

    public List<BundleWire> getWires() {
        ArrayList<BundleWire> wires = new ArrayList<BundleWire>();
        wires.addAll(m_wires);
        return wires;
    }

}
