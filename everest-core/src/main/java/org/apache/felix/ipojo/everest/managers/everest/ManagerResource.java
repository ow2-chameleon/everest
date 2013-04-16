package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.Paths;
import org.apache.felix.ipojo.everest.impl.Relations;
import org.apache.felix.ipojo.everest.services.*;

import java.util.List;

/**
 * Resource representing managers.
 */
public class ManagerResource extends DefaultResource {
    private final Resource manager;

    public ManagerResource(Resource resource, EverestRootResource parent) {
        super(parent, resource.getMetadata().get("name", String.class));
        manager = resource;
    }

    @Override
    public ResourceMetadata getMetadata() {
        return ImmutableResourceMetadata.of(manager.getMetadata());
    }

    @Override
    public List<Relation> getRelations() {
        return new Relations.Builder()
                .addRelation(getPath(), Action.GET, "everest:manager",
                        "get metadata about the manager")
                .addRelation(manager.getCanonicalPath(), Action.GET, "everest:domain",
                        "get the root of the domain '" + manager.getMetadata().get("name", String.class) + "'")
                .build();
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource put(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource post(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }
}
