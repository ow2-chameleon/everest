/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.managers.everest;

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.impl.*;
import org.ow2.chameleon.everest.services.*;

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
