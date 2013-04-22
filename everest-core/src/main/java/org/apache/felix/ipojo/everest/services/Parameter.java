package org.apache.felix.ipojo.everest.services;

/**
 * Represents relation parameter
 */
public interface Parameter {

    String name();

    Class type();

    String description();

    boolean optional();
}
