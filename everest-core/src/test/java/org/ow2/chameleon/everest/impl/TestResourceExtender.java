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

import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.filters.ResourceFilters;
import org.ow2.chameleon.everest.managers.everest.EverestRootResource;
import org.ow2.chameleon.everest.services.*;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test resource extenders.
 */
public class TestResourceExtender {

    private final Everest everest = new Everest();

    @Test
    public void testExtendedResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestRootResource root = new EverestRootResource(everest);
        everest.bindRootResource(root);
        everest.bindExtender(new MyResourceExtender());

        // Extended request.
        Request request = new DefaultRequest(Action.READ, Path.from("/everest/domains/everest"), null);
        Resource resource = everest.process(request);

        assertThat(resource.getMetadata().get("extended", Boolean.class)).isEqualTo(true);
    }

    @Test
    public void testNotExtendedResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestRootResource root = new EverestRootResource(everest);
        everest.bindRootResource(root);
        everest.bindExtender(new MyResourceExtender());

        // Extended request.
        Request request = new DefaultRequest(Action.READ, Path.from("/everest"), null);
        Resource resource = everest.process(request);

        assertThat(resource.getMetadata().get("extended", Boolean.class)).isNull();
    }

    private class MyResourceExtender implements ResourceExtender {

        public ResourceFilter getFilter() {
            Resource res = everest.getResource("/everest/domains/everest");
            return ResourceFilters.hasPath(res.getPath());
        }

        public Resource extend(Request request, Resource resource) {
            ResourceMetadata metadata = new ImmutableResourceMetadata.Builder(resource.getMetadata())
                    .set("extended", true).build();
            try {
                return new DefaultResource.Builder(resource).with(metadata).build();
            } catch (IllegalResourceException e) {
                return resource;
            }
        }
    }

}
