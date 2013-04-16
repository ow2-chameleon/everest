package org.apache.felix.ipojo.everest.services;

/**
 * Exception thrown when an exception cannot be created.
 */
public class IllegalResourceException extends Exception {

    public IllegalResourceException(String s, Throwable e) {
        super(s, e);
    }
}
