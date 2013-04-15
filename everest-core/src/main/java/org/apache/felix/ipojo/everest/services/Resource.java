package org.apache.felix.ipojo.everest.services;

import java.util.List;

/**
 * Represents abstract resources.
 */
public interface Resource {

    /**
     * @return the path identifying the resource
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

}
