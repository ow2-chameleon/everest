package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedService;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_SERVICES;
import static org.apache.felix.ipojo.handlers.providedservice.ProvidedService.*;

/**
 * '/ipojo/instance/$name/providing/$id' resource.
 */
public class ServiceProvidingResource extends DefaultReadOnlyResource implements ProvidedServiceListener {

    private WeakReference<ProvidedServiceDescription> m_description;

    public ServiceProvidingResource(InstanceResource instance, String index, ProvidedServiceDescription providing) {
        super(instance.getProvidings().getPath().addElements(index),
                new ImmutableResourceMetadata.Builder()
                        .set("serviceSpecifications", providing.getServiceSpecifications())
                        .set("policy", policyToString(providing.getPolicy()))
                        .set("creationStrategy", providing.getCreationStrategy().getName())
                        .set("controller", providing.getController())
                        .build());
        m_description = new WeakReference<ProvidedServiceDescription>(providing);
        providing.addListener(this);
    }

    @Override
    public ResourceMetadata getMetadata() {
        ResourceMetadata m = super.getMetadata();
        ProvidedServiceDescription d = m_description.get();
        if (d == null) {
            return m;
        } else {
            return new ImmutableResourceMetadata.Builder()
                    .set("state", stateToString(d.getState()))
                    .set("properties", d.getProperties())
                    .build();
        }
    }

    @Override
    public List<Relation> getRelations() {
        List<Relation> r = super.getRelations();
        ProvidedServiceDescription d = m_description.get();
        if (d == null) {
            return r;
        }
        ServiceReference<?> s = d.getServiceReference();
        if (s == null) {
            return r;
        }
        r = new ArrayList<Relation>(r);
        r.add(new DefaultRelation(
                PATH_TO_OSGI_SERVICES.addElements(String.valueOf(s.getProperty(Constants.SERVICE_ID))),
                Action.READ,
                "service",
                "Provided service"
        ));
        return Collections.unmodifiableList(r);
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        ProvidedServiceDescription d = m_description.get();
        if (clazz == ProvidedService.class) {
            return clazz.cast(getProvidedService(d));
        } else if (clazz == ProvidedServiceDescription.class) {
            return clazz.cast(d);
        } else if (clazz == ServiceReference.class) {
            return d == null ? null : clazz.cast(d.getServiceReference());
        } else {
            return super.adaptTo(clazz);
        }
    }

    private static String policyToString(int policy) {
        switch (policy) {
            case SINGLETON_STRATEGY:
                return "SINGLETON_STRATEGY";
            case SERVICE_STRATEGY:
                return "SERVICE_STRATEGY";
            case STATIC_STRATEGY:
                return "STATIC_STRATEGY";
            case INSTANCE_STRATEGY:
                return "INSTANCE_STRATEGY";
            case CUSTOM_STRATEGY:
                return "CUSTOM_STRATEGY";
            default:
                return "unknown";
        }
    }

    private static String stateToString(int state) {
        switch (state) {
            case REGISTERED:
                return "REGISTERED";
            case UNREGISTERED:
                return "UNREGISTERED";
            default:
                return "unknown";
        }
    }

    private static Integer stringAsState(String state) {
        if ("REGISTERED".equalsIgnoreCase(state)) {
            return REGISTERED;
        } else if ("UNREGISTERED".equalsIgnoreCase(state)) {
            return UNREGISTERED;
        }
        // Unknown state
        return null;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {

        ProvidedService p = getProvidedService(m_description.get());
        if (p == null) {
            throw new IllegalActionOnResourceException(request, this, "Provided service description has gone");
        }

        String s = request.get("state", String.class);
        if (s != null) {
            // Requested to change the state of the provided service.
            Integer state = stringAsState(s);
            if (state == null) {
                throw new IllegalActionOnResourceException(request, this, "Invalid requested service state: " + s);
            }
            try {
                Method hack;
                switch (state) {
                    case REGISTERED:
                        hack = ProvidedService.class.getDeclaredMethod("registerService");
                        break;
                    case UNREGISTERED:
                        hack = ProvidedService.class.getDeclaredMethod("unregisterService");
                        break;
                    default:
                        // Cannot happen!
                        throw new AssertionError();
                }
                hack.setAccessible(true);
                try {
                    hack.invoke(p);
                } finally {
                    hack.setAccessible(false);
                }
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot change provided service state to " + s);
                ee.initCause(e);
                throw ee;
            }
        }

        return this;
    }

    // WARN: This is a hack!
    private static ProvidedService getProvidedService(ProvidedServiceDescription description) {
        if (description == null) {
            return null;
        }
        Field shunt = null;
        try {
            shunt = ProvidedServiceDescription.class.getDeclaredField("m_ps");
            shunt.setAccessible(true);
            return (ProvidedService) shunt.get(description);
        } catch (Exception e) {
            throw new IllegalStateException("cannot get provided service", e);
        } finally {
            if (shunt != null) {
                shunt.setAccessible(false);
            }
        }
    }

    public void serviceRegistered(ComponentInstance instance, ProvidedService providedService) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void serviceModified(ComponentInstance instance, ProvidedService providedService) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void serviceUnregistered(ComponentInstance instance, ProvidedService providedService) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void cleanup() {
        // Remove the listener
        ProvidedServiceDescription p = m_description.get();
        if (p != null) {
            try {
                p.removeListener(this);
            } catch (NoSuchElementException e) {
                // Swallow
            }
        }
    }

    /**
     * Create a fake service providing resource for the given service providing.
     *
     * @param instance path to grand-parent instance resource
     * @param index index of the service providing
     * @param providing service providing to represent
     * @return fake resource representing the given service providing
     */
    public static Resource fakeServiceProvidingResource(Path instance, String index,
                                                ProvidedServiceDescription providing) {
        try {
            Builder r = new Builder()
                    .fromPath(instance.addElements("providing", index))
                    .with(new ImmutableResourceMetadata.Builder()
                            .set("serviceSpecifications", providing.getServiceSpecifications())
                            .set("policy", policyToString(providing.getPolicy()))
                            .set("creationStrategy", providing.getCreationStrategy().getName())
                            .set("controller", providing.getController())
                            .set("state", stateToString(providing.getState()))
                            .set("properties", providing.getProperties())
                            .set("__isFake", true)
                            .build());
            ServiceReference<?> ref = providing.getServiceReference();
            if (ref != null) {
                r.with(new DefaultRelation(
                        PATH_TO_OSGI_SERVICES.addElements(String.valueOf(ref.getProperty(Constants.SERVICE_ID))),
                        Action.READ,
                        "service",
                        "Provided service"));
            }
            return r.build();
        } catch (IllegalResourceException e) {
            // Should never happen!
            throw new AssertionError(e);
        }
    }

}
