package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.filters.RelationFilters;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.ops4j.pax.exam.MavenUtils;
import org.osgi.framework.Bundle;

import static org.apache.felix.ipojo.everest.filters.RelationFilters.*;
import static org.apache.felix.ipojo.everest.ipojo.test.ResourceAssert.assertThatResource;
import static org.fest.assertions.Assertions.assertThat;

public class TestResources extends Common {

    /**
     * Shortcut method to process a READ request on an everest resource.
     *
     * @param path the path of the resource to read
     * @return the read resource
     * @throws ResourceNotFoundException if the resource cannot be found
     * @throws IllegalActionOnResourceException
     *                                   if READ is not a valid operation on the targeted resource
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }

    /**
     * Check that the '/ipojo' resource is present.
     */
    @Test
    public void testRootIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/ipojo");
    }

    /**
     * Check that the '/ipojo' resource metadata contains the iPOJO version.
     */
    @Test
    public void testIpojoVersion() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource ipojo = read("/ipojo");
        // Get the iPOJO version using Pax Exam and compare versions.
        String version = MavenUtils.getArtifactVersion("org.apache.felix", "org.apache.felix.ipojo").replace('-', '.');
        assertThat(ipojo.getMetadata().get("version")).isEqualTo(version);
    }

    /**
     * Check that the '/ipojo' has a relation to the iPOJO bundle.
     */
    @Test
    public void testRelationToIpojoBundle() throws ResourceNotFoundException, IllegalActionOnResourceException {
        assertThatResource(read("/ipojo")).hasRelation(and(hasName("bundle"), hasAction(Action.READ), hasHref("/osgi/bundles/" + ipojoBundle.getBundleId())));
    }

    /**
     * Check that the '/ipojo/factory' resource is present.
     */
    @Test
    public void testFactoryIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/ipojo/factory");
    }

    /**
     * Check that the '/ipojo/factory' resource is present.
     */
    @Test
    public void testHandlerIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/ipojo/handler");
    }

    /**
     * Check that the '/ipojo/instance' resource is present.
     */
    @Test
    public void testInstanceIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/ipojo/instance");
    }

    /**
     * Check that the '/ipojo/declaration' resource is present.
     */
    @Test
    public void testDeclarationIsPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        read("/ipojo/declaration");
    }

}
