package org.apache.felix.ipojo.everest.services;

import java.util.List;

/**
 * Represents abstract resources.
 * Resources are in charge of request targeting them.
 */
public interface Resource {

    /**
     * @return the absolute path identifying the resource
     */
    String getPath();

    /**
     * Retrieves the sub-resources from the current resource.
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
     * Process a request targeting the current resource managed by this manager.
     * @param request the request.
     * @return the updated resource.
     * @throws IllegalActionOnResourceException when the action is illegal for the target resource.
     * @throws ResourceNotFoundException when the action is targeting a not available resource.
     */
    Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException;

}
