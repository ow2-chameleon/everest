package org.apache.felix.ipojo.everest.osgi.service;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource Manager for services.
 */
public class ServiceResourceManager extends AbstractResourceCollection {

    /**
     * Name for the services resource
     */
    public static final String SERVICE_ROOT_NAME = "services";

    /**
     * Path to osgi services : "/osgi/services"
     */
    public static final Path SERVICES_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + SERVICE_ROOT_NAME));

    /**
     * Map of service resources by their {@code org.osgi.framework.Constants.SERVICE_ID}
     */
    private Map<Object, ServiceResource> m_serviceResourceMap = new HashMap<Object, ServiceResource>();

    /**
     * Static instance of this singleton class
     */
    private static final ServiceResourceManager instance = new ServiceResourceManager();

    /**
     * Getter of the static instance of this singleton class
     *
     * @return the singleton static instance
     */
    public static ServiceResourceManager getInstance() {
        return instance;
    }

    /**
     * An utility method for creating a resource that contains only relations to a list osgi services
     *
     * @param path     resource path
     * @param services list of {@code ServiceReference} for services
     * @return {@code Builder} for the resource
     */
    public static Builder relationsBuilder(Path path, List<ServiceReference> services) {
        DefaultResource.Builder builder = new Builder().fromPath(path);
        for (ServiceReference service : services) {
            if (service != null) {
                String serviceId = service.getProperty(Constants.SERVICE_ID).toString();
                Path servicePath = ServiceResourceManager.getInstance().getPath().addElements(serviceId);
                builder.with(new DefaultRelation(servicePath, Action.READ, serviceId));
            }
        }
        return builder;
    }

    /**
     * Constructor for service resource manager
     */
    public ServiceResourceManager() {
        super(SERVICES_PATH);
    }

    //    @Override
//    public ResourceMetadata getMetadata() {
//        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
//        synchronized (m_serviceResourceMap) {
//            for (Entry<Object, ServiceResource> e : m_serviceResourceMap.entrySet()) {
//                metadataBuilder.set(e.getKey().toString(), e.getValue().getSimpleMetadata());
//            }
//        }
//        return metadataBuilder.build();
//    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        synchronized (m_serviceResourceMap) {
            resources.addAll(m_serviceResourceMap.values());
        }
        return resources;
    }

    // Callback redirections from Osgi Root
    // =================================================================================================================

    public void addService(ServiceReference serviceReference) {
        Object serviceId = serviceReference.getProperty(Constants.SERVICE_ID);
        ServiceResource createdService;
        synchronized (m_serviceResourceMap) {
            createdService = new ServiceResource(serviceReference);
            m_serviceResourceMap.put(serviceId, createdService);
        }
        Everest.postResource(ResourceEvent.CREATED, createdService);
    }

    public void modifyService(ServiceReference serviceReference) {
        Object serviceId = serviceReference.getProperty(Constants.SERVICE_ID);
        ServiceResource updatedService;
        synchronized (m_serviceResourceMap) {
            if (!m_serviceResourceMap.containsKey(serviceId)) {
                updatedService = new ServiceResource(serviceReference);
                m_serviceResourceMap.put(serviceId, updatedService);
            } else {
                updatedService = m_serviceResourceMap.get(serviceId);
            }
        }
        Everest.postResource(ResourceEvent.UPDATED, updatedService);
    }

    public void removeService(ServiceReference serviceReference) {
        ServiceResource removedService;
        synchronized (m_serviceResourceMap) {
            removedService = m_serviceResourceMap.remove(serviceReference.getProperty(Constants.SERVICE_ID));
        }
        Everest.postResource(ResourceEvent.DELETED, removedService);
    }

}
