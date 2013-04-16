package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.Paths;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the everest entity.
 */
public class EverestResourceManager extends AbstractResourceManager {


    public static final String EVEREST_ROOT_PATH = "/everest";
    private final Everest everest;

    public EverestResourceManager(Everest everest) {
        super(EVEREST_ROOT_PATH, "The everest introspection domain");
        this.everest = everest;
    }

    @Override
    public Resource getRoot() {
        return new EverestResource(everest);
    }

    @Override
    public Resource resolve(String path) {
        Resource root = getRoot();
        if (path.equals(getName()) || path.equals(EVEREST_ROOT_PATH + Paths.PATH_SEPARATOR)) {
            return root;
        } else {
            // TODO
            return root;
        }
    }
}
