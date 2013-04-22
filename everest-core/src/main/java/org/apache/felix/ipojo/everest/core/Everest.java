package org.apache.felix.ipojo.everest.core;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.managers.everest.EverestRootResource;
import org.apache.felix.ipojo.everest.services.*;

import java.util.*;

/**
 * Everest Core.
 */
@Component
@Instantiate
@Provides(specifications = EverestService.class)
public class Everest extends DefaultReadOnlyResource implements EverestService {

    private Map<Path, Resource> resources = new LinkedHashMap<Path, Resource>();
    private List<ResourceExtender> extenders = new ArrayList<ResourceExtender>();

    public Everest() {
        super(Path.from("/"));
        // Add the everest domain
        resources.put(Path.from("/everest"), new EverestRootResource(this));
    }

    @Bind(optional = true, aggregate = true)
    public void bindRootResource(Resource resource) {
        synchronized (this) {
            resources.put(resource.getCanonicalPath(), resource);
        }
    }

    @Unbind
    public void unbindRootResource(Resource resource) {
        synchronized (this) {
            resources.remove(resource.getCanonicalPath());
        }
    }

    @Bind(optional = true, aggregate = true)
    public void bindExtender(ResourceExtender extender) {
        synchronized (this) {
            extenders.add(extender);
        }
    }

    @Unbind
    public void unbindExtender(ResourceExtender extender) {
        synchronized (this) {
            extenders.remove(extender);
        }
    }

    public synchronized Map<Path, Resource> getEverestResources() {
        return new TreeMap<Path, Resource>(resources);
    }

    public synchronized List<Resource> getResources() {
        return new ArrayList<Resource>(resources.values());
    }

    public synchronized List<ResourceExtender> getExtenders() {
        return new ArrayList<ResourceExtender>(extenders);
    }

    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {
        // We can't extend when the original action fails.

        Resource result = super.process(request);

        // Extensions
        // We must update the resulted resource with the extensions
        for (ResourceExtender extender : getExtenders()) {
            if (extender.getFilter().accept(result)) {
                result = extender.extend(request, result);
            }
        }

        return result;
    }
}
