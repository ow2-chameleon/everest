package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.core.Everest;
import org.apache.felix.ipojo.everest.filters.ResourceFilters;
import org.apache.felix.ipojo.everest.managers.everest.EverestRootResource;
import org.apache.felix.ipojo.everest.services.*;
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
