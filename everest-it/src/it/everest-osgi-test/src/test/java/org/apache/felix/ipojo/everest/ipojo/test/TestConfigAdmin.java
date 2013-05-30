package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.osgi.bundle.BundleResource;
import org.apache.felix.ipojo.everest.osgi.config.ConfigurationResource;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/30/13
 * Time: 12:44 AM
 */
public class TestConfigAdmin extends Common {

    @Test
    public void testExistingConfigurations() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource configAdmin = get("/osgi/configurations");
        for (Resource config : configAdmin.getResources()) {
            ConfigurationResource configurationResource = config.adaptTo(ConfigurationResource.class);
            System.out.println(configurationResource.getPid());
        }

    }

    @Test
    public void testConfigurationCreationAndUpdate() throws ResourceNotFoundException, IllegalActionOnResourceException {
        String location = null;
        Resource bundles = get("/osgi/bundles");
        for (Resource bundle : bundles.getResources()) {
            BundleResource bundleResource = bundle.adaptTo(BundleResource.class);
            if (bundleResource.getSymbolicName().equals("test.bundle")) {
                location = bundleResource.getLocation();
            }
        }
        Resource configAdmin = get("/osgi/configurations");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pid", "ozan");
        params.put("location", location);
        Resource configuration = create(Path.from("/osgi/configurations"), params);
        ConfigurationResource cfgRes = configuration.adaptTo(ConfigurationResource.class);
        System.out.println(cfgRes.getMetadata());
        for (Resource config : configAdmin.getResources()) {
            ConfigurationResource configurationResource = config.adaptTo(ConfigurationResource.class);
            System.out.println(configurationResource.getPid());
        }

        params = new HashMap<String, Object>();
        Dictionary properties = new Hashtable();
        properties.put("property", "value");
        params.put("properties", properties);
        Resource config = update(Path.from("/osgi/configurations/ozan"), params);

        ConfigurationResource configurationResource = config.adaptTo(ConfigurationResource.class);
        Dictionary props = configurationResource.getProperties();
        assertThat(props.get("property")).isEqualTo("value");


    }

}
