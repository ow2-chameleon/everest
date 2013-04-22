package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.*;

public class TestFactories extends Common {

    @Test
    public void testFooFactoryResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource foo = read("/ipojo/factory/Foo/1.2.3.foo");
        ResourceMetadata meta = foo.getMetadata();

        // Check name, version , class name
        assertThat(meta.get("name")).isEqualTo("Foo");
        assertThat(meta.get("version")).isEqualTo("1.2.3.foo");
        assertThat(meta.get("className")).isEqualTo("org.apache.felix.ipojo.everest.ipojo.components.FooProviderImpl");

        // Check state
        assertThat(meta.get("state")).isEqualTo("valid");

        // Check missing handler
        assertThat(meta.get("missingHandlers", List.class)).isEmpty();
    }

    @Test
    public void tesBarFactoryResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource bar = read("/ipojo/factory/org.apache.felix.ipojo.everest.ipojo.components.BarProviderImpl/null");
    }

}
