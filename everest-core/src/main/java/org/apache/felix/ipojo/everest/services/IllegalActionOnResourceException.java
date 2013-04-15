package org.apache.felix.ipojo.everest.services;

/**
 * Exception thrown when a request is illegal for the targeted resource.
 */
public class IllegalActionOnResourceException extends Exception {

    private final Request request;

    private final Resource resource;

    public IllegalActionOnResourceException(Request request, Resource resource) {
        this.request = request;
        this.resource = resource;
    }

    public Request getRequest() {
        return request;
    }

    public Resource getResource() {
        return resource;
    }
}
