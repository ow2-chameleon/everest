package org.apache.felix.ipojo.everest.services;

/**
 * An interface to filter resources.
 */
public interface ResourceFilter {

    boolean accept(Resource resource);
}
