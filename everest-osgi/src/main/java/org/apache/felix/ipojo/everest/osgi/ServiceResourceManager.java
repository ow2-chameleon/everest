package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.ServiceReference;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:55 AM
 */
public class ServiceResourceManager extends DefaultReadOnlyResource {

    public static final String SERVICE_ROOT_NAME = "services";

    public static final Path SERVICES_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + SERVICE_ROOT_NAME));


    public ServiceResourceManager(){
        super(SERVICES_PATH);

    }

    public void addService(ServiceReference serviceReference) {

    }

    public void removeService(ServiceReference serviceReference) {

    }
}
