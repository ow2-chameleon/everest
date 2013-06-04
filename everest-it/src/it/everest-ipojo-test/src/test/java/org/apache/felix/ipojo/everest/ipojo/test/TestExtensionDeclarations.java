package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.filters.RelationFilters;
import org.apache.felix.ipojo.everest.ipojo.test.ex.DummyExtension;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.apache.felix.ipojo.extender.internal.declaration.AbstractDeclaration;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.apache.felix.ipojo.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.apache.felix.ipojo.everest.services.Action.READ;
import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.OptionUtils.combine;

/**
 * Test /ipojo/declaration/extension and sons
 */
public class TestExtensionDeclarations extends EverestIpojoTestCommon {

    /**
     * The bundle symbolic name of the extension generated test bundle.
     */
    public static final String TEST_BUNDLE_EX_SYMBOLIC_NAME = "test.bundle.ex";

    /**
     * The extension generated test bundle.
     */
    private Bundle testBundleEx;

    @Override
    protected Option[] getCustomOptions() {
        // Generate a bundle that contains an iPOJO extension.
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("IPOJO-Extension", "dummy:" + DummyExtension.class.getName());
        return combine(super.getCustomOptions(),
                generateTestBundle(
                        TEST_BUNDLE_EX_SYMBOLIC_NAME,
                        IPOJO + ".everest.ipojo.test.ex",
                        null,
                        headers));
    }

    @Before
    public void setUp() {
        super.commonSetUp();
        testBundleEx = osgiHelper.getBundle(TEST_BUNDLE_EX_SYMBOLIC_NAME);
    }

    /**
     * Read /ipojo/declaration/extension
     */
    @Test
    public void testReadExtensions() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/extension");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        // Metadata should be empty
        assertThat(r.getMetadata()).isEmpty();
        // Check relation to "component" extension
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("extension[component]"),
                RelationFilters.hasHref("/ipojo/declaration/extension/component")));
        // Check relation to "handler" extension
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("extension[handler]"),
                RelationFilters.hasHref("/ipojo/declaration/extension/handler")));
        // Check relation to "dummy" extension
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("extension[dummy]"),
                RelationFilters.hasHref("/ipojo/declaration/extension/dummy")));
    }

    /**
     * Read /ipojo/declaration/extension/component
     */
    @Test
    public void testComponentExtension() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/extension/component");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check name and status
        assertThat(m.get("name", String.class)).isEqualTo("component");
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(AbstractDeclaration.DECLARATION_BOUND_MESSAGE);
        // Check relation to the iPOJO bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("bundle"),
                RelationFilters.hasHref("/osgi/bundles/" + ipojoBundle.getBundleId())));
        // Check relation to the OSGi service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("service"),
                RelationFilters.hasHref("/osgi/services/" + getExtensionDeclarationReference("component").getProperty(Constants.SERVICE_ID))));
    }

    /**
     * Read /ipojo/declaration/extension/handler
     */
    @Test
    public void testHandlerExtension() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/extension/handler");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check name and status
        assertThat(m.get("name", String.class)).isEqualTo("handler");
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(AbstractDeclaration.DECLARATION_BOUND_MESSAGE);
        // Check relation to the iPOJO bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("bundle"),
                RelationFilters.hasHref("/osgi/bundles/" + ipojoBundle.getBundleId())));
        // Check relation to the OSGi service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("service"),
                RelationFilters.hasHref("/osgi/services/" + getExtensionDeclarationReference("handler").getProperty(Constants.SERVICE_ID))));
    }

    /**
     * Read /ipojo/declaration/extension/dummy
     */
    @Test
    public void testDummyExtension() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/ipojo/declaration/extension/dummy");
        // Resource should be observable
        assertThat(r.isObservable()).isTrue();
        ResourceMetadata m = r.getMetadata();
        // Check name and status
        assertThat(m.get("name", String.class)).isEqualTo("dummy");
        assertThat(m.get("status.isBound", Boolean.class)).isTrue();
        assertThat(m.get("status.message", String.class)).isEqualTo(AbstractDeclaration.DECLARATION_BOUND_MESSAGE);
        // Check relation to the iPOJO bundle
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("bundle"),
                RelationFilters.hasHref("/osgi/bundles/" + testBundleEx.getBundleId())));
        // Check relation to the OSGi service
        assertThatResource(r).hasRelation(RelationFilters.and(
                RelationFilters.hasAction(READ),
                RelationFilters.hasName("service"),
                RelationFilters.hasHref("/osgi/services/" + getExtensionDeclarationReference("dummy").getProperty(Constants.SERVICE_ID))));
    }

}
