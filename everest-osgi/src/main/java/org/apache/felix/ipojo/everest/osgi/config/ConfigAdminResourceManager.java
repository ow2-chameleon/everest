package org.apache.felix.ipojo.everest.osgi.config;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:56 AM
 */
public class ConfigAdminResourceManager extends DefaultResource {

    public static final String CONFIG_ROOT_NAME = "configurations";

    public static final Path CONFIG_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + CONFIG_ROOT_NAME));

    public static final String BUNDLE_LOCATION = "location";

    public static final String PID = "pid";

    public static final String FACTORY_PID = "factoryPid";

    private final ConfigurationAdmin m_configAdmin;

    public ConfigAdminResourceManager(ConfigurationAdmin configAdmin) {
        super(CONFIG_PATH);
        this.m_configAdmin = configAdmin;

        setRelations(new DefaultRelation(getPath(),Action.CREATE,"create",
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
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        try {
            Configuration[] configs = m_configAdmin.listConfigurations(null);
            if (configs != null) {
                for (Configuration cfg : configs) {
                    metadataBuilder.set(cfg.getPid(), null);
                }
            }
        } catch (IOException e) {
            // well, this may happen..
            throw new RuntimeException(e.getMessage());
        } catch (InvalidSyntaxException e) {
            // should never happen..
            throw new RuntimeException(e.getMessage());
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        try {
            Configuration[] configs = m_configAdmin.listConfigurations(null);
            if (configs != null) {
                for (Configuration cfg : configs) {
                    resources.add(new ConfigurationResource(cfg));
                }
            }
        } catch (IOException e) {
            // well, this may happen..
            throw new RuntimeException(e.getMessage());
        } catch (InvalidSyntaxException e) {
            // should never happen..
            throw new RuntimeException(e.getMessage());
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
            } else if (factoryPid!=null){
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
        if(configuration!=null){
            return new ConfigurationResource(configuration);
        }
        return this;
    }

}
