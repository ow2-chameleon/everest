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

package org.ow2.chameleon.everest.osgi.packages;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;
import static org.ow2.chameleon.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource manager for packages.
 */
public class PackageResourceManager extends AbstractResourceCollection {

    /**
     * Name for packages resource
     */
    public static final String PACKAGE_ROOT_NAME = "packages";

    /**
     * Path to osgi packages : "/osgi/packages"
     */
    public static final Path PACKAGE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + PACKAGE_ROOT_NAME));

    /**
     * Map of package resources by unique id
     */
    private Map<String, PackageResource> m_packageResourceByPackageIdMap = new HashMap<String, PackageResource>();

    /**
     * Static instance of this singleton class
     */
    private static final PackageResourceManager instance = new PackageResourceManager();

    /**
     * Getter of the static instance of this singleton class
     *
     * @return the singleton static instance
     */
    public static PackageResourceManager getInstance() {
        return instance;
    }

    /**
     * An utility method for creating a resource that contains only relations to a list osgi packages
     *
     * @param path         resource path
     * @param capabilities list of packages as {@code BundleCapability}
     * @return {@code Builder} for the resource
     */
    public static Builder relationsBuilder(Path path, List<BundleCapability> capabilities) {
        DefaultResource.Builder builder = new Builder().fromPath(path);
        ArrayList<BundleCapability> copyCapabilities = new ArrayList<BundleCapability>(capabilities);
        for (BundleCapability capability : copyCapabilities) {
            if (capability != null) {
                String packageId = uniqueCapabilityId(capability);
                Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
                builder.with(new DefaultRelation(packagePath, Action.READ, packageId));
            }
        }
        return builder;
    }

    /**
     * Constructor for package resource manager
     */
    public PackageResourceManager() {
        super(PACKAGE_PATH);
    }

//    @Override
//    public ResourceMetadata getMetadata() {
//        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
//        synchronized (m_packageResourceByPackageIdMap) {
//            for (Map.Entry<String, PackageResource> e : m_packageResourceByPackageIdMap.entrySet()) {
//                metadataBuilder.set(e.getKey(), e.getValue().getSimpleMetadata());
//            }
//        }
//        return metadataBuilder.build();
//    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        synchronized (m_packageResourceByPackageIdMap) {
            resources.addAll(m_packageResourceByPackageIdMap.values());
        }
        return resources;
    }

    // Callback redirections from osgi root
    // =================================================================================================================

    public void addPackagesFrom(Bundle bundle) {
        synchronized (m_packageResourceByPackageIdMap) {
            BundleRevision revision = bundle.adapt(BundleRevision.class);
            if (revision != null) {
                List<BundleCapability> bundleCapabilities = revision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
                if (!bundleCapabilities.isEmpty()) {
                    for (BundleCapability bc : bundleCapabilities) {
                        PackageResource packageResource = new PackageResource(bc);
                        String uniquePackageId = packageResource.getUniquePackageId();
                        PackageResource oldPackage = m_packageResourceByPackageIdMap.put(uniquePackageId, packageResource);
                        if (oldPackage != null) {
                            Everest.postResource(ResourceEvent.UPDATED, packageResource);
                        } else {
                            Everest.postResource(ResourceEvent.CREATED, packageResource);
                        }
                    }
                }
            }
        }
    }

    public void removePackagesFrom(Bundle bundle) {
        synchronized (m_packageResourceByPackageIdMap) {
            ArrayList<String> packageIds = new ArrayList<String>();
            for (PackageResource pr : m_packageResourceByPackageIdMap.values()) {
                if (bundle.getBundleId() == pr.getBundleId()) {
                    packageIds.add(pr.getUniquePackageId());
                }
            }
            if (!packageIds.isEmpty()) {
                for (String s : packageIds) {
                    PackageResource removedPackage = m_packageResourceByPackageIdMap.remove(s);
                    Everest.postResource(ResourceEvent.DELETED, removedPackage);
                }
            }
        }
    }

}
