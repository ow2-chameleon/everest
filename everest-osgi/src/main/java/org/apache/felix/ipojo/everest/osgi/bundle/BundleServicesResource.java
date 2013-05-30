package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.service.ServiceResourceManager;
import org.apache.felix.ipojo.everest.services.IllegalResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 4:13 PM
 */
public class BundleServicesResource extends AbstractResourceCollection {

    public static final String BUNDLE_SERVICES_NAME = "services";

    public static final String BUNDLE_REGISTERED_SERVICES_NAME = "registered";

    public static final String BUNDLE_USE_SERVICES_NAME = "uses";

    private final Bundle m_bundle;

    public BundleServicesResource(Path path, Bundle bundle) {
        super(path.addElements(BUNDLE_SERVICES_NAME));
        m_bundle = bundle;
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        DefaultResource.Builder builder;
        // bundle registered services
        ServiceReference[] registered = m_bundle.getRegisteredServices();
        if (registered != null) {
            builder = ServiceResourceManager.relationsBuilder(getPath().addElements(BUNDLE_REGISTERED_SERVICES_NAME), Arrays.asList(registered));
            try {
                resources.add(builder.build());
            } catch (IllegalResourceException e) {
                // should never happen
            }
        }

        // bundle used services
        ServiceReference[] uses = m_bundle.getServicesInUse();
        if (uses != null) {
            builder = ServiceResourceManager.relationsBuilder(getPath().addElements(BUNDLE_USE_SERVICES_NAME), Arrays.asList(uses));
            try {
                resources.add(builder.build());
            } catch (IllegalResourceException e) {
                // should never happen
            }
        }
        return resources;
    }

}
