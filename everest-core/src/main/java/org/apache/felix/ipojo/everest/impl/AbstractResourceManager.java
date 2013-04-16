package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.filters.Filters;
import org.apache.felix.ipojo.everest.services.*;

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

    public abstract Resource getRoot();

    public String getRootPath() {
        return Path.SEPARATOR + name;
    }

    public abstract Resource resolve(String path);

    public Resource getResource(String path) {
        return resolve(path);
    }

    public List<Resource> getResources(Resource resource, ResourceFilter filter) {
        return getResources(Filters.and(
                Filters.isSubResourceOf(resource),
                filter
        ));
    }

    public List<Resource> getResources(ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();

        // Traverse the whole tree.
        List<Resource> all = new ArrayList<Resource>();
        traverse(getRoot(), all);

        for (Resource res : all) {
            if (filter.accept(res)) {
                resources.add(res);
            }
        }
        return resources;
    }

    protected void traverse(Resource resource, List<Resource> list) {
        list.add(resource);
        for (Resource res : resource.getResources()) {
            traverse(res, list);
        }
    }

    public Resource process(Request request) throws ResourceNotFoundException, IllegalActionOnResourceException {
        // 1) resolve the resource
        Resource resource = resolve(request.path());
        // 2) delegate processing
        return resource.process(request);
    }

}
