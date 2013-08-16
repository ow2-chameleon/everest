package org.ow2.chameleon.everest.ipojo.test;

import org.ow2.chameleon.everest.filters.RelationFilters;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.apache.felix.ipojo.extender.TypeDeclaration;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import static org.ow2.chameleon.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.ow2.chameleon.everest.services.Action.READ;
import static org.apache.felix.ipojo.extender.internal.declaration.AbstractDeclaration.DECLARATION_BOUND_MESSAGE;
import static org.fest.assertions.Assertions.assertThat;
import static org.osgi.framework.Constants.SERVICE_ID;

/**
 * Test /ipojo/declaration/type and sons
 */
public class TestTypeDeclarations extends EverestIpojoTestCommon {

    /**
     * Read /ipojo/declaration/type
     */
    @Test
    public void testReadTypes() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Check relation to the "Foo" type
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("types[Foo]"),
                RelationFilters.hasHref("/ipojo/declaration/type/Foo")));
        // Check relation to the "$BAR" type
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("types[" + BAR +"]"),
                RelationFilters.hasHref("/ipojo/declaration/type/" + BAR)));
    }

    /**
     * Read /ipojo/declaration/type/Foo
     */
    @Test
    public void testFooTypes() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/Foo");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/declaration/type/Foo/1.2.3.foo
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("type[1.2.3.foo]"),
                RelationFilters.hasHref("/ipojo/declaration/type/Foo/1.2.3.foo")));
    }

    /**
     * Read /ipojo/declaration/type/Foo/1.2.3.foo
     */
    @Test
    public void testFoo123Type() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/Foo/1.2.3.foo");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check adaptation
        assertThat(r.adaptTo(TypeDeclaration.class)).isSameAs(getTypeDeclaration("Foo", "1.2.3.foo"));
        // Check name, version, extension, ...
        assertThat(m.get("name", String.class)).isEqualTo("Foo");
        assertThat(m.get("version", String.class)).isEqualTo("1.2.3.foo");
        assertThat(m.get("extension", String.class)).isEqualTo("component");
        assertThat(m.get("isPublic", Boolean.class)).isTrue();
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(DECLARATION_BOUND_MESSAGE);
        //TODO Check more metadata, as soon as more metadata are provided...
        // Check relation on declaring bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("bundle"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/bundles/" + testBundle.getBundleId())));
        // Check relation on TypeDeclaration service
        ServiceReference<TypeDeclaration> ref = getTypeDeclarationReference("Foo", "1.2.3.foo");
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("service"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/services/" + ref.getProperty(SERVICE_ID))));
        // Check relation on "component" extension service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("extension"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/ipojo/declaration/extension/component")));
        //TODO Check relations, as soon as some relations are provided...
    }

    /**
     * Read /ipojo/declaration/type/$BAR
     */
    @Test
    public void testBarTypes() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/" + BAR);
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/declaration/type/$BAR/null
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("type[null]"),
                RelationFilters.hasHref("/ipojo/declaration/type/" + BAR +"/null")));
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("type[2.0.0]"),
                RelationFilters.hasHref("/ipojo/declaration/type/" + BAR +"/2.0.0")));
    }

    /**
     * Read /ipojo/declaration/type/$BAR/null
     */
    @Test
    public void testBarNullType() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/" +BAR + "/null");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check adaptation
        assertThat(r.adaptTo(TypeDeclaration.class)).isSameAs(getTypeDeclaration(BAR, null));
        // Check name, version, extension, ...
        assertThat(m.get("name", String.class)).isEqualTo(BAR);
        assertThat(m.get("version", String.class)).isNull();
        assertThat(m.get("extension", String.class)).isEqualTo("component");
        assertThat(m.get("isPublic", Boolean.class)).isTrue();
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(DECLARATION_BOUND_MESSAGE);
        // TODO Check more metadata, as soon as more metadata are provided...
        // Check relation on declaring bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("bundle"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/bundles/" + testBundle.getBundleId())));
        // Check relation on TypeDeclaration service
        ServiceReference<TypeDeclaration> ref = getTypeDeclarationReference(BAR, null);
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("service"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/services/" + ref.getProperty(SERVICE_ID))));
        // Check relation on "component" extension service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("extension"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/ipojo/declaration/extension/component")));
        // TODO Check relations, as soon as some relations are provided...
    }

    /**
     * Read /ipojo/declaration/type/$BAR/null
     */
    @Test
    public void testBar2Type() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/" +BAR + "/2.0.0");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check adaptation
        assertThat(r.adaptTo(TypeDeclaration.class)).isSameAs(getTypeDeclaration(BAR, "2.0.0"));
        // Check name, version, extension, ...
        assertThat(m.get("name", String.class)).isEqualTo(BAR);
        assertThat(m.get("version", String.class)).isEqualTo("2.0.0");
        assertThat(m.get("extension", String.class)).isEqualTo("component");
        assertThat(m.get("isPublic", Boolean.class)).isTrue();
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(DECLARATION_BOUND_MESSAGE);
        // TODO Check more metadata, as soon as more metadata are provided...
        // Check relation on declaring bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("bundle"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/bundles/" + testBundle2.getBundleId())));
        // Check relation on TypeDeclaration service
        ServiceReference<TypeDeclaration> ref = getTypeDeclarationReference(BAR, "2.0.0");
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("service"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/services/" + ref.getProperty(SERVICE_ID))));
        // Check relation on "component" extension service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("extension"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/ipojo/declaration/extension/component")));
        // TODO Check relations, as soon as some relations are provided...
    }

    /**
     * Read /ipojo/declaration/type/qux
     */
    @Test
    public void testQuxTypes() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/qux");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Resource should have relations to /ipojo/declaration/type/qux/null
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("type[null]"),
                RelationFilters.hasHref("/ipojo/declaration/type/qux/null")));
    }

    /**
     * Read /ipojo/declaration/type/$BAR/null
     */
    @Test
    public void testQuxNullType() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/type/qux/null");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check adaptation
        assertThat(r.adaptTo(TypeDeclaration.class)).isSameAs(getTypeDeclaration("qux", null));
        // Check name, version, extension, ...
        assertThat(m.get("name", String.class)).isEqualTo("qux");
        assertThat(m.get("version", String.class)).isNull();
        assertThat(m.get("extension", String.class)).isEqualTo("handler");
        assertThat(m.get("isPublic", Boolean.class)).isTrue();
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(DECLARATION_BOUND_MESSAGE);
        // TODO Check more metadata, as soon as more metadata are provided...
        // Check relation on declaring bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("bundle"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/bundles/" + testBundle.getBundleId())));
        // Check relation on TypeDeclaration service
        ServiceReference<TypeDeclaration> ref = getTypeDeclarationReference("qux", null);
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("service"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/osgi/services/" + ref.getProperty(SERVICE_ID))));
        // Check relation on "handler" extension service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasName("extension"),
                RelationFilters.hasAction(READ),
                RelationFilters.hasHref("/ipojo/declaration/extension/handler")));
        // TODO Check relations, as soon as some relations are provided...
    }

}
