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

package org.ow2.chameleon.everest.osgi.service;

import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.osgi.bundle.BundleResourceManager;
import org.ow2.chameleon.everest.osgi.packages.PackageResourceManager;
import org.ow2.chameleon.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.packageNamesFromService;
import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.ow2.chameleon.everest.osgi.service.ServiceResourceManager.SERVICES_PATH;

/**
 * Resource representing an osgi service.
 */
public class ServiceResource extends AbstractResourceCollection {

    /**
     * Relation name for the bundle from which this service is published
     */
    public static final String FROM_BUNDLE_NAME = "from-bundle";

    /**
     * Relation name for the bundles that uses this service
     */
    public static final String USES_BUNDLES_NAME = "using-bundles";

    /**
     * Relation name for the package that defines this service
     */
    public static final String FROM_PACKAGE_NAME = "from-package";

    /**
     * The service reference
     */
    private ServiceReference m_serviceReference;

    /**
     * Constructor for service resource
     *
     * @param serviceReference the service reference
     */
    public ServiceResource(ServiceReference serviceReference) {
        super(SERVICES_PATH.addElements(Long.toString((Long) serviceReference.getProperty(Constants.SERVICE_ID))));
        m_serviceReference = serviceReference;
        List<Relation> relations = new ArrayList<Relation>();
        // Bundle from which this service is registered
        Bundle bundle = m_serviceReference.getBundle();
        Path bundlePath = BundleResourceManager.getInstance().getPath().addElements(Long.toString(bundle.getBundleId()));
        relations.add(new DefaultRelation(bundlePath, Action.READ, FROM_BUNDLE_NAME));
        //Package of the bundle that is exposed for this service
        String[] packageNames = packageNamesFromService(m_serviceReference);
        BundleRevision rev = bundle.adapt(BundleRevision.class);
        List<BundleCapability> capabilities = rev.getDeclaredCapabilities(PACKAGE_NAMESPACE);
        BundleCapability capability = null;
        //TODO go find the package
        for (BundleCapability cap : capabilities) {
            for (String packageName : packageNames) {
                if (cap.getAttributes().get(PACKAGE_NAMESPACE).equals(packageName)) {
                    //System.out.println(serviceReference.getProperty(Constants.OBJECTCLASS)+" - "+packageName);
                    capability = cap;
                }
            }
        }
        if (capability != null) {
            Path packagePath = PackageResourceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + uniqueCapabilityId(capability)));
            relations.add(new DefaultRelation(packagePath, Action.READ, FROM_PACKAGE_NAME));
        }

        // Create relations
        setRelations(relations);
    }

//    public ResourceMetadata getSimpleMetadata() {
//        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
//        metadataBuilder.set(Constants.SERVICE_ID, m_serviceReference.getProperty(Constants.SERVICE_ID));
//        metadataBuilder.set(Constants.OBJECTCLASS, m_serviceReference.getProperty(Constants.OBJECTCLASS));
//        metadataBuilder.set(FROM_BUNDLE_NAME, m_serviceReference.getBundle().getBundleId());
//        return metadataBuilder.build();
//    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String s : m_serviceReference.getPropertyKeys()) {
            metadataBuilder.set(s, m_serviceReference.getProperty(s));
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        // Uses Bundles
        Bundle[] uses = m_serviceReference.getUsingBundles();
        if (uses != null) {
            DefaultResource.Builder builder = BundleResourceManager.relationsBuilder(getPath().addElements(USES_BUNDLES_NAME), Arrays.asList(uses));
            try {
                resources.add(builder.build());
            } catch (IllegalResourceException e) {
                // should never happen
            }
        }
        return resources;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (ServiceReference.class.equals(clazz)) {
            return (A) m_serviceReference;
        } else if (ServiceResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    // Methods for easy accessing some information
    // =================================================================================================================

    public long getServiceId() {
        return (Long) m_serviceReference.getProperty(Constants.SERVICE_ID);
    }

    public String[] getObjectClass() {
        return (String[]) m_serviceReference.getProperty(Constants.OBJECTCLASS);
    }

    public long fromBundle() {
        return m_serviceReference.getBundle().getBundleId();
    }

}
