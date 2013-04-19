package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;

/**
 * '/ipojo/instance' resource.
 */
public class InstancesResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("instance");

    public InstancesResource() {
        super(PATH);
    }

    void addInstance(Architecture instance) {
        // TODO
    }

    void removeInstance(Architecture instance) {
        // TODO
    }

}
