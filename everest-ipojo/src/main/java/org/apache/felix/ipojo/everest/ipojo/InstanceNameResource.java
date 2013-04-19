package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultResource;

/**
 * '/ipojo/instance/$name' resource, where $name stands for the name of an instance.
 */
public class InstanceNameResource extends DefaultResource {

    /**
     * The represented instance.
     */
    private final Architecture m_instance;

    /**
     * @param instance the instance represented by this resource
     */
    public InstanceNameResource(Architecture instance) {
        super(InstancesResource.PATH.addElements(instance.getInstanceDescription().getName()));
        m_instance = instance;
    }

}
