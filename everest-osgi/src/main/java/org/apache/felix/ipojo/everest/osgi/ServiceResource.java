package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.ServiceResourceManager.SERVICES_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 11:39 AM
 */
public class ServiceResource extends DefaultReadOnlyResource {

    public static final String FROM_BUNDLE_PATH = "from-bundle";

    public static final String FROM_PACKAGE_PATH = "from-package";

    private ServiceReference m_serviceReference;

    public ServiceResource(ServiceReference serviceReference){
        super(SERVICES_PATH.add(Path.from((String)serviceReference.getProperty(Constants.SERVICE_ID))));
        m_serviceReference = serviceReference;
    }

    public ResourceMetadata getSimpleMetadata(){
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(Constants.SERVICE_ID,m_serviceReference.getProperty(Constants.SERVICE_ID));
        metadataBuilder.set(Constants.OBJECTCLASS,m_serviceReference.getProperty(Constants.OBJECTCLASS));
        metadataBuilder.set(FROM_BUNDLE_PATH,m_serviceReference.getBundle().getBundleId());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for(String s : m_serviceReference.getPropertyKeys()){
            metadataBuilder.set(s,m_serviceReference.getProperty(s));
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        //Bundle from which this service is registered
        m_serviceReference.getBundle(); //...
        //new DefaultReadOnlyResource.Builder();
        //Bundles
        m_serviceReference.getUsingBundles();

        //Package of the bundle that is exposed for this service
        //TODO find the package exporting this service...
        return resources;
    }
}
