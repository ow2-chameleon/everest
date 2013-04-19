package org.apache.felix.ipojo.everest.services;

/**
 * Everest entry point.
 */
public interface EverestService {
    Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException;
}
