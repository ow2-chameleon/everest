package org.apache.felix.ipojo.everest.services;

/**
 * Relations are implementing 'Hypermedia as the Engine of Application State'.
 * Each resource gives a list of links
 */
public interface Relation {

    String getPath();

    Action getAction();

    String getName();

    String getDescription();

}
