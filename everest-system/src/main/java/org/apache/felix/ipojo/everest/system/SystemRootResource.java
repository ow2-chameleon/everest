package org.apache.felix.ipojo.everest.system;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.system.mx.MXResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:37 PM
 */
@Component
@Provides(specifications = Resource.class)
@Instantiate
public class SystemRootResource extends AbstractResourceManager {

    public static final String SYSTEM_ROOT = "system";
    public static final Path SYSTEM_ROOT_PATH = Path.from(Path.SEPARATOR + SYSTEM_ROOT);

    private static final String SYSTEM_DESCRIPTION = "system resources";
    private final List<Resource> systemResources = new ArrayList<Resource>();

    public SystemRootResource() {
        super(SYSTEM_ROOT, SYSTEM_DESCRIPTION);
        systemResources.add(new SystemPropertiesResource());
        systemResources.add(new EnvironmentPropertiesResource());
        systemResources.add(new MXResourceManager());
    }

    @Override
    public List<Resource> getResources() {
        return systemResources;
    }
}
