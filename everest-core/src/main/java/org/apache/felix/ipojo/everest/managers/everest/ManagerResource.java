package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.impl.*;
import org.apache.felix.ipojo.everest.services.*;

import java.util.List;

/**
 * Resource representing managers.
 */
public class ManagerResource extends DefaultReadOnlyResource {
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
}
