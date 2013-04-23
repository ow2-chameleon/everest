package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.*;

public class TestFactories extends Common {

    private static final String BAR = "org.apache.felix.ipojo.everest.ipojo.components.BarProviderImpl";
    private static final String BAR_2 = "org.apache.felix.ipojo.everest.ipojo.components.BarProviderImpl2";

    /**
     * Test that the resource representing all the factories has the expected content.
     */
    @Test
    public void testReadFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all factories
        Resource factories = read("/ipojo/factory");
        ResourceMetadata meta = factories.getMetadata();
        assertThat(meta).isNotNull();

        // Check that the metadata of that version are correct
        ResourceMetadata fooVersions = meta.get("Foo", ResourceMetadata.class);
        assertThat(fooVersions).isNotNull();
        assertThat(fooVersions).hasSize(1);

        ResourceMetadata foo = fooVersions.get("1.2.3.foo", ResourceMetadata.class);
        assertThat(foo).isNotNull();
        assertThat(foo.get("name")).isEqualTo("Foo");
        assertThat(foo.get("version")).isEqualTo("1.2.3.foo");

        ResourceMetadata barVersions = meta.get(BAR, ResourceMetadata.class);
        assertThat(barVersions).isNotNull();
        assertThat(barVersions).hasSize(2);

        ResourceMetadata barNull = barVersions.get(null, ResourceMetadata.class);
        assertThat(barNull).isNotNull();
        assertThat(barNull.get("name")).isEqualTo(BAR);
        assertThat(barNull.get("version")).isEqualTo(null);

        ResourceMetadata bar2 = barVersions.get("2.0.0", ResourceMetadata.class);
        assertThat(bar2).isNotNull();
        assertThat(bar2.get("name")).isEqualTo(BAR);
        assertThat(bar2.get("version")).isEqualTo("2.0.0");
    }

    /**
     * Test that the resource representing all the factories named "Foo" has the expected content.
     */
    @Test
    public void testReadFooFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all factories named "Foo"
        Resource factories = read("/ipojo/factory/Foo");
        ResourceMetadata meta = factories.getMetadata();
        assertThat(meta).hasSize(1);

        // Check that the metadata of that version are correct
        ResourceMetadata foo = meta.get("1.2.3.foo", ResourceMetadata.class);
        assertThat(foo).isNotNull();
        assertThat(foo.get("name")).isEqualTo("Foo");
        assertThat(foo.get("version")).isEqualTo("1.2.3.foo");
    }

    /**
     * Test that the resource representing all the factories named "...BarProviderImpl" has the expected content.
     */
    @Test
    public void testReadBarFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read all factories named "$BAR"
        Resource factories = read("/ipojo/factory/" + BAR);
        ResourceMetadata meta = factories.getMetadata();
        assertThat(meta).hasSize(2);

        // Check that the metadata of null version are correct
        ResourceMetadata barNull = meta.get(null, ResourceMetadata.class);
        assertThat(barNull).isNotNull();
        assertThat(barNull.get("name")).isEqualTo(BAR);
        assertThat(barNull.get("version")).isEqualTo(null);

        // Check that the metadata of null version are correct
        ResourceMetadata bar2 = meta.get("2.0.0", ResourceMetadata.class);
        assertThat(bar2).isNotNull();
        assertThat(bar2.get("name")).isEqualTo(BAR);
        assertThat(bar2.get("version")).isEqualTo("2.0.0");
    }

    /**
     * Test that the resource representing Foo factory has the expected metadata.
     */
    @Test
    public void testReadFooFactory() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read metadata of Foo factory
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

        //TODO Check more, as soon as more metadata are provided...
    }

    /**
     * Test that the resource representing Bar factory with no (null) version has the expected metadata.
     */
    @Test
    public void testReadBarNullFactory() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read metadata of Foo factory
        Resource barNull = read("/ipojo/factory/" + BAR + "/null");
        ResourceMetadata meta = barNull.getMetadata();

        // Check name, version , class name
        assertThat(meta.get("name")).isEqualTo(BAR);
        assertThat(meta.get("version")).isEqualTo(null);
        assertThat(meta.get("className")).isEqualTo(BAR);

        // Check state
        assertThat(meta.get("state")).isEqualTo("valid");

        // Check missing handler
        assertThat(meta.get("missingHandlers", List.class)).isEmpty();

        //TODO Check more, as soon as more metadata are provided...
    }

    /**
     * Test that the resource representing Bar factory with "2.0.0" version has the expected metadata.
     */
    @Test
    public void testReadBar2Factory() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Read metadata of Foo factory
        Resource bar2 = read("/ipojo/factory/" + BAR + "/2.0.0");
        ResourceMetadata meta = bar2.getMetadata();

        // Check name, version , class name
        assertThat(meta.get("name")).isEqualTo(BAR);
        assertThat(meta.get("version")).isEqualTo("2.0.0");
        assertThat(meta.get("className")).isEqualTo(BAR_2);

        // Check state
        assertThat(meta.get("state")).isEqualTo("valid");

        // Check missing handler
        assertThat(meta.get("missingHandlers", List.class)).isEmpty();

        //TODO Check more, as soon as more metadata are provided...
    }

    // ========================================================================
    // Destructive tests that MUST be executed at the very end of this suite!!!

    /**
     * Test that DELETE action on resource representing Bar factory 2.0.0 has the expected behavior.
     */
    @Test
    public void testDeleteBar2Factory() throws ResourceNotFoundException, IllegalActionOnResourceException, InvalidSyntaxException {
        // Check refs before
        Collection<ServiceReference<Factory>> refs1 = bc.getServiceReferences(Factory.class, "(factory.name=" + BAR + ")");
        assertThat(refs1).hasSize(2);

        // Delete factory Bar v2.0.0
        Request req = new DefaultRequest(Action.DELETE, Path.from("/ipojo/factory/" + BAR + "/2.0.0"), null);
        Resource result = everest.process(req);
        //TODO what is result? what can we check on it?
        //assertThat(result).isNull();

        // Check that the service reference has gone.
        Collection<ServiceReference<Factory>> refs2 = bc.getServiceReferences(Factory.class, "(factory.name=" + BAR + ")");
        assertThat(refs2).hasSize(1);

        // Check that accessing Bar version null still works
        assertThat(read("/ipojo/factory/" + BAR + "/null")).isNotNull();

        // Check that accessing Bar version "2.0.0" fails miserably
        try {
            read("/ipojo/factory/" + BAR + "/2.0.0");
            org.junit.Assert.fail("/ipojo/factory/" + BAR + "/2.0.0 should not exist anymore" );
        } catch (ResourceNotFoundException e) {
            // Ok : that's normal!
        }
    }

}
