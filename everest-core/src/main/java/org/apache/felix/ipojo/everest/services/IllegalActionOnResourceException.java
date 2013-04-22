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

    public IllegalActionOnResourceException(Request request, Resource resource, String message) {
        this.getMessage();
        this.request = request;
        this.resource = resource;
    }

    public IllegalActionOnResourceException(Request request, String message) {
        super(message);
        this.resource = null;
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public Resource getResource() {
        return resource;
    }
}
