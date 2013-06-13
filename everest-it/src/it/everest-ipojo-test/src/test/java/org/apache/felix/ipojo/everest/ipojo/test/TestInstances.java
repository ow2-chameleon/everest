package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.filters.RelationFilters;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.test.b1.BarService;
import org.apache.felix.ipojo.everest.ipojo.test.b1.FooService;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.apache.felix.ipojo.everest.services.Action.READ;
import static org.apache.felix.ipojo.everest.services.Action.UPDATE;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.osgi.framework.Constants.SERVICE_ID;

/**
 * Test for instance resources.
 */
public class TestInstances extends EverestIpojoTestCommon {

    @Before
    public void setup() {
        // Create a Foo instance so Bar instances can be valid
        ipojoHelper.createComponentInstance("Foo");
    }

    /**
     * Read /ipojo/instance
     */
    @Test
    public void testReadInstances() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/instance");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/instance/DeclaredFoo123
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instance[DeclaredFoo123]"),
                RelationFilters.hasHref("/ipojo/instance/DeclaredFoo123")));
    }

    /**
     * Read /ipojo/instance
     */
    @Test
    public void testReadInstanceDeclaredFoo123() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/instance/DeclaredFoo123");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check name, factory
        assertThat(m.get("name")).isEqualTo("DeclaredFoo123");
        assertThat(m.get("factory.name")).isEqualTo("Foo");
        assertThat(m.get("factory.version")).isEqualTo("1.2.3.foo");
        // Check state
        assertThat(m.get("state")).isEqualTo("valid");
        //TODO Check more metadata, as soon as more metadata are provided...
        // Check adaptations
        assertThat(r.adaptTo(Architecture.class)).isSameAs(getArchitecture("DeclaredFoo123"));
        assertThat(r.adaptTo(ComponentInstance.class)).isSameAs(getComponentInstance("DeclaredFoo123"));
        // Check relation on factory resource
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("factory"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/ipojo/factory/Foo/1.2.3.foo")));
        // Check relation on Architecture service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("service"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/services/" + getArchitectureReference("DeclaredFoo123").getProperty(Constants.SERVICE_ID))));
    }

    /**
     * Create /ipojo/instance/Foo-2001 with configuration
     */
    @Test
    public void testCreateFoo2001() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Request creation on instance Foo-2001 resource, with configuration
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("factory.name", "Foo");
        params.put("factory.version", "1.2.3.foo");
        params.put("fooPrefix", "__configured");
        params.put("fooCounter", 666);
        Resource r = everest.process(
                new DefaultRequest(Action.CREATE, Path.from("/ipojo/instance/Foo-2001"), params));
        // Check name, factory, state
        ResourceMetadata m = r.getMetadata();
        assertThat(m.get("name", String.class)).isEqualTo("Foo-2001");
        assertThat(m.get("factory.name", String.class)).isEqualTo("Foo");
        assertThat(m.get("factory.version", String.class)).isEqualTo("1.2.3.foo");
        assertThat(m.get("state", String.class)).isEqualTo("valid");
        // Check configuration has been taken into consideration.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), "Foo-2001");
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("fooCounter")).isEqualTo(666);
        FooService foo = (FooService) context.getService(ref);
        assertThat(foo.getFoo()).isEqualTo("__configured666");
        // Check relation from instance to factory
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("factory"),
                RelationFilters.hasHref("/ipojo/factory/Foo/1.2.3.foo")));
        // Check relation from factory to instance
        assertThatResource(read("/ipojo/factory/Foo/1.2.3.foo")).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instance[Foo-2001]"),
                RelationFilters.hasHref(r)));
    }

    /**
     * Create /ipojo/instance/Bar-2002 with configuration
     */
    @Test
    public void testCreateBar2002() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Request creation on instance Foo-2001 resource, with configuration
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("factory.name", BAR);
        params.put("barPrefix", "__configured");
        params.put("barSuffix", "configured__");
        Resource r = everest.process(
                new DefaultRequest(Action.CREATE, Path.from("/ipojo/instance/Bar-2002"), params));
        // Check name, factory, state
        ResourceMetadata m = r.getMetadata();
        assertThat(m.get("name", String.class)).isEqualTo("Bar-2002");
        assertThat(m.get("factory.name", String.class)).isEqualTo(BAR);
        assertThat(m.get("factory.version", String.class)).isNull();
        assertThat(m.get("state", String.class)).isEqualTo("valid");
        // Check configuration has been taken into consideration.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(BarService.class.getName(), "Bar-2002");
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("barSuffix")).isEqualTo("configured__");
        BarService bar = (BarService) context.getService(ref);
        String s = bar.getBar();
        assertThat(s).startsWith("__configured");
        assertThat(s).endsWith("configured__");
        // Check relation from instance to factory
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("factory"),
                RelationFilters.hasHref("/ipojo/factory/" + BAR + "/null")));
        // Check relation from factory to instance
        assertThatResource(read("/ipojo/factory/" + BAR + "/null")).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instance[Bar-2002]"),
                RelationFilters.hasHref(r)));
    }

    /**
     * Create /ipojo/instance/Bar-2003 with configuration
     */
    @Test
    public void testCreateBar2003() throws ResourceNotFoundException, IllegalActionOnResourceException {
        // Request creation on instance Foo-2001 resource, with configuration
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("factory.name", BAR);
        params.put("factory.version", "2.0.0");
        params.put("barPrefix", "__yet__another");
        params.put("barSuffix", "bar__service__");
        Resource r = everest.process(
                new DefaultRequest(Action.CREATE, Path.from("/ipojo/instance/Bar-2003"), params));
        // Check name, factory, state
        ResourceMetadata m = r.getMetadata();
        assertThat(m.get("name", String.class)).isEqualTo("Bar-2003");
        assertThat(m.get("factory.name", String.class)).isEqualTo(BAR);
        assertThat(m.get("factory.version", String.class)).isEqualTo("2.0.0");
        assertThat(m.get("state", String.class)).isEqualTo("valid");
        // Check configuration has been taken into consideration.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(BarService.class.getName(), "Bar-2003");
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("barSuffix")).isEqualTo("bar__service__");
        BarService bar = (BarService) context.getService(ref);
        String s = bar.getBar();
        assertThat(s).startsWith("__yet__another");
        assertThat(s).endsWith("bar__service__-v2.0.0");
        // Check relation from instance to factory
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("factory"),
                RelationFilters.hasHref("/ipojo/factory/" + BAR + "/2.0.0")));
        // Check relation from factory to instance
        assertThatResource(read("/ipojo/factory/" + BAR + "/2.0.0")).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instance[Bar-2003]"),
                RelationFilters.hasHref(r)));
    }

    /**
     * Update /ipojo/instance/Foo-2001 with "configuration" parameter.
     */
    @Test
    public void testReconfiguration() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/instance/Foo-2001");

        // Reconfigure the instance and check
        Map<String, Object> configuration = new HashMap<String, Object>();
        configuration.put("fooPrefix", "__reconfigured");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("configuration", configuration);
        r = everest.process(new DefaultRequest(UPDATE, Path.from("/ipojo/instance/Foo-2001"), params));
        assertThat(r.getMetadata().get("configuration", Map.class).get("fooPrefix")).isEqualTo("__reconfigured");
    }

    /**
     * Update /ipojo/instance/Foo-2001 with "state" parameter.
     */
    @Test
    public void testStateChange() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/instance/Foo-2001");

        // Check state is "valid"
        assertThat(r.getMetadata().get("state", String.class)).isEqualTo("valid");

        // Set set to "stopped" and check
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", "stopped");
        r = everest.process(new DefaultRequest(UPDATE, Path.from("/ipojo/instance/Foo-2001"), params));
        assertThat(r.getMetadata().get("state", String.class)).isEqualTo("stopped");

        // Set set to "invalid" and check
        // Component state should be "valid", as validity cannot be forced externally.
        params.put("state", "invalid");
        r = everest.process(new DefaultRequest(UPDATE, Path.from("/ipojo/instance/Foo-2001"), params));
        assertThat(r.getMetadata().get("state", String.class)).isEqualTo("valid");

        // Stop again and check
        params.put("state", "stopped");
        r = everest.process(new DefaultRequest(UPDATE, Path.from("/ipojo/instance/Foo-2001"), params));
        assertThat(r.getMetadata().get("state", String.class)).isEqualTo("stopped");

        // Set state to "disposed" and check
        params.put("state", "disposed");
        r = everest.process(new DefaultRequest(UPDATE, Path.from("/ipojo/instance/Foo-2001"), params));
        assertThat(r.getMetadata().get("state", String.class)).isEqualTo("disposed");

        // Try to get the resource, it should fail
        try {
            read("/ipojo/instance/Foo-2001");
            fail();
        } catch (ResourceNotFoundException e) {
            // Ok!
        }
    }

}
