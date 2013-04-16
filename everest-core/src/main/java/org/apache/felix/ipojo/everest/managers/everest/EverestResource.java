package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Everest root resource
 */
public class EverestResource extends DefaultResource {

    public static final String PATH = "/everest";
    private final Everest everest;


    public EverestResource(Everest core) {
        super(PATH);
        this.everest = core;
    }

    /**
     * Builds the sub-resources (managers) and return the list.
     * @return the list of manager resource.
     */
    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();
        for (ResourceManager manager : everest.getResourceManagers()) {
            resources.add(new ManagerResource(manager, this));
        }
        return resources;
    }
}
