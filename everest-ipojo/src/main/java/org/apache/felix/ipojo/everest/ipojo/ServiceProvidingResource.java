package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceDescription;

/**
 * '/ipojo/instance/$name/dependency/$is' resource.
 */
public class ServiceProvidingResource extends DefaultReadOnlyResource {

    public ServiceProvidingResource(InstanceResource instance, String index, ProvidedServiceDescription providing) {
        super(instance.getProvidings().getPath().addElements(index));
    }

    // TODO implement providing state listener
    @Override
    public boolean isObservable() {
        return true;
    }
}
