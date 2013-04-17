package org.apache.felix.ipojo.everest.core;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.*;

import java.util.*;

/**
 * Everest Core.
 */
@Component
@Instantiate
public class Everest extends DefaultResource {

    private Map<Path, Resource> resources = new HashMap<Path, Resource>();

    public Everest() {
        super(Path.from("/"));
    }

    @Bind(optional = true, aggregate = true)
    public void bindResourceManager(Resource resource) {
        synchronized (this) {
            resources.put(resource.getCanonicalPath(), resource);
        }
    }

    @Unbind
    public void unbindResourceManager(Resource resource) {
        synchronized (this) {
            resources.remove(resource.getCanonicalPath());
        }
    }

    public synchronized Map<Path, Resource> getEverestResources() {
        return new TreeMap<Path, Resource>(resources);
    }

    public synchronized List<Resource> getResources() {
        return new ArrayList<Resource>(resources.values());
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource put(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource post(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }
}
