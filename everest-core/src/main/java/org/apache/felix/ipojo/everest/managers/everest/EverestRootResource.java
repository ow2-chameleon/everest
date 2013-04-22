package org.apache.felix.ipojo.everest.managers.everest;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.impl.*;
import org.apache.felix.ipojo.everest.services.*;

import java.util.*;

/**
 * Manages the everest entity.
 * The model is the following:
 * <pre>
 *     /everest <- root
 *          /domains <- domain list
 *              /domain <- domain metadata
 * </pre>
 *
 * TODO How to resource-ify extenders
 */
public class EverestRootResource extends AbstractResourceManager {


    public static final String EVEREST_ROOT_PATH = "everest";
    private final Everest everest;

    public EverestRootResource(Everest everest) {
        super(EVEREST_ROOT_PATH, "The everest introspection domain");
        this.everest = everest;
    }

    public List<Resource> getResources() {
        List<Resource> list = new ArrayList<Resource>();
        try {
            list.add(getDomains());
        } catch (IllegalResourceException e) {
            // TODO Log.
        }
        return list;
    }

    private DefaultResource getDomains() throws IllegalResourceException {
        DefaultResource.Builder domains = new Builder()
                .fromPath(getCanonicalPath() + "/domains");

        // For each root, define a manager resource, and insert a relation
        for (Map.Entry<Path, Resource> entry : everest.getEverestResources().entrySet()) {
            domains.with(new ManagerResource(entry.getValue()));
            domains.with(
                    new DefaultRelation(entry.getValue(), Action.READ,
                            "everest:getDomain(" + entry.getValue().getMetadata().get("name", String.class) + ")")
            );
        }

        return domains.build();
    }

}
