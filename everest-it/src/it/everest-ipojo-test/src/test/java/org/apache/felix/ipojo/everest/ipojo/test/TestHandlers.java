package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for handler resources.
 */
public class TestHandlers extends Common {

    private static final String IPOJO_NS = "org.apache.felix.ipojo";
    private static final String[] CORE_HANDLERS = {"properties", "provides", "controller", "callback", "architecture", "requires"};

    /**
     * Test that the resource representing all the handlers has the expected content.
     */
    @Test
    public void testReadHandlers() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all handlers
        Resource handlers = read("/ipojo/handler");
        ResourceMetadata meta = handlers.getMetadata();
        assertThat(meta).isNotNull();

        // Check core handlers are present
        ResourceMetadata coreHandlers = meta.get(IPOJO_NS, ResourceMetadata.class);
        assertThat(coreHandlers).isNotNull();
        assertThat(coreHandlers).hasSize(6);
        for (String name : CORE_HANDLERS) {
            ResourceMetadata h = coreHandlers.get(name, ResourceMetadata.class);
            assertThat(h).describedAs("core handler " + IPOJO_NS + ":" + name).isNotNull();
            assertThat(h.get("namespace")).isEqualTo(IPOJO_NS);
            assertThat(h.get("name")).isEqualTo(name);

        }

        // Check the test handler is here
        ResourceMetadata fooBarHandlers = meta.get("foo.bar", ResourceMetadata.class);
        assertThat(fooBarHandlers).isNotNull();
        assertThat(fooBarHandlers).hasSize(1);
        ResourceMetadata quxHandler = fooBarHandlers.get("qux", ResourceMetadata.class);
        assertThat(quxHandler).isNotNull();
        assertThat(quxHandler.get("namespace")).isEqualTo("foo.bar");
        assertThat(quxHandler.get("name")).isEqualTo("qux");
    }

    // Test that illegal actions on /ipojo/handler/org.apache.felix.ipojo are illegal

    @Test(expected = IllegalActionOnResourceException.class)
    public void testCreateOnCoreHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.CREATE, Path.from("/ipojo/handler/" + IPOJO_NS), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testUpdateOnCoreHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.UPDATE, Path.from("/ipojo/handler/" + IPOJO_NS), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testDeleteOnCoreHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.DELETE, Path.from("/ipojo/handler/" + IPOJO_NS), null));
    }

    /**
     * Test that the resource representing all the handlers has the expected content.
     */
    @Test
    public void testReadCoreHandlers() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all handlers
        Resource handlers = read("/ipojo/handler/" + IPOJO_NS);
        ResourceMetadata meta = handlers.getMetadata();
        assertThat(meta).isNotNull();
        assertThat(meta).hasSize(6);

        // Check core handlers are present
        for (String name : CORE_HANDLERS) {
            ResourceMetadata h = meta.get(name, ResourceMetadata.class);
            assertThat(h).describedAs("core handler " + IPOJO_NS + ":" + name).isNotNull();
            assertThat(h.get("namespace")).isEqualTo(IPOJO_NS);
            assertThat(h.get("name")).isEqualTo(name);

        }
    }

    // Test that illegal actions on /ipojo/handler are illegal

    @Test(expected = IllegalActionOnResourceException.class)
    public void testCreateOnHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.CREATE, Path.from("/ipojo/handler"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testUpdateOnHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.UPDATE, Path.from("/ipojo/handler"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testDeleteOnHandlersIsIllegal() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.DELETE, Path.from("/ipojo/handler"), null));
    }

    /**
     * Test that the resource representing all the handlers has the expected content.
     */
    @Test
    public void testReadFooBarHandlers() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all handlers
        Resource handlers = read("/ipojo/handler/foo.bar");
        ResourceMetadata meta = handlers.getMetadata();
        assertThat(meta).isNotNull();
        assertThat(meta).hasSize(1);

        ResourceMetadata quxHandler = meta.get("qux", ResourceMetadata.class);
        assertThat(quxHandler).isNotNull();
        assertThat(quxHandler.get("namespace")).isEqualTo("foo.bar");
        assertThat(quxHandler.get("name")).isEqualTo("qux");
    }

}
