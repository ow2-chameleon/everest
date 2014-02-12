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

import org.ow2.chameleon.everest.services.*;

import java.util.Collection;
import java.util.List;

/**
 * Resource the is a link to another resource.
 */
public class SymbolicLinkResource<T> implements Resource<T> {

    /**
     * The path of this symbolic link.
     */
    private final Path path;

    /**
     * The targeted resource.
     */
    private final Resource<T> target;

    /**
     * Create a new symbolic link resource
     *
     * @param path   the path of this symbolic link
     * @param target the targeted resource. May be a symbolic link too.
     */
    public SymbolicLinkResource(Path path, Resource target) {
        this.path = path;
        this.target = target;
    }

    public Resource getTarget() {
        return target;
    }

    // METHODS DELEGATED TO target

    public Path getCanonicalPath() {
        return target.getCanonicalPath();
    }

    public Path getPath() {
        return path;
    }

    public Collection<Resource<?>> getResources() {
        return target.getResources();
    }

    public ResourceMetadata getMetadata() {
        return target.getMetadata();
    }

    public Collection<Relation> getRelations() {
        return target.getRelations();
    }

    public Collection<Resource<?>> getResources(ResourceFilter filter) {
        return target.getResources(filter);
    }

    public Resource getResource(String path) {
        return target.getResource(path);
    }

    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {
        return target.process(request);
    }

    public T get() {
        return target.get();
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
        return target.adaptTo(clazz);
    }

    public boolean isObservable() {
        return target.isObservable();
    }
}
