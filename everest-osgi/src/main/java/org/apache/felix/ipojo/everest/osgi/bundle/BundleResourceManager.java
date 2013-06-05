package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:52 AM
 */
public class BundleResourceManager extends AbstractResourceCollection {

    public static final String BUNDLE_ROOT_NAME = "bundles";

    public static final String INSTALL_INPUT_PARAMETER = "input";

    public static final String INSTALL_LOCATION_PARAMETER = "location";

    public static final String BUNDLE_INSTALL_RELATION = "install";

    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    public static final String BUNDLES_UPDATE_RELATION = "update";

    public static final String REFRESH_PARAMETER = "refresh";

    public static final String RESOLVE_PARAMETER = "resolve";

    private Map<Long, BundleResource> m_bundleResourcesMap = new HashMap<Long, BundleResource>();

    private static final BundleResourceManager instance = new BundleResourceManager();

    public static BundleResourceManager getInstance() {
        return instance;
    }

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

    // bundle listener delegate methods

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
}
