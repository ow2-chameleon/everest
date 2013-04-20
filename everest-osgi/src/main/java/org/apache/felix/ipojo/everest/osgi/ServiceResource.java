package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import static org.apache.felix.ipojo.everest.osgi.ServiceResourceManager.SERVICES_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 11:39 AM
 */
public class ServiceResource extends DefaultReadOnlyResource {

    public ServiceResource(ServiceReference serviceReference){
        super(SERVICES_PATH.add(Path.from((String)serviceReference.getProperty(Constants.SERVICE_ID))));
    }
}
