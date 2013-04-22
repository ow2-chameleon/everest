package org.apache.felix.ipojo.everest.services;

import java.util.List;
import java.util.Map;

/**
 * Relations are implementing 'Hypermedia as the Engine of Application State'.
 * Each resource gives a list of links.
 *
 * From a relation a request can be emitted.
 */
public interface Relation {

    /**
     * @return The path of the resource that will process the request.
     */
    Path getHref();

    /**
     * @return The action of the request to emit
     */
    Action getAction();

    /**
     * @return The relation name
     */
    String getName();

    /**
     * @return A description of the relation
     */
    String getDescription();

    List<Parameter> getParameters();

}
