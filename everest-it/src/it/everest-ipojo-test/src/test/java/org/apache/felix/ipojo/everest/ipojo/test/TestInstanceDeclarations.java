package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.filters.RelationFilters;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.apache.felix.ipojo.extender.internal.declaration.AbstractDeclaration;
import org.junit.Test;

import java.util.Dictionary;

import static org.apache.felix.ipojo.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.apache.felix.ipojo.everest.services.Action.READ;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test /ipojo/declaration/instance and sons
 */
public class TestInstanceDeclarations extends EverestIpojoTestCommon {

    /**
     * Read /ipojo/declaration/instance
     */
    @Test
    public void testReadInstances() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/instance");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/declaration/instance/DeclaredFoo123 and /ipojo/declaration/instance/unnamed
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instances[DeclaredFoo123]"),
                RelationFilters.hasHref("/ipojo/declaration/instance/DeclaredFoo123")));
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instances[unnamed]"),
                RelationFilters.hasHref("/ipojo/declaration/instance/unnamed")));
    }

    /**
     * Read /ipojo/declaration/instance/DeclaredFoo123
     */
    @Test
    public void testReadDeclaredFoo123Instances() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/instance/DeclaredFoo123");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/declaration/instance/DeclaredFoo123/0
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("instance[0]"),
                RelationFilters.hasHref("/ipojo/declaration/instance/DeclaredFoo123/0")));
    }

    /**
     * Read /ipojo/declaration/instance/DeclaredFoo123/0
     */
    @Test
    public void testReadDeclaredFoo123Instance0() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/instance/DeclaredFoo123/0");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check name, factory, status
        assertThat(m.get("name", String.class)).isEqualTo("DeclaredFoo123");
        assertThat(m.get("factory.name", String.class)).isEqualTo("Foo");
        assertThat(m.get("factory.version", String.class)).isEqualTo("1.2.3.foo");
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(AbstractDeclaration.DECLARATION_BOUND_MESSAGE);
        // Check configuration
        @SuppressWarnings("unchecked")
        Dictionary<String, Object> c = m.get("configuration", Dictionary.class);
        assertThat(c.get("instance.name")).isEqualTo("DeclaredFoo123");
        assertThat(c.get("fooPrefix")).isEqualTo("__declared");
        assertThat(c.get("fooCounter")).isEqualTo("123");
        //TODO Check more metadata, as soon as more metadata are provided...
        //TODO Check relations, as soon as some relations are provided...
    }

    // Cannot test unnamed declarations: unknown path
    // TODO find the declarations using relations, as soon as relations are available

}
