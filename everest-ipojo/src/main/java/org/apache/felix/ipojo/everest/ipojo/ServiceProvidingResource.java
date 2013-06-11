package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedService;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceDescription;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_SERVICES;
import static org.apache.felix.ipojo.handlers.providedservice.ProvidedService.*;

/**
 * '/ipojo/instance/$name/providing/$id' resource.
 */
// TODO Register as a providing listener as soon as iPOJO defines this interface
public class ServiceProvidingResource extends DefaultReadOnlyResource {

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
}
