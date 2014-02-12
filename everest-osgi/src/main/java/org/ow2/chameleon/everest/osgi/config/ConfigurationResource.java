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

package org.ow2.chameleon.everest.osgi.config;

import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

import static org.ow2.chameleon.everest.osgi.config.ConfigAdminResourceManager.CONFIG_PATH;
import static org.osgi.service.cm.ConfigurationAdmin.SERVICE_FACTORYPID;


/**
 * Resource representing a {@code Configuration}.
 */
public class ConfigurationResource extends DefaultResource<Object> {

    /**
     * Relation name for update
     */
    private static final String UPDATE_RELATION = "update";

    /**
     * Relation name for delete
     */
    private static final String DELETE_RELATION = "delete";

    /**
     * Parameter name for properties
     */
    public static final String CONFIG_PROPERTIES_PARAMETER = "properties";

    /**
     * Parameter name for bundle location
     */
    public static final String CONFIG_LOCATION_PARAMETER = "location";

    /**
     * Represented configuration
     */
    private final Configuration m_configuration;

    /**
     * Constructor for Configuration resource
     *
     * @param configuration {@code Configuration}
     */
    public ConfigurationResource(Configuration configuration) {
        super(CONFIG_PATH.addElements(configuration.getPid()));
        this.m_configuration = configuration;
        setRelations(new DefaultRelation(getPath(), Action.UPDATE, UPDATE_RELATION,
                new DefaultParameter()
                        .name(CONFIG_PROPERTIES_PARAMETER)
                        .description("properties")
                        .optional(true)
                        .type(Dictionary.class),
                new DefaultParameter()
                        .name(CONFIG_LOCATION_PARAMETER)
                        .description("bundle location")
                        .optional(true)
                        .type(String.class)
        ),
                new DefaultRelation(getPath(), Action.DELETE, DELETE_RELATION));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(Constants.SERVICE_PID, m_configuration.getPid());
        metadataBuilder.set(CONFIG_LOCATION_PARAMETER, m_configuration.getBundleLocation());
        if (m_configuration.getFactoryPid() != null) {
            metadataBuilder.set(SERVICE_FACTORYPID, m_configuration.getFactoryPid());
        }
        Dictionary<String, Object> properties = m_configuration.getProperties();
        if (properties != null) {
            Enumeration<String> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                metadataBuilder.set(key, properties.get(key));
            }
        }
        return metadataBuilder.build();
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        try {
            Dictionary properties = request.get(CONFIG_PROPERTIES_PARAMETER, Dictionary.class);
            this.update(properties);
            String bundleLocation = request.get(CONFIG_LOCATION_PARAMETER, String.class);
            if (bundleLocation != null) {
                if (!m_configuration.getBundleLocation().equals(bundleLocation)) {
                    m_configuration.setBundleLocation(bundleLocation);
                }
            }
            return this;
        } catch (IllegalActionOnResourceException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        try {
            m_configuration.delete();
            // TODO should return some empty resource
            return this;
        } catch (IOException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (Configuration.class.equals(clazz)) {
            return (A) m_configuration;
        } else if (ConfigurationResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public Dictionary getProperties() {
        return m_configuration.getProperties();
    }

    public String getPid() {
        return m_configuration.getPid();
    }

    public String getFactoryPid() {
        return m_configuration.getFactoryPid();
    }

    public String getBundleLocation() {
        return m_configuration.getBundleLocation();
    }

    public void setBundleLocation(String bundleLocation) {
        m_configuration.setBundleLocation(bundleLocation);
    }

    public void update(Dictionary properties) throws IllegalActionOnResourceException {
        try {
            if (properties != null) {
                m_configuration.update(properties);
            } else {
                m_configuration.update();
            }
        } catch (IOException e) {
            throw new IllegalActionOnResourceException(null, e.getMessage());
        }
    }

}
