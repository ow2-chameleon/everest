package org.apache.felix.ipojo.everest.osgi.config;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

import static org.apache.felix.ipojo.everest.osgi.config.ConfigAdminResourceManager.CONFIG_PATH;
import static org.osgi.service.cm.ConfigurationAdmin.SERVICE_BUNDLELOCATION;
import static org.osgi.service.cm.ConfigurationAdmin.SERVICE_FACTORYPID;


/**
 * Resource representing a {@code Configuration}.
 */
public class ConfigurationResource extends DefaultResource {

    /**
     * Relation name for update
     */
    private static final String UPDATE_RELATION = "update";

    /**
     * Relation name for delete
     */
    private static final String DELETE_RELATION = "delete";

    /**
     * Property name for properties
     */
    public static final String CONFIG_PROPERTIES = "properties";

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
                        .name(CONFIG_PROPERTIES)
                        .description("properties")
                        .optional(true)
                        .type(Dictionary.class),
                new DefaultParameter()
                        .name(SERVICE_BUNDLELOCATION)
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
        metadataBuilder.set(SERVICE_BUNDLELOCATION, m_configuration.getBundleLocation());
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
            Dictionary properties = request.get(CONFIG_PROPERTIES, Dictionary.class);
            this.update(properties);
            String bundleLocation = request.get(SERVICE_BUNDLELOCATION, String.class);
            if (bundleLocation != null) {
                m_configuration.setBundleLocation(bundleLocation);
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
