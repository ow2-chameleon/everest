package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;

import java.io.ByteArrayInputStream;
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

    public static final String INSTALL_RELATION_PARAMETER = "input";

    public static final String BUNDLE_INSTALL_RELATION = "install";

    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    private static final String BUNDLE_REFRESH_RELATION = "refresh";

    private static final String REFRESH_PARAMETER = "bundles";

    private Map<Long, BundleResource> bundleResources = new HashMap<Long, BundleResource>();

    private static final BundleResourceManager instance = new BundleResourceManager();

    public static BundleResourceManager getInstance() {
        return instance;
    }

    public BundleResourceManager() {
        super(BUNDLE_PATH);

        setRelations(new DefaultRelation(getPath(), Action.CREATE, BUNDLE_INSTALL_RELATION,
                new DefaultParameter()
                        .name(INSTALL_RELATION_PARAMETER)
                        .description(INSTALL_RELATION_PARAMETER)
                        .optional(false)
                        .type(ByteArrayInputStream.class)),
                new DefaultRelation(getPath(), Action.UPDATE, BUNDLE_REFRESH_RELATION,
                        new DefaultParameter()
                                .name(REFRESH_PARAMETER)
                                .description("")
                                .optional(true)
                                .type(List.class)));
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
    public Resource create(Request request) throws IllegalActionOnResourceException {
        Resource resource = this;
        Object parameter = request.parameters().get(INSTALL_RELATION_PARAMETER);
        if (parameter != null) {

        }
        return resource;
    }

}
