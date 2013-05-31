package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * '/ipojo/instance/$name' resource, where $name stands for the name of an instance.
 */
public class InstanceResource extends DefaultReadOnlyResource {

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
    public InstanceResource(Architecture instance) {
        super(IpojoRootResource.INSTANCES.addElements(instance.getInstanceDescription().getName()));
        m_instance = instance;

        // Build the immutable metadata of this instance.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", instance.getInstanceDescription().getName()); // String

        Factory factory = getComponentInstance().getFactory();
        mb.set("factory.name", factory.getName()); // String
        mb.set("factory.version", factory.getVersion()); // String
        m_baseMetadata = mb.build();

        // Relations
        List<Relation> relations = new ArrayList<Relation>();

        // Add relation 'factory' to READ the factory of this instance
        relations.add(new DefaultRelation(IpojoRootResource.FACTORIES.addElements(factory.getName(), String.valueOf(factory.getVersion())), Action.READ, "factory"));

        setRelations(relations);
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

    @Override
    public boolean isObservable() {
        return true;
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
