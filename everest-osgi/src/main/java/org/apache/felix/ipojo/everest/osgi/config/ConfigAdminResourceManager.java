package org.apache.felix.ipojo.everest.osgi.config;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource manager for Configuration Admin service.
 */
public class ConfigAdminResourceManager extends AbstractResourceCollection implements ConfigurationListener {

    /**
     * Name for configurations resource
     */
    public static final String CONFIG_ROOT_NAME = "configurations";

    /**
     * Path for this configurations resource : "/osgi/configurations"
     */
    public static final Path CONFIG_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + CONFIG_ROOT_NAME));

    /**
     * Location property name
     */
    public static final String BUNDLE_LOCATION = "location";

    /**
     * Pid property name
     */
    public static final String PID = "pid";

    /**
     * Factory pid property name
     */
    public static final String FACTORY_PID = "factoryPid";

    /**
     * Config admin service
     */
    private final ConfigurationAdmin m_configAdmin;

    /**
     * Configurations map by pid
     */
    private Map<String, ConfigurationResource> m_configurationResourceMap = new HashMap<String, ConfigurationResource>();

    /**
     * Constructor for configuration resource manager
     *
     * @param configAdmin {@code ConfigurationAdmin}
     */
    public ConfigAdminResourceManager(ConfigurationAdmin configAdmin) {
        super(CONFIG_PATH);
        this.m_configAdmin = configAdmin;

        setRelations(new DefaultRelation(getPath(), Action.CREATE, "create",
                new DefaultParameter()
                        .name(BUNDLE_LOCATION)
                        .description("bundle location")
                        .type(String.class)
                        .optional(false),
                new DefaultParameter()
                        .name(PID)
                        .description("Persistent id of configuration")
                        .type(String.class)
                        .optional(true),
                new DefaultParameter()
                        .name(FACTORY_PID)
                        .description("Persistent id of factory pid")
                        .type(String.class)
                        .optional(true)));

        try {
            Configuration[] configs = m_configAdmin.listConfigurations(null);
            if (configs != null) {
                for (Configuration cfg : configs) {
                    m_configurationResourceMap.put(cfg.getPid(), new ConfigurationResource(cfg));
                }
            }
        } catch (IOException e) {
            // well, this may happen..
            throw new RuntimeException(e.getMessage());
        } catch (InvalidSyntaxException e) {
            // should never happen..
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Override
//    public ResourceMetadata getMetadata() {
//        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
//        for (ConfigurationResource configurationResource : m_configurationResourceMap.values()) {
//            metadataBuilder.set(configurationResource.getPid(),null);
//        }
//        return metadataBuilder.build();
//    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        synchronized (m_configurationResourceMap) {
            resources.addAll(m_configurationResourceMap.values());
        }
        return resources;

    }

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        String pid = request.get(PID, String.class);
        String factoryPid = request.get(FACTORY_PID, String.class);
        Configuration configuration = null;
        String location = request.get(BUNDLE_LOCATION, String.class);
        if (location != null) {
            if (pid != null) {
                try {
                    configuration = m_configAdmin.getConfiguration(pid, location);
                } catch (IOException e) {
                    throw new IllegalActionOnResourceException(request, e.getMessage());
                }
            } else if (factoryPid != null) {
                try {
                    configuration = m_configAdmin.createFactoryConfiguration(factoryPid, location);
                } catch (IOException e) {
                    throw new IllegalActionOnResourceException(request, e.getMessage());
                }
            } else {
                throw new IllegalActionOnResourceException(request, "factory pid or pid parameter is mandatory");
            }

        } else {
            throw new IllegalActionOnResourceException(request, "location parameter is mandatory");
        }
        ConfigurationResource configurationResource = new ConfigurationResource(configuration);
        synchronized (m_configurationResourceMap) {
            m_configurationResourceMap.put(pid, configurationResource);
        }
        return configurationResource;
    }

    public void configurationEvent(ConfigurationEvent event) {
        String pid = event.getPid();
        ResourceEvent resourceEvent;
        ConfigurationResource configurationResource;
        try {
            Configuration configuration = m_configAdmin.getConfiguration(pid);
            if (!m_configurationResourceMap.containsKey(pid)) {
                configurationResource = new ConfigurationResource(configuration);
                synchronized (m_configurationResourceMap) {
                    m_configurationResourceMap.put(pid, configurationResource);
                }
                resourceEvent = ResourceEvent.CREATED;
            } else {
                synchronized (m_configurationResourceMap) {
                    configurationResource = m_configurationResourceMap.get(pid);
                }
                if (event.getType() != ConfigurationEvent.CM_DELETED) {
                    resourceEvent = ResourceEvent.UPDATED;
                } else {
                    resourceEvent = ResourceEvent.DELETED;
                }
            }
            Everest.postResource(ResourceEvent.UPDATED, configurationResource);
        } catch (IOException e) {
            // something gone wrong
            //TODO
        }

    }
}
