package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.service.cm.ConfigurationAdmin;

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

    public ConfigAdminResourceManager(ConfigurationAdmin configAdmin) {
        super(CONFIG_PATH);
    }
}
