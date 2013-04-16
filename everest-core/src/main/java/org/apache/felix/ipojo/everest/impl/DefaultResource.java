package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.filters.Filters;
import org.apache.felix.ipojo.everest.services.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default resource implementation
 */
public class DefaultResource implements Resource {

    private final Path path;
    private final Resource[] resources;
    private final ResourceMetadata metadata;
    private Relation[] relations;

    public DefaultResource(Path path) {
        this(path, null);
    }

    public DefaultResource(String path) {
        this(Path.from(path), null);
    }

    public DefaultResource(Resource parent, String name) {
        this(parent.getPath() + Paths.PATH_SEPARATOR + name);
    }

    public DefaultResource(Path path, ResourceMetadata metadata, Resource... resources) {
        this.path = path;
        this.resources = resources;
        this.metadata = metadata;
    }

    public DefaultResource setRelations(Relation... relations) {
        this.relations = relations;
        return this;
    }

    public Path getPath() {
        return path;
    }

    public Path getCanonicalPath() {
        return path;
    }

    public List<Resource> getResources() {
        if (resources == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Resource>(Arrays.asList(resources));
    }

    public ResourceMetadata getMetadata() {
        if (metadata != null) {
            return ImmutableResourceMetadata.of(metadata);
        } else {
            return ImmutableResourceMetadata.of(Collections.<String, Object>emptyMap());
        }

    }

    public List<Relation> getRelations() {
        if (relations == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Relation>(Arrays.asList(relations));
    }

    public DefaultResource setRelations(List<Relation> relations) {
        this.relations = relations.toArray(new Relation[relations.size()]);
        return this;
    }

    public List<Resource> getResources(ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();

        for (Resource res : all()) {
            if (filter.accept(res)) {
                resources.add(res);
            }
        }
        return resources;
    }

    public Resource getResource(String path) {
        List<Resource> list = getResources(Filters.hasPath(path));
        if (! list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void traverse(Resource resource, List<Resource> list) {
        list.add(resource);
        for (Resource res : resource.getResources()) {
            traverse(res, list);
        }
    }

    public List<Resource> all() {
        List<Resource> all = new ArrayList<Resource>();
        traverse(this, all);
        return all;
    }


    /**
     * A request was emitted on the current request.
     * This method handles the request.
     *
     * @param request the request.
     * @return the updated resource
     * @throws IllegalActionOnResourceException
     *
     * @throws ResourceNotFoundException
     */
    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {
        //Trace
        System.out.println("Processing request " + request.action() + " " + request.path() + " by " +
                getCanonicalPath());
        //End Trace

        // 1) Substract our path from the request path.

        // 2) The request is targeting us...
        if (request.path().equals(getPath())) {
            switch (request.action()) {
                case GET:
                    return get(request);
                case DELETE:
                    return delete(request);
                case PUT:
                    return put(request);
                case POST:
                    return post(request);
            }
            return null;
        }

        // 3) The request is targeting one of our child.
        String firstSegment = ""; //TODO
        Path path = Path.from(getPath().toString() + "/" + firstSegment);
        for (Resource resource : getResources()) {
            if (resource.getPath().equals(path)) {
                return resource.process(request);
            }
        }

        throw new ResourceNotFoundException(request);
    }

    /**
     * Default get action : return the current resource.
     *
     * @param request the request
     * @return the current resource
     */
    public Resource get(Request request) {
        return this;
    }

    /**
     * Method to override to support resource deletion. By default it returns the current resource, unchanged.
     *
     * @param request the request
     * @return the current resource (unchanged by default).
     */
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        return this;
    }

    /**
     * Method to override to support explicit resource creation. By default it returns {@literal null}.
     *
     * @param request the request
     * @return {@literal null}
     */
    public Resource put(Request request) throws IllegalActionOnResourceException {
        return null;
    }

    /**
     * Method to override to support resource update. By default it returns the current resource, unchanged.
     *
     * @param request the request
     * @return the current resource (unchanged)
     */
    public Resource post(Request request) throws IllegalActionOnResourceException {
        return this;
    }

    public static class Builder {

        private final Class<? extends DefaultResource> clazz;
        private String path;
        private ResourceMetadata metadata;
        private List<Relation> relations;
        private List<Resource> resources;

        public Builder() {
            this(DefaultResource.class);
        }

        public Builder(Class<? extends DefaultResource> clazz) {
            this.clazz = clazz;
        }

        Builder(String path) {
            this(DefaultResource.class);
            fromPath(path);
        }

        public Builder fromPath(String path) {
            this.path = path;
            return this;
        }

        public Builder with(ResourceMetadata resourceMetadata) {
            this.metadata = resourceMetadata;
            return this;
        }

        public Builder with(Resource resource) {
            if (this.resources == null) {
                this.resources = new ArrayList<Resource>();
            }
            resources.add(resource);
            return this;
        }

        public Builder with(Relation relation) {
            if (this.relations == null) {
                this.relations = new ArrayList<Relation>();
            }
            relations.add(relation);
            return this;
        }

        public Resource build() throws IllegalResourceException {
            Resource[] sub = null;
            if (resources != null) {
                sub = resources.toArray(sub);
            }
            DefaultResource res = createResource();
            res.setRelations(relations);
            return res;
        }

        private DefaultResource createResource() throws IllegalResourceException {
            Resource[] sub = null;
            if (resources != null) {
                sub = resources.toArray(sub);
            }

            try {
                //TODO BAD  BAD BAD we're not sure about this constructor !
                Constructor<? extends DefaultResource> cst = clazz.getConstructor(String
                        .class, ResourceMetadata.class, (new Resource[0]).getClass());
                return cst.newInstance(path, metadata, sub);
            } catch (Exception e) {
                throw new IllegalResourceException("Cannot create resource", e);
            }
        }
    }
}
