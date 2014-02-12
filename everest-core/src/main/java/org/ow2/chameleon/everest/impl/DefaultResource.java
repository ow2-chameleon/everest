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

package org.ow2.chameleon.everest.impl;

import org.ow2.chameleon.everest.filters.ResourceFilters;
import org.ow2.chameleon.everest.services.*;

import java.lang.ref.WeakReference;
import java.util.*;

import static org.ow2.chameleon.everest.core.Everest.DEBUG_REQUEST;

/**
 * Default resource implementation
 */
public class DefaultResource<T> implements Resource<T> {

    private final Path path;
    private final Resource<?>[] resources;
    private final ResourceMetadata metadata;
    private final WeakReference<T> ref;
    private Relation[] relations;

    public DefaultResource(Path path) {
        this(path, null, null, (Resource[]) null);
    }

    public DefaultResource(String path) {
        this(Path.from(path));
    }

    public DefaultResource(Resource parent, String name) {
        this(parent.getPath() + Paths.PATH_SEPARATOR + name);
    }

    public DefaultResource(Path path, ResourceMetadata metadata, Resource... resources) {
        this(path, null, metadata, resources);
    }

    public DefaultResource(Path path, T object, ResourceMetadata metadata, Resource... resources) {
        this.path = path;
        this.resources = resources;
        this.metadata = metadata;
        if (object != null) {
            this.ref = new WeakReference<T>(object);
        } else {
            this.ref = null;
        }
    }

    public DefaultResource(Path path, T object) {
        this(path, object, null);
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

    public Collection<Resource<?>> getResources() {
        if (resources == null) {
            return Collections.emptyList();
        }

        // We need to build a copy of the array
        return new ArrayList<>(Arrays.asList(resources));
    }

    public ResourceMetadata getMetadata() {
        if (metadata != null) {
            return ImmutableResourceMetadata.of(metadata);
        } else {
            return ImmutableResourceMetadata.of(Collections.<String, Object>emptyMap());
        }

    }

    public List<Relation> getRelations() {

        Set<Relation> setRelation = new HashSet<Relation>();
        if (relations != null) {
            Collections.addAll(setRelation, relations);
        }
        try {
            setRelation.add(new DefaultRelation(this.getCanonicalPath(), Action.READ, "Self", "Return the current resource \"" + this.getPath().getLast() + "\""));
        } catch (IndexOutOfBoundsException e) {
            setRelation.add(new DefaultRelation(this.getCanonicalPath(), Action.READ, "Self", "Return the current resource \"/"));
        }

        if (!(this.getPath().toString().equalsIgnoreCase("/")))
            try {
                setRelation.add(new DefaultRelation(this.getPath().getParent(), Action.READ, "Parent", "Return the parent resource \"" + this.getPath().getParent().getLast() + "\""));
            } catch (IndexOutOfBoundsException e) {
                setRelation.add(new DefaultRelation(this.getPath().getParent(), Action.READ, "Parent", "Return the parent resource \"/\""));

            }

        if (relations != null && ((getResources() != null) || !(getResources().isEmpty()))) {

            for (Resource resource : getResources()) {
                Relation currentRelation = new DefaultRelation(resource.getPath(), Action.READ, "Child:" + resource.getPath().getLast(), "Get the child  \"" + resource.getPath().getLast() + "\"");
                setRelation.add(currentRelation);
            }
            return new ArrayList<Relation>(setRelation);
        } else if (relations == null && ((getResources() != null) || !(getResources().isEmpty()))) {
            for (Resource resource : getResources()) {
                Relation currentRelation = new DefaultRelation(resource.getPath(), Action.READ, "Child:" + resource.getPath().getLast(), "Get the child  \"" + resource.getPath().getLast() + "\"");
                setRelation.add(currentRelation);
            }
            return new ArrayList<Relation>(setRelation);
        } else if (relations != null && ((getResources() == null) || (getResources().isEmpty()))) {

            return new ArrayList<Relation>(setRelation);
        } else {
            return new ArrayList<Relation>(setRelation);
        }
    }

    public DefaultResource setRelations(Collection<Relation> relations) {
        this.relations = relations.toArray(new Relation[relations.size()]);
        return this;
    }

    public List<Resource<?>> getResources(ResourceFilter filter) {
        List<Resource<?>> resources = new ArrayList<Resource<?>>();

        for (Resource res : all()) {
            if (filter.accept(res)) {
                resources.add(res);
            }
        }
        return resources;
    }

    public Resource getResource(String path) {
        List<Resource<?>> list = getResources(ResourceFilters.hasPath(path));
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void traverse(Resource<?> resource, List<Resource> list) {
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

    public T get() {
        if (ref != null) {
            return ref.get();
        } else {
            return null;
        }
    }

    /**
     * Translates this resource to the represented object. Note that some resources may not represent any object.
     * Unlike the {@link org.ow2.chameleon.everest.services.Resource#get()} method,
     * this method receives the type of the retrieved object. It is useful when the represented object can be mapped
     * to different classes.
     *
     * @param clazz class of the represented object.
     * @return the represented object, {@literal null} if resource does not represents a particular object of the given type.
     */
    @Override
    public <A> A adaptTo(Class<A> clazz) {
        T object = get();
        if (object != null && clazz.isAssignableFrom(object.getClass())) {
            return (A) object;
        }
        return null;
    }

    public boolean isObservable() {
        return false;
    }

    /**
     * A request was emitted on the current request.
     * This method handles the request.
     *
     * @param request the request.
     * @return the updated resource
     * @throws IllegalActionOnResourceException
     * @throws ResourceNotFoundException
     */
    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {

        String debug = System.getProperty(DEBUG_REQUEST);
        if (debug != null && debug.equalsIgnoreCase("true")) {
            //Trace
            System.out.println("Processing request " + request.action() + " " + request.path() + " by " +
                    getCanonicalPath());
            //End Trace
        }


        // 1) Substract our path from the request path.
        Path rel = request.path().subtract(this.getPath());

        // 2) The request is targeting us...
        if (request.path().equals(getPath())) {
            switch (request.action()) {
                case READ:
                    return read(request);
                case DELETE:
                    return delete(request);
                case CREATE:
                    return create(request);
                case UPDATE:
                    return update(request);
            }
            return null;
        }

        // 3) The request is targeting one of our child.
        Path path = getPath().add(Path.fromElements(rel.getFirst()));

        for (Resource resource : getResources()) {
            if (resource.getPath().equals(path)) {
                return resource.process(request);
            }
        }

        throw new ResourceNotFoundException(request);
    }

    /**
     * Default read action : return the current resource.
     *
     * @param request the request
     * @return the current resource
     */
    public Resource read(Request request) {
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
    public Resource create(Request request) throws IllegalActionOnResourceException {
        return null;
    }

    /**
     * Method to override to support resource update. By default it returns the current resource, unchanged.
     *
     * @param request the request
     * @return the current resource (unchanged)
     */
    public Resource update(Request request) throws IllegalActionOnResourceException {
        return this;
    }

    public static interface ResourceFactory<T extends DefaultResource<A>, A> {

        T create(Path path, ResourceMetadata metadata, Collection<Resource<?>> resources) throws
                IllegalResourceException;

        T create(Path path, A o, ResourceMetadata metadata, Collection<Resource<?>> resources) throws
                IllegalResourceException;

    }

    public static class DefaultResourceFactory<A> implements ResourceFactory<DefaultResource<A>, A> {

        public DefaultResource<A> create(Path path, ResourceMetadata metadata, Collection<Resource<?>> resources) {
            if (resources != null) {
                return new DefaultResource<A>(path, metadata, resources.toArray(new Resource[resources.size()]));
            } else {
                return new DefaultResource<A>(path, null, metadata);
            }
        }

        @Override
        public DefaultResource<A> create(Path path, A o, ResourceMetadata metadata,
                                         Collection<Resource<?>> resources) throws IllegalResourceException {
            if (resources != null) {
                return new DefaultResource<A>(path, o, metadata, resources.toArray(new Resource[resources.size()]));
            } else {
                return new DefaultResource<A>(path, o, metadata);
            }
        }
    }

    /**
     * Two resources are equals if and only if their canonical paths are equals
     *
     * @param object the object
     * @return {@literal true} if the given resource has the same canonical paths as the current resource.
     */
    public boolean equals(Object object) {
        return object instanceof Resource && getCanonicalPath().equals(((Resource) object).getCanonicalPath());
    }

    /**
     * @return the hash code of the canonical path.
     */
    public int hashCode() {
        return getCanonicalPath().hashCode();
    }

    public static class Builder<A> {

        private final ResourceFactory<? extends Resource<A>, A> factory;
        private Path path;
        private ResourceMetadata metadata;
        private Collection<Relation> relations;
        private Collection<Resource<?>> resources;
        private A object;

        public Builder() {
            factory = new DefaultResourceFactory<>();
        }

        public Builder(ResourceFactory<? extends Resource<A>, A> factory) {
            this.factory = factory;
        }

        public Builder(String path) {
            this();
            fromPath(path);
        }

        public Builder(Resource<A> resource) {
            this();
            this.path = resource.getPath();
            this.object = resource.get();
            this.metadata = resource.getMetadata();
            this.relations = resource.getRelations();
            this.resources = resource.getResources();
        }

        public Builder(Resource<A> resource, ResourceFactory<? extends Resource<A>, A> factory) {
            this(factory);
            this.path = resource.getPath();
            this.object = resource.get();
            this.metadata = resource.getMetadata();
            this.relations = resource.getRelations();
            this.resources = resource.getResources();
        }

        public Builder fromPath(String path) {
            this.path = Path.from(path);
            return this;
        }

        public Builder fromPath(Path path) {
            this.path = path;
            return this;
        }

        public Builder with(ResourceMetadata resourceMetadata) {
            this.metadata = resourceMetadata;
            return this;
        }

        public Builder with(Resource resource) {
            if (this.resources == null) {
                this.resources = new ArrayList<>();
            }
            resources.add(resource);
            return this;
        }

        public Builder with(Relation relation) {
            if (this.relations == null) {
                this.relations = new ArrayList<>();
            }
            relations.add(relation);
            return this;
        }

        public DefaultResource build() throws IllegalResourceException {
            DefaultResource<A> res;
            if (object != null) {
                res = factory.create(path, object, metadata, resources);
            } else {
                res = factory.create(path, metadata, resources);
            }

            if (relations != null) {
                res.setRelations(relations);
            }
            return res;
        }
    }
}
