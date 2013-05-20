package org.apache.felix.ipojo.everest.osgi.config;

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

    private final ConfigurationAdmin m_configAdmin;

    public ConfigAdminResourceManager(ConfigurationAdmin configAdmin) {
        super(CONFIG_PATH);
        this.m_configAdmin = configAdmin;
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
        String pid = request.get("pid", String.class);
        Configuration configuration = null;
        if (pid != null) {
            String location = request.get("location", String.class);
            try {
                if (location != null) {
                    configuration = m_configAdmin.getConfiguration(pid, location);
                } else {
                    configuration = m_configAdmin.getConfiguration(pid);
                }
            } catch (IOException e) {
                throw new IllegalActionOnResourceException(request, e.getMessage());
            }
        }
        return this;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {

        return this;
    }
}
