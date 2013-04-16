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
    private final ResourceManager manager;

    public ManagerResource(ResourceManager manager, EverestResource parent) {
        super(parent, manager.getName());
        this.manager = manager;
    }

    @Override
    public ResourceMetadata getMetadata() {
        return new ImmutableResourceMetadata.Builder()
                .set("name", manager.getName())
                .set("description", manager.getDescription())
                .build();
    }

    @Override
    public List<Relation> getRelations() {
        return new Relations.Builder()
                .addRelation(getPath(), Action.GET, "everest:manager",
                        "get metadata on the manager")
                .addRelation(Paths.PATH_SEPARATOR + manager.getName(), Action.GET, "everest:domain",
                        "get the root of the domain '" + manager.getName() + "'")
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
