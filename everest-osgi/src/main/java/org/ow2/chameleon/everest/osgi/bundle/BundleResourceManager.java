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

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.ow2.chameleon.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource manager for bundles.
 */
public class BundleResourceManager extends AbstractResourceCollection {

    /**
     * Name for bundles resource
     */
    public static final String BUNDLE_ROOT_NAME = "bundles";

    /**
     * Path to osgi bundles : "/osgi/bundles"
     */
    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    /**
     * Relation name for install relation
     */
    public static final String BUNDLE_INSTALL_RELATION = "install";

    /**
     * Relation name for update relation
     */
    public static final String BUNDLES_UPDATE_RELATION = "update";

    /**
     * Parameter for input for install/update relation
     */
    public static final String INSTALL_INPUT_PARAMETER = "input";

    /**
     * Parameter for location for install relation
     */
    public static final String INSTALL_LOCATION_PARAMETER = "location";

    /**
     * Parameter for refresh for update relation
     */
    public static final String REFRESH_PARAMETER = "refresh";

    /**
     * Parameter for resolve for update relation
     */
    public static final String RESOLVE_PARAMETER = "resolve";

    /**
     * Map of bundle resource by bundle id
     */
    private Map<Long, BundleResource> m_bundleResourcesMap = new HashMap<Long, BundleResource>();

    /**
     * Static instance of this singleton class
     */
    private static final BundleResourceManager instance = new BundleResourceManager();

    /**
     * Getter of the static instance of this singleton class
     *
     * @return the singleton static instance
     */
    public static BundleResourceManager getInstance() {
        return instance;
    }

    /**
     * An utility method for creating a resource that contains only relations to a list osgi bundles
     *
     * @param path    resource path
     * @param bundles list of bundles {@code Bundle}
     * @return {@code Builder} for the resource
     */
    public static Builder relationsBuilder(Path path, List<Bundle> bundles) {
        DefaultResource.Builder builder = new Builder().fromPath(path);
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                String bundleId = Long.toString(bundle.getBundleId());
                Path bundlePath = BundleResourceManager.getInstance().getPath().addElements(bundleId);
                builder.with(new DefaultRelation(bundlePath, Action.READ, bundleId));
            }
        }
        return builder;
    }

    /**
     * Constructor for bundle resource manager
     */
    public BundleResourceManager() {
        super(BUNDLE_PATH);

        setRelations(
                new DefaultRelation(getPath(), Action.CREATE, BUNDLE_INSTALL_RELATION,
                        new DefaultParameter()
                                .name(INSTALL_LOCATION_PARAMETER)
                                .description(INSTALL_LOCATION_PARAMETER)
                                .optional(false)
                                .type(String.class),
                        new DefaultParameter()
                                .name(INSTALL_INPUT_PARAMETER)
                                .description(INSTALL_INPUT_PARAMETER)
                                .optional(true)
                                .type(ByteArrayInputStream.class))
                ,
                new DefaultRelation(getPath(), Action.UPDATE, BUNDLES_UPDATE_RELATION,
                        new DefaultParameter()
                                .name(REFRESH_PARAMETER)
                                .description("list of long bunde ids to refresh")
                                .optional(true)
                                .type(List.class),
                        new DefaultParameter()
                                .name(RESOLVE_PARAMETER)
                                .description("list of long bundle ids to resolve")
                                .optional(true)
                                .type(List.class)));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (Map.Entry<Long, BundleResource> entry : m_bundleResourcesMap.entrySet()) {
            metadataBuilder.set(entry.getKey().toString(), entry.getValue().getSimpleMetadata());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        synchronized (m_bundleResourcesMap) {
            resources.addAll(m_bundleResourcesMap.values());
        }
        return resources;
    }

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        BundleResource resource = null;
        try {
            Bundle fw = m_bundleResourcesMap.get(0L).getBundle();
            String location = request.get(INSTALL_LOCATION_PARAMETER, String.class);
            if (location != null) {
                InputStream input = request.get(INSTALL_INPUT_PARAMETER, ByteArrayInputStream.class);
                Bundle newBundle = fw.getBundleContext().installBundle(location, input);
                synchronized (m_bundleResourcesMap) {
                    resource = new BundleResource(newBundle, this);
                    m_bundleResourcesMap.put(newBundle.getBundleId(), resource);
                }
                Everest.postResource(ResourceEvent.CREATED, resource);
            }
        } catch (BundleException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
        return resource;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        Resource resource = this;
        try {
            List bundlesToResolve = request.get(RESOLVE_PARAMETER, List.class);

            if (bundlesToResolve != null) {
                this.resolveBundles(bundlesToResolve);
            }
            List bundlesToRefresh = request.get(REFRESH_PARAMETER, List.class);
            if (bundlesToRefresh != null) {
                this.refreshBundles(bundlesToRefresh);
            }
        } catch (Throwable t) {
            throw new IllegalActionOnResourceException(request, t.getMessage());
        }
        return resource;
    }


    public void refreshBundles(List<Long> bundleIds) {
        Bundle fw = m_bundleResourcesMap.get(0L).getBundle();
        FrameworkWiring fwiring = fw.adapt(FrameworkWiring.class);
        List<Bundle> bundlesToRefresh = new ArrayList<Bundle>();
        for (Long bundleId : bundleIds) {
            Bundle bundle = fw.getBundleContext().getBundle(bundleId);
            if (bundle != null) {
                bundlesToRefresh.add(bundle);
            }
        }
        fwiring.refreshBundles(bundlesToRefresh);
    }

    public boolean resolveBundles(List<Long> bundleIds) {
        Bundle fw = m_bundleResourcesMap.get(0L).getBundle();
        FrameworkWiring fwiring = fw.adapt(FrameworkWiring.class);
        List<Bundle> bundlesToResolve = new ArrayList<Bundle>();
        for (Long bundleId : bundleIds) {
            Bundle bundle = fw.getBundleContext().getBundle(bundleId);
            if (bundle != null) {
                bundlesToResolve.add(bundle);
            }
        }
        return fwiring.resolveBundles(bundlesToResolve);
    }


    public List<Long> getDependencyClosure(List<Long> bundleIds) {
        Bundle fw = m_bundleResourcesMap.get(0L).getBundle();
        FrameworkWiring fwiring = fw.adapt(FrameworkWiring.class);
        List<Bundle> bundles = new ArrayList<Bundle>();
        for (long bundleId : bundleIds) {
            Bundle bundle = m_bundleResourcesMap.get(bundleId).getBundle();
            if (bundle != null) {
                bundles.add(bundle);
            }
        }
        Collection<Bundle> dependencyClosure = fwiring.getDependencyClosure(bundles);
        List<Long> dependencyClosureBundles = new ArrayList<Long>();
        for (Bundle b : dependencyClosure) {
            dependencyClosureBundles.add(b.getBundleId());
        }
        return dependencyClosureBundles;
    }

    // Callback redirections from osgi root
    // =================================================================================================================

    public void addBundle(Bundle bundle) {
        BundleResource newBundle = new BundleResource(bundle, this);
        synchronized (m_bundleResourcesMap) {
            m_bundleResourcesMap.put(bundle.getBundleId(), newBundle);
        }
        Everest.postResource(ResourceEvent.CREATED, newBundle);
    }

    public void removeBundle(Bundle bundle) {
        BundleResource removedResource;
        synchronized (m_bundleResourcesMap) {
            removedResource = m_bundleResourcesMap.remove(bundle.getBundleId());
        }
        Everest.postResource(ResourceEvent.DELETED, removedResource);
    }

    public void modifyBundle(Bundle bundle) {
        BundleResource bundleResource;
        synchronized (m_bundleResourcesMap) {
            if (!m_bundleResourcesMap.containsKey(bundle.getBundleId())) {
                bundleResource = new BundleResource(bundle, this);
                m_bundleResourcesMap.put(bundle.getBundleId(), bundleResource);
            } else {
                bundleResource = m_bundleResourcesMap.get(bundle.getBundleId());
                bundleResource.initializeCapabilitiesRequirements();
            }
        }
        Everest.postResource(ResourceEvent.UPDATED, bundleResource);
    }
}
