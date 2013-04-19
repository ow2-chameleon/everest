package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;

/**
 * '/ipojo/handler' resource.
 */
public class HandlersResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("handler");

    public HandlersResource() {
        super(PATH);
    }

    public void addHandler(HandlerFactory handler) {
        //TODO
    }

    public void removeHandler(HandlerFactory handler) {
        //TODO
    }

}
