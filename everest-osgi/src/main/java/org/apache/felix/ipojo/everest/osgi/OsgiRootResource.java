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
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 9:50 AM
 */
@Component
@Provides(specifications = Resource.class)
@Instantiate
public class OsgiRootResource extends AbstractResourceManager implements BundleTrackerCustomizer, ServiceTrackerCustomizer {

    public static final String OSGI_ROOT = "osgi";

    public static final Path OSGI_ROOT_PATH = Path.from(Path.SEPARATOR + OSGI_ROOT);

    public static final String OSGI_DESCRIPTION = "This root represents osgi framework and its subresources";

    private Object resourceLock;

    private final BundleContext m_context;

    private final BundleTracker m_bundleTracker;

    private final ServiceTracker m_serviceTracker;

    private final BundleResourceManager m_bundleResourceManager;

    private final PackageResourceManager m_packageResourceManager;

    private final ServiceResourceManager m_serviceResourceManager;

    private ConfigAdminResourceManager m_configResourceManager;

    private DeploymentAdminResourceManager m_deploymentResourceManager;

    private LogServiceResourceManager m_logResourceManager;

    public OsgiRootResource(BundleContext context) {
        super(OSGI_ROOT, OSGI_DESCRIPTION);

        m_context = context;

        //TODO take some metadata from the framework
        Bundle framework = m_context.getBundle(0);


        // Initialize bundle & service trackers
        int stateMask = Bundle.ACTIVE | Bundle.INSTALLED | Bundle.RESOLVED | Bundle.STARTING | Bundle.STOPPING | Bundle.UNINSTALLED;
        Filter allServicesFilter = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(Constants.OBJECTCLASS);
            sb.append("=*)");
            allServicesFilter = FrameworkUtil.createFilter(sb.toString());
        } catch (InvalidSyntaxException e) {
            // Should never happen
            throw new RuntimeException(e.getMessage());
        }

        m_bundleTracker = new BundleTracker(m_context, stateMask, this);
        m_bundleTracker.open();

        m_serviceTracker = new ServiceTracker(m_context, allServicesFilter, this);
        m_serviceTracker.open(true);

        // Initialize subresource managers
        m_bundleResourceManager = BundleResourceManager.getInstance();
        m_packageResourceManager = PackageResourceManager.getInstance();
        m_serviceResourceManager = ServiceResourceManager.getInstance();
    }

    @Invalidate
    public void stopped() {

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
        synchronized (resourceLock) {
            resources.add(m_bundleResourceManager);
            resources.add(m_packageResourceManager);
            resources.add(m_serviceResourceManager);
            if (m_configResourceManager != null) {
                resources.add(m_configResourceManager);
            }
            if (m_deploymentResourceManager != null) {
                resources.add(m_deploymentResourceManager);
            }
            if (m_logResourceManager != null) {
                resources.add(m_logResourceManager);
            }
        }
        return resources;
    }

    // Config Admin Bind / Unbind

    @Bind(id = "configadmin", optional = true, aggregate = false)
    public void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        synchronized (resourceLock) {
            m_configResourceManager = new ConfigAdminResourceManager(configAdmin);
        }
    }

    @Unbind(id = "configadmin")
    public void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        synchronized (resourceLock) {
            //TODO do something to close configadminresourcemanager
        }
    }

    // Deploy Admin Bind / Unbind

    @Bind(id = "deploymentadmin", optional = true, aggregate = false)
    public void bindDeploymentAdmin(DeploymentAdmin deploymentAdmin) {
        synchronized (resourceLock) {
            m_deploymentResourceManager = new DeploymentAdminResourceManager(deploymentAdmin);
        }
    }

    @Unbind(id = "deploymentadmin")
    public void unbindDeploymentAdmin(DeploymentAdmin deploymentAdmin) {
        synchronized (resourceLock) {
            //TODO do something to close deploymentadminresourcemanager
        }
    }

    // Log Service Bind / Unbind

    @Bind(id = "logservice")
    public void bindLogService(LogService logService) {
        synchronized (resourceLock) {
            m_logResourceManager = new LogServiceResourceManager(logService);
        }
    }

    @Unbind(id = "logservice")
    public void unbindLogService(LogService logService) {
        synchronized (resourceLock) {
            //TODO do something to close logresourcemanager
        }
    }

    // Bundle Tracker Methods

    public Object addingBundle(Bundle bundle, BundleEvent bundleEvent) {
        m_bundleResourceManager.addBundle(bundle);
        return bundle.getBundleId();
    }

    public void modifiedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        // TODO think of sending some event
        if (bundleEvent.getType() == BundleEvent.RESOLVED) {
            m_packageResourceManager.addPackagesFrom(bundle);
        }
        if (bundleEvent.getType() == BundleEvent.UNRESOLVED) {
            m_packageResourceManager.removePackagesFrom(bundle);
        }
    }

    public void removedBundle(Bundle bundle, BundleEvent bundleEvent, Object o) {
        m_bundleResourceManager.removeBundle(bundle);
    }

    // Service Tracker Methods

    public Object addingService(ServiceReference serviceReference) {
        String serviceId = (String) serviceReference.getProperty(Constants.SERVICE_ID);
        m_serviceResourceManager.addService(serviceReference);
        return serviceId;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        // TODO think of sending some event
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        m_serviceResourceManager.removeService(serviceReference);
    }
}
