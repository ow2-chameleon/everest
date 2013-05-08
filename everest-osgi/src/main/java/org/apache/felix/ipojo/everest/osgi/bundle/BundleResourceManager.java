package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:52 AM
 */
public class BundleResourceManager extends DefaultResource {

    public static final String BUNDLE_ROOT_NAME = "bundles";

    public static final String INSTALL_INPUT_PARAMETER = "input";

    private static final String INSTALL_LOCATION_PARAMETER = "location";

    public static final String BUNDLE_INSTALL_RELATION = "install";

    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    private static final String BUNDLES_UPDATE_RELATION = "update";

    private static final String REFRESH_PARAMETER = "refresh";

    private static final String RESOLVE_PARAMETER = "resolve";


    private Map<Long, BundleResource> bundleResources = new HashMap<Long, BundleResource>();

    private static final BundleResourceManager instance = new BundleResourceManager();

    public static BundleResourceManager getInstance() {
        return instance;
    }

    public BundleResourceManager() {
        super(BUNDLE_PATH);

        setRelations(new DefaultRelation(getPath(), Action.CREATE, BUNDLE_INSTALL_RELATION,
                new DefaultParameter()
                        .name(INSTALL_LOCATION_PARAMETER)
                        .description(INSTALL_LOCATION_PARAMETER)
                        .optional(false)
                        .type(String.class),
                new DefaultParameter()
                        .name(INSTALL_INPUT_PARAMETER)
                        .description(INSTALL_INPUT_PARAMETER)
                        .optional(true)
                        .type(ByteArrayInputStream.class)),
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


    public void addBundle(Bundle bundle) {
        synchronized (bundleResources) {
            if (!bundleResources.containsKey(bundle.getBundleId())) {
                BundleResource newBundle = new BundleResource(bundle, this);
                bundleResources.put(bundle.getBundleId(), newBundle);
            }
        }
    }

    public void removeBundle(Bundle bundle) {
        synchronized (bundleResources) {
            bundleResources.remove(bundle.getBundleId());
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (Entry<Long, BundleResource> entry : bundleResources.entrySet()) {
            metadataBuilder.set(entry.getKey().toString(), entry.getValue().getSimpleMetadata());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.addAll(bundleResources.values());
        return resources;
    }

    // TODO add relations install uninstall

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        BundleResource resource = null;
        try {
            Bundle fw = bundleResources.get(0L).getBundle();
            String location = request.get(INSTALL_LOCATION_PARAMETER, String.class);
            if (location != null) {
                InputStream input = request.get(INSTALL_INPUT_PARAMETER, ByteArrayInputStream.class);
                Bundle newBundle = fw.getBundleContext().installBundle(location, input);
                synchronized (bundleResources) {
                    resource = new BundleResource(newBundle, this);
                    bundleResources.put(newBundle.getBundleId(), resource);
                }
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

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        return super.delete(request);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void refreshBundles(List<Long> bundleIds) {
        Bundle fw = bundleResources.get(0L).getBundle();
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
        Bundle fw = bundleResources.get(0L).getBundle();
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
}
