package org.apache.felix.ipojo.everest.osgi.service;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
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
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:55 AM
 */
public class ServiceResourceManager extends AbstractResourceCollection {

    public static final String SERVICE_ROOT_NAME = "services";

    public static final Path SERVICES_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + SERVICE_ROOT_NAME));

    private Map<Object, ServiceResource> m_serviceResourceMap = new HashMap<Object, ServiceResource>();

    private static final ServiceResourceManager instance = new ServiceResourceManager();

    public static ServiceResourceManager getInstance() {
        return instance;
    }

    public ServiceResourceManager() {
        super(SERVICES_PATH);
    }

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
            updatedService = new ServiceResource(serviceReference);
            m_serviceResourceMap.put(serviceId, updatedService);
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


}
