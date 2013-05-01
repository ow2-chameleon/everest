package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResourceManager;
import org.apache.felix.ipojo.everest.osgi.config.ConfigAdminResourceManager;
import org.apache.felix.ipojo.everest.osgi.deploy.DeploymentAdminResourceManager;
import org.apache.felix.ipojo.everest.osgi.log.LogServiceResourceManager;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResourceManager;
import org.apache.felix.ipojo.everest.osgi.service.ServiceResourceManager;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.*;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.log.LogReaderService;
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

    public static final String FRAMEWORK_STOP_RELATION = "stop";

    private final Object resourceLock = new Object();

    private final BundleContext m_context;

    private final BundleTracker m_bundleTracker;

    private final ServiceTracker m_serviceTracker;

    private final BundleResourceManager m_bundleResourceManager;

    private final PackageResourceManager m_packageResourceManager;

    private final ServiceResourceManager m_serviceResourceManager;

    private final ResourceMetadata m_metadata;

    private final FrameworkWiring fwiring;

    private ConfigAdminResourceManager m_configResourceManager;

    private DeploymentAdminResourceManager m_deploymentResourceManager;

    private LogServiceResourceManager m_logResourceManager;

    public OsgiRootResource(BundleContext context) {
        super(OSGI_ROOT, OSGI_DESCRIPTION);

        m_context = context;
        // Initialize subresource managers
        m_bundleResourceManager = BundleResourceManager.getInstance();
        m_packageResourceManager = PackageResourceManager.getInstance();
        m_serviceResourceManager = ServiceResourceManager.getInstance();
        // Construct framework metadata
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        Bundle fw = m_context.getBundle(0);
        fwiring = fw.adapt(FrameworkWiring.class);
        //TODO take some metadata from the framework
        BundleContext fwContext = fw.getBundleContext();
        metadataBuilder.set(Constants.FRAMEWORK_VERSION, fwContext.getProperty(Constants.FRAMEWORK_VERSION));
        metadataBuilder.set(Constants.FRAMEWORK_VENDOR, fwContext.getProperty(Constants.FRAMEWORK_VENDOR));
        metadataBuilder.set(Constants.FRAMEWORK_LANGUAGE, fwContext.getProperty(Constants.FRAMEWORK_LANGUAGE));
        metadataBuilder.set(Constants.FRAMEWORK_PROCESSOR, fwContext.getProperty(Constants.FRAMEWORK_PROCESSOR));
        metadataBuilder.set(Constants.FRAMEWORK_OS_NAME, fwContext.getProperty(Constants.FRAMEWORK_OS_NAME));
        metadataBuilder.set(Constants.FRAMEWORK_OS_VERSION, fwContext.getProperty(Constants.FRAMEWORK_OS_VERSION));
        metadataBuilder.set(Constants.FRAMEWORK_UUID, fwContext.getProperty(Constants.FRAMEWORK_UUID));
        metadataBuilder.set(Constants.SUPPORTS_FRAMEWORK_EXTENSION, fwContext.getProperty(Constants.SUPPORTS_FRAMEWORK_EXTENSION));
        metadataBuilder.set(Constants.SUPPORTS_FRAMEWORK_FRAGMENT, fwContext.getProperty(Constants.SUPPORTS_FRAMEWORK_FRAGMENT));
        metadataBuilder.set(Constants.SUPPORTS_FRAMEWORK_REQUIREBUNDLE, fwContext.getProperty(Constants.SUPPORTS_FRAMEWORK_REQUIREBUNDLE));
        metadataBuilder.set(Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION, fwContext.getProperty(Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION));
        metadataBuilder.set(Constants.FRAMEWORK_BOOTDELEGATION, fwContext.getProperty(Constants.FRAMEWORK_BOOTDELEGATION));
        metadataBuilder.set(Constants.FRAMEWORK_SYSTEMPACKAGES, fwContext.getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES));

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
        m_serviceTracker = new ServiceTracker(m_context, allServicesFilter, this);

        m_metadata = metadataBuilder.build();

        setRelations(
                new DefaultRelation(getPath(), Action.DELETE, FRAMEWORK_STOP_RELATION, "Stops the osgi framework")
        );
    }

    @Validate
    public void started() {
        // start trackers
        m_bundleTracker.open();
        m_serviceTracker.open(true);
    }

    @Invalidate
    public void stopped() {
        // stop trackers
        m_bundleTracker.close();
        m_serviceTracker.close();
    }

    @Override
    public ResourceMetadata getMetadata() {
        return m_metadata;
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

    @Bind(id = "logservice", optional = true, aggregate = false)
    public void bindLogService(LogReaderService logService) {
        synchronized (resourceLock) {
            m_logResourceManager = new LogServiceResourceManager(logService);
        }
    }

    @Unbind(id = "logservice")
    public void unbindLogService(LogReaderService logService) {
        synchronized (resourceLock) {
            //TODO do something to close logresourcemanager
        }
    }

    // Bundle Tracker Methods

    public Object addingBundle(Bundle bundle, BundleEvent bundleEvent) {
        m_bundleResourceManager.addBundle(bundle);
        if (bundle.getState() == Bundle.ACTIVE || bundle.getState() == Bundle.RESOLVED) {
            m_packageResourceManager.addPackagesFrom(bundle);
        }
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
        Object serviceId = serviceReference.getProperty(Constants.SERVICE_ID);
        m_serviceResourceManager.addService(serviceReference);
        return serviceId;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        // TODO think of sending some event
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        m_serviceResourceManager.removeService(serviceReference);
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {

        return this;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        // R.I.P :REST in PEACE
        try {
            fwiring.getBundle().stop();
        } catch (BundleException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
        return null;
    }

}
