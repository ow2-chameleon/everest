package org.apache.felix.ipojo.everest.osgi.system;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:37 PM
 */
public class SystemResourceManager extends DefaultResource {

    public static final String SYSTEM_ROOT_NAME = "system";

    public static final Path SYSTEM_ROOT_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + SYSTEM_ROOT_NAME));


    private static final SystemResourceManager instance = new SystemResourceManager();

    public static SystemResourceManager getInstance() {
        return instance;
    }

    public SystemResourceManager() {
        super(SYSTEM_ROOT_PATH);

    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new SystemPropertiesResource());
        resources.add(new EnvironmentPropertiesResource());
        return resources;
    }
}
