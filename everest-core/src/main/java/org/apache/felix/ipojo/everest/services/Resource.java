package org.apache.felix.ipojo.everest.services;

import java.util.List;

/**
 * Represents abstract resources.
 * Resources are in charge of request targeting them.
 */
public interface Resource {

    /**
     * @return the path of the resource.
     */
    Path getPath();

    /**
     * @return the canonical path of the resource. Two resources are equals if their canonical path are equals.
     */
    Path getCanonicalPath();

    /**
     * Retrieves the sub-resources from the current resource.
     *
     * @return the sub-resources, empty is the resource is a leaf.
     */
    List<Resource> getResources();

    /**
     * @return resource metadata.
     */
    ResourceMetadata getMetadata();

    /**
     * @return the list of relations related to the current resource.
     */
    List<Relation> getRelations();

    /**
     * Finds all resources managed by this manager matching the given filter.
     *
     * @param filter the filter.
     * @return the set of matching resources, empty if none match.
     */
    List<Resource> getResources(ResourceFilter filter);

    /**
     * Gets a resource identified by its path.
     *
     * @param path the path
     * @return the resource identified by the given path, {@literal null} if not found.
     */
    Resource getResource(String path);

    /**
     * Process a request targeting the current resource managed by this manager.
     *
     * @param request the request.
     * @return the updated resource.
     * @throws IllegalActionOnResourceException
     *                                   when the action is illegal for the target resource.
     * @throws ResourceNotFoundException when the action is targeting a not available resource.
     */
    Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException;

    /**
     * Translates this resource to the represented object. Note that some resources may not represent any object.
     *
     * @param clazz class of the represented object.
     * @param <A>   type of the represented object.
     * @return the represented object, {@literal null} if resource does not represents a particular object of the given type.
     */
    <A> A adaptTo(Class<A> clazz);

    /**
     * Observable resources send events through event admin about changes in the resource state.
     * Any observers can listen events through event admin using the canonical path of the resource as the event topic.
     *
     * @return true if the resource is observable.
     */
    boolean isObservable();

}
