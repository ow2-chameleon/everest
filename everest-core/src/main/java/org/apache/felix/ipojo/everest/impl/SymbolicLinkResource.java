package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.*;

import java.util.List;

/**
 * Resource the is a link to another resource.
 */
public class SymbolicLinkResource implements Resource {

    /**
     * The path of this symbolic link.
     */
    private final Path path;

    /**
     * The targeted resource.
     */
    private final Resource target;

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

    public List<Resource> getResources() {
        return target.getResources();
    }

    public ResourceMetadata getMetadata() {
        return target.getMetadata();
    }

    public List<Relation> getRelations() {
        return target.getRelations();
    }

    public List<Resource> getResources(ResourceFilter filter) {
        return target.getResources(filter);
    }

    public Resource getResource(String path) {
        return target.getResource(path);
    }

    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {
        return target.process(request);
    }

    public <A> A adaptTo(Class<A> clazz) {
        return target.adaptTo(clazz);
    }
}
