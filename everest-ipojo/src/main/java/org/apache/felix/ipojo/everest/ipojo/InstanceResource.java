package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.*;

/**
 * '/ipojo/instance/$name' resource.
 */
// TODO extends resourceMap and add resources for dependencies, providings (and ???)
public class InstanceResource extends DefaultReadOnlyResource {

    /**
     * The underlying Architecture service.
     */
    private final WeakReference<Architecture> m_instance;

    public InstanceResource(Architecture instance, ServiceReference<Architecture> ref) {
        super(INSTANCES.addElements(instance.getInstanceDescription().getName()),
                new ImmutableResourceMetadata.Builder()
                        .set("name", instance.getInstanceDescription().getName())
                        .set("factory.name", getComponentInstance(instance).getFactory().getName())
                        .set("factory.version", getComponentInstance(instance).getFactory().getVersion())
                        .build());
        m_instance = new WeakReference<Architecture>(instance);
        // Set the immutable relations
        Factory factory = getComponentInstance(instance).getFactory();
        setRelations(
                new DefaultRelation(
                        PATH_TO_OSGI_SERVICES.addElements(String.valueOf(ref.getProperty(Constants.SERVICE_ID))),
                        Action.READ,
                        "service",
                        "The Architecture OSGi service"),
                new DefaultRelation(
                        FACTORIES.addElements(factory.getName(), String.valueOf(factory.getVersion())),
                        Action.READ,
                        "factory",
                        "The factory of this component instance"));
        // TODO add relation on declaration (tricky because declarations are (most of the time) unnamed)
    }

    /**
     * Create a fake instance resource for the given component instance. This method is used when a new component is
     * created but the architecture service is not present, so we return a "one-shot" fake instance resource.
     * "One-shot" means here that the returned resource will NOT be accessible by further requests.
     *
     * @param instance component instance to fake
     * @return fake resource representing the given component instance
     */
    public static Resource fakeInstanceResource(ComponentInstance instance) {
        String name = instance.getInstanceName();
        Factory factory = instance.getFactory();
        try {
            return new Builder()
                    .fromPath(INSTANCES.addElements(name))
                    .with(new ImmutableResourceMetadata.Builder()
                            .set("name", name)
                            .set("factory.name", factory.getName())
                            .set("factory.version", factory.getVersion())
                            .set("state", stateAsString(instance.getState()))
                            .build())
                    .with(new DefaultRelation(
                            FACTORIES.addElements(factory.getName(), String.valueOf(factory.getVersion())),
                            Action.READ,
                            "factory",
                            "The factory of this component instance"))
                    .build();
        } catch (IllegalResourceException e) {
            // Should never happen!
            throw new AssertionError(e);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        Architecture i = m_instance.get();
        ResourceMetadata m = super.getMetadata();
        if (i == null) {
            // Reference has been released
            return m;
        }
        // Add dynamic metadata
        return new ImmutableResourceMetadata.Builder(m)
                .set("state", stateAsString(i.getInstanceDescription().getState())) // String
                .build();
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == Architecture.class) {
            // Returns null if reference has been released
            return clazz.cast(m_instance.get());
        } else if (clazz == ComponentInstance.class) {
            // Returns null if reference has been released
            Architecture a = m_instance.get();
            if (a == null) {
                return null;
            } else {
                return clazz.cast(getComponentInstance(a));
            }
        } else {
            return super.adaptTo(clazz);
        }
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
        Architecture a = m_instance.get();
        if (a == null) {
            throw new IllegalActionOnResourceException(request, this, "Architecture has gone");
        }
        // The instance must be destroyed
        getComponentInstance(a).dispose();
        // At this point, this resource must have been set to a stale state!
        return this;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        Architecture a = m_instance.get();
        if (a == null) {
            throw new IllegalActionOnResourceException(request, this, "Architecture has gone");
        }
        // The instance configuration must be updated.
        Hashtable<String, ?> config;
        if (request.parameters() != null) {
            config = new Hashtable<String, Object>(request.parameters());
        } else {
            config = new Hashtable<String, Object>();
        }
        getComponentInstance(a).reconfigure(config);
        return this;
    }

}
