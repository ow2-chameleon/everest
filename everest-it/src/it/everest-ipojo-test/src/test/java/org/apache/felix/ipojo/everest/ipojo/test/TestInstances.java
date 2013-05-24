package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.test.b1.FooService;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.filters.RelationFilters.*;
import static org.apache.felix.ipojo.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for instance resources.
 */
public class TestInstances extends EverestIpojoTestCommon {

    /**
     * Check that a "good" CREATE request to the path of an non-existing instance actually creates the instance.
     */
    @Test
    public void testInstanceCreation() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("factory.name", "Foo");
        params.put("factory.version", "1.2.3.foo");
        params.put("factory.version", "1.2.3.foo");
        params.put("fooPrefix", "__configured");
        params.put("fooCounter", 666);

        // Request instance creation
        Request req = new DefaultRequest(Action.CREATE, Path.from("/ipojo/instance/Foo-2001"), params);
        Resource result = everest.process(req);
        assertThat(result).isNotNull();

        // Read metadata of resulting resource
        ResourceMetadata meta = result.getMetadata();

        // Check standard properties
        assertThat(meta.get("name", String.class)).isEqualTo("Foo-2001");
        assertThat(meta.get("state", String.class)).isEqualTo("valid");
        assertThat(meta.get("factory.name", String.class)).isEqualTo("Foo");
        assertThat(meta.get("factory.version", String.class)).isEqualTo("1.2.3.foo");

        // Check configuration has been taken into consideration.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), "Foo-2001");
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("fooCounter")).isEqualTo(666);

        FooService foo = (FooService) context.getService(ref);
        assertThat(foo.getFoo()).isEqualTo("__configured666");

        // Check relation to factory
        assertThatResource(result).hasRelation(and(hasName("factory"), hasAction(Action.READ), hasHref("/ipojo/factory/Foo/1.2.3.foo")));

        // Check relation from factory to instance
        assertThatResource(read("/ipojo/factory/Foo/1.2.3.foo")).hasRelation(and(hasName("instance:Foo-2001"), hasAction(Action.READ), hasHref(result)));
    }

}
