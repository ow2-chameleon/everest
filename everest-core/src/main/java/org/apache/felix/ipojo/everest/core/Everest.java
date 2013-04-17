package org.apache.felix.ipojo.everest.core;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.*;

/**
 * Everest Core.
 */
@Component
@Instantiate
public class Everest extends DefaultReadOnlyResource {

    private Map<Path, Resource> resources = new HashMap<Path, Resource>();

    public Everest() {
        super(Path.from("/"));
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

    public synchronized Map<Path, Resource> getEverestResources() {
        return new TreeMap<Path, Resource>(resources);
    }

    public synchronized List<Resource> getResources() {
        return new ArrayList<Resource>(resources.values());
    }
}
