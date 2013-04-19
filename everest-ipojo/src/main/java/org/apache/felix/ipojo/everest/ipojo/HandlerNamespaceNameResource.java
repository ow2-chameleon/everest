package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;

/**
 * '/ipojo/handler/$namespace/$name' resource, where $namespace stands for the namespace of a handler, and $name for its name.
 */
public class HandlerNamespaceNameResource extends DefaultResource {

    public HandlerNamespaceNameResource(HandlerFactory handler) {
        super(canonicalPathOf(handler));
    }

    public static Path canonicalPathOf(HandlerFactory h) {
        // Canonical path is '/ipojo/handler/$namespace/$name'
        return HandlersResource.PATH.addElements(h.getNamespace(), h.getHandlerName());
    }
}
