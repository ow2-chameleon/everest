package org.apache.felix.ipojo.everest.services;

import java.util.List;

/**
 * Service interface defining a set of resources.
 */
public interface ResourceManager {

    /**
     * @return the name. Resources would be accessed using {@literal /name}.
     */
    String getName();

    /**
     * @return a friendly description of the manager.
     */
    String getDescription();

    /**
     * Gets a resource identified by its path.
     * @param path the path
     * @return the resource identified by the given path, {@literal null} if not found.
     */
    Resource getResource(String path);

    /**
     * Finds all resources from a set of (transitive) sub-resources from the given resource matching the given filter.
     * @param resource the root resource
     * @param filter a filter
     * @return the set of matching resources, empty if none match.
     */
    List<Resource> getResources(Resource resource, ResourceFilter filter);

    /**
     * Finds all resources managed by this manager matching the given filter.
     * @param filter the filter.
     * @return the set of matching resources, empty if none match.
     */
    List<Resource> getResources(ResourceFilter filter);

    /**
     * Asks the resource manager to resolve the given path.
     * Resources may have different paths, this methods search for the right resource and returns the targeted
     * resource. If the path is the normalized absolute path (with the manager's name as root),
     * this method is equivalent to {@link #getResource(String)}.
     * @param path the path
     * @return the resolved resource.
     */
    Resource resolve(String path);

    /**
     * Process a request targeting the current resource managed by this manager.
     * @param request the request.
     * @return the updated resource.
     * @throws IllegalActionOnResourceException when the action is illegal for the target resource.
     * @throws ResourceNotFoundException when the action is targeting a not available resource.
     */
    Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException;

}
