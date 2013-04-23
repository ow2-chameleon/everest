package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 8:08 PM
 */
public class ServiceRelationsResource extends DefaultReadOnlyResource {


    private final ServiceReference[] m_services;

    public ServiceRelationsResource(Path path, ServiceReference[] services) {
        super(path);
        m_services = services;
        if (m_services != null) {
            List<Relation> relations = new ArrayList<Relation>();
            for (ServiceReference service : m_services) {
                String serviceId = service.getProperty(Constants.SERVICE_ID).toString();
                Path servicePath = ServiceResourceManager.getInstance().getPath().addElements(serviceId);
                relations.add(new DefaultRelation(servicePath, Action.READ, serviceId));
            }
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        if (m_services != null) {
            for (ServiceReference service : m_services) {
                ImmutableResourceMetadata.Builder serviceMetadataBuilder = new ImmutableResourceMetadata.Builder();
                for (String s : service.getPropertyKeys()) {
                    serviceMetadataBuilder.set(s, service.getProperty(s));
                }
                metadataBuilder.set(service.getProperty(Constants.SERVICE_ID).toString(), serviceMetadataBuilder.build());
            }
        }
        return metadataBuilder.build();
    }

}
