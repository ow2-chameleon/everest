package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;

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

    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    private Map<Long, BundleResource> bundleResources = new HashMap<Long, BundleResource>();

    private static final BundleResourceManager instance = new BundleResourceManager();

    public static BundleResourceManager getInstance() {
        return instance;
    }

    public BundleResourceManager() {
        super(BUNDLE_PATH);
    }


    public void addBundle(Bundle bundle) {
        synchronized (bundleResources) {
            BundleResource newBundle = new BundleResource(bundle);
            bundleResources.put(bundle.getBundleId(), newBundle);
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
    public Resource put(Request request) throws IllegalActionOnResourceException {
        // TODO install bundle
        return super.put(request);
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        return super.delete(request);
    }
}
