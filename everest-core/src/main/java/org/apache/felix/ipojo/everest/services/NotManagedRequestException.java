package org.apache.felix.ipojo.everest.services;

/**
 * Exception thrown when no manager can handle the given request.
 */
public class NotManagedRequestException extends Exception {

    private final Request request;

    public NotManagedRequestException(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
