package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.architecture.PropertyDescription;
import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.ipojo.handlers.configuration.ConfigurationHandlerDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandlerDescription;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.*;

/**
 * '/ipojo/instance/$name' resource.
 */
public class InstanceResource extends ResourceMap implements InstanceStateListener {

    /**
     * The service dependencies of this iPOJO component instance, index by id.
     */
    private final ResourceMap m_dependencies = new ResourceMap(getPath().addElements("dependency"), false, null);

    /**
     * The service providings of this iPOJO component instance, index by id.
     */
    private final ResourceMap m_providings = new ResourceMap(getPath().addElements("providing"), false, null);

    /**
     * The underlying Architecture service.
     */
    private final WeakReference<Architecture> m_instance;

    public InstanceResource(Architecture instance, ServiceReference<Architecture> ref) {
        super(INSTANCES.addElements(instance.getInstanceDescription().getName()), true,
                new ImmutableResourceMetadata.Builder()
                        .set("name", instance.getInstanceDescription().getName())
                        .set("factory.name", getComponentInstance(instance).getFactory().getName())
                        .set("factory.version", getComponentInstance(instance).getFactory().getVersion())
                        .build());
        m_instance = new WeakReference<Architecture>(instance);
        // Set the immutable relations
        ComponentInstance ci = getComponentInstance(instance);
        ci.addInstanceStateListener(this);
        Factory factory = ci.getFactory();
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
                        "The factory of this component instance"),
                new DefaultRelation(
                        getPath(),
                        Action.DELETE,
                        "delete",
                        "Destroy this component instance"),
                new DefaultRelation(
                        getPath(),
                        Action.UPDATE,
                        "reconfigure",
                        "Reconfigure this component instance",
                        new DefaultParameter()
                                .name("state")
                                .type(String.class)
                                .description("The wanted state of the component instance")
                                .optional(true),
                        new DefaultParameter()
                                .name("configuration")
                                .type(Map.class)
                                .description("The wanted configuration of the component instance")
                                .optional(true)
                ));

        // Add service dependencies
        addResource(m_dependencies, "dependencies", "The service dependencies of this component instance");
        @SuppressWarnings("unchecked")
        DependencyHandlerDescription dhd = (DependencyHandlerDescription)
                instance.getInstanceDescription().getHandlerDescription("org.apache.felix.ipojo:requires");
        if (dhd != null) {
            for (DependencyDescription d : dhd.getDependencies()) {
                String id = d.getId();
                m_dependencies.addResource(
                        new ServiceDependencyResource(this, d),
                        "dependency[" + id + "]",
                        String.format("Service dependency '%s'", id));
            }
        }

        // Add service providings
        addResource(m_providings, "providings", "The service providings of this component instance");
        @SuppressWarnings("unchecked")
        ProvidedServiceHandlerDescription phd = (ProvidedServiceHandlerDescription)
                instance.getInstanceDescription().getHandlerDescription("org.apache.felix.ipojo:provides");
        if (phd != null) {
            ProvidedServiceDescription[] providedServices = phd.getProvidedServices();
            for (int i = 0; i < providedServices.length; i++) {
                ProvidedServiceDescription p = providedServices[i];
                String id = Integer.toString(i);
                m_providings.addResource(
                        new ServiceProvidingResource(this, id, p),
                        "providing[" + id + "]",
                        String.format("Service providing '%s'", id));
            }
        }

        // TODO add relation on declaration (tricky because declarations are (most of the time) unnamed)
    }

    ResourceMap getDependencies() {
        return m_dependencies;
    }

    ResourceMap getProvidings() {
        return m_providings;
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
                            .set("configuration", getInstanceConfiguration(instance.getInstanceDescription())) // ResourceMetadata
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
                .set("configuration", getInstanceConfiguration(i.getInstanceDescription())) // ResourceMetadata
                .build();
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

    private static Integer stringAsState(String state) {
        if ("valid".equalsIgnoreCase(state)) {
            return ComponentInstance.VALID;
        } else if ("invalid".equalsIgnoreCase(state)) {
            return ComponentInstance.INVALID;
        } else if ("stopped".equalsIgnoreCase(state)) {
            return ComponentInstance.STOPPED;
        } else if ("disposed".equalsIgnoreCase(state)) {
            return ComponentInstance.DISPOSED;
        }
        // Unknown state
        return null;
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
        ComponentInstance i = getComponentInstance(a);

        String s = request.get("state", String.class);
        if (s != null) {
            // Requested to change the state of the component.
            Integer state = stringAsState(s);
            if (state == null) {
                throw new IllegalActionOnResourceException(request, this, "Invalid requested component state: " + s);
            }
            try {
                switch (state) {
                    case ComponentInstance.VALID:
                    case ComponentInstance.INVALID:
                        i.start();
                        break;
                    case ComponentInstance.STOPPED:
                        i.stop();
                        break;
                    case ComponentInstance.DISPOSED:
                        i.dispose();
                        break;
                    default:
                }
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot set component instance state to " + s);
                ee.initCause(e);
                throw ee;
            }
        }

        Map<String, Object> c = request.get("configuration", Map.class);
        if (c != null) {
            // Reconfiguration requested
            Hashtable<String, ?> config = new Hashtable<String, Object>(c);
            // Do reconfigure!
            try {
                i.reconfigure(config);
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot reconfigure component instance");
                ee.initCause(e);
                throw ee;
            }
        }

        return this;
    }

    public void stateChanged(ComponentInstance instance, int newState) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    private static ResourceMetadata getInstanceConfiguration(InstanceDescription instanceDescription) {
        // Get the configuration
        ImmutableResourceMetadata.Builder props = new ImmutableResourceMetadata.Builder();
        ConfigurationHandlerDescription chd = (ConfigurationHandlerDescription)
                instanceDescription.getHandlerDescription("org.apache.felix.ipojo:properties");
        if (chd != null) {
            PropertyDescription[] pp = chd.getProperties();
            for (PropertyDescription p : pp) {
                props.set(p.getName(), p.getValue());
            }
        }
        return props.build();
    }
}
