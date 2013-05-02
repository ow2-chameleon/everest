package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Request;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * '/ipojo/instance/$name' resource, where $name stands for the name of an instance.
 */
public class InstanceNameResource extends DefaultReadOnlyResource {

    /**
     * The represented instance.
     */
    private final Architecture m_instance;

    /**
     * Flag indicating if the underlying Architecture service still exists.
     */
    private volatile boolean m_isStale = false;

    /**
     * The base immutable metadata of this resource.
     */
    private final ResourceMetadata m_baseMetadata;

    //TODO add relations


    /**
     * @param instance the instance represented by this resource
     */
    public InstanceNameResource(Architecture instance) {
        super(InstancesResource.PATH.addElements(instance.getInstanceDescription().getName()));
        m_instance = instance;

        // Build the immutable metadata of this instance.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", instance.getInstanceDescription().getName()); // String
        m_baseMetadata = mb.build();
    }

    /**
     * Set this instance resource as stale. It happens when the underlying Architecture service vanishes.
     */
    void setStale() {
        m_isStale = true;
    }

    @Override
    public synchronized ResourceMetadata getMetadata() {
        // Append mutable state to the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder(m_baseMetadata);
        mb.set("state", stateAsString(m_instance.getInstanceDescription().getState())); // String
        return mb.build();
    }

    private static String stateAsString(int state) {
        switch (state) {
            case ComponentInstance.VALID:
                return "valid";
            case ComponentInstance.INVALID:
                return "invalid";
            case ComponentInstance.STOPPED:
                return "stopped";
            case ComponentInstance.DISPOSED:
                return "disposed";
            case -2:
                return "changing";
            default:
                return "unknown";
        }
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        // The instance must be destroyed
        getComponentInstance().dispose();
        // At this point, this resource must have been set to a stale state!
        return this;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        // The instance configuration must be updated.
        Hashtable<String, ?> config;
        if (request.parameters() != null) {
            config = new Hashtable<String, Object>(request.parameters());
        } else {
            config = new Hashtable<String, Object>();
        }
        getComponentInstance().reconfigure(config);
        return this;
    }

    // This is a hack!
    private ComponentInstance getComponentInstance() {
        Field shunt = null;
        try {
            shunt = InstanceDescription.class.getDeclaredField("m_instance");
            shunt.setAccessible(true);
            return (ComponentInstance) shunt.get(m_instance.getInstanceDescription());
        } catch (Exception e) {
            throw new IllegalStateException("cannot get component instance", e);
        } finally {
            if (shunt != null) {
                shunt.setAccessible(false);
            }
        }
    }
}
