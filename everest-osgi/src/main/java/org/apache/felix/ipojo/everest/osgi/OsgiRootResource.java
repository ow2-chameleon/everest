package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 9:50 AM
 */
@Component
@Provides   (specifications = Resource.class)
@Instantiate
public class OsgiRootResource extends AbstractResourceManager implements BundleTrackerCustomizer,ServiceTrackerCustomizer {

    public static final String OSGI_ROOT = "osgi";

    public static final Path OSGI_ROOT_PATH = Path.from(Path.SEPARATOR+OSGI_ROOT);

    public static final String OSGI_DESCRIPTION = "This root represents osgi framework and its subresources";

    Object resourceLock;

    private final BundleContext m_context;

    private final BundleTracker m_bundleTracker;

    private final ServiceTracker m_serviceTracker;

    private final BundleResourceManager m_bundleResourceManager;

    private final PackageResourceManager m_packageResourceManager;

    private final ServiceResourceManager m_serviceResourceManager;

    private Resource m_configResourceManager;

    private Resource m_deploymentResourceManager;

    public OsgiRootResource(BundleContext context) {
        super(OSGI_ROOT,OSGI_DESCRIPTION);

        m_context = context;

        // take some metadata from the framework
        //TODO

        // Initialize bundle & service trackers
        int stateMask = Bundle.ACTIVE | Bundle.INSTALLED | Bundle.RESOLVED | Bundle.STARTING | Bundle.STOPPING | Bundle.UNINSTALLED;
        Filter allServicesFilter=null;
        try {
            allServicesFilter = FrameworkUtil.createFilter("(objectClass=*)");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        m_bundleTracker = new BundleTracker(m_context,stateMask, this);
        m_bundleTracker.open();

        m_serviceTracker = new ServiceTracker(m_context,allServicesFilter, this);
        m_serviceTracker.open(true);

        // Initialize subresource managers
        m_bundleResourceManager = new BundleResourceManager();
        m_packageResourceManager = new PackageResourceManager();
        m_serviceResourceManager = new ServiceResourceManager();
    }

    @Override
    public ResourceMetadata getMetadata() {
        return new ImmutableResourceMetadata.Builder()
            //    .set()
            //    .set()
            // metadata to be defined
                .build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(m_bundleResourceManager);
        resources.add(m_packageResourceManager);
        resources.add(m_serviceResourceManager);
        if(m_configResourceManager !=null){
            resources.add(m_configResourceManager);
        }
        if(m_deploymentResourceManager !=null){
            resources.add(m_deploymentResourceManager);
        }
        return resources;
    }

    // Config Admin Bind / Unbind

    @Bind(id="configadmin", optional = true, aggregate = false)
    public void bindConfigAdmin(ConfigurationAdmin configAdmin){
        synchronized (resourceLock){
            m_configResourceManager = new ConfigAdminResourceManager(configAdmin);
        }
    }

    @Unbind(id="configadmin")
    public void unbindConfigAdmin(ConfigurationAdmin configAdmin){
        synchronized (resourceLock){
            //TODO do something to close configadminresourcemanager
        }
    }

    // Deploy Admin Bind / Unbind

    @Bind(id="deploymentadmin", optional = true, aggregate = false)
    public void bindDeploymentAdmin(DeploymentAdmin deploymentAdmin){
        synchronized (resourceLock){
            m_deploymentResourceManager = new DeploymentAdminResourceManager(deploymentAdmin);
        }
    }

    @Unbind(id="deploymentadmin")
    public void unbindDeploymentAdmin(DeploymentAdmin deploymentAdmin){
        synchronized (resourceLock){
            //TODO do something to close deploymentadminresourcemanager
        }
    }

    // Bundle Tracker Methods

    public Object addingBundle(Bundle bundle, BundleEvent bundleEvent) {
           m_bundleResourceManager.addBundle(bundle);
           return bundle.getBundleId();
    }

    public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // Service Tracker Methods

    public Object addingService(ServiceReference serviceReference) {
        String serviceId = (String) serviceReference.getProperty(Constants.SERVICE_ID);
        m_serviceResourceManager.addService(serviceReference);
        return serviceId;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        m_serviceResourceManager.removeService(serviceReference);
    }
}
