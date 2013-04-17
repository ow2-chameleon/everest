package org.apache.felix.ipojo.everest.services;

/**
 * Resource extender service specification.
 * Resource extender <i>extends</i> the resources returned by some requests.
 */
public interface ResourceExtender {

    /**
     * The resource filter used to detects whether this extender is going to extend the resource returned by a request.
     * The returned filter is called with the returned resource.
     * @return the resource filter.
     */
    ResourceFilter getFilter();

    /**
     * Extends the given resources returned by the given request.
     * @param request the request
     * @param resource the resource
     * @return the extended resource.
     */
    Resource extend(Request request, Resource resource);


}
