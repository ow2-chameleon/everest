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

package org.ow2.chameleon.everest.osgi;

import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.IllegalResourceException;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Collection of resources that is initiated with a path and a map of name,metadata for its children.
 * Children are only resource with static metadata
 */
public class ReadOnlyLeafCollectionResource extends AbstractResourceCollection {

    /**
     * Map of metadata
     */
    private final Map<String, ResourceMetadata> m_leafMetadata;

    /**
     * Constructor for this resource collection
     *
     * @param path         path of the parent resource
     * @param leafMetadata {@code Map<String,ResourceMetadata>} name and metadata of the children
     */
    public ReadOnlyLeafCollectionResource(Path path, Map<String, ResourceMetadata> leafMetadata) {
        super(path);
        m_leafMetadata = leafMetadata;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String key : m_leafMetadata.keySet()) {
            metadataBuilder.set(key, null);
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (Entry<String, ResourceMetadata> leaf : m_leafMetadata.entrySet()) {
            Path leafPath = getPath().add(Path.from(Path.SEPARATOR + leaf.getKey()));
            Resource resource;
            try {
                resource = new Builder().fromPath(leafPath).with(leaf.getValue()).build();
            } catch (IllegalResourceException e) {
                throw new RuntimeException(e.getMessage());
            }
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

}
