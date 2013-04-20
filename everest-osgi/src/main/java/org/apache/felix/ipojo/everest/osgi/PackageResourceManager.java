package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:54 AM
 */
public class PackageResourceManager extends DefaultReadOnlyResource {

    public static final String PACKAGE_ROOT_NAME = "packages";

    public static final Path PACKAGE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + PACKAGE_ROOT_NAME));

    private Map<Long,PackageResource> packageResourceByBundleIdMap = new HashMap<Long, PackageResource>();

    // For indexing packages
    // private Map<String, PackageResource> packageResourceByPackageIdMap = new HashMap<String, PackageResource>();

    public PackageResourceManager(){
        super(PACKAGE_PATH);
    }


    public void addPackagesFrom(Bundle bundle){

    }

    public void removePackagesFrom(Bundle bundle){

    }
}
