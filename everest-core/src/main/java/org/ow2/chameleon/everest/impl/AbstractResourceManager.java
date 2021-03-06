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

package org.ow2.chameleon.everest.impl;

import org.ow2.chameleon.everest.services.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of default 'root' resource. Root resources are the resource representing a domain and
 * tracked by the everest core.
 * By default, this resource is read-only.
 */
public abstract class AbstractResourceManager extends DefaultReadOnlyResource {

    private final String name;
    private final String description;

    public AbstractResourceManager(String name) {
        this(name, null);
    }

    public AbstractResourceManager(String name, String description) {
        super(Path.SEPARATOR + name);

        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;

        if (description == null) {
            this.description = name;
        } else {
            this.description = description;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ResourceMetadata getMetadata() {
        return new ImmutableResourceMetadata.Builder()
                .set("name", getName())
                .set("description", getDescription())
                .build();
    }

    /**
     * Extracts the direct children and add a {@literal READ} relation to them.
     *
     * @return a list of relations
     */
    public List<Relation> getRelations() {
        List<Relation> relations = new ArrayList<Relation>();
        for (Resource resource : getResources()) {
            int size = getCanonicalPath().getCount();
            String name = resource.getCanonicalPath().getElements()[size];
            relations.add(new DefaultRelation(resource.getCanonicalPath(), Action.READ, "everest:" + name,
                    "Get " + name));
        }
        return relations;
    }

}
