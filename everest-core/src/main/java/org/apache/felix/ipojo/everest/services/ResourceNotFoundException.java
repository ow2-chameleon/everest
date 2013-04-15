package org.apache.felix.ipojo.everest.services;

/**
 * Exception thrown when a resource does not exist.
 */
public class ResourceNotFoundException extends Exception {

    private final Request request;

    public ResourceNotFoundException(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
