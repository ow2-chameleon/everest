package org.apache.felix.ipojo.everest.osgi;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:54 AM
 */
public class PackageResourceManager extends DefaultReadOnlyResource {

    public static final String PACKAGE_ROOT_NAME = "packages";

    public static final Path PACKAGE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + PACKAGE_ROOT_NAME));

    private Map<Long,Bundle> bundles = new HashMap<Long, Bundle>();

    public PackageResourceManager(){
        super(PACKAGE_PATH);
    }



}
