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

import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.osgi.service.ServiceResourceManager;
import org.ow2.chameleon.everest.services.IllegalResourceException;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resource representing services of a bundle.
 */
public class BundleServicesResource extends AbstractResourceCollection {

    /**
     * Name for services
     */
    public static final String BUNDLE_SERVICES_NAME = "services";

    /**
     * Relation name for registered services
     */
    public static final String BUNDLE_REGISTERED_SERVICES_NAME = "registered";

    /**
     * Relation name for services used by this bundle
     */
    public static final String BUNDLE_USE_SERVICES_NAME = "uses";

    /**
     * Concerned bundle
     */
    private final Bundle m_bundle;

    /**
     * Constructor for bundle services resource
     *
     * @param path
     * @param bundle
     */
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
