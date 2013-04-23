package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.IPojoFactory;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.ipojo.services.FooService;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
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

    // Test that illegal actions on /ipojo/factory are illegal

    @Test(expected = IllegalActionOnResourceException.class)
    public void testCreateOnFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.CREATE, Path.from("/ipojo/factory"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testUpdateOnFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.UPDATE, Path.from("/ipojo/factory"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testDeleteOnFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.DELETE, Path.from("/ipojo/factory"), null));
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

    // Test that illegal actions on /ipojo/factory/Foo are illegal

    @Test(expected = IllegalActionOnResourceException.class)
    public void testCreateOnFooFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.CREATE, Path.from("/ipojo/factory/Foo"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testUpdateOnFooFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.UPDATE, Path.from("/ipojo/factory/Foo"), null));
    }

    @Test(expected = IllegalActionOnResourceException.class)
    public void testDeleteOnFooFactories() throws ResourceNotFoundException, IllegalActionOnResourceException {
        everest.process(new DefaultRequest(Action.DELETE, Path.from("/ipojo/factory/Foo"), null));
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

    //TODO test that UPDATE is forbidden on factories

    /**
     * Test that a CREATE action on the resource representing Foo factory has the expected behavior.
     */
    @Test
    public void testCreateOnFooFactory() throws ResourceNotFoundException, IllegalActionOnResourceException {

        // Request creation on factory Foo, without any parameter
        Request req = new DefaultRequest(Action.CREATE, Path.from("/ipojo/factory/Foo/1.2.3.foo"), null);
        Resource result = everest.process(req);
        assertThat(result).isNotNull();

        // Read metadata of resulting resource
        ResourceMetadata meta = result.getMetadata();

        // Check name
        String name = meta.get("name", String.class);
        assertThat(name).startsWith("Foo-");

        // Check default configuration has taken place.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), name);
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("fooCounter")).isEqualTo(0);

        FooService foo = (FooService) bc.getService(ref);
        assertThat(foo.getFoo()).isEqualTo("0");

        //TODO check relation to factory
    }

    /**
     * Test that a CREATE action (with parameters) on the resource representing Foo factory has the expected behavior.
     */
    @Test
    public void testCreateOnFooFactoryWithConfiguration() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Map<String, Object> config = new LinkedHashMap<String, Object>();
        config.put("instance.name", "ConfiguredFoo");
        config.put("fooPrefix", "__configured");
        config.put("fooCounter", 666);

        // Request creation on factory Foo, with configuration
        Request req = new DefaultRequest(Action.CREATE, Path.from("/ipojo/factory/Foo/1.2.3.foo"), config);
        Resource result = everest.process(req);
        assertThat(result).isNotNull();

        // Read metadata of resulting resource
        ResourceMetadata meta = result.getMetadata();

        // Check name
        assertThat(meta.get("name", String.class)).isEqualTo("ConfiguredFoo");

        // Check configuration has been taken into consideration.
        ServiceReference ref = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), "ConfiguredFoo");
        assertThat(ref).isNotNull();
        assertThat(ref.getProperty("fooCounter")).isEqualTo(666);

        FooService foo = (FooService) bc.getService(ref);
        assertThat(foo.getFoo()).isEqualTo("__configured666");
        //TODO check relation to factory
    }

    /**
     * Test that a CREATE action (with BAD parameters) on the resource representing Foo factory has the expected behavior.
     */
    @Test(expected=IllegalActionOnResourceException.class)
    public void testCreateOnFooFactoryWithBadConfiguration() throws ResourceNotFoundException, IllegalActionOnResourceException {

        Map<String, Object> badConfig = new LinkedHashMap<String, Object>();
        badConfig.put("instance.name", "BadConfiguredFoo");
        badConfig.put("fooPrefix", "__bad_configured");
        badConfig.put("fooCounter", "notAnInteger");

        // Request creation on factory Foo, with bad configuration
        Request req = new DefaultRequest(Action.CREATE, Path.from("/ipojo/factory/Foo/1.2.3.foo"), badConfig);
        Resource result = everest.process(req);

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

        // Check that the result represents the killed factory
        assertThat(result).isNotNull();
        ResourceMetadata meta = result.getMetadata();
        assertThat(meta.get("name")).isEqualTo(BAR);
        assertThat(meta.get("version")).isEqualTo("2.0.0");
        assertThat(meta.get("className")).isEqualTo(BAR_2);

        // Check that the service reference has gone.
        Collection<ServiceReference<Factory>> refs2 = bc.getServiceReferences(Factory.class, "(factory.name=" + BAR + ")");
        assertThat(refs2).hasSize(1);

        // Check that accessing Bar version null still works
        assertThat(read("/ipojo/factory/" + BAR + "/null")).isNotNull();

        // Check that accessing the deleted resource (Bar version "2.0.0") fails miserably
        try {
            read("/ipojo/factory/" + BAR + "/2.0.0");
            org.junit.Assert.fail("/ipojo/factory/" + BAR + "/2.0.0 should not exist anymore" );
        } catch (ResourceNotFoundException e) {
            // Ok : that's normal!
        }
    }

    // HACKS below this point!!!

    private Architecture getArchitecture(String name) throws InvalidSyntaxException {
        Collection<ServiceReference<Architecture>> refs = bc.getServiceReferences(Architecture.class, "(architecture.instance=" + name + ")");
        if (refs.isEmpty()) {
            return null;
        } else if (refs.size() > 1) {
            // Should never happen!
            throw new AssertionError("multiple architecture service with same instance name");
        }
        ServiceReference<Architecture> ref = refs.iterator().next();
        return bc.getService(ref);
    }

    private ComponentInstance getComponentInstance(String name) throws InvalidSyntaxException, NoSuchFieldException, IllegalAccessException {
        Architecture arch = getArchitecture(name);
        if (arch == null) {
            return null;
        }
        ComponentInstance instance;
        InstanceDescription desc = arch.getInstanceDescription();
        Field shunt = InstanceDescription.class.getDeclaredField("m_instance");
        shunt.setAccessible(true);
        try {
            return (ComponentInstance) shunt.get(desc);
        } finally {
            shunt.setAccessible(false);
        }
    }

    // WARN: this is a hack!!!
    private boolean killInstance(String name) throws InvalidSyntaxException, NoSuchFieldException, IllegalAccessException {
        ComponentInstance instance = getComponentInstance(name);
        if (instance == null) {
            return false;
        }
        // FATALITY!!!
        instance.dispose();
        return true;
    }

    private Factory getFactory(String name, String version) throws InvalidSyntaxException {
        // Scientifically build the selection filter.
        String filter = "(&(factory.name=" + name + ")";
        if (version != null) {
            filter += "(factory.version=" + version + ")";
        } else {
            filter += "(!(factory.version=*))";
        }
        filter += ")";

        Collection<ServiceReference<Factory>> refs = bc.getServiceReferences(Factory.class, filter);
        if (refs.isEmpty()) {
            return null;
        } else if (refs.size() > 1) {
            // Should never happen!
            throw new AssertionError("multiple factory service with same name/version");
        }
        return bc.getService(refs.iterator().next());
    }

    // WARN: this is a hack!!!
    private boolean killFactory(String name, String version) throws InvalidSyntaxException {
        Factory factory = getFactory(name, version);
        if (factory == null) {
            return false;
        }

        IPojoFactory f = (IPojoFactory) factory;
        Method weapon = null;
        try {
            weapon = IPojoFactory.class.getDeclaredMethod("dispose");
            weapon.setAccessible(true);
            // FATALITY!!!
            weapon.invoke(f);
        } catch (Exception e) {
            throw new IllegalStateException("cannot kill factory", e);
        } finally {
            // It's a bad idea to let kids play with such a weapon...
            if (weapon != null) {
                weapon.setAccessible(false);
            }
        }
        return true;
    }

}
