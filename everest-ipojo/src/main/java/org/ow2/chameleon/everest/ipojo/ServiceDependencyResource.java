/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.ipojo;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;
import org.apache.felix.ipojo.handlers.dependency.Dependency;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.util.DependencyModel;
import org.apache.felix.ipojo.util.DependencyModelListener;
import org.osgi.framework.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;

import static org.ow2.chameleon.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_SERVICES;
import static org.apache.felix.ipojo.util.DependencyModel.*;

/**
 * '/ipojo/instance/$name/dependency/$id' resource.
 */
public class ServiceDependencyResource extends DefaultReadOnlyResource implements DependencyModelListener {

    private WeakReference<DependencyDescription> m_description;

    public ServiceDependencyResource(InstanceResource instance, DependencyDescription description) {
        super(instance.getDependencies().getPath().addElements(description.getId()),
                new ImmutableResourceMetadata.Builder()
                        .set("id", description.getId())
                        .set("specification", description.getSpecification()) // May become mutable is future iPOJO releases?
                        .set("isNullable", description.supportsNullable())
                        .set("isProxy", description.isProxy())
                        .set("defaultImplementation", description.getDefaultImplementation())
                                // All other attributes are considered dynamic
                        .build());
        m_description = new WeakReference<DependencyDescription>(description);
        description.addListener(this);
    }

    @Override
    public ResourceMetadata getMetadata() {
        ResourceMetadata m = super.getMetadata();
        DependencyDescription description = m_description.get();
        if (description == null) {
            return m;
        } else {
            return new ImmutableResourceMetadata.Builder()
                    .set("id", description.getId())
                    .set("specification", description.getSpecification())
                    .set("isNullable", description.supportsNullable())
                    .set("isProxy", description.isProxy())
                    .set("defaultImplementation", description.getDefaultImplementation())
                    .set("state", stateToString(description.getState()))
                    .set("filter", description.getFilter())
                    .set("policy", policyToString(description.getPolicy()))
                    .set("comparator", description.getComparator())
                    .set("isAggregate", description.isMultiple())
                    .set("isOptional", description.isOptional())
                    .set("isFrozen", description.isFrozen())
                    .build();
        }
    }

    @Override
    public List<Relation> getRelations() {
        List<Relation> r = super.getRelations();
        DependencyDescription d = m_description.get();
        if (d == null) {
            return r;
        } else {
            r = new ArrayList<Relation>(r);
            @SuppressWarnings("unchecked")
            List<ServiceReference> matching = d.getServiceReferences();
            if (matching != null) {
                for(ServiceReference<?> s : matching) {
                    String id = String.valueOf(s.getProperty(Constants.SERVICE_ID));
                    r.add(new DefaultRelation(
                            PATH_TO_OSGI_SERVICES.addElements(id),
                            Action.READ,
                            String.format("matchingService[%s]", id),
                            String.format("Matching service with id '%s'", id)));
                }
            }
            @SuppressWarnings("unchecked")
            List<ServiceReference> used = d.getUsedServices();
            if (used != null) {
                for(ServiceReference<?> s : used) {
                    String id = String.valueOf(s.getProperty(Constants.SERVICE_ID));
                    r.add(new DefaultRelation(
                            PATH_TO_OSGI_SERVICES.addElements(id),
                            Action.READ,
                            String.format("usedService[%s]", id),
                            String.format("Used service with id '%s'", id)));
                }
            }
            return Collections.unmodifiableList(r);
        }
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == DependencyModel.class) {
            return clazz.cast(getDependency(m_description.get()));
        } else if (clazz == DependencyDescription.class) {
            return clazz.cast(m_description.get());
        } else {
            return super.adaptTo(clazz);
        }
    }

    public void validate(DependencyModel dependency) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void invalidate(DependencyModel dependency) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    private static String policyToString(int policy) {
        switch (policy) {
            case DYNAMIC_BINDING_POLICY:
                return "DYNAMIC_BINDING";
            case STATIC_BINDING_POLICY:
                return "STATIC_BINDING";
            case DYNAMIC_PRIORITY_BINDING_POLICY:
                return "DYNAMIC_PRIORITY_BINDING";
            default:
                return "CUSTOMIZED"; // Optimistic!
        }
    }

    private static String stateToString(int state) {
        switch (state) {
            case BROKEN:
                return "BROKEN";
            case UNRESOLVED:
                return "UNRESOLVED";
            case RESOLVED:
                return "RESOLVED";
            default:
                return "unknown";
        }
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {

        DependencyDescription d = m_description.get();
        if (d == null) {
            throw new IllegalActionOnResourceException(request, this, "Dependency description has gone");
        }
        BundleContext bc = d.getDependency().getBundleContext();

        String f = request.get("filter", String.class);
        if (f != null) {
            // Requested to change the filter of the dependency.
            try {
                if (!f.isEmpty()) {
                    d.setFilter(bc.createFilter(f));
                } else {
                    d.setFilter(null);
                }
            } catch (InvalidSyntaxException e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Bad filter: " + f);
                ee.initCause(e);
                throw ee;
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot reconfigure filter");
                ee.initCause(e);
                throw ee;
            }
        }

        Boolean aa = getBooleanParameter(request, "isAggregate");
        if (aa != null) {
            try {
                d.setAggregate(aa);
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot reconfigure isAggregate flag");
                ee.initCause(e);
                throw ee;
            }
        }

        Boolean oo = getBooleanParameter(request, "isOptional");
        if (oo != null) {
            try {
                d.setOptional(oo);
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot reconfigure isOptional flag");
                ee.initCause(e);
                throw ee;
            }
        }

        Comparator<?> cc = request.get("comparator", Comparator.class);
        if (cc != null ) {
            try {
                d.setComparator(cc);
            } catch (Exception e) {
                IllegalActionOnResourceException ee = new IllegalActionOnResourceException(
                        request,
                        this,
                        "Cannot reconfigure comparator");
                ee.initCause(e);
                throw ee;
            }
        }

        return this;
    }

    // WARN: This is a hack!
    private static Dependency getDependency(DependencyDescription description) {
        if (description == null) {
            return null;
        }
        Field shunt = null;
        try {
            shunt = DependencyDescription.class.getDeclaredField("m_dependency");
            shunt.setAccessible(true);
            return (Dependency) shunt.get(description);
        } catch (Exception e) {
            throw new IllegalStateException("cannot get service dependency", e);
        } finally {
            if (shunt != null) {
                shunt.setAccessible(false);
            }
        }
    }

    private Boolean getBooleanParameter(Request request, String key) throws IllegalActionOnResourceException {
        String s = request.get(key, String.class);
        if (s != null && !s.isEmpty()) {
            if (s.equalsIgnoreCase("true")) {
                return true;
            } else if (s.equalsIgnoreCase("false")) {
                return false;
            } else {
                throw new IllegalActionOnResourceException(request, this,
                        String.format("Invalid value for parameter '%s': %s",
                                key, s));
            }
        } else {
            return null;
        }
    }

    public void matchingServiceArrived(DependencyModel dependency, ServiceReference<?> service) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void matchingServiceModified(DependencyModel dependency, ServiceReference<?> service) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void matchingServiceDeparted(DependencyModel dependency, ServiceReference<?> service) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void serviceBound(DependencyModel dependency, ServiceReference<?> service, Object object) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void serviceUnbound(DependencyModel dependency, ServiceReference<?> service, Object object) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void reconfigured(DependencyModel dependency) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void cleanup() {
        // Remove the listener
        DependencyDescription d = m_description.get();
        if (d != null) {
            try {
                d.removeListener(this);
            } catch (NoSuchElementException e) {
                // Swallow
            }
        }
    }

    /**
     * Create a fake service dependency resource for the given service dependency.
     *
     * @param instance path to grand-parent instance resource
     * @param d service dependency to represent
     * @return fake resource representing the given service dependency
     */
    public static Resource fakeServiceDependencyResource(Path instance, DependencyDescription d) {
        try {
            Builder r = new Builder()
                    .fromPath(instance.addElements("dependency", d.getId()))
                    .with(new ImmutableResourceMetadata.Builder()
                            .set("id", d.getId())
                            .set("specification", d.getSpecification()) // May become mutable is future iPOJO releases?
                            .set("isNullable", d.supportsNullable())
                            .set("isProxy", d.isProxy())
                            .set("defaultImplementation", d.getDefaultImplementation())
                            .set("state", stateToString(d.getState()))
                            .set("filter", d.getFilter())
                            .set("policy", policyToString(d.getPolicy()))
                            .set("comparator", d.getComparator())
                            .set("isAggregate", d.isMultiple())
                            .set("isOptional", d.isOptional())
                            .set("isFrozen", d.isFrozen())
                            .set("__isFake", true)
                            .build());
            @SuppressWarnings("unchecked")
            List<ServiceReference> matching = d.getServiceReferences();
            if (matching != null) {
                for(ServiceReference<?> s : matching) {
                    String id = String.valueOf(s.getProperty(Constants.SERVICE_ID));
                    r.with(new DefaultRelation(
                            PATH_TO_OSGI_SERVICES.addElements(id),
                            Action.READ,
                            String.format("matchingService[%s]", id),
                            String.format("Matching service with id '%s'", id)));
                }
            }
            @SuppressWarnings("unchecked")
            List<ServiceReference> used = d.getUsedServices();
            if (used != null) {
                for(ServiceReference<?> s : used) {
                    String id = String.valueOf(s.getProperty(Constants.SERVICE_ID));
                    r.with(new DefaultRelation(
                            PATH_TO_OSGI_SERVICES.addElements(id),
                            Action.READ,
                            String.format("usedService[%s]", id),
                            String.format("Used service with id '%s'", id)));
                }
            }
            return r.build();
        } catch (IllegalResourceException e) {
            // Should never happen!
            throw new AssertionError(e);
        }
    }
}
