package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.AbstractResourceManager;
import org.apache.felix.ipojo.everest.impl.Paths;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Request;
import org.apache.felix.ipojo.everest.services.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages the everest entity.
 */
public class EverestRootResource extends AbstractResourceManager {


    public static final String EVEREST_ROOT_PATH = "everest";
    private final Everest everest;

    public EverestRootResource(Everest everest) {
        super(EVEREST_ROOT_PATH, "The everest introspection domain");
        this.everest = everest;
    }

    public List<Resource> getResources() {
        List<Resource> domains = new ArrayList<Resource>();
        for (Map.Entry<Path, Resource> entry : everest.getEverestResources().entrySet()) {
            domains.add(new ManagerResource(entry.getValue(), this));
        }
        return domains;
    }
}
