package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.*;

/**
 * '/ipojo/instance/$name/dependency/$is' resource.
 */
public class ServiceDependencyResource extends DefaultReadOnlyResource {

    public ServiceDependencyResource(InstanceResource instance, DependencyDescription dependency) {
        super(instance.getDependencies().getPath().addElements(dependency.getId()));
    }

    // TODO implement dependency state listener
    @Override
    public boolean isObservable() {
        return true;
    }
}
