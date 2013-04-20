package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:52 AM
 */
public class BundleResourceManager extends DefaultResource {

    public static final String BUNDLE_ROOT_NAME = "bundles";

    public static final Path BUNDLE_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + BUNDLE_ROOT_NAME));

    private Map<Long,BundleResource> bundleResources = new HashMap<Long, BundleResource>();

    public BundleResourceManager(){
        super(BUNDLE_PATH);
    }


    public void addBundle(Bundle bundle){
        synchronized (bundleResources){
            BundleResource newBundle = new BundleResource(bundle);
            bundleResources.put(bundle.getBundleId(),newBundle);
        }
    }

    public void removeBundle(Bundle bundle){
        synchronized (bundleResources){
            bundleResources.remove(bundle.getBundleId());
        }
    }

    @Override
    public List<Resource> getResources() {
        return super.getResources();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
