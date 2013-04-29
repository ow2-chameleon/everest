package org.apache.felix.ipojo.everest.services;

/**
 * An interface to filter relations.
 */
public interface RelationFilter {

    boolean accept(Relation relation);
}
