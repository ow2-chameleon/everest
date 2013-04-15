package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.filters.Filters;
import org.apache.felix.ipojo.everest.services.Request;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceFilter;
import org.apache.felix.ipojo.everest.services.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of resource manager.
 */
public abstract class AbstractResourceManager implements ResourceManager {

    private final String name;
    private final String description;

    public AbstractResourceManager(String name) {
        this(name, null);
    }

    public AbstractResourceManager(String name, String description) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;

        if (description == null) {
            this.description = name;
        } else {
            this.description = description;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract List<Resource> retrieveAll();

    public abstract Resource process(Request request);


    public Resource getResource(String path) {
        for (Resource res : retrieveAll()) {
            if (res.getPath().equals(path)) {
                return res;
            }
        }
        return null;
    }

    public List<Resource> getResources(Resource resource, ResourceFilter filter) {
        return getResources(Filters.and(
                Filters.isSubResourceOf(resource),
                filter
        ));
    }

    public List<Resource> getResources(ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Resource res : retrieveAll()) {
            if (filter.accept(res)) {
                resources.add(res);
            }
        }
        return resources;
    }

}
